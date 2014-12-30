package iac.grn.infosweb.context.app.system.modify;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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


import iac.cud.infosweb.dataitems.AppSystemModifyItem;
import iac.cud.infosweb.dataitems.BaseItem;
import iac.cud.infosweb.entity.AcApplication;
import iac.grn.infosweb.session.table.BaseManager;
import iac.grn.serviceitems.BaseTableItem;
import iac.grn.serviceitems.HeaderTableItem;

@Name("appSystemModifyManager")
 public class AppSystemModifyManager extends BaseManager{

	
	private String rejectReason;
	private String commentText;
	
	private List<HeaderTableItem> headerItemsListContextCREATE;
	
	public void invokeLocal(String type, int firstRow, int numberOfRows,
	           String sessionId) {
		
		 log.info("hostsManager:invokeLocal");
		 try{
			 String orderQueryAppSys=null;
			 log.info("hostsManager:invokeLocal");
			 
			 AppSystemModifyStateHolder appSystemModifyStateHolder = (AppSystemModifyStateHolder)
					  Component.getInstance("appSystemModifyStateHolder",ScopeType.SESSION);
			 Map<String, String> filterMap = appSystemModifyStateHolder.getColumnFilterValues();
			 String st=null;
			  
			 if("list".equals(type)){
				 log.info("AppSys:invokeLocal:list:01");
				 
				 Set<Map.Entry<String, String>> set = appSystemModifyStateHolder.getSortOrders().entrySet();
                 for (Map.Entry<String, String> me : set) {
      		       log.info("me.getKey+:"+me.getKey());
      		       log.info("me.getValue:"+me.getValue());
      		       
      		       if(orderQueryAppSys==null){
      		    	 orderQueryAppSys="order by "+me.getKey()+" "+me.getValue();
      		       }else{
      		    	 orderQueryAppSys=orderQueryAppSys+", "+me.getKey()+" "+me.getValue();  
      		       }
      		     }
                 log.info("invokeLocal:list:orderQueryAppSys:"+orderQueryAppSys);
                 
                 if(filterMap!=null){
    	    		 Set<Map.Entry<String, String>> setFilterAppSys = filterMap.entrySet();
    	              for (Map.Entry<String, String> me : setFilterAppSys) {
    	            	 
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
                 log.info("AppSys:invokeLocal:list:filterQuery:"+st);

             
               List<Object[]> lo=null;
               AppSystemModifyItem ui = null;
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
             "from JOURN_APP_SYSTEM_MODIFY_BSS_T jas, "+
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
                "and ARM.ID_SRV(+)=JAS.UP_IS_APP "+
             ") t1 "+
              (st!=null ? " where "+st :" ")+
              (orderQueryAppSys!=null ? orderQueryAppSys+", t1_id desc " : " order by t1_id desc "))
              .setFirstResult(firstRow)
              .setMaxResults(numberOfRows)
              .getResultList();
               auditList = new ArrayList<BaseItem>();
               
               for(Object[] objectArray :lo){
            	   try{
            	     ui= new AppSystemModifyItem(
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
            		   log.error("invokeLocal:for:error:"+e1);
            	   }
               }  
               
             log.info("AppSys:invokeLocal:list:02");
             
			 } else if("count".equals(type)){
				 log.info("AppSys:count:01");
				 
                 
                 if(filterMap!=null){
    	    		 Set<Map.Entry<String, String>> setFilterAppSys = filterMap.entrySet();
    	              for (Map.Entry<String, String> me : setFilterAppSys) {
    	             	
    	            	  
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
					             "from JOURN_APP_SYSTEM_MODIFY_BSS_T jas, "+
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
					                "and ARM.ID_SRV(+)=JAS.UP_IS_APP "+
					             ") t1 "+
		         (st!=null ? " where "+st :" "))
               .getSingleResult()).longValue();
                 
                 
               log.info("AppSys:invokeLocal:count:02:"+auditCount);
           	 } else if("bean".equals(type)){
				 
			 }
		}catch(Exception e){
			  log.error("AppSys:invokeLocal:error:"+e);
			  evaluteForList=false;
			  FacesMessages.instance().add("Ошибка!");
		}

	}
	
	 private AppSystemModifyItem getUserItem(Long idUser){
		 if(idUser==null){
			  return null;
		   }
		   
		   try{
	           List<Object[]> lo=null;
	           AppSystemModifyItem ui = null;
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
	        		             "from JOURN_APP_SYSTEM_MODIFY_BSS_T jas, "+
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
	        		                "and ARM.ID_SRV(+)=JAS.UP_IS_APP " +
	        		                "and JAS.ID_SRV=? "+
	        		             ") t1 ")
	         .setParameter(1, idUser)
	         .getResultList();
	           
	           for(Object[] objectArray :lo){
	        	   try{
	        		   log.info("AppSystemModifyManager:getUserItem:login:"+objectArray[1].toString());
	        		   
	        		   ui= new AppSystemModifyItem(
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
	        		   log.error("AppSystemModifyManager:getUserItem:for:error:"+e1);
	        	   }
	           }  
		   }catch(Exception e){
			   log.error("AppSystemModifyManager:getUserItem:error:"+e);
		   }
		   return null;
	 }
	 
	 
	 public void createArm(){
		 
		   log.info("AppSystemModifyManager:createArm:01");
		  
		   String sessionIdAppSystemModify = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	       log.info("AppSystemModifyManager:createArm:sessionId:"+sessionIdAppSystemModify);
	     
	       Long idApp=null;
	       Long idArm=null;
	       
		   try{
			   
			   idApp =  new Long(sessionIdAppSystemModify); 
			   
			   Object[] app=(Object[]) entityManager.createNativeQuery(
			    			  "select JAS.UP_IS_APP, JAS.FULL_NAME, JAS.SHORT_NAME, JAS.DESCRIPTION "+
	                          "from JOURN_APP_SYSTEM_MODIFY_BSS_T jas "+
	                          "where  JAS.ID_SRV=? ")
			    			.setParameter(1, idApp)
			    			.getSingleResult();  
				 
			   idArm=new Long(app[0].toString());
			 
			   AcApplication aam = entityManager.find(AcApplication.class, idArm);
				  
			   if(app[1]!=null){	
			      aam.setName(app[1].toString().trim());
			   }
			   if(app[2]!=null){	
				 
			   }
			   if(app[3]!=null){	
				    aam.setDescription(app[3].toString().trim());
			   }
				  
			   
			   
				  
				entityManager.flush();
		     
		        entityManager.createNativeQuery(
	 	     		   "update JOURN_APP_SYSTEM_MODIFY_BSS_T t1 " +
	 	     		   "set t1.STATUS=1, t1.UP_USER_EXEC=? " +
	 	     		   "where t1.ID_SRV=? ")
	 	     		 .setParameter(1, getCurrentUser().getBaseId())
	 	     		 .setParameter(2, new Long(sessionIdAppSystemModify))
	         	 	 .executeUpdate();
		    
		     AppSystemModifyItem ui = getUserItem(new Long(sessionIdAppSystemModify));
		     
		     Contexts.getEventContext().set("contextBeanView", ui);
		     
		   }catch(Exception e){
			   log.error("AppSystemModifyManager:createArm:error:"+e);
		   }
	 }
	 
	 public void reject(){
		   log.info("AppSystemModifyManager:reject:01");
		  
		   String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	       log.info("AppSystemModifyManager:reject:sessionId:"+sessionId);
	     
		   try{
			   
		     entityManager.createNativeQuery(
	 	     		   "update JOURN_APP_SYSTEM_MODIFY_BSS_T t1 " +
	 	     		   "set t1.STATUS=2,  t1.REJECT_REASON=?, t1.UP_USER_EXEC=? " +
	 	     		   "where t1.ID_SRV=? ")
	 	     		 .setParameter(1, this.rejectReason)
	 	     		 .setParameter(2, getCurrentUser().getBaseId())
	 	     		 .setParameter(3, new Long(sessionId))
	 	     	 	 .executeUpdate();
		     
             AppSystemModifyItem ui = getUserItem(new Long(sessionId)); 
		     
		     Contexts.getEventContext().set("contextBeanView", ui);
		     
		   }catch(Exception e){
			   log.error("AppSystemModifyManager:reject:error:"+e);
		   }
	 }
	 
	 public void comment(){
		   log.info("AppSystemModifyManager:comment:01");
		  
		   String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	       log.info("AppSystemModifyManager:comment:sessionId:"+sessionId);
	     
		   try{
			   
		     entityManager.createNativeQuery(
	 	     		   "update JOURN_APP_SYSTEM_MODIFY_BSS_T t1 " +
	 	     		   "set t1.COMMENT_=? " +
	 	     		   "where t1.ID_SRV=? ")
	 	     		 .setParameter(1, this.commentText)
	 	     		 .setParameter(2, new Long(sessionId))
	 	     	 	 .executeUpdate();
		     
           AppSystemModifyItem ui = getUserItem(new Long(sessionId)); 
		     
		   Contexts.getEventContext().set("contextBeanView", ui);
		     
		   }catch(Exception e){
			   log.error("AppSystemModifyManager:reject:error:"+e);
		   }
	 }
	 
	 public void forViewCrt() {
		   try{
			 String sessionId = FacesContext.getCurrentInstance().getExternalContext()
				        .getRequestParameterMap()
				        .get("sessionId");
		     log.info("AppSystemModifyManager:forViewCrt:sessionId:"+sessionId);
		     
		     AppSystemModifyItem ui = getUserItem(new Long(sessionId));
	    		
	    	 Contexts.getEventContext().set("contextBeanView", ui);
		     
		   }catch(Exception e){
			 log.error("AppSystemModifyManager:forViewCrt:Error:"+e);
		   }
	   } 
	 
	 public void forViewUpdDel() {
		 try{
		     String sessionId = FacesContext.getCurrentInstance().getExternalContext()
				        .getRequestParameterMap()
				        .get("sessionId");
		     log.info("AppSystemModifyManager:forViewUpdDel:sessionId:"+sessionId);
		     if(sessionId!=null){
		    	
		     	 
		    	 AppSystemModifyItem ui = getUserItem(new Long(sessionId));
		        	 
		   	 Contexts.getEventContext().set("appSystemModifyBean", ui);
		     }
		   }catch(Exception e){
			   log.error("AppSystemModifyManager:forViewUpdDel:Error:"+e);
		   }
	 }
	 
	 public void forViewComment() {
		   
		   try{
			     String sessionId = FacesContext.getCurrentInstance().getExternalContext()
					        .getRequestParameterMap()
					        .get("sessionId");
			     log.info("AppSystemModifyManager:forViewComment:sessionId:"+sessionId);
			     if(sessionId!=null){
			    	
			     	 
			    	 AppSystemModifyItem ui = getUserItem(new Long(sessionId));
			    	 
			    	 this.commentText=ui.getComment();

	     }
			}catch(Exception e){
				   log.error("AppSystemModifyManager:forViewComment:Error:"+e);
			}
	   } 
	 
	 public String getRejectReason(){
		 return this.rejectReason;
	 }
	 public void setRejectReason(String rejectReason){
		 this.rejectReason=rejectReason;
	 }
	 
	 public String getCommentText(){
		 return this.commentText;
	 }
	 public void setCommentText(String commentText){
		 this.commentText=commentText;
	 }
	 
	 public List <BaseTableItem> getAuditItemsListSelect() {
		   log.info("getAuditItemsListSelect:01");
		   AppSystemModifyContext ac= new AppSystemModifyContext();
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
	   log.info("AppSystemModifyManager:getAuditItemsListContext");
	   if(auditItemsListContext==null){
		   AppSystemModifyContext ac= new AppSystemModifyContext();
		  
		   
		   
		   auditItemsListContext=ac.getAuditItemsCollection();
		   
	   }
	   return this.auditItemsListContext;
  }
  
  public List<HeaderTableItem> getHeaderItemsListContext() {
	  
	  if(headerItemsListContext==null){
		   AppSystemModifyContext ac= new AppSystemModifyContext();
		   headerItemsListContext=ac.getHeaderItemsList();
		   
	
		   
	   }
	  
	   return this.headerItemsListContext;
  }
  
  
  public List<HeaderTableItem> getHeaderItemsListContext(String ids) {
	  
	 	AppSystemModifyContext ac= new AppSystemModifyContext();
		
	 	if(ids!=null) {
	 		
	 	
	 		headerItemsListContext=new ArrayList<HeaderTableItem>();
	 				
	 	    
	 	
	 	     List<String> idsList =  Arrays.asList(ids.split(","));
	 	   
	    	for(HeaderTableItem hti :ac.getHeaderItemsList()){
			
			 
			
			  if(idsList.contains(hti.getItemField())){
				  headerItemsListContext.add(hti);
			  }
			  
		   }

	 	}
	   return this.headerItemsListContext;
 }
  
  public List<HeaderTableItem> getHeaderItemsListContextCREATE(String ids) {
	  
	 	AppSystemModifyContext ac= new AppSystemModifyContext();
		
	 	if(ids!=null) {
	 		
	 	
	 		headerItemsListContextCREATE=new ArrayList<HeaderTableItem>();
	 				
	 	    
	 	
	 	     List<String> idsList =  Arrays.asList(ids.split(","));
	 	   
	    	for(HeaderTableItem hti :ac.getHeaderItemsList()){
			
			 
			
			  if(idsList.contains(hti.getItemField())){
				  headerItemsListContextCREATE.add(hti);
			  }
			  
		   }

	 	}
	   return this.headerItemsListContextCREATE;
}
}
