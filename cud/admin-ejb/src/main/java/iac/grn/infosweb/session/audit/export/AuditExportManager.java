package iac.grn.infosweb.session.audit.export;


import iac.cud.infosweb.ws.AuditServiceClient;
import iac.grn.infosweb.session.audit.actions.ActionsMap;
import iac.grn.infosweb.session.audit.actions.ResourcesMap;
import iac.grn.infosweb.session.navig.LinksMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.items.AuditFunction;


/**
 * Управляющий бин, осуществляющий реализацию навигации 
 * по используемым ресурсам приложения
 * @author bubnov
 *
 */
@Name("auditExportManager")
 public class AuditExportManager {

	 @Logger private Log log;
	 
	 final static org.slf4j.Logger LOGGER = LoggerFactory
				.getLogger(AuditExportManager.class);
	 
	 @In 
	 EntityManager entityManager;
	
	 @In
	 LinksMap linksMap;
	
	
	 public void export(List<AuditFunction> funcList, String uid) throws Exception{
		 log.info("auditExportManager:export:01");
		 
		 //токен_ид теперь[11.12.13] не используется
		 
		    try {
		    	
		    	
		    	AuditServiceClient asc = (AuditServiceClient)Component.getInstance("auditServicesClient",ScopeType.EVENT);
		    	
		    
            	log.info("auditExportManager:export:funcList:"+(funcList!=null?funcList.size():"null"));
		    	log.info("auditExportManager:export:uid:"+uid);
		    	
		    	if(funcList==null){
		    		funcList = new ArrayList<AuditFunction>();
		     	}
		    	//добавление логаут
		    	AuditFunction func = new AuditFunction();
				func.setDateFunction(new Date());
				func.setCodeFunction(ResourcesMap.USER.getCode()+":"+ActionsMap.LOGOUT_ADM.getCode());
		    	funcList.add(func);
		    	
		    	//LOGGER.info(cau.getFio()+":"+ResourcesMap.USER.getCode()+":"+ActionsMap.LOGOUT_ADM.getCode());
		    	
		        asc.audit(uid, funcList);
		    	
		        
		      } catch (Exception e) {
		    	 log.error("auditExportManager:export:ERROR:"+e);
		    	 log.trace(e);
		    	 
		    	  throw e;
		   }
		    
		    log.info("auditExportManager:export:02");
	 }
	 
}

