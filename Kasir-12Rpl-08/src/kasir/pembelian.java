/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package kasir;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.print.PrinterException;
import java.sql.*;
import java.text.MessageFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class pembelian extends javax.swing.JFrame {

 
    public pembelian() {
        initComponents();
btnPilih.setEnabled(true);
btnTambah.setEnabled(true);
btnDelete.setEnabled(false);
btnEdit.setEnabled(false);
this.setResizable(false);
this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
this.setVisible(true);

     java.util.Date akhir = new java.util.Date();
jdcTanggal.setDate(akhir);
loadRiwayat();
     java.util.Calendar cal = java.util.Calendar.getInstance();
    java.util.Date today = cal.getTime();
    jdcEnd.setDate(today);

    // Set tanggal awal bulan
    cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
    java.util.Date firstDay = cal.getTime();
    jdcStart.setDate(firstDay);

    // (Opsional) langsung tampilkan data bulan ini
jdcStart.addPropertyChangeListener("date", evt1 -> {
        if (jdcStart.getDate() != null && jdcEnd.getDate() != null) {
            filterRiwayatByTanggal();
        }
    });

    jdcEnd.addPropertyChangeListener("date", evt1 -> {
        if (jdcStart.getDate() != null && jdcEnd.getDate() != null) {
            filterRiwayatByTanggal();
        }
    });    }
 private void showDetailPopup(int idPembelian) {
    // === Buat dialog ===
    JDialog dialog = new JDialog(this, "Detail Pembelian", true);
    dialog.setSize(600, 400);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout());

    // === Buat tabel ===
    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("Nama Bahan");
    model.addColumn("Jumlah");
    model.addColumn("Satuan");
    model.addColumn("Harga Satuan");
    model.addColumn("Subtotal");

    JTable tblDetail = new JTable(model);
    JScrollPane scroll = new JScrollPane(tblDetail);
    dialog.add(scroll, BorderLayout.CENTER);

    // === Panel bawah: total dan tombol tutup ===
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JLabel lblTotal = new JLabel("Total: ");
    JTextField txtTotal = new JTextField(10);
    txtTotal.setEditable(false);
    JButton btnTutup = new JButton("Tutup");
    bottomPanel.add(lblTotal);
    bottomPanel.add(txtTotal);
    bottomPanel.add(btnTutup);
    dialog.add(bottomPanel, BorderLayout.SOUTH);

    // === Ambil data detail dari database ===
    int total = 0;
    try (Connection conn = kasir.koneksi.dbKonek()) {
        String sql = """
            SELECT b.namabahan, d.jumlah, d.satuan, d.harga_satuan, d.subtotal
            FROM detail_pembelian d
            JOIN bahan b ON d.idbahan = b.idbahan
            WHERE d.idpembelian = ?
        """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, idPembelian);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int subtotal = rs.getInt("subtotal");
            total += subtotal;
            model.addRow(new Object[]{
                rs.getString("namabahan"),
                rs.getInt("jumlah"),
                rs.getString("satuan"),
                rs.getInt("harga_satuan"),
                subtotal
            });
        }
        txtTotal.setText(String.valueOf(total));
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal memuat detail: " + e.getMessage());
        e.printStackTrace();
    }

    // === Tombol tutup ===
    btnTutup.addActionListener(evt -> dialog.dispose());

    // === Tampilkan popup ===
    dialog.setVisible(true);
}


    public void setNamaBahan(String nama) {
    txtNama.setText(nama);
}

public void setHargaBahan(String harga) {
    txtHarga.setText(harga);
}

public void setSatuanBahan(String satuan) {
    txtSatuan.setText(satuan);
}
private void clearField() {
    txtNama.setText("");
    txtSatuan.setText("");
    txtHarga.setText("");
    txtJumlah.setText("");
    txtTotal.setText("");
}
private void filterRiwayatByTanggal() {
    java.util.Date startDate = jdcStart.getDate();
    java.util.Date endDate = jdcEnd.getDate();

    if (startDate == null || endDate == null) {
        JOptionPane.showMessageDialog(this, "Silakan pilih tanggal awal dan akhir terlebih dahulu!");
        return;
    }

    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("Id");
    model.addColumn("No");
    model.addColumn("Tanggal");
    model.addColumn("Total");
    tblRiwayat.setModel(model);

    try (Connection conn = kasir.koneksi.dbKonek()) {
        // Pastikan tanggal awal dan akhir mencakup seluruh hari
        java.sql.Timestamp start = new java.sql.Timestamp(startDate.getTime());
        java.sql.Timestamp end = new java.sql.Timestamp(endDate.getTime() + (24 * 60 * 60 * 1000) - 1);

        String sql = """
            SELECT idpembelian, tanggal, total 
            FROM pembelian_bahan 
            WHERE tanggal BETWEEN ? AND ?
            ORDER BY tanggal DESC
        """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setTimestamp(1, start);
        ps.setTimestamp(2, end);
        ResultSet rs = ps.executeQuery();

        int no = 1;
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("idpembelian"),
                no++,
                rs.getTimestamp("tanggal"),
                rs.getInt("total")
            });
        }

        // Sembunyikan kolom ID
        tblRiwayat.getColumnModel().getColumn(0).setMinWidth(0);
        tblRiwayat.getColumnModel().getColumn(0).setMaxWidth(0);
        tblRiwayat.getColumnModel().getColumn(0).setWidth(0);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal memfilter riwayat: " + e.getMessage());
        e.printStackTrace();
    }
}

private void loadRiwayat() {
    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("Id");
    model.addColumn("No");
    model.addColumn("Tanggal");
    model.addColumn("Total");
    tblRiwayat.setModel(model);

    try (Connection conn = kasir.koneksi.dbKonek()) {
        String sql = "SELECT idpembelian, tanggal, total FROM pembelian_bahan ORDER BY tanggal DESC";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        int no = 1;
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("idpembelian"),
                no++,
                rs.getDate("tanggal"),
                rs.getInt("total")
            });
        }
        // Sembunyikan kolom ID (kolom ke-0)
tblRiwayat.getColumnModel().getColumn(0).setMinWidth(0);
tblRiwayat.getColumnModel().getColumn(0).setMaxWidth(0);
tblRiwayat.getColumnModel().getColumn(0).setWidth(0);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal memuat riwayat: " + e.getMessage());
        e.printStackTrace();
    }
}

private void hitungTotalNota() {
    double totalNota = 0;
    DefaultTableModel model = (DefaultTableModel) tblPembelian.getModel();

    for (int i = 0; i < model.getRowCount(); i++) {
        Object totalObj = model.getValueAt(i, 5); // ganti 5 dengan index kolom "Total"
        if (totalObj != null) {
            try {
                totalNota += Double.parseDouble(totalObj.toString());
            } catch (NumberFormatException e) {
                // abaikan kalau format tidak valid
            }
        }
    }

    txtTotalNota.setText(String.format("%.0f", totalNota)); // tanpa desimal
}
private void cetakNota() {
    try {
        boolean complete = tblPembelian.print(
            JTable.PrintMode.FIT_WIDTH,
            new MessageFormat("NOTA PEMBELIAN BAHAN"),
            new MessageFormat("Total: Rp" + txtTotalNota.getText())
        );
        if (complete) {
            JOptionPane.showMessageDialog(this, "Nota berhasil dicetak!");
        } else {
            JOptionPane.showMessageDialog(this, "Pencetakan dibatalkan.");
        }
    } catch (PrinterException e) {
        JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat mencetak: " + e.getMessage());
    }
}


  
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        txtJumlah = new javax.swing.JTextField();
        txtTotal = new javax.swing.JTextField();
        jdcTanggal = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPembelian = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        txtHarga = new javax.swing.JTextField();
        txtSatuan = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btnPilih = new javax.swing.JButton();
        btnTambah = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        txtTotalNota = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblRiwayat = new javax.swing.JTable();
        jdcEnd = new com.toedter.calendar.JDateChooser();
        jdcStart = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(242, 236, 228));
        jPanel2.setPreferredSize(new java.awt.Dimension(1080, 720));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtJumlah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtJumlahKeyReleased(evt);
            }
        });
        jPanel2.add(txtJumlah, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 130, 228, 58));

        txtTotal.setEditable(false);
        txtTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalActionPerformed(evt);
            }
        });
        jPanel2.add(txtTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 130, 328, 58));

        jdcTanggal.setEnabled(false);
        jPanel2.add(jdcTanggal, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 34, 331, 58));

        tblPembelian.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tanggal", "Nama Bahan", "Satuan", "HargaSatuan", "Jumlah", "Total"
            }
        ));
        tblPembelian.setRowHeight(35);
        tblPembelian.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPembelianMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblPembelian);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 270, 680, 351));

        jLabel1.setText("Jumlah");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 110, -1, -1));

        jLabel2.setText("Total Seluruh Pembelian");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 110, -1, -1));

        jLabel3.setText("*Tanggal");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 12, -1, -1));

        txtNama.setEditable(false);
        txtNama.setActionCommand("<Not Set>");
        txtNama.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        txtNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaActionPerformed(evt);
            }
        });
        jPanel2.add(txtNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 30, 250, 58));

        txtHarga.setEditable(false);
        txtHarga.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        txtHarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHargaActionPerformed(evt);
            }
        });
        jPanel2.add(txtHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 30, 328, 58));

        txtSatuan.setEditable(false);
        txtSatuan.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        jPanel2.add(txtSatuan, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 100, 58));

        jLabel5.setText("*Satuan");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, -1, -1));

        jLabel6.setText("*Harga satuan");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 10, -1, -1));

        btnPilih.setText("Pilih");
        btnPilih.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPilihActionPerformed(evt);
            }
        });
        jPanel2.add(btnPilih, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 30, -1, 58));

        btnTambah.setBackground(new java.awt.Color(102, 102, 255));
        btnTambah.setText("Tambah Ke keranjang");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });
        jPanel2.add(btnTambah, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 200, 60));

        jButton3.setBackground(new java.awt.Color(0, 204, 153));
        jButton3.setText("BELI");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 630, 330, 60));

        jButton4.setBackground(new java.awt.Color(102, 102, 102));
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("BATAL");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 200, 100, 60));

        btnEdit.setBackground(new java.awt.Color(255, 153, 0));
        btnEdit.setText("EDIT");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        jPanel2.add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 200, 100, 60));

        btnDelete.setBackground(new java.awt.Color(250, 52, 88));
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("DELETE");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        jPanel2.add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 200, 100, 60));

        jButton7.setBackground(new java.awt.Color(204, 204, 204));
        jButton7.setText("RESET");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 630, 330, 60));

        txtTotalNota.setText("0");
        txtTotalNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalNotaActionPerformed(evt);
            }
        });
        jPanel2.add(txtTotalNota, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 130, 328, 58));

        jLabel7.setText("__");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 220, -1, -1));

        tblRiwayat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "No", "Tanggal", "Total"
            }
        ));
        tblRiwayat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblRiwayatMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblRiwayat);
        if (tblRiwayat.getColumnModel().getColumnCount() > 0) {
            tblRiwayat.getColumnModel().getColumn(0).setMaxWidth(40);
        }

        jPanel2.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 270, 340, 420));

        jdcEnd.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jdcEndPropertyChange(evt);
            }
        });
        jPanel2.add(jdcEnd, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 200, 160, 60));

        jdcStart.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jdcStartPropertyChange(evt);
            }
        });
        jPanel2.add(jdcStart, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 200, 150, 60));

        jLabel9.setText("*Nama Barang");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 10, -1, -1));

        jLabel10.setText("*Total");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 110, -1, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 0, -1, -1));

        jPanel1.setBackground(new java.awt.Color(119, 85, 70));

        jPanel8.setBackground(new java.awt.Color(119, 85, 70));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1.setBackground(new java.awt.Color(102, 51, 255));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Info");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jButton5.setBackground(new java.awt.Color(242, 236, 228));
        jButton5.setText("Kasir");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 245, 165, 50));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/logo.png"))); // NOI18N
        jLabel8.setText("jLabel1");
        jPanel8.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jButton6.setBackground(new java.awt.Color(242, 236, 228));
        jButton6.setText("Admin");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 190, 165, 50));

        jButton8.setBackground(new java.awt.Color(242, 236, 228));
        jButton8.setText("Resep");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 520, 165, 50));

        jButton9.setBackground(new java.awt.Color(242, 236, 228));
        jButton9.setText("RiwayatTransaksi");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 300, 165, 50));

        jButton10.setBackground(new java.awt.Color(206, 171, 141));
        jButton10.setText("Pembelian");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton10, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 355, 165, 50));

        jButton11.setBackground(new java.awt.Color(242, 236, 228));
        jButton11.setText("Menu");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton11, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 410, 165, 50));

        jButton12.setBackground(new java.awt.Color(242, 236, 228));
        jButton12.setText("Inventory");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton12, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 465, 165, 50));

        jButton13.setBackground(new java.awt.Color(255, 153, 153));
        jButton13.setText("Log out");
        jButton13.setFocusTraversalPolicyProvider(true);
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton13, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 650, 165, 50));

        jButton14.setBackground(new java.awt.Color(242, 236, 228));
        jButton14.setText("Pengguna");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton14, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 575, 165, 50));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 730, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(11, 11, 11)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(12, 12, 12)))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 730));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblPembelianMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPembelianMouseClicked
btnPilih.setEnabled(false);
btnTambah.setEnabled(false);
btnDelete.setEnabled(true);
btnEdit.setEnabled(true);
        int row = tblPembelian.getSelectedRow();
    if (row != -1) {
        txtNama.setText(tblPembelian.getValueAt(row, 1).toString());
        txtSatuan.setText(tblPembelian.getValueAt(row, 2).toString());
        txtHarga.setText(tblPembelian.getValueAt(row, 3).toString());
        txtJumlah.setText(tblPembelian.getValueAt(row, 4).toString());
        txtTotal.setText(tblPembelian.getValueAt(row, 5).toString());
    }    }//GEN-LAST:event_tblPembelianMouseClicked

    private void btnPilihActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPilihActionPerformed
 jDialogBahan dialog = new jDialogBahan(this, true, this);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);     
    }//GEN-LAST:event_btnPilihActionPerformed

    private void txtJumlahKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtJumlahKeyReleased
  try {
        int jumlah = Integer.parseInt(txtJumlah.getText());
        int harga = Integer.parseInt(txtHarga.getText());
        int total = jumlah * harga;
        txtTotal.setText(String.valueOf(total));
    } catch (NumberFormatException e) {
        txtTotal.setText("0");
    }        // TODO add your handling code here:
    }//GEN-LAST:event_txtJumlahKeyReleased

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
String jumlahText = txtJumlah.getText().trim();
if (!jumlahText.matches("\\d+")) {
    JOptionPane.showMessageDialog(this,
            "Jumlah hanya boleh angka!",
            "Input Salah",
            JOptionPane.ERROR_MESSAGE);
    txtJumlah.requestFocus();
    return; // stop supaya tidak lanjut addRow
}
String namaText = txtNama.getText().trim();
if (namaText.isEmpty()) {
    JOptionPane.showMessageDialog(this,
            "Nama tidak boleh kosong!",
            "Input Salah",
            JOptionPane.WARNING_MESSAGE);
    btnPilih.requestFocus();
    return;
}

        DefaultTableModel model = (DefaultTableModel) tblPembelian.getModel();
    model.addRow(new Object[]{
        jdcTanggal.getDate(),
        txtNama.getText(),
        txtSatuan.getText(),
        txtHarga.getText(),
        txtJumlah.getText(),
        txtTotal.getText()
    });
    hitungTotalNota();

    // kosongkan input setelah tambah
    txtNama.setText("");
    txtSatuan.setText("");
    txtHarga.setText("");
    txtJumlah.setText("");
    txtTotal.setText("");        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambahActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    try (Connection conn = kasir.koneksi.dbKonek()) {
        
        conn.setAutoCommit(false); // mulai transaksi manual

        DefaultTableModel model = (DefaultTableModel) tblPembelian.getModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tabel pembelian masih kosong!");
            return;
        }

        // Hitung total keseluruhan
        int totalTransaksi = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            totalTransaksi += Integer.parseInt(model.getValueAt(i, 5).toString());
        }

        // Insert ke tabel pembelian_bahan
        String sqlPembelian = "INSERT INTO pembelian_bahan (tanggal, total) VALUES (?, ?) RETURNING idpembelian";
        PreparedStatement psPembelian = conn.prepareStatement(sqlPembelian);
        psPembelian.setDate(1, new java.sql.Date(jdcTanggal.getDate().getTime()));
        psPembelian.setInt(2, totalTransaksi);
        ResultSet rs = psPembelian.executeQuery();
        rs.next();
        int idPembelianBaru = rs.getInt("idpembelian");

        // Siapkan statement untuk detail
        String sqlDetail = """
            INSERT INTO detail_pembelian 
            (idpembelian, idbahan, jumlah, harga_satuan, satuan, subtotal)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        PreparedStatement psDetail = conn.prepareStatement(sqlDetail);

        // Loop setiap baris tabel pembelian
        for (int i = 0; i < model.getRowCount(); i++) {
            String namaBahan = model.getValueAt(i, 1).toString();

            // Ambil idbahan dari tabel bahan
            PreparedStatement psId = conn.prepareStatement("SELECT idbahan FROM bahan WHERE namabahan = ?");
            psId.setString(1, namaBahan);
            ResultSet rsId = psId.executeQuery();

            if (!rsId.next()) {
                throw new SQLException("Bahan '" + namaBahan + "' tidak ditemukan di database!");
            }

            int idbahan = rsId.getInt("idbahan");

            int jumlah = Integer.parseInt(model.getValueAt(i, 4).toString());
            int harga = Integer.parseInt(model.getValueAt(i, 3).toString());
            String satuan = model.getValueAt(i, 2).toString();
            int subtotal = Integer.parseInt(model.getValueAt(i, 5).toString());

            // Insert ke detail pembelian
            psDetail.setInt(1, idPembelianBaru);
            psDetail.setInt(2, idbahan);
            psDetail.setInt(3, jumlah);
            psDetail.setInt(4, harga);
            psDetail.setString(5, satuan);
            psDetail.setInt(6, subtotal);
            psDetail.addBatch();

            // Update stok bahan
            PreparedStatement psUpdate = conn.prepareStatement(
                "UPDATE bahan SET stok = stok + ? WHERE idbahan = ?"
            );
            psUpdate.setInt(1, jumlah);
            psUpdate.setInt(2, idbahan);
            psUpdate.executeUpdate();
            psUpdate.close();
        }

        // Jalankan batch insert
        psDetail.executeBatch();

        conn.commit();
        JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan!");

loadRiwayat();
        model.setRowCount(0);
        clearField();
        txtTotalNota.setText("0");

        // Tanya apakah mau cetak nota
        int cetak = JOptionPane.showConfirmDialog(
            this, 
            "Apakah Anda ingin mencetak nota pembelian?", 
            "Cetak Nota", 
            JOptionPane.YES_NO_OPTION
        );
        if (cetak == JOptionPane.YES_OPTION) {
            cetakNota();
        }

    } catch (Exception e) {
        e.printStackTrace();
        try {
            Connection conn = kasir.koneksi.dbKonek();
            conn.rollback();
        } catch (Exception ex) {
            System.err.println("Rollback gagal: " + ex.getMessage());
        }
        JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi: " + e.getMessage());
    }

    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
   hitungTotalNota();

        clearField();
btnPilih.setEnabled(true);
btnTambah.setEnabled(true);
btnDelete.setEnabled(false);
btnEdit.setEnabled(false);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
int row = tblPembelian.getSelectedRow();
    if (row != -1) {
        DefaultTableModel model = (DefaultTableModel) tblPembelian.getModel();

        model.setValueAt(txtNama.getText(), row, 1);
        model.setValueAt(txtSatuan.getText(), row, 2);
        model.setValueAt(txtHarga.getText(), row, 3);
        model.setValueAt(txtJumlah.getText(), row, 4);
        model.setValueAt(txtTotal.getText(), row, 5);

        JOptionPane.showMessageDialog(this, "Data berhasil diperbarui.");
        hitungTotalNota();
btnPilih.setEnabled(true);
btnTambah.setEnabled(true);
btnDelete.setEnabled(false);
btnEdit.setEnabled(false);
        clearField(); // kosongkan input setelah edit
    } else {
        JOptionPane.showMessageDialog(this, "Pilih baris yang ingin diedit!");
    }
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
  int row = tblPembelian.getSelectedRow();
    if (row != -1) {
        DefaultTableModel model = (DefaultTableModel) tblPembelian.getModel();
        model.removeRow(row);
        JOptionPane.showMessageDialog(this, "Data berhasil dihapus dari kERANJANG.");
        hitungTotalNota();
btnPilih.setEnabled(true);
btnTambah.setEnabled(true);
btnDelete.setEnabled(false);
btnEdit.setEnabled(false);
        clearField();
    } else {
        JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus!");
    }        // TODO add your handling code here:
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
    int confirm = JOptionPane.showConfirmDialog(
        this, 
        "Apakah Anda yakin ingin membatalkan seluruh pembelian?", 
        "Konfirmasi", 
        JOptionPane.YES_NO_OPTION
    );
    
    if (confirm == JOptionPane.YES_OPTION) {
        // Hapus semua baris di tabel
        DefaultTableModel model = (DefaultTableModel) tblPembelian.getModel();
        model.setRowCount(0); // ini cara paling cepat untuk kosongkan tabel

        // Bersihkan field input
        clearField();

        // Kalau ada total nota, kosongkan juga
        try {
            txtTotalNota.setText("0");
        } catch (Exception e) {
            // abaikan kalau field total nota belum ada
        }

        JOptionPane.showMessageDialog(this, "Transaksi berhasil dibatalkan.");
    }

    }//GEN-LAST:event_jButton7ActionPerformed

    private void txtTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalActionPerformed

    private void txtTotalNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalNotaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalNotaActionPerformed

    private void txtHargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHargaActionPerformed

    private void txtNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNamaActionPerformed

    private void tblRiwayatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRiwayatMouseClicked
int row = tblRiwayat.getSelectedRow();
    if (row == -1) return;

    int idPembelian = Integer.parseInt(tblRiwayat.getValueAt(row, 0).toString());
    showDetailPopup(idPembelian);
    }//GEN-LAST:event_tblRiwayatMouseClicked

    private void jdcStartPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdcStartPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_jdcStartPropertyChange

    private void jdcEndPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdcEndPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_jdcEndPropertyChange

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        menuKasir ksr= new menuKasir();
        ksr.setVisible(true);
        this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        admin ad= new admin();
        ad.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
resep res = new resep();
res.setVisible(true);
this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        historyKasir hk = new  historyKasir();
        hk.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        pembelian pb = new pembelian();
        pb.setVisible(true);
        this.dispose();// TODO add your handling code here:
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        inventoryMenu im = new inventoryMenu();
        im.setVisible(true);
        this.dispose();
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        bahan bhn = new bahan();
        bhn.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this,
            "Yakin ingin logout?",
            "Konfirmasi Logout",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Reset Session
            Session.username = null;
            Session.namaPelayan = null;
            Session.role = null;

            // Kembali ke form login
            login lg = new login();
            lg.setVisible(true);

            this.dispose(); // tutup halaman sekarang
        }
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        user pu = new user();
        pu.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
info in= new info();
in.setVisible(true);// TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(pembelian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(pembelian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(pembelian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(pembelian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new pembelian().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnPilih;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private com.toedter.calendar.JDateChooser jdcEnd;
    private com.toedter.calendar.JDateChooser jdcStart;
    private com.toedter.calendar.JDateChooser jdcTanggal;
    private javax.swing.JTable tblPembelian;
    private javax.swing.JTable tblRiwayat;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtJumlah;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtSatuan;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtTotalNota;
    // End of variables declaration//GEN-END:variables
}
