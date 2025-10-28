/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package kasir;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.table.*;
import java.util.Calendar;
import java.util.Date;

public class historyKasir extends javax.swing.JFrame {

    /**
     * Creates new form historyKasir
     */
    public historyKasir() {

        initComponents();
                    loadRiwayat();
setTanggalBulanIni();
this.setResizable(false);
this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
this.setVisible(true);

    }
    private String formatRupiah(Number value) {
    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setGroupingSeparator('.');   // pemisah ribuan = titik
    symbols.setDecimalSeparator(',');    // pemisah desimal = koma

    DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
    return df.format(value);
}
    
    

private void filterTanggal() {
    java.util.Date tglMulai = jdcStart.getDate();
    java.util.Date tglAkhir = jdcEnd.getDate();

    if (tglMulai == null || tglAkhir == null) {
        return; // kalau belum diisi dua-duanya, jangan filter
    }

    DefaultTableModel model = (DefaultTableModel) tableRiwayat.getModel();
    model.setRowCount(0);

    String sql = "SELECT id, no_transaksi, tgl_transaksi, nama, grand_total, pelayan, agen " +
                 "FROM transaksi " +
                 "WHERE tgl_transaksi BETWEEN ? AND ? ORDER BY tgl_transaksi ASC";

    try {
        Connection conn = kasir.koneksi.dbKonek();
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setDate(1, new java.sql.Date(tglMulai.getTime()));
        pst.setDate(2, new java.sql.Date(tglAkhir.getTime()));

        ResultSet rs = pst.executeQuery();

        int i = 1;
        while (rs.next()) {
            Object[] rowData = {
                i++,
                rs.getInt("id"),
                rs.getString("no_transaksi"),
                rs.getDate("tgl_transaksi"),
                rs.getString("nama"),
                formatRupiah(rs.getBigDecimal("grand_total")),
                rs.getString("pelayan"),
                rs.getString("agen")
            };
            model.addRow(rowData);
        }

        rs.close();
        pst.close();
        conn.close();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error filter tanggal: " + e.getMessage());
    }
}
private void setTanggalBulanIni() {
    try {
        Calendar cal = Calendar.getInstance();

        // 🗓️ Set ke awal bulan
        cal.set(Calendar.DAY_OF_MONTH, 1);
        jdcStart.setDate(cal.getTime());

        // 🗓️ Set ke akhir bulan
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        jdcEnd.setDate(cal.getTime());

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal mengatur tanggal: " + e.getMessage());
    }
}

private void filterCMB() {
    DefaultTableModel model = (DefaultTableModel) tableRiwayat.getModel();
    model.setRowCount(0);

    String pilihan = (String) cmbFilter.getSelectedItem();

    String sql = "SELECT id, no_transaksi, tgl_transaksi, nama, grand_total, pelayan, agen " +
                 "FROM transaksi ";

    // tambahkan kondisi filter sesuai pilihan
    switch (pilihan) {
        case "Hari Ini":
            sql += "WHERE tgl_transaksi = CURRENT_DATE ";
            break;
        case "Kemarin":
            sql += "WHERE tgl_transaksi = CURRENT_DATE - INTERVAL '1 day' ";
            break;
        case "Bulan Ini":
            sql += "WHERE EXTRACT(MONTH FROM tgl_transaksi) = EXTRACT(MONTH FROM CURRENT_DATE) " +
                   "AND EXTRACT(YEAR FROM tgl_transaksi) = EXTRACT(YEAR FROM CURRENT_DATE) ";
            break;
        case "Tahun Ini":
            sql += "WHERE EXTRACT(YEAR FROM tgl_transaksi) = EXTRACT(YEAR FROM CURRENT_DATE) ";
            break;
        case "Semua":
        default:
            // tanpa WHERE
            break;
    }

    sql += "ORDER BY tgl_transaksi DESC";

    try {
        Connection conn = kasir.koneksi.dbKonek();
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        int i = 1;
        while (rs.next()) {
            Object[] rowData = {
                i++,
                rs.getInt("id"),
                rs.getString("no_transaksi"),
                rs.getDate("tgl_transaksi"),
                rs.getString("nama"),
                formatRupiah(rs.getBigDecimal("grand_total")),
                rs.getString("pelayan"),
                rs.getString("agen")
            };
            model.addRow(rowData);
        }

        rs.close();
        pst.close();
        conn.close();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error filter riwayat: " + e.getMessage());
    }
}
    private void loadRiwayat() {
        
    DefaultTableModel model = (DefaultTableModel) tableRiwayat.getModel();
    model.setRowCount(0); // bersihkan tabel dulu

    try {
        Connection conn = kasir.koneksi.dbKonek();
        String sql = "SELECT id, no_transaksi, tgl_transaksi, nama, grand_total, pelayan,agen " +
                     "FROM transaksi ORDER BY tgl_transaksi DESC";
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        int i = 1;
        while (rs.next()) {
            Object[] rowData = {
                i++,
                rs.getInt("id"),
                rs.getString("no_transaksi"),
                rs.getDate("tgl_transaksi"),
                rs.getString("nama"),
        formatRupiah(rs.getBigDecimal("grand_total")), // ⬅️ sudah diformat
                rs.getString("pelayan"),
                rs.getString("agen")
            };
            model.addRow(rowData);
                DefaultTableModel aa = (DefaultTableModel) tableDetail.getModel();
    aa.setRowCount(0); // hapus semua baris di tableDetail
        }

        rs.close();
        pst.close();
        conn.close();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error load riwayat: " + e.getMessage());
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

        jPanel6 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        cmbFilter = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableDetail = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableRiwayat = new javax.swing.JTable();
        jdcStart = new com.toedter.calendar.JDateChooser();
        jdcEnd = new com.toedter.calendar.JDateChooser();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jButton12 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1280, 720));
        setSize(new java.awt.Dimension(1080, 720));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBackground(new java.awt.Color(51, 0, 0));
        jPanel6.setPreferredSize(new java.awt.Dimension(1080, 720));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(242, 236, 228));
        jPanel1.setPreferredSize(new java.awt.Dimension(1080, 720));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cmbFilter.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cmbFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Semua", "Hari Ini", "Kemarin", "7 Hari Terakhir", "Bulan Ini", "Tahun Ini" }));
        cmbFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbFilterActionPerformed(evt);
            }
        });
        jPanel1.add(cmbFilter, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 20, 233, 40));

        jPanel3.setBackground(new java.awt.Color(242, 236, 228));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Table Detail Transaksi"));
        jPanel3.setMaximumSize(new java.awt.Dimension(10002, 240));

        tableDetail.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tableDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Id", "Nama Makanan", "Qty", "Harga", "Total", "Keterangan"
            }
        ));
        tableDetail.setRowHeight(35);
        jScrollPane2.setViewportView(tableDetail);
        if (tableDetail.getColumnModel().getColumnCount() > 0) {
            tableDetail.getColumnModel().getColumn(0).setMaxWidth(35);
            tableDetail.getColumnModel().getColumn(1).setMaxWidth(35);
        }

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 360, 1030, 330));

        jPanel4.setBackground(new java.awt.Color(242, 236, 228));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Riwayat Transaksi"));

        tableRiwayat.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tableRiwayat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "No", "ID", "No Transaksi", "Tanggal", "Nama Pelanggan", "Total", "Pelayan", "Agen"
            }
        ));
        tableRiwayat.setRowHeight(35);
        tableRiwayat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableRiwayatMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableRiwayat);
        if (tableRiwayat.getColumnModel().getColumnCount() > 0) {
            tableRiwayat.getColumnModel().getColumn(0).setMaxWidth(35);
            tableRiwayat.getColumnModel().getColumn(1).setMaxWidth(35);
        }

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1020, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, 260));
        jPanel4.getAccessibleContext().setAccessibleDescription("");

        jdcStart.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jdcStartPropertyChange(evt);
            }
        });
        jPanel1.add(jdcStart, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 152, 41));

        jdcEnd.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jdcEndPropertyChange(evt);
            }
        });
        jPanel1.add(jdcEnd, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 20, 152, 41));

        jButton1.setBackground(new java.awt.Color(255, 51, 102));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("CETAK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 20, 95, 41));

        jButton2.setBackground(new java.awt.Color(102, 51, 255));
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("CETAK TAHUNAN");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 20, -1, 40));

        jButton13.setText("Reset");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton13, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 20, 170, 40));

        jPanel6.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 0, -1, -1));

        jPanel2.setBackground(new java.awt.Color(119, 85, 70));

        jPanel5.setBackground(new java.awt.Color(119, 85, 70));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton12.setBackground(new java.awt.Color(102, 51, 255));
        jButton12.setForeground(new java.awt.Color(255, 255, 255));
        jButton12.setText("Info");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jButton3.setBackground(new java.awt.Color(242, 236, 228));
        jButton3.setText("Kasir");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 245, 165, 50));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/logo.png"))); // NOI18N
        jLabel7.setText("jLabel1");
        jPanel5.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jButton4.setBackground(new java.awt.Color(242, 236, 228));
        jButton4.setText("Admin");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 190, 165, 50));

        jButton5.setBackground(new java.awt.Color(242, 236, 228));
        jButton5.setText("Resep");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 520, 165, 50));

        jButton6.setBackground(new java.awt.Color(206, 171, 141));
        jButton6.setText("RiwayatTransaksi");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 300, 165, 50));

        jButton7.setBackground(new java.awt.Color(242, 236, 228));
        jButton7.setText("Pembelian");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 355, 165, 50));

        jButton8.setBackground(new java.awt.Color(242, 236, 228));
        jButton8.setText("Menu");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 410, 165, 50));

        jButton9.setBackground(new java.awt.Color(242, 236, 228));
        jButton9.setText("Inventory");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 465, 165, 50));

        jButton10.setBackground(new java.awt.Color(255, 153, 153));
        jButton10.setText("Log out");
        jButton10.setFocusTraversalPolicyProvider(true);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton10, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 650, 165, 50));

        jButton11.setBackground(new java.awt.Color(242, 236, 228));
        jButton11.setText("Pengguna");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton11, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 575, 165, 50));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 206, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 730, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(11, 11, 11)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(12, 12, 12)))
        );

        jPanel6.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 730));

        getContentPane().add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tableRiwayatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableRiwayatMouseClicked
   int baris = tableRiwayat.getSelectedRow();
if (baris >= 0) {
    String idTransaksi = tableRiwayat.getValueAt(baris, 1).toString(); // kolom ID tersembunyi

    DefaultTableModel model = (DefaultTableModel) tableDetail.getModel();
    model.setRowCount(0); // bersihkan detail lama

    try {
        Connection conn = kasir.koneksi.dbKonek();

        // === 1. Ambil detail menu ===
        String sqlDetail = "SELECT kode_menu, nama_menu, qty, harga, jumlah, keterangan " +
                           "FROM transaksi_detail WHERE id_transaksi = ?";
        PreparedStatement pstDetail = conn.prepareStatement(sqlDetail);
        pstDetail.setInt(1, Integer.parseInt(idTransaksi));
        ResultSet rs = pstDetail.executeQuery();

        int a = 1;
        while (rs.next()) {
            Object[] rowData = {
                a++,
                rs.getString("kode_menu"),
                rs.getString("nama_menu"),
                rs.getInt("qty"),
                rs.getBigDecimal("harga"),
                rs.getBigDecimal("jumlah"),
                rs.getString("keterangan")
            };
            model.addRow(rowData);
        }
        rs.close();
        pstDetail.close();

        String sqlHeader = "SELECT diskon, pajak, service, grand_total " +
                           "FROM transaksi WHERE id = ?";
        PreparedStatement pstHeader = conn.prepareStatement(sqlHeader);
        pstHeader.setInt(1, Integer.parseInt(idTransaksi));
        ResultSet rs2 = pstHeader.executeQuery();

        if (rs2.next()) {
            model.addRow(new Object[]{"", "", "", "", "", "", ""});

            model.addRow(new Object[]{"", "", " ", "", "Diskon:", rs2.getInt("diskon"), ""});
            model.addRow(new Object[]{"", "", " ", "", "Pajak:", rs2.getInt("pajak"), ""});
            model.addRow(new Object[]{"", "", " ", "", "Service:", rs2.getInt("service"), ""});
            model.addRow(new Object[]{"", "", "Grand Total", "", "", rs2.getBigDecimal("grand_total"), ""});
        }

        rs2.close();
        pstHeader.close();
        conn.close();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error load detail: " + e.getMessage());
    }
}

    }//GEN-LAST:event_tableRiwayatMouseClicked

    private void cmbFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbFilterActionPerformed
      filterCMB();        // TODO add your handling code here:
    }//GEN-LAST:event_cmbFilterActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        java.util.Date tglAwal = jdcStart.getDate();
    java.util.Date tglAkhir = jdcEnd.getDate();

    if (tglAwal == null || tglAkhir == null) {
        JOptionPane.showMessageDialog(this, "Pilih tanggal awal dan akhir terlebih dahulu!");
        return;
    }

    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Simpan Laporan Periode");
    SimpleDateFormat sdfFile = new SimpleDateFormat("ddMMyyyy");
    chooser.setSelectedFile(new File("Laporan_Penjualan_" + sdfFile.format(tglAwal) + "_sd_" + sdfFile.format(tglAkhir) + ".pdf"));

    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();

        try {
            com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(doc, new FileOutputStream(file));
            doc.open();

            com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fontSub = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 13, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fontNormal = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            doc.add(new com.itextpdf.text.Paragraph("LAPORAN PENJUALAN PERIODE", fontTitle));
            doc.add(new com.itextpdf.text.Paragraph("Dari: " + sdf.format(tglAwal) + "  s/d  " + sdf.format(tglAkhir), fontNormal));
            doc.add(new com.itextpdf.text.Paragraph(" "));

            Connection conn = kasir.koneksi.dbKonek();

            // total keseluruhan periode
            PreparedStatement pstTotal = conn.prepareStatement(
                "SELECT SUM(grand_total) AS total FROM transaksi WHERE tgl_transaksi BETWEEN ? AND ?");
            pstTotal.setDate(1, new java.sql.Date(tglAwal.getTime()));
            pstTotal.setDate(2, new java.sql.Date(tglAkhir.getTime()));
            ResultSet rsTotal = pstTotal.executeQuery();

            BigDecimal totalPeriode = BigDecimal.ZERO;
            if (rsTotal.next() && rsTotal.getBigDecimal("total") != null) {
                totalPeriode = rsTotal.getBigDecimal("total");
            }
            rsTotal.close();
            pstTotal.close();

            doc.add(new com.itextpdf.text.Paragraph("Total Pemasukan Periode: Rp " + totalPeriode, fontSub));
            doc.add(new com.itextpdf.text.Paragraph(" "));

            // ambil tanggal-tanggal yang ada transaksi
            PreparedStatement pstTgl = conn.prepareStatement(
                "SELECT DISTINCT tgl_transaksi FROM transaksi WHERE tgl_transaksi BETWEEN ? AND ? ORDER BY tgl_transaksi ASC");
            pstTgl.setDate(1, new java.sql.Date(tglAwal.getTime()));
            pstTgl.setDate(2, new java.sql.Date(tglAkhir.getTime()));
            ResultSet rsTgl = pstTgl.executeQuery();

            boolean adaData = false;
            while (rsTgl.next()) {
                adaData = true;
                java.sql.Date tgl = rsTgl.getDate("tgl_transaksi");
                doc.add(new com.itextpdf.text.Paragraph("Tanggal: " + sdf.format(tgl), fontSub));

                // total harian
                PreparedStatement pstTotalHarian = conn.prepareStatement(
                    "SELECT SUM(grand_total) AS total FROM transaksi WHERE tgl_transaksi = ?");
                pstTotalHarian.setDate(1, tgl);
                ResultSet rsTot = pstTotalHarian.executeQuery();
                BigDecimal totalHarian = BigDecimal.ZERO;
                if (rsTot.next() && rsTot.getBigDecimal("total") != null) {
                    totalHarian = rsTot.getBigDecimal("total");
                }
                rsTot.close();
                pstTotalHarian.close();

                doc.add(new com.itextpdf.text.Paragraph("Total Pemasukan: Rp " + totalHarian, fontNormal));
                doc.add(new com.itextpdf.text.Paragraph(" "));

                // ambil transaksi per tanggal
                PreparedStatement pstTrans = conn.prepareStatement(
                    "SELECT * FROM transaksi WHERE tgl_transaksi = ? ORDER BY id ASC");
                pstTrans.setDate(1, tgl);
                ResultSet rsTrans = pstTrans.executeQuery();

                while (rsTrans.next()) {
                    doc.add(new com.itextpdf.text.Paragraph("No Transaksi  : " + rsTrans.getString("no_transaksi"), fontNormal));
                    doc.add(new com.itextpdf.text.Paragraph("Nama Pelanggan: " + rsTrans.getString("nama"), fontNormal));
                    doc.add(new com.itextpdf.text.Paragraph("Pelayan       : " + rsTrans.getString("pelayan"), fontNormal));
                    doc.add(new com.itextpdf.text.Paragraph("Agen          : " + rsTrans.getString("agen"), fontNormal));
                    doc.add(new com.itextpdf.text.Paragraph(" "));

                    // buat tabel barang
                    com.itextpdf.text.pdf.PdfPTable tableBarang = new com.itextpdf.text.pdf.PdfPTable(6);
                    tableBarang.setWidths(new float[]{1f, 5f, 2f, 3f, 3f, 4f});
                    tableBarang.addCell("No");
                    tableBarang.addCell("Nama Menu");
                    tableBarang.addCell("Qty");
                    tableBarang.addCell("Harga");
                    tableBarang.addCell("Total");
                    tableBarang.addCell("Keterangan");

                    PreparedStatement pstDet = conn.prepareStatement(
                        "SELECT nama_menu, qty, harga, jumlah, keterangan FROM transaksi_detail WHERE id_transaksi = ?");
                    pstDet.setInt(1, rsTrans.getInt("id"));
                    ResultSet rsDet = pstDet.executeQuery();

                    int no = 1;
                    while (rsDet.next()) {
                        tableBarang.addCell(String.valueOf(no++));
                        tableBarang.addCell(rsDet.getString("nama_menu"));
                        tableBarang.addCell(String.valueOf(rsDet.getInt("qty")));
                        tableBarang.addCell("Rp " + rsDet.getBigDecimal("harga"));
                        tableBarang.addCell("Rp " + rsDet.getBigDecimal("jumlah"));
                        tableBarang.addCell(rsDet.getString("keterangan") != null ? rsDet.getString("keterangan") : "-");
                    }
                    rsDet.close();
                    pstDet.close();

                    doc.add(tableBarang);
                    doc.add(new com.itextpdf.text.Paragraph(" "));
                }

                rsTrans.close();
                pstTrans.close();
                doc.add(new com.itextpdf.text.Paragraph(" "));
            }

            if (!adaData) {
                doc.add(new com.itextpdf.text.Paragraph("Tidak ada transaksi dalam periode ini.", fontNormal));
            }

            rsTgl.close();
            pstTgl.close();

            conn.close();
            doc.close();
            JOptionPane.showMessageDialog(this, "Laporan periode berhasil disimpan!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error cetak periode: " + e.getMessage());
            e.printStackTrace();
        }
    }

        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    String tahunStr = JOptionPane.showInputDialog(this, "Masukkan tahun (contoh: 2025):");
    if (tahunStr == null || tahunStr.trim().isEmpty()) return;

    int tahun;
    try {
        tahun = Integer.parseInt(tahunStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Tahun tidak valid!");
        return;
    }

    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Simpan Laporan Tahunan");
    chooser.setSelectedFile(new File("Laporan_Penjualan_Tahun_" + tahun + ".pdf"));

    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();

        try {
            com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(doc, new FileOutputStream(file));
            doc.open();

            com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fontSub = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 13, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fontNormal = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10);

            doc.add(new com.itextpdf.text.Paragraph("LAPORAN PENJUALAN TAHUN " + tahun, fontTitle));
            doc.add(new com.itextpdf.text.Paragraph(" "));

            Connection conn = kasir.koneksi.dbKonek();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String[] namaBulan = {
                "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember"
            };

            for (int bulan = 1; bulan <= 12; bulan++) {
                doc.add(new com.itextpdf.text.Paragraph("Bulan " + namaBulan[bulan - 1], fontSub));

                // Total pemasukan bulan
                PreparedStatement pstTotalBulan = conn.prepareStatement(
                    "SELECT SUM(grand_total) AS total_bulan FROM transaksi " +
                    "WHERE EXTRACT(MONTH FROM tgl_transaksi) = ? AND EXTRACT(YEAR FROM tgl_transaksi) = ?");
                pstTotalBulan.setInt(1, bulan);
                pstTotalBulan.setInt(2, tahun);
                ResultSet rsTotalBulan = pstTotalBulan.executeQuery();

                BigDecimal totalBulan = BigDecimal.ZERO;
                if (rsTotalBulan.next() && rsTotalBulan.getBigDecimal("total_bulan") != null) {
                    totalBulan = rsTotalBulan.getBigDecimal("total_bulan");
                }
                doc.add(new com.itextpdf.text.Paragraph("Total Pemasukan Bulan Ini: Rp " + totalBulan, fontNormal));
                doc.add(new com.itextpdf.text.Paragraph(" "));

                rsTotalBulan.close();
                pstTotalBulan.close();

                // Ambil tanggal-tanggal transaksi
                PreparedStatement pstTgl = conn.prepareStatement(
                    "SELECT DISTINCT tgl_transaksi FROM transaksi " +
                    "WHERE EXTRACT(MONTH FROM tgl_transaksi) = ? AND EXTRACT(YEAR FROM tgl_transaksi) = ? ORDER BY tgl_transaksi ASC");
                pstTgl.setInt(1, bulan);
                pstTgl.setInt(2, tahun);
                ResultSet rsTgl = pstTgl.executeQuery();

                boolean adaData = false;
                while (rsTgl.next()) {
                    adaData = true;
                    java.sql.Date tgl = rsTgl.getDate("tgl_transaksi");
                    doc.add(new com.itextpdf.text.Paragraph("Tanggal: " + sdf.format(tgl), fontSub));

                    // total harian
                    PreparedStatement pstTotalHarian = conn.prepareStatement(
                        "SELECT SUM(grand_total) AS total FROM transaksi WHERE tgl_transaksi = ?");
                    pstTotalHarian.setDate(1, tgl);
                    ResultSet rsTot = pstTotalHarian.executeQuery();
                    BigDecimal totalHarian = BigDecimal.ZERO;
                    if (rsTot.next() && rsTot.getBigDecimal("total") != null) {
                        totalHarian = rsTot.getBigDecimal("total");
                    }
                    doc.add(new com.itextpdf.text.Paragraph("Total Pemasukan: Rp " + totalHarian, fontNormal));
                    doc.add(new com.itextpdf.text.Paragraph(" "));

                    rsTot.close();
                    pstTotalHarian.close();

                    // Ambil daftar transaksi harian
                    PreparedStatement pstTrans = conn.prepareStatement(
                        "SELECT * FROM transaksi WHERE tgl_transaksi = ? ORDER BY id ASC");
                    pstTrans.setDate(1, tgl);
                    ResultSet rsTrans = pstTrans.executeQuery();

                    while (rsTrans.next()) {
                        doc.add(new com.itextpdf.text.Paragraph("No Transaksi  : " + rsTrans.getString("no_transaksi"), fontNormal));
                        doc.add(new com.itextpdf.text.Paragraph("Nama Pelanggan: " + rsTrans.getString("nama"), fontNormal));
                        doc.add(new com.itextpdf.text.Paragraph("Pelayan       : " + rsTrans.getString("pelayan"), fontNormal));
                        doc.add(new com.itextpdf.text.Paragraph("Agen          : " + rsTrans.getString("agen"), fontNormal));
                        doc.add(new com.itextpdf.text.Paragraph(" "));

                        // buat tabel barang
                        com.itextpdf.text.pdf.PdfPTable tableBarang = new com.itextpdf.text.pdf.PdfPTable(6);
                        tableBarang.setWidths(new float[]{1f, 5f, 2f, 3f, 3f, 4f});
                        tableBarang.addCell("No");
                        tableBarang.addCell("Nama Menu");
                        tableBarang.addCell("Qty");
                        tableBarang.addCell("Harga");
                        tableBarang.addCell("Total");
                        tableBarang.addCell("Keterangan");

                        PreparedStatement pstDet = conn.prepareStatement(
                            "SELECT nama_menu, qty, harga, jumlah, keterangan FROM transaksi_detail WHERE id_transaksi = ?");
                        pstDet.setInt(1, rsTrans.getInt("id"));
                        ResultSet rsDet = pstDet.executeQuery();

                        int no = 1;
                        while (rsDet.next()) {
                            tableBarang.addCell(String.valueOf(no++));
                            tableBarang.addCell(rsDet.getString("nama_menu"));
                            tableBarang.addCell(String.valueOf(rsDet.getInt("qty")));
                            tableBarang.addCell("Rp " + rsDet.getBigDecimal("harga"));
                            tableBarang.addCell("Rp " + rsDet.getBigDecimal("jumlah"));
                            tableBarang.addCell(rsDet.getString("keterangan") != null ? rsDet.getString("keterangan") : "-");
                        }
                        rsDet.close();
                        pstDet.close();

                        doc.add(tableBarang);
                        doc.add(new com.itextpdf.text.Paragraph(" "));
                    }

                    rsTrans.close();
                    pstTrans.close();
                    doc.add(new com.itextpdf.text.Paragraph(" "));
                }

                if (!adaData) {
                    doc.add(new com.itextpdf.text.Paragraph("Tidak ada transaksi bulan ini.", fontNormal));
                    doc.add(new com.itextpdf.text.Paragraph(" "));
                }

                rsTgl.close();
                pstTgl.close();
            }

            conn.close();
            doc.close();
            JOptionPane.showMessageDialog(this, "Laporan tahunan berhasil disimpan!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error cetak tahunan: " + e.getMessage());
            e.printStackTrace();
        }
    }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jdcEndPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdcEndPropertyChange
 if ("date".equals(evt.getPropertyName())) {
        filterTanggal();
    }    }//GEN-LAST:event_jdcEndPropertyChange

    private void jdcStartPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdcStartPropertyChange
 if ("date".equals(evt.getPropertyName())) {
        filterTanggal();
    }        // TODO add your handling code here:
    }//GEN-LAST:event_jdcStartPropertyChange

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        menuKasir ksr= new menuKasir();
        ksr.setVisible(true);
        this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        admin ad= new admin();
        ad.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
resep res = new resep();
res.setVisible(true);
this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        historyKasir hk = new  historyKasir();
        hk.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        pembelian pb = new pembelian();
        pb.setVisible(true);
        this.dispose();// TODO add your handling code here:
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        inventoryMenu im = new inventoryMenu();
        im.setVisible(true);
        this.dispose();
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        bahan bhn = new bahan();
        bhn.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
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
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        user pu = new user();
        pu.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        info in= new info();
        in.setVisible(true);// TODO add your handling code here:
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
  cmbFilter.setSelectedItem("Bulan Ini");
    filterCMB(); // panggil lagi filter yang sudah ada
    setTanggalBulanIni();
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton13ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {        

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new historyKasir().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cmbFilter;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private com.toedter.calendar.JDateChooser jdcEnd;
    private com.toedter.calendar.JDateChooser jdcStart;
    private javax.swing.JTable tableDetail;
    private javax.swing.JTable tableRiwayat;
    // End of variables declaration//GEN-END:variables
}
