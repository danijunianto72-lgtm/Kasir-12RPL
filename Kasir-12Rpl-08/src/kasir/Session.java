/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kasir;
import java.sql.*;
import javax.swing.*;
import java.text.NumberFormat;
import java.util.Locale;

public class Session {
        public static String namaPelayan;
        public static String role;  
    public static String username;
        
        
        public static String getTotalTransaksi() {
        String total = "0";
        try {
    Connection conn = (Connection) kasir.koneksi.dbKonek();
            String sql = "SELECT COUNT(*) AS total_transaksi " +
                         "FROM transaksi WHERE tgl_transaksi = CURRENT_DATE";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                total = rs.getString("total_transaksi");
            }
            rs.close();
            pst.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }
    private static final Locale localeID = new Locale("id", "ID");
    private static final NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(localeID);

    public static String formatRupiah(double value) {
        return rupiahFormat.format(value);
    }

    public static String getPendapatanHariIni() {
        String pendapatan = "0";
        try {
            Connection conn = (Connection) kasir.koneksi.dbKonek();
            String sql = "SELECT COALESCE(SUM(grand_total),0) AS pendapatan " +
                         "FROM transaksi WHERE tgl_transaksi = CURRENT_DATE";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                double nilai = rs.getDouble("pendapatan");
                pendapatan = formatRupiah(nilai);
            }
            rs.close();
            pst.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pendapatan;
    }
}