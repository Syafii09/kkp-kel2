package dao;

import koneksi.Koneksi;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LaporanDAO {

    public List<String> getJenisSimpanan() throws SQLException {
        String sql = "SELECT nama_jenis FROM jenis_simpanan ORDER BY id_jenis_simpanan ASC";
        List<String> data = new ArrayList<>();

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                data.add(result.getString("nama_jenis"));
            }
        }

        return data;
    }

    public TabelLaporan getLaporan(Filter filter) throws SQLException {
        QueryLaporan query = buatQueryLaporan(filter);

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(query.sql())) {
            for (int i = 0; i < query.params().size(); i++) {
                statement.setObject(i + 1, query.params().get(i));
            }

            try (ResultSet result = statement.executeQuery()) {
                return resultSetToTableData(result);
            }
        }
    }

    private QueryLaporan buatQueryLaporan(Filter filter) {
        return switch (filter.jenisLaporan()) {
            case "simpanan" -> querySimpanan(filter);
            case "pinjaman" -> queryPinjaman(filter);
            case "angsuran" -> queryAngsuran(filter);
            case "transaksi" -> queryTransaksi(filter);
            case "neraca" -> queryNeraca();
            case "shu" -> queryShu();
            default -> queryAnggota(filter);
        };
    }

    private QueryLaporan queryAnggota(Filter filter) {
        String orderBy = filter.urutNama() ? "a.nama ASC" : "a.tanggal_daftar ASC";
        String sql = """
                SELECT a.no_anggota AS 'No. Anggota', a.nik AS 'NIK', a.nama AS 'Nama',
                       a.no_hp AS 'No. HP', a.alamat AS 'Alamat',
                       a.tanggal_daftar AS 'Tanggal Daftar', a.status AS 'Status'
                FROM anggota a
                WHERE (? IS NULL OR a.tanggal_daftar >= ?)
                  AND (? IS NULL OR a.tanggal_daftar <= ?)
                ORDER BY %s
                """.formatted(orderBy);

        return new QueryLaporan(sql, tanggalAwalAkhirParams(filter));
    }

    private QueryLaporan querySimpanan(Filter filter) {
        String orderBy = filter.urutNama() ? "a.nama ASC" : "s.tanggal ASC";
        String sql = """
                SELECT s.tanggal AS 'Tanggal', a.no_anggota AS 'No. Anggota',
                       a.nama AS 'Nama', js.nama_jenis AS 'Jenis Simpanan',
                       s.nominal AS 'Nominal', s.saldo AS 'Saldo', s.keterangan AS 'Keterangan'
                FROM simpanan s
                JOIN anggota a ON a.id_anggota = s.id_anggota
                JOIN jenis_simpanan js ON js.id_jenis_simpanan = s.id_jenis_simpanan
                WHERE (? IS NULL OR s.tanggal >= ?)
                  AND (? IS NULL OR s.tanggal <= ?)
                  AND (? = '-- semua --' OR js.nama_jenis = ?)
                ORDER BY %s
                """.formatted(orderBy);

        List<Object> params = tanggalAwalAkhirParams(filter);
        params.add(filter.jenisSimpanan());
        params.add(filter.jenisSimpanan());
        return new QueryLaporan(sql, params);
    }

    private QueryLaporan queryPinjaman(Filter filter) {
        String orderBy = filter.urutNama() ? "a.nama ASC" : "p.tanggal_pinjaman ASC";
        String sql = """
                SELECT p.no_pinjaman AS 'No. Pinjaman', p.tanggal_pinjaman AS 'Tanggal',
                       a.no_anggota AS 'No. Anggota', a.nama AS 'Nama',
                       p.jumlah_pinjaman AS 'Jumlah', p.tenor_bulan AS 'Tenor',
                       p.angsuran_per_bulan AS 'Angsuran/Bulan', p.status AS 'Status'
                FROM pinjaman p
                JOIN anggota a ON a.id_anggota = p.id_anggota
                WHERE (? IS NULL OR p.tanggal_pinjaman >= ?)
                  AND (? IS NULL OR p.tanggal_pinjaman <= ?)
                ORDER BY %s
                """.formatted(orderBy);

        return new QueryLaporan(sql, tanggalAwalAkhirParams(filter));
    }

    private QueryLaporan queryAngsuran(Filter filter) {
        String orderBy = filter.urutNama() ? "agt.nama ASC" : "ang.tanggal_bayar ASC";
        String sql = """
                SELECT p.no_pinjaman AS 'No. Pinjaman', agt.no_anggota AS 'No. Anggota',
                       agt.nama AS 'Nama', ang.angsuran_ke AS 'Angsuran Ke',
                       ang.tanggal_bayar AS 'Tanggal Bayar', ang.jumlah_bayar AS 'Jumlah',
                       ang.status AS 'Status'
                FROM angsuran ang
                JOIN pinjaman p ON p.id_pinjaman = ang.id_pinjaman
                JOIN anggota agt ON agt.id_anggota = p.id_anggota
                WHERE (? IS NULL OR ang.tanggal_bayar >= ?)
                  AND (? IS NULL OR ang.tanggal_bayar <= ?)
                ORDER BY %s
                """.formatted(orderBy);

        return new QueryLaporan(sql, tanggalAwalAkhirParams(filter));
    }

    private QueryLaporan queryTransaksi(Filter filter) {
        String sql = """
                SELECT t.tanggal AS 'Tanggal', t.jenis_transaksi AS 'Jenis',
                       a.no_anggota AS 'No. Anggota', a.nama AS 'Nama',
                       t.debet AS 'Debet', t.kredit AS 'Kredit', t.keterangan AS 'Keterangan'
                FROM transaksi t
                LEFT JOIN anggota a ON a.id_anggota = t.id_anggota
                WHERE (? IS NULL OR DATE(t.tanggal) >= ?)
                  AND (? IS NULL OR DATE(t.tanggal) <= ?)
                ORDER BY t.tanggal ASC
                """;

        return new QueryLaporan(sql, tanggalAwalAkhirParams(filter));
    }

    private QueryLaporan queryNeraca() {
        String sql = """
                SELECT 'Total Simpanan' AS 'Komponen', COALESCE(SUM(nominal), 0) AS 'Nominal' FROM simpanan
                UNION ALL
                SELECT 'Total Pinjaman Aktif', COALESCE(SUM(jumlah_pinjaman), 0) FROM pinjaman WHERE status = 'Aktif'
                UNION ALL
                SELECT 'Kas Masuk', COALESCE(SUM(debet), 0) FROM transaksi
                UNION ALL
                SELECT 'Kas Keluar', COALESCE(SUM(kredit), 0) FROM transaksi
                """;

        return new QueryLaporan(sql, new ArrayList<>());
    }

    private QueryLaporan queryShu() {
        String sql = """
                SELECT 'Pendapatan Angsuran' AS 'Komponen', COALESCE(SUM(debet), 0) AS 'Nominal'
                FROM transaksi WHERE jenis_transaksi = 'Angsuran'
                UNION ALL
                SELECT 'Dana Pinjaman Keluar', COALESCE(SUM(kredit), 0)
                FROM transaksi WHERE jenis_transaksi = 'Pinjaman'
                UNION ALL
                SELECT 'Estimasi SHU', COALESCE(SUM(debet), 0) - COALESCE(SUM(kredit), 0)
                FROM transaksi
                """;

        return new QueryLaporan(sql, new ArrayList<>());
    }

    private List<Object> tanggalAwalAkhirParams(Filter filter) {
        List<Object> params = new ArrayList<>();
        params.add(filter.tanggalAwal());
        params.add(filter.tanggalAwal());
        params.add(filter.tanggalAkhir());
        params.add(filter.tanggalAkhir());
        return params;
    }

    private TabelLaporan resultSetToTableData(ResultSet result) throws SQLException {
        ResultSetMetaData metaData = result.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<String> columns = new ArrayList<>();
        List<List<Object>> rows = new ArrayList<>();

        for (int column = 1; column <= columnCount; column++) {
            columns.add(metaData.getColumnLabel(column));
        }

        while (result.next()) {
            List<Object> row = new ArrayList<>();
            for (int column = 1; column <= columnCount; column++) {
                Object value = result.getObject(column);
                row.add(value instanceof BigDecimal ? value : value);
            }
            rows.add(row);
        }

        return new TabelLaporan(columns, rows);
    }

    private record QueryLaporan(String sql, List<Object> params) {
    }

    public record Filter(
            String jenisLaporan,
            java.sql.Date tanggalAwal,
            java.sql.Date tanggalAkhir,
            String jenisSimpanan,
            boolean urutNama
    ) {
    }

    public record TabelLaporan(List<String> columns, List<List<Object>> rows) {
    }
}
