/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import dao.AnggotaDAO;
import java.awt.Point;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author Rangga
 */
public class FormPengisian extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FormPengisian.class.getName());
    private final AnggotaDAO anggotaDAO = new AnggotaDAO();
    private boolean modeEdit = false;
    private String noAnggotaLama = "";
    private Runnable onDataSaved;
    private Point dragStartPoint;

    /**
     * Creates new form form_pengisian
     */
    public FormPengisian() {
        initComponents();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        btSimpancalonanggota.addActionListener(e -> simpanAnggota());
        btExitcalonanggota.addActionListener(e -> dispose());
        btResetcalonanggota.addActionListener(e -> resetForm());
        aktifkanDragFrame();
        setModeTambah();
    }

    private void aktifkanDragFrame() {
        java.awt.event.MouseAdapter dragAdapter = new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                dragStartPoint = evt.getPoint();
            }

            @Override
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                if (dragStartPoint == null) {
                    return;
                }

                Point screenPoint = evt.getLocationOnScreen();
                setLocation(screenPoint.x - dragStartPoint.x, screenPoint.y - dragStartPoint.y);
            }
        };

        getContentPane().addMouseListener(dragAdapter);
        getContentPane().addMouseMotionListener(dragAdapter);
        jLabel1.addMouseListener(dragAdapter);
        jLabel1.addMouseMotionListener(dragAdapter);
    }

    public void setModeTambah() {
        modeEdit = false;
        noAnggotaLama = "";
        setTitle("Tambah Anggota");
        jLabel1.setText("IDENTITAS CALON ANGGOTA KOPERASI");
        btSimpancalonanggota.setText("SIMPAN");
        tfNoanggotakoperasi.setEditable(false);
        isiNoAnggotaOtomatis();
    }

    public void setModeEdit() {
        modeEdit = true;
        setTitle("Edit Anggota");
        jLabel1.setText("EDIT IDENTITAS ANGGOTA KOPERASI");
        btSimpancalonanggota.setText("UPDATE");
        tfNoanggotakoperasi.setEditable(false);
    }

    private void isiNoAnggotaOtomatis() {
        try {
            tfNoanggotakoperasi.setText(anggotaDAO.generateNoAnggotaBerikutnya());
        } catch (SQLException | RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal membuat No. Anggota otomatis.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void isiDataAnggota(String noAnggota, String nama, String tempatTglLahir,
            String alamat, String noHp, String divisi, Date tanggalDaftar) {
        tfNoanggotakoperasi.setText(noAnggota);
        tfNamacalonanggota.setText(nama);
        tfTempattglanggota.setText(tempatTglLahir);
        taAlamatanggota.setText(alamat);
        tfNohpanggota.setText(noHp);
        tfdivisi.setText(divisi);
        calTgldaftaranggota.setDate(tanggalDaftar);
        noAnggotaLama = noAnggota;
    }

    public void isiDataAnggota(String noAnggota, String nama, String tempatLahir,
            Date tanggalLahir, String jenisKelamin, String alamat,
            String kotaKabupaten, String noHp, String divisi, Date tanggalDaftar) {
        isiDataAnggota(noAnggota, "", nama, tempatLahir, tanggalLahir, jenisKelamin,
                alamat, kotaKabupaten, noHp, divisi, tanggalDaftar);
    }

    public void isiDataAnggota(String noAnggota, String nik, String nama, String tempatLahir,
            Date tanggalLahir, String jenisKelamin, String alamat,
            String kotaKabupaten, String noHp, String divisi, Date tanggalDaftar) {
        tfNoanggotakoperasi.setText(noAnggota);
        setNIK(nik);
        tfNamacalonanggota.setText(nama);
        tfTempattglanggota.setText(tempatLahir);
        calCalonanggota.setDate(tanggalLahir);
        pilihJenisKelamin(jenisKelamin);
        taAlamatanggota.setText(alamat);
        setKotaKabupaten(kotaKabupaten);
        tfNohpanggota.setText(noHp);
        tfdivisi.setText(divisi);
        calTgldaftaranggota.setDate(tanggalDaftar);
        noAnggotaLama = noAnggota;
    }

    public void setOnDataSaved(Runnable onDataSaved) {
        this.onDataSaved = onDataSaved;
    }

    private void simpanAnggota() {
        String noAnggota = tfNoanggotakoperasi.getText().trim();
        String nama = tfNamacalonanggota.getText().trim();

        if (noAnggota.isEmpty() || nama.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No. Anggota dan Nama wajib diisi.",
                    "Validasi",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (calTgldaftaranggota.getDate() == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Tanggal daftar wajib diisi.",
                    "Validasi",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (modeEdit) {
            updateAnggota();
        } else {
            tambahAnggota();
        }
    }

    private void tambahAnggota() {
        if (tfNoanggotakoperasi.getText().trim().isEmpty()) {
            isiNoAnggotaOtomatis();
        }

        try {
            anggotaDAO.insert(buatDataAnggota());
            JOptionPane.showMessageDialog(this, "Data anggota berhasil disimpan.");
            refreshDataInduk();
            dispose();
        } catch (SQLException | RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal menyimpan data anggota.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateAnggota() {
        try {
            int jumlahUpdate = anggotaDAO.update(noAnggotaLama, buatDataAnggota());
            if (jumlahUpdate == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Data anggota tidak ditemukan.",
                        "Update Gagal",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            JOptionPane.showMessageDialog(this, "Data anggota berhasil diperbarui.");
            refreshDataInduk();
            dispose();
        } catch (SQLException | RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal memperbarui data anggota.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private AnggotaDAO.AnggotaData buatDataAnggota() {
        return new AnggotaDAO.AnggotaData(
                tfNoanggotakoperasi.getText().trim(),
                getNIK(),
                tfNamacalonanggota.getText().trim(),
                tfTempattglanggota.getText().trim(),
                calCalonanggota.getDate(),
                getJenisKelamin(),
                taAlamatanggota.getText().trim(),
                getKotaKabupaten(),
                tfNohpanggota.getText().trim(),
                tfdivisi.getText().trim(),
                calTgldaftaranggota.getDate(),
                "Aktif"
        );
    }

    private String getJenisKelamin() {
        if (rbLakianggota.isSelected()) {
            return "Laki-laki";
        }

        if (rbPerempuananggota.isSelected()) {
            return "Perempuan";
        }

        return null;
    }

    private void pilihJenisKelamin(String jenisKelamin) {
        bgJenkelanggota.clearSelection();

        if ("Laki-laki".equalsIgnoreCase(jenisKelamin)) {
            rbLakianggota.setSelected(true);
        } else if ("Perempuan".equalsIgnoreCase(jenisKelamin)) {
            rbPerempuananggota.setSelected(true);
        }
    }

    private String getNIK() {
        JTextField field = getNIKField();
        return field == null ? "" : field.getText().trim();
    }

    private void setNIK(String nik) {
        JTextField field = getNIKField();
        if (field != null) {
            field.setText(nik == null ? "" : nik);
        }
    }

    private JTextField getNIKField() {
        try {
            java.lang.reflect.Field field = getClass().getDeclaredField("tfNIK");
            field.setAccessible(true);
            Object value = field.get(this);
            return value instanceof JTextField ? (JTextField) value : null;
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            return null;
        }
    }

    private String getKotaKabupaten() {
        JTextField field = getKotaKabupatenField();
        return field == null ? "" : field.getText().trim();
    }

    private void setKotaKabupaten(String kotaKabupaten) {
        JTextField field = getKotaKabupatenField();
        if (field != null) {
            field.setText(kotaKabupaten == null ? "" : kotaKabupaten);
        }
    }

    private JTextField getKotaKabupatenField() {
        try {
            java.lang.reflect.Field field = getClass().getDeclaredField("tfKotakabupaten");
            field.setAccessible(true);
            Object value = field.get(this);
            return value instanceof JTextField ? (JTextField) value : null;
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            return null;
        }
    }

    private void refreshDataInduk() {
        if (onDataSaved != null) {
            onDataSaved.run();
        }
    }

    private void resetForm() {
        if (modeEdit) {
            tfNoanggotakoperasi.setText(noAnggotaLama);
        } else {
            isiNoAnggotaOtomatis();
        }
        setNIK("");
        tfNamacalonanggota.setText("");
        tfTempattglanggota.setText("");
        setKotaKabupaten("");
        bgJenkelanggota.clearSelection();
        tfNohpanggota.setText("");
        taAlamatanggota.setText("");
        tfdivisi.setText("");
        calCalonanggota.setDate(null);
        calTgldaftaranggota.setDate(null);
        tfNamacalonanggota.requestFocus();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgJenkelanggota = new javax.swing.ButtonGroup();
        jLabel10 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        calCalonanggota = new com.toedter.calendar.JDateChooser();
        tfTempattglanggota = new javax.swing.JTextField();
        rbLakianggota = new javax.swing.JRadioButton();
        rbPerempuananggota = new javax.swing.JRadioButton();
        tfNoanggotakoperasi = new javax.swing.JTextField();
        tfNamacalonanggota = new javax.swing.JTextField();
        tfNohpanggota = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        taAlamatanggota = new javax.swing.JTextArea();
        tfdivisi = new javax.swing.JTextField();
        calTgldaftaranggota = new com.toedter.calendar.JDateChooser();
        btSimpancalonanggota = new javax.swing.JButton();
        btResetcalonanggota = new javax.swing.JButton();
        btExitcalonanggota = new javax.swing.JButton();
        tfNIK = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        rbother = new javax.swing.JRadioButton();

        jLabel10.setText("jLabel10");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("Input Anggota");

        jLabel2.setText("No. Anggota");

        jLabel3.setText("Tempat, Tgl Lahir");

        jLabel4.setText("Divisi/Departement");

        jLabel5.setText("No.HP");

        jLabel6.setText("Nama");

        jLabel7.setText("Alamat");

        jLabel8.setText("Tgl Daftar");

        jLabel9.setText("Jenis Kelamin ");

        bgJenkelanggota.add(rbLakianggota);
        rbLakianggota.setText("Laki-laki");

        bgJenkelanggota.add(rbPerempuananggota);
        rbPerempuananggota.setText("Perempuan");
        rbPerempuananggota.addActionListener(this::rbPerempuananggotaActionPerformed);

        taAlamatanggota.setColumns(20);
        taAlamatanggota.setRows(5);
        jScrollPane1.setViewportView(taAlamatanggota);

        btSimpancalonanggota.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btSimpancalonanggota.setText("SIMPAN");

        btResetcalonanggota.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btResetcalonanggota.setText("RESET");

        btExitcalonanggota.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btExitcalonanggota.setText("KEMBALI");

        jLabel11.setText("NIK");

        bgJenkelanggota.add(rbother);
        rbother.setText("Other");
        rbother.addActionListener(this::rbotherActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(tfTempattglanggota, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(calCalonanggota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfNohpanggota, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(tfNIK, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(tfNamacalonanggota)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(rbLakianggota, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(rbPerempuananggota, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(rbother, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(tfdivisi, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(calTgldaftaranggota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGap(31, 31, 31)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(tfNoanggotakoperasi, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btExitcalonanggota)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btResetcalonanggota)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btSimpancalonanggota)))))
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
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tfNIK, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                            .addComponent(tfNamacalonanggota))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(calCalonanggota, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                            .addComponent(tfTempattglanggota)
                            .addComponent(tfNohpanggota)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel9)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(rbLakianggota)
                                .addComponent(rbPerempuananggota)
                                .addComponent(rbother)))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(33, 33, 33)
                            .addComponent(tfNoanggotakoperasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(calTgldaftaranggota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(7, 7, 7)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfdivisi, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btSimpancalonanggota)
                    .addComponent(btResetcalonanggota)
                    .addComponent(btExitcalonanggota))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rbPerempuananggotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbPerempuananggotaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbPerempuananggotaActionPerformed

    private void rbotherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbotherActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbotherActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgJenkelanggota;
    private javax.swing.JButton btExitcalonanggota;
    private javax.swing.JButton btResetcalonanggota;
    private javax.swing.JButton btSimpancalonanggota;
    private com.toedter.calendar.JDateChooser calCalonanggota;
    private com.toedter.calendar.JDateChooser calTgldaftaranggota;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton rbLakianggota;
    private javax.swing.JRadioButton rbPerempuananggota;
    private javax.swing.JRadioButton rbother;
    private javax.swing.JTextArea taAlamatanggota;
    private javax.swing.JTextField tfNIK;
    private javax.swing.JTextField tfNamacalonanggota;
    private javax.swing.JTextField tfNoanggotakoperasi;
    private javax.swing.JTextField tfNohpanggota;
    private javax.swing.JTextField tfTempattglanggota;
    private javax.swing.JTextField tfdivisi;
    // End of variables declaration//GEN-END:variables
}
