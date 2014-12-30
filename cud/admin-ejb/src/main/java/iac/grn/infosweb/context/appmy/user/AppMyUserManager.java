package iac.grn.infosweb.context.appmy.user;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.Set;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;


import iac.cud.infosweb.dataitems.AppUserItem;
import iac.cud.infosweb.dataitems.BaseItem;
import iac.grn.infosweb.session.table.BaseManager;
import iac.grn.serviceitems.BaseTableItem;
import iac.grn.serviceitems.HeaderTableItem;

@Name("appMyUserManager")
 public class AppMyUserManager extends BaseManager{

	
	private String rejectReason;
	private String commentText;
	
	public void invokeLocal(String type, int firstRow, int numberOfRows,
	           String sessionId) {
		
		 log.info("AppMyUserManager:invokeLocal");
		 try{
			 String orderQuery=null;
			 log.info("AppMyUserManager:invokeLocal");
			 
			 AppMyUserStateHolder appMyUserStateHolder = (AppMyUserStateHolder)
					  Component.getInstance("appMyUserStateHolder",ScopeType.SESSION);
			 Map<String, String> filterMapAppMyUser = appMyUserStateHolder.getColumnFilterValues();
			 String st=null;
			  
			 if("list".equals(type)){
				 log.info("AppMyUser:invokeLocal:list:01");
				 
				 Set<Map.Entry<String, String>> set = appMyUserStateHolder.getSortOrders().entrySet();
                 for (Map.Entry<String, String> me : set) {
      		       log.info("me.getKey+:"+me.getKey());
      		       log.info("me.getValue:"+me.getValue());
      		       
      		       if(orderQuery==null){
      		    	 orderQuery="order by "+me.getKey()+" "+me.getValue();
      		       }else{
      		    	 orderQuery=orderQuery+", "+me.getKey()+" "+me.getValue();  
      		       }
      		     }
                 log.info("invokeLocal:list:orderQuery:"+orderQuery);
                 
                 if(filterMapAppMyUser!=null){
    	    		 Set<Map.Entry<String, String>> setFilterAppMyUser = filterMapAppMyUser.entrySet();
    	              for (Map.Entry<String, String> me : setFilterAppMyUser) {
    	              
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
                 log.info("AppMyUser:invokeLocal:list:filterQuery:"+st);

             
               List<Object[]> loAppMyUser=null;
               AppUserItem ui = null;
               DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm:ss");
               

             loAppMyUser=entityManager.createNativeQuery(
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
                "where JAS.UP_USER=AU_FULL.ID_SRV "+
                "and AU_FULL.UP_SIGN=t03.CL_ORG_CODE "+
                "and CL_ORG_FULL.ID_SRV=t03.CL_ORG_ID "+
                "and AU_FULL.UP_SIGN_USER=t02.CL_USR_CODE(+) "+
                "and CL_USR_FULL.ID_SRV(+)=t02.CL_USR_ID "+
               "and au_APP.ID_SRV(+) =JAS.UP_USER_APP "+
               "and JAS.UP_USER= :idUser "+ 
               
               ") t1"+
              (st!=null ? " where "+st :" ")+
              (orderQuery!=null ? orderQuery+", t1_id desc " : " order by t1_id desc "))
              .setFirstResult(firstRow)
              .setMaxResults(numberOfRows)
              .setParameter("idUser", getCurrentUser().getBaseId())
              .getResultList();
               auditList = new ArrayList<BaseItem>();
               
               for(Object[] objectArray :loAppMyUser){
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
            		   log.error("AppMyUser:invokeLocal:for:error:"+e1);
            	   }
               }  
               
             log.info("AppMyUser:invokeLocal:list:02");
             
			 } else if("count".equals(type)){
				 log.info("AppMyUserList:count:01");
				 
                 
                 if(filterMapAppMyUser!=null){
    	    		 Set<Map.Entry<String, String>> setFilterAppMyUser = filterMapAppMyUser.entrySet();
    	              for (Map.Entry<String, String> me : setFilterAppMyUser) {
    	              
    	            	  
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
				               "and JAS.UP_USER= :idUser "+ 
				               
				               ") t1"+
		         (st!=null ? " where "+st :" "))
		       .setParameter("idUser", getCurrentUser().getBaseId())
               .getSingleResult()).longValue();
                 
                 
               log.info("AppMyUser:invokeLocal:count:02:"+auditCount);
           	 }
		}catch(Exception eAppMyUser){
			  log.error("AppMyUser:invokeLocal:error:"+eAppMyUser);
			  evaluteForList=false;
			  FacesMessages.instance().add("Ошибка!");
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
		   AppMyUserContext ac= new AppMyUserContext();
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
	   log.info("AppMyUserManager:getAuditItemsListContext");
	   if(auditItemsListContext==null){
		   AppMyUserContext ac= new AppMyUserContext();
		  
		   
		   
		   auditItemsListContext=ac.getAuditItemsCollection();
		   
	   }
	   return this.auditItemsListContext;
  }
  
  public List<HeaderTableItem> getHeaderItemsListContext() {
	  
	  if(headerItemsListContext==null){
		   AppMyUserContext ac= new AppMyUserContext();
		   headerItemsListContext=ac.getHeaderItemsList();
		   
		
		   
	   }
	  
	   return this.headerItemsListContext;
  }
}
