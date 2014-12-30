package ru.spb.iac.cud.uarm.ejb.audit;

public enum ResourcesMap {

	

	USER("user");
		
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
