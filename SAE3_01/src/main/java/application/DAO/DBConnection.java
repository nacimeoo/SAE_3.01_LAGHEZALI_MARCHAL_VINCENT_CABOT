package application.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private String userName = "root";
    private String password = "";
    private String serverName = "127.0.0.1";
    private String portNumber = "3306";
    private static String dbname = "sae_taches";
    public static Connection instance;
    private DBConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Properties connectionProps = new Properties();
        connectionProps.put("user", userName);
        connectionProps.put("password", password);
        String urlDB = "jdbc:mysql://" + serverName + ":";
        urlDB += portNumber + "/" + dbname;
        instance = DriverManager.getConnection(urlDB, connectionProps);
    }

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        if (instance == null) {
            new DBConnection();
        }
        return instance;
    }
}
