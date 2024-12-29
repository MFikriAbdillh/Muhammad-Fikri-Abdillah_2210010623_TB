
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Pongo
 */
public class koneksiDB {

    public static Connection getConnection() throws SQLException {
    String DB = "jdbc:mysql://localhost:3306/db_pengelolaankrs"; // URL database
    String user = "root"; // Nama pengguna database
    String pass = ""; // Kata sandi database
    DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver()); // Driver untuk MySQL
    return DriverManager.getConnection(DB, user, pass);
}

    // Metode main untuk pengujian koneksi
    public static void main(String[] args) {
        try {
            getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }  
}
