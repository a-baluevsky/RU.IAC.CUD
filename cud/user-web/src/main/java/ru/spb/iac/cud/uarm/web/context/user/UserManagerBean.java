package ru.spb.iac.cud.uarm.web.context.user;


import iac.cud.infosweb.dataitems.BaseItem;

import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap; import java.util.Map;
import java.util.List;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import ru.spb.iac.cud.uarm.ejb.context.user.UserManagerEJB;
import ru.spb.iac.cud.uarm.ejb.entity.AcIsBssT;
import ru.spb.iac.cud.uarm.ejb.entity.AcRolesBssT;
import ru.spb.iac.cud.uarm.ejb.entity.AcUsersCertBssT;
import ru.spb.iac.cud.uarm.ejb.entity.AcUsersKnlT;
import ru.spb.iac.cud.uarm.ejb.entity.GroupUsersKnlT;
import ru.spb.iac.cud.uarm.ejb.entity.JournAppAccessBssT;
import ru.spb.iac.cud.uarm.ejb.entity.JournAppAdminUserSysBssT;
import ru.spb.iac.cud.uarm.ejb.entity.JournAppOrgManagerBssT;
import ru.spb.iac.cud.uarm.util.CUDUserConsoleConstants;
import org.apache.xml.security.utils.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ManagedBean(name="userManagerBean")
@RequestScoped
 public class UserManagerBean implements Serializable {
 
	private static final Logger LOGGER = LoggerFactory.getLogger(UserManagerBean.class);
	
	@EJB(beanName = "CUDUserConsole-ejb.jar#UserManagerEJB")
	private UserManagerEJB userManagerEJB;
	
	@ManagedProperty("#{userSessionBean}")
	private UserSessionBean userSessionBean;
		
    private static final long serialVersionUID = 1L;
 
    private static final Long CUD_ID = 1L;
    
    private AcUsersKnlT user;

    private List<AcIsBssT> listArm;
    
    private List<AcIsBssT> listArmFull;
    
    private List<AcRolesBssT> listRolesFromArm;
    
    private List<GroupUsersKnlT> listGroupsFromArm;
    
    private List<JournAppAccessBssT> listAppAccess;
    
    private List<JournAppAccessBssT> listAppAccessGroups;
    
    private List<JournAppAdminUserSysBssT> listAppAdminUserSys;
    
    private List<JournAppOrgManagerBssT> listAppOrgMan;
        
    private List<AcRolesBssT> listRolesAdminSys;
    
    private Boolean renderedRolesTable;
    
    private Boolean renderedGroupsTable;
    
    private List<String> sumRoles;
    
    private List<String> sumGroups;
    
    private Part certFile; 
    
    private List<GroupUsersKnlT> userGroups;
    
    private List<BaseItem> userCertList;
    
	public AcUsersKnlT getUser() {
		
		if(this.user==null){
			
			
			HttpSession hs = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
			Long authUserID = (Long) hs.getAttribute(CUDUserConsoleConstants.authUserID);
			
         	
			this.user =userManagerEJB.getUserItem(authUserID);
         	
			
			if(this.user!=null){
			  LOGGER.debug("UserManagerBean:getUser:01:"+this.user.getLogin());
			}
			
		}
		return user;
	}

	public void setUser(AcUsersKnlT user) {
		this.user = user;
	}
	
	public void loadUser() {
		this.user = user;
	}

	public List<AcIsBssT> getListArm() {
		
		HttpSession hs = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
		Long authUserID = (Long) hs.getAttribute(CUDUserConsoleConstants.authUserID);
		
		LOGGER.debug("UserManagerBean:getListArm:01:"+authUserID);
		
		if(this.listArm==null){
			this.listArm =userManagerEJB.getUserRoles(authUserID);
		}
		
		if(this.listArm!=null){
			LOGGER.debug("UserManagerBean:getListArm:01:"+this.listArm.size());
		}
		
		return listArm;
	}

	public void setListArm(List<AcIsBssT> listArm) {
		this.listArm = listArm;
	}

	public List<AcIsBssT> getListArmFull() {
		
		LOGGER.debug("UserManagerBean:getListArmFull:01");
		
		
		String requestType = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
			     .get("requestType");
		String onArmSelectOpen = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
			     .get("onArmSelectOpen");
		
		LOGGER.debug("UserManagerBean:getListArmFull:01_1:"+requestType);
		LOGGER.debug("UserManagerBean:getListArmFull:01_2:"+onArmSelectOpen);
		
		if(this.listArmFull==null){
				this.listArmFull =userManagerEJB.getArmList();
		}
		
		if(this.listArmFull!=null){
			LOGGER.debug("UserManagerBean:getListArmFull:02:"+this.listArmFull.size());
		}
		
		return listArmFull;
	}

	public void setListArmFull(List<AcIsBssT> listArmFull) {
		this.listArmFull = listArmFull;
	}

	public List<JournAppAccessBssT> getListAppAccess() {
		
	    LOGGER.debug("UserManagerBean:getListAppAccess:01");
		
		if(listAppAccess==null){
			
			HttpSession hs = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
			Long authUserID = (Long) hs.getAttribute(CUDUserConsoleConstants.authUserID);
			
			LOGGER.debug("UserManagerBean:getListAppAccess:02:"+authUserID);
		
			this.listAppAccess =userManagerEJB.getAppAccessList(authUserID);
		}
		
		return listAppAccess;
	}

	public void setListAppAccess(List<JournAppAccessBssT> listAppAccess) {
		this.listAppAccess = listAppAccess;
	}

    public List<JournAppAccessBssT> getListAppAccessGroups() {
		
	    LOGGER.debug("UserManagerBean:getListAppAccessGroups:01");
		
		if(listAppAccessGroups==null){
			
			HttpSession hs = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
			Long authUserID = (Long) hs.getAttribute(CUDUserConsoleConstants.authUserID);
			
			LOGGER.debug("UserManagerBean:getListAppAccessGroups:02:"+authUserID);
		
			this.listAppAccessGroups =userManagerEJB.getAppAccessGroupsList(authUserID);
		}
		
		return listAppAccessGroups;
	}

	public void setListAppAccessGroups(List<JournAppAccessBssT> listAppAccessGroups) {
		this.listAppAccessGroups = listAppAccessGroups;
	}
	
	public List<AcRolesBssT> getListRolesFromArm() {
		
		if(listRolesFromArm==null){
			
			String pidArm = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					     .get("idArm");
			
			LOGGER.debug("UserManagerBean:getListRolesFromArm:01:"+pidArm);
			
			listRolesFromArm = userManagerEJB.getListRolesFromArm(new Long(pidArm));
			
			if(userSessionBean.getSumRoles()!=null&&this.listRolesFromArm!=null){
				
				for(AcRolesBssT role : listRolesFromArm){
					
					if(userSessionBean.getSumRoles().containsKey(role.getIdSrv())){
						role.setChecked(true);
					}
				}
				
			}
			
		}
		
		return listRolesFromArm;
	}

	public void setListRolesFromArm(List<AcRolesBssT> listRolesFromArm) {
		this.listRolesFromArm = listRolesFromArm;
	}
   
   public List<GroupUsersKnlT> getListGroupsFromArm() {
		
		if(listGroupsFromArm==null){
			
			String pidArm = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					     .get("idArm");
			
			LOGGER.debug("UserManagerBean:getListGroupsFromArm:01:"+pidArm);
			
			listGroupsFromArm = userManagerEJB.getListGroupsFromArm(new Long(pidArm));
			
			if(userSessionBean.getSumGroups()!=null&&this.listGroupsFromArm!=null){
				
				for(GroupUsersKnlT group : listGroupsFromArm){
					
					if(userSessionBean.getSumGroups().containsKey(group.getIdSrv())){
						group.setChecked(true);
					}
				}
				
			}
			
		}
		
		return listGroupsFromArm;
	}

	public void setListGroupsFromArm(List<GroupUsersKnlT> listGroupsFromArm) {
		this.listGroupsFromArm = listGroupsFromArm;
	}
	
	public void addRole() {
		try{
			 String idRole = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					     .get("idRole");
			 String nameRole = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				        .get("nameRole");
	
			 LOGGER.debug("UserManagerBean:addRole:01:"+idRole);
			 LOGGER.debug("UserManagerBean:addRole:02:"+nameRole);
			 
			 if(userSessionBean.getSumRoles()==null){
				Map<Long, String> sumRoles = new  HashMap<Long, String>();
				userSessionBean.setSumRoles(sumRoles);
			 }
			 
			 if(userSessionBean.getSumRoles().containsKey(new Long(idRole))){
				 userSessionBean.getSumRoles().remove(new Long(idRole));
			 }else{
			     userSessionBean.getSumRoles().put(new Long(idRole), nameRole);
			 }
			 
		}catch(Exception e){
			 LOGGER.error("UserManagerBean:addRole:error:"+e);
		}
	}

	public void addGroup() {
		try{
			 String idGroup = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					     .get("idGroup");
			 String nameGroup = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				        .get("nameGroup");
	
			 LOGGER.debug("UserManagerBean:addGroup:01:"+idGroup);
			 LOGGER.debug("UserManagerBean:addGroup:02:"+nameGroup);
			 
			 if(userSessionBean.getSumGroups()==null){
				Map<Long, String> sumGroups = new  HashMap<Long, String>();
				userSessionBean.setSumGroups(sumGroups);
			 }
			 
			 if(userSessionBean.getSumGroups().containsKey(new Long(idGroup))){
				 userSessionBean.getSumGroups().remove(new Long(idGroup));
			 }else{
			     userSessionBean.getSumGroups().put(new Long(idGroup), nameGroup);
			 }
			 
		}catch(Exception e){
			 LOGGER.error("UserManagerBean:addGroup:error:"+e);
		}
	}
	
	public List<String> getSumRoles() {
		try{
			 LOGGER.debug("UserManagerBean:getSumRoles:01");
			 
			 if(this.sumRoles==null){
				 if(userSessionBean.getSumRoles()!=null){
				   this.sumRoles= new ArrayList(userSessionBean.getSumRoles().values());
				
				   Collections.sort(this.sumRoles, new Comparator<String>() {
                       public int compare(String o1, String o2) {
				        	 return o1.compareTo(o2);
				         }
				    });
				 }
			 }
		}catch(Exception e){
			 LOGGER.error("UserManagerBean:getSumRoles:error:"+e);
		}
		return this.sumRoles;
	}
	
	public void setSumRoles(List<String> sumRoles) {
		this.sumRoles = sumRoles;
	}
	
	public List<String> getSumGroups() {
		try{
			 LOGGER.debug("UserManagerBean:getSumGroups:01");
			 
			 if(this.sumGroups==null){
				 if(userSessionBean.getSumGroups()!=null){
				   this.sumGroups= new ArrayList(userSessionBean.getSumGroups().values());
				
				   Collections.sort(this.sumGroups, new Comparator<String>() {
                       public int compare(String o1, String o2) {
				        	 return o1.compareTo(o2);
				         }
				    });
				 }
			 }
		}catch(Exception e){
			 LOGGER.error("UserManagerBean:getSumGroups:error:"+e);
		}
		return this.sumGroups;
	}
	
	public void setSumGroups(List<String> sumGroups) {
		this.sumGroups = sumGroups;
	}
	
	public void resetSumRoles() {
		try{
			 String requestType = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				     .get("requestType");
			
			 LOGGER.debug("UserManagerBean:resetSumRoles:01:"+requestType);
			 
			 if(userSessionBean.getSumRoles()!=null&&
					 "armSelect".equals(requestType)){
				 
				 userSessionBean.getSumRoles().clear();
			 }
		}catch(Exception e){
			 LOGGER.error("UserManagerBean:resetSumRoles:error:"+e);
		}
	}
	
	public void resetSumGroups() {
		try{
			 String requestType = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				     .get("requestType");
			
			 LOGGER.debug("UserManagerBean:resetSumGroups:01:"+requestType);
			 
			 if(userSessionBean.getSumGroups()!=null&&
					 "armSelect".equals(requestType)){
				 
				 userSessionBean.getSumGroups().clear();
			 }
		}catch(Exception e){
			 LOGGER.error("UserManagerBean:resetSumGroups:error:"+e);
		}
	}
	
	
	public Boolean getRenderedRolesTable() {
		
		LOGGER.debug("UserManagerBean:getRenderedRolesTable:01");
		
		if(this.renderedRolesTable==null){
			
			String requestType = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				     .get("requestType");
			
			if("armSelect".equals(requestType)||
			   "rolesScroll".equals(requestType)||
			   "rolesSum".equals(requestType)){
				
				LOGGER.debug("UserManagerBean:getRenderedRolesTable:02!!!");
				this.renderedRolesTable=true;
			}
			
		}
		return renderedRolesTable;
	}

	public void setRenderedRolesTable(Boolean renderedRolesTable) {
		this.renderedRolesTable = renderedRolesTable;
	}

   public Boolean getRenderedGroupsTable() {
		
		LOGGER.debug("UserManagerBean:getRenderedGroupsTable:01");
		
		if(this.renderedGroupsTable==null){
			
			String requestType = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				     .get("requestType");
			
			if("armSelect".equals(requestType)||
			   "groupsScroll".equals(requestType)||
			   "groupsSum".equals(requestType)){
				
				LOGGER.debug("UserManagerBean:getRenderedGroupsTable:02!!!");
				this.renderedGroupsTable=true;
			}
			
		}
		return renderedGroupsTable;
	}

	public void setRenderedGroupsTable(Boolean renderedGroupsTable) {
		this.renderedGroupsTable = renderedGroupsTable;
	}
	
	public UserSessionBean getUserSessionBean() {
		return userSessionBean;
	}

	public void setUserSessionBean(UserSessionBean userSessionBean) {
		this.userSessionBean = userSessionBean;
	}

	public List<JournAppAdminUserSysBssT> getListAppAdminUserSys() {
		
     LOGGER.debug("UserManagerBean:getListAppAdminUserSys:01");
		
		if(listAppAdminUserSys==null){
			
			HttpSession hs = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
			Long authUserID = (Long) hs.getAttribute(CUDUserConsoleConstants.authUserID);
			
			LOGGER.debug("UserManagerBean:getListAppAdminUserSys:02:"+authUserID);
		
			this.listAppAdminUserSys =userManagerEJB.getAppAdminUserSysList(authUserID);
		}
		return listAppAdminUserSys;
	}

	public void setListAppAdminUserSys(
			List<JournAppAdminUserSysBssT> listAppAdminUserSys) {
		this.listAppAdminUserSys = listAppAdminUserSys;
	}

	public List<JournAppOrgManagerBssT> getListAppOrgMan() {
		
	     LOGGER.debug("UserManagerBean:getListAppOrgMan:01");
			
			if(listAppOrgMan==null){
				
				HttpSession hs = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
				Long authUserID = (Long) hs.getAttribute(CUDUserConsoleConstants.authUserID);
				
				LOGGER.debug("UserManagerBean:getListAppOrgMan:02:"+authUserID);
			
				this.listAppOrgMan =userManagerEJB.getAppOrgManList(authUserID);
			}
			return listAppOrgMan;
		}

		public void setListAppOrgMan(
				List<JournAppOrgManagerBssT> listAppAOrgMan) {
			this.listAppOrgMan = listAppOrgMan;
		}
	/**
     * для заявки на администрирование ИС
     * выбрать роль на консоле администрирования ЦУД
     * @return
     */
	public List<AcRolesBssT> getListRolesAdminSys() {
		
		if(listRolesAdminSys==null){
			listRolesAdminSys=userManagerEJB.getListRolesFromArm(CUD_ID);
		}
		return listRolesAdminSys;
	}

	public void setListRolesAdminSys(List<AcRolesBssT> listRolesAdminSys) {
		this.listRolesAdminSys = listRolesAdminSys;
	}
    
	public void changePassword() {
		try{
			 LOGGER.debug("UserManagerBean:changePassword:01");
	
	          String userOldPassword =  (String) FacesContext.getCurrentInstance().getExternalContext().getFlash()
	        		  .get("userOldPassword");
					
	          String userNewPassword =  (String) FacesContext.getCurrentInstance().getExternalContext().getFlash()
	        		  .get("userNewPassword");
	          
	          String userReNewPassword =  (String) FacesContext.getCurrentInstance().getExternalContext().getFlash()
	        		  .get("userReNewPassword");
			 
	          LOGGER.debug("UserManagerBean:changePassword:02:"+userOldPassword);
	          LOGGER.debug("UserManagerBean:changePassword:03:"+userNewPassword);
	          LOGGER.debug("UserManagerBean:changePassword:04:"+userReNewPassword);
	          
	          
	          if(userOldPassword==null||userOldPassword.isEmpty()||
	        	 userNewPassword==null||userNewPassword.isEmpty()||
	        	 userReNewPassword==null||userReNewPassword.isEmpty()){
	        	  
	        	   LOGGER.debug("UserManagerBean:changePassword:05");
	        	  
	        	  FacesContext.getCurrentInstance().addMessage(null, 
		        			new FacesMessage("Обязятельны все поля!"));
	        	   return;
	          }
	          
	          
	          Boolean latin = Pattern.matches("[^а-яА-Я]*", userNewPassword);
	        
	          if(!latin){
	        	  
	        	  LOGGER.debug("UserManagerBean:changePassword:06");
	        	  
	        	  FacesContext.getCurrentInstance().addMessage(null, 
		        			new FacesMessage("В пароле не допустима кириллица!"));
	        	   return;
	          }
	          
	          if(!userNewPassword.equals(userReNewPassword)){
	        	  
	        	  LOGGER.debug("UserManagerBean:changePassword:07");
	        	  
			      	FacesContext.getCurrentInstance().addMessage(null, 
		        			new FacesMessage("Пароли не совпадают!"));
		    }else{
	          
				HttpSession hs = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
				Long authUserID = (Long) hs.getAttribute(CUDUserConsoleConstants.authUserID);
					
				LOGGER.debug("UserManagerBean:changePassword:08:"+authUserID);
				
	          
				try{
					
				   userManagerEJB.changePassword(authUserID, userOldPassword, userNewPassword);
				
				}catch(Exception e){
					
					
					
					
				 if(e.getCause() instanceof NoResultException){	
				   FacesContext.getCurrentInstance().addMessage(null, 
		        			new FacesMessage("Не верный текущий пароль!"));
				     return;
				 }else{
					 throw e;
				 }
				} 
				
	          
				FacesContext
				.getCurrentInstance()
				.getExternalContext()
				.redirect(
						((HttpServletRequest) FacesContext
								.getCurrentInstance().getExternalContext()
								.getRequest()).getContextPath()
								+ "/context/profile/info/list.xhtml");
	          }
			 
	          LOGGER.debug("UserManagerBean:changePassword:09");
	          
		}catch(Exception e){
			 LOGGER.error("UserManagerBean:changePassword:error:"+e);
				
			 FacesContext.getCurrentInstance().addMessage(null, 
	        			new FacesMessage("Во время выполнения произошла ошибка!"));
		}
	}

	public Part getCertFile() {
		return certFile;
	}

	public void setCertFile(Part certFile) {
		this.certFile = certFile;
	}
	
	public void uploadCertFile() throws Exception { 
		
		InputStream inputStream = null;
		
		DateFormat df = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
		 
		try{
		
		   LOGGER.debug("UserManagerBean:uploadCertFile:01");
			
		   HttpSession hs = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
		   Long authUserID = (Long) hs.getAttribute(CUDUserConsoleConstants.authUserID);
			
		   
		   inputStream = certFile.getInputStream();   
		   
		   CertificateFactory userCf = CertificateFactory.getInstance("X.509");
           X509Certificate userCertX = (X509Certificate)
        		   userCf.generateCertificate(inputStream);
          
           String x509Cert = Base64.encode(userCertX.getEncoded());
           
           String serial = dec_to_hex(userCertX.getSerialNumber());
           
           if(userManagerEJB.certNumExistCrt(serial)){
        	   LOGGER.debug("UsrManager:saveUserCertificate:01_1:return;");
        	   FacesContext.getCurrentInstance().addMessage(null, 
           			new FacesMessage("Сертификат уже привязан!"));
        	   return ;
           }
           
           AcUsersCertBssT userCert = new AcUsersCertBssT();
    	   
    	   userCert.setCertNum(serial);
    	   userCert.setCertDate(df.format(userCertX.getNotAfter()));
    	   
    	   //!!!
    	   //сохраняем именно не userCertX.getEncoded(),
    	   //а x509Cert.getBytes
    	   userCert.setCertValue(x509Cert.getBytes("UTF-8"));
    	   
    	   String subject = userCertX.getSubjectDN().getName();
    	   
    	   LOGGER.debug("UserManagerBean:saveUserCertificate:02:"+subject);
    	   
		   LdapName ldapDN = new LdapName(subject);
		   
		   for(Rdn rdn: ldapDN.getRdns()) {
			    LOGGER.debug(rdn.getType() + " -> " + rdn.getValue());
			    
			    if("CN".equals(rdn.getType())){
			    	userCert.setUserFio((String)rdn.getValue());
			    }else if("OU".equals(rdn.getType())){
			    	userCert.setDepName((String)rdn.getValue());
			    }else if("O".equals(rdn.getType())){
			    	userCert.setOrgName((String)rdn.getValue());
			    }
			    
			}
    	   
    	   userCert.setUpUserRaw(authUserID);
    	   
    	   userCert.setCreator(authUserID);
    	   userCert.setCreated(new Date());
    	   
    	   
    	   userManagerEJB.uploadCertFile(userCert);
    	  
	       
	       FacesContext.getCurrentInstance().addMessage(null, 
       			new FacesMessage("Сертификат добавлен!"));
    
	       
		} catch(Exception e){
			 LOGGER.error("UserManagerBean:uploadCertFile:error:"+e);
			 
			FacesContext.getCurrentInstance().addMessage(null, 
        			new FacesMessage("Ошибка при добавлении сертификата!"));
       }finally{
    	   try{
    		   if(inputStream!=null){
    			   inputStream.close();
    		   }
    	   }catch(Exception e){
    		   
    	   }
       }
   }  
	
	private static String dec_to_hex(BigInteger bi) {
		
		String result = null;
		
		try
		{
		 result = bi.toString(16);
	     LOGGER.debug("num_convert:num:"+result);
		}
		catch (NumberFormatException e)
		{
		     LOGGER.error("Error! tried to parse an invalid number format");
		}
		 return result;
	 }

	public List<GroupUsersKnlT> getUserGroups() {
		
		if(userGroups==null){
			
			try{
			HttpSession hs = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
			Long authUserID = (Long) hs.getAttribute(CUDUserConsoleConstants.authUserID);
				
			LOGGER.debug("UserManagerBean:getUserGroups:01:"+authUserID);
		
			userGroups = userManagerEJB.getUserGroups(authUserID);
			
			} catch(Exception e){
				 LOGGER.error("UserManagerBean:getUserGroups:error:"+e);
			
				//не будет отображаться
				//сообщения, созданные в RENDER_RESPONSE фазе не отображаются
			   
	       }
		}
		
		return userGroups;
	}

	public void setUserGroups(List<GroupUsersKnlT> userGroups) {
		this.userGroups = userGroups;
	}
	

	public List<BaseItem> getUserCertList() {
		
		if(this.userCertList==null){
			
			try{
			   HttpSession hs = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
			   Long authUserID = (Long) hs.getAttribute(CUDUserConsoleConstants.authUserID);
			
     		   this.userCertList =userManagerEJB.getUserCertList(authUserID);
			
			}catch(Exception e){
				 LOGGER.error("UserManagerBean:getUserCertList:error:"+e);
			}
		}
		
		return userCertList;
	}

	public void setUserCertList(List<BaseItem> userCertList) {
		this.userCertList = userCertList;
	}
	 
	
}
