package io.kvineet.sysconfigurator.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.kvineet.sysconfigurator.models.Columns;
import io.kvineet.sysconfigurator.models.Columns.SortByOrder;

public class SystemConfigDao {

  public List<Columns> fetchColumns(String tableName, Connection conn) throws SQLException {
    String query =
        "select isc.column_name, isc.ORDINAL_POSITION, kc.constraint_name iS NOT NULL as is_primary_key\n"
        + "from information_schema.columns isc\n"
        + "left join information_schema.key_column_usage kc\n"
            + "on kc.table_name = isc.table_name and kc.table_schema = isc.table_schema and isc.column_name = kc.column_name\n"
        + "where isc.table_name = '" + tableName + "'\n"
        + "order by isc.ORDINAL_POSITION asc";
    System.out.println(query);

    PreparedStatement s = conn.prepareStatement(query);
    ResultSet rs = s.executeQuery();
    List<Columns> result = new ArrayList<>();
    while (rs.next()) {
      String colName = rs.getString(1);
      int ordinal = rs.getInt(2);
      boolean isPrimary = rs.getBoolean(3);

      Columns col = new Columns();
      col.setName(colName);
      col.setOrder(ordinal);
      col.setPrimaryKey(isPrimary);
      col.setEncrypted(!isPrimary);
      result.add(col);
    }
    return result;
  }

	public void insertOrUpdateData(String tableName, List<Map<String, String>> dataSet,
      List<Columns> columns, Connection conn) throws SQLException {
    if (dataSet.isEmpty()) {
      return;
    }
	
    String query = constructInsertOrUpdateQuery(tableName, dataSet, columns);
    System.out.println(query);
    Statement stmnt = conn.createStatement();
    stmnt.execute(query);
  }

  public String constructInsertOrUpdateQuery(String tableName, List<Map<String, String>> dataSet,
		List<Columns> columns) {
	List<String> pkeys = columns.stream().filter(Columns::isPrimaryKey).map(Columns::getName)
        .collect(Collectors.toList());
    List<String> cols = columns.stream().filter(e -> !e.isPrimaryKey()).map(Columns::getName)
        .collect(Collectors.toList());

    String query = "INSERT INTO " + tableName + "\n(\n"
        + columns.stream().map(e -> e.getName()).collect(Collectors.joining(", ")) + "\n)\n"
        + "VALUES\n"
        + dataSet.stream().map(e -> "(" + joinData(e, columns) + ")")
            .collect(Collectors.joining(", \n"))
        + "\n" + "ON CONFLICT(" + pkeys.stream().map(e -> e).collect(Collectors.joining(", "))
        + ")\n" + "DO UPDATE \n" + "SET \n"
        + cols.stream().map(e -> e + "= excluded." + e).collect(Collectors.joining(", \n"));
	return query;
  }

  private String joinData(Map<String, String> data, List<Columns> cols) {
    return cols.stream().map(e -> "\'" + escape(data.get(e.getName())) + "\'")
        .collect(Collectors.joining(", "));
  }

  private String escape(String string) {
    return StringUtils.replace(string, "'", "''");
  }

  public List<Map<String, String>> retriveData(String tableName, List<Columns> columns,
      Connection conn) throws SQLException {
    Collections.sort(columns, new SortByOrder());
    String query =
        "SELECT \n" + columns.stream().map(Columns::getName).collect(Collectors.joining(", "))
            + " FROM " + tableName + " ORDER BY "
            + columns.stream().filter(Columns::isPrimaryKey).map(Columns::getName).collect(Collectors.joining(", "))
            + " ASC";
    System.out.println("Query: " + query);
    PreparedStatement ps = conn.prepareStatement(query);
    ResultSet rs = ps.executeQuery();
    List<Map<String, String>> dataSet = new ArrayList<>();
    while (rs.next()) {
      Map<String, String> data = new HashMap<>();
      columns.forEach((ans) -> data.put(ans.getName(), extractColValue(rs, ans)));
      dataSet.add(data);
    }
    return dataSet;
  }

  private String extractColValue(ResultSet rs, Columns col) {
    String str = null;
    try {
      str = rs.getString(col.getName());
    } catch (SQLException e) {
      System.out.println("Failed to get column");
    }
    System.out.println("Col Val:" + str);
    return str;
  }

  public void removeData(String tableName, Map<String, String> data, List<Columns> columns,
      Connection conn) throws SQLException {
    List<String> pkeys = columns.stream().filter(e -> e.isPrimaryKey()).map(e -> e.getName())
        .collect(Collectors.toList());

    String query = "DELETE FROM " + tableName + " \n" + "WHERE \n" + pkeys.stream()
        .map(e -> e + " = \'" + escape(data.get(e)) + "\' ").collect(Collectors.joining("AND "));

    System.out.println(query);
    Statement stmnt = conn.createStatement();
    stmnt.execute(query);
  }
}
