package ru.spb.iac.cud.uarm.web.context.forgot;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.uarm.util.CUDUserConsoleConstants;
 
@WebServlet(value="/userForgotServlet")
 public class UserForgotServlet extends HttpServlet {
 
  private static final Logger LOGGER = LoggerFactory.getLogger(UserForgotServlet.class);
	
   private static final long serialVersionUID = 1L;
 
   public UserForgotServlet() {
        super();
   }

  

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		LOGGER.debug("UserForgotServlet:service:01");
		boolean success = false;
		String email = null;
		String login = null;
		
		try{
		
			email = request.getParameter("email");
			login = request.getParameter("login");
			
			String validationKey = request.getParameter("validationKey");
			
			if(email!=null&&!email.trim().isEmpty()&&
			   validationKey!=null&&!validationKey.trim().isEmpty()){
				
				validationKey = URLDecoder.decode(validationKey, "UTF-8");
				email = URLDecoder.decode(email, "UTF-8");
				
				
				String validationKeyTrue = (new BigInteger((email+login).getBytes("utf-8"))).toString(16);
			
				LOGGER.debug("UserForgotServlet:service:02:"+validationKeyTrue);
				
				if(validationKey.equals(validationKeyTrue)){
					success = true;
				}
			}
			
	    }catch(Exception e){
	    	LOGGER.error("UserForgotServlet:service:error:"+e);
	    }
		
		if(success){
			//!!!
			HttpSession hs = (HttpSession) request.getSession(true); 
			LOGGER.debug("UserForgotServlet:service:03:"+hs.getId());
			hs.setAttribute(CUDUserConsoleConstants.userLoginForgot, login);
		
			response.sendRedirect(request.getContextPath()+"/context/forgot/pass_step2.xhtml");
		}else{
			response.sendRedirect(request.getContextPath()+"/welcome.xhtml");
		}
	
	}

	

	
}
