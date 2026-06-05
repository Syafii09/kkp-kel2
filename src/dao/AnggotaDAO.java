package dao;

import koneksi.Koneksi;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AnggotaDAO {

    public String generateNoAnggotaBerikutnya() throws SQLException {
        String sql = """
                SELECT COALESCE(MAX(CAST(SUBSTRING(no_anggota, 3) AS UNSIGNED)), 0) + 1 AS nomor
                FROM anggota
                WHERE no_anggota REGEXP '^A_[0-9]+$'
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            int nomor = result.next() ? result.getInt("nomor") : 1;
            return String.format("A_%03d", nomor);
        }
    }

    public void insert(AnggotaData data) throws SQLException {
        String sql = """
                INSERT INTO anggota (
                  no_anggota, nik, nama, tempat_lahir, tanggal_lahir, jenis_kelamin,
                  alamat, kota_kabupaten, no_hp, divisi, tanggal_daftar, status
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Aktif')
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            isiParameterAnggota(statement, data);
            statement.executeUpdate();
        }
    }

    public int update(String noAnggotaLama, AnggotaData data) throws SQLException {
        String sql = """
                UPDATE anggota
                SET no_anggota = ?,
                    nik = ?,
                    nama = ?,
                    tempat_lahir = ?,
                    tanggal_lahir = ?,
                    jenis_kelamin = ?,
                    alamat = ?,
                    kota_kabupaten = ?,
                    no_hp = ?,
                    divisi = ?,
                    tanggal_daftar = ?
                WHERE no_anggota = ?
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            isiParameterAnggota(statement, data);
            statement.setString(12, noAnggotaLama);
            return statement.executeUpdate();
        }
    }

    public AnggotaData getByNoAnggota(String noAnggota) throws SQLException {
        String sql = """
                SELECT no_anggota, nama, tempat_lahir, tanggal_lahir, jenis_kelamin,
                       nik, alamat, kota_kabupaten, no_hp, divisi, tanggal_daftar, status
                FROM anggota
                WHERE no_anggota = ?
                LIMIT 1
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, noAnggota);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapAnggota(result) : null;
            }
        }
    }

    public AnggotaData getById(int idAnggota) throws SQLException {
        String sql = """
                SELECT no_anggota, nama, tempat_lahir, tanggal_lahir, jenis_kelamin,
                       nik, alamat, kota_kabupaten, no_hp, divisi, tanggal_daftar, status
                FROM anggota
                WHERE id_anggota = ?
                LIMIT 1
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idAnggota);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapAnggota(result) : null;
            }
        }
    }

    public String getStatusById(int idAnggota) throws SQLException {
        String sql = "SELECT status FROM anggota WHERE id_anggota = ? LIMIT 1";
        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idAnggota);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getString("status") : null;
            }
        }
    }

    public void lengkapiDataDiri(int idAnggota, AnggotaData data) throws SQLException {
        String sql = """
                UPDATE anggota
                SET nik = ?,
                    nama = ?,
                    tempat_lahir = ?,
                    tanggal_lahir = ?,
                    jenis_kelamin = ?,
                    alamat = ?,
                    kota_kabupaten = ?,
                    no_hp = ?,
                    divisi = ?,
                    status = 'Aktif'
                WHERE id_anggota = ?
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setStringNullable(statement, 1, data.nik());
            statement.setString(2, data.nama());
            statement.setString(3, data.tempatLahir());
            setTanggal(statement, 4, data.tanggalLahir());
            statement.setString(5, data.jenisKelamin());
            statement.setString(6, data.alamat());
            setStringNullable(statement, 7, data.kotaKabupaten());
            statement.setString(8, data.noHp());
            statement.setString(9, data.divisi());
            statement.setInt(10, idAnggota);

            if (statement.executeUpdate() == 0) {
                throw new SQLException("Data anggota tidak ditemukan.");
            }
        }
    }

    public List<AnggotaData> search(String keyword) throws SQLException {
        String sql = """
                SELECT no_anggota, nik, nama, tempat_lahir, tanggal_lahir, no_hp,
                       alamat, divisi, tanggal_daftar, status, jenis_kelamin, kota_kabupaten
                FROM anggota
                WHERE ? = ''
                   OR no_anggota LIKE ?
                   OR COALESCE(nik, '') LIKE ?
                   OR nama LIKE ?
                   OR COALESCE(tempat_lahir, '') LIKE ?
                   OR COALESCE(no_hp, '') LIKE ?
                   OR COALESCE(alamat, '') LIKE ?
                   OR COALESCE(divisi, '') LIKE ?
                   OR status LIKE ?
                ORDER BY id_anggota ASC
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String search = keyword == null ? "" : keyword.trim();
            String likeSearch = "%" + search + "%";
            statement.setString(1, search);
            statement.setString(2, likeSearch);
            statement.setString(3, likeSearch);
            statement.setString(4, likeSearch);
            statement.setString(5, likeSearch);
            statement.setString(6, likeSearch);
            statement.setString(7, likeSearch);
            statement.setString(8, likeSearch);
            statement.setString(9, likeSearch);

            try (ResultSet result = statement.executeQuery()) {
                List<AnggotaData> data = new ArrayList<>();
                while (result.next()) {
                    data.add(mapAnggota(result));
                }
                return data;
            }
        }
    }

    public DetailAnggota getDetail(String noAnggota) throws SQLException {
        String sql = """
                SELECT
                  a.no_anggota,
                  a.nik,
                  a.nama,
                  a.tempat_lahir,
                  a.tanggal_lahir,
                  a.alamat,
                  a.no_hp,
                  a.divisi,
                  a.status,
                  a.tanggal_daftar,
                  COALESCE(ts.total_simpanan, 0) AS total_simpanan,
                  COALESCE(tp.total_pinjaman_aktif, 0) AS total_pinjaman
                FROM anggota a
                LEFT JOIN v_total_simpanan_anggota ts ON ts.id_anggota = a.id_anggota
                LEFT JOIN v_total_pinjaman_anggota tp ON tp.id_anggota = a.id_anggota
                WHERE a.no_anggota = ?
                LIMIT 1
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, noAnggota);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }

                return new DetailAnggota(
                        result.getString("no_anggota"),
                        result.getString("nik"),
                        result.getString("nama"),
                        result.getString("tempat_lahir"),
                        result.getDate("tanggal_lahir"),
                        result.getString("alamat"),
                        result.getString("no_hp"),
                        result.getString("divisi"),
                        result.getString("status"),
                        result.getDate("tanggal_daftar"),
                        result.getBigDecimal("total_simpanan"),
                        result.getBigDecimal("total_pinjaman")
                );
            }
        }
    }

    public void deleteJikaSelesai(String noAnggota) throws SQLException {
        try (Connection connection = Koneksi.getConnection()) {
            boolean autoCommitLama = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {
                int idAnggota = getIdAnggota(connection, noAnggota);
                validasiAnggotaBolehDihapus(connection, idAnggota);

                executeUpdate(connection, "UPDATE users SET id_anggota = NULL WHERE id_anggota = ?", idAnggota);
                executeUpdate(connection, "DELETE FROM transaksi WHERE id_anggota = ?", idAnggota);
                executeUpdate(connection, """
                        DELETE a
                        FROM angsuran a
                        JOIN pinjaman p ON p.id_pinjaman = a.id_pinjaman
                        WHERE p.id_anggota = ?
                        """, idAnggota);
                executeUpdate(connection, "DELETE FROM pinjaman WHERE id_anggota = ?", idAnggota);
                executeUpdate(connection, "DELETE FROM simpanan WHERE id_anggota = ?", idAnggota);

                int jumlahHapus = executeUpdate(connection, "DELETE FROM anggota WHERE id_anggota = ?", idAnggota);
                if (jumlahHapus == 0) {
                    throw new SQLException("Data anggota tidak ditemukan.");
                }

                connection.commit();
            } catch (SQLException | RuntimeException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(autoCommitLama);
            }
        }
    }

    private int getIdAnggota(Connection connection, String noAnggota) throws SQLException {
        String sql = "SELECT id_anggota FROM anggota WHERE no_anggota = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, noAnggota);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt("id_anggota");
                }
            }
        }
        throw new SQLException("Data anggota tidak ditemukan.");
    }

    private void validasiAnggotaBolehDihapus(Connection connection, int idAnggota) throws SQLException {
        int pinjamanBelumSelesai = countByAnggota(connection, """
                SELECT COUNT(*)
                FROM pinjaman
                WHERE id_anggota = ?
                  AND status NOT IN ('Lunas', 'Ditolak')
                """, idAnggota);

        if (pinjamanBelumSelesai > 0) {
            throw new SQLException("Anggota masih memiliki pinjaman yang belum selesai/lunas.");
        }

        int angsuranBelumSelesai = countByAnggota(connection, """
                SELECT COUNT(*)
                FROM angsuran a
                JOIN pinjaman p ON p.id_pinjaman = a.id_pinjaman
                WHERE p.id_anggota = ?
                  AND a.status <> 'Dibayar'
                """, idAnggota);

        if (angsuranBelumSelesai > 0) {
            throw new SQLException("Anggota masih memiliki angsuran yang belum selesai dibayar.");
        }
    }

    private int countByAnggota(Connection connection, String sql, int idAnggota) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idAnggota);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getInt(1) : 0;
            }
        }
    }

    private int executeUpdate(Connection connection, String sql, int idAnggota) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idAnggota);
            return statement.executeUpdate();
        }
    }

    private void isiParameterAnggota(PreparedStatement statement, AnggotaData data) throws SQLException {
        statement.setString(1, data.noAnggota());
        setStringNullable(statement, 2, data.nik());
        statement.setString(3, data.nama());
        statement.setString(4, data.tempatLahir());
        setTanggal(statement, 5, data.tanggalLahir());
        statement.setString(6, data.jenisKelamin());
        statement.setString(7, data.alamat());
        setStringNullable(statement, 8, data.kotaKabupaten());
        statement.setString(9, data.noHp());
        statement.setString(10, data.divisi());
        setTanggal(statement, 11, data.tanggalDaftar());
    }

    private AnggotaData mapAnggota(ResultSet result) throws SQLException {
        return new AnggotaData(
                result.getString("no_anggota"),
                result.getString("nik"),
                result.getString("nama"),
                result.getString("tempat_lahir"),
                result.getDate("tanggal_lahir"),
                result.getString("jenis_kelamin"),
                result.getString("alamat"),
                result.getString("kota_kabupaten"),
                result.getString("no_hp"),
                result.getString("divisi"),
                result.getDate("tanggal_daftar"),
                result.getString("status")
        );
    }

    private void setStringNullable(PreparedStatement statement, int index, String value) throws SQLException {
        if (value == null || value.isBlank()) {
            statement.setNull(index, java.sql.Types.VARCHAR);
            return;
        }
        statement.setString(index, value.trim());
    }

    private void setTanggal(PreparedStatement statement, int index, Date date) throws SQLException {
        if (date == null) {
            statement.setNull(index, java.sql.Types.DATE);
            return;
        }
        statement.setDate(index, new java.sql.Date(date.getTime()));
    }

    public record AnggotaData(String noAnggota, String nik, String nama, String tempatLahir,
                              Date tanggalLahir, String jenisKelamin, String alamat,
                              String kotaKabupaten, String noHp, String divisi,
                              Date tanggalDaftar, String status) {
    }

    public record DetailAnggota(String noAnggota, String nik, String nama, String tempatLahir,
                                Date tanggalLahir, String alamat, String noHp, String divisi,
                                String status, Date tanggalDaftar, BigDecimal totalSimpanan,
                                BigDecimal totalPinjaman) {
    }
}
