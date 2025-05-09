package LIBRARY;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DBConnection{
    protected Connection conn;

    public DBConnection() throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_db", "root", "");
    }

    public void closeConnection() throws SQLException {
        if (this.conn != null && !this.conn.isClosed()) {
            this.conn.close();
        }
    }
}
