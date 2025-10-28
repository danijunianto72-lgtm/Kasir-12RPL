/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package kasir;

import javax.swing.JLabel;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.*;
   import java.sql.Statement;
/**
 *
 * @author yaniyan
 */
public class MenuAll extends javax.swing.JFrame {

    /**
     * Creates new form MenuAll
     */
    private menuKasir kasirFrame;  // simpan referensi frame kasir

public MenuAll(menuKasir kasirFrame) {
    initComponents();
        this.kasirFrame = kasirFrame;

                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // <-- PENTING
loadKategori();
  panelAll.setLayout(new WrapLayout(FlowLayout.LEFT, 5, 5));
    panelAll.setBackground(Color.WHITE);

    // Pastikan jScrollPane1 sudah ada di form, tinggal atur policy
    jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    loadMenuFromDatabase("Semua","");
}



public MenuAll() {

    initComponents();
setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    // Atur layout panelAll agar bisa scroll dua arah
    panelAll.setLayout(new WrapLayout(FlowLayout.LEFT, 5, 5));
    panelAll.setBackground(new Color(242,236,228));

    // Pastikan jScrollPane1 sudah ada di form, tinggal atur policy
    jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

     }

private void loadKategori() {
    try {
        Connection conn = kasir.koneksi.dbKonek();
        String sql = "SELECT namakategori FROM kategori ORDER BY namakategori ASC";
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        cmbKategori.removeAllItems(); // hapus isi lama
        cmbKategori.addItem("Semua"); // <-- pilihan default

        while (rs.next()) {
            String namaKategori = rs.getString("namakategori");
            cmbKategori.addItem(namaKategori);
        }

        rs.close();
        pst.close();
        conn.close();

        // Event listener


    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Gagal memuat kategori: " + e.getMessage());
    }
}
 private PreparedStatement createPreparedStatement(Connection conn, String kategori, String keyword) throws SQLException {
    String baseQuery = "SELECT idmenu, namamenu, hargamenu, stok, gambar FROM menu";
    
    // kalau tidak ada filter → tampilkan semua
    if ((kategori == null || kategori.equals("Semua")) && (keyword == null || keyword.isEmpty())) {
        return conn.prepareStatement(baseQuery);
    }

    // buat kondisi
    StringBuilder sql = new StringBuilder(baseQuery + " WHERE 1=1");
    if (kategori != null && !kategori.equals("Semua")) {
        sql.append(" AND kategori = ?");
    }
if (keyword != null && !keyword.isEmpty()) {
    sql.append(" AND LOWER(namamenu) LIKE ?");
}

    PreparedStatement pst = conn.prepareStatement(sql.toString());

    int paramIndex = 1;
    if (kategori != null && !kategori.equals("Semua")) {
        pst.setString(paramIndex++, kategori);
    }
if (keyword != null && !keyword.isEmpty()) {
    pst.setString(paramIndex++, "%" + keyword.toLowerCase() + "%");
}


    return pst;
}

private void loadMenuFromDatabase(String kategoriFilter, String keyword) {
    try (Connection conn = kasir.koneksi.dbKonek();
         PreparedStatement pst = createPreparedStatement(conn, kategoriFilter, keyword);
         ResultSet rs = pst.executeQuery()) {

        panelAll.removeAll(); // hapus semua card lama

        while (rs.next()) {
            String kode = rs.getString("idmenu");
            String nama = rs.getString("namamenu");
            int harga = rs.getInt("hargamenu");
            int stok = rs.getInt("stok");
            String gambarPath = rs.getString("gambar");

            // === Card ===
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setPreferredSize(new Dimension(200, 280));
            card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            card.setBackground(Color.WHITE);

            // ==== Gambar ====
            JLabel lblImage = new JLabel();
            lblImage.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblImage.setPreferredSize(new Dimension(170, 170));
            try {
                ImageIcon icon = new ImageIcon(gambarPath);
                Image img = icon.getImage().getScaledInstance(170, 170, Image.SCALE_SMOOTH);
                lblImage.setIcon(new ImageIcon(img));
            } catch (Exception e) {
                lblImage.setText("No Image");
                lblImage.setHorizontalAlignment(JLabel.CENTER);
            }
            card.add(Box.createVerticalStrut(5));
            card.add(lblImage);
            card.add(Box.createVerticalStrut(5));

            // ==== Nama menu ====
            JLabel lblNama = new JLabel(nama, JLabel.CENTER);
            lblNama.setFont(new Font("SansSerif", Font.BOLD, 16));
            lblNama.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(lblNama);

            // ==== Info harga & stok ====
            JPanel panelInfo = new JPanel(new GridLayout(2, 1));
            panelInfo.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 0));
            panelInfo.setOpaque(false);

            JLabel lblHarga = new JLabel("Harga: Rp " + harga);
            lblHarga.setFont(new Font("SansSerif", Font.PLAIN, 12));
            panelInfo.add(lblHarga);

            JLabel lblStok = new JLabel("Stok: " + stok);
            lblStok.setFont(new Font("SansSerif", Font.PLAIN, 12));
            panelInfo.add(lblStok);

            card.add(panelInfo);

            // ==== Tombol Beli ====
            JButton btnBeli = new JButton("Tambah Ke Keranjang");
            btnBeli.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnBeli.setMaximumSize(new Dimension(card.getPreferredSize().width - 20, 35));

            final JLabel lblStokFinal = lblStok;
            final int[] stokArr = { stok };
            String namaFinal = nama;
            int hargaFinal = harga;
            

            btnBeli.addActionListener(e -> {
                try (Connection connUpdate = kasir.koneksi.dbKonek();
                     PreparedStatement pstUpdate = connUpdate.prepareStatement(
                         "UPDATE menu SET stok = stok - 1 WHERE idmenu = ? AND stok > 0"
                     )) {

                    pstUpdate.setInt(1, Integer.parseInt(kode));
                    int affected = pstUpdate.executeUpdate();

                    if (affected > 0) {
                        stokArr[0]--;
                        lblStokFinal.setText("Stok: " + stokArr[0]);

                        if (stokArr[0] <= 0) {
                            btnBeli.setEnabled(false);
                            btnBeli.setText("Habis");
                        }

                        if (kasirFrame != null) {
                            kasirFrame.tambahTransaksi(kode, namaFinal, hargaFinal);
                        }

                    } else {
                        JOptionPane.showMessageDialog(card,
                            "Stok habis!", "Info", JOptionPane.INFORMATION_MESSAGE);
                        btnBeli.setEnabled(false);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            card.add(btnBeli);
            panelAll.add(card);
        }

        panelAll.revalidate();
        panelAll.repaint();

    } catch (Exception e) {
        e.printStackTrace();
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

        jMenuItem1 = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        panelAll = new javax.swing.JPanel();
        txtCari = new javax.swing.JTextField();
        cmbKategori = new javax.swing.JComboBox<>();

        jMenuItem1.setText("jMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAutoRequestFocus(false);
        setBackground(new java.awt.Color(242, 236, 228));
        setMaximumSize(new java.awt.Dimension(900, 900));
        setPreferredSize(new java.awt.Dimension(850, 850));
        setSize(new java.awt.Dimension(900, 900));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(242, 236, 228));

        jScrollPane1.setMaximumSize(new java.awt.Dimension(1000, 850));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(1000, 850));

        panelAll.setBackground(new java.awt.Color(242, 236, 228));
        panelAll.setMaximumSize(new java.awt.Dimension(1000, 1000));
        panelAll.setName(""); // NOI18N
        panelAll.setPreferredSize(new java.awt.Dimension(1000, 850));

        javax.swing.GroupLayout panelAllLayout = new javax.swing.GroupLayout(panelAll);
        panelAll.setLayout(panelAllLayout);
        panelAllLayout.setHorizontalGroup(
            panelAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
        );
        panelAllLayout.setVerticalGroup(
            panelAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 850, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(panelAll);

        txtCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCariActionPerformed(evt);
            }
        });
        txtCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCariKeyReleased(evt);
            }
        });

        cmbKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "aa" }));
        cmbKategori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbKategoriActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 830, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cmbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCari)))
                .addGap(54, 54, 54))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 43, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 787, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(63, 63, 63))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 890, 950));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbKategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbKategoriActionPerformed
   String selectedKategori = (String) cmbKategori.getSelectedItem();
    String keyword = txtCari.getText().trim(); // ambil isi pencarian kalau ada
    loadMenuFromDatabase(selectedKategori, keyword);
    }//GEN-LAST:event_cmbKategoriActionPerformed

    private void txtCariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariKeyReleased
   String selectedKategori = (String) cmbKategori.getSelectedItem();
    String keyword = txtCari.getText().trim();
    loadMenuFromDatabase(selectedKategori, keyword);

    }//GEN-LAST:event_txtCariKeyReleased

    private void txtCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCariActionPerformed

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
            java.util.logging.Logger.getLogger(MenuAll.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuAll.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuAll.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuAll.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuAll().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cmbKategori;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelAll;
    private javax.swing.JTextField txtCari;
    // End of variables declaration//GEN-END:variables
}
