package ru.spb.iac.cud.services.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.as.web.security.SecurityContextAssociationValve;
import org.picketlink.common.constants.GeneralConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class AccessServicesWeb
 */
 public class AccessServicesWebLogin extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AccessServicesWebLogin.class);

	private static final long serialVersionUID = 1L;

	private static final String SAMLMessageKey = "CUD_SAML_MESSAGE";

	private static final String HTTPMethodKey = "CUD_HTTP_METHOD";

	private static final String RequestQueryStringKey = "CUD_REQUEST_QUERY_STRING";

	private static final String RequestRequestURIKey = "CUD_REQUEST_REQUEST_URI";

	public AccessServicesWebLogin() {
		super();
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String backUrl = null, login = null, password = null,  pswitch = null;
		String destination = "WebLoginAction";

		try {
			LOGGER.debug("service:01");

			org.apache.catalina.connector.Request request2 = null;
			request2 = SecurityContextAssociationValve.getActiveRequest();
			String samlRequestMessage = (String) request2.getSessionInternal()
					.getNote(GeneralConstants.SAML_REQUEST_KEY);
			String httpMethod = (String) request.getSession().getAttribute(
					"incoming_http_method");

			String multiAuthRequest = (String) request.getSession().getAttribute(
					"multi_auth_request");
			
			String RequestQueryString = (String) request.getSession()
					.getAttribute(RequestQueryStringKey);
			String RequestRequestURI = (String) request.getSession()
					.getAttribute(RequestRequestURIKey);

			backUrl = request.getParameter("backUrl");
			login = request.getParameter("login");
			password = request.getParameter("password");

			pswitch = request.getParameter("switch");

			// подумать - так как при проверке сессия создастся
			
			/*
			 * //не дойдёт до выполнения //перехватывается в фильтре //if
			 * requestURI/.endsWith(cert_to_form_ie) //if(principal2/!=null)/{
			 * 
			 * if(tokenID/_attr!=null&&/isTokenActive(tokenID_attr)){ //sso && не
			 * было logout/ sendPost/(backUrl, response, "true", /tokenID_attr);
			 * }else{
			 */
			

			if (login == null && password == null) {

				response.setContentType("text/html; charset=windows-1251");

				PrintWriter pw = response.getWriter();

				pw.print("<html>");
				pw.print("<HEAD>");
				pw.print("<link rel=\"stylesheet\" type=\"text/css\" href=\""
						+ request.getContextPath()
						+ "/stylesheet/theme.css\"/>");
				pw.print("</HEAD>");

				pw.print("<body>");

				pw.print("<table style=\"width:100%; height:100%;\">");
				pw.print("<tr>");
				pw.print("<td align=\"center\" valign=\"middle\">");

				pw.print("<div style=\"border:0; width:253px; height:150px\">");
				pw.print("<div style=\"border:1px solid silver; width:250px; height:130px\">");

				pw.print("<FORM METHOD=\"POST\" ACTION=\"" + destination
						+ "\">");

				if (backUrl != null) {
					pw.print("<INPUT TYPE=\"HIDDEN\" NAME=\"backUrl\" VALUE=\""
							+ backUrl + "\"/>");
				}

				if (samlRequestMessage != null && !samlRequestMessage.isEmpty()) {
					pw.print("<INPUT TYPE=\"HIDDEN\" NAME=\"" + SAMLMessageKey
							+ "\" VALUE=\"" + samlRequestMessage + "\"/>");
				}
				if (httpMethod != null && !httpMethod.isEmpty()) {
					pw.print("<INPUT TYPE=\"HIDDEN\" NAME=\"" + HTTPMethodKey
							+ "\" VALUE=\"" + httpMethod + "\"/>");
				}

				if (RequestQueryString != null && !RequestQueryString.isEmpty()) {
					pw.print("<INPUT TYPE=\"HIDDEN\" NAME=\""
							+ RequestQueryStringKey + "\" VALUE=\""
							+ RequestQueryString + "\"/>");
				}
				if (RequestRequestURI != null && !RequestRequestURI.isEmpty()) {
					pw.print("<INPUT TYPE=\"HIDDEN\" NAME=\""
							+ RequestRequestURIKey + "\" VALUE=\""
							+ RequestRequestURI + "\"/>");
				}

				if (pswitch == null || !"false".equals(pswitch)) {
					LOGGER.debug("service:pswitch");
				} else {
					pw.print("<INPUT TYPE=\"HIDDEN\" NAME=\"switch\" VALUE=\"false\"/>");
				}

				pw.print("<table width = \"220px\">");

				if (request.getParameter("success") != null
						&& "false".equals(request.getParameter("success"))) {
					pw.print("<tr>");
					pw.print("<td colspan=\"2\" align=\"center\" height = \"20px\" style=\"color:red;\" >");
					pw.print("Пользователь не идентифицирован!");
					pw.print("</td>");
					pw.print("</tr>");
				}
				pw.print("<tr>");
				pw.print("<td colspan=\"2\" align=\"center\" height = \"40px\" >");
				pw.print("Зарегистрируйтесь!");
				pw.print("</td>");
				pw.print("</tr>");
				pw.print("<tr>");
				pw.print("<td width = \"50px\">");
				pw.print("Логин:");
				pw.print("</td>");
				pw.print("<td>");
				pw.print("<input type=\"text\" NAME=\"login\" />");
				pw.print("</td>");
				pw.print("</tr>");
				pw.print("<tr>");
				pw.print("<td>");
				pw.print("Пароль:");
				pw.print("</td>");
				pw.print("<td>");
				pw.print("<input type=\"password\" NAME=\"password\" />");
				pw.print("</td>");
				pw.print("</tr>");
				pw.print("<tr>");

				pw.print("<td colspan=\"2\"  align=\"center\" height = \"30px\">");
				pw.print("<input type=\"submit\" value=\"Войти\" class=\"but_class\"/>");
				pw.print("</td>");

				
				pw.print("</tr>");
				
				pw.print("</table>");

				pw.print("</FORM>");

				pw.print("</div>");

				
				if (multiAuthRequest!=null) {
					
				    pw.print("<table width = \"220px\">");
				    pw.print("<tr>");
				    pw.print("<td colspan=\"2\" align=\"right\" >");
				     
				    pw.print("<a href=\""+request.getContextPath()+"/services/access_cert.jsp"+
			    		   "\" />Войти по сертификату</a>");
				    
				    pw.print("</td>");
			        pw.print("</tr>");
	   		        pw.print("</table>");
				}
				

				pw.print("</td>");
				pw.print("</tr>");
				pw.print("</table>");
				
				
				pw.print("</body>");
				pw.print("</html>");
				pw.close();

			} else { // новый режим - получаем от клиента логин/пароль
				String process = request.getContextPath()
						+ "/WebLoginAction?forceBack=true&backUrl=" + backUrl
						+ "&login=" + login + "&password=" + password;
				response.sendRedirect(process);
			}

			
		} catch (Exception e3) {
			LOGGER.error("error3:" + e3.getMessage());
		}

	}

	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}



}
