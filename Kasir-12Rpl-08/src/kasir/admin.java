/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package kasir;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import java.util.Date;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import javax.swing.JFrame;


/**
 *
 * @author user
 */
public class admin extends javax.swing.JFrame {



      public admin() {
    initComponents();
hitungSaldo();
    cekStok();
    tampilDataHariIni();
this.setResizable(false);
this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
this.setVisible(true);

  tampilTanggal();
     java.util.Date akhir = new java.util.Date();
   setDefaultDates();
loadPengeluaranHariIni();
loadChart();
}
     private void loadChart() {
    Date awal = dateAwal.getDate();
    Date akhir = dateAkhir.getDate();

    if (awal == null || akhir == null) {
        return;
    }

    LineChartPanel chart = new LineChartPanel(awal, akhir);

    // Dapatkan jumlah data (jumlah hari antara tanggal awal dan akhir)
    int dataCount = Math.max(10, chart.getDataCount());

    // Lebarkan panel sesuai jumlah data biar bisa discroll
    chart.setPreferredSize(new Dimension(100 * dataCount, 350));

    // Bungkus chart dalam JScrollPane
    JScrollPane scrollPane = new JScrollPane(chart);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    // Bersihkan isi panelChart sebelum tambah baru
    panelChart.removeAll();
    panelChart.setLayout(new BorderLayout());
    panelChart.add(scrollPane, BorderLayout.CENTER);
    panelChart.revalidate();
    panelChart.repaint();
}

private void loadPengeluaranHariIni() {
    try {
        Connection conn = kasir.koneksi.dbKonek();

        String sql = "SELECT COALESCE(SUM(total), 0) AS total_hari_ini " +
                     "FROM pembelian_bahan WHERE DATE(tanggal) = CURRENT_DATE";
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            int total = rs.getInt("total_hari_ini");

            // Format ke Rupiah
            java.text.NumberFormat nf = java.text.NumberFormat.getInstance(new java.util.Locale("id", "ID"));
            lblPengeluaran.setText("Rp " + nf.format(total));
        } else {
            lblPengeluaran.setText("Rp 0");
        }

    } catch (SQLException e) {
        e.printStackTrace();
        lblPengeluaran.setText("Error");
    }
}
private void setDefaultDates() {
    Calendar cal = Calendar.getInstance();

    // Set jdcEnd = hari ini
    Date today = cal.getTime();
    dateAkhir.setDate(today);

    // Set jdcStart = 7 hari sebelum hari ini
    cal.add(Calendar.DAY_OF_MONTH, -6); // 6 agar totalnya 7 hari termasuk hari ini
    Date sevenDaysAgo = cal.getTime();
    dateAwal.setDate(sevenDaysAgo);
}

      private void tampilTanggal() {
    Calendar cal = Calendar.getInstance();
SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", new java.util.Locale("id", "ID"));
    lblJam1.setText(sdf.format(cal.getTime()));
}
private void hitungSaldo() {
    try (Connection conn = kasir.koneksi.dbKonek()) {

        double totalPenjualan = 0;
        double totalPembelian = 0;

        // Ambil total grand_total dari transaksi
        String sqlTransaksi = "SELECT COALESCE(SUM(grand_total), 0) AS total FROM transaksi";
        try (PreparedStatement ps = conn.prepareStatement(sqlTransaksi);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                totalPenjualan = rs.getDouble("total");
            }
        }

        // Ambil total total dari pembelian_bahan
        String sqlPembelian = "SELECT COALESCE(SUM(total), 0) AS total FROM pembelian_bahan";
        try (PreparedStatement ps = conn.prepareStatement(sqlPembelian);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                totalPembelian = rs.getDouble("total");
            }
        }

        // Hitung saldo akhir
        double saldoAkhir = totalPenjualan - totalPembelian;

        // Format ke rupiah
        java.text.NumberFormat rupiah = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("id", "ID"));
        lblSaldo.setText(rupiah.format(saldoAkhir));

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Gagal menghitung saldo: " + e.getMessage());
    }
}


private void tampilDataHariIni() {
    lblTotalTransaksi.setText(Session.getTotalTransaksi());
lblPendapatan.setText(Session.getPendapatanHariIni());
}
 private void cekStok() {
    try {
        Connection conn = kasir.koneksi.dbKonek();
        String sql = "SELECT COUNT(*) AS jml FROM menu WHERE stok < 5";
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            int jumlah = rs.getInt("jml");
            lblStok.setText(String.valueOf(jumlah));

            if (jumlah > 0) {
                lblStok.setForeground(Color.WHITE); // warning kalau ada stok kritis
            } else {
                lblStok.setForeground(Color.WHITE); // aman
            }
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error cek stok: " + e.getMessage());
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

        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        lblTotalTransaksi = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        lblPendapatan = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        panelMerah1 = new javax.swing.JPanel();
        lblPengeluaran = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        panelMerah = new javax.swing.JPanel();
        lblSaldo = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        lblStok = new javax.swing.JLabel();
        btnStok = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        panelChart = new javax.swing.JPanel();
        dateAwal = new com.toedter.calendar.JDateChooser();
        dateAkhir = new com.toedter.calendar.JDateChooser();
        btnTampilkan = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        panel2 = new java.awt.Panel();
        jPanel14 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        lblJam1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(new java.awt.Dimension(800, 800));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(242, 236, 228));
        jPanel3.setForeground(new java.awt.Color(0, 153, 153));
        jPanel3.setPreferredSize(new java.awt.Dimension(1280, 720));

        jPanel2.setBackground(new java.awt.Color(242, 236, 228));
        jPanel2.setPreferredSize(new java.awt.Dimension(1080, 720));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBackground(new java.awt.Color(119, 85, 70));
        jPanel6.setMaximumSize(new java.awt.Dimension(1000, 1000));
        jPanel6.setPreferredSize(new java.awt.Dimension(300, 200));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTotalTransaksi.setFont(new java.awt.Font("Segoe UI", 1, 58)); // NOI18N
        lblTotalTransaksi.setForeground(new java.awt.Color(254, 254, 254));
        lblTotalTransaksi.setText("999");
        jPanel6.add(lblTotalTransaksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 260, 100));

        jPanel4.setBackground(new java.awt.Color(206, 171, 141));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 0, 51));
        jLabel5.setText("Transaksi Hari Ini");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel5)
                .addContainerGap(161, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 300, 40));

        jPanel2.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 300, 210));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(254, 254, 254));
        jLabel2.setText("Transaksi Hari Ini");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 62, -1, -1));

        jPanel7.setBackground(new java.awt.Color(88, 129, 87));
        jPanel7.setMaximumSize(new java.awt.Dimension(1000, 1000));
        jPanel7.setPreferredSize(new java.awt.Dimension(200, 200));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblPendapatan.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblPendapatan.setForeground(new java.awt.Color(254, 254, 254));
        lblPendapatan.setText("20000000000000");
        jPanel7.add(lblPendapatan, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 370, 40));

        jPanel8.setBackground(new java.awt.Color(189, 224, 216));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 0, 51));
        jLabel3.setText("Pendapatan Hari Ini");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(291, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 430, 40));

        jPanel2.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 10, 420, 100));

        panelMerah1.setBackground(new java.awt.Color(119, 85, 70));
        panelMerah1.setMaximumSize(new java.awt.Dimension(1000, 1000));
        panelMerah1.setPreferredSize(new java.awt.Dimension(200, 200));
        panelMerah1.setVerifyInputWhenFocusTarget(false);
        panelMerah1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblPengeluaran.setBackground(new java.awt.Color(88, 129, 87));
        lblPengeluaran.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblPengeluaran.setForeground(new java.awt.Color(254, 254, 254));
        lblPengeluaran.setText("120000000,00");
        panelMerah1.add(lblPengeluaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 400, 50));

        jPanel11.setBackground(new java.awt.Color(206, 171, 141));

        jLabel6.setBackground(new java.awt.Color(206, 171, 141));
        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 0, 51));
        jLabel6.setText("Pengeluaran Hari Ini");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addContainerGap(287, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addContainerGap())
        );

        panelMerah1.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 430, 40));

        jPanel2.add(panelMerah1, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 120, 420, 100));

        panelMerah.setBackground(new java.awt.Color(88, 129, 87));
        panelMerah.setMaximumSize(new java.awt.Dimension(1000, 1000));
        panelMerah.setPreferredSize(new java.awt.Dimension(200, 200));
        panelMerah.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblSaldo.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblSaldo.setForeground(new java.awt.Color(254, 254, 254));
        lblSaldo.setText("120000000,00");
        panelMerah.add(lblSaldo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 660, 50));

        jPanel10.setBackground(new java.awt.Color(189, 224, 216));

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 51, 0));
        jLabel4.setText("Total Saldo Akhir");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addContainerGap(628, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        panelMerah.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 750, 20));

        jPanel2.add(panelMerah, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 700, 70));

        jPanel9.setBackground(new java.awt.Color(88, 129, 87));
        jPanel9.setPreferredSize(new java.awt.Dimension(300, 300));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel12.setBackground(new java.awt.Color(189, 224, 216));
        jPanel12.setPreferredSize(new java.awt.Dimension(110, 70));

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(45, 45, 45));
        jLabel7.setText("Jumlah Stok < 5");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 310, 40));

        lblStok.setFont(new java.awt.Font("Segoe UI", 1, 58)); // NOI18N
        lblStok.setForeground(new java.awt.Color(255, 255, 255));
        lblStok.setText("999");
        jPanel9.add(lblStok, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 260, 100));

        btnStok.setBackground(new java.awt.Color(189, 224, 216));
        btnStok.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnStok.setText("Detail");
        btnStok.setToolTipText("");
        btnStok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStokActionPerformed(evt);
            }
        });
        jPanel9.add(btnStok, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 260, 40));

        jPanel2.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 10, 300, 210));

        jPanel13.setBackground(new java.awt.Color(242, 236, 228));
        jPanel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelChart.setBackground(new java.awt.Color(242, 236, 228));
        panelChart.setBorder(javax.swing.BorderFactory.createTitledBorder("Data Penghasilan"));
        panelChart.setToolTipText("");
        panelChart.setPreferredSize(new java.awt.Dimension(750, 290));
        panelChart.setLayout(new java.awt.BorderLayout());
        jPanel13.add(panelChart, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 47, 1020, 329));

        dateAwal.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateAwalPropertyChange(evt);
            }
        });
        jPanel13.add(dateAwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 150, 40));

        dateAkhir.setBackground(new java.awt.Color(189, 224, 216));
        dateAkhir.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateAkhirPropertyChange(evt);
            }
        });
        jPanel13.add(dateAkhir, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 10, 140, 40));

        btnTampilkan.setBackground(new java.awt.Color(189, 224, 216));
        btnTampilkan.setText("reset");
        btnTampilkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTampilkanActionPerformed(evt);
            }
        });
        jPanel13.add(btnTampilkan, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 10, 89, 40));

        jLabel8.setText("_");
        jPanel13.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(267, 20, 30, -1));

        jPanel2.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 1040, 390));

        panel2.setBackground(new java.awt.Color(88, 129, 87));

        jPanel14.setBackground(new java.awt.Color(189, 224, 216));
        jPanel14.setPreferredSize(new java.awt.Dimension(100, 20));

        jLabel9.setBackground(new java.awt.Color(0, 0, 0));
        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 0, 51));
        jLabel9.setText("Tanggal Sekarang");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addGap(0, 377, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        lblJam1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblJam1.setForeground(new java.awt.Color(254, 254, 254));
        lblJam1.setText("Jum at 99 november,  2025");

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 498, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblJam1, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblJam1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(panel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 230, 330, 70));

        jPanel1.setBackground(new java.awt.Color(119, 85, 70));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1.setBackground(new java.awt.Color(242, 236, 228));
        jButton1.setText("Kasir");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 245, 165, 50));

        jButton10.setBackground(new java.awt.Color(102, 51, 255));
        jButton10.setForeground(new java.awt.Color(255, 255, 255));
        jButton10.setText("Info");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/logo.png"))); // NOI18N
        jLabel1.setText("jLabel1");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jButton2.setBackground(new java.awt.Color(206, 171, 141));
        jButton2.setText("Admin");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 190, 165, 50));

        jButton3.setBackground(new java.awt.Color(242, 236, 228));
        jButton3.setText("Resep");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 520, 165, 50));

        jButton4.setBackground(new java.awt.Color(242, 236, 228));
        jButton4.setText("RiwayatTransaksi");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 300, 165, 50));

        jButton5.setBackground(new java.awt.Color(242, 236, 228));
        jButton5.setText("Pembelian");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 355, 165, 50));

        jButton6.setBackground(new java.awt.Color(242, 236, 228));
        jButton6.setText("Menu");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 410, 165, 50));

        jButton7.setBackground(new java.awt.Color(242, 236, 228));
        jButton7.setText("Inventory");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 465, 165, 50));

        jButton8.setBackground(new java.awt.Color(255, 153, 153));
        jButton8.setText("Log out");
        jButton8.setFocusTraversalPolicyProvider(true);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 650, 165, 50));

        jButton9.setBackground(new java.awt.Color(242, 236, 228));
        jButton9.setText("Pengguna");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 575, 165, 50));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 1080, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 720));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnStokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStokActionPerformed
  menu menuForm = new menu(this); // kirim referensi kasirku
        menuForm.setVisible(true); 
    }//GEN-LAST:event_btnStokActionPerformed

    private void btnTampilkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTampilkanActionPerformed
setDefaultDates();
    }//GEN-LAST:event_btnTampilkanActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
   menuKasir ksr= new menuKasir();
        ksr.setVisible(true);
        this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
   admin ad= new admin();
        ad.setVisible(true);
        this.dispose();
     }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
resep res = new resep();
res.setVisible(true);
this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
historyKasir hk = new  historyKasir();
hk.setVisible(true);
this.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
pembelian pb = new pembelian();
pb.setVisible(true);
this.dispose();// TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
inventoryMenu im = new inventoryMenu();
im.setVisible(true);
this.dispose();
// TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
bahan bhn = new bahan();
bhn.setVisible(true);
this.dispose();

    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
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
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
user pu = new user();
pu.setVisible(true);
this.dispose();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        info in= new info();
        in.setVisible(true);// TODO add your handling code here:
    }//GEN-LAST:event_jButton10ActionPerformed

    private void dateAwalPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dateAwalPropertyChange
        if ("date".equals(evt.getPropertyName())) {
                    loadChart();
                }        // TODO add your handling code here:
    }//GEN-LAST:event_dateAwalPropertyChange

    private void dateAkhirPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dateAkhirPropertyChange
        if ("date".equals(evt.getPropertyName())) {
                    loadChart();
                }        // TODO add your handling code here:
    }//GEN-LAST:event_dateAkhirPropertyChange

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
            java.util.logging.Logger.getLogger(admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new admin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnStok;
    private javax.swing.JButton btnTampilkan;
    private com.toedter.calendar.JDateChooser dateAkhir;
    private com.toedter.calendar.JDateChooser dateAwal;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel lblJam1;
    private javax.swing.JLabel lblPendapatan;
    private javax.swing.JLabel lblPengeluaran;
    private javax.swing.JLabel lblSaldo;
    private javax.swing.JLabel lblStok;
    private javax.swing.JLabel lblTotalTransaksi;
    private java.awt.Panel panel2;
    private javax.swing.JPanel panelChart;
    private javax.swing.JPanel panelMerah;
    private javax.swing.JPanel panelMerah1;
    // End of variables declaration//GEN-END:variables
}
