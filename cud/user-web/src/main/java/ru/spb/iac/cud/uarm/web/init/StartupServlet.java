package ru.spb.iac.cud.uarm.web.init;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mypackage.Configuration;

 
 



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
		
			String signRequired = context.getInitParameter("SIGN_REQUIRED"); 
			
			String samlRequestLoginUarm = context.getInitParameter("SAML_REQUEST_LOGIN_UARM"); 
			
			String samlRequestLogout = context.getInitParameter("SAML_REQUEST_LOGOUT"); 
			
            String samlAssertion = context.getInitParameter("SAML_ASSERTION"); 
			
			String storePath = context.getInitParameter("STORE_PATH"); 
			
            String auditService = context.getInitParameter("AUDIT_SERVICE");
			
			String stsOboService = context.getInitParameter("STS_OBO_SERVICE");
			
			String stsService = context.getInitParameter("STS_SERVICE");
		
			
			Configuration.setSignRequired(
						!"FALSE".equals(signRequired)&&!"false".equals(signRequired));
		
		
			Configuration.setSamlRequestLoginUarm(samlRequestLoginUarm);
			Configuration.setSamlRequestLogout(samlRequestLogout);
			Configuration.setSamlAssertion(samlAssertion);
			Configuration.setStorePath(storePath);
			Configuration.setAuditService(auditService);
			Configuration.setStsOboService(stsOboService);
			Configuration.setStsService(stsService);
			
		} catch (Exception e) {
			LOGGER.error("init:error:", e);
		}
	}

}
