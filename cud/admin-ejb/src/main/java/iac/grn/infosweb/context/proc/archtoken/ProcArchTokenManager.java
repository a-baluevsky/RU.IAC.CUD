package iac.grn.infosweb.context.proc.archtoken;

import iac.cud.infosweb.dataitems.BaseParamItem;
import iac.cud.infosweb.local.service.IHLocal;
import iac.cud.infosweb.local.service.ServiceReestr;
import iac.cud.infosweb.local.service.ServiceReestrAction;
import iac.grn.infosweb.session.audit.actions.ActionsMap;
import iac.grn.infosweb.session.audit.actions.ResourcesMap;
import iac.grn.infosweb.session.audit.export.AuditExportData;
import iac.grn.serviceitems.ProcATInfoItem;
import iac.grn.serviceitems.ProcATItem;

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

@Name("procArchTokenManager")
 public class ProcArchTokenManager {

	@Logger private Log log;
	
	private static final String proc_atoken_exec_file=System.getProperty("jboss.server.config.dir")+"/"+"proc_atoken_exec.properties";
	
	private static final String proc_atoken_info_file=System.getProperty("jboss.server.config.dir")+"/"+"proc_atoken_info.properties";
	
	private Date startDate;
	
	private Long period=1L;
	
	
	private ProcATItem procATBean;
	
	private ProcATInfoItem procATInfoBean;
	
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
	public ProcATItem getProcATBean(){
		if(this.procATBean==null){
			initProcATBean();
		}
		return this.procATBean;
	}
	public void setProcATBean(ProcATItem procATBean){
		this.procATBean=procATBean;
	}
	
	@Factory
	public ProcATInfoItem getProcATInfoBean(){
		 log.info("__confLogContrManager:getProcATInfoBean");
		if(this.procATInfoBean==null){
			initProcATInfoBean();
		}
		return this.procATInfoBean;
	}
	public void setProcATInfoBean(ProcATInfoItem procATInfoBean){
		this.procATInfoBean=procATInfoBean;
	}
	
	public void initProcATBean(){
		 log.info("confLogContrManager:initProcATBean:01");
		 
		 String  statusTkn=null;
		 Properties properties = new Properties();
		 String path = proc_atoken_exec_file;
		 InputStream is = null;
		 
		 String  remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("remoteAudit");
		 log.info("confLogContrManager:initProcATBean:remoteAudit:"+remoteAudit);
		
		 procATBean= new ProcATItem();
		 
		 if(remoteAudit!=null && "procInfo".equals(remoteAudit)){
			 // кнопка "обновить" задействована для обновления и Center, и Bottom панелей 
		     // чтобы отобразить изменения, если кто другой запустил/остановил процесс
			 
		
		 }
		 
		 if(remoteAudit!=null && "procCrt".equals(remoteAudit)){
			 procATBean.setStatus("passive");
			 return;
		 }
		 if(remoteAudit!=null && "procDel".equals(remoteAudit)){
			 procATBean.setStatus("active");
			 return;
		 }
		 if(remoteAudit!=null && "procPause".equals(remoteAudit)){
			 procATBean.setStatus("active");
			 return;
		 }
		 if(remoteAudit!=null && "procRun".equals(remoteAudit)){
			 procATBean.setStatus("pause");
			 return;
		 }
		 
		 try {
			   
             File fTkn=new File(path); 
             
		     if(fTkn.exists()) { 
		    	 
		    	 properties.load(is=new FileInputStream(fTkn));
		    	 
		    	 statusTkn=properties.getProperty("status");
		    	 
		    	 log.info("confLogContrManager:initProcATBean:status:"+statusTkn);
		    	 
		    	 if(statusTkn!=null){
		    				 procATBean.setStatus(statusTkn);
		    		 }
		      }else{
		    	  procATBean.setStatus("passive");
		      }
		 }catch (Exception eTkn) {
				log.error("confLogContrManager:initProcATBean:error:"+eTkn);
		 }finally{
			try {
			  if(is!=null){
			    is.close();
			   }
			} catch (Exception eTkn) {
				log.error("confLogContrManager:initProcATBean:finally:is:error:"+eTkn);
			}
	   }    
	}
	
	public void initProcATInfoBean(){
		 log.info("confLogContrManager:initProcATInfoBean:01");
		 
		 String execDate=null, execHit=null;
		 Properties properties = new Properties();
		 String path = proc_atoken_info_file;
		 InputStream is = null;
		 
		 try {
			 DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm:ss");
		    
		    
		     
            File f=new File(path); 
            
		     if(f.exists()) { 
		    	 
		    	 procATInfoBean= new ProcATInfoItem();
		    	 
		    	 properties.load(is=new FileInputStream(f));
		    	 
		    	 execDate=properties.getProperty("exec_date");
		    	 execHit=properties.getProperty("exec_hit");
		    		 
		    	 log.info("confLogContrManager:initProcATInfoBean:exec_date:"+execDate);
		    	 log.info("confLogContrManager:initProcATInfoBean:exec_hit:"+execHit);
		    	 	 
		    	 procATInfoBean.setExecDate(execDate != null ? df.parse(execDate) : null);
		    	 procATInfoBean.setExecHit(execHit != null ? ("true".equals(execHit) ? "Запущен" : "Сбой") : null);
		      	 
		     }
		  }catch (Exception eTkn) {
				log.error("confLogContrManager:initProcATInfoBean:error:"+eTkn);
		 }finally{
			try {
			  if(is!=null){
			    is.close();
			   }
			} catch (Exception eTkn) {
				log.error("confLogContrManager:initProcATInfoBean:finally:is:error:"+eTkn);
			}
	   }    
	}
	
	public synchronized void procCrt(){
		
		  log.info("Tkn:procCrt:01");
			  
		  Properties properties = new Properties();
		  String path = proc_atoken_exec_file;
		  OutputStream os = null;
		  
			   
		  try {
				
             File fTkn=new File(path);
             
		        properties.setProperty("status", "active");
		       
		       properties.store(os=new FileOutputStream(fTkn), null);
		       
		     
		       Context ctx = new InitialContext(); 
	 	    	 
 	           BaseParamItem bpiTkn = new BaseParamItem();
	    	   bpiTkn.put("gactiontype", ServiceReestrAction.PROCESS_START.name());
	    	   ((IHLocal)ctx.lookup(ServiceReestr.ArchiveToken)).run(bpiTkn);
    		   
    		   log.info("confLogContrManager:procCrt:03");
    	  
    		   forView("procCrt");
    		   
    		   audit(ResourcesMap.PROC_ARCH_TOKEN, ActionsMap.START);
    		   
		  }catch (Exception eTkn) {
				log.error("confLogContrManager:procCrt:error:"+eTkn);
		  }finally{
			 try {
				if(os!=null){
					 os.close();
				}
			 } catch (Exception eTkn) {
				log.error("confLogContrManager:procCrt:os:error:"+eTkn);
			 }
		 }
	}
	public synchronized void procDel(){
		log.info("confLogContrManager:procDel:01");
		
		InputStream is = null;
 		OutputStream os = null;
 		
		try {
		   Context ctx = new InitialContext();
		   
   		   BaseParamItem bpiTkn = new BaseParamItem();
   		   
   		   bpiTkn.put("gactiontype", ServiceReestrAction.PROCESS_STOP.name());
 	       
 	       ((IHLocal)ctx.lookup(ServiceReestr.ArchiveToken)).run(bpiTkn);
   		   
   		   Properties properties = new Properties();
   		   String path = proc_atoken_exec_file;
   		  
   		    File fTkn=new File(path); 
          
	      
	       
	       
	       properties.load(is=new FileInputStream(fTkn));
	       properties.setProperty("status", "passive");
	       properties.store(os=new FileOutputStream(fTkn), null);
	       
	       forView("procDel");
	       
	       audit(ResourcesMap.PROC_ARCH_TOKEN, ActionsMap.STOP);
	       
        }catch (Exception eTkn) {
				log.error("confLogContrManager:procDel:error:"+eTkn);
	   }finally{
		 try {
			if(os!=null){
				 os.close();
			}
		 } catch (Exception eTkn) {
			log.error("confLogContrManager:procDel:os:error:"+eTkn);
		 }
		 try {
			  if(is!=null){
			    is.close();
			  }
		} catch (Exception eTkn) {
			log.error("confLogContrManager:procDel:finally:is:error:"+eTkn);
		}
	 }
	}
	public synchronized void procPause(){
		log.info("confLogContrManager:procPause:01");
		
		InputStream is = null;
 		OutputStream os = null;
 		try {
		   Context ctx = new InitialContext();
		   
   		   BaseParamItem bpiTkn = new BaseParamItem();
	       bpiTkn.put("gactiontype", ServiceReestrAction.PROCESS_STOP.name());
	       ((IHLocal)ctx.lookup(ServiceReestr.ArchiveToken)).run(bpiTkn);
   		   
   		   log.info("confLogContrManager:procPause:02");
   		   
   		   Properties properties = new Properties();
   		   String path = proc_atoken_exec_file;
   		  
   		   File fTkn=new File(path); 

	       if(fTkn.exists()) {
	      
	       
	       
	          properties.load(is=new FileInputStream(fTkn));
	       
	       
	              properties.setProperty("status", "pause");
	          properties.store(os=new FileOutputStream(fTkn), null);
	       
	          forView("procPause");
	       }
	       
	       audit(ResourcesMap.PROC_ARCH_TOKEN, ActionsMap.PAUSE);
	       
        }catch (Exception eTkn) {
				log.error("confLogContrManager:procPause:error:"+eTkn);
	   }finally{
		 try {
			if(os!=null){
				 os.close();
			}
		 } catch (Exception eTkn) {
			log.error("confLogContrManager:procPause:os:error:"+eTkn);
		 }
		 try {
			  if(is!=null){
			    is.close();
			  }
		} catch (Exception eTkn) {
			log.error("confLogContrManager:procDel:finally:is:error:"+eTkn);
		}
	 }
	}
	public synchronized void procRun(){
		  log.info("confLogContrManager:procRun:01");
		  
		  Properties properties = new Properties();
		  String path = proc_atoken_exec_file;
		  OutputStream os = null;
			   
		  try {
		    
             File fTkn=new File(path); 
            
		     if(fTkn.exists()) {
		    	 
		       properties.load(new FileInputStream(fTkn));
		       
		          
		       properties.setProperty("status", "active");
		       properties.store(os=new FileOutputStream(fTkn), null);
		       
		       Context ctx = new InitialContext();
		        
    		   BaseParamItem bpiTkn = new BaseParamItem();
     	       bpiTkn.put("gactiontype", ServiceReestrAction.PROCESS_START.name());
     	       ((IHLocal)ctx.lookup(ServiceReestr.ArchiveToken)).run(bpiTkn);
    		   
    		   log.info("confLogContrManager:procRun:03");
    	  
    		   forView("procRun");
    		 }
		     
		     audit(ResourcesMap.PROC_ARCH_TOKEN, ActionsMap.START);
		     
		  }catch (Exception eTkn) {
				log.error("confLogContrManager:procRun:error:"+eTkn);
		  }finally{
			 try {
				if(os!=null){
					 os.close();
				}
			 } catch (Exception eTkn) {
				log.error("confLogContrManager:procRun:os:error:"+eTkn);
			 }
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
	
	private void forView(String type){
		   try {
			   
			   procATBean= new ProcATItem();
			   
			  if(type.equals("procCrt")){
			  
			    procATBean.setStatus("active");
			  }else if (type.equals("procDel")){ 
				  procATBean.setStatus("passive");
			  }else if (type.equals("procPause")){
				procATBean.setStatus("pause");
			  }else if (type.equals("procRun")){
				procATBean.setStatus("active");
			  }
				  
			  Contexts.getEventContext().set("procATBean", this.procATBean);
			  
		   }catch (Exception e) {
			  log.error("confLogContrManager:forView:error:"+e);
		   }
		}
}
