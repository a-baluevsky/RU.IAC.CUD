package iac.grn.infosweb.context.mc.ugroup;

import iac.cud.infosweb.dataitems.BaseItem;
import iac.cud.infosweb.entity.AcApplication;
import iac.cud.infosweb.entity.AcRole;
import iac.cud.infosweb.entity.AcUser;
import iac.cud.infosweb.entity.GroupUsersKnlT;
import iac.cud.infosweb.entity.LinkGroupUsersRolesKnlT;
import iac.grn.infosweb.session.audit.actions.ActionsMap;
import iac.grn.infosweb.session.audit.actions.ResourcesMap;
import iac.grn.infosweb.session.audit.export.AuditExportData;
import iac.grn.serviceitems.BaseTableItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

/**
 * Управляющий Бин
 * @author bubnov
 *
 */
@Name("ugroupManager")
 public class UgroupManager {
	
	 @Logger private Log log;
	
	 @In 
	 EntityManager entityManager;
	 
	private int connectError=0;
	private Boolean evaluteForList;
	private Boolean evaluteForListFooter;  
	private Boolean evaluteForBean;

	
	private List <BaseTableItem> auditItemsListSelect;
		
	private List <BaseTableItem> auditItemsListContext;
		
	private String dellMessage;
	 
	private List<BaseItem> auditList; 
	
	private Long auditCount;
	
	
    private List<BaseItem> roleList;
	
	private List<AcApplication> listGroupArmForView = null;

		
	private List<AcUser> usrList;
	
	private List<String> usrSelectList;
	
	private List<String> usrSelectEditList;
	
	private List<AcUser> usrSelectListForView;
	
		
	 
	public List<BaseItem> getAuditList(int firstRow, int numberOfRows){
	  String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
	             .getRequestParameterMap()
	             .get("remoteAudit");
	   log.info("auditManager:getAuditList:remoteAudit:"+remoteAudit);
	  
	  
	  log.info("getAuditList:firstRow:"+firstRow);
	  log.info("getAuditList:numberOfRows:"+numberOfRows);
	  
	  List<BaseItem> ugroupListCached = (List<BaseItem>)
			  Component.getInstance("ugroupListCached",ScopeType.SESSION);
	  if(auditList==null){
		  log.info("getAuditList:01");
		 	if(("rowSelectFact".equals(remoteAudit)||
			    "selRecAllFact".equals(remoteAudit)||
			    "clRecAllFact".equals(remoteAudit)||
			    "clSelOneFact".equals(remoteAudit)||
			    "onSelColSaveFact".equals(remoteAudit))&&
			    ugroupListCached!=null){
		 	    	this.auditList=ugroupListCached;
			}else{
				log.info("getAuditList:03");
		    	invokeLocal("list", firstRow, numberOfRows, null);
			    Contexts.getSessionContext().set("ugroupListCached", this.auditList);
			    log.info("getAuditList:03:"+this.auditList.size());
			}
		 	
		 	List<String>  selRecUgroup = (ArrayList<String>)
					  Component.getInstance("selRecUgroup",ScopeType.SESSION);
		 	if(this.auditList!=null && selRecUgroup!=null) {
		 		 for(BaseItem it:this.auditList){
				   if(selRecUgroup.contains(it.getBaseId().toString())){
					 
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
			 String orderQueryUgroup=null;
			 log.info("UgroupManager:invokeLocal");
			 
			 if("list".equals(type)){
				 log.info("Ugroup:invokeLocal:list:01");
				 
				 UgroupStateHolder ugroupStateHolder = (UgroupStateHolder)
						  Component.getInstance("ugroupStateHolder",ScopeType.SESSION);
				 Set<Map.Entry<String, String>> set = ugroupStateHolder.getSortOrders().entrySet();
                 for (Map.Entry<String, String> me : set) {
      		        
      		       if(orderQueryUgroup==null){
      		    	 orderQueryUgroup="order by o."+me.getKey()+" "+me.getValue();
      		       }else{
      		    	 orderQueryUgroup=orderQueryUgroup+", o."+me.getKey()+" "+me.getValue();  
      		       }
      		     }
                 log.info("Ugroup:invokeLocal:list:orderQueryUgroup:"+orderQueryUgroup);
                 
				 auditList = entityManager.createQuery("select o from GroupUsersKnlT o "+(orderQueryUgroup!=null ? orderQueryUgroup+", o.idSrv " : " order by o.idSrv "))
                       .setFirstResult(firstRow)
                       .setMaxResults(numberOfRows)
                       .getResultList();
             log.info("Ugr:invokeLocal:list:02");
  
			 } else if("count".equals(type)){
				 log.info("UgroupList:count:01");
				 auditCount = (Long)entityManager.createQuery(
						 "select count(o) " +
				         "from GroupUsersKnlT o ")
		                .getSingleResult();
				 
               log.info("Ugroup:invokeLocal:count:02:"+auditCount);
           	 }
			 
		}catch(Exception eUgr){
			  log.error("Ugroup:invokeLocal:error:"+eUgr);
			  evaluteForList=false;
			  FacesMessages.instance().add("Ошибка!");
		}
	}


   public void forView(String modelType) {
	   String  uGroupId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	   
	  log.info("ugroupManager:forView:01:"+uGroupId);
	   
	  if(uGroupId!=null ){
		  
		   
			if(modelType==null){
		    	return ;
		    }
			
		 GroupUsersKnlT ar = searchBean(uGroupId);
		 
		 if(!isAllowedSys(new Long(uGroupId))){
			 log.info("ugroupManager:forView:02");
			 ar.setIsAllowedSys(false);
		 }
		 
		 Contexts.getEventContext().set("ugroupBean", ar);
	  }
   }
   
   public void setAuditList(List<BaseItem> auditList){
		this.auditList=auditList;
	}
   
   public boolean isAllowedSys(Long idGr){
	   log.info("ugroupManager:isAllowedSys:01:"+idGr);
	 
	   boolean result = false;
	   
	   try {
	   
		  AcUser au = (AcUser) Component.getInstance("currentUser",ScopeType.SESSION); 
  		 
   		  if(au.getAllowedSys()!=null){
   		   
   			//список ИС в группе, которых нет в привязке к пользователю  
		    List<Object> list= entityManager.createNativeQuery(
                        "SELECT RL.UP_IS " + 
                        "  FROM GROUP_USERS_KNL_T gr, " + 
                        "       LINK_GROUP_USERS_ROLES_KNL_T lgru, " + 
                        "       AC_ROLES_BSS_T rl " + 
                        " WHERE GR.ID_SRV = LGRU.UP_GROUP_USERS  " + 
                        " AND LGRU.UP_ROLES = RL.ID_SRV " + 
                        " and GR.ID_SRV = :idGr " + 
                        " and RL.UP_IS not in (:idsArm) " + 
                        "group by RL.UP_IS")
                      .setParameter("idGr", idGr)
                      .setParameter("idsArm", au.getAllowedSys())
                      .getResultList();
		    
		    //если нет групп, которые не привязаны к пользователю,
		    //тогда у пользователя есть доступ к этой группе
		    //в т. ч. пустые группы
		    if(list.isEmpty()){
		    	result = true;
		    }
		    
	    }else{
	    	result = true;
	    }
	   
	   }catch (Exception e) {
	       log.error("ugroupManager:isAllowedSys:ERROR:"+e);
	    }
	   
	   log.info("ugroupManager:isAllowedSys:02:"+result);
	   
	   return result;
	}
   
   
   private  GroupUsersKnlT searchBean(String sessionId){
    	
      if(sessionId!=null){
    	 List<GroupUsersKnlT> ugroupListCached = (List<GroupUsersKnlT>)
				  Component.getInstance("ugroupListCached",ScopeType.SESSION);
    	 
		if(ugroupListCached!=null){
			for(GroupUsersKnlT it : ugroupListCached){
				 
			 
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
   
   public void addUgroup(){
	  log.info("ugroupManager:addugroup:01");
	   
	   
	  GroupUsersKnlT ugroupBeanCrt = (GroupUsersKnlT)
				  Component.getInstance("ugroupBeanCrt",ScopeType.CONVERSATION);
	   
	   if(ugroupBeanCrt==null){
		   return;
	   }
	 
	   try {
		  AcUser au = (AcUser) Component.getInstance("currentUser",ScopeType.SESSION); 
		   
		  if(ugroupBeanCrt.getDescription()!=null&&!ugroupBeanCrt.getDescription().trim().equals("")){
			  ugroupBeanCrt.setDescription(ugroupBeanCrt.getDescription().trim());
		  }else{
			  ugroupBeanCrt.setDescription(null);
		  }
		  
		  
		  ugroupBeanCrt.setCreator(au.getIdUser());
		  ugroupBeanCrt.setCreated(new Date());
		  
		 
		  
		  entityManager.persist(ugroupBeanCrt);
		  
		  
		  ugroupBeanCrt.setSignObject("urn:group-users:"+ugroupBeanCrt.getIdSrv());
		  
		  entityManager.flush();
		  
		
	  		  
		  audit(ResourcesMap.UGROUP, ActionsMap.CREATE); 
		  
	    }catch (Exception e) {
	       log.error("ugroupManager:addugroup:ERROR:"+e);
	    }
	  
   }
   
   public void updUgroup(){
	  
	   log.info("ugroupManager:updugroup:01");
	   
	      
	   GroupUsersKnlT ugroupBean = (GroupUsersKnlT)
				  Component.getInstance("ugroupBean",ScopeType.CONVERSATION);
	   
	   String  sessionId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	   log.info("ugroupManager:updugroup:sessionId:"+sessionId);
	
	   if(ugroupBean==null || sessionId==null){
		   return;
	   }
	
	   try {
		  AcUser au = (AcUser) Component.getInstance("currentUser",ScopeType.SESSION);
		   
		  GroupUsersKnlT aam = entityManager.find(GroupUsersKnlT.class, new Long(sessionId));
		  
		  aam.setFull(ugroupBean.getFull());
		  
		  if(ugroupBean.getDescription()!=null&&!ugroupBean.getDescription().trim().equals("")){
		     aam.setDescription(ugroupBean.getDescription().trim());
		  }else{
			  aam.setDescription(null);
		  }
		  
		  aam.setModificator(au.getIdUser());
		  aam.setModified(new Date());
		 
		  
		
		  entityManager.flush();
		  
	       Contexts.getEventContext().set("ugroupBean", aam);
	    
	      audit(ResourcesMap.UGROUP, ActionsMap.UPDATE); 
	      
	     }catch (Exception e) {
           log.error("armManager:updSrm:ERROR:"+e);
         }
   }
  
   
 
 public void updUgroupRole(){
	   
	   log.info("ugroupManager:updUgroupRole:01");
	   
	   
	   GroupUsersKnlT ugroupBean = (GroupUsersKnlT)
				  Component.getInstance("ugroupBean",ScopeType.CONVERSATION);
	   
	   String idArm = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("idArm");
	   String  sessionId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	   log.info("ugroupManager:updUgroupRole:idArm:"+idArm);
	   log.info("ugroupManager:updUgroupRole:sessionId:"+sessionId);
	
	   if(ugroupBean==null || sessionId==null || idArm==null){
		   return;
	   }
	
	   try {
		   
		  GroupUsersKnlT aum = entityManager.find(GroupUsersKnlT.class, new Long(sessionId));
			
		  List<LinkGroupUsersRolesKnlT> guuExistList =  aum.getLinkGroupUsersRolesKnlTs();
		  
		  log.info("usrManager:updUsrRole:size1:"+guuExistList.size());
		  
	      for(BaseItem rol:this.roleList){
	    			  log.info("ugroupManager:updUgroupRole:"+((AcRole)rol).getRoleTitle());
	    			  log.info("ugroupManager:updUgroupRole:"+((AcRole)rol).getUsrChecked());
	    			  
	    			  if(((AcRole)rol).getUsrChecked().booleanValue()){ //отмечен
	    				  LinkGroupUsersRolesKnlT au = new LinkGroupUsersRolesKnlT(((AcRole)rol).getIdRol(), new Long(sessionId));
	    			            au.setCreated(new Date());
	    			            au.setCreator(new Long(1));
	    			            
	    			            if(guuExistList.contains(au)){
	    			            	//есть в базе
		    			        	 //ничего не делаем. и так есть в базе
		    			        	 log.info("ugroupManager:updUgroupRole:02");
		    			         }else{
		    			           
		    			            guuExistList.add(au);
		    			            log.info("ugroupManager:updUgroupRole:03");
		    			        }
	    			  }else{ //не отмечен
	    				  LinkGroupUsersRolesKnlT au = new LinkGroupUsersRolesKnlT(((AcRole)rol).getIdRol(), new Long(sessionId));
			               
			                if(guuExistList.contains(au)){
			                	//есть в базе
			                	
			                	guuExistList.remove(au);
			                	
			                	entityManager.createQuery(
			                		"delete from LinkGroupUsersRolesKnlT lgu " +
			                		"where lgu.pk.groupUser = :groupUser " +
			                		"and lgu.pk.acRole = :acRole ")
			                	.setParameter("groupUser", new Long(sessionId))
			                	.setParameter("acRole", ((AcRole)rol).getIdRol())
			                	.executeUpdate();
			                	
			                	 log.info("ugroupManager:updUgroupRole:04");
			                }else{
			                //ничего не делаем. и так нет в базе
			                	 log.info("ugroupManager:updUgroupRole:05");
			                }
	    			  }
	    		  }
	     	  
	    	log.info("ugroupManager:updUgroupRole:size3:"+aum.getLinkGroupUsersRolesKnlTs().size());
	    	  
	        entityManager.flush();
	    	 
	        entityManager.refresh(aum);
	    	  
	        Contexts.getEventContext().set("ugroupBean", aum);
	    
	      
	        ugroupRoleReset();
	    	
	    	audit(ResourcesMap.UGROUP, ActionsMap.UPDATE_ROLE);
	    	
	     }catch (Exception e) {
         log.error("ugroupManager:updUgroupRole:ERROR:"+e);
       }
 }
 
 
   public void delUgroup(){
	 try{
		log.info("ugroupManager:delugroup:01");  
		
		GroupUsersKnlT armBean = (GroupUsersKnlT)
				  Component.getInstance("ugroupBean",ScopeType.CONVERSATION);
		
		
		if(armBean==null){
			return;
		}
		 
		log.info("ugroupManager:delugroup:Idugroup:"+armBean.getIdSrv());
		
		GroupUsersKnlT aom = entityManager.find(GroupUsersKnlT.class, armBean.getIdSrv());
		  
		entityManager.remove(aom);
		
		audit(ResourcesMap.UGROUP, ActionsMap.DELETE);
		
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
	    	 GroupUsersKnlT ao = entityManager.find(GroupUsersKnlT.class, new Long(sessionId));
	    	 Contexts.getEventContext().set("ugroupBean", ao);
	    	 
	    	//устанавливаем на 1 страницу пагинатор в модальном окне
	    	 UgroupStateHolder ugroupStateHolder = (UgroupStateHolder)
					  Component.getInstance("ugroupStateHolder",ScopeType.SESSION);
	    	 ugroupStateHolder.resetPageNumber();
	    	 
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
			  GroupUsersKnlT aa = entityManager.find(GroupUsersKnlT.class, new Long(sessionId));
			
			 Contexts.getEventContext().set("ugroupBean", aa);
		 }	
    }
  
    public void forViewWord(String fileNameUgr){
		log.info("JournManager:forViewWord:01");
		try{
		 
			if(fileNameUgr==null || fileNameUgr.equals("")){
				fileNameUgr="invoke_services";
			}
			
		  HttpServletResponse response = (HttpServletResponse)
				  FacesContext.getCurrentInstance().getExternalContext().getResponse();
		  response.setHeader("Content-disposition", "attachment; filename="+fileNameUgr+".doc");
	      response.setContentType("application/msword");

		}catch(Exception eUgr){
			log.error("auditReportsManager:forViewWord:error:"+eUgr);
		}
		log.info("auditReportsManager:forViewWord:02");
	}
   
    public List<AcUser> getUsrList(){
    	
    	//!!!
    	//не используется
    	
    	log.info("ugroupManager:getUsrList:01");
    	
    	List<Object[]> lo=null;
    	if(this.usrList==null){
    	
    		lo=entityManager.createNativeQuery(
   		    		"select t1.t1_id, t1.t1_login, t1.t1_fio "+
                    "from ( "+
                    "select  AU_FULL.ID_SRV t1_id, AU_FULL.LOGIN t1_login, "+           
                    "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO) t1_fio "+ 
                    "from  AC_USERS_KNL_T AU_FULL, "+ 
                    "ISP_BSS_T cl_usr_full, "+
                    "(select max(CL_usr.ID_SRV) CL_USR_ID, CL_USR.SIGN_OBJECT CL_USR_CODE "+
                    "from ISP_BSS_T cl_usr, "+ 
                    "AC_USERS_KNL_T au "+ 
                    "where AU.UP_SIGN_USER  = CL_usr.SIGN_OBJECT "+ 
                    "group by CL_usr.SIGN_OBJECT) t2 "+ 
                    "where  AU_FULL.UP_SIGN_USER=t2.CL_USR_CODE(+) "+ 
                    "and CL_USR_FULL.ID_SRV(+)=t2.CL_USR_ID "+
                    "and AU_FULL.STATUS!=2 "+ 
                    ") t1 " +
                    "order by t1_fio ")
   		    	 .getResultList();
   	          
   	       this.usrList=new ArrayList<AcUser>();
   	       
   	       for(Object[] objectArray :lo){
   	    	 AcUser au = new AcUser();
   	    	 
   	    	 this.usrList.add(au);
   	    	 
   	    	 au.setIdUser(new Long(objectArray[0].toString()));
   	    	 au.setFio(objectArray[2]!=null?objectArray[2].toString():"");
   	    	 au.setLogin(objectArray[1]!=null?objectArray[1].toString():"");
   	       }
   	      	 
      	}
    	return this.usrList;
    }
    
    
    
    
    public List<String> getUsrSelectList(){
       	return this.usrSelectList;
    }
    public void setUsrSelectList(List<String> usrSelectList){
    	this.usrSelectList=usrSelectList;
    }
    
    public List<String> getUsrSelectEditList(){
    	
      try{
    		
    	if(this.usrSelectEditList==null){
    		String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
	             .getRequestParameterMap()
	             .get("remoteAudit");
    		String sessionId = FacesContext.getCurrentInstance().getExternalContext()
   	             .getRequestParameterMap()
   	             .get("sessionId");
    		
    		if(remoteAudit==null||sessionId==null){
    			return null;
    		}
    		
	        log.info("ugroupManager:getUsrSelectEditList:remoteAudit:"+remoteAudit);
	        log.info("ugroupManager:getUsrSelectEditList:sessionId:"+sessionId);
	        
	        if(!"UpdFact".equals(remoteAudit)){
		      this.usrSelectEditList=entityManager.createQuery(
		    		"select o.pk.acUser from LinkGroupUsersUsersKnlT o " +
		      		"where o.pk.groupUser = :sessionId ")
		      		.setParameter("sessionId", new Long(sessionId))
				 .getResultList();
	    	}
    	 }
    	}catch(Exception e){
			log.error("ugroupManager:getUsrSelectEditList:error:"+e);
		}
       	return this.usrSelectEditList;
    }
    public void setUsrSelectEditList(List<String> usrSelectEditList){
    	this.usrSelectEditList=usrSelectEditList;
    }
    
  
    
   public List<AcUser> getUsrSelectListForView(){
    	
	   List<Object[]> loUgr=null;
       try{
      		
      	if(this.usrSelectListForView==null){
      		String remoteAuditUgr = FacesContext.getCurrentInstance().getExternalContext()
  	             .getRequestParameterMap()
  	             .get("remoteAudit");
      		String sessionId = FacesContext.getCurrentInstance().getExternalContext()
     	             .getRequestParameterMap()
     	             .get("sessionId");
      		
      		if(remoteAuditUgr==null||sessionId==null){
      			return null;
      		}
      		
  	        log.info("ugroupManager:getUsrSelectListForView:remoteAudit:"+remoteAuditUgr);
  	        log.info("ugroupManager:getUsrSelectListForViewt:sessionId:"+sessionId);
  	       
  	        
  	       loUgr=entityManager.createNativeQuery(
 		    		"select t1.t1_id, t1.t1_login, t1.t1_fio "+
                    "from (select AU_FULL.ID_SRV t1_id, AU_FULL.LOGIN t1_login, "+  
                   "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO) t1_fio "+
                      "from "+ 
                      "AC_USERS_KNL_T AU_full, "+ 
                      "LINK_GROUP_USERS_USERS_KNL_T uul, "+ 
                      "ISP_BSS_T CL_USR_FULL, "+
                      "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "+ 
                          "from ISP_BSS_T cl_usr, "+ 
                          "AC_USERS_KNL_T au "+ 
                         "where AU.UP_SIGN_USER  = CL_usr.SIGN_OBJECT "+ 
                         "group by CL_usr.SIGN_OBJECT) t2 "+   
                      "where  AU_FULL.UP_SIGN_USER=t2.CL_USR_CODE(+) "+ 
                      "and CL_USR_FULL.ID_SRV(+)=t2.CL_USR_ID "+
                      "and UUL.UP_USERS= AU_FULL.ID_SRV "+ 
                      "and UUL.UP_GROUP_USERS=? "+ 
                     //!!!
					 "and AU_FULL.STATUS !=3 "+
                      "order by t1_fio "+ 
                      ") t1")
 		      		.setParameter(1, new Long(sessionId))
 				 .getResultList();
  	    		         
  	       this.usrSelectListForView=new ArrayList<AcUser>();
  	       
  	       for(Object[] objectArray :loUgr){
  	    	 AcUser au = new AcUser();
  	    	 
  	    	 this.usrSelectListForView.add(au);
  	    	 
  	    	 //не используется при только отображении
  	    	 
  	    	 au.setFio(objectArray[2]!=null?objectArray[2].toString():"");
  	    	 au.setLogin(objectArray[1]!=null?objectArray[1].toString():"");
  	       }
  	         
      	 }
      	}catch(Exception eUgr){
  			log.error("ugroupManager:getUsrSelectListForView:error:"+eUgr);
  		}
         	return this.usrSelectListForView;
      }
   
  
    
    public List<BaseItem> getRoleList(){
    	
 	   if(this.roleList==null){
 		   String idArm = FacesContext.getCurrentInstance().getExternalContext()
 			        .getRequestParameterMap()
 			        .get("idArm");
 		   String sessionId = FacesContext.getCurrentInstance().getExternalContext()
 			        .getRequestParameterMap()
 			        .get("sessionId");
 		   String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
 			        .getRequestParameterMap()
 			        .get("remoteAudit");
 		   log.info("UgroupManager:getRoleList:idArm:"+idArm);
 		   log.info("UgroupManager:getRoleList:sessionId:"+sessionId);
 		   log.info("UgroupManager:getRoleList:remoteAudit:"+remoteAudit);
 		   
 		   if(idArm==null||sessionId==null){
 			   return this.roleList;
 		   }
 		   
 		   String st=null;
 			 
 		  UgroupRoleStateHolder ugroupRoleStateHolder = (UgroupRoleStateHolder)
 					  Component.getInstance("ugroupRoleStateHolder",ScopeType.SESSION);
 		   Map<String, String> filterMap = ugroupRoleStateHolder.getColumnFilterValues();
 		   
 		   
 		   if(filterMap!=null){
 	    		 Set<Map.Entry<String, String>> setFilterUgroupRole = filterMap.entrySet();
 	              for (Map.Entry<String, String> me : setFilterUgroupRole) {
 	            	
 	             //аналог
 	            if(me.getValue()!=null&&!"".equals(me.getValue())){
 	            	
 	   		   		//делаем фильтр на начало
 	            	  st=(st!=null?st+" and " :"")+" lower("+me.getKey()+") like lower('"+me.getValue()+"%') ";
 	        	
 	             }
 	           }
 	      }
 		   log.info("UgroupManager:getGroupList:st:"+st);
 		   
 		   
 		   this.roleList = entityManager.createQuery(
 				   "select o from AcRole o where o.acApplication= :idArm " +
 					(st!=null ? " and "+st :" ")+
 				   "order by o.roleTitle ")
 				   .setParameter("idArm", new Long(idArm))
                    .getResultList();
 		   
 		   List<AcRole> listUsrRol=entityManager.createQuery(
		    		 "select o from AcRole o,  LinkGroupUsersRolesKnlT o1 " +
		    		 "where o1.pk.acRole = o.idRol " +
		    		 "and o1.pk.groupUser = :groupUser ")
					 .setParameter("groupUser", new Long(sessionId))
		      		 .getResultList();
 		   
 		     for(BaseItem role :this.roleList){
 	           if (listUsrRol.contains((AcRole)role)){  
 	        	  ((AcRole)role).setUsrChecked(true);
 			   }
 	         } 
 	   }
 	   return this.roleList;
    }
    
    
    public void setRoleList(List<BaseItem> roleList){
 	   this.roleList=roleList;
    }
    
    public void ugroupRoleReset(){
  	   if(this.roleList!=null){
  		   this.roleList.clear();
  		   this.roleList=null;  
  	   }
    }
  
    
   
    
    public List<AcApplication> getListGroupArmForView() throws Exception{
	    log.info("GroupManager:getListUGroupArmForView:01");
	   
	    List<Object[]> lo=null;
	    AcApplication app = null;
	    AcRole rol = null;
	    
	    String sessionId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	    
	    log.info("GroupManager:getListGroupArmForView:sessionId:"+sessionId);
	    
	    try {
	    	
	    	if(listGroupArmForView==null && sessionId!=null){
	      	
	    		lo=entityManager.createNativeQuery(
	    				"select  APP.ID_SRV app_id, APP.FULL_ app_name, ROL.FULL_ role_name "+
                        "from LINK_GROUP_USERS_ROLES_KNL_T lur, AC_ROLES_BSS_T rol, AC_IS_BSS_T app "+
                        "where  LUR.UP_GROUP_USERS=? and ROL.ID_SRV=LUR.UP_ROLES "+
                        "and APP.ID_SRV=ROL.UP_IS "+
                        "order by  APP.FULL_, APP.ID_SRV, ROL.FULL_ ")
	    				.setParameter(1, new Long(sessionId))
	    				.getResultList();
	    		
	    		listGroupArmForView=new ArrayList<AcApplication>();
	    		
                 for(Object[] objectArray :lo){
	    			
	    			if(app==null||!app.getIdArm().toString().equals(objectArray[0].toString())){
	    			  
	    		       app=new AcApplication();
	    			   
	    		       listGroupArmForView.add(app);
	    			   
	    			   app.setIdArm(new Long(objectArray[0].toString()));
	    			   app.setName(objectArray[1]!=null?objectArray[1].toString():"");
	    			   app.setRolList(new ArrayList<AcRole>());
	    			 }
	    			 
	    			 rol=new AcRole();
	    			 rol.setRoleTitle(objectArray[2]!=null?objectArray[2].toString():"");
	    			 
	    			 app.getRolList().add(rol);
	    		 }
	    	 }
		} catch (Exception e) {
	    	 log.error("GroupManager:getListGroupArmForView:ERROR:"+e);
	         throw e;
	     }
	    return listGroupArmForView;
   }
    
    public int getConnectError(){
	   return connectError;
    }
    
    public void setAuditItemsListSelect(List <BaseTableItem> auditItemsListSelect) {
	    this.auditItemsListSelect=auditItemsListSelect;
  }
   public List <BaseTableItem> getAuditItemsListSelect() {
		 log.info("getAuditItemsListSelect:01");
	
	    UgroupContext acUgr= new UgroupContext();
		   if( auditItemsListSelect==null){
			   log.info("getAuditItemsListSelect:02");
			   auditItemsListSelect = new ArrayList<BaseTableItem>();
			   
			
			   auditItemsListSelect.add(acUgr.getAuditItemsMap().get("full"));
			   auditItemsListSelect.add(acUgr.getAuditItemsMap().get("description"));
		   }
	       return this.auditItemsListSelect;
   }
   
  
   
   public List <BaseTableItem> getAuditItemsListContext() {
	   log.info("GroupManager:getAuditItemsListContext");
	   if(auditItemsListContext==null){
		   UgroupContext acUgr= new UgroupContext();
		   auditItemsListContext = new ArrayList<BaseTableItem>();
		   
		   
		   auditItemsListContext=acUgr.getAuditItemsCollection();
	   }
	   return this.auditItemsListContext;
   }
      
    
   public void selectRecord(){
	    String  sessionIdUgr = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	    log.info("selectRecord:sessionId="+sessionIdUgr);
	    
	   //!!!
	    List<String>  selRecUgr = (ArrayList<String>)
				  Component.getInstance("selRecUgroup",ScopeType.SESSION);
	    
	    if(selRecUgr==null){
	       selRecUgr = new ArrayList<String>();
	       log.info("selectRecord:01");
	    }
	    
	    
	    GroupUsersKnlT aa = new GroupUsersKnlT();
  	    
	    
	    if(aa!=null){
	     if(selRecUgr.contains(sessionIdUgr)){
	    	selRecUgr.remove(sessionIdUgr);
	    	aa.setSelected(false);
	    	log.info("selectRecord:02");
	     }else{
	    	selRecUgr.add(sessionIdUgr);
	    	aa.setSelected(true);
	    	log.info("selectRecord:03");
	     }
	    Contexts.getSessionContext().set("selRecUgroup", selRecUgr);	
	    
	    Contexts.getEventContext().set("ugroupBean", aa);
	   }
	}
   
  
   
   public String getDellMessage() {
	   return dellMessage;
   }
   public void setDellMessage(String dellMessage) {
	   this.dellMessage = dellMessage;
   } 
   
   public void audit(ResourcesMap resourcesMap, ActionsMap actionsMap){
	   try{
		   AuditExportData auditExportDataUgr = (AuditExportData)Component.getInstance("auditExportData",ScopeType.SESSION);
		   auditExportDataUgr.addFunc(resourcesMap.getCode()+":"+actionsMap.getCode());
		   
	   }catch(Exception eUgr){
		   log.error("GroupManager:audit:error:"+eUgr);
	   }
   }
   
    public Boolean getEvaluteForList() {
	
   	log.info("armManager:evaluteForList:01");
   	if(evaluteForList==null){
   		evaluteForList=false;
    	String remoteAuditUgr = FacesContext.getCurrentInstance().getExternalContext()
	             .getRequestParameterMap()
	             .get("remoteAudit");
	   log.info("augroupanager:evaluteForList:remoteAudit:"+remoteAuditUgr);
     	
    	if(remoteAuditUgr!=null&&
    	 
    	   !"OpenCrtFact".equals(remoteAuditUgr)&&	
    	   !"OpenUpdFact".equals(remoteAuditUgr)&&
    	   !"OpenDelFact".equals(remoteAuditUgr)&&
   	       !"onSelColFact".equals(remoteAuditUgr)&&
   	       !"refreshPdFact".equals(remoteAuditUgr)){
    		log.info("ugroupManager:evaluteForList!!!");
   		    evaluteForList=true;
    	}
   	 }
       return evaluteForList;
   }
   public Boolean getEvaluteForListFooter() {
		
	  
	   	if(evaluteForListFooter==null){
	   		evaluteForListFooter=false;
	    	String remoteAuditUgr = FacesContext.getCurrentInstance().getExternalContext()
		             .getRequestParameterMap()
		             .get("remoteAudit");
		   log.info("ugroupManager:evaluteForListFooter:remoteAudit:"+remoteAuditUgr);
	     
	    	if(getEvaluteForList()&&
	    	  
	    	   !"protBeanWord".equals(remoteAuditUgr)&&	
	    	  
	   	       !"selRecAllFact".equals(remoteAuditUgr)&&
	   	       !"clRecAllFact".equals(remoteAuditUgr)&&
	   	      // !remoteAudit equals "clSelOneFact"
	   	       !"onSelColSaveFact".equals(remoteAuditUgr)){
	    		  log.info("ugroupManager:evaluteForListFooter!!!");
	   		      evaluteForListFooter=true;
	    	}
	   	 }
	       return evaluteForListFooter;
	   }
   
   public Boolean getEvaluteForBean() {
		
		  
		   	if(evaluteForBean==null){
		   		evaluteForBean=false;
		    	String remoteAuditUgr = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("remoteAudit");
			    log.info("ugroupManager:evaluteForBean:remoteAudit:"+remoteAuditUgr);
				String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("sessionId");
			    log.info("ugroupManager:evaluteForBean:sessionId:"+sessionId);
		    	if(sessionId!=null && remoteAuditUgr!=null &&
		    	   ("rowSelectFact".equals(remoteAuditUgr)||	
		    	    "UpdFact".equals(remoteAuditUgr))){
		    	      log.info("ugroupManager:evaluteForBean!!!");
		   		      evaluteForBean=true;
		    	}
		   	 }
		     return evaluteForBean;
		   }

}

