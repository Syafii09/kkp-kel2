/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package app;

import utils.SesiLogin;
import view.Dashboard;
import view.DashboardAnggota;
import view.Login;

/**
 *
 * @author mac
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Login.applyLookAndFeel();
        java.awt.EventQueue.invokeLater(() -> {
            SesiLogin.SesiTersimpan sesi = SesiLogin.getInfoLoginTersimpan();
            if (sesi == null) {
                new Login().setVisible(true);
                return;
            }

            SesiLogin.masuk(sesi.idUser(), sesi.idAnggota(), sesi.nama(), sesi.group());
            if ("Pengguna".equalsIgnoreCase(sesi.group())) {
                if (sesi.idAnggota() == null) {
                    SesiLogin.hapusInfoLoginTersimpan();
                    SesiLogin.keluar();
                    new Login().setVisible(true);
                    return;
                }

                new DashboardAnggota(sesi.idAnggota(), sesi.nama()).setVisible(true);
                return;
            }

            new Dashboard(sesi.nama(), sesi.group()).setVisible(true);
        });
    }
    
}
