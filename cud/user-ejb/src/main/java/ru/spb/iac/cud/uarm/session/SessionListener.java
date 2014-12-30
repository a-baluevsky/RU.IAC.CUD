package ru.spb.iac.cud.uarm.session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;






import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.items.AuditFunction;
import ru.spb.iac.cud.uarm.ejb.audit.ActionsMap;
import ru.spb.iac.cud.uarm.ejb.audit.ResourcesMap;
import ru.spb.iac.cud.uarm.ws.AuditServiceClient;

 public class SessionListener implements HttpSessionListener {

	 private static final Logger LOGGER = LoggerFactory.getLogger(SessionListener.class);
	
	  public void sessionCreated(HttpSessionEvent arg0) {
	  }
	 
	  public void sessionDestroyed(HttpSessionEvent arg0) {
		  LOGGER.debug("+++SessionListener:sessionDestroyed:01");
		
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
		
		  LOGGER.debug("+++SessionListener:sessionDestroyed:02");
		
		}catch(Exception e){
			 LOGGER.error("SessionListener:sessionDestroyed:error:"+e);
		}
	  }	
	  
	  private void auditExport(List<AuditFunction> funcList, String uid){
			 try{
				 LOGGER.debug("SessionListener:auditExport:01");
			
				 AuditServiceClient asc = new AuditServiceClient();
			    	
				     	
				    	LOGGER.debug("auditExportManager:export:funcList:"+(funcList!=null?funcList.size():"null"));
				    	LOGGER.debug("auditExportManager:export:uid:"+uid);
				    	
				    	if(funcList==null){
				    		funcList = new ArrayList<AuditFunction>();
				     	}
				    	//добавление logout
				    	AuditFunction func = new AuditFunction();
						func.setDateFunction(new Date());
						func.setCodeFunction(ResourcesMap.USER.getCode()+":"+ActionsMap.LOGOUT_UARM.getCode());
				    	funcList.add(func);
				    	
				        asc.audit(uid, funcList); 
				   
				 LOGGER.debug("SessionListener:auditExport:02");
				 
			   }catch(Exception e){
				   LOGGER.error("SessionListener:audit:auditExport:error:"+e);
			   }
			 LOGGER.debug("SessionListener:auditExport:05");
		}
}
