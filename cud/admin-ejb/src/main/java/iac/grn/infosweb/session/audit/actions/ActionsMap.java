package iac.grn.infosweb.session.audit.actions;

public enum ActionsMap {

	CREATE("create"),
	UPDATE("update"),
	UPDATE_USER("update_user"),//+is
	UPDATE_ROLE("update_role"),
	UPDATE_GROUP("update_group"),
	UPDATE_ADMIN_SYS("update_admin_sys"),
	DELETE("delete"),
	START("start"),
	STOP("stop"),
	PAUSE("pause"),
	SET_PARAM("set_param"),
	LOGIN_ADM("login_adm"),
	LOGOUT_ADM("logout_adm"),
	TRANSFER("transfer"),
	LOAD("load"),
	//user
	ADD_CERT("add_cert"), //+is
	REMOVE_CERT("remove_cert"),//+is
	UPDATE_AC_ORG("update_ac_org"),
	
	//is_group
	UPDATE_IS("update_is"),
	
	EXECUTE("execute"),
	REJECT("reject");
	
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
