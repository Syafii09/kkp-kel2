/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package koneksi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author sabil
 */
public class Koneksi {
    private static final String URL = "jdbc:mysql://localhost:3306/koperasi_raya_abadi?useSSL=false&serverTimezone=Asia/Jakarta";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public Connection connect() {
        Connection connection = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Berhasil koneksi ke database");
        } catch (ClassNotFoundException ex) {
            System.out.println("Driver MySQL tidak ditemukan: " + ex.getMessage());
        } catch (SQLException ex) {
            System.out.println("Gagal koneksi ke database: " + ex.getMessage());
        }

        return connection;
    }

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Driver MySQL tidak ditemukan", ex);
        } catch (SQLException ex) {
            throw new RuntimeException("Gagal koneksi ke database", ex);
        }
    }

}
