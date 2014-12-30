package iac.grn.infosweb.context.app.system;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;






import iac.cud.infosweb.dataitems.AppSystemItem;
import iac.cud.infosweb.dataitems.BaseItem;
import iac.cud.infosweb.entity.AcApplication;
import iac.grn.infosweb.context.mc.arm.ArmManager;
import iac.grn.infosweb.session.audit.actions.ActionsMap;
import iac.grn.infosweb.session.audit.actions.ResourcesMap;
import iac.grn.infosweb.session.table.BaseManager;
import iac.grn.serviceitems.BaseTableItem;
import iac.grn.serviceitems.HeaderTableItem;

@Name("appSystemManager")
 public class AppSystemManager extends BaseManager{

	
	private String rejectReason;
	private String commentText;
	
	public void invokeLocal(String type, int firstRow, int numberOfRows,
	           String sessionId) {
		
		 log.info("AppSystem:invokeLocal");
		 try{
			 String orderQueryAppSystem=null;
			 log.info("hostsManager:invokeLocal");
			 
			 AppSystemStateHolder appSystemStateHolder = (AppSystemStateHolder)
					  Component.getInstance("appSystemStateHolder",ScopeType.SESSION);
			 Map<String, String> filterMapAppSys = appSystemStateHolder.getColumnFilterValues();
			 String st=null;
			  
			 if("list".equals(type)){
				 log.info("AppSystem:invokeLocal:list:01");
				 
				 Set<Map.Entry<String, String>> set = appSystemStateHolder.getSortOrders().entrySet();
                 for (Map.Entry<String, String> me : set) {
      		       
      		       if(orderQueryAppSystem==null){
      		    	 orderQueryAppSystem="order by "+me.getKey()+" "+me.getValue();
      		       }else{
      		    	 orderQueryAppSystem=orderQueryAppSystem+", "+me.getKey()+" "+me.getValue();  
      		       }
      		     }
                 log.info("invokeLocal:list:orderQueryAppSystem:"+orderQueryAppSystem);
                 
                 if(filterMapAppSys!=null){
    	    		 Set<Map.Entry<String, String>> setFilterAppSystem = filterMapAppSys.entrySet();
    	              for (Map.Entry<String, String> me : setFilterAppSystem) {
    	              
    	   		     if("t1_crt_date".equals(me.getKey())){  
    	        	   
    	        	   //делаем фильтр на начало  
    	        	     st=(st!=null?st+" and " :"")+" lower(to_char("+me.getKey()+",'DD.MM.YY HH24:MI:SS')) like lower('"+me.getValue()+"%') ";
    	    	   
    	   		     }else if("t1_iogv_bind_type".equals(me.getKey())&&(me.getValue()!=null && "-2".equals(me.getValue()))){
    	    	    	 
    	    	    	 st=(st!=null?st+" and " :"")+" t1_usr_code is null ";
    	    	    	 
    	    	     }else{
    	        		//делаем фильтр на начало
    	            	  st=(st!=null?st+" and " :"")+" lower("+me.getKey()+") like lower('"+me.getValue()+"%') ";
    	        	  }
    	              }
    	    	   }
                 log.info("AppSystem:invokeLocal:list:filterQuery:"+st);

             
               List<Object[]> loAppSys=null;
               AppSystemItem ui = null;
               DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm:ss");
               

             loAppSys=entityManager.createNativeQuery(
             "select t1.t1_id, t1.t1_created, t1.t1_full_name, t1.t1_short_name, t1.t1_description, "+
             "t1.t1_status, t1_org_name,  t1_user_fio, t1_reject_reason, " +
             "t1_arm_id, t1_arm_code, t1_arm_name, t1_arm_description, t1_comment "+
              "from( "+ 
             "select JAS.ID_SRV t1_id, JAS.CREATED t1_created, JAS.FULL_NAME t1_full_name, "+ 
             "JAS.SHORT_NAME t1_short_name, JAS.DESCRIPTION t1_description, "+ 
             "JAS.STATUS t1_status,  CL_ORG_FULL.FULL_ t1_org_name, "+
              "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO ) t1_user_fio, " +
              "JAS.REJECT_REASON t1_reject_reason, " +
              "ARM.ID_SRV t1_arm_id, ARM.SIGN_OBJECT t1_arm_code, ARM.FULL_ t1_arm_name, ARM.DESCRIPTION  t1_arm_description, " +
              "JAS.COMMENT_ t1_comment "+
             "from JOURN_APP_SYSTEM_BSS_T jas, "+
               "AC_USERS_KNL_T au_FULL, "+  
                "ISP_BSS_T cl_org_full, "+
                 "ISP_BSS_T cl_usr_full, " +
                 "AC_IS_BSS_T arm, "+
              "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE "+ 
                "from ISP_BSS_T cl_org "+
                "where  CL_ORG.SIGN_OBJECT LIKE '%00000' "+
                "group by CL_ORG.SIGN_OBJECT) t03, "+
                 "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "+ 
                            "from ISP_BSS_T cl_usr "+
                            "where CL_USR.FIO is not null "+
                            "group by CL_usr.SIGN_OBJECT) t02 "+  
                "where JAS.UP_USER=AU_FULL.ID_SRV "+
                "and AU_FULL.UP_SIGN=t03.CL_ORG_CODE "+
                "and CL_ORG_FULL.ID_SRV=t03.CL_ORG_ID "+
                "and AU_FULL.UP_SIGN_USER=t02.CL_USR_CODE(+) "+
                "and CL_USR_FULL.ID_SRV(+)=t02.CL_USR_ID " +
                "and ARM.ID_SRV(+)=JAS.UP_IS "+
             ") t1 "+
              (st!=null ? " where "+st :" ")+
              (orderQueryAppSystem!=null ? orderQueryAppSystem+", t1_id desc " : " order by t1_id desc "))
              .setFirstResult(firstRow)
              .setMaxResults(numberOfRows)
              .getResultList();
               auditList = new ArrayList<BaseItem>();
               
               for(Object[] objectArray :loAppSys){
            	   try{
            	     ui= new AppSystemItem(
            			  objectArray[0]!=null?new Long(objectArray[0].toString()):null,
            			  objectArray[1]!=null?df.format((Date)objectArray[1]) :"",
            			  objectArray[5]!=null?Integer.parseInt(objectArray[5].toString()):0,
            			  objectArray[2]!=null?objectArray[2].toString():"",
            			  objectArray[3]!=null?objectArray[3].toString():"",
            			  objectArray[4]!=null?objectArray[4].toString():"",
            			  objectArray[6]!=null?objectArray[6].toString():"",
            			  objectArray[7]!=null?objectArray[7].toString():"",
            			  objectArray[8]!=null?objectArray[8].toString():"",
            			  objectArray[9]!=null?new Long(objectArray[9].toString()):null,
            			  objectArray[10]!=null?objectArray[10].toString():"",
            			  objectArray[11]!=null?objectArray[11].toString():"",
            			  objectArray[12]!=null?objectArray[12].toString():"",
            			  objectArray[13]!=null?objectArray[13].toString():"");
            	     auditList.add(ui);
            	   }catch(Exception e1){
            		   log.error("AppSystem:invokeLocal:for:error:"+e1);
            	   }
               }  
               
             log.info("AppSystem:invokeLocal:list:02");
             
			 } else if("count".equals(type)){
				 log.info("AppSystem:count:01");
				 
                 
                 if(filterMapAppSys!=null){
    	    		 Set<Map.Entry<String, String>> setFilterAppSystem = filterMapAppSys.entrySet();
    	              for (Map.Entry<String, String> me : setFilterAppSystem) {
    	            	 
    	            	  
    	              if("t1_iogv_bind_type".equals(me.getKey())&&(me.getValue()!=null && "-2".equals(me.getValue()))){
     	    	    	 st=(st!=null?st+" and " :"")+" t1_usr_code is null ";
    	              }else{
    	            	 st=(st!=null?st+" and " :"")+" lower("+me.getKey()+") like lower('"+me.getValue()+"%') ";
    	              }	 
     	    	 
    	            	  
    	              }
    	    	   }
				 auditCount = ((java.math.BigDecimal)entityManager.createNativeQuery(
						       "select count(*) " +
								"from( "+ 
					             "select JAS.ID_SRV t1_id, JAS.CREATED t1_created, JAS.FULL_NAME t1_full_name, "+ 
					             "JAS.SHORT_NAME t1_short_name, JAS.DESCRIPTION t1_description, "+ 
					             "JAS.STATUS t1_status,  CL_ORG_FULL.FULL_ t1_org_name, "+
					              "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO ) t1_user_fio, " +
					              "JAS.REJECT_REASON t1_reject_reason, " +
					              "ARM.ID_SRV t1_arm_id, ARM.SIGN_OBJECT t1_arm_code, ARM.FULL_ t1_arm_name, ARM.DESCRIPTION  t1_arm_description, " +
					              "JAS.COMMENT_ t1_comment "+
					             "from JOURN_APP_SYSTEM_BSS_T jas, "+
					               "AC_USERS_KNL_T au_FULL, "+  
					                "ISP_BSS_T cl_org_full, "+
					                 "ISP_BSS_T cl_usr_full, " +
					                 "AC_IS_BSS_T arm, "+
					              "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE "+ 
					                "from ISP_BSS_T cl_org "+
					                "where  CL_ORG.SIGN_OBJECT LIKE '%00000' "+
					                "group by CL_ORG.SIGN_OBJECT) t03, "+
					                 "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "+ 
					                            "from ISP_BSS_T cl_usr "+
					                            "where CL_USR.FIO is not null "+
					                            "group by CL_usr.SIGN_OBJECT) t02 "+  
					                "where JAS.UP_USER=AU_FULL.ID_SRV "+
					                "and AU_FULL.UP_SIGN=t03.CL_ORG_CODE "+
					                "and CL_ORG_FULL.ID_SRV=t03.CL_ORG_ID "+
					                "and AU_FULL.UP_SIGN_USER=t02.CL_USR_CODE(+) "+
					                "and CL_USR_FULL.ID_SRV(+)=t02.CL_USR_ID " +
					                "and ARM.ID_SRV(+)=JAS.UP_IS "+
					             ") t1 "+
		         (st!=null ? " where "+st :" "))
               .getSingleResult()).longValue();
                 
                 
               log.info("AppSystem:invokeLocal:count:02:"+auditCount);
           	 } 
		}catch(Exception eAppSys){
			  log.error("AppSystem:invokeLocal:error:"+eAppSys);
			  evaluteForList=false;
			  FacesMessages.instance().add("Ошибка!");
		}

	}
	
	 private AppSystemItem getUserItem(Long idUser){
		 if(idUser==null){
			  return null;
		   }
		   
		   try{
	           List<Object[]> lo=null;
	           AppSystemItem ui = null;
	           DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm:ss");
	           
	           lo=entityManager.createNativeQuery(
	        		   "select t1.t1_id, t1.t1_created, t1.t1_full_name, t1.t1_short_name, t1.t1_description, "+
	        		             "t1.t1_status, t1_org_name,  t1_user_fio, t1_reject_reason, " +
	        		             "t1_arm_id, t1_arm_code, t1_arm_name, t1_arm_description, t1_comment "+
	        		              "from( "+ 
	        		             "select JAS.ID_SRV t1_id, JAS.CREATED t1_created, JAS.FULL_NAME t1_full_name, "+ 
	        		             "JAS.SHORT_NAME t1_short_name, JAS.DESCRIPTION t1_description, "+ 
	        		             "JAS.STATUS t1_status,  CL_ORG_FULL.FULL_ t1_org_name, "+
	        		              "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO ) t1_user_fio, " +
	        		              "JAS.REJECT_REASON t1_reject_reason, " +
	        		              "ARM.ID_SRV t1_arm_id, ARM.SIGN_OBJECT t1_arm_code, ARM.FULL_ t1_arm_name, ARM.DESCRIPTION  t1_arm_description, " +
	        		              "JAS.COMMENT_ t1_comment "+
	        		             "from JOURN_APP_SYSTEM_BSS_T jas, "+
	        		               "AC_USERS_KNL_T au_FULL, "+  
	        		               "ISP_BSS_T cl_org_full, "+
	        		               "ISP_BSS_T cl_usr_full, " +
	        		               "AC_IS_BSS_T arm, "+
	        		              "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE "+ 
	        		                "from ISP_BSS_T cl_org "+
	        		                "where  CL_ORG.SIGN_OBJECT LIKE '%00000' "+
	        		                "group by CL_ORG.SIGN_OBJECT) t03, "+
	        		                 "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "+ 
	        		                            "from ISP_BSS_T cl_usr "+
	        		                            "where CL_USR.FIO is not null "+
	        		                            "group by CL_usr.SIGN_OBJECT) t02 "+  
	        		                "where JAS.UP_USER=AU_FULL.ID_SRV "+
	        		                "and AU_FULL.UP_SIGN=t03.CL_ORG_CODE "+
	        		                "and CL_ORG_FULL.ID_SRV=t03.CL_ORG_ID "+
	        		                "and AU_FULL.UP_SIGN_USER=t02.CL_USR_CODE(+) "+
	        		                "and CL_USR_FULL.ID_SRV(+)=t02.CL_USR_ID " +
	        		                "and ARM.ID_SRV(+)=JAS.UP_IS " +
	        		                "and JAS.ID_SRV=? "+
	        		             ") t1 ")
	         .setParameter(1, idUser)
	         .getResultList();
	           
	           for(Object[] objectArray :lo){
	        	   try{
	        		   log.info("AppSystemManager:getUserItem:login:"+objectArray[1].toString());
	        		   
	        		   ui= new AppSystemItem(
	            			  objectArray[0]!=null?new Long(objectArray[0].toString()):null,
	            			  objectArray[1]!=null?df.format((Date)objectArray[1]) :"",
	            			  objectArray[5]!=null?Integer.parseInt(objectArray[5].toString()):0,
	            			  objectArray[2]!=null?objectArray[2].toString():"",
	            			  objectArray[3]!=null?objectArray[3].toString():"",
	            			  objectArray[4]!=null?objectArray[4].toString():"",
	            			  objectArray[6]!=null?objectArray[6].toString():"",
	            			  objectArray[7]!=null?objectArray[7].toString():"",
	            			  objectArray[8]!=null?objectArray[8].toString():"",
	            			  objectArray[9]!=null?new Long(objectArray[9].toString()):null,
	            			  objectArray[10]!=null?objectArray[10].toString():"",
	            			  objectArray[11]!=null?objectArray[11].toString():"",
	            			  objectArray[12]!=null?objectArray[12].toString():"",
	            			  objectArray[13]!=null?objectArray[13].toString():"");
	        	     return ui;
	        	   }catch(Exception e1){
	        		   log.error("AppSystemManager:getUserItem:for:error:"+e1);
	        	   }
	           }  
		   }catch(Exception e){
			   log.error("AppSystemManager:getUserItem:error:"+e);
		   }
		   return null;
	 }
	 
	 
	 public void createArm(){
		 
		   log.info("AppSystemManager:createArm:01");
		  
		   String sessionIdAppSystem = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	       log.info("AppSystemManager:createArm:sessionIdAppSystem:"+sessionIdAppSystem);
	     
		   try{
			   
			
			 
			 
			 
		     ArmManager armManager = (ArmManager)
			          Component.getInstance("armManager", ScopeType.EVENT);
		   
		     armManager.addArm();
		   
		     AcApplication armBeanCrt = (AcApplication)
					  Component.getInstance("armBeanCrt", ScopeType.CONVERSATION);   
		     
		     entityManager.createNativeQuery(
	 	     		   "update JOURN_APP_SYSTEM_BSS_T t1 " +
	 	     		   "set t1.UP_IS=?, t1.STATUS=1, t1.UP_USER_EXEC=? " +
	 	     		   "where t1.ID_SRV=? ")
	 	     		 .setParameter(1, armBeanCrt.getBaseId())
	 	     		 .setParameter(2, getCurrentUser().getBaseId())
	 	     		 .setParameter(3, new Long(sessionIdAppSystem))
	         	 	 .executeUpdate();
		    
		     AppSystemItem ui = getUserItem(new Long(sessionIdAppSystem));
		     
		     Contexts.getEventContext().set("contextBeanView", ui);
		     
		     audit(ResourcesMap.APP_SYS, ActionsMap.EXECUTE);
		     
		   }catch(Exception e){
			   log.error("AppSystemManager:createArm:error:"+e);
		   }
	 }
	 
	 public void reject(){
		   log.info("AppSystemManager:reject:01");
		  
		   String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	       log.info("AppSystemManager:reject:sessionId:"+sessionId);
	     
		   try{
			   
		     entityManager.createNativeQuery(
	 	     		   "update JOURN_APP_SYSTEM_BSS_T t1 " +
	 	     		   "set t1.STATUS=2,  t1.REJECT_REASON=?, t1.UP_USER_EXEC=? " +
	 	     		   "where t1.ID_SRV=? ")
	 	     		 .setParameter(1, this.rejectReason)
	 	     		 .setParameter(2, getCurrentUser().getBaseId())
	 	     		 .setParameter(3, new Long(sessionId))
	 	     	 	 .executeUpdate();
		     
             AppSystemItem ui = getUserItem(new Long(sessionId)); 
		     
		     Contexts.getEventContext().set("contextBeanView", ui);
		     
		     audit(ResourcesMap.APP_SYS, ActionsMap.REJECT);
		     
		   }catch(Exception e){
			   log.error("AppSystemManager:reject:error:"+e);
		   }
	 }
	 
	 public void comment(){
		   log.info("AppSystemManager:comment:01");
		  
		   String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	       log.info("AppSystemManager:comment:sessionId:"+sessionId);
	     
		   try{
			   
		     entityManager.createNativeQuery(
	 	     		   "update JOURN_APP_SYSTEM_BSS_T t1 " +
	 	     		   "set t1.COMMENT_=? " +
	 	     		   "where t1.ID_SRV=? ")
	 	     		 .setParameter(1, this.commentText)
	 	     		 .setParameter(2, new Long(sessionId))
	 	     	 	 .executeUpdate();
		     
           AppSystemItem ui = getUserItem(new Long(sessionId)); 
		     
		   Contexts.getEventContext().set("contextBeanView", ui);
		     
		   }catch(Exception e){
			   log.error("AppSystemManager:reject:error:"+e);
		   }
	 }
	 
	 public void forViewCrt() {
		   try{
			 String sessionId = FacesContext.getCurrentInstance().getExternalContext()
				        .getRequestParameterMap()
				        .get("sessionId");
		     log.info("AppSystemManager:forViewCrt:sessionId:"+sessionId);
		     
		     AppSystemItem ui = getUserItem(new Long(sessionId));
	    		
	    	 AcApplication ao = new AcApplication();
	    		
	    	 ao.setName(ui.getFullName());
	    	 ao.setDescription(ui.getDescription());
	    	 
		     Contexts.getEventContext().set("armBeanCrt", ao);
		     
		   }catch(Exception e){
			 log.error("AppSystemManager:forViewCrt:Error:"+e);
		   }
	   } 
	 
	 public void forViewUpdDel() {
		 try{
		     String sessionId = FacesContext.getCurrentInstance().getExternalContext()
				        .getRequestParameterMap()
				        .get("sessionId");
		     log.info("AppSystemManager:forViewUpdDel:sessionId:"+sessionId);
		     if(sessionId!=null){
		    	
		     	 
		    	 AppSystemItem ui = getUserItem(new Long(sessionId));
		        	 
		   	 Contexts.getEventContext().set("appSystemBean", ui);
		     }
		   }catch(Exception e){
			   log.error("AppSystemManager:forViewUpdDel:Error:"+e);
		   }
	 }
	 
	 public void forViewComment() {
		   
		   try{
			     String sessionId = FacesContext.getCurrentInstance().getExternalContext()
					        .getRequestParameterMap()
					        .get("sessionId");
			     log.info("AppSystemManager:forViewComment:sessionId:"+sessionId);
			     if(sessionId!=null){
			    	
			     	 
			    	 AppSystemItem ui = getUserItem(new Long(sessionId));
			    	 
			    	 this.commentText=ui.getComment();

	     }
			}catch(Exception e){
				   log.error("AppSystemManager:forViewComment:Error:"+e);
			}
	   } 
	 
	 
	 public String getCommentText(){
		 return this.commentText;
	 }
	 public void setCommentText(String commentText){
		 this.commentText=commentText;
	 }
	 
	 public String getRejectReason(){
		 return this.rejectReason;
	 }
	 public void setRejectReason(String rejectReason){
		 this.rejectReason=rejectReason;
	 }
	
	 public List <BaseTableItem> getAuditItemsListSelect() {
		   log.info("getAuditItemsListSelect:01");
		   AppSystemContext ac= new AppSystemContext();
		   if( auditItemsListSelect==null){
			   log.info("getAuditItemsListSelect:02");
			   auditItemsListSelect = new ArrayList<BaseTableItem>();
			   
			   auditItemsListSelect.add(ac.getAuditItemsMap().get("idApp"));
			   auditItemsListSelect.add(ac.getAuditItemsMap().get("created"));
			   auditItemsListSelect.add(ac.getAuditItemsMap().get("orgName"));
			  
			   auditItemsListSelect.add(ac.getAuditItemsMap().get("statusValue"));
		   }
	       return this.auditItemsListSelect;
  }
  

  
  public List <BaseTableItem> getAuditItemsListContext() {
	   log.info("AppSystemManager:getAuditItemsListContext");
	   if(auditItemsListContext==null){
		   AppSystemContext ac= new AppSystemContext();
		  
		   
		   
		   auditItemsListContext=ac.getAuditItemsCollection();
		   
	   }
	   return this.auditItemsListContext;
  }
  
  public List<HeaderTableItem> getHeaderItemsListContext() {
	  
	  if(headerItemsListContext==null){
		   AppSystemContext ac= new AppSystemContext();
		   headerItemsListContext=ac.getHeaderItemsList();
		   
	
		   
	   }
	  
	   return this.headerItemsListContext;
  }
}
