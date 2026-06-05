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

public class PinjamanDAO {
    private final DatabaseMigrationDAO migrationDAO = new DatabaseMigrationDAO();

    public List<AnggotaPilihan> getAnggotaAktif() throws SQLException {
        String sql = """
                SELECT no_anggota, nama
                FROM anggota
                WHERE status = 'Aktif'
                ORDER BY id_anggota ASC
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            List<AnggotaPilihan> data = new ArrayList<>();
            while (result.next()) {
                data.add(new AnggotaPilihan(result.getString("no_anggota"), result.getString("nama")));
            }
            return data;
        }
    }

    public void insert(String noAnggota, BigDecimal jumlahPinjaman, int tenor,
            BigDecimal bungaPersen, BigDecimal angsuranPerBulan, String tujuan) throws SQLException {
        try (Connection connection = Koneksi.getConnection()) {
            migrationDAO.pastikanKolomPetugasTransaksi(connection);
            connection.setAutoCommit(false);

            try {
                int idAnggota = getIdAnggota(connection, noAnggota);
                String noPinjaman = buatNoPinjaman(connection);
                int idPinjaman = insertPinjaman(connection, noPinjaman, idAnggota, jumlahPinjaman, tenor,
                        bungaPersen, angsuranPerBulan, tujuan);
                insertTransaksiPinjaman(connection, idAnggota, idPinjaman, jumlahPinjaman, tujuan);
                connection.commit();
            } catch (SQLException | RuntimeException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public List<PinjamanRow> searchByAnggota(String noAnggota, String keyword) throws SQLException {
        String sql = """
                SELECT p.no_pinjaman, a.no_anggota, a.nama, p.tanggal_pinjaman,
                       p.jumlah_pinjaman, p.tenor_bulan, p.angsuran_per_bulan, p.status
                FROM pinjaman p
                JOIN anggota a ON a.id_anggota = p.id_anggota
                WHERE a.no_anggota = ?
                  AND (? = ''
                       OR p.no_pinjaman LIKE ?
                       OR a.no_anggota LIKE ?
                       OR a.nama LIKE ?
                       OR p.status LIKE ?
                       OR CAST(p.tanggal_pinjaman AS CHAR) LIKE ?)
                ORDER BY p.id_pinjaman ASC
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String search = keyword == null ? "" : keyword.trim();
            String likeSearch = "%" + search + "%";
            statement.setString(1, noAnggota);
            statement.setString(2, search);
            statement.setString(3, likeSearch);
            statement.setString(4, likeSearch);
            statement.setString(5, likeSearch);
            statement.setString(6, likeSearch);
            statement.setString(7, likeSearch);

            try (ResultSet result = statement.executeQuery()) {
                List<PinjamanRow> data = new ArrayList<>();
                while (result.next()) {
                    data.add(new PinjamanRow(
                            result.getString("nama"),
                            result.getDate("tanggal_pinjaman"),
                            result.getBigDecimal("jumlah_pinjaman"),
                            result.getInt("tenor_bulan"),
                            result.getBigDecimal("angsuran_per_bulan"),
                            result.getString("status")
                    ));
                }
                return data;
            }
        }
    }

    private int getIdAnggota(Connection connection, String noAnggota) throws SQLException {
        String sql = "SELECT id_anggota FROM anggota WHERE no_anggota = ? AND status = 'Aktif' LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, noAnggota);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt("id_anggota");
                }
            }
        }
        throw new SQLException("Anggota tidak ditemukan atau tidak aktif.");
    }

    private String buatNoPinjaman(Connection connection) throws SQLException {
        String sql = "SELECT COALESCE(MAX(id_pinjaman), 0) + 1 AS nomor FROM pinjaman";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            if (result.next()) {
                return String.format("PJ%04d", result.getInt("nomor"));
            }
        }
        return "PJ0001";
    }

    private int insertPinjaman(Connection connection, String noPinjaman, int idAnggota,
            BigDecimal jumlahPinjaman, int tenor, BigDecimal bungaPersen,
            BigDecimal angsuranPerBulan, String tujuan) throws SQLException {
        String sql = """
                INSERT INTO pinjaman (
                  no_pinjaman, id_anggota, tanggal_pinjaman, jumlah_pinjaman,
                  tenor_bulan, bunga_persen, angsuran_per_bulan, tujuan, status
                ) VALUES (?, ?, CURDATE(), ?, ?, ?, ?, ?, 'Aktif')
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, noPinjaman);
            statement.setInt(2, idAnggota);
            statement.setBigDecimal(3, jumlahPinjaman);
            statement.setInt(4, tenor);
            statement.setBigDecimal(5, bungaPersen);
            statement.setBigDecimal(6, angsuranPerBulan);
            statement.setString(7, tujuan);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("ID pinjaman gagal dibuat.");
    }

    private void insertTransaksiPinjaman(Connection connection, int idAnggota, int idPinjaman,
            BigDecimal jumlahPinjaman, String tujuan) throws SQLException {
        String sql = """
                INSERT INTO transaksi (
                  jenis_transaksi, id_anggota, id_user, referensi_tabel, referensi_id, debet, kredit, keterangan
                ) VALUES ('Pinjaman', ?, ?, 'pinjaman', ?, 0, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idAnggota);
            statement.setObject(2, SesiLogin.getIdUser());
            statement.setInt(3, idPinjaman);
            statement.setBigDecimal(4, jumlahPinjaman);
            statement.setString(5, tujuan);
            statement.executeUpdate();
        }
    }

    public record AnggotaPilihan(String noAnggota, String nama) {
        public String pilihan() {
            return noAnggota + " - " + nama;
        }
    }

    public record PinjamanRow(String nama, Date tanggalPinjaman, BigDecimal jumlah,
                              int tenor, BigDecimal angsuranPerBulan, String status) {
    }
}
