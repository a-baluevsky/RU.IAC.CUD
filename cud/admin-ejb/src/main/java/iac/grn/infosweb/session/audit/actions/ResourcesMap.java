package iac.grn.infosweb.session.audit.actions;

public enum ResourcesMap {

	

	USER("user"),
	ROLE("role"),
	IS("is"),
	UGROUP("ugroup"),
	RES("res"),
	AUDIT_SYS("audit_sys"),
	AUDIT_USER("audit_user"),
	//AUDIT_TOKEN("audit_token"),
	PROC_ARCH_AUDIT_SYS("proc_arch_audit_sys"),
	PROC_ARCH_AUDIT_USER("proc_arch_audit_user"),
	PROC_ARCH_TOKEN("proc_arch_token"),
	PROC_BIND_UNBIND("proc_bind_unbind"),
	PROC_BIND_NOACT("proc_bind_noact"),
	NAVIG("navig_tree"),
	CONF_PARAM("conf_param"),
	BINDING_IOGV("binding_iogv"),
	CLASSIF_IOGV("classif_iogv"),
	//
	IS_GROUP("is_group"),
	APP_USER("app_user"),
	APP_USER_ACC("app_user_acc"),
	APP_USER_UPDATE("app_user_update"),
	APP_USER_CERT("app_user_cert"),
	
	APP_SYS("app_sys"),
	APP_USER_BLOCK("app_user_block"),
	APP_ORG_MAN("app_org_man");
	
    private String code;
	
    ResourcesMap(String code){
		this.code=code;
    }
      
    public String getCode(){
    	return this.code;
    }  
    public void setCode(String code){
    	this.code=code;
    }
}
