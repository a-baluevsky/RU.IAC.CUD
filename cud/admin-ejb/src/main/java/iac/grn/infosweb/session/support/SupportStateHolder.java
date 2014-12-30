package iac.grn.infosweb.session.support;

import java.util.ArrayList;
import java.util.HashMap;
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


@Name("supportStateHolder")
@Scope(ScopeType.SESSION)
@AutoCreate
 public class SupportStateHolder {
	
       @Logger private Log log;
	
       private Map<String, HashMap<String, String>> sortOrders = new HashMap<String, HashMap<String, String>>();
       private Map<String, HashMap<String, String>> columnFilterValues = new HashMap<String, HashMap<String, String>>();
      
       private List <String> auditFieldListSelect = new ArrayList<String>();
       
     
       
       @Create
       public void create() {
    	   log.info("datatableStateHolder:create");
    	
    	
   		   
        }
       
       public List <String> getAuditFieldListSelect() {
    	   return this.auditFieldListSelect;
       }
       public void setAuditFieldListSelect(List <String> auditFieldListSelect) {
    	   this.auditFieldListSelect=auditFieldListSelect;
       }
       
       public Map<String, HashMap<String, String>> getColumnFilterValues() {
           return columnFilterValues;
       }
       public Map<String, String> getColumnFilterValues(String modelType) {
    	   log.info("datatableStateHolder:getColumnFilterValues:01");
    	   if(!columnFilterValues.containsKey(modelType)){
    		   log.info("datatableStateHolder:getColumnFilterValues:02");
    		   columnFilterValues.put(modelType, new HashMap<String, String>());
    	   }
           return columnFilterValues.get(modelType);
       }
       public void setColumnFilterValues(Map<String, HashMap<String, String>> columnFilterValues) {
    	   log.info("datatableStateHolder:setColumnFilterValues");
    	   this.columnFilterValues = columnFilterValues;
       }
       
       public Map<String, HashMap<String, String>> getSortOrders() {
               return sortOrders;
       }
       public void setSortOrders(Map<String, HashMap<String, String>> sortOrders) {
                this.sortOrders = sortOrders;
       }
       
       public void clearFilters(String modelType){
    	   log.info("clearFilters:01");
    	   if(columnFilterValues!=null){
          	
    		   if(columnFilterValues.containsKey(modelType)){
    			  Map<String, String>  hm = columnFilterValues.get(modelType);
          		  for(Iterator<Map.Entry<String, String>> it = hm.entrySet().iterator(); it.hasNext();){
    			      Map.Entry<String, String> me = it.next();
    		 		  if(me.getValue()==null||me.getValue().isEmpty()||"#-1#".equals(me.getValue())){
    	     			  log.info("Ahtung!!!");
    	     			  it.remove();
    	     		   }
    			}
    	     }
          }
       }
       
       public void clearAll(String modelType){
    	   log.info("clearAllFilters:01:modelType:"+modelType);
    	   if(sortOrders!=null){
      		   if(sortOrders.containsKey(modelType)){
    			 
    			   
    			  Map<String, String>  hm = sortOrders.get(modelType);
          		  for(Iterator<Map.Entry<String, String>> it = hm.entrySet().iterator(); it.hasNext();){
          			it.next();
  			        it.remove();
    			}
    	     }
          }
    	  if(columnFilterValues!=null){
     		   if(columnFilterValues.containsKey(modelType)){
    			  Map<String, String>  hm = columnFilterValues.get(modelType);
          		  for(Iterator<Map.Entry<String, String>> it = hm.entrySet().iterator(); it.hasNext();){
    			     it.next();
    		  	      it.remove();
    			}
    	     }
          }
       }
       
 
    
       public void filterSelect(){
    	   String modelType = FacesContext.getCurrentInstance().getExternalContext()
      	         .getRequestParameterMap()
      	         .get("modelType");
    	   String fieldName = FacesContext.getCurrentInstance().getExternalContext()
  	             .getRequestParameterMap()
  	             .get("fieldName");
    	   String fieldValue = FacesContext.getCurrentInstance().getExternalContext()
    	         .getRequestParameterMap()
    	        .get("fieldValue");
    	   log.info("filterSelect:modelType:"+modelType);
    	   log.info("filterSelect:fieldName:"+fieldName);
  	       log.info("filterSelect:fieldValue:"+fieldValue);
  	       
  	  	   
  	  	  Map<String, String> fv = getColumnFilterValues(modelType);
  	  	
  	  	   fv.put(fieldName, fieldValue);
  	  	   
    	   log.info("filterSelect:02");
       }
       
       public void sort(){
    	   String itemField = FacesContext.getCurrentInstance().getExternalContext()
  	             .getRequestParameterMap()
  	             .get("itemField");
  	       log.info("sort:itemField:"+itemField);
  	       
  	       String modelType = FacesContext.getCurrentInstance().getExternalContext()
  	             .getRequestParameterMap()
  	             .get("modelType");
  	 
    	   log.info("sort:modelType:"+modelType);
    	   String value=null;
    	   
    	   if(this.sortOrders.containsKey(modelType)){
    		   
    		   if(this.sortOrders.get(modelType).containsKey(itemField)){
        		   value=this.sortOrders.get(modelType).get(itemField);
        		   if(value!=null && "asc".equals(value)){
        			   this.sortOrders.get(modelType).put(itemField, "desc");
        		   }else{
        			   this.sortOrders.get(modelType).put(itemField, "asc");
        		   }
        	   }else{
        		   this.sortOrders.get(modelType).clear();
        		   this.sortOrders.get(modelType).put(itemField, "asc");  
        	   }
    	   }else{
    		   HashMap<String, String>  hm = new HashMap<String, String>();
    		   hm.put(itemField, "asc");
    		   this.sortOrders.put(modelType, hm);
    	   }
    	   
       	   log.info("sort:02");
       }
       public String getSortImg(String modelType, String itemField) {
           if(this.sortOrders.containsKey(modelType)){
    		   
    		   if(this.sortOrders.get(modelType).containsKey(itemField)){
        		   return this.sortOrders.get(modelType).get(itemField);
        	   }else{
        		   return "";
        	   }
    	   }else{
    		   return "";
    	   }
           
       }
       
   
    }