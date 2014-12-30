package ru.spb.iac.cud.services.web.init;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.context.ContextAccessSTSManager;
import ru.spb.iac.cud.context.ContextIDPAccessManager;
import ru.spb.iac.cud.context.ContextIDPUtilManager;

/**
 * Servlet implementation class StartupServlet
 */
 public class StartupServlet extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(StartupServlet.class);

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public StartupServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		try {
			ServletContext context = config.getServletContext();
			String jboss_jndi_port = context
					.getInitParameter("JBOSS_JNDI_PORT");
			ContextIDPUtilManager.initContext(jboss_jndi_port);
			ContextAccessSTSManager.initContext(jboss_jndi_port);
			ContextIDPAccessManager.initContext(jboss_jndi_port);

			
			String signRequired = context.getInitParameter("SIGN_REQUIRED"); 
			
			Configuration.setSignRequired(
					!"FALSE".equals(signRequired)&&!"false".equals(signRequired));
			
		} catch (Exception e) {
			LOGGER.error("init:error:", e);
		}
	}

}
