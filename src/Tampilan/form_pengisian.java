/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Tampilan;

import Koneksi.Koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 *
 * @author Rangga
 */
public class form_pengisian extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(form_pengisian.class.getName());
    private boolean modeEdit = false;
    private String noAnggotaLama = "";
    private Runnable onDataSaved;

    /**
     * Creates new form form_pengisian
     */
    public form_pengisian() {
        initComponents();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        btSimpancalonanggota.addActionListener(e -> simpanAnggota());
        btExitcalonanggota.addActionListener(e -> dispose());
        btResetcalonanggota.addActionListener(e -> resetForm());
    }

    public void setModeTambah() {
        modeEdit = false;
        noAnggotaLama = "";
        setTitle("Tambah Anggota");
        jLabel1.setText("IDENTITAS CALON ANGGOTA KOPERASI");
        btSimpancalonanggota.setText("SIMPAN");
    }

    public void setModeEdit() {
        modeEdit = true;
        setTitle("Edit Anggota");
        jLabel1.setText("EDIT IDENTITAS ANGGOTA KOPERASI");
        btSimpancalonanggota.setText("UPDATE");
    }

    public void isiDataAnggota(String noAnggota, String nama, String tempatTglLahir,
            String alamat, String noHp, String pekerjaan, Date tanggalDaftar) {
        tfNoanggotakoperasi.setText(noAnggota);
        tfNamacalonanggota.setText(nama);
        tfTempattglanggota.setText(tempatTglLahir);
        taAlamatanggota.setText(alamat);
        tfNohpanggota.setText(noHp);
        tfPekerjaananggota.setText(pekerjaan);
        calTgldaftaranggota.setDate(tanggalDaftar);
        noAnggotaLama = noAnggota;
    }

    public void isiDataAnggota(String noAnggota, String nama, String tempatLahir,
            Date tanggalLahir, String jenisKelamin, String alamat,
            String kotaKabupaten, String noHp, String pekerjaan, Date tanggalDaftar) {
        tfNoanggotakoperasi.setText(noAnggota);
        tfNamacalonanggota.setText(nama);
        tfTempattglanggota.setText(tempatLahir);
        calCalonanggota.setDate(tanggalLahir);
        pilihJenisKelamin(jenisKelamin);
        taAlamatanggota.setText(alamat);
        tfKotakabupaten.setText(kotaKabupaten);
        tfNohpanggota.setText(noHp);
        tfPekerjaananggota.setText(pekerjaan);
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
        String sql = """
                INSERT INTO anggota (
                  no_anggota, nama, tempat_lahir, tanggal_lahir, jenis_kelamin,
                  alamat, kota_kabupaten, no_hp, pekerjaan, tanggal_daftar, status
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Aktif')
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            isiParameterAnggota(statement);
            statement.executeUpdate();

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
        String sql = """
                UPDATE anggota
                SET no_anggota = ?,
                    nama = ?,
                    tempat_lahir = ?,
                    tanggal_lahir = ?,
                    jenis_kelamin = ?,
                    alamat = ?,
                    kota_kabupaten = ?,
                    no_hp = ?,
                    pekerjaan = ?,
                    tanggal_daftar = ?
                WHERE no_anggota = ?
                """;

        try (Connection connection = Koneksi.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            isiParameterAnggota(statement);
            statement.setString(11, noAnggotaLama);

            int jumlahUpdate = statement.executeUpdate();
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

    private void isiParameterAnggota(PreparedStatement statement) throws SQLException {
        statement.setString(1, tfNoanggotakoperasi.getText().trim());
        statement.setString(2, tfNamacalonanggota.getText().trim());
        statement.setString(3, tfTempattglanggota.getText().trim());
        setTanggal(statement, 4, calCalonanggota.getDate());
        statement.setString(5, getJenisKelamin());
        statement.setString(6, taAlamatanggota.getText().trim());
        statement.setString(7, tfKotakabupaten.getText().trim());
        statement.setString(8, tfNohpanggota.getText().trim());
        statement.setString(9, tfPekerjaananggota.getText().trim());
        setTanggal(statement, 10, calTgldaftaranggota.getDate());
    }

    private void setTanggal(PreparedStatement statement, int index, Date date) throws SQLException {
        if (date == null) {
            statement.setNull(index, java.sql.Types.DATE);
            return;
        }

        statement.setDate(index, new java.sql.Date(date.getTime()));
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

    private void refreshDataInduk() {
        if (onDataSaved != null) {
            onDataSaved.run();
        }
    }

    private void resetForm() {
        tfNoanggotakoperasi.setText("");
        tfNamacalonanggota.setText("");
        tfTempattglanggota.setText("");
        tfKotakabupaten.setText("");
        bgJenkelanggota.clearSelection();
        tfNohpanggota.setText("");
        taAlamatanggota.setText("");
        tfPekerjaananggota.setText("");
        calCalonanggota.setDate(null);
        calTgldaftaranggota.setDate(null);
        tfNoanggotakoperasi.requestFocus();
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
        jLabel10 = new javax.swing.JLabel();
        tfKotakabupaten = new javax.swing.JTextField();
        rbLakianggota = new javax.swing.JRadioButton();
        rbPerempuananggota = new javax.swing.JRadioButton();
        tfNoanggotakoperasi = new javax.swing.JTextField();
        tfNamacalonanggota = new javax.swing.JTextField();
        tfNohpanggota = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        taAlamatanggota = new javax.swing.JTextArea();
        tfPekerjaananggota = new javax.swing.JTextField();
        calTgldaftaranggota = new com.toedter.calendar.JDateChooser();
        btSimpancalonanggota = new javax.swing.JButton();
        btResetcalonanggota = new javax.swing.JButton();
        btExitcalonanggota = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("IDENTITAS CALON ANGGOTA KOPERASI");
        jLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jLabel2.setText("No. Anggota");

        jLabel3.setText("Tempat, Tgl Lahir");

        jLabel4.setText("Pekerjaan");

        jLabel5.setText("No.HP");

        jLabel6.setText("Nama");

        jLabel7.setText("Alamat");

        jLabel8.setText("Tgl Daftar");

        jLabel9.setText("Jenis Kelamin ");

        jLabel10.setText("Kota/Kabupaten");

        bgJenkelanggota.add(rbLakianggota);
        rbLakianggota.setText("Laki-laki");

        bgJenkelanggota.add(rbPerempuananggota);
        rbPerempuananggota.setText("Perempuan");
        rbPerempuananggota.addActionListener(this::rbPerempuananggotaActionPerformed);

        taAlamatanggota.setColumns(20);
        taAlamatanggota.setRows(5);
        jScrollPane1.setViewportView(taAlamatanggota);

        btSimpancalonanggota.setBackground(new java.awt.Color(0, 51, 204));
        btSimpancalonanggota.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btSimpancalonanggota.setForeground(new java.awt.Color(255, 255, 255));
        btSimpancalonanggota.setText("SIMPAN");

        btResetcalonanggota.setBackground(new java.awt.Color(255, 153, 0));
        btResetcalonanggota.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btResetcalonanggota.setForeground(new java.awt.Color(255, 255, 255));
        btResetcalonanggota.setText("RESET");

        btExitcalonanggota.setBackground(new java.awt.Color(255, 0, 51));
        btExitcalonanggota.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btExitcalonanggota.setForeground(new java.awt.Color(255, 255, 255));
        btExitcalonanggota.setText("EXIT");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(tfTempattglanggota, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(calCalonanggota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(tfNoanggotakoperasi, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tfNamacalonanggota)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(tfKotakabupaten, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rbLakianggota, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rbPerempuananggota, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(tfNohpanggota, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tfPekerjaananggota, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(calTgldaftaranggota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(btSimpancalonanggota)
                                        .addGap(18, 18, 18)
                                        .addComponent(btResetcalonanggota)
                                        .addGap(18, 18, 18)
                                        .addComponent(btExitcalonanggota))
                                    .addComponent(jScrollPane1))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel1)
                .addGap(48, 48, 48)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tfNoanggotakoperasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(tfNamacalonanggota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(tfTempattglanggota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(calCalonanggota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(tfKotakabupaten, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(rbLakianggota)
                            .addComponent(rbPerempuananggota))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5))
                    .addComponent(tfNohpanggota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tfPekerjaananggota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel8)
                        .addComponent(calTgldaftaranggota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btSimpancalonanggota)
                        .addComponent(btResetcalonanggota)
                        .addComponent(btExitcalonanggota)))
                .addGap(76, 76, 76))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rbPerempuananggotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbPerempuananggotaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbPerempuananggotaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new form_pengisian().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgJenkelanggota;
    private javax.swing.JButton btExitcalonanggota;
    private javax.swing.JButton btResetcalonanggota;
    private javax.swing.JButton btSimpancalonanggota;
    private com.toedter.calendar.JDateChooser calCalonanggota;
    private com.toedter.calendar.JDateChooser calTgldaftaranggota;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JTextArea taAlamatanggota;
    private javax.swing.JTextField tfKotakabupaten;
    private javax.swing.JTextField tfNamacalonanggota;
    private javax.swing.JTextField tfNoanggotakoperasi;
    private javax.swing.JTextField tfNohpanggota;
    private javax.swing.JTextField tfPekerjaananggota;
    private javax.swing.JTextField tfTempattglanggota;
    // End of variables declaration//GEN-END:variables
}
