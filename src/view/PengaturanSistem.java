package view;

import dao.PengaturanDAO;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

public final class PengaturanSistem {
    private static final PengaturanDAO DAO = new PengaturanDAO();

    private PengaturanSistem() {
    }

    public static Data muat() {
        try {
            PengaturanDAO.Data data = DAO.getKonfigurasi();
            return new Data(data.mataUang(), data.persenBunga(), data.simpananWajib());
        } catch (SQLException | RuntimeException ex) {
            return Data.defaultData();
        }
    }

    public static void simpan(String mataUang, BigDecimal persenBunga, BigDecimal simpananWajib) throws SQLException {
        DAO.simpanKonfigurasi(mataUang, persenBunga, simpananWajib);
    }

    public static BigDecimal parseNominal(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }

        String cleaned = value
                .replace("Rp", "")
                .replace("rp", "")
                .replace("$", "")
                .replace("RM", "")
                .replace("rm", "")
                .replace(".", "")
                .replace(",", ".")
                .trim();

        try {
            return new BigDecimal(cleaned);
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    public static String formatUang(BigDecimal value) {
        return formatUang(value, muat().mataUang());
    }

    public static String formatUang(BigDecimal value, String mataUang) {
        BigDecimal angka = value == null ? BigDecimal.ZERO : value;
        NumberFormat format = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        format.setMaximumFractionDigits(0);

        String prefix = switch (mataUang == null ? "" : mataUang) {
            case "Dolar Amerika($)" -> "$";
            case "Ringgit(RM)" -> "RM";
            default -> "Rp";
        };

        return prefix + " " + format.format(angka);
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
