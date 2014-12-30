package iac.cud.infosweb.util;

import java.util.Random;

 public class BaseUtil {

	private static String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	public static String password() {
		 

		Random rnd = new Random();

		int len = 8;

		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		}
		return sb.toString();

	}
}
