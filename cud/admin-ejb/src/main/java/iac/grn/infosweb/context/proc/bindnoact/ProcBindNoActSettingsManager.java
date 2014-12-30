package iac.grn.infosweb.context.proc.bindnoact;

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

@Name("procBindNoActSettingsManager")
 public class ProcBindNoActSettingsManager {

	@Logger private Log log;
	
	@In
	EntityManager entityManager;
	 
		
	private static String param_code="to_archive_audit_func";
	
	public void init(){
		
		String monthInterval = null;
		
		try{
		   log.info("procBindNoActSettingsManager:init:01");
		   
		    String  remoteAuditBindNoAct = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("remoteAudit");
		    log.info("procBindNoActSettingsManager:init:remoteAudit:"+remoteAuditBindNoAct);
	
		     if(remoteAuditBindNoAct!=null /*&& !remoteAudit.equals("procSetting")*/){ 
		    	 //при сохранении настроек
		    	 //procArchAFuncSettingsBean устанавливать не нужно
			     //он автоматически продолжается в EventContext
			     //от оправки формы до её отображения
			   return;
			 }
		   
		    
		     ProcBindNoActSettingsBean beanSettings = new ProcBindNoActSettingsBean();
		    	
		     
				 
			 log.info("procBindNoActSettingsManager:getCLDSBeanView:01");
			 
			 InputStream is = null;
			 
			 try {
				 
					 List<String> losBindNoAct = entityManager.createNativeQuery(
			              "select ST.VALUE_PARAM "+
	                      "from SETTINGS_KNL_T st "+
	                      "where ST.SIGN_OBJECT=? ")
	                      .setParameter(1, param_code)
	                      .getResultList();
		    	  
		    	  if(losBindNoAct!=null&&!losBindNoAct.isEmpty()){
		    	    monthInterval=losBindNoAct.get(0);
		    	  }
		    	  
		    	  if(monthInterval==null){ 
		    		  monthInterval="6";
		    	  }	    	 
			     
		    	  beanSettings.setParamActualData(new Long(monthInterval));
		    	  
			      Contexts.getEventContext().set("procBindNoActSettingsBean", beanSettings);
			     
			      
			      
			  }catch (Exception eBindNoAct) {
					log.error("confLoadDataManager:initConfLDInfoBean:error:"+eBindNoAct);
			 }finally{
				try {
				  if(is!=null){
				    is.close();
				   }
				} catch (Exception eBindNoAct) {
					log.error("confLoadDataManager:initConfLDInfoBean:finally:is:error:"+eBindNoAct);
				}
		   }    
		  
		   
		}catch(Exception eBindNoAct){
		   log.error("procBindNoActSettingsManager:init:ERROR:"+eBindNoAct);
		} 
	}     
	
	public void save(){
		try{
		   log.info("procBindNoActSettingsManager:save:01");
		    
		   OutputStream os = null;
			 
		   ProcBindNoActSettingsBean beanSettingsBindNoAct = (ProcBindNoActSettingsBean) 
				   Contexts.getEventContext().get("procBindNoActSettingsBean");
		   
		   
		   
			  if(beanSettingsBindNoAct.getParamActualData()==null){
				  log.info("procBindNoActSettingsManager:save:02");
				  return;
			  }
			   
			  try {
					  entityManager.createNativeQuery(
			              "update SETTINGS_KNL_T st " +
			              "set ST.VALUE_PARAM=? "+
	                      "where ST.SIGN_OBJECT=? ")
	                      .setParameter(1, beanSettingsBindNoAct.getParamActualData())
	                      .setParameter(2, param_code)
	                      .executeUpdate();
	                      
			     log.info("procBindNoActSettingsManager:save:03");
	    	  
			     //procArchAFuncSettingsBean устанавливать не нужно
			     //он автоматически продолжается в EventContext
			     //от оправки формы до её отображения
			     
			     audit(ResourcesMap.PROC_ARCH_AUDIT_USER, ActionsMap.SET_PARAM);
			     
		  	  }catch (Exception eBindNoAct) {
					log.error("procBindNoActSettingsManager:save:"+eBindNoAct);
			  }finally{
				 try {
					if(os!=null){
						 os.close();
					}
				 } catch (Exception eBindNoAct) {
					log.error("procBindNoActSettingsManager:save:os:error:"+eBindNoAct);
				 }
			 }
			   
		}catch(Exception eBindNoAct){
		   log.error("procBindNoActSettingsManager:save:ERROR:"+eBindNoAct);
		} 
	}     
	 public void audit(ResourcesMap resourcesMap, ActionsMap actionsMap){
		   try{
			   AuditExportData auditExportDataBindNoAct = (AuditExportData)Component.getInstance("auditExportData",ScopeType.SESSION);
			   auditExportDataBindNoAct.addFunc(resourcesMap.getCode()+":"+actionsMap.getCode());
			   
		   }catch(Exception e){
			   log.error("GroupManager:audit:error:"+e);
		   }
	  }
	

	
}
