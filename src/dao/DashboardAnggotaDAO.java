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

public class DashboardAnggotaDAO {
    private final DatabaseMigrationDAO migrationDAO = new DatabaseMigrationDAO();

    public ProfilAnggota getProfil(int idAnggota) throws SQLException {
        String sql = """
                SELECT no_anggota, nik, nama, tempat_lahir, tanggal_lahir, jenis_kelamin,
                       alamat, no_hp, divisi
                FROM anggota
                WHERE id_anggota = ?
                LIMIT 1
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idAnggota);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new ProfilAnggota(
                            result.getString("no_anggota"),
                            result.getString("nik"),
                            result.getString("nama"),
                            result.getString("tempat_lahir"),
                            result.getDate("tanggal_lahir"),
                            result.getString("jenis_kelamin"),
                            result.getString("alamat"),
                            result.getString("no_hp"),
                            result.getString("divisi")
                    );
                }
            }
        }
        return null;
    }

    public RingkasanAnggota getRingkasan(int idAnggota) throws SQLException {
        try (Connection connection = Koneksi.getConnection()) {
            return new RingkasanAnggota(
                    queryBigDecimal(connection, "SELECT COALESCE(SUM(nominal), 0) FROM simpanan WHERE id_anggota = ?", idAnggota),
                    queryBigDecimal(connection, "SELECT COALESCE(SUM(jumlah_pinjaman), 0) FROM pinjaman WHERE id_anggota = ? AND status = 'Aktif'", idAnggota),
                    queryBigDecimal(connection, "SELECT COALESCE(SUM(jumlah_pinjaman), 0) FROM pinjaman WHERE id_anggota = ?", idAnggota),
                    queryProgressAngsuran(connection, idAnggota)
            );
        }
    }

    public List<RiwayatSimpanan> getRiwayatSimpanan(int idAnggota) throws SQLException {
        String sql = """
                SELECT js.nama_jenis, s.nominal, s.tanggal, COALESCE(u.nama, '') AS petugas
                FROM simpanan s
                JOIN jenis_simpanan js ON js.id_jenis_simpanan = s.id_jenis_simpanan
                LEFT JOIN transaksi t ON t.referensi_tabel = 'simpanan'
                    AND t.referensi_id = s.id_simpanan
                    AND t.jenis_transaksi = 'Simpanan'
                LEFT JOIN users u ON u.id_user = t.id_user
                WHERE s.id_anggota = ?
                ORDER BY s.tanggal DESC, s.id_simpanan DESC
                LIMIT 20
                """;

        List<RiwayatSimpanan> data = new ArrayList<>();
        try (Connection connection = Koneksi.getConnection()) {
            migrationDAO.pastikanKolomPetugasTransaksi(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, idAnggota);
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        data.add(new RiwayatSimpanan(
                                result.getString("nama_jenis"),
                                result.getBigDecimal("nominal"),
                                result.getDate("tanggal"),
                                result.getString("petugas")
                        ));
                    }
                }
            }
        }
        return data;
    }

    public List<RiwayatPinjaman> getRiwayatPinjaman(int idAnggota) throws SQLException {
        String sql = """
                SELECT p.jumlah_pinjaman, p.bunga_persen, p.tenor_bulan, p.tujuan,
                       p.tanggal_pinjaman, p.status, COALESCE(u.nama, '') AS petugas
                FROM pinjaman p
                LEFT JOIN transaksi t ON t.referensi_tabel = 'pinjaman'
                    AND t.referensi_id = p.id_pinjaman
                    AND t.jenis_transaksi = 'Pinjaman'
                LEFT JOIN users u ON u.id_user = t.id_user
                WHERE p.id_anggota = ?
                ORDER BY p.tanggal_pinjaman DESC, p.id_pinjaman DESC
                LIMIT 20
                """;

        List<RiwayatPinjaman> data = new ArrayList<>();
        try (Connection connection = Koneksi.getConnection()) {
            migrationDAO.pastikanKolomPetugasTransaksi(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, idAnggota);
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        data.add(new RiwayatPinjaman(
                                result.getBigDecimal("jumlah_pinjaman"),
                                result.getBigDecimal("bunga_persen"),
                                result.getInt("tenor_bulan"),
                                result.getString("tujuan"),
                                result.getDate("tanggal_pinjaman"),
                                result.getString("status"),
                                result.getString("petugas")
                        ));
                    }
                }
            }
        }
        return data;
    }

    public List<RiwayatAngsuran> getRiwayatAngsuran(int idAnggota) throws SQLException {
        String sql = """
                SELECT a.jumlah_bayar, a.angsuran_ke, p.tenor_bulan, a.tanggal_bayar,
                       COALESCE(u.nama, '') AS petugas
                FROM angsuran a
                JOIN pinjaman p ON p.id_pinjaman = a.id_pinjaman
                LEFT JOIN transaksi t ON t.referensi_tabel = 'angsuran'
                    AND t.referensi_id = a.id_angsuran
                    AND t.jenis_transaksi = 'Angsuran'
                LEFT JOIN users u ON u.id_user = t.id_user
                WHERE p.id_anggota = ?
                ORDER BY a.tanggal_bayar DESC, a.id_angsuran DESC
                LIMIT 20
                """;

        List<RiwayatAngsuran> data = new ArrayList<>();
        try (Connection connection = Koneksi.getConnection()) {
            migrationDAO.pastikanKolomPetugasTransaksi(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, idAnggota);
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        data.add(new RiwayatAngsuran(
                                result.getBigDecimal("jumlah_bayar"),
                                result.getInt("angsuran_ke"),
                                result.getInt("tenor_bulan"),
                                result.getDate("tanggal_bayar"),
                                result.getString("petugas")
                        ));
                    }
                }
            }
        }
        return data;
    }

    private BigDecimal queryBigDecimal(Connection connection, String sql, int idAnggota) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idAnggota);
            try (ResultSet result = statement.executeQuery()) {
                BigDecimal value = result.next() ? result.getBigDecimal(1) : BigDecimal.ZERO;
                return value == null ? BigDecimal.ZERO : value;
            }
        }
    }

    private String queryProgressAngsuran(Connection connection, int idAnggota) throws SQLException {
        String sql = """
                SELECT p.tenor_bulan, COALESCE(MAX(a.angsuran_ke), 0) AS angsuran_ke
                FROM pinjaman p
                LEFT JOIN angsuran a ON a.id_pinjaman = p.id_pinjaman
                WHERE p.id_anggota = ? AND p.status = 'Aktif'
                GROUP BY p.id_pinjaman, p.tenor_bulan
                ORDER BY p.id_pinjaman DESC
                LIMIT 1
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idAnggota);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt("angsuran_ke") + "/" + result.getInt("tenor_bulan") + " bulan";
                }
            }
        }
        return "0/0 bulan";
    }

    public record ProfilAnggota(
            String noAnggota,
            String nik,
            String nama,
            String tempatLahir,
            Date tanggalLahir,
            String jenisKelamin,
            String alamat,
            String noHp,
            String divisi
    ) {
    }

    public record RingkasanAnggota(BigDecimal totalSimpanan, BigDecimal pinjamanAktif, BigDecimal totalPinjaman, String progressAngsuran) {
    }

    public record RiwayatSimpanan(String namaJenis, BigDecimal nominal, Date tanggal, String petugas) {
    }

    public record RiwayatPinjaman(BigDecimal jumlahPinjaman, BigDecimal bungaPersen, int tenorBulan, String tujuan, Date tanggalPinjaman, String status, String petugas) {
    }

    public record RiwayatAngsuran(BigDecimal jumlahBayar, int angsuranKe, int tenorBulan, Date tanggalBayar, String petugas) {
    }
}
