package iac.grn.infosweb.context.proc.archtoken;

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

@Name("procArchTokenSettingsManager")
 public class ProcArchTokenSettingsManager {

	@Logger private Log log;
	
	@In
	EntityManager entityManager;
	 
	
	private static String param_code="to_archive_token";
	
	public void init(){
		
		String monthInterval = null;
		
		try{
		   log.info("ConfLoadDataSettingsManager:init:01");
		   
		    String  remoteAuditArchToken = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("remoteAudit");
		    log.info("confLoadDataManager:init:remoteAudit:"+remoteAuditArchToken);
	
		     if(remoteAuditArchToken!=null /*&& !remoteAudit.equals("procSetting")*/){ 
		    	 //при сохранении настроек
		    	 //procArchTokenSettingsBean устанавливать не нужно
			     //он автоматически продолжается в EventContext
			     //от оправки формы до её отображения
			   return;
			 }
		   
		    
		     ProcArchTokenSettingsBean beanSettingsArchToken = new ProcArchTokenSettingsBean();
		    	
		     
				 
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
			     
		    	  beanSettingsArchToken.setParamActualData(new Long(monthInterval));
		    	  
			      Contexts.getEventContext().set("procArchTokenSettingsBean", beanSettingsArchToken);
			     
			      
			      
			  }catch (Exception eArchToken) {
					log.error("confLoadDataManager:initConfLDInfoBean:error:"+eArchToken);
			 }finally{
				try {
				  if(is!=null){
				    is.close();
				   }
				} catch (Exception eArchToken) {
					log.error("confLoadDataManager:initConfLDInfoBean:finally:is:error:"+eArchToken);
				}
		   }    
		  
		   
		}catch(Exception e){
		   log.error("confLoadDataSettingsManager:init:ERROR:"+e);
		} 
	}     
	
	public void save(){
		try{
		   log.info("confLoadDataSettingsManager:save:01");
		   
		   OutputStream os = null;
			 
		   ProcArchTokenSettingsBean beanSettingsArchToken = (ProcArchTokenSettingsBean) 
				   Contexts.getEventContext().get("procArchTokenSettingsBean");
		   
		   
		   
			  if(beanSettingsArchToken.getParamActualData()==null){
				  log.info("confLoadDataSettingsManager:save:02");
				  return;
			  }
			   
			  try {
				  entityManager.createNativeQuery(
			              "update SETTINGS_KNL_T st " +
			              "set ST.VALUE_PARAM=? "+
	                      "where ST.SIGN_OBJECT=? ")
	                      .setParameter(1, beanSettingsArchToken.getParamActualData())
	                      .setParameter(2, param_code)
	                      .executeUpdate();
	                      
			     log.info("confLoadDataSettingsManager:save:03");
	    	  
			     //procArchTokenSettingsBean устанавливать не нужно
			     //он автоматически продолжается в EventContext
			     //от оправки формы до её отображения
			     
			     audit(ResourcesMap.PROC_ARCH_TOKEN, ActionsMap.SET_PARAM);
			     
		  	  }catch (Exception eArchToken) {
					log.error("confLoadDataSettingsManager:save:"+eArchToken);
			  }finally{
				 try {
					if(os!=null){
						 os.close();
					}
				 } catch (Exception eArchToken) {
					log.error("confLoadDataSettingsManager:save:os:error:"+eArchToken);
				 }
			 }
			   
		}catch(Exception eArchToken){
		   log.error("ConfLoadDataSettingsManager:save:ERROR:"+eArchToken);
		} 
	}     
	
	public void audit(ResourcesMap resourcesMap, ActionsMap actionsMap){
		   try{
			   AuditExportData auditExportDataArchToken = (AuditExportData)Component.getInstance("auditExportData",ScopeType.SESSION);
			   auditExportDataArchToken.addFunc(resourcesMap.getCode()+":"+actionsMap.getCode());
			   
		   }catch(Exception e){
			   log.error("GroupManager:audit:error:"+e);
		   }
	  }
	

	
}
