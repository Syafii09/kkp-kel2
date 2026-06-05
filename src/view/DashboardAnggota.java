/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
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
        setLocationRelativeTo(null);
        setupMenuAnggota();
        loadDashboardAnggota();
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
        JMenuItem lengkapiDataItem = new JMenuItem("Lengkapi Data Diri");
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
                .addContainerGap(19, Short.MAX_VALUE))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
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
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
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
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
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
            .addComponent(jTabbedPane1)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
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
