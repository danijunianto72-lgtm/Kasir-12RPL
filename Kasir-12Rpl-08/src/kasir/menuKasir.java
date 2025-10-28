/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package kasir;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.*;
/**
 *
 * @author user
 */
public class menuKasir extends javax.swing.JFrame {

    /**
     * Creates new form menuKasir
     */


public menuKasir() {
    initComponents();
    this.setResizable(false);
this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
this.setVisible(true);

        txtDiskon.setText("0");
    txtPajak.setText("11");
    txtService.setText("11");
        loadRiwayatHariIni();
    jDateChooser.setDate(new java.util.Date()); // isi tanggal hari ini otomatis
loadTotalHariIni();   // refresh total hari ini

    hitungGrandTotal();
    loadAgen();
hitungTotalAkhir();
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    txtTransaksi.setText(generateNoTransaksi());
setdel();
    // Pasang F12 listener
        txtPelayan.setText(Session.namaPelayan);
    setF12Shortcut();
      if (Session.role != null && Session.role.equalsIgnoreCase("admin")) {
            btnAdmin.setVisible(true);   // tampil kalau admin
        } else {
            btnAdmin.setVisible(false);  // sembunyikan kalau bukan admin
        }

    
    // Tambahkan ini di constructor atau setelah initComponents()
this.addWindowListener(new java.awt.event.WindowAdapter() {
    @Override
    public void windowClosing(java.awt.event.WindowEvent e) {
        kembalikanSemuaStok();
    }
});

// Fungsi untuk mengembalikan semua stok dari tabel transaksi

}
private void kembalikanSemuaStok() {
    DefaultTableModel model = (DefaultTableModel) tableTransaksi.getModel();
    int rowCount = model.getRowCount();

    for (int i = 0; i < rowCount; i++) {
        String idmenu = model.getValueAt(i, 0).toString();  // Kolom ID Menu
        int qty = Integer.parseInt(model.getValueAt(i, 2).toString()); // Kolom Qty

        tambahStok(idmenu, qty);
    }

    // Kosongkan tabel (opsional)
    model.setRowCount(0);

    JOptionPane.showMessageDialog(this, "Transaksi dibatalkan, stok sudah dikembalikan.");
}
private void updateStokDiDatabase(String idMenu, int selisih) {
    try (Connection conn = kasir.koneksi.dbKonek()) {
        PreparedStatement pst;

        if (selisih < 0) {
            // Tambah stok kembali (barang dikurangi dari transaksi)
            pst = conn.prepareStatement("UPDATE menu SET stok = stok + ? WHERE idmenu = ?");
            pst.setInt(1, Math.abs(selisih));
        } else {
            // Kurangi stok (barang ditambah di transaksi)
            pst = conn.prepareStatement("UPDATE menu SET stok = stok - ? WHERE idmenu = ? AND stok >= ?");
            pst.setInt(1, selisih);
            pst.setInt(3, selisih);
        }

        pst.setInt(2, Integer.parseInt(idMenu));
        int affected = pst.executeUpdate();

        if (affected == 0) {
            JOptionPane.showMessageDialog(this, "Stok tidak mencukupi!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Gagal update stok!", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
private void editTransaksi() {
    int selectedRow = tableTransaksi.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih data yang ingin diedit dulu!", "Info", JOptionPane.WARNING_MESSAGE);
        return;
    }

    DefaultTableModel model = (DefaultTableModel) tableTransaksi.getModel();
    String kode = model.getValueAt(selectedRow, 0).toString();
    String nama = model.getValueAt(selectedRow, 1).toString();
    int harga = Integer.parseInt(model.getValueAt(selectedRow, 3).toString());
    int qtyLama = Integer.parseInt(model.getValueAt(selectedRow, 2).toString());

    // === Tampilkan input dialog ===
    String input = JOptionPane.showInputDialog(this, 
            "Ubah jumlah untuk \"" + nama + "\":", 
            qtyLama);

    if (input == null || input.trim().isEmpty()) return;

    try {
        int qtyBaru = Integer.parseInt(input.trim());
        if (qtyBaru < 1) {
            JOptionPane.showMessageDialog(this, "Qty tidak boleh kurang dari 1!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update stok di database (kembalikan selisih stok)
        int selisih = qtyBaru - qtyLama;
        updateStokDiDatabase(kode, selisih);

        // Update tampilan tabel
        model.setValueAt(qtyBaru, selectedRow, 2);
        model.setValueAt(harga * qtyBaru, selectedRow, 4);

        // Rehitung subtotal & grandtotal
        hitungSubtotal();

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Input tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

public void tambahTransaksi(String kode, String nama, int hargaSatuan) {
    DefaultTableModel model = (DefaultTableModel) tableTransaksi.getModel();
    boolean found = false;

    for (int i = 0; i < model.getRowCount(); i++) {
        String kodeRow = model.getValueAt(i, 0).toString();
        if (kodeRow.equals(kode)) {
            // update qty dan harga total
            int qty = Integer.parseInt(model.getValueAt(i, 2).toString());
int jumlah = hargaSatuan*qty;
            qty++;
            model.setValueAt(qty, i, 2);
            model.setValueAt(hargaSatuan * qty, i, 3);
            found = true;
            break;
        }
    }
if (!found) {
    int qty = 1;
    int jumlah = hargaSatuan * qty;
    // (Kode, Nama, Qty, Harga, Jumlah, Status, Keterangan)
    model.addRow(new Object[]{kode, nama, qty, hargaSatuan, jumlah, "", ""});
}


    // Hitung subtotal ulang
    hitungSubtotal();
}


// Method khusus untuk shortcut F12
private void setF12Shortcut() {
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        .put(KeyStroke.getKeyStroke("F12"), "pilihAction");

    getRootPane().getActionMap().put("pilihAction", new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            btnPilihMenu.doClick(); // jalankan aksi btnPilih
        }
    });
     getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        .put(KeyStroke.getKeyStroke("F11"), "bahanAction");

    getRootPane().getActionMap().put("bahanAction", new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            btnBahan.doClick(); // jalankan aksi btnPilih
        }
    });
}
private void setdel(){
tableTransaksi.getInputMap(JTable.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DeleteRow");
tableTransaksi.getActionMap().put("DeleteRow", new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
        btnDelete.doClick(); 
    }
});
}
private void tambahStok(String idmenu, int jumlah) {
    try {
        Connection conn = (Connection)kasir.koneksi.dbKonek();
        String sql = "UPDATE menu SET stok = stok + ? WHERE idmenu = ?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setInt(1, jumlah); // jumlah stok yang ditambah
        pst.setInt(2, Integer.parseInt(idmenu)); // idMenu integer
        pst.executeUpdate();
        pst.close();
        conn.close();
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Gagal menambah stok: " + e.getMessage());
    }
}

private void loadRiwayatHariIni() {
    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("No");
    model.addColumn("No Transaksi");
    model.addColumn("Tanggal");
    model.addColumn("Total Transaksi");

    try (Connection conn = kasir.koneksi.dbKonek()) {
        String sql = """
            SELECT no_transaksi, tgl_transaksi, grand_total
            FROM transaksi
            WHERE DATE(tgl_transaksi) = CURRENT_DATE
            ORDER BY tgl_transaksi DESC
        """;

        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        int no = 1;
        while (rs.next()) {
            model.addRow(new Object[]{
                no++,
                rs.getString("no_transaksi"),
                rs.getDate("tgl_transaksi"),
                rs.getBigDecimal("grand_total")
            });
        }

        tblRiwayat.setModel(model);
  tblRiwayat.getColumnModel().getColumn(0).setPreferredWidth(40);   // Kolom "No"
        tblRiwayat.getColumnModel().getColumn(1).setPreferredWidth(40);  // Kolom "No Transaksi"
        tblRiwayat.getColumnModel().getColumn(2).setPreferredWidth(130);  // Kolom "Tanggal"
        tblRiwayat.getColumnModel().getColumn(3).setPreferredWidth(130);  // Kolom "Total Transaksi"

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Gagal memuat riwayat: " + e.getMessage());
    }
}


   private void loadAgen() {
    try {
        Connection conn = (Connection)kasir.koneksi.dbKonek();

        Statement pst = conn.createStatement();
        ResultSet rs = pst.executeQuery("SELECT namaagen FROM agen ORDER BY namaagen ASC");

        while (rs.next()) {
            cmbAgen.addItem(rs.getString("namaagen"));
        }
        rs.close();
        pst.close();
        conn.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal load Agen: " + e.getMessage());
    }
}
   private void hitungSubtotal() {
    DefaultTableModel model = (DefaultTableModel) tableTransaksi.getModel();
    int subtotal = 0;

    for (int i = 0; i < model.getRowCount(); i++) {
        int jumlah = Integer.parseInt(model.getValueAt(i, 4).toString());
        subtotal += jumlah;
    }

    txtSubTotal.setText(String.valueOf(subtotal));
    hitungGrandTotal();
}

   private double defaultDiskon = 0.0;   // 0%
private double defaultService = 10.0; // 10%
private double defaultPajak = 10.0;   // 10%
private void hitungTotalAkhir() {
    int subtotal = 0;
    try {
        subtotal = Integer.parseInt(txtSubTotal.getText().trim());
    } catch (NumberFormatException e) {
        subtotal = 0;
    }
       
    // Diskon %
    double persenDiskon = 0;
    try {
        persenDiskon = Double.parseDouble(txtDiskon.getText().trim());
    } catch (NumberFormatException e) {
        persenDiskon = 0;
    }
    int subDiskon = (int) (subtotal * (persenDiskon / 100.0));
    txtSubDiskon.setText(String.valueOf(subDiskon));

    // Pajak %
    double persenPajak = 0;
    try {
        persenPajak = Double.parseDouble(txtPajak.getText().trim());
    } catch (NumberFormatException e) {
        persenPajak = 0;
    }
    int subPajak = (int) (subtotal * (persenPajak / 100.0));
    txtSubPajak.setText(String.valueOf(subPajak));

    // Service %
    double persenService = 0;
    try {
        persenService = Double.parseDouble(txtService.getText().trim());
    } catch (NumberFormatException e) {
        persenService = 0;
    }
    int subService = (int) (subtotal * (persenService / 100.0));
    txtSubService.setText(String.valueOf(subService));

    // Grand total
 
    int grandTotal = subtotal - subDiskon + subPajak + subService;
    txtGrandTotal.setText(String.valueOf(grandTotal));
    txtTotal.setText(txtGrandTotal.getText());

}
private double getDouble(String text) {
    if (text == null || text.trim().isEmpty()) return 0;
    try {
        return Double.parseDouble(text.trim());
    } catch (NumberFormatException e) {
        return 0;
    }
}
private void hitungKembalian() {
    double grandTotal = getDouble(txtGrandTotal.getText());
    double tunai = getDouble(txtTunai.getText());
    double kredit = getDouble(txtKredit.getText());

    double totalBayar = tunai + kredit;
    double kembalian = totalBayar - grandTotal;

    txtKembalian.setText(String.format("%.0f", kembalian));
}private void tampilkanDetailTransaksiLengkap(String noTransaksi) {
    try (Connection conn = kasir.koneksi.dbKonek()) {

        // === Ambil data header ===
        String sqlHeader = "SELECT * FROM transaksi WHERE no_transaksi = ?";
        PreparedStatement pstHeader = conn.prepareStatement(sqlHeader);
        pstHeader.setString(1, noTransaksi);
        ResultSet rsHeader = pstHeader.executeQuery();

        if (!rsHeader.next()) {
            JOptionPane.showMessageDialog(this, "Data transaksi tidak ditemukan!");
            return;
        }

        // === Ambil data detail ===
        String sqlDetail = """
            SELECT nama_menu, qty, harga, jumlah
            FROM transaksi_detail d
            JOIN transaksi t ON d.id_transaksi = t.id
            WHERE t.no_transaksi = ?
        """;
        PreparedStatement pstDetail = conn.prepareStatement(sqlDetail);
        pstDetail.setString(1, noTransaksi);
        ResultSet rsDetail = pstDetail.executeQuery();

        // === Bangun tampilan ===
        StringBuilder sb = new StringBuilder();
        sb.append("============================================\n");
        sb.append("               DETAIL TRANSAKSI             \n");
        sb.append("============================================\n");
        sb.append(String.format("No Transaksi : %s\n", rsHeader.getString("no_transaksi")));
        sb.append(String.format("Tanggal      : %s\n", rsHeader.getTimestamp("tgl_transaksi")));
        sb.append(String.format("Pelayan      : %s\n", rsHeader.getString("pelayan")));
        sb.append(String.format("Agen         : %s\n", rsHeader.getString("agen")));
        sb.append(String.format("Member       : %s\n", rsHeader.getString("member")));
        sb.append(String.format("Nama         : %s\n", rsHeader.getString("nama")));
        sb.append(String.format("Keterangan   : %s\n", rsHeader.getString("keterangan")));
        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-20s %5s %10s %12s\n", "Item", "Qty", "Harga", "Jumlah"));
        sb.append("--------------------------------------------\n");

        while (rsDetail.next()) {
            sb.append(String.format(
                "%-20s %5d %10s %12s\n",
                rsDetail.getString("nama_menu"),
                rsDetail.getInt("qty"),
                String.format("%,.0f", rsDetail.getDouble("harga")),
                String.format("%,.0f", rsDetail.getDouble("jumlah"))
            ));
        }

        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-20s : %15s\n", "Subtotal", String.format("%,.0f", rsHeader.getDouble("subtotal"))));
        sb.append(String.format("%-20s : %15s\n", "Diskon", String.format("%,.0f", rsHeader.getDouble("diskon"))));
        sb.append(String.format("%-20s : %15s\n", "Pajak", String.format("%,.0f", rsHeader.getDouble("pajak"))));
        sb.append(String.format("%-20s : %15s\n", "Service", String.format("%,.0f", rsHeader.getDouble("service"))));
        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-20s : %15s\n", "Grand Total", String.format("%,.0f", rsHeader.getDouble("grand_total"))));
        sb.append(String.format("%-20s : %15s\n", "Tunai", String.format("%,.0f", rsHeader.getDouble("tunai"))));
        sb.append(String.format("%-20s : %15s\n", "Kredit", String.format("%,.0f", rsHeader.getDouble("kredit"))));
        sb.append(String.format("%-20s : %15s\n", "Kembalian", String.format("%,.0f", rsHeader.getDouble("kembalian"))));
        sb.append(String.format("%-20s : %s\n", "Akun Kas", rsHeader.getString("akun_kas")));
        sb.append("============================================\n");
        sb.append("               TERIMA KASIH 🙏              \n");
        sb.append("============================================\n");

        // === Tampilkan ke popup ===
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        textArea.setBackground(new Color(245, 245, 245));
        textArea.setForeground(Color.DARK_GRAY);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(520, 430));

        JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "Detail Transaksi " + noTransaksi,
            JOptionPane.INFORMATION_MESSAGE
        );

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Gagal tampilkan detail: " + e.getMessage());
    }
}

private void loadTotalHariIni() {
    try (Connection conn = kasir.koneksi.dbKonek()) {
        String sql = "SELECT COALESCE(SUM(grand_total), 0) AS total_hari_ini " +
                     "FROM transaksi " +
                     "WHERE DATE(tgl_transaksi) = CURRENT_DATE";
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            double totalHariIni = rs.getDouble("total_hari_ini");
            lblTotal.setText("Rp " + String.format("%,.0f", totalHariIni));
        }

    } catch (Exception e) {
        e.printStackTrace();
        lblTotal.setText("Rp 0");
    }
}

private void hitungGrandTotal() {
    double subtotal = getDouble(txtSubTotal.getText());
    double diskonPersen = getDouble(txtDiskon.getText());
    double pajakPersen = getDouble(txtPajak.getText());
    double servicePersen = getDouble(txtService.getText());

    // Hitung nominal
    double subDiskon = subtotal * (diskonPersen / 100);
    double subPajak = subtotal * (pajakPersen / 100);
    double subService = subtotal * (servicePersen / 100);

    txtSubDiskon.setText(String.format("%.0f", subDiskon));
    txtSubPajak.setText(String.format("%.0f", subPajak));
    txtSubService.setText(String.format("%.0f", subService));


    double grandTotal = subtotal - subDiskon + subPajak + subService;
    txtGrandTotal.setText(String.format("%.0f", grandTotal));
    txtTotal.setText(String.format("%.0f", grandTotal));

    hitungKembalian();
}

private void resetForm() {
    txtTransaksi.setText(generateNoTransaksi());
    txtMember.setText("-");
    txtNama.setText("");
    cmbAgen.setSelectedIndex(0);   // kembali ke pilihan pertama
    jDateChooser.setDate(new java.util.Date()); // isi tanggal hari ini otomatis
    txtKeterangan.setText("");
    txtSubTotal.setText("0");
    txtDiskon.setText("0");
    txtPajak.setText("11");
    txtService.setText("11");
    txtGrandTotal.setText("0");
    txtTunai.setText("0");
    txtKredit.setText("0");
    txtKembalian.setText("0");
     txtSubDiskon.setText("0");
    txtSubPajak.setText("0");
    txtSubService.setText("0");

    // kosongkan JTable transaksi
    DefaultTableModel model = (DefaultTableModel) tableTransaksi.getModel();
    model.setRowCount(0); // hapus semua baris

    // fokuskan kembali ke field No.Transaksi
    txtTransaksi.requestFocus();
}


private String generateNoTransaksi() {
    String prefix = "TRX";
    String date = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
    String noTransaksi = "";

    try (Connection conn = kasir.koneksi.dbKonek()) {
        String sql = "SELECT no_transaksi FROM transaksi " +
                     "WHERE no_transaksi LIKE ? ORDER BY no_transaksi DESC LIMIT 1";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, prefix + date + "%");
        ResultSet rs = ps.executeQuery();

        int counter = 0;
        if (rs.next()) {
            String lastNo = rs.getString("no_transaksi");
            counter = Integer.parseInt(lastNo.substring(lastNo.length() - 3));
        }
        counter++;

        String counterStr = String.format("%03d", counter);
        noTransaksi = prefix + date + counterStr;

    } catch (Exception e) {
        e.printStackTrace();
        // fallback kalau gagal query
        noTransaksi = prefix + date + "001";
    }

    return noTransaksi;
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        cmbAgen = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        txtPelayan = new javax.swing.JTextField();
        txtNama = new javax.swing.JTextField();
        txtTransaksi = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtMember = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        tpMenu = new javax.swing.JTabbedPane();
        pMenu = new javax.swing.JPanel();
        btnPilihMenu = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableTransaksi = new javax.swing.JTable();
        btnBahan = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtKeterangan = new javax.swing.JTextArea();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtSubTotal = new javax.swing.JTextField();
        txtSubDiskon = new javax.swing.JTextField();
        txtDiskon = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtPajak = new javax.swing.JTextField();
        txtService = new javax.swing.JTextField();
        txtSubPajak = new javax.swing.JTextField();
        txtSubService = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtGrandTotal = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtTunai = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtKredit = new javax.swing.JTextField();
        txtKembalian = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnAdmin = new javax.swing.JButton();
        jDateChooser = new com.toedter.calendar.JDateChooser();
        jPanel4 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblRiwayat = new javax.swing.JTable();
        lblTotal = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1280, 720));
        setSize(new java.awt.Dimension(1280, 720));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 153, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(1280, 720));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(250, 245, 234));
        jPanel2.setPreferredSize(new java.awt.Dimension(1280, 720));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cmbAgen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbAgenActionPerformed(evt);
            }
        });
        jPanel2.add(cmbAgen, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 30, 160, 30));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("No.Transaksi");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 94, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Pelayan");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 80, 94, -1));

        txtTotal.setEditable(false);
        txtTotal.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel2.add(txtTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 60, 175, 50));

        txtPelayan.setEditable(false);
        txtPelayan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPelayanActionPerformed(evt);
            }
        });
        jPanel2.add(txtPelayan, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 80, 160, 30));

        txtNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaActionPerformed(evt);
            }
        });
        jPanel2.add(txtNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 80, 160, 30));

        txtTransaksi.setEditable(false);
        txtTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTransaksiActionPerformed(evt);
            }
        });
        jPanel2.add(txtTransaksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 30, 160, 30));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Tanggal ");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 80, 94, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Agen");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 30, 94, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Member");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 30, 94, -1));

        txtMember.setText("-");
        txtMember.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMemberActionPerformed(evt);
            }
        });
        jPanel2.add(txtMember, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 30, 160, 30));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel7.setText("Total : ");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 20, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Nama");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 80, 94, 20));

        jPanel3.setBackground(new java.awt.Color(250, 245, 234));

        tpMenu.setBackground(new java.awt.Color(250, 245, 234));
        tpMenu.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        pMenu.setBackground(new java.awt.Color(250, 245, 234));
        pMenu.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pMenu.setPreferredSize(new java.awt.Dimension(1080, 439));

        btnPilihMenu.setBackground(new java.awt.Color(0, 102, 255));
        btnPilihMenu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnPilihMenu.setForeground(new java.awt.Color(255, 255, 255));
        btnPilihMenu.setMnemonic('p');
        btnPilihMenu.setText("[F12] Pilih Menu");
        btnPilihMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPilihMenuActionPerformed(evt);
            }
        });

        btnEdit.setBackground(new java.awt.Color(255, 102, 0));
        btnEdit.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnEdit.setForeground(new java.awt.Color(255, 255, 255));
        btnEdit.setText("[F2] EDIT");
        btnEdit.setActionCommand("[F2] Edit");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(255, 51, 51));
        btnDelete.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("[DEL] DELETE");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        tableTransaksi.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tableTransaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Nama", "Qty", "totalHarga", "harga/item", "Status", "Keterangan"
            }
        ));
        tableTransaksi.setColumnSelectionAllowed(true);
        tableTransaksi.setRowHeight(30);
        tableTransaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableTransaksiMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableTransaksi);
        tableTransaksi.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        btnBahan.setBackground(new java.awt.Color(204, 153, 0));
        btnBahan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnBahan.setForeground(new java.awt.Color(255, 255, 255));
        btnBahan.setMnemonic('p');
        btnBahan.setText("[F11] Inventory");
        btnBahan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBahanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pMenuLayout = new javax.swing.GroupLayout(pMenu);
        pMenu.setLayout(pMenuLayout);
        pMenuLayout.setHorizontalGroup(
            pMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pMenuLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(pMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 970, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pMenuLayout.createSequentialGroup()
                        .addComponent(btnPilihMenu)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBahan)
                        .addGap(16, 16, 16)
                        .addComponent(btnEdit)
                        .addGap(18, 18, 18)
                        .addComponent(btnDelete)))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        pMenuLayout.setVerticalGroup(
            pMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pMenuLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(pMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPilihMenu)
                    .addComponent(btnEdit)
                    .addComponent(btnDelete)
                    .addComponent(btnBahan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        tpMenu.addTab("[F13] Menu", pMenu);

        jPanel5.setBackground(new java.awt.Color(250, 245, 234));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Pembayaran"));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtKeterangan.setColumns(20);
        txtKeterangan.setRows(5);
        txtKeterangan.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtKeteranganFocusLost(evt);
            }
        });
        jScrollPane2.setViewportView(txtKeterangan);

        jPanel5.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 224, 161));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Keterangan : ");
        jPanel5.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, 34));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Sub Total :");
        jPanel5.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(249, 34, -1, -1));

        txtSubTotal.setEditable(false);
        txtSubTotal.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtSubTotal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSubTotalKeyReleased(evt);
            }
        });
        jPanel5.add(txtSubTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(371, 24, 320, 41));

        txtSubDiskon.setEditable(false);
        jPanel5.add(txtSubDiskon, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 76, 170, 41));

        txtDiskon.setText("0");
        txtDiskon.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDiskonFocusLost(evt);
            }
        });
        txtDiskon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDiskonActionPerformed(evt);
            }
        });
        txtDiskon.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDiskonKeyReleased(evt);
            }
        });
        jPanel5.add(txtDiskon, new org.netbeans.lib.awtextra.AbsoluteConstraints(371, 76, 130, 41));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Diskon     :");
        jPanel5.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 80, -1, 26));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Pajak       :");
        jPanel5.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(249, 136, 93, 25));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("Service    :");
        jPanel5.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(249, 178, 125, 39));

        txtPajak.setText("0");
        txtPajak.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPajakFocusLost(evt);
            }
        });
        txtPajak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPajakActionPerformed(evt);
            }
        });
        txtPajak.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPajakKeyReleased(evt);
            }
        });
        jPanel5.add(txtPajak, new org.netbeans.lib.awtextra.AbsoluteConstraints(371, 129, 130, 40));

        txtService.setText("0");
        txtService.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtServiceFocusLost(evt);
            }
        });
        txtService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtServiceActionPerformed(evt);
            }
        });
        txtService.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtServiceKeyReleased(evt);
            }
        });
        jPanel5.add(txtService, new org.netbeans.lib.awtextra.AbsoluteConstraints(371, 178, 130, 41));

        txtSubPajak.setEditable(false);
        jPanel5.add(txtSubPajak, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 129, 170, 41));

        txtSubService.setEditable(false);
        jPanel5.add(txtSubService, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 180, 170, 41));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setText("Grand Total :");
        jPanel5.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 20, 106, 45));

        txtGrandTotal.setEditable(false);
        txtGrandTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGrandTotalActionPerformed(evt);
            }
        });
        jPanel5.add(txtGrandTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 20, 190, 41));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setText("[F9] Tunai    :");
        jPanel5.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(706, 76, 90, 32));

        txtTunai.setText("0");
        txtTunai.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTunaiFocusLost(evt);
            }
        });
        txtTunai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTunaiActionPerformed(evt);
            }
        });
        txtTunai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTunaiKeyReleased(evt);
            }
        });
        jPanel5.add(txtTunai, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 80, 190, 41));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel19.setText("[F10] Kredit :");
        jPanel5.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 130, 90, 41));

        txtKredit.setText("0");
        txtKredit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKreditActionPerformed(evt);
            }
        });
        txtKredit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKreditKeyReleased(evt);
            }
        });
        jPanel5.add(txtKredit, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 130, 190, 41));

        txtKembalian.setEditable(false);
        txtKembalian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKembalianActionPerformed(evt);
            }
        });
        jPanel5.add(txtKembalian, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 180, 190, 41));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel20.setText("Kembalian  :");
        jPanel5.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 180, 90, 45));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel21.setText("Akun Kas    :");
        jPanel5.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(706, 232, 123, 43));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel22.setText("Uang di Tangan");
        jPanel5.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(847, 232, 146, 43));

        btnCancel.setBackground(new java.awt.Color(255, 255, 0));
        btnCancel.setText("Cancel");
        btnCancel.setActionCommand("CENCEL");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jPanel5.add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(472, 252, 206, 60));

        btnSave.setBackground(new java.awt.Color(0, 255, 51));
        btnSave.setText("BAYAR");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel5.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(249, 252, 205, 60));

        btnAdmin.setText("MASUK KE ADMIN");
        btnAdmin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdminActionPerformed(evt);
            }
        });
        jPanel5.add(btnAdmin, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 250, 210, 60));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tpMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 1012, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(46, 46, 46))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(tpMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(128, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 121, 1020, 720));

        jDateChooser.setEnabled(false);
        jPanel2.add(jDateChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 80, 160, 30));

        jPanel4.setBackground(new java.awt.Color(250, 245, 234));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBackground(new java.awt.Color(119, 85, 70));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Riwayat Transaksi Hari Ini");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addContainerGap(204, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel8)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, -1, -1));

        tblRiwayat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblRiwayat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblRiwayatMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblRiwayat);

        jPanel4.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 230, 590));

        lblTotal.setText("txtTotal");
        jPanel4.add(lblTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 670, -1, -1));

        jPanel2.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 10, 250, 700));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(-5, 0, 1290, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 720));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbAgenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbAgenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbAgenActionPerformed

    private void txtPelayanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPelayanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPelayanActionPerformed

    private void txtTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTransaksiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTransaksiActionPerformed

    private void txtMemberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMemberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMemberActionPerformed

    private void txtNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNamaActionPerformed

    private void txtSubTotalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSubTotalKeyReleased

    }//GEN-LAST:event_txtSubTotalKeyReleased

    private void txtDiskonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiskonFocusLost

    }//GEN-LAST:event_txtDiskonFocusLost

    private void txtDiskonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDiskonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDiskonActionPerformed

    private void txtPajakFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPajakFocusLost

    }//GEN-LAST:event_txtPajakFocusLost

    private void txtPajakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPajakActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPajakActionPerformed

    private void txtServiceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtServiceFocusLost

    }//GEN-LAST:event_txtServiceFocusLost

    private void txtServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtServiceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtServiceActionPerformed

    private void txtGrandTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGrandTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtGrandTotalActionPerformed

    private void txtTunaiFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTunaiFocusLost
       
    }//GEN-LAST:event_txtTunaiFocusLost

    private void txtTunaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTunaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTunaiActionPerformed

    private void txtKreditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKreditActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKreditActionPerformed

    private void txtKembalianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKembalianActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKembalianActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
kembalikanSemuaStok();
        resetForm();        // TODO add your handling code here:
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
   Connection conn = null;

        try {
              DefaultTableModel model = (DefaultTableModel) tableTransaksi.getModel();

    // 1️⃣ Cek apakah tabel kosong
    if (model.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, 
            "Belum ada menu yang dipesan!", 
            "Peringatan", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    // 2️⃣ Cek apakah kembalian negatif
    BigDecimal kembalian = new BigDecimal(txtKembalian.getText());
    if (kembalian.compareTo(BigDecimal.ZERO) < 0) {
        JOptionPane.showMessageDialog(this, 
            "Uang tidak cukup untuk membayar!", 
            "Peringatan", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    conn = kasir.koneksi.dbKonek();

        conn.setAutoCommit(false); // mulai transaksi

        // ==== INSERT HEADER TRANSAKSI ====
        String sql = "INSERT INTO transaksi(no_transaksi, member, nama, agen, pelayan, tgl_transaksi, keterangan, subtotal, diskon, pajak, service, grand_total, tunai, kredit, kembalian, akun_kas) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING id";

        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, txtTransaksi.getText());
        pst.setString(2, txtMember.getText());
        pst.setString(3, txtNama.getText());
        pst.setString(4, cmbAgen.getSelectedItem().toString());
        pst.setString(5, txtPelayan.getText());
        pst.setDate(6, new java.sql.Date(jDateChooser.getDate().getTime()));
        pst.setString(7, txtKeterangan.getText());
        pst.setBigDecimal(8, new java.math.BigDecimal(txtSubTotal.getText()));
        pst.setBigDecimal(9, new java.math.BigDecimal(txtDiskon.getText()));
        pst.setBigDecimal(10, new java.math.BigDecimal(txtPajak.getText()));
        pst.setBigDecimal(11, new java.math.BigDecimal(txtService.getText()));
        pst.setBigDecimal(12, new java.math.BigDecimal(txtGrandTotal.getText()));
        pst.setBigDecimal(13, new java.math.BigDecimal(txtTunai.getText()));
        pst.setBigDecimal(14, new java.math.BigDecimal(txtKredit.getText()));
        pst.setBigDecimal(15, new java.math.BigDecimal(txtKembalian.getText()));
        pst.setString(16, "Uang di Tangan");

        ResultSet rs = pst.executeQuery();
if (rs.next()) {
    int idTransaksi = rs.getInt(1); // ✅ aman
    System.out.println("ID Transaksi = " + idTransaksi);
} else {
    throw new SQLException("Gagal ambil ID transaksi!");
}

        int idTransaksi = rs.getInt(1); // ambil ID transaksi

        // ==== INSERT DETAIL MENU ====
//        DefaultTableModel model = (DefaultTableModel) tableTransaksi.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            String sqlDetail = "INSERT INTO transaksi_detail(id_transaksi, kode_menu, nama_menu, qty, harga, jumlah, status, keterangan) " +
                               "VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement pstDetail = conn.prepareStatement(sqlDetail);
            pstDetail.setInt(1, idTransaksi);
            pstDetail.setString(2, model.getValueAt(i, 0).toString()); // kode
            pstDetail.setString(3, model.getValueAt(i, 1).toString()); // nama
            pstDetail.setInt(4, Integer.parseInt(model.getValueAt(i, 2).toString())); // qty
            pstDetail.setBigDecimal(5, new java.math.BigDecimal(model.getValueAt(i, 3).toString())); // harga
            pstDetail.setBigDecimal(6, new java.math.BigDecimal(model.getValueAt(i, 4).toString())); // jumlah
            pstDetail.setString(7, model.getValueAt(i, 5).toString()); // status
            pstDetail.setString(8, model.getValueAt(i, 6).toString()); // keterangan
            pstDetail.executeUpdate();
        }

        conn.commit(); // simpan
        JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan!");
        resetForm();
loadRiwayatHariIni();
loadTotalHariIni();   // refresh total hari ini



    } catch (Exception e) {
    if (conn != null) {
        try { conn.rollback(); } catch (Exception ex) {}
    }
    e.printStackTrace(); // <<<< ini biar kelihatan error asli di console
    JOptionPane.showMessageDialog(this, "Gagal simpan: " + e.getMessage());
}

        // TODO add your handling code here:
    }//GEN-LAST:event_btnSaveActionPerformed

    private void txtKeteranganFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtKeteranganFocusLost
 int selectedRow = tableTransaksi.getSelectedRow();
    if (selectedRow >= 0) {
        DefaultTableModel model = (DefaultTableModel) tableTransaksi.getModel();
        model.setValueAt(txtKeterangan.getText(), selectedRow, 6);
        txtKeterangan.setText("");
    }        // TODO add your handling code here:
    }//GEN-LAST:event_txtKeteranganFocusLost

    private void tableTransaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableTransaksiMouseClicked
        int selectedRow = tableTransaksi.getSelectedRow();
        if (selectedRow >= 0) {
            String ket = tableTransaksi.getValueAt(selectedRow, 6) != null ?
            tableTransaksi.getValueAt(selectedRow, 6).toString() : "";
            txtKeterangan.setText(ket);
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_tableTransaksiMouseClicked

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int selectedRow = tableTransaksi.getSelectedRow();

        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) tableTransaksi.getModel();

            String idmenu = model.getValueAt(selectedRow, 0).toString(); // Kode menu
            int qty = Integer.parseInt(model.getValueAt(selectedRow, 2).toString()); // Qty yang dipesan

            // Hapus baris dari tabel
            model.removeRow(selectedRow);

            // Tambahkan stok di database
            tambahStok(idmenu, qty);

            hitungSubtotal();
        } else {
            JOptionPane.showMessageDialog(this, "Pilih item yang mau dihapus terlebih dahulu.");
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnPilihMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPilihMenuActionPerformed
     MenuAll menuFrame = new MenuAll(this); 
    menuFrame.setVisible(true); 
    }//GEN-LAST:event_btnPilihMenuActionPerformed

    private void txtDiskonKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDiskonKeyReleased
    hitungGrandTotal();
// TODO add your handling code here:
    }//GEN-LAST:event_txtDiskonKeyReleased

    private void txtPajakKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPajakKeyReleased
    hitungGrandTotal();
    }//GEN-LAST:event_txtPajakKeyReleased

    private void txtServiceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtServiceKeyReleased
    hitungGrandTotal();
        // TODO add your handling code here:
    }//GEN-LAST:event_txtServiceKeyReleased

    private void txtTunaiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTunaiKeyReleased
hitungKembalian();        // TODO add your handling code here:
    }//GEN-LAST:event_txtTunaiKeyReleased

    private void txtKreditKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKreditKeyReleased
hitungKembalian();        // TODO add your handling code here:
    }//GEN-LAST:event_txtKreditKeyReleased

    private void btnAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdminActionPerformed
kembalikanSemuaStok();
        admin ad = new admin();
ad.setVisible(true);
this.dispose();
    }//GEN-LAST:event_btnAdminActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
    editTransaksi();
        // TODO add your handling code here:
    }//GEN-LAST:event_btnEditActionPerformed

    private void tblRiwayatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRiwayatMouseClicked
 int row = tblRiwayat.getSelectedRow();
    if (row == -1) return;

    String noTransaksi = tblRiwayat.getValueAt(row, 1).toString();
        tampilkanDetailTransaksiLengkap(noTransaksi);
    }//GEN-LAST:event_tblRiwayatMouseClicked

    private void btnBahanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBahanActionPerformed
jDialogBahan dialog = new jDialogBahan(this, true, this);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);  
    }//GEN-LAST:event_btnBahanActionPerformed
 
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
            java.util.logging.Logger.getLogger(menuKasir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(menuKasir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(menuKasir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(menuKasir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new menuKasir().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdmin;
    private javax.swing.JButton btnBahan;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnPilihMenu;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cmbAgen;
    private com.toedter.calendar.JDateChooser jDateChooser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JPanel pMenu;
    private javax.swing.JTable tableTransaksi;
    private javax.swing.JTable tblRiwayat;
    private javax.swing.JTabbedPane tpMenu;
    private javax.swing.JTextField txtDiskon;
    private javax.swing.JTextField txtGrandTotal;
    private javax.swing.JTextField txtKembalian;
    private javax.swing.JTextArea txtKeterangan;
    private javax.swing.JTextField txtKredit;
    private javax.swing.JTextField txtMember;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtPajak;
    private javax.swing.JTextField txtPelayan;
    private javax.swing.JTextField txtService;
    private javax.swing.JTextField txtSubDiskon;
    private javax.swing.JTextField txtSubPajak;
    private javax.swing.JTextField txtSubService;
    private javax.swing.JTextField txtSubTotal;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtTransaksi;
    private javax.swing.JTextField txtTunai;
    // End of variables declaration//GEN-END:variables
}
