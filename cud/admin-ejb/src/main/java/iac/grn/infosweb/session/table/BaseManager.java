package iac.grn.infosweb.session.table;

import org.jboss.seam.annotations.Name;


	import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;

import iac.cud.infosweb.dataitems.BaseItem;
import iac.cud.infosweb.entity.AcUser;
import iac.grn.infosweb.session.audit.actions.ActionsMap;
import iac.grn.infosweb.session.audit.actions.ResourcesMap;
import iac.grn.infosweb.session.audit.export.AuditExportData;
import iac.grn.infosweb.session.navig.LinksMap;

import java.util.*;

	import org.jboss.seam.Component;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import iac.grn.serviceitems.BaseTableItem;
import iac.grn.serviceitems.HeaderTableItem;

	/**
	 * ”правл€ющий Ѕин
	 * @author bubnov
	 *
	 */
	
	//bindListCached -> contextListCached
	//selRecBind -> contextSelRec
	//bindBeanView-> contextBeanView
	
	@Name("baseManager")
	 public class BaseManager {
		
		@Logger 
		protected Log log;
		
		
	    @In 
	    protected EntityManager entityManager;
		 
		protected List<BaseItem> auditList; 
		
		protected Long auditCount;
		
		protected List <BaseTableItem> auditItemsListSelect;
		
		protected List <BaseTableItem> auditItemsListContext;
		
		//protected HashMap<String, List<BaseTableItem>> headerItemsListContext;
		
		protected List<HeaderTableItem> headerItemsListContext;
		
		protected Boolean evaluteForList;
		protected Boolean evaluteForListFooter;  
		protected Boolean evaluteForBean;
		
		private LinksMap linksMap = null;
		private AcUser currentUser = null;
		
		private String dellMessage = null;
		
		
		public List<BaseItem> getAuditList(int firstRow, int numberOfRows){
		  String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
		             .getRequestParameterMap()
		             .get("remoteAudit");
		   log.info("baseManager:getAuditList:remoteAudit:"+remoteAudit);
		  
		  
		  log.info("baseManager:getAuditList:firstRow:"+firstRow);
		  log.info("baseManager:getAuditList:numberOfRows:"+numberOfRows);
		  
		  List<BaseItem> contextListCached = (List<BaseItem>)
				  Component.getInstance("contextListCached",ScopeType.SESSION);
		  if(auditList==null){
			  log.info("baseManager:getAuditList:01");
			 	if(("rowSelectFact".equals(remoteAudit)||
				    "selRecAllFact".equals(remoteAudit)||
				    "clRecAllFact".equals(remoteAudit)||
				    "clSelOneFact".equals(remoteAudit)||
				    "onSelColSaveFact".equals(remoteAudit))&&
				    contextListCached!=null){
			 		log.info("baseManager:getAuditList:02:"+contextListCached.size());
				    	this.auditList=contextListCached;
				}else{
					log.info("baseManager:getAuditList:03");
			    	invokeLocal("list", firstRow, numberOfRows, null);
				    Contexts.getSessionContext().set("contextListCached", this.auditList);
				    log.info("baseManager:getAuditList:03:"+this.auditList.size());
				}
			 	
			 	ArrayList<String> contextSelRec = (ArrayList<String>)
						  Component.getInstance("contextSelRec",ScopeType.SESSION);
			 	if(this.auditList!=null && contextSelRec!=null) {
			 		 for(BaseItem it:this.auditList){
					   if(contextSelRec.contains(it.getBaseId().toString())){
						 
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
			
			 log.info("baseManager:invokeLocal");

		}


		
	   public void forView() {
		   String  sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
		  log.info("baseManager:forView:sessionId:"+sessionId);
		   if(sessionId!=null ){
			  
	
			 
				 BaseItem au = searchBean(sessionId);
			 
	
		    	
	
	 
		     Contexts.getEventContext().set("contextBeanView", au);
		  }
	   }
	   
	   protected BaseItem searchBean(String sessionId){
	    	
	      if(sessionId!=null){
	    	 List<BaseItem> contextListCached = (List<BaseItem>)
					  Component.getInstance("contextListCached",ScopeType.SESSION);
			if(contextListCached!=null){
				for(BaseItem it : contextListCached){
					 
				 
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
	   
	   public void add(){
		   log.info("BaseManager:add:01");
		   
	   }
	   
	   public void upd(){
		   
		   log.info("BaseManager:upd:01");
	   }
	   
	   public void del(){
			
				log.info("BaseManager:del:01");  
	   }		
	
	   public void forViewUpdDel() {
		   
		   log.info("forViewUpdDel");
	   } 
	   
	   public List <BaseTableItem> getAuditItemsListSelect() {
	       return this.auditItemsListSelect;
	   }
	   
	   public void setAuditItemsListSelect(List <BaseTableItem> auditItemsListSelect) {
			this.auditItemsListSelect=auditItemsListSelect;
	   }
	   
	   public List <BaseTableItem> getAuditItemsListContext() {
		   return this.auditItemsListContext;
	   }
	      
	   public List<HeaderTableItem> getHeaderItemsListContext() {
		   return this.headerItemsListContext;
	   }
	  
	   public void selectRecord(){
		    String  sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
		   
		    
		   //  forView(); //!!!
		    ArrayList<String> contextSelRec = (ArrayList<String>)
					  Component.getInstance("contextSelRec",ScopeType.SESSION);
		    
		    if(contextSelRec==null){
		    	contextSelRec = new ArrayList<String>();
		       
		    }
		    
		   
		  
		    
		    BaseItem au = new BaseItem();
		    
		 
		    
		    if(au!=null){ 
		     if(contextSelRec.contains(sessionId)){
		    	 contextSelRec.remove(sessionId);
		    	au.setSelected(false);
		    	 
		     }else{
		    	 contextSelRec.add(sessionId);
		    	au.setSelected(true);
		    	 
		    }
		    Contexts.getSessionContext().set("contextSelRec", contextSelRec);	
		    
	       // !!! переделано через getIsSelect()
		   // в centerCenterUnit.xhtml contextBeanView.selected  заменено на appSystemManager.isSelect
		   
		    }
	   }
	   
	 
	   public boolean getIsSelect(){
		   
	      boolean result=false;
	   
		   try{
			   String  sessionId = FacesContext.getCurrentInstance().getExternalContext()
				        .getRequestParameterMap()
				        .get("sessionId");
			    log.info("BaseManager:isSelect:sessionId="+sessionId);

			 	ArrayList<String> contextSelRec = (ArrayList<String>)
						  Component.getInstance("contextSelRec",ScopeType.SESSION);
			 	if(contextSelRec!=null) {
			 		if(contextSelRec.contains(sessionId)){
			 			result=true;
			 		}
			 	}
			    
		   }catch(Exception e){
			   log.error("BaseManager:isSelect:error:"+e);
		   }
		   
		   return result;
	   }
	   public LinksMap getLinksMap() {
		   if(this.linksMap==null){
			   linksMap= (LinksMap)Component.getInstance("linksMap",ScopeType.APPLICATION);
		   }
		   return linksMap;
	   }
	   
	   public AcUser getCurrentUser() {
		   if(this.currentUser==null){
			   currentUser= (AcUser) Component.getInstance("currentUser",ScopeType.SESSION);
		   }
		   return currentUser;
	   }
	   
	   public String getDellMessage() {
		   return dellMessage;
	   }
	   public void setDellMessage(String dellMessage) {
		   this.dellMessage = dellMessage;
	   } 
	   
	   public void audit(ResourcesMap resourcesMap, ActionsMap actionsMap){
		   try{
			   AuditExportData auditExportData = (AuditExportData)Component.getInstance("auditExportData",ScopeType.SESSION);
			   auditExportData.addFunc(resourcesMap.getCode()+":"+actionsMap.getCode());
			   
		   }catch(Exception e){
			   log.error("BaseManager:audit:error:"+e);
		   }
	   }
	   
	   public Boolean getEvaluteForList() {
		
	   	log.info("BaseManager:evaluteForList:01");
	   	if(evaluteForList==null){
	   		evaluteForList=false;
	    	String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
		             .getRequestParameterMap()
		             .get("remoteAudit");
		   log.info("BaseManager:evaluteForList:remoteAudit:"+remoteAudit);
	     	
	    	if(remoteAudit!=null&&
	    	 
	    	   !"OpenCrtFact".equals(remoteAudit)&&	
	    	   !"OpenUpdFact".equals(remoteAudit)&&
	    	   !"OpenDelFact".equals(remoteAudit)&&
	   	       !"onSelColFact".equals(remoteAudit)&&
	   	       !"refreshPdFact".equals(remoteAudit)&&
	   	       !remoteAudit.equals("OpenCommentFact")
	   	    ){
	    		log.info("BaseManager:evaluteForList!!!");
	   		    evaluteForList=true;
	    	}
	   	 }
	       return evaluteForList;
	   }
	   public Boolean getEvaluteForListFooter() {
			
		  
		   	if(evaluteForListFooter==null){
		   		evaluteForListFooter=false;
		    	String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("remoteAudit");
			   log.info("BaseManager:evaluteForListFooter:remoteAudit:"+remoteAudit);
		     
		    	if(getEvaluteForList()&&
		    	    	
		    	   !"protBeanWord".equals(remoteAudit)&&	
		    	    	
		   	       !"selRecAllFact".equals(remoteAudit)&&
		   	       !"clRecAllFact".equals(remoteAudit)&&
		   	      // !remoteAudit equals "clSelOneFact"
		   	       !"onSelColSaveFact".equals(remoteAudit)){
		    		log.info("BaseManager:evaluteForListFooter!!!");
		   		    evaluteForListFooter=true;
		    	}
		   	 }
		       return evaluteForListFooter;
		   }
	   
	   public Boolean getEvaluteForBean() {
			
			  
			   	if(evaluteForBean==null){
			   		evaluteForBean=false;
			    	String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
				             .getRequestParameterMap()
				             .get("remoteAudit");
				    log.info("BaseManager:evaluteForBean:remoteAudit:"+remoteAudit);
					String sessionId = FacesContext.getCurrentInstance().getExternalContext()
				             .getRequestParameterMap()
				             .get("sessionId");
				    log.info("BaseManager:evaluteForBean:sessionId:"+sessionId);
			    	if(sessionId!=null && remoteAudit!=null &&
			    	   ("rowSelectFact".equals(remoteAudit)||	
			    	    "UpdFact".equals(remoteAudit)||
			    	    remoteAudit.equals("CommentFact"))){
			    	      log.info("BaseManager:evaluteForBean!!!");
			   		      evaluteForBean=true;
			    	}
			   	 }
			     return evaluteForBean;
			   }
	
	}

