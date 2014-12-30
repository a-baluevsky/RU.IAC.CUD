package iac.grn.infosweb.context.proc.bindnoact;

import iac.cud.infosweb.dataitems.BaseParamItem;
import iac.cud.infosweb.local.service.IHLocal;
import iac.cud.infosweb.local.service.ServiceReestr;
import iac.cud.infosweb.local.service.ServiceReestrAction;
import iac.grn.infosweb.session.audit.actions.ActionsMap;
import iac.grn.infosweb.session.audit.actions.ResourcesMap;
import iac.grn.infosweb.session.audit.export.AuditExportData;
import iac.grn.serviceitems.ProcBNAInfoItem;
import iac.grn.serviceitems.ProcBNAItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;

@Name("procBindNoActManager")
 public class ProcBindNoActManager {

	@Logger private Log log;
	
	private static final String proc_binding_noact_exec_file=System.getProperty("jboss.server.config.dir")+"/"+"proc_binding_noact_exec.properties";
	
	private static final String proc_binding_noact_info_file=System.getProperty("jboss.server.config.dir")+"/"+"proc_binding_noact_info.properties";
		
	private Date startDate;
	
	private Long period=1L;
	
	
	private ProcBNAItem procBNABean;
	
	private ProcBNAInfoItem procBNAInfoBean;
	
	public Date getStartDate(){
		return this.startDate;
	}
	public void setStartDate(Date startDate){
		this.startDate=startDate;
	}
	
	public Long getPeriod(){
		return this.period;
	}
	public void setPeriod(Long period){
		this.period=period;
	}
	
	
	
	@Factory
	public ProcBNAItem getProcBNABean(){
		if(this.procBNABean==null){
			initProcBNABean();
		}
		return this.procBNABean;
	}
	public void setProcBNABean(ProcBNAItem procBNABean){
		this.procBNABean=procBNABean;
	}
	
	@Factory
	public ProcBNAInfoItem getProcBNAInfoBean(){
		 log.info("procBindNoActManager:getProcBNAInfoBean");
		if(this.procBNAInfoBean==null){
			initProcBNAInfoBean();
		}
		return this.procBNAInfoBean;
	}
	public void setProcBNAInfoBean(ProcBNAInfoItem procBNAInfoBean){
		this.procBNAInfoBean=procBNAInfoBean;
	}
	
	public void initProcBNABean(){
		 log.info("procBindNoActManager:initProcBNABean:01");
		 
		 String startDateValue=null, periodValue=null, status=null;
		 Properties properties = new Properties();
		 String path = proc_binding_noact_exec_file;
		 InputStream is = null;
		 
		 String  remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("remoteAudit");
		 log.info("procBindNoActManager:initProcBNABean:remoteAudit:"+remoteAudit);
		
		 procBNABean= new ProcBNAItem();
		 
		 if(remoteAudit!=null && "procInfo".equals(remoteAudit)){
			 // кнопка "обновить" задействована для обновления и Center, и Bottom панелей 
		     // чтобы отобразить изменения, если кто другой запустил/остановил процесс
			
			 log.info("confLogContrManager:initProcBindNoActBean:return");
		 }
		 
		 if(remoteAudit!=null && "procCrt".equals(remoteAudit)){
				 procBNABean.setStatus("passive");
			 return;
		 }
		 if(remoteAudit!=null && "procDel".equals(remoteAudit)){
			 procBNABean.setStatus("active");
			 return;
		 }
		 if(remoteAudit!=null && "procPause".equals(remoteAudit)){
			 procBNABean.setStatus("active");
			 return;
		 }
		 if(remoteAudit!=null && "procRun".equals(remoteAudit)){
			 procBNABean.setStatus("pause");
			 return;
		 }
		 
		 try {
			 DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm");
		    
		   
		     
		     File f=new File(path); 
		     
		     if(f.exists()) { 
		    	 
		    	 properties.load(is=new FileInputStream(f));
		    	 
		    	 startDateValue=properties.getProperty("start_date");
		    	 periodValue=properties.getProperty("period");
		    	 status=properties.getProperty("status");
		    	 
		    	log.info("procBindNoActManager:initProcBNABean:start_date:"+startDateValue);
		    	 log.info("procBindNoActManager:initProcBNABean:period:"+periodValue);
		    	 log.info("procBindNoActManager:initProcBNABean:status:"+status);
		    	 
		    	 if(startDateValue!=null&&periodValue!=null&&status!=null){
		    		 if("active".equals(status)||"pause".equals(status)){
		    			 procBNABean.setStartDate(df.parse(startDateValue));
		    			 procBNABean.setPeriod(new Long(periodValue));
		    		   }
		    		 procBNABean.setStatus(status);
		    		 }
		      }else{
		    	  procBNABean.setStatus("passive");
		      }
		 }catch (Exception e) {
				log.error("procBindNoActManager:initProcBNABean:error:"+e);
		 }finally{
			try {
			  if(is!=null){
			    is.close();
			   }
			} catch (Exception e) {
				log.error("procBindNoActManager:initProcBNABean:finally:is:error:"+e);
			}
	   }    
	}
	
	public void initProcBNAInfoBean(){
		 log.info("procBindNoActManager:initProcBNAInfoBean:01");
		 
		 String execDate=null, execHit=null;
		 Properties properties = new Properties();
		 String path = proc_binding_noact_info_file;
		 InputStream is = null;
		 
		 try {
			 DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm:ss");
		    
		   
		     
		     File f=new File(path); 
		     
		     if(f.exists()) { 
		    	 
		    	 procBNAInfoBean= new ProcBNAInfoItem();
		    	 
		    	 properties.load(is=new FileInputStream(f));
		    	 
		    	 execDate=properties.getProperty("exec_date");
		    	 execHit=properties.getProperty("exec_hit");
		    		 
		    	 log.info("procBindNoActManager:initProcBNAInfoBean:exec_date:"+execDate);
		    	 log.info("procBindNoActManager:initProcBNAInfoBean:exec_hit:"+execHit);
		    		 
		    	 procBNAInfoBean.setExecDate(execDate != null ? df.parse(execDate) : null);
		    	 procBNAInfoBean.setExecHit(execHit != null ? ("true".equals(execHit) ? "Запущен" : "Сбой") : null);
		       	 
		     }
		  }catch (Exception eBna) {
				log.error("procBindNoActManager:initProcBNAInfoBean:error:"+eBna);
		 }finally{
			try {
			  if(is!=null){
			    is.close();
			   }
			} catch (Exception eBna) {
				log.error("procBindNoActManager:initProcBNAInfoBean:finally:is:error:"+eBna);
			}
	   }    
	}
	public synchronized void procCrt(){
		  log.info("procBindNoActManager:procCrt:01");
		  log.info("procBindNoActManager:procCrt:startDate:"+startDate);
		  log.info("procBindNoActManager:procCrt:period:"+period);
		  
		  Properties properties = new Properties();
		  String path = proc_binding_noact_exec_file;
		  OutputStream os = null;
		  
		  if(this.period==null || this.startDate==null){
			  log.info("procBindNoActManager:procCrt:02");
			  return;
		  }
		   
		  try {
			 DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm");
		   
		   
		 	
		     File fBna=new File(path); 
		     
		       properties.setProperty("start_date", df.format(this.startDate));
		       properties.setProperty("period", this.period.toString());
		       properties.setProperty("status", "active");
		       
		       properties.store(os=new FileOutputStream(fBna), null);
		       
		     
    		  
    		 
		       Context ctx = new InitialContext(); 
	 	    	 
 	           BaseParamItem bpiBna = new BaseParamItem();
	    	   bpiBna.put("gactiontype", ServiceReestrAction.PROCESS_START.name());
	    	   
	    	   bpiBna.put("startDate", this.startDate);
	    	   bpiBna.put("period", this.period);
	    	   
	    	   ((IHLocal)ctx.lookup(ServiceReestr.BindingNoAct)).run(bpiBna);
    		   
    		   log.info("procBindNoActManager:procCrt:03");
    	  
    		   forView("procCrt");
    		   
    		   audit(ResourcesMap.PROC_BIND_NOACT, ActionsMap.START); 
    		   
		  }catch (Exception eBna) {
				log.error("procBindNoActManager:procCrt:error:"+eBna);
		  }finally{
			 try {
				if(os!=null){
					 os.close();
				}
			 } catch (Exception eBna) {
				log.error("procBindNoActManager:procCrt:os:error:"+eBna);
			 }
		 }
	}
	public synchronized void procDel(){
		log.info("procBindNoActManager:procDel:01");
		
		InputStream is = null;
 		OutputStream os = null;
 		
		try {
		   Context ctx = new InitialContext();
		   
   		   BaseParamItem bpiBna = new BaseParamItem();
   		   
   		   bpiBna.put("gactiontype", ServiceReestrAction.PROCESS_STOP.name());
 	       
 	       ((IHLocal)ctx.lookup(ServiceReestr.BindingNoAct)).run(bpiBna);
   		   
   		   Properties properties = new Properties();
   		   String path = proc_binding_noact_exec_file;
   		  
   		  
	     
	       
	       File fBna=new File(path); 
	       
	      
	       
	       
	       properties.load(is=new FileInputStream(fBna));
	       properties.setProperty("status", "passive");
	       properties.store(os=new FileOutputStream(fBna), null);
	       
	       forView("procDel");
	       
	       audit(ResourcesMap.PROC_BIND_NOACT, ActionsMap.STOP);
	       
        }catch (Exception eBna) {
				log.error("procBindNoActManager:procDel:error:"+eBna);
	   }finally{
		 try {
			if(os!=null){
				 os.close();
			}
		 } catch (Exception eBna) {
			log.error("procBindNoActManager:procDel:os:error:"+eBna);
		 }
		 try {
			  if(is!=null){
			    is.close();
			  }
		} catch (Exception eBna) {
			log.error("procBindNoActManager:procDel:finally:is:error:"+eBna);
		}
	 }
	}
	public synchronized void procPause(){
		log.info("procBindNoActManager:procPause:01");
		
		InputStream is = null;
 		OutputStream os = null;
 		DateFormat dfBna = new SimpleDateFormat ("dd.MM.yy HH:mm");
 		String startDateValue=null, periodValue=null;
		try {
		   Context ctx = new InitialContext();
		   
   		   BaseParamItem bpi = new BaseParamItem();
	       bpi.put("gactiontype", ServiceReestrAction.PROCESS_STOP.name());
	       ((IHLocal)ctx.lookup(ServiceReestr.BindingNoAct)).run(bpi);
   		   
   		   log.info("procBindNoActManager:procPause:02");
   		   
   		   Properties properties = new Properties();
   		   String path = proc_binding_noact_exec_file;
   		  
   		   
	      
	       
	       File f=new File(path); 
	       
	       if(f.exists()) {
	      
	       
	       
	          properties.load(is=new FileInputStream(f));
	       
	          periodValue=properties.getProperty("period");
	          startDateValue=properties.getProperty("start_date");
	      
	          if(periodValue==null || startDateValue==null){
			    log.info("procBindNoActManager:procRun:02");
			    return;
		      }
	 	
	          this.startDate=dfBna.parse(startDateValue);
	          this.period=new Long(periodValue);
	      
	          properties.setProperty("status", "pause");
	          properties.store(os=new FileOutputStream(f), null);
	       
	          forView("procPause");
	       }
	       
	       audit(ResourcesMap.PROC_BIND_NOACT, ActionsMap.PAUSE);
	       
        }catch (Exception eBna) {
				log.error("procBindNoActManager:procPause:error:"+eBna);
	   }finally{
		 try {
			if(os!=null){
				 os.close();
			}
		 } catch (Exception eBna) {
			log.error("procBindNoActManager:procPause:os:error:"+eBna);
		 }
		 try {
			  if(is!=null){
			    is.close();
			  }
		} catch (Exception eBna) {
			log.error("procBindNoActManager:procDel:finally:is:error:"+eBna);
		}
	 }
	}
	public synchronized void procRun(){
		  log.info("procBindNoActManager:procRun:01");
		  
		  Properties properties = new Properties();
		  String pathBna = proc_binding_noact_exec_file;
		  OutputStream os = null;
		  String startDateValue=null, periodValue=null;
			   
		  try {
			 DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm");
		    
		    
		     
		     File fBna=new File(pathBna); 
		     
		     if(fBna.exists()) {
		    	 
		       properties.load(new FileInputStream(fBna));
		       
		       periodValue=properties.getProperty("period");
		       startDateValue=properties.getProperty("start_date");
		     
		       if(periodValue==null || startDateValue==null){
				  log.info("procBindNoActManager:procRun:02");
				  return;
			   }
		 	 
		       this.startDate=df.parse(startDateValue);
		       this.period=new Long(periodValue);
		      
		       
		       properties.setProperty("status", "active");
		       properties.store(os=new FileOutputStream(fBna), null);
		       
		       Context ctx = new InitialContext();
		         
    		   BaseParamItem bpi = new BaseParamItem();
     	       bpi.put("gactiontype", ServiceReestrAction.PROCESS_START.name());
     	       
     	       bpi.put("startDate", this.startDate);
	    	   bpi.put("period", this.period);
     	       
     	       ((IHLocal)ctx.lookup(ServiceReestr.BindingNoAct)).run(bpi);
    		   
    		   log.info("procBindNoActManager:procRun:03");
    	  
    		   forView("procRun");
    		   
    		   audit(ResourcesMap.PROC_BIND_NOACT, ActionsMap.START);
    		   
    		 }
		  }catch (Exception eBna) {
				log.error("procBindNoActManager:procRun:error:"+eBna);
		  }finally{
			 try {
				if(os!=null){
					 os.close();
				}
			 } catch (Exception eBna) {
				log.error("procBindNoActManager:procRun:os:error:"+eBna);
			 }
		 }
	}
	
	
	private void forView(String typeBna){
	   try {
		   
		   procBNABean= new ProcBNAItem();
		   
		  if(typeBna.equals("procCrt")){
		    this.procBNABean.setPeriod(this.period);
		    this.procBNABean.setStartDate(this.startDate);
		 
		    procBNABean.setStatus("active");
		  }else if (typeBna.equals("procDel")){ 
			  procBNABean.setStatus("passive");
		  }else if (typeBna.equals("procPause")){
			this.procBNABean.setPeriod(this.period);
			this.procBNABean.setStartDate(this.startDate);
			procBNABean.setStatus("pause");
		  }else if (typeBna.equals("procRun")){
			this.procBNABean.setPeriod(this.period);
			this.procBNABean.setStartDate(this.startDate);
			procBNABean.setStatus("active");
		  }
		  
		  Contexts.getEventContext().set("procBNABean", this.procBNABean);
		  
	   }catch (Exception eBna) {
		  log.error("procBindNoActManager:forView:error:"+eBna);
	   }
	}
	
	 public void audit(ResourcesMap resourcesMap, ActionsMap actionsMap){
		   try{
			   AuditExportData auditExportDataBna = (AuditExportData)Component.getInstance("auditExportData",ScopeType.SESSION);
			   auditExportDataBna.addFunc(resourcesMap.getCode()+":"+actionsMap.getCode());
			   
		   }catch(Exception e){
			   log.error("GroupManager:audit:error:"+e);
		   }
	  }
}
