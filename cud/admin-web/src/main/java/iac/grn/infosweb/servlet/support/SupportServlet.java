package iac.grn.infosweb.servlet.support;

import iac.grn.infosweb.session.support.SupportManager;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.servlet.ContextualHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 public class SupportServlet extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(SupportServlet.class);

	private static final long serialVersionUID = 1L;

	private String helpFio = null, helpPost = null, helpMail = null,
			helpText = null, helpTel = null;

	public SupportServlet() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		LOGGER.debug("SupportServlet:init:01");
	}

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		LOGGER.debug("SupportServlet:service:01");

		final HttpServletRequest f_request = request;
		final HttpServletResponse f_response = response;

		try {
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<html>");
			out.println("<head>");
			out.println("<title>SupportServlet</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<h1>SupportServlet</h1>");
			out.println("</body>");
			out.println("<script>");
			out.println("window.close();");
			out.println("</script>");
			out.println("</html>");

			helpFio = request.getParameter("help_fio");
			helpPost = request.getParameter("help_post");
			helpMail = request.getParameter("help_mail");
			helpText = request.getParameter("help_text");
			helpTel = request.getParameter("help_tel");

			LOGGER.debug("SupportServlet:service:help_fio:" + helpFio);
			LOGGER.debug("SupportServlet:service:help_post:" + helpPost);
			LOGGER.debug("SupportServlet:service:help_mail:" + helpMail);
			LOGGER.debug("SupportServlet:service:help_text:" + helpText);
			LOGGER.debug("SupportServlet:service:help_texl:" + helpTel);

			new ContextualHttpServletRequest(request) {
				@Override
				public void process() throws Exception {
					doWork(f_request, f_response);
				}
			}.run();

		} catch (Exception e) {
			LOGGER.error("SupportServlet:service:ERROR:", e);
		}
		LOGGER.debug("SupportServlet:service:02");
	}

	private void doWork(HttpServletRequest request, HttpServletResponse response) {
		SupportManager sm = (SupportManager) Component.getInstance(
				"supportManager", ScopeType.EVENT);
		sm.sendMail(helpFio, helpPost, helpMail, helpText, helpTel);

	}
	
}
