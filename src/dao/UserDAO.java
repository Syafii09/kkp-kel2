package dao;

import koneksi.Koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public LoginResult login(String akun, String password) throws SQLException {
        String sql = """
                SELECT u.id_user, u.id_anggota, u.nama, g.nama_group, a.status AS status_anggota
                FROM users u
                JOIN groups g ON g.id_group = u.id_group
                LEFT JOIN anggota a ON a.id_anggota = u.id_anggota
                WHERE (u.email = ? OR u.nama = ?)
                  AND u.password_hash = SHA2(?, 256)
                  AND u.status = 'Aktif'
                LIMIT 1
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, akun);
            statement.setString(2, akun);
            statement.setString(3, password);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }

                Integer idUser = result.getInt("id_user");
                if (result.wasNull()) {
                    idUser = null;
                }

                Integer idAnggota = result.getInt("id_anggota");
                if (result.wasNull()) {
                    idAnggota = null;
                }

                return new LoginResult(
                        idUser,
                        idAnggota,
                        result.getString("nama"),
                        result.getString("nama_group"),
                        result.getString("status_anggota")
                );
            }
        }
    }

    public int tambahPengguna(String nama, String email, String password) throws SQLException {
        try (Connection connection = Koneksi.getConnection()) {
            boolean autoCommitLama = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {
                int idGroup = getIdGroup(connection, "Pengguna");
                String noAnggota = generateNoAnggotaBerikutnya(connection);
                int idAnggota = insertAnggotaSignup(connection, noAnggota, nama);
                int inserted = insertUserSignup(connection, idGroup, idAnggota, nama, email, password);
                connection.commit();
                return inserted;
            } catch (SQLException | RuntimeException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(autoCommitLama);
            }
        }
    }

    private int getIdGroup(Connection connection, String namaGroup) throws SQLException {
        String sql = "SELECT id_group FROM groups WHERE nama_group = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, namaGroup);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt("id_group");
                }
            }
        }
        throw new SQLException("Group " + namaGroup + " belum tersedia di database.");
    }

    private String generateNoAnggotaBerikutnya(Connection connection) throws SQLException {
        String sql = """
                SELECT COALESCE(MAX(CAST(SUBSTRING(no_anggota, 3) AS UNSIGNED)), 0) + 1 AS nomor
                FROM anggota
                WHERE no_anggota REGEXP '^A_[0-9]+$'
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            int nomor = result.next() ? result.getInt("nomor") : 1;
            return String.format("A_%03d", nomor);
        }
    }

    private int insertAnggotaSignup(Connection connection, String noAnggota, String nama) throws SQLException {
        String sql = """
                INSERT INTO anggota (no_anggota, nama, tanggal_daftar, status)
                VALUES (?, ?, CURDATE(), 'Calon')
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, noAnggota);
            statement.setString(2, nama);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Gagal membuat data anggota.");
    }

    private int insertUserSignup(Connection connection, int idGroup, int idAnggota, String nama, String email, String password)
            throws SQLException {
        pastikanKolomJabatan(connection);
        String sql = """
                INSERT INTO users (id_group, id_anggota, nama, email, password_hash, status, jabatan)
                VALUES (?, ?, ?, ?, SHA2(?, 256), 'Aktif', ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idGroup);
            statement.setInt(2, idAnggota);
            statement.setString(3, nama);
            statement.setString(4, email);
            statement.setString(5, password);
            statement.setString(6, "Anggota");
            return statement.executeUpdate();
        }
    }

    public void pastikanRoleDefault() throws SQLException {
        String sql = """
                INSERT IGNORE INTO groups (nama_group, deskripsi)
                VALUES
                  ('Super Admin', 'Akses penuh ke seluruh menu'),
                  ('Admin', 'Akses administrasi dan pengelolaan data'),
                  ('Pengguna', 'Akses pengguna atau anggota koperasi')
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }

    public List<RoleData> getRoles() throws SQLException {
        String sql = """
                SELECT id_group, nama_group
                FROM groups
                ORDER BY FIELD(nama_group, 'Super Admin', 'Admin', 'Pengguna'), nama_group
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            List<RoleData> data = new ArrayList<>();
            while (result.next()) {
                data.add(new RoleData(result.getInt("id_group"), result.getString("nama_group")));
            }
            return data;
        }
    }

    public List<UserData> getUsersByGroup(Integer idGroup) throws SQLException {
        String sql = """
                SELECT u.id_user, u.nama, u.email, a.no_anggota, u.jabatan,
                       g.nama_group, u.status, u.created_at
                FROM users u
                JOIN groups g ON g.id_group = u.id_group
                LEFT JOIN anggota a ON a.id_anggota = u.id_anggota
                WHERE ? IS NULL OR g.id_group = ?
                ORDER BY g.nama_group, u.nama
                """;

        try (Connection connection = Koneksi.getConnection()) {
            pastikanKolomJabatan(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (idGroup == null) {
                statement.setNull(1, java.sql.Types.INTEGER);
                statement.setNull(2, java.sql.Types.INTEGER);
            } else {
                statement.setInt(1, idGroup);
                statement.setInt(2, idGroup);
            }

            try (ResultSet result = statement.executeQuery()) {
                List<UserData> data = new ArrayList<>();
                while (result.next()) {
                    data.add(new UserData(
                            result.getInt("id_user"),
                            result.getString("nama"),
                            result.getString("email"),
                            result.getString("no_anggota"),
                            result.getString("jabatan"),
                            result.getString("nama_group"),
                            result.getString("status"),
                            result.getTimestamp("created_at")
                    ));
                }
                return data;
            }
            }
        }
    }

    public Integer getIdAnggotaAktif(String noAnggota) throws SQLException {
        String sql = "SELECT id_anggota FROM anggota WHERE no_anggota = ? AND status = 'Aktif' LIMIT 1";

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, noAnggota);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getInt("id_anggota") : null;
            }
        }
    }

    public void insert(String nama, String email, String password, int idGroup, Integer idAnggota, String jabatan) throws SQLException {
        String sql = """
                INSERT INTO users (id_group, id_anggota, nama, email, password_hash, status, jabatan)
                VALUES (?, ?, ?, ?, SHA2(?, 256), 'Aktif', ?)
                """;

        try (Connection connection = Koneksi.getConnection()) {
            pastikanKolomJabatan(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idGroup);
            setNullableInt(statement, 2, idAnggota);
            statement.setString(3, nama);
            statement.setString(4, email);
            statement.setString(5, password);
            statement.setString(6, kosongJadiNull(jabatan));
            statement.executeUpdate();
            }
        }
    }

    public void update(int idUser, String nama, String email, String password, int idGroup, Integer idAnggota, String jabatan)
            throws SQLException {
        String sql = password == null || password.isEmpty()
                ? "UPDATE users SET id_group = ?, id_anggota = ?, nama = ?, email = ?, jabatan = ? WHERE id_user = ?"
                : "UPDATE users SET id_group = ?, id_anggota = ?, nama = ?, email = ?, jabatan = ?, password_hash = SHA2(?, 256) WHERE id_user = ?";

        try (Connection connection = Koneksi.getConnection()) {
            pastikanKolomJabatan(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idGroup);
            setNullableInt(statement, 2, idAnggota);
            statement.setString(3, nama);
            statement.setString(4, email);
            statement.setString(5, kosongJadiNull(jabatan));
            if (password == null || password.isEmpty()) {
                statement.setInt(6, idUser);
            } else {
                statement.setString(6, password);
                statement.setInt(7, idUser);
            }
            statement.executeUpdate();
            }
        }
    }

    public void delete(int idUser) throws SQLException {
        String sql = "DELETE FROM users WHERE id_user = ?";
        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idUser);
            statement.executeUpdate();
        }
    }

    private void setNullableInt(PreparedStatement statement, int index, Integer value) throws SQLException {
        if (value == null) {
            statement.setNull(index, java.sql.Types.INTEGER);
            return;
        }
        statement.setInt(index, value);
    }

    private void pastikanKolomJabatan(Connection connection) throws SQLException {
        String cekSql = """
                SELECT COUNT(*) AS jumlah
                FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = 'users'
                  AND COLUMN_NAME = 'jabatan'
                """;

        try (PreparedStatement statement = connection.prepareStatement(cekSql);
             ResultSet result = statement.executeQuery()) {
            if (result.next() && result.getInt("jumlah") > 0) {
                return;
            }
        }

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("ALTER TABLE users ADD COLUMN jabatan varchar(100) DEFAULT NULL AFTER email");
        }
    }

    private String kosongJadiNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public record LoginResult(Integer idUser, Integer idAnggota, String nama, String group, String statusAnggota) {
    }

    public record RoleData(int idGroup, String namaGroup) {
    }

    public record UserData(int idUser, String nama, String email, String noAnggota, String jabatan,
                           String namaGroup, String status, java.sql.Timestamp createdAt) {
    }
}
