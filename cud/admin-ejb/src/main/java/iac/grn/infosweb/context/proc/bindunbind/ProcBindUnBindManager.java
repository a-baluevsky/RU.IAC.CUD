package iac.grn.infosweb.context.proc.bindunbind;

import iac.cud.infosweb.dataitems.BaseParamItem;
import iac.cud.infosweb.local.service.IHLocal;
import iac.cud.infosweb.local.service.ServiceReestr;
import iac.cud.infosweb.local.service.ServiceReestrAction;
import iac.grn.infosweb.session.audit.actions.ActionsMap;
import iac.grn.infosweb.session.audit.actions.ResourcesMap;
import iac.grn.infosweb.session.audit.export.AuditExportData;
import iac.grn.serviceitems.ProcBUBInfoItem;
import iac.grn.serviceitems.ProcBUBItem;

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

@Name("procBindUnBindManager")
 public class ProcBindUnBindManager {

	@Logger private Log log;
	
	private static final String proc_binding_unbind_exec_file=System.getProperty("jboss.server.config.dir")+"/"+"proc_binding_unbind_exec.properties";

	private static final String proc_binding_unbind_info_file=System.getProperty("jboss.server.config.dir")+"/"+"proc_binding_unbind_info.properties";
	
    private ProcBUBItem procBUBBean;
	
	private ProcBUBInfoItem procBUBInfoBean;

	
    private Date startDate;
	
	private Long period=1L;
	
		
	public Long getPeriod(){
		return this.period;
	}
	public void setPeriod(Long period){
		this.period=period;
	}

	
	public Date getStartDate(){
		return this.startDate;
	}
	public void setStartDate(Date startDate){
		this.startDate=startDate;
	}
	
	
	public void setProcBUBBean(ProcBUBItem procBUBBean){
		this.procBUBBean=procBUBBean;
	}
	
	
	@Factory
	public ProcBUBItem getProcBUBBean(){
		if(this.procBUBBean==null){
			initProcBUBBean();
		}
		return this.procBUBBean;
	}

	@Factory
	public ProcBUBInfoItem getProcBUBInfoBean(){
		 log.info("procBindUnBindManager:getProcBUBInfoBean");
		if(this.procBUBInfoBean==null){
			initProcBUBInfoBean();
		}
		return this.procBUBInfoBean;
	}
	public void setProcBUBInfoBean(ProcBUBInfoItem procBUBInfoBean){
		this.procBUBInfoBean=procBUBInfoBean;
	}
	
	public void initProcBUBBean(){
		 log.info("procBindUnBindManager:initProcBUBBean:01");
		 
		 String startDateValue=null, periodValue=null, status=null;
		 Properties properties = new Properties();
		 String pathBindUnBind = proc_binding_unbind_exec_file;
		 InputStream is = null;
		 
		 String  remoteAuditBindUnBind = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("remoteAudit");
		 log.info("procBindUnBindManager:initProcBUBBean:remoteAudit:"+remoteAuditBindUnBind);
		
		 procBUBBean= new ProcBUBItem();
		 
		 if(remoteAuditBindUnBind!=null && "procInfo".equals(remoteAuditBindUnBind)){
			 // кнопка "обновить" задействована для обновления и Center, и Bottom панелей 
		     // чтобы отобразить изменения, если кто другой запустил/остановил процесс
			 
			 log.info("initProcBUBBean");
		 }
		 
		 if(remoteAuditBindUnBind!=null && "procCrt".equals(remoteAuditBindUnBind)){
			 procBUBBean.setStatus("passive");
			 return;
		 }
		 if(remoteAuditBindUnBind!=null && "procDel".equals(remoteAuditBindUnBind)){
			 procBUBBean.setStatus("active");
			 return;
		 }
		 if(remoteAuditBindUnBind!=null && "procPause".equals(remoteAuditBindUnBind)){
			 procBUBBean.setStatus("active");
			 return;
		 }
		 if(remoteAuditBindUnBind!=null && "procRun".equals(remoteAuditBindUnBind)){
			 procBUBBean.setStatus("pause");
			 return;
		 }
		 
		 try {
			 DateFormat dfBub = new SimpleDateFormat ("dd.MM.yy HH:mm");
		    
		    
		     
		     File fBub=new File(pathBindUnBind); 
		     
		     if(fBub.exists()) { 
		    	 
		    	 properties.load(is=new FileInputStream(fBub));
		    	 
		    	 startDateValue=properties.getProperty("start_date");
		    	 periodValue=properties.getProperty("period");
		    	 status=properties.getProperty("status");
		    	 
		    	log.info("procBindUnBindManager:initProcBUBBean:start_date:"+startDateValue);
		    	 log.info("procBindUnBindManager:initProcBUBBean:period:"+periodValue);
		    	 log.info("procBindUnBindManager:initProcBUBBean:status:"+status);
		    	 
		    	 if(startDateValue!=null&&periodValue!=null&&status!=null){
		    		 if("active".equals(status)||"pause".equals(status)){
		    			 procBUBBean.setStartDate(dfBub.parse(startDateValue));
		    			 procBUBBean.setPeriod(new Long(periodValue));
		    		   }
		    		 procBUBBean.setStatus(status);
		    		 }
		      }else{
		    	  procBUBBean.setStatus("passive");
		      }
		 }catch (Exception eBub) {
				log.error("procBindUnBindManager:initProcBUBBean:error:"+eBub);
		 }finally{
			try {
			  if(is!=null){
			    is.close();
			   }
			} catch (Exception eBub) {
				log.error("procBindUnBindManager:initProcBUBBean:finally:is:error:"+eBub);
			}
	   }    
	}
	
	public void initProcBUBInfoBean(){
		 log.info("procBindUnBindManager:initProcBUBInfoBean:01");
		 
		 String execDate=null, execHit=null;
		 Properties properties = new Properties();
		 String path = proc_binding_unbind_info_file;
		 InputStream is = null;
		 
		 try {
			 DateFormat dfBindUnBind = new SimpleDateFormat ("dd.MM.yy HH:mm:ss");
		    
		    
		     
		     File f=new File(path); 
		     
		     if(f.exists()) { 
		    	 
		    	 procBUBInfoBean= new ProcBUBInfoItem();
		    	 
		    	 properties.load(is=new FileInputStream(f));
		    	 
		    	 execDate=properties.getProperty("exec_date");
		    	 execHit=properties.getProperty("exec_hit");
		    	 
		    	 log.info("procBindUnBindManager:initProcBUBInfoBean:exec_date:"+execDate);
		    	 log.info("procBindUnBindManager:initProcBUBInfoBean:exec_hit:"+execHit);
		    	 
		    	 procBUBInfoBean.setExecDate(execDate != null ? dfBindUnBind.parse(execDate) : null);
		    	 procBUBInfoBean.setExecHit(execHit != null ? ("true".equals(execHit) ? "Запущен" : "Сбой") : null);
		       	 
		     }
		  }catch (Exception eBub) {
				log.error("procBindUnBindManager:initProcBUBInfoBean:error:"+eBub);
		 }finally{
			try {
			  if(is!=null){
			    is.close();
			   }
			} catch (Exception eBub) {
				log.error("procBindUnBindManager:initProcBUBInfoBean:finally:is:error:"+eBub);
			}
	   }    
	}
	public synchronized void procCrt(){
		  log.info("procBindUnBindManager:procCrt:01");
			  
		  Properties properties = new Properties();
		  String pathBub = proc_binding_unbind_exec_file;
		  OutputStream os = null;
		  
		if(this.period==null || this.startDate==null){
			  log.info("procBindUnBindManager:procCrt:02");
			  return;
		  }
		   
		  try {
			 DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm");
		    
		    
		 	
		     File fBub=new File(pathBub); 
		     
		       properties.setProperty("start_date", df.format(this.startDate));
		       properties.setProperty("period", this.period.toString());
		       properties.setProperty("status", "active");
		       
		       properties.store(os=new FileOutputStream(fBub), null);
		       
		     
    		  
    		 
		       Context ctx = new InitialContext(); 
	 	    	 
 	           BaseParamItem bpiBub = new BaseParamItem();
	    	   bpiBub.put("gactiontype", ServiceReestrAction.PROCESS_START.name());
	    	  
	    	   bpiBub.put("startDate", this.startDate);
	    	   bpiBub.put("period", this.period);
	    	   
	    	   ((IHLocal)ctx.lookup(ServiceReestr.BindingUnBind)).run(bpiBub);
    		   
    		   log.info("procBindUnBindManager:procCrt:03");
    	  
    		   forView("procCrt");
    		   
    		   audit(ResourcesMap.PROC_BIND_UNBIND, ActionsMap.START); 
    		   
		  }catch (Exception eBub) {
				log.error("procBindUnBindManager:procCrt:error:"+eBub);
		  }finally{
			 try {
				if(os!=null){
					 os.close();
				}
			 } catch (Exception eBub) {
				log.error("procBindUnBindManager:procCrt:os:error:"+eBub);
			 }
		 }
	}
	public synchronized void procDel(){
		log.info("procBindUnBindManager:procDel:01");
		
		InputStream is = null;
 		OutputStream os = null;
 		
		try {
		   Context ctx = new InitialContext();
		   
   		   BaseParamItem bpiBub = new BaseParamItem();
   		   
   		   bpiBub.put("gactiontype", ServiceReestrAction.PROCESS_STOP.name());
 	       
 	       ((IHLocal)ctx.lookup(ServiceReestr.BindingUnBind)).run(bpiBub);
   		   
   		   Properties properties = new Properties();
   		   String path = proc_binding_unbind_exec_file;
   		  
   		   
	      
	       
   		    File f=new File(path); 
   		
	      
	       
	       
	       properties.load(is=new FileInputStream(f));
	       properties.setProperty("status", "passive");
	       properties.store(os=new FileOutputStream(f), null);
	       
	       forView("procDel");
	       
	       audit(ResourcesMap.PROC_BIND_UNBIND, ActionsMap.STOP);
	       
        }catch (Exception eBub) {
				log.error("procBindUnBindManager:procDel:error:"+eBub);
	   }finally{
		 try {
			if(os!=null){
				 os.close();
			}
		 } catch (Exception eBub) {
			log.error("procBindUnBindManager:procDel:os:error:"+eBub);
		 }
		 try {
			  if(is!=null){
			    is.close();
			  }
		} catch (Exception eBub) {
			log.error("procBindUnBindManager:procDel:finally:is:error:"+eBub);
		}
	 }
	}
	public synchronized void procPause(){
		log.info("procBindUnBindManager:procPause:01");
		
		InputStream is = null;
 		OutputStream os = null;
 		DateFormat dfBub = new SimpleDateFormat ("dd.MM.yy HH:mm");
 		String startDateValue=null, periodValue=null;
		try {
		   Context ctx = new InitialContext();
		   
   		   BaseParamItem bpiBub = new BaseParamItem();
	       bpiBub.put("gactiontype", ServiceReestrAction.PROCESS_STOP.name());
	       ((IHLocal)ctx.lookup(ServiceReestr.BindingUnBind)).run(bpiBub);
   		   
   		   log.info("procBindUnBindManager:procPause:02");
   		   
   		   Properties properties = new Properties();
   		   String path = proc_binding_unbind_exec_file;
   		  
   		  
	     
	       
	       File fBub=new File(path); 
	       
	       if(fBub.exists()) {
	      
	       
	       
	          properties.load(is=new FileInputStream(fBub));
	       
	          periodValue=properties.getProperty("period");
	          startDateValue=properties.getProperty("start_date");
	      
	         if(periodValue==null || startDateValue==null){
			    log.info("procBindUnBindManager:procRun:02");
			    return;
		      }
	 	
	          this.startDate=dfBub.parse(startDateValue);
	          this.period=new Long(periodValue);
	     
	          properties.setProperty("status", "pause");
	          properties.store(os=new FileOutputStream(fBub), null);
	       
	          forView("procPause");
	       }
	       
	       audit(ResourcesMap.PROC_BIND_UNBIND, ActionsMap.PAUSE);
	       
        }catch (Exception eBubPause) {
				log.error("procBindUnBindManager:procPause:error:"+eBubPause);
	   }finally{
		 try {
			if(os!=null){
				 os.close();
			}
		 } catch (Exception eBubPause) {
			log.error("procBindUnBindManager:procPause:os:error:"+eBubPause);
		 }
		 try {
			  if(is!=null){
			    is.close();
			  }
		} catch (Exception eBubPause) {
			log.error("procBindUnBindManager:procDel:finally:is:error:"+eBubPause);
		}
	 }
	}
	public synchronized void procRun(){
		  log.info("procBindUnBindManager:procRun:01");
		  
		  Properties properties = new Properties();
		  String path = proc_binding_unbind_exec_file;
		  OutputStream os = null;
		  String startDateValue=null, periodValue=null;
			   
		  try {
			 DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm");
		    
		    
		     
		     File fBubRun=new File(path); 
		     
		     if(fBubRun.exists()) {
		    	 
		       properties.load(new FileInputStream(fBubRun));
		       
		       periodValue=properties.getProperty("period");
		       startDateValue=properties.getProperty("start_date");
		     
		       if(periodValue==null || startDateValue==null){
				  log.info("procBindUnBindManager:procRun:02");
				  return;
			   }
		 	 
		       this.startDate=df.parse(startDateValue);
		       this.period=new Long(periodValue);
		       
		       
		       properties.setProperty("status", "active");
		       properties.store(os=new FileOutputStream(fBubRun), null);
		       
		       Context ctx = new InitialContext();
		        
    		   BaseParamItem bpiBubRun = new BaseParamItem();
     	       bpiBubRun.put("gactiontype", ServiceReestrAction.PROCESS_START.name());
     	       
     	       bpiBubRun.put("startDate", this.startDate);
	    	   bpiBubRun.put("period", this.period);
     	       
     	       ((IHLocal)ctx.lookup(ServiceReestr.BindingUnBind)).run(bpiBubRun);
    		   
    		   log.info("procBindUnBindManager:procRun:03");
    	  
    		   forView("procRun");
    		   
    		   audit(ResourcesMap.PROC_BIND_UNBIND, ActionsMap.START);
    		   
    		 }
		  }catch (Exception eBubRun) {
				log.error("procBindUnBindManager:procRun:error:"+eBubRun);
		  }finally{
			 try {
				if(os!=null){
					 os.close();
				}
			 } catch (Exception eBubRun) {
				log.error("procBindUnBindManager:procRun:os:error:"+eBubRun);
			 }
		 }
	}
	
	
	private void forView(String typeBub){
	   try {
		   
		   procBUBBean= new ProcBUBItem();
		   
		  if(typeBub.equals("procCrt")){
		    this.procBUBBean.setPeriod(this.period);
		    this.procBUBBean.setStartDate(this.startDate);
		 
		    procBUBBean.setStatus("active");
		  }else if (typeBub.equals("procDel")){ 
			  procBUBBean.setStatus("passive");
		  }else if (typeBub.equals("procPause")){
			this.procBUBBean.setPeriod(this.period);
			this.procBUBBean.setStartDate(this.startDate);
			procBUBBean.setStatus("pause");
		  }else if (typeBub.equals("procRun")){
			this.procBUBBean.setPeriod(this.period);
			this.procBUBBean.setStartDate(this.startDate);
			procBUBBean.setStatus("active");
		  }
			  
		  Contexts.getEventContext().set("procBUBBean", this.procBUBBean);
		  
	   }catch (Exception e) {
		  log.error("procBindUnBindManager:forView:error:"+e);
	   }
	}
	
	 public void audit(ResourcesMap resourcesMap, ActionsMap actionsMap){
		   try{
			   AuditExportData auditExportDataBub = (AuditExportData)Component.getInstance("auditExportData",ScopeType.SESSION);
			   auditExportDataBub.addFunc(resourcesMap.getCode()+":"+actionsMap.getCode());
			   
			   log.info("procBindUnBindManager:audit:"+(resourcesMap.getCode()+":"+actionsMap.getCode()));
			   
		   }catch(Exception e){
			   log.error("procBindUnBindManager:audit:error:"+e);
		   }
	  }
}
