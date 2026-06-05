package utils;

import java.util.prefs.Preferences;

public final class SesiLogin {
    private static final Preferences PREFS = Preferences.userNodeForPackage(SesiLogin.class);
    private static final String PREF_AKTIF = "sesi_login_aktif";
    private static final String PREF_ID_USER = "id_user";
    private static final String PREF_ID_ANGGOTA = "id_anggota";
    private static final String PREF_NAMA_USER = "nama_user";
    private static final String PREF_GROUP_USER = "group_user";

    private static Integer idUser;
    private static Integer idAnggota;
    private static String namaUser = "User";
    private static String groupUser = "-";

    private SesiLogin() {
    }

    public static void masuk(Integer idUserLogin, String nama, String group) {
        masuk(idUserLogin, null, nama, group);
    }

    public static void masuk(Integer idUserLogin, Integer idAnggotaLogin, String nama, String group) {
        idUser = idUserLogin;
        idAnggota = idAnggotaLogin;
        namaUser = nama == null || nama.isBlank() ? "User" : nama;
        groupUser = group == null || group.isBlank() ? "-" : group;
    }

    public static void keluar() {
        idUser = null;
        idAnggota = null;
        namaUser = "User";
        groupUser = "-";
    }

    public static void simpanInfoLoginSaatIni(Integer idAnggotaLogin) {
        if (idUser == null) {
            return;
        }

        Integer anggotaUntukDisimpan = idAnggotaLogin != null ? idAnggotaLogin : idAnggota;
        PREFS.putBoolean(PREF_AKTIF, true);
        PREFS.putInt(PREF_ID_USER, idUser);
        PREFS.putInt(PREF_ID_ANGGOTA, anggotaUntukDisimpan == null ? -1 : anggotaUntukDisimpan);
        PREFS.put(PREF_NAMA_USER, namaUser);
        PREFS.put(PREF_GROUP_USER, groupUser);
    }

    public static void hapusInfoLoginTersimpan() {
        PREFS.putBoolean(PREF_AKTIF, false);
        PREFS.remove(PREF_ID_USER);
        PREFS.remove(PREF_ID_ANGGOTA);
        PREFS.remove(PREF_NAMA_USER);
        PREFS.remove(PREF_GROUP_USER);
    }

    public static SesiTersimpan getInfoLoginTersimpan() {
        if (!PREFS.getBoolean(PREF_AKTIF, false)) {
            return null;
        }

        int idUserTersimpan = PREFS.getInt(PREF_ID_USER, -1);
        if (idUserTersimpan <= 0) {
            return null;
        }

        int idAnggotaTersimpan = PREFS.getInt(PREF_ID_ANGGOTA, -1);
        String nama = PREFS.get(PREF_NAMA_USER, "User");
        String group = PREFS.get(PREF_GROUP_USER, "-");
        return new SesiTersimpan(
                idUserTersimpan,
                idAnggotaTersimpan <= 0 ? null : idAnggotaTersimpan,
                nama,
                group
        );
    }

    public static Integer getIdUser() {
        return idUser;
    }

    public static Integer getIdAnggota() {
        return idAnggota;
    }

    public static String getNamaUser() {
        return namaUser;
    }

    public static String getGroupUser() {
        return groupUser;
    }

    public record SesiTersimpan(Integer idUser, Integer idAnggota, String nama, String group) {
    }
}
