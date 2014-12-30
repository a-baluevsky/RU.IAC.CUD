package ru.spb.iac.cud.services.web.init;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 public class StartupServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(StartupServlet.class);

	public StartupServlet() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		try {

			

			ServletContext context = config.getServletContext();
				
            String signRequired = context.getInitParameter("SIGN_REQUIRED"); 
			
			Configuration.setSignRequired(
					!"FALSE".equals(signRequired)&&!"false".equals(signRequired));
	

		} catch (Exception e) {
			LOGGER.error("init:error:", e);
		}
	}

}
