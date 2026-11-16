package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoPool {
    private static Connection instancia;

    public static Connection getConnection() throws SQLException {
        if (instancia == null || instancia.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL não encontrado.", e);
            }
            instancia = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/sentinel_net?useSSL=false&serverTimezone=UTC",
                    "root", ""
            );
        }
        return instancia;
    }
}