package iac.grn.infosweb.context.mc.clorg;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import iac.cud.infosweb.dataitems.BaseItem;
import iac.cud.infosweb.entity.AcLegalEntityType;
import iac.cud.infosweb.entity.IspBssT;
import iac.cud.infosweb.entity.AcUser;
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
@Name("clOrgManager")
 public class ClOrgManager {
	
	 @Logger private Log log;
	
	 @In 
	 EntityManager entityManager;
	 
	/**
     * Ёкспортируема€ сущности 
     * дл€ отображени€
     */
	
	 private String dellMessage;
	 
	private List<BaseItem> auditList; 
	
	private Long auditCount;
	
	private List <BaseTableItem> auditItemsListSelect;
	
	private List <BaseTableItem> auditItemsListContext;
	
	private int connectError=0;
	private Boolean evaluteForList;
	private Boolean evaluteForListFooter;  
	private Boolean evaluteForBean;
	
	private List<AcLegalEntityType> listLET = null;
	
	private List<IspBssT> listOrg = null;
	
 	public List<BaseItem> getAuditList(int firstRow, int numberOfRows){
	  String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
	             .getRequestParameterMap()
	             .get("remoteAudit");
	   log.info("auditManager:getAuditList:remoteAudit:"+remoteAudit);
	  
	  
	  log.info("getAuditList:firstRow:"+firstRow);
	  log.info("getAuditList:numberOfRows:"+numberOfRows);
	  
	  List<BaseItem> clOrgListCached = (List<BaseItem>)
			  Component.getInstance("clOrgListCached",ScopeType.SESSION);
	  if(auditList==null){
		  log.info("getAuditList:01");
		 	if(("rowSelectFact".equals(remoteAudit)||
			    "selRecAllFact".equals(remoteAudit)||
			    "clRecAllFact".equals(remoteAudit)||
			    "clSelOneFact".equals(remoteAudit)||
			    "onSelColSaveFact".equals(remoteAudit))&&
			    clOrgListCached!=null){
			    	this.auditList=clOrgListCached;
			}else{
				log.info("getAuditList:03");
		    	invokeLocal("list", firstRow, numberOfRows, null);
			    Contexts.getSessionContext().set("clOrgListCached", this.auditList);
			    log.info("getAuditList:03:"+this.auditList.size());
			}
		 	
		 	List<String>  selRecClOrg = (ArrayList<String>)
					  Component.getInstance("selRecOrg",ScopeType.SESSION);
		 	if(this.auditList!=null && selRecClOrg!=null) {
		 		 for(BaseItem it:this.auditList){
				   if(selRecClOrg.contains(it.getBaseId().toString())){
					 
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
			 String orderQueryClOrg=null;
			 log.info("ClOrgManager:invokeLocal");
			 
			 if("list".equals(type)){
				 log.info("ClOrg:invokeLocal:list:01");
				 
				 ClOrgStateHolder clOrgStateHolder = (ClOrgStateHolder)
						  Component.getInstance("clOrgStateHolder",ScopeType.SESSION);
				 Set<Map.Entry<String, String>> set = clOrgStateHolder.getSortOrders().entrySet();
                 for (Map.Entry<String, String> me : set) {
      		       
      		       if(orderQueryClOrg==null){
      		    	 orderQueryClOrg="order by o."+me.getKey()+" "+me.getValue();
      		       }else{
      		    	 orderQueryClOrg=orderQueryClOrg+", o."+me.getKey()+" "+me.getValue();  
      		       }
      		     }
                 log.info("ClOrg:invokeLocal:list:orderQueryClOrg:"+orderQueryClOrg);
                 
				 auditList = entityManager.createQuery(
					"select o from IspBssT o  " +
					 (orderQueryClOrg!=null ? orderQueryClOrg : ""))
                       .setFirstResult(firstRow)
                       .setMaxResults(numberOfRows)
                       .getResultList();
				 
             log.info("ClOrg:invokeLocal:list:02");
  
			 } else if("count".equals(type)){
				 log.info("ClOrgList:count:01");
				 auditCount = (Long)entityManager.createQuery(
						 "select count(o) " +
						   "from IspBssT o  ")
		                .getSingleResult();
				 
               log.info("ClOrg:invokeLocal:count:02:"+auditCount);
           	 } 
		}catch(Exception eClOrg){
			  log.error("ClOrg:invokeLocal:error:"+eClOrg);
			  evaluteForList=false;
			  FacesMessages.instance().add("ќшибка!");
		}
	}
	
	
   public void forView(String modelType) {
	   String  clOrgId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	  log.info("forView:clOrgId:"+clOrgId);
	  log.info("forView:modelType:"+modelType);
	  if(clOrgId!=null ){
		  
		   
			if(modelType==null){
		    	return ;
		    }
			
			IspBssT ar = searchBean(clOrgId);
		 Contexts.getEventContext().set("clOrgBean", ar);
	  }
   }
   
   private IspBssT searchBean(String sessionId){
    	
      if(sessionId!=null){
    	 List<IspBssT> clOrgListCached = (List<IspBssT>)
				  Component.getInstance("clOrgListCached",ScopeType.SESSION);
		if(clOrgListCached!=null){
			for(IspBssT it : clOrgListCached){
				 
			 
			  if(it.getBaseId().toString().equals(sessionId)){
					 log.info("searchBean_Achtung!!!");
					 return it;
			  }
			}
		 }
	   }
	   return null;
    }
   
   
   public void addOrg(){
	   log.info("clOrgManager:addOrg:01");
	   
	   IspBssT clOrgBeanCrt = (IspBssT)
				  Component.getInstance("clOrgBeanCrt",ScopeType.CONVERSATION);
	   
	   if(clOrgBeanCrt==null){
		   return;
	   }
	 
	   try {
		  AcUser au = (AcUser) Component.getInstance("currentUser",ScopeType.SESSION); 
		   
	      clOrgBeanCrt.setCreator(au.getIdUser());
		  clOrgBeanCrt.setCreated(new Date());
	      entityManager.persist(clOrgBeanCrt);
	    	   
	      entityManager.flush();
	      entityManager.refresh(clOrgBeanCrt);
	    	     
	    }catch (Exception e) {
	       log.error("clOrgManager:addOrg:ERROR:"+e);
	    }
	   
   }
   
   public Long getAuditCount(){
	   log.info("getAuditCount");
	 
	   invokeLocal("count",0,0,null);
	  
	   return auditCount;
	  
   }
   
   public void updOrg(){
	   
	   log.info("clOrgManager:updOrg:01");
	   
	   IspBssT clOrgBean = (IspBssT)
				  Component.getInstance("clOrgBean",ScopeType.CONVERSATION);
	   
	   String  sessionId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	   log.info("clOrgManager:updOrg:sessionId:"+sessionId);
	
	   if(clOrgBean==null || sessionId==null){
		   return;
	   }
	
	   try {
		   AcUser au = (AcUser) Component.getInstance("currentUser",ScopeType.SESSION);
		   
		 
		  IspBssT aomClOrg = entityManager.find(IspBssT.class, new Long(sessionId));
		  
		  aomClOrg.setFull(clOrgBean.getFull());
		
		  
		  aomClOrg.setModificator(au.getIdUser());
		  aomClOrg.setModified(new Date());
		  
		  entityManager.flush();
	      entityManager.refresh(aomClOrg);
	    	  
	      Contexts.getEventContext().set("clOrgBean", aomClOrg);
	    	  
	     }catch (Exception eClOrg) {
           log.error("clOrgManager:updOrg:ERROR:"+eClOrg);
         }
   }
     public void delOrg(){
	 try{
		log.info("clOrgManager:delOrg:01");  
		
		IspBssT clOrgBean = (IspBssT)
				  Component.getInstance("clOrgBean",ScopeType.CONVERSATION);
		// <h:inputHidden value="#{usrBean.idUser}"/>
		
		if(clOrgBean==null){
			return;
		}
		 
		log.info("clOrgManager:delOrg:IdOrg:"+clOrgBean.getBaseId());
		
		IspBssT aom = entityManager.find(IspBssT.class, clOrgBean.getBaseId());
		  
		entityManager.remove(aom);
		
	 }catch(Exception eClOrg){
		 log.error("clOrgManager:delOrg:error:"+eClOrg); 
	 }
    }
 
    public void forViewUpdDel() {
	   try{
	     String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	     log.info("forViewUpdDel:sessionId:"+sessionId);
	     if(sessionId!=null){
	    	 IspBssT ao = entityManager.find(IspBssT.class, new Long(sessionId));
	    	 Contexts.getEventContext().set("clOrgBean", ao);
	   	 }
	   }catch(Exception e){
		   log.error("forViewUpdDel:Error:"+e);
	   }
    } 
   
    public void forViewDelMessage() {
		
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
  
    public List<IspBssT> getListOrg() throws Exception{
	    log.info("getListOrg:01");
	    try {
	    	if(listOrg==null){
	    		log.info("getListOrg:02");
	    		listOrg=entityManager.createQuery("select o from IspBssT o where o.status='A' and o.signObject like '%00000' ").getResultList();
	    	}
	     } catch (Exception e) {
	    	 log.error("getListOrg:ERROR:"+e);
	         throw e;
	     }
	    return listOrg;
   }
    
    public List<IspBssT> autocomplete(Object suggest) throws Exception{
    	String pref = (String)suggest;
    	
	    log.info("autocomplete:01:pref:"+pref);
	    try {
	    	
	    	if(listOrg==null){
	    		
	    		log.info("autocomplete:02");
	    		
	    		AcUser  cau = (AcUser) Component.getInstance("currentUser",ScopeType.SESSION);
	    		
	    		listOrg=entityManager.createQuery(
	    				"select o from IspBssT o where o.status='A' " +
	    				"and o.signObject like '%00000' " +
	    				"and ( 1 = :orgAccFlag  or o.signObject = :orgCode) " +
	    			//	"and o.full like '"+pref+"%' " +
	    				"and upper(o.full) like upper(:pref) " +
	    				"order by o.full ")
	    				.setParameter("pref", pref+"%")
	    				.setParameter("orgAccFlag", cau.getIsAccOrgManagerValue() ? -1 : 1)
	    				.setParameter("orgCode", cau.getUpSign()!=null? cau.getUpSign():"")
	    				.getResultList();
	    		
	    		log.info("autocomplete:03:size:"+listOrg.size());
	    	}
	     } catch (Exception e) {
	    	 log.error("autocomplete:ERROR:"+e);
	         throw e;
	     }
	    return listOrg;
   }
    
   public int getConnectError(){
	   return connectError;
   }
   
   public List <BaseTableItem> getAuditItemsListSelect() {
		   
	
	    ClOrgContext acClOrg= new ClOrgContext();
		   if( auditItemsListSelect==null){
			   log.info("getAuditItemsListSelect:02");
			   auditItemsListSelect = new ArrayList<BaseTableItem>();
			   
			
			   
			   auditItemsListSelect.add(acClOrg.getAuditItemsMap().get("signObject"));
			   auditItemsListSelect.add(acClOrg.getAuditItemsMap().get("full"));
			   auditItemsListSelect.add(acClOrg.getAuditItemsMap().get("fio"));
			   }
	       return this.auditItemsListSelect;
   }
   
   public void setAuditItemsListSelect(List <BaseTableItem> auditItemsListSelect) {
		    this.auditItemsListSelect=auditItemsListSelect;
   }
   
   public List <BaseTableItem> getAuditItemsListContext() {
	   log.info("clOrgManager:getAuditItemsListContext");
	   if(auditItemsListContext==null){
		   ClOrgContext acClOrg= new ClOrgContext();
		   auditItemsListContext = new ArrayList<BaseTableItem>();
		   
		   
		   auditItemsListContext=acClOrg.getAuditItemsCollection();
	   }
	   return this.auditItemsListContext;
   }
      
    
   public void selectRecord(){
	    String  sessionIdClOrg = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	    log.info("selectRecord:sessionId="+sessionIdClOrg);
	    
	   //  forV/ew(); //!!!
	    List<String>  selRecClOrg = (ArrayList<String>)
				  Component.getInstance("selRecOrg",ScopeType.SESSION);
	    
	    if(selRecClOrg==null){
	       selRecClOrg = new ArrayList<String>();
	       log.info("selectRecord:01");
	    }
	    
	  
	   IspBssT ao = new IspBssT();
	
	    
	    if(ao!=null){
	     if(selRecClOrg.contains(sessionIdClOrg)){
	    	selRecClOrg.remove(sessionIdClOrg);
	    	ao.setSelected(false);
	    	log.info("selectRecord:02");
	     }else{
	    	selRecClOrg.add(sessionIdClOrg);
	    	ao.setSelected(true);
	    	log.info("selectRecord:03");
	     }
	    Contexts.getSessionContext().set("selRecOrg", selRecClOrg);	
	    
	    Contexts.getEventContext().set("clOrgBean", ao);
	    }
	  }
   
  
   public void forViewAutocomplete() {
	   try{
		     String signObject = FacesContext.getCurrentInstance().getExternalContext()
				        .getRequestParameterMap()
				        .get("signObject");
		     log.info("forViewAutocomplete:signObject:"+signObject);
		     if(signObject!=null){
		    	 IspBssT ao = (IspBssT)entityManager.createQuery(
		    				"select o from IspBssT o where o.status='A' " +
		    				"and o.signObject = :signObject ")
		    		    	.setParameter("signObject", signObject)
		    		    	.getSingleResult();
		    	
		    	 
		    	 Contexts.getEventContext().set("clOrgBean", ao);
		   	 }
		   }catch(Exception e){
			   log.error("forViewAutocomplete:Error:"+e);
		   }
   }
   
   public void forViewAutocomplete(String signObject) {
	   try{
		     log.info("forViewAutocomplete:signObject:"+signObject);
		     if(signObject!=null){
		    	 IspBssT ao = (IspBssT)entityManager.createQuery(
		    				"select o from IspBssT o where o.status='A' " +
		    				"and o.signObject = :signObject ")
		    		    	.setParameter("signObject", signObject)
		    		    	.getSingleResult();
		    		 
		    	 Contexts.getEventContext().set("clOrgBean", ao);
		   	 }
		   }catch(Exception e){
			   log.error("forViewAutocomplete:Error:"+e);
		   }
   }
   
   public String getDellMessage() {
	   return dellMessage;
   }
   public void setDellMessage(String dellMessage) {
	   this.dellMessage = dellMessage;
   } 
   
   
   public Boolean getEvaluteForList() {
	
   	log.info("clOrgManager:evaluteForList:01");
   	if(evaluteForList==null){
   		evaluteForList=false;
    	String remoteAuditClOrg = FacesContext.getCurrentInstance().getExternalContext()
	             .getRequestParameterMap()
	             .get("remoteAudit");
	   log.info("clOrgManager:evaluteForList:remoteAudit:"+remoteAuditClOrg);
     	
    	if(remoteAuditClOrg!=null&&
    	 
    	   !"OpenCrtFact".equals(remoteAuditClOrg)&&	
    	   !"OpenUpdFact".equals(remoteAuditClOrg)&&
    	   !"OpenDelFact".equals(remoteAuditClOrg)&&
   	       !"onSelColFact".equals(remoteAuditClOrg)&&
   	       !"refreshPdFact".equals(remoteAuditClOrg)){
    		log.info("reposManager:evaluteForList!!!");
   		    evaluteForList=true;
    	}
   	 }
       return evaluteForList;
   }
   public Boolean getEvaluteForListFooter() {
		
	  
	   	if(evaluteForListFooter==null){
	   		evaluteForListFooter=false;
	    	String remoteAuditClOrg = FacesContext.getCurrentInstance().getExternalContext()
		             .getRequestParameterMap()
		             .get("remoteAudit");
		   log.info("clOrgManager:evaluteForListFooter:remoteAudit:"+remoteAuditClOrg);
	     
	    	if(getEvaluteForList()&&
	    	    	
	    	   !"protBeanWord".equals(remoteAuditClOrg)&&	
	    	    	
	   	       !"selRecAllFact".equals(remoteAuditClOrg)&&
	   	       !"clRecAllFact".equals(remoteAuditClOrg)&&
	   	      // !remoteAudit equals "clSelOneFact"
	   	       !"onSelColSaveFact".equals(remoteAuditClOrg)){
	    		  log.info("clOrgManager:evaluteForListFooter!!!");
	   		      evaluteForListFooter=true;
	    	}
	   	 }
	       return evaluteForListFooter;
	   }
   
   public Boolean getEvaluteForBean() {
		
		  
		   	if(evaluteForBean==null){
		   		evaluteForBean=false;
		    	String remoteAuditClOrg = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("remoteAudit");
			    log.info("clOrgManager:evaluteForBean:remoteAudit:"+remoteAuditClOrg);
				String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("sessionId");
			    log.info("clOrgManager:evaluteForBean:sessionId:"+sessionId);
		    	if(sessionId!=null && remoteAuditClOrg!=null &&
		    	   ("rowSelectFact".equals(remoteAuditClOrg)||	
		    	    "UpdFact".equals(remoteAuditClOrg))){
		    	      log.info("clOrgManager:evaluteForBean!!!");
		   		      evaluteForBean=true;
		    	}
		   	 }
		     return evaluteForBean;
		   }

}

