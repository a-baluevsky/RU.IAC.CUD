package iac.grn.infosweb.context.mc.clusr;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import iac.cud.infosweb.dataitems.BaseItem;
import iac.cud.infosweb.dataitems.BaseParamItem;
import iac.cud.infosweb.entity.AcLegalEntityType;
import iac.cud.infosweb.entity.IspBssT;
import iac.cud.infosweb.entity.IspTempBssT;
import iac.cud.infosweb.entity.AcUser;
import iac.cud.infosweb.entity.JournIspLoad;
import iac.cud.infosweb.local.service.ServiceReestrAction;
import iac.cud.infosweb.local.service.ServiceReestrPro;
import iac.cud.infosweb.remote.frontage.IRemoteFrontageLocal;
import iac.cud.infosweb.ws.classifierzip.clientsample.ClassifLoadProcessor;
import iac.cud.infosweb.ws.classifierzip.clientsample.ClientSample;
import iac.grn.infosweb.session.audit.actions.ActionsMap;
import iac.grn.infosweb.session.audit.actions.ResourcesMap;
import iac.grn.infosweb.session.audit.export.AuditExportData;
import java.util.*;

import org.jboss.seam.Component;

import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import iac.grn.serviceitems.BaseTableItem;

import javax.persistence.NoResultException;


/**
 * Управляющий Бин
 * @author bubnov
 *
 */
@Name("clUsrManager")
 public class ClUsrManager {
	
	 @Logger private Log log;
	
	 @In 
	 EntityManager entityManager;
	 
	/**
     * Экспортируемая сущности 
     * для отображения
     */
	
	private int versionDetectFlag = 1;
	
	private int workExistFlag = 1;
	
	private String classifLoadVersion = "Не установлена";
	 
	private String classifExistVersion = "Не установлена";
	
	private List<BaseItem> auditList; 
	
	private Long auditCount;
	
	private List <BaseTableItem> auditItemsListSelect;
	
	private List <BaseTableItem> auditItemsListContext;
	
	private int connectError=0;
	private Boolean evaluteForList;
	private Boolean evaluteForListFooter;  
	private Boolean evaluteForBean;
	
	private List<AcLegalEntityType> listLET = null;
	
	private List<IspTempBssT> listOrg = null;
	
	private List<IspBssT> listUsrAutocomplete = null;
	
	private String loadMessage;
	private int loadFlag =0;
	
	private static String jndiBinding = "java:global/InfoS-ear/InfoS-ejb/IRemoteFrontage!iac.cud.infosweb.remote.frontage.IRemoteFrontageLocal";
	
	public List<BaseItem> getAuditList(int firstRow, int numberOfRows){
	  String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
	             .getRequestParameterMap()
	             .get("remoteAudit");
	   log.info("auditManager:getAuditList:remoteAudit:"+remoteAudit);
	  
	  
	  log.info("getAuditList:firstRow:"+firstRow);
	  log.info("getAuditList:numberOfRows:"+numberOfRows);
	  
	  List<BaseItem> clUsrListCached = (List<BaseItem>)
			  Component.getInstance("clUsrListCached",ScopeType.SESSION);
	  if(auditList==null){
		  log.info("getAuditList:01");
		 	if(("rowSelectFact".equals(remoteAudit)||
			    "selRecAllFact".equals(remoteAudit)||
			    "clRecAllFact".equals(remoteAudit)||
			    "clSelOneFact".equals(remoteAudit)||
			    "onSelColSaveFact".equals(remoteAudit))&&
			    clUsrListCached!=null){
		 	    	this.auditList=clUsrListCached;
			}else{
				log.info("getAuditList:03");
		    	invokeLocal("list", firstRow, numberOfRows, null);
			    Contexts.getSessionContext().set("clUsrListCached", this.auditList);
			    log.info("getAuditList:03:"+this.auditList.size());
			}
		 	
		 	List<String>  selRecOrgClUsr = (ArrayList<String>)
					  Component.getInstance("selRecOrg",ScopeType.SESSION);
		 	if(this.auditList!=null && selRecOrgClUsr!=null) {
		 		 for(BaseItem it:this.auditList){
				   if(selRecOrgClUsr.contains(it.getBaseId().toString())){
					 
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
			 String orderQueryCLUser=null;
			 log.info("CLUserManager:invokeLocal");
			 
			 if("list".equals(type)){
				 log.info("CLUser:invokeLocal:list:01");
				 
				 ClUsrStateHolder clUsrStateHolder = (ClUsrStateHolder)
						  Component.getInstance("clUsrStateHolder",ScopeType.SESSION);
				 Set<Map.Entry<String, String>> set = clUsrStateHolder.getSortOrders().entrySet();
                 for (Map.Entry<String, String> me : set) {
      		       log.info("me.getKey+:"+me.getKey());
      		       log.info("me.getValue:"+me.getValue());
      		       
      		       if(orderQueryCLUser==null){
      		    	 orderQueryCLUser="order by o."+me.getKey()+" "+me.getValue();
      		       }else{
      		    	 orderQueryCLUser=orderQueryCLUser+", o."+me.getKey()+" "+me.getValue();  
      		       }
      		     }
                 log.info("CLUser:invokeLocal:list:orderQueryCLUser:"+orderQueryCLUser);
                 
				 auditList = entityManager.createQuery(
					"select o from IspTempBssT o " +
					 (orderQueryCLUser!=null ? orderQueryCLUser : ""))
                       .setFirstResult(firstRow)
                       .setMaxResults(numberOfRows)
                       .getResultList();
             log.info("invokeLocal:list:02");
  
			 } else if("count".equals(type)){
				 log.info("CLUserList:count:01");
				 auditCount = (Long)entityManager.createQuery(
						 "select count(o) " +
				         "from IspTempBssT o  ")
		                .getSingleResult();
				 
               log.info("invokeLocal:count:02:"+auditCount);
           	 }
		}catch(Exception eClUser){
			  log.error("CLUser:invokeLocal:error:"+eClUser);
			  evaluteForList=false;
			  FacesMessages.instance().add("Ошибка!");
		}
	}
	 
	public void setAuditList(List<BaseItem> auditList){
		this.auditList=auditList;
	}
   public void forView(String modelType) {
	   String  clUsrId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	  log.info("forView:clUsrId:"+clUsrId);
	  log.info("forView:modelType:"+modelType);
	  if(clUsrId!=null ){
		  
		   
			if(modelType==null){
		    	return ;
		    }
			
			IspTempBssT ar = searchBean(clUsrId);
		 Contexts.getEventContext().set("clTempBean", ar);
	  }
   }
   
   private IspTempBssT searchBean(String sessionId){
    	
      if(sessionId!=null){
    	 List<IspTempBssT> clUsrListCached = (List<IspTempBssT>)
				  Component.getInstance("clUsrListCached",ScopeType.SESSION);
		if(clUsrListCached!=null){
			for(IspTempBssT itClUsr : clUsrListCached){
				 
			 
			  if(itClUsr.getBaseId().toString().equals(sessionId)){
					 log.info("searchBean_Achtung!!!");
					 return itClUsr;
			  }
			}
		 }
	   }
	   return null;
    }
  
   
   public void addOrg(){
	   log.info("clUsrManager:addOrg:01");
	   
	   IspTempBssT clUsrBeanCrt = (IspTempBssT)
				  Component.getInstance("clUsrBeanCrt",ScopeType.CONVERSATION);
	   
	   if(clUsrBeanCrt==null){
		   return;
	   }
	 
	   try {
		   
	    
		  clUsrBeanCrt.setCreated(new Date());
	      entityManager.persist(clUsrBeanCrt);
	    	   
	      entityManager.flush();
	      entityManager.refresh(clUsrBeanCrt);
	    	     
	    }catch (Exception e) {
	       log.error("clUsrManager:addOrg:ERROR:"+e);
	    }
	   
   }
   
   public Long getAuditCount(){
	   log.info("getAuditCount");
	 
	   invokeLocal("count",0,0,null);
	  
	   return auditCount;
	  
   }
   
   public void loadClassif(){
	   log.info("clUsrManager:loadClassif:01");
	   
	   try {
		   
		   String clVersion = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("clVersion");
		   
		  log.info("clUsrManager:loadClassif:02:"+clVersion);
		  
		 
		  
		   
		   AcUser au = (AcUser) Component.getInstance("currentUser",ScopeType.SESSION);
		 
		   
		   String idSess =(String) this. entityManager.createNativeQuery("select to_char(JOURN_ISP_LOAD_SEQ.nextval) sgnc from dual").getSingleResult();
	        
	       int seancact = Integer.valueOf(idSess);
		   
		   entityManager.createNativeQuery(
				   "INSERT INTO JOURN_ISP_LOAD (ID_SRV, LOAD_START, CREATED, CREATOR) "+
		   		   "VALUES ("+seancact+", sysdate, sysdate, "+au.getIdUser()+" ) ")
			.executeUpdate();
		  
		
		   Context ctx = new InitialContext(); 
	    	 
	       BaseParamItem bpi = new BaseParamItem(ServiceReestrPro.ClassifLoad.name());
	
	       bpi.put("gactiontype", ServiceReestrAction.TASK_RUN.name());
	       
	       bpi.put("seancact", new Long(seancact));
	       
	       if(clVersion!=null&&!clVersion.isEmpty()){
	         bpi.put("clVersion", new Integer(clVersion));
	       }
	       
	       IRemoteFrontageLocal obj = (IRemoteFrontageLocal)ctx.lookup(jndiBinding);
        		   
           obj.run(bpi);
		   
           audit(ResourcesMap.CLASSIF_IOGV, ActionsMap.LOAD); 
           
	    }catch (Exception e) {
	       log.error("clUsrManager:loadClassif:ERROR:"+e);
	    }
	   
   }
   
   public void moveClassif(){
	   
	   log.info("clUsrManager:moveClassif:01");
	   
	   try {
		   
		   String clVersion = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("clVersion");
		   
		  log.info("clUsrManager:moveClassif:02:"+clVersion);
		 
		   
			 
		  
		   Context ctx = new InitialContext(); 
	    	 
	       BaseParamItem bpi = new BaseParamItem(ServiceReestrPro.ClassifLoad.name());
	
	       bpi.put("gactiontype", ServiceReestrAction.TO_WORK.name());
	     //пока не задействовали
	     
	       
	       
	       IRemoteFrontageLocal obj = (IRemoteFrontageLocal)ctx.lookup(jndiBinding);
       		   
           obj.run(bpi);
          
		  /*
		  //чистим архив
		  //clear ISP_ARH_BSS_T
		
		   //копируем текущее в архив на случай отката
		   //ISP_BSS_T -> ISP_ARH_BSS_T
		   entityManager.createNativeQuery(
			
		  //чистим текущее
		  //clear ISP_BSS_T
		   
		   
		   //копируем из технологич. т. в текущее
		   //ISP_TEMP_BSS_T -> ISP_BSS_T	   
		   
		   
		    // "TRUNCATE TABLE ISP_TEMP_BSS_T")
			
		*/
		   
           audit(ResourcesMap.CLASSIF_IOGV, ActionsMap.TRANSFER);   
		   
	    }catch (Exception e) {
	       log.error("clUsrManager:moveClassif:ERROR:"+e);
	     }
	   
   }
   
   public void updOrg(){
	   
	   log.info("clUsrManager:updOrg:01");
	   
	   IspTempBssT clUsrBean = (IspTempBssT)
				  Component.getInstance("clUsrBean",ScopeType.CONVERSATION);
	   
	   String  sessionId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	   log.info("clUsrManager:updOrg:sessionId:"+sessionId);
	
	   if(clUsrBean==null || sessionId==null){
		   return;
	   }
	
	   try {
		  
		  IspTempBssT aom = entityManager.find(IspTempBssT.class, new Long(sessionId));
		  
		  aom.setFull(clUsrBean.getFull());
		
		 
		 
		  
		  entityManager.flush();
	      entityManager.refresh(aom);
	    	  
	      Contexts.getEventContext().set("clUsrBean", aom);
	    	  
	     }catch (Exception e) {
           log.error("clUsrManager:updOrg:ERROR:"+e);
         }
   }
   
   public void delOrg(){
	 try{
		log.info("clUsrManager:delOrg:01");  
		
		IspTempBssT clUsrBean = (IspTempBssT)
				  Component.getInstance("clUsrBean",ScopeType.CONVERSATION);
		// <h:inputHidden value="#{usrBean.idUser}"/>
		
		if(clUsrBean==null){
			return;
		}
		 
		log.info("clUsrManager:delOrg:IdOrg:"+clUsrBean.getBaseId());
		
		IspTempBssT aom = entityManager.find(IspTempBssT.class, clUsrBean.getBaseId());
		  
		entityManager.remove(aom);
		
	 }catch(Exception e){
		 log.error("clUsrManager:delOrg:error:"+e); 
	 }
    }
 
    public void forViewUpdDel() {
	   try{
	 
		   
		  try{
			  //1 - успех
			  //0 или null- не успех
			  String clVerTemp = (String) entityManager.createNativeQuery(
				   "select to_char(max(JIL.CLASSIF_VERSION)) cl_ver "+
                   "from JOURN_ISP_LOAD jil, "+
                //   "ISP_TEST_BSS_T it "+
                   "ISP_BSS_T it "+
                   "where JIL.ID_SRV=IT.UP_ISP_LOAD  ")
		   		.getSingleResult();
			
			  if(clVerTemp!=null&&!clVerTemp.trim().isEmpty()){
	   				this.classifExistVersion =clVerTemp;
			  }
			  
		  }catch(NoResultException e){
			  log.info("clUsrManager:forViewUpdDel:NoResultException");
		  }
		   
		  log.info("clUsrManager:forViewUpdDel:02:"+this.classifExistVersion);
		  
           String clLdVer = ClientSample.getVersion();
		   
           log.info("clUsrManager:forViewUpdDel:02+:"+clLdVer);
        		   
           if(clLdVer!=null){
             this.classifLoadVersion=clLdVer;
             
             if(clLdVer.equals(this.classifExistVersion)){
            	 this.versionDetectFlag=-2;
             }
             
           }else{
        	   this.versionDetectFlag=0;
           }
           
		   log.info("clUsrManager:forViewUpdDel:03:"+this.classifLoadVersion);
		   
		   if(ClassifLoadProcessor.getControls().containsKey("classif_load")){
	    		log.info("clUsrManager:forViewUpdDel:04:return");
	    		this.versionDetectFlag=-1;
	    		return;
	       }
		   
		   
	   }catch(Exception e){
		   log.error("clUsrManager:forViewUpdDel:Error:"+e);
		   this.versionDetectFlag=0;
	   }
    } 
   
    public void forViewDelMessage() {
		
    }
    
     public void forViewLoadMessage() {
		  log.info("forViewLoadMessage:01");
		  
		  JournIspLoad ao = null;
				  
         try{
			  
        	 ao = entityManager.createQuery(
				   "select t from JournIspLoad t "+
		   		   "where t.idSrv = "+
		   		   "(select max(t1.idSrv) from JournIspLoad t1 ) ", 
		   		JournIspLoad.class)
		   		.getSingleResult();
			  
		  }catch(NoResultException e){
			  log.info("clUsrManager:forViewUpdDel:NoResultException");
		  }
  
         if(ao==null){
        	 loadMessage="Загрузок не было";
         }else{
        	 
        	 if(ao.getLoadFinish()==null){
        		 loadMessage="Процесс загрузки выполняется";
        		 loadFlag=1;
        	 }else{
        		 loadMessage="Процесс загрузки завершен";
        		 loadFlag=2;
        	 }
            
        	 Contexts.getEventContext().set("journIspLoadBean", ao);
         }
		 
	
    }
    
     public void forViewMove() {
	   log.info("forViewMove:01");
		  
				  
      	 List<Object[]> lo = (List<Object[]>)entityManager.createNativeQuery(
				   "select 1 from dual where EXISTS (select * from ISP_TEMP_BSS_T) ")
		   		.getResultList();
			  
		
        if(lo==null||lo.isEmpty()){
       	 
        	workExistFlag=-1;
        }else{
        	
        	 try{
   			  //1 - успех
   			  //0 или null- не успех
        		String clVerTemp = (String) entityManager.createNativeQuery(
   				   "select to_char(max(JIL.CLASSIF_VERSION)) cl_ver "+
                      "from JOURN_ISP_LOAD jil, "+
                    //  "ISP_TEST_BSS_T it "+
                      "ISP_BSS_T it "+
                      "where JIL.ID_SRV=IT.UP_ISP_LOAD  ")
   		   		.getSingleResult();
   			  
   			 if(clVerTemp!=null&&!clVerTemp.trim().isEmpty()){
   				this.classifExistVersion =clVerTemp;
			  }
   			
   		  }catch(NoResultException e){
   			  log.info("clUsrManager:forViewMove:1:NoResultException");
   		  }
         }
        
        try{
 			  //1 - успех
 			  //0 или null- не успех
 			  String clVerTemp = (String) entityManager.createNativeQuery(
 				   "select to_char(max(JIL.CLASSIF_VERSION)) cl_ver "+
                    "from JOURN_ISP_LOAD jil, "+
                    "ISP_TEMP_BSS_T it "+
                    "where JIL.ID_SRV=IT.UP_ISP_LOAD  ")
 		   		.getSingleResult();
 			 
 			  if(clVerTemp!=null&&!clVerTemp.trim().isEmpty()){
 			     this.classifLoadVersion =clVerTemp;
 			  }
 		  }catch(NoResultException e){
 			  log.info("clUsrManager:forViewMove:2:NoResultException");
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
    public int getConnectError(){
 	   return connectError;
    }
    
    public List<IspTempBssT> getListOrg() throws Exception{
	    log.info("getListOrg:01");
	    try {
	    	if(listOrg==null){
	    		log.info("getListOrg:02");
	    		listOrg=entityManager.createQuery("select o from IspTempBssT o").getResultList();
	    	}
	     } catch (Exception eClUsr) {
	    	 log.error("getListOrg:ERROR:"+eClUsr);
	         throw eClUsr;
	     }
	    return listOrg;
   }
    
    public void setAuditItemsListSelect(List <BaseTableItem> auditItemsListSelect) {
	    this.auditItemsListSelect=auditItemsListSelect;
   }
   public List <BaseTableItem> getAuditItemsListSelect() {
		   
	
	    ClUsrContext acClUsr= new ClUsrContext();
		   if( auditItemsListSelect==null){
			   log.info("getAuditItemsListSelect:02");
			   auditItemsListSelect = new ArrayList<BaseTableItem>();
			   
			
			   auditItemsListSelect.add(acClUsr.getAuditItemsMap().get("signObject"));
			   auditItemsListSelect.add(acClUsr.getAuditItemsMap().get("full"));
			   auditItemsListSelect.add(acClUsr.getAuditItemsMap().get("fio"));
			   
			   }
	       return this.auditItemsListSelect;
   }
   
 
   
   public List <BaseTableItem> getAuditItemsListContext() {
	   log.info("clUsrManager:getAuditItemsListContext");
	   if(auditItemsListContext==null){
		   ClUsrContext acClUsr= new ClUsrContext();
		   auditItemsListContext = new ArrayList<BaseTableItem>();
		   
		   
		   auditItemsListContext=acClUsr.getAuditItemsCollection();
	   }
	   return this.auditItemsListContext;
   }
      
    
   public void selectRecord(){
	    String  sessionIdClUsr = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	    log.info("selectRecord:sessionId="+sessionIdClUsr);
	    
	   //  for/View(); //!!!
	    List<String>  selRecOrgClUsr = (ArrayList<String>)
				  Component.getInstance("selRecOrg",ScopeType.SESSION);
	    
	    if(selRecOrgClUsr==null){
	       selRecOrgClUsr = new ArrayList<String>();
	       log.info("selectRecord:01");
	    }
	    
	  
	   IspTempBssT ao = new IspTempBssT();
	 
	    
	    if(ao!=null){
	     if(selRecOrgClUsr.contains(sessionIdClUsr)){
	    	selRecOrgClUsr.remove(sessionIdClUsr);
	    	ao.setSelected(false);
	    	log.info("selectRecord:02");
	     }else{
	    	selRecOrgClUsr.add(sessionIdClUsr);
	    	ao.setSelected(true);
	    	log.info("selectRecord:03");
	     }
	    Contexts.getSessionContext().set("selRecOrg", selRecOrgClUsr);	
	    
	    Contexts.getEventContext().set("clTempBean", ao);
	    }
	  }
   
  
   
   //!!!
   //IspBssT а не IspTempBssT
   public List<IspBssT> autocomplete(Object suggest) throws Exception{
   	String pref = (String)suggest;
   	
	    log.info("Usr:autocomplete:01:pref:"+pref);
	    
	    String  signObject = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("signObject");
	    log.info("Usr:autocomplete:signObject="+signObject);
	    
	    String codeOrg=signObject.substring(0, 3);
	    
	    try {
	    	if(listUsrAutocomplete==null){
	    		log.info("Usr:autocomplete:02");
	    		listUsrAutocomplete=entityManager.createQuery(
	    				"select o from IspBssT o where o.status='A' and o.signObject not like '%000' " +
	    			//	"and o.full like '"+pref+"%' " +
	    				"and upper(o.fio) like upper(:pref) " +
	    				"and substr(o.signObject,1,3) = :codeOrg " +
	    				"order by o.fio ")
	    				.setParameter("pref", pref+"%")
	    				.setParameter("codeOrg", codeOrg)
	    				.getResultList();
	    		log.info("Usr:autocomplete:03:size:"+listUsrAutocomplete.size());
	    	}
	     } catch (Exception e) {
	    	 log.error("Usr:autocomplete:ERROR:"+e);
	         throw e;
	     }
	    return listUsrAutocomplete;
  }
   //!!!
   //IspBssT а не IspTempBssT
   public void forViewAutocomplete() {
	   try{
		    String department = null;
		     String signObjectClUsr = FacesContext.getCurrentInstance().getExternalContext()
				        .getRequestParameterMap()
				        .get("signObject");
		     log.info("forViewAutocomplete:signObject:"+signObjectClUsr);
		     if(signObjectClUsr!=null){
		    	 IspBssT ao = (IspBssT)entityManager.createQuery(
		    				"select o from IspBssT o where o.status='A' " +
		    				"and o.signObject = :signObject ")
		    		    	.setParameter("signObject", signObjectClUsr)
		    		    	.getSingleResult();
		    	 
		    	   try{
		    		 
		    	     IspBssT ao_dep = (IspBssT)entityManager.createQuery(
		    				  "select o from IspBssT o where o.status='A' " +
		    				  "and o.signObject = :signObject ")
		    		    	  .setParameter("signObject", signObjectClUsr.substring(0, 5)+"000")
		    		    	  .getSingleResult();
		    	    
		    	     department=ao_dep.getFull();
		    	    
		    	    }catch(NoResultException eClUsr){
					   log.error("forViewAutocomplete:NoResultError:"+eClUsr);
				    }
		    	   
		    	   if(department!=null){
		    	     ao.setDepartment(department);
		    	   }
		     	 Contexts.getEventContext().set("clUsrBean", ao);
		   	 }
		   }catch(Exception eClUsr){
			   log.error("forViewAutocomplete:Error:"+eClUsr);
		   }
   }
   
   //!!!
   //IspBssT а не IspTempBssT
   public void forViewAutocomplete(String signObjectClUsr) {
	   try{
		    String department = null;
		     log.info("forViewAutocomplete:signObject:"+signObjectClUsr);
		     if(signObjectClUsr!=null){
		    	 IspBssT ao = (IspBssT)entityManager.createQuery(
		    				"select o from IspBssT o where o.status='A' " +
		    				"and o.signObject = :signObject ")
		    		    	.setParameter("signObject", signObjectClUsr)
		    		    	.getSingleResult();
		    	 
		    	   try{
		    		 
		    	     IspBssT ao_dep = (IspBssT)entityManager.createQuery(
		    				  "select o from IspBssT o where o.status='A' " +
		    				  "and o.signObject = :signObject ")
		    		    	  .setParameter("signObject", signObjectClUsr.substring(0, 5)+"000")
		    		    	  .getSingleResult();
		    	    
		    	     department=ao_dep.getFull();
		    	    
		    	    }catch(NoResultException e){
					   log.error("forViewAutocomplete:NoResultError:"+e);
				    }
		    	   
		    	   if(department!=null){
		    	     ao.setDepartment(department);
		    	   }
		     	 Contexts.getEventContext().set("clUsrBean", ao);
		   	 }
		   }catch(Exception eClUsr){
			   log.error("forViewAutocomplete:Error:"+eClUsr);
		   }
   }
   
   public Boolean getEvaluteForList() {
	
   	log.info("clUsrManager:evaluteForList:01");
   	if(evaluteForList==null){
   		evaluteForList=false;
    	String remoteAuditClUsr = FacesContext.getCurrentInstance().getExternalContext()
	             .getRequestParameterMap()
	             .get("remoteAudit");
	   log.info("clUsrManager:evaluteForList:remoteAudit:"+remoteAuditClUsr);
     	
    	if(remoteAuditClUsr!=null&&
    	 
    	   !"OpenCrtFact".equals(remoteAuditClUsr)&&	
    	   !"OpenUpdFact".equals(remoteAuditClUsr)&&
    	   !"OpenDelFact".equals(remoteAuditClUsr)&&
   	       !"onSelColFact".equals(remoteAuditClUsr)&&
   	       !"refreshPdFact".equals(remoteAuditClUsr)){
    		log.info("reposManager:evaluteForList!!!");
   		    evaluteForList=true;
    	}
   	 }
       return evaluteForList;
   }
   public Boolean getEvaluteForListFooter() {
		
	  
	   	if(evaluteForListFooter==null){
	   		evaluteForListFooter=false;
	    	String remoteAuditClUsr = FacesContext.getCurrentInstance().getExternalContext()
		             .getRequestParameterMap()
		             .get("remoteAudit");
		   log.info("clUsrManager:evaluteForListFooter:remoteAudit:"+remoteAuditClUsr);
	     
	    	if(getEvaluteForList()&&
	    	    	
	    	   !"protBeanWord".equals(remoteAuditClUsr)&&	
	    	    	
	   	       !"selRecAllFact".equals(remoteAuditClUsr)&&
	   	       !"clRecAllFact".equals(remoteAuditClUsr)&&
	   	      // !remoteAudit equals "clSelOneFact"
	   	       !"onSelColSaveFact".equals(remoteAuditClUsr)){
	    		  log.info("clUsrManager:evaluteForListFooter!!!");
	   		      evaluteForListFooter=true;
	    	}
	   	 }
	       return evaluteForListFooter;
	   }
   
   public Boolean getEvaluteForBean() {
		
		  
		   	if(evaluteForBean==null){
		   		evaluteForBean=false;
		    	String remoteAuditClUsr = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("remoteAudit");
			    log.info("clUsrManager:evaluteForBean:remoteAudit:"+remoteAuditClUsr);
				String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("sessionId");
			    log.info("clUsrManager:evaluteForBean:sessionId:"+sessionId);
		    	if(sessionId!=null && remoteAuditClUsr!=null &&
		    	   ("rowSelectFact".equals(remoteAuditClUsr)||	
		    	    "UpdFact".equals(remoteAuditClUsr))){
		    	      log.info("clUsrManager:evaluteForBean!!!");
		   		      evaluteForBean=true;
		    	}
		   	 }
		     return evaluteForBean;
		   }
public String getClassifLoadVersion() {
	return classifLoadVersion;
}
public void setClassifLoadVersion(String classifLoadVersion) {
	this.classifLoadVersion = classifLoadVersion;
}
public String getClassifExistVersion() {
	return classifExistVersion;
}
public void setClassifExistVersion(String classifExistVersion) {
	this.classifExistVersion = classifExistVersion;
}
public int getVersionDetectFlag() {
	return versionDetectFlag;
}
public void setVersionDetectFlag(int versionDetectFlag) {
	this.versionDetectFlag = versionDetectFlag;
}
public String getLoadMessage() {
	return loadMessage;
}
public void setLoadMessage(String loadMessage) {
	this.loadMessage = loadMessage;
}
public int getWorkExistFlag() {
	return workExistFlag;
}
public void setWorkExistFlag(int workExistFlag) {
	this.workExistFlag = workExistFlag;
}
public int getLoadFlag() {
	return loadFlag;
}
public void setLoadFlag(int loadFlag) {
	this.loadFlag = loadFlag;
}
public List<IspBssT> getListUsrAutocomplete() {
	return listUsrAutocomplete;
}
public void setListUsrAutocomplete(List<IspBssT> listUsrAutocomplete) {
	this.listUsrAutocomplete = listUsrAutocomplete;
}

public void audit(ResourcesMap resourcesMap, ActionsMap actionsMap){
	   try{
		   AuditExportData auditExportData = (AuditExportData)Component.getInstance("auditExportData",ScopeType.SESSION);
		   auditExportData.addFunc(resourcesMap.getCode()+":"+actionsMap.getCode());
		   
	   }catch(Exception e){
		   log.error("clUsrManager:audit:error:"+e);
	   }
}

}

