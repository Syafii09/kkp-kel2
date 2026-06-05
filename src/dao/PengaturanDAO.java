package dao;

import koneksi.Koneksi;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PengaturanDAO {

    public Data getKonfigurasi() throws SQLException {
        pastikanKolomSimpananWajib();
        String sql = """
                SELECT mata_uang, persen_bunga, simpanan_wajib
                FROM pengaturan_koperasi
                ORDER BY id_pengaturan
                LIMIT 1
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            if (result.next()) {
                return new Data(
                        result.getString("mata_uang"),
                        result.getBigDecimal("persen_bunga"),
                        result.getBigDecimal("simpanan_wajib")
                );
            }
            return Data.defaultData();
        }
    }

    public void simpanKonfigurasi(String mataUang, BigDecimal persenBunga, BigDecimal simpananWajib) throws SQLException {
        pastikanKolomSimpananWajib();
        String sql = """
                UPDATE pengaturan_koperasi
                SET mata_uang = ?, persen_bunga = ?, simpanan_wajib = ?
                WHERE id_pengaturan = (
                    SELECT id_pengaturan FROM (
                        SELECT id_pengaturan FROM pengaturan_koperasi ORDER BY id_pengaturan LIMIT 1
                    ) AS target
                )
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, mataUang == null || mataUang.isBlank() ? "Rupiah(Rp)" : mataUang);
            statement.setBigDecimal(2, persenBunga == null ? BigDecimal.ZERO : persenBunga);
            statement.setBigDecimal(3, simpananWajib == null ? BigDecimal.ZERO : simpananWajib);

            if (statement.executeUpdate() == 0) {
                insertDefault(connection, mataUang, persenBunga, simpananWajib);
            }
        }
    }

    private void insertDefault(Connection connection, String mataUang,
            BigDecimal persenBunga, BigDecimal simpananWajib) throws SQLException {
        String sql = """
                INSERT INTO pengaturan_koperasi (
                    nama_koperasi, mata_uang, persen_bunga, simpanan_wajib, tahun_buku
                ) VALUES ('Koperasi Raya Abadi Saudara', ?, ?, ?, YEAR(CURDATE()))
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, mataUang == null || mataUang.isBlank() ? "Rupiah(Rp)" : mataUang);
            statement.setBigDecimal(2, persenBunga == null ? BigDecimal.ZERO : persenBunga);
            statement.setBigDecimal(3, simpananWajib == null ? BigDecimal.ZERO : simpananWajib);
            statement.executeUpdate();
        }
    }

    private void pastikanKolomSimpananWajib() throws SQLException {
        String cekSql = """
                SELECT COUNT(*) AS total
                FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = 'pengaturan_koperasi'
                  AND COLUMN_NAME = 'simpanan_wajib'
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement cek = connection.prepareStatement(cekSql);
             ResultSet result = cek.executeQuery()) {
            if (result.next() && result.getInt("total") > 0) {
                return;
            }

            try (PreparedStatement alter = connection.prepareStatement("""
                    ALTER TABLE pengaturan_koperasi
                    ADD COLUMN simpanan_wajib decimal(15,2) NOT NULL DEFAULT 0.00 AFTER persen_bunga
                    """)) {
                alter.executeUpdate();
            }
        }
    }

    public record Data(String mataUang, BigDecimal persenBunga, BigDecimal simpananWajib) {
        public Data {
            mataUang = mataUang == null || mataUang.isBlank() ? "Rupiah(Rp)" : mataUang;
            persenBunga = persenBunga == null ? BigDecimal.ZERO : persenBunga;
            simpananWajib = simpananWajib == null ? BigDecimal.ZERO : simpananWajib;
        }

        public static Data defaultData() {
            return new Data("Rupiah(Rp)", BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
}
