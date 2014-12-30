package ru.spb.iac.cud.uarm.web.auth;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.uarm.util.CUDUserConsoleConstants;

/**
 * Servlet Filter implementation class AuthSessionFilter
 */
@WebFilter("*")
 public class AuthSessionFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthSessionFilter.class);
	
    /**
     * Default constructor. 
     */
    public AuthSessionFilter() {
    	LOGGER.debug("init");
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		LOGGER.debug("destroy");
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		// place your code here

		int flag_redirect=0;
		
		HttpServletRequest hsr = (HttpServletRequest)request;
		HttpServletResponse hsresp = (HttpServletResponse)response;
		
		String requestURI = hsr.getRequestURI();
		String contextPath = hsr.getContextPath();
		
		
		LOGGER.debug("AuthSessionFilter:doFilter:01:"+requestURI);
		
		if(
			!requestURI.endsWith(".js")&&
			!requestURI.endsWith(".js.xhtml")&&
			!requestURI.endsWith(".css")&&
			!requestURI.endsWith(".ecss")&&
			!requestURI.endsWith(".jpg")&&
			!requestURI.endsWith(".jpeg")&&
			!requestURI.endsWith(".png")&&
			!requestURI.endsWith(".gif")&&
				hsr.getSession().getAttribute(CUDUserConsoleConstants.authUserID)==null){
			
			if(!requestURI.endsWith("/welcome.xhtml")&&
			   !requestURI.endsWith("/public.xhtml")&&
			   !requestURI.endsWith("/login_obo.xhtml")&&
			   !requestURI.endsWith("/login_direct.xhtml")&&
			   !requestURI.endsWith(contextPath)&&
			   !requestURI.endsWith(contextPath+"/")&&
			   !requestURI.endsWith("/userRegServlet")&&
			   !requestURI.endsWith("/userForgotServlet")&&
			   !requestURI.startsWith(contextPath+"/context/registr/")&&
			   !requestURI.startsWith(contextPath+"/context/forgot/")){
				
						
				hsresp.sendRedirect(contextPath+"/welcome.xhtml?error=req_auth");
				
				
				flag_redirect=1;
			}
			
		}
		
		if(flag_redirect==0){
		  LOGGER.debug("AuthSessionFilter:doFilter:03");
		  chain.doFilter(request, response);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
