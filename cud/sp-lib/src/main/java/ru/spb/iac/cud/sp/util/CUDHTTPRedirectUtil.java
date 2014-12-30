package ru.spb.iac.cud.sp.util;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


 public class CUDHTTPRedirectUtil {
	/**
	 * Send the response to the redirected destination while adding the
	 * character encoding of "UTF-8" as well as adding headers for cache-control
	 * and Pragma
	 * 
	 * @param destination
	 *            Destination URI where the response needs to redirect
	 * @param response
	 *            HttpServletResponse
	 * @throws IOException
	 */
	public static void sendRedirectForRequestor(String destination,
			HttpServletResponse response) throws IOException {
		common(destination, response);
		response.setHeader("Cache-Control", "no-cache, no-store");
		sendRedirect(response, destination);
	}

	/**
	 * @see #sendRedirectForRequestor(String, HttpServletResponse)
	 */
	public static void sendRedirectForResponder(String destination,
			HttpServletResponse response) throws IOException {
		common(destination, response);
		response.setHeader("Cache-Control",
				"no-cache, no-store, must-revalidate,private");
		sendRedirect(response, destination);
	}

	private static void common(String destination, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Location", destination);
		response.setHeader("Pragma", "no-cache");
	}

	private static void sendRedirect(HttpServletResponse response,
			String destination) throws IOException {

		// !!!
		// response.reset();
		response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		response.sendRedirect(destination);
	}

}
