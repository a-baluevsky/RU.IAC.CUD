package ru.spb.iac.cud.idp.core.access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.core.util.CUDConstants;

/**
 * EJB для предоставления информации по пользователю при авторизации
 * 
 * @author bubnov
 * 
 */

@Stateless
 public class IDPAccessManager implements IDPAccessManagerLocal,
		IDPAccessManagerRemote {

	@PersistenceContext(unitName = "AuthServices")
	EntityManager em;

	private static final Logger LOGGER = LoggerFactory.getLogger(IDPAccessManager.class);

	private static Map<Integer,String> attributesList;
	
	static{
	   
	   attributesList= new HashMap<Integer,String>();
		
       attributesList.put(0, "USER_UID");
       attributesList.put(1, "USER_LOGIN");
       attributesList.put(3, "USER_FIO");
       attributesList.put(4, "USER_PHONE");
       attributesList.put(5, "USER_EMAIL");
       attributesList.put(9, "ORG_NAME");
       attributesList.put(8, "ORG_CODE_IOGV");
       attributesList.put(10, "ORG_ADDRESS");
       attributesList.put(11, "ORG_PHONE");
       attributesList.put(7, "DEP_NAME");
       attributesList.put(6, "USER_POSITION");
       attributesList.put(12, "ORG_CODE_OKATO");
       attributesList.put(13, "DEP_ADDRESS");
		
	}
	
	/**
	 * получение аттрибутов пользователя
	 */
	public Map<String, String> attributes(String login) throws Exception {

		Map<String, String> result = new HashMap<String, String>();

		List<Object[]> lo = null;

		try {
			LOGGER.debug("attributes:login:" + login);

			
							
			Query query = em.createNativeQuery(				
							"SELECT t1.t1_id, " + 
							"       t1.t1_login,  " + 
							"       t1.t1_usr_code, " + 
							"       t1.t1_fio, " + 
							"       t1.t1_tel, " + 
							"       t1.t1_email, " + 
							"       t1.t1_pos, " + 
							"       t1.t1_dep_name, " + 
							"       t1.t1_org_code, " + 
							"       t1.t1_org_name, " + 
							"       t1.t1_org_adr, " + 
							"       t1.t1_org_tel, " + 
							"       t1.t1_org_okato, "+
							"       t1.t1_dep_adr " + 
							"  FROM (SELECT AU_FULL.ID_SRV t1_id, " + 
							"               AU_FULL.login t1_login, " + 
							"               AU_FULL.CERTIFICATE t1_cert, " + 
							"               t2.CL_USR_CODE t1_usr_code, " + 
							"               DECODE ( " + 
							"                  AU_FULL.UP_SIGN_USER, " + 
							"                  NULL,    AU_FULL.SURNAME " + 
							"                        || ' ' " + 
							"                        || AU_FULL.NAME_ " + 
							"                        || ' ' " + 
							"                        || AU_FULL.PATRONYMIC, " + 
							"                  CL_USR_FULL.FIO) " + 
							"                  t1_fio, " + 
							"               DECODE (AU_FULL.UP_SIGN_USER, " + 
							"                       NULL, AU_FULL.PHONE, " + 
							"                       CL_USR_FULL.PHONE) " + 
							"                  t1_tel, " + 
							"               DECODE (AU_FULL.UP_SIGN_USER, " + 
							"                       NULL, AU_FULL.E_MAIL, " + 
							"                       CL_USR_FULL.EMAIL) " + 
							"                  t1_email, " + 
							"               DECODE (AU_FULL.UP_SIGN_USER, " + 
							"                       NULL, AU_FULL.POSITION, " + 
							"                       CL_USR_FULL.POSITION) " + 
							"                  t1_pos, " + 
							"               DECODE ( " + 
							"                  AU_FULL.UP_SIGN_USER, " + 
							"                  NULL, AU_FULL.DEPARTMENT, " + 
							"                  DECODE (SUBSTR (CL_DEP_FULL.sign_object, 4, 2), " + 
							"                          '00', NULL, " + 
							"                          CL_DEP_FULL.FULL_)) " + 
							"                  t1_dep_name, " + 
							"               t1.CL_ORG_CODE t1_org_code, " + 
							"               CL_ORG_FULL.FULL_ t1_org_name, " + 
							"               CL_ORG_FULL.PREFIX " + 
							"               || DECODE (CL_ORG_FULL.HOUSE, " + 
							"                          NULL, NULL, " + 
							"                          ',' || CL_ORG_FULL.HOUSE) " + 
							"                  t1_org_adr, " + 
							"               CL_ORG_FULL.PHONE t1_org_tel, " + 
							"                   AU_FULL.STATUS t1_status, " + 
							"                 DECODE ( " + 
							"                  AU_FULL.UP_SIGN_USER, " + 
							"                  NULL, NULL, " + 
							"                  DECODE (SUBSTR (CL_DEP_FULL.sign_object, 4, 2), " + 
							"                          '00', NULL, " + 
							"                          CL_DEP_FULL.sign_object)) " + 
							"                  t1_dep_code, " + 
							"               CL_ORG_FULL.STATUS t1_org_status, " + 
							"               CL_usr_FULL.STATUS t1_usr_status, " + 
							"               DECODE ( " + 
							"                  AU_FULL.UP_SIGN_USER, " + 
							"                  NULL, NULL, " + 
							"                  DECODE (SUBSTR (CL_DEP_FULL.sign_object, 4, 2), " + 
							"                          '00', NULL, " + 
							"                          CL_DEP_FULL.STATUS)) " + 
							"                  t1_dep_status, " + 
							"               CL_ORG_FULL.SIGN_OKATO t1_org_okato, " + 
							"               CL_DEP_FULL.PREFIX " + 
							"               || DECODE (CL_DEP_FULL.HOUSE, " + 
							"                          NULL, NULL, " + 
							"                          ',' || CL_DEP_FULL.HOUSE) " + 
							"                  t1_dep_adr " + 
							"          FROM (  SELECT MAX (CL_ORG.ID_SRV) CL_ORG_ID, " + 
							"                         CL_ORG.SIGN_OBJECT CL_ORG_CODE " + 
							"                    FROM ISP_BSS_T cl_org, AC_USERS_KNL_T au " + 
							"                   WHERE AU.UP_SIGN = CL_ORG.SIGN_OBJECT AND AU.LOGIN = :login " + 
							"                GROUP BY CL_ORG.SIGN_OBJECT) t1, " + 
							"               (  SELECT MAX (CL_usr.ID_SRV) CL_USR_ID, " + 
							"                         CL_USR.SIGN_OBJECT CL_USR_CODE " + 
							"                    FROM ISP_BSS_T cl_usr, AC_USERS_KNL_T au " + 
							"                   WHERE AU.UP_SIGN_USER = CL_usr.SIGN_OBJECT " + 
							"                         AND AU.LOGIN = :login " + 
							"                GROUP BY CL_usr.SIGN_OBJECT) t2, " + 
							"               (  SELECT MAX (CL_dep.ID_SRV) CL_DEP_ID, " + 
							"                         CL_DEP.SIGN_OBJECT CL_DEP_CODE " + 
							"                    FROM ISP_BSS_T cl_dep, AC_USERS_KNL_T au " + 
							"                   WHERE SUBSTR (au.UP_SIGN_USER, 1, 5) || '000' = " + 
							"                            cl_dep.SIGN_OBJECT(+) " + 
							"                         AND AU.LOGIN = :login " + 
							"                GROUP BY CL_DEP.SIGN_OBJECT) t3, " + 
							"               ISP_BSS_T cl_org_full, " + 
							"               ISP_BSS_T cl_usr_full, " + 
							"               ISP_BSS_T cl_dep_full, " + 
							"               AC_USERS_KNL_T au_full " + 
							"         WHERE     cl_org_full.ID_SRV = CL_ORG_ID " + 
							"               AND cl_usr_full.ID_SRV(+) = CL_USR_ID " + 
							"               AND cl_DEP_full.ID_SRV(+) = CL_DEP_ID " + 
							"               AND au_full.UP_SIGN = CL_ORG_CODE " + 
							"               AND au_full.UP_SIGN_USER = CL_USR_CODE(+) " + 
							"               AND SUBSTR (au_full.UP_SIGN_USER, 1, 5) || '000' = " + 
							"                      CL_DEP_CODE(+) " + 
							"               AND au_full.login = :login) t1 ")
							
							
					.setParameter("login", login)
					.setHint("org.hibernate.readOnly", true);
					lo = query.getResultList();

			for (Object[] objectArray : lo) {

				String name = null;

				for (int i = 0; i < objectArray.length; i++) {

			
					if (attributesList.get(i) != null) {

						result.put(
								attributesList.get(i),
								objectArray[i] != null ? objectArray[i]
										.toString() : "");
					}
				}
			}

			if (result != null) {
				LOGGER.debug("attributes:result:" + result.size());
			}
		} catch (Exception e) {
			LOGGER.error("attributes:error:", e);
		}

		// если result = null, то будет выброшено исключение
		// в KeyStoreKeyManager.getValidatingKey() -
		// throw LOGGER.keyStoreMissingDomainAlias(domain);

		return result;
	}

	/**
	 * получение кодов ролей пользователя
	 */
	public List<String> rolesCodes(String login, String domain)
			throws Exception {

		List<String> result = new ArrayList<String>();
		try {
			LOGGER.debug("rolesCodes:domain:" + domain);

			if (domain != null
					&& (domain.startsWith(CUDConstants.armPrefix) || domain
							.startsWith(CUDConstants.subArmPrefix))) {
			
				result = (List<String>) em
						.createNativeQuery(
															
										"  SELECT * " + 
										"    FROM (  SELECT ROL.SIGN_OBJECT " + 
										"              FROM AC_IS_BSS_T sys, " + 
										"                   AC_ROLES_BSS_T rol, " + 
										"                   AC_USERS_LINK_KNL_T url, " + 
										"                   AC_USERS_KNL_T AU, " + 
										"                   AC_SUBSYSTEM_CERT_BSS_T subsys " + 
										"             WHERE (SYS.SIGN_OBJECT = :idIs " + 
										"                    OR SUBSYS.SUBSYSTEM_CODE = :idIs) " + 
										"                   AND ROL.ID_SRV = URL.UP_ROLES " + 
										"                   AND URL.UP_USERS = AU.ID_SRV " + 
										"                   AND ROL.UP_IS = sys.ID_SRV " + 
										"                   AND AU.LOGIN = :login " + 
										"                   AND SUBSYS.UP_IS(+) = SYS.ID_SRV " + 
										"          GROUP BY ROL.SIGN_OBJECT " + 
										"          UNION ALL " + 
										"            SELECT ROL.SIGN_OBJECT " + 
										"              FROM AC_IS_BSS_T sys, " + 
										"                   AC_ROLES_BSS_T rol, " + 
										"                   AC_USERS_KNL_T AU, " + 
										"                   AC_SUBSYSTEM_CERT_BSS_T subsys, " + 
										"                   LINK_GROUP_USERS_ROLES_KNL_T lugr, " + 
										"                   LINK_GROUP_USERS_USERS_KNL_T lugu " + 
										"             WHERE (SYS.SIGN_OBJECT = :idIs " + 
										"                    OR SUBSYS.SUBSYSTEM_CODE = :idIs) " + 
										"                   AND ROL.ID_SRV = LUGR.UP_ROLES " + 
										"                   AND LUGU.UP_GROUP_USERS = LUGR.UP_GROUP_USERS " + 
										"                   AND LUGU.UP_USERS = AU.ID_SRV " + 
										"                   AND ROL.UP_IS = sys.ID_SRV " + 
										"                   AND AU.LOGIN = :login " + 
										"                   AND SUBSYS.UP_IS(+) = SYS.ID_SRV " + 
										"          GROUP BY ROL.SIGN_OBJECT) " + 
										"GROUP BY SIGN_OBJECT")
										
										
						.setParameter("idIs", domain)
						.setParameter("login", login).getResultList();

			} else if (domain != null
					&& domain.startsWith(CUDConstants.groupArmPrefix)) {

				result = (List<String>) em
						.createNativeQuery(
								
										
										"SELECT '[' || sys_code || ']' || role_code role_full_code " + 
										"    FROM (  SELECT SYS.SIGN_OBJECT sys_code, ROL.SIGN_OBJECT role_code " + 
										"              FROM GROUP_SYSTEMS_KNL_T gsys, " + 
										"                   AC_IS_BSS_T sys, " + 
										"                   AC_ROLES_BSS_T rol, " + 
										"                   LINK_GROUP_SYS_SYS_KNL_T lgr, " + 
										"                   AC_USERS_LINK_KNL_T url, " + 
										"                   AC_USERS_KNL_T AU " + 
										"             WHERE     GSYS.GROUP_CODE = :idIs " + 
										"                   AND GSYS.ID_SRV = LGR.UP_GROUP_SYSTEMS " + 
										"                   AND LGR.UP_SYSTEMS = SYS.ID_SRV " + 
										"                   AND ROL.UP_IS = SYS.ID_SRV " + 
										"                   AND ROL.ID_SRV = URL.UP_ROLES " + 
										"                   AND URL.UP_USERS  = AU.ID_SRV " + 
										"                   AND AU.LOGIN = :login " + 
										"          GROUP BY SYS.SIGN_OBJECT, ROL.SIGN_OBJECT " + 
										"          UNION ALL " + 
										"            SELECT SYS.SIGN_OBJECT sys_code, ROL.SIGN_OBJECT role_code " + 
										"              FROM GROUP_SYSTEMS_KNL_T gsys, " + 
										"                   AC_IS_BSS_T sys, " + 
										"                   AC_ROLES_BSS_T rol, " + 
										"                   LINK_GROUP_SYS_SYS_KNL_T lgr, " + 
										"                   LINK_GROUP_USERS_ROLES_KNL_T lugr, " + 
										"                   LINK_GROUP_USERS_USERS_KNL_T lugu, " + 
										"                   AC_USERS_KNL_T AU " + 
										"             WHERE     GSYS.GROUP_CODE = :idIs " + 
										"                   AND GSYS.ID_SRV = LGR.UP_GROUP_SYSTEMS " + 
										"                   AND LGR.UP_SYSTEMS = SYS.ID_SRV " + 
										"                   AND ROL.UP_IS = SYS.ID_SRV " + 
										"                   AND ROL.ID_SRV = LUGR.UP_ROLES " + 
										"                   AND LUGU.UP_GROUP_USERS = LUGR.UP_GROUP_USERS(+) " + 
										"                   AND LUGU.UP_USERS = AU.ID_SRV " + 
										"                   AND AU.LOGIN = :login " + 
										"          GROUP BY SYS.SIGN_OBJECT, ROL.SIGN_OBJECT) " + 
										"GROUP BY sys_code, role_code " + 
										"ORDER BY sys_code ")
										
										
						.setParameter("idIs", domain)
						.setParameter("login", login).getResultList();

			}

			if (result != null) {
				LOGGER.debug("rolesCodes:result:" + result.size());
			}
		} catch (Exception e) {
			LOGGER.error("rolesCodes:error:", e);
		}

		return result;
	}

	/**
	 * получение списка подсистем
	 */
	@SuppressWarnings("unchecked")
	public List<String> resources(String login, String domain) throws Exception {

		List<String> result = new ArrayList<String>();
		try {
			LOGGER.debug("resources:01:" + domain);

			if (domain != null
					&& (domain.startsWith(CUDConstants.armPrefix) || domain
							.startsWith(CUDConstants.subArmPrefix))) {

				// закомментированно, т.к. ресурсы отдаём
				// только для групп систем
				/*
				 * result = (List<String>) em.createNativeQuery(
				 * "select '['||sys_full.SIGN_OBJECT||']' || '['||sys_full.FULL_||']' || '['||sys_full.LINKS ||']' from AC_IS_BSS_T sys_full, "
				 * + " (select   SYS.ID_SRV sys_id " +
				 * "                     from  AC_IS_BSS_T sys,     " +
				 * "                              AC_ROLES_BSS_T rol,     " +
				 * "                              AC_USERS_LINK_KNL_T url,     "
				 * + "                              AC_USERS_KNL_T AU,     " +
				 * "                              AC_SUBSYSTEM_CERT_BSS_T subsys,   "
				 * +
				 * "                              LINK_GROUP_USERS_ROLES_KNL_T lugr,   "
				 * +
				 * "                              LINK_GROUP_USERS_USERS_KNL_T lugu     "
				 * +
				 * "                     where (SYS.SIGN_OBJECT= :idIs or  SUBSYS.SUBSYSTEM_CODE=:idIs)     "
				 * +
				 * "                           and (ROL.ID_SRV = URL.UP_ROLES or ROL.ID_SRV = LUGR.UP_ROLES )   "
				 * +
				 * "                           and LUGU.UP_GROUP_USERS= LUGR.UP_GROUP_USERS(+)   "
				 * +
				 * "                           and LUGU.UP_USERS(+)  = AU.ID_SRV   "
				 * +
				 * "                           and URL.UP_USERS(+)  = AU.ID_SRV   "
				 * + "                           and ROL.UP_IS=sys.ID_SRV    " +
				 * "                           and AU.LOGIN= :login     " +
				 * "                           and  SUBSYS.UP_IS(+) =SYS.ID_SRV     "
				 * + "                     group by SYS.ID_SRV  ) t1 " +
				 * "                     " +
				 * "                     where t1.sys_id = SYS_FULL.ID_SRV ")
				 * .setParameter("idIs", domain) .setParameter("login", login)
				 * .getResultList();
				 */

			} else if (domain != null
					&& domain.startsWith(CUDConstants.groupArmPrefix)) {

				result = (List<String>) em
						.createNativeQuery(
								" select '['||sys_full.SIGN_OBJECT||']' || '['||sys_full.FULL_||']' || '['|| sys_full.LINKS ||']' "
										+ "                         from AC_IS_BSS_T sys_full, (   "
										+ "                         select SYS.ID_SRV sys_id  "
										+ "                         from GROUP_SYSTEMS_KNL_T gsys,   "
										+ "                         AC_IS_BSS_T sys,   "
										+ "                          AC_ROLES_BSS_T rol,   "
										+ "                          LINK_GROUP_SYS_SYS_KNL_T lgr,   "
										+ "                          LINK_GROUP_USERS_ROLES_KNL_T lugr,   "
										+ "                          LINK_GROUP_USERS_USERS_KNL_T lugu,   "
										+ "                          AC_USERS_LINK_KNL_T url,   "
										+ "                          AC_USERS_KNL_T AU    "
										+ "                         where GSYS.GROUP_CODE=:idIs   "
										+ "                         and GSYS.ID_SRV=LGR.UP_GROUP_SYSTEMS   "
										+ "                         and LGR.UP_SYSTEMS=SYS.ID_SRV   "
										+ "                         and ROL.UP_IS= SYS.ID_SRV   "
										+ "                         and (ROL.ID_SRV = URL.UP_ROLES or ROL.ID_SRV = LUGR.UP_ROLES )   "
										+ "                         and LUGU.UP_GROUP_USERS = LUGR.UP_GROUP_USERS(+)   "
										+ "                         and LUGU.UP_USERS(+)  = AU.ID_SRV   "
										+ "                         and URL.UP_USERS(+)  = AU.ID_SRV   "
										+ "                         and AU.LOGIN= :login    "
										+ "                         group by SYS.ID_SRV )   t1 "
										+ "                          where t1.sys_id = SYS_FULL.ID_SRV "
										+ "                         order by sys_full.SIGN_OBJECT")
						.setParameter("idIs", domain)
						.setParameter("login", login).getResultList();

			}

			if (result != null) {
				LOGGER.debug("resources:result:" + result.size());
			}
		} catch (Exception e) {
			LOGGER.error("resources:error:", e);
		}

		return result;
	}

	public void saveTokenID(String tokenID, String userID) throws Exception{
		
	 try{
		LOGGER.debug("saveTokenID:01");
		
		em.createNativeQuery(
				 "insert into TOKEN_KNL_T "
				 + "(ID_SRV, UP_USERS, SIGN_OBJECT,  CREATED ) "
				 + "values "
				 + "(TOKEN_KNL_SEQ.nextval, ?, ?, sysdate) ")
				 .setParameter(1, userID)
				 .setParameter(2, tokenID)
				 .executeUpdate();
	} catch (Exception e) {
		LOGGER.error("saveTokenID:error:", e);
	}
	}
}
