package iac.grn.infosweb.context.mc.audit.func;

 
import iac.cud.infosweb.dataitems.AuditFuncItem;
import iac.cud.infosweb.dataitems.BaseItem;
import iac.cud.infosweb.dataitems.BaseParamItem;
import iac.cud.infosweb.entity.AcUser;
import iac.cud.infosweb.entity.ActionsLogKnlT;
import iac.cud.infosweb.local.service.ServiceReestrAction;
import iac.cud.infosweb.local.service.ServiceReestrPro;
import iac.cud.infosweb.remote.frontage.IRemoteFrontageLocal;
import iac.grn.infosweb.session.audit.actions.ActionsMap;
import iac.grn.infosweb.session.audit.actions.ResourcesMap;
import iac.grn.infosweb.session.audit.export.AuditExportData;
import iac.grn.serviceitems.BaseTableItem;

import java.io.BufferedWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

/**
 * ”правл€ющий Ѕин
 * @author bubnov
 *
 */
@Name("aFuncManager")
 public class AFuncManager {
	
	 @Logger private Log log;
	
	 @In 
	 EntityManager entityManager;
	 
	 private Long auditReportCubeCount;
		
	private Long auditCount;

		
	private String dellMessage;
	 
	private List<BaseItem> auditList; 
	
	
	private List<AuditFuncItem> auditReportCubeList;
	
		
	private List <BaseTableItem> auditItemsListSelect;
	
	private List <BaseTableItem> auditItemsListContext;
	
	private int connectError=0;
	
	private Date clearDate1;
	private Date clearDate2;
	
	private List<BaseItem> auditReportList;
	
	
    private Date reportDate1;
	
	private Date reportDate2;
	
	private Boolean evaluteForList;
	private Boolean evaluteForListFooter;  
	private Boolean evaluteForBean;
	
	
	private static String param_code="to_archive_audit_func";
	
	private static String jndiBinding = "java:global/InfoS-ear/InfoS-ejb/IRemoteFrontage!iac.cud.infosweb.remote.frontage.IRemoteFrontageLocal";
	
	
	private Long archiveParamValue=null;
	
	public List<BaseItem> getAuditList(int firstRow, int numberOfRows){
	  String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
	             .getRequestParameterMap()
	             .get("remoteAudit");
	   log.info("auditManager:getAuditList:remoteAudit:"+remoteAudit);
	  
	  
	  log.info("getAuditList:firstRow:"+firstRow);
	  log.info("getAuditList:numberOfRows:"+numberOfRows);
	  
	  List<BaseItem> aFuncListCached = (List<BaseItem>)
			  Component.getInstance("aFuncListCached",ScopeType.SESSION);
	  if(auditList==null){
		  log.info("getAuditList:01");
		 	if(("clRecAllFact".equals(remoteAudit)||
		 		"rowSelectFact".equals(remoteAudit)||
			    "selRecAllFact".equals(remoteAudit)||
			    "onSelColSaveFact".equals(remoteAudit)||
			    "clSelOneFact".equals(remoteAudit) )&&
			    aFuncListCached!=null){
		 	    	this.auditList=aFuncListCached;
			}else{
				log.info("getAuditList:03");
		    	invokeLocal("list", firstRow, numberOfRows, null);
			    Contexts.getSessionContext().set("aFuncListCached", this.auditList);
			    log.info("getAuditList:03:"+this.auditList.size());
			}
		 	
		 	List<String>  selRecArmFunc = (ArrayList<String>)
					  Component.getInstance("selRecaFunc",ScopeType.SESSION);
		 	
		 	if(this.auditList!=null && selRecArmFunc!=null) {
		 		 for(BaseItem it:this.auditList){
				   if(selRecArmFunc.contains(it.getBaseId().toString())){
					 
					 it.setSelected(true);
				   }else{
					  it.setSelected(false);
				   }
				 }
		    }
		}
		return this.auditList;
	}
	
	
	public List<BaseItem> getAuditReportList(){
		
		  String date2 = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("date2");
		 
		  String date1 = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("date1");
		
		  DateFormat df = new SimpleDateFormat ("dd.MM.yy");
		 
		  
		  try{
			  
			 this.reportDate2 = df.parse(date2);
			  
		    this.reportDate1 = df.parse(date1);
		    
		   
		 
		  }catch(Exception e){
			  log.info("aFuncManager:getAuditReportList:parseError:"+e);
		  }
		  
		  if(this.reportDate1!=null && this.reportDate2!=null){
		  
		    if(auditReportList==null){
			  invokeLocal("listReport", 0, 0, null);
	  	    }
		  
		  }
		  return this.auditReportList;
	}
	

	public List<AuditFuncItem> getAuditReportCubeList(){
		try{
			if(this.auditReportCubeList==null){
				 invokeLocal("listReportCube", 0, 0, null);
			}
		}catch(Exception e){
			 log.error("aFuncManager:getAuditReportCubeList:Error:"+e);
		}
		return this.auditReportCubeList;
	}
	
	public void invokeLocal(String type, int firstRow, int numberOfRows,
	           String sessionId) {
		try{
			 String orderQuery=null;
			 log.info("AFuncManager:invokeLocal");
			 
			 AFuncStateHolder aFuncStateHolder = (AFuncStateHolder)
					  Component.getInstance("aFuncStateHolder",ScopeType.SESSION);
			 Map<String, String> filterMap = aFuncStateHolder.getColumnFilterValues();
			 String st=null;
			 
			 if("list".equals(type)){
				 log.info("AFunc:invokeLocal:list:01");
				 
				
				 Set<Map.Entry<String, String>> set = aFuncStateHolder.getSortOrders().entrySet();
                 for (Map.Entry<String, String> me : set) {
      		        
      		       if(orderQuery==null){
      		    	 orderQuery="order by "+me.getKey()+" "+me.getValue();
      		       }else{
      		    	 orderQuery=orderQuery+", "+me.getKey()+" "+me.getValue();  
      		       }
      		     }
                 log.info("AFunc:invokeLocal:list:orderQuery:"+orderQuery);
                 
                 if(filterMap!=null){
    	    		 Set<Map.Entry<String, String>> setFilterAFunc = filterMap.entrySet();
    	              for (Map.Entry<String, String> me : setFilterAFunc) {
    	            
    	           //у нас act_dat_value переведена в строку уже в запросе
    	   		       if(me.getKey().equals("arm_id")){ 
      	        		  st=(st!=null?st+" and " :" ")+me.getKey()+" = "+me.getValue();
      	        	 }else{
    	        		//делаем фильтр на начало
    	            	  st=(st!=null?st+" and " :"")+" lower("+me.getKey()+") like lower('"+me.getValue()+"%') ";
    	        	  }
    	            }
    	    	  }
                 log.info("AFunc:invokeLocal:list:filterQuery:"+st);
                 
				
                List<Object[]> lo = null;  
                 
                AcUser au = (AcUser) Component.getInstance("currentUser",ScopeType.SESSION); 
	    		 
 	    		if(au.getAllowedSys()!=null){
                 
                  lo = entityManager.createNativeQuery(
                		 "select t1.act_id, t1.act_dat_value, t1.usr_fio, t1.arm_name, t1.act_name "+
                         "from "+ 
                         "(select AL.ID_SRV act_id, "+  
                         "AL.DATE_ACTION act_dat, to_char(AL.DATE_ACTION, 'DD.MM.YY HH24:MI:SS') act_dat_value, "+ 
                         "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO) usr_fio, "+ 
                         "ARM.ID_SRV arm_id, ARM.FULL_ arm_name, ACT.FULL_ act_name "+ 
                         "from ACTIONS_LOG_KNL_T al, "+ 
                         "AC_IS_BSS_T arm, "+ 
                         "ACTIONS_BSS_T act, "+ 
                         "AC_USERS_KNL_T AU_FULL, "+ 
                         "ISP_BSS_T cl_usr_full, "+
                         "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "+ 
                         "from ISP_BSS_T cl_usr, "+ 
                         "AC_USERS_KNL_T au "+ 
                         "where AU.UP_SIGN_USER  = CL_usr.SIGN_OBJECT "+ 
                         "group by CL_usr.SIGN_OBJECT) t2 "+ 
                         "where ACT.ID_SRV=AL.UP_ACTIONS "+ 
                         "and ACT.UP_IS=ARM.ID_SRV "+ 
                         "and AU_FULL.UP_SIGN_USER=t2.CL_USR_CODE(+) "+ 
                         "and AU_FULL.ID_SRV=AL.UP_USERS "+ 
                         "and CL_USR_FULL.ID_SRV(+)=t2.CL_USR_ID "+
                         (au.getIsAccOrgManagerValue() ? "and au_full.UP_SIGN = "+au.getUpSign() : "" )+
                         ") t1 "+
                         "where arm_id in (:idsArm) "+
                         (st!=null ? " and "+st :" ")+
                         (orderQuery!=null ? orderQuery+", act_id desc " : " order by act_id desc "))
                          .setFirstResult(firstRow)
                          .setMaxResults(numberOfRows)
                          .setParameter("idsArm", au.getAllowedSys())
        		          .getResultList();
 	    		}else{
 	    			
 	                 lo = entityManager.createNativeQuery(
 	                		 "select t1.act_id, t1.act_dat_value, t1.usr_fio, t1.arm_name, t1.act_name "+
 	                         "from "+ 
 	                         "(select AL.ID_SRV act_id, "+  
 	                         "AL.DATE_ACTION act_dat, to_char(AL.DATE_ACTION, 'DD.MM.YY HH24:MI:SS') act_dat_value, "+ 
 	                         "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO) usr_fio, "+ 
 	                         "ARM.ID_SRV arm_id, ARM.FULL_ arm_name, ACT.FULL_ act_name "+ 
 	                         "from ACTIONS_LOG_KNL_T al, "+ 
 	                         "AC_IS_BSS_T arm, "+ 
 	                         "ACTIONS_BSS_T act, "+ 
 	                         "AC_USERS_KNL_T AU_FULL, "+ 
 	                         "ISP_BSS_T cl_usr_full, "+
 	                         "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "+ 
 	                         "from ISP_BSS_T cl_usr, "+ 
 	                         "AC_USERS_KNL_T au "+ 
 	                         "where AU.UP_SIGN_USER  = CL_usr.SIGN_OBJECT "+ 
 	                         "group by CL_usr.SIGN_OBJECT) t2 "+ 
 	                         "where ACT.ID_SRV=AL.UP_ACTIONS "+ 
 	                         "and ACT.UP_IS=ARM.ID_SRV "+ 
 	                         "and AU_FULL.UP_SIGN_USER=t2.CL_USR_CODE(+) "+ 
 	                         "and AU_FULL.ID_SRV=AL.UP_USERS "+ 
 	                         "and CL_USR_FULL.ID_SRV(+)=t2.CL_USR_ID "+
 	                        (au.getIsAccOrgManagerValue() ? "and au_full.UP_SIGN = "+au.getUpSign() : "" )+
 	                         ") t1 "+
 	                         (st!=null ? " where "+st :" ")+
 	                         (orderQuery!=null ? orderQuery+", act_id desc " : " order by act_id desc "))
 	                          .setFirstResult(firstRow)
 	                          .setMaxResults(numberOfRows)
 	        		          .getResultList();
 	    			
 	    		}
                 
                 auditList = new ArrayList<BaseItem>();
                 
                 for(Object[] objectArray :lo){
                	 
                	 try{ 
                		 
                      ActionsLogKnlT al = new ActionsLogKnlT();
                      
                      al.setIdSrv(new Long(objectArray[0].toString()));
                      al.setDateActionValue(objectArray[1].toString());
                      al.setUserName(objectArray[2]!=null?objectArray[2].toString():"");
                      al.setIsName(objectArray[3]!=null?objectArray[3].toString():"");
                      al.setActName(objectArray[4]!=null?objectArray[4].toString():"");

                      auditList.add(al);
                      
              	   }catch(Exception e1){
              		   log.error("AFunc:invokeLocal:for:error:"+e1);
              	   }
                 }
                 
             log.info("AFunc:invokeLocal:list:02");
  
			 } else if("count".equals(type)){
				 log.info("AFuncList:count:01");
				
				 
                 if(filterMap!=null){
    	    		 Set<Map.Entry<String, String>> setFilterAFunc = filterMap.entrySet();
    	              for (Map.Entry<String, String> me : setFilterAFunc) {
    	            
    	            	  //у нас act_dat_value переведена в строку уже в запросе
    	   		       if(me.getKey().equals("arm_id")){ 
          	        	 st=(st!=null?st+" and " :" ")+me.getKey()+" = "+me.getValue();
          	         }else{
    	        		//делаем фильтр на начало
    	            	  st=(st!=null?st+" and " :"")+" lower("+me.getKey()+") like lower('"+me.getValue()+"%') ";
    	        	  }
    	              }
    	    	  }
                 log.info("AFunc:invokeLocal:count:filterQuery:"+st);
              
                 
                 AcUser au = (AcUser) Component.getInstance("currentUser",ScopeType.SESSION); 
	    		 
  	    		if(au.getAllowedSys()!=null){
  	    			
  	    		  auditCount = ((java.math.BigDecimal)entityManager.createNativeQuery(
                		         "select count(*) "+
                				 "from "+ 
                                 "(select AL.ID_SRV act_id, "+  
                                 "AL.DATE_ACTION act_dat, to_char(AL.DATE_ACTION, 'DD.MM.YY HH24:MI:SS') act_dat_value, "+ 
                                 "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO) usr_fio, "+ 
                                 "ARM.ID_SRV arm_id, ARM.FULL_ arm_name, ACT.FULL_ act_name "+ 
                                 "from ACTIONS_LOG_KNL_T al, "+ 
                                 "AC_IS_BSS_T arm, "+ 
                                 "ACTIONS_BSS_T act, "+ 
                                 "AC_USERS_KNL_T AU_FULL, "+ 
                                 "ISP_BSS_T cl_usr_full, "+
                                 "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "+ 
                                 "from ISP_BSS_T cl_usr, "+ 
                                 "AC_USERS_KNL_T au "+ 
                                 "where AU.UP_SIGN_USER  = CL_usr.SIGN_OBJECT "+ 
                                 "group by CL_usr.SIGN_OBJECT) t2 "+ 
                                 "where ACT.ID_SRV=AL.UP_ACTIONS "+ 
                                 "and ACT.UP_IS=ARM.ID_SRV "+ 
                                 "and AU_FULL.UP_SIGN_USER=t2.CL_USR_CODE(+) "+ 
                                 "and AU_FULL.ID_SRV=AL.UP_USERS "+ 
                                 "and CL_USR_FULL.ID_SRV(+)=t2.CL_USR_ID "+
                                 ") t1 "+
                                 (au.getIsAccOrgManagerValue() ? "and au_full.UP_SIGN = "+au.getUpSign() : "" )+
                                 "where arm_id in (:idsArm) "+
                         (st!=null ? " and "+st :" "))
                         .setParameter("idsArm", au.getAllowedSys())
                         .getSingleResult()).longValue();
  	    		}else{
  	    			 auditCount = ((java.math.BigDecimal)entityManager.createNativeQuery(
            		         "select count(*) "+
            				 "from "+ 
                             "(select AL.ID_SRV act_id, "+  
                             "AL.DATE_ACTION act_dat, to_char(AL.DATE_ACTION, 'DD.MM.YY HH24:MI:SS') act_dat_value, "+ 
                             "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO) usr_fio, "+ 
                             "ARM.ID_SRV arm_id, ARM.FULL_ arm_name, ACT.FULL_ act_name "+ 
                             "from ACTIONS_LOG_KNL_T al, "+ 
                             "AC_IS_BSS_T arm, "+ 
                             "ACTIONS_BSS_T act, "+ 
                             "AC_USERS_KNL_T AU_FULL, "+ 
                             "ISP_BSS_T cl_usr_full, "+
                             "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "+ 
                             "from ISP_BSS_T cl_usr, "+ 
                             "AC_USERS_KNL_T au "+ 
                             "where AU.UP_SIGN_USER  = CL_usr.SIGN_OBJECT "+ 
                             "group by CL_usr.SIGN_OBJECT) t2 "+ 
                             "where ACT.ID_SRV=AL.UP_ACTIONS "+ 
                             "and ACT.UP_IS=ARM.ID_SRV "+ 
                             "and AU_FULL.UP_SIGN_USER=t2.CL_USR_CODE(+) "+ 
                             "and AU_FULL.ID_SRV=AL.UP_USERS "+ 
                             "and CL_USR_FULL.ID_SRV(+)=t2.CL_USR_ID "+
                             (au.getIsAccOrgManagerValue() ? "and au_full.UP_SIGN = "+au.getUpSign() : "" )+
                             ") t1 "+
                     (st!=null ? " where "+st :" "))
                     .getSingleResult()).longValue();
  	    		} 
                 
                 
               log.info("AFunc:invokeLocal:count:02:"+auditCount);
           	 }  else if(type.equals("listReport")){
				 log.info("AFunc:invokeLocal:listReport:01");
                 
				 auditReportList = entityManager.createQuery(
						 "select o from ActionsLogKnlT o  " +
						 "where o.dateAction >= :date1 " +
						 "and o.dateAction <= :date2 " +
						 "order by o.idSrv desc ")
						 .setParameter("date1", this.reportDate1)
    	                 .setParameter("date2", this.reportDate2)
	                     .getResultList();
					 
	             log.info("invokeLocal:listReport:02:size:"+auditReportList.size());
				 
			 }else if(type.equals("listReportCube")){
				 log.info("invokeLocal:listReportCube:01");
                 
				 this.auditReportCubeList=new ArrayList<AuditFuncItem>();
				 int count=0;
				 
				 List<Object[]> lo  = entityManager.createNativeQuery(
					    "нет запроса"
						)
						 .getResultList();
				 
                        
	                    
				 for(Object[] objectArray :lo){
					 AuditFuncItem afi = new AuditFuncItem(
							 objectArray[0]!=null?(String)objectArray[0]:"",
							 objectArray[1]!=null?(String)objectArray[1]:"",
							 objectArray[2]!=null?((java.math.BigDecimal)objectArray[2]).longValue():0L
							 );
					 this.auditReportCubeList.add(afi);
					 count+=(objectArray[2]!=null?((java.math.BigDecimal)objectArray[2]).intValue():0);
				 }
				 
				 this.auditReportCubeCount = new Long(count);
	             log.info("invokeLocal:listReportCube:02:size:"+auditReportList.size());
			 }
		}catch(Exception e){
			  log.error("invokeLocal:error:"+e);
			  evaluteForList=false;
			  FacesMessages.instance().add("ќшибка!");
		}
	}
	
	public void setAuditList(List<BaseItem> auditList){
		this.auditList=auditList;
	}
	
  public Long getAuditReportCubeCount(){
	  return this.auditReportCubeCount;
  }


  
   public void forView(String modelType) {
	   String  aFuncId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	  log.info("forView:aFuncId:"+aFuncId);
	  log.info("forView:modelType:"+modelType);
	  if(aFuncId!=null ){
		  
		   
			if(modelType==null){
		    	return ;
		    }
			
	
		
		 ActionsLogKnlT ar = searchBean(aFuncId);
		 Contexts.getEventContext().set("aFuncBean", ar);
	  }
   }
   
   private  ActionsLogKnlT searchBean(String sessionId){
    	
      if(sessionId!=null){
    	 List<ActionsLogKnlT> aFuncListCached = (List<ActionsLogKnlT>)
				  Component.getInstance("aFuncListCached",ScopeType.SESSION);
    	 
		if(aFuncListCached!=null){
			for(ActionsLogKnlT it : aFuncListCached){
				 
			 
			  if(it.getBaseId().toString().equals(sessionId)){
					 log.info("searchBean_Achtung!!!");
					 return it;
			  }
			}
		 }
	   }
	   return null;
    }
    public Long getAuditCount(){
	   log.info("getAuditCount");
	 
	   invokeLocal("count",0,0,null);
	  
	   return auditCount;
	  
   }
   
     
    public void forViewWord(String fileName){
		log.info("JournManager:forViewWord:01");
		try{
		 
			if(fileName==null || fileName.equals("")){
				fileName="user_actions";
			}
			
		  HttpServletResponse response = (HttpServletResponse)
				  FacesContext.getCurrentInstance().getExternalContext().getResponse();
		  response.setHeader("Content-disposition", "attachment; filename="+fileName+".doc");
	      response.setContentType("application/msword");

		}catch(Exception e){
			log.error("auditReportsManager:forViewWord:error:"+e);
		}
		log.info("auditReportsManager:forViewWord:02");
	}
    
   public int getConnectError(){
	   return connectError;
   }
  
   
   public List <BaseTableItem> getAuditItemsListSelect() {
		 log.info("getAuditItemsListSelect:01");
	
	    AFuncContext ac= new AFuncContext();
		   if( auditItemsListSelect==null){
			   log.info("getAuditItemsListSelect:02");
			   auditItemsListSelect = new ArrayList<BaseTableItem>();
			   
			
			   auditItemsListSelect.add(ac.getAuditItemsMap().get("dateAction"));
			   auditItemsListSelect.add(ac.getAuditItemsMap().get("userName"));
			   auditItemsListSelect.add(ac.getAuditItemsMap().get("isName"));
			   auditItemsListSelect.add(ac.getAuditItemsMap().get("actName"));
		   }
	       return this.auditItemsListSelect;
   }
   
   public void setAuditItemsListSelect(List <BaseTableItem> auditItemsListSelect) {
		    this.auditItemsListSelect=auditItemsListSelect;
   }
   
   public List <BaseTableItem> getAuditItemsListContext() {
	   log.info("orgManager:getAuditItemsListContext");
	   if(auditItemsListContext==null){
		   AFuncContext ac= new AFuncContext();
		   auditItemsListContext = new ArrayList<BaseTableItem>();
		   
		   
		   auditItemsListContext=ac.getAuditItemsCollection();
	   }
	   return this.auditItemsListContext;
   }
      
    
   public void selectRecord(){
	    String  sessionIdAFunc = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	    
	    log.info("selectRecord:sessionIdAFunc="+sessionIdAFunc);
	    
	  
	    List<String>  selRecFunc = (ArrayList<String>)
				  Component.getInstance("selRecaFunc",ScopeType.SESSION);
	    
	    if(selRecFunc==null){
	       selRecFunc = new ArrayList<String>();
	       log.info("selectRecord:01");
	    }
	    
	    
	    ActionsLogKnlT aa = new ActionsLogKnlT();
  	   
	    
	    if(aa!=null){
	     if(selRecFunc.contains(sessionIdAFunc)){
	    	selRecFunc.remove(sessionIdAFunc);
	    	aa.setSelected(false);
	    	log.info("selectRecord:02");
	     }else{
	    	selRecFunc.add(sessionIdAFunc);
	    	aa.setSelected(true);
	    	log.info("selectRecord:03");
	     }
	    Contexts.getSessionContext().set("selRecaFunc", selRecFunc);	
	    
	    Contexts.getEventContext().set("aFuncBean", aa);
	   }
	}
   
public void clearDate(){
	   
	   log.info("aFuncManager:clearDate:archiveParamValue:"+this.archiveParamValue);
	   
	  BufferedWriter bw=null;
	  
	   try{

		   Context ctx = new InitialContext(); 
	    	 
	       BaseParamItem bpiAFunc = new BaseParamItem(ServiceReestrPro.ArchiveAuditFunc.name());
	      
	       bpiAFunc.put("gactiontype", ServiceReestrAction.TASK_RUN.name());
	       
	       bpiAFunc.put("archiveParamValue", this.archiveParamValue);
	       
	       IRemoteFrontageLocal obj = (IRemoteFrontageLocal)ctx.lookup(jndiBinding);
           obj.run(bpiAFunc);
	       
           audit(ResourcesMap.AUDIT_USER, ActionsMap.START);
           
		

		   
	   }catch (Exception e) {
	   	 log.error("aFuncManager:clearDate:ERROR:"+e);
	   }finally{
		   try{
		    if(bw!=null){
			    bw.close  ();
			}
		   }catch (Exception e) {}
	   }
   }
   
public Date getClearDate1(){
	   return this.clearDate1;
}
public void setClearDate1(Date clearDate1){
	   this.clearDate1=clearDate1;
}

   public String getDellMessage() {
	   return dellMessage;
   }
   public void setDellMessage(String dellMessage) {
	   this.dellMessage = dellMessage;
   } 
   
  
  
   public Date getClearDate2(){
	   return this.clearDate2;
   }
   public void setClearDate2(Date clearDate2){
	   this.clearDate2=clearDate2;
   }

public Long getArchiveParamValue() {
	   
	   log.info("aSysManager:getArchiveParamValue:01");
	   
	   if(this.archiveParamValue==null){
		   
		   //!!!
		   this.archiveParamValue=6L;
		   
		   try{
			   List<String> losAFunc = entityManager.createNativeQuery(
			              "select ST.VALUE_PARAM "+
	                      "from SETTINGS_KNL_T st "+
	                      "where ST.SIGN_OBJECT=? ")
	                      .setParameter(1, param_code)
	                      .getResultList();
		    	  
		       if(losAFunc!=null&&!losAFunc.isEmpty()){
		    	   this.archiveParamValue= Long.parseLong(losAFunc.get(0));
		       }
		     
		       log.info("aSysManager:getArchiveParamValue:archiveParamValue:"+this.archiveParamValue);
		    	  
		   }catch(Exception e){
			   log.error("aSysManager:getArchiveParamValue:ERROR:"+e);
		   }
	   }
	   return this.archiveParamValue;
   }
   public void setArchiveParamValue(Long archiveParamValue) {
		this.archiveParamValue = archiveParamValue;
   }
   
   public void audit(ResourcesMap resourcesMap, ActionsMap actionsMap){
	   try{
		   AuditExportData auditExportData = (AuditExportData)Component.getInstance("auditExportData",ScopeType.SESSION);
		   auditExportData.addFunc(resourcesMap.getCode()+":"+actionsMap.getCode());
		   
	   }catch(Exception e){
		   log.error("GroupManager:audit:error:"+e);
	   }
  }
   
   public Boolean getEvaluteForList() {
	
   	log.info("armManager:evaluteForList:01");
   	if(evaluteForList==null){
   		evaluteForList=false;
    	String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
	             .getRequestParameterMap()
	             .get("remoteAudit");
	   log.info("aFuncManager:evaluteForList:remoteAudit:"+remoteAudit);
     	
    	if(remoteAudit!=null&&
    		!"OpenDelFact".equals(remoteAudit)&&
    	   !"OpenCrtFact".equals(remoteAudit)&&	
    	   !"onSelColFact".equals(remoteAudit)&&
    	   !"OpenUpdFact".equals(remoteAudit)&&
    	   
   	       
   	       !"refreshPdFact".equals(remoteAudit)){
    		log.info("aFuncManager:evaluteForList!!!");
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
		   log.info("aFuncManager:evaluteForListFooter:remoteAudit:"+remoteAudit);
	     
	    	if(getEvaluteForList()&&
	    	  
	    	   !"protBeanWord".equals(remoteAudit)&&	
	    	   
	    	   !"clRecAllFact".equals(remoteAudit)&&
	   	       !"selRecAllFact".equals(remoteAudit)&&
	   	       
	   	      // !remoteAudit equals "clSelOneFact"
	   	       !"onSelColSaveFact".equals(remoteAudit)){
	    		  log.info("aFuncManager:evaluteForListFooter!!!");
	   		      evaluteForListFooter=true;
	    	}
	   	 }
	       return evaluteForListFooter;
	   }
   
   public Boolean getEvaluteForBean() {
		
		  
		   	if(evaluteForBean==null){
		   		evaluteForBean=false;
		    	String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("remoteAudit");
			    log.info("aFuncManager:evaluteForBean:remoteAudit:"+remoteAudit);
				String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("sessionId");
			    log.info("aFuncManager:evaluteForBean:sessionId:"+sessionId);
		    	if(sessionId!=null && remoteAudit!=null &&
		    	   (	
		    	    "UpdFact".equals(remoteAudit))||
		    	    "rowSelectFact".equals(remoteAudit)
		    			){
		    	      log.info("aFuncManager:evaluteForBean!!!");
		   		      evaluteForBean=true;
		    	}
		   	 }
		     return evaluteForBean;
		   }

}

