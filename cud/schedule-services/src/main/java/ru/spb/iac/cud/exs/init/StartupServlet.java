package ru.spb.iac.cud.exs.init;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.exs.config.Configuration;
import ru.spb.iac.cud.exs.shedule.LaunchAuditFuncArchiveTask;
import ru.spb.iac.cud.exs.shedule.LaunchAuditSysArchiveTask;
import ru.spb.iac.cud.exs.shedule.LaunchBindingNoActTask;
import ru.spb.iac.cud.exs.shedule.LaunchBindingUnBindTask;
import ru.spb.iac.cud.exs.shedule.LaunchCRLTask;
import ru.spb.iac.cud.exs.shedule.LaunchUCCertTask;

/**
 * Servlet implementation class StartupServlet
 */
 public class StartupServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(StartupServlet.class);

	public StartupServlet() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {

		LOGGER.debug("init:01");
		try {
			LaunchCRLTask.initTask(25);
			LaunchAuditSysArchiveTask.initTask(30);
			LaunchAuditFuncArchiveTask.initTask(35);
			// архивирование токенов отключено
			LaunchBindingUnBindTask.initTask(45);
			LaunchBindingNoActTask.initTask(50);
			LaunchUCCertTask.initTask(55);

			ServletContext context = config.getServletContext();
			String crlReestr = context.getInitParameter("CRL_REESTR"); 
			
			Configuration.setCrlReestr(crlReestr);
			
			LOGGER.debug("StartupServlet:init:02");

				
		} catch (Exception e) {
			LOGGER.error("StartupServlet:init:error:", e);
		}
	}

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		LOGGER.debug("service:01");
	}

}
