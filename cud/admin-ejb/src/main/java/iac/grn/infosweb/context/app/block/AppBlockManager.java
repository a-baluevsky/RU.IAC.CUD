package iac.grn.infosweb.context.app.block;

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





import iac.cud.infosweb.dataitems.AppBlockItem;
import iac.cud.infosweb.dataitems.BaseItem;
import iac.grn.infosweb.context.app.access.AppAccessContext;
import iac.grn.infosweb.session.audit.actions.ActionsMap;
import iac.grn.infosweb.session.audit.actions.ResourcesMap;
import iac.grn.infosweb.session.table.BaseManager;
import iac.grn.serviceitems.BaseTableItem;
import iac.grn.serviceitems.HeaderTableItem;

@Name("appBlockManager")
 public class AppBlockManager extends BaseManager{

	
	private String rejectReason;
	private String commentText;
	
	public void invokeLocal(String type, int firstRow, int numberOfRows,
	           String sessionId) {
		
		 log.info("appBlockManager:invokeLocal");
		 try{
			 String orderQueryAppBlock=null;
			 log.info("appBlockManager:invokeLocal");
			 
			 AppBlockStateHolder appBlockStateHolder = (AppBlockStateHolder)
					  Component.getInstance("appBlockStateHolder",ScopeType.SESSION);
			 Map<String, String> filterMapBlock = appBlockStateHolder.getColumnFilterValues();
			 String st=null;
			  
			 if("list".equals(type)){
				 log.info("appBlockManager:invokeLocal:list:01");
				 
				 Set<Map.Entry<String, String>> set = appBlockStateHolder.getSortOrders().entrySet();
                 for (Map.Entry<String, String> me : set) {
      		       log.info("me.getKey+:"+me.getKey());
      		       log.info("me.getValue:"+me.getValue());
      		       
      		       if(orderQueryAppBlock==null){
      		    	 orderQueryAppBlock="order by "+me.getKey()+" "+me.getValue();
      		       }else{
      		    	 orderQueryAppBlock=orderQueryAppBlock+", "+me.getKey()+" "+me.getValue();  
      		       }
      		     }
                 log.info("appBlockManager:invokeLocal:list:orderQuery:"+orderQueryAppBlock);
                 
                 if(filterMapBlock!=null){
    	    		 Set<Map.Entry<String, String>> setFilterAppBlock = filterMapBlock.entrySet();
    	              for (Map.Entry<String, String> me : setFilterAppBlock) {
    	            	
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
                 log.info("appBlockManager:invokeLocal:list:filterQuery:"+st);

             
               List<Object[]> loBlock=null;
               AppBlockItem uiBlock = null;
               DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm:ss");
               

             loBlock=entityManager.createNativeQuery(
             "select t1.t1_id, t1.t1_created, "+
             "t1.t1_status, t1_org_name,  t1_user_fio, t1_reject_reason, t1_comment, "+
             "t1_org_name_app, t1_user_id_app,  t1_user_login_app, t1_user_fio_app, t1_user_pos_app, "+
             "t1_dep_name_app, "+
             "t1_MODE_EXEC,  t1_BLOCK_REASON "+
              "from(  "+
             "select JAS.ID_SRV t1_id, JAS.CREATED t1_created, " +
             "JAS.STATUS t1_status,  CL_ORG_FULL.FULL_ t1_org_name, " +
             "JAS.COMMENT_ t1_comment, " +
              "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO ) t1_user_fio, " +
              "JAS.REJECT_REASON t1_reject_reason,  " +
                            
              "AU_APP.ID_SRV  t1_user_id_app, AU_APP.LOGIN  t1_user_login_app, " +
               "CL_ORG_app.FULL_ t1_org_name_app,  decode(AU_app.UP_SIGN_USER, null, AU_app.SURNAME||' '||AU_app.NAME_ ||' '|| AU_app.PATRONYMIC,  CL_USR_app.FIO ) t1_user_fio_app, " +
                 "decode(AU_app.UP_SIGN_USER, null, AU_app.POSITION, CL_USR_app.POSITION) t1_user_pos_app, " +
                 
                 "decode(AU_app.UP_SIGN_USER, null, AU_app.DEPARTMENT, decode(substr(CL_DEP_app.sign_object,4,2), '00', null, CL_DEP_app.FULL_)) t1_dep_name_app, " +
                "JAS.MODE_EXEC t1_MODE_EXEC, JAS.BLOCK_REASON t1_BLOCK_REASON " +
              "from JOURN_APP_BLOCK_BSS_T jas, " +
               "AC_USERS_KNL_T au_FULL, " +  
                "ISP_BSS_T cl_org_full, " +
                 "ISP_BSS_T cl_usr_full, " +
                 "ISP_BSS_T cl_org_app, " +
                 "ISP_BSS_T cl_usr_app, " +
                 "ISP_BSS_T cl_dep_app, " +
                 "AC_USERS_KNL_T au_APP, " +
              "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE  " +
                "from ISP_BSS_T cl_org " +
                "where  CL_ORG.SIGN_OBJECT LIKE '%00000' " +
                "group by CL_ORG.SIGN_OBJECT) t03, " +
                 "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE  " +
                            "from ISP_BSS_T cl_usr " +
                            "where CL_USR.FIO is not null " +
                            "group by CL_usr.SIGN_OBJECT) t02,  " + 
                
                 "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE  " +
                "from ISP_BSS_T cl_org " +
                "where  CL_ORG.SIGN_OBJECT LIKE '%00000' " +
                "group by CL_ORG.SIGN_OBJECT) t03_app, " +
                 "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE " + 
                            "from ISP_BSS_T cl_usr " +
                            "where CL_USR.FIO is not null " +
                            "group by CL_usr.SIGN_OBJECT) t02_app, " +  
               "(select max(CL_dep.ID_SRV) CL_DEP_ID,  CL_DEP.SIGN_OBJECT  CL_DEP_CODE  " +
                            "from ISP_BSS_T cl_dep " +
                            "where CL_dep.SIGN_OBJECT LIKE '%000' " +
                            "group by CL_DEP.SIGN_OBJECT) t04_app " +
                                      
                "where JAS.UP_USER=AU_FULL.ID_SRV " +
                "and AU_FULL.UP_SIGN=t03.CL_ORG_CODE " +
                "and CL_ORG_FULL.ID_SRV=t03.CL_ORG_ID " +
                "and AU_FULL.UP_SIGN_USER=t02.CL_USR_CODE(+) " +
                "and CL_USR_FULL.ID_SRV(+)=t02.CL_USR_ID " +
                 "and au_APP.ID_SRV =JAS.UP_USER_APP " +
                
                "and au_APP.UP_SIGN=t03_APP.CL_ORG_CODE " +
                "and CL_ORG_app.ID_SRV=t03_APP.CL_ORG_ID " +
                
                "and AU_APP.UP_SIGN_USER=t02_APP.CL_USR_CODE(+) " +
                "and CL_USR_APP.ID_SRV(+)=t02_APP.CL_USR_ID " +
                
                "and substr(au_APP.UP_SIGN,1,5)||'000'=t04_APP.CL_dep_CODE(+) " +
                "and CL_dep_app.ID_SRV=t04_APP.CL_dep_ID " +
              ") t1 "+
              (st!=null ? " where "+st :" where t1_MODE_EXEC=0 ")+
              (orderQueryAppBlock!=null ? orderQueryAppBlock+", t1_id desc " : " order by t1_id desc "))
              .setFirstResult(firstRow)
              .setMaxResults(numberOfRows)
              .getResultList();
               auditList = new ArrayList<BaseItem>();
               
               for(Object[] objectArrayBlock :loBlock){
            	   try{
            	     uiBlock= new AppBlockItem(
            	    		objectArrayBlock[0]!=null?new Long(objectArrayBlock[0].toString()):null,
            				objectArrayBlock[1]!=null?df.format((Date)objectArrayBlock[1]) :"",
            				objectArrayBlock[2]!=null?Integer.parseInt(objectArrayBlock[2].toString()):0,	
            				objectArrayBlock[3]!=null?objectArrayBlock[3].toString():"",
            				objectArrayBlock[4]!=null?objectArrayBlock[4].toString():"",
            				objectArrayBlock[5]!=null?objectArrayBlock[5].toString():"",
            				objectArrayBlock[6]!=null?objectArrayBlock[6].toString():"",
            				 
            				objectArrayBlock[7]!=null?objectArrayBlock[7].toString():"",
	            			 
	            			objectArrayBlock[8]!=null?new Long(objectArrayBlock[8].toString()):null,
	            			 
	            			objectArrayBlock[9]!=null?objectArrayBlock[9].toString():"",
	            			objectArrayBlock[10]!=null?objectArrayBlock[10].toString():"",
	            			objectArrayBlock[11]!=null?objectArrayBlock[11].toString():"",
	            			 
	            			objectArrayBlock[12]!=null?objectArrayBlock[12].toString():"",
	            			objectArrayBlock[13]!=null?Integer.parseInt(objectArrayBlock[13].toString()):1,
	            			objectArrayBlock[14]!=null?objectArrayBlock[14].toString():""
	            			 );
            	     auditList.add(uiBlock);
            	   }catch(Exception eBlock){
            		   log.error("invokeLocal:for:error:"+eBlock);
            	   }
               }  
               
             log.info("AppBlock:invokeLocal:list:02");
             
			 } else if("count".equals(type)){
				 log.info("AppBlock:count:01");
				 
                 
                 if(filterMapBlock!=null){
    	    		 Set<Map.Entry<String, String>> setFilterAppBlock = filterMapBlock.entrySet();
    	              for (Map.Entry<String, String> me : setFilterAppBlock) {
    	              	  
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
						               "select JAS.ID_SRV t1_id, JAS.CREATED t1_created, " +
						               "JAS.STATUS t1_status,  CL_ORG_FULL.FULL_ t1_org_name, " +
						               "JAS.COMMENT_ t1_comment, " +
						                "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO ) t1_user_fio, " +
						                "JAS.REJECT_REASON t1_reject_reason,  " +
						                              
						                "AU_APP.ID_SRV  t1_user_id_app, AU_APP.LOGIN  t1_user_login_app, " +
						                 "CL_ORG_app.FULL_ t1_org_name_app,  decode(AU_app.UP_SIGN_USER, null, AU_app.SURNAME||' '||AU_app.NAME_ ||' '|| AU_app.PATRONYMIC,  CL_USR_app.FIO ) t1_user_fio_app, " +
						                   "decode(AU_app.UP_SIGN_USER, null, AU_app.POSITION, CL_USR_app.POSITION) t1_user_pos_app, " +
						                   
						                   "decode(AU_app.UP_SIGN_USER, null, AU_app.DEPARTMENT, decode(substr(CL_DEP_app.sign_object,4,2), '00', null, CL_DEP_app.FULL_)) t1_dep_name_app, " +
						                  "JAS.MODE_EXEC t1_MODE_EXEC, JAS.BLOCK_REASON t1_BLOCK_REASON " +
						                "from JOURN_APP_BLOCK_BSS_T jas, " +
						                 "AC_USERS_KNL_T au_FULL, " +  
						                  "ISP_BSS_T cl_org_full, " +
						                   "ISP_BSS_T cl_usr_full, " +
						                   "ISP_BSS_T cl_org_app, " +
						                   "ISP_BSS_T cl_usr_app, " +
						                   "ISP_BSS_T cl_dep_app, " +
						                   "AC_USERS_KNL_T au_APP, " +
						                "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE  " +
						                  "from ISP_BSS_T cl_org " +
						                  "where  CL_ORG.SIGN_OBJECT LIKE '%00000' " +
						                  "group by CL_ORG.SIGN_OBJECT) t03, " +
						                   "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE  " +
						                              "from ISP_BSS_T cl_usr " +
						                              "where CL_USR.FIO is not null " +
						                              "group by CL_usr.SIGN_OBJECT) t02,  " + 
						                  
						                   "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE  " +
						                  "from ISP_BSS_T cl_org " +
						                  "where  CL_ORG.SIGN_OBJECT LIKE '%00000' " +
						                  "group by CL_ORG.SIGN_OBJECT) t03_app, " +
						                   "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE " + 
						                              "from ISP_BSS_T cl_usr " +
						                              "where CL_USR.FIO is not null " +
						                              "group by CL_usr.SIGN_OBJECT) t02_app, " +  
						                 "(select max(CL_dep.ID_SRV) CL_DEP_ID,  CL_DEP.SIGN_OBJECT  CL_DEP_CODE  " +
						                              "from ISP_BSS_T cl_dep " +
						                              "where CL_dep.SIGN_OBJECT LIKE '%000' " +
						                              "group by CL_DEP.SIGN_OBJECT) t04_app " +
						                                        
						                  "where JAS.UP_USER=AU_FULL.ID_SRV " +
						                  "and AU_FULL.UP_SIGN=t03.CL_ORG_CODE " +
						                  "and CL_ORG_FULL.ID_SRV=t03.CL_ORG_ID " +
						                  "and AU_FULL.UP_SIGN_USER=t02.CL_USR_CODE(+) " +
						                  "and CL_USR_FULL.ID_SRV(+)=t02.CL_USR_ID " +
						                   "and au_APP.ID_SRV =JAS.UP_USER_APP " +
						                  
						                  "and au_APP.UP_SIGN=t03_APP.CL_ORG_CODE " +
						                  "and CL_ORG_app.ID_SRV=t03_APP.CL_ORG_ID " +
						                  
						                  "and AU_APP.UP_SIGN_USER=t02_APP.CL_USR_CODE(+) " +
						                  "and CL_USR_APP.ID_SRV(+)=t02_APP.CL_USR_ID " +
						                  
						                  "and substr(au_APP.UP_SIGN,1,5)||'000'=t04_APP.CL_dep_CODE(+) " +
						                  "and CL_dep_app.ID_SRV=t04_APP.CL_dep_ID " +
						                ") t1 "+
		         (st!=null ? " where "+st :" where t1_MODE_EXEC=0 "))
               .getSingleResult()).longValue();
                 
                 
               log.info("AppBlock:invokeLocal:count:02:"+auditCount);
           	 } 
		}catch(Exception eBlock){
			  log.error("AppBlock:invokeLocal:error:"+eBlock);
			  evaluteForList=false;
			  FacesMessages.instance().add("ќшибка!");
		}

	}
	
	 private AppBlockItem getUserItem(Long idUser){
		 if(idUser==null){
			  return null;
		   }
		   
		   try{
	           List<Object[]> lo=null;
	           AppBlockItem ui = null;
	           DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm:ss");
	           
	           lo=entityManager.createNativeQuery(
	                   "select t1.t1_id, t1.t1_created, "+
	                           "t1.t1_status, t1_org_name,  t1_user_fio, t1_reject_reason, t1_comment, "+
	                           "t1_org_name_app, t1_user_id_app,  t1_user_login_app, t1_user_fio_app, t1_user_pos_app, "+
	                           "t1_dep_name_app, "+
	                           "t1_MODE_EXEC,  t1_BLOCK_REASON "+
	                            "from(  "+
	                           "select JAS.ID_SRV t1_id, JAS.CREATED t1_created, " +
	                           "JAS.STATUS t1_status,  CL_ORG_FULL.FULL_ t1_org_name, " +
	                           "JAS.COMMENT_ t1_comment, " +
	                            "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO ) t1_user_fio, " +
	                            "JAS.REJECT_REASON t1_reject_reason,  " +
	                                          
	                            "AU_APP.ID_SRV  t1_user_id_app, AU_APP.LOGIN  t1_user_login_app, " +
	                             "CL_ORG_app.FULL_ t1_org_name_app,  decode(AU_app.UP_SIGN_USER, null, AU_app.SURNAME||' '||AU_app.NAME_ ||' '|| AU_app.PATRONYMIC,  CL_USR_app.FIO ) t1_user_fio_app, " +
	                               "decode(AU_app.UP_SIGN_USER, null, AU_app.POSITION, CL_USR_app.POSITION) t1_user_pos_app, " +
	                               
	                               "decode(AU_app.UP_SIGN_USER, null, AU_app.DEPARTMENT, decode(substr(CL_DEP_app.sign_object,4,2), '00', null, CL_DEP_app.FULL_)) t1_dep_name_app, " +
	                              "JAS.MODE_EXEC t1_MODE_EXEC, JAS.BLOCK_REASON t1_BLOCK_REASON " +
	                            "from JOURN_APP_BLOCK_BSS_T jas, " +
	                             "AC_USERS_KNL_T au_FULL, " +  
	                              "ISP_BSS_T cl_org_full, " +
	                               "ISP_BSS_T cl_usr_full, " +
	                               "ISP_BSS_T cl_org_app, " +
	                               "ISP_BSS_T cl_usr_app, " +
	                               "ISP_BSS_T cl_dep_app, " +
	                               "AC_USERS_KNL_T au_APP, " +
	                            "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE  " +
	                              "from ISP_BSS_T cl_org " +
	                              "where  CL_ORG.SIGN_OBJECT LIKE '%00000' " +
	                              "group by CL_ORG.SIGN_OBJECT) t03, " +
	                               "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE  " +
	                                          "from ISP_BSS_T cl_usr " +
	                                          "where CL_USR.FIO is not null " +
	                                          "group by CL_usr.SIGN_OBJECT) t02,  " + 
	                              
	                               "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE  " +
	                              "from ISP_BSS_T cl_org " +
	                              "where  CL_ORG.SIGN_OBJECT LIKE '%00000' " +
	                              "group by CL_ORG.SIGN_OBJECT) t03_app, " +
	                               "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE " + 
	                                          "from ISP_BSS_T cl_usr " +
	                                          "where CL_USR.FIO is not null " +
	                                          "group by CL_usr.SIGN_OBJECT) t02_app, " +  
	                             "(select max(CL_dep.ID_SRV) CL_DEP_ID,  CL_DEP.SIGN_OBJECT  CL_DEP_CODE  " +
	                                          "from ISP_BSS_T cl_dep " +
	                                          "where CL_dep.SIGN_OBJECT LIKE '%000' " +
	                                          "group by CL_DEP.SIGN_OBJECT) t04_app " +
	                                                    
	                              "where JAS.UP_USER=AU_FULL.ID_SRV " +
	                              "and AU_FULL.UP_SIGN=t03.CL_ORG_CODE " +
	                              "and CL_ORG_FULL.ID_SRV=t03.CL_ORG_ID " +
	                              "and AU_FULL.UP_SIGN_USER=t02.CL_USR_CODE(+) " +
	                              "and CL_USR_FULL.ID_SRV(+)=t02.CL_USR_ID " +
	                               "and au_APP.ID_SRV =JAS.UP_USER_APP " +
	                              
	                              "and au_APP.UP_SIGN=t03_APP.CL_ORG_CODE " +
	                              "and CL_ORG_app.ID_SRV=t03_APP.CL_ORG_ID " +
	                              
	                              "and AU_APP.UP_SIGN_USER=t02_APP.CL_USR_CODE(+) " +
	                              "and CL_USR_APP.ID_SRV(+)=t02_APP.CL_USR_ID " +
	                              
	                              "and substr(au_APP.UP_SIGN,1,5)||'000'=t04_APP.CL_dep_CODE(+) " +
	                              "and CL_dep_app.ID_SRV=t04_APP.CL_dep_ID " +
	                              "and JAS.ID_SRV=? "+
	        		             ") t1 ")
	         .setParameter(1, idUser)
	         .getResultList();
	           
	           for(Object[] objectArray :lo){
	        	   try{
	        		   log.info("AppBlockManager:getUserItem:login:"+objectArray[1].toString());
	        		   
	        		   ui= new AppBlockItem(
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
		            			objectArray[13]!=null?Integer.parseInt(objectArray[13].toString()):1,
		            			objectArray[14]!=null?objectArray[14].toString():""
	        				   
	        				   );
	        	     return ui;
	        	   }catch(Exception erBlock){
	        		   log.error("getUserItem:for:error:"+erBlock);
	        	   }
	           }  
		   }catch(Exception eBlock){
			   log.error("getUserItem:error:"+eBlock);
		   }
		   return null;
	 }
	 
	 
	 public void createBlock(){
		  
		// MODE_EXEC: 0 Ц УBLOCKФ, 1 - УUNBLOCKФ
		 
		   log.info("AppBlockManager:createBlock:01");
		  
		   String sessionIdBlock = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	       log.info("AppBlockManager:createAccess:sessionId:"+sessionIdBlock);
	     
	       Long idApp=null;
	       Long idUser=null;
	       int modeExec=0;
	       
	       try{
			 
			 if(sessionIdBlock==null){
				  return;
			 }
			   
			 idApp =  new Long(sessionIdBlock); 
			   
			 Object[] app=(Object[]) entityManager.createNativeQuery(
		    			  "select JAS.UP_USER_APP, JAS.MODE_EXEC "+
                          "from JOURN_APP_BLOCK_BSS_T jas "+
                          "where  JAS.ID_SRV=? ")
		    			.setParameter(1, idApp)
		    			.getSingleResult();  
			 
			 idUser=new Long(app[0].toString());
			 modeExec=Integer.parseInt(app[1].toString());
			 
			 log.info("AppAccessManager:createBlock:idUser:"+idUser);
			 log.info("AppAccessManager:createBlock:modeExec:"+modeExec);
			 
			 if(modeExec==0){ //BLOCK
				 
				 entityManager.createNativeQuery(
		 	     		 "update AC_USERS_KNL_T t1 " +
	 	     		     "set t1.STATUS=2 " +
	 	     		     "where t1.ID_SRV=? ")
		 	     		 .setParameter(1, idUser)
		 	     		 .executeUpdate();
				 
			 }else if(modeExec==1){ //UNBLOCK
			 
				 entityManager.createNativeQuery(
		 	     		 "update AC_USERS_KNL_T t1 " +
	 	     		     "set t1.STATUS=1 " +
	 	     		     "where t1.ID_SRV=? ")
		 	     		 .setParameter(1, idUser)
		 	     		 .executeUpdate();
	
			 }else{
				 return;
			 }
			   
			   
		     entityManager.createNativeQuery(
	 	     		   "update JOURN_APP_BLOCK_BSS_T t1 " +
	 	     		   "set t1.STATUS=1, t1.UP_USER_EXEC=? " +
	 	     		   "where t1.ID_SRV=? ")
	 	     		 .setParameter(1, getCurrentUser().getBaseId())
	 	     		 .setParameter(2, idApp)
	         	 	 .executeUpdate();
		    
		     AppBlockItem ui = getUserItem(new Long(sessionIdBlock));
		     
		     Contexts.getEventContext().set("contextBeanView", ui);
		     
		     audit(ResourcesMap.APP_USER_BLOCK, ActionsMap.EXECUTE);
		     
		   }catch(Exception e){
			   log.error("AppBlockManager:createBlock:error:"+e);
		   }
	 }
	 
	 public void reject(){
		   log.info("AppBlockManager:reject:01");
		  
		   String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	       log.info("AppBlockManager:reject:sessionId:"+sessionId);
	     
		   try{
			   
		     entityManager.createNativeQuery(
	 	     		   "update JOURN_APP_BLOCK_BSS_T t1 " +
	 	     		   "set t1.STATUS=2,  t1.REJECT_REASON=?, t1.UP_USER_EXEC=? " +
	 	     		   "where t1.ID_SRV=? ")
	 	     		 .setParameter(1, this.rejectReason)
	 	     		 .setParameter(2, getCurrentUser().getBaseId())
	 	     		 .setParameter(3, new Long(sessionId))
	 	     	 	 .executeUpdate();
		     
             AppBlockItem ui = getUserItem(new Long(sessionId)); 
		     
		     Contexts.getEventContext().set("contextBeanView", ui);
		     
		     audit(ResourcesMap.APP_USER_BLOCK, ActionsMap.REJECT);
		     
		   }catch(Exception e){
			   log.error("AppBlockManager:reject:error:"+e);
		   }
	 }
	 
	 public void comment(){
		   log.info("AppBlockManager:comment:01");
		  
		   String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	       log.info("AppBlockManager:comment:sessionId:"+sessionId);
	     
		   try{
			   
		     entityManager.createNativeQuery(
	 	     		   "update JOURN_APP_BLOCK_BSS_T t1 " +
	 	     		   "set t1.COMMENT_=? " +
	 	     		   "where t1.ID_SRV=? ")
	 	     		 .setParameter(1, this.commentText)
	 	     		 .setParameter(2, new Long(sessionId))
	 	     	 	 .executeUpdate();
		     
           AppBlockItem ui = getUserItem(new Long(sessionId)); 
		     
		   Contexts.getEventContext().set("contextBeanView", ui);
		     
		   
		   
		   }catch(Exception e){
			   log.error("AppBlockManager:reject:error:"+e);
		   }
	 }
	 
	 public void forViewCrt() {
		   try{
			 String sessionId = FacesContext.getCurrentInstance().getExternalContext()
				        .getRequestParameterMap()
				        .get("sessionId");
		     log.info("AppBlockManager:forViewCrt:sessionId:"+sessionId);
		     
		     AppBlockItem ui = getUserItem(new Long(sessionId));
	    		
	         Contexts.getEventContext().set("contextBeanView", ui);
		     
		   }catch(Exception e){
			 log.error("AppBlockManager:forViewCrt:Error:"+e);
		   }
	   } 
	 
	 public void forViewUpdDel() {
		 try{
		     String sessionId = FacesContext.getCurrentInstance().getExternalContext()
				        .getRequestParameterMap()
				        .get("sessionId");
		     log.info("AppBlockManager:forViewUpdDel:sessionId:"+sessionId);
		     if(sessionId!=null){
		    	
		     	 
		    	 AppBlockItem ui = getUserItem(new Long(sessionId));
		        	 
		   	 Contexts.getEventContext().set("appBlockBean", ui);
		     }
		   }catch(Exception e){
			   log.error("AppBlockManager:forViewUpdDel:Error:"+e);
		   }
	 }
	 
	 public void forViewComment() {
		   
		   try{
			     String sessionId = FacesContext.getCurrentInstance().getExternalContext()
					        .getRequestParameterMap()
					        .get("sessionId");
			     log.info("AppBlockManager:forViewComment:sessionId:"+sessionId);
			     if(sessionId!=null){
			    	
			     	 
			    	 AppBlockItem ui = getUserItem(new Long(sessionId));
			    	 
			    	 this.commentText=ui.getComment();

	     }
			}catch(Exception e){
				   log.error("AppBlockManager:forViewComment:Error:"+e);
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
		   AppBlockContext acBlock= new AppBlockContext();
		   if( auditItemsListSelect==null){
			   log.info("getAuditItemsListSelect:02");
			   auditItemsListSelect = new ArrayList<BaseTableItem>();
			   
			   auditItemsListSelect.add(acBlock.getAuditItemsMap().get("idApp"));
			   auditItemsListSelect.add(acBlock.getAuditItemsMap().get("created"));
			   auditItemsListSelect.add(acBlock.getAuditItemsMap().get("orgName"));
			  
			   auditItemsListSelect.add(acBlock.getAuditItemsMap().get("statusValue"));
		   }
	       return this.auditItemsListSelect;
  }
  

  
  public List <BaseTableItem> getAuditItemsListContext() {
	   log.info("AppBlockManager:getAuditItemsListContext");
	   if(auditItemsListContext==null){
		   AppBlockContext acBlock= new AppBlockContext();
		    
		   auditItemsListContext=acBlock.getAuditItemsCollection();
		   
	   }
	   return this.auditItemsListContext;
  }
  
  public List<HeaderTableItem> getHeaderItemsListContext() {
	  
	  if(headerItemsListContext==null){
		   AppBlockContext acBlock= new AppBlockContext();
			   headerItemsListContext=acBlock.getHeaderItemsList();
			   
	   }
	  
	   return this.headerItemsListContext;
  }
  
  public List<HeaderTableItem> getHeaderItemsListContext(String ids) {
	  
	 	AppAccessContext acBlock= new AppAccessContext();
		
	 	if(ids!=null) {
	 		
	 	
	 		headerItemsListContext=new ArrayList<HeaderTableItem>();
	 				
	 	    
	 	
	 	     List<String> idsList =  Arrays.asList(ids.split(","));
	 	   
	    	for(HeaderTableItem hti :acBlock.getHeaderItemsList()){
			
			 
			
			  if(idsList.contains(hti.getItemField())){
				  headerItemsListContext.add(hti);
			  }
			  
		   }

	 	}
	   return this.headerItemsListContext;
}
}
