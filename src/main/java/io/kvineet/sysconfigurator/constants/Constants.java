package io.kvineet.sysconfigurator.constants;

import java.util.regex.Pattern;

public class Constants {

	public static final String TAG_LENGTH = "tagLength";

	public static final String TABLE_NAME = "tableName";

	public static final String DB_PASSWORD = "dbPassword";

	public static final String DB_URL = "dbUrl";

	public static final String DB_USER_NAME = "dbUserName";

	public static final String AES_KEY = "aesKey";

	public static final String SOMETHING_WENT_WRONG = "Something went wrong";

	public static final String ERROR_MSG = "Error: ";

	public static final String ALERT = "Alert";

	public static final String CONNECT = "Connect";

	public static final String DISCONNECT = "Disconnect";
	
	public static final String CONNECTED = "Connected";
	
	public static final String DISCONNECTED = "Disconnected";
	
	public static final int DEFAULT_SELECT = 1;

	public static final Integer DEFAULT_TAG_LENGTH = 128;
	
	public static final Pattern SERVER_NAME_PATTERN = Pattern.compile(".*//(.*)[?]*.*");

	public static final int MAX_RECENT_CONFIGS = 10;
}
