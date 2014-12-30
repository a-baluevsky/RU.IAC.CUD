package iac.grn.infosweb.context.app.adminsys;

import iac.cud.infosweb.dataitems.AppAdminSysItem;
import iac.cud.infosweb.dataitems.BaseItem;
import iac.cud.infosweb.entity.AcUser;
import iac.cud.infosweb.entity.LinkAdminUserSys;
import iac.grn.infosweb.session.table.BaseManager;
import iac.grn.serviceitems.BaseTableItem;
import iac.grn.serviceitems.HeaderTableItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;

@Name("appAdminSysManager")
 public class AppAdminSysManager extends BaseManager{

	
	private String rejectReason;
	private String commentText;
	
	public void invokeLocal(String type, int firstRow, int numberOfRows,
	           String sessionId) {
		
		 log.info("appAdminSysManager:invokeLocal");
		 try{
			 String orderQueryAppAdmin=null;
			 log.info("hostsManager:invokeLocal");
			 
			 AppAdminSysStateHolder appAdminSysStateHolder = (AppAdminSysStateHolder)
					  Component.getInstance("appAdminSysStateHolder",ScopeType.SESSION);
			 Map<String, String> filterMap = appAdminSysStateHolder.getColumnFilterValues();
			 String st=null;
			  
			 if("list".equals(type)){
				 log.info("AppAdmin:invokeLocal:list:01");
				 
				 Set<Map.Entry<String, String>> set = appAdminSysStateHolder.getSortOrders().entrySet();
                 for (Map.Entry<String, String> me : set) {
      		       log.info("me.getKey+:"+me.getKey());
      		       log.info("me.getValue:"+me.getValue());
      		       
      		       if(orderQueryAppAdmin==null){
      		    	 orderQueryAppAdmin="order by "+me.getKey()+" "+me.getValue();
      		       }else{
      		    	 orderQueryAppAdmin=orderQueryAppAdmin+", "+me.getKey()+" "+me.getValue();  
      		       }
      		     }
                 log.info("invokeLocal:list:orderQueryAppAdmin:"+orderQueryAppAdmin);
                 
                 if(filterMap!=null){
    	    		 Set<Map.Entry<String, String>> setFilterAppAdmin = filterMap.entrySet();
    	              for (Map.Entry<String, String> me : setFilterAppAdmin) {
    	              
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
                 log.info("AppAdmin:invokeLocal:list:filterQuery:"+st);

             
               List<Object[]> lo=null;
               AppAdminSysItem ui = null;
               DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm:ss");
               

             lo=entityManager.createNativeQuery(
             "select t1.t1_id, t1.t1_created, "+
             "t1.t1_status, t1_org_name,  t1_user_fio, t1_reject_reason, t1_comment, "+
             "t1_arm_id, t1_arm_code, t1_arm_name, t1_arm_description, "+
             "t1_org_name_app, t1_user_id_app,  t1_user_login_app, t1_user_fio_app, t1_user_pos_app, "+
             "t1_dep_name_app, " +
             "t1_MODE_EXEC "+
             "from( "+ 
             "select JAS.ID_SRV t1_id, JAS.CREATED t1_created, "+  
             "JAS.STATUS t1_status,  CL_ORG_FULL.FULL_ t1_org_name, "+
             "JAS.COMMENT_ t1_comment, "+
              "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO ) t1_user_fio, "+
              "JAS.REJECT_REASON t1_reject_reason, "+ 
              "ARM.ID_SRV t1_arm_id, ARM.SIGN_OBJECT t1_arm_code, ARM.FULL_ t1_arm_name, ARM.DESCRIPTION  t1_arm_description, "+
              
              "AU_APP.ID_SRV  t1_user_id_app, AU_APP.LOGIN  t1_user_login_app, "+
               "CL_ORG_app.FULL_ t1_org_name_app,  decode(AU_app.UP_SIGN_USER, null, AU_app.SURNAME||' '||AU_app.NAME_ ||' '|| AU_app.PATRONYMIC,  CL_USR_app.FIO ) t1_user_fio_app, "+
                 "decode(AU_app.UP_SIGN_USER, null, AU_app.POSITION, CL_USR_app.POSITION) t1_user_pos_app, "+
                 
                 "decode(AU_app.UP_SIGN_USER, null, AU_app.DEPARTMENT, decode(substr(CL_DEP_app.sign_object,4,2), '00', null, CL_DEP_app.FULL_)) t1_dep_name_app, " +
                 "JAS.MODE_EXEC t1_MODE_EXEC "+  
             "from JOURN_APP_ADMIN_USER_SYS_BSS_T jas, "+
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
                "and ARM.ID_SRV =JAS.UP_SYS "+
                "and au_APP.ID_SRV =JAS.UP_USER_APP "+
                
                "and au_APP.UP_SIGN=t03_APP.CL_ORG_CODE "+
                "and CL_ORG_app.ID_SRV=t03_APP.CL_ORG_ID "+
                
                "and AU_APP.UP_SIGN_USER=t02_APP.CL_USR_CODE(+) "+
                "and CL_USR_APP.ID_SRV(+)=t02_APP.CL_USR_ID "+
                
                "and substr(au_APP.UP_SIGN,1,5)||'000'=t04_APP.CL_dep_CODE(+) "+
                "and CL_dep_app.ID_SRV=t04_APP.CL_dep_ID "+
             ") t1 "+
              (st!=null ? " where "+st :" ")+
              (orderQueryAppAdmin!=null ? orderQueryAppAdmin+", t1_id desc " : " order by t1_id desc "))
              .setFirstResult(firstRow)
              .setMaxResults(numberOfRows)
              .getResultList();
               auditList = new ArrayList<BaseItem>();
               
               for(Object[] objectArray :lo){
            	   try{
            	     ui= new AppAdminSysItem(
            	    		objectArray[0]!=null?new Long(objectArray[0].toString()):null,
            				objectArray[1]!=null?df.format((Date)objectArray[1]) :"",
            				objectArray[2]!=null?Integer.parseInt(objectArray[2].toString()):0,	
            				objectArray[3]!=null?objectArray[3].toString():"",
            				objectArray[4]!=null?objectArray[4].toString():"",
            				objectArray[5]!=null?objectArray[5].toString():"",
            				objectArray[6]!=null?objectArray[6].toString():"",
            				 
            				objectArray[7]!=null?new Long(objectArray[7].toString()):null,
	            			objectArray[8]!=null?objectArray[8].toString():"",
	            			objectArray[9]!=null?objectArray[9].toString():"",
	            			objectArray[10]!=null?objectArray[10].toString():"",
	            			 
	            			objectArray[11]!=null?objectArray[11].toString():"",
	            			 
	            			objectArray[12]!=null?new Long(objectArray[12].toString()):null,
	            			 
	            			objectArray[13]!=null?objectArray[13].toString():"",
	            			objectArray[14]!=null?objectArray[14].toString():"",
	            			objectArray[15]!=null?objectArray[15].toString():"",
	            			 
	            			objectArray[16]!=null?objectArray[16].toString():"", 
	            			objectArray[17]!=null?Integer.parseInt(objectArray[17].toString()):1
            	    		 );
            	     auditList.add(ui);
            	   }catch(Exception e1){
            		   log.error("invokeLocal:for:error:"+e1);
            	   }
               }  
               
             log.info("AppAdmin:invokeLocal:list:02");
             
			 } else if("count".equals(type)){
				 log.info("AppAdmin:count:01");
				 
                 
                 if(filterMap!=null){
    	    		 Set<Map.Entry<String, String>> setFilterAppAdmin = filterMap.entrySet();
    	              for (Map.Entry<String, String> me : setFilterAppAdmin) {
    	            	  
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
						                "ARM.ID_SRV t1_arm_id, ARM.SIGN_OBJECT t1_arm_code, ARM.FULL_ t1_arm_name, ARM.DESCRIPTION  t1_arm_description, "+
						                
						                "AU_APP.ID_SRV  t1_user_id_app, AU_APP.LOGIN  t1_user_login_app, "+
						                 "CL_ORG_app.FULL_ t1_org_name_app,  decode(AU_app.UP_SIGN_USER, null, AU_app.SURNAME||' '||AU_app.NAME_ ||' '|| AU_app.PATRONYMIC,  CL_USR_app.FIO ) t1_user_fio_app, "+
						                   "decode(AU_app.UP_SIGN_USER, null, AU_app.POSITION, CL_USR_app.POSITION) t1_user_pos_app, "+
						                   
						                   "decode(AU_app.UP_SIGN_USER, null, AU_app.DEPARTMENT, decode(substr(CL_DEP_app.sign_object,4,2), '00', null, CL_DEP_app.FULL_)) t1_dep_name_app, "+
						                   "JAS.MODE_EXEC t1_MODE_EXEC "+  
						               "from JOURN_APP_ADMIN_USER_SYS_BSS_T jas, "+
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
						                  "and ARM.ID_SRV =JAS.UP_SYS "+
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
                 
                 
               log.info("AppAdmin:invokeLocal:count:02:"+auditCount);
           	 } else if("bean".equals(type)){
				 
			 }
		}catch(Exception e){
			  log.error("AppAdmin:invokeLocal:error:"+e);
			  evaluteForList=false;
			  FacesMessages.instance().add("ќшибка!");
		}

	}
	
	 private AppAdminSysItem getUserItem(Long idUser){
		 if(idUser==null){
			  return null;
		   }
		   
		   try{
	           List<Object[]> lo=null;
	           AppAdminSysItem ui = null;
	           DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm:ss");
	           
	           lo=entityManager.createNativeQuery(
	        		             "select t1.t1_id, t1.t1_created, "+
	        		             "t1.t1_status, t1_org_name,  t1_user_fio, t1_reject_reason, t1_comment, "+
	        		             "t1_arm_id, t1_arm_code, t1_arm_name, t1_arm_description, "+
	        		             "t1_org_name_app, t1_user_id_app,  t1_user_login_app, t1_user_fio_app, t1_user_pos_app, "+
	        		             "t1_dep_name_app, " +
	        		             "t1_MODE_EXEC "+
	        		             "from( "+ 
	        		             "select JAS.ID_SRV t1_id, JAS.CREATED t1_created, "+  
	        		             "JAS.STATUS t1_status,  CL_ORG_FULL.FULL_ t1_org_name, "+
	        		             "JAS.COMMENT_ t1_comment, "+
	        		              "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO ) t1_user_fio, "+
	        		              "JAS.REJECT_REASON t1_reject_reason, "+ 
	        		              "ARM.ID_SRV t1_arm_id, ARM.SIGN_OBJECT t1_arm_code, ARM.FULL_ t1_arm_name, ARM.DESCRIPTION  t1_arm_description, "+
	        		              
	        		              "AU_APP.ID_SRV  t1_user_id_app, AU_APP.LOGIN  t1_user_login_app, "+
	        		               "CL_ORG_app.FULL_ t1_org_name_app,  decode(AU_app.UP_SIGN_USER, null, AU_app.SURNAME||' '||AU_app.NAME_ ||' '|| AU_app.PATRONYMIC,  CL_USR_app.FIO ) t1_user_fio_app, "+
	        		                 "decode(AU_app.UP_SIGN_USER, null, AU_app.POSITION, CL_USR_app.POSITION) t1_user_pos_app, "+
	        		                 
	        		                 "decode(AU_app.UP_SIGN_USER, null, AU_app.DEPARTMENT, decode(substr(CL_DEP_app.sign_object,4,2), '00', null, CL_DEP_app.FULL_)) t1_dep_name_app, "+
	        		                 "JAS.MODE_EXEC t1_MODE_EXEC "+  
	        		             "from JOURN_APP_ADMIN_USER_SYS_BSS_T jas, "+
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
	        		                "and ARM.ID_SRV =JAS.UP_SYS "+
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
	        		   log.info("AppAdminSysManager:getUserItem:login:"+objectArray[1].toString());
	        		   
	        		   ui= new AppAdminSysItem(
	        				  objectArray[0]!=null?new Long(objectArray[0].toString()):null,
	            				objectArray[1]!=null?df.format((Date)objectArray[1]) :"",
	            				objectArray[2]!=null?Integer.parseInt(objectArray[2].toString()):0,	
	            				objectArray[3]!=null?objectArray[3].toString():"",
	            				objectArray[4]!=null?objectArray[4].toString():"",
	            				objectArray[5]!=null?objectArray[5].toString():"",
	            				objectArray[6]!=null?objectArray[6].toString():"",
	            				 
	            				objectArray[7]!=null?new Long(objectArray[7].toString()):null,
		            			objectArray[8]!=null?objectArray[8].toString():"",
		            			objectArray[9]!=null?objectArray[9].toString():"",
		            			objectArray[10]!=null?objectArray[10].toString():"",
		            			 
		            			objectArray[11]!=null?objectArray[11].toString():"",
		            			 
		            			objectArray[12]!=null?new Long(objectArray[12].toString()):null,
		            			 
		            			objectArray[13]!=null?objectArray[13].toString():"",
		            			objectArray[14]!=null?objectArray[14].toString():"",
		            			objectArray[15]!=null?objectArray[15].toString():"",
		            			 
		            			objectArray[16]!=null?objectArray[16].toString():"",
		            			objectArray[17]!=null?Integer.parseInt(objectArray[17].toString()):1
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
	 
	 
	 public void createAdminSys(){
		 
		// MODE_EXEC: 0 Ц УREPLACEФ, 1 - УADDФ, 2 Ц УREMOVEФ 
		 
		   log.info("AppAdminSysManager:createAdminSys:01");
		  
		   String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	       log.info("AppAdminSysManager:createAdminSys:sessionId:"+sessionId);
	     
	       Long idApp=null;
	       Long idUser=null;
	       Long idArm=null;
	       int modeExec=1;
	       
	        
		   try{
			 
			 if(sessionId==null){
				  return;
			 }
			   
			 idApp =  new Long(sessionId); 
			   
			 Object[] app=(Object[]) entityManager.createNativeQuery(
		    			  "select JAS.UP_USER_APP, JAS.UP_SYS, JAS.MODE_EXEC "+
                          "from JOURN_APP_ADMIN_USER_SYS_BSS_T jas "+
                          "where  JAS.ID_SRV=? ")
		    			.setParameter(1, idApp)
		    			.getSingleResult();  
			 
			 idUser=new Long(app[0].toString());
			 idArm=new Long(app[1].toString());
			 modeExec=Integer.parseInt(app[2].toString());
			 
			 log.info("AppAdminSysManager:createAdminSys:idUser:"+idUser);
			 log.info("AppAdminSysManager:createAdminSys:idArm:"+idArm);
			 log.info("AppAdminSysManager:createAdminSys:modeExec:"+modeExec);
			 
			 AcUser aum = entityManager.find(AcUser.class, idUser);
				
			 List<LinkAdminUserSys> guuExistList =  aum.getLinkAdminUserSys();
		 
			 LinkAdminUserSys au = new LinkAdminUserSys(idUser, idArm);
	            au.setCreated(new Date());
	            au.setCreator(idUser);
	       
			 
			 if(modeExec==0){ 
				 //REMOVE
				 
				 if(guuExistList.contains(au)){
					 //есть в базе
	                	
	                	guuExistList.remove(au);
	                	
	                	entityManager.createQuery(
	                		"delete from LinkAdminUserSys lgu " +
	                		"where lgu.pk.upUser = :upUser " +
	                		"and lgu.pk.upSys = :upSys ")
	                	.setParameter("upUser", idUser)
	                	.setParameter("upSys", idArm)
	                	.executeUpdate();
	                	
	                	 log.info("AppAdminSysManager:createAdminSys:02");
	                }else{
	                //ничего не делаем. и так нет в базе
	                	 log.info("AppAdminSysManager:createAdminSys:03");
	                }
				
				 
			 }else if(modeExec==1){ 
				 //ADD
			 
				     
				 if(guuExistList.contains(au)){
					 //есть в базе
		        	 //ничего не делаем. и так есть в базе
		        	 log.info("AppAdminSysManager:createAdminSys:04");
		         }else{
		           
		            guuExistList.add(au);
		            log.info("AppAdminSysManager:createAdminSys:05");
		         }
				 
			 }else{
				 return;
			 }
			 
			 entityManager.flush();
	    	 
		     entityManager.createNativeQuery(
	 	     		   "update JOURN_APP_ADMIN_USER_SYS_BSS_T t1 " +
	 	     		   "set t1.STATUS=1, t1.UP_USER_EXEC=? " +
	 	     		   "where t1.ID_SRV=? ")
	 	     		 .setParameter(1, getCurrentUser().getBaseId())
	 	     		 .setParameter(2, idApp)
	         	 	 .executeUpdate();
		    
		     AppAdminSysItem ui = getUserItem(new Long(sessionId));
		     
		     Contexts.getEventContext().set("contextBeanView", ui);
		     
		   }catch(Exception e){
			   log.error("AppAdminSysManager:createAdminSys:error:"+e);
		   }
	 }
	 
	 public void reject(){
		   log.info("AppAdminSysManager:reject:01");
		  
		   String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	       log.info("AppAdminSysManager:reject:sessionId:"+sessionId);
	     
		   try{
			   
		     entityManager.createNativeQuery(
	 	     		   "update JOURN_APP_ADMIN_USER_SYS_BSS_T t1 " +
	 	     		   "set t1.STATUS=2,  t1.REJECT_REASON=?, t1.UP_USER_EXEC=? " +
	 	     		   "where t1.ID_SRV=? ")
	 	     		 .setParameter(1, this.rejectReason)
	 	     		 .setParameter(2, getCurrentUser().getBaseId())
	 	     		 .setParameter(3, new Long(sessionId))
	 	     	 	 .executeUpdate();
		     
             AppAdminSysItem ui = getUserItem(new Long(sessionId)); 
		     
		     Contexts.getEventContext().set("contextBeanView", ui);
		     
		   }catch(Exception e){
			   log.error("AppAdminSysManager:reject:error:"+e);
		   }
	 }
	 
	 public void comment(){
		   log.info("AppAdminSysManager:comment:01");
		  
		   String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	       log.info("AppAdminSysManager:comment:sessionId:"+sessionId);
	     
		   try{
			   
		     entityManager.createNativeQuery(
	 	     		   "update JOURN_APP_ADMIN_USER_SYS_BSS_T t1 " +
	 	     		   "set t1.COMMENT_=? " +
	 	     		   "where t1.ID_SRV=? ")
	 	     		 .setParameter(1, this.commentText)
	 	     		 .setParameter(2, new Long(sessionId))
	 	     	 	 .executeUpdate();
		     
           AppAdminSysItem ui = getUserItem(new Long(sessionId)); 
		     
		   Contexts.getEventContext().set("contextBeanView", ui);
		     
		   }catch(Exception e){
			   log.error("AppAdminSysManager:reject:error:"+e);
		   }
	 }
	 
	 public void forViewCrt() {
		   try{
			 String sessionId = FacesContext.getCurrentInstance().getExternalContext()
				        .getRequestParameterMap()
				        .get("sessionId");
		     log.info("AppAdminSysManager:forViewCrt:sessionId:"+sessionId);
		     
		     AppAdminSysItem ui = getUserItem(new Long(sessionId));
	    		
	         Contexts.getEventContext().set("contextBeanView", ui);
		     
		   }catch(Exception e){
			 log.error("AppAdminSysManager:forViewCrt:Error:"+e);
		   }
	   } 
	 
	 public void forViewUpdDel() {
		 try{
		     String sessionId = FacesContext.getCurrentInstance().getExternalContext()
				        .getRequestParameterMap()
				        .get("sessionId");
		     log.info("AppAdminSysManager:forViewUpdDel:sessionId:"+sessionId);
		     if(sessionId!=null){
		    	
		     	 
		    	 AppAdminSysItem ui = getUserItem(new Long(sessionId));
		        	 
		   	 Contexts.getEventContext().set("appAdminSysBean", ui);
		     }
		   }catch(Exception e){
			   log.error("AppAdminSysManager:forViewUpdDel:Error:"+e);
		   }
	 }
	 
	 public void forViewComment() {
		   
		   try{
			     String sessionId = FacesContext.getCurrentInstance().getExternalContext()
					        .getRequestParameterMap()
					        .get("sessionId");
			     log.info("AppAdminSysManager:forViewComment:sessionId:"+sessionId);
			     if(sessionId!=null){
			    	
			     	 
			    	 AppAdminSysItem ui = getUserItem(new Long(sessionId));
			    	 
			    	 this.commentText=ui.getComment();

	     }
			}catch(Exception e){
				   log.error("AppAdminSysManager:forViewComment:Error:"+e);
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
		   AppAdminSysContext ac= new AppAdminSysContext();
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
	   log.info("AppAdminSysManager:getAuditItemsListContext");
	   if(auditItemsListContext==null){
		   AppAdminSysContext ac= new AppAdminSysContext();
		  
		   
		   
		   auditItemsListContext=ac.getAuditItemsCollection();
		   
	   }
	   return this.auditItemsListContext;
  }
  
  public List<HeaderTableItem> getHeaderItemsListContext() {
	  
	  if(headerItemsListContext==null){
		   AppAdminSysContext ac= new AppAdminSysContext();
		   headerItemsListContext=ac.getHeaderItemsList();
		   
	
		   
	   }
	
	   return this.headerItemsListContext;
  }

 public List<HeaderTableItem> getHeaderItemsListContext(String ids) {
	  
	 	AppAdminSysContext ac= new AppAdminSysContext();
		
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
