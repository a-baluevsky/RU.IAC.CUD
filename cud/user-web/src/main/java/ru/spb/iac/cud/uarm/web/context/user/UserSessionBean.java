package ru.spb.iac.cud.uarm.web.context.user;

import java.io.Serializable;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
 
@ManagedBean(name="userSessionBean")
@SessionScoped
 public class UserSessionBean implements Serializable {
 
    private static final long serialVersionUID = 1L;
 
    private Map<Long, String> sumRoles;

    private Map<Long, String> sumGroups;
    
    private String userEmailReg;
    
    public Map<Long, String> getSumRoles() {
		return sumRoles;
	}

	public void setSumRoles(Map<Long, String> sumRoles) {
		this.sumRoles = sumRoles;
	}

	public String getUserEmailReg() {
		return userEmailReg;
	}

	public void setUserEmailReg(String userEmailReg) {
		this.userEmailReg = userEmailReg;
	}

	public Map<Long, String> getSumGroups() {
		return sumGroups;
	}

	public void setSumGroups(Map<Long, String> sumGroups) {
		this.sumGroups = sumGroups;
	}

	
   
    
}
