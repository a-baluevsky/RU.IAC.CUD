package iac.grn.infosweb.context.app.access;

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


import iac.cud.infosweb.dataitems.AppAccessItem;
import iac.cud.infosweb.dataitems.BaseItem;
import iac.cud.infosweb.entity.AcRole;
import iac.grn.infosweb.session.table.BaseManager;
import iac.grn.serviceitems.BaseTableItem;
import iac.grn.serviceitems.HeaderTableItem;

@Name("appAccessManager")
 public class AppAccessManager extends BaseManager{

	
	private String rejectReason;
	private String commentText;
	
	private List<AcRole> listRolesAppForView = null;
	
	public void invokeLocal(String type, int firstRow, int numberOfRows,
	           String sessionId) {
		
		 log.info("appAccessManager:invokeLocal");
		 try{
			 String orderQueryAppAccess=null;
			 log.info("appAccessManager:invokeLocal");
			 
			 AppAccessStateHolder appAccessStateHolder = (AppAccessStateHolder)
					  Component.getInstance("appAccessStateHolder",ScopeType.SESSION);
			 Map<String, String> filterMapAppAccess = appAccessStateHolder.getColumnFilterValues();
			 String stAppAccess=null;
			  
			 if("list".equals(type)){
				 log.info("appAccess:invokeLocal:list:01");
				 
				 Set<Map.Entry<String, String>> set = appAccessStateHolder.getSortOrders().entrySet();
                 for (Map.Entry<String, String> me : set) {
      		       
      		       if(orderQueryAppAccess==null){
      		    	 orderQueryAppAccess="order by "+me.getKey()+" "+me.getValue();
      		       }else{
      		    	 orderQueryAppAccess=orderQueryAppAccess+", "+me.getKey()+" "+me.getValue();  
      		       }
      		     }
                 log.info("appAccess:invokeLocal:list:orderQuery:"+orderQueryAppAccess);
                 
                 if(filterMapAppAccess!=null){
    	    		 Set<Map.Entry<String, String>> setFilterappAccess = filterMapAppAccess.entrySet();
    	              for (Map.Entry<String, String> me : setFilterappAccess) {
    	            	  log.info("me.getKey+:"+me.getKey());
    	            	  log.info("me.getValue:"+me.getValue());
    	   		      
    	   		     if("t1_crt_date".equals(me.getKey())){  
    	        	   
    	        	   //делаем фильтр на начало  
    	        	     stAppAccess=(stAppAccess!=null?stAppAccess+" and " :"")+" lower(to_char("+me.getKey()+",'DD.MM.YY HH24:MI:SS')) like lower('"+me.getValue()+"%') ";
    	    	   
    	   		     }else if("t1_iogv_bind_type".equals(me.getKey())&&(me.getValue()!=null && "-2".equals(me.getValue()))){
    	    	    	 
    	    	    	 stAppAccess=(stAppAccess!=null?stAppAccess+" and " :"")+" t1_usr_code is null ";
    	    	    	 
    	    	     }else{
    	        		//делаем фильтр на начало
    	            	  stAppAccess=(stAppAccess!=null?stAppAccess+" and " :"")+" lower("+me.getKey()+") like lower('"+me.getValue()+"%') ";
    	        	  }
    	              }
    	    	   }
                 log.info("appAccess:invokeLocal:list:filterQuery:"+stAppAccess);

             
               List<Object[]> loAppAccess=null;
               AppAccessItem ui = null;
               DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm:ss");
               

             loAppAccess=entityManager.createNativeQuery(
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
             "from JOURN_APP_ACCESS_BSS_T jas, "+
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
                "and ARM.ID_SRV =JAS.UP_IS_APP "+
                "and au_APP.ID_SRV =JAS.UP_USER_APP "+
                
                "and au_APP.UP_SIGN=t03_APP.CL_ORG_CODE "+
                "and CL_ORG_app.ID_SRV=t03_APP.CL_ORG_ID "+
                
                "and AU_APP.UP_SIGN_USER=t02_APP.CL_USR_CODE(+) "+
                "and CL_USR_APP.ID_SRV(+)=t02_APP.CL_USR_ID "+
                
                "and substr(au_APP.UP_SIGN,1,5)||'000'=t04_APP.CL_dep_CODE(+) "+
                "and CL_dep_app.ID_SRV=t04_APP.CL_dep_ID "+
             ") t1 "+
              (stAppAccess!=null ? " where "+stAppAccess :" ")+
              (orderQueryAppAccess!=null ? orderQueryAppAccess+", t1_id desc " : " order by t1_id desc "))
              .setFirstResult(firstRow)
              .setMaxResults(numberOfRows)
              .getResultList();
               auditList = new ArrayList<BaseItem>();
               
               for(Object[] objectArray :loAppAccess){
            	   try{
            	     ui= new AppAccessItem(
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
            	   }catch(Exception erAppAccess){
            		   log.error("invokeLocal:for:error:"+erAppAccess);
            	   }
               }  
               
             log.info("invokeLocal:list:02");
             
			 } else if("count".equals(type)){
				 log.info("AppAccess:count:01");
				 
                 
                 if(filterMapAppAccess!=null){
    	    		 Set<Map.Entry<String, String>> setFilterAppAdmin = filterMapAppAccess.entrySet();
    	              for (Map.Entry<String, String> me : setFilterAppAdmin) {
    	            		  
    	              if("t1_iogv_bind_type".equals(me.getKey())&&(me.getValue()!=null && "-2".equals(me.getValue()))){
     	    	    	 stAppAccess=(stAppAccess!=null?stAppAccess+" and " :"")+" t1_usr_code is null ";
    	              }else{
    	            	 stAppAccess=(stAppAccess!=null?stAppAccess+" and " :"")+" lower("+me.getKey()+") like lower('"+me.getValue()+"%') ";
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
						               "from JOURN_APP_ACCESS_BSS_T jas, "+
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
						                  "and ARM.ID_SRV =JAS.UP_IS_APP "+
						                  "and au_APP.ID_SRV =JAS.UP_USER_APP "+
						                  
						                  "and au_APP.UP_SIGN=t03_APP.CL_ORG_CODE "+
						                  "and CL_ORG_app.ID_SRV=t03_APP.CL_ORG_ID "+
						                  
						                  "and AU_APP.UP_SIGN_USER=t02_APP.CL_USR_CODE(+) "+
						                  "and CL_USR_APP.ID_SRV(+)=t02_APP.CL_USR_ID "+
						                  
						                  "and substr(au_APP.UP_SIGN,1,5)||'000'=t04_APP.CL_dep_CODE(+) "+
						                  "and CL_dep_app.ID_SRV=t04_APP.CL_dep_ID "+
						               ") t1 "+
		         (stAppAccess!=null ? " where "+stAppAccess :" "))
               .getSingleResult()).longValue();
                 
                 
               log.info("AppAccess:invokeLocal:count:02:"+auditCount);
           	 }
		}catch(Exception eAppAccess){
			  log.error("AppAccess:invokeLocal:error:"+eAppAccess);
			  evaluteForList=false;
			  FacesMessages.instance().add("ќшибка!");
		}

	}
	
	 private AppAccessItem getUserItem(Long idUser){
		 if(idUser==null){
			  return null;
		   }
		   
		   try{
	           List<Object[]> lo=null;
	           AppAccessItem ui = null;
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
	        		             "from JOURN_APP_ACCESS_BSS_T jas, "+
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
	        		                "and ARM.ID_SRV =JAS.UP_IS_APP "+
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
	        		   log.info("AppAccessManager:getUserItem:login:"+objectArray[1].toString());
	        		   
	        		   ui= new AppAccessItem(
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
	 
	 
	 public void createAccess(){
		 
		// MODE_EXEC: 0 Ц УREPLACEФ, 1 - УADDФ, 2 Ц УREMOVEФ 
		 
		   log.info("AppAccessManager:createAccess:01");
		  
		   String sessionIdAppAccess = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	       log.info("AppAccessManager:createAccess:sessionId:"+sessionIdAppAccess);
	     
	       Long idApp=null;
	       Long idUser=null;
	       Long idArm=null;
	       int modeExec=1;
	       
	       String rolesLine=null; 
	       
		   try{
			 
			 if(sessionIdAppAccess==null){
				  return;
			 }
			   
			 idApp =  new Long(sessionIdAppAccess); 
			   
			 Object[] app=(Object[]) entityManager.createNativeQuery(
		    			  "select JAS.UP_USER_APP, JAS.UP_IS_APP, JAS.MODE_EXEC "+
                          "from JOURN_APP_ACCESS_BSS_T jas "+
                          "where  JAS.ID_SRV=? ")
		    			.setParameter(1, idApp)
		    			.getSingleResult();  
			 
			 idUser=new Long(app[0].toString());
			 idArm=new Long(app[1].toString());
			 modeExec=Integer.parseInt(app[2].toString());
			 
			 log.info("AppAccessManager:createAccess:idUser:"+idUser);
			 log.info("AppAccessManager:createAccess:idArm:"+idArm);
			 log.info("AppAccessManager:createAccess:modeExec:"+modeExec);
			 
			 //список ролей, которые надо назначить пользователю по за€вке
			 List<String> roles_app=(List<String>) entityManager.createNativeQuery(
	    			  "select to_char(RL.ID_SRV) "+
                       "from JOURN_APP_ACCESS_BSS_T jas, "+
                       "ROLES_APP_ACCESS_BSS_T rlapp, "+
                       "AC_ROLES_BSS_T rl "+
                       "where RLAPP.UP_APP_ACCESS=JAS.ID_SRV "+
                       "and RL.ID_SRV=RLAPP.UP_ROLE "+
                       "and JAS.ID_SRV=? "+
                       "order by RL.FULL_ ")
	    			.setParameter(1, idApp)
	    			.getResultList();
			 
			 //список ролей, которые уже есть у пользовател€ в системе 
			 List<String> roles_user=(List<String>)entityManager.createNativeQuery(
	    			  "select to_char(URL.UP_ROLES) "+
                      "from AC_ROLES_BSS_T rl, "+
                      "AC_USERS_LINK_KNL_T url "+
                      "where RL.ID_SRV=URL.UP_ROLES "+
                      "and RL.UP_IS=? "+
                      "and URL.UP_USERS=? ")
	    			.setParameter(1, idArm)
	    			.setParameter(2, idUser)
	    			.getResultList();
			
			 if(modeExec==0){ //REPLACE
				 
				 //удал€ем имеющиес€ у пользовател€ роли в системе
				 for(String role :roles_user){
					 if(rolesLine==null){
						 rolesLine=role;
					 }else{
						 rolesLine+=", "+role;
					 }
						  
				 }
				 
				 log.info("AppAccessManager:createAccess:rolesLine:"+rolesLine);
				 
				 entityManager.createNativeQuery(
		 	     		 "DELETE FROM AC_USERS_LINK_KNL_T url "+
                         "WHERE URL.UP_ROLES in ("+rolesLine+") "+
                         "and URL.UP_USERS= ? ")
		 	     		 .setParameter(1, idUser)
		 	     		 .executeUpdate();
				 
				 //назначаем роли из за€вки
				 for(String role :roles_app){
					   
						   entityManager.createNativeQuery(
				 	     		   "insert into AC_USERS_LINK_KNL_T (UP_ROLES, UP_USERS, CREATOR, CREATED) "+
		                           "values(?, ?, ?, sysdate) ")
				 	     		 .setParameter(1, new Long(role))
				 	     		 .setParameter(2, idUser)
				 	     		 .setParameter(3, getCurrentUser().getIdUser())
				         	 	 .executeUpdate();
				  }
				 
			 }else if(modeExec==1){ //ADD
			 
			  for(String role :roles_app){
				   
				   if(!roles_user.contains(role)){
					   
					   entityManager.createNativeQuery(
			 	     		   "insert into AC_USERS_LINK_KNL_T (UP_ROLES, UP_USERS, CREATOR, CREATED) "+
	                           "values(?, ?, ?, sysdate) ")
			 	     		 .setParameter(1, new Long(role))
			 	     		 .setParameter(2, idUser)
			 	     		 .setParameter(3, getCurrentUser().getIdUser())
			         	 	 .executeUpdate();
				   }
			  }
	
			 }else if(modeExec==2){  //REMOVE
				//удал€ем роли из за€вки
				 for(String role :roles_app){
					   
					 if(rolesLine==null){
						 rolesLine=role;
					 }else{
						 rolesLine+=", "+role;
					 }
				  }
				 
				 entityManager.createNativeQuery(
		 	     		 "DELETE FROM AC_USERS_LINK_KNL_T url "+
                         "WHERE URL.UP_ROLES in ("+rolesLine+") "+
                         "and URL.UP_USERS= ? ")
		 	     		 .setParameter(1, idUser)
		 	     		 .executeUpdate();
			 }else{
				 return;
			 }
			   
			   
		     entityManager.createNativeQuery(
	 	     		   "update JOURN_APP_ACCESS_BSS_T t1 " +
	 	     		   "set t1.STATUS=1, t1.UP_USER_EXEC=? " +
	 	     		   "where t1.ID_SRV=? ")
	 	     		 .setParameter(1, getCurrentUser().getBaseId())
	 	     		 .setParameter(2, idApp)
	         	 	 .executeUpdate();
		    
		     AppAccessItem ui = getUserItem(new Long(sessionIdAppAccess));
		     
		     Contexts.getEventContext().set("contextBeanView", ui);
		     
		   }catch(Exception eAppAccess){
			   log.error("AppAccessManager:createAccess:error:"+eAppAccess);
		   }
	 }
	 
	 public void reject(){
		   log.info("AppAccessManager:reject:01");
		  
		   String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	       log.info("AppAccessManager:reject:sessionId:"+sessionId);
	     
		   try{
			   
		     entityManager.createNativeQuery(
	 	     		   "update JOURN_APP_ACCESS_BSS_T t1 " +
	 	     		   "set t1.STATUS=2,  t1.REJECT_REASON=?, t1.UP_USER_EXEC=? " +
	 	     		   "where t1.ID_SRV=? ")
	 	     		 .setParameter(1, this.rejectReason)
	 	     		 .setParameter(2, getCurrentUser().getBaseId())
	 	     		 .setParameter(3, new Long(sessionId))
	 	     	 	 .executeUpdate();
		     
             AppAccessItem ui = getUserItem(new Long(sessionId)); 
		     
		     Contexts.getEventContext().set("contextBeanView", ui);
		     
		   }catch(Exception e){
			   log.error("AppAccessManager:reject:error:"+e);
		   }
	 }
	 
	 public void comment(){
		   log.info("AppAccessManager:comment:01");
		  
		   String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	       log.info("AppAccessManager:comment:sessionId:"+sessionId);
	     
		   try{
			   
		     entityManager.createNativeQuery(
	 	     		   "update JOURN_APP_ACCESS_BSS_T t1 " +
	 	     		   "set t1.COMMENT_=? " +
	 	     		   "where t1.ID_SRV=? ")
	 	     		 .setParameter(1, this.commentText)
	 	     		 .setParameter(2, new Long(sessionId))
	 	     	 	 .executeUpdate();
		     
           AppAccessItem ui = getUserItem(new Long(sessionId)); 
		     
		   Contexts.getEventContext().set("contextBeanView", ui);
		     
		   }catch(Exception e){
			   log.error("AppAccessManager:reject:error:"+e);
		   }
	 }
	 
	 public void forViewCrt() {
		   try{
			 String sessionId = FacesContext.getCurrentInstance().getExternalContext()
				        .getRequestParameterMap()
				        .get("sessionId");
		     log.info("AppAccessManager:forViewCrt:sessionId:"+sessionId);
		     
		     AppAccessItem ui = getUserItem(new Long(sessionId));
	    		
	         Contexts.getEventContext().set("contextBeanView", ui);
		     
		   }catch(Exception e){
			 log.error("AppAccessManager:forViewCrt:Error:"+e);
		   }
	   } 
	 
	 public void forViewUpdDel() {
		 try{
		     String sessionId = FacesContext.getCurrentInstance().getExternalContext()
				        .getRequestParameterMap()
				        .get("sessionId");
		     log.info("AppAccessManager:forViewUpdDel:sessionId:"+sessionId);
		     if(sessionId!=null){
		    	
		     	 
		    	 AppAccessItem ui = getUserItem(new Long(sessionId));
		        	 
		   	 Contexts.getEventContext().set("appAccessBean", ui);
		     }
		   }catch(Exception e){
			   log.error("AppAccessManager:forViewUpdDel:Error:"+e);
		   }
	 }
	 
	 public void forViewComment() {
		   
		   try{
			     String sessionId = FacesContext.getCurrentInstance().getExternalContext()
					        .getRequestParameterMap()
					        .get("sessionId");
			     log.info("AppAccessManager:forViewComment:sessionId:"+sessionId);
			     if(sessionId!=null){
			    	
			     	 
			    	 AppAccessItem ui = getUserItem(new Long(sessionId));
			    	 
			    	 this.commentText=ui.getComment();

	     }
			}catch(Exception e){
				   log.error("AppAccessManager:forViewComment:Error:"+e);
			}
	   } 
	 
	 
	 public List<AcRole> getListRolesAppForView() throws Exception{
		    log.info("AppAccessManager:getRolesAppForView:01");
		   
		    List<Object[]> lo=null;
		    AcRole role = null;
		   		    
		    String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
		    
		    log.info("AppAccessManager:getRolesAppForView:sessionId:"+sessionId);
		    
		    try {
		    	
		    	if(listRolesAppForView==null && sessionId!=null){
		      	
		    		lo=entityManager.createNativeQuery(
		    				"select RL.ID_SRV,  RL.SIGN_OBJECT, RL.FULL_, RL.DESCRIPTION "+
                            "from JOURN_APP_ACCESS_BSS_T jas, "+
                            "ROLES_APP_ACCESS_BSS_T rlapp, "+
                            "AC_ROLES_BSS_T rl "+
                            "where RLAPP.UP_APP_ACCESS=JAS.ID_SRV "+
                            "and RL.ID_SRV=RLAPP.UP_ROLE "+
                            "and JAS.ID_SRV=? "+
	                        "order by RL.FULL_ ")
		    				.setParameter(1, new Long(sessionId))
		    				.getResultList();
		    		
		    		listRolesAppForView = new ArrayList<AcRole>();
		    		
		    		for(Object[] objectArray :lo){
		    			 
		    			 role=new AcRole();
		    			 role.setIdRol(new Long(objectArray[0].toString()));
		    			 role.setSign(objectArray[1]!=null?objectArray[1].toString():"");
		    			 role.setRoleTitle(objectArray[2]!=null?objectArray[2].toString():"");
		    			 role.setRoleDescription(objectArray[3]!=null?objectArray[3].toString():"");
		    			 
		    			 listRolesAppForView.add(role);
		    		 }
		    	   }
				} catch (Exception e) {
		    	 log.error("AppAccessManager:getRolesAppForView:ERROR:"+e);
		         throw e;
		     }
		    return listRolesAppForView;
	 }
	
	 public String getCommentText(){
		 return this.commentText;
	 }
	 public void setCommentText(String commentText){
		 this.commentText=commentText;
	 }
	 
	public void setListRolesAppForView(List<AcRole> listRolesAppForView) {
			this.listRolesAppForView = listRolesAppForView;
	}
		
	
	 
	 public List <BaseTableItem> getAuditItemsListSelect() {
		   log.info("getAuditItemsListSelect:01");
		   AppAccessContext acAppAccess= new AppAccessContext();
		   if( auditItemsListSelect==null){
			   log.info("getAuditItemsListSelect:02");
			   auditItemsListSelect = new ArrayList<BaseTableItem>();
			   
			   auditItemsListSelect.add(acAppAccess.getAuditItemsMap().get("idApp"));
			   auditItemsListSelect.add(acAppAccess.getAuditItemsMap().get("created"));
			   auditItemsListSelect.add(acAppAccess.getAuditItemsMap().get("orgName"));
			  
			   auditItemsListSelect.add(acAppAccess.getAuditItemsMap().get("statusValue"));
		   }
	       return this.auditItemsListSelect;
  }
  
	 public String getRejectReason(){
		 return this.rejectReason;
	 }
	 public void setRejectReason(String rejectReason){
		 this.rejectReason=rejectReason;
	 }
  
  public List <BaseTableItem> getAuditItemsListContext() {
	   log.info("AppAccessManager:getAuditItemsListContext");
	   if(auditItemsListContext==null){
		   AppAccessContext acAppAccess= new AppAccessContext();
		  
		   
		   
		   auditItemsListContext=acAppAccess.getAuditItemsCollection();
		   
	   }
	   return this.auditItemsListContext;
  }
  
  public List<HeaderTableItem> getHeaderItemsListContext() {
	  
	  if(headerItemsListContext==null){
		   AppAccessContext acAppAccess= new AppAccessContext();
			   headerItemsListContext=acAppAccess.getHeaderItemsList();
		   
	   }
	
	   return this.headerItemsListContext;
  }

 public List<HeaderTableItem> getHeaderItemsListContext(String ids) {
	  
	 	AppAccessContext acAppAccess= new AppAccessContext();
		
	 	if(ids!=null) {
	 		
	 	
	 		headerItemsListContext=new ArrayList<HeaderTableItem>();
	 				
	 	    
	 	
	 	     List<String> idsList =  Arrays.asList(ids.split(","));
	 	   
	    	for(HeaderTableItem hti :acAppAccess.getHeaderItemsList()){
			
			 
			
			  if(idsList.contains(hti.getItemField())){
				  headerItemsListContext.add(hti);
			  }
			  
		   }

	 	}
	   return this.headerItemsListContext;
  }


}
