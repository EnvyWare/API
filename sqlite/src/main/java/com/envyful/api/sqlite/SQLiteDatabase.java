package com.envyful.api.sqlite;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.database.Database;
import com.envyful.api.database.sql.util.NonCloseableConnection;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 *
 * SQLite implementation of the {@link Database} interface
 *
 */
public class SQLiteDatabase implements Database {

    private final DataSource dataSource;
    private final NonCloseableConnection connection;

    public SQLiteDatabase(String filePath) throws SQLException {
        this.loadDriver();
        this.connection = new NonCloseableConnection(DriverManager.getConnection("jdbc:sqlite:" + filePath));
        this.dataSource = new SQLiteDataSource();
    }

    private void loadDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            UtilLogger.getLogger().error("Failed to load H2 driver");
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.connection;
    }

    @Override
    public void close() {
        try {
            this.connection.forceClose();
        } catch (SQLException e) {
            UtilLogger.getLogger().error("Failed to close H2 connection", e);
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.dataSource.getConnection(username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.dataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.dataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.dataSource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return this.dataSource.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return this.dataSource.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.dataSource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.dataSource.isWrapperFor(iface);
    }
}
