package ru.spb.iac.cud.util;

import java.security.Principal;

 public class CudPrincipal implements Principal {

	private String systemName;

	private String userName;

	private String authType;
	
	public CudPrincipal() {
	}

	public CudPrincipal(String systemName) {
		this.systemName = systemName;
	}

	public CudPrincipal(String systemName, String userName, String authType){
		this.systemName=systemName;
		this.userName=userName;
		this.authType = authType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getName() {
		if (this.userName != null) {
			return this.userName;
		}

		return this.systemName;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}
}
