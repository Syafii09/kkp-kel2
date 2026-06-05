package dao;

import koneksi.Koneksi;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardDAO {

    public RingkasanDashboard getRingkasan() throws SQLException {
        String sqlTotalAnggota = "SELECT COUNT(*) AS total FROM anggota";
        String sqlKasMasukHariIni = """
                SELECT COALESCE(SUM(debet), 0) AS total
                FROM transaksi
                WHERE DATE(tanggal) = CURDATE()
                  AND jenis_transaksi IN ('Simpanan', 'Angsuran')
                """;
        String sqlTotalPinjaman = """
                SELECT COALESCE(SUM(jumlah_pinjaman), 0) AS total
                FROM pinjaman
                WHERE status = 'Aktif'
                """;

        try (Connection connection = Koneksi.getConnection()) {
            return new RingkasanDashboard(
                    getSingleValue(connection, sqlTotalAnggota),
                    getSingleDecimal(connection, sqlKasMasukHariIni),
                    getSingleDecimal(connection, sqlTotalPinjaman)
            );
        }
    }

    public List<AktivitasTerbaru> getAktivitasTerbaru() throws SQLException {
        String sql = """
                SELECT tanggal, jenis_transaksi, keterangan
                FROM transaksi
                ORDER BY id_transaksi DESC
                LIMIT 6
                """;

        List<AktivitasTerbaru> data = new ArrayList<>();
        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                data.add(new AktivitasTerbaru(
                        result.getTimestamp("tanggal"),
                        result.getString("jenis_transaksi"),
                        result.getString("keterangan")
                ));
            }
        }
        return data;
    }

    public Map<String, BigDecimal> getGrafikSimpananEnamBulan() throws SQLException {
        DateTimeFormatter keyFormat = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, BigDecimal> totalPerBulan = new LinkedHashMap<>();

        LocalDate bulanAwal = LocalDate.now().minusMonths(5).withDayOfMonth(1);
        for (int i = 0; i < 6; i++) {
            LocalDate bulan = bulanAwal.plusMonths(i);
            totalPerBulan.put(bulan.format(keyFormat), BigDecimal.ZERO);
        }

        String sql = """
                SELECT DATE_FORMAT(tanggal, '%Y-%m') AS bulan,
                       SUM(debet) AS total
                FROM transaksi
                WHERE tanggal >= DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 5 MONTH), '%Y-%m-01')
                  AND jenis_transaksi = 'Simpanan'
                GROUP BY DATE_FORMAT(tanggal, '%Y-%m')
                ORDER BY DATE_FORMAT(tanggal, '%Y-%m') ASC
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                String bulan = result.getString("bulan");
                if (totalPerBulan.containsKey(bulan)) {
                    totalPerBulan.put(bulan, nilaiNonMinus(result.getBigDecimal("total")));
                }
            }
        }

        return totalPerBulan;
    }

    private int getSingleValue(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            return result.next() ? result.getInt("total") : 0;
        }
    }

    private BigDecimal getSingleDecimal(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            BigDecimal value = result.next() ? result.getBigDecimal("total") : BigDecimal.ZERO;
            return value == null ? BigDecimal.ZERO : value;
        }
    }

    private BigDecimal nilaiNonMinus(BigDecimal value) {
        if (value == null || value.signum() < 0) {
            return BigDecimal.ZERO;
        }
        return value;
    }

    public record RingkasanDashboard(int totalAnggota, BigDecimal kasMasukHariIni, BigDecimal totalPinjamanAktif) {
    }

    public record AktivitasTerbaru(Timestamp tanggal, String jenisTransaksi, String keterangan) {
    }
}
