package ru.spb.iac.cud.uarm.web.context.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.uarm.util.CUDUserConsoleConstants;
 
@WebServlet(value="/authServlet")
 public class AuthServlet extends HttpServlet {
 

   private static final Logger LOGGER = LoggerFactory.getLogger(AuthServlet.class);
	
   private static final long serialVersionUID = 1L;
 
   public AuthServlet() {
        super();
   }

  

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		LOGGER.debug("AuthServlet:service:01");
		boolean success = false;
		String email = null;
				
		
		
		if(success){
			//!!!
			HttpSession hs = (HttpSession) request.getSession(true); 
			LOGGER.debug("AuthServlet:service:03:"+hs.getId());
			hs.setAttribute(CUDUserConsoleConstants.userEmailReg, email);
		
			response.sendRedirect(request.getContextPath()+"/context/registr/reg_user_step2.xhtml");
		}else{
			response.sendRedirect(request.getContextPath()+"/welcome.xhtml");
		}
	
	}

	

	
}
