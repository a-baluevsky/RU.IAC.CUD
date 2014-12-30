package ru.spb.iac.cud.uarm.web.context.forgot;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.exceptions.web.BaseError;
import ru.spb.iac.cud.items.CodesErrors;
import ru.spb.iac.cud.uarm.ejb.context.forgot.UserForgotEJB;
import ru.spb.iac.cud.uarm.util.CUDUserConsoleConstants;
 
@ManagedBean(name="userForgotBean")
@RequestScoped
 public class UserForgotBean implements Serializable {
 
	private static final Logger LOGGER = LoggerFactory.getLogger(UserForgotBean.class);
	
	private static final long serialVersionUID = 1L;
	   
    @EJB(beanName = "CUDUserConsole-ejb.jar#UserForgotEJB")
	private UserForgotEJB userForgotEJB;
	
    private String userLogin;
 
    private String userEmail;
    
    private static final String userEmailsList = "userEmailsList";
    
    private String newPass;
    private String reNewPass;
    
    public void action() {
        
    	try{
        
    	LOGGER.debug("UserForgotBean:action:01");
        
    	 String userNewPassword = newPass;
         
         String userReNewPassword = reNewPass;
		 
         LOGGER.debug("UserForgotBean:changePassword:03:"+userNewPassword);
         LOGGER.debug("UserForgotBean:changePassword:04:"+userReNewPassword);
         
         
         if(userNewPassword==null||userNewPassword.isEmpty()||
       	  userReNewPassword==null||userReNewPassword.isEmpty()){
       	  
       	   LOGGER.debug("UserForgotBean:changePassword:05");
       	  
       	  FacesContext.getCurrentInstance().addMessage(null, 
	        			new FacesMessage("Обязятельны все поля!"));
       	   return;
         }
         
         
         Boolean latin = Pattern.matches("[^а-яА-Я]*", userNewPassword);
       
         if(!latin){
       	  
       	  LOGGER.debug("UserForgotBean:changePassword:06");
       	  
       	  FacesContext.getCurrentInstance().addMessage(null, 
	        			new FacesMessage("В пароле не допустима кириллица!"));
       	   return;
         }
         
         if(!userNewPassword.equals(userReNewPassword)){
       	  
       	  LOGGER.debug("UserForgotBean:changePassword:07");
       	  
		      	FacesContext.getCurrentInstance().addMessage(null, 
	        			new FacesMessage("Пароли не совпадают!"));
	   	 }else{
         
			HttpSession hs = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
			String userLogin = (String) hs.getAttribute(CUDUserConsoleConstants.userLoginForgot);
				
			LOGGER.debug("UserForgotBean:changePassword:08:"+userLogin);
			
			userForgotEJB.changePassword(userLogin, userNewPassword);
			
			FacesContext
			.getCurrentInstance()
			.getExternalContext()
			.redirect(
					((HttpServletRequest) FacesContext
							.getCurrentInstance().getExternalContext()
							.getRequest()).getContextPath()
							+ "/welcome.xhtml");
         }
		 
         LOGGER.debug("UserForgotBean:changePassword:09");
      
    	}catch(Exception e){
    		LOGGER.error("UserForgotBean:action:error:"+e);
    	}
   }

   public void step1() {
        
	   //здесь в 2-х случаях:
	   //1) на первом шаге (this.userEmail = null)
	   //2) пользователь уточнил свой email (this.userEmail = уточнённый email)
     try{
        
    	LOGGER.debug("UserForgotBean:step1:01");
        
    	HttpSession hs = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
 		
    	//th/is.use/rLogin!=null при шаге1
    	//th/is.us/erLogin==null при уточнении пользователем email
    	String userLoginFact=(this.userLogin!=null?this.userLogin:(String)hs.getAttribute(CUDUserConsoleConstants.userLoginForgot));
    	
    	HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	String context_url=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
			
    	List<String> emails = userForgotEJB.step1(userLoginFact, this.userEmail, context_url);
        //при уточнении email, emails будет = этому уточненнному email  
    	
    	if(emails==null||emails.isEmpty()){
    		//у пользователя нет прикреплённых email
    		
    		LOGGER.debug("UserForgotBean:step1:02");
    		
    		  FacesContext.getCurrentInstance().getExternalContext().redirect(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest())
    	        		.getContextPath()+"/context/forgot/pass_step1_message_not_email.xhtml");
    	   
    	}else if(emails.size()>1){
    		//у пользователя не один email
    		//даём пользователю выбрать email
    		
    		LOGGER.debug("UserForgotBean:step1:03:"+emails.size());
    		
    		
    		 hs.setAttribute(userEmailsList, emails);
    		 
    		 hs.setAttribute(CUDUserConsoleConstants.userLoginForgot, this.userLogin);
    			
    		  FacesContext.getCurrentInstance().getExternalContext().redirect(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest())
  	        		.getContextPath()+"/context/forgot/pass_step1_message_more_email.xhtml");
  	
    		  
    	}else {
    		//нормальный вариант - один email
    		
    		LOGGER.debug("UserForgotBean:step1:04");
    		
    	    FacesContext.getCurrentInstance().getExternalContext().redirect(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest())
        		.getContextPath()+"/context/forgot/pass_step1_message.xhtml");
   
    	}
        
      }catch(BaseError be){
 		LOGGER.error("UserForgotBean:step1:berror:"+be);
 		
 		if(CodesErrors.NOT_FOUND.equals(be.getCodeError())){
 			//пользователь не определён
 			 try{
 				 
 			   FacesContext.getCurrentInstance().addMessage(null, 
 	        			new FacesMessage("Пользователь не определён!"));
 	      	
 			 }catch(Exception e){
 	    		LOGGER.error("UserForgotBean:step1:error2:"+e);
 	          }
 			
 		}
 		
 	  }catch(Exception e){
    		LOGGER.error("UserForgotBean:step1:error:"+e);
      }
     }



	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public String getNewPass() {
		return newPass;
	}

	public void setNewPass(String newPass) {
		this.newPass = newPass;
	}

	public String getReNewPass() {
		return reNewPass;
	}

	public void setReNewPass(String reNewPass) {
		this.reNewPass = reNewPass;
	}



}
