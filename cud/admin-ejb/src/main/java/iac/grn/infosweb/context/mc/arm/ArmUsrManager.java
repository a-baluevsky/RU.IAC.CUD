package iac.grn.infosweb.context.mc.arm;

import iac.cud.infosweb.dataitems.BaseItem;
import iac.cud.infosweb.dataitems.UserItem;
import iac.cud.infosweb.entity.AcApplication;
import iac.cud.infosweb.entity.AcUser;
import iac.cud.infosweb.entity.LinkAdminUserSys;
import iac.grn.infosweb.context.mc.usr.UsrContext;
import iac.grn.infosweb.session.audit.actions.ActionsMap;
import iac.grn.infosweb.session.audit.actions.ResourcesMap;
import iac.grn.serviceitems.BaseTableItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
 

@Name("armUsrManager")
 public class ArmUsrManager {
	
	@Logger private Log log;
	
	@In 
	EntityManager entityManager;
	
	private List <BaseTableItem> auditItemsListSelect;
	
    private List<BaseItem> auditList; 
	
	private Long auditCount;
	
	private Boolean evaluteForList;
	private Boolean evaluteForListFooter;  
	
	public List<BaseItem> getAuditList(int firstRow, int numberOfRows){
		  
		  String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
		             .getRequestParameterMap()
		             .get("remoteAudit");
		   String sessionId = FacesContext.getCurrentInstance().getExternalContext()
 			        .getRequestParameterMap()
 			        .get("sessionId");
		   
		   log.info("armUsrManager:getAuditList:remoteAudit:"+remoteAudit);
		   log.info("armUsrManager:getAuditList:sessionId:"+sessionId);
		   
		  
		   
		  List<BaseItem> armUsrListCached = (List<BaseItem>)
				  Component.getInstance("armUsrListCached",ScopeType.SESSION);
		  if(auditList==null){
			  log.info("armUsrManager:getAuditList:01");
			 	if(("rowSelectFact".equals(remoteAudit)||
				    "selRecAllFact".equals(remoteAudit)||
				    "clRecAllFact".equals(remoteAudit)||
				    "clSelOneFact".equals(remoteAudit)||
				    "onSelColSaveFact".equals(remoteAudit))&&
				    armUsrListCached!=null){
			 		log.info("armUsrManager:getAuditList:02:"+armUsrListCached.size());
				    	this.auditList=armUsrListCached;
				}else{
					log.info("armUsrManager:getAuditList:03");
			    	invokeLocal("list", firstRow, numberOfRows, null);
				    Contexts.getSessionContext().set("armUsrListCached", this.auditList);
				    log.info("armUsrManager:getAuditList:03:"+this.auditList.size());
				}
			 	
			 	
			 	
			 	try{
			 		
			 		List<Long> listUsr=entityManager.createQuery(
	   	 		    		 "select o.pk.upUser from LinkAdminUserSys o " +
	   	 		    		 "where o.pk.upSys = :upSys ")
	   	 					 .setParameter("upSys", new Long(sessionId))
	   	 		      		 .getResultList();
	   	 		 
	   	    	     for(BaseItem user :this.auditList){
	   	 	           if (listUsr.contains(user.getBaseId())){  
	   	 	        	  ((UserItem)user).setUsrChecked(true);
	   	 			   }
	   	 	         } 
			 	
			 	//альтернативное решение - через createNativeQuery
			 	
			 		
			 	}catch(Exception e){
			 		log.error("armUsrManager:getUsrAlfList:error:"+e);
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
				 log.info("armUsrManager:invokeLocal");
				 
				 ArmUsrStateHolder armUsrStateHolder = (ArmUsrStateHolder)
						  Component.getInstance("armUsrStateHolder",ScopeType.SESSION);
				 
				 Map<String, String> filterMap = armUsrStateHolder.getColumnFilterValues();
				 String st=null;
				  
				 if("list".equals(type)){
					 log.info("armUsrManager:list:01");
					 
					 Set<Map.Entry<String, String>> set = armUsrStateHolder.getSortOrders().entrySet();
	                 for (Map.Entry<String, String> me : set) {
	      		       log.info("me.getKey+:"+me.getKey());
	      		       log.info("me.getValue:"+me.getValue());
	      		       
	      		       if(orderQuery==null){
	      		    	 orderQuery="order by "+me.getKey()+" "+me.getValue();
	      		       }else{
	      		    	 orderQuery=orderQuery+", "+me.getKey()+" "+me.getValue();  
	      		       }
	      		     }
	                 log.info("armUsrManager:invokeLocal:list:orderQuery:"+orderQuery);
	                 
	                 if(filterMap!=null){
	    	    		 Set<Map.Entry<String, String>> setFilterArmUsr = filterMap.entrySet();
	    	              for (Map.Entry<String, String> me : setFilterArmUsr) {
	    	              
	    	   		     if("t1_crt_date".equals(me.getKey())){  
	    	        	   
	    	        	   //делаем фильтр на начало  
	    	        	     st=(st!=null?st+" and " :"")+" lower(to_char("+me.getKey()+",'DD.MM.YY HH24:MI:SS')) like lower('"+me.getValue()+"%') ";
	    	    	     }else{
	    	        			//делаем фильтр на начало
	    	            	  st=(st!=null?st+" and " :"")+" lower("+me.getKey()+") like lower('"+me.getValue()+"%') ";
	    	        	  }
	    	              }
	    	    	   }
	                 log.info("armUsrManager:invokeLocal:list:filterQuery:"+st);
	                 
	 	             
	               List<Object[]> lo=null;
	               UserItem ui = null;
	               DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm:ss");
     
	               lo=entityManager.createNativeQuery(
	"select t1.t1_id, t1.t1_login, t1.t1_cert, t1.t1_usr_code, t1.t1_fio, " +
	       "t1.t1_tel, t1.t1_email,t1.t1_pos, t1.t1_dep_name, t1.t1_org_code, " +
	       "t1.t1_org_name, t1.t1_org_adr, t1.t1_org_tel, t1.t1_start, t1.t1_end, " +
	       "t1.t1_status, t1.t1_crt_date, t1.t1_crt_usr_login, t1.t1_upd_date, t1.t1_upd_usr_login, "+
	       "t1.t1_dep_code, t1.t1_org_status, t1.t1_usr_status, t1.t1_dep_status, t1.t1_iogv_bind_type  "+ 
	"from( "+
	"select AU_FULL.ID_SRV t1_id, AU_FULL.login t1_login, AU_FULL.CERTIFICATE t1_cert, t2.CL_USR_CODE t1_usr_code, "+
	 "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO ) t1_fio, "+  
	  "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.PHONE, CL_USR_FULL.PHONE ) t1_tel, "+   
	  "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.E_MAIL, CL_USR_FULL.EMAIL) t1_email, "+  
	  "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.POSITION, CL_USR_FULL.POSITION)t1_pos, "+  
	  "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.DEPARTMENT, decode(substr(CL_DEP_FULL.sign_object,4,2), '00', null, CL_DEP_FULL.FULL_)) t1_dep_name, "+ 
	  "t1.CL_ORG_CODE t1_org_code, CL_ORG_FULL.FULL_ t1_org_name, "+
	  "CL_ORG_FULL.PREFIX || decode(CL_ORG_FULL.HOUSE, null, null, ','  ||CL_ORG_FULL.HOUSE  ) t1_org_adr, "+
	  "CL_ORG_FULL.PHONE t1_org_tel, "+
	  "to_char(AU_FULL.START_ACCOUNT, 'DD.MM.YY HH24:MI:SS') t1_start, "+ 
	  "to_char(AU_FULL.END_ACCOUNT, 'DD.MM.YY HH24:MI:SS') t1_end, "+  
	  "AU_FULL.STATUS t1_status, "+  
	  "AU_FULL.CREATED t1_crt_date, "+ 
	  "USR_CRT.LOGIN t1_crt_usr_login, "+ 
	  "to_char(AU_FULL.MODIFIED, 'DD.MM.YY HH24:MI:SS') t1_upd_date, "+ 
	  "USR_UPD.LOGIN t1_upd_usr_login, "+ 
	  "decode(AU_FULL.UP_SIGN_USER, null, null, decode(substr(CL_DEP_FULL.sign_object,4,2), '00', null, CL_DEP_FULL.sign_object)) t1_dep_code, "+ 
	  "CL_ORG_FULL.STATUS t1_org_status,  CL_usr_FULL.STATUS t1_usr_status, "+ 
	   "decode(AU_FULL.UP_SIGN_USER, null, null, decode(substr(CL_DEP_FULL.sign_object,4,2), '00', null, CL_DEP_FULL.STATUS)) t1_dep_status, " +
	   "AU_FULL.UP_BINDING t1_iogv_bind_type "+      
	"from "+
	"(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE "+
	"from ISP_BSS_T cl_org, "+
	"AC_USERS_KNL_T au "+
	"where AU.UP_SIGN = CL_ORG.SIGN_OBJECT "+
	"group by CL_ORG.SIGN_OBJECT) t1, "+
	"(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "+
	"from ISP_BSS_T cl_usr, "+
	"AC_USERS_KNL_T au "+
	"where AU.UP_SIGN_USER  = CL_usr.SIGN_OBJECT "+
	"group by CL_usr.SIGN_OBJECT) t2, "+
	"(select max(CL_dep.ID_SRV) CL_DEP_ID,  CL_DEP.SIGN_OBJECT  CL_DEP_CODE "+
	"from ISP_BSS_T cl_dep, "+
	"AC_USERS_KNL_T au "+
	"where substr(au.UP_SIGN_USER,1,5)||'000'  =cl_dep.SIGN_OBJECT(+) "+
	"group by CL_DEP.SIGN_OBJECT) t3, "+
	"ISP_BSS_T cl_org_full, "+
	"ISP_BSS_T cl_usr_full, "+
	"ISP_BSS_T cl_dep_full, "+
	"AC_USERS_KNL_T au_full, "+
	"AC_USERS_KNL_T usr_crt, "+  
	"AC_USERS_KNL_T usr_upd "+
	"where cl_org_full.ID_SRV= CL_ORG_ID "+
	"and cl_usr_full.ID_SRV(+)=CL_USR_ID "+
	"and cl_DEP_full.ID_SRV(+)=CL_DEP_ID "+
	"and au_full.UP_SIGN = CL_ORG_CODE "+
	"and au_full.UP_SIGN_USER  =  CL_USR_CODE(+) "+
	"and substr(au_full.UP_SIGN_USER,1,5)||'000'  =  CL_DEP_CODE(+) "+
	"and au_full.CREATOR=USR_CRT.ID_SRV "+ 
	"and au_full.MODIFICATOR=USR_UPD.ID_SRV(+) "+ 
	")t1 "+
	                      (st!=null ? " where "+st :" ")+
	                      (orderQuery!=null ? orderQuery+", t1_fio " : " order by t1_fio "))
	              .setFirstResult(firstRow)
	              .setMaxResults(numberOfRows)
	              .getResultList();
	               
	               
	               auditList = new ArrayList<BaseItem>();
	               
	               for(Object[] objectArray :lo){
	            	   try{
	            	     ui= new UserItem(
	            			  objectArray[0]!=null?new Long(objectArray[0].toString()):null,
	            			  objectArray[1]!=null?objectArray[1].toString():"",
	            			  objectArray[2]!=null?objectArray[2].toString():"",
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
	            			  objectArray[15]!=null?new Long(objectArray[15].toString()):null,
	            			  objectArray[16]!=null?df.format((Date)objectArray[16]) :"",
	            			  objectArray[17]!=null?objectArray[17].toString():"",
	            			  objectArray[18]!=null?objectArray[18].toString():"",
	            			  objectArray[19]!=null?objectArray[19].toString():"",
	            			  objectArray[20]!=null?objectArray[20].toString():"",
	            			  objectArray[21]!=null?objectArray[21].toString():"",
	            			  objectArray[22]!=null?objectArray[22].toString():"",
	            			  objectArray[23]!=null?objectArray[23].toString():"",
	            			  objectArray[24]!=null?new Long(objectArray[24].toString()):null
	            			   );
	            	     auditList.add(ui);
	            	   }catch(Exception e1){
	            		   log.error("invokeLocal:for:error:"+e1);
	            	   }
	               }  
	               
	             log.info("armUsrManager:invokeLocal:list:02");
	             
				 } else if("count".equals(type)){
					 log.info("armUsrManager:count:01");
					 
	                 
	                 if(filterMap!=null){
	    	    		 Set<Map.Entry<String, String>> setFilterArmUsr = filterMap.entrySet();
	    	              for (Map.Entry<String, String> me : setFilterArmUsr) {
	    	              
	    	   		   		//делаем фильтр на начало
	    	            	  st=(st!=null?st+" and " :"")+" lower("+me.getKey()+") like lower('"+me.getValue()+"%') ";
	    	               }
	    	    	   }
					 
					 
					
					 auditCount = ((java.math.BigDecimal)entityManager.createNativeQuery(
							        "select count(*) "+ 
									 "from( "+
									 "select AU_FULL.ID_SRV t1_id, AU_FULL.login t1_login, AU_FULL.CERTIFICATE t1_cert, t2.CL_USR_CODE t1_usr_code, "+
									  "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO ) t1_fio, "+  
									   "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.PHONE, CL_USR_FULL.PHONE ) t1_tel, "+   
									   "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.E_MAIL, CL_USR_FULL.EMAIL) t1_email, "+  
									   "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.POSITION, CL_USR_FULL.POSITION)t1_pos, "+  
									   "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.DEPARTMENT, decode(substr(CL_DEP_FULL.sign_object,4,2), '00', null, CL_DEP_FULL.FULL_)) t1_dep_name, "+ 
									   "t1.CL_ORG_CODE t1_org_code, CL_ORG_FULL.FULL_ t1_org_name, "+
									   "CL_ORG_FULL.PREFIX || decode(CL_ORG_FULL.HOUSE, null, null, ','  ||CL_ORG_FULL.HOUSE  ) t1_org_adr, "+
									   "CL_ORG_FULL.PHONE t1_org_tel, "+
									   "to_char(AU_FULL.START_ACCOUNT, 'DD.MM.YY HH24:MI:SS') t1_start, "+ 
									   "to_char(AU_FULL.END_ACCOUNT, 'DD.MM.YY HH24:MI:SS') t1_end, "+  
									   "AU_FULL.STATUS t1_status, "+  
									   "AU_FULL.CREATED t1_crt_date, "+ 
									   "USR_CRT.LOGIN t1_crt_usr_login, "+ 
									   "to_char(AU_FULL.MODIFIED, 'DD.MM.YY HH24:MI:SS') t1_upd_date, "+ 
									   "USR_UPD.LOGIN t1_upd_usr_login, "+ 
									   "decode(AU_FULL.UP_SIGN_USER, null, null, decode(substr(CL_DEP_FULL.sign_object,4,2), '00', null, CL_DEP_FULL.sign_object)) t1_dep_code, "+ 
									   "CL_ORG_FULL.STATUS t1_org_status,  CL_usr_FULL.STATUS t1_usr_status, "+ 
									    "decode(AU_FULL.UP_SIGN_USER, null, null, decode(substr(CL_DEP_FULL.sign_object,4,2), '00', null, CL_DEP_FULL.STATUS)) t1_dep_status, " +
									    "AU_FULL.UP_BINDING t1_iogv_bind_type "+      
									 "from "+
									 "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE "+
									 "from ISP_BSS_T cl_org, "+
									 "AC_USERS_KNL_T au "+
									 "where AU.UP_SIGN = CL_ORG.SIGN_OBJECT "+
									 "group by CL_ORG.SIGN_OBJECT) t1, "+
									 "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "+
									 "from ISP_BSS_T cl_usr, "+
									 "AC_USERS_KNL_T au "+
									 "where AU.UP_SIGN_USER  = CL_usr.SIGN_OBJECT "+
									 "group by CL_usr.SIGN_OBJECT) t2, "+
									 "(select max(CL_dep.ID_SRV) CL_DEP_ID,  CL_DEP.SIGN_OBJECT  CL_DEP_CODE "+
									 "from ISP_BSS_T cl_dep, "+
									 "AC_USERS_KNL_T au "+
									 "where substr(au.UP_SIGN_USER,1,5)||'000'  =cl_dep.SIGN_OBJECT(+) "+
									 "group by CL_DEP.SIGN_OBJECT) t3, "+
									 "ISP_BSS_T cl_org_full, "+
									 "ISP_BSS_T cl_usr_full, "+
									 "ISP_BSS_T cl_dep_full, "+
									 "AC_USERS_KNL_T au_full, "+
									 "AC_USERS_KNL_T usr_crt, "+  
									 "AC_USERS_KNL_T usr_upd "+
									 "where cl_org_full.ID_SRV= CL_ORG_ID "+
									 "and cl_usr_full.ID_SRV(+)=CL_USR_ID "+
									 "and cl_DEP_full.ID_SRV(+)=CL_DEP_ID "+
									 "and au_full.UP_SIGN = CL_ORG_CODE "+
									 "and au_full.UP_SIGN_USER  =  CL_USR_CODE(+) "+
									 "and substr(au_full.UP_SIGN_USER,1,5)||'000'  =  CL_DEP_CODE(+) "+
									 "and au_full.CREATOR=USR_CRT.ID_SRV "+ 
									 "and au_full.MODIFICATOR=USR_UPD.ID_SRV(+) "+ 
									 ")t1 "+
			         (st!=null ? " where "+st :" "))
	               .getSingleResult()).longValue();
	                 
	                 
	               log.info("armUsrManager:invokeLocal:count:02:"+auditCount);
	           	 } else if("bean".equals(type)){
					 
				 }
			}catch(Exception e){
				  log.error("invokeLocal:error:"+e);
				  evaluteForList=false;
				  FacesMessages.instance().add("Ошибка!");
			}
		}
	
		 public Long getAuditCount(){
			   log.info("getAuditCount");
			 
			   invokeLocal("count",0,0,null);
			  
			   return auditCount;
			  
		   }	
		
		 public void updArmUserAlf(){
			   
			   log.info("armUsrManager:updArmUserAlf:01");
			   
			   LinkAdminUserSys lguu=null;
			   
				   String  sessionId = FacesContext.getCurrentInstance().getExternalContext()
				        .getRequestParameterMap()
				        .get("sessionId");
			   log.info("armUsrManager:updArmUserAlf:sessionId:"+sessionId);
			   
			   if(sessionId==null){
				   return;
			   }
			
			 	   
			   try {
				   AcUser au = (AcUser) Component.getInstance("currentUser",ScopeType.SESSION);
				   
				   AcApplication aum = entityManager.find(AcApplication.class, new Long(sessionId));
				   
				   List<LinkAdminUserSys> oldLinkList = aum.getLinkAdminUserSys();
				   
				   
				   
				   
				   for(BaseItem user:this.auditList){
					  log.info("ugroupManager:updArmUserAlf:Login:"+((UserItem)user).getLogin());
					  log.info("ugroupManager:updArmUserAlf:UsrChecked:"+((UserItem)user).getUsrChecked());
					  
					  if(((UserItem)user).getUsrChecked().booleanValue()){ //отмечен
						
						 
						 lguu=new LinkAdminUserSys(user.getBaseId(), new Long(sessionId));
						 if(oldLinkList.contains(lguu)){  
						 
						 }else{//нет в базе
							 lguu.setCreated(new Date()); 
							 lguu.setCreator(au.getIdUser());
					         
					         oldLinkList.add(lguu);
					         
							
						  }
						  
					  }else{
						  //не отмечен
						 lguu=new LinkAdminUserSys(user.getBaseId(), new Long(sessionId));
						 if(oldLinkList.contains(lguu)){ 
							oldLinkList.remove(lguu);
							entityManager.createQuery("DELETE FROM LinkAdminUserSys gu " +
					                  "WHERE gu.pk.upSys=:upSys " +
					                  "and gu.pk.upUser=:upUser ")
			                         .setParameter("upSys", new Long(sessionId))
			                         .setParameter("upUser", user.getBaseId())
							    .executeUpdate();
						  }else{
							  //в базе и так нет
							  log.info("armUsrManager:not_in_db");
						  }
					  }
				  }

				   
			        entityManager.flush();
			    	
				    entityManager.refresh(aum);
			    	  
				    Contexts.getEventContext().set("armBean", aum);
			    	 
				  //аудит!!!
				    ArmManager armManager = (ArmManager)Component.getInstance("armManager", ScopeType.EVENT);
					armManager.audit(ResourcesMap.IS, ActionsMap.UPDATE_USER); 
			    	
			    	
			    	
			     }catch (Exception e) {
		       log.error("armUsrManager:updArmUserAlf:ERROR:"+e);
		     }
		}
		 
		 
	public List <BaseTableItem> getAuditItemsListSelect() {
		   
	
	    UsrContext ac= new UsrContext();
		   if( auditItemsListSelect==null){
			   log.info("getAuditItemsListSelect:02");
			   auditItemsListSelect = new ArrayList<BaseTableItem>();
			  
			   auditItemsListSelect.add(new BaseTableItem("", "", "usrChecked"));
			   
			   auditItemsListSelect.add(ac.getAuditItemsMap().get("fio"));
			   auditItemsListSelect.add(ac.getAuditItemsMap().get("login"));
			   auditItemsListSelect.add(ac.getAuditItemsMap().get("orgName"));
			  
			 
			   
			   
				   }
	       return this.auditItemsListSelect;
 }
 
 public void setAuditItemsListSelect(List <BaseTableItem> auditItemsListSelect) {
		    this.auditItemsListSelect=auditItemsListSelect;
 }
 
 public Boolean getEvaluteForList() {
		
	   	log.info("armUsrManager:evaluteForList:01");
	   	if(evaluteForList==null){
	   		evaluteForList=false;
	    	String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
		             .getRequestParameterMap()
		             .get("remoteAudit");
		   log.info("armUsrManager:evaluteForList:remoteAudit:"+remoteAudit);
	     	
	    	if(remoteAudit!=null&&
	    	 
	    	   !"OpenCrtFact".equals(remoteAudit)&&	
	    	   !"OpenUpdFact".equals(remoteAudit)&&
	    	   !"OpenDelFact".equals(remoteAudit)&&
	   	       !"onSelColFact".equals(remoteAudit)&&
	   	       !"refreshPdFact".equals(remoteAudit)){
	    		log.info("armUsrManager:evaluteForList!!!");
	   		    evaluteForList=true;
	    	}
	   	 }
	       return evaluteForList;
	   }
	   public Boolean getEvaluteForListFooter() {
			
		  
		   	if(evaluteForListFooter==null){
		   		evaluteForListFooter=false;
		    	String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("remoteAudit");
			   log.info("armUsrManager:evaluteForListFooter:remoteAudit:"+remoteAudit);
		     
		    	if(getEvaluteForList()&&
		    	  
		    	   !"protBeanWord".equals(remoteAudit)&&	
		    	  
		   	       !"selRecAllFact".equals(remoteAudit)&&
		   	       !"clRecAllFact".equals(remoteAudit)&&
		   	      // !remoteAudit equals "clSelOneFact"
		   	       !"onSelColSaveFact".equals(remoteAudit)){
		    		log.info("armUsrManager:evaluteForListFooter!!!");
		   		    evaluteForListFooter=true;
		    	}
		   	 }
		       return evaluteForListFooter;
		   }
 
	
}
