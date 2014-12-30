package iac.grn.infosweb.context.mc.usr;

import iac.grn.infosweb.session.table.BaseStateHolder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;


@Name("usrRoleStateHolder")
@Scope(ScopeType.SESSION)
@AutoCreate
 public class UsrRoleStateHolder extends BaseStateHolder{
	
	   @Logger private Log log;
	
       @Create
       public void create() {
    	   log.info("usrRoleStateHolder:create");
       }
       
         public void clearFilters(){
    	   log.info("clearFilters:01");
    	   if(columnFilterValues!=null){
          		for(Iterator<Map.Entry<String, String>> it = columnFilterValues.entrySet().iterator(); it.hasNext();)
    			{
    			      Map.Entry<String, String> me = it.next();
    			  	  if(me.getValue()==null||me.getValue().isEmpty()||"#-1#".equals(me.getValue())){
    	     			  log.info("Ahtung!!!");
    	     			  it.remove();
    	     		   }
    			}
          }
       }
       public void sort(String itemField){
    	   log.info("sort:itemField:"+itemField);
    	   
    	   String value=null;
    	   
    	   if(this.sortOrders.containsKey(itemField)){
    		   value=this.sortOrders.get(itemField);
    		   if(value!=null && "asc".equals(value)){
    			   this.sortOrders.put(itemField, "desc");
    		   }else{
    			   this.sortOrders.put(itemField, "asc");
    		   }
    	   }else{
    		   this.sortOrders.clear();
    		   this.sortOrders.put(itemField, "asc");  
    	   }
    	   log.info("sort:02");
       }
       
       public void sort(){
    	   String itemField = FacesContext.getCurrentInstance().getExternalContext()
  	             .getRequestParameterMap()
  	             .get("itemField");
  	 
    	   log.info("sort:itemField:"+itemField);
    	   
    	   String value=null;
    	   
    	   if(this.sortOrders.containsKey(itemField)){
    		   value=this.sortOrders.get(itemField);
    		   if(value!=null && "asc".equals(value)){
    			   this.sortOrders.put(itemField, "desc");
    		   }else{
    			   this.sortOrders.put(itemField, "asc");
    		   }
    	   }else{
    		   this.sortOrders.clear();
    		   this.sortOrders.put(itemField, "asc");  
    	   }
    	   log.info("sort:02");
       }
       
    private List<Long> appRolesList = new ArrayList<Long>();


   	public List<Long> getAppRolesList() {
   		return appRolesList;
   	}

   	public void setAppRolesList(List<Long> appRolesList) {
   		this.appRolesList = appRolesList;
   	}
   	
   	public void resetAppRolesList() {
   		if(this.appRolesList != null){
   		   this.appRolesList.clear();
   		   this.appRolesList = null;
   		}
   	}
       
    }