package io.kvineet.sysconfigurator.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import io.kvineet.sysconfigurator.constants.Constants;

public class Configuration implements Serializable {

	private static final long serialVersionUID = -5646677666562657690L;

	private String tagLength;
	private String tableName;
	private String dbPassword;
	private String dbUrl;
	private String dbUserName;
	private String aesKey;

	public Configuration() {
		// Not Used
	}

	public Configuration(Map<String, String> data) {
		if (data != null) {
			aesKey = data.get(Constants.AES_KEY);
			dbUrl = data.get(Constants.DB_URL);
			dbUserName = data.get(Constants.DB_USER_NAME);
			dbPassword = data.get(Constants.DB_PASSWORD);
			tableName = data.get(Constants.TABLE_NAME);
			Integer tagL = null;
			try {
				tagL = Integer.parseInt(data.get(Constants.TAG_LENGTH));
			} catch (NumberFormatException e) {
				tagL = Constants.DEFAULT_TAG_LENGTH;
			}
			tagLength = String.valueOf(tagL);
		}
	}

	public String getTagLength() {
		return tagLength;
	}

	public void setTagLength(String tagLength) {
		this.tagLength = tagLength;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getDbUserName() {
		return dbUserName;
	}

	public void setDbUserName(String dbUserName) {
		this.dbUserName = dbUserName;
	}

	public String getAesKey() {
		return aesKey;
	}

	public void setAesKey(String aesKey) {
		this.aesKey = aesKey;
	}

	public Map<String, String> toMap() {

		Map<String, String> data = new HashMap<>();
		data.put(Constants.AES_KEY, aesKey);
		data.put(Constants.DB_URL, dbUrl);
		data.put(Constants.DB_USER_NAME, dbUserName);
		data.put(Constants.DB_PASSWORD, dbPassword);
		data.put(Constants.TABLE_NAME, tableName);
		data.put(Constants.TAG_LENGTH, tagLength);
		return data;
	}
	
	@Override
	public String toString() {
		
		String serverName = "No Server information.";
		try {		
			if (dbUrl != null && !dbUrl.isEmpty()) {			
				Matcher matcher = Constants.SERVER_NAME_PATTERN.matcher(dbUrl);
				if (matcher.matches()) {
					serverName = matcher.group(1);					
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return serverName.replace("/", " - ");
	}
}
