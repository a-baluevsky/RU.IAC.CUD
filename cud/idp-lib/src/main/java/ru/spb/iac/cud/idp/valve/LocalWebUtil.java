package ru.spb.iac.cud.idp.valve;

 public class LocalWebUtil {

	public static String getTokenValue(String queryString, String token) {
		return getTokenValue(getToken(queryString, token));
	}

	private static String getToken(String queryString, String token) {
		if (queryString == null) {
			return null;
		}

		token += "=";

		int start = queryString.indexOf(token);
		if (start < 0) {
			return null;
		}

		int end = queryString.indexOf("&", start);
		//"&amp;"

		if (end == -1) {
			return queryString.substring(start);
		}

		return queryString.substring(start, end);
	}

	private static String getTokenValue(String token) {
		if (token == null) {
			return token;
		}

		int eq = token.indexOf('=');
		if (eq == -1) {
			return token;
		} else {
			return token.substring(eq + 1);
		}
	}
}
