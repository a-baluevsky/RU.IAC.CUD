package iac.grn.infosweb.context.mc.perm;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import iac.cud.infosweb.dataitems.BaseItem;
import iac.cud.infosweb.entity.AcPermissionsList;
import java.util.*;

import org.jboss.seam.Component;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import iac.grn.serviceitems.BaseTableItem;

/**
 * Управляющий Бин
 * @author bubnov
 *
 */
@Name("permManager")
 public class PermManager {
	
	 @Logger private Log log;
	
	 @In 
	 EntityManager entityManager;
	 
	
	private String dellMessage;
	 
	private List<BaseItem> auditList; 
	
	private List <BaseTableItem> auditItemsListContext;

	
	private Long auditCount;
	
	private Boolean evaluteForList;
	private Boolean evaluteForListFooter;  
	private Boolean evaluteForBean;

	private List <BaseTableItem> auditItemsListSelect;
	
	
	private int connectError=0;
	
	 
	public List<BaseItem> getAuditList(int firstRow, int numberOfRows){
	  String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
	             .getRequestParameterMap()
	             .get("remoteAudit");
	   log.info("auditManager:getAuditList:remoteAudit:"+remoteAudit);
	  
	  
	  log.info("getAuditList:firstRow:"+firstRow);
	  log.info("getAuditList:numberOfRows:"+numberOfRows);
	  
	  List<BaseItem> permListCached = (List<BaseItem>)
			  Component.getInstance("permListCached",ScopeType.SESSION);
	  if(auditList==null){
		  log.info("getAuditList:01");
		 	if(("rowSelectFact".equals(remoteAudit)||
			    "selRecAllFact".equals(remoteAudit)||
			    "clRecAllFact".equals(remoteAudit)||
			    "clSelOneFact".equals(remoteAudit)||
			    "onSelColSaveFact".equals(remoteAudit))&&
			    permListCached!=null){
		 	    	this.auditList=permListCached;
			}else{
				log.info("getAuditList:03");
		    	invokeLocal("list", firstRow, numberOfRows, null);
			    Contexts.getSessionContext().set("permListCached", this.auditList);
			    log.info("getAuditList:03:"+this.auditList.size());
			}
		 	
		 	List<String>  selRecPerm = (ArrayList<String>)
					  Component.getInstance("selRecPerm",ScopeType.SESSION);
		 	if(this.auditList!=null && selRecPerm!=null) {
		 		 for(BaseItem it:this.auditList){
				   if(selRecPerm.contains(it.getBaseId().toString())){
					 
					 it.setSelected(true);
				   }else{
					  it.setSelected(false);
				   }
				 }
		    }
		}
		return this.auditList;
	}
	
	public void invokeLocal(String type, int firstRow, int numberOfRows,
	           String sessionId) {
		try{
			 String orderQuery=null;
			 log.info("Perm:Manager:invokeLocal");
			 
			 if("list".equals(type)){
				 log.info("Perm:invokeLocal:list:01");
				 
				 PermStateHolder permStateHolder = (PermStateHolder)
						  Component.getInstance("permStateHolder",ScopeType.SESSION);
				 Set<Map.Entry<String, String>> set = permStateHolder.getSortOrders().entrySet();
                 for (Map.Entry<String, String> me : set) {
      		       log.info("me.getKey+:"+me.getKey());
      		       log.info("me.getValue:"+me.getValue());
      		       
      		       if(orderQuery==null){
      		    	 orderQuery="order by "+me.getKey()+" "+me.getValue();
      		       }else{
      		    	 orderQuery=orderQuery+", "+me.getKey()+" "+me.getValue();  
      		       }
      		     }
                 log.info("Perm:invokeLocal:list:orderQuery:"+orderQuery);
                 
					 auditList = entityManager.createQuery("select o from AcPermissionsList o "+(orderQuery!=null ? orderQuery+", orderNum " : " order by orderNum "))
                       .setFirstResult(firstRow)
                       .setMaxResults(numberOfRows)
                       .getResultList();
             log.info("invokeLocal:list:02");
  
			 } else if("count".equals(type)){
				 
				 log.info("PermList:count:01");
				 auditCount = (Long)entityManager.createQuery(
						 "select count(o) " +
				         "from AcPermissionsList o ")
		                .getSingleResult();
				 
               log.info("Perm:invokeLocal:count:02:"+auditCount);
           	 } 
		}catch(Exception ePerm){
			  log.error("Perm:invokeLocal:error:"+ePerm);
			  evaluteForList=false;
			  FacesMessages.instance().add("Ошибка!");
		}
	}
	
	
   public void forView(String modelType) {
	   String  permId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	  log.info("forView:permId:"+permId);
	  log.info("forView:modelType:"+modelType);
	  if(permId!=null ){
		  
		   
			if(modelType==null){
		    	return ;
		    }
			
			 AcPermissionsList ar = searchBean(permId);
		 Contexts.getEventContext().set("permBean", ar);
	  }
   }
   
   public void setAuditList(List<BaseItem> auditList){
		this.auditList=auditList;
	}
   
   private  AcPermissionsList searchBean(String sessionId){
    	
      if(sessionId!=null){
    	 List<AcPermissionsList> permListCached = (List< AcPermissionsList>)
				  Component.getInstance("permListCached",ScopeType.SESSION);
    	 
		if(permListCached!=null){
			for( AcPermissionsList it : permListCached){
				 
			 
			  if(it.getBaseId().toString().equals(sessionId)){
					 log.info("searchBean_Achtung!!!");
					 return it;
			  }
			}
		 }
	   }
	   return null;
    }
    public Long getAuditCount(){
	   log.info("getAuditCount");
	 
	   invokeLocal("count",0,0,null);
	  
	   return auditCount;
	  
   }
   
   public void addPerm(){
	   log.info("permManager:addPerm:01");
	   
	   AcPermissionsList permBeanCrt = (AcPermissionsList)
				  Component.getInstance("permBeanCrt",ScopeType.CONVERSATION);
	   
	   if(permBeanCrt==null){
		   return;
	   }
	 
	   try {
		
	      entityManager.persist(permBeanCrt);
	 
	     
	    
	    
	    }catch (Exception e) {
	       log.error("permManager:addPerm:ERROR:"+e);
	    }
	   
   }
   
   public void updPerm(){
	   
	   log.info("permManager:updPerm:01");
	   
	   AcPermissionsList permBean = (AcPermissionsList)
				  Component.getInstance("permBean",ScopeType.CONVERSATION);
	   
	   String  sessionId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	   log.info("permManager:updPerm:sessionId:"+sessionId);
	
	   if(permBean==null || sessionId==null){
		   return;
	   }
	
	   try {
		    
		  AcPermissionsList aam = entityManager.find(AcPermissionsList.class, new Long(sessionId));
		  
		  aam.setPermName(permBean.getPermName());
		  aam.setPermDescr(permBean.getPermDescr());
		  aam.setOrderNum(permBean.getOrderNum());
		  
		 
		
		  
		  entityManager.flush();
	      entityManager.refresh(aam);
	    	  
	       Contexts.getEventContext().set("permBean", aam);
	    	  
	     }catch (Exception e) {
           log.error("armManager:updSrm:ERROR:"+e);
         }
   }
   
   public void delPerm(){
	 try{
		log.info("permManager:delPerm:01");  
		
		AcPermissionsList armBean = (AcPermissionsList)
				  Component.getInstance("permBean",ScopeType.CONVERSATION);
		// <h:inputHidden value="#{armBean.idArm}"/>
		
		if(armBean==null){
			return;
		}
		 
		log.info("permManager:delPerm:IdPerm:"+armBean.getIdPerm());
		
		AcPermissionsList aom = entityManager.find(AcPermissionsList.class, armBean.getIdPerm());
		  
		entityManager.remove(aom);
		
	 }catch(Exception e){
		 log.error("armManager:delArm:error:"+e); 
	 }
    }
 
    public void forViewUpdDel() {
	   try{
	     String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	     log.info("forViewUpdDel:sessionId:"+sessionId);
	     if(sessionId!=null){
	    	 AcPermissionsList ao = entityManager.find(AcPermissionsList.class, new Long(sessionId));
	    	 Contexts.getEventContext().set("permBean", ao);
	   	 }
	   }catch(Exception e){
		   log.error("forViewUpdDel:Error:"+e);
	   }
    } 
   
    public void forViewDelMessage() {
		  String sessionIdPerm = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap()
				.get("sessionId");
		  log.info("forViewDel:sessionId:"+sessionIdPerm);
		  if(sessionIdPerm!=null){
			 AcPermissionsList aa = entityManager.find(AcPermissionsList.class, new Long(sessionIdPerm));
			 if((aa.getAcLinkRoleAppPagePrmssns()!=null&&!aa.getAcLinkRoleAppPagePrmssns().isEmpty())){
				dellMessage="У разрешения есть порождённые записи! При удалении они будут удалены!";
			 }
			 Contexts.getEventContext().set("permBean", aa);
		 }	
    }
  
    
   public int getConnectError(){
	   return connectError;
   }
 
   
   public List <BaseTableItem> getAuditItemsListSelect() {
		 log.info("getAuditItemsListSelect:01");
	
	    PermContext acPerm= new PermContext();
		   if( auditItemsListSelect==null){
			   log.info("getAuditItemsListSelect:02");
			   auditItemsListSelect = new ArrayList<BaseTableItem>();
			   
			
			   auditItemsListSelect.add(acPerm.getAuditItemsMap().get("permName"));
			   auditItemsListSelect.add(acPerm.getAuditItemsMap().get("permDescr"));
		   }
	       return this.auditItemsListSelect;
   }
   
   public void setAuditItemsListSelect(List <BaseTableItem> auditItemsListSelect) {
		    this.auditItemsListSelect=auditItemsListSelect;
   }
   
   public List <BaseTableItem> getAuditItemsListContext() {
	   log.info("orgManager:getAuditItemsListContext");
	   if(auditItemsListContext==null){
		   PermContext acPerm= new PermContext();
		   auditItemsListContext = new ArrayList<BaseTableItem>();
		   
		   
		   auditItemsListContext=acPerm.getAuditItemsCollection();
	   }
	   return this.auditItemsListContext;
   }
      
    
   public void selectRecord(){
	    String  sessionIdPerm = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	    log.info("selectRecord:sessionId="+sessionIdPerm);
	    
	   //!!!
	    List<String>  selRecPerm = (ArrayList<String>)
				  Component.getInstance("selRecPerm",ScopeType.SESSION);
	    
	    if(selRecPerm==null){
	       selRecPerm = new ArrayList<String>();
	       log.info("selectRecord:01");
	    }
	    
	    
	    AcPermissionsList aa = new  AcPermissionsList();
  	   
	    
	    if(aa!=null){
	     if(selRecPerm.contains(sessionIdPerm)){
	    	selRecPerm.remove(sessionIdPerm);
	    	aa.setSelected(false);
	    	log.info("selectRecord:02");
	     }else{
	    	selRecPerm.add(sessionIdPerm);
	    	aa.setSelected(true);
	    	log.info("selectRecord:03");
	     }
	    Contexts.getSessionContext().set("selRecPerm", selRecPerm);	
	    
	    Contexts.getEventContext().set("permBean", aa);
	   }
	}
   
   public String getDellMessage() {
	   return dellMessage;
   }
   public void setDellMessage(String dellMessage) {
	   this.dellMessage = dellMessage;
   } 
   
   public Boolean getEvaluteForList() {
	
   	log.info("armManager:evaluteForList:01");
   	if(evaluteForList==null){
   		evaluteForList=false;
    	String remoteAuditPerm = FacesContext.getCurrentInstance().getExternalContext()
	             .getRequestParameterMap()
	             .get("remoteAudit");
	   log.info("permManager:evaluteForList:remoteAudit:"+remoteAuditPerm);
     	
    	if(remoteAuditPerm!=null&&
    	 
    	   !"OpenCrtFact".equals(remoteAuditPerm)&&	
    	   !"OpenUpdFact".equals(remoteAuditPerm)&&
    	   !"OpenDelFact".equals(remoteAuditPerm)&&
   	       !"onSelColFact".equals(remoteAuditPerm)&&
   	       !"refreshPdFact".equals(remoteAuditPerm)){
    		log.info("permManager:evaluteForList!!!");
   		    evaluteForList=true;
    	}
   	 }
       return evaluteForList;
   }
   public Boolean getEvaluteForListFooter() {
		
	  
	   	if(evaluteForListFooter==null){
	   		evaluteForListFooter=false;
	    	String remoteAuditPerm = FacesContext.getCurrentInstance().getExternalContext()
		             .getRequestParameterMap()
		             .get("remoteAudit");
		   log.info("permManager:evaluteForListFooter:remoteAudit:"+remoteAuditPerm);
	     
	    	if(getEvaluteForList()&&
	    	    	
	    	   !"protBeanWord".equals(remoteAuditPerm)&&	
	    	    	
	   	       !"selRecAllFact".equals(remoteAuditPerm)&&
	   	       !"clRecAllFact".equals(remoteAuditPerm)&&
	   	      // !remoteAudit equals "clSelOneFact"
	   	       !"onSelColSaveFact".equals(remoteAuditPerm)){
	    		  log.info("permManager:evaluteForListFooter!!!");
	   		      evaluteForListFooter=true;
	    	}
	   	 }
	       return evaluteForListFooter;
	   }
   
   public Boolean getEvaluteForBean() {
		
		  
		   	if(evaluteForBean==null){
		   		evaluteForBean=false;
		    	String remoteAuditPerm = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("remoteAudit");
			    log.info("permManager:evaluteForBean:remoteAudit:"+remoteAuditPerm);
				String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("sessionId");
			    log.info("permManager:evaluteForBean:sessionId:"+sessionId);
		    	if(sessionId!=null && remoteAuditPerm!=null &&
		    	   ("rowSelectFact".equals(remoteAuditPerm)||	
		    	    "UpdFact".equals(remoteAuditPerm))){
		    	      log.info("permManager:evaluteForBean!!!");
		   		      evaluteForBean=true;
		    	}
		   	 }
		     return evaluteForBean;
		   }

}
