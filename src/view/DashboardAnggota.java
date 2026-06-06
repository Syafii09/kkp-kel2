/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import dao.AnggotaDAO;
import dao.DashboardAnggotaDAO;
import java.awt.Image;
import java.awt.Window;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.DefaultListModel;
import javax.swing.Box;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import utils.SesiLogin;

/**
 *
 * @author sabil
 */
public class DashboardAnggota extends javax.swing.JFrame {
    private final DashboardAnggotaDAO dashboardAnggotaDAO = new DashboardAnggotaDAO();
    private final AnggotaDAO anggotaDAO = new AnggotaDAO();
    private final Integer idAnggota;
    private final String namaUserLogin;
    private final Locale indonesia = new Locale("id", "ID");

    /**
     * Creates new form Dashboard_Anggota
     */
    public DashboardAnggota() {
        this(null, "Anggota");
    }

    public DashboardAnggota(Integer idAnggota, String namaUserLogin) {
        this.idAnggota = idAnggota;
        this.namaUserLogin = namaUserLogin == null || namaUserLogin.isBlank() ? "Anggota" : namaUserLogin;
        initComponents();
        getRootPane().putClientProperty("JRootPane.menuBarEmbedded", Boolean.FALSE);
        setupWindowClose();
        setupFooterLinks();
        setLocationRelativeTo(null);
        setupMenuAnggota();
        loadDashboardAnggota();
    }

    private void setupWindowClose() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                keluarAplikasiDenganPilihanSimpanLogin();
            }
        });
    }

    private void setupFooterLinks() {
        LPrivacypolicy12.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        LPrivacypolicy12.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tampilkanPrivacyPolicy();
            }
        });

        LTos12.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        LTos12.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tampilkanTermsOfService();
            }
        });
    }

    private void setupMenuAnggota() {
        jMenuBar.removeAll();
        jmenu.setText("Menu");
        jmenu.removeAll();
        JMenuItem exitItem = new JMenuItem("Exit / Keluar");
        exitItem.addActionListener(e -> keluarAplikasiDenganPilihanSimpanLogin());
        jmenu.add(exitItem);

        jmview.setText("View");
        jmview.removeAll();
        JMenuItem lightItem = new JMenuItem("Light");
        JMenuItem darkItem = new JMenuItem("Dark");
        lightItem.addActionListener(e -> gantiTema(false));
        darkItem.addActionListener(e -> gantiTema(true));
        jmview.add(lightItem);
        jmview.add(darkItem);

        jmhelp.setText("Help");
        jmhelp.removeAll();
        JMenuItem faqItem = new JMenuItem("FAQ");
        JMenuItem tentangItem = new JMenuItem("Tentang Aplikasi");
        JMenuItem aboutItem = new JMenuItem("About/Tentang");
        faqItem.addActionListener(e -> tampilkanFaq());
        tentangItem.addActionListener(e -> tampilkanTentangAplikasi());
        aboutItem.addActionListener(e -> tampilkanAboutTentang());
        jmhelp.add(faqItem);
        jmhelp.add(tentangItem);
        jmhelp.add(aboutItem);

        jmusermenu.setText(namaUserLogin);
        jmusermenu.setIcon(iconUserMenu());
        jmusermenu.removeAll();
        JMenuItem lengkapiDataItem = new JMenuItem(teksMenuDataDiri());
        JMenuItem profileItem = new JMenuItem("Profile");
        JMenuItem logoutItem = new JMenuItem("Logout / Keluar");
        lengkapiDataItem.addActionListener(e -> bukaFormLengkapiDataDiri());
        profileItem.addActionListener(e -> tampilkanProfile());
        logoutItem.addActionListener(e -> logout());
        jmusermenu.add(lengkapiDataItem);
        jmusermenu.addSeparator();
        jmusermenu.add(profileItem);
        jmusermenu.addSeparator();
        jmusermenu.add(logoutItem);

        jMenuBar.add(jmenu);
        jMenuBar.add(jmview);
        jMenuBar.add(jmhelp);
        jMenuBar.add(Box.createHorizontalGlue());
        jMenuBar.add(jmusermenu);
        setJMenuBar(jMenuBar);
        jMenuBar.revalidate();
        jMenuBar.repaint();
    }

    private String teksMenuDataDiri() {
        return dataDiriSudahLengkap() ? "Edit Data Diri" : "Lengkapi Data Diri";
    }

    private boolean dataDiriSudahLengkap() {
        if (idAnggota == null) {
            return false;
        }

        try {
            AnggotaDAO.AnggotaData data = anggotaDAO.getById(idAnggota);
            return data != null
                    && !kosong(data.nama())
                    && !kosong(data.nik())
                    && !kosong(data.tempatLahir())
                    && data.tanggalLahir() != null
                    && !kosong(data.jenisKelamin())
                    && !kosong(data.alamat())
                    && !kosong(data.noHp())
                    && !kosong(data.divisi());
        } catch (SQLException | RuntimeException ex) {
            return false;
        }
    }

    private boolean kosong(String value) {
        return value == null || value.isBlank();
    }

    private javax.swing.Icon iconUserMenu() {
        java.net.URL iconUrl = getClass().getResource("/resources/icons/user.png");
        if (iconUrl == null) {
            return null;
        }

        Image image = new javax.swing.ImageIcon(iconUrl)
                .getImage()
                .getScaledInstance(15, 15, Image.SCALE_SMOOTH);
        return new javax.swing.ImageIcon(image);
    }

    private void gantiTema(boolean dark) {
        try {
            UIManager.setLookAndFeel(dark ? new FlatMacDarkLaf() : new FlatMacLightLaf());
            for (Window window : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
                window.invalidate();
                window.validate();
                window.repaint();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal mengganti tema.\n" + ex.getMessage(),
                    "Tema",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void tampilkanFaq() {
        JOptionPane.showMessageDialog(
                this,
                "<html><body>"
                + "<div>"
                + "<div style='font-size:20px;font-weight:bold;padding-top:-10px'>FAQ</div>"
                + "<b>1.</b> Dashboard anggota menampilkan profil, simpanan, pinjaman, dan angsuran milik akun yang login.<br><br>"
                + "<b>2.</b> Jika data belum tampil, pastikan akun sudah terhubung dengan No Anggota.<br><br>"
                + "<b>3.</b> Riwayat menampilkan transaksi terbaru beserta petugas yang memproses."
                + "</div></body></html>",
                "FAQ",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void tampilkanTentangAplikasi() {
        JOptionPane.showMessageDialog(
                this,
                "<html><body>"
                + "<div style='text-align:center'>"
                + "<h2>Koperasi Raya Abadi Saudara</h2>"
                + "<br>"
                + "<div>Aplikasi desktop untuk membantu pengelolaan data koperasi,</div>"
                + "<div>mulai dari anggota, simpanan, pinjaman, angsuran, transaksi, dashboard, dan laporan.</div>"
                + "</div></body></html>",
                "Tentang Aplikasi",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private void tampilkanAboutTentang() {
        JOptionPane.showMessageDialog(
                this,
                "<html><body>"
                + "<div style='text-align:center'>"
                + "<h2 style='margin:0'>Koperasi Raya Abadi Saudara</h2>"
                + "<div>Version 1.0</div>"
                + "<br>"
                + "<h3 style='margin:0'>Developed by</h3>"
                + "<div>Rangga Danuarta | Syafii Muhammad Arif | Ferry Kadafi</div>"
                + "<div>Sabilarrusdi | Beni Akbar Suparman | Muhammad Alfaridzi | Julfi Alfiansyah</div>"
                + "<br>"
                + "<h3 style='margin:0'>Teknologi Utama</h3>"
                + "<div>Netbeans | Java | Html | Mysql</div>"
                + "<br>"
                + "<h3 style='margin:0'>Universitas Indraprasta PGRI</h3>"
                + "<h3 style='margin:0'>2026</h3>"
                + "</div></body></html>",
                "About",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private void tampilkanPrivacyPolicy() {
        JOptionPane.showMessageDialog(
                this,
                "<html><body style='width:420px'>"
                + "<h2 style='margin:0;text-align:center'>Privacy Policy</h2>"
                + "<h3 style='margin:6px 0 12px;text-align:center'>Koperasi Raya Abadi Saudara</h3>"
                + "<p>Koperasi Raya Abadi Saudara is committed to protecting the privacy and confidentiality "
                + "of its members and application users. This Privacy Policy explains how personal and financial "
                + "data is collected, used, stored, and protected within the cooperative management system.</p>"
                + "<h4 style='margin-bottom:4px'>1. Information We Collect</h4>"
                + "<p>The application may collect member identity data, account information, and cooperative "
                + "transaction data, including savings, loans, installments, and financial records.</p>"
                + "<h4 style='margin-bottom:4px'>2. Use of Information</h4>"
                + "<p>Collected data is used to manage membership, record cooperative transactions, generate "
                + "reports, manage user access, and support internal cooperative operations.</p>"
                + "<h4 style='margin-bottom:4px'>3. Data Access</h4>"
                + "<p>Access to data is limited based on user roles and permissions. Users may only access "
                + "information according to their authorized responsibilities.</p>"
                + "<h4 style='margin-bottom:4px'>4. Data Storage and Security</h4>"
                + "<p>Data is stored in the application database. The system applies role-based access control, "
                + "and users are responsible for maintaining the confidentiality of their login credentials.</p>"
                + "<h4 style='margin-bottom:4px'>5. Data Disclosure</h4>"
                + "<p>Personal and financial data will not be shared with external parties unless required by law, "
                + "official cooperative needs, or with the consent of the related member.</p>"
                + "<h4 style='margin-bottom:4px'>6. Policy Updates</h4>"
                + "<p>This Privacy Policy may be updated to reflect system improvements, operational needs, "
                + "or regulatory requirements.</p>"
                + "</body></html>",
                "Privacy Policy",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private void tampilkanTermsOfService() {
        JOptionPane.showMessageDialog(
                this,
                "<html><body style='width:420px'>"
                + "<h2 style='margin:0;text-align:center'>Terms of Service</h2>"
                + "<h3 style='margin:6px 0 12px;text-align:center'>Koperasi Raya Abadi Saudara</h3>"
                + "<p>These Terms of Service define the rules and responsibilities for using the cooperative "
                + "management application of Koperasi Raya Abadi Saudara.</p>"
                + "<h4 style='margin-bottom:4px'>1. Authorized Use</h4>"
                + "<p>The application may only be used by authorized users according to their assigned roles "
                + "and responsibilities within the cooperative.</p>"
                + "<h4 style='margin-bottom:4px'>2. Account Responsibility</h4>"
                + "<p>Users are responsible for maintaining the confidentiality of their login credentials and "
                + "for all activities performed through their account.</p>"
                + "<h4 style='margin-bottom:4px'>3. Data Accuracy</h4>"
                + "<p>Users must ensure that all data entered into the system is accurate, valid, and related "
                + "to official cooperative activities.</p>"
                + "<h4 style='margin-bottom:4px'>4. Restricted Actions</h4>"
                + "<p>Users must not misuse the system, access unauthorized data, modify records without proper "
                + "permission, or perform actions that may disrupt application operations.</p>"
                + "<h4 style='margin-bottom:4px'>5. System Changes</h4>"
                + "<p>The cooperative may update application features, access rules, and operational procedures "
                + "as needed to support system improvements.</p>"
                + "<h4 style='margin-bottom:4px'>6. Acceptance</h4>"
                + "<p>By using this application, users agree to comply with these Terms of Service and the "
                + "applicable cooperative policies.</p>"
                + "</body></html>",
                "Terms of Service",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private void tampilkanProfile() {
        JOptionPane.showMessageDialog(
                this,
                "Profile Anggota\n\n"
                + "Nama: " + namaUserLogin + "\n"
                + "No. Anggota: " + lblnoanggota.getText(),
                "Profile",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void bukaFormLengkapiDataDiri() {
        if (idAnggota == null) {
            JOptionPane.showMessageDialog(this, "Data anggota tidak ditemukan.", "Lengkapi Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        new FormLengkapiDataDiri(idAnggota, namaUserLogin, true).setVisible(true);
    }

    private void logout() {
        SesiLogin.hapusInfoLoginTersimpan();
        SesiLogin.keluar();
        new Login().setVisible(true);
        dispose();
    }

    private void keluarAplikasiDenganPilihanSimpanLogin() {
        Object[] pilihan = {"Simpan Info Login", "Tidak Simpan", "Batal"};
        int hasil = JOptionPane.showOptionDialog(
                this,
                "Simpan Info Login?",
                "Exit / Keluar",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                pilihan,
                pilihan[0]
        );

        if (hasil == JOptionPane.CLOSED_OPTION || hasil == 2) {
            return;
        }

        if (hasil == 0) {
            SesiLogin.simpanInfoLoginSaatIni(idAnggota);
        } else {
            SesiLogin.hapusInfoLoginTersimpan();
        }

        dispose();
        System.exit(0);
    }

    private void loadDashboardAnggota() {
        if (idAnggota == null) {
            kosongkanDashboard();
            return;
        }

        loadProfil();
        loadRingkasan();
        loadRiwayatSimpanan();
        loadRiwayatPinjaman();
        loadRiwayatAngsuran();
    }

    private void kosongkanDashboard() {
        lblnama.setText("-");
        lblNIK.setText("-");
        lblnohp.setText("-");
        lblalamat.setText("-");
        lbltgllahir.setText("-");
        lbldivisi.setText("-");
        lblnoanggota.setText("-");
        lbljeniskelamin.setText("-");
        jLabel2.setText(formatRupiah(BigDecimal.ZERO));
        lblpinjamanaktif.setText(formatRupiah(BigDecimal.ZERO));
        lbltotalpinjaman.setText(formatRupiah(BigDecimal.ZERO));
        jLabel6.setText("0/0 bulan");
        Lriwayatsimpanan.setModel(modelKosong());
        Lriwayatpinjaman.setModel(modelKosong());
        Lriwayatangsuran.setModel(modelKosong());
    }

    private DefaultListModel<String> modelKosong() {
        DefaultListModel<String> model = new DefaultListModel<>();
        model.addElement("<html><span style='color:#777'>Belum ada data.</span></html>");
        return model;
    }

    private void loadProfil() {
        try {
            DashboardAnggotaDAO.ProfilAnggota profil = dashboardAnggotaDAO.getProfil(idAnggota);
            if (profil != null) {
                lblnoanggota.setText(profil.noAnggota());
                lblnama.setText(profil.nama());
                lblNIK.setText(nilai(profil.nik()));
                lblnohp.setText(nilai(profil.noHp()));
                lblalamat.setText(nilai(profil.alamat()));
                lbltgllahir.setText(formatTempatTanggal(profil.tempatLahir(), profil.tanggalLahir()));
                lbldivisi.setText(nilai(profil.divisi()));
                lbljeniskelamin.setText(nilai(profil.jenisKelamin()));
            }
        } catch (SQLException | RuntimeException ex) {
            tampilkanError("Gagal memuat profil anggota.", ex);
        }
    }

    private void loadRingkasan() {
        try {
            DashboardAnggotaDAO.RingkasanAnggota ringkasan = dashboardAnggotaDAO.getRingkasan(idAnggota);
            jLabel2.setText(formatRupiah(ringkasan.totalSimpanan()));
            lblpinjamanaktif.setText(formatRupiah(ringkasan.pinjamanAktif()));
            lbltotalpinjaman.setText(formatRupiah(ringkasan.totalPinjaman()));
            jLabel6.setText(ringkasan.progressAngsuran());
        } catch (SQLException | RuntimeException ex) {
            tampilkanError("Gagal memuat ringkasan anggota.", ex);
        }
    }

    private void loadRiwayatSimpanan() {
        DefaultListModel<String> model = new DefaultListModel<>();
        try {
            for (DashboardAnggotaDAO.RiwayatSimpanan row : dashboardAnggotaDAO.getRiwayatSimpanan(idAnggota)) {
                model.addElement(itemHtml(
                        row.namaJenis(),
                        formatRupiah(row.nominal()),
                        "Diproses oleh " + namaPetugas(row.petugas()),
                        formatTanggal(row.tanggal())
                ));
            }
        } catch (SQLException | RuntimeException ex) {
            tampilkanError("Gagal memuat riwayat simpanan.", ex);
        }

        Lriwayatsimpanan.setModel(model.isEmpty() ? modelKosong() : model);
    }

    private void loadRiwayatPinjaman() {
        DefaultListModel<String> model = new DefaultListModel<>();
        try {
            for (DashboardAnggotaDAO.RiwayatPinjaman row : dashboardAnggotaDAO.getRiwayatPinjaman(idAnggota)) {
                    String title = "Pengajuan Pinjaman";
                    if ("Aktif".equalsIgnoreCase(row.status())) {
                        title = "Pinjaman Aktif";
                    } else if ("Lunas".equalsIgnoreCase(row.status())) {
                        title = "Pinjaman Lunas";
                    }

                    model.addElement(itemHtml(
                            title,
                            formatRupiah(row.jumlahPinjaman()),
                            "bunga " + formatPersen(row.bungaPersen()),
                            "Tenor " + row.tenorBulan() + " bulan",
                            nilai(row.tujuan()),
                            "Diproses oleh " + namaPetugas(row.petugas()),
                            formatTanggal(row.tanggalPinjaman())
                    ));
            }
        } catch (SQLException | RuntimeException ex) {
            tampilkanError("Gagal memuat riwayat pinjaman.", ex);
        }

        Lriwayatpinjaman.setModel(model.isEmpty() ? modelKosong() : model);
    }

    private void loadRiwayatAngsuran() {
        DefaultListModel<String> model = new DefaultListModel<>();
        try {
            for (DashboardAnggotaDAO.RiwayatAngsuran row : dashboardAnggotaDAO.getRiwayatAngsuran(idAnggota)) {
                model.addElement(itemHtml(
                        "Pembayaran Angsuran",
                        formatRupiah(row.jumlahBayar()),
                        row.angsuranKe() + "/" + row.tenorBulan() + " bulan",
                        "Diproses oleh " + namaPetugas(row.petugas()),
                        formatTanggal(row.tanggalBayar())
                ));
            }
        } catch (SQLException | RuntimeException ex) {
            tampilkanError("Gagal memuat riwayat angsuran.", ex);
        }

        Lriwayatangsuran.setModel(model.isEmpty() ? modelKosong() : model);
    }

    private String itemHtml(String judul, String... lines) {
        StringBuilder html = new StringBuilder("<html><div style='padding:6px 2px'>");
        html.append("&#9679; <b>").append(escapeHtml(judul)).append("</b>");
        for (String line : lines) {
            if (line != null && !line.isBlank() && !"-".equals(line)) {
                html.append("<br>&nbsp;&nbsp;&nbsp;").append(escapeHtml(line));
            }
        }
        html.append("</div></html>");
        return html.toString();
    }

    private String formatRupiah(BigDecimal value) {
        NumberFormat format = NumberFormat.getCurrencyInstance(indonesia);
        format.setMaximumFractionDigits(0);
        return format.format(value == null ? BigDecimal.ZERO : value);
    }

    private String formatPersen(BigDecimal value) {
        BigDecimal angka = value == null ? BigDecimal.ZERO : value.stripTrailingZeros();
        return angka.toPlainString() + "%";
    }

    private String formatTanggal(Date date) {
        if (date == null) {
            return "-";
        }
        return new SimpleDateFormat("dd MMMM yyyy", indonesia).format(date);
    }

    private String formatTempatTanggal(String tempat, Date tanggal) {
        String tempatLahir = nilai(tempat);
        String tanggalLahir = formatTanggal(tanggal);
        if ("-".equals(tempatLahir)) {
            return tanggalLahir;
        }
        if ("-".equals(tanggalLahir)) {
            return tempatLahir;
        }
        return tempatLahir + ", " + tanggalLahir;
    }

    private String nilai(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String namaPetugas(String value) {
        return value == null || value.isBlank() ? "Belum tercatat" : value;
    }

    private String escapeHtml(String value) {
        String text = value == null ? "" : value;
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    private void tampilkanError(String pesan, Exception ex) {
        JOptionPane.showMessageDialog(this, pesan + "\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        pnlsimpanan = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        pnltotalpinjaman = new javax.swing.JPanel();
        lbltotalpinjaman = new javax.swing.JLabel();
        pnlangsuran = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        pnlpinjamanaktif = new javax.swing.JPanel();
        lblpinjamanaktif = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Lriwayatsimpanan = new javax.swing.JList<>();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Lriwayatpinjaman = new javax.swing.JList<>();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Lriwayatangsuran = new javax.swing.JList<>();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblnama = new javax.swing.JLabel();
        lblNIK = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblnohp = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblalamat = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lbltgllahir = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lbldivisi = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        lblnoanggota = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lbljeniskelamin = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        Lcr12 = new javax.swing.JLabel();
        LPrivacypolicy12 = new javax.swing.JLabel();
        jSeparator14 = new javax.swing.JSeparator();
        LTos12 = new javax.swing.JLabel();
        jMenuBar = new javax.swing.JMenuBar();
        jmenu = new javax.swing.JMenu();
        jmview = new javax.swing.JMenu();
        jmhelp = new javax.swing.JMenu();
        jmusermenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlsimpanan.setBorder(javax.swing.BorderFactory.createTitledBorder("Simpanan"));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Rp. 2000,000");

        javax.swing.GroupLayout pnlsimpananLayout = new javax.swing.GroupLayout(pnlsimpanan);
        pnlsimpanan.setLayout(pnlsimpananLayout);
        pnlsimpananLayout.setHorizontalGroup(
            pnlsimpananLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlsimpananLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlsimpananLayout.setVerticalGroup(
            pnlsimpananLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
        );

        pnltotalpinjaman.setBorder(javax.swing.BorderFactory.createTitledBorder("Total Pinjaman"));

        lbltotalpinjaman.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbltotalpinjaman.setText("Rp. 2000,000");

        javax.swing.GroupLayout pnltotalpinjamanLayout = new javax.swing.GroupLayout(pnltotalpinjaman);
        pnltotalpinjaman.setLayout(pnltotalpinjamanLayout);
        pnltotalpinjamanLayout.setHorizontalGroup(
            pnltotalpinjamanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnltotalpinjamanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbltotalpinjaman, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnltotalpinjamanLayout.setVerticalGroup(
            pnltotalpinjamanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbltotalpinjaman, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
        );

        pnlangsuran.setBorder(javax.swing.BorderFactory.createTitledBorder("Angsuran"));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("1/12 bulan");

        javax.swing.GroupLayout pnlangsuranLayout = new javax.swing.GroupLayout(pnlangsuran);
        pnlangsuran.setLayout(pnlangsuranLayout);
        pnlangsuranLayout.setHorizontalGroup(
            pnlangsuranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlangsuranLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlangsuranLayout.setVerticalGroup(
            pnlangsuranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
        );

        pnlpinjamanaktif.setBorder(javax.swing.BorderFactory.createTitledBorder("Pinjaman Aktif"));

        lblpinjamanaktif.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblpinjamanaktif.setText("Rp. 2000,000");

        javax.swing.GroupLayout pnlpinjamanaktifLayout = new javax.swing.GroupLayout(pnlpinjamanaktif);
        pnlpinjamanaktif.setLayout(pnlpinjamanaktifLayout);
        pnlpinjamanaktifLayout.setHorizontalGroup(
            pnlpinjamanaktifLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlpinjamanaktifLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblpinjamanaktif, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlpinjamanaktifLayout.setVerticalGroup(
            pnlpinjamanaktifLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblpinjamanaktif, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlsimpanan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlangsuran, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(pnlpinjamanaktif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnltotalpinjaman, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlsimpanan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnltotalpinjaman, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlpinjamanaktif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlangsuran, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Total", jPanel1);

        Lriwayatsimpanan.setOpaque(false);
        jScrollPane1.setViewportView(Lriwayatsimpanan);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Riwayat Simpanan", jPanel2);

        Lriwayatpinjaman.setOpaque(false);
        jScrollPane2.setViewportView(Lriwayatpinjaman);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Riwayat Pinjaman", jPanel3);

        Lriwayatangsuran.setOpaque(false);
        jScrollPane3.setViewportView(Lriwayatangsuran);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Riwayat Angsuran", jPanel4);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Profile"));

        jLabel1.setText("Nama");

        lblnama.setText("Sabilarrusdi");

        lblNIK.setText("21319231231");

        jLabel4.setText("NIK");

        jLabel5.setText("No Hp");

        lblnohp.setText("085278218227");

        jLabel7.setText("Alamat");

        lblalamat.setText("Depok");

        jLabel9.setText("Tgl Lahir");

        lbltgllahir.setText("24 juni 2004");

        jLabel11.setText("Divisi");

        lbldivisi.setText("Marketing");

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "No Anggota", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        lblnoanggota.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblnoanggota.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblnoanggota.setText("A001");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblnoanggota, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblnoanggota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel12.setText("Jenis Kelamin");

        lbljeniskelamin.setText("Laki - Laki");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblalamat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblnohp, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbldivisi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblnama, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbltgllahir, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblNIK, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbljeniskelamin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(lblnama)
                            .addComponent(jLabel9)
                            .addComponent(lbltgllahir))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel12)
                                .addComponent(lbljeniskelamin))
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4)
                                .addComponent(lblNIK)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel11)
                                .addComponent(lbldivisi))
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(lblnohp)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(lblalamat))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel20.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), null));

        Lcr12.setForeground(java.awt.SystemColor.textInactiveText);
        Lcr12.setText("© 2026 Kelompok 2 Pemograman Visual. All Right Reserved");

        LPrivacypolicy12.setForeground(java.awt.SystemColor.textInactiveText);
        LPrivacypolicy12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LPrivacypolicy12.setText("Privacy Policy");

        jSeparator14.setOrientation(javax.swing.SwingConstants.VERTICAL);

        LTos12.setForeground(java.awt.SystemColor.textInactiveText);
        LTos12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LTos12.setText("Term Of Service");

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Lcr12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(LPrivacypolicy12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LTos12)
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator14, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Lcr12)
                        .addComponent(LTos12)
                        .addComponent(LPrivacypolicy12)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jMenuBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jmenu.setText("Menu");
        jMenuBar.add(jmenu);

        jmview.setText("View");
        jMenuBar.add(jmview);

        jmhelp.setText("Help");
        jMenuBar.add(jmhelp);

        jmusermenu.setText("UserMenu");
        jMenuBar.add(jmusermenu);

        setJMenuBar(jMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LPrivacypolicy;
    private javax.swing.JLabel LPrivacypolicy1;
    private javax.swing.JLabel LPrivacypolicy10;
    private javax.swing.JLabel LPrivacypolicy11;
    private javax.swing.JLabel LPrivacypolicy12;
    private javax.swing.JLabel LPrivacypolicy2;
    private javax.swing.JLabel LPrivacypolicy3;
    private javax.swing.JLabel LPrivacypolicy4;
    private javax.swing.JLabel LPrivacypolicy5;
    private javax.swing.JLabel LPrivacypolicy6;
    private javax.swing.JLabel LPrivacypolicy7;
    private javax.swing.JLabel LPrivacypolicy8;
    private javax.swing.JLabel LPrivacypolicy9;
    private javax.swing.JLabel LTos;
    private javax.swing.JLabel LTos1;
    private javax.swing.JLabel LTos10;
    private javax.swing.JLabel LTos11;
    private javax.swing.JLabel LTos12;
    private javax.swing.JLabel LTos2;
    private javax.swing.JLabel LTos3;
    private javax.swing.JLabel LTos4;
    private javax.swing.JLabel LTos5;
    private javax.swing.JLabel LTos6;
    private javax.swing.JLabel LTos7;
    private javax.swing.JLabel LTos8;
    private javax.swing.JLabel LTos9;
    private javax.swing.JLabel Lcr;
    private javax.swing.JLabel Lcr1;
    private javax.swing.JLabel Lcr10;
    private javax.swing.JLabel Lcr11;
    private javax.swing.JLabel Lcr12;
    private javax.swing.JLabel Lcr2;
    private javax.swing.JLabel Lcr3;
    private javax.swing.JLabel Lcr4;
    private javax.swing.JLabel Lcr5;
    private javax.swing.JLabel Lcr6;
    private javax.swing.JLabel Lcr7;
    private javax.swing.JLabel Lcr8;
    private javax.swing.JLabel Lcr9;
    private javax.swing.JList<String> Lriwayatangsuran;
    private javax.swing.JList<String> Lriwayatpinjaman;
    private javax.swing.JList<String> Lriwayatsimpanan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JMenu jmenu;
    private javax.swing.JMenu jmhelp;
    private javax.swing.JMenu jmusermenu;
    private javax.swing.JMenu jmview;
    private javax.swing.JLabel lblNIK;
    private javax.swing.JLabel lblalamat;
    private javax.swing.JLabel lbldivisi;
    private javax.swing.JLabel lbljeniskelamin;
    private javax.swing.JLabel lblnama;
    private javax.swing.JLabel lblnoanggota;
    private javax.swing.JLabel lblnohp;
    private javax.swing.JLabel lblpinjamanaktif;
    private javax.swing.JLabel lbltgllahir;
    private javax.swing.JLabel lbltotalpinjaman;
    private javax.swing.JPanel pnlangsuran;
    private javax.swing.JPanel pnlpinjamanaktif;
    private javax.swing.JPanel pnlsimpanan;
    private javax.swing.JPanel pnltotalpinjaman;
    // End of variables declaration//GEN-END:variables
}
