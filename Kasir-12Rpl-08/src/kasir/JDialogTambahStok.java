/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package kasir;

import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
public class JDialogTambahStok extends javax.swing.JDialog {

    /**
     * Creates new form JDialogTambahStok
     */
    public JDialogTambahStok(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        txtJumlah.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
    public void insertUpdate(javax.swing.event.DocumentEvent e) { updateInfo(); }
    public void removeUpdate(javax.swing.event.DocumentEvent e) { updateInfo(); }
    public void changedUpdate(javax.swing.event.DocumentEvent e) { updateInfo(); }
});

    }
    
    
    
private int idMenu;
private String namaMenu;
private java.util.List<BahanResep> listBahan = new java.util.ArrayList<>();
private static class BahanResep {
    String namaBahan;
    double jumlahPerMenu;
    String satuan;

    BahanResep(String namaBahan, double jumlahPerMenu, String satuan) {
        this.namaBahan = namaBahan;
        this.jumlahPerMenu = jumlahPerMenu;
        this.satuan = satuan;
    }
}

public void setIdMenu(int idMenu) {
    this.idMenu = idMenu;
}

public void setNamaMenu(String namaMenu) {
    this.namaMenu = namaMenu;
    lblNamaMenu.setText("Tambah Menu "+namaMenu);
}

private boolean cekStokCukup(int jumlahProduksi) {
    try (Connection conn = kasir.koneksi.dbKonek()) {
        for (BahanResep b : listBahan) {
            String sql = "SELECT stok FROM bahan WHERE namabahan = ?";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, b.namaBahan);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    double stokSekarang = rs.getDouble("stok");
                    double kebutuhan = b.jumlahPerMenu * jumlahProduksi;

                    if (stokSekarang < kebutuhan) {
                        JOptionPane.showMessageDialog(this,
                            "Stok bahan tidak cukup untuk: " + b.namaBahan +
                            "\nDibutuhkan: " + kebutuhan + " " + b.satuan +
                            "\nTersedia: " + stokSekarang + " " + b.satuan,
                            "Stok Tidak Cukup", JOptionPane.WARNING_MESSAGE);
                        return false; // stop proses
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Bahan " + b.namaBahan + " tidak ditemukan di database!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        return true; // semua cukup
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal cek stok: " + e.getMessage());
        return false;
    }
}
public void loadInfoBahan() {
    txtInfoBahan.setText("");
    listBahan.clear();

    try (Connection conn = kasir.koneksi.dbKonek()) {
        String sql = """
            SELECT b.namabahan, r.jumlah_bahan, b.satuan
            FROM resep r
            JOIN bahan b ON r.idbahan = b.idbahan
            WHERE r.idmenu = ?
        """;
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idMenu);
            ResultSet rs = pst.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                String nama = rs.getString("namabahan");
                double jumlah = rs.getDouble("jumlah_bahan");
                String satuan = rs.getString("satuan");

                // simpan ke list
                listBahan.add(new BahanResep(nama, jumlah, satuan));

                sb.append(nama)
                  .append(" - ")
                  .append(jumlah)
                  .append(" ")
                  .append(satuan)
                  .append("\n");
            }
            txtInfoBahan.setText(sb.toString());
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal load bahan: " + e.getMessage());
    }
}
private void updateInfo() {
    String jumlahStr = txtJumlah.getText().trim();
    int jumlahProduksi = 0;

    try {
        jumlahProduksi = Integer.parseInt(jumlahStr);
    } catch (NumberFormatException e) {
        txtInfoBahan.setText("-");
        return;
    }

    if (jumlahProduksi <= 0) {
        txtInfoBahan.setText("Jumlah harus lebih dari 0!");
        return;
    }

    StringBuilder sb = new StringBuilder();
    for (BahanResep b : listBahan) {
        double total = b.jumlahPerMenu * jumlahProduksi;
        sb.append(b.namaBahan)
          .append(" -")
          .append(total)
          .append("")
          .append(b.satuan)
          .append("\n");
    }

    txtInfoBahan.setText(sb.toString());
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
        lblNamaMenu = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtJumlah = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtInfoBahan = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(242, 236, 228));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(206, 171, 141));

        lblNamaMenu.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblNamaMenu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNamaMenu.setText("jLabel2");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblNamaMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(131, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addComponent(lblNamaMenu)
                .addContainerGap())
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(-14, 0, 490, 50));

        jLabel1.setText("Jumlah Tambah");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, -1, -1));

        txtJumlah.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel1.add(txtJumlah, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 80, 320, 50));

        jButton1.setBackground(new java.awt.Color(102, 102, 102));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("BATAL");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 290, 140, 50));

        jButton2.setBackground(new java.awt.Color(0, 255, 102));
        jButton2.setText("simpan");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, 130, 50));

        txtInfoBahan.setColumns(20);
        txtInfoBahan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtInfoBahan.setRows(5);
        jScrollPane1.setViewportView(txtInfoBahan);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 140, 320, 140));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 350, 360));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
String jumlahStr = txtJumlah.getText().trim();
if (jumlahStr.isEmpty()) {
    JOptionPane.showMessageDialog(this, "Masukkan jumlah stok yang ingin ditambah!");
    return;
}

int jumlahTambah;
try {
    jumlahTambah = Integer.parseInt(jumlahStr);
} catch (NumberFormatException e) {
    JOptionPane.showMessageDialog(this, "Jumlah harus angka!");
    return;
}

try (Connection conn = kasir.koneksi.dbKonek()) {
    conn.setAutoCommit(false); // mulai transaksi

    // 🔹 1. Cek stok bahan dulu sebelum dikurangi
    String cekBahan = """
        SELECT b.idbahan, b.namabahan, b.stok, r.jumlah_bahan, b.satuan
        FROM resep r
        JOIN bahan b ON r.idbahan = b.idbahan
        WHERE r.idmenu = ?
    """;
    boolean stokKurang = false;
    StringBuilder pesan = new StringBuilder();

    try (PreparedStatement pst = conn.prepareStatement(cekBahan)) {
        pst.setInt(1, idMenu);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            double stokBahan = rs.getDouble("stok");
            double jumlahPerMenu = rs.getDouble("jumlah_bahan");
            double totalKurang = jumlahPerMenu * jumlahTambah;
            String namaBahan = rs.getString("namabahan");
            String satuan = rs.getString("satuan");

            if (totalKurang > stokBahan) {
                stokKurang = true;
                pesan.append("❌ Stok bahan tidak cukup untuk: ")
                     .append(namaBahan)
                     .append(" (butuh ")
                     .append(totalKurang)
                     .append(" ")
                     .append(satuan)
                     .append(", stok tersedia: ")
                     .append(stokBahan)
                     .append(" ")
                     .append(satuan)
                     .append(")\n");
            }
        }
    }

    // ❌ Kalau stok bahan kurang, batalkan
    if (stokKurang) {
        JOptionPane.showMessageDialog(this, pesan.toString(), "Stok Bahan Tidak Cukup", JOptionPane.WARNING_MESSAGE);
        conn.rollback();
        return;
    }

    // 🔹 2. Update stok menu
    String sqlMenu = "UPDATE menu SET stok = stok + ? WHERE idmenu = ?";
    try (PreparedStatement pst = conn.prepareStatement(sqlMenu)) {
        pst.setInt(1, jumlahTambah);
        pst.setInt(2, idMenu);
        pst.executeUpdate();
    }

    // 🔹 3. Kurangi stok bahan
    String sqlBahan = """
        SELECT idbahan, jumlah_bahan
        FROM resep
        WHERE idmenu = ?
    """;
    try (PreparedStatement pst = conn.prepareStatement(sqlBahan)) {
        pst.setInt(1, idMenu);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            int idBahan = rs.getInt("idbahan");
            double jumlahPerMenu = rs.getDouble("jumlah_bahan");
            double totalKurang = jumlahPerMenu * jumlahTambah;

            String updateBahan = "UPDATE bahan SET stok = stok - ? WHERE idbahan = ?";
            try (PreparedStatement pst2 = conn.prepareStatement(updateBahan)) {
                pst2.setDouble(1, totalKurang);
                pst2.setInt(2, idBahan);
                pst2.executeUpdate();
            }
        }
    }

    conn.commit();
    JOptionPane.showMessageDialog(this, "✅ Stok menu berhasil ditambah dan bahan telah dikurangi!");
    dispose();

} catch (Exception e) {
    JOptionPane.showMessageDialog(this, "Gagal update stok: " + e.getMessage());
    e.printStackTrace();
}
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    dispose();
        // TODO add your handling code here:
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
            java.util.logging.Logger.getLogger(JDialogTambahStok.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDialogTambahStok.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDialogTambahStok.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialogTambahStok.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialogTambahStok dialog = new JDialogTambahStok(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblNamaMenu;
    private javax.swing.JTextArea txtInfoBahan;
    private javax.swing.JTextField txtJumlah;
    // End of variables declaration//GEN-END:variables
}
