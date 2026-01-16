package com.envyful.api.database.impl;

import com.envyful.api.config.type.SQLDatabaseDetails;
import com.envyful.api.database.Database;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 *
 * Hikari SQL implementation of the {@link Database} interface
 *
 */
public class SimpleHikariDatabase implements Database {

    private final HikariDataSource hikari;

    public SimpleHikariDatabase(SQLDatabaseDetails details) {
        this(details.getConnectionUrl(),
                details.getPoolName(),
                details.getIp(),
                details.getPort(),
                details.getUsername(),
                details.getPassword(),
                details.getDatabase(),
                details.getMaxPoolSize(),
                details.getMaxLifeTimeSeconds(),
                details.isDisableSSL()
        );
    }

    public SimpleHikariDatabase(String name, String ip,
                                int port, String username,
                                String password, String database) {
        this(null, name, ip, port, username, password, database, 30, 30, false);
    }

    public SimpleHikariDatabase(String connectionUrl, String name,
                                String ip, int port, String username,
                                String password, String database,
                                int maxConnections, long maxLifeTime,
                                boolean disableSSL) {
        HikariConfig config = new HikariConfig();

        config.setMaximumPoolSize(Math.max(1, maxConnections));
        config.setPoolName(name);

        if (connectionUrl == null) {
            config.setJdbcUrl(
                    "jdbc:mysql://" + ip + ":" + port + "/" + database + "?noAccessToProcedureBodies=true"+ (disableSSL ? "&useSSL=false" : ""));
        } else {
            config.setJdbcUrl(connectionUrl);
        }

        config.addDataSourceProperty("serverName", ip);
        config.addDataSourceProperty("port", port);
        config.addDataSourceProperty("databaseName", database);
        config.addDataSourceProperty("user", username);
        config.addDataSourceProperty("password", password);
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("cacheCallableStmts", true);
        config.addDataSourceProperty("alwaysSendSetIsolation", false);
        config.addDataSourceProperty("cacheServerConfiguration", true);
        config.addDataSourceProperty("elideSetAutoCommits", true);
        config.addDataSourceProperty("useLocalSessionState", true);
        config.addDataSourceProperty("characterEncoding","utf8");
        config.addDataSourceProperty("useUnicode","true");
        config.addDataSourceProperty("maxLifetime",
                TimeUnit.SECONDS.toMillis(maxLifeTime));
        config.setMaxLifetime(TimeUnit.SECONDS.toMillis(maxLifeTime));
        config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(30));
        config.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(60));
        config.setConnectionTestQuery("/* Ping */ SELECT 1");

        this.hikari = new HikariDataSource(config);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.hikari.getConnection();
    }

    @Override
    public void close() {
        this.hikari.close();
    }


    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.hikari.getConnection(username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.hikari.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.hikari.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.hikari.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return this.hikari.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return this.hikari.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.hikari.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.hikari.isWrapperFor(iface);
    }
}
