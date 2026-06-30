/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import dao.UserDAO;
import java.sql.SQLException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

/**
 *
 * @author sabil
 */
public class FormPengisianUser extends javax.swing.JFrame {
    private final UserDAO userDAO = new UserDAO();
    private boolean modeEdit = false;
    private int idUserEdit = -1;
    private Runnable onDataSaved;

    /**
     * Creates new form form_pengisian_user
     */
    public FormPengisianUser() {
        initComponents();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        muatRole();
        setupAksi();
    }

    public void setModeTambah() {
        modeEdit = false;
        idUserEdit = -1;
        jLabel1.setText("Input User");
        pfpassword.setEnabled(true);
        pfconfirmpassword.setEnabled(true);
    }

    public void setModeEdit(int idUser, String nama, String email, String role) {
        setModeEdit(idUser, nama, email, role, "");
    }

    public void setModeEdit(int idUser, String nama, String email, String role, String noAnggota) {
        setModeEdit(idUser, nama, email, role, noAnggota, "");
    }

    public void setModeEdit(int idUser, String nama, String email, String role, String noAnggota, String jabatan) {
        modeEdit = true;
        idUserEdit = idUser;
        jLabel1.setText("Edit User");
        tfNIK.setText(nama);
        tfNamacalonanggota.setText(email);
        pilihRole(role);
        pilihJabatan(jabatan);
        pfpassword.setText("");
        pfconfirmpassword.setText("");
        pfpassword.putClientProperty("JTextField.placeholderText", "Kosongkan jika tidak diganti");
        pfconfirmpassword.putClientProperty("JTextField.placeholderText", "Kosongkan jika tidak diganti");
    }

    public void setOnDataSaved(Runnable onDataSaved) {
        this.onDataSaved = onDataSaved;
    }

    private void setupAksi() {
        tfNIK.putClientProperty("JTextField.placeholderText", "Nama");
        tfNamacalonanggota.putClientProperty("JTextField.placeholderText", "Email");
        pfpassword.putClientProperty("JTextField.placeholderText", "Password");
        pfconfirmpassword.putClientProperty("JTextField.placeholderText", "Confirm password");
        getRootPane().registerKeyboardAction(
                e -> simpanUser(),
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.KeyEvent.CTRL_DOWN_MASK),
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        btSave.addActionListener(e -> simpanUser());
        btCancel.addActionListener(e -> dispose());
        tfNIK.addActionListener(e -> tfNamacalonanggota.requestFocus());
        tfNamacalonanggota.addActionListener(e -> pfpassword.requestFocus());
        pfpassword.addActionListener(e -> pfconfirmpassword.requestFocus());
        pfconfirmpassword.addActionListener(e -> simpanUser());
    }

    private void muatRole() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();

        try {
            for (UserDAO.RoleData role : userDAO.getRoles()) {
                model.addElement(new RoleItem(role.idGroup(), role.namaGroup()));
            }
        } catch (SQLException | RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat role.\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        cbgroups.setModel(model);
    }

    private void pilihRole(String role) {
        for (int index = 0; index < cbgroups.getItemCount(); index++) {
            Object item = cbgroups.getItemAt(index);
            if (item instanceof RoleItem roleItem
                    && (roleItem.namaGroup().equalsIgnoreCase(role) || roleItem.toString().equalsIgnoreCase(role))) {
                cbgroups.setSelectedIndex(index);
                return;
            }
        }
    }

    private void pilihJabatan(String jabatan) {
        if (jabatan == null || jabatan.isBlank()) {
            cbJabatan.setSelectedIndex(0);
            return;
        }

        for (int index = 0; index < cbJabatan.getItemCount(); index++) {
            String item = cbJabatan.getItemAt(index);
            if (item != null && item.equalsIgnoreCase(jabatan)) {
                cbJabatan.setSelectedIndex(index);
                return;
            }
        }
        cbJabatan.setSelectedIndex(0);
    }

    private void simpanUser() {
        String nama = tfNIK.getText().trim();
        String email = tfNamacalonanggota.getText().trim();
        String password = new String(pfpassword.getPassword()).trim();
        String konfirmasi = new String(pfconfirmpassword.getPassword()).trim();
        Object selectedRole = cbgroups.getSelectedItem();
        String jabatan = cbJabatan.getSelectedItem() == null ? "" : cbJabatan.getSelectedItem().toString();

        if (nama.isEmpty() || email.isEmpty() || selectedRole == null || jabatan.startsWith("--")) {
            JOptionPane.showMessageDialog(this, "Nama, email, role, dan jabatan wajib diisi.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Format email belum benar.", "Validasi", JOptionPane.WARNING_MESSAGE);
            tfNamacalonanggota.requestFocus();
            return;
        }

        if (!modeEdit && password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password wajib diisi untuk user baru.", "Validasi", JOptionPane.WARNING_MESSAGE);
            pfpassword.requestFocus();
            return;
        }

        if (!password.equals(konfirmasi)) {
            JOptionPane.showMessageDialog(this, "Confirm password tidak sama.", "Validasi", JOptionPane.WARNING_MESSAGE);
            pfconfirmpassword.setText("");
            pfconfirmpassword.requestFocus();
            return;
        }

        RoleItem role = (RoleItem) selectedRole;
        Integer idAnggota = null;

        if (modeEdit) {
            updateUser(nama, email, password, role.idGroup(), idAnggota, jabatan);
        } else {
            tambahUser(nama, email, password, role.idGroup(), idAnggota, jabatan);
        }
    }

    private void tambahUser(String nama, String email, String password, int idGroup, Integer idAnggota, String jabatan) {
        try {
            userDAO.insert(nama, email, password, idGroup, idAnggota, jabatan);
            selesaiSimpan("User berhasil ditambahkan.");
        } catch (SQLException ex) {
            tampilkanErrorSimpan(ex);
        }
    }

    private void updateUser(String nama, String email, String password, int idGroup, Integer idAnggota, String jabatan) {
        try {
            userDAO.update(idUserEdit, nama, email, password, idGroup, idAnggota, jabatan);
            selesaiSimpan("User berhasil diperbarui.");
        } catch (SQLException ex) {
            tampilkanErrorSimpan(ex);
        }
    }

    private void selesaiSimpan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan);
        if (onDataSaved != null) {
            onDataSaved.run();
        }
        dispose();
    }

    private void tampilkanErrorSimpan(SQLException ex) {
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("duplicate")) {
            JOptionPane.showMessageDialog(this, "Email sudah terdaftar.", "Simpan User", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Gagal menyimpan user.\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        tfNIK = new javax.swing.JTextField();
        tfNamacalonanggota = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        pfpassword = new javax.swing.JPasswordField();
        pfconfirmpassword = new javax.swing.JPasswordField();
        jLabel13 = new javax.swing.JLabel();
        cbgroups = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        cbJabatan = new javax.swing.JComboBox<>();
        btSave = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("Input User");

        jLabel11.setText("Nama");

        jLabel6.setText("Email");

        jLabel12.setText("Password");

        jLabel7.setText("Confirm Password");

        jLabel13.setText("Role");

        cbgroups.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Pilih Role --" }));

        jLabel2.setText("Jabatan");

        cbJabatan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Pilih Jabatan --", "Ketua Koperasi", "Wakil Ketua", "Sekretaris", "Bendahara", "Supervisor", "Staff" }));

        btSave.setText("Simpan");

        btCancel.setText("Kembali");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(cbgroups, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbJabatan, 0, 162, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(tfNIK, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(tfNamacalonanggota, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(pfpassword, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                                    .addComponent(pfconfirmpassword))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btSave)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tfNIK)
                    .addComponent(tfNamacalonanggota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pfpassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pfconfirmpassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbgroups, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbJabatan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btSave)
                    .addComponent(btCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btSave;
    private javax.swing.JComboBox<String> cbJabatan;
    private javax.swing.JComboBox<String> cbgroups;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPasswordField pfconfirmpassword;
    private javax.swing.JPasswordField pfpassword;
    private javax.swing.JTextField tfNIK;
    private javax.swing.JTextField tfNamacalonanggota;
    // End of variables declaration//GEN-END:variables

    private record RoleItem(int idGroup, String namaGroup) {
        @Override
        public String toString() {
            if ("Pengguna".equalsIgnoreCase(namaGroup)) {
                return "User/Pengguna (Anggota)";
            }

            return namaGroup;
        }
    }
}
