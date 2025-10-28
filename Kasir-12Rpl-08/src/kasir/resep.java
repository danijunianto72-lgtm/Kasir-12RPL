package kasir;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author yaniyan
 */
public class resep extends javax.swing.JFrame {

    /**
     * Creates new form user
     */
    public resep() {
        initComponents();
        loadMenu();
        loadKategoriMenu();
        btnDelete.setEnabled(false);
        btnEdit.setEnabled(false);
        btnTambah.setEnabled(true);
this.setResizable(false);
this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
this.setVisible(true);

;    loadBahan("");
    }
    public void setMenuData(int idMenu, String namaMenu) {
    txtMenu.setText(namaMenu);
    txtMenu.setName(String.valueOf(idMenu));

    // Panggil fungsi load resep untuk menu ini
    loadResep(String.valueOf(idMenu));
}
private void kosong(){
txtBahan.setText("");
txtSatuan.setText("");
txtJumlah.setText("");
tblBahan.clearSelection();
  }
    private void loadResep(String idMenu) {
    DefaultTableModel model = new DefaultTableModel();
        model.addColumn("No");
    model.addColumn("Id");
    model.addColumn("Nama Bahan");
    model.addColumn("Jumlah");
    model.addColumn("Satuan");
    tblResep.setModel(model);

try ( Connection conn = (Connection)kasir.koneksi.dbKonek()) {
    String sql = """
            SELECT r.idresep, b.namabahan, r.jumlah_bahan, r.satuan
            FROM resep r
            JOIN bahan b ON r.idbahan = b.idbahan
            WHERE r.idmenu = ?
        """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, Integer.parseInt(idMenu));
        ResultSet rs = ps.executeQuery();
int no=1;
        while (rs.next()) {
            model.addRow(new Object[]{
                no++,
                rs.getInt("idresep"),
                rs.getString("namabahan"),
                rs.getDouble("jumlah_bahan"),
                rs.getString("satuan")
            });
        }
          tblResep.getColumnModel().getColumn(0).setMaxWidth(40); // Kolom No
        tblResep.getColumnModel().getColumn(1).setMaxWidth(40); // Kolom ID Resep

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
}
private void loadKategoriMenu() {
    cmbFilterM.removeAllItems(); // kosongkan dulu isinya
    cmbFilterM.addItem("Semua"); // opsi default

    try (Connection conn = kasir.koneksi.dbKonek()) {
        String sql = "SELECT DISTINCT kategori FROM menu ORDER BY kategori ASC";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            cmbFilterM.addItem(rs.getString("kategori"));
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal memuat kategori: " + e.getMessage());
    }
}
private void filterMenu() {
    if (cmbFilterM.getSelectedItem() == null) {
        return; // keluar dulu, combo belum siap
    }
    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("No");
    model.addColumn("Id");
    model.addColumn("Nama");
    model.addColumn("Harga");
    model.addColumn("Kategori");
    tblMenu.setModel(model);

    String kategori = cmbFilterM.getSelectedItem().toString();
    String cari = txtCariM.getText();

    String sql = "SELECT * FROM menu WHERE namamenu ILIKE ? ";
    if (!kategori.equals("Semua")) {
        sql += "AND kategori = ? ";
    }
    sql += "ORDER BY idmenu";

    try (Connection conn = kasir.koneksi.dbKonek()) {
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, "%" + cari + "%");
        if (!kategori.equals("Semua")) {
            ps.setString(2, kategori);
        }
        ResultSet rs = ps.executeQuery();
        int no = 1;
        while (rs.next()) {
            model.addRow(new Object[]{
                no++,
                rs.getInt("idmenu"),
                rs.getString("namamenu"),
                rs.getInt("hargamenu"),
                rs.getString("kategori")
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
    }
}

private void loadMenu() {
    DefaultTableModel model = new DefaultTableModel();
        model.addColumn("No");
    model.addColumn("Id");
    model.addColumn("Nama Menu");
    model.addColumn("Harga");
    model.addColumn("Kategori");
    tblMenu.setModel(model);

    try (        Connection conn = (Connection)kasir.koneksi.dbKonek()) {
        String sql = "SELECT * FROM menu ORDER BY idmenu";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        int no = 1;
        while (rs.next()) {
            model.addRow(new Object[]{
                no++,
                rs.getInt("idmenu"),
                rs.getString("namamenu"),
                rs.getInt("hargamenu"),
                rs.getString("kategori")
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
}
private void clearResepForm() {
    txtMenu.setText("");
    txtMenu.setName(null);
    txtBahan.setText("");
    txtBahan.setName(null);
    txtJumlah.setText("");
    txtSatuan.setText("");

    // Hilangkan seleksi di tabel
    tblMenu.clearSelection();
    tblBahan.clearSelection();
    tblResep.clearSelection();
}
private void loadBahan(String keyword) {
    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("ID");       // ← tambahkan kolom ID (disembunyikan nanti)
    model.addColumn("No");
    model.addColumn("Nama Bahan");
    model.addColumn("Stok");
    model.addColumn("Satuan");
    tblBahan.setModel(model);

    try (Connection conn = kasir.koneksi.dbKonek()) {
        String sql = "SELECT * FROM bahan WHERE namabahan ILIKE ? ORDER BY idbahan";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, "%" + keyword + "%");
        ResultSet rs = ps.executeQuery();

        int no = 1;
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("idbahan"),   // kolom tersembunyi
                no++,
                rs.getString("namabahan"),
                rs.getDouble("stok"),
                rs.getString("satuan")
            });
        }

        // Sembunyikan kolom ID
        tblBahan.getColumnModel().getColumn(0).setMinWidth(0);
        tblBahan.getColumnModel().getColumn(0).setMaxWidth(0);
        tblBahan.getColumnModel().getColumn(0).setWidth(0);

        // Lebar kolom No
        tblBahan.getColumnModel().getColumn(1).setPreferredWidth(40);
        tblBahan.getColumnModel().getColumn(1).setMaxWidth(40);
        tblBahan.getColumnModel().getColumn(1).setMinWidth(40);

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMenu = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblBahan = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnTambah = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtSatuan = new javax.swing.JTextField();
        txtJumlah = new javax.swing.JTextField();
        txtBahan = new javax.swing.JTextField();
        btnEdit = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblResep = new javax.swing.JTable();
        jButton4 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtMenu = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cmbFilterM = new javax.swing.JComboBox<>();
        txtCariM = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtCariB = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(242, 236, 228));
        jPanel1.setPreferredSize(new java.awt.Dimension(1280, 720));

        jPanel2.setBackground(new java.awt.Color(242, 236, 228));
        jPanel2.setPreferredSize(new java.awt.Dimension(1080, 720));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(242, 236, 228));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblMenu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tblMenu.setModel(new javax.swing.table.DefaultTableModel(
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
        tblMenu.setRowHeight(30);
        tblMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMenuMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblMenu);

        jPanel4.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 460, 240));

        tblBahan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tblBahan.setModel(new javax.swing.table.DefaultTableModel(
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
        tblBahan.setRowHeight(30);
        tblBahan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBahanMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblBahan);

        jPanel4.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 430, 520, 180));

        jLabel4.setText("Jumlah");
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 250, -1, -1));

        jLabel3.setText("Nama Bahan");
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 70, -1, -1));

        btnTambah.setBackground(new java.awt.Color(0, 204, 153));
        btnTambah.setText("TAMBAH");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });
        jPanel4.add(btnTambah, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 340, 120, 50));

        btnDelete.setBackground(new java.awt.Color(250, 52, 88));
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("DELETE");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        jPanel4.add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, 100, 50));

        jLabel2.setText("Satuan");
        jPanel4.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 160, -1, -1));

        txtSatuan.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel4.add(txtSatuan, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 180, 520, 60));

        txtJumlah.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel4.add(txtJumlah, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 270, 520, 60));

        txtBahan.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel4.add(txtBahan, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 90, 250, 61));

        btnEdit.setBackground(new java.awt.Color(255, 153, 0));
        btnEdit.setText("EDIT");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        jPanel4.add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 340, 100, 50));

        tblResep.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tblResep.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Id", "Nama Bahan", "Jumlah", "Satuan"
            }
        ));
        tblResep.setRowHeight(30);
        tblResep.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblResepMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblResep);
        if (tblResep.getColumnModel().getColumnCount() > 0) {
            tblResep.getColumnModel().getColumn(0).setMaxWidth(40);
            tblResep.getColumnModel().getColumn(1).setMaxWidth(40);
        }

        jPanel4.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 430, 460, 180));

        jButton4.setBackground(new java.awt.Color(102, 102, 102));
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("BATAL");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 340, 100, 50));

        jLabel1.setText("Nama Menu");
        jPanel4.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 70, -1, -1));

        txtMenu.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel4.add(txtMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 90, 250, 61));

        jLabel5.setText("Table Bahan");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 410, -1, -1));

        jLabel6.setText("Table Menu");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, -1));

        jLabel7.setText("Table Resep");
        jPanel4.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 410, -1, -1));

        cmbFilterM.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbFilterM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbFilterMActionPerformed(evt);
            }
        });
        jPanel4.add(cmbFilterM, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 30, 150, 40));

        txtCariM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCariMKeyReleased(evt);
            }
        });
        jPanel4.add(txtCariM, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 300, 40));

        jLabel9.setText("Cari");
        jPanel4.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 350, -1, -1));

        jLabel10.setText("Cari");
        jPanel4.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        txtCariB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCariBActionPerformed(evt);
            }
        });
        txtCariB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCariBKeyReleased(evt);
            }
        });
        jPanel4.add(txtCariB, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 372, 440, 50));

        jLabel11.setText("FilterKategori");
        jPanel4.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 10, -1, -1));

        jPanel2.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 1060, 640));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel8.setText("KELOLA RESEP");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jButton2.setBackground(new java.awt.Color(102, 51, 255));
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Cara pakai");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 30, -1, 40));

        jPanel3.setBackground(new java.awt.Color(119, 85, 70));

        jPanel8.setBackground(new java.awt.Color(119, 85, 70));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton5.setBackground(new java.awt.Color(242, 236, 228));
        jButton5.setText("Kasir");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 245, 165, 50));

        jButton1.setBackground(new java.awt.Color(102, 51, 255));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Info");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/logo.png"))); // NOI18N
        jLabel12.setText("jLabel1");
        jPanel8.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jButton6.setBackground(new java.awt.Color(242, 236, 228));
        jButton6.setText("Admin");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 190, 165, 50));

        jButton8.setBackground(new java.awt.Color(206, 171, 141));
        jButton8.setText("Resep");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 520, 165, 50));

        jButton9.setBackground(new java.awt.Color(242, 236, 228));
        jButton9.setText("RiwayatTransaksi");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 300, 165, 50));

        jButton10.setBackground(new java.awt.Color(242, 236, 228));
        jButton10.setText("Pembelian");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton10, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 355, 165, 50));

        jButton11.setBackground(new java.awt.Color(242, 236, 228));
        jButton11.setText("Menu");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton11, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 410, 165, 50));

        jButton12.setBackground(new java.awt.Color(242, 236, 228));
        jButton12.setText("Inventory");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton12, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 465, 165, 50));

        jButton13.setBackground(new java.awt.Color(255, 153, 153));
        jButton13.setText("Log out");
        jButton13.setFocusTraversalPolicyProvider(true);
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton13, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 650, 165, 50));

        jButton14.setBackground(new java.awt.Color(242, 236, 228));
        jButton14.setText("Pengguna");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton14, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 575, 165, 50));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 730, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(11, 11, 11)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(12, 12, 12)))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, -1, 730));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMenuMouseClicked
kosong();
        int row = tblMenu.getSelectedRow();
    if (row != -1) {
        String idMenu = tblMenu.getValueAt(row, 1).toString();
        String namaMenu = tblMenu.getValueAt(row, 2).toString();

        txtMenu.setText(namaMenu);
        txtMenu.setName(idMenu); // simpan ID menu di name untuk nanti
        loadResep(idMenu);       // tampilkan resep bahan dari menu tsb
    }        // TODO add your handling code here:
    }//GEN-LAST:event_tblMenuMouseClicked

    private void tblBahanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBahanMouseClicked
    btnDelete.setEnabled(false);
        btnEdit.setEnabled(false);
        btnTambah.setEnabled(true);
        tblResep.clearSelection();
    txtJumlah.setText("");
        int row = tblBahan.getSelectedRow();
if (row != -1) {
    String idBahan = tblBahan.getValueAt(row, 0).toString(); // ambil dari kolom tersembunyi (idbahan)
    String namaBahan = tblBahan.getValueAt(row, 2).toString();
    String satuan = tblBahan.getValueAt(row, 4).toString();

    txtBahan.setText(namaBahan);
    txtBahan.setName(idBahan); // simpan ID bahan yang benar
    txtSatuan.setText(satuan);
}

        // TODO add your handling code here:
    }//GEN-LAST:event_tblBahanMouseClicked

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
String jml = txtJumlah.getText();
String St = txtSatuan.getText();
   if (jml.isEmpty() || St.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lengkapi semua data", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try ( Connection conn = (Connection)kasir.koneksi.dbKonek()) {
    String idMenu = txtMenu.getName();
        String idBahan = txtBahan.getName();
        double jumlah = Double.parseDouble(txtJumlah.getText());
        String satuan = txtSatuan.getText();

        String sql = "INSERT INTO resep (idmenu, idbahan, jumlah_bahan, satuan) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, Integer.parseInt(idMenu));
        ps.setInt(2, Integer.parseInt(idBahan));
        ps.setDouble(3, jumlah);
        ps.setString(4, satuan);
        ps.executeUpdate();

        JOptionPane.showMessageDialog(this, "Resep berhasil ditambahkan!");
        loadResep(idMenu);
        kosong();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    
}
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
  // Pastikan ada resep yang dipilih
    if (txtBahan.getName() == null || txtBahan.getName().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Pilih resep yang ingin diubah dari tabel!");
        return;
    }

try ( Connection conn = (Connection)kasir.koneksi.dbKonek()) {
    String idResep = txtBahan.getName();
        double jumlahBaru = Double.parseDouble(txtJumlah.getText());
        String satuanBaru = txtSatuan.getText();

        String sql = "UPDATE resep SET jumlah_bahan = ?, satuan = ? WHERE idresep = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setDouble(1, jumlahBaru);
        ps.setString(2, satuanBaru);
        ps.setInt(3, Integer.parseInt(idResep));
        ps.executeUpdate();

        JOptionPane.showMessageDialog(this, "Resep berhasil diperbarui!");

        // Refresh tabel resep untuk menu aktif
        loadResep(txtMenu.getName());

        // Kosongkan input setelah edit
        txtBahan.setText("");
        txtBahan.setName(null);
        txtJumlah.setText("");
        txtSatuan.setText("");
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka!");
    }
    }//GEN-LAST:event_btnEditActionPerformed

    private void tblResepMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblResepMouseClicked
tblBahan.clearSelection();
    btnDelete.setEnabled(true);
        btnEdit.setEnabled(true);
        btnTambah.setEnabled(false);
        int row = tblResep.getSelectedRow();
    if (row != -1) {
        // Ambil nilai dari tabel
        String idResep = tblResep.getValueAt(row, 1).toString();
        String namaBahan = tblResep.getValueAt(row, 2).toString();
        String jumlah = tblResep.getValueAt(row, 3).toString();
        String satuan = tblResep.getValueAt(row, 4).toString();

        // Tampilkan ke textfield
        txtBahan.setText(namaBahan);
        txtJumlah.setText(jumlah);
        txtSatuan.setText(satuan);

        // Simpan ID Resep di name agar bisa dipakai saat edit
        txtBahan.setName(idResep);
    }        // TODO add your handling code here:
    }//GEN-LAST:event_tblResepMouseClicked

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
int row = tblResep.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Pilih resep yang ingin dihapus dari tabel!");
        return;
    }

    int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Yakin ingin menghapus resep ini?",
            "Konfirmasi",
            JOptionPane.YES_NO_OPTION);

    if (konfirmasi == JOptionPane.YES_OPTION) {
try ( Connection conn = (Connection)kasir.koneksi.dbKonek()) {
    String idResep = tblResep.getValueAt(row, 0).toString();
            String sql = "DELETE FROM resep WHERE idresep = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(idResep));
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Resep berhasil dihapus!");

            // Refresh data resep setelah hapus
            loadResep(txtMenu.getName());

            // Bersihkan form
            clearResepForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }        // TODO add your handling code here:
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
    clearResepForm();
        DefaultTableModel model = (DefaultTableModel) tblResep.getModel();
    model.setRowCount(0);

     DefaultTableModel bhn = (DefaultTableModel) tblBahan.getModel();
    model.setRowCount(0);

        btnDelete.setEnabled(false);
        btnEdit.setEnabled(false);
        btnTambah.setEnabled(true);

    }//GEN-LAST:event_jButton4ActionPerformed

    private void txtCariMKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariMKeyReleased
        filterMenu();
    }//GEN-LAST:event_txtCariMKeyReleased

    private void cmbFilterMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbFilterMActionPerformed
        filterMenu();

    }//GEN-LAST:event_cmbFilterMActionPerformed

    private void txtCariBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCariBActionPerformed
 String keyword = txtCariB.getText();
    loadBahan(keyword);        // TODO add your handling code here:
    }//GEN-LAST:event_txtCariBActionPerformed

    private void txtCariBKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariBKeyReleased
 String keyword = txtCariB.getText();
    loadBahan(keyword);        // TODO add your handling code here:
    }//GEN-LAST:event_txtCariBKeyReleased

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

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    String pesan = """
        Panduan Penggunaan Form:
        
        1. Pilih menu yang ingin ditambah atau diubah pada tabel Menu.
        2. Pilih bahan yang akan digunakan dari tabel Bahan.
        3. Gunakan tombol 'Tambah' untuk menambahkan bahan ke menu yang dipilih.
        4. Gunakan tombol 'Edit' untuk mengubah resep dari menu yang sudah ada.
        5. Gunakan tombol 'Hapus' untuk menghapus bahan dari resep.
        6. Klik tabel Resep untuk memilih resep yang ingin diubah atau dihapus.
        """;
    
    JOptionPane.showMessageDialog(this, pesan, "Petunjuk Penggunaan", JOptionPane.INFORMATION_MESSAGE);

        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

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
            java.util.logging.Logger.getLogger(resep.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(resep.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(resep.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(resep.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new resep().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnTambah;
    private javax.swing.JComboBox<String> cmbFilterM;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tblBahan;
    private javax.swing.JTable tblMenu;
    private javax.swing.JTable tblResep;
    private javax.swing.JTextField txtBahan;
    private javax.swing.JTextField txtCariB;
    private javax.swing.JTextField txtCariM;
    private javax.swing.JTextField txtJumlah;
    private javax.swing.JTextField txtMenu;
    private javax.swing.JTextField txtSatuan;
    // End of variables declaration//GEN-END:variables
}
