package iac.grn.infosweb.session;

import java.util.List;

import iac.grn.infosweb.session.audit.export.AuditExportManager;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.contexts.Lifecycle;

import ru.spb.iac.cud.items.AuditFunction;

 public class SessionListener implements HttpSessionListener {

	  private Logger log = Logger.getLogger(SessionListener.class);
	
	  public void sessionCreated(HttpSessionEvent arg0) {
	  }
	 
	  public void sessionDestroyed(HttpSessionEvent arg0) {
		  log.info("+++SessionListener:sessionDestroyed:01");
		
		try{
		  HttpSession hs = (HttpSession) arg0.getSession();
		
		  
		  
		  final String uid =(String)hs.getAttribute("auditExportUid");
		  
		  final List<AuditFunction> funcList =(List<AuditFunction>)hs.getAttribute("auditExportFuncList");
		
		  if(funcList!=null&&!funcList.isEmpty()){
		    new Thread(
				new Runnable(){
				      public void run() {
				    	  
				    	 
				    	 
				    	  auditExport(funcList, uid);
				    }
				   }
				).start();
		}
		
		  log.info("+++SessionListener:sessionDestroyed:02");
		
		}catch(Exception e){
			 log.error("SessionListener:sessionDestroyed:error:"+e);
		}
	  }	
	  
	  private void auditExport(List<AuditFunction> funcList, String uid){
			 try{
				 log.info("SessionListener:auditExport:01");
			
				 Lifecycle.beginCall();
				 
				 AuditExportManager auditExportManager = (AuditExportManager)Component.getInstance("auditExportManager",ScopeType.EVENT);
				 
				 auditExportManager.export(funcList, uid);
				   
				 log.info("SessionListener:auditExport:02");
				 
			   }catch(Exception e){
				   log.error("SessionListener:audit:auditExport:error:"+e);
			   }finally{
				    Lifecycle.endCall();
			   }
			 log.info("SessionListener:auditExport:05");
		}
}
