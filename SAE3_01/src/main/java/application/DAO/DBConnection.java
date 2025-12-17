package application.DAO;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {

    private static final HikariDataSource dataSource;
    private static final String userName = "root";
    private static final String password = "";
    private static final String serverName = "127.0.0.1";
    private static final String portNumber = "3306";
    private static final String dbname = "sae_taches";

    static {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + serverName + ":" + portNumber + "/" + dbname);
        config.setUsername(userName);
        config.setPassword(password);

        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}