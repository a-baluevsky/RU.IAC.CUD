package iac.grn.infosweb.context.mc.res;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.log.Log;

import java.util.*;

import iac.cud.infosweb.entity.*;
import iac.grn.infosweb.session.navig.LinksMap;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import iac.grn.infosweb.session.audit.actions.ActionsMap;
import iac.grn.infosweb.session.audit.actions.ResourcesMap;
import iac.grn.infosweb.session.audit.export.AuditExportData;


/**
 * Управляющий Бин, обеспечивающий выполение операций создания, изменения,
 * удаления и представления над сущностями Рубрики 
 * @author bubnov
 *
 */
@Name("resManager")
 public class ResManager {
	
	 @Logger private Log log;
	
	
	/**
     * Список доступных сущностей Рубрика для отображения
     */
	   private List<AcAppPage> listRes = null;
	   
	   /**
        *Сообщение выдаваемое при удалении сущности Консоль 
        *при обнаружении порождённых записей
        */
	   private String dellMessage;
	   
	   /**
        * Импортируемая и экспортируемая сущности Рубрика
        * для отображения и редактирования
        */
	   @In(required = false)
	   @Out(required = false)
	   private AcAppPage acResBean;
	   
	   /**
        * Импортируемая и экспортируемая сущности Рубрика 
        * для создания
        */
	   @In(required = false)
	   @Out(required = false)
	   private AcAppPage acResBeanCrt;
	   
	   /**
	     * Список доступных сущностей Рубрика для создания
	     */
	   private List<AcApplication> listResArm = null;
	   
	 
	   /**
        * Менеджер сущностей, обеспечивающий взаимодействие с БД
        */
	   @In 
	   EntityManager entityManager;
   
	   public String getDellMessage() {
		      return dellMessage;
	   }
	   public void setDellMessage(String dellMessage) {
		      this.dellMessage = dellMessage;
	   }
	   
	   public List<AcAppPage> getListRes() throws Exception{
		    log.info("getListRes_01");
		    try {
		    	if(listRes==null){
		    		listRes=entityManager.createQuery("select o from AcAppPage o").getResultList();
		    	}
		     } catch (Exception e) {
		    	 log.error("getListRes_ERROR="+e);
		         throw e;
		     }
		    return listRes;
	   }
	   public List<AcApplication> getListResArm() throws Exception{
		    log.info("getListResArm_01");
		    try {
		    	if(listResArm==null){
		    		listResArm=entityManager.createQuery("select o from AcApplication o WHERE o.acAppPages IS NOT EMPTY").getResultList();
		    	}
		     } catch (Exception e) {
		    	 log.error("getListResArm_ERROR="+e);
		         throw e;
		     }
		    return listResArm;
	   }
	   /**
		  * Создание сущности Рубрика
		  * @return флаг успешности
		  * @throws Exception
		  */
	   public String addRes() throws Exception {
		      log.info("addRes");
		      try {
			 	  
		    	  String  pidRootNode = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("idRootNode");
		    	  log.info("addRes:pidRootNode:"+pidRootNode);
		    	  
		    	  String pappCode = FacesContext.getCurrentInstance().getExternalContext()
					        .getRequestParameterMap()
					        .get("appCode");
				  log.info("addRes:pappCode:"+pappCode);
				    	
				    	  
		    	  if(pidRootNode!=null&&pappCode!=null){ 
		    	   acResBeanCrt.setIdParent2(new Long(pidRootNode));
		    	   acResBeanCrt.setAcApplication(new Long(pappCode));
		    	   
		    	  if(acResBeanCrt.getVisibleBoolean()){
		    		  acResBeanCrt.setVisible(new Long(1));
		    	  }else{
		    		  acResBeanCrt.setVisible(new Long(0));
		    	  }
		    	  
		     //  [вариант -1-]
		     // <a4j:region>
		    	 entityManager.persist(acResBeanCrt);
		    	 entityManager.flush();
		      	 entityManager.refresh(acResBeanCrt);
		    
		     //  [вариант -2-]
		     
		     
		     
		     
		    	  
		     //  [вариант -3-]
             
		     
		     
		     
		    
		      //  [вариант -4-]
		      
			  
			  
			  
		      	String ac = ((LinksMap)Component.getInstance("linksMap",ScopeType.APPLICATION)).getAppCode().toString();
				if(pappCode.equals(ac)){
				  cache();
				}
				audit(ResourcesMap.RES, ActionsMap.CREATE); 
				
		   	    }
		      } catch (Exception e) {
		        log.error("addRes_ERROR="+e);
		        throw e;
		      }
		      return "addRes";
		   }
	   /**
		  * Изменение сущности Рубрика
		  * @return флаг успешности
		  * @throws Exception
		  */
	   public String editRes() throws Exception {
		      log.info("ResManager:editRes:IdRes:"+acResBean.getIdRes());
		     
		      
		      String  pidRootNode = FacesContext.getCurrentInstance().getExternalContext()
				        .getRequestParameterMap()
				        .get("idRootNode");
			  log.info("editRes:pidRootNode:"+pidRootNode);
			   
			  String  pappCode = FacesContext.getCurrentInstance().getExternalContext()
				        .getRequestParameterMap()
				        .get("appCode");
			  log.info("addRes:pappCode:"+pappCode);
		
			  String  idRes = FacesContext.getCurrentInstance().getExternalContext()
				        .getRequestParameterMap()
				        .get("idRes");
			  log.info("addRes:idRes:"+idRes);
		
		      try {
		    	  if(pidRootNode!=null && pappCode!=null && idRes!=null){
		    	
		    		  
		    	  AcAppPage apm = entityManager.find(AcAppPage.class, new Long(idRes)/*acResBean.getIdRes()*/);
		    	  
		    	  apm.setPageName(acResBean.getPageName());
		    	  apm.setIdParent2(new Long(pidRootNode));
		    	  apm.setPageCode(acResBean.getPageCode());
		    	  apm.setAcApplication(new Long(pappCode));
		    	  apm.setVisible((acResBean.getVisibleBoolean()? new Long(1): new Long(0)));
		    	  apm.setOrderNum(acResBean.getOrderNum());
		    	  
		   	 //  <a4j:region>
		     //	 (relationship cascade REFRESH)
		    	  
		    	  entityManager.flush();
		    	  entityManager.refresh(apm);
		    	  
		    	  acResBean=apm;
		    	  
		    	  String ac = ((LinksMap)Component.getInstance("linksMap",ScopeType.APPLICATION)).getAppCode().toString();
				  if(pappCode.equals(ac)){
					  cache();
				  }
				  
				  audit(ResourcesMap.RES, ActionsMap.UPDATE); 
				  
		   	   }
	          } catch (Exception e) {
	            log.error("editRes_ERROR="+e);
	            throw e;
	         }
		      return "editRes";
		   }
	   /**
		  * Удаление сущности Рубрика
		  * @return флаг успешности
		  * @throws Exception
		  */
	   public String delRes() throws Exception {
		      log.info("delRes:01");
		      try {
		    	  AcAppPage ap= entityManager.find(AcAppPage.class, acResBean.getIdRes()) ;
		    	  
		    	  entityManager.remove(ap);
		    	  
		    	   
		    	
		    	  entityManager.flush();
		     	
		    	 // [Вариант -1-]
		    	 // Основывается, что refresh - не каскадное :
		    	 // @OneToMany(mappedBy="idParent", cascade={CascadeType.REMOVE, /*CascadeType.REFRESH*/})
		    	 
		    	 // иначе при refresh родителя, будут refresh его потомки из списка idResCollection, 
		    	 // а этот список ещё содержит удалённого потомка, и при попытке его синхронизации 
		    	 // будет ошибка, так как после flush() в бд удалённого потомка уже нет
		    	 
		    	  entityManager.refresh(ap.getIdParent());
		    	  
		    	 // [Вариант -2-]
		    	 /* if/(par/ent!=null && parent.getI/dResCollecti/on()!=null /&& parent.getIdResCollection().contains(ap)){
		    		   /pare/nt.getIdResColle/ction().rem/ove(ap);/}*/
		    	  String ac = ((LinksMap)Component.getInstance("linksMap",ScopeType.APPLICATION)).getAppCode().toString();
				  if(ap.getAcApplication().toString().equals(ac)){
					cache();
				  }
				  
				  audit(ResourcesMap.RES, ActionsMap.DELETE); 
				  
		         } catch (Exception e) {
	           log.error("delRes_ERROR="+e);
	           throw e;
	          }
 		      return "delRes";
		   }
	   /**
		  * Подготовка сущности Рубрика 
		  * для последующих операций просмотра, создания, изменения
		  */
	   public void forView() {
		   String  pidRes = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("idRes");
			log.info("forView_pidRes="+pidRes);
			if(pidRes!=null){
				 acResBean = entityManager.find(AcAppPage.class, new Long(pidRes));
			}
		}
	   /**
		  * Подготовка сущности Рубрика 
		  * для последующих операций удаления
		  */
	   public void forViewDel() {
			String  pidRes = FacesContext.getCurrentInstance().getExternalContext()
					.getRequestParameterMap()
					.get("idRes");
			log.info("forViewDel_pidRes="+pidRes);
			if(pidRes!=null){
				 acResBean = entityManager.find(AcAppPage.class, new Long(pidRes));
				 if(acResBean.getIdResCollection()!=null&&!acResBean.getIdResCollection().isEmpty()){
					 dellMessage="У рубрики есть порождённые записи!\n При удалении они будут удалены!";
					 log.info("forViewDel:dellMessage="+dellMessage);
			    }
			}
	 }
	 public void cache(){
		try{
		/* 
		 Cach/eManager c/m = (Cach/eManager) Comp/onent/.getInstance("cacheMan/ager",ScopeType.EVENT);
		 c/m.remov/eCache("leftUni/tFragment", "tree/Navigation");
		 */
		}catch(Exception e){
		 log.error("armManager:cache:Error:"+e);
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
