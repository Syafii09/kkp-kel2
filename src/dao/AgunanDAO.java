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

public class AgunanDAO {

    public void pastikanTabelAgunan() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS agunan (
                    id_agunan int(11) NOT NULL AUTO_INCREMENT,
                    id_pinjaman int(11) NOT NULL,
                    jenis_agunan varchar(100) NOT NULL,
                    nilai_taksir decimal(15,2) NOT NULL DEFAULT 0.00,
                    tanggal date NOT NULL,
                    status varchar(30) NOT NULL DEFAULT 'Aktif',
                    deskripsi text DEFAULT NULL,
                    created_at timestamp NOT NULL DEFAULT current_timestamp(),
                    updated_at timestamp NULL DEFAULT NULL ON UPDATE current_timestamp(),
                    PRIMARY KEY (id_agunan),
                    KEY fk_agunan_pinjaman (id_pinjaman),
                    CONSTRAINT fk_agunan_pinjaman FOREIGN KEY (id_pinjaman)
                        REFERENCES pinjaman (id_pinjaman)
                        ON DELETE CASCADE ON UPDATE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """;

        try (Connection connection = Koneksi.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    public List<AgunanRow> search(String jenis, String status) throws SQLException {
        String sql = """
                SELECT ag.id_agunan, p.no_pinjaman, a.no_anggota, a.nama,
                       ag.jenis_agunan, ag.nilai_taksir, ag.tanggal, ag.status, ag.deskripsi
                FROM agunan ag
                JOIN pinjaman p ON p.id_pinjaman = ag.id_pinjaman
                JOIN anggota a ON a.id_anggota = p.id_anggota
                WHERE (? = 'Semua Jenis' OR ag.jenis_agunan = ?)
                  AND (? = 'Semua Status' OR ag.status = ?)
                ORDER BY ag.id_agunan ASC
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, jenis);
            statement.setString(2, jenis);
            statement.setString(3, status);
            statement.setString(4, status);

            try (ResultSet result = statement.executeQuery()) {
                List<AgunanRow> rows = new ArrayList<>();
                while (result.next()) {
                    rows.add(mapRow(result));
                }
                return rows;
            }
        }
    }

    public AgunanData getById(int idAgunan) throws SQLException {
        String sql = """
                SELECT ag.id_agunan, p.no_pinjaman, ag.jenis_agunan,
                       ag.nilai_taksir, ag.tanggal, ag.status, ag.deskripsi
                FROM agunan ag
                JOIN pinjaman p ON p.id_pinjaman = ag.id_pinjaman
                WHERE ag.id_agunan = ?
                LIMIT 1
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idAgunan);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }
                return new AgunanData(
                        result.getInt("id_agunan"),
                        result.getString("no_pinjaman"),
                        result.getString("jenis_agunan"),
                        result.getBigDecimal("nilai_taksir"),
                        result.getDate("tanggal"),
                        result.getString("status"),
                        result.getString("deskripsi")
                );
            }
        }
    }

    public void insert(AgunanData data) throws SQLException {
        String sql = """
                INSERT INTO agunan (id_pinjaman, jenis_agunan, nilai_taksir, tanggal, status, deskripsi)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            isiParameter(statement, connection, data);
            statement.executeUpdate();
        }
    }

    public int update(AgunanData data) throws SQLException {
        String sql = """
                UPDATE agunan
                SET id_pinjaman = ?, jenis_agunan = ?, nilai_taksir = ?,
                    tanggal = ?, status = ?, deskripsi = ?
                WHERE id_agunan = ?
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            isiParameter(statement, connection, data);
            statement.setInt(7, data.idAgunan());
            return statement.executeUpdate();
        }
    }

    public int delete(int idAgunan) throws SQLException {
        String sql = "DELETE FROM agunan WHERE id_agunan = ?";
        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idAgunan);
            return statement.executeUpdate();
        }
    }

    public List<String> getJenisAgunan() throws SQLException {
        String sql = "SELECT DISTINCT jenis_agunan FROM agunan ORDER BY jenis_agunan ASC";
        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            List<String> data = new ArrayList<>();
            while (result.next()) {
                data.add(result.getString("jenis_agunan"));
            }
            return data;
        }
    }

    public List<String> getStatusAgunan() throws SQLException {
        String sql = "SELECT DISTINCT status FROM agunan ORDER BY status ASC";
        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            List<String> data = new ArrayList<>();
            while (result.next()) {
                data.add(result.getString("status"));
            }
            return data;
        }
    }

    public List<PinjamanPilihan> getPinjamanAktif() throws SQLException {
        String sql = """
                SELECT p.no_pinjaman, a.no_anggota, a.nama
                FROM pinjaman p
                JOIN anggota a ON a.id_anggota = p.id_anggota
                WHERE p.status <> 'Ditolak'
                ORDER BY p.id_pinjaman ASC
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            List<PinjamanPilihan> data = new ArrayList<>();
            while (result.next()) {
                data.add(new PinjamanPilihan(
                        result.getString("no_pinjaman"),
                        result.getString("no_anggota"),
                        result.getString("nama")
                ));
            }
            return data;
        }
    }

    private void isiParameter(PreparedStatement statement, Connection connection, AgunanData data) throws SQLException {
        statement.setInt(1, getIdPinjaman(connection, data.noPinjaman()));
        statement.setString(2, data.jenisAgunan());
        statement.setBigDecimal(3, data.nilaiTaksir());
        statement.setDate(4, new Date(data.tanggal().getTime()));
        statement.setString(5, data.status());
        statement.setString(6, data.deskripsi());
    }

    private int getIdPinjaman(Connection connection, String noPinjaman) throws SQLException {
        String sql = "SELECT id_pinjaman FROM pinjaman WHERE no_pinjaman = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, noPinjaman);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt("id_pinjaman");
                }
            }
        }
        throw new SQLException("Kode peminjaman tidak ditemukan.");
    }

    private AgunanRow mapRow(ResultSet result) throws SQLException {
        return new AgunanRow(
                result.getInt("id_agunan"),
                result.getString("no_pinjaman"),
                result.getString("no_anggota"),
                result.getString("nama"),
                result.getString("jenis_agunan"),
                result.getBigDecimal("nilai_taksir"),
                result.getDate("tanggal"),
                result.getString("status"),
                result.getString("deskripsi")
        );
    }

    public record AgunanRow(int idAgunan, String noPinjaman, String noAnggota,
                            String namaAnggota, String jenisAgunan, BigDecimal nilaiTaksir,
                            java.util.Date tanggal, String status, String deskripsi) {
    }

    public record AgunanData(Integer idAgunan, String noPinjaman, String jenisAgunan,
                             BigDecimal nilaiTaksir, java.util.Date tanggal,
                             String status, String deskripsi) {
    }

    public record PinjamanPilihan(String noPinjaman, String noAnggota, String namaAnggota) {
        @Override
        public String toString() {
            return noPinjaman + " - " + noAnggota + " - " + namaAnggota;
        }
    }
}
