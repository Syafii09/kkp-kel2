package dao;

import koneksi.Koneksi;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransaksiDAO {

    public HasilTransaksi search(String jenis, java.sql.Date tanggal, String search) throws SQLException {
        String sql = """
                SELECT t.tanggal, t.jenis_transaksi, a.no_anggota, a.nama,
                       t.debet, t.kredit, t.keterangan
                FROM transaksi t
                LEFT JOIN anggota a ON a.id_anggota = t.id_anggota
                WHERE (? = '- Semua -' OR t.jenis_transaksi = ?)
                  AND (? IS NULL OR DATE(t.tanggal) = ?)
                  AND (
                    ? = ''
                    OR t.jenis_transaksi LIKE ?
                    OR COALESCE(a.no_anggota, '') LIKE ?
                    OR COALESCE(a.nama, '') LIKE ?
                    OR COALESCE(t.keterangan, '') LIKE ?
                    OR CAST(t.tanggal AS CHAR) LIKE ?
                  )
                ORDER BY t.id_transaksi ASC
                """;

        List<TransaksiRow> rows = new ArrayList<>();
        BigDecimal totalDebet = BigDecimal.ZERO;
        BigDecimal totalKredit = BigDecimal.ZERO;
        String keyword = search == null ? "" : search.trim();
        String likeSearch = "%" + keyword + "%";

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, jenis);
            statement.setString(2, jenis);
            statement.setDate(3, tanggal);
            statement.setDate(4, tanggal);
            statement.setString(5, keyword);
            statement.setString(6, likeSearch);
            statement.setString(7, likeSearch);
            statement.setString(8, likeSearch);
            statement.setString(9, likeSearch);
            statement.setString(10, likeSearch);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    BigDecimal debet = nolJikaNull(result.getBigDecimal("debet"));
                    BigDecimal kredit = nolJikaNull(result.getBigDecimal("kredit"));
                    totalDebet = totalDebet.add(debet);
                    totalKredit = totalKredit.add(kredit);

                    rows.add(new TransaksiRow(
                            result.getTimestamp("tanggal"),
                            result.getString("jenis_transaksi"),
                            result.getString("no_anggota"),
                            result.getString("nama"),
                            debet,
                            kredit,
                            result.getString("keterangan")
                    ));
                }
            }
        }

        return new HasilTransaksi(rows, totalDebet, totalKredit);
    }

    private BigDecimal nolJikaNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public record HasilTransaksi(List<TransaksiRow> rows, BigDecimal totalDebet, BigDecimal totalKredit) {
    }

    public record TransaksiRow(
            java.sql.Timestamp tanggal,
            String jenisTransaksi,
            String noAnggota,
            String nama,
            BigDecimal debet,
            BigDecimal kredit,
            String keterangan
    ) {
    }
}
