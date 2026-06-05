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

public class SimpananDAO {
    private final DatabaseMigrationDAO migrationDAO = new DatabaseMigrationDAO();

    public List<String> getJenisSimpanan() throws SQLException {
        String sql = "SELECT nama_jenis FROM jenis_simpanan ORDER BY id_jenis_simpanan ASC";

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            List<String> data = new ArrayList<>();
            while (result.next()) {
                data.add(result.getString("nama_jenis"));
            }
            return data;
        }
    }

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

    public void insert(String noAnggota, String jenisSimpanan, BigDecimal nominal, String keterangan) throws SQLException {
        try (Connection connection = Koneksi.getConnection()) {
            migrationDAO.pastikanKolomPetugasTransaksi(connection);
            connection.setAutoCommit(false);

            try {
                int idAnggota = getIdAnggota(connection, noAnggota);
                int idJenisSimpanan = getIdJenisSimpanan(connection, jenisSimpanan);
                BigDecimal saldoBaru = getSaldoSimpananAnggota(connection, idAnggota).add(nominal);
                int idSimpanan = insertSimpanan(connection, idAnggota, idJenisSimpanan, nominal, saldoBaru, keterangan);
                insertTransaksiSimpanan(connection, idAnggota, idSimpanan, nominal, keterangan);
                connection.commit();
            } catch (SQLException | RuntimeException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public List<SimpananRow> searchByAnggota(String noAnggota, String keyword) throws SQLException {
        String sql = """
                SELECT s.tanggal, a.no_anggota, a.nama, js.nama_jenis, s.nominal, s.saldo, s.keterangan
                FROM simpanan s
                JOIN anggota a ON a.id_anggota = s.id_anggota
                JOIN jenis_simpanan js ON js.id_jenis_simpanan = s.id_jenis_simpanan
                WHERE a.no_anggota = ?
                  AND (? = ''
                       OR a.no_anggota LIKE ?
                       OR a.nama LIKE ?
                       OR js.nama_jenis LIKE ?
                       OR COALESCE(s.keterangan, '') LIKE ?
                       OR CAST(s.tanggal AS CHAR) LIKE ?)
                ORDER BY s.id_simpanan ASC
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
                List<SimpananRow> data = new ArrayList<>();
                while (result.next()) {
                    data.add(new SimpananRow(
                            result.getDate("tanggal"),
                            result.getString("nama_jenis"),
                            result.getBigDecimal("nominal"),
                            result.getBigDecimal("saldo")
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

    private int getIdJenisSimpanan(Connection connection, String namaJenis) throws SQLException {
        String sql = "SELECT id_jenis_simpanan FROM jenis_simpanan WHERE nama_jenis = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, namaJenis);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt("id_jenis_simpanan");
                }
            }
        }
        throw new SQLException("Jenis simpanan tidak ditemukan.");
    }

    private BigDecimal getSaldoSimpananAnggota(Connection connection, int idAnggota) throws SQLException {
        String sql = "SELECT COALESCE(SUM(nominal), 0) AS saldo FROM simpanan WHERE id_anggota = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idAnggota);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getBigDecimal("saldo") : BigDecimal.ZERO;
            }
        }
    }

    private int insertSimpanan(Connection connection, int idAnggota, int idJenisSimpanan,
            BigDecimal nominal, BigDecimal saldoBaru, String keterangan) throws SQLException {
        String sql = """
                INSERT INTO simpanan (id_anggota, id_jenis_simpanan, tanggal, nominal, saldo, keterangan)
                VALUES (?, ?, CURDATE(), ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, idAnggota);
            statement.setInt(2, idJenisSimpanan);
            statement.setBigDecimal(3, nominal);
            statement.setBigDecimal(4, saldoBaru);
            statement.setString(5, keterangan);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("ID simpanan gagal dibuat.");
    }

    private void insertTransaksiSimpanan(Connection connection, int idAnggota, int idSimpanan,
            BigDecimal nominal, String keterangan) throws SQLException {
        String sql = """
                INSERT INTO transaksi (
                  jenis_transaksi, id_anggota, id_user, referensi_tabel, referensi_id, debet, kredit, keterangan
                ) VALUES ('Simpanan', ?, ?, 'simpanan', ?, ?, 0, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idAnggota);
            statement.setObject(2, SesiLogin.getIdUser());
            statement.setInt(3, idSimpanan);
            statement.setBigDecimal(4, nominal);
            statement.setString(5, keterangan);
            statement.executeUpdate();
        }
    }

    public record AnggotaPilihan(String noAnggota, String nama) {
        public String pilihan() {
            return noAnggota + " - " + nama;
        }
    }

    public record SimpananRow(Date tanggal, String namaJenis, BigDecimal nominal, BigDecimal saldo) {
    }
}
