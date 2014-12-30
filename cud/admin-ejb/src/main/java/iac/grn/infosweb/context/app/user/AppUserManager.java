package iac.grn.infosweb.context.app.user;

import iac.cud.infosweb.dataitems.AppUserItem;
import iac.cud.infosweb.dataitems.BaseItem;
import iac.cud.infosweb.entity.AcApplication;
import iac.cud.infosweb.entity.AcRole;
import iac.cud.infosweb.entity.AcUser;
import iac.cud.infosweb.entity.IspBssT;
import iac.cud.infosweb.util.BaseUtil;
import iac.grn.infosweb.context.mc.clorg.ClOrgManager;
import iac.grn.infosweb.context.mc.clusr.ClUsrManager;
import iac.grn.infosweb.context.mc.usr.UsrManager;
import iac.grn.infosweb.session.audit.actions.ActionsMap;
import iac.grn.infosweb.session.audit.actions.ResourcesMap;
import iac.grn.infosweb.session.table.BaseManager;
import iac.grn.serviceitems.BaseTableItem;
import iac.grn.serviceitems.HeaderTableItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

@Name("appUserManager")
 public class AppUserManager extends BaseManager{

	
	private String rejectReason;
	private String commentText;
	
	private List<AcApplication> listUsrArmForView = null;
	
	public void invokeLocal(String type, int firstRow, int numberOfRows,
	           String sessionId) {
		
		 log.info("hostsManager:invokeLocal");
		 try{
			 String orderQuery=null;
			 log.info("hostsManager:invokeLocal");
			 
			 AppUserStateHolder appUserStateHolder = (AppUserStateHolder)
					  Component.getInstance("appUserStateHolder",ScopeType.SESSION);
			 Map<String, String> filterMap = appUserStateHolder.getColumnFilterValues();
			 String st=null;
			  
			 if("list".equals(type)){
				 log.info("invokeLocal:list:01");
				 
				 Set<Map.Entry<String, String>> set = appUserStateHolder.getSortOrders().entrySet();
                 for (Map.Entry<String, String> me : set) {
      		       log.info("me.getKey+:"+me.getKey());
      		       log.info("me.getValue:"+me.getValue());
      		       
      		       if(orderQuery==null){
      		    	 orderQuery="order by "+me.getKey()+" "+me.getValue();
      		       }else{
      		    	 orderQuery=orderQuery+", "+me.getKey()+" "+me.getValue();  
      		       }
      		     }
                 log.info("AppUser:invokeLocal:list:orderQuery:"+orderQuery);
                 
                 if(filterMap!=null){
    	    		 Set<Map.Entry<String, String>> setFilterAppUser = filterMap.entrySet();
    	              for (Map.Entry<String, String> me : setFilterAppUser) {
    	              
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
                 log.info("AppUser:invokeLocal:list:filterQuery:"+st);

             
               List<Object[]> lo=null;
               AppUserItem ui = null;
               DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm:ss");
               

             lo=entityManager.createNativeQuery(
             "select t1.t1_id, t1.t1_created, t1.t1_status, t1_org_name,  t1_user_fio, t1_reject_reason, t1_comment, "+
                  "t1_SURNAME_USER, "+
                  "t1_NAME_USER, "+
                  "t1_PATRONYMIC_USER, "+
                  "t1_SIGN_USER, "+ 
                  "t1_POSITION_USER, "+
                  "t1_EMAIL_USER, "+ 
                  "t1_PHONE_USER, "+
                  "t1_CERTIFICATE_USER, "+ 
                  "t1_NAME_DEPARTAMENT, "+ 
                  "t1_NAME_ORG, "+ 
                  "t1_SIGN_ORG, "+
              "t1_user_id, t1_user_login, " +
              "t1_comment_app "+
              "from( "+ 
             "select JAS.ID_SRV t1_id, JAS.CREATED t1_created, "+
                  "JAS.SURNAME_USER t1_SURNAME_USER, "+
                  "JAS.NAME_USER t1_NAME_USER, "+
                  "JAS.PATRONYMIC_USER t1_PATRONYMIC_USER, "+
                  "JAS.SIGN_USER t1_SIGN_USER, "+
                  "JAS.POSITION_USER t1_POSITION_USER, "+
                  "JAS.EMAIL_USER t1_EMAIL_USER, "+ 
                  "JAS.PHONE_USER t1_PHONE_USER, "+
                  "JAS.CERTIFICATE_USER t1_CERTIFICATE_USER, "+ 
                  "JAS.NAME_DEPARTAMENT t1_NAME_DEPARTAMENT, "+ 
                  "JAS.NAME_ORG t1_NAME_ORG, "+ 
                  "JAS.SIGN_ORG t1_SIGN_ORG, "+
                  "JAS.STATUS t1_status,  CL_ORG_FULL.FULL_ t1_org_name, "+
              "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO ) t1_user_fio, "+
              "JAS.REJECT_REASON t1_reject_reason, "+ 
              "au_APP.ID_SRV t1_user_id, AU_APP.LOGIN t1_user_login, " +
              "JAS.COMMENT_ t1_comment, " +
              "JAS.COMMENT_APP t1_comment_app "+
             "from JOURN_APP_USER_BSS_T jas, "+
               "AC_USERS_KNL_T au_FULL, "+  
                "ISP_BSS_T cl_org_full, "+
                 "ISP_BSS_T cl_usr_full, "+
                  "AC_USERS_KNL_T au_APP, "+
              "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE "+ 
                "from ISP_BSS_T cl_org "+
                "where  CL_ORG.SIGN_OBJECT LIKE '%00000' "+
                "group by CL_ORG.SIGN_OBJECT) t03, "+
                 "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "+ 
                            "from ISP_BSS_T cl_usr "+
                            "where CL_USR.FIO is not null "+
                            "group by CL_usr.SIGN_OBJECT) t02 "+  
               "where JAS.UP_USER=AU_FULL.ID_SRV(+) "+
                "and AU_FULL.UP_SIGN=t03.CL_ORG_CODE(+) "+
                "and CL_ORG_FULL.ID_SRV(+)=t03.CL_ORG_ID "+
                "and AU_FULL.UP_SIGN_USER=t02.CL_USR_CODE(+) "+
                "and CL_USR_FULL.ID_SRV(+)=t02.CL_USR_ID "+
               "and au_APP.ID_SRV(+) =JAS.UP_USER_APP "+
               ") t1"+
              (st!=null ? " where "+st :" ")+
              (orderQuery!=null ? orderQuery+", t1_id desc " : " order by t1_id desc "))
              .setFirstResult(firstRow)
              .setMaxResults(numberOfRows)
              .getResultList();
               auditList = new ArrayList<BaseItem>();
               
               for(Object[] objectArray :lo){
            	   try{
            		 ui= new AppUserItem(
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
            				objectArray[17]!=null?objectArray[17].toString():"",
            				objectArray[18]!=null?new Long(objectArray[18].toString()):null,
              			    objectArray[19]!=null?objectArray[19].toString():"",
              			    objectArray[20]!=null?objectArray[20].toString():""
            				);  
            	   	     auditList.add(ui);
            	   }catch(Exception e1){
            		   log.error("AppUserinvokeLocal:for:error:"+e1);
            	   }
               }  
               
             log.info("AppUser:invokeLocal:list:02");
             
			 } else if("count".equals(type)){
				 log.info("AppUser:AppUserList:count:01");
				 
                 
                 if(filterMap!=null){
    	    		 Set<Map.Entry<String, String>> setFilterAppUser = filterMap.entrySet();
    	              for (Map.Entry<String, String> me : setFilterAppUser) {
    	            	
    	            	  
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
				                  "JAS.SURNAME_USER t1_SURNAME_USER, "+
				                  "JAS.NAME_USER t1_NAME_USER, "+
				                  "JAS.PATRONYMIC_USER t1_PATRONYMIC_USER, "+
				                  "JAS.SIGN_USER t1_SIGN_USER, "+
				                  "JAS.POSITION_USER t1_POSITION_USER, "+
				                  "JAS.EMAIL_USER t1_EMAIL_USER, "+ 
				                  "JAS.PHONE_USER t1_PHONE_USER, "+
				                  "JAS.CERTIFICATE_USER t1_CERTIFICATE_USER, "+ 
				                  "JAS.NAME_DEPARTAMENT t1_NAME_DEPARTAMENT, "+ 
				                  "JAS.NAME_ORG t1_NAME_ORG, "+ 
				                  "JAS.SIGN_ORG t1_SIGN_ORG, "+
				                  "JAS.STATUS t1_status,  CL_ORG_FULL.FULL_ t1_org_name, "+
				              "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO ) t1_user_fio, "+
				              "JAS.REJECT_REASON t1_reject_reason, "+ 
				              "au_APP.ID_SRV t1_user_id, AU_APP.LOGIN t1_user_login, "+
				              "JAS.COMMENT_ t1_comment "+
				             "from JOURN_APP_USER_BSS_T jas, "+
				               "AC_USERS_KNL_T au_FULL, "+  
				                "ISP_BSS_T cl_org_full, "+
				                 "ISP_BSS_T cl_usr_full, "+
				                  "AC_USERS_KNL_T au_APP, "+
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
				                "and CL_USR_FULL.ID_SRV(+)=t02.CL_USR_ID "+
				               "and au_APP.ID_SRV(+) =JAS.UP_USER_APP "+
				               ") t1"+
		         (st!=null ? " where "+st :" "))
               .getSingleResult()).longValue();
                 
                 
               log.info("AppUser:invokeLocal:count:02:"+auditCount);
           	 } else if("bean".equals(type)){
				 
			 }
		}catch(Exception e){
			  log.error("AppUser:invokeLocal:error:"+e);
			  evaluteForList=false;
			  FacesMessages.instance().add("Ошибка!");
		}

	}
	
	 private AppUserItem getUserItem(Long idUser){
		 if(idUser==null){
			  return null;
		   }
		   
		   try{
	           List<Object[]> lo=null;
	           AppUserItem ui = null;
	           DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm:ss");
	           
	           lo=entityManager.createNativeQuery(
	        		   "select t1.t1_id, t1.t1_created, t1.t1_status, t1_org_name,  t1_user_fio, t1_reject_reason, t1_comment, "+
	        	                  "t1_SURNAME_USER, "+
	        	                  "t1_NAME_USER, "+
	        	                  "t1_PATRONYMIC_USER, "+
	        	                  "t1_SIGN_USER, "+ 
	        	                  "t1_POSITION_USER, "+
	        	                  "t1_EMAIL_USER, "+ 
	        	                  "t1_PHONE_USER, "+
	        	                  "t1_CERTIFICATE_USER, "+ 
	        	                  "t1_NAME_DEPARTAMENT, "+ 
	        	                  "t1_NAME_ORG, "+ 
	        	                  "t1_SIGN_ORG, "+
	        	              "t1_user_id, t1_user_login, "+
	        	              "t1_comment_app "+
	        	              "from( "+ 
	        	             "select JAS.ID_SRV t1_id, JAS.CREATED t1_created, "+
	        	                  "JAS.SURNAME_USER t1_SURNAME_USER, "+
	        	                  "JAS.NAME_USER t1_NAME_USER, "+
	        	                  "JAS.PATRONYMIC_USER t1_PATRONYMIC_USER, "+
	        	                  "JAS.SIGN_USER t1_SIGN_USER, "+
	        	                  "JAS.POSITION_USER t1_POSITION_USER, "+
	        	                  "JAS.EMAIL_USER t1_EMAIL_USER, "+ 
	        	                  "JAS.PHONE_USER t1_PHONE_USER, "+
	        	                  "JAS.CERTIFICATE_USER t1_CERTIFICATE_USER, "+ 
	        	                  "JAS.NAME_DEPARTAMENT t1_NAME_DEPARTAMENT, "+ 
	        	                  "JAS.NAME_ORG t1_NAME_ORG, "+ 
	        	                  "JAS.SIGN_ORG t1_SIGN_ORG, "+
	        	                  "JAS.STATUS t1_status,  CL_ORG_FULL.FULL_ t1_org_name, "+
	        	              "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO ) t1_user_fio, "+
	        	              "JAS.REJECT_REASON t1_reject_reason, "+ 
	        	              "au_APP.ID_SRV t1_user_id, AU_APP.LOGIN t1_user_login, "+
	        	              "JAS.COMMENT_ t1_comment, "+
	        	              "JAS.COMMENT_APP t1_comment_app "+
	        	             "from JOURN_APP_USER_BSS_T jas, "+
	        	               "AC_USERS_KNL_T au_FULL, "+  
	        	                "ISP_BSS_T cl_org_full, "+
	        	                 "ISP_BSS_T cl_usr_full, "+
	        	                  "AC_USERS_KNL_T au_APP, "+
	        	              "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE "+ 
	        	                "from ISP_BSS_T cl_org "+
	        	                "where  CL_ORG.SIGN_OBJECT LIKE '%00000' "+
	        	                "group by CL_ORG.SIGN_OBJECT) t03, "+
	        	                 "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "+ 
	        	                            "from ISP_BSS_T cl_usr "+
	        	                            "where CL_USR.FIO is not null "+
	        	                            "group by CL_usr.SIGN_OBJECT) t02 "+  
	        	             /*   "where JAS.UP_USER=AU_FULL.ID_SRV "+
	        	                "and AU_FULL.UP_SIGN=t03.CL_ORG_CODE "+
	        	                "and CL_ORG_FULL.ID_SRV=t03.CL_ORG_ID "+*/
	        	                "where JAS.UP_USER=AU_FULL.ID_SRV(+) "+
	        	                "and AU_FULL.UP_SIGN=t03.CL_ORG_CODE(+) "+
	        	                "and CL_ORG_FULL.ID_SRV(+)=t03.CL_ORG_ID "+
	        	                "and AU_FULL.UP_SIGN_USER=t02.CL_USR_CODE(+) "+
	        	                "and CL_USR_FULL.ID_SRV(+)=t02.CL_USR_ID "+
	        	               "and au_APP.ID_SRV(+) =JAS.UP_USER_APP " +
	        	               "and JAS.ID_SRV=? "+
	        	               ") t1")
	         .setParameter(1, idUser)
	         .getResultList();
	           
	           for(Object[] objectArray :lo){
	        	   try{
	        		   log.info("AppUserManager:getUserItem:login:"+objectArray[1].toString());
	        		   
	        		   ui= new AppUserItem(
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
	            				objectArray[17]!=null?objectArray[17].toString():"",
	            				objectArray[18]!=null?new Long(objectArray[18].toString()):null,
	              			    objectArray[19]!=null?objectArray[19].toString():"",
	              			    objectArray[20]!=null?objectArray[20].toString():""
	            				);  
	        	     return ui;
	        	   }catch(Exception e1){
	        		   log.error("AppUserManager:getUserItem:for:error:"+e1);
	        	   }
	           }  
		   }catch(Exception e){
			   log.error("AppUserManager:getUserItem:error:"+e);
		   }
		   return null;
	 }
	 
	 
	 public void createArm(){
		 
		   log.info("AppUserManager:createArm:01");
		  
		   String sessionIdAppUser = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	       log.info("AppUserManager:createArm:sessionId:"+sessionIdAppUser);
	     
		   try{
			   
			   
			 
			 
			 UsrManager usrManager = (UsrManager)
			          Component.getInstance("usrManager", ScopeType.EVENT);
		   
		     usrManager.addUsr();
		   
		     
		     Long idUserCrt = usrManager.getIdUserCrt();
		     
		     entityManager.createNativeQuery(
		    	"insert into AC_USERS_LINK_KNL_T (UP_ROLES, UP_USERS, CREATOR,  CREATED) " + 
		    	"select app_roles.UP_ROLE, :idUser, :creator, sysdate " + 
		    	"from ROLES_APP_USER_BSS_T app_roles " + 
		    	"where  APP_ROLES.UP_APP_USER= :idApp " + 
		    	"group by app_roles.UP_ROLE")
		    	  .setParameter("idUser", idUserCrt)
	     		  .setParameter("creator", getCurrentUser().getBaseId())
	     		  .setParameter("idApp", new Long(sessionIdAppUser))
        	 	  .executeUpdate();	 
		    		 
		    		 
		    		 
		     AcUser usrBeanCrt = (AcUser)
					  Component.getInstance("usrBeanCrt",ScopeType.CONVERSATION);  
		     
		     entityManager.createNativeQuery(
	 	     		   "update JOURN_APP_USER_BSS_T t1 " +
	 	     		   "set t1.UP_USER_APP=?, t1.STATUS=1, t1.UP_USER_EXEC=? " +
	 	     		   "where t1.ID_SRV=? ")
	 	     		 .setParameter(1, usrBeanCrt.getBaseId())
	 	     		 .setParameter(2, getCurrentUser().getBaseId())
	 	     		 .setParameter(3, new Long(sessionIdAppUser))
	         	 	 .executeUpdate();
		    
		     AppUserItem ui = getUserItem(new Long(sessionIdAppUser));
		     
		     Contexts.getEventContext().set("contextBeanView", ui);
		     
		     audit(ResourcesMap.APP_USER, ActionsMap.EXECUTE); 
		     
		   }catch(Exception e){
			   log.error("AppUserManager:createArm:error:"+e);
		   }
	 }
	 
	 public void reject(){
		   log.info("AppUserManager:reject:01");
		  
		   String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	       log.info("AppUserManager:reject:sessionId:"+sessionId);
	     
		   try{
			   
		     entityManager.createNativeQuery(
	 	     		   "update JOURN_APP_USER_BSS_T t1 " +
	 	     		   "set t1.STATUS=2,  t1.REJECT_REASON=?, t1.UP_USER_EXEC=? " +
	 	     		   "where t1.ID_SRV=? ")
	 	     		 .setParameter(1, this.rejectReason)
	 	     		 .setParameter(2, getCurrentUser().getBaseId())
	 	     		 .setParameter(3, new Long(sessionId))
	 	     	 	 .executeUpdate();
		     
             AppUserItem ui = getUserItem(new Long(sessionId)); 
		     
		     Contexts.getEventContext().set("contextBeanView", ui);
		     
		     audit(ResourcesMap.APP_USER, ActionsMap.REJECT); 
		     
		   }catch(Exception e){
			   log.error("AppUserManager:reject:error:"+e);
		   }
	 }
	 
	 public void comment(){
		   log.info("AppUserManager:comment:01");
		  
		   String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("sessionId");
	       log.info("AppUserManager:comment:sessionId:"+sessionId);
	     
		   try{
			   
		     entityManager.createNativeQuery(
	 	     		   "update JOURN_APP_USER_BSS_T t1 " +
	 	     		   "set t1.COMMENT_=? " +
	 	     		   "where t1.ID_SRV=? ")
	 	     		 .setParameter(1, this.commentText)
	 	     		 .setParameter(2, new Long(sessionId))
	 	     	 	 .executeUpdate();
		     
           AppUserItem ui = getUserItem(new Long(sessionId)); 
		     
		   Contexts.getEventContext().set("contextBeanView", ui);
		     
		   }catch(Exception e){
			   log.error("AppUserManager:reject:error:"+e);
		   }
	 }
	 
	 public void forViewCrt() {
		 
		  IspBssT clUsrBean = null;
		  IspBssT clOrgBean = null; 		 
		  try{
			 String sessionId = FacesContext.getCurrentInstance().getExternalContext()
				        .getRequestParameterMap()
				        .get("sessionId");
		     log.info("AppUserManager:forViewCrt:sessionId:"+sessionId);
		     
		     AppUserItem ui = getUserItem(new Long(sessionId));
	    
		     ClUsrManager clUsrManager = (ClUsrManager)
                    Component.getInstance("clUsrManager", ScopeType.EVENT);
	    
		     ClOrgManager clOrgManager = (ClOrgManager)
                    Component.getInstance("clOrgManager", ScopeType.EVENT);
	    
		    
		     log.info("AppUserManager:forViewCrt:01:"+(ui==null));
		     
		    
		     
		    if(ui.getIogvCodeUser()!=null&&ui.getIogvCodeUser().length()==8){ //в заявке корректный код иогв пользователя
		    	
		      log.info("AppUserManager:forViewCrt:02");
		    	 
		      //берём из него код организации
		      clOrgManager.forViewAutocomplete(ui.getIogvCodeUser().substring(0, 3)+"00000");
		    	 
		      clOrgBean = (IspBssT)
		                    Component.getInstance("clOrgBean", ScopeType.EVENT);
				    
			  if(clOrgBean.getBaseId()!=null){ //нашли действующую организацию
					
				    ui.setNameOrg(clOrgBean.getFull());
				    
				    log.info("AppUserManager:forViewCrt:02:"+clOrgBean.getFull());
				    
				    //проводим поиск действующего пользователя
				    clUsrManager.forViewAutocomplete(ui.getIogvCodeUser());
				      
				    clUsrBean = (IspBssT)
			                    Component.getInstance("clUsrBean", ScopeType.EVENT);
					    
					   
					    
				    if(clUsrBean.getBaseId()!=null){ //нашли действующего пользователя
						  
						    log.info("AppUserManager:forViewCrt:03");
						  
						    ui.setFioIogvUser(clUsrBean.getFio());
				    }
			  }else{
				  //если из кода пользователя нет дйствующей организации,
				  //то поиск действующего пользователя не проводим
			  }

		      
		    }
		    //в итоге имеем: 
		    //1) организация + пользователь :
		    //   clOrgBean!=null && clOrgBean.getBaseId()!=null  && clUsrBean!= null && clUsrBean.getBaseId()!=null
		    //2) организация  :
		    //   clOrgBean!=null && clOrgBean.getBaseId()!=null && clUsrBean!= null && clUsrBean.getBaseId()==null
		    //3) ничего
		    //   clOrgBean!=null && clOrgBean.getBaseId()==null && clUsrBean==null
		    
		    log.info("AppUserManager:forViewCrt:04");
		    
		    if((clOrgBean==null||clOrgBean.getBaseId()==null)&&ui.getIogvCodeOrg()!=null&&ui.getIogvCodeOrg().length()==8){
		     //нет корректного кода иогв пользователя или не нашли действующую организацию
		     //но при этом есть корректный код иогв организации
		    	
		    	log.info("AppUserManager:forViewCrt:05");
		    	  
		      clOrgManager.forViewAutocomplete(ui.getIogvCodeOrg());
		   
		    
		      clOrgBean = (IspBssT)
                    Component.getInstance("clOrgBean", ScopeType.EVENT);
		    
		   
		    
		      if(clOrgBean.getBaseId()!=null){
		    	  
		    	 ui.setNameOrg(clOrgBean.getFull());
		    	
		    	 log.info("AppUserManager:forViewCrt:06:"+clOrgBean.getFull());
		      }
		    }
		    
		    log.info("AppUserManager:forViewCrt:07");
		    
		    ui.setPasswordTechUser(BaseUtil.password());
		    
		    Contexts.getEventContext().set("contextBeanView", ui);
		    
		   
		
		    
		   }catch(Exception e){
			 log.error("AppUserManager:forViewCrt:Error:"+e);
		   }
	   } 
	 
	
	 
	 public void forViewUpdDel() {
		 try{
		     String sessionId = FacesContext.getCurrentInstance().getExternalContext()
				        .getRequestParameterMap()
				        .get("sessionId");
		     log.info("AppUserManager:forViewUpdDel:sessionId:"+sessionId);
		     if(sessionId!=null){
		    	
		     	 
		    	 AppUserItem ui = getUserItem(new Long(sessionId));
		        	 
		   	 Contexts.getEventContext().set("appUserBean", ui);
		     }
		   }catch(Exception e){
			   log.error("AppUserManager:forViewUpdDel:Error:"+e);
		   }
	 }
	 
	 public void forViewComment() {
		   
		   try{
			     String sessionId = FacesContext.getCurrentInstance().getExternalContext()
					        .getRequestParameterMap()
					        .get("sessionId");
			     log.info("AppUserManager:forViewComment:sessionId:"+sessionId);
			     if(sessionId!=null){
			    	
			     	 
			    	 AppUserItem ui = getUserItem(new Long(sessionId));
			    	 
			    	 this.commentText=ui.getComment();

	     }
			}catch(Exception e){
				   log.error("AppUserManager:forViewComment:Error:"+e);
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
		   AppUserContext ac= new AppUserContext();
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
	   log.info("AppUserManager:getAuditItemsListContext");
	   if(auditItemsListContext==null){
		   AppUserContext ac= new AppUserContext();
		  
		   
		   
		   auditItemsListContext=ac.getAuditItemsCollection();
		   
	   }
	   return this.auditItemsListContext;
  }
  
  public List<HeaderTableItem> getHeaderItemsListContext() {
	  
	  if(headerItemsListContext==null){
		   AppUserContext ac= new AppUserContext();
		   headerItemsListContext=ac.getHeaderItemsList();
		   
	
		   
	   }
	  
	   return this.headerItemsListContext;
  }
  
  public List<AcApplication> getListUsrArmForView() throws Exception{
	    log.info("AppUsrManager:getListUsrArmForView:01");
	   
	    String sessionIdApp = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	    
	    log.info("AppUsrManager:getListUsrArmForView:sessionIdApp:"+sessionIdApp);
	    List<Object[]> loApp=null;
	    AcApplication app = null;
	    AcRole rolApp = null;
	    
	    try {
	    	
	    	if(listUsrArmForView==null && sessionIdApp!=null){
	      	
	    		loApp=entityManager.createNativeQuery(
	    			"select arm.ID_SRV app_id, arm.FULL_ app_name, ROL.FULL_ role_name " + 
	    			" from AC_IS_BSS_T arm, AC_ROLES_BSS_T rol,  ROLES_APP_USER_BSS_T app_roles " + 
	    			" where ROL.UP_IS=arm.ID_SRV and app_roles.UP_ROLE=ROL.ID_SRV and APP_ROLES.UP_APP_USER= ? " + 
	    			" group by arm.FULL_, arm.ID_SRV, ROL.FULL_ " + 
	    			" order by arm.FULL_, arm.ID_SRV, ROL.FULL_ ")
	    				 .setParameter(1, new Long(sessionIdApp))
	    				.getResultList();

	    		 listUsrArmForView = new ArrayList<AcApplication>();
	    		
	    		 for(Object[] objectArray :loApp){
	    			 
	    			 if(app==null||!app.getIdArm().toString().equals(objectArray[0].toString())){
	    			   app=new AcApplication();
	    			   
	    			   listUsrArmForView.add(app);
	    			   
	    			   app.setIdArm(new Long(objectArray[0].toString()));
	    			   app.setName(objectArray[1]!=null?objectArray[1].toString():"");
	    			   app.setRolList(new ArrayList<AcRole>());
	    			 }
	    			 
	    			 rolApp=new AcRole();
	    			 rolApp.setRoleTitle(objectArray[2]!=null?objectArray[2].toString():"");
	    			 
	    			 app.getRolList().add(rolApp);
	    		 }
	    		
	    		
	    	 }
			} catch (Exception eApp) {
	    	 log.error("AppUsrManager:getListUsrArmForView:ERROR:"+eApp);
	         throw eApp;
	     }
	    return listUsrArmForView;
}
}
