package iac.grn.infosweb.context.proc.archasys;

import iac.grn.infosweb.session.audit.actions.ActionsMap;
import iac.grn.infosweb.session.audit.actions.ResourcesMap;
import iac.grn.infosweb.session.audit.export.AuditExportData;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;

@Name("procArchASysSettingsManager")
 public class ProcArchASysSettingsManager {

	@Logger private Log log;
	
	@In
	EntityManager entityManager;
	 
	
	private static String param_code="to_archive_audit_sys";
	
	public void init(){
		
		String monthInterval = null;
		
		try{
		   log.info("ConfLoadDataSettingsManager:init:01");
		   
		    String  remoteAuditASys = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("remoteAudit");
		    log.info("confLoadDataManager:init:remoteAudit:"+remoteAuditASys);
	
		     if(remoteAuditASys!=null ){ 
		    	 //при сохранении настроек
		    	 //procArchASysSettingsBean устанавливать не нужно
			     //он автоматически продолжается в EventContext
			     //от оправки формы до её отображения
			   return;
			 }
		   
		    
		     ProcArchASysSettingsBean beanSettingsASys = new ProcArchASysSettingsBean();
		    	
		     
				 
			 
			 log.info("ConfLoadDataSettingsManager:getCLDSBeanView:01");
			 
			 InputStream is = null;
			 
			 try {
				 
						 List<String> los = entityManager.createNativeQuery(
			              "select ST.VALUE_PARAM "+
	                      "from SETTINGS_KNL_T st "+
	                      "where ST.SIGN_OBJECT=? ")
	                      .setParameter(1, param_code)
	                      .getResultList();
		    	  
		    	  if(los!=null&&!los.isEmpty()){
		    	    monthInterval=los.get(0);
		    	  }
		    	  
		    	  if(monthInterval==null){ 
		    		  monthInterval="6";
		    	  }	    	 
			     
		    	  beanSettingsASys.setParamActualData(new Long(monthInterval));
		    	  
			      Contexts.getEventContext().set("procArchASysSettingsBean", beanSettingsASys);
			     
			      
			      
			  }catch (Exception eASys) {
					log.error("confLoadDataManager:initConfLDInfoBean:error:"+eASys);
			 }finally{
				try {
				  if(is!=null){
				    is.close();
				   }
				} catch (Exception eASys) {
					log.error("confLoadDataManager:initConfLDInfoBean:finally:is:error:"+eASys);
				}
		   }    
		  
		   
		}catch(Exception eASys){
		   log.error("confLoadDataSettingsManager:init:ERROR:"+eASys);
		} 
	}     
	
	public void save(){
		try{
			
		   log.info("confLoadDataSettingsManager:save:01");
		  
		   OutputStream os = null;
			 
		   ProcArchASysSettingsBean beanSettingsASys = (ProcArchASysSettingsBean) 
				   Contexts.getEventContext().get("procArchASysSettingsBean");
		   
		   
		   
			  if(beanSettingsASys.getParamActualData()==null){
				  log.info("confLoadDataSettingsManager:save:02");
				  return;
			  }
			   
			  try {
					  entityManager.createNativeQuery(
			              "update SETTINGS_KNL_T st " +
			              "set ST.VALUE_PARAM=? "+
	                      "where ST.SIGN_OBJECT=? ")
	                      .setParameter(1, beanSettingsASys.getParamActualData())
	                      .setParameter(2, param_code)
	                      .executeUpdate();
	                      
			     log.info("confLoadDataSettingsManager:save:03");
	    	  
			     //procArchASysSettingsBean устанавливать не нужно
			     //он автоматически продолжается в EventContext
			     //от оправки формы до её отображения
			     
			     audit(ResourcesMap.PROC_ARCH_AUDIT_SYS, ActionsMap.SET_PARAM);
			     
		  	  }catch (Exception eASys) {
					log.error("confLoadDataSettingsManager:save:"+eASys);
			  }finally{
				 try {
					if(os!=null){
						 os.close();
					}
				 } catch (Exception eASys) {
					log.error("confLoadDataSettingsManager:save:os:error:"+eASys);
				 }
			 }
			   
		}catch(Exception eASys){
		   log.error("ConfLoadDataSettingsManager:save:ERROR:"+eASys);
		} 
	}     
	
	public void audit(ResourcesMap resourcesMap, ActionsMap actionsMap){
		   try{
			   AuditExportData auditExportDataASys = (AuditExportData)Component.getInstance("auditExportData",ScopeType.SESSION);
			   auditExportDataASys.addFunc(resourcesMap.getCode()+":"+actionsMap.getCode());
			   
		   }catch(Exception eASys){
			   log.error("GroupManager:audit:error:"+eASys);
		   }
	  }
	

	
}
