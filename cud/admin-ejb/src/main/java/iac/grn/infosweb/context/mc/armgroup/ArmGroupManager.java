package iac.grn.infosweb.context.mc.armgroup;

import iac.cud.infosweb.dataitems.BaseItem;
import iac.cud.infosweb.dataitems.SystemCertItem;
import iac.cud.infosweb.entity.AcApplication;
import iac.cud.infosweb.entity.AcUser;
import iac.cud.infosweb.entity.GroupSystemsKnlT;
import iac.cud.infosweb.entity.LinkGroupSysSysKnlT;
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

import ru.spb.iac.cud.core.util.CUDConstants;

/**
 * ”правл€ющий Ѕин
 * @author bubnov
 *
 */
@Name("armGroupManager")
 public class ArmGroupManager {
	
	 @Logger private Log log;
	
	 @In 
	 EntityManager entityManager;
	 
	/**
     * Ёкспортируема€ сущности 
     * дл€ отображени€
     */
	
	private String dellMessage=null;
	private int delNot=0;
	
	private List<BaseItem> auditList; 
	
	private Long auditCount;
	
	private List <BaseTableItem> auditItemsListSelect;
	
	private List <BaseTableItem> auditItemsListContext;
	
	private int connectError=0;
	private Boolean evaluteForList;
	private Boolean evaluteForListFooter;  
	private Boolean evaluteForBean;
	
	private List<GroupSystemsKnlT> listArmGroup = null;
	
	private boolean armGroupCodeExist=false;
	
	private List<BaseItem> ISList;
	
	private List<BaseItem> ISSelectListForView;
	
	private Long idArmGroupCrt;
	
	public List<BaseItem> getAuditList(int firstRow, int numberOfRows){
	  String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
	             .getRequestParameterMap()
	             .get("remoteAudit");
	   log.info("auditManager:getAuditList:remoteAudit:"+remoteAudit);
	  
	  
	  log.info("getAuditList:firstRow:"+firstRow);
	  log.info("getAuditList:numberOfRows:"+numberOfRows);
	  
	  List<BaseItem> armGroupListCached = (List<BaseItem>)
			  Component.getInstance("armGroupListCached",ScopeType.SESSION);
	  if(auditList==null){
		  log.info("getAuditList:01");
		 	if(("rowSelectFact".equals(remoteAudit)||
			    "selRecAllFact".equals(remoteAudit)||
			    "clRecAllFact".equals(remoteAudit)||
			    "clSelOneFact".equals(remoteAudit)||
			    "onSelColSaveFact".equals(remoteAudit))&&
			    armGroupListCached!=null){
		    	this.auditList=armGroupListCached;
			}else{
				log.info("getAuditList:03");
		    	invokeLocal("list", firstRow, numberOfRows, null);
			    Contexts.getSessionContext().set("armGroupListCached", this.auditList);
			    log.info("getAuditList:03:"+this.auditList.size());
			}
		 	
		 	List<String>  selRecArmGroup = (ArrayList<String>)
					  Component.getInstance("selRecArmGroup",ScopeType.SESSION);
		 	if(this.auditList!=null && selRecArmGroup!=null) {
		 		 for(BaseItem it:this.auditList){
				   if(selRecArmGroup.contains(it.getBaseId().toString())){
					 
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
			 String orderQuery=null;
			 log.info("hostsManager:invokeLocal");
			 
			 ArmGroupStateHolder orgStateHolder = (ArmGroupStateHolder)
					  Component.getInstance("armGroupStateHolder",ScopeType.SESSION);
			 
			 Map<String, String> filterMapArmGroup = orgStateHolder.getColumnFilterValues();
			 String st=null;
			 
			 if("list".equals(type)){
				 log.info("ArmGroup:invokeLocal:list:01");
				 
				
				 Set<Map.Entry<String, String>> set = orgStateHolder.getSortOrders().entrySet();
                 for (Map.Entry<String, String> me : set) {
      		        
      		       if(orderQuery==null){
      		    	 orderQuery="order by "+me.getKey()+" "+me.getValue();
      		       }else{
      		    	 orderQuery=orderQuery+", "+me.getKey()+" "+me.getValue();  
      		       }
      		     }
                 log.info("ArmGroup:invokeLocal:list:orderQuery:"+orderQuery);
                
                 if(filterMapArmGroup!=null){
    	    		 Set<Map.Entry<String, String>> setFilterArmGroup = filterMapArmGroup.entrySet();
    	              for (Map.Entry<String, String> me : setFilterArmGroup) {
    	             
    	            		//делаем фильтр на начало
    	            	  st=(st!=null?st+" and " :"")+" lower("+me.getKey()+") like lower('"+me.getValue()+"%') ";
    	                }
    	    	   }
                 log.info("ArmGroup:invokeLocal:list:filterQuery:"+st);
             
                 auditList = entityManager.createQuery("select o from GroupSystemsKnlT o "+
						 (st!=null ? " where "+st :"")+
						 (orderQuery!=null ? orderQuery+", o.idSrv " : " order by o.idSrv "))
                       .setFirstResult(firstRow)
                       .setMaxResults(numberOfRows)
                       .getResultList();
             log.info("ArmGroup:invokeLocal:list:02");
  
			 } else if("count".equals(type)){
				 log.info("ArmGroupManagerList:count:01");
				 
				 if(filterMapArmGroup!=null){
    	    		 Set<Map.Entry<String, String>> setFilterArmGroup = filterMapArmGroup.entrySet();
    	              for (Map.Entry<String, String> me : setFilterArmGroup) {
    	             		//делаем фильтр на начало
    	            	  st=(st!=null?st+" and " :"")+" lower("+me.getKey()+") like lower('"+me.getValue()+"%') ";
    	               }
    	    	   }
                 log.info("ArmGroup:invokeLocal:count:filterQuery:"+st);
				 
				 
				 auditCount = (Long)entityManager.createQuery(
						 "select count(o) " +  
				         "from GroupSystemsKnlT o "+
				         (st!=null ? " where "+st :""))
		                .getSingleResult();
				 
               log.info("ArmGroup:invokeLocal:count:02:"+auditCount);
           	 }
		}catch(Exception eArmGroup){
			  log.error("ArmGroup:invokeLocal:error:"+eArmGroup);
			  evaluteForList=false;
			  FacesMessages.instance().add("ќшибка!");
		}
	}
	
	
   public void forView(String modelType) {
	   String  armGroupId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	  log.info("forView:sessionId:"+armGroupId);
	  log.info("forView:modelType:"+modelType);
	  if(armGroupId!=null){
		  
		   
			if(modelType==null){
		    	return ;
		    }
			
		
		 GroupSystemsKnlT ar = searchBean(armGroupId);
		 
		 if(ar==null){
			 ar = entityManager.find(GroupSystemsKnlT.class, new Long(armGroupId));
		 }
		 
		 if(!isAllowedSys(new Long(armGroupId))){
			 log.info("armGroupManager:forView:02");
			 ar.setIsAllowedSys(false);
		 }		 
		 
		 Contexts.getEventContext().set("armGroupBean", ar);
		 
		 forViewCert();
	  }
   }
   
   public boolean isAllowedSys(Long idGr){
	   log.info("armGroupManager:isAllowedSys:01:"+idGr);
	 
	   boolean result = false;
	   
	   try {
	   
		  AcUser au = (AcUser) Component.getInstance("currentUser",ScopeType.SESSION); 
  		 
   		  if(au.getAllowedSys()!=null){
   		   
   			//список »— в группе, которых нет в прив€зке к пользователю  
		    List<Object> list= entityManager.createNativeQuery(
                        "SELECT LGS.UP_SYSTEMS " + 
                        "   FROM GROUP_SYSTEMS_KNL_T gr, LINK_GROUP_SYS_SYS_KNL_T lgs " + 
                        "   WHERE     GR.ID_SRV = LGS.UP_GROUP_SYSTEMS " + 
                        "         AND GR.ID_SRV = :idGr " + 
                        "         AND LGS.UP_SYSTEMS NOT IN (:idsArm) " + 
                        "GROUP BY LGS.UP_SYSTEMS ")
                      .setParameter("idGr", idGr)
                      .setParameter("idsArm", au.getAllowedSys())
                      .getResultList();
		    
		    //если нет групп, которые не прив€заны к пользователю,
		    //тогда у пользовател€ есть доступ к этой группе
		    //в т. ч. пустые группы
		    if(list.isEmpty()){
		    	result = true;
		    }
		    
	    }else{
	    	result = true;
	    }
	   
	   }catch (Exception e) {
	       log.error("armGroupManager:isAllowedSys:ERROR:"+e);
	    }
	   
	   log.info("armGroupManager:isAllowedSys:02:"+result);
	   
	   return result;
	}
   
   
   private GroupSystemsKnlT searchBean(String sessionId){
    	
      if(sessionId!=null){
    	 List<GroupSystemsKnlT> armGroupListCached = (List<GroupSystemsKnlT>)
				  Component.getInstance("armGroupListCached",ScopeType.SESSION);
		if(armGroupListCached!=null){
			for(GroupSystemsKnlT it : armGroupListCached){
				 
			 
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
   
   public void addArmGroup(){
	   log.info("armGroupManager:addOrg:01");
	   
	   GroupSystemsKnlT armGroupBeanCrt = (GroupSystemsKnlT)
				  Component.getInstance("armGroupBeanCrt",ScopeType.CONVERSATION);
	   
	   if(armGroupBeanCrt==null){
		   return;
	   }
	 
	   try {
		  
		 if(!armGroupCodeExistCrt( 
				 CUDConstants.groupArmPrefix+armGroupBeanCrt.getGroupCode().trim())){
		   
		  AcUser au = (AcUser) Component.getInstance("currentUser",ScopeType.SESSION); 
		  
		  armGroupBeanCrt.setGroupName(armGroupBeanCrt.getGroupName().trim());
		  armGroupBeanCrt.setGroupCode(CUDConstants.groupArmPrefix+armGroupBeanCrt.getGroupCode().trim());
			  
		  if(armGroupBeanCrt.getDescription()!=null&&!armGroupBeanCrt.getDescription().trim().equals("")){
			  armGroupBeanCrt.setDescription(armGroupBeanCrt.getDescription().trim());
		  }else{
			  armGroupBeanCrt.setDescription(null);
		  }
		  
		  armGroupBeanCrt.setCreator(au.getIdUser());
		  armGroupBeanCrt.setCreated(new Date());
	      entityManager.persist(armGroupBeanCrt);
	    
	      entityManager.flush();
 	  	  entityManager.refresh(armGroupBeanCrt);
	    
	      idArmGroupCrt=armGroupBeanCrt.getBaseId();
	      
	      audit(ResourcesMap.IS_GROUP, ActionsMap.CREATE); 
	      
	    }
	      
	    }catch (Exception e) {
	       log.error("armGroupManager:addArmGroup:ERROR:"+e);
	    }
	   
   }
   
   public void updArmGroup(){
	   
	   log.info("armGroupManager:updArmGroup:01");
	   
	   GroupSystemsKnlT armGroupBean = (GroupSystemsKnlT)
				  Component.getInstance("armGroupBean",ScopeType.CONVERSATION);
	   
	   String  sessionId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	   log.info("armGroupManager:updArmGroup:sessionId:"+sessionId);
	
	   if(armGroupBean==null || sessionId==null){
		   return;
	   }
	
	   try {
		   
		 if(!armGroupCodeExistUpd(
				 armGroupBean.getGroupCode().trim(), new Long(sessionId))){  
		
		 AcUser au = (AcUser) Component.getInstance("currentUser",ScopeType.SESSION);
		   
		 GroupSystemsKnlT aam = entityManager.find(GroupSystemsKnlT.class, new Long(sessionId));
		  
		  aam.setGroupName(armGroupBean.getGroupName().trim());
		  aam.setGroupCode(armGroupBean.getGroupCode().trim());
		  
		  if(armGroupBean.getDescription()!=null&&!armGroupBean.getDescription().trim().equals("")){
			  aam.setDescription(armGroupBean.getDescription().trim());
		  }else{
			  aam.setDescription(null);
		  }
			  
		  aam.setModificator(au.getIdUser());
		  aam.setModified(new Date());
		  
		  entityManager.flush();
	      entityManager.refresh(aam);
	    	  
	      Contexts.getEventContext().set("armGroupBean", aam);
		
	      audit(ResourcesMap.IS_GROUP, ActionsMap.UPDATE); 
	      
		 }
		 
	     }catch (Exception e) {
           log.error("armGroupManager:updSrm:ERROR:"+e);
         }
   }
   
    public void updIS(){
	   
	   log.info("armGroupManager:updIS:01");
	   
	   
	   String sessionId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	   log.info("armGroupManager:updIS:sessionId:"+sessionId);
	
	   if( sessionId==null){
		   return;
	   }
	
	   AcUser currentUser = (AcUser) Component.getInstance("currentUser",ScopeType.SESSION);
	   
	   try {
		   
		   GroupSystemsKnlT aum = entityManager.find(GroupSystemsKnlT.class, new Long(sessionId));
	
		 
		
		  List<LinkGroupSysSysKnlT> guuExistList =  aum.getLinkGroupSysSysKnlTs();
		  log.info("armGroupManager:updIS:size1:"+guuExistList.size());
		  
		  
		   
		  
		  for(BaseItem is:this.ISList){
	    			  log.info("UsrManager:editUsr:updUsrGroup:"+((AcApplication)is).getName());
	    			  log.info("UsrManager:editUsr:updUsrGroup:"+((AcApplication)is).getUsrChecked());
	    			  
	    			  if(((AcApplication)is).getUsrChecked().booleanValue()){
	    				  // отмечен
	    				  
	    				  LinkGroupSysSysKnlT au = new LinkGroupSysSysKnlT( ((AcApplication)is).getIdArm(), new Long(sessionId));
	    			            au.setCreated(new Date());
	    			            au.setCreator(currentUser.getBaseId());
	    			            
	    			         if(guuExistList.contains(au)){
	    			        	 //есть в базе
	    			        	 //ничего не делаем. и так есть в базе
	    			        	 log.info("armGroupManager:updIS:02");
	    			         }else{
	    			           
	    			            guuExistList.add(au);
	    			            log.info("armGroupManager:updIS:03");
	    			         }
	    			  }else{ // не отмечен
	    				  LinkGroupSysSysKnlT au = new LinkGroupSysSysKnlT( ((AcApplication)is).getIdArm(), new Long(sessionId));
 		            
 			                if(guuExistList.contains(au)){
 			                	//есть в базе
 			                	
 			                	guuExistList.remove(au);
 			                	
 			                	entityManager.createQuery(
 			                		"delete from LinkGroupSysSysKnlT lgu " +
 			                		"where lgu.pk.acIsBssT = :acIsBssT " +
 			                		"and lgu.pk.groupSystemsKnlT = :groupSystemsKnlT ")
 			                	.setParameter("acIsBssT", ((AcApplication)is).getIdArm())
 			                	.setParameter("groupSystemsKnlT", new Long(sessionId))
 			                	.executeUpdate();
 			                	
 			                	 log.info("armGroupManager:updIS:04");
 			                }else{
 			                //ничего не делаем. и так нет в базе
 			                	 log.info("armGroupManager:updIS:05");
 			                }
	    			  }
	    		  }
	     	  
	    
	     	
	     	
	   	log.info("armGroupManager:updIS:size3:"+aum.getLinkGroupSysSysKnlTs().size());
	    	  
	        entityManager.flush();
	    	 
	        entityManager.refresh(aum);
	    	  
	        	 
         GroupSystemsKnlT ar = searchBean(sessionId);
         if(ar==null){
			 ar = entityManager.find(GroupSystemsKnlT.class, new Long(sessionId));
		 }
         
		 Contexts.getEventContext().set("armGroupBean", ar);
		 
		 forViewCert();
		 
	  
 
	    	ISReset();
	    	
	    	audit(ResourcesMap.IS_GROUP, ActionsMap.UPDATE_IS); 
	    	
	     }catch (Exception e) {
        log.error("armGroupManager:updIS:ERROR:"+e);
      }
}

    public void saveArmGroupCertificate(byte[] file_byte, Long id_sys){
	   
	   log.info("armGroupManager:saveArmGroupCertificate:01:"+(file_byte!=null));
	   log.info("armGroupManager:saveArmGroupCertificate:02:"+id_sys);
	   
	   try {
		   
		  
		   
		   CertificateFactory userCf = CertificateFactory.getInstance("X.509");
		   X509Certificate userCertX = null;
           try{
        	   
         
           userCertX = (X509Certificate)
        		   userCf.generateCertificate(new  ByteArrayInputStream(file_byte));
           
           }catch(Exception e){
        	   
        	   log.info("armGroupManager:saveArmGroupCertificate:02");
        	   
        	   
           //2-€ попытка
           //ловим случай - когда сертификат скачан вручную  с сайта ”÷
           //в этом случае он без -BEGIN CERTIFICATE-
           
        	String certificateString =  "-----BEGIN CERTIFICATE-----\n"+new String(file_byte, "utf-8")+"\n-----END CERTIFICATE-----";
        	
        
        	   
            userCertX = (X509Certificate)
        		   userCf.generateCertificate(new ByteArrayInputStream(certificateString.getBytes("utf-8")));
           }  
           
           String x509Cert = Base64.encode(userCertX.getEncoded());
           
           log.info("armGroupManager:saveArmGroupCertificate:03:"+x509Cert);
           
           Transaction.instance().begin();
		   
		   Transaction.instance().enlist(entityManager);
		   
		   entityManager.createNativeQuery("update GROUP_SYSTEMS_KNL_T t1 " + 
		   		                           "set T1.CERT_DATA=? " + 
		   		                           "where t1.ID_SRV=? ")
		   .setParameter(1, x509Cert)
		   .setParameter(2, id_sys)
		   .executeUpdate();  
			 
		   audit(ResourcesMap.IS_GROUP, ActionsMap.ADD_CERT); 
		   
		   Transaction.instance().commit();
		   
	     }catch (Exception e) {
           log.error("armGroupManager:saveArmGroupCertificate:ERROR:"+e);
           try{
             Transaction.instance().rollback();
           }catch(Exception et){}
         }
   }

    public void removeCert(){
 	   
 	   try {
 		   
 		   String  sessionId = FacesContext.getCurrentInstance().getExternalContext()
 			        .getRequestParameterMap()
 			        .get("sessionId");
 		   log.info("armGroupManager:removeCert:sessionId:"+sessionId);
 		
 		   if(sessionId==null||sessionId.trim().equals("")){
 			   return;
 		   }
 			   
 		   entityManager.createNativeQuery("update GROUP_SYSTEMS_KNL_T t1 " + 
 		   		                           "set T1.CERT_DATA=null " + 
 		   		                           "where t1.ID_SRV=? ")
 		   .setParameter(1, new Long(sessionId))
 		   .executeUpdate();  
 			 
 		  audit(ResourcesMap.IS_GROUP, ActionsMap.REMOVE_CERT); 
 			   
 	     }catch (Exception e) {
            log.error("armGroupManager:removeCert:ERROR:"+e);
          }
   }
    
   public void delArmGroup(){
	 try{
		log.info("armGroupManager:delArmGroup:01");  
		
		GroupSystemsKnlT armGroupBean = (GroupSystemsKnlT)
				  Component.getInstance("armGroupBean",ScopeType.CONVERSATION);
	
		
		if(armGroupBean==null){
			return;
		}
		 
		log.info("armGroupManager:delArmGroup:IdArmGroup:"+armGroupBean.getBaseId());
		
		GroupSystemsKnlT aom = entityManager.find(GroupSystemsKnlT.class, armGroupBean.getBaseId());
		  
		entityManager.remove(aom);
		
		audit(ResourcesMap.IS_GROUP, ActionsMap.DELETE); 
		
	 }catch(Exception e){
		 log.error("armGroupManager:delArmGroup:error:"+e); 
	 }
    }
 
   public void setAuditItemsListSelect(List <BaseTableItem> auditItemsListSelect) {
	    this.auditItemsListSelect=auditItemsListSelect;
}
   
    public void forViewUpdDel() {
	   try{
	     String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	     log.info("forViewUpdDel:sessionId:"+sessionId);
	     if(sessionId!=null){
	    	 GroupSystemsKnlT ao = entityManager.find(GroupSystemsKnlT.class, new Long(sessionId));
	    	 Contexts.getEventContext().set("armGroupBean", ao);
	    	 
	    	//устанавливаем на 1 страницу пагинатор в модальном окне
	    	 ArmGroupStateHolder armGroupStateHolder = (ArmGroupStateHolder)
					  Component.getInstance("armGroupStateHolder",ScopeType.SESSION);
	    	 armGroupStateHolder.resetPageNumber();
	    	 
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
		
			  
			  GroupSystemsKnlT aa = entityManager.find(GroupSystemsKnlT.class, new Long(sessionId));
			
			 
			 
			 Contexts.getEventContext().set("armGroupBean", aa);
			 
			 
			 
		 }	
    }
  
    public void forViewCert() {
 	   try{
 	     String sessionId = FacesContext.getCurrentInstance().getExternalContext()
 			        .getRequestParameterMap()
 			        .get("sessionId");
 	     log.info("forViewCert:sessionId:"+sessionId);
 	     
 	     DateFormat df = new SimpleDateFormat ("dd.MM.yy");
 	    
 	     if(sessionId!=null){
 	    	 
 	    	String certDataX = (String) entityManager.createNativeQuery(
 	    			 "select to_char(T1.CERT_DATA) " + 
 	    	 		 "from GROUP_SYSTEMS_KNL_T t1 " + 
 	    	 		 "where T1.ID_SRV=? ")
                 .setParameter(1, new Long(sessionId))
                 .getSingleResult();
 	    	
 	    	 log.info("forViewCert:cert_data:"+certDataX); 
 	    	
 	     if(certDataX!=null&&!certDataX.trim().equals("")){
 	    	 byte[] certByteX = Base64.decode(certDataX);
 	    		 
 	    	 CertificateFactory userCf = CertificateFactory.getInstance("X.509");
 	           X509Certificate userCertX = (X509Certificate)
 	        		   userCf.generateCertificate(new ByteArrayInputStream(certByteX));
 	          
 	           
 	           log.info("armGroupManager:saveArmGroupCertificate:02:"+userCertX);
 	         
 	          SystemCertItem sci = new SystemCertItem();
 	    	 
 	          sci.setName(userCertX.getSubjectDN().getName());
 	          sci.setIssuer(userCertX.getIssuerDN().getName());
 	          sci.setSerial(decToHex(userCertX.getSerialNumber()));
 	          sci.setDate1(df.format(userCertX.getNotBefore()));
 	          sci.setDate2(df.format(userCertX.getNotAfter()));
 	          
 	    	 Contexts.getEventContext().set("systemCertBean", sci);
 	    	 
 	       }
 	   	 }
 	   }catch(Exception e){
 		   log.error("forViewCert:Error:"+e);
 	   }
     } 
    
   public int getConnectError(){
	   return connectError;
   }
   
   public List<GroupSystemsKnlT> getListArmGroup() throws Exception{
	    log.info("armGroupManager::getListArmGroup:01");
	    try {
	    	if(listArmGroup==null){
	       		listArmGroup=entityManager.createQuery("select o from AcSubsystemCertBssT o").getResultList();
	    	}
	     } catch (Exception e) {
	    	 log.error("armGroupManager::getListArmGroup:ERROR:"+e);
	         throw e;
	     }
	    return listArmGroup;
  }
   
   public List <BaseTableItem> getAuditItemsListSelect() {
		 log.info("getAuditItemsListSelect:01");
	
	    ArmGroupContext acArmGroup= new ArmGroupContext();
		   if( auditItemsListSelect==null){
			   log.info("getAuditItemsListSelect:02");
			   auditItemsListSelect = new ArrayList<BaseTableItem>();
			   
			
			   auditItemsListSelect.add(acArmGroup.getAuditItemsMap().get("groupName"));
			   auditItemsListSelect.add(acArmGroup.getAuditItemsMap().get("groupCode"));
		   }
	       return this.auditItemsListSelect;
   }
   
  
   
   public List <BaseTableItem> getAuditItemsListContext() {
	   log.info("orgManager:getAuditItemsListContext");
	   if(auditItemsListContext==null){
		   ArmGroupContext acArmGroup= new ArmGroupContext();
		   auditItemsListContext = new ArrayList<BaseTableItem>();
		  
		   auditItemsListContext=acArmGroup.getAuditItemsCollection();
	   }
	   return this.auditItemsListContext;
   }
      
    
   public void selectRecord(){
	    String  sessionId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	    log.info("selectRecord:sessionId="+sessionId);
	    
	   //  forView(); //!!!
	    List<String>  selRecArmGroup = (ArrayList<String>)
				  Component.getInstance("selRecArmGroup",ScopeType.SESSION);
	    
	    if(selRecArmGroup==null){
	       selRecArmGroup = new ArrayList<String>();
	       log.info("selectRecord:01");
	    }
	    
	     GroupSystemsKnlT aa = new GroupSystemsKnlT();
  	    
	    if(aa!=null){
	     if(selRecArmGroup.contains(sessionId)){
	    	selRecArmGroup.remove(sessionId);
	    	aa.setSelected(false);
	    	log.info("selectRecord:02");
	     }else{
	    	selRecArmGroup.add(sessionId);
	    	aa.setSelected(true);
	    	log.info("selectRecord:03");
	     }
	    Contexts.getSessionContext().set("selRecArmGroup", selRecArmGroup);	
	    
	    Contexts.getEventContext().set("armGroupBean", aa);
	   }
	}
   
   public String getDellMessage() {
	   return dellMessage;
   }
   public void setDellMessage(String dellMessage) {
	   this.dellMessage = dellMessage;
   } 
   
   public int getDelNot() {
	   return delNot;
   }
   public boolean getArmGroupCodeExist() {
	    return armGroupCodeExist;
   }
   private boolean armGroupCodeExistCrt(String armGroupCode) throws Exception {
		log.info("ArmGroupManager:ArmGroupcodeExistCrt:armGroupCode="+armGroupCode);
		if(armGroupCode!=null){
		  try{
			  List<Object> lo=entityManager.createNativeQuery(
					  "select 1 "+
                      "from GROUP_SYSTEMS_KNL_T ass "+
                      "where ASS.GROUP_CODE = ? ")
	  				.setParameter(1, armGroupCode)
	  				.getResultList();
	  
	          if(!lo.isEmpty()){
		        armGroupCodeExist=true;
		        return this.armGroupCodeExist;
	          }
	          
	      	  log.info("ArmGroupManager:ArmGroupCodeExistCrt:addLoginExist!");	
			  
		  }catch(Exception e){
	           log.error("ArmGroupManager:ArmGroupCodeExistCrt:Error:"+e);
	           throw e;
       }
		}
		return this.armGroupCodeExist;
 }
  
  private boolean armGroupCodeExistUpd(String armGroupCode, Long idArmGroup) throws Exception {
	   
		log.info("ArmGroupManager:ArmGroupcodeExistUpd:armGroupCode:"+armGroupCode);
		log.info("ArmGroupManager:ArmGroupcodeExistUpd:idArmGroup:"+idArmGroup);
			
		if(armGroupCode!=null){
		  try{
			  List<Object> lo=entityManager.createNativeQuery(
					          "select 1 "+
		                      "from GROUP_SYSTEMS_KNL_T ass "+
		                      "where ASS.GROUP_CODE = ? " +
		                      "and ASS.ID_SRV!= ? ")
			  				.setParameter(1, armGroupCode)
			  				.setParameter(2, idArmGroup)
			  				.getResultList();
			  
			  if(!lo.isEmpty()){
				  armGroupCodeExist=true;
				  return this.armGroupCodeExist;
			  }
			  
			  if(!lo.isEmpty()){
		        armGroupCodeExist=true;
	          }
			  
			  log.info("ArmGroupManager:armGroupCodeExistUpd:addLoginExist!");		     
		    }catch(Exception e){
	           log.error("ArmGroupManager:armGroupCodeExistUpd:Error:"+e);
	           throw e;
         }
		}
		return this.armGroupCodeExist;
  }
  
  public List<BaseItem> getISList(){
		 
	   //в updUsrGroup вызываетс€ usrGroupReset() - чтобы
	   // если перед сохранением изменить фильтр на фильтр_2, то в APPLY_REQUEST »—ѕќЋ№«”ё“—я
	   //≈ў® ѕ–≈∆Ќ»≈ ‘»Ћ№“–џ - так и надо, но эти фильтры затем переустанавливаютс€ на фильтр_2,
	   //и в commandButton есть reRender="..., outCBdtUpdArmGroup_dtbl, ..."
	   //а значит нам надо обновить groupList с учЄтом новых фильтров.
	   
	   if(this.ISList==null){
		  String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
		   String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("remoteAudit");
		   log.info("ArmGroupManager:getISList:sessionId:"+sessionId);
		   log.info("ArmGroupManager:getISList:remoteAudit:"+remoteAudit);
		   
		   if(sessionId==null){
			   return this.ISList;
		   }
		   
		   String st=null;
		 
		   ArmGroupISStateHolder armGroupISStateHolder = (ArmGroupISStateHolder)
					  Component.getInstance("armGroupISStateHolder",ScopeType.SESSION);
		   Map<String, String> filterMap = armGroupISStateHolder.getColumnFilterValues();
		   
		   
		   if(filterMap!=null){
	    		 Set<Map.Entry<String, String>> setFilterArmGroup = filterMap.entrySet();
	              for (Map.Entry<String, String> me : setFilterArmGroup) {
	            	
	             //аналог
	            if(me.getValue()!=null&&!"".equals(me.getValue())){
	            	
	   		    		//делаем фильтр на начало
	            	  st=(st!=null?st+" and " :"")+" lower("+me.getKey()+") like lower('"+me.getValue()+"%') ";
	             }
	           }
	      }
		   log.info("ArmGroupManager:getGroupList:st:"+st);
		   
		   AcUser au = (AcUser) Component.getInstance("currentUser",ScopeType.SESSION); 
  		 
   		  if(au.getAllowedSys()!=null){
		   
		   this.ISList = entityManager.createQuery(
				   "select o from AcApplication o " +
				   "where o.idArm in (:idsArm) "+
					(st!=null ? " and "+st :" ")+
				   " order by o.name ")
				   .setParameter("idsArm", au.getAllowedSys())
				   .getResultList();
		 
   		  }else{
   			 this.ISList = entityManager.createQuery(
  				   "select o from AcApplication o " +
  					(st!=null ? " where "+st :" ")+
  				   " order by o.name ")
  				   .getResultList();
   		  }
		  
		   
		   
		     List<AcApplication> listIS=entityManager.createQuery(
		    		 "select o from AcApplication o JOIN o.linkGroupSysSysKnlTs o1 where o1.pk.groupSystemsKnlT = :acGroup ")
					 .setParameter("acGroup", new Long(sessionId))
		      		 .getResultList();
		   
		     for(BaseItem group :this.ISList){
	           if (listIS.contains((AcApplication)group)){  
	        	  ((AcApplication)group).setUsrChecked(true);
			   }
	         } 
	   }
	   return this.ISList;
  }
  
  public void setISList(List<BaseItem> groupList){
	   this.ISList=groupList;
  }
  
  public void ISReset(){
	   if(this.ISList!=null){
		   this.ISList.clear();
		   this.ISList=null;  
	   }
  }
  public Long getIdArmGroupCrt() {
		return idArmGroupCrt;
	  }
	  public void setIdArmGroupCrt(Long idArmGroupCrt) {
		this.idArmGroupCrt = idArmGroupCrt;
	  }
  public List<BaseItem> getISSelectListForView(){
  	
	   List<Object[]> lo=null;
      try{
     		
     	if(this.ISSelectListForView==null){
     		String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
 	             .getRequestParameterMap()
 	             .get("remoteAudit");
     		String sessionId = FacesContext.getCurrentInstance().getExternalContext()
    	             .getRequestParameterMap()
    	             .get("sessionId");
     		
     		if(remoteAudit==null||sessionId==null){
     			return null;
     		}
     		
 	        log.info("ArmGroupManager:getISSelectListForView:remoteAudit:"+remoteAudit);
 	        log.info("ArmGroupManager:getISSelectListForView:sessionId:"+sessionId);
 	        
 	    
 	        
 	       lo=entityManager.createNativeQuery(
		    		"select ARM.FULL_, ARM.SIGN_OBJECT "+
                    "from AC_IS_BSS_T arm, "+
                    "GROUP_SYSTEMS_KNL_T garm, "+
                    "LINK_GROUP_SYS_SYS_KNL_T lga "+
                    "where ARM.ID_SRV=LGA.UP_SYSTEMS "+
                    "and GARM.ID_SRV=LGA.UP_GROUP_SYSTEMS "+
                    "and GARM.ID_SRV = ? ")
		      		.setParameter(1, new Long(sessionId))
				 .getResultList();
 	    	 
 	         
 	       this.ISSelectListForView=new ArrayList<BaseItem>();
 	       
 	       for(Object[] objectArray :lo){
 	    	 AcApplication au = new AcApplication();
 	    	 
 	    	 this.ISSelectListForView.add(au);
 	    	 
 	    	 //не используетс€ при только отображении
 	    	 au.setName(objectArray[0]!=null?objectArray[0].toString():"");
 	    	 au.setCode(objectArray[1]!=null?objectArray[1].toString():"");
 	       }
 	         
     	 }
     	}catch(Exception e){
 			log.error("ArmGroupManager:getISSelectListForView:error:"+e);
 		}
        	return this.ISSelectListForView;
     }
  
 
  
  public void audit(ResourcesMap resourcesMap, ActionsMap actionsMap){
	   try{
		   AuditExportData auditExportDataArmGroup = (AuditExportData)Component.getInstance("auditExportData",ScopeType.SESSION);
		   auditExportDataArmGroup.addFunc(resourcesMap.getCode()+":"+actionsMap.getCode());
		   
	   }catch(Exception e){
		   log.error("GroupManager:audit:error:"+e);
	   }
  }
  
   public Boolean getEvaluteForList() {
	
   	log.info("armGroupManager:evaluteForList:01");
   	if(evaluteForList==null){
   		evaluteForList=false;
    	String remoteAuditArmGroup = FacesContext.getCurrentInstance().getExternalContext()
	             .getRequestParameterMap()
	             .get("remoteAudit");
	   log.info("armGroupManager:evaluteForList:remoteAudit:"+remoteAuditArmGroup);
     	
    	if(remoteAuditArmGroup!=null&&
    	 
    	   !"OpenCrtFact".equals(remoteAuditArmGroup)&&	
    	   !"OpenUpdFact".equals(remoteAuditArmGroup)&&
    	   !"OpenDelFact".equals(remoteAuditArmGroup)&&
   	       !"onSelColFact".equals(remoteAuditArmGroup)&&
   	       !"refreshPdFact".equals(remoteAuditArmGroup)){
    		log.info("reposManager:evaluteForList!!!");
   		    evaluteForList=true;
    	}
   	 }
       return evaluteForList;
   }
   public Boolean getEvaluteForListFooter() {
		
	  
	   	if(evaluteForListFooter==null){
	   		evaluteForListFooter=false;
	    	String remoteAuditArmGroup = FacesContext.getCurrentInstance().getExternalContext()
		             .getRequestParameterMap()
		             .get("remoteAudit");
		   log.info("armGroupManager:evaluteForListFooter:remoteAudit:"+remoteAuditArmGroup);
	     
	    	if(getEvaluteForList()&&
	    	    	
	    	   !"protBeanWord".equals(remoteAuditArmGroup)&&	
	    	    	
	   	       !"selRecAllFact".equals(remoteAuditArmGroup)&&
	   	       !"clRecAllFact".equals(remoteAuditArmGroup)&&
	   	      // !rem/oteAudit.equa/ls("clSelOneFact")&&
	   	       !"onSelColSaveFact".equals(remoteAuditArmGroup)){
	    		  log.info("armGroupManager:evaluteForListFooter!!!");
	   		      evaluteForListFooter=true;
	    	}
	   	 }
	       return evaluteForListFooter;
	   }
   
   public Boolean getEvaluteForBean() {
		
		   	if(evaluteForBean==null){
		   		evaluteForBean=false;
		    	String remoteAuditArmGroup = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("remoteAudit");
			    log.info("armGroupManager:evaluteForBean:remoteAudit:"+remoteAuditArmGroup);
				String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("sessionId");
			    log.info("armGroupManager:evaluteForBean:sessionId:"+sessionId);
		    	if(sessionId!=null && remoteAuditArmGroup!=null &&
		    	   ("rowSelectFact".equals(remoteAuditArmGroup)||	
		    	    "UpdFact".equals(remoteAuditArmGroup))){
		    	      log.info("armGroupManager:evaluteForBean!!!");
		   		      evaluteForBean=true;
		    	}
		   	 }
		     return evaluteForBean;
		   }

   private String decToHex(BigInteger bi) {
		
		String resultArmGroup = null;
		
		try
		{
		 resultArmGroup = bi.toString(16);
		 
		}
		catch (NumberFormatException eArmGroup)
		{
			log.error("Error! tried to parse an invalid number format");
		}
		 return resultArmGroup;
	}
   
}

