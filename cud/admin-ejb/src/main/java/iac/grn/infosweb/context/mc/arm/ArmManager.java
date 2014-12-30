package iac.grn.infosweb.context.mc.arm;

import iac.cud.infosweb.dataitems.BaseItem;
import iac.cud.infosweb.dataitems.SystemCertItem;
import iac.cud.infosweb.entity.AcAppPage;
import iac.cud.infosweb.entity.AcApplication;
import iac.cud.infosweb.entity.AcUser;
import iac.grn.infosweb.context.mc.usr.TIDEncodePLBase64;
import iac.grn.infosweb.session.audit.actions.ActionsMap;
import iac.grn.infosweb.session.audit.actions.ResourcesMap;
import iac.grn.infosweb.session.audit.export.AuditExportData;
import iac.grn.serviceitems.BaseTableItem;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.apache.xml.security.utils.Base64;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.transaction.Transaction;

/**
 * Управляющий Бин
 * @author bubnov
 *
 */
@Name("armManager")
 public class ArmManager {
	
	 @Logger private Log log;
	
	 @In 
	 EntityManager entityManager;
	 
	/**
     * Экспортируемая сущности 
     * для отображения
     */
	
	private int connectError=0;
	private Boolean evaluteForList;
		
		
		
	private List<BaseItem> auditList; 
	
	private Long auditCount;
	
	private List <BaseTableItem> auditItemsListSelect;
	
	private List <BaseTableItem> auditItemsListContext;
	
	private Boolean evaluteForListFooter;  
	private Boolean evaluteForBean;
	
	private String dellMessage=null;
	private int delNot=0;

	
	private List<AcApplication> listArm = null;
	
	private boolean armCodeExist=false;
	
	private List<AcUser> adminListForView;
	
	private String commentApp = null;
	
	
	
	public List<BaseItem> getAuditList(int firstRow, int numberOfRows){
	  String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
	             .getRequestParameterMap()
	             .get("remoteAudit");
	   log.info("ArmManager:auditManager:getAuditList:remoteAudit:"+remoteAudit);
	  
	  
	  log.info("ArmManager:getAuditList:firstRow:"+firstRow);
	  log.info("ArmManager:getAuditList:numberOfRows:"+numberOfRows);
	  
	  List<BaseItem> armListCached = (List<BaseItem>)
			  Component.getInstance("armListCached",ScopeType.SESSION);
	  if(auditList==null){
		  log.info("getAuditList:01");
		 	if(("rowSelectFact".equals(remoteAudit)||
			    "selRecAllFact".equals(remoteAudit)||
			    "clRecAllFact".equals(remoteAudit)||
			    "clSelOneFact".equals(remoteAudit)||
			    "onSelColSaveFact".equals(remoteAudit))&&
			    armListCached!=null){
			    	this.auditList=armListCached;
			}else{
				
				
		    	invokeLocal("list", firstRow, numberOfRows, null);
			    Contexts.getSessionContext().set("armListCached", this.auditList);
			    log.info("getAuditList:03:"+this.auditList.size());
			}
		 	
		 	List<String>  selRecArm = (ArrayList<String>)
					  Component.getInstance("selRecArm",ScopeType.SESSION);
		 	if(this.auditList!=null && selRecArm!=null) {
		 		 for(BaseItem it:this.auditList){
				   if(selRecArm.contains(it.getBaseId().toString())){
					 
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
			 String orderQueryArm=null;
			 log.info("hostsManager:invokeLocal");
			 
			 ArmStateHolder orgStateHolder = (ArmStateHolder)
					  Component.getInstance("armStateHolder",ScopeType.SESSION);
			 
			 Map<String, String> filterMapArm = orgStateHolder.getColumnFilterValues();
			 String st=null;
			 
			 if("list".equals(type)){
				 log.info("ArmManager:invokeLocal:list:01");
				 
				
				 Set<Map.Entry<String, String>> set = orgStateHolder.getSortOrders().entrySet();
                 for (Map.Entry<String, String> me : set) {
      		        
      		       if(orderQueryArm==null){
      		    	 orderQueryArm="order by "+me.getKey()+" "+me.getValue();
      		       }else{
      		    	 orderQueryArm=orderQueryArm+", "+me.getKey()+" "+me.getValue();  
      		       }
      		     }
                 log.info("ArmManager:invokeLocal:list:orderQueryArm:"+orderQueryArm);
                
                 if(filterMapArm!=null){
    	    		 Set<Map.Entry<String, String>> setFilterArmManager = filterMapArm.entrySet();
    	              for (Map.Entry<String, String> me : setFilterArmManager) {
    	            	      		//делаем фильтр на начало
    	            	  st=(st!=null?st+" and " :"")+" lower("+me.getKey()+") like lower('"+me.getValue()+"%') ";
    	           
    	              }
    	    	   }
                 log.info("Arm:invokeLocal:list:filterQuery:"+st);
                 
				 auditList = entityManager.createQuery("select o from AcApplication o "+
						 (st!=null ? " where "+st :"")+
						 (orderQueryArm!=null ? orderQueryArm+", o.idArm " : " order by o.idArm "))
                       .setFirstResult(firstRow)
                       .setMaxResults(numberOfRows)
                       .getResultList();
             log.info("Arm:invokeLocal:list:02");
  
			 } else if("count".equals(type)){
				 log.info("ArmManagerList:count:01");
				 
				 if(filterMapArm!=null){
    	    		 Set<Map.Entry<String, String>> setFilterArmManager = filterMapArm.entrySet();
    	              for (Map.Entry<String, String> me : setFilterArmManager) {
    	            	
    	            		//делаем фильтр на начало
    	            	  st=(st!=null?st+" and " :"")+" lower("+me.getKey()+") like lower('"+me.getValue()+"%') ";
    	         
    	              }
    	    	   }
                 log.info("ArmManager:invokeLocal:count:filterQuery:"+st);
				 
				 
				 auditCount = (Long)entityManager.createQuery(
						 "select count(o) " +  
				         "from AcApplication o "+
				         (st!=null ? " where "+st :""))
		                .getSingleResult();
				 
               log.info("ArmManager:invokeLocal:count:02:"+auditCount);
           	 } 
		}catch(Exception e){
			  log.error("ArmManager:invokeLocal:error:"+e);
			  evaluteForList=false;
			  FacesMessages.instance().add("Ошибка!");
		}
	}
	 
	
   public void forView(String modelType) {
	   
	   String  armId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	   
	  log.info("forView:armId:"+armId);
	  log.info("forView:modelType:"+modelType);
	  if(armId!=null /*&& usrBean==null*/){
		  
		   
			if(modelType==null){
		    	return ;
		    }
			
		 AcApplication ar = searchBean(armId);
		 Contexts.getEventContext().set("armBean", ar);
		 
		 forViewCert();
	  }
   }
   
   private AcApplication searchBean(String sessionId){
    	
      if(sessionId!=null){
    	 List<AcApplication> armListCached = (List<AcApplication>)
				  Component.getInstance("armListCached",ScopeType.SESSION);
		if(armListCached!=null){
			for(AcApplication it : armListCached){
				 
			 
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
    
   public void addArm(){
	   log.info("armManager:addArm:01");
	   
	   AcApplication armBeanCrt = (AcApplication)
				  Component.getInstance("armBeanCrt",ScopeType.CONVERSATION);
	   
	   if(armBeanCrt==null){
		   return;
	   }
	 
	   try {
		  
		   AcUser au = (AcUser) Component.getInstance("currentUser",ScopeType.SESSION); 
			 
		   
		 if((au.getAllowedSys()!=null && !au.isAllowedReestr("004", "2"))||!armCodeExistCrt(armBeanCrt.getCode().trim())){
		   
			 
			 if(au.getAllowedSys()!=null && !au.isAllowedReestr("004", "2")){
	    		  //пользователь имеет право только создать заявку 
	    		  //на создание пользователя
	    		  log.info("armManager:addArm:05");
	    		  
	    		  armBeanCrt.setCreator(au.getIdUser());
	    		  addArmApp(armBeanCrt, commentApp);
	    		  
	    		  audit(ResourcesMap.APP_SYS, ActionsMap.CREATE) ;
	    		  
	    	  }else{  
	    		  
				  armBeanCrt.setName(armBeanCrt.getName().trim());
				  armBeanCrt.setCode(armBeanCrt.getCode().trim());
				  
				  if(armBeanCrt.getDescription()!=null&&!armBeanCrt.getDescription().trim().equals("")){
					  armBeanCrt.setDescription(armBeanCrt.getDescription().trim());
				  }else{
					  armBeanCrt.setDescription(null);
				  }
				  
				  if(armBeanCrt.getLinks()!=null&&!armBeanCrt.getLinks().trim().equals("")){
					  armBeanCrt.setLinks(armBeanCrt.getLinks().replaceAll(" ", ""));
				  }else{
					  armBeanCrt.setLinks(null);
				  }
				  
				  armBeanCrt.setCreator(au.getIdUser());
				  armBeanCrt.setCreated(new Date());
			      entityManager.persist(armBeanCrt);
			    	
			      
			      AcAppPage ap= new AcAppPage();
		    	  ap.setAcApplication(armBeanCrt.getIdArm());
		    	  ap.setPageName("ROOT_NODE - "+armBeanCrt.getName());
		    	  ap.setIdParent2(new Long(1));
		    	  entityManager.persist(ap);
		    	  
			      
			      entityManager.flush();
			      entityManager.refresh(armBeanCrt);
			    
			      audit(ResourcesMap.IS, ActionsMap.CREATE); 
	      
	    	  }
	      
	    }
	      
	    }catch (Exception e) {
	       log.error("armManager:addArm:ERROR:"+e);
	    }
	   
   }
   
 private void addArmApp(AcApplication armBeanCrt, String commentApp){
	   
	   log.info("ArmManager:addArmApp:01");
	   try{
		   String secret = TIDEncodePLBase64.getSecret();
		   
		   entityManager.createNativeQuery(
				   "insert into JOURN_APP_SYSTEM_BSS_T (ID_SRV, FULL_NAME, SHORT_NAME, " +
                             "DESCRIPTION, UP_USER, SECRET, COMMENT_APP) " +
				   " values ( JOURN_APP_SYSTEM_SEQ.nextval, ?, ?, ?, ?, ?, ? ) ")
				    .setParameter(1, armBeanCrt.getName())
					.setParameter(2, "-")
					.setParameter(3, armBeanCrt.getDescription())
					.setParameter(4, armBeanCrt.getCreator())
					.setParameter(5, secret)
				    .setParameter(6, commentApp)
				   .executeUpdate();
			
	   }catch(Exception e){
		   log.error("ArmManager:addArmApp:error:"+e);
	   }
	   
   }
 
   public void updArm(){
	   
	   log.info("armManager:updArm:01");
	   
	   AcApplication armBean = (AcApplication)
				  Component.getInstance("armBean",ScopeType.CONVERSATION);
	   
	   String  sessionId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	   log.info("armManager:updArm:sessionId:"+sessionId);
	
	   if(armBean==null || sessionId==null){
		   return;
	   }
	
	   try {
		   
		 if(!armCodeExistUpd(armBean.getCode().trim(), new Long(sessionId))){  
			   
		  AcApplication aam = entityManager.find(AcApplication.class, new Long(sessionId));
		  
		  aam.setName(armBean.getName().trim());
		  aam.setCode(armBean.getCode().trim());
		  
		  if(armBean.getDescription()!=null&&!armBean.getDescription().trim().equals("")){
			  aam.setDescription(armBean.getDescription().trim());
		  }else{
			  aam.setDescription(null);
		  }
		  
		  if(armBean.getLinks()!=null&&!armBean.getLinks().trim().equals("")){
			  aam.setLinks(armBean.getLinks().replaceAll(" ", ""));
		  }else{
			  aam.setLinks(null);
		  }
		  
		  
		  
		  entityManager.flush();
	      entityManager.refresh(aam);
	    	  
	       Contexts.getEventContext().set("armBean", aam);
		  
	      audit(ResourcesMap.IS, ActionsMap.UPDATE); 
	      
		 }
		 
	     }catch (Exception e) {
           log.error("armManager:updSrm:ERROR:"+e);
         }
   }
   
    public void saveArmCertificate(byte[] file_byte, Long id_sys){
	   
       //в базе храним текст base64 сертификата
       //можно в любом формате: der и base64
       //при base64 нам не надо здесь добавлять ----BEGIN CERT---- и ---END CERT---
       //так как они уже есть в загружаемом файле - 
       //windows при создании файла сертификата помещает в него эти конструкции
  	 
 	  //это хорошо, но работает только когда сертификат получается из криптопро CSP,
 	  //а когда скачивается вручную с сайта реестра, то в нём нет ---BEGIN CERT---- и ---END CERT---
 	  //и их надо добавлять вручную.
    	
	   log.info("armManager:saveArmCertificate:01:"+(file_byte!=null));
	   
	   try {
		   
		   CertificateFactory userCfArm = CertificateFactory.getInstance("X.509");
           
           X509Certificate userCertX = null;
           try{
        	   
         
           userCertX = (X509Certificate)
        		   userCfArm.generateCertificate(new  ByteArrayInputStream(file_byte));
           
           }catch(Exception eArm){
        	   
        	   log.info("armManager:saveArmCertificate:02");
        	   
        	   
           //2-я попытка
           //ловим случай - когда сертификат скачан вручную  с сайта УЦ
           //в этом случае он без -BEGIN CERTIFICATE-
           
        	String certificateString =  "-----BEGIN CERTIFICATE-----\n"+new String(file_byte, "utf-8")+"\n-----END CERTIFICATE-----";
        	
        
        	   
            userCertX = (X509Certificate)
        		   userCfArm.generateCertificate(new ByteArrayInputStream(certificateString.getBytes("utf-8")));
           }
           
           
           String x509CertArm = Base64.encode(userCertX.getEncoded());
           
           log.info("armManager:saveArmCertificate:02:"+x509CertArm);
           
           Transaction.instance().begin();
		   
		   Transaction.instance().enlist(entityManager);
		   
		   entityManager.createNativeQuery("update AC_IS_BSS_T t1 " + 
		   		                           "set T1.CERT_DATE=? " + 
		   		                           "where t1.ID_SRV=? ")
		   .setParameter(1, x509CertArm)
		   .setParameter(2, id_sys)
		   .executeUpdate();  
			 
		   audit(ResourcesMap.IS, ActionsMap.ADD_CERT); 
		   
		   Transaction.instance().commit();
		   
	     }catch (Exception eArm) {
           log.error("armManager:saveArmCertificate:ERROR:"+eArm);
           try{
             Transaction.instance().rollback();
           }catch(Exception etArm){
        	   log.error("armManager:saveArmCertificate:ERROR:rollback:"+eArm);
           }
         }
   }

    public void removeCert(){
  	   
  	   try {
  		   
  		   String  sessionIdArm = FacesContext.getCurrentInstance().getExternalContext()
  			        .getRequestParameterMap()
  			        .get("sessionId");
  		   log.info("armManager:removeCert:sessionId:"+sessionIdArm);
  		
  		   if(sessionIdArm==null||sessionIdArm.trim().equals("")){
  			   return;
  		   }
  			   
  		   entityManager.createNativeQuery("update AC_IS_BSS_T t1 " + 
  		   		                           "set T1.CERT_DATE=null " + 
  		   		                           "where t1.ID_SRV=? ")
  		   .setParameter(1, new Long(sessionIdArm))
  		   .executeUpdate();  
  			 
  		 audit(ResourcesMap.IS, ActionsMap.REMOVE_CERT); 
  		 
  	     }catch (Exception eArm) {
             log.error("armManager:removeCert:ERROR:"+eArm);
           }
    }
    
   public void delArm(){
	 try{
		log.info("armManager:delArm:01");  
		
		AcApplication armBean = (AcApplication)
				  Component.getInstance("armBean",ScopeType.CONVERSATION);
		// <h:inputHidden value="#{armBean.idArm}"/>
		
		if(armBean==null){
			return;
		}
		 
		log.info("armManager:delArm:IdArm:"+armBean.getIdArm());
		
		AcApplication aom = entityManager.find(AcApplication.class, armBean.getIdArm());
		  
		entityManager.remove(aom);
		
		audit(ResourcesMap.IS, ActionsMap.DELETE); 
		
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
	    	 AcApplication ao = entityManager.find(AcApplication.class, new Long(sessionId));
	    	 Contexts.getEventContext().set("armBean", ao);
	    	 
	    	//устанавливаем на 1 страницу пагинатор в модальном окне
	    	 ArmStateHolder armStateHolder = (ArmStateHolder)
					  Component.getInstance("armStateHolder",ScopeType.SESSION);
	    	 armStateHolder.resetPageNumber();
	    	 
	   	 }
	   }catch(Exception e){
		   log.error("forViewUpdDel:Error:"+e);
	   }
    } 
   
    public void forViewDelMessage() {
		  String sessionId = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap()
				.get("sessionId");
		  log.info("forViewDelMessage:sessionId:"+sessionId);
		  if(sessionId!=null){
		
			  DateFormat df = new SimpleDateFormat ("dd.MM.yy");
			  
			  AcApplication aa = entityManager.find(AcApplication.class, new Long(sessionId));
			  
		      List<Object[]> lo = (List<Object[]>) entityManager.createNativeQuery(
		    		                          "select JAS.ID_SRV, JAS.CREATED "+
                                              "from JOURN_APP_SYSTEM_BSS_T jas, "+
                                              "AC_IS_BSS_T arm "+
                                              "where ARM.ID_SRV =JAS.UP_IS "+
                                              "and ARM.ID_SRV=? ")
                           .setParameter(1, new Long(sessionId))
                           .getResultList();
			  
		      if(lo.size()==0){
			  
			    if(aa.getAcRoles()!=null&&!aa.getAcRoles().isEmpty()){
				  dellMessage="У ИС есть порождённые записи: Роли";
			    }
			    if(aa.getAcSubsystemCertBssTs()!=null&&!aa.getAcSubsystemCertBssTs().isEmpty()){
			    	dellMessage+="<br/>У ИС есть порождённые записи: Подсистемы";
				}
			    
			    List<AcAppPage> aapl = aa.getAcAppPages();
			    if(aapl!=null&&!aapl.isEmpty()){
			      int NOT_ROOT_NODE=0;
			    
				  for(AcAppPage aap :aapl){
					 
					 if(aap.getIdParent2()!=null&&!aap.getIdParent2().equals(1L)){
						 
						 
						 NOT_ROOT_NODE=1;
						 break;
					 }
					 
				  }
			  
				
				 if(NOT_ROOT_NODE==1){
				   if(dellMessage!=null){ 
				     dellMessage+=", Ресурсы.";
				  }else{
				    dellMessage="У ИС есть порождённые записи: Ресурсы.";
				  }
				 }
			   }
			   if(dellMessage!=null){
				 dellMessage+="<br/>При удалении они будут удалены!";
			   }
		      }else{
		    	  
		    	  dellMessage="У ИС есть привязка к заявке на создание системы " +
		    	  		      "<br/>№ "+lo.get(0)[0].toString()+
		    	  		      " от "+df.format((Date)lo.get(0)[1])+
		    			      ".<br/>Удаление невозможно! ";
		    	  delNot=1;
		      }
			 
			 
			 Contexts.getEventContext().set("armBean", aa);
			 
			 
			 
		 }	
    }
  
    public void forViewCert() {
 	   try{
 	     String sessionIdArm = FacesContext.getCurrentInstance().getExternalContext()
 			        .getRequestParameterMap()
 			        .get("sessionId");
 	     log.info("forViewCert:sessionId:"+sessionIdArm);
 	     
 	     DateFormat df = new SimpleDateFormat ("dd.MM.yy");
 	    
 	     if(sessionIdArm!=null){
 	    	 
 	    	String certDataX = (String) entityManager.createNativeQuery(
 	    			 "select to_char(T1.CERT_DATE) " + 
 	    	 		 "from AC_IS_BSS_T t1 " + 
 	    	 		 "where T1.ID_SRV=? ")
                 .setParameter(1, new Long(sessionIdArm))
                 .getSingleResult();
 	    	
 	    	 log.info("forViewCert:cert_data:"+certDataX); 
 	    	 
 	    	 SystemCertItem sciArm = new SystemCertItem();
 	    	 
 	    	 if(certDataX!=null){
 	    	 
 	    	 byte[] certByteX = Base64.decode(certDataX);
 	    	 
 	         
 	    	 
 	    	 CertificateFactory userCfArm = CertificateFactory.getInstance("X.509");
 	           X509Certificate userCertX = (X509Certificate)
 	        		   userCfArm.generateCertificate(new  ByteArrayInputStream(certByteX));
 	          
 	           
 	           log.info("armManager:forViewCert:02:"+userCertX);
 	         
 	          sciArm.setName(userCertX.getSubjectDN().getName());
 	          sciArm.setIssuer(userCertX.getIssuerDN().getName());
 	          sciArm.setSerial(decToHex(userCertX.getSerialNumber()));
 	          sciArm.setDate1(df.format(userCertX.getNotBefore()));
	          sciArm.setDate2(df.format(userCertX.getNotAfter()));
	          
 	    	 }
 	    	 
 	    	 Contexts.getEventContext().set("systemCertBean", sciArm);
 	   	 }
 	   }catch(Exception e){
 		   log.error("forViewCert:Error:"+e);
 	   }
     } 
    
   public int getConnectError(){
	   return connectError;
   }
   
   public List<AcApplication> getListArm() throws Exception{
	    log.info("armManager::getListArm:01");
	    
	    
	    try {
	    	if(listArm==null){
	    		
	    		AcUser au = (AcUser) Component.getInstance("currentUser",ScopeType.SESSION); 
	    		 
	    		if(au.getAllowedSys()!=null){
	    			
	    		   
	    			 
	    			listArm=entityManager.createQuery(
		       				  "select o from AcApplication o " +
		       				  "where o.idArm in (:idsArm) ")
		       				  .setParameter("idsArm", au.getAllowedSys())
		       				  .getResultList();
	    		}else{
	       		   listArm=entityManager.createQuery(
	       				  "select o from AcApplication o")
	       				  .getResultList();
	    		}
	    	}
	     } catch (Exception eArm) {
	    	 log.error("armManager::getListArm:ERROR:"+eArm);
	         throw eArm;
	     }
	    return listArm;
  }
   
   public void setAuditItemsListSelect(List <BaseTableItem> auditItemsListSelect) {
	    this.auditItemsListSelect=auditItemsListSelect;
}

   
   public List <BaseTableItem> getAuditItemsListSelect() {
		 log.info("getAuditItemsListSelect:01");
	
	    ArmContext acArm= new ArmContext();
		   if( auditItemsListSelect==null){
			   log.info("getAuditItemsListSelect:02");
			   auditItemsListSelect = new ArrayList<BaseTableItem>();
			   
			
			   auditItemsListSelect.add(acArm.getAuditItemsMap().get("name"));
			   auditItemsListSelect.add(acArm.getAuditItemsMap().get("code"));
			   auditItemsListSelect.add(acArm.getAuditItemsMap().get("description"));
		   }
	       return this.auditItemsListSelect;
   }
   
   
   public List <BaseTableItem> getAuditItemsListContext() {
	   log.info("orgManager:getAuditItemsListContext");
	   if(auditItemsListContext==null){
		   ArmContext acArm= new ArmContext();
		   auditItemsListContext = new ArrayList<BaseTableItem>();
		   
		   
		   auditItemsListContext=acArm.getAuditItemsCollection();
	   }
	   return this.auditItemsListContext;
   }
   
	public List<AcUser> getAdminListForView() {
		
		List<Object[]> lo=null;
	      try{
	     		
	     	if(this.adminListForView==null){
	     		String remoteAuditArm = FacesContext.getCurrentInstance().getExternalContext()
	 	             .getRequestParameterMap()
	 	             .get("remoteAudit");
	     		String sessionId = FacesContext.getCurrentInstance().getExternalContext()
	    	             .getRequestParameterMap()
	    	             .get("sessionId");
	     		
	     		if(remoteAuditArm==null||sessionId==null){
	     			return null;
	     		}
	     		
	 	        log.info("rolManager:getUsrSelectListForView:remoteAudit:"+remoteAuditArm);
	 	        log.info("rolManager:getUsrSelectListForViewt:sessionId:"+sessionId);
	 	        
	 	            
	 	       lo=entityManager.createNativeQuery(
			    	   "select t1.t1_id, t1.t1_login, t1.t1_fio "+
	                   "from (select AU_FULL.ID_SRV t1_id, AU_FULL.LOGIN t1_login, "+  
	                  "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO) t1_fio "+
	                     "from "+ 
	                     "AC_USERS_KNL_T AU_full, "+ 
	                     "LINK_ADMIN_USER_SYS uul, "+ 
	                     "ISP_BSS_T CL_USR_FULL, "+
	                     "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "+
	                         "from ISP_BSS_T cl_usr, "+ 
	                         "AC_USERS_KNL_T au "+ 
	                        "where AU.UP_SIGN_USER  = CL_usr.SIGN_OBJECT "+ 
	                        "group by CL_usr.SIGN_OBJECT) t2 "+   
	                     "where  AU_FULL.UP_SIGN_USER=t2.CL_USR_CODE(+) "+ 
	                     "and CL_USR_FULL.ID_SRV(+)=t2.CL_USR_ID "+
	                   "and UUL.UP_USER= AU_FULL.ID_SRV "+ 
	                     "and UUL.UP_SYS=? "+
	                     "order by t1_fio "+ 
	                     ") t1 ")
			      		.setParameter(1, new Long(sessionId))
					 .getResultList();
	 	    	 
	 	         
	 	       this.adminListForView=new ArrayList<AcUser>();
	 	       
	 	       for(Object[] objectArray :lo){
	 	    	 AcUser au = new AcUser();
	 	    	 
	 	    	 this.adminListForView.add(au);
	 	    	 
	 	    	 //не используется при только отображении
	 	    	 
	 	    	 au.setFio(objectArray[2]!=null?objectArray[2].toString():"");
	 	    	 au.setLogin(objectArray[1]!=null?objectArray[1].toString():"");
	 	       }
	 	         
	     	 }
	     	}catch(Exception e){
	 			log.error("rolManager:getUsrSelectListForView:error:"+e);
	 		}
	   return adminListForView;
	}
	public void setAdminListForView(List<AcUser> adminListForView) {
		this.adminListForView = adminListForView;
	}
	
    
   public void selectRecord(){
	    String  sessionIdArm = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	    log.info("selectRecord:sessionId="+sessionIdArm);
	    
	   //  forView; //!!!
	    List<String>  selRecArm = (ArrayList<String>)
				  Component.getInstance("selRecArm",ScopeType.SESSION);
	    
	    if(selRecArm==null){
	       selRecArm = new ArrayList<String>();
	       log.info("selectRecord:01");
	    }
	    
	    
	    AcApplication aa = new AcApplication();
  	   
	    
	    if(aa!=null){
	     if(selRecArm.contains(sessionIdArm)){
	    	selRecArm.remove(sessionIdArm);
	    	aa.setSelected(false);
	    	log.info("selectRecord:02");
	     }else{
	    	selRecArm.add(sessionIdArm);
	    	aa.setSelected(true);
	    	log.info("selectRecord:03");
	     }
	    Contexts.getSessionContext().set("selRecArm", selRecArm);	
	    
	    Contexts.getEventContext().set("armBean", aa);
	   }
	}
   
  
   
   public int getDelNot() {
	   return delNot;
   }
   public boolean getArmCodeExist() {
	    return armCodeExist;
   }
   public String getDellMessage() {
	   return dellMessage;
   }
   public void setDellMessage(String dellMessage) {
	   this.dellMessage = dellMessage;
   } 
   
   private boolean armCodeExistCrt(String armCode) throws Exception {
		log.info("ArmManager:ArmcodeExistCrt:armCode="+armCode);
		if(armCode!=null){
		  try{
			  List<Object> lo=entityManager.createNativeQuery(
					  "select rl.sign_object "+
                      "from AC_IS_BSS_T rl "+
                       "where RL.SIGN_OBJECT=? ")
	  				.setParameter(1, armCode)
	  				.getResultList();
	  
	          if(!lo.isEmpty()){
		        armCodeExist=true;
	          }
			  log.info("ArmManager:ArmCodeExistCrt:addLoginExist!");	
			  
		  }catch(Exception e){
	           log.error("ArmManager:ArmCodeExistCrt:Error:"+e);
	           throw e;
       }
		}
		return this.armCodeExist;
 }
  
  private boolean armCodeExistUpd(String armCode, Long idArm) throws Exception {
	   
		log.info("ArmManager:ArmcodeExistUpd:armCode:"+armCode);
		log.info("ArmManager:ArmcodeExistUpd:idArm:"+idArm);
			
		if(armCode!=null){
		  try{
			  List<Object> lo=entityManager.createNativeQuery(
			  			        "select rl.sign_object "+
                                "from AC_IS_BSS_T rl "+
                                "where RL.SIGN_OBJECT=? "+ 
                                "and RL.ID_SRV !=? ")
			  				.setParameter(1, armCode)
			  				.setParameter(2, idArm)
			  				.getResultList();
			  
			  if(!lo.isEmpty()){
				  armCodeExist=true;
			  }
			  
			  log.info("ArmManager:armCodeExistUpd:addLoginExist!");		     
		    }catch(Exception e){
	           log.error("ArmManager:armCodeExistUpd:Error:"+e);
	           throw e;
         }
		}
		return this.armCodeExist;
  }
  
  public void audit(ResourcesMap resourcesMap, ActionsMap actionsMap){
	   try{
		   AuditExportData auditExportDataArm = (AuditExportData)Component.getInstance("auditExportData",ScopeType.SESSION);
		   auditExportDataArm.addFunc(resourcesMap.getCode()+":"+actionsMap.getCode());
		   
	   }catch(Exception e){
		   log.error("GroupManager:audit:error:"+e);
	   }
  }
  
   public Boolean getEvaluteForList() {
	
   	log.info("armManager:evaluteForList:01");
   	if(evaluteForList==null){
   		evaluteForList=false;
    	String remoteAuditArm = FacesContext.getCurrentInstance().getExternalContext()
	             .getRequestParameterMap()
	             .get("remoteAudit");
	   log.info("armManager:evaluteForList:remoteAudit:"+remoteAuditArm);
     	
    	if(remoteAuditArm!=null&&
    	 
    	   !"OpenCrtFact".equals(remoteAuditArm)&&	
    	   !"OpenUpdFact".equals(remoteAuditArm)&&
    	   !"OpenDelFact".equals(remoteAuditArm)&&
   	       !"onSelColFact".equals(remoteAuditArm)&&
   	       !"refreshPdFact".equals(remoteAuditArm)){
    		log.info("reposManager:evaluteForList!!!");
   		    evaluteForList=true;
    	}
   	 }
       return evaluteForList;
   }
   public Boolean getEvaluteForListFooter() {
		
	  
	   	if(evaluteForListFooter==null){
	   		evaluteForListFooter=false;
	    	String remoteAuditArm = FacesContext.getCurrentInstance().getExternalContext()
		             .getRequestParameterMap()
		             .get("remoteAudit");
		   log.info("armManager:evaluteForListFooter:remoteAudit:"+remoteAuditArm);
	     
	    	if(getEvaluteForList()&&
	    	    	
	    	   !"protBeanWord".equals(remoteAuditArm)&&	
	    	    	
	   	       !"selRecAllFact".equals(remoteAuditArm)&&
	   	       !"clRecAllFact".equals(remoteAuditArm)&&
	   	      // !remoteAudit equals "clSelOneFact"
	   	       !"onSelColSaveFact".equals(remoteAuditArm)){
	    		  log.info("armManager:evaluteForListFooter!!!");
	   		      evaluteForListFooter=true;
	    	}
	   	 }
	       return evaluteForListFooter;
	   }
   
   public Boolean getEvaluteForBean() {
		
		  
		   	if(evaluteForBean==null){
		   		evaluteForBean=false;
		    	String remoteAuditArm = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("remoteAudit");
			    log.info("armManager:evaluteForBean:remoteAudit:"+remoteAuditArm);
				String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("sessionId");
			    log.info("armManager:evaluteForBean:sessionId:"+sessionId);
		    	if(sessionId!=null && remoteAuditArm!=null &&
		    	   ("rowSelectFact".equals(remoteAuditArm)||	
		    	    "UpdFact".equals(remoteAuditArm))){
		    	      log.info("armManager:evaluteForBean!!!");
		   		      evaluteForBean=true;
		    	}
		   	 }
		     return evaluteForBean;
		   }
   
   private String decToHex(BigInteger bi) {
		
		String resultArm = null;
		
		try
		{
		 resultArm = bi.toString(16);
		 
		}
		catch (NumberFormatException e)
		{
			log.error("Error! tried to parse an invalid number format");
		}
		 return resultArm;
	}
public String getCommentApp() {
	return commentApp;
}
public void setCommentApp(String commentApp) {
	this.commentApp = commentApp;
}
}

