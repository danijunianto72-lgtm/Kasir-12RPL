/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package kasir;
import javax.swing.*;
import java.sql.*;
import javax.swing.table.*;
import java.sql.SQLException;

public class koneksi{
private static Connection postgreKonek;
public static Connection dbKonek()throws SQLException {
if(postgreKonek == null || postgreKonek.isClosed()){
    try{
                DriverManager.registerDriver(new org.postgresql.Driver());

        String db = "jdbc:postgresql://localhost:5432/dbRestoran";
        String user = "postgres";
        String pass = "1234";
        postgreKonek = (Connection)DriverManager.getConnection(db,user,pass);
    }catch(Exception e)
    {
    e.printStackTrace();
    }
    
}return postgreKonek;
}
    
}
