/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.Window;
import javax.swing.Box;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import utils.SesiLogin;
/**
 *
 * @author BeniAkbar
 */
public class Dashboard extends javax.swing.JFrame {
   
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Dashboard.class.getName());
    private String namaUserLogin = "User";
    private String groupUserLogin = "-";

    /**
     * Creates new form Menu
     */
    public Dashboard() {
        this("User", "-");
    }

    public Dashboard(String namaUserLogin, String groupUserLogin) {
        this.namaUserLogin = namaUserLogin == null || namaUserLogin.isBlank() ? "User" : namaUserLogin;
        this.groupUserLogin = groupUserLogin == null || groupUserLogin.isBlank() ? "-" : groupUserLogin;
        initComponents();
        getRootPane().putClientProperty("JRootPane.menuBarEmbedded", Boolean.FALSE);
        setTitle("Koperasi Raya Abadi Saudara");
        setupFooterLinks();
        setupMenuBar();
        setupTabbedPages();
        setLocationRelativeTo(null);
    }

    private void setupFooterLinks() {
        LPrivacypolicy.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        LPrivacypolicy.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tampilkanPrivacyPolicy();
            }
        });

        LTos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        LTos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tampilkanTermsOfService();
            }
        });
    }

    private void setupMenuBar() {
        jMenuBar2.removeAll();
        jmhelp.setText("help");
        jmhelp.removeAll();
        JMenuItem faqItem = new JMenuItem("FAQ");
        JMenuItem tentangItem = new JMenuItem("Tentang Aplikasi");
        JMenuItem developerItem = new JMenuItem("About/Tentang");
        faqItem.addActionListener(e -> tampilkanFaq());
        tentangItem.addActionListener(e -> tampilkanTentangAplikasi());
        developerItem.addActionListener(e -> tampilkanAboutTentang());
        jmhelp.add(faqItem);
        jmhelp.add(tentangItem);
        jmhelp.add(developerItem);

        jmview.setText("View");
        jmview.removeAll();
        JMenuItem lightItem = new JMenuItem("Light");
        JMenuItem darkItem = new JMenuItem("Dark");
        lightItem.addActionListener(e -> gantiTema(false));
        darkItem.addActionListener(e -> gantiTema(true));
        jmview.add(lightItem);
        jmview.add(darkItem);

        jmusermenu.setText(namaUserLogin);
        jmusermenu.setIcon(iconUserMenu());
        jmusermenu.removeAll();
        JMenuItem profileItem = new JMenuItem("Profile");
        JMenuItem logoutItem = new JMenuItem("Logout / Keluar");
        profileItem.addActionListener(e -> tampilkanProfile());
        logoutItem.addActionListener(e -> logout());
        jmusermenu.add(profileItem);
        jmusermenu.addSeparator();
        jmusermenu.add(logoutItem);

        jMenuBar2.add(jmhelp);
        jMenuBar2.add(jmview);
        jMenuBar2.add(Box.createHorizontalGlue());
        jMenuBar2.add(jmusermenu);
        setJMenuBar(jMenuBar2);
        jMenuBar2.revalidate();
        jMenuBar2.repaint();
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

    private void setupTabbedPages() {
        isiTab(jpdashboard, new DashboardPanel());
        isiTab(jpManggota, new ManajemenAnggota());
        isiTab(jpMsimpanan, new ManajemenSimpanan());
        isiTab(jpMpinjaman, new ManajemenPinjaman());
        isiTab(JpManajemenAgunan, new ManajemenAgunan().getContentPane());
        isiTab(jpangsuran, new Angsuran());
        isiTab(jptransaksi, new Transaksi());
        isiTab(jplaporan, new Laporan());
        isiTab(jppengaturan, new Pengaturan(groupUserLogin));
        Transaksi.setSelectedIndex(0);
    }

    private void isiTab(JPanel tabPanel, Component contentPanel) {
        tabPanel.removeAll();
        tabPanel.setLayout(new BorderLayout());
        tabPanel.add(contentPanel, BorderLayout.CENTER);
        tabPanel.revalidate();
        tabPanel.repaint();
    }

    private void tampilkanFaq() {
        JOptionPane.showMessageDialog(
                this,
                "FAQ\n\n"
                + "1. Login menggunakan email/nama dan password yang terdaftar.\n"
                + "2. Data anggota, simpanan, pinjaman, angsuran, transaksi, dan laporan tersimpan di database.\n"
                + "3. Jika data tidak tampil, pastikan koneksi database aktif.",
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
                "Profile User\n\n"
                + "Nama: " + namaUserLogin + "\n"
                + "Hak Akses: " + groupUserLogin,
                "Profile",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void logout() {
        int pilihan = JOptionPane.showConfirmDialog(
                this,
                "Keluar dari aplikasi dan kembali ke login?",
                "Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (pilihan == JOptionPane.YES_OPTION) {
            SesiLogin.hapusInfoLoginTersimpan();
            SesiLogin.keluar();
            this.dispose();
            new Login().setVisible(true);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        Transaksi = new javax.swing.JTabbedPane();
        jpdashboard = new javax.swing.JPanel();
        jpManggota = new javax.swing.JPanel();
        jpMsimpanan = new javax.swing.JPanel();
        jpMpinjaman = new javax.swing.JPanel();
        JpManajemenAgunan = new javax.swing.JPanel();
        jpangsuran = new javax.swing.JPanel();
        jptransaksi = new javax.swing.JPanel();
        jplaporan = new javax.swing.JPanel();
        jppengaturan = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        Lcr = new javax.swing.JLabel();
        LTos = new javax.swing.JLabel();
        LPrivacypolicy = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuBar2 = new javax.swing.JMenuBar();
        jmhelp = new javax.swing.JMenu();
        jmview = new javax.swing.JMenu();
        jmusermenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Menu");
        setBackground(new java.awt.Color(255, 255, 255));

        Transaksi.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        Transaksi.setOpaque(true);

        jpdashboard.setOpaque(false);

        javax.swing.GroupLayout jpdashboardLayout = new javax.swing.GroupLayout(jpdashboard);
        jpdashboard.setLayout(jpdashboardLayout);
        jpdashboardLayout.setHorizontalGroup(
            jpdashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1136, Short.MAX_VALUE)
        );
        jpdashboardLayout.setVerticalGroup(
            jpdashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 606, Short.MAX_VALUE)
        );

        Transaksi.addTab("Dashboard", jpdashboard);

        jpManggota.setDoubleBuffered(false);
        jpManggota.setOpaque(false);

        javax.swing.GroupLayout jpManggotaLayout = new javax.swing.GroupLayout(jpManggota);
        jpManggota.setLayout(jpManggotaLayout);
        jpManggotaLayout.setHorizontalGroup(
            jpManggotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1136, Short.MAX_VALUE)
        );
        jpManggotaLayout.setVerticalGroup(
            jpManggotaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 606, Short.MAX_VALUE)
        );

        Transaksi.addTab("Manajemen Anggota", jpManggota);

        jpMsimpanan.setOpaque(false);

        javax.swing.GroupLayout jpMsimpananLayout = new javax.swing.GroupLayout(jpMsimpanan);
        jpMsimpanan.setLayout(jpMsimpananLayout);
        jpMsimpananLayout.setHorizontalGroup(
            jpMsimpananLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1136, Short.MAX_VALUE)
        );
        jpMsimpananLayout.setVerticalGroup(
            jpMsimpananLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 606, Short.MAX_VALUE)
        );

        Transaksi.addTab("Manajemen Simpanan", jpMsimpanan);

        jpMpinjaman.setOpaque(false);

        javax.swing.GroupLayout jpMpinjamanLayout = new javax.swing.GroupLayout(jpMpinjaman);
        jpMpinjaman.setLayout(jpMpinjamanLayout);
        jpMpinjamanLayout.setHorizontalGroup(
            jpMpinjamanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1136, Short.MAX_VALUE)
        );
        jpMpinjamanLayout.setVerticalGroup(
            jpMpinjamanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 606, Short.MAX_VALUE)
        );

        Transaksi.addTab("Manajemen Pinjaman", jpMpinjaman);

        javax.swing.GroupLayout JpManajemenAgunanLayout = new javax.swing.GroupLayout(JpManajemenAgunan);
        JpManajemenAgunan.setLayout(JpManajemenAgunanLayout);
        JpManajemenAgunanLayout.setHorizontalGroup(
            JpManajemenAgunanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1136, Short.MAX_VALUE)
        );
        JpManajemenAgunanLayout.setVerticalGroup(
            JpManajemenAgunanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 606, Short.MAX_VALUE)
        );

        Transaksi.addTab("Manajemen Agunan", JpManajemenAgunan);

        jpangsuran.setOpaque(false);

        javax.swing.GroupLayout jpangsuranLayout = new javax.swing.GroupLayout(jpangsuran);
        jpangsuran.setLayout(jpangsuranLayout);
        jpangsuranLayout.setHorizontalGroup(
            jpangsuranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1136, Short.MAX_VALUE)
        );
        jpangsuranLayout.setVerticalGroup(
            jpangsuranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 606, Short.MAX_VALUE)
        );

        Transaksi.addTab("Angsuran", jpangsuran);

        jptransaksi.setOpaque(false);

        javax.swing.GroupLayout jptransaksiLayout = new javax.swing.GroupLayout(jptransaksi);
        jptransaksi.setLayout(jptransaksiLayout);
        jptransaksiLayout.setHorizontalGroup(
            jptransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1136, Short.MAX_VALUE)
        );
        jptransaksiLayout.setVerticalGroup(
            jptransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 606, Short.MAX_VALUE)
        );

        Transaksi.addTab("Transaksi", jptransaksi);

        jplaporan.setOpaque(false);

        javax.swing.GroupLayout jplaporanLayout = new javax.swing.GroupLayout(jplaporan);
        jplaporan.setLayout(jplaporanLayout);
        jplaporanLayout.setHorizontalGroup(
            jplaporanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1136, Short.MAX_VALUE)
        );
        jplaporanLayout.setVerticalGroup(
            jplaporanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 606, Short.MAX_VALUE)
        );

        Transaksi.addTab("Laporan", jplaporan);

        jppengaturan.setOpaque(false);

        javax.swing.GroupLayout jppengaturanLayout = new javax.swing.GroupLayout(jppengaturan);
        jppengaturan.setLayout(jppengaturanLayout);
        jppengaturanLayout.setHorizontalGroup(
            jppengaturanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1136, Short.MAX_VALUE)
        );
        jppengaturanLayout.setVerticalGroup(
            jppengaturanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 606, Short.MAX_VALUE)
        );

        Transaksi.addTab("Pengaturan", jppengaturan);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setOpaque(false);

        Lcr.setForeground(java.awt.SystemColor.textInactiveText);
        Lcr.setText("© 2026 Kelompok 2 Pemograman Visual. All Right Reserved");

        LTos.setForeground(java.awt.SystemColor.textInactiveText);
        LTos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LTos.setText("Term Of Service");

        LPrivacypolicy.setForeground(java.awt.SystemColor.textInactiveText);
        LPrivacypolicy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LPrivacypolicy.setText("Privacy Policy");

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Lcr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LPrivacypolicy, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LTos, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Lcr)
                        .addComponent(LTos)
                        .addComponent(LPrivacypolicy)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Transaksi)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(Transaksi)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        jMenuBar2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jmhelp.setText("Help");
        jMenuBar2.add(jmhelp);

        jmview.setText("View");
        jMenuBar2.add(jmview);

        jmusermenu.setText("UserMenu");
        jMenuBar2.add(jmusermenu);

        setJMenuBar(jMenuBar2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bsimpanan1ActionPerformed(java.awt.event.ActionEvent evt) {
        Transaksi.setSelectedComponent(jpMsimpanan);
    }

    private void bangsuranActionPerformed(java.awt.event.ActionEvent evt) {
        Transaksi.setSelectedComponent(jpangsuran);
    }

    private void blaporanActionPerformed(java.awt.event.ActionEvent evt) {
        Transaksi.setSelectedComponent(jplaporan);
    }

    private void bpengaturanActionPerformed(java.awt.event.ActionEvent evt) {
        Transaksi.setSelectedComponent(jppengaturan);
    }

    private void blogoutActionPerformed(java.awt.event.ActionEvent evt) {
        logout();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel JpManajemenAgunan;
    private javax.swing.JLabel LPrivacypolicy;
    private javax.swing.JLabel LTos;
    private javax.swing.JLabel Lcr;
    private javax.swing.JTabbedPane Transaksi;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JMenu jmhelp;
    private javax.swing.JMenu jmusermenu;
    private javax.swing.JMenu jmview;
    private javax.swing.JPanel jpManggota;
    private javax.swing.JPanel jpMpinjaman;
    private javax.swing.JPanel jpMsimpanan;
    private javax.swing.JPanel jpangsuran;
    private javax.swing.JPanel jpdashboard;
    private javax.swing.JPanel jplaporan;
    private javax.swing.JPanel jppengaturan;
    private javax.swing.JPanel jptransaksi;
    // End of variables declaration//GEN-END:variables

}
