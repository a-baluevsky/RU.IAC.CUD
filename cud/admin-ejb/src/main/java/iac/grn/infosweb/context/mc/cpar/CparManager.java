package iac.grn.infosweb.context.mc.cpar;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import iac.cud.infosweb.dataitems.BaseItem;
import iac.cud.infosweb.entity.SettingsKnlT;
import iac.grn.infosweb.session.audit.actions.ActionsMap;
import iac.grn.infosweb.session.audit.actions.ResourcesMap;
import iac.grn.infosweb.session.audit.export.AuditExportData;
import java.util.*;

import org.jboss.seam.Component;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import iac.grn.serviceitems.BaseTableItem;

/**
 * ”правл€ющий Ѕин
 * @author bubnov
 *
 */
@Name("cparManager")
 public class CparManager {
	
	 @Logger private Log log;
	
	 @In 
	 EntityManager entityManager;
	 
			
	private String dellMessage;
	 
	private List<BaseItem> auditList; 
	
	private Long auditCount;
	
    private SettingsKnlT setting;
	
	private String settingValue;
		
	private int connectError=0;
	private Boolean evaluteForList;
	private Boolean evaluteForListFooter;  
	private Boolean evaluteForBean;

   private List <BaseTableItem> auditItemsListSelect;
	
	private List <BaseTableItem> auditItemsListContext;

	
	
	
	public List<BaseItem> getAuditList(int firstRow, int numberOfRows){
	  String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
	             .getRequestParameterMap()
	             .get("remoteAudit");
	   log.info("auditManager:getAuditList:remoteAudit:"+remoteAudit);
	  
	  
	  log.info("getAuditList:firstRow:"+firstRow);
	  log.info("getAuditList:numberOfRows:"+numberOfRows);
	  
	  List<BaseItem> cparListCached = (List<BaseItem>)
			  Component.getInstance("cparListCached",ScopeType.SESSION);
	  if(auditList==null){
		  log.info("getAuditList:01");
		 	if(("rowSelectFact".equals(remoteAudit)||
			    "selRecAllFact".equals(remoteAudit)||
			    "clRecAllFact".equals(remoteAudit)||
			    "clSelOneFact".equals(remoteAudit)||
			    "onSelColSaveFact".equals(remoteAudit))&&
			    cparListCached!=null){
		 		    	this.auditList=cparListCached;
			}else{
				log.info("getAuditList:03");
		    	invokeLocal("list", firstRow, numberOfRows, null);
			    Contexts.getSessionContext().set("cparListCached", this.auditList);
			    log.info("getAuditList:03:"+this.auditList.size());
			}
		 	
		 	List<String>  selRecCpar = (ArrayList<String>)
					  Component.getInstance("selRecCpar",ScopeType.SESSION);
		 	if(this.auditList!=null && selRecCpar!=null) {
		 		 for(BaseItem it:this.auditList){
				   if(selRecCpar.contains(it.getBaseId().toString())){
					 
					 it.setSelected(true);
				   }else{
					  it.setSelected(false);
				   }
				 }
		    }
		}
		return this.auditList;
	}
	public void setAuditList(List<BaseItem> auditList){
		this.auditList=auditList;
	}
	public void invokeLocal(String type, int firstRow, int numberOfRows,
	           String sessionId) {
		try{
			 String orderQueryCPar=null;
			 log.info("CParManager:invokeLocal");
			 
			 if("list".equals(type)){
				 log.info("CPar:invokeLocal:list:01");
				 
				 CparStateHolder orgStateHolder = (CparStateHolder)
						  Component.getInstance("cparStateHolder",ScopeType.SESSION);
				 Set<Map.Entry<String, String>> set = orgStateHolder.getSortOrders().entrySet();
                 for (Map.Entry<String, String> me : set) {
      		        
      		       if(orderQueryCPar==null){
      		    	 orderQueryCPar="order by "+me.getKey()+" "+me.getValue();
      		       }else{
      		    	 orderQueryCPar=orderQueryCPar+", "+me.getKey()+" "+me.getValue();  
      		       }
      		     }
                 log.info("invokeLocal:list:orderQueryCPar:"+orderQueryCPar);
                 
				 if(orderQueryCPar!=null&&orderQueryCPar.contains("o1.full")){
                	 auditList = entityManager.createQuery(
                	"select o from SettingsKnlT o LEFT JOIN o.servicesBssT o1 "+		 
                	 (orderQueryCPar!=null ? orderQueryCPar : ""))
                             .setFirstResult(firstRow)
                             .setMaxResults(numberOfRows)
                             .getResultList();
                 }else{
				  auditList = entityManager.createQuery("select o from SettingsKnlT o "+(orderQueryCPar!=null ? orderQueryCPar : ""))
                       .setFirstResult(firstRow)
                       .setMaxResults(numberOfRows)
                       .getResultList();
				 }
             log.info("invokeLocal:list:02");
  
			 } else if("count".equals(type)){
				 log.info("CParList:count:01");
				 auditCount = (Long)entityManager.createQuery(
						 "select count(o) " +
				         "from SettingsKnlT o ")
		                .getSingleResult();
				 
               log.info("CPar:invokeLocal:count:02:"+auditCount);
           	 } else if("bean".equals(type)){
				 
			 }
		}catch(Exception e){
			  log.error("invokeLocal:error:"+e);
			  evaluteForList=false;
			  FacesMessages.instance().add("ќшибка!");
		}
	}
	
	
   public void forView(String modelType) {
	   String  cParId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	  log.info("forView:cParId:"+cParId);
	  log.info("forView:modelType:"+modelType);
	  if(cParId!=null ){
		  
		   
			if(modelType==null){
		    	return ;
		    }
			
		 SettingsKnlT ar = searchBean(cParId);
		 Contexts.getEventContext().set("cparBean", ar);
	  }
   }
   
   private SettingsKnlT searchBean(String sessionId){
    	
      if(sessionId!=null){
    	 List<SettingsKnlT> cparListCached = (List<SettingsKnlT>)
				  Component.getInstance("cparListCached",ScopeType.SESSION);
		if(cparListCached!=null){
			for(SettingsKnlT it : cparListCached){
				 
			 
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
  
   
   public void updCpar(){
	   
	   log.info("cparManager:updCpar:01");
	   
	   SettingsKnlT cparBean = (SettingsKnlT)
				  Component.getInstance("cparBean",ScopeType.CONVERSATION);
	   
	   String  sessionId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	   log.info("cparManager:updCpar:sessionId:"+sessionId);
	
	   if(cparBean==null || sessionId==null){
		   return;
	   }
	
	   try {
			   
		  SettingsKnlT aam = entityManager.find(SettingsKnlT.class, new Long(sessionId));
		  
		  aam.setValueParam(cparBean.getValueParam());
				  
		  entityManager.flush();
	      entityManager.refresh(aam);
	    	  
	      Contexts.getEventContext().set("cparBean", aam);
	    	  
	      audit(ResourcesMap.CONF_PARAM, ActionsMap.UPDATE); 
	      
	     }catch (Exception e) {
           log.error("cparManager:updSrm:ERROR:"+e);
         }
   }
 
 
    public void forViewUpdDel() {
	   try{
	     String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	     log.info("forViewUpdDel:sessionId:"+sessionId);
	     if(sessionId!=null){
	    	 SettingsKnlT ao = entityManager.find(SettingsKnlT.class, new Long(sessionId));
	    	 Contexts.getEventContext().set("cparBean", ao);
	   	 }
	   }catch(Exception e){
		   log.error("forViewUpdDel:Error:"+e);
	   }
    } 
  
  
    public SettingsKnlT getSetting(String codeParam){
    	
    	log.info("cparManager:getParam:01:"+codeParam);
    	 
    	try{
    		if(setting==null){
    		    setting = entityManager.createQuery(
					   "select o " +
			           "from SettingsKnlT o " +
			           "where o.signObject = :codeParam ", SettingsKnlT.class)
			           .setParameter("codeParam", codeParam)
	                   .getSingleResult();
    		 
    		}
    	}catch(Exception e){
    		log.info("cparManager:getParam:error:"+e);
    	}
   	    return setting;
   }
   
  public String getSettingValue(String codeParam){
    	
    	log.info("cparManager:getSettingValue:01:"+codeParam);
    	 
    	try{
    		if(settingValue==null){
    			settingValue = (String) entityManager.createQuery(
					   "select o.valueParam " +
			           "from SettingsKnlT o " +
			           "where o.signObject = :codeParam ")
			           .setParameter("codeParam", codeParam)
	                   .getSingleResult();
    		 
    		}
    	}catch(Exception e){
    		log.info("cparManager:getSettingValue:error:"+e);
    	}
   	    return settingValue;
   }

   public int getConnectError(){
	   return connectError;
   }
   
   public void setAuditItemsListSelect(List <BaseTableItem> auditItemsListSelect) {
	    this.auditItemsListSelect=auditItemsListSelect;
}
   
   public List <BaseTableItem> getAuditItemsListSelect() {
		 log.info("getAuditItemsListSelect:01");
	
	    CparContext acCpar= new CparContext();
		   if( auditItemsListSelect==null){
			   log.info("getAuditItemsListSelect:02");
			   auditItemsListSelect = new ArrayList<BaseTableItem>();
			   
			 
			   auditItemsListSelect.add(acCpar.getAuditItemsMap().get("nameParam"));
			   auditItemsListSelect.add(acCpar.getAuditItemsMap().get("valueParam"));
			   auditItemsListSelect.add(acCpar.getAuditItemsMap().get("servName"));
		   }
	       return this.auditItemsListSelect;
   }
   
 
   
   public List <BaseTableItem> getAuditItemsListContext() {
	   log.info("orgManager:getAuditItemsListContext");
	   if(auditItemsListContext==null){
		   CparContext ac= new CparContext();
		   auditItemsListContext = new ArrayList<BaseTableItem>();
		   
		   
		   auditItemsListContext=ac.getAuditItemsCollection();
	   }
	   return this.auditItemsListContext;
   }
      
    
   public void selectRecord(){
	    String  sessionId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	    log.info("selectRecord:sessionId="+sessionId);
	    
	   //  for/View(/); //!!!
	    List<String>  selRecCpar = (ArrayList<String>)
				  Component.getInstance("selRecCpar",ScopeType.SESSION);
	    
	    if(selRecCpar==null){
	       selRecCpar = new ArrayList<String>();
	       log.info("selectRecord:01");
	    }
	    
	    
	    SettingsKnlT aa = new SettingsKnlT();
  	    
	    
	    if(aa!=null){
	     if(selRecCpar.contains(sessionId)){
	    	selRecCpar.remove(sessionId);
	    	aa.setSelected(false);
	    	log.info("selectRecord:02");
	     }else{
	    	selRecCpar.add(sessionId);
	    	aa.setSelected(true);
	    	log.info("selectRecord:03");
	     }
	    Contexts.getSessionContext().set("selRecCpar", selRecCpar);	
	    
	    Contexts.getEventContext().set("cparBean", aa);
	   }
	}
   
  
   public void audit(ResourcesMap resourcesMap, ActionsMap actionsMap){
	   try{
		   AuditExportData auditExportDataCpar = (AuditExportData)Component.getInstance("auditExportData",ScopeType.SESSION);
		   auditExportDataCpar.addFunc(resourcesMap.getCode()+":"+actionsMap.getCode());
		   
	   }catch(Exception eCpar){
		   log.error("GroupManager:audit:error:"+eCpar);
	   }
  }
   
   public String getDellMessage() {
	   return dellMessage;
   }
   public void setDellMessage(String dellMessage) {
	   this.dellMessage = dellMessage;
   } 
   
   
   public Boolean getEvaluteForList() {
	
   	log.info("cparManager:evaluteForList:01");
   	if(evaluteForList==null){
   		evaluteForList=false;
    	String remoteAuditCpar = FacesContext.getCurrentInstance().getExternalContext()
	             .getRequestParameterMap()
	             .get("remoteAudit");
	   log.info("cparManager:evaluteForList:remoteAudit:"+remoteAuditCpar);
     	
    	if(remoteAuditCpar!=null&&
    	 
    	   !"OpenCrtFact".equals(remoteAuditCpar)&&	
    	   !"OpenUpdFact".equals(remoteAuditCpar)&&
    	   !"OpenDelFact".equals(remoteAuditCpar)&&
   	       !"onSelColFact".equals(remoteAuditCpar)&&
   	       !"refreshPdFact".equals(remoteAuditCpar)){
    		log.info("reposManager:evaluteForList!!!");
   		    evaluteForList=true;
    	}
   	 }
       return evaluteForList;
   }
   public Boolean getEvaluteForListFooter() {
		
	  
	   	if(evaluteForListFooter==null){
	   		evaluteForListFooter=false;
	    	String remoteAuditCpar = FacesContext.getCurrentInstance().getExternalContext()
		             .getRequestParameterMap()
		             .get("remoteAudit");
		   log.info("cparManager:evaluteForListFooter:remoteAudit:"+remoteAuditCpar);
	     
	    	if(getEvaluteForList()&&
	    	    	
	    	   !"protBeanWord".equals(remoteAuditCpar)&&	
	    	    	
	   	       !"selRecAllFact".equals(remoteAuditCpar)&&
	   	       !"clRecAllFact".equals(remoteAuditCpar)&&
	   	      // !remoteAudit equals "clSelOneFact"
	   	       !"onSelColSaveFact".equals(remoteAuditCpar)){
	    		  log.info("cparManager:evaluteForListFooter!!!");
	   		      evaluteForListFooter=true;
	    	}
	   	 }
	       return evaluteForListFooter;
	   }
   
   public Boolean getEvaluteForBean() {
		
		  
		   	if(evaluteForBean==null){
		   		evaluteForBean=false;
		    	String remoteAuditCpar = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("remoteAudit");
			    log.info("cparManager:evaluteForBean:remoteAudit:"+remoteAuditCpar);
				String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("sessionId");
			    log.info("cparManager:evaluteForBean:sessionId:"+sessionId);
		    	if(sessionId!=null && remoteAuditCpar!=null &&
		    	   ("rowSelectFact".equals(remoteAuditCpar)||	
		    	    "UpdFact".equals(remoteAuditCpar))){
		    	      log.info("cparManager:evaluteForBean!!!");
		   		      evaluteForBean=true;
		    	}
		   	 }
		     return evaluteForBean;
		   }


}

