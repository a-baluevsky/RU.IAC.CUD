package iac.grn.infosweb.context.proc.archafunc;

import iac.cud.infosweb.dataitems.BaseParamItem;
import iac.cud.infosweb.local.service.IHLocal;
import iac.cud.infosweb.local.service.ServiceReestr;
import iac.cud.infosweb.local.service.ServiceReestrAction;
import iac.grn.infosweb.session.audit.actions.ActionsMap;
import iac.grn.infosweb.session.audit.actions.ResourcesMap;
import iac.grn.infosweb.session.audit.export.AuditExportData;
import iac.grn.serviceitems.ProcAAFInfoItem;
import iac.grn.serviceitems.ProcAAFItem;

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

@Name("procArchAFuncManager")
 public class ProcArchAFuncManager {

	@Logger private Log log;
	
	private static final String proc_aafunc_exec_file=System.getProperty("jboss.server.config.dir")+"/"+"proc_aafunc_exec.properties";
	
	private static final String proc_aafunc_info_file=System.getProperty("jboss.server.config.dir")+"/"+"proc_aafunc_info.properties";
	
	private Date startDate;
	
	private Long period=1L;
	
	
	private ProcAAFItem procAAFBean;
	
	private ProcAAFInfoItem procAAFInfoBean;
	
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
	public ProcAAFItem getProcAAFBean(){
		if(this.procAAFBean==null){
			initProcAAFBean();
		}
		return this.procAAFBean;
	}
	public void setProcAAFBean(ProcAAFItem procAAFBean){
		this.procAAFBean=procAAFBean;
	}
	
	@Factory
	public ProcAAFInfoItem getProcAAFInfoBean(){
		 log.info("__confLogContrManager:getProcAAFInfoBean");
		if(this.procAAFInfoBean==null){
			initProcAAFInfoBean();
		}
		return this.procAAFInfoBean;
	}
	public void setProcAAFInfoBean(ProcAAFInfoItem procAAFInfoBean){
		this.procAAFInfoBean=procAAFInfoBean;
	}
	
	public void initProcAAFBean(){
		 log.info("confLogContrManager:initProcAAFBean:01");
		 
		 String status=null;
		 Properties properties = new Properties();
		 String path = proc_aafunc_exec_file;
		 InputStream is = null;
		 
		 String  remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("remoteAudit");
		 log.info("confLogContrManager:initProcAAFBean:remoteAudit:"+remoteAudit);
		
		 procAAFBean= new ProcAAFItem();
		 
		 if(remoteAudit!=null && "procInfo".equals(remoteAudit)){
			 // кнопка "обновить" задействована для обновления и Center, и Bottom панелей 
		     // чтобы отобразить изменения, если кто другой запустил/остановил процесс
			
			 log.info("confLogContrManager:initProcAAFBean:return");
		 }
		 
		 if(remoteAudit!=null && "procCrt".equals(remoteAudit)){
			 procAAFBean.setStatus("passive");
			 return;
		 }
		 if(remoteAudit!=null && "procDel".equals(remoteAudit)){
			 procAAFBean.setStatus("active");
			 return;
		 }
		 if(remoteAudit!=null && "procPause".equals(remoteAudit)){
			 procAAFBean.setStatus("active");
			 return;
		 }
		 if(remoteAudit!=null && "procRun".equals(remoteAudit)){
			 procAAFBean.setStatus("pause");
			 return;
		 }
		 
		 try {
			    
			 File f=new File(path); 
			 
		     if(f.exists()) { 
		    	 
		    	 properties.load(is=new FileInputStream(f));
		    	 
		    		 status=properties.getProperty("status");
		    	 
		    	 log.info("confLogContrManager:initProcAAFBean:status:"+status);
		    	 
		    	 if(status!=null){
		    		 if("active".equals(status)||"pause".equals(status)){
		    			   }
		    		 procAAFBean.setStatus(status);
		    		 }
		      }else{
		    	  procAAFBean.setStatus("passive");
		      }
		 }catch (Exception e) {
				log.error("confLogContrManager:initProcAAFBean:error:"+e);
		 }finally{
			try {
			  if(is!=null){
			    is.close();
			   }
			} catch (Exception e) {
				log.error("confLogContrManager:initProcAAFBean:finally:is:error:"+e);
			}
	   }    
	}
	
	public void initProcAAFInfoBean(){
		 log.info("confLogContrManager:initProcAAFInfoBean:01");
		 
		 String execDate=null, execHit=null;
		 Properties properties = new Properties();
		 String path = proc_aafunc_info_file;
		 InputStream is = null;
		 
		 try {
			 DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm:ss");
		    
		    
		     
			 File f=new File(path); 
			 
		     if(f.exists()) { 
		    	 
		    	 procAAFInfoBean= new ProcAAFInfoItem();
		    	 
		    	 properties.load(is=new FileInputStream(f));
		    	 
		    	 execDate=properties.getProperty("exec_date");
		    	 execHit=properties.getProperty("exec_hit");
		    	 
		    	 log.info("confLogContrManager:initProcAAFInfoBean:exec_date:"+execDate);
		    	 log.info("confLogContrManager:initProcAAFInfoBean:exec_hit:"+execHit);
		    		 
		    	 procAAFInfoBean.setExecDate(execDate != null ? df.parse(execDate) : null);
		    	 procAAFInfoBean.setExecHit(execHit != null ? ("true".equals(execHit) ? "Запущен" : "Сбой") : null);
		       	 
		     }
		  }catch (Exception e) {
				log.error("confLogContrManager:initProcAAFInfoBean:error:"+e);
		 }finally{
			try {
			  if(is!=null){
			    is.close();
			   }
			} catch (Exception e) {
				log.error("confLogContrManager:initProcAAFInfoBean:finally:is:error:"+e);
			}
	   }    
	}
	public synchronized void procCrt(){
		  log.info("confLogContrManager:procCrt:01");
			  
		  Properties properties = new Properties();
		  String path = proc_aafunc_exec_file;
		  OutputStream os = null;
		  
		
		   
		  try {
		 	
		     File f=new File(path); 
		     
		        properties.setProperty("status", "active");
		       
		       properties.store(os=new FileOutputStream(f), null);
		       
		     
    		  
    		 
		       Context ctx = new InitialContext(); 
	 	    	 
 	           BaseParamItem bpiCrt = new BaseParamItem();
	    	   bpiCrt.put("gactiontype", ServiceReestrAction.PROCESS_START.name());
	    	   ((IHLocal)ctx.lookup(ServiceReestr.ArchiveAuditFunc)).run(bpiCrt);
    		   
    		   log.info("confLogContrManager:procCrt:03");
    	  
    		   forView("procCrt");
    		   
    		   audit(ResourcesMap.PROC_ARCH_AUDIT_USER, ActionsMap.START); 
    		   
		  }catch (Exception eCrt) {
				log.error("confLogContrManager:procCrt:error:"+eCrt);
		  }finally{
			 try {
				if(os!=null){
					 os.close();
				}
			 } catch (Exception eCrt) {
				log.error("confLogContrManager:procCrt:os:error:"+eCrt);
			 }
		 }
	}
	public synchronized void procDel(){
		log.info("confLogContrManager:procDel:01");
		
		InputStream is = null;
 		OutputStream os = null;
 		
		try {
		   Context ctx = new InitialContext();
		    
   		   BaseParamItem bpi = new BaseParamItem();
   		   
   		   bpi.put("gactiontype", ServiceReestrAction.PROCESS_STOP.name());
 	       
 	       ((IHLocal)ctx.lookup(ServiceReestr.ArchiveAuditFunc)).run(bpi);
   		   
   		   Properties properties = new Properties();
   		   String path = proc_aafunc_exec_file;
   		  
   		  
	      
	       
   		   File f=new File(path); 
   		
	      
	       
	       
	       properties.load(is=new FileInputStream(f));
	       properties.setProperty("status", "passive");
	       properties.store(os=new FileOutputStream(f), null);
	       
	       forView("procDel");
	       
	       audit(ResourcesMap.PROC_ARCH_AUDIT_USER, ActionsMap.STOP);
	       
        }catch (Exception eDel) {
				log.error("confLogContrManager:procDel:error:"+eDel);
	   }finally{
		 try {
			if(os!=null){
				 os.close();
			}
		 } catch (Exception eDel) {
			log.error("confLogContrManager:procDel:os:error:"+eDel);
		 }
		 try {
			  if(is!=null){
			    is.close();
			  }
		} catch (Exception eDel) {
			log.error("confLogContrManager:procDel:finally:is:error:"+eDel);
		}
	 }
	}
	public synchronized void procPause(){
		log.info("confLogContrManager:procPause:01");
		
		InputStream is = null;
 		OutputStream os = null;
 		try {
		   Context ctx = new InitialContext();
		   
   		   BaseParamItem bpi = new BaseParamItem();
	       bpi.put("gactiontype", ServiceReestrAction.PROCESS_STOP.name());
	       ((IHLocal)ctx.lookup(ServiceReestr.ArchiveAuditFunc)).run(bpi);
   		   
   		   log.info("confLogContrManager:procPause:02");
   		   
   		   Properties properties = new Properties();
   		   String path = proc_aafunc_exec_file;
   		  
   		  
	     
	       
	       File f=new File(path); 
	       
	       if(f.exists()) {
	      
	       
	       
	          properties.load(is=new FileInputStream(f));
	       
	      
	          properties.setProperty("status", "pause");
	          properties.store(os=new FileOutputStream(f), null);
	       
	          forView("procPause");
	       }
	       
	       audit(ResourcesMap.PROC_ARCH_AUDIT_USER, ActionsMap.PAUSE);
	       
        }catch (Exception e) {
				log.error("confLogContrManager:procPause:error:"+e);
	   }finally{
		 try {
			if(os!=null){
				 os.close();
			}
		 } catch (Exception e) {
			log.error("confLogContrManager:procPause:os:error:"+e);
		 }
		 try {
			  if(is!=null){
			    is.close();
			  }
		} catch (Exception e) {
			log.error("confLogContrManager:procDel:finally:is:error:"+e);
		}
	 }
	}
	public synchronized void procRun(){
		  log.info("confLogContrManager:procRun:01");
		  
		  Properties properties = new Properties();
		  String path = proc_aafunc_exec_file;
		  OutputStream os = null;
		  try {
		    
		     
		     File f=new File(path); 
		     
		     if(f.exists()) {
		    	 
		       properties.load(new FileInputStream(f));
		     
		       
		       properties.setProperty("status", "active");
		       properties.store(os=new FileOutputStream(f), null);
		       
		       Context ctx = new InitialContext();
		          
    		   BaseParamItem bpi = new BaseParamItem();
     	       bpi.put("gactiontype", ServiceReestrAction.PROCESS_START.name());
     	       ((IHLocal)ctx.lookup(ServiceReestr.ArchiveAuditFunc)).run(bpi);
    		   
    		   log.info("confLogContrManager:procRun:03");
    	  
    		   forView("procRun");
    		   
    		   audit(ResourcesMap.PROC_ARCH_AUDIT_USER, ActionsMap.START);
    		   
    		 }
		  }catch (Exception e) {
				log.error("confLogContrManager:procRun:error:"+e);
		  }finally{
			 try {
				if(os!=null){
					 os.close();
				}
			 } catch (Exception e) {
				log.error("confLogContrManager:procRun:os:error:"+e);
			 }
		 }
	}
	
	
	private void forView(String type){
	   try {
		   
		   procAAFBean= new ProcAAFItem();
		   
		  if(type.equals("procCrt")){
		  
		    procAAFBean.setStatus("active");
		  }else if ("procDel".equals(type)){ 
			  procAAFBean.setStatus("passive");
		  }else if ("procPause".equals(type)){
			procAAFBean.setStatus("pause");
		  }else if ("procRun".equals(type)){
			procAAFBean.setStatus("active");
		  }
			  
		  Contexts.getEventContext().set("procAAFBean", this.procAAFBean);
		  
	   }catch (Exception e) {
		  log.error("confLogContrManager:forView:error:"+e);
	   }
	}
	
	 public void audit(ResourcesMap resourcesMap, ActionsMap actionsMap){
		   try{
			   AuditExportData auditExportData = (AuditExportData)Component.getInstance("auditExportData",ScopeType.SESSION);
			   auditExportData.addFunc(resourcesMap.getCode()+":"+actionsMap.getCode());
			   
		   }catch(Exception e){
			   log.error("GroupManager:audit:error:"+e);
		   }
	  }
}
