package io.kvineet.sysconfigurator.models;

import java.io.Serializable;

public class DbConfig implements Serializable {

  private static final long serialVersionUID = -123513623735607121L;
	
  private String poolName;
  private int maximumPoolSize;
  private int minimumIdle;
  private String jdbcUrl;
  private String dbUserName;
  private String dbPassword;
  private boolean cachePrepStmts;
  private int prepStmtCacheSize;
  private int prepStmtCacheSqlLimit;
  private boolean useServerPrepStmts;

  public String getPoolName() {
    return poolName;
  }

  public void setPoolName(String poolName) {
    this.poolName = poolName;
  }

  public int getMaximumPoolSize() {
    return maximumPoolSize;
  }

  public void setMaximumPoolSize(int maximumPoolSize) {
    this.maximumPoolSize = maximumPoolSize;
  }

  public int getMinimumIdle() {
    return minimumIdle;
  }

  public void setMinimumIdle(int minimumIdle) {
    this.minimumIdle = minimumIdle;
  }

  public String getJdbcUrl() {
    return jdbcUrl;
  }

  public void setJdbcUrl(String jdbcUrl) {
    this.jdbcUrl = jdbcUrl;
  }

  public String getDbUserName() {
    return dbUserName;
  }

  public void setDbUserName(String dbUserName) {
    this.dbUserName = dbUserName;
  }

  public String getDbPassword() {
    return dbPassword;
  }

  public void setDbPassword(String dbPassword) {
    this.dbPassword = dbPassword;
  }

  public boolean isCachePrepStmts() {
    return cachePrepStmts;
  }

  public void setCachePrepStmts(boolean cachePrepStmts) {
    this.cachePrepStmts = cachePrepStmts;
  }

  public int getPrepStmtCacheSize() {
    return prepStmtCacheSize;
  }

  public void setPrepStmtCacheSize(int prepStmtCacheSize) {
    this.prepStmtCacheSize = prepStmtCacheSize;
  }

  public int getPrepStmtCacheSqlLimit() {
    return prepStmtCacheSqlLimit;
  }

  public void setPrepStmtCacheSqlLimit(int prepStmtCacheSqlLimit) {
    this.prepStmtCacheSqlLimit = prepStmtCacheSqlLimit;
  }

  public boolean isUseServerPrepStmts() {
    return useServerPrepStmts;
  }

  public void setUseServerPrepStmts(boolean useServerPrepStmts) {
    this.useServerPrepStmts = useServerPrepStmts;
  }

}
