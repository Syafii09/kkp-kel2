/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import dao.UserDAO;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Rangga
 */
public class Pengaturan extends javax.swing.JPanel {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Pengaturan.class.getName());
    private final UserDAO userDAO = new UserDAO();
    private final String groupUserLogin;
    private RoleGroup selectedRoleFilter;

    /**
     * Creates new form Pengaturan
     */
    public Pengaturan() {
        this("Super Admin");
    }

    public Pengaturan(String groupUserLogin) {
        this.groupUserLogin = groupUserLogin == null || groupUserLogin.isBlank() ? "-" : groupUserLogin;
        initComponents();
        setupUserHakAkses();
        setupKonfigurasiSistem();
    }

    private void setupKonfigurasiSistem() {
        muatKonfigurasiSistem();
        cbbMatauang.addActionListener(e -> simpanKonfigurasiSistem(false));
        tfPersenbunga.addActionListener(e -> simpanKonfigurasiSistem(true));
        tfSimpananWajib.addActionListener(e -> simpanKonfigurasiSistem(true));

        FocusAdapter autoSave = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                simpanKonfigurasiSistem(false);
            }
        };
        tfPersenbunga.addFocusListener(autoSave);
        tfSimpananWajib.addFocusListener(autoSave);
    }

    private void muatKonfigurasiSistem() {
        PengaturanSistem.Data data = PengaturanSistem.muat();
        cbbMatauang.setSelectedItem(data.mataUang());
        tfPersenbunga.setText(data.persenBunga().stripTrailingZeros().toPlainString());
        tfSimpananWajib.setText(PengaturanSistem.formatUang(data.simpananWajib(), data.mataUang()));
    }

    private void simpanKonfigurasiSistem(boolean tampilkanPesan) {
        BigDecimal bunga = PengaturanSistem.parseNominal(tfPersenbunga.getText());
        BigDecimal simpananWajib = PengaturanSistem.parseNominal(tfSimpananWajib.getText());
        String mataUang = cbbMatauang.getSelectedItem() == null
                ? "Rupiah(Rp)"
                : cbbMatauang.getSelectedItem().toString();

        try {
            PengaturanSistem.simpan(mataUang, bunga, simpananWajib);
            tfPersenbunga.setText(bunga.stripTrailingZeros().toPlainString());
            tfSimpananWajib.setText(PengaturanSistem.formatUang(simpananWajib, mataUang));

            if (tampilkanPesan) {
                JOptionPane.showMessageDialog(this, "Konfigurasi sistem berhasil disimpan.");
            }
        } catch (SQLException | RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal menyimpan konfigurasi sistem.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void setupUserHakAkses() {
        setupTabelUser();
        pastikanRoleDefault();
        muatTreeGroupPengguna();
        TGroupPengguna.addTreeSelectionListener(this::pilihGroupPengguna);
        btEdituser.addActionListener(e -> editUserTerpilih());
        btHapususer.addActionListener(e -> hapusUserTerpilih());
        setAksiUserEnabled();
        loadUserByGroup(null);
    }

    private void setupTabelUser() {
        TabelUser.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "No", "No Anggota", "Nama", "Email", "Jabatan", "Role", "Status", "Waktu Dibuat"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        TabelUser.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        if (TabelUser.getColumnModel().getColumnCount() > 0) {
            TabelUser.getColumnModel().getColumn(0).setMinWidth(0);
            TabelUser.getColumnModel().getColumn(0).setPreferredWidth(0);
            TabelUser.getColumnModel().getColumn(0).setMaxWidth(0);
            TabelUser.getColumnModel().getColumn(1).setPreferredWidth(42);
            TabelUser.getColumnModel().getColumn(1).setMaxWidth(55);
            TabelUser.getColumnModel().getColumn(2).setPreferredWidth(110);
            TabelUser.getColumnModel().getColumn(3).setPreferredWidth(180);
            TabelUser.getColumnModel().getColumn(4).setPreferredWidth(220);
            TabelUser.getColumnModel().getColumn(5).setPreferredWidth(140);
            TabelUser.getColumnModel().getColumn(6).setPreferredWidth(150);
            TabelUser.getColumnModel().getColumn(7).setPreferredWidth(90);
            TabelUser.getColumnModel().getColumn(8).setPreferredWidth(150);
        }
    }

    private void setAksiUserEnabled() {
        boolean superAdmin = isSuperAdminLogin();
        btTambahuser.setEnabled(superAdmin);
        btEdituser.setEnabled(superAdmin);
        btHapususer.setEnabled(superAdmin);
        String tooltip = superAdmin ? null : "Hanya Super Admin yang bisa tambah, edit, dan hapus user.";
        btTambahuser.setToolTipText(tooltip);
        btEdituser.setToolTipText(tooltip);
        btHapususer.setToolTipText(tooltip);
    }

    private boolean isSuperAdminLogin() {
        return "Super Admin".equalsIgnoreCase(groupUserLogin);
    }

    private boolean pastikanSuperAdmin(String aksi) {
        if (isSuperAdminLogin()) {
            return true;
        }

        JOptionPane.showMessageDialog(
                this,
                "Hanya Super Admin yang bisa " + aksi + " user.",
                "Akses Ditolak",
                JOptionPane.WARNING_MESSAGE
        );
        return false;
    }

    private void pastikanRoleDefault() {
        try {
            userDAO.pastikanRoleDefault();
        } catch (SQLException | RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal menyiapkan role pengguna.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void muatTreeGroupPengguna() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Group Pengguna");

        try {
            for (UserDAO.RoleData role : userDAO.getRoles()) {
                root.add(new DefaultMutableTreeNode(new RoleGroup(
                        role.idGroup(),
                        role.namaGroup()
                )));
            }
        } catch (SQLException | RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal memuat group pengguna.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        TGroupPengguna.setModel(new DefaultTreeModel(root));
        TGroupPengguna.setRootVisible(true);
        TGroupPengguna.expandRow(0);
        TGroupPengguna.setSelectionRow(0);
    }

    private void pilihGroupPengguna(TreeSelectionEvent event) {
        TreePath path = event.getPath();
        if (path == null) {
            loadUserByGroup(null);
            return;
        }

        Object selected = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        if (selected instanceof RoleGroup roleGroup) {
            loadUserByGroup(roleGroup);
        } else {
            loadUserByGroup(null);
        }
    }

    private void loadUserByGroup(RoleGroup roleGroup) {
        selectedRoleFilter = roleGroup;
        DefaultTableModel model = (DefaultTableModel) TabelUser.getModel();
        model.setRowCount(0);
        jLabel10.setText(roleGroup == null
                ? "Tabel User - Semua Role"
                : "Tabel User - " + roleGroup);

        try {
            int nomor = 1;
            Integer idGroup = roleGroup == null ? null : roleGroup.idGroup();
            for (UserDAO.UserData user : userDAO.getUsersByGroup(idGroup)) {
                model.addRow(new Object[]{
                    user.idUser(),
                    nomor++,
                    user.noAnggota(),
                    user.nama(),
                    user.email(),
                    user.jabatan(),
                    displayRole(user.namaGroup()),
                    user.status(),
                    user.createdAt()
                });
            }
        } catch (SQLException | RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal memuat data user.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void tambahUser() {
        if (!pastikanSuperAdmin("menambah")) {
            return;
        }

        FormPengisianUser formUser = new FormPengisianUser();
        formUser.setModeTambah();
        formUser.setOnDataSaved(this::refreshDataUser);
        formUser.setVisible(true);
    }

    private void editUserTerpilih() {
        if (!pastikanSuperAdmin("mengedit")) {
            return;
        }

        int modelRow = getSelectedUserModelRow();
        if (modelRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih user yang akan diedit terlebih dahulu.", "Edit User", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) TabelUser.getModel();
        FormPengisianUser formUser = new FormPengisianUser();
        formUser.setModeEdit(
                (Integer) model.getValueAt(modelRow, 0),
                String.valueOf(model.getValueAt(modelRow, 3)),
                String.valueOf(model.getValueAt(modelRow, 4)),
                String.valueOf(model.getValueAt(modelRow, 6)),
                nilaiTabel(model.getValueAt(modelRow, 2)),
                nilaiTabel(model.getValueAt(modelRow, 5))
        );
        formUser.setOnDataSaved(this::refreshDataUser);
        formUser.setVisible(true);
    }

    private void hapusUserTerpilih() {
        if (!pastikanSuperAdmin("menghapus")) {
            return;
        }

        int modelRow = getSelectedUserModelRow();
        if (modelRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih user yang akan dihapus terlebih dahulu.", "Hapus User", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) TabelUser.getModel();
        int idUser = (Integer) model.getValueAt(modelRow, 0);
        String nama = String.valueOf(model.getValueAt(modelRow, 3));

        int pilihan = JOptionPane.showConfirmDialog(
                this,
                "Hapus user " + nama + "?",
                "Hapus User",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (pilihan != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            userDAO.delete(idUser);
            JOptionPane.showMessageDialog(this, "User berhasil dihapus.");
            refreshDataUser();
        } catch (SQLException | RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus user.\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getSelectedUserModelRow() {
        int selectedRow = TabelUser.getSelectedRow();
        return selectedRow < 0 ? -1 : TabelUser.convertRowIndexToModel(selectedRow);
    }

    private void refreshDataUser() {
        loadUserByGroup(selectedRoleFilter);
    }

    private String nilaiTabel(Object value) {
        return value == null ? "" : value.toString();
    }

    private String displayRole(String namaGroup) {
        if ("Pengguna".equalsIgnoreCase(namaGroup)) {
            return "User/Pengguna (Anggota)";
        }

        return namaGroup;
    }

    private record RoleGroup(int idGroup, String namaGroup) {
        @Override
        public String toString() {
            if ("Pengguna".equalsIgnoreCase(namaGroup)) {
                return "User/Pengguna (Anggota)";
            }

            return namaGroup;
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TGroupPengguna = new javax.swing.JTree();
        jPanel4 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TabelUser = new javax.swing.JTable();
        btHapususer = new javax.swing.JButton();
        btEdituser = new javax.swing.JButton();
        btTambahuser = new javax.swing.JButton();
        PengaturanUmum = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cbbMatauang = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        tfPersenbunga = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        tfSimpananWajib = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tfNotelppengaturan = new javax.swing.JTextField();
        tfAlamatpengaturan = new javax.swing.JTextField();
        tfNamakoperasi = new javax.swing.JTextField();
        tfEmailpengaturan = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel3.setOpaque(false);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Group Pengguna");
        TGroupPengguna.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        TGroupPengguna.setOpaque(false);
        jScrollPane2.setViewportView(TGroupPengguna);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Tabel User");

        TabelUser.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "No", "No Anggota", "Nama", "Email", "Jabatan", "Role", "Status", "Waktu Dibuat"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(TabelUser);
        if (TabelUser.getColumnModel().getColumnCount() > 0) {
            TabelUser.getColumnModel().getColumn(0).setMaxWidth(30);
        }

        btHapususer.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btHapususer.setText("Hapus");

        btEdituser.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btEdituser.setText("Edit");

        btTambahuser.setBackground(new java.awt.Color(0, 51, 204));
        btTambahuser.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btTambahuser.setForeground(new java.awt.Color(255, 255, 255));
        btTambahuser.setText("Tambah");
        btTambahuser.addActionListener(this::btTambahuserActionPerformed);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(30, 30, 30)
                        .addComponent(btTambahuser)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btEdituser)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btHapususer)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btTambahuser)
                        .addComponent(btEdituser)
                        .addComponent(btHapususer))
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(81, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("User & Hak Acces", jPanel2);

        PengaturanUmum.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        PengaturanUmum.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel23.setText("Pengaturan Keuangan");

        jLabel5.setText("Mata Uang");

        cbbMatauang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Rupiah(Rp)", "Dolar Amerika($)", "Ringgit(RM)" }));

        jLabel6.setText("Persen Bunga");

        jLabel24.setText("Simpanan Wajib");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel10Layout.createSequentialGroup()
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(cbbMatauang, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(tfPersenbunga)
                                .addComponent(tfSimpananWajib)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cbbMatauang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tfPersenbunga)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tfSimpananWajib)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Nama Koperasi");

        jLabel2.setText("Alamat");

        jLabel3.setText("No. Telepon");

        jLabel4.setText("Email");

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel25.setText("Profile Koperasi");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(tfAlamatpengaturan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                            .addComponent(tfNotelppengaturan, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfEmailpengaturan, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfNamakoperasi))))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tfNamakoperasi)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tfAlamatpengaturan)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tfNotelppengaturan)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfEmailpengaturan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(193, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(187, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout PengaturanUmumLayout = new javax.swing.GroupLayout(PengaturanUmum);
        PengaturanUmum.setLayout(PengaturanUmumLayout);
        PengaturanUmumLayout.setHorizontalGroup(
            PengaturanUmumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        PengaturanUmumLayout.setVerticalGroup(
            PengaturanUmumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Konfigurasi Sistem", PengaturanUmum);

        jPanel9.setAlignmentX(0.0F);
        jPanel9.setAlignmentY(0.0F);

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Developed by");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Rangga Danuarta | Syafii Muhammad Arif | Ferry Kadafi");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Sabilarrusdi | Beni Akbar Suparman | Muhammad Alfaridzi | Julfi Alfiansyah");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Teknologi Utama");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Netbeans | Java | Html | Mysql");

        jLabel18.setForeground(new java.awt.Color(153, 153, 153));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("Dibuat Oleh Kelompok 2 Pemrograman Visual - Versi 1.0");
        jLabel18.setToolTipText("");

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Koperasi Raya Abadi Saudara");

        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("Version 1.0");

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("Universitas Indraprasta PGRI");

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("2026");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 638, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 638, Short.MAX_VALUE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel19)
                .addGap(0, 0, 0)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel13)
                .addGap(0, 0, 0)
                .addComponent(jLabel14)
                .addGap(0, 0, 0)
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel16)
                .addGap(0, 0, 0)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel21)
                .addGap(0, 0, 0)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
                .addComponent(jLabel18))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Tentang", jPanel8);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btTambahuserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btTambahuserActionPerformed
        tambahUser();
    }//GEN-LAST:event_btTambahuserActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PengaturanUmum;
    private javax.swing.JTree TGroupPengguna;
    private javax.swing.JTable TabelUser;
    private javax.swing.JButton btEdituser;
    private javax.swing.JButton btHapususer;
    private javax.swing.JButton btTambahuser;
    private javax.swing.JComboBox<String> cbbMatauang;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField tfAlamatpengaturan;
    private javax.swing.JTextField tfEmailpengaturan;
    private javax.swing.JTextField tfNamakoperasi;
    private javax.swing.JTextField tfNotelppengaturan;
    private javax.swing.JTextField tfPersenbunga;
    private javax.swing.JTextField tfSimpananWajib;
    // End of variables declaration//GEN-END:variables
}
