package iac.grn.infosweb.session.navig;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.log.Log;

import java.util.*;

import javax.faces.context.FacesContext;

/**
 * ”правл€емый бин, определ€ющий задействованные в приложении ресурсы и 
 * их отображение на используемые локальные определени€ 
 * @author bubnov
 *
 */
@Name("linksMap")
@Scope(ScopeType.APPLICATION)
@Startup
 public class LinksMap {

	 @Logger private Log log;
	 
	/**
	 * —писок предоставл€емых приложением ресурсов
	 */
	 private Map lm= new HashMap();

	 /**
	  * »дентификационный код приложени€
	  */
	 private Long appCode=new Long(1);
	 
	 private Long superUserCode=new Long(1);
	 
	 
	 private String main="-1";
	 private String notFound="0";
	 private String lClOrg="001";
	 private String lUsr="002";
	 
     private String lClUsr="0022";
	 private String lRes="003";
	 private String lArm="004";
	 private String lArmSub="urn_access_subsystems";
	 
	 private String lRol="005";
	 private String lPerm="011";
	 private String aFunc="urn_part_audit_func";
	 private String aSys="urn_part_audit_sys";
	 private String aReport="urn_part_audit_report";
		
		
	 private String confParam="urn_conf_param";
	 
	 private String procAASys="urn_proc_audit_sys";
	 private String procAAFunc="urn_proc_audit_func";
	 private String procAToken="urn_proc_token";
	 private String procBUnBind="urn_proc_bind_unbind";
	 private String procBNoAct="urn_proc_bind_noact";
	 
	 private String ugroup="urn_ugroup";
	 
	 private String armgroup="urn_armgroup";
	 
	 private String servicesBindingOGK="urn_services_binding_OGK";
	 
	 private String appSystem="urn_app_system";
	 private String appUser="urn_app_user";
	 private String appAccess="urn_app_access";
	 private String appBlock="urn_app_block";
	 private String appSystemModify="urn_app_system_modify";
	 private String appUserModify="urn_app_user_modify";
	 private String appUserAccModify="urn_app_user_acc_modify";
	 private String appUserCertModify="urn_app_user_cert_modify";
	 private String appAdminSys="urn_app_admin_sys";
	 private String appOrgMan="urn_app_org_man";
	 
	 private String appUserDepModify="urn_app_user_dep_modify";
	 
	 
	 private String appMySystem="urn_appmy_system";
	 private String appMyUser="urn_appmy_user";
	 private String appMyBlock="urn_appmy_block";
	 private String appMyUserModify="urn_appmy_user_modify";
	 private String appMyUserAccModify="urn_appmy_user_acc_modify";
	 private String appMyUserCertModify="urn_appmy_user_cert_modify";
	 
	 private String auditUFMSView="006";
	 private String auditUFMSReports="0063";
	 private String auditZAGSView="007";
	 private String auditZAGSReports="0073";
	 
	 private String reposAll="Repos";
	
	
	 private String confHosts="0033";
	 
	 private String errorPerm="_errorPerm_";
	 
	 /**
	  * ”становка инициализирующих параметров при 
	  * развЄртывании приложени€
	  */
	 @Create
	 public void create() {
		 lm.put(main, "/main.xhtml");
		 lm.put(notFound, "/notFound.xhtml");
		 lm.put(errorPerm, "/error_perm.seam");
		 
		 lm.put(lClOrg, "/context_pro/mc/clorg/clorg_list.xhtml");
		 lm.put(lUsr, "/context_pro/mc/usr/usr_list.xhtml");
		 lm.put(lClUsr, "/context_pro/mc/clusr/clusr_list.xhtml");
		 lm.put(lRes, "/context_pro/mc/res/res_list.xhtml");
		 lm.put(lArm, "/context_pro/mc/arm/arm_list.xhtml");
		 lm.put(lArmSub, "/context_pro/mc/armsub/list.xhtml");
		 
		 lm.put(lRol, "/context_pro/mc/rol/rol_list.xhtml");
		 lm.put(lPerm, "/context_pro/mc/perm/perm_list.xhtml");
		 lm.put(aFunc, "/context_pro/mc/audit/func/func_list.xhtml");
		 lm.put(aSys, "/context_pro/mc/audit/sys/sys_list.xhtml");
		 lm.put(aReport, "/context_pro/mc/audit/report/list.xhtml");
		 
		 lm.put(confParam, "/context_pro/mc/cpar/cpar_list.xhtml");
		 lm.put(procAASys, "/context_pro/proc/archASys.xhtml");
		 lm.put(procAAFunc, "/context_pro/proc/archAFunc.xhtml");
		 lm.put(procAToken, "/context_pro/proc/archToken.xhtml");
		 lm.put(procBUnBind, "/context_pro/proc/bindUnBind.xhtml");
		 lm.put(procBNoAct, "/context_pro/proc/bindNoAct.xhtml");
		 
		 lm.put(ugroup, "/context_pro/mc/ugroup/ugroup_list.xhtml");
		 lm.put(armgroup, "/context_pro/mc/armgroup/list.xhtml");
		 
		 lm.put(servicesBindingOGK, "/context_pro/services/binding/usr_list.xhtml");
		 
		 lm.put(appSystem, "/context_pro/app/system/list.xhtml");
		 lm.put(appUser, "/context_pro/app/user/list.xhtml");
		 lm.put(appAccess, "/context_pro/app/access/list.xhtml");
		 lm.put(appBlock, "/context_pro/app/block/list.xhtml");
		 lm.put(appSystemModify, "/context_pro/app/system_modify/list.xhtml");
		 lm.put(appUserModify, "/context_pro/app/user_modify/list.xhtml");
		 lm.put(appUserAccModify, "/context_pro/app/user_acc_modify/list.xhtml");
		 lm.put(appUserCertModify, "/context_pro/app/user_cert_modify/list.xhtml");
		 lm.put(appAdminSys, "/context_pro/app/admin_sys/list.xhtml");
		 lm.put(appOrgMan, "/context_pro/app/org_man/list.xhtml");
		
		 lm.put(appUserDepModify, "/context_pro/app/user_dep_modify/list.xhtml");
		 
		 lm.put(appMySystem, "/context_pro/appmy/system/list.xhtml");
		 lm.put(appMyUser, "/context_pro/appmy/user/list.xhtml");
		 lm.put(appMyBlock, "/context_pro/appmy/block/list.xhtml");
		 lm.put(appMyUserModify, "/context_pro/appmy/user_modify/list.xhtml");
		 lm.put(appMyUserAccModify, "/context_pro/appmy/user_acc_modify/list.xhtml");
		 lm.put(appMyUserCertModify, "/context_pro/appmy/user_cert_modify/list.xhtml");
		 
		 lm.put(auditUFMSView, "/context_pro/audit/ufms/ufms_list.xhtml");
		 
		 lm.put(auditUFMSReports, "/context_pro/audit/ufms/reports.xhtml");
		 lm.put(auditZAGSView, "/context_pro/audit/zags/list.xhtml");
		 
		 lm.put(auditZAGSReports, "/context_pro/audit/zags/reports.xhtml");
		 
		 lm.put(reposAll, "/context_pro/repos/repos.seam");
		
		 
		 
		 lm.put(confHosts, "/context_pro/conf/hosts.xhtml");
		 
		 
	 }
	 /**
	  * ќбращение к ресурсу по его коду
	  * @param pCode код ресурса
	  * @return адрес размещени€ ресурса
	  */
	 public String getLink(String pCode){
		 
		 
		 
		 String link=null;
		 if(pCode!=null){
			if(pCode.startsWith("Repos")){
			  String[] sa = pCode.split("-");
			  if(sa.length==2){
				 link=(String)lm.get("Repos")+"?reposType="+sa[1]; 
			  }
			}else{
		      link=(String)lm.get(pCode);
		    }
		 }
		 if(pCode.equals("_errorPerm_")){
		  String actSect = FacesContext.getCurrentInstance().getExternalContext()
		         .getRequestParameterMap()
		         .get("actSect");
		  String actSectItem = FacesContext.getCurrentInstance().getExternalContext()
		         .getRequestParameterMap()
		         .get("actSectItem");
		  String idRai = FacesContext.getCurrentInstance().getExternalContext()
		         .getRequestParameterMap()
		         .get("idRai");
		 
		  log.info("LinksMap:getLink:actSect:"+actSect);
		  log.info("LinksMap:getLink:actSectItem:"+actSectItem);
		  log.info("LinksMap:getLink:idRai:"+idRai);
		  link=link+"?actSect="+actSect+"&actSectItem="+actSectItem+(idRai!=null ? "&idRai="+idRai : "");
		 }
		 return (link!=null?link:(String)lm.get(notFound));
	 }
	 /**
	  * »дентификационный код приложени€
	  * @return код приложени€
	  */
	 public Long getAppCode(){
		 return appCode;
	 }
	 
	 public Long getSuperUserCode(){
		 return superUserCode;
	 }
	 
	 public String getlUsr() {
			return lUsr;
	 }
	 
	 public String getlArm() {
			return lArm;
	 }

	
}
