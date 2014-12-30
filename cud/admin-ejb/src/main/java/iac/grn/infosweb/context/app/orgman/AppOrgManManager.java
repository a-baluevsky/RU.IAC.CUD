package iac.grn.infosweb.context.app.orgman;

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










import iac.cud.infosweb.dataitems.AppOrgManItem;
import iac.cud.infosweb.dataitems.BaseItem;
import iac.grn.infosweb.session.audit.actions.ActionsMap;
import iac.grn.infosweb.session.audit.actions.ResourcesMap;
import iac.grn.infosweb.session.table.BaseManager;
import iac.grn.serviceitems.BaseTableItem;
import iac.grn.serviceitems.HeaderTableItem;

@Name("appOrgManManager")
 public class AppOrgManManager extends BaseManager{

	
	private String rejectReason;
	private String commentText;
	
	public void invokeLocal(String type, int firstRow, int numberOfRows,
	           String sessionId) {
		
		 log.info("appOrgManManager:invokeLocal");
		 try{
			 String orderQueryAppOrgMan=null;
			 log.info("AppOrgMan:invokeLocal");
			 
			 AppOrgManStateHolder appOrgManStateHolder = (AppOrgManStateHolder)
					  Component.getInstance("appOrgManStateHolder",ScopeType.SESSION);
			 Map<String, String> filterMap = appOrgManStateHolder.getColumnFilterValues();
			 String st=null;
			  
			 if("list".equals(type)){
				 log.info("AppOrgMan:invokeLocal:list:01");
				 
				 Set<Map.Entry<String, String>> set = appOrgManStateHolder.getSortOrders().entrySet();
                 for (Map.Entry<String, String> me : set) {
      		       
      		       if(orderQueryAppOrgMan==null){
      		    	 orderQueryAppOrgMan="order by "+me.getKey()+" "+me.getValue();
      		       }else{
      		    	 orderQueryAppOrgMan=orderQueryAppOrgMan+", "+me.getKey()+" "+me.getValue();  
      		       }
      		     }
                 log.info("AppOrgMan:invokeLocal:list:orderQuery:"+orderQueryAppOrgMan);
                 
                 if(filterMap!=null){
    	    		 Set<Map.Entry<String, String>> setFilterOrgMan = filterMap.entrySet();
    	              for (Map.Entry<String, String> me : setFilterOrgMan) {
    	            	
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
                 log.info("AppOrgMan:invokeLocal:list:filterQuery:"+st);

             
               List<Object[]> lo=null;
               AppOrgManItem ui = null;
               DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm:ss");
               

             lo=entityManager.createNativeQuery(
             "select t1.t1_id, t1.t1_created, "+
             "t1.t1_status, t1_org_name,  t1_user_fio, t1_reject_reason, t1_comment, "+
             "t1_org_name_app, t1_user_id_app,  t1_user_login_app, t1_user_fio_app, t1_user_pos_app, "+
             "t1_dep_name_app, " +
             "t1_MODE_EXEC "+
             "from( "+ 
             "select JAS.ID_SRV t1_id, JAS.CREATED t1_created, "+  
             "JAS.STATUS t1_status,  CL_ORG_FULL.FULL_ t1_org_name, "+
             "JAS.COMMENT_ t1_comment, "+
              "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO ) t1_user_fio, "+
              "JAS.REJECT_REASON t1_reject_reason, "+ 
              
              "AU_APP.ID_SRV  t1_user_id_app, AU_APP.LOGIN  t1_user_login_app, "+
               "CL_ORG_app.FULL_ t1_org_name_app,  decode(AU_app.UP_SIGN_USER, null, AU_app.SURNAME||' '||AU_app.NAME_ ||' '|| AU_app.PATRONYMIC,  CL_USR_app.FIO ) t1_user_fio_app, "+
                 "decode(AU_app.UP_SIGN_USER, null, AU_app.POSITION, CL_USR_app.POSITION) t1_user_pos_app, "+
                 
                 "decode(AU_app.UP_SIGN_USER, null, AU_app.DEPARTMENT, decode(substr(CL_DEP_app.sign_object,4,2), '00', null, CL_DEP_app.FULL_)) t1_dep_name_app, " +
                 "JAS.MODE_EXEC t1_MODE_EXEC "+  
             "from JOURN_APP_ORG_MANAGER_BSS_T jas, "+
               "AC_USERS_KNL_T au_FULL, "+  
                "ISP_BSS_T cl_org_full, "+
                 "ISP_BSS_T cl_usr_full, "+
                 "ISP_BSS_T cl_org_app, "+
                 "ISP_BSS_T cl_usr_app, "+
                 "ISP_BSS_T cl_dep_app, "+
                  "AC_USERS_KNL_T au_APP, "+
              "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE "+ 
                "from ISP_BSS_T cl_org "+
                "where  CL_ORG.SIGN_OBJECT LIKE '%00000' "+
                "group by CL_ORG.SIGN_OBJECT) t03, "+
                 "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "+
                            "from ISP_BSS_T cl_usr "+
                            "where CL_USR.FIO is not null "+
                            "group by CL_usr.SIGN_OBJECT) t02, "+  
                
                 "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE "+ 
                "from ISP_BSS_T cl_org "+
                "where  CL_ORG.SIGN_OBJECT LIKE '%00000' "+
                "group by CL_ORG.SIGN_OBJECT) t03_app, "+
                 "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "+ 
                            "from ISP_BSS_T cl_usr "+
                            "where CL_USR.FIO is not null "+
                            "group by CL_usr.SIGN_OBJECT) t02_app, "+ 
               "(select max(CL_dep.ID_SRV) CL_DEP_ID,  CL_DEP.SIGN_OBJECT  CL_DEP_CODE "+ 
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
                "and CL_dep_app.ID_SRV=t04_APP.CL_dep_ID "+
             ") t1 "+
              (st!=null ? " where "+st :" ")+
              (orderQueryAppOrgMan!=null ? orderQueryAppOrgMan+", t1_id desc " : " order by t1_id desc "))
              .setFirstResult(firstRow)
              .setMaxResults(numberOfRows)
              .getResultList();
               auditList = new ArrayList<BaseItem>();
               
               for(Object[] objectArray :lo){
            	   try{
            	     ui= new AppOrgManItem(
            	    		objectArray[0]!=null?new Long(objectArray[0].toString()):null,
            				objectArray[1]!=null?df.format((Date)objectArray[1]) :"",
            				objectArray[2]!=null?Integer.parseInt(objectArray[2].toString()):0,	
            				objectArray[3]!=null?objectArray[3].toString():"",
            				objectArray[4]!=null?objectArray[4].toString():"",
            				objectArray[5]!=null?objectArray[5].toString():"",
            				objectArray[6]!=null?objectArray[6].toString():"",
            				 
            			 
	            			objectArray[7]!=null?objectArray[7].toString():"",
	            			 
	            			objectArray[8]!=null?new Long(objectArray[8].toString()):null,
	            			 
	            			objectArray[9]!=null?objectArray[9].toString():"",
	            			objectArray[10]!=null?objectArray[10].toString():"",
	            			objectArray[11]!=null?objectArray[11].toString():"",
	            			 
	            			objectArray[12]!=null?objectArray[12].toString():"", 
	            			objectArray[13]!=null?Integer.parseInt(objectArray[13].toString()):1
            	    		 );
            	     auditList.add(ui);
            	   }catch(Exception e1){
            		   log.error("invokeLocal:for:error:"+e1);
            	   }
               }  
               
             log.info("AppOrgMan:invokeLocal:list:02");
             
			 } else if("count".equals(type)){
				 log.info("AppOrgMan:count:01");
				 
                 
                 if(filterMap!=null){
    	    		 Set<Map.Entry<String, String>> setFilterOrgMan = filterMap.entrySet();
    	              for (Map.Entry<String, String> me : setFilterOrgMan) {
    	              	
    	            	  
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
						               "select JAS.ID_SRV t1_id, JAS.CREATED t1_created, "+  
						               "JAS.STATUS t1_status,  CL_ORG_FULL.FULL_ t1_org_name, "+
						               "JAS.COMMENT_ t1_comment, "+
						                "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO ) t1_user_fio, "+
						                "JAS.REJECT_REASON t1_reject_reason, "+ 
						                
						                "AU_APP.ID_SRV  t1_user_id_app, AU_APP.LOGIN  t1_user_login_app, "+
						                 "CL_ORG_app.FULL_ t1_org_name_app,  decode(AU_app.UP_SIGN_USER, null, AU_app.SURNAME||' '||AU_app.NAME_ ||' '|| AU_app.PATRONYMIC,  CL_USR_app.FIO ) t1_user_fio_app, "+
						                   "decode(AU_app.UP_SIGN_USER, null, AU_app.POSITION, CL_USR_app.POSITION) t1_user_pos_app, "+
						                   
						                   "decode(AU_app.UP_SIGN_USER, null, AU_app.DEPARTMENT, decode(substr(CL_DEP_app.sign_object,4,2), '00', null, CL_DEP_app.FULL_)) t1_dep_name_app, "+
						                   "JAS.MODE_EXEC t1_MODE_EXEC "+  
						               "from JOURN_APP_ORG_MANAGER_BSS_T jas, "+
						                 "AC_USERS_KNL_T au_FULL, "+  
						                  "ISP_BSS_T cl_org_full, "+
						                   "ISP_BSS_T cl_usr_full, "+
						                   "ISP_BSS_T cl_org_app, "+
						                   "ISP_BSS_T cl_usr_app, "+
						                   "ISP_BSS_T cl_dep_app, "+
						                   "AC_USERS_KNL_T au_APP, "+
						                "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE "+ 
						                  "from ISP_BSS_T cl_org "+
						                  "where  CL_ORG.SIGN_OBJECT LIKE '%00000' "+
						                  "group by CL_ORG.SIGN_OBJECT) t03, "+
						                   "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "+
						                              "from ISP_BSS_T cl_usr "+
						                              "where CL_USR.FIO is not null "+
						                              "group by CL_usr.SIGN_OBJECT) t02, "+  
						                  
						                   "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE "+ 
						                  "from ISP_BSS_T cl_org "+
						                  "where  CL_ORG.SIGN_OBJECT LIKE '%00000' "+
						                  "group by CL_ORG.SIGN_OBJECT) t03_app, "+
						                   "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "+ 
						                              "from ISP_BSS_T cl_usr "+
						                              "where CL_USR.FIO is not null "+
						                              "group by CL_usr.SIGN_OBJECT) t02_app, "+ 
						                 "(select max(CL_dep.ID_SRV) CL_DEP_ID,  CL_DEP.SIGN_OBJECT  CL_DEP_CODE "+ 
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
						                  "and CL_dep_app.ID_SRV=t04_APP.CL_dep_ID "+
						               ") t1 "+
		         (st!=null ? " where "+st :" "))
               .getSingleResult()).longValue();
                 
                 
               log.info("AppOrgMan:invokeLocal:count:02:"+auditCount);
           	 } else if("bean".equals(type)){
				 
			 }
		}catch(Exception e){
			  log.error("AppOrgMan:invokeLocal:error:"+e);
			  evaluteForList=false;
			  FacesMessages.instance().add("ќшибка!");
		}

	}
	
	 private AppOrgManItem getUserItem(Long idUser){
		 if(idUser==null){
			  return null;
		   }
		   
		   try{
	           List<Object[]> lo=null;
	           AppOrgManItem ui = null;
	           DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm:ss");
	           
	           lo=entityManager.createNativeQuery(
	        		             "select t1.t1_id, t1.t1_created, "+
	        		             "t1.t1_status, t1_org_name,  t1_user_fio, t1_reject_reason, t1_comment, "+
	        		             "t1_org_name_app, t1_user_id_app,  t1_user_login_app, t1_user_fio_app, t1_user_pos_app, "+
	        		             "t1_dep_name_app, " +
	        		             "t1_MODE_EXEC "+
	        		             "from( "+ 
	        		             "select JAS.ID_SRV t1_id, JAS.CREATED t1_created, "+  
	        		             "JAS.STATUS t1_status,  CL_ORG_FULL.FULL_ t1_org_name, "+
	        		             "JAS.COMMENT_ t1_comment, "+
	        		              "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO ) t1_user_fio, "+
	        		              "JAS.REJECT_REASON t1_reject_reason, "+ 
	        		              
	        		              "AU_APP.ID_SRV  t1_user_id_app, AU_APP.LOGIN  t1_user_login_app, "+
	        		               "CL_ORG_app.FULL_ t1_org_name_app,  decode(AU_app.UP_SIGN_USER, null, AU_app.SURNAME||' '||AU_app.NAME_ ||' '|| AU_app.PATRONYMIC,  CL_USR_app.FIO ) t1_user_fio_app, "+
	        		                 "decode(AU_app.UP_SIGN_USER, null, AU_app.POSITION, CL_USR_app.POSITION) t1_user_pos_app, "+
	        		                 
	        		                 "decode(AU_app.UP_SIGN_USER, null, AU_app.DEPARTMENT, decode(substr(CL_DEP_app.sign_object,4,2), '00', null, CL_DEP_app.FULL_)) t1_dep_name_app, "+
	        		                 "JAS.MODE_EXEC t1_MODE_EXEC "+  
	        		             "from JOURN_APP_ORG_MANAGER_BSS_T jas, "+
	        		               "AC_USERS_KNL_T au_FULL, "+  
	        		                "ISP_BSS_T cl_org_full, "+
	        		                 "ISP_BSS_T cl_usr_full, "+
	        		                 "ISP_BSS_T cl_org_app, "+
	        		                 "ISP_BSS_T cl_usr_app, "+
	        		                 "ISP_BSS_T cl_dep_app, "+
	        		                 "AC_IS_BSS_T arm, "+
	        		                 "AC_USERS_KNL_T au_APP, "+
	        		              "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE "+ 
	        		                "from ISP_BSS_T cl_org "+
	        		                "where  CL_ORG.SIGN_OBJECT LIKE '%00000' "+
	        		                "group by CL_ORG.SIGN_OBJECT) t03, "+
	        		                 "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "+
	        		                            "from ISP_BSS_T cl_usr "+
	        		                            "where CL_USR.FIO is not null "+
	        		                            "group by CL_usr.SIGN_OBJECT) t02, "+  
	        		                
	        		                 "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE "+ 
	        		                "from ISP_BSS_T cl_org "+
	        		                "where  CL_ORG.SIGN_OBJECT LIKE '%00000' "+
	        		                "group by CL_ORG.SIGN_OBJECT) t03_app, "+
	        		                 "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "+ 
	        		                            "from ISP_BSS_T cl_usr "+
	        		                            "where CL_USR.FIO is not null "+
	        		                            "group by CL_usr.SIGN_OBJECT) t02_app, "+ 
	        		               "(select max(CL_dep.ID_SRV) CL_DEP_ID,  CL_DEP.SIGN_OBJECT  CL_DEP_CODE "+ 
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
	        		                "and CL_dep_app.ID_SRV=t04_APP.CL_dep_ID "+
	        		                "and JAS.ID_SRV=? "+
	        		             ") t1 ")
	         .setParameter(1, idUser)
	         .getResultList();
	           
	           for(Object[] objectArray :lo){
	        	   try{
	        		   log.info("AppOrgManManager:getUserItem:login:"+objectArray[1].toString());
	        		   
	        		   ui= new AppOrgManItem(
	        				  objectArray[0]!=null?new Long(objectArray[0].toString()):null,
	            				objectArray[1]!=null?df.format((Date)objectArray[1]) :"",
	            				objectArray[2]!=null?Integer.parseInt(objectArray[2].toString()):0,	
	            				objectArray[3]!=null?objectArray[3].toString():"",
	            				objectArray[4]!=null?objectArray[4].toString():"",
	            				objectArray[5]!=null?objectArray[5].toString():"",
	            				objectArray[6]!=null?objectArray[6].toString():"",
	            				 
	            				objectArray[7]!=null?objectArray[7].toString():"",
		            			 
		            			objectArray[8]!=null?new Long(objectArray[8].toString()):null,
		            			 
		            			objectArray[9]!=null?objectArray[9].toString():"",
		            			objectArray[10]!=null?objectArray[10].toString():"",
		            			objectArray[11]!=null?objectArray[11].toString():"",
		            			 
		            			objectArray[12]!=null?objectArray[12].toString():"",
		            			objectArray[13]!=null?Integer.parseInt(objectArray[13].toString()):1
	            			   );
	        	     return ui;
	        	   }catch(Exception e1){
	        		   log.error("getUserItem:for:error:"+e1);
	        	   }
	           }  
		   }catch(Exception e){
			   log.error("getUserItem:error:"+e);
		   }
		   return null;
	 }
	 
	 
	 public void createOrgMan(){
		 
		// MODE_EXEC: 0 Ц УREPLACEФ, 1 - УADDФ, 2 Ц УREMOVEФ 
		 
		   log.info("AppOrgManManager:createOrgMan:01");
		  
		   String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	       log.info("AppOrgManManager:createOrgMan:sessionId:"+sessionId);
	     
	       Long idApp=null;
	       Long idUser=null;
	       int modeExec=1;
	       
	       try{
			 
			 if(sessionId==null){
				  return;
			 }
			   
			 idApp =  new Long(sessionId); 
			   
			 Object[] app=(Object[]) entityManager.createNativeQuery(
		    			  "select JAS.UP_USER_APP, JAS.MODE_EXEC "+
                          "from JOURN_APP_ORG_MANAGER_BSS_T jas "+
                          "where  JAS.ID_SRV=? ")
		    			.setParameter(1, idApp)
		    			.getSingleResult();  
			 
			 idUser=new Long(app[0].toString());
			 modeExec=Integer.parseInt(app[1].toString());
			 
			 log.info("AppOrgManManager:createOrgMan:idUser:"+idUser);
			 log.info("AppOrgManManager:createOrgMan:modeExec:"+modeExec);
			 
				 
			 if(modeExec==0){ //REMOVE
				 
				       	entityManager.createNativeQuery(
	     	     				"update AC_USERS_KNL_T t1 " +
	     	     				"set t1.IS_ACC_ORG_MANAGER = 0 " +
	     	     				"where t1.ID_SRV = :idUser ")
	     	     		 .setParameter("idUser", idUser)
	    	             .executeUpdate();
	                	
	                	 log.info("AppOrgManManager:createOrgMan:02");
	      		 
			 }else if(modeExec==1){ //ADD
			 
				 entityManager.createNativeQuery(
  	     				"update AC_USERS_KNL_T t1 " +
  	     				"set t1.IS_ACC_ORG_MANAGER = 1 " +
  	     				"where t1.ID_SRV = :idUser ")
  	     		 .setParameter("idUser", idUser)
 	             .executeUpdate();
				 
		         log.info("AppOrgManManager:createOrgMan:03");
		     
				 
			 }else{
				 return;
			 }
			 
			 entityManager.flush();
	    	 
		     entityManager.createNativeQuery(
	 	     		   "update JOURN_APP_ORG_MANAGER_BSS_T t1 " +
	 	     		   "set t1.STATUS=1, t1.UP_USER_EXEC=? " +
	 	     		   "where t1.ID_SRV=? ")
	 	     		 .setParameter(1, getCurrentUser().getBaseId())
	 	     		 .setParameter(2, idApp)
	         	 	 .executeUpdate();
		    
		     AppOrgManItem ui = getUserItem(new Long(sessionId));
		     
		     Contexts.getEventContext().set("contextBeanView", ui);
		     
		     audit(ResourcesMap.APP_ORG_MAN, ActionsMap.EXECUTE); 
		     
		   }catch(Exception e){
			   log.error("AppOrgManManager:createOrgMan:error:"+e);
		   }
	 }
	 
	 public void reject(){
		   log.info("AppOrgManManager:reject:01");
		  
		   String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	       log.info("AppOrgManManager:reject:sessionId:"+sessionId);
	     
		   try{
			   
		     entityManager.createNativeQuery(
	 	     		   "update JOURN_APP_ORG_MANAGER_BSS_T t1 " +
	 	     		   "set t1.STATUS=2,  t1.REJECT_REASON=?, t1.UP_USER_EXEC=? " +
	 	     		   "where t1.ID_SRV=? ")
	 	     		 .setParameter(1, this.rejectReason)
	 	     		 .setParameter(2, getCurrentUser().getBaseId())
	 	     		 .setParameter(3, new Long(sessionId))
	 	     	 	 .executeUpdate();
		     
             AppOrgManItem ui = getUserItem(new Long(sessionId)); 
		     
		     Contexts.getEventContext().set("contextBeanView", ui);
		    
		     audit(ResourcesMap.APP_ORG_MAN, ActionsMap.REJECT); 
		     
		   }catch(Exception e){
			   log.error("AppOrgManManager:reject:error:"+e);
		   }
	 }
	 
	 public void comment(){
		   log.info("AppOrgManManager:comment:01");
		  
		   String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	       log.info("AppOrgManManager:comment:sessionId:"+sessionId);
	     
		   try{
			   
		     entityManager.createNativeQuery(
	 	     		   "update JOURN_APP_ORG_MANAGER_BSS_T t1 " +
	 	     		   "set t1.COMMENT_=? " +
	 	     		   "where t1.ID_SRV=? ")
	 	     		 .setParameter(1, this.commentText)
	 	     		 .setParameter(2, new Long(sessionId))
	 	     	 	 .executeUpdate();
		     
           AppOrgManItem ui = getUserItem(new Long(sessionId)); 
		     
		   Contexts.getEventContext().set("contextBeanView", ui);
		     
		   }catch(Exception e){
			   log.error("AppOrgManManager:reject:error:"+e);
		   }
	 }
	 
	 public void forViewCrt() {
		   try{
			 String sessionId = FacesContext.getCurrentInstance().getExternalContext()
				        .getRequestParameterMap()
				        .get("sessionId");
		     log.info("AppOrgManManager:forViewCrt:sessionId:"+sessionId);
		     
		     AppOrgManItem ui = getUserItem(new Long(sessionId));
	    		
	         Contexts.getEventContext().set("contextBeanView", ui);
		     
		   }catch(Exception e){
			 log.error("AppOrgManManager:forViewCrt:Error:"+e);
		   }
	   } 
	 
	 public void forViewUpdDel() {
		 try{
		     String sessionId = FacesContext.getCurrentInstance().getExternalContext()
				        .getRequestParameterMap()
				        .get("sessionId");
		     log.info("AppOrgManManager:forViewUpdDel:sessionId:"+sessionId);
		     if(sessionId!=null){
		    	
		     	 
		    	 AppOrgManItem ui = getUserItem(new Long(sessionId));
		        	 
		   	 Contexts.getEventContext().set("appOrgManBean", ui);
		     }
		   }catch(Exception e){
			   log.error("AppOrgManManager:forViewUpdDel:Error:"+e);
		   }
	 }
	 
	 public void forViewComment() {
		   
		   try{
			     String sessionId = FacesContext.getCurrentInstance().getExternalContext()
					        .getRequestParameterMap()
					        .get("sessionId");
			     log.info("AppOrgManManager:forViewComment:sessionId:"+sessionId);
			     if(sessionId!=null){
			    	
			     	 
			    	 AppOrgManItem ui = getUserItem(new Long(sessionId));
			    	 
			    	 this.commentText=ui.getComment();

	     }
			}catch(Exception e){
				   log.error("AppOrgManManager:forViewComment:Error:"+e);
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
		   AppOrgManContext ac= new AppOrgManContext();
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
	   log.info("AppOrgManManager:getAuditItemsListContext");
	   if(auditItemsListContext==null){
		   AppOrgManContext ac= new AppOrgManContext();
		  
		   
		   
		   auditItemsListContext=ac.getAuditItemsCollection();
		   
	   }
	   return this.auditItemsListContext;
  }
  
  public List<HeaderTableItem> getHeaderItemsListContext() {
	  
	  if(headerItemsListContext==null){
		   AppOrgManContext ac= new AppOrgManContext();
		   headerItemsListContext=ac.getHeaderItemsList();
		   
		
		   
	   }
	
	   return this.headerItemsListContext;
  }

 public List<HeaderTableItem> getHeaderItemsListContext(String ids) {
	  
	 	AppOrgManContext ac= new AppOrgManContext();
		
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


}
