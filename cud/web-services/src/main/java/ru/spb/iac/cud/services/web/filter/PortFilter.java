package ru.spb.iac.cud.services.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 public class PortFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(PortFilter.class);

	private List<String> available_ports;

	public PortFilter() {
		LOGGER.debug("init");
	}

	public void destroy() {
		LOGGER.debug("destroy");
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		boolean success = true;

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		LOGGER.debug("doFilter:01:" + available_ports);
		LOGGER.debug("doFilter:02:" + httpRequest.getServerPort());
		LOGGER.debug("doFilter:03:" + httpRequest.getServerName());
		LOGGER.debug("doFilter:012:" + httpRequest.getContextPath());

		if (available_ports != null) {

			if (!available_ports.contains(String.valueOf(httpRequest
					.getServerPort()))) {
				success = false;
			}
		}

		LOGGER.debug("doFilter:013:" + success);

		if (success) {
			chain.doFilter(request, response);
		} else {
			httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	/**
	 * 
	 */
	public void init(FilterConfig fConfig) throws ServletException {

		String param = fConfig.getInitParameter("available_ports");
		LOGGER.debug("doFilter:init:01:" + param);

		// если параметр не задан, или без значения - то доступно на всех портах

		if (param != null && !param.trim().isEmpty()) {
			available_ports = new ArrayList<String>(Arrays.asList(param
					.split(",")));
		}
	}

}
