package iac.grn.infosweb.context.proc.archafunc;

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

@Name("procArchAFuncSettingsManager")
 public class ProcArchAFuncSettingsManager {

	@Logger private Log log;
	
	@In
	EntityManager entityManager;
	 
	
	private static String param_code="to_archive_audit_func";
	
	public void init(){
		
		String monthInterval = null;
		
		try{
		   log.info("ConfLoadDataSettingsManager:init:01");
		   
		    String  remoteAuditAFunc = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("remoteAudit");
		    log.info("confLoadDataManager:init:remoteAudit:"+remoteAuditAFunc);
	
		     if(remoteAuditAFunc!=null ){ 
		    	 //при сохранении настроек
		    	 //procArchAFuncSettingsBean устанавливать не нужно
			     //он автоматически продолжается в EventContext
			     //от оправки формы до её отображения
			   return;
			 }
		   
		    
		     ProcArchAFuncSettingsBean beanSettings = new ProcArchAFuncSettingsBean();
		    	
			 log.info("ConfLoadDataSettingsManager:getCLDSBeanView:01");
			 
			 InputStream is = null;
			 
			 try {
				 
				
				 List<String> losAFunc = entityManager.createNativeQuery(
			              "select ST.VALUE_PARAM "+
	                      "from SETTINGS_KNL_T st "+
	                      "where ST.SIGN_OBJECT=? ")
	                      .setParameter(1, param_code)
	                      .getResultList();
		    	  
		    	  if(losAFunc!=null&&!losAFunc.isEmpty()){
		    	    monthInterval=losAFunc.get(0);
		    	  }
		    	  
		    	  if(monthInterval==null){ 
		    		  monthInterval="6";
		    	  }	    	 
			     
		    	  beanSettings.setParamActualData(new Long(monthInterval));
		    	  
			      Contexts.getEventContext().set("procArchAFuncSettingsBean", beanSettings);
			     
			      
			      
			  }catch (Exception eAFunc) {
					log.error("confLoadDataManager:initConfLDInfoBean:error:"+eAFunc);
			 }finally{
				try {
				  if(is!=null){
				    is.close();
				   }
				} catch (Exception eAFunc) {
					log.error("confLoadDataManager:initConfLDInfoBean:finally:is:error:"+eAFunc);
				}
		   }    
		  
		   
		}catch(Exception eAFunc){
		   log.error("confLoadDataSettingsManager:init:ERROR:"+eAFunc);
		} 
	}     
	
	public void save(){
		try{
		   log.info("AFuncSettingsManager:save:01");
		  
		   OutputStream os = null;
			 
		   ProcArchAFuncSettingsBean beanSettingsAFunc = (ProcArchAFuncSettingsBean) 
				   Contexts.getEventContext().get("procArchAFuncSettingsBean");
		   
		   
		   
			  if(beanSettingsAFunc.getParamActualData()==null){
				  log.info("confLoadDataSettingsManager:save:02");
				  return;
			  }
			   
			  try {
				
				  entityManager.createNativeQuery(
			              "update SETTINGS_KNL_T st " +
			              "set ST.VALUE_PARAM=? "+
	                      "where ST.SIGN_OBJECT=? ")
	                      .setParameter(1, beanSettingsAFunc.getParamActualData())
	                      .setParameter(2, param_code)
	                      .executeUpdate();
	                      
			     log.info("confLoadDataSettingsManager:save:03");
	    	  
			     //procArchAFuncSettingsBean устанавливать не нужно
			     //он автоматически продолжается в EventContext
			     //от оправки формы до её отображения
			     
			     audit(ResourcesMap.PROC_ARCH_AUDIT_USER, ActionsMap.SET_PARAM);
			     
		  	  }catch (Exception eAFunc) {
					log.error("confLoadDataSettingsManager:save:"+eAFunc);
			  }finally{
				 try {
					if(os!=null){
						 os.close();
					}
				 } catch (Exception eAFunc) {
					log.error("confLoadDataSettingsManager:save:os:error:"+eAFunc);
				 }
			 }
			   
		}catch(Exception eAFunc){
		   log.error("ConfLoadDataSettingsManager:save:ERROR:"+eAFunc);
		} 
	}     
	 public void audit(ResourcesMap resourcesMap, ActionsMap actionsMap){
		   try{
			   AuditExportData auditExportDataAFunc = (AuditExportData)Component.getInstance("auditExportData",ScopeType.SESSION);
			   auditExportDataAFunc.addFunc(resourcesMap.getCode()+":"+actionsMap.getCode());
			   
		   }catch(Exception e){
			   log.error("GroupManager:audit:error:"+e);
		   }
	  }
	

	
}
