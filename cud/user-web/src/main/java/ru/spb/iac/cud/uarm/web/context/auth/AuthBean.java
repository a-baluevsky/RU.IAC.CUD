package ru.spb.iac.cud.uarm.web.context.auth;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.uarm.ejb.context.auth.AuthEJB;
import ru.spb.iac.cud.uarm.ejb.entity.AcUsersKnlT;
import ru.spb.iac.cud.uarm.util.CUDUserConsoleConstants;
 
@ManagedBean(name="authBean")
@RequestScoped
 public class AuthBean implements Serializable {
 
	@EJB(beanName = "CUDUserConsole-ejb.jar#AuthEJB")
	private AuthEJB authEJB;
	
		
    private static final long serialVersionUID = 1L;
 
    private String userLogin;
   
    private String userPassword;
    
    private static final Logger LOGGER = LoggerFactory
			.getLogger(AuthBean.class);
    
    public void action() {
       
    	 
    	try{
        
    	LOGGER.debug("AuthBean:action:01");
        
        String version = FacesContext.class.getPackage().getImplementationVersion();
        
        LOGGER.debug("AuthBean:action:02:"+version);
        
        String redirectURL = "/context/roles_pages/role_user.xhtml";
        
        AcUsersKnlT user = authEJB.login(userLogin, userPassword);
        
        if(user==null){
        	
        	redirectURL = "/welcome.xhtml";
        	FacesContext.getCurrentInstance().addMessage(null, 
        			new FacesMessage("Пользователь не аутентифицирован!"));
        	FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            
        }else{
        	LOGGER.debug("AuthBean:action:03:"+user.getLogin());
         
        	HttpSession hs = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
         	hs.setAttribute(CUDUserConsoleConstants.authUserID, user.getIdSrv());
        }
        
         
          FacesContext.getCurrentInstance().getExternalContext().redirect(
        		((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest())
        		.getContextPath()+redirectURL);
   
    	}catch(Exception e){
    		LOGGER.error("AuthBean:action:error:"+e);
    	}
   }

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public void cudAuth(String typeAuth) {
	       
   	  try{
        
   		LOGGER.debug("cudAuth:01++:"+typeAuth);
        
    	  if(typeAuth==null){
    		  return;
    	  }
    	  
    	  HttpSession hs = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
          Long userID =(Long)hs.getAttribute(CUDUserConsoleConstants.authUserID);
     
          LOGGER.debug("AuthBean:cudAuth:02:"+userID);
          
          if(userID==null||
        		  !typeAuth.equals(hs.getAttribute(CUDUserConsoleConstants.authType))){
        	 
        	  authEJB.cudAuth(typeAuth);
        	  
          }else{
               
              String redirectURL = "/context/profile/info/list.xhtml?pageItem=profile_account";
         	  FacesContext.getCurrentInstance().getExternalContext().redirect(
 	               		((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest())
 	               		.getContextPath()+redirectURL);
          }
           
    	}catch(Exception e){
    		LOGGER.error("AuthBean:cudAuth:error:"+e);
    	}
    }
	
	public void cudAuthDirect(String typeAuthDirect) {
	       
	   	  try{
	        
	   		LOGGER.debug("cudAuthDirect:01++:"+typeAuthDirect);
	        
	    	  if(typeAuthDirect==null){
	    		  return;
	    	  }
	    	  
	         
	          authEJB.cudAuth(typeAuthDirect);
	        	  
	               
	    	}catch(Exception e){
	    		LOGGER.error("AuthBean:cudAuthDirect:error:"+e);
	    	}
	    }
	
	public void cudAuthOBO() {
	       
	   	  try{
	        
	    	  LOGGER.debug("AuthBean:cudAuthOBO:01");
	        
	    	  
	    	  HttpSession hs = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
	          Long userID =(Long)hs.getAttribute(CUDUserConsoleConstants.authUserID);
	     
	          LOGGER.debug("AuthBean:cudAuthOBO:02:"+userID);
	          
	          if(userID==null){
	        	 
	        	  authEJB.cudAuthOBO();
	        	  
	        	  authenticate();
	        	  
	          }else{
	               
	              String redirectURL = "/context/profile/info/list.xhtml?pageItem=profile_account";
	         	  FacesContext.getCurrentInstance().getExternalContext().redirect(
	 	               		((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest())
	 	               		.getContextPath()+redirectURL);
	          }
	           
	    	}catch(Exception e){
	    		LOGGER.error("AuthBean:cudAuthOBO:error:"+e);
	    	}
	    }
	public void authenticate() {
	       
	   	  try{
	        
	    	  LOGGER.debug("AuthBean:authenticate:01");
	        
	    	  String redirectURL = null;
	    	  
	           if(authEJB.authenticate()){
	        	   redirectURL = "/context/profile/info/list.xhtml?pageItem=profile_account";
	        	}else{
	        	   redirectURL = "/welcome.xhtml";
	            }
	           
	           FacesContext.getCurrentInstance().getExternalContext().redirect(
	               		((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest())
	               		.getContextPath()+redirectURL);
	           
	    	}catch(Exception e){
	    		LOGGER.error("AuthBean:authenticate:error:"+e);
	    	}
	    }
	
	public void logout() {
	       
	   	  try{
	        
	    	  LOGGER.debug("AuthBean:logout:01");
	    	  
	    	  authEJB.localLogout();
	    	 	           
	    	}catch(Exception e){
	    		LOGGER.error("AuthBean:logout:error:"+e);
	    	}
	    }
}
