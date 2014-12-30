package ru.spb.iac.cud.uarm.web.context.reg;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.uarm.ejb.context.reg.UserRegEJB;
import ru.spb.iac.cud.uarm.ejb.entity.JournAppUserBssT;
import ru.spb.iac.cud.uarm.util.CUDUserConsoleConstants;
 
@ManagedBean(name="userRegBean")
@RequestScoped
 public class UserRegBean implements Serializable {
 
	private static final Logger LOGGER = LoggerFactory.getLogger(UserRegBean.class);
	
	private static final long serialVersionUID = 1L;
	   
    @EJB(beanName = "CUDUserConsole-ejb.jar#UserRegEJB")
	private UserRegEJB userRegEJB;
	
    private String orgName;
   
    private String depName;
    
    private String userPos;
    
    private String userFam;
 
    private String userName;
    
    private String userOtch;
    
    private String userPhone;
 
    private String userEmail;
    
    private static final String userEmailReg = "userEmailReg";
   
    public void action() {
        
    	try{
        
    	LOGGER.debug("UserRegBean:action:01");
        
        String version = FacesContext.class.getPackage().getImplementationVersion();
        
        LOGGER.debug("UserRegBean:action:02:"+version);
        
        HttpSession hs = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
		String email = (String) hs.getAttribute(CUDUserConsoleConstants.userEmailReg);
	
        
        JournAppUserBssT t1 = new JournAppUserBssT();
        
        t1.setSurnameUser(userFam);
        t1.setNameUser(userName);
 	    t1.setPatronymicUser(userOtch);
 	     t1.setEmailUser(email);
 	    t1.setPhoneUser(userPhone);
 	    t1.setNameOrg(orgName);
 	    t1.setNameDepartament(depName);
 	    t1.setPositionUser(userPos);
        
        userRegEJB.save(t1);
        
         FacesContext.getCurrentInstance().getExternalContext().redirect(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest())
        		.getContextPath()+"/context/registr/reg_user_step2_message.xhtml");
   
    	}catch(Exception e){
    		LOGGER.error("UserRegBean:action:error:"+e);
    	}
   }

   public void step1() {
        
     try{
        
    	LOGGER.debug("UserRegBean:step1:01");
        
        String version = FacesContext.class.getPackage().getImplementationVersion();
        
        LOGGER.debug("UserRegBean:step1:02:"+version);
        
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	String context_url=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
		
    	userRegEJB.step1(this.userEmail, context_url);
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash()
             .put(userEmailReg, this.userEmail);
        
        FacesContext.getCurrentInstance().getExternalContext().redirect(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest())
        		.getContextPath()+"/context/registr/reg_user_step1_message.xhtml");
   
    	}catch(Exception e){
    		LOGGER.error("UserRegBean:step1:error:"+e);
    	}
    	}

	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getDepName() {
		return depName;
	}
	public void setDepName(String depName) {
		this.depName = depName;
	}

	public String getUserPos() {
		return userPos;
	}
	public void setUserPos(String userPos) {
		this.userPos = userPos;
	}

   	public String getUserPhone() {
		return userPhone;
	}
	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserFam() {
		return userFam;
	}
	public void setUserFam(String userFam) {
		this.userFam = userFam;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserOtch() {
		return userOtch;
	}
	public void setUserOtch(String userOtch) {
		this.userOtch = userOtch;
	}

}
