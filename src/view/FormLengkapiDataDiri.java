/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import dao.AnggotaDAO;
import java.sql.SQLException;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import utils.SesiLogin;
import view.DashboardAnggota;
import view.Login;

/**
 *
 * @author sabil
 */
public class FormLengkapiDataDiri extends javax.swing.JFrame {
    private final AnggotaDAO anggotaDAO = new AnggotaDAO();
    private final Integer idAnggota;
    private final String namaUserLogin;
    private final boolean kembaliKeDashboard;
    private final ButtonGroup bgJenisKelamin = new ButtonGroup();

    /**
     * Creates new form FormLengkapiDataDiri
     */
    public FormLengkapiDataDiri() {
        this(null, "Anggota");
    }

    public FormLengkapiDataDiri(Integer idAnggota, String namaUserLogin) {
        this(idAnggota, namaUserLogin, false);
    }

    public FormLengkapiDataDiri(Integer idAnggota, String namaUserLogin, boolean kembaliKeDashboard) {
        this.idAnggota = idAnggota;
        this.namaUserLogin = namaUserLogin == null || namaUserLogin.isBlank() ? "Anggota" : namaUserLogin;
        this.kembaliKeDashboard = kembaliKeDashboard;
        initComponents();
        setupForm();
        muatDataAnggota();
    }

    private void setupForm() {
        setLocationRelativeTo(null);
        bgJenisKelamin.add(rbLakianggota);
        bgJenisKelamin.add(rbPerempuananggota);
        bgJenisKelamin.add(rbother);
        rbLakianggota.setActionCommand("Laki-laki");
        rbPerempuananggota.setActionCommand("Perempuan");
        rbother.setActionCommand("Other");
        tfNoanggotakoperasi.setEditable(false);
        btSimpancalonanggota.addActionListener(e -> simpanDataDiri());
        btResetcalonanggota.addActionListener(e -> resetInput());
        btExitcalonanggota.addActionListener(e -> kembaliKeLogin());
    }

    private void muatDataAnggota() {
        if (idAnggota == null) {
            JOptionPane.showMessageDialog(this, "Data anggota tidak ditemukan.", "Lengkapi Data", JOptionPane.WARNING_MESSAGE);
            kembaliKeLogin();
            return;
        }

        try {
            AnggotaDAO.AnggotaData data = anggotaDAO.getById(idAnggota);
            if (data == null) {
                JOptionPane.showMessageDialog(this, "Data anggota tidak ditemukan.", "Lengkapi Data", JOptionPane.WARNING_MESSAGE);
                kembaliKeLogin();
                return;
            }

            tfNoanggotakoperasi.setText(data.noAnggota());
            tfNamaanggota.setText(nilai(data.nama(), namaUserLogin));
            tfNIK.setText(nilai(data.nik(), ""));
            tfTempattglanggota.setText(nilai(data.tempatLahir(), ""));
            calCalonanggota.setDate(data.tanggalLahir());
            tfNohpanggota.setText(nilai(data.noHp(), ""));
            taAlamatanggota.setText(nilai(data.alamat(), ""));
            tfdivisi.setText(nilai(data.divisi(), ""));

            if ("Perempuan".equalsIgnoreCase(data.jenisKelamin())) {
                rbPerempuananggota.setSelected(true);
            } else if ("Laki-laki".equalsIgnoreCase(data.jenisKelamin())) {
                rbLakianggota.setSelected(true);
            }
        } catch (SQLException | RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data anggota.\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void simpanDataDiri() {
        String nama = tfNamaanggota.getText().trim();
        String nik = tfNIK.getText().trim();
        String tempatLahir = tfTempattglanggota.getText().trim();
        java.util.Date tanggalLahir = calCalonanggota.getDate();
        String noHp = tfNohpanggota.getText().trim();
        String alamat = taAlamatanggota.getText().trim();
        String divisi = tfdivisi.getText().trim();
        String jenisKelamin = getJenisKelamin();

        if (nama.isEmpty() || nik.isEmpty() || tempatLahir.isEmpty() || tanggalLahir == null
                || noHp.isEmpty() || alamat.isEmpty() || divisi.isEmpty() || jenisKelamin == null) {
            JOptionPane.showMessageDialog(this, "Lengkapi semua data terlebih dahulu.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            AnggotaDAO.AnggotaData data = new AnggotaDAO.AnggotaData(
                    tfNoanggotakoperasi.getText().trim(),
                    nik,
                    nama,
                    tempatLahir,
                    tanggalLahir,
                    jenisKelamin,
                    alamat,
                    null,
                    noHp,
                    divisi,
                    new java.util.Date(),
                    "Aktif"
            );

            anggotaDAO.lengkapiDataDiri(idAnggota, data);
            SesiLogin.masuk(SesiLogin.getIdUser(), idAnggota, nama, SesiLogin.getGroupUser());
            JOptionPane.showMessageDialog(this, "Data diri berhasil dilengkapi.");
            new DashboardAnggota(idAnggota, nama).setVisible(true);
            dispose();
        } catch (SQLException | RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data diri.\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getJenisKelamin() {
        if (bgJenisKelamin.getSelection() == null) {
            return null;
        }
        String jenisKelamin = bgJenisKelamin.getSelection().getActionCommand();
        return "Other".equalsIgnoreCase(jenisKelamin) ? null : jenisKelamin;
    }

    private void resetInput() {
        muatDataAnggota();
    }

    private void kembaliKeLogin() {
        if (kembaliKeDashboard && idAnggota != null) {
            dispose();
        } else {
            SesiLogin.hapusInfoLoginTersimpan();
            SesiLogin.keluar();
            new Login().setVisible(true);
            dispose();
        }
    }

    private String nilai(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tfTempattglanggota = new javax.swing.JTextField();
        rbLakianggota = new javax.swing.JRadioButton();
        rbPerempuananggota = new javax.swing.JRadioButton();
        tfNoanggotakoperasi = new javax.swing.JTextField();
        tfNamaanggota = new javax.swing.JTextField();
        tfNohpanggota = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        taAlamatanggota = new javax.swing.JTextArea();
        tfdivisi = new javax.swing.JTextField();
        btSimpancalonanggota = new javax.swing.JButton();
        btResetcalonanggota = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btExitcalonanggota = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        tfNIK = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        rbother = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        calCalonanggota = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        rbLakianggota.setText("Laki-laki");

        rbPerempuananggota.setText("Perempuan");
        rbPerempuananggota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbPerempuananggotaActionPerformed(evt);
            }
        });

        tfNoanggotakoperasi.setEditable(false);

        taAlamatanggota.setColumns(20);
        taAlamatanggota.setRows(5);
        jScrollPane1.setViewportView(taAlamatanggota);

        btSimpancalonanggota.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btSimpancalonanggota.setText("SIMPAN");

        btResetcalonanggota.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btResetcalonanggota.setText("RESET");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("Lengkapi  Data Diri");

        btExitcalonanggota.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btExitcalonanggota.setText("CANCEL");

        jLabel2.setText("No. Anggota");

        jLabel3.setText("Tempat, Tgl Lahir");

        jLabel11.setText("NIK");

        jLabel4.setText("Divisi/Departement");

        rbother.setText("Other");
        rbother.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbotherActionPerformed(evt);
            }
        });

        jLabel5.setText("No.HP");

        jLabel6.setText("Nama");

        jLabel7.setText("Alamat");

        jLabel9.setText("Jenis Kelamin ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tfNoanggotakoperasi)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(tfTempattglanggota, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
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
                                    .addComponent(tfNamaanggota)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(rbLakianggota, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(rbPerempuananggota, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(rbother, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btExitcalonanggota)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btResetcalonanggota)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btSimpancalonanggota))
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(tfdivisi, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tfNoanggotakoperasi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(28, 28, 28))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tfNIK, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                            .addComponent(tfNamaanggota))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(calCalonanggota, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                            .addComponent(tfTempattglanggota)
                            .addComponent(tfNohpanggota))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbLakianggota)
                            .addComponent(rbPerempuananggota)
                            .addComponent(rbother)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfdivisi, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btSimpancalonanggota)
                            .addComponent(btResetcalonanggota)
                            .addComponent(btExitcalonanggota))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rbPerempuananggotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbPerempuananggotaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbPerempuananggotaActionPerformed

    private void rbotherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbotherActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbotherActionPerformed

    /**
     * @param args the command line arguments
     */


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btExitcalonanggota;
    private javax.swing.JButton btResetcalonanggota;
    private javax.swing.JButton btSimpancalonanggota;
    private com.toedter.calendar.JDateChooser calCalonanggota;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton rbLakianggota;
    private javax.swing.JRadioButton rbPerempuananggota;
    private javax.swing.JRadioButton rbother;
    private javax.swing.JTextArea taAlamatanggota;
    private javax.swing.JTextField tfNIK;
    private javax.swing.JTextField tfNamaanggota;
    private javax.swing.JTextField tfNoanggotakoperasi;
    private javax.swing.JTextField tfNohpanggota;
    private javax.swing.JTextField tfTempattglanggota;
    private javax.swing.JTextField tfdivisi;
    // End of variables declaration//GEN-END:variables
}
