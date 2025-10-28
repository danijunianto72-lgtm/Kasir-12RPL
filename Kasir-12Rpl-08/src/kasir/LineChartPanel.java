package kasir;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class LineChartPanel extends JPanel {
    private List<Integer> data = new ArrayList<>();
    private List<String> labels = new ArrayList<>();
    private List<Point> points = new ArrayList<>();
    private JLabel infoLabel;

    private java.util.Date startDate;
    private java.util.Date endDate;

        public int getDataCount() {
    return data.size();
}


    public LineChartPanel(java.util.Date startDate, java.util.Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;

        setPreferredSize(new Dimension(1000, 350));
setBackground(new Color(242, 236, 228));

        infoLabel = new JLabel("Klik titik untuk melihat detail");
        this.add(infoLabel);

        loadDataFromDatabase();

        // Event klik titik
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (int i = 0; i < points.size(); i++) {
                    Point p = points.get(i);
                    if (p.distance(e.getPoint()) < 7) {
                        String msg = "Tanggal: " + labels.get(i) +
                                "\nPenjualan: Rp " + data.get(i);
                        infoLabel.setText("Tanggal: " + labels.get(i) + " | Penjualan: Rp " + data.get(i));
                        JOptionPane.showMessageDialog(LineChartPanel.this, msg, "Detail Penjualan", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
    }

    private void loadDataFromDatabase() {
        data.clear();
        labels.clear();

        try (Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/dbRestoran", "postgres", "1234")) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tglAwal = sdf.format(startDate);
            String tglAkhir = sdf.format(endDate);

            String sql = "SELECT d::date AS tanggal, " +
                    "COALESCE(SUM(t.grand_total),0) AS total " +
                    "FROM generate_series(?::date, ?::date, interval '1 day') d " +
                    "LEFT JOIN transaksi t ON t.tgl_transaksi::date = d::date " +
                    "GROUP BY d ORDER BY d";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tglAwal);
            ps.setString(2, tglAkhir);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                labels.add(rs.getDate("tanggal").toString());
                data.add(rs.getInt("total"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (data.isEmpty()) {
            g.drawString("Tidak ada data", getWidth() / 2 - 30, getHeight() / 2);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int margin = 60;

        // Axis
        g2.setColor(Color.BLACK);
        g2.drawLine(margin, height - margin, width - margin, height - margin); // X
        g2.drawLine(margin, margin, margin, height - margin); // Y

        // Cari max value
        int maxData = data.stream().max(Integer::compare).orElse(1);

        // Skala
        int[] yScales = {50000, 100000, 150000, 200000, 500000, 1000000};
        int maxY = yScales[yScales.length - 1];
        for (int s : yScales) {
            if (s >= maxData) {
                maxY = s;
                break;
            }
        }

        // Jika hanya 1 data
        if (data.size() == 1) {
            int x = width / 2;
            int y = height - margin - (data.get(0) * (height - 2 * margin) / maxY);
            g2.setColor(Color.RED);
            g2.fillOval(x - 4, y - 4, 8, 8);
            points.add(new Point(x, y));

            g2.setColor(Color.BLACK);
            AffineTransform old = g2.getTransform();
            g2.rotate(Math.toRadians(-45), x, height - margin + 20);
            g2.drawString(labels.get(0), x, height - margin + 20);
            g2.setTransform(old);
            return;
        }

        int xStep = (width - 2 * margin) / (data.size() - 1);
        points.clear();

        // Garis grid + Y label
        for (int y : yScales) {
            if (y > maxY) break;
            int yPos = height - margin - (y * (height - 2 * margin) / maxY);

            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(margin, yPos, width - margin, yPos);

            g2.setColor(Color.BLACK);
            g2.drawString("Rp " + y, 5, yPos + 5);
        }

        // Garis dan titik data
        g2.setColor(Color.RED);
        for (int i = 0; i < data.size() - 1; i++) {
            int x1 = margin + i * xStep;
            int y1 = height - margin - (data.get(i) * (height - 2 * margin) / maxY);
            int x2 = margin + (i + 1) * xStep;
            int y2 = height - margin - (data.get(i + 1) * (height - 2 * margin) / maxY);

            g2.drawLine(x1, y1, x2, y2);
            g2.fillOval(x1 - 4, y1 - 4, 8, 8);
            points.add(new Point(x1, y1));

            // Label tanggal miring
            g2.setColor(Color.BLACK);
            AffineTransform old = g2.getTransform();
            g2.rotate(Math.toRadians(-45), x1, height - margin + 20);
            g2.drawString(labels.get(i), x1, height - margin + 20);
            g2.setTransform(old);
            g2.setColor(Color.RED);
        }

        // Titik terakhir
        int lastX = margin + (data.size() - 1) * xStep;
        int lastY = height - margin - (data.get(data.size() - 1) * (height - 2 * margin) / maxY);
        g2.fillOval(lastX - 4, lastY - 4, 8, 8);
        points.add(new Point(lastX, lastY));

        g2.setColor(Color.BLACK);
        AffineTransform old = g2.getTransform();
        g2.rotate(Math.toRadians(-45), lastX, height - margin + 20);
        g2.drawString(labels.get(data.size() - 1), lastX, height - margin + 20);
        g2.setTransform(old);
    }
}
