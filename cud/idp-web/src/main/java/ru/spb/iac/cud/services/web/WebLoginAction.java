package ru.spb.iac.cud.services.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Session;
import org.jboss.as.web.security.SecurityContextAssociationValve;
import org.picketlink.common.constants.GeneralConstants;
import org.picketlink.identity.federation.api.saml.v2.request.SAML2Request;
import org.picketlink.identity.federation.core.saml.v2.common.SAMLDocumentHolder;
import org.picketlink.identity.federation.saml.v2.protocol.AuthnRequestType;
import org.picketlink.identity.federation.web.util.PostBindingUtil;
import org.picketlink.identity.federation.web.util.RedirectBindingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.context.ContextAccessWebManager;
import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.exceptions.InvalidCredentials;
import ru.spb.iac.cud.items.AuthMode;

/**
 * Servlet implementation class AccessServicesWeb
 */
 public class WebLoginAction extends HttpServlet {
	
   private static final long serialVersionUID = 1L;

   private static final Logger LOGGER = LoggerFactory.getLogger(WebLoginAction.class);
   
   public WebLoginAction() {
        super();
     }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String backUrl = null, forceBack=null;
		String success = "false";
		String login=null;
		String password=null;
		String repeatLoginUrl=null;
		String pswitch = null;
		 
		String loginUser = null;
		
		try{
			backUrl = request.getParameter("backUrl");
			login = request.getParameter("login");
			password = request.getParameter("password");
			
			forceBack = request.getParameter("forceBack");
			
			pswitch = request.getParameter("switch");
			
				
			if(login!=null && password!=null){
				
		       loginUser = (new ContextAccessWebManager())
					  .authenticate_login(login, password, AuthMode.HTTP_REDIRECT, getIPAddress(request),
							  getCodeSystem(request));
			
			  success="true"; 
			
			
			  
			}
		}catch(InvalidCredentials e1){
	 	    LOGGER.error("error1:"+e1.getMessage());
	 	}catch(GeneralFailure e2){
	     	LOGGER.error("error2:"+e2.getMessage());
	    }catch(Exception e3){
		    LOGGER.error("error3:"+e3.getMessage());
		}
		
		LOGGER.debug("service:03");
		
		 if("true".equals(success)){
			 
			
			request.getSession().setAttribute("cud_auth_type", "urn:oasis:names:tc:SAML:2.0:ac:classes:password");
				
			request.getSession().setAttribute("authenticate", "success");
			
			response.sendRedirect(request.getContextPath()+"/");
			 
				    
			request.getSession().setAttribute("login_user", loginUser);
			    
		 }else{
			
			 
			 
			 if(forceBack==null){//здесь в режиме использования своей формы логин/пароль
			 
				 LOGGER.debug("service:06");
				 
				 if(backUrl!=null){
					if(pswitch==null||!pswitch.equals("false")){ 
		  	          repeatLoginUrl = request.getContextPath()+"/AccessServicesWebLogin?success=false&backUrl="+backUrl;
					}else{
					  repeatLoginUrl = request.getContextPath()+"/AccessServicesWebLogin?switch=false&success=false&backUrl="+backUrl;	
					}
				 }else{
					repeatLoginUrl = request.getContextPath()+"/AccessServicesWebLogin?success=false";
				 }
				 
				common(response);
		  	    response.sendRedirect(repeatLoginUrl);
			
		  	    
			}
		 }
		 
	}

	
	
	
	
	 private String getIPAddress(HttpServletRequest request){
	        
	        String ipAddress = request.getRemoteAddr(); 
	         
	    return ipAddress;
	 }
	
	 private static void common(HttpServletResponse response) {
		    response.setCharacterEncoding("UTF-8");
		    response.setHeader("Pragma", "no-cache");
		    response.setHeader("Cache-Control", "no-cache, no-store");
	 }
	 
	 private String getCodeSystem(HttpServletRequest request){
		  
		//!!!
		//для метода важно, что в ExtFilter при /if(...||/requestURI./endsWith(login_to_auth)...)
		//идёт установка /request2./getSessionInternal().setNote(/GeneralConstants/.SAML_REQUEST_KEY, request/.getParameter(SAMLMessageKey))
		//и request./getSession()/.setAttribute(/"incoming_http_method", /request./getParameter(HTTPMethodKey))

		 
		 LOGGER.debug("getCodeSystem:031");
		 String result=null;
		 
		 try{
	             org.apache.catalina.connector.Request request2=null;
				 request2 = SecurityContextAssociationValve.getActiveRequest();
				 Session session = request2.getSessionInternal(false);
				 
				 //"SAMLRequest"
				 String samlRequestMessage = (String)session.getNote(GeneralConstants.SAML_REQUEST_KEY);
				 
				 if(samlRequestMessage!=null){
				 
					 
				 boolean begin_req_method = "GET".equals((String)request.getSession().getAttribute("incoming_http_method"));	
					
				 SAMLDocumentHolder samlDocumentHolder = getSAMLDocumentHolder(samlRequestMessage, begin_req_method);
	             
				 if(samlDocumentHolder!=null){
				 
				 if(samlDocumentHolder.getSamlObject()!=null){
					
					 AuthnRequestType requestAbstractType = (AuthnRequestType)samlDocumentHolder.getSamlObject();
					  result = requestAbstractType.getIssuer().getValue();

				      LOGGER.debug("getCodeSystem:032:"+result);
				      
				     }
				 }
				 
			   }
					 
		 }catch(Exception e){
			 LOGGER.error("getCodeSystem:error:"+e);
		 }
		 
		 return result;
	 }
	 
	 public SAMLDocumentHolder getSAMLDocumentHolder(String samlMessage, boolean redirectProfile) throws Exception
	  {
		LOGGER.debug("getSAMLDocumentHolder:01:"+redirectProfile);
		 
	    InputStream is = null;
	    SAML2Request saml2Request = new SAML2Request();
	    try
	    {
	      if (redirectProfile) {
	        is = RedirectBindingUtil.base64DeflateDecode(samlMessage);
	      } else {
	        byte[] samlBytes = PostBindingUtil.base64Decode(samlMessage);
	         is = new ByteArrayInputStream(samlBytes);
	      }
	    } catch (Exception rte) {
	      LOGGER.error("getSAMLDocumentHolder:error:"+rte);
	      throw rte;
	    }

	    saml2Request.getSAML2ObjectFromStream(is);

	    return saml2Request.getSamlDocumentHolder();
	  }
}
