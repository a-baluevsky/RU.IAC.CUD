package iac.grn.infosweb.context.mc.audit.sys;

 
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import iac.cud.infosweb.dataitems.BaseItem;
import iac.cud.infosweb.dataitems.BaseParamItem;
import iac.cud.infosweb.entity.ServicesLogKnlT;
import iac.cud.infosweb.local.service.ServiceReestrAction;
import iac.cud.infosweb.local.service.ServiceReestrPro;
import iac.cud.infosweb.remote.frontage.IRemoteFrontageLocal;
import iac.grn.infosweb.session.audit.actions.ActionsMap;
import iac.grn.infosweb.session.audit.actions.ResourcesMap;
import iac.grn.infosweb.session.audit.export.AuditExportData;
import java.io.BufferedWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.jboss.seam.Component;

import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import iac.grn.serviceitems.BaseTableItem;

import javax.servlet.http.HttpServletResponse;

/**
 * ”правл€ющий Ѕин
 * @author bubnov
 *
 */
@Name("aSysManager")
 public class ASysManager {
	
	 @Logger private Log log;
	
	 @In 
	 EntityManager entityManager;
	 
	/**
     * Ёкспортируема€ сущности 
     * дл€ отображени€
     */
	//private BaseItem usrBean;             !!! ѕроверить !!!
	
	private String dellMessage;
	 
	private List<BaseItem> auditList; 
	
	private List<BaseItem> auditReportList;
	
	private Long auditCount;
	
	private List <BaseTableItem> auditItemsListSelect;
	
	private List <BaseTableItem> auditItemsListContext;
	
	private int connectError=0;
	private Boolean evaluteForList;
	private Boolean evaluteForListFooter;  
	private Boolean evaluteForBean;
	
	private Date clearDate1;
	private Date clearDate2;
	
	private Date reportDate1;
		
	private Date reportDate2;
	
	private static String jndiBinding = "java:global/InfoS-ear/InfoS-ejb/IRemoteFrontage!iac.cud.infosweb.remote.frontage.IRemoteFrontageLocal";
	
	private static String param_code="to_archive_audit_sys";
	 
		
	private Long archiveParamValue=null;
	
	 
	public List<BaseItem> getAuditList(int firstRow, int numberOfRows){
	  String remoteAuditSys = FacesContext.getCurrentInstance().getExternalContext()
	             .getRequestParameterMap()
	             .get("remoteAudit");
	   log.info("auditManager:getAuditList:remoteAudit:"+remoteAuditSys);
	  
	  
	  log.info("getAuditList:firstRow:"+firstRow);
	  log.info("getAuditList:numberOfRows:"+numberOfRows);
	  
	  List<BaseItem> aFuncListCached = (List<BaseItem>)
			  Component.getInstance("aFuncListCached",ScopeType.SESSION);
	  if(auditList==null){
		  log.info("getAuditList:01");
		 	if(("rowSelectFact".equals(remoteAuditSys)||
			    "selRecAllFact".equals(remoteAuditSys)||
			    "clRecAllFact".equals(remoteAuditSys)||
			    "clSelOneFact".equals(remoteAuditSys)||
			    "onSelColSaveFact".equals(remoteAuditSys))&&
			    aFuncListCached!=null){
		 	    	this.auditList=aFuncListCached;
			}else{
				log.info("getAuditList:03");
		    	invokeLocal("list", firstRow, numberOfRows, null);
			    Contexts.getSessionContext().set("aFuncListCached", this.auditList);
			    log.info("getAuditList:03:"+this.auditList.size());
			}
		 	
		 	List<String>  selRecSys = (ArrayList<String>)
					  Component.getInstance("selRecaFunc",ScopeType.SESSION);
		 	if(this.auditList!=null && selRecSys!=null) {
		 		 for(BaseItem it:this.auditList){
				   if(selRecSys.contains(it.getBaseId().toString())){
					 
					 it.setSelected(true);
				   }else{
					  it.setSelected(false);
				   }
				 }
		    }
		}
		return this.auditList;
	}
	public void setAuditList(List<BaseItem> auditList){
		this.auditList=auditList;
	}
	
	public List<BaseItem> getAuditReportList(){
		  String date1 = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("date1");
		  String date2 = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("date2");
		 
		  
		  DateFormat df = new SimpleDateFormat ("dd.MM.yy");
		 
		  
		  try{
		    this.reportDate1 = df.parse(date1);
		    this.reportDate2 = df.parse(date2);
		  }catch(Exception e){
			  log.error("aFuncManager:getAuditReportList:parseError:"+e);
		  }
		  
		  if(this.reportDate1!=null && this.reportDate2!=null){
		  
		    if(auditReportList==null){
			  invokeLocal("listReport", 0, 0, null);
	  	    }
		  
		  }
		  return this.auditReportList;
	}
	
    public void invokeLocal(String type, int firstRow, int numberOfRows,
	           String sessionId) {
		try{
			 String orderQuery=null;
			 DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm:ss");
			 
			 log.info("ASysManager:invokeLocal");
			 
			 ASysStateHolder aSysStateHolder = (ASysStateHolder)
					  Component.getInstance("aSysStateHolder",ScopeType.SESSION);
			 Map<String, String> filterMap = aSysStateHolder.getColumnFilterValues();
			 String st=null;
			 
			 if("list".equals(type)){
				 log.info("ASys:invokeLocal:list:01");
				 
				 Set<Map.Entry<String, String>> set = aSysStateHolder.getSortOrders().entrySet();
                 for (Map.Entry<String, String> me : set) {
      		       log.info("me.getKey+:"+me.getKey());
      		       log.info("me.getValue:"+me.getValue());
      		       
      		       if(orderQuery==null){
      		    	 orderQuery="order by "+me.getKey()+" "+me.getValue();
      		       }else{
      		    	 orderQuery=orderQuery+", "+me.getKey()+" "+me.getValue();  
      		       }
      		     }
                 log.info("ASys:invokeLocal:list:orderQuery:"+orderQuery);
                 
                 if(filterMap!=null){
    	    		 Set<Map.Entry<String, String>> setFilterASys = filterMap.entrySet();
    	              for (Map.Entry<String, String> me : setFilterASys) {
    	            	
    	   		        	  if(me.getKey().equals("crt_date")){  
    	    	      	     	 st=(st!=null?st+" and " :"")+" lower(to_char("+me.getKey()+",'DD.MM.YY HH24:MI:SS')) like lower('"+me.getValue()+"%') ";
    	    	    	  }else{
    	    	        		//делаем фильтр на начало
    	    	            	  st=(st!=null?st+" and " :"")+" lower("+me.getKey()+") like lower('"+me.getValue()+"%') ";
    	    	          }
    	              }
    	    	   }
                 log.info("ASys:invokeLocal:list:filterQuery:"+st);
                 
				
				 
                 List<Object[]> lo = entityManager.createNativeQuery(
                		 "select t1.sys_id, t1.crt_date, t1.serv_name, t1.input_param, t1.RESULT_VALUE, "+ 
                         "t1.IP_ADDRESS, t1.fio "+
                         "from( "+
                         "select SL.ID_SRV sys_id , "+
                         "SL.CREATED crt_date, "+ 
                          "SERV.FULL_ serv_name, SL.INPUT_PARAM input_param, SL.RESULT_VALUE RESULT_VALUE, SL.IP_ADDRESS, "+
                         "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO) fio "+
                         "from SERVICES_LOG_KNL_T sl,  "+
                         "SERVICES_BSS_T serv, "+
                         "AC_USERS_KNL_T AU_FULL, "+
                         "ISP_BSS_T cl_usr_full, "+
                         "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "+ 
                         "from ISP_BSS_T cl_usr, "+ 
                         "AC_USERS_KNL_T au "+ 
                         "where AU.UP_SIGN_USER  = CL_usr.SIGN_OBJECT "+ 
                         "group by CL_usr.SIGN_OBJECT) t2 "+
                         "where SERV.ID_SRV=SL.UP_SERVICES "+
                         "and AU_FULL.UP_SIGN_USER=t2.CL_USR_CODE(+) "+ 
                         "and AU_FULL.ID_SRV (+)=SL.UP_USERS "+
                         "and CL_USR_FULL.ID_SRV(+)=t2.CL_USR_ID "+
                         ") t1"+
                         (st!=null ? " where "+st :" ")+
                         (orderQuery!=null ? orderQuery+", sys_id desc " : " order by sys_id desc "))
                          .setFirstResult(firstRow)
                          .setMaxResults(numberOfRows)
        		          .getResultList();
                 auditList = new ArrayList<BaseItem>();
                 
                 for(Object[] objectArray :lo){
                	 
                	 try{ 
                		 
                	  ServicesLogKnlT sl = new ServicesLogKnlT();
                      
                      sl.setIdSrv(new Long(objectArray[0].toString()));
                      sl.setCreatedValue(df.format((Date)objectArray[1]));
                      sl.setServName(objectArray[2]!=null?objectArray[2].toString():"");
                      sl.setInputParam(objectArray[3]!=null?objectArray[3].toString():"");
                      sl.setResultValue(objectArray[4]!=null?objectArray[4].toString():"");
                      sl.setIpAddress(objectArray[5]!=null?objectArray[5].toString():"");
                      sl.setUserFio(objectArray[6]!=null?objectArray[6].toString():"");
                      
                       auditList.add(sl);
                      
              	   }catch(Exception e1){
              		   log.error("ASys:invokeLocal:for:error:"+e1);
              	   }
                 }
				 
             log.info("ASys:invokeLocal:list:02");
  
			 } else if("count".equals(type)){
				 log.info("ASysList:count:01");
				 
				 if(filterMap!=null){
    	    		 Set<Map.Entry<String, String>> setFilterASys = filterMap.entrySet();
    	              for (Map.Entry<String, String> me : setFilterASys) {
    	              
    	   		   
    	             if(me.getKey().equals("crt_date")){  
  	    	             	 st=(st!=null?st+" and " :"")+" lower(to_char("+me.getKey()+",'DD.MM.YY HH24:MI:SS')) like lower('"+me.getValue()+"%') ";
  	    	    	  }else{
  	    	        		//делаем фильтр на начало
  	    	            	  st=(st!=null?st+" and " :"")+" lower("+me.getKey()+") like lower('"+me.getValue()+"%') ";
  	    	          }  
    	              }
    	    	   }
                 log.info("ASys:invokeLocal:count:filterQuery:"+st);
				 
				
				 
				 auditCount = ((java.math.BigDecimal)entityManager.createNativeQuery(
					        "select count(*) "+ 
			                         "from( "+
			                         "select SL.ID_SRV sys_id , "+
			                         "SL.CREATED crt_date, "+ 
			                       //"to_char(SL.CREATED , 'DD.MM.YY HH24:MI:SS') crt_value, "+ 
			                         "SERV.FULL_ serv_name, SL.INPUT_PARAM input_param, SL.RESULT_VALUE RESULT_VALUE, SL.IP_ADDRESS, "+
			                         "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO) fio "+
			                         "from SERVICES_LOG_KNL_T sl,  "+
			                         "SERVICES_BSS_T serv, "+
			                         "AC_USERS_KNL_T AU_FULL, "+
			                         "ISP_BSS_T cl_usr_full, "+
			                         "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "+ 
			                         "from ISP_BSS_T cl_usr, "+ 
			                         "AC_USERS_KNL_T au "+ 
			                         "where AU.UP_SIGN_USER  = CL_usr.SIGN_OBJECT "+ 
			                         "group by CL_usr.SIGN_OBJECT) t2 "+
			                         "where SERV.ID_SRV=SL.UP_SERVICES "+
			                         "and AU_FULL.UP_SIGN_USER=t2.CL_USR_CODE(+) "+ 
			                         "and AU_FULL.ID_SRV (+)=SL.UP_USERS "+
			                         "and CL_USR_FULL.ID_SRV(+)=t2.CL_USR_ID "+
			                         ") t1"+    
	               (st!=null ? " where "+st :" "))
                   .getSingleResult()).longValue();
                 
               log.info("ASys:invokeLocal:count:02:"+auditCount);
           	 } 
			 else if(type.equals("listReport")){
				 log.info("invokeLocal:listReport:01");
                 
				 auditReportList = entityManager.createQuery(
						 "select o from ServicesLogKnlT o  " +
						 "where o.dateAction >= :date1 " +
						 "and o.dateAction <= :date2 " +
						 "order by o.idSrv desc ")
						 .setParameter("date1", this.reportDate1)
    	                 .setParameter("date2", this.reportDate2)
	                     .getResultList();
					 
	             log.info("ASys:invokeLocal:listReport:02:size:"+auditReportList.size());
			 }
		}catch(Exception e){
			  log.error("ASys:invokeLocal:error:"+e);
			  evaluteForList=false;
			  FacesMessages.instance().add("ќшибка!");
		}
	}


  
   private  ServicesLogKnlT searchBean(String sessionId){
    	
      if(sessionId!=null){
    	 List<ServicesLogKnlT> aFuncListCached = (List<ServicesLogKnlT>)
				  Component.getInstance("aFuncListCached",ScopeType.SESSION);
    	 
		if(aFuncListCached!=null){
			for(ServicesLogKnlT it : aFuncListCached){
				 
			 
			  if(it.getBaseId().toString().equals(sessionId)){
					 log.info("searchBean_Achtung!!!");
					 return it;
			  }
			}
		 }
	   }
	   return null;
    }
   
   public void forView(String modelType) {
	   String  aSysId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	  log.info("forView:aSysId:"+aSysId);
	  log.info("forView:modelType:"+modelType);
	  if(aSysId!=null ){
		  
		   
			if(modelType==null){
		    	return ;
		    }
			
			ServicesLogKnlT ar = searchBean(aSysId);
		 Contexts.getEventContext().set("aFuncBean", ar);
	  }
   }
   
  
   
  
  
    public void forViewWord(String fileNameSys){
		log.info("JournManager:forViewWord:01");
		try{
		 
			if(fileNameSys==null || fileNameSys.equals("")){
				fileNameSys="invoke_services";
			}
			
		  HttpServletResponse response = (HttpServletResponse)
				  FacesContext.getCurrentInstance().getExternalContext().getResponse();
		  response.setHeader("Content-disposition", "attachment; filename="+fileNameSys+".doc");
	      response.setContentType("application/msword");

		}catch(Exception e){
			log.error("auditReportsManager:forViewWord:error:"+e);
		}
		log.info("auditReportsManager:forViewWord:02");
	}
   
    
    public Long getAuditCount(){
	   log.info("getAuditCount");
	 
	   invokeLocal("count",0,0,null);
	  
	   return auditCount;
	  
   }
    
   public int getConnectError(){
	   return connectError;
   }
  
   
   public List <BaseTableItem> getAuditItemsListSelect() {
		 log.info("getAuditItemsListSelect:01");
	
	    ASysContext acSys= new ASysContext();
		   if( auditItemsListSelect==null){
			   log.info("getAuditItemsListSelect:02");
			   auditItemsListSelect = new ArrayList<BaseTableItem>();
			   
			
			   auditItemsListSelect.add(acSys.getAuditItemsMap().get("createdValue"));
			   auditItemsListSelect.add(acSys.getAuditItemsMap().get("servName"));
			   auditItemsListSelect.add(acSys.getAuditItemsMap().get("userFio"));
			   auditItemsListSelect.add(acSys.getAuditItemsMap().get("ipAddress"));
		   }
	       return this.auditItemsListSelect;
   }
   
   public void setAuditItemsListSelect(List <BaseTableItem> auditItemsListSelect) {
		    this.auditItemsListSelect=auditItemsListSelect;
   }
   
   public List <BaseTableItem> getAuditItemsListContext() {
	   log.info("orgManager:getAuditItemsListContext");
	   if(auditItemsListContext==null){
		   ASysContext acSys= new ASysContext();
		   auditItemsListContext = new ArrayList<BaseTableItem>();
		   
		   
		   auditItemsListContext=acSys.getAuditItemsCollection();
	   }
	   return this.auditItemsListContext;
   }
      
    
   public void selectRecord(){
	    String  sessionId = FacesContext.getCurrentInstance().getExternalContext()
		        .getRequestParameterMap()
		        .get("sessionId");
	    log.info("selectRecord:sessionId="+sessionId);
	    
	   //  forVi/ew(/); //!!!
	    List<String>  selRecSys = (ArrayList<String>)
				  Component.getInstance("selRecaFunc",ScopeType.SESSION);
	    
	    if(selRecSys==null){
	       selRecSys = new ArrayList<String>();
	       log.info("selectRecord:01");
	    }
	    
	    
	    ServicesLogKnlT aa = new ServicesLogKnlT();
  	   
	    
	    if(aa!=null){
	     if(selRecSys.contains(sessionId)){
	    	selRecSys.remove(sessionId);
	    	aa.setSelected(false);
	    	log.info("selectRecord:02");
	     }else{
	    	selRecSys.add(sessionId);
	    	aa.setSelected(true);
	    	log.info("selectRecord:03");
	     }
	    Contexts.getSessionContext().set("selRecaFunc", selRecSys);	
	    
	    Contexts.getEventContext().set("aFuncBean", aa);
	   }
	}
   
   public void clearDate(){
	   
	   log.info("aSysManager:clearDate:archiveParamValue:"+this.archiveParamValue);
	   
	   BufferedWriter bw=null;
	 
	   try{

		   Context ctx = new InitialContext(); 
	    	 
	       BaseParamItem bpi = new BaseParamItem(ServiceReestrPro.ArchiveAuditSys.name());
	      
	       bpi.put("gactiontype", ServiceReestrAction.TASK_RUN.name());
	       
	       bpi.put("archiveParamValue", this.archiveParamValue);
	       
	       IRemoteFrontageLocal obj = (IRemoteFrontageLocal)ctx.lookup(jndiBinding);
        		   
          

          
           
	       obj.run(bpi);
	       
           audit(ResourcesMap.AUDIT_SYS, ActionsMap.START);
           
		

		   
	   }catch (Exception eSys) {
	   	 log.error("aSysManager:clearDate:ERROR:"+eSys);
	   }finally{
		   try{
		    if(bw!=null){
			    bw.close  ();
			}
		   }catch (Exception erSys) {
				 log.error("aSysManager:clearDate:ERROR:"+erSys);
		   }
	   }
   }
   
   public String getDellMessage() {
	   return dellMessage;
   }
   public void setDellMessage(String dellMessage) {
	   this.dellMessage = dellMessage;
   } 
   
   public Date getClearDate1(){
	   return this.clearDate1;
   }
   public void setClearDate1(Date clearDate1){
	   this.clearDate1=clearDate1;
   }
  
   public Date getClearDate2(){
	   return this.clearDate2;
   }
   public void setClearDate2(Date clearDate2){
	   this.clearDate2=clearDate2;
   }

  /*
 //  было использовано только дл€ отображени€ параметра
 // <!--h:outputText value="#{aSysManager.archiveParamValue}"/-->
 //  заменено на ввод значени€ Long archiveParamValue
  
   publ/ic Strin/g getArchivePar/amValue(/) {
   pub/lic void setArchive/ParamValue(String archiv/eParamValue) {
*/
   
   public Long getArchiveParamValue() {
	   
	   log.info("aSysManager:getArchiveParamValue:01");
	   
	   if(this.archiveParamValue==null){
		   
		   //!!!
		   this.archiveParamValue=6L;
		   
		   try{
			   List<String> los = entityManager.createNativeQuery(
			              "select ST.VALUE_PARAM "+
	                      "from SETTINGS_KNL_T st "+
	                      "where ST.SIGN_OBJECT=? ")
	                      .setParameter(1, param_code)
	                      .getResultList();
		    	  
		       if(los!=null&&!los.isEmpty()){
		    	   this.archiveParamValue= Long.parseLong(los.get(0));
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
		   AuditExportData auditExportDataSys = (AuditExportData)Component.getInstance("auditExportData",ScopeType.SESSION);
		   auditExportDataSys.addFunc(resourcesMap.getCode()+":"+actionsMap.getCode());
		   
	   }catch(Exception e){
		   log.error("GroupManager:audit:error:"+e);
	   }
  }
   
   public Boolean getEvaluteForList() {
	
   	log.info("armManager:evaluteForList:01");
   	if(evaluteForList==null){
   		evaluteForList=false;
    	String remoteAuditSys = FacesContext.getCurrentInstance().getExternalContext()
	             .getRequestParameterMap()
	             .get("remoteAudit");
	   log.info("aFuncManager:evaluteForList:remoteAudit:"+remoteAuditSys);
     	
    	if(remoteAuditSys!=null&&
    	 
    	   !"OpenCrtFact".equals(remoteAuditSys)&&	
    	   !"OpenUpdFact".equals(remoteAuditSys)&&
    	   !"OpenDelFact".equals(remoteAuditSys)&&
   	       !"onSelColFact".equals(remoteAuditSys)&&
   	       !"refreshPdFact".equals(remoteAuditSys)){
    		log.info("aFuncManager:evaluteForList!!!");
   		    evaluteForList=true;
    	}
   	 }
       return evaluteForList;
   }
   public Boolean getEvaluteForListFooter() {
		
	  
	   	if(evaluteForListFooter==null){
	   		evaluteForListFooter=false;
	    	String remoteAuditSys = FacesContext.getCurrentInstance().getExternalContext()
		             .getRequestParameterMap()
		             .get("remoteAudit");
		   log.info("aFuncManager:evaluteForListFooter:remoteAudit:"+remoteAuditSys);
	     
	    	if(getEvaluteForList()&&
	    	    	
	    	   !"protBeanWord".equals(remoteAuditSys)&&	
	    	    	
	   	       !"selRecAllFact".equals(remoteAuditSys)&&
	   	       !"clRecAllFact".equals(remoteAuditSys)&&
	   	      // !remoteAudit equals "clSelOneFact"
	   	       !"onSelColSaveFact".equals(remoteAuditSys)){
	    		  log.info("aFuncManager:evaluteForListFooter!!!");
	   		      evaluteForListFooter=true;
	    	}
	   	 }
	       return evaluteForListFooter;
	   }
   
   public Boolean getEvaluteForBean() {
		
		  
		   	if(evaluteForBean==null){
		   		evaluteForBean=false;
		    	String remoteAuditSys = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("remoteAudit");
			    log.info("aFuncManager:evaluteForBean:remoteAudit:"+remoteAuditSys);
				String sessionId = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("sessionId");
			    log.info("aFuncManager:evaluteForBean:sessionId:"+sessionId);
		    	if(sessionId!=null && remoteAuditSys!=null &&
		    	   ("rowSelectFact".equals(remoteAuditSys)||	
		    	    "UpdFact".equals(remoteAuditSys))){
		    	      log.info("aFuncManager:evaluteForBean!!!");
		   		      evaluteForBean=true;
		    	}
		   	 }
		     return evaluteForBean;
		   }

}

