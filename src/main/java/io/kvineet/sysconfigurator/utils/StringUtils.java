package io.kvineet.sysconfigurator.utils;

public class StringUtils {

	public static boolean isNullOrEmpty(String str) {
		if (str == null || str.trim().isEmpty()) {
			return true;
		}
		return false;
	}

}
