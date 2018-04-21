package io.kvineet.sysconfigurator.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.kvineet.sysconfigurator.ConnectionPool;
import io.kvineet.sysconfigurator.daos.SystemConfigDao;
import io.kvineet.sysconfigurator.models.Columns;
import io.kvineet.sysconfigurator.utils.EncryptionUtil;

@Singleton
public class BasicService {
	
	@Inject
	private SystemConfigDao basicDao;
	
	@Inject
	private ConnectionPool connectionPool;

	public List<Columns> listAllColumns(String tableName) {
		Connection conn = null;
		try {
			conn = connectionPool.getConnection();
			return basicDao.fetchColumns(tableName, conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			connectionPool.closeConnection(conn);
		}
		return new ArrayList<>();
	} 
	
	public String updateConfig(String tableName, List<Map<String, String>> dataSet, List<Map<String, String>> removedSet, List<Columns> columns)  {
		Connection conn = null;
		try {
			conn = connectionPool.getConnection();
			basicDao.insertOrUpdateData(tableName, dataSet, columns, conn);
			for(Map<String, String> data: removedSet) {
				basicDao.removeData(tableName, data, columns, conn);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			connectionPool.closeConnection(conn);
		}
		return "success";
	}

	public List<Map<String, String>> retriveData(String tableName, List<Columns> columns) {
		
		Connection conn = null;
		try {
			conn = connectionPool.getConnection();
			return basicDao.retriveData(tableName, columns, conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			connectionPool.closeConnection(conn);
		}
		return new ArrayList<>();
		
	}
	
	public void encryptData(String key, List<Map<String, String>> dataSet, List<Columns> columns){
		dataSet.forEach(e -> {
			columns.stream().filter(c -> c.isEncrypted()).forEach(c -> {
				String str = e.get(c.getName());
				String enc = EncryptionUtil.encrypt(str, key);
				e.put(c.getName(), enc);
			});
		});
	}
	
	public void decryptData(String key, List<Map<String, String>> dataSet, List<Columns> columns){
		dataSet.forEach(e -> {
			columns.stream().filter(c -> c.isEncrypted()).forEach(c -> {
				String str = e.get(c.getName());
				String enc = EncryptionUtil.decrypt(str, key);
				e.put(c.getName(), enc);
			});
		});
	}
	

}
