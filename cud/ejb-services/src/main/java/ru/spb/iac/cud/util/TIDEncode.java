package ru.spb.iac.cud.util;

import java.util.Random;

import org.apache.xml.security.utils.Base64;

/**
 * Класс для генерации случайных значений и перевод их в Base64 формат
 * @author bubnov
 *
 */
 public class TIDEncode {

	/**
	 * генерация случайных значений и перевод их в Base64 формат
	 */
	public static String getSecret() {

		Random random = new Random();

		byte[] keyRandom = new byte[3];
		random.nextBytes(keyRandom);

		String key = Base64.encode(keyRandom);
		
		key += Long.toString(System.currentTimeMillis());

		String result = Base64.encode(key.getBytes());
	
		return result;
	}


}
