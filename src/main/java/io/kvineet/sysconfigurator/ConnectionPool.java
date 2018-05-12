package io.kvineet.sysconfigurator;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.kvineet.sysconfigurator.models.DbConfig;

@Singleton
public class ConnectionPool {

  private DataSource datasource;

  @Inject
  private ConnectionPool() {}

  public void initConnection(DbConfig dbConfig) throws SQLException {
    if (datasource != null) {
      return;
    }
    if (dbConfig == null) {
      dbConfig = new DbConfig();
      dbConfig.setPoolName("test");
      dbConfig.setJdbcUrl("jdbc:postgresql://172.17.0.3:5432/postgres");
      dbConfig.setDbUserName("postgres");
      dbConfig.setDbPassword("postgres");
      dbConfig.setMaximumPoolSize(3);
      dbConfig.setMinimumIdle(1);
    }
    this.datasource = ConnectionPool.getDataSourceFromConfig(dbConfig);
  }

  public Connection getConnection() {
    try {
      return datasource.getConnection();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  public void closeConnection(Connection conn) {
    if (conn == null) {
      return;
    }
    try {
      conn.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /*
   * Expects a config in the following format
   *
   * poolName = "test pool" jdbcUrl = "" maximumPoolSize = 10 minimumIdle = username = "" password =
   * "" cachePrepStmts = true prepStmtCacheSize = 256 prepStmtCacheSqlLimit = 2048
   * useServerPrepStmts = true
   *
   * Let HikariCP bleed out here on purpose
   */
  public static HikariDataSource getDataSourceFromConfig(DbConfig dbConfig) {

    HikariConfig jdbcConfig = new HikariConfig();
    jdbcConfig.setPoolName(dbConfig.getPoolName());
    jdbcConfig.setMaximumPoolSize(dbConfig.getMaximumPoolSize());
    jdbcConfig.setMinimumIdle(dbConfig.getMinimumIdle());
    jdbcConfig.setJdbcUrl(dbConfig.getJdbcUrl());
    jdbcConfig.setUsername(dbConfig.getDbUserName());
    jdbcConfig.setPassword(dbConfig.getDbPassword());

    jdbcConfig.addDataSourceProperty("cachePrepStmts", dbConfig.isCachePrepStmts());
    jdbcConfig.addDataSourceProperty("prepStmtCacheSize", dbConfig.getPrepStmtCacheSize());
    jdbcConfig.addDataSourceProperty("prepStmtCacheSqlLimit", dbConfig.getPrepStmtCacheSqlLimit());
    jdbcConfig.addDataSourceProperty("useServerPrepStmts", dbConfig.isUseServerPrepStmts());

    return new HikariDataSource(jdbcConfig);
  }

  public void closeDataSource() throws SQLException {
    closeDataSource(this.datasource);
    this.datasource = null;
  }

  private static void closeDataSource(DataSource dataSource) throws SQLException {
    if (dataSource != null && HikariDataSource.class.equals(dataSource.getClass())) {
      dataSource.unwrap(HikariDataSource.class).close();
    }
  }


}

