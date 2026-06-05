package dao;

import koneksi.Koneksi;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import utils.SesiLogin;

public class AngsuranDAO {
    private final DatabaseMigrationDAO migrationDAO = new DatabaseMigrationDAO();

    public List<PinjamanPilihan> getPinjamanAktif() throws SQLException {
        String sql = """
                SELECT p.id_pinjaman, p.no_pinjaman, a.no_anggota, a.nama
                FROM pinjaman p
                JOIN anggota a ON a.id_anggota = p.id_anggota
                WHERE p.status = 'Aktif'
                ORDER BY p.id_pinjaman ASC
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            List<PinjamanPilihan> data = new ArrayList<>();
            while (result.next()) {
                data.add(new PinjamanPilihan(
                        result.getInt("id_pinjaman"),
                        result.getString("no_pinjaman"),
                        result.getString("no_anggota"),
                        result.getString("nama")
                ));
            }
            return data;
        }
    }

    public PinjamanInfo getPinjamanInfo(int idPinjaman) throws SQLException {
        try (Connection connection = Koneksi.getConnection()) {
            return getPinjamanInfo(connection, idPinjaman);
        }
    }

    public int getAngsuranBerikutnya(int idPinjaman) throws SQLException {
        try (Connection connection = Koneksi.getConnection()) {
            return getAngsuranBerikutnya(connection, idPinjaman);
        }
    }

    public void insertAngsuran(int idPinjaman, int angsuranKe,
            java.util.Date tanggalBayar, BigDecimal jumlahBayar, String keterangan) throws SQLException {
        try (Connection connection = Koneksi.getConnection()) {
            migrationDAO.pastikanKolomPetugasTransaksi(connection);
            connection.setAutoCommit(false);

            try {
                PinjamanInfo pinjaman = getPinjamanInfo(connection, idPinjaman);
                int idAngsuran = insertAngsuran(connection, idPinjaman, angsuranKe, tanggalBayar, jumlahBayar, keterangan);
                insertTransaksiAngsuran(connection, pinjaman.idAnggota(), idAngsuran, jumlahBayar, keterangan);

                BigDecimal sisaSetelahBayar = pinjaman.sisaPinjaman().subtract(jumlahBayar);
                if (sisaSetelahBayar.compareTo(BigDecimal.ZERO) <= 0) {
                    updateStatusPinjaman(connection, idPinjaman, "Lunas");
                }
                connection.commit();
            } catch (SQLException | RuntimeException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public List<AngsuranRow> getRiwayatAngsuran(int idPinjaman) throws SQLException {
        String sql = """
                SELECT angsuran_ke, tanggal_bayar, jumlah_bayar, status, keterangan
                FROM angsuran
                WHERE id_pinjaman = ?
                ORDER BY angsuran_ke ASC
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idPinjaman);
            try (ResultSet result = statement.executeQuery()) {
                List<AngsuranRow> data = new ArrayList<>();
                while (result.next()) {
                    data.add(new AngsuranRow(
                            result.getInt("angsuran_ke"),
                            result.getDate("tanggal_bayar"),
                            result.getBigDecimal("jumlah_bayar"),
                            result.getString("status")
                    ));
                }
                return data;
            }
        }
    }

    private PinjamanInfo getPinjamanInfo(Connection connection, int idPinjaman) throws SQLException {
        String sql = """
                SELECT p.id_anggota, p.jumlah_pinjaman, p.angsuran_per_bulan,
                       COALESCE(SUM(a.jumlah_bayar), 0) AS total_dibayar
                FROM pinjaman p
                LEFT JOIN angsuran a ON a.id_pinjaman = p.id_pinjaman
                WHERE p.id_pinjaman = ?
                GROUP BY p.id_pinjaman, p.id_anggota, p.jumlah_pinjaman, p.angsuran_per_bulan
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idPinjaman);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new PinjamanInfo(
                            result.getInt("id_anggota"),
                            result.getBigDecimal("jumlah_pinjaman"),
                            result.getBigDecimal("angsuran_per_bulan"),
                            result.getBigDecimal("total_dibayar")
                    );
                }
            }
        }
        throw new SQLException("Data pinjaman tidak ditemukan.");
    }

    private int getAngsuranBerikutnya(Connection connection, int idPinjaman) throws SQLException {
        String sql = "SELECT COALESCE(MAX(angsuran_ke), 0) + 1 AS berikutnya FROM angsuran WHERE id_pinjaman = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idPinjaman);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getInt("berikutnya") : 1;
            }
        }
    }

    private int insertAngsuran(Connection connection, int idPinjaman, int angsuranKe,
            java.util.Date tanggalBayar, BigDecimal jumlahBayar, String keterangan) throws SQLException {
        String sql = """
                INSERT INTO angsuran (
                  id_pinjaman, angsuran_ke, tanggal_jatuh_tempo, tanggal_bayar,
                  jumlah_bayar, status, keterangan
                ) VALUES (?, ?, NULL, ?, ?, 'Dibayar', ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, idPinjaman);
            statement.setInt(2, angsuranKe);
            statement.setDate(3, new java.sql.Date(tanggalBayar.getTime()));
            statement.setBigDecimal(4, jumlahBayar);
            statement.setString(5, keterangan);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("ID angsuran gagal dibuat.");
    }

    private void insertTransaksiAngsuran(Connection connection, int idAnggota, int idAngsuran,
            BigDecimal jumlahBayar, String keterangan) throws SQLException {
        String sql = """
                INSERT INTO transaksi (
                  jenis_transaksi, id_anggota, id_user, referensi_tabel, referensi_id, debet, kredit, keterangan
                ) VALUES ('Angsuran', ?, ?, 'angsuran', ?, ?, 0, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idAnggota);
            statement.setObject(2, SesiLogin.getIdUser());
            statement.setInt(3, idAngsuran);
            statement.setBigDecimal(4, jumlahBayar);
            statement.setString(5, keterangan);
            statement.executeUpdate();
        }
    }

    private void updateStatusPinjaman(Connection connection, int idPinjaman, String status) throws SQLException {
        String sql = "UPDATE pinjaman SET status = ? WHERE id_pinjaman = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, idPinjaman);
            statement.executeUpdate();
        }
    }

    public record PinjamanPilihan(int idPinjaman, String noPinjaman, String noAnggota, String nama) {
        public String pilihan() {
            return noPinjaman + " - " + noAnggota + " - " + nama;
        }
    }

    public record PinjamanInfo(int idAnggota, BigDecimal jumlahPinjaman,
                               BigDecimal angsuranPerBulan, BigDecimal totalDibayar) {
        public PinjamanInfo {
            jumlahPinjaman = jumlahPinjaman == null ? BigDecimal.ZERO : jumlahPinjaman;
            angsuranPerBulan = angsuranPerBulan == null ? BigDecimal.ZERO : angsuranPerBulan;
            totalDibayar = totalDibayar == null ? BigDecimal.ZERO : totalDibayar;
        }

        public BigDecimal sisaPinjaman() {
            return jumlahPinjaman.subtract(totalDibayar);
        }
    }

    public record AngsuranRow(int angsuranKe, Date tanggalBayar, BigDecimal jumlahBayar, String status) {
    }
}
