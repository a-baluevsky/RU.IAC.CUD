package ru.spb.iac.cud.uarm.ejb.audit;

public enum ActionsMap {

	UPDATE_PASSWORD("update_password"),
	ADD_CERTIFICATE("add_certificate"),
	LOGIN_UARM("login_uarm"),
	LOGOUT_UARM("logout_uarm");
	
    private String code;
	
    ActionsMap(String code){
		this.code=code;
    }
      
    public String getCode(){
    	return this.code;
    }  
    public void setCode(String code){
    	this.code=code;
    }
}
