package iac.grn.infosweb.context.appmy.user.modify;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.Set;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;

import iac.cud.infosweb.dataitems.AppUserModifyItem;
import iac.cud.infosweb.dataitems.BaseItem;
import iac.grn.infosweb.session.table.BaseManager;
import iac.grn.serviceitems.BaseTableItem;
import iac.grn.serviceitems.HeaderTableItem;

@Name("appMyUserModifyManager")
 public class AppMyUserModifyManager extends BaseManager{

	
	private String rejectReason;
	private String commentText;
	
	private List<HeaderTableItem> headerItemsListContextCREATE;
	
	public void invokeLocal(String type, int firstRow, int numberOfRows,
	           String sessionId) {
		
		 log.info("AppMyUserModManager:invokeLocal");
		 try{
			 String orderQuery=null;
			 log.info("hostsManager:invokeLocal");
			 
			 AppMyUserModifyStateHolder appMyUserModifyStateHolder = (AppMyUserModifyStateHolder)
					  Component.getInstance("appMyUserModifyStateHolder",ScopeType.SESSION);
			 Map<String, String> filterMapMyUserMod = appMyUserModifyStateHolder.getColumnFilterValues();
			 String st=null;
			  
			 if("list".equals(type)){
				 log.info("AppMyUserMod:invokeLocal:list:01");
				 
				 Set<Map.Entry<String, String>> set = appMyUserModifyStateHolder.getSortOrders().entrySet();
                 for (Map.Entry<String, String> me : set) {
      		       log.info("me.getKey+:"+me.getKey());
      		       log.info("me.getValue:"+me.getValue());
      		       
      		       if(orderQuery==null){
      		    	 orderQuery="order by "+me.getKey()+" "+me.getValue();
      		       }else{
      		    	 orderQuery=orderQuery+", "+me.getKey()+" "+me.getValue();  
      		       }
      		     }
                 log.info("AppMyUserMod:invokeLocal:list:orderQuery:"+orderQuery);
                 
                 if(filterMapMyUserMod!=null){
    	    		 Set<Map.Entry<String, String>> setFilterAppMyUserMod = filterMapMyUserMod.entrySet();
    	              for (Map.Entry<String, String> me : setFilterAppMyUserMod) {
    	               
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
                 log.info("AppMyUserMod:invokeLocal:list:filterQuery:"+st);

             
               List<Object[]> loMyUserMod=null;
               AppUserModifyItem ui = null;
               DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm:ss");
               

             loMyUserMod=entityManager.createNativeQuery(
             "select t1.t1_id, t1.t1_created, "+
             "t1.t1_status, t1_org_name,  t1_user_fio, t1_reject_reason, t1_comment, "+
           
             "t1_SURNAME_USER, "+
                  "t1_NAME_USER , "+
                  "t1_PATRONYMIC_USER , "+
                  "t1_SIGN_USER,  "+
                  "t1_POSITION_USER, "+
                  "t1_EMAIL_USER,  "+
                  "t1_PHONE_USER, "+
                  "t1_CERTIFICATE_USER,  "+
                  "t1_NAME_DEPARTAMENT,  "+

             "t1_org_name_app, t1_user_id_app,  t1_user_login_app, t1_user_fio_app, t1_user_pos_app, "+
             "t1_dep_name_app, " +
             "t1_cert_app,  t1_usr_code_app, t1_user_tel_app,  t1_user_email_app "+
              "from(  "+
             "select JAS.ID_SRV t1_id, JAS.CREATED t1_created,   "+
             "JAS.STATUS t1_status,  CL_ORG_FULL.FULL_ t1_org_name, "+
             "JAS.COMMENT_ t1_comment, "+
              "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO ) t1_user_fio, "+
              "JAS.REJECT_REASON t1_reject_reason,  "+
      
              "JAS.SURNAME_USER t1_SURNAME_USER, "+
                  "JAS.NAME_USER t1_NAME_USER , "+
                  "JAS.PATRONYMIC_USER t1_PATRONYMIC_USER , "+
                  "JAS.SIGN_USER t1_SIGN_USER,  "+
                  "JAS.POSITION_USER t1_POSITION_USER, "+
                  "JAS.EMAIL_USER t1_EMAIL_USER,  "+
                  "JAS.PHONE_USER t1_PHONE_USER, "+
                  "JAS.CERTIFICATE_USER t1_CERTIFICATE_USER,  "+
                  "JAS.NAME_DEPARTAMENT t1_NAME_DEPARTAMENT,  "+
          
              "AU_APP.ID_SRV  t1_user_id_app, AU_APP.LOGIN  t1_user_login_app, "+
               "CL_ORG_app.FULL_ t1_org_name_app,  decode(AU_app.UP_SIGN_USER, null, AU_app.SURNAME||' '||AU_app.NAME_ ||' '|| AU_app.PATRONYMIC,  CL_USR_app.FIO ) t1_user_fio_app, "+
                 "decode(AU_app.UP_SIGN_USER, null, AU_app.POSITION, CL_USR_app.POSITION) t1_user_pos_app, "+
                 
                 "decode(AU_app.UP_SIGN_USER, null, AU_app.DEPARTMENT, decode(substr(CL_DEP_app.sign_object,4,2), '00', null, CL_DEP_app.FULL_)) t1_dep_name_app, " +
                 "AU_app.CERTIFICATE t1_cert_app, AU_app.UP_SIGN_user t1_usr_code_app, "+   
                 "decode(AU_app.UP_SIGN_USER, null, AU_app.PHONE, CL_USR_app.PHONE ) t1_user_tel_app, "+   
                 "decode(AU_app.UP_SIGN_USER, null, AU_app.E_MAIL, CL_USR_app.EMAIL) t1_user_email_app "+
            "from JOURN_APP_USER_MODIFY_BSS_T jas, "+
               "AC_USERS_KNL_T au_FULL,   "+
                "ISP_BSS_T cl_org_full, "+
                 "ISP_BSS_T cl_usr_full, "+
                 "ISP_BSS_T cl_org_app, "+
                 "ISP_BSS_T cl_usr_app, "+
                 "ISP_BSS_T cl_dep_app, "+
                "AC_USERS_KNL_T au_APP, "+
              "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE  "+
                "from ISP_BSS_T cl_org "+
                "where  CL_ORG.SIGN_OBJECT LIKE '%00000' "+
                "group by CL_ORG.SIGN_OBJECT) t03, "+
                 "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE  "+
                            "from ISP_BSS_T cl_usr "+
                            "where CL_USR.FIO is not null "+
                            "group by CL_usr.SIGN_OBJECT) t02,   "+
                
                 "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE  "+
                "from ISP_BSS_T cl_org "+
                "where  CL_ORG.SIGN_OBJECT LIKE '%00000' "+
                "group by CL_ORG.SIGN_OBJECT) t03_app, "+
                 "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE  "+
                            "from ISP_BSS_T cl_usr "+
                            "where CL_USR.FIO is not null "+
                            "group by CL_usr.SIGN_OBJECT) t02_app,  "+ 
               "(select max(CL_dep.ID_SRV) CL_DEP_ID,  CL_DEP.SIGN_OBJECT  CL_DEP_CODE  "+
                            "from ISP_BSS_T cl_dep "+
                            "where CL_dep.SIGN_OBJECT LIKE '%000' "+
                            "group by CL_DEP.SIGN_OBJECT) t04_app "+
                                      
                "where JAS.UP_USER=AU_FULL.ID_SRV "+
                "and AU_FULL.UP_SIGN=t03.CL_ORG_CODE "+
                "and CL_ORG_FULL.ID_SRV=t03.CL_ORG_ID "+
                "and AU_FULL.UP_SIGN_USER=t02.CL_USR_CODE(+) "+
                "and CL_USR_FULL.ID_SRV(+)=t02.CL_USR_ID "+
                "and au_APP.ID_SRV =JAS.UP_USER_APP "+
                
                "and au_APP.UP_SIGN=t03_APP.CL_ORG_CODE "+
                "and CL_ORG_app.ID_SRV=t03_APP.CL_ORG_ID "+
                
                "and AU_APP.UP_SIGN_USER=t02_APP.CL_USR_CODE(+) "+
                "and CL_USR_APP.ID_SRV(+)=t02_APP.CL_USR_ID "+
                
                "and substr(au_APP.UP_SIGN,1,5)||'000'=t04_APP.CL_dep_CODE(+) "+
                "and CL_dep_app.ID_SRV(+)=t04_APP.CL_dep_ID "+
                "and JAS.UP_USER= :idUser "+ 
                
              ") t1 "+
              (st!=null ? " where "+st :" ")+
              (orderQuery!=null ? orderQuery+", t1_id desc " : " order by t1_id desc "))
              .setFirstResult(firstRow)
              .setMaxResults(numberOfRows)
              .setParameter("idUser", getCurrentUser().getBaseId())
              .getResultList();
               auditList = new ArrayList<BaseItem>();
               
               for(Object[] objectArray :loMyUserMod){
            	   try{
            		   log.info("AppMyUserMod:invokeLocal:list:02");
            		   

                	     ui= new AppUserModifyItem(
            	    		objectArray[0]!=null?new Long(objectArray[0].toString()):null,
            				objectArray[1]!=null?df.format((Date)objectArray[1]) :"",
            				objectArray[2]!=null?Integer.parseInt(objectArray[2].toString()):0,	
            				objectArray[3]!=null?objectArray[3].toString():"",
            				objectArray[4]!=null?objectArray[4].toString():"",
            				objectArray[5]!=null?objectArray[5].toString():"",
            				objectArray[6]!=null?objectArray[6].toString():"",
            				 
            				objectArray[7]!=null?objectArray[7].toString():"",
	            			objectArray[8]!=null?objectArray[8].toString():"",
	            			objectArray[9]!=null?objectArray[9].toString():"",
	            			objectArray[10]!=null?objectArray[10].toString():"",
	            			objectArray[11]!=null?objectArray[11].toString():"",
	            			objectArray[12]!=null?objectArray[12].toString():"",
	            			objectArray[13]!=null?objectArray[13].toString():"",
	            			objectArray[14]!=null?objectArray[14].toString():"",
	            			objectArray[15]!=null?objectArray[15].toString():"",
	            			 
	            			objectArray[16]!=null?objectArray[16].toString():"",
	            			objectArray[17]!=null?new Long(objectArray[17].toString()):null,
	            			objectArray[18]!=null?objectArray[18].toString():"",
	            			objectArray[19]!=null?objectArray[19].toString():"",
	            			objectArray[20]!=null?objectArray[20].toString():"",
	            			objectArray[21]!=null?objectArray[21].toString():"",
	            			objectArray[22]!=null?objectArray[22].toString():"",
	            			objectArray[23]!=null?objectArray[23].toString():"",
	            			objectArray[24]!=null?objectArray[24].toString():"",
	            			objectArray[25]!=null?objectArray[25].toString():"");
                	     
            	     auditList.add(ui);
            	   }catch(Exception erMyUserMod){
            		   log.error("AppMyUserMod:invokeLocal:for:error:"+erMyUserMod);
            	   }
               }  
               
             log.info("AppMyUserMod:invokeLocal:list:02");
             
			 } else if("count".equals(type)){
				 log.info("AppMyUserModList:count:01");
				 
                 
                 if(filterMapMyUserMod!=null){
    	    		 Set<Map.Entry<String, String>> setFilterAppMyUserMod = filterMapMyUserMod.entrySet();
    	              for (Map.Entry<String, String> me : setFilterAppMyUserMod) {
    	              	
    	            	  
    	              if("t1_iogv_bind_type".equals(me.getKey())&&(me.getValue()!=null && "-2".equals(me.getValue()))){
     	    	    	 st=(st!=null?st+" and " :"")+" t1_usr_code is null ";
    	              }else{
    	            	 st=(st!=null?st+" and " :"")+" lower("+me.getKey()+") like lower('"+me.getValue()+"%') ";
    	              }	 
     	    	 
    	            	  
    	              }
    	    	   }
				 
				
				 auditCount = ((java.math.BigDecimal)entityManager.createNativeQuery(
						       "select count(*) " +
						    		  "from(  "+
						                "select JAS.ID_SRV t1_id, JAS.CREATED t1_created,   "+
						                "JAS.STATUS t1_status,  CL_ORG_FULL.FULL_ t1_org_name, "+
						                "JAS.COMMENT_ t1_comment, "+
						                 "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO ) t1_user_fio, "+
						                 "JAS.REJECT_REASON t1_reject_reason,  "+
						         
						                 "JAS.SURNAME_USER t1_SURNAME_USER, "+
						                     "JAS.NAME_USER t1_NAME_USER , "+
						                     "JAS.PATRONYMIC_USER t1_PATRONYMIC_USER , "+
						                     "JAS.SIGN_USER t1_SIGN_USER,  "+
						                     "JAS.POSITION_USER t1_POSITION_USER, "+
						                     "JAS.EMAIL_USER t1_EMAIL_USER,  "+
						                     "JAS.PHONE_USER t1_PHONE_USER, "+
						                     "JAS.CERTIFICATE_USER t1_CERTIFICATE_USER,  "+
						                     "JAS.NAME_DEPARTAMENT t1_NAME_DEPARTAMENT,  "+
						             
						                 "AU_APP.ID_SRV  t1_user_id_app, AU_APP.LOGIN  t1_user_login_app, "+
						                  "CL_ORG_app.FULL_ t1_org_name_app,  decode(AU_app.UP_SIGN_USER, null, AU_app.SURNAME||' '||AU_app.NAME_ ||' '|| AU_app.PATRONYMIC,  CL_USR_app.FIO ) t1_user_fio_app, "+
						                    "decode(AU_app.UP_SIGN_USER, null, AU_app.POSITION, CL_USR_app.POSITION) t1_user_pos_app, "+
						                    
						                    "decode(AU_app.UP_SIGN_USER, null, AU_app.DEPARTMENT, decode(substr(CL_DEP_app.sign_object,4,2), '00', null, CL_DEP_app.FULL_)) t1_dep_name_app, "+
						                    "AU_app.CERTIFICATE t1_cert_app, AU_app.UP_SIGN_user t1_usr_code_app, "+   
						                    "decode(AU_app.UP_SIGN_USER, null, AU_app.PHONE, CL_USR_app.PHONE ) t1_user_tel_app, "+   
						                    "decode(AU_app.UP_SIGN_USER, null, AU_app.E_MAIL, CL_USR_app.EMAIL) t1_user_email_app "+
						               "from JOURN_APP_USER_MODIFY_BSS_T jas, "+
						                  "AC_USERS_KNL_T au_FULL,   "+
						                   "ISP_BSS_T cl_org_full, "+
						                    "ISP_BSS_T cl_usr_full, "+
						                    "ISP_BSS_T cl_org_app, "+
						                    "ISP_BSS_T cl_usr_app, "+
						                    "ISP_BSS_T cl_dep_app, "+
						                   "AC_USERS_KNL_T au_APP, "+
						                 "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE  "+
						                   "from ISP_BSS_T cl_org "+
						                   "where  CL_ORG.SIGN_OBJECT LIKE '%00000' "+
						                   "group by CL_ORG.SIGN_OBJECT) t03, "+
						                    "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE  "+
						                               "from ISP_BSS_T cl_usr "+
						                               "where CL_USR.FIO is not null "+
						                               "group by CL_usr.SIGN_OBJECT) t02,   "+
						                   
						                    "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE  "+
						                   "from ISP_BSS_T cl_org "+
						                   "where  CL_ORG.SIGN_OBJECT LIKE '%00000' "+
						                   "group by CL_ORG.SIGN_OBJECT) t03_app, "+
						                    "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE  "+
						                               "from ISP_BSS_T cl_usr "+
						                               "where CL_USR.FIO is not null "+
						                               "group by CL_usr.SIGN_OBJECT) t02_app,  "+ 
						                  "(select max(CL_dep.ID_SRV) CL_DEP_ID,  CL_DEP.SIGN_OBJECT  CL_DEP_CODE  "+
						                               "from ISP_BSS_T cl_dep "+
						                               "where CL_dep.SIGN_OBJECT LIKE '%000' "+
						                               "group by CL_DEP.SIGN_OBJECT) t04_app "+
						                                         
						                   "where JAS.UP_USER=AU_FULL.ID_SRV "+
						                   "and AU_FULL.UP_SIGN=t03.CL_ORG_CODE "+
						                   "and CL_ORG_FULL.ID_SRV=t03.CL_ORG_ID "+
						                   "and AU_FULL.UP_SIGN_USER=t02.CL_USR_CODE(+) "+
						                   "and CL_USR_FULL.ID_SRV(+)=t02.CL_USR_ID "+
						                   "and au_APP.ID_SRV =JAS.UP_USER_APP "+
						                   
						                   "and au_APP.UP_SIGN=t03_APP.CL_ORG_CODE "+
						                   "and CL_ORG_app.ID_SRV=t03_APP.CL_ORG_ID "+
						                   
						                   "and AU_APP.UP_SIGN_USER=t02_APP.CL_USR_CODE(+) "+
						                   "and CL_USR_APP.ID_SRV(+)=t02_APP.CL_USR_ID "+
						                   
						                   "and substr(au_APP.UP_SIGN,1,5)||'000'=t04_APP.CL_dep_CODE(+) "+
						                   "and CL_dep_app.ID_SRV(+)=t04_APP.CL_dep_ID "+
						                   "and JAS.UP_USER= :idUser "+ 
						                   
						                 ") t1 "+
		         (st!=null ? " where "+st :" "))
		       .setParameter("idUser", getCurrentUser().getBaseId())
               .getSingleResult()).longValue();
                 
                 
               log.info("AppMyUserMod:invokeLocal:count:02:"+auditCount);
           	 } 
			 
		}catch(Exception eMyUserMod){
			  log.error("AppMyUserMod:invokeLocal:error:"+eMyUserMod);
			  evaluteForList=false;
			  FacesMessages.instance().add("Ошибка!");
		}

	}
	
	
	
	public void setRejectReason(String rejectReason){
		 this.rejectReason=rejectReason;
	 }
	 public String getRejectReason(){
		 return this.rejectReason;
	 }
	 
	
	 
	 public List <BaseTableItem> getAuditItemsListSelect() {
		   log.info("getAuditItemsListSelect:01");
		   AppMyUserModifyContext acMyUserMod= new AppMyUserModifyContext();
		   if( auditItemsListSelect==null){
			   log.info("getAuditItemsListSelect:02");
			   auditItemsListSelect = new ArrayList<BaseTableItem>();
			   
			   auditItemsListSelect.add(acMyUserMod.getAuditItemsMap().get("idApp"));
			   auditItemsListSelect.add(acMyUserMod.getAuditItemsMap().get("created"));
			   auditItemsListSelect.add(acMyUserMod.getAuditItemsMap().get("orgName"));
			  
			   auditItemsListSelect.add(acMyUserMod.getAuditItemsMap().get("statusValue"));
		   }
	       return this.auditItemsListSelect;
  }
  
	 
	 public String getCommentText(){
		 return this.commentText;
	 }
	 public void setCommentText(String commentText){
		 this.commentText=commentText;
	 }
  
  public List <BaseTableItem> getAuditItemsListContext() {
	   log.info("AppMyUserModifyManager:getAuditItemsListContext");
	   if(auditItemsListContext==null){
		   AppMyUserModifyContext acMyUserMod= new AppMyUserModifyContext();
		  
		   
		   
		   auditItemsListContext=acMyUserMod.getAuditItemsCollection();
		   
	   }
	   return this.auditItemsListContext;
  }
  
  public List<HeaderTableItem> getHeaderItemsListContext() {
	  
	  if(headerItemsListContext==null){
		   AppMyUserModifyContext acMyUserMod= new AppMyUserModifyContext();
		   headerItemsListContext=acMyUserMod.getHeaderItemsList();
		   
		
		   
	   }
	  
	   return this.headerItemsListContext;
  }
  
  
  public List<HeaderTableItem> getHeaderItemsListContext(String ids) {
	  
	 	AppMyUserModifyContext acMyUserMod= new AppMyUserModifyContext();
		
	 	if(ids!=null) {
	 		
	 	
	 		headerItemsListContext=new ArrayList<HeaderTableItem>();
	 				
	 	    
	 	
	 	     List<String> idsList =  Arrays.asList(ids.split(","));
	 	   
	    	for(HeaderTableItem hti :acMyUserMod.getHeaderItemsList()){
			
			 
			
			  if(idsList.contains(hti.getItemField())){
				  headerItemsListContext.add(hti);
			  }
			  
		   }

	 	}
	   return this.headerItemsListContext;
 }
  
  public List<HeaderTableItem> getHeaderItemsListContextCREATE(String ids) {
	  
	 	AppMyUserModifyContext acMyUserMod= new AppMyUserModifyContext();
		
	 	if(ids!=null) {
	 		
	 	
	 		headerItemsListContextCREATE=new ArrayList<HeaderTableItem>();
	 				
	 	    
	 	
	 	     List<String> idsList =  Arrays.asList(ids.split(","));
	 	   
	    	for(HeaderTableItem hti :acMyUserMod.getHeaderItemsList()){
			
			 
			
			  if(idsList.contains(hti.getItemField())){
				  headerItemsListContextCREATE.add(hti);
			  }
			  
		   }

	 	}
	   return this.headerItemsListContextCREATE;
}
}
