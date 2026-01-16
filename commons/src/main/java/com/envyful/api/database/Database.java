package com.envyful.api.database;

import com.envyful.api.database.sql.UtilSql;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 *
 * Interface representing a database connection
 *
 */
public interface Database extends DataSource {

    /**
     *
     * Gets the SQL connection
     *
     * @return An SQL Connection
     * @throws SQLException An error if there's no connections
     * @throws UnsupportedOperationException If this isn't an SQL database
     */
    default Connection getConnection()
            throws SQLException,UnsupportedOperationException {
        throw new UnsupportedOperationException("Not an SQL database");
    }

    /**
     *
     * A shorthand method for creating a new query builder
     *
     * @param query The query
     * @return The builder
     * @param <T> The type of the query
     */
    default <T> UtilSql.QueryBuilder<T> query(String query) {
        return UtilSql.<T>query(this).query(query);
    }

    /**
     *
     * A shorthand method for creating a new update builder
     *
     * @param query The query
     * @return The builder
     */
    default UtilSql.UpdateBuilder update(String query) {
        return UtilSql.update(this).query(query);
    }

    /**
     *
     * A shorthand method for creating a new batch update builder
     *
     * @param query The query
     * @return The builder
     * @param <T> The type of the query
     */
    default <T> UtilSql.BatchUpdateBuilder<T> batch(String query) {
        return UtilSql.<T>batchUpdate().database(this).query(query);
    }

    /**
     *
     * Gets the Jedis connection from the database if available
     *
     * @return The jedis connection
     * @throws UnsupportedOperationException If this database isn't redis lol
     */
    default StatefulRedisConnection<String, String> getRedis()
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not a redis database");
    }

    /**
     *
     * Gets the client
     *
     * @return The client
     * @throws UnsupportedOperationException If this database isn't redis
     */
    default RedisClient getClient() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not a redis database");
    }

    /**
     *
     * Gets the URI
     *
     * @return The URI
     * @throws UnsupportedOperationException If this database isn't redis
     */
    default RedisURI getURI() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not a redis database");
    }

    /**
     *
     * Subscribes the given object using the annotations
     *
     * @param o The object
     * @throws UnsupportedOperationException If this database isn't redis
     */
    default void subscribe(Object o) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not a redis database");
    }

    /**
     *
     * Publishes a message to the redis over the specified channel
     *
     * @param channel The channel
     * @param message The message
     * @throws UnsupportedOperationException If this database isn't redis
     */
    default void publish(String channel, String message)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not a redis database");
    }

    /**
     *
     * Closes the connection
     *
     */
    void close();

    /**
     *
     * SQL DataSource methods
     *
     */

    @Override
    default Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException("Not an SQL database");
    }

    @Override
    default PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException("Not an SQL database");
    }

    @Override
    default void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException("Not an SQL database");
    }

    @Override
    default void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException("Not an SQL database");
    }

    @Override
    default int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException("Not an SQL database");
    }

    @Override
    default Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("Not an SQL database");
    }

    @Override
    default <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not an SQL database");
    }

    @Override
    default boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not an SQL database");
    }
}
