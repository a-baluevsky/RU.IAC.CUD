package iac.grn.infosweb.context.mc.org;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import iac.cud.infosweb.dataitems.BaseItem;
import iac.cud.infosweb.entity.AcLegalEntityType;
import iac.cud.infosweb.entity.AcOrganization;
import iac.cud.infosweb.entity.AcUser;
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
@Name("orgManager")
 public class OrgManager {
	
	 @Logger private Log log;
	
	 @In 
	 EntityManager entityManager;
	 
	
	 private List <BaseTableItem> auditItemsListSelect;
		
	private List <BaseTableItem> auditItemsListContext;
		
	 private String dellMessage;
	 
	private List<BaseItem> auditList; 
	
	private Long auditCount;
	
	
	private int connectError=0;
	
	private List<AcLegalEntityType> listLET = null;
	
	private List<AcOrganization> listOrg = null;
	
	private Boolean evaluteForList;
	private Boolean evaluteForListFooter;  
	private Boolean evaluteForBean;
	
	
 	public List<BaseItem> getAuditList(int firstRow, int numberOfRows){
	  String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
	             .getRequestParameterMap()
	             .get("remoteAudit");
	   log.info("auditManager:getAuditList:remoteAudit:"+remoteAudit);
	  
	  
	  log.info("getAuditList:firstRow:"+firstRow);
	  log.info("getAuditList:numberOfRows:"+numberOfRows);
	  
	  List<BaseItem> orgListCached = (List<BaseItem>)
			  Component.getInstance("orgListCached",ScopeType.SESSION);
	  if(auditList==null){
		  log.info("getAuditList:01");
		 	if(("rowSelectFact".equals(remoteAudit)||
			    "selRecAllFact".equals(remoteAudit)||
			    "clRecAllFact".equals(remoteAudit)||
			    "clSelOneFact".equals(remoteAudit)||
			    "onSelColSaveFact".equals(remoteAudit))&&
			    orgListCached!=null){
		 	    	this.auditList=orgListCached;
			}else{
				log.info("getAuditList:03");
		    	invokeLocal("list", firstRow, numberOfRows, null);
			    Contexts.getSessionContext().set("orgListCached", this.auditList);
			    log.info("getAuditList:03:"+this.auditList.size());
			}
		 	
		 	List<String>  selRecOrg = (ArrayList<String>)
					  Component.getInstance("selRecOrg",ScopeType.SESSION);
		 	if(this.auditList!=null && selRecOrg!=null) {
		 		 for(BaseItem it:this.auditList){
				   if(selRecOrg.contains(it.getBaseId().toString())){
					 
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
			 String orderQueryOrg=null;
			 log.info("OrgManager:invokeLocal");
			 
			 if("list".equals(type)){
				 log.info("Org:invokeLocal:list:01");
				 
				 OrgStateHolder orgStateHolder = (OrgStateHolder)
						  Component.getInstance("orgStateHolder",ScopeType.SESSION);
				 Set<Map.Entry<String, String>> set = orgStateHolder.getSortOrders().entrySet();
                 for (Map.Entry<String, String> me : set) {
      		       
      		       if(orderQueryOrg==null){
      		    	 orderQueryOrg="order by "+me.getKey()+" "+me.getValue();
      		       }else{
      		    	 orderQueryOrg=orderQueryOrg+", "+me.getKey()+" "+me.getValue();  
      		       }
      		     }
                 log.info("invokeLocal:list:orderQueryOrg:"+orderQueryOrg);
                 
				 auditList = entityManager.createQuery("select o from AcOrganization o "+(orderQueryOrg!=null ? orderQueryOrg : ""))
                       .setFirstResult(firstRow)
                       .setMaxResults(numberOfRows)
                       .getResultList();
             log.info("invokeLocal:list:02");
  
			 } else if("count".equals(type)){
				 log.info("OrgList:count:01");
				 auditCount = (Long)entityManager.createQuery(
						 "select count(o) " +
				         "from AcOrganization o ")
		                .getSingleResult();
				 
               log.info("Org:invokeLocal:count:02:"+auditCount);
           	 } 
		}catch(Exception e){
			  log.error("invokeLocal:error:"+e);
			  evaluteForList=false;
			  FacesMessages.instance().add("Ошибка!");
		}
	}
	
	
   public void forView(String modelType) {
	   String  orgId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	  log.info("forView:orgId:"+orgId);
	  log.info("forView:modelType:"+modelType);
	  if(orgId!=null){
		  
		   
			if(modelType==null){
		    	return ;
		    }
			
		 AcOrganization ar = searchBean(orgId);
		 Contexts.getEventContext().set("orgBean", ar);
	  }
   }
   
   private AcOrganization searchBean(String sessionId){
    	
      if(sessionId!=null){
    	 List<AcOrganization> orgListCached = (List<AcOrganization>)
				  Component.getInstance("orgListCached",ScopeType.SESSION);
		if(orgListCached!=null){
			for(AcOrganization it : orgListCached){
				 
			 
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
   
   public void addOrg(){
	   log.info("orgManager:addOrg:01");
	   
	   AcOrganization orgBeanCrt = (AcOrganization)
				  Component.getInstance("orgBeanCrt",ScopeType.CONVERSATION);
	   
	   if(orgBeanCrt==null){
		   return;
	   }
	 
	   try {
		  AcUser au = (AcUser) Component.getInstance("currentUser",ScopeType.SESSION); 
		   
	      orgBeanCrt.setCreator(au.getIdUser());
		  orgBeanCrt.setCreated(new Date());
	      entityManager.persist(orgBeanCrt);
	    	   
	      entityManager.flush();
	      entityManager.refresh(orgBeanCrt);
	    	     
	    }catch (Exception e) {
	       log.error("orgManager:addOrg:ERROR:"+e);
	    }
	   
   }
   
   public void updOrg(){
	   
	   log.info("orgManager:updOrg:01");
	   
	   AcOrganization orgBean = (AcOrganization)
				  Component.getInstance("orgBean",ScopeType.CONVERSATION);
	   
	   String  sessionId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	   log.info("orgManager:updOrg:sessionId:"+sessionId);
	
	   if(orgBean==null || sessionId==null){
		   return;
	   }
	
	   try {
		   AcUser au = (AcUser) Component.getInstance("currentUser",ScopeType.SESSION);
		   
		 
		  AcOrganization aom = entityManager.find(AcOrganization.class, new Long(sessionId));
		  
		  aom.setFullName(orgBean.getFullName());
		  aom.setShortName(orgBean.getShortName());
		  aom.setContactEmployeeFio(orgBean.getContactEmployeeFio());
		  aom.setContactEmployeePosition(orgBean.getContactEmployeePosition());
		  aom.setContactEmployeePhone(orgBean.getContactEmployeePhone());
		  aom.setAcLegalEntityType(orgBean.getAcLegalEntityType());
		  aom.setIsExternal(orgBean.getIsExternal());
		  
		  aom.setModificator(au.getIdUser());
		  aom.setModified(new Date());
		  
		  entityManager.flush();
	      entityManager.refresh(aom);
	    	  
	      Contexts.getEventContext().set("orgBean", aom);
	    	  
	     }catch (Exception e) {
           log.error("orgManager:updOrg:ERROR:"+e);
         }
   }
   
   public void delOrg(){
	 try{
		log.info("orgManager:delOrg:01");  
		
		AcOrganization orgBean = (AcOrganization)
				  Component.getInstance("orgBean",ScopeType.CONVERSATION);
		// <h:inputHidden value="#{usrBean.idUser}"/>
		
		if(orgBean==null){
			return;
		}
		 
		log.info("orgManager:delOrg:IdOrg:"+orgBean.getIdOrg());
		
		AcOrganization aom = entityManager.find(AcOrganization.class, orgBean.getIdOrg());
		  
		entityManager.remove(aom);
		
	 }catch(Exception e){
		 log.error("orgManager:delOrg:error:"+e); 
	 }
    }
 
    public void forViewUpdDel() {
	   try{
	     String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	     log.info("forViewUpdDel:sessionId:"+sessionId);
	     if(sessionId!=null){
	    	 AcOrganization ao = entityManager.find(AcOrganization.class, new Long(sessionId));
	    	 Contexts.getEventContext().set("orgBean", ao);
	   	 }
	   }catch(Exception e){
		   log.error("forViewUpdDel:Error:"+e);
	   }
    } 
   
    public void forViewDelMessage() {
		  String sessionId = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap()
				.get("sessionId");
		  log.info("forViewDel:sessionId:"+sessionId);
		  if(sessionId!=null){
			 AcOrganization ao = entityManager.find(AcOrganization.class, new Long(sessionId));
					dellMessage="У организации есть порождённые записи! При удалении они будут удалены!";
			 Contexts.getEventContext().set("orgBean", ao);
		 }	
    }
    public List<AcLegalEntityType> getListLET() throws Exception{
	   log.info("getLET");
	    try {
	    	if(listLET==null){
	    	  listLET = entityManager.createQuery("select r from AcLegalEntityType r").getResultList();
	    	 }
	    	} catch (Exception e) {
	    	 log.error("getLET:ERROR="+e);
	         throw e;
           }
	    return listLET;
   }
  
    public List<AcOrganization> getListOrg() throws Exception{
	    log.info("getListOrg:01");
	    try {
	    	if(listOrg==null){
	    		log.info("getListOrg:02");
	    		listOrg=entityManager.createQuery("select o from AcOrganization o").getResultList();
	    	}
	     } catch (Exception e) {
	    	 log.error("getListOrg:ERROR:"+e);
	         throw e;
	     }
	    return listOrg;
   }
    
   public int getConnectError(){
	   return connectError;
   }
   
   public List <BaseTableItem> getAuditItemsListSelect() {
		   
	
	    OrgContext acOrg= new OrgContext();
		   if( auditItemsListSelect==null){
			   log.info("getAuditItemsListSelect:02");
			   auditItemsListSelect = new ArrayList<BaseTableItem>();
			   
			
			   auditItemsListSelect.add(acOrg.getAuditItemsMap().get("fullName"));
			   auditItemsListSelect.add(acOrg.getAuditItemsMap().get("letValue"));
			   auditItemsListSelect.add(acOrg.getAuditItemsMap().get("contactEmployeePosition"));
			   auditItemsListSelect.add(acOrg.getAuditItemsMap().get("contactEmployeeFio"));
			   auditItemsListSelect.add(acOrg.getAuditItemsMap().get("isExternalValue"));
		   }
	       return this.auditItemsListSelect;
   }
   
   public void setAuditItemsListSelect(List <BaseTableItem> auditItemsListSelect) {
		    this.auditItemsListSelect=auditItemsListSelect;
   }
   
   public List <BaseTableItem> getAuditItemsListContext() {
	   log.info("orgManager:getAuditItemsListContext");
	   if(auditItemsListContext==null){
		   OrgContext acOrg= new OrgContext();
		   auditItemsListContext = new ArrayList<BaseTableItem>();
		   
		   
		   auditItemsListContext=acOrg.getAuditItemsCollection();
	   }
	   return this.auditItemsListContext;
   }
   public String getDellMessage() {
	   return dellMessage;
   }
   public void setDellMessage(String dellMessage) {
	   this.dellMessage = dellMessage;
   }    
    
   public void selectRecord(){
	    String  sessionId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	    log.info("selectRecord:sessionId="+sessionId);
	    
	   //  for/View(/); //!!!
	    List<String>  selRecOrg = (ArrayList<String>)
				  Component.getInstance("selRecOrg",ScopeType.SESSION);
	    
	    if(selRecOrg==null){
	       selRecOrg = new ArrayList<String>();
	       log.info("selectRecord:01");
	    }
	    
	  
	   AcOrganization ao = new AcOrganization();
	 
	    
	    if(ao!=null){
	     if(selRecOrg.contains(sessionId)){
	    	selRecOrg.remove(sessionId);
	    	ao.setSelected(false);
	    	log.info("selectRecord:02");
	     }else{
	    	selRecOrg.add(sessionId);
	    	ao.setSelected(true);
	    	log.info("selectRecord:03");
	     }
	    Contexts.getSessionContext().set("selRecOrg", selRecOrg);	
	    
	    Contexts.getEventContext().set("orgBean", ao);
	    }
	  }
   
  
   
   public Boolean getEvaluteForList() {
	
   	log.info("orgManager:evaluteForList:01");
   	if(evaluteForList==null){
   		evaluteForList=false;
    	String remoteAuditOrg = FacesContext.getCurrentInstance().getExternalContext()
	             .getRequestParameterMap()
	             .get("remoteAudit");
	   log.info("orgManager:evaluteForList:remoteAudit:"+remoteAuditOrg);
     	
    	if(remoteAuditOrg!=null&&
    	 
    	   !"OpenCrtFact".equals(remoteAuditOrg)&&	
    	   !"OpenUpdFact".equals(remoteAuditOrg)&&
    	   !"OpenDelFact".equals(remoteAuditOrg)&&
   	       !"onSelColFact".equals(remoteAuditOrg)&&
   	       !"refreshPdFact".equals(remoteAuditOrg)){
    		log.info("reposManager:evaluteForList!!!");
   		    evaluteForList=true;
    	}
   	 }
       return evaluteForList;
   }
   public Boolean getEvaluteForListFooter() {
		
	  
	   	if(evaluteForListFooter==null){
	   		evaluteForListFooter=false;
	    	String remoteAuditOrg = FacesContext.getCurrentInstance().getExternalContext()
		             .getRequestParameterMap()
		             .get("remoteAudit");
		   log.info("orgManager:evaluteForListFooter:remoteAudit:"+remoteAuditOrg);
	     
	    	if(getEvaluteForList()&&
	    	    	
	    	   !"protBeanWord".equals(remoteAuditOrg)&&	
	    	    	
	   	       !"selRecAllFact".equals(remoteAuditOrg)&&
	   	       !"clRecAllFact".equals(remoteAuditOrg)&&
	   	      // !remoteAudit equals "clSelOneFact"
	   	       !"onSelColSaveFact".equals(remoteAuditOrg)){
	    		  log.info("orgManager:evaluteForListFooter!!!");
	   		      evaluteForListFooter=true;
	    	}
	   	 }
	       return evaluteForListFooter;
	   }
   
   public Boolean getEvaluteForBean() {
		
		  
		   	if(evaluteForBean==null){
		   		evaluteForBean=false;
		    	String remoteAuditOrg = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("remoteAudit");
			    log.info("orgManager:evaluteForBean:remoteAudit:"+remoteAuditOrg);
				String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("sessionId");
			    log.info("orgManager:evaluteForBean:sessionId:"+sessionId);
		    	if(sessionId!=null && remoteAuditOrg!=null &&
		    	   ("rowSelectFact".equals(remoteAuditOrg)||	
		    	    "UpdFact".equals(remoteAuditOrg))){
		    	      log.info("orgManager:evaluteForBean!!!");
		   		      evaluteForBean=true;
		    	}
		   	 }
		     return evaluteForBean;
		   }

}

