package ru.spb.iac.cud.uarm.ejb.context.user;

import iac.cud.infosweb.dataitems.BaseItem;
import iac.cud.infosweb.dataitems.UCCertItem;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.uarm.ejb.audit.ActionsMap;
import ru.spb.iac.cud.uarm.ejb.audit.AuditExportData;
import ru.spb.iac.cud.uarm.ejb.audit.ResourcesMap;
import ru.spb.iac.cud.uarm.ejb.entity.AcIsBssT;
import ru.spb.iac.cud.uarm.ejb.entity.AcRolesBssT;
import ru.spb.iac.cud.uarm.ejb.entity.AcUsersCertBssT;
import ru.spb.iac.cud.uarm.ejb.entity.AcUsersKnlT;
import ru.spb.iac.cud.uarm.ejb.entity.GroupUsersKnlT;
import ru.spb.iac.cud.uarm.ejb.entity.JournAppAccessBssT;
import ru.spb.iac.cud.uarm.ejb.entity.JournAppAdminUserSysBssT;
import ru.spb.iac.cud.uarm.ejb.entity.JournAppOrgManagerBssT;

/**
 * Session Bean implementation class HomeBean
 */
@Stateless(mappedName = "userManagerEJB")
@LocalBean
 public class UserManagerEJB {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserManagerEJB.class);
	
	@PersistenceContext(unitName = "CUDUserConsolePU")
    private EntityManager entityManager;
	
	@EJB(beanName = "CUDUserConsole-ejb.jar#AuditExportData")
	private AuditExportData auditExportData;
	
    public UserManagerEJB() {
       
    }

    
    public AcUsersKnlT getUserItem(Long idUser){
 	   
    	LOGGER.debug("UserManagerEJB:getUserItem:idUser:"+idUser);
 	   
 	   if(idUser==null){
 		  return null;
 	   }
 	   
 	   try{
            List<Object[]> lo=null;
            AcUsersKnlT.UserItem ui = null;
            DateFormat df = new SimpleDateFormat ("dd.MM.yy HH:mm:ss");
            
          
            
            lo=entityManager.createNativeQuery(
         		   "select t1.t1_id, t1.t1_login, t1.t1_cert, t1.t1_usr_code, t1.t1_fio, t1.t1_tel, t1.t1_email,t1.t1_pos, t1.t1_dep_name, "+
         				   "t1.t1_org_code, t1.t1_org_name, t1.t1_org_adr, t1.t1_org_tel, t1.t1_start, t1.t1_end, t1.t1_status, "+
         				    "t1.t1_crt_date, t1.t1_crt_usr_login, t1.t1_upd_date, t1.t1_upd_usr_login, "+
         				    "t1.t1_dep_code, t1.t1_org_status, t1.t1_usr_status, t1.t1_dep_status, t1.t1_iogv_bind_type,  "+
         				    "t1.t1_email_second "+ 
         				   "from( "+
         				   "select AU_FULL.ID_SRV t1_id, AU_FULL.login t1_login, AU_FULL.CERTIFICATE t1_cert, t2.CL_USR_CODE t1_usr_code, "+
         				    "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO ) t1_fio, "+  
         				     "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.PHONE, CL_USR_FULL.PHONE ) t1_tel, "+   
         				     "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.E_MAIL, CL_USR_FULL.EMAIL) t1_email, "+  
         				     "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.POSITION, CL_USR_FULL.POSITION)t1_pos, "+  
         				     "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.DEPARTMENT, decode(substr(CL_DEP_FULL.sign_object,4,2), '00', null, CL_DEP_FULL.FULL_)) t1_dep_name, "+ 
         				     "t1.CL_ORG_CODE t1_org_code, CL_ORG_FULL.FULL_ t1_org_name, "+
         				     "CL_ORG_FULL.PREFIX || decode(CL_ORG_FULL.HOUSE, null, null, ','  ||CL_ORG_FULL.HOUSE  ) t1_org_adr, "+
         				     "CL_ORG_FULL.PHONE t1_org_tel, "+
         				     "to_char(AU_FULL.START_ACCOUNT, 'DD.MM.YY HH24:MI:SS') t1_start, "+ 
         				     "to_char(AU_FULL.END_ACCOUNT, 'DD.MM.YY HH24:MI:SS') t1_end, "+  
         				     "AU_FULL.STATUS t1_status, "+  
         				     "AU_FULL.CREATED t1_crt_date, "+ 
         				     "USR_CRT.LOGIN t1_crt_usr_login, "+ 
         				     "to_char(AU_FULL.MODIFIED, 'DD.MM.YY HH24:MI:SS') t1_upd_date, "+ 
         				     "USR_UPD.LOGIN t1_upd_usr_login, "+ 
         				     "decode(AU_FULL.UP_SIGN_USER, null, null, decode(substr(CL_DEP_FULL.sign_object,4,2), '00', null, CL_DEP_FULL.sign_object)) t1_dep_code, "+ 
         				     "CL_ORG_FULL.STATUS t1_org_status,  CL_usr_FULL.STATUS t1_usr_status, "+ 
         				      "decode(AU_FULL.UP_SIGN_USER, null, null, decode(substr(CL_DEP_FULL.sign_object,4,2), '00', null, CL_DEP_FULL.STATUS)) t1_dep_status, " +
         				      "AU_FULL.UP_BINDING t1_iogv_bind_type,  AU_FULL.E_MAIL t1_email_second  "+      
         				   "from "+
         				   "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE "+
         				   "from ISP_BSS_T cl_org, "+
         				   "AC_USERS_KNL_T au "+
         				   "where AU.UP_SIGN = CL_ORG.SIGN_OBJECT "+
         				   "group by CL_ORG.SIGN_OBJECT) t1, "+
         				   "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "+
         				   "from ISP_BSS_T cl_usr, "+
         				   "AC_USERS_KNL_T au "+
         				   "where AU.UP_SIGN_USER  = CL_usr.SIGN_OBJECT "+
         				   "group by CL_usr.SIGN_OBJECT) t2, "+
         				   "(select max(CL_dep.ID_SRV) CL_DEP_ID,  CL_DEP.SIGN_OBJECT  CL_DEP_CODE "+
         				   "from ISP_BSS_T cl_dep, "+
         				   "AC_USERS_KNL_T au "+
         				   "where substr(au.UP_SIGN_USER,1,5)||'000'  =cl_dep.SIGN_OBJECT(+) "+
         				   "group by CL_DEP.SIGN_OBJECT) t3, "+
         				   "ISP_BSS_T cl_org_full, "+
         				   "ISP_BSS_T cl_usr_full, "+
         				   "ISP_BSS_T cl_dep_full, "+
         				   "AC_USERS_KNL_T au_full, "+
         				   "AC_USERS_KNL_T usr_crt, "+  
         				   "AC_USERS_KNL_T usr_upd "+
         				   "where cl_org_full.ID_SRV= CL_ORG_ID "+
         				   "and cl_usr_full.ID_SRV(+)=CL_USR_ID "+
         				   "and cl_DEP_full.ID_SRV(+)=CL_DEP_ID "+
         				   "and au_full.UP_SIGN = CL_ORG_CODE "+
         				   "and au_full.UP_SIGN_USER  =  CL_USR_CODE(+) "+
         				   "and substr(au_full.UP_SIGN_USER,1,5)||'000'  =  CL_DEP_CODE(+) "+
         				   "and au_full.CREATOR=USR_CRT.ID_SRV "+ 
         				   "and au_full.MODIFICATOR=USR_UPD.ID_SRV(+) " +
         				   "and au_full.ID_SRV=? "+ 
         				   ")t1 ")
          .setParameter(1, idUser)
          .getResultList();
            
            for(Object[] objectArray :lo){
         	   try{
         		  LOGGER.debug("UserManagerEJB:getUserItem:login:"+objectArray[1].toString());
         		 
         		 AcUsersKnlT userDataItem = new AcUsersKnlT();
         		 
         	     ui= userDataItem.new UserItem(
         			  objectArray[0]!=null?new Long(objectArray[0].toString()):null,
         			  objectArray[1]!=null?objectArray[1].toString():"",
         			  objectArray[2]!=null?objectArray[2].toString():"",
         			  objectArray[3]!=null?objectArray[3].toString():"",
         			  objectArray[4]!=null?objectArray[4].toString():"",
         			  objectArray[5]!=null?objectArray[5].toString():"",
         			   //!!!
         			   //для email вместо "" ставим null [для UserForgotEJB.step1()]
         			  objectArray[6]!=null?objectArray[6].toString():null,
         			  objectArray[7]!=null?objectArray[7].toString():"",
         			  objectArray[8]!=null?objectArray[8].toString():"",
         			  objectArray[9]!=null?objectArray[9].toString():"",
         			  objectArray[10]!=null?objectArray[10].toString():"",
         			  objectArray[11]!=null?objectArray[11].toString():"",
         			  objectArray[12]!=null?objectArray[12].toString():"",
         			  objectArray[13]!=null?objectArray[13].toString():"",
         			  objectArray[14]!=null?objectArray[14].toString():"",
         			  objectArray[15]!=null?new Long(objectArray[15].toString()):null,
         			  objectArray[16]!=null?df.format((Date)objectArray[16]) :"",
         			  objectArray[17]!=null?objectArray[17].toString():"",
         			  objectArray[18]!=null?objectArray[18].toString():"",
         			  objectArray[19]!=null?objectArray[19].toString():"",
         			  objectArray[20]!=null?objectArray[20].toString():"",
         			  objectArray[21]!=null?objectArray[21].toString():"",
         			  objectArray[22]!=null?objectArray[22].toString():"",
         			  objectArray[23]!=null?objectArray[23].toString():"",
         			  objectArray[24]!=null?new Long(objectArray[24].toString()):null,
         			   //!!!
         			   //для email вместо "" ставим null [для UserForgotEJB.step1()]
         			  objectArray[25]!=null?objectArray[25].toString():null
         			   );
         	     
         	     userDataItem.setUserItem(ui);
         	     
         	     return userDataItem;
         	   }catch(Exception e1){
         		  LOGGER.error("UserManagerEJB:getUserItem:for:error:"+e1);
         	   }
            }  
 	   }catch(Exception e){
 		  LOGGER.error("UserManagerEJB:getUserItem:error:"+e);
 	   }
 	   return null;
    }
    
    public AcUsersKnlT getUserItemFromLogin(String loginUser){
        return getUserItem(getIdUser(loginUser));
    }
    
    public List<AcIsBssT> getUserRoles(Long idUser){
  	   
    	LOGGER.debug("UserManagerEJB:getUserRoles:idUser:"+idUser);
 	   
 	   if(idUser==null){
 		  return null;
 	   }
 	   
 	   try{
            List<Object[]> lo=null;
            List<AcIsBssT> armList=null;
            List<AcRolesBssT> roleList=null;
            AcIsBssT arm = null;
            AcRolesBssT role = null;
            Long idArm = null;
           
             lo=entityManager.createNativeQuery(
         		    "select  sys_full.id_srv sys_id,  sys_full.FULL_ sys_name, " +
         		    "rol_full.id_srv role_id, rol_full.FULL_ role_name "+
            		"from( "+
            		"select SYS.ID_SRV sys_id,  ROL.ID_SRV role_id "+
            		"from  "+
            		"AC_IS_BSS_T sys, "+
            		 "AC_ROLES_BSS_T rol, "+
            		  "LINK_GROUP_USERS_ROLES_KNL_T lugr, "+
            		 "LINK_GROUP_USERS_USERS_KNL_T lugu, "+
            		 "AC_USERS_LINK_KNL_T url, "+  
            		 "AC_USERS_KNL_T AU  "+
            		"where  "+
            		 "ROL.UP_IS= SYS.ID_SRV "+
            		"and (ROL.ID_SRV = URL.UP_ROLES or ROL.ID_SRV = LUGR.UP_ROLES ) "+
            		"and LUGU.UP_GROUP_USERS = LUGR.UP_GROUP_USERS(+) "+
            		"and LUGU.UP_USERS(+)  = AU.ID_SRV "+
            		"and URL.UP_USERS(+)  = AU.ID_SRV "+
            		"and AU.ID_SRV= :idUser  "+
            		"group by SYS.ID_SRV,  ROL.ID_SRV "+
            		"),  "+
            		"AC_IS_BSS_T sys_full,  AC_ROLES_BSS_T rol_full "+
            		"where sys_full.id_srv = sys_id "+
            		"and rol_full.id_srv =  role_id "+
            		"order by  sys_full.FULL_ , rol_full.FULL_"
            		)
          .setParameter("idUser", idUser)
          .getResultList();
            
            for(Object[] objectArray :lo){
            	
            	
            	idArm = (objectArray[0]!=null?new Long(objectArray[0].toString()):0L);
            			
            	if(armList==null){
            		armList = new ArrayList<AcIsBssT>();
            	}
              	
            	if(arm==null||!idArm.equals(arm.getIdSrv())){
            		
            	  arm = new AcIsBssT();
            	  roleList = new ArrayList<AcRolesBssT>();
                  arm.setAcRolesBssTs(roleList);
                 
                  arm.setIdSrv(idArm);
                  arm.setFull (objectArray[1]!=null?objectArray[1].toString():"");
              
                  armList.add(arm);
                 
                }
            	
                role = new AcRolesBssT();
                roleList.add(role);
                
                role.setIdSrv (objectArray[2]!=null?new Long(objectArray[2].toString()):null);
                role.setFull (objectArray[3]!=null?objectArray[3].toString():"");
       
            }  
            
            return armList;
            
 	   }catch(Exception e){
 		  LOGGER.error("UserManagerEJB:getUserRoles:error:"+e);
 	   }
 	   return null;
    }
    
    public List<AcIsBssT> getArmList(){
    	   
    	LOGGER.debug("UserManagerEJB:getArmList:01");
 	   
 	   
    	 try{
             List<AcIsBssT> armList=entityManager.createQuery(
          		   "select t1 from AcIsBssT t1 "+
          		   "order by t1.full ")
                  .getResultList();
            
            return armList;
             
  	   }catch(Exception e){
  		  LOGGER.error("UserManagerEJB:getArmList:error:"+e);
  	   }
 	   return null;
    }
    
    public List<JournAppAccessBssT> getAppAccessList(Long idUser){
 	   
    	LOGGER.debug("UserManagerEJB:getAppAccessList:01:"+idUser);
 	   
 	   
    	 try{
             List<JournAppAccessBssT> armList=entityManager.createQuery(
          		   "select t1 from JournAppAccessBssT t1 " +
          		   "where t1.acUsersKnlT2.idSrv = :idUser "+
          		   "order by t1.idSrv desc ")
          		  .setParameter("idUser", idUser)
                  .getResultList();
            
            return armList;
             
  	   }catch(Exception e){
  		  LOGGER.error("UserManagerEJB:getAppAccessList:error:"+e);
  	   }
 	   return null;
    }
    
    public List<JournAppAccessBssT> getAppAccessGroupsList(Long idUser){
  	   
    	LOGGER.debug("UserManagerEJB:getAppAccessGroupsList:01:"+idUser);
 	   
 	   
    	 try{
             List<JournAppAccessBssT> armList=entityManager.createQuery(
          		   "select t1 from JournAppAccessGroupsBssT t1 " +
          		   "where t1.acUsersKnlT2.idSrv = :idUser "+
          		   "order by t1.idSrv desc ")
          		  .setParameter("idUser", idUser)
                  .getResultList();
            
            return armList;
             
  	   }catch(Exception e){
  		  LOGGER.error("UserManagerEJB:getAppAccessGroupsList:error:"+e);
  	   }
 	   return null;
    }
    
    public List<JournAppAdminUserSysBssT> getAppAdminUserSysList(Long idUser){
  	   
    	LOGGER.debug("UserManagerEJB:getAppAdminUserSysList:01:"+idUser);
 	   
 	   
    	 try{
             List<JournAppAdminUserSysBssT> armList=entityManager.createQuery(
          		   "select t1 from JournAppAdminUserSysBssT t1 " +
          		   "where t1.acUsersKnlT2.idSrv = :idUser "+
          		   "order by t1.idSrv desc ")
          		  .setParameter("idUser", idUser)
                  .getResultList();
            
            return armList;
             
  	   }catch(Exception e){
  		  LOGGER.error("UserManagerEJB:getAppAdminUserSysList:error:"+e);
  	   }
 	   return null;
    }
    
    public List<JournAppOrgManagerBssT> getAppOrgManList(Long idUser){
   	   
    	LOGGER.debug("UserManagerEJB:getAppOrgManList:01:"+idUser);
 	   
 	   
    	 try{
             List<JournAppOrgManagerBssT> armList=entityManager.createQuery(
          		   "select t1 from JournAppOrgManagerBssT t1 " +
          		   "where t1.acUsersKnlT2.idSrv = :idUser "+
          		   "order by t1.idSrv desc ")
          		  .setParameter("idUser", idUser)
                  .getResultList();
            
            return armList;
             
  	   }catch(Exception e){
  		  LOGGER.error("UserManagerEJB:getAppOrgManList:error:"+e);
  	   }
 	   return null;
    }
    
    public List<AcRolesBssT> getListRolesFromArm(Long pidArm){
    	   
    	LOGGER.debug("UserManagerEJB:getListRolesFromArm:01:"+pidArm);
 	   
 	   if(pidArm==null){
 		   return null;
 	   }
    	
    	 try{
             List<Object[]> lo=null;
             List<AcIsBssT> armList=null;
             List<AcRolesBssT> roleList=null;
             AcIsBssT arm = null;
             AcRolesBssT role = null;
             Long idArm = null;
              
              lo=entityManager.createNativeQuery(
          		   "select sys_full.id_srv sys_id,  sys_full.FULL_ sys_name, " +
          		   "rol_full.id_srv role_id, rol_full.FULL_ role_name, rol_full.SIGN_OBJECT role_code "+
          		   "from( "+
          		   "select SYS.ID_SRV sys_id,  ROL.ID_SRV role_id "+
          		   "from "+ 
          		   "AC_IS_BSS_T sys, "+
          		   "AC_ROLES_BSS_T rol "+
          		   "where  "+
          		   "ROL.UP_IS= SYS.ID_SRV "+
          		   (pidArm!=null?"and SYS.ID_SRV="+pidArm+" ":"")+
                   " group by SYS.ID_SRV,  ROL.ID_SRV "+
          		   "),  "+
          		   "AC_IS_BSS_T sys_full,  AC_ROLES_BSS_T rol_full "+
          		   "where sys_full.id_srv = sys_id "+
          		   "and rol_full.id_srv =  role_id "+
          		   "order by  sys_full.FULL_ , rol_full.FULL_ "
             		)
           .getResultList();
             
             for(Object[] objectArray :lo){
             	
             	
             	idArm = (objectArray[0]!=null?new Long(objectArray[0].toString()):0L);
             			
             	if(armList==null){
             		armList = new ArrayList<AcIsBssT>();
             	}
               	
             	if(arm==null||!idArm.equals(arm.getIdSrv())){
             		
             	  arm = new AcIsBssT();
             	  roleList = new ArrayList<AcRolesBssT>();
                   arm.setAcRolesBssTs(roleList);
                  
                   arm.setIdSrv(idArm);
                   arm.setFull (objectArray[1]!=null?objectArray[1].toString():"");
               
                   armList.add(arm);
                  
                 }
             	
                 role = new AcRolesBssT();
                 roleList.add(role);
                 
                 role.setIdSrv (objectArray[2]!=null?new Long(objectArray[2].toString()):null);
                 role.setFull (objectArray[3]!=null?objectArray[3].toString():"");
                 role.setSignObject (objectArray[4]!=null?objectArray[4].toString():"");
        
             }  
             
             if(!armList.isEmpty()){
                return armList.get(0).getAcRolesBssTs();
             }else{
            	return null;
             }
  	   }catch(Exception e){
  		  LOGGER.error("UserManagerEJB:getListRolesFromArm:error:"+e);
  		   }
 	   return null;
    } 
    
    public List<GroupUsersKnlT> getListGroupsFromArm(Long pidArm){
 	   
    	LOGGER.debug("UserManagerEJB:getListGroupsFromArm:01:"+pidArm);
 	   
 	   if(pidArm==null){
 		   return null;
 	   }
    	
    	 try{
             List<Object[]> lo=null;
             List<AcIsBssT> armList=null;
             List<GroupUsersKnlT> group_list=null;
             AcIsBssT arm = null;
             GroupUsersKnlT group = null;
             Long idArm = null;
              
              lo=entityManager.createNativeQuery(
          		   "select sys_full.id_srv sys_id,  sys_full.FULL_ sys_name, "+
					 "gr_full.id_srv gr_id, gr_full.FULL_ gr_name, gr_full.SIGN_OBJECT gr_code "+ 
					 "from( "+ 
					"select  RL.UP_IS sys_id, GR.ID_SRV gr_id "+
					 "from GROUP_USERS_KNL_T gr, "+
					 "LINK_GROUP_USERS_ROLES_KNL_T lgur, "+
					 "AC_ROLES_BSS_T rl "+
					 "where  LGUR.UP_GROUP_USERS=GR.ID_SRV "+
					 "and   LGUR.UP_ROLES = RL.ID_SRV "+
					 (pidArm!=null?"and RL.UP_IS="+pidArm+" ":"")+
					 "group by RL.UP_IS, GR.ID_SRV "+
					 "), "+
					 "AC_IS_BSS_T sys_full, "+
					 "GROUP_USERS_KNL_T gr_full "+
					 "where sys_full.ID_SRV = sys_id "+
					 "and gr_full.ID_SRV = gr_id "+
					 "order by  sys_full.FULL_ , gr_full.FULL_")
           .getResultList();
             
             for(Object[] objectArray :lo){
             	
             	
             	idArm = (objectArray[0]!=null?new Long(objectArray[0].toString()):0L);
             			
             	if(armList==null){
             		armList = new ArrayList<AcIsBssT>();
             	}
               	
             	if(arm==null||!idArm.equals(arm.getIdSrv())){
             		
             	   arm = new AcIsBssT();
             	   group_list = new ArrayList<GroupUsersKnlT>();
                   arm.setGroups(group_list);
                  
                   arm.setIdSrv(idArm);
                   arm.setFull (objectArray[1]!=null?objectArray[1].toString():"");
               
                   armList.add(arm);
                  
                 }
             	
                 group = new GroupUsersKnlT();
                 group_list.add(group);
                 
                 group.setIdSrv (objectArray[2]!=null?new Long(objectArray[2].toString()):null);
                 group.setFull (objectArray[3]!=null?objectArray[3].toString():"");
                 group.setSignObject (objectArray[4]!=null?objectArray[4].toString():"");
        
             }  
             
             if(!armList.isEmpty()){
                return armList.get(0).getGroups();
             }else{
            	return null;
             }
  	   }catch(Exception e){
  		  LOGGER.error("UserManagerEJB:getListGroupsFromArm:error:"+e);
  	   }
 	   return null;
    } 
    
    public void changePassword(Long idUser, String userOldPassword, String newPassword) throws Exception{
    	
    	 LOGGER.debug("UserManagerEJB:changePassword:01");
    	 
    	 try{
    		 
    		 entityManager.createNativeQuery(
            		   "select 1 from  AC_USERS_KNL_T t1 " +
            		   "where t1.ID_SRV = ? "+
            		   "and t1.PASSWORD_ = ? ")
            		   .setParameter(1, idUser)
            		   .setParameter(2, userOldPassword)
                   .getSingleResult(); 
    		 
           entityManager.createNativeQuery(
          		   "update  AC_USERS_KNL_T t1 " +
          		   "set t1. PASSWORD_ = ? "+
          		   "where t1.ID_SRV = ? ")
          		   .setParameter(1, newPassword)
          		   .setParameter(2, idUser)
                  .executeUpdate();
            
           audit(ResourcesMap.USER, ActionsMap.UPDATE_PASSWORD); 
           
         }catch(Exception e){
  		  LOGGER.error("UserManagerEJB:changePassword:error:"+e);
  		  throw e;
  	   }
    	 
     }
    
    public void uploadCertFile(AcUsersCertBssT userCert) throws Exception {
      LOGGER.debug("UserManagerEJB:uploadCertFile:01");
    	 
    	 try{
    		 
    		 entityManager.persist(userCert);
            
    		 audit(ResourcesMap.USER, ActionsMap.ADD_CERTIFICATE);
    		 
         }catch(Exception e){
  		  LOGGER.error("UserManagerEJB:uploadCertFile:error:"+e);
  		  throw e;
  	   }
    }
    
    public boolean certNumExistCrt(String certNum) throws Exception {
 	   
    	LOGGER.debug("UserManagerEJB:certNumExistCrt:certNum="+certNum);
		
    	boolean result = false;
    	
		if(certNum!=null&&!"".equals(certNum.trim())){
		  try{
			  certNum = certNum.replaceAll(" ", "").toUpperCase();
			  
			  entityManager.createNativeQuery(
			      "select 1 from dual "+
			      "where exists( select 1 from  AC_USERS_KNL_T au where upper(AU.CERTIFICATE) = upper(:certNum)) "+
			      "or  exists( select 1 from  AC_USERS_CERT_BSS_T  user_cert  where upper(USER_CERT.CERT_NUM) = upper(:certNum) ) ") 
			  .setParameter("certNum", certNum)   
			  .getSingleResult();
			  
			  result=true;
			  
				  LOGGER.debug("UserManagerEJB:certNumExistCrt:addCertNumExist!");		     
		    }catch (NoResultException ex){
		    	LOGGER.error("UserManagerEJB:certNumExistCrt:NoResultException");
          }catch(Exception e){
        	  LOGGER.error("UserManagerEJB:certNumExistCrt:Error:"+e);
	           throw e;
        }
		}
		
		return result;
  }
    
    
    private Long getIdUser(String loginUser){
  	   
       LOGGER.debug("UserManagerEJB:getIdUser:01:"+loginUser);
 	   
       Long result = null;
       
 	   if(loginUser==null){
 		  return null;
 	   }
 	   
 	   try{
             
            result= ((BigDecimal) entityManager.createNativeQuery(
         		   "select AU_FULL.ID_SRV "+
                   "from AC_USERS_KNL_T au_full "+
                   "where au_full.login=? ")
             .setParameter(1, loginUser)
             .getSingleResult()).longValue();
              
 	   }catch(NullPointerException e){
 		  LOGGER.error("UserManagerEJB:getIdUser:error_NPE:"+e);
 	   }catch(Exception e){
 		  LOGGER.error("UserManagerEJB:getIdUser:error:"+e);
 	   }
 	   return result;
    }
    
    
    public List<GroupUsersKnlT> getUserGroups(Long idUser) throws Exception{
  	   
    	LOGGER.debug("UserManagerEJB:getUserGroups:01:"+idUser);
 	   
 	   if(idUser==null){
 		   return null;
 	   }
 	   List<GroupUsersKnlT> listUsrGroupForView = null;
 	  
    	 try{
    		    List<Object[]> lo=null;
    		    GroupUsersKnlT group = null;
    		    AcIsBssT app = null;
    		    AcRolesBssT rol = null;
    		    int group_change_flag=0;
    		    
    		   	
    		    		lo=entityManager.createNativeQuery(
    		    				"select GR.ID_SRV gr_id, GR.FULL_ gr_name, APP.ID_SRV app_id, APP.FULL_ app_name, ROL.FULL_ role_name "+
    	                        "from GROUP_USERS_KNL_T gr, LINK_GROUP_USERS_USERS_KNL_T uul, "+
    	                        "LINK_GROUP_USERS_ROLES_KNL_T lur, AC_ROLES_BSS_T rol, AC_IS_BSS_T app "+
    	                        "where UUL.UP_GROUP_USERS=GR.ID_SRV and UUL.UP_USERS=? "+
    	                        "and LUR.UP_GROUP_USERS=GR.ID_SRV and ROL.ID_SRV=LUR.UP_ROLES "+
    	                        "and APP.ID_SRV=ROL.UP_IS "+
    	                        "order by GR.FULL_, GR.ID_SRV, APP.FULL_, APP.ID_SRV, ROL.FULL_ ")
    		    				.setParameter(1, idUser)
    		    				.getResultList();
    		    		
    		    		listUsrGroupForView = new ArrayList<GroupUsersKnlT>();
    		    		
    		    		for(Object[] objectArray :lo){
    		    			
    		    			if(group==null||!group.getIdSrv().toString().equals(objectArray[0].toString())){
    		    				
    		    				group=new GroupUsersKnlT();
    			    			   
    		    				listUsrGroupForView.add(group);
    			    			   
    		    				group.setIdSrv(new Long(objectArray[0].toString()));
    		    				group.setFull(objectArray[1]!=null?objectArray[1].toString():"");
    		    				group.setArmList(new ArrayList<AcIsBssT>());
    		    				
    		    				group_change_flag=1;
    			    		}
    		    			
    		    			if(app==null||!app.getIdSrv().toString().equals(objectArray[2].toString())||group_change_flag==1){
    		    			  
    		    		       app=new AcIsBssT();
    		    			   
    		    			   group.getArmList().add(app);
    		    			   
    		    			   app.setIdSrv(new Long(objectArray[2].toString()));
    		    			   app.setFull(objectArray[3]!=null?objectArray[3].toString():"");
    		    			   app.setAcRolesBssTs(new ArrayList<AcRolesBssT>());
    		    			 }
    		    			 
    		    			 rol=new AcRolesBssT();
    		    			 rol.setFull(objectArray[4]!=null?objectArray[4].toString():"");
    		    			 
    		    			 app.getAcRolesBssTs().add(rol);
    		    			 
    		    			 group_change_flag=0;
    		    		 }
    		    return listUsrGroupForView;
    		    
  	   }catch(Exception e){
  		  LOGGER.error("UserManagerEJB:getUserGroups:error:"+e);
  		  throw e;
  	   }
 	  
    } 
    
  public List<BaseItem> getUserCertList(Long authUserID) throws Exception{
		
	  List<BaseItem> userCertList=null;
		
	  UCCertItem ui = null;
	  
	  try{
		
		  //уже имеющиеся сертификаты пользователя
		     
			   List<Object[]> user_cert_list  = (List<Object[]>) entityManager.createNativeQuery(
					      
		                "select AUC.ID_SRV, auc.ORG_NAME, auc.USER_FIO, auc.DEP_NAME, " + 
		                "AUC.CERT_NUM, AUC.CERT_DATE " + 
		                "from AC_USERS_CERT_BSS_T auc " +
		                "where AUC.UP_USER = ? ")
		                .setParameter(1, authUserID)
			    		.setMaxResults(100)
			    		.getResultList();
			     
			   LOGGER.debug("UserManagerEJB:getUserCertList:size:"+user_cert_list.size());
			     
			     userCertList= new ArrayList<BaseItem>();
			     
			     for(Object[] objectArray : user_cert_list){
			    	 
			    	 try{
		        	     ui= new UCCertItem(
		        			  objectArray[0]!=null?new Long(objectArray[0].toString()):null,
		        			  objectArray[1]!=null?objectArray[1].toString():"",
		        			  objectArray[2]!=null?objectArray[2].toString():"",
		        			  objectArray[3]!=null?objectArray[3].toString():"",
		        			   "",
		        			  objectArray[4]!=null?objectArray[4].toString():"",
		        			  objectArray[5]!=null?objectArray[5].toString():"",
		        			   "");
		        	     userCertList.add(ui);
		        	   }catch(Exception e1){
		        		   LOGGER.error("UserManagerEJB:getUserCertList:for:error:"+e1);
		        	   } 
			    	 
			     }
     		
      }catch(Exception e){
		  LOGGER.error("UserManagerEJB:getUserCertList:error:"+e);
		  throw e;
	   }
		
		return userCertList;
	}

  public void audit(ResourcesMap resourcesMap, ActionsMap actionsMap){
	   try{
		  
		   auditExportData.addFunc(resourcesMap.getCode()+":"+actionsMap.getCode());
		   
	   }catch(Exception e){
		   LOGGER.error("UserManagerEJB:audit:error:"+e);
	   }
 }

    
}
