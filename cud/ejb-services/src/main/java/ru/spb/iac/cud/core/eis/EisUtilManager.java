package ru.spb.iac.cud.core.eis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.core.UtilManagerLocal;
import ru.spb.iac.cud.core.UtilManagerRemote;
import ru.spb.iac.cud.core.util.CUDConstants;
import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.items.Group;
import ru.spb.iac.cud.items.GroupsData;
import ru.spb.iac.cud.items.ISOrganisations;
import ru.spb.iac.cud.items.Resource;
import ru.spb.iac.cud.items.ResourceNU;
import ru.spb.iac.cud.items.ResourcesData;
import ru.spb.iac.cud.items.Role;
import ru.spb.iac.cud.items.User;
import ru.spb.iac.cud.items.UsersData;
import ru.spb.iac.cud.util.CommonUtil;

/**
 * EJB для справочного сервиса
 * 
 * @author bubnov
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
 public class EisUtilManager implements UtilManagerLocal, UtilManagerRemote {

	@PersistenceContext(unitName = "AuthServices")
	EntityManager em;

	private static final Integer MAX_CONT_USERS = 50;

	private static final Integer MAX_CONT_USERS_ACCOUNTS = 500;
	
	private static final Integer MAX_CONT_GROUPS = 50;

	private static final Integer MAX_CONT_RES = 50;

	private static final String LINKS_SEPARATOR = ",";

	private static final Logger LOGGER = LoggerFactory.getLogger(EisUtilManager.class);

	private static final String SELECT_ROLE_START = 
			" SELECT gr_id, '[' || sys_code || ']' || role_code role_full_code "
			+ " FROM ( "
			+ " SELECT gr.ID_SRV gr_id, SYS.SIGN_OBJECT sys_code, ROL.SIGN_OBJECT role_code "
			+ "    FROM AC_IS_BSS_T sys, "
			+ "         AC_ROLES_BSS_T rol, "
			+ "         GROUP_USERS_KNL_T gr, "
			+ "         LINK_GROUP_USERS_ROLES_KNL_T lugr, "
			+ "         GROUP_SYSTEMS_KNL_T gsys, "
			+ "         LINK_GROUP_SYS_SYS_KNL_T lgr "
			+ "   WHERE  ROL.ID_SRV = LUGR.UP_ROLES "
			+ "         AND LUGR.UP_GROUP_USERS = gr.ID_SRV "
			+ "         AND ROL.UP_IS = sys.ID_SRV "
			+ "         AND gr.ID_SRV IN (";
	
	private static final String SELECT_ROLE_END =
			         ") "
					+ "   AND GSYS.GROUP_CODE = :idIS "
					+ "   AND GSYS.ID_SRV = LGR.UP_GROUP_SYSTEMS "
					+ "   AND LGR.UP_SYSTEMS = SYS.ID_SRV "
					+ "GROUP BY gr.ID_SRV, SYS.SIGN_OBJECT, ROL.SIGN_OBJECT ) "
					+ "ORDER BY gr_id, sys_code ";
			
	public EisUtilManager() {
	}

	/**
	 * данные по пользователям
	 */
	public UsersData users_data(String idIS, List<String> uidsUsers,
			String category, List<String> rolesCodes, List<String> groupsCodes,
			Integer start, Integer count, List<String> settings,
			Long idUserAuth, String IPAddress) throws GeneralFailure {

		LOGGER.debug("users_data:01");

		// onlyISUsers условие сильнее, чем rolesCodes
		// то есть, если стоит onlyISUsers = false [все пользователи],
		// то rolesCodes уже не учитывается

		Integer resultCount = 0;
		List<User> result = new ArrayList<User>();
		Map<String, User> resultIds = new HashMap<String, User>();
		
		List<Object[]> lo = null;
		
		UsersData isu = new UsersData();

		String rolesLine = null;
		String groupsLine = null;
		String usersIdsLine = null;
	
		try {

			Map<String,String> settingsMap = getSettingsMap(settings);
			
			if (idIS == null) {
				LOGGER.debug("users_data:return:1");
				throw new GeneralFailure("idIS is null!");
			}

			if (count == null ) {
				
				count = MAX_CONT_USERS;
				
				if("TRUE".equals(settingsMap.get("ACCOUNTS_ONLY"))){
					count = MAX_CONT_USERS_ACCOUNTS;
				}
				
			}
			if (start == null) {
				start = 0;
			}

			if (MAX_CONT_USERS < count&&!"TRUE".equals(settingsMap.get("ACCOUNTS_ONLY"))) {
				LOGGER.debug("users_data:return:2");
				throw new GeneralFailure("'count' should be less than "
						+ MAX_CONT_USERS);
			}

			if (MAX_CONT_USERS_ACCOUNTS < count&&"TRUE".equals(settingsMap.get("ACCOUNTS_ONLY"))) {
				LOGGER.debug("users_data:return:32");
				throw new GeneralFailure("'count' with 'ACCOUNTS_ONLY' should be less than "
						+ MAX_CONT_USERS_ACCOUNTS);
			}
			
			LOGGER.debug("users_data:idIS1:" + idIS);

			String uidsLine = null;

            uidsLine = CommonUtil.createLine(uidsUsers);
			

			if (CUDConstants.categorySYS.equals(category) && rolesCodes != null
					&& !rolesCodes.isEmpty()) {

				rolesLine = CommonUtil.createLine(rolesCodes);
			
			}

			if (
			groupsCodes != null && !groupsCodes.isEmpty()) {

				groupsLine = CommonUtil.createLine(groupsCodes);
				
			
			}

			if (idIS.startsWith(CUDConstants.armPrefix)
					|| idIS.startsWith(CUDConstants.subArmPrefix)) {

				
				// !!!
				idIS = getCodeIs(idIS);

				LOGGER.debug("is_users:idIS2:" + idIS);

				String filterSt = null;

				if (rolesLine != null && groupsLine != null) {
					filterSt = "and( " + "((ROL.SIGN_OBJECT IN (" + rolesLine
							+ ") and ais.SIGN_OBJECT = :idIS)  "
							+ "   or (ROL_group.SIGN_OBJECT IN (" + rolesLine
							+ " )  and ais_group.SIGN_OBJECT = :idIS) ) "
							+ "or (GR.SIGN_OBJECT IN (" + groupsLine + ") ) "
							+ ") ";
				} else if (rolesLine != null) {

					filterSt = "and " + "((ROL.SIGN_OBJECT IN (" + rolesLine
							+ ") and ais.SIGN_OBJECT = :idIS) "
							+ "   or (ROL_group.SIGN_OBJECT IN (" + rolesLine
							+ " )  and ais_group.SIGN_OBJECT = :idIS) ) ";
				} else if (groupsLine != null) {
					filterSt = "and " + "GR.SIGN_OBJECT IN (" + groupsLine
							+ ") ";
				}

				// системы и подсистемы
				// 1.пользователи

				lo = em.createNativeQuery(
						"select t1.t1_id, t1.t1_login, t1.t1_cert, t1.t1_usr_code, t1.t1_fio, t1.t1_tel, t1.t1_email,t1.t1_pos, t1.t1_dep_name, "
								+ "t1.t1_org_code, t1.t1_org_name, t1.t1_org_adr, t1.t1_org_tel, t1.t1_start, t1.t1_end, t1.t1_status, "
								+ "t1.t1_crt_date, t1.t1_crt_usr_login, t1.t1_upd_date, t1.t1_upd_usr_login, "
								+ "t1.t1_dep_code, t1.t1_org_status, t1.t1_usr_status, t1.t1_dep_status, t1.t1_org_okato, t1.t1_dep_adr "
								+ "from( "
								+ "select AU_FULL.ID_SRV t1_id, AU_FULL.login t1_login, AU_FULL.CERTIFICATE t1_cert, t2.CL_USR_CODE t1_usr_code, "
								+ "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO ) t1_fio, "
								+ "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.PHONE, CL_USR_FULL.PHONE ) t1_tel, "
								+ "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.E_MAIL, CL_USR_FULL.EMAIL) t1_email, "
								+ "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.POSITION, CL_USR_FULL.POSITION)t1_pos, "
								+ "decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.DEPARTMENT, decode(substr(CL_DEP_FULL.sign_object,4,2), '00', null, CL_DEP_FULL.FULL_)) t1_dep_name, "
								+ "t1.CL_ORG_CODE t1_org_code, CL_ORG_FULL.FULL_ t1_org_name, "
								+ "CL_ORG_FULL.PREFIX || decode(CL_ORG_FULL.HOUSE, null, null, ','  ||CL_ORG_FULL.HOUSE  ) t1_org_adr, "
								+ "CL_ORG_FULL.PHONE t1_org_tel, "
								+ "to_char(AU_FULL.START_ACCOUNT, 'DD.MM.YY HH24:MI:SS') t1_start, "
								+ "to_char(AU_FULL.END_ACCOUNT, 'DD.MM.YY HH24:MI:SS') t1_end, "
								+ "AU_FULL.STATUS t1_status, "
								+ "AU_FULL.CREATED t1_crt_date, "
								+ "USR_CRT.LOGIN t1_crt_usr_login, "
								+ "to_char(AU_FULL.MODIFIED, 'DD.MM.YY HH24:MI:SS') t1_upd_date, "
								+ "USR_UPD.LOGIN t1_upd_usr_login, "
								+ "decode(AU_FULL.UP_SIGN_USER, null, null, decode(substr(CL_DEP_FULL.sign_object,4,2), '00', null, CL_DEP_FULL.sign_object)) t1_dep_code, "
								+ "CL_ORG_FULL.STATUS t1_org_status,  CL_usr_FULL.STATUS t1_usr_status, "
								+ "decode(AU_FULL.UP_SIGN_USER, null, null, decode(substr(CL_DEP_FULL.sign_object,4,2), '00', null, CL_DEP_FULL.STATUS)) t1_dep_status, "
								+ "CL_ORG_FULL.SIGN_OKATO t1_org_okato, CL_DEP_FULL.PREFIX || decode(CL_DEP_FULL.HOUSE, null, null, ','  ||CL_DEP_FULL.HOUSE  ) t1_dep_adr  "
								+ "from "
								+ "(select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE "
								+ "from ISP_BSS_T cl_org, "
								+ "AC_USERS_KNL_T au "
								+ "where AU.UP_SIGN = CL_ORG.SIGN_OBJECT "
								+ "group by CL_ORG.SIGN_OBJECT) t1, "
								+ "(select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "
								+ "from ISP_BSS_T cl_usr, "
								+ "AC_USERS_KNL_T au "
								+ "where AU.UP_SIGN_USER  = CL_usr.SIGN_OBJECT "
								+ "group by CL_usr.SIGN_OBJECT) t2, "
								+ "(select max(CL_dep.ID_SRV) CL_DEP_ID,  CL_DEP.SIGN_OBJECT  CL_DEP_CODE "
								+ "from ISP_BSS_T cl_dep, "
								+ "AC_USERS_KNL_T au  "
								+ "where substr(au.UP_SIGN_USER,1,5)||'000'  =cl_dep.SIGN_OBJECT(+) "
								+ "group by CL_DEP.SIGN_OBJECT) t3, "
								+ "( "
								+
								// core-1-
								"select t_core.t_core_id "
								+ "from( "
								+ "select AU_FULL.ID_SRV t_core_id "
								+ "from "
								+ "AC_USERS_KNL_T au_full, "
								+ "LINK_GROUP_USERS_USERS_KNL_T lgu, "
								+ "AC_USERS_LINK_KNL_T lur, "
								+ "AC_ROLES_BSS_T rol, "
								+ "AC_ROLES_BSS_T rol_group, "
								+ "LINK_GROUP_USERS_ROLES_KNL_T lgr, "
								+ "AC_IS_BSS_T ais, "
								+ "AC_IS_BSS_T ais_group, "
								+ "GROUP_USERS_KNL_T gr "
								+ "where "
								+ "lur.UP_USERS(+)=AU_FULL.ID_SRV and "
								+ "lgu.UP_USERS(+)=AU_FULL.ID_SRV "
								+ "and ROL.ID_SRV(+)=LUR.UP_ROLES "
								+
								

								"and lgr.UP_GROUP_USERS(+)=GR.ID_SRV "
								+ "and lgu.UP_GROUP_USERS=GR.ID_SRV(+) "
								+

								"and rol_group.ID_SRV(+) =LGR.UP_ROLES "
								+ "and ROL.UP_IS=ais.ID_SRV(+) "
								+ "and ROL_group.UP_IS=ais_group.ID_SRV(+) "
								+ "and (ais.SIGN_OBJECT  = :idIS "
								+ "or ais_group.SIGN_OBJECT = :idIS "
								+ "or -1 = :onlyISUsers ) "
								+

								(uidsLine != null ? "and to_char(au_full.ID_SRV) in ("
										+ uidsLine + ") "
										: " ")
								+

								(filterSt != null ? filterSt : " ")
								+

								"order by t_core_id "
								+ ")t_core "
								+ "group by t_core_id "
								+ "order by t_core.t_core_id "
								+

								// core-2-
								") t4, "
								+

								"ISP_BSS_T cl_org_full, "
								+ "ISP_BSS_T cl_usr_full, "
								+ "ISP_BSS_T cl_dep_full, "
								+ "AC_USERS_KNL_T au_full, "
								+ "AC_USERS_KNL_T usr_crt, "
								+ "AC_USERS_KNL_T usr_upd "
								+ "where cl_org_full.ID_SRV= CL_ORG_ID "
								+ "and cl_usr_full.ID_SRV(+)=CL_USR_ID "
								+ "and cl_DEP_full.ID_SRV(+)=CL_DEP_ID "
								+ "and au_full.UP_SIGN = CL_ORG_CODE "
								+ "and au_full.UP_SIGN_USER  =  CL_USR_CODE(+) "
								+ "and substr(au_full.UP_SIGN_USER,1,5)||'000'  =  CL_DEP_CODE(+) "
								+ "and au_full.CREATOR=USR_CRT.ID_SRV "
								+ "and au_full.MODIFICATOR=USR_UPD.ID_SRV(+) "
								+ "and AU_FULL.ID_SRV=t4.t_core_id "
								+ ")t1 "
								+ "where 1=1 "
								+
								(settingsMap.containsKey("FILTER_FIO") ? 
										 " and upper(t1_fio) LIKE  upper('%"+settingsMap.get("FILTER_FIO")+"%')" : "")
								+
								(settingsMap.containsKey("FILTER_ORG") ? 
										 " and upper(t1_org_name) LIKE  upper('%"+settingsMap.get("FILTER_ORG")+"%')" : "")
								
								+ "order by t1_fio ")
						.setParameter("idIS", idIS)
						.setParameter(
								"onlyISUsers",
								CUDConstants.categorySYS.equals(category) ? 1
										: -1)
						.setFirstResult(start != null ? start.intValue() : 0)
						.setMaxResults(
								count != null ? count.intValue() : 1000000)
						.getResultList();

				usersIdsLine = CommonUtil.createAttributes(lo,  result, resultIds);
			
				

				LOGGER.debug("users_data:02");

				// 2.роли

			if(!"TRUE".equals(settingsMap.get("ACCOUNTS_ONLY"))){
				
				lo = em.createNativeQuery(
						"select AU.ID_SRV, ROL.SIGN_OBJECT "
								+ "from  AC_IS_BSS_T sys, "
								+ "        AC_ROLES_BSS_T rol, "
								+ "        AC_USERS_LINK_KNL_T url, "
								+ "        AC_USERS_KNL_T AU, "
								+ "        AC_SUBSYSTEM_CERT_BSS_T subsys, "
								+ "        LINK_GROUP_USERS_ROLES_KNL_T lugr, "
								+ "        LINK_GROUP_USERS_USERS_KNL_T lugu "
								+ "where (SYS.SIGN_OBJECT= :idIS or  SUBSYS.SUBSYSTEM_CODE=:idIS) "
								+ "     and (ROL.ID_SRV = URL.UP_ROLES or ROL.ID_SRV = LUGR.UP_ROLES )  "
								+ "     and LUGU.UP_GROUP_USERS= LUGR.UP_GROUP_USERS(+)  "
								+ "     and LUGU.UP_USERS(+)  = AU.ID_SRV  "
								+ "     and URL.UP_USERS(+)  = AU.ID_SRV "
								+ "     and ROL.UP_IS=sys.ID_SRV   "
								+ "     and AU.ID_SRV IN(" + usersIdsLine
								+ ")  "
								+ "     and  SUBSYS.UP_IS(+) =SYS.ID_SRV  "
								+ "group by AU.ID_SRV, ROL.SIGN_OBJECT  "
								+ "order by AU.ID_SRV, ROL.SIGN_OBJECT ")
						.setParameter("idIS", idIS).getResultList();

				for (Object[] objectArrayRoles : lo) {

					if (resultIds.get(objectArrayRoles[0].toString()).getCodesRoles() == null) {
						resultIds.get(objectArrayRoles[0].toString()).setCodesRoles(new ArrayList<String>());
					}

					resultIds.get(objectArrayRoles[0].toString()).getCodesRoles().add(objectArrayRoles[1].toString());
				}

				// 3 .группы
				lo = em.createNativeQuery(
						"select LGU.UP_USERS, GR.SIGN_OBJECT "
								+ " from  GROUP_USERS_KNL_T gr, "
								+ " LINK_GROUP_USERS_USERS_KNL_T lgu "
								+ " where LGU.UP_GROUP_USERS=GR.ID_SRV "
								+ " and  LGU.UP_USERS in (" + usersIdsLine+ ") "
								+ " order by LGU.UP_USERS, GR.SIGN_OBJECT ")
						.getResultList();

				for (Object[] objectArrayGroups : lo) {

					if (resultIds.get(objectArrayGroups[0].toString()).getCodesGroups() == null) {
						resultIds.get(objectArrayGroups[0].toString()).setCodesGroups(new ArrayList<String>());
					}

					if(objectArrayGroups[1]!=null){
					  resultIds.get(objectArrayGroups[0].toString()).getCodesGroups().add(objectArrayGroups[1].toString());
					}

				}

			}
				
				
				// 4.количество

				LOGGER.debug("users_data:03");

			if(!"TRUE".equals(settingsMap.get("ACCOUNTS_ONLY"))){	
				resultCount = ((java.math.BigDecimal) em
						.createNativeQuery(
								"select count(*) from ( "
										+ "select t_core.t_core_id "
										+ "from( "
										+ "select AU_FULL.ID_SRV t_core_id "
										+ "from "
										+ "AC_USERS_KNL_T au_full, "
										+ "LINK_GROUP_USERS_USERS_KNL_T lgu, "
										+ "AC_USERS_LINK_KNL_T lur, "
										+ "AC_ROLES_BSS_T rol, "
										+ "AC_ROLES_BSS_T rol_group, "
										+ "LINK_GROUP_USERS_ROLES_KNL_T lgr, "
										+ "AC_IS_BSS_T ais, "
										+ "AC_IS_BSS_T ais_group, "
										+ "GROUP_USERS_KNL_T gr "
										+ "where "
										+ "lur.UP_USERS(+)=AU_FULL.ID_SRV and "
										+ "lgu.UP_USERS(+)=AU_FULL.ID_SRV "
										+ "and ROL.ID_SRV(+)=LUR.UP_ROLES "
										+ "and lgr.UP_GROUP_USERS(+)=GR.ID_SRV "
										+ "and lgu.UP_GROUP_USERS=GR.ID_SRV(+) "
										+ "and rol_group.ID_SRV(+) =LGR.UP_ROLES "
										+ "and ROL.UP_IS=ais.ID_SRV(+) "
										+ "and ROL_group.UP_IS=ais_group.ID_SRV(+) "
										+ "and (ais.SIGN_OBJECT  = :idIS "
										+ "or ais_group.SIGN_OBJECT = :idIS "
										+ "or -1 = :onlyISUsers ) "
										+
                                       (uidsLine != null ? "and to_char(au_full.ID_SRV) in ("
												+ uidsLine + ") "
												: " ") +

										(filterSt != null ? filterSt : " ") +

										"order by t_core_id " + ")t_core "
										+ "group by t_core_id "
										+ "order by t_core.t_core_id "
										+ " )")
						.setParameter("idIS", idIS)
						.setParameter("onlyISUsers",
								CUDConstants.categorySYS.equals(category) ? 1: -1).getSingleResult()).intValue();
			   }else{
				   
				   resultCount =  result.size();
				   
				   LOGGER.debug("users_data:count:01_1:"+resultCount);
				   LOGGER.debug("users_data:count:01_2:"+count);
				   
				   if (resultCount.intValue()==count.intValue()){
					   //записей на самом деле больше чем count
					   //поэтому нужен подсчёт через запрос
				   
				   resultCount = ((java.math.BigDecimal) em
							.createNativeQuery(
									"SELECT COUNT (*) " + 
									"  FROM (  SELECT t1.t1_fio, t1.t1_org_name " + 
									"            FROM (SELECT DECODE ( " + 
									"                            AU_FULL.UP_SIGN_USER, " + 
									"                            NULL,    AU_FULL.SURNAME " + 
									"                                  || ' ' " + 
									"                                  || AU_FULL.NAME_ " + 
									"                                  || ' ' " + 
									"                                  || AU_FULL.PATRONYMIC, " + 
									"                            CL_USR_FULL.FIO) " + 
									"                            t1_fio, " + 
									"                         t1.CL_ORG_CODE t1_org_code, " + 
									"                         CL_ORG_FULL.FULL_ t1_org_name " + 
									"                    FROM (  SELECT MAX (CL_ORG.ID_SRV) CL_ORG_ID, " + 
									"                                   CL_ORG.SIGN_OBJECT CL_ORG_CODE " + 
									"                              FROM ISP_BSS_T cl_org, AC_USERS_KNL_T au " + 
									"                             WHERE AU.UP_SIGN = CL_ORG.SIGN_OBJECT " + 
									"                          GROUP BY CL_ORG.SIGN_OBJECT) t1, " + 
									"                         (  SELECT MAX (CL_usr.ID_SRV) CL_USR_ID, " + 
									"                                   CL_USR.SIGN_OBJECT CL_USR_CODE " + 
									"                              FROM ISP_BSS_T cl_usr, AC_USERS_KNL_T au " + 
									"                             WHERE AU.UP_SIGN_USER = CL_usr.SIGN_OBJECT " + 
									"                          GROUP BY CL_usr.SIGN_OBJECT) t2, " + 
									"                         (  SELECT t_core.t_core_id " + 
									"                              FROM (  SELECT AU_FULL.ID_SRV t_core_id " + 
									"                                        FROM AC_USERS_KNL_T au_full, " + 
									"                                             LINK_GROUP_USERS_USERS_KNL_T lgu, " + 
									"                                             AC_USERS_LINK_KNL_T lur, " + 
									"                                             AC_ROLES_BSS_T rol, " + 
									"                                             AC_ROLES_BSS_T rol_group, " + 
									"                                             LINK_GROUP_USERS_ROLES_KNL_T lgr, " + 
									"                                             AC_IS_BSS_T ais, " + 
									"                                             AC_IS_BSS_T ais_group, " + 
									"                                             GROUP_USERS_KNL_T gr " + 
									"                                       WHERE     lur.UP_USERS(+) = AU_FULL.ID_SRV " + 
									"                                             AND lgu.UP_USERS(+) = AU_FULL.ID_SRV " + 
									"                                             AND ROL.ID_SRV(+) = LUR.UP_ROLES " + 
									"                                             AND lgr.UP_GROUP_USERS(+) = GR.ID_SRV " + 
									"                                             AND lgu.UP_GROUP_USERS = GR.ID_SRV(+) " + 
									"                                             AND rol_group.ID_SRV(+) = LGR.UP_ROLES " + 
									"                                             AND ROL.UP_IS = ais.ID_SRV(+) " + 
									"                                             AND ROL_group.UP_IS = " + 
									"                                                    ais_group.ID_SRV(+) " + 
									"                                      and (ais.SIGN_OBJECT  = :idIS " + 
									"                                      or ais_group.SIGN_OBJECT = :idIS " + 
									"                                      or -1 = :onlyISUsers )  " + 
									
									(uidsLine != null ? "and to_char(au_full.ID_SRV) in ("
											+ uidsLine + ") "
											: " ")+

									(filterSt != null ? filterSt : " ")+
									
									
									"                         ORDER BY t_core_id) t_core " + 
									"                          GROUP BY t_core_id " + 
									"                          ORDER BY t_core.t_core_id) t4, " + 
									"                         ISP_BSS_T cl_org_full, " + 
									"                         ISP_BSS_T cl_usr_full, " + 
									"                         AC_USERS_KNL_T au_full, " + 
									"                         AC_USERS_KNL_T usr_crt, " + 
									"                         AC_USERS_KNL_T usr_upd " + 
									"                   WHERE     cl_org_full.ID_SRV = CL_ORG_ID " + 
									"                         AND cl_usr_full.ID_SRV(+) = CL_USR_ID " + 
									"                         AND au_full.UP_SIGN = CL_ORG_CODE " + 
									"                         AND au_full.UP_SIGN_USER = CL_USR_CODE(+) " + 
									"                         AND au_full.CREATOR = USR_CRT.ID_SRV " + 
									"                         AND au_full.MODIFICATOR = USR_UPD.ID_SRV(+) " + 
									"                         AND AU_FULL.ID_SRV = t4.t_core_id) t1 " + 
									"           WHERE 1 = 1 " + 
										
									(settingsMap.containsKey("FILTER_FIO") ? 
											 " and upper(t1_fio) LIKE  upper('%"+settingsMap.get("FILTER_FIO")+"%')" : "") +
									
									(settingsMap.containsKey("FILTER_ORG") ? 
											 " and upper(t1_org_name) LIKE  upper('%"+settingsMap.get("FILTER_ORG")+"%')" : "") +
								    
									" ) ")
							.setParameter("idIS", idIS)
							.setParameter("onlyISUsers",
									CUDConstants.categorySYS.equals(category) ? 1: -1).getSingleResult()).intValue();
				   }
				   
			   }
			
			} else if (idIS.startsWith(CUDConstants.groupArmPrefix)) {

				String filterSt = null;

				if (rolesLine != null && groupsLine != null) {
					filterSt = "and( " + "((ROL.SIGN_OBJECT IN (" + rolesLine
							+ ") and ais.SIGN_OBJECT = :idIS)  "
							+ "   or (ROL_group.SIGN_OBJECT IN (" + rolesLine
							+ " )  and LINKSYSGROUP.UP_SYSTEMS=AIS.ID_SRV) ) "
							+ "or (GR.SIGN_OBJECT IN (" + groupsLine + ") ) ) "
							;
				} else if (rolesLine != null) {

					filterSt = "and " + "((ROL.SIGN_OBJECT IN (" + rolesLine
							+ ") and LINKSYSGROUP.UP_SYSTEMS=AIS.ID_SRV) "
							+ "   or (ROL_group.SIGN_OBJECT IN (" + rolesLine
							+ " ) and  LINKSYSGROUP.UP_SYSTEMS=AIS.ID_SRV) ) ";
				} else if (groupsLine != null) {
					filterSt = "and " + "GR.SIGN_OBJECT IN (" + groupsLine + ") ";
				}

				// группы
				// 1. пользователи

				LOGGER.debug("users_data:05+");

				lo = em.createNativeQuery(
						"select t1.t1_id, t1.t1_login, t1.t1_cert, t1.t1_usr_code, t1.t1_fio, t1.t1_tel, t1.t1_email,t1.t1_pos, t1.t1_dep_name,  "
								+ "                        t1.t1_org_code, t1.t1_org_name, t1.t1_org_adr, t1.t1_org_tel, t1.t1_start, t1.t1_end, t1.t1_status,  "
								+ "                         t1.t1_crt_date, t1.t1_crt_usr_login, t1.t1_upd_date, t1.t1_upd_usr_login,  "
								+ "                         t1.t1_dep_code, t1.t1_org_status, t1.t1_usr_status, t1.t1_dep_status, t1.t1_org_okato, t1.t1_dep_adr   "
								+ "                        from( "
								+ "                        select AU_FULL.ID_SRV t1_id, AU_FULL.login t1_login, AU_FULL.CERTIFICATE t1_cert, t2.CL_USR_CODE t1_usr_code,  "
								+ "                         decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.SURNAME||' '||AU_FULL.NAME_ ||' '|| AU_FULL.PATRONYMIC,  CL_USR_FULL.FIO ) t1_fio,    "
								+ "                          decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.PHONE, CL_USR_FULL.PHONE ) t1_tel,     "
								+ "                          decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.E_MAIL, CL_USR_FULL.EMAIL) t1_email,    "
								+ "                          decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.POSITION, CL_USR_FULL.POSITION)t1_pos,    "
								+ "                          decode(AU_FULL.UP_SIGN_USER, null, AU_FULL.DEPARTMENT, decode(substr(CL_DEP_FULL.sign_object,4,2), '00', null, CL_DEP_FULL.FULL_)) t1_dep_name,   "
								+ "                          t1.CL_ORG_CODE t1_org_code, CL_ORG_FULL.FULL_ t1_org_name,  "
								+ "                          CL_ORG_FULL.PREFIX || decode(CL_ORG_FULL.HOUSE, null, null, ','  ||CL_ORG_FULL.HOUSE  ) t1_org_adr,  "
								+ "                          CL_ORG_FULL.PHONE t1_org_tel,  "
								+ "                          to_char(AU_FULL.START_ACCOUNT, 'DD.MM.YY HH24:MI:SS') t1_start,   "
								+ "                          to_char(AU_FULL.END_ACCOUNT, 'DD.MM.YY HH24:MI:SS') t1_end,    "
								+ "                          AU_FULL.STATUS t1_status,    "
								+ "                          AU_FULL.CREATED t1_crt_date,   "
								+ "                          USR_CRT.LOGIN t1_crt_usr_login,   "
								+ "                          to_char(AU_FULL.MODIFIED, 'DD.MM.YY HH24:MI:SS') t1_upd_date,   "
								+ "                          USR_UPD.LOGIN t1_upd_usr_login,   "
								+ "                          decode(AU_FULL.UP_SIGN_USER, null, null, decode(substr(CL_DEP_FULL.sign_object,4,2), '00', null, CL_DEP_FULL.sign_object)) t1_dep_code,   "
								+ "                          CL_ORG_FULL.STATUS t1_org_status,  CL_usr_FULL.STATUS t1_usr_status,   "
								+ "                          decode(AU_FULL.UP_SIGN_USER, null, null, decode(substr(CL_DEP_FULL.sign_object,4,2), '00', null, CL_DEP_FULL.STATUS)) t1_dep_status,  "
								+ "                          CL_ORG_FULL.SIGN_OKATO t1_org_okato, CL_DEP_FULL.PREFIX || decode(CL_DEP_FULL.HOUSE, null, null, ','  ||CL_DEP_FULL.HOUSE  ) t1_dep_adr        "
								+ "                        from  "
								+ "                        (select max(CL_ORG.ID_SRV) CL_ORG_ID,  CL_ORG.SIGN_OBJECT  CL_ORG_CODE  "
								+ "                        from ISP_BSS_T cl_org,  "
								+ "                        AC_USERS_KNL_T au  "
								+ "                        where AU.UP_SIGN = CL_ORG.SIGN_OBJECT  "
								+ "                        group by CL_ORG.SIGN_OBJECT) t1,  "
								+ "                        (select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE  "
								+ "                        from ISP_BSS_T cl_usr,  "
								+ "                        AC_USERS_KNL_T au  "
								+ "                        where AU.UP_SIGN_USER  = CL_usr.SIGN_OBJECT  "
								+ "                        group by CL_usr.SIGN_OBJECT) t2,  "
								+ "                        (select max(CL_dep.ID_SRV) CL_DEP_ID,  CL_DEP.SIGN_OBJECT  CL_DEP_CODE  "
								+ "                        from ISP_BSS_T cl_dep,  "
								+ "                        AC_USERS_KNL_T au  "
								+ "                        where substr(au.UP_SIGN_USER,1,5)||'000'  =cl_dep.SIGN_OBJECT(+)  "
								+ "                        group by CL_DEP.SIGN_OBJECT) t3,  "
								+ "                        (  "
								+ "                        "
								+ "                       select t_core.t_core_id  "
								+ "                        from(   "
								+ "                        select AU_FULL.ID_SRV t_core_id   "
								+ "                        from   "
								+ "                        AC_USERS_KNL_T au_full,   "
								+ "                        LINK_GROUP_USERS_USERS_KNL_T lgu,   "
								+ "                       AC_USERS_LINK_KNL_T lur,  "
								+ "                        AC_ROLES_BSS_T rol,  "
								+ "                        AC_ROLES_BSS_T rol_group,  "
								+ "                        LINK_GROUP_USERS_ROLES_KNL_T lgr,  "
								+ "                        AC_IS_BSS_T ais,  "
								+ "                        AC_IS_BSS_T ais_group, "
								+ "                        GROUP_SYSTEMS_KNL_T sysgroup, "
								+ "                        LINK_GROUP_SYS_SYS_KNL_T linksysgroup, "
								+ "                        GROUP_USERS_KNL_T gr "
								+ "                        where  "
								+ "                        lur.UP_USERS(+)=AU_FULL.ID_SRV and  "
								+ "                        lgu.UP_USERS(+)=AU_FULL.ID_SRV  "
								+ "                        and ROL.ID_SRV(+)=LUR.UP_ROLES  "
								+
								// "                        and lgr.UP_GROUP_USERS(+)=lgu.UP_GROUP_USERS  "
								// +

								"and lgr.UP_GROUP_USERS(+)=GR.ID_SRV "
								+ "and lgu.UP_GROUP_USERS=GR.ID_SRV(+) "
								+

								"                        and rol_group.ID_SRV(+) =LGR.UP_ROLES  "
								+ "                        and ROL.UP_IS=ais.ID_SRV(+)   "
								+ "                        and ROL_group.UP_IS=ais_group.ID_SRV(+)   "
								+ "                       and SYSGROUP.ID_SRV = LINKSYSGROUP.UP_GROUP_SYSTEMS "
								+ "                       and ( "
								+ "                        (LINKSYSGROUP.UP_SYSTEMS=AIS.ID_SRV "
								+ "                         or LINKSYSGROUP.UP_SYSTEMS=AIS_GROUP.ID_SRV) "
								+ "                         and SYSGROUP.GROUP_CODE = :idIS "
								+ "                         or -1 = :onlyISUsers   "
								+ "                        ) "
								+

								(uidsLine != null ? "and to_char(au_full.ID_SRV) in ("
										+ uidsLine + ") "
										: " ")
								+

								(filterSt != null ? filterSt : " ")
								+

								"                      order by t_core_id  "
								+ "                      )t_core   "
								+ "                       group by t_core_id  "
								+ "                       order by t_core.t_core_id  "
								+ "                         "
								+ "                        ) t4, "
								+ "                         "
								+ "                        ISP_BSS_T cl_org_full,  "
								+ "                        ISP_BSS_T cl_usr_full,  "
								+ "                        ISP_BSS_T cl_dep_full,  "
								+ "                        AC_USERS_KNL_T au_full,  "
								+ "                        AC_USERS_KNL_T usr_crt,    "
								+ "                        AC_USERS_KNL_T usr_upd "
								+ "                        where cl_org_full.ID_SRV= CL_ORG_ID  "
								+ "                        and cl_usr_full.ID_SRV(+)=CL_USR_ID "
								+ "                        and cl_DEP_full.ID_SRV(+)=CL_DEP_ID  "
								+ "                        and au_full.UP_SIGN = CL_ORG_CODE  "
								+ "                        and au_full.UP_SIGN_USER  =  CL_USR_CODE(+)  "
								+ "                        and substr(au_full.UP_SIGN_USER,1,5)||'000'  =  CL_DEP_CODE(+)  "
								+ "                        and au_full.CREATOR=USR_CRT.ID_SRV   "
								+ "                        and au_full.MODIFICATOR=USR_UPD.ID_SRV(+)   "
								+ "                        and AU_FULL.ID_SRV=t4.t_core_id "
								+ "                        )t1  "
								+ "where 1=1 "	
+
(settingsMap.containsKey("FILTER_FIO") ? 
		 " and upper(t1_fio) LIKE  upper('%"+settingsMap.get("FILTER_FIO")+"%')" : "")
+
(settingsMap.containsKey("FILTER_ORG") ? 
		 " and upper(t1_org_name) LIKE  upper('%"+settingsMap.get("FILTER_ORG")+"%')" : "")

								
								+ "                        order by t1_fio   ")
						.setParameter("idIS", idIS)
						.setParameter(
								"onlyISUsers",
								CUDConstants.categorySYS.equals(category) ? 1
										: -1)
						.setFirstResult(start != null ? start.intValue() : 0)
						.setMaxResults(
								count != null ? count.intValue() : 1000000)
						.getResultList();

				usersIdsLine = CommonUtil.createAttributes(lo,  result, resultIds);
				
				LOGGER.debug("users_data:04");

				// 2. роли
			if(!"TRUE".equals(settingsMap.get("ACCOUNTS_ONLY"))){
				lo = em.createNativeQuery(
						"SELECT user_id, '[' || sys_code || ']' || role_code role_full_code "
								+ "FROM (  SELECT AU.ID_SRV user_id, SYS.SIGN_OBJECT sys_code, ROL.SIGN_OBJECT role_code "
								+ "FROM GROUP_SYSTEMS_KNL_T gsys, "
								+ "    AC_IS_BSS_T sys, "
								+ "    AC_ROLES_BSS_T rol, "
								+ "    LINK_GROUP_SYS_SYS_KNL_T lgr, "
								+ "    LINK_GROUP_USERS_ROLES_KNL_T lugr, "
								+ "    LINK_GROUP_USERS_USERS_KNL_T lugu, "
								+ "    AC_USERS_LINK_KNL_T url, "
								+ "    AC_USERS_KNL_T AU "
								+ "WHERE     GSYS.GROUP_CODE = :idIS "
								+ "    AND GSYS.ID_SRV = LGR.UP_GROUP_SYSTEMS "
								+ "    AND LGR.UP_SYSTEMS = SYS.ID_SRV "
								+ "    AND ROL.UP_IS = SYS.ID_SRV "
								+ "    AND (ROL.ID_SRV = URL.UP_ROLES OR ROL.ID_SRV = LUGR.UP_ROLES) "
								+ "    AND LUGU.UP_GROUP_USERS = LUGR.UP_GROUP_USERS(+) "
								+ "    AND LUGU.UP_USERS(+) = AU.ID_SRV "
								+ "    AND URL.UP_USERS(+) = AU.ID_SRV "
								+ "     and AU.ID_SRV IN("
								+ usersIdsLine
								+ ")  "
								+ "GROUP BY AU.ID_SRV, SYS.SIGN_OBJECT, ROL.SIGN_OBJECT) "
								+ "ORDER BY user_id, sys_code ")
						.setParameter("idIS", idIS).getResultList();

				for (Object[] objectArray : lo) {

					if (resultIds.get(objectArray[0].toString())
							.getCodesRoles() == null) {
						resultIds.get(objectArray[0].toString())
								.setCodesRoles(new ArrayList<String>());
					}

					resultIds.get(objectArray[0].toString()).getCodesRoles()
							.add(objectArray[1].toString());
				}

				// 3.группы

				lo = em.createNativeQuery(
						"select LGU.UP_USERS, GR.SIGN_OBJECT "
								+ "from  GROUP_USERS_KNL_T gr, "
								+ " LINK_GROUP_USERS_USERS_KNL_T lgu "
								+ " where LGU.UP_GROUP_USERS=GR.ID_SRV "
								+ " and  LGU.UP_USERS in (" + usersIdsLine
								+ ") "
								+ " order by LGU.UP_USERS, GR.SIGN_OBJECT ")
						.getResultList();

				for (Object[] objectArray : lo) {

					if (resultIds.get(objectArray[0].toString())
							.getCodesGroups() == null) {
						resultIds.get(objectArray[0].toString())
								.setCodesGroups(new ArrayList<String>());
					}
					
					if(objectArray[1]!=null){
					  resultIds.get(objectArray[0].toString()).getCodesGroups()
							.add(objectArray[1].toString());
					}
				}
			}
				// 4. количество

				LOGGER.debug("users_data:05");

			if(!"TRUE".equals(settingsMap.get("ACCOUNTS_ONLY"))){	
				resultCount = ((java.math.BigDecimal) em
						.createNativeQuery(
								"select count(*) from ( "
										+ "select t_core.t_core_id "
										+ "from( "
										+ "select AU_FULL.ID_SRV t_core_id "
										+ "from "
										+ "AC_USERS_KNL_T au_full, "
										+ "LINK_GROUP_USERS_USERS_KNL_T lgu, "
										+ "AC_USERS_LINK_KNL_T lur, "
										+ "AC_ROLES_BSS_T rol, "
										+ "AC_ROLES_BSS_T rol_group, "
										+ "LINK_GROUP_USERS_ROLES_KNL_T lgr, "
										+ "AC_IS_BSS_T ais, "
										+ "AC_IS_BSS_T ais_group, "
										+ "GROUP_SYSTEMS_KNL_T sysgroup, "
										+ "LINK_GROUP_SYS_SYS_KNL_T linksysgroup, "
										+ "GROUP_USERS_KNL_T gr "
										+ "where "
										+ "lur.UP_USERS(+)=AU_FULL.ID_SRV and "
										+ "lgu.UP_USERS(+)=AU_FULL.ID_SRV "
										+ "and ROL.ID_SRV(+)=LUR.UP_ROLES "
										+
								
										"and lgr.UP_GROUP_USERS(+)=GR.ID_SRV "
										+ "and lgu.UP_GROUP_USERS=GR.ID_SRV(+) "
										+

										"and rol_group.ID_SRV(+) =LGR.UP_ROLES "
										+ "and ROL.UP_IS=ais.ID_SRV(+) "
										+ "and ROL_group.UP_IS=ais_group.ID_SRV(+) "
										+ "and SYSGROUP.ID_SRV = LINKSYSGROUP.UP_GROUP_SYSTEMS "
										+ "and ( "
										+ "(LINKSYSGROUP.UP_SYSTEMS=AIS.ID_SRV "
										+ "or LINKSYSGROUP.UP_SYSTEMS=AIS_GROUP.ID_SRV) "
										+ "and SYSGROUP.GROUP_CODE = :idIS "
										+ "or -1 = :onlyISUsers   "
										+ ") "
										+

										(uidsLine != null ? "and to_char(au_full.ID_SRV) in ("
												+ uidsLine + ") "
												: " ") +

										(filterSt != null ? filterSt : " ") +

										"order by t_core_id " + ")t_core "
										+ "group by t_core_id "
										+ "order by t_core.t_core_id )")
						.setParameter("idIS", idIS)
						.setParameter(
								"onlyISUsers",
								CUDConstants.categorySYS.equals(category) ? 1
										: -1).getSingleResult()).intValue();
			 }else{
				 resultCount =  result.size();
				   
				   LOGGER.debug("users_data:count:02_1:"+resultCount);
				   LOGGER.debug("users_data:count:02_2:"+count);
				   
				   if (resultCount.intValue()==count.intValue()){
					   //записей на самом деле больше чем count
					   //поэтому нужен подсчёт через запрос
				   
				   resultCount = ((java.math.BigDecimal) em
							.createNativeQuery(
									"SELECT COUNT (*) " + 
									"  FROM (SELECT t1.t1_fio, t1.t1_org_name " + 
									"          FROM (SELECT DECODE ( " + 
									"                          AU_FULL.UP_SIGN_USER, " + 
									"                          NULL,    AU_FULL.SURNAME " + 
									"                                || ' ' " + 
									"                                || AU_FULL.NAME_ " + 
									"                                || ' ' " + 
									"                                || AU_FULL.PATRONYMIC, " + 
									"                          CL_USR_FULL.FIO) " + 
									"                          t1_fio, " + 
									"                       CL_ORG_FULL.FULL_ t1_org_name " + 
									"                  FROM (  SELECT MAX (CL_ORG.ID_SRV) CL_ORG_ID, " + 
									"                                 CL_ORG.SIGN_OBJECT CL_ORG_CODE " + 
									"                            FROM ISP_BSS_T cl_org, AC_USERS_KNL_T au " + 
									"                           WHERE AU.UP_SIGN = CL_ORG.SIGN_OBJECT " + 
									"                        GROUP BY CL_ORG.SIGN_OBJECT) t1, " + 
									"                       (  SELECT MAX (CL_usr.ID_SRV) CL_USR_ID, " + 
									"                                 CL_USR.SIGN_OBJECT CL_USR_CODE " + 
									"                            FROM ISP_BSS_T cl_usr, AC_USERS_KNL_T au " + 
									"                           WHERE AU.UP_SIGN_USER = CL_usr.SIGN_OBJECT " + 
									"                        GROUP BY CL_usr.SIGN_OBJECT) t2, " + 
									"                       (  SELECT t_core.t_core_id " + 
									"                            FROM (  SELECT AU_FULL.ID_SRV t_core_id " + 
									"                                      FROM AC_USERS_KNL_T au_full, " + 
									"                                           LINK_GROUP_USERS_USERS_KNL_T lgu, " + 
									"                                           AC_USERS_LINK_KNL_T lur, " + 
									"                                           AC_ROLES_BSS_T rol, " + 
									"                                           AC_ROLES_BSS_T rol_group, " + 
									"                                           LINK_GROUP_USERS_ROLES_KNL_T lgr, " + 
									"                                           AC_IS_BSS_T ais, " + 
									"                                           AC_IS_BSS_T ais_group, " + 
									"                                           GROUP_SYSTEMS_KNL_T sysgroup, " + 
									"                                           LINK_GROUP_SYS_SYS_KNL_T linksysgroup, " + 
									"                                           GROUP_USERS_KNL_T gr " + 
									"                                     WHERE     lur.UP_USERS(+) = AU_FULL.ID_SRV " + 
									"                                           AND lgu.UP_USERS(+) = AU_FULL.ID_SRV " + 
									"                                           AND ROL.ID_SRV(+) = LUR.UP_ROLES " + 
									"                                           AND lgr.UP_GROUP_USERS(+) = GR.ID_SRV " + 
									"                                           AND lgu.UP_GROUP_USERS = GR.ID_SRV(+) " + 
									"                                           AND rol_group.ID_SRV(+) = LGR.UP_ROLES " + 
									"                                           AND ROL.UP_IS = ais.ID_SRV(+) " + 
									"                                           AND ROL_group.UP_IS = " + 
									"                                                  ais_group.ID_SRV(+) " + 
									"                                           AND SYSGROUP.ID_SRV = " + 
									"                                                  LINKSYSGROUP.UP_GROUP_SYSTEMS " + 
									"                                   and ( " + 
									"                                     (LINKSYSGROUP.UP_SYSTEMS=AIS.ID_SRV " + 
									"                                      or LINKSYSGROUP.UP_SYSTEMS=AIS_GROUP.ID_SRV) " + 
									"                                      and SYSGROUP.GROUP_CODE = :idIS " + 
									"                                      or -1 = :onlyISUsers " + 
									"                                     )  " + 
									 
(uidsLine != null ? "and to_char(au_full.ID_SRV) in ("
		+ uidsLine + ") "
		: " ") +

(filterSt != null ? filterSt : " ") +
 
									 
									"                                  ORDER BY t_core_id) t_core " + 
									"                        GROUP BY t_core_id " + 
									"                        ORDER BY t_core.t_core_id) t4, " + 
									"                       ISP_BSS_T cl_org_full, " + 
									"                       ISP_BSS_T cl_usr_full, " + 
									"                       AC_USERS_KNL_T au_full, " + 
									"                       AC_USERS_KNL_T usr_crt, " + 
									"                       AC_USERS_KNL_T usr_upd " + 
									"                 WHERE     cl_org_full.ID_SRV = CL_ORG_ID " + 
									"                       AND cl_usr_full.ID_SRV(+) = CL_USR_ID " + 
									"                       AND au_full.UP_SIGN = CL_ORG_CODE " + 
									"                       AND au_full.UP_SIGN_USER = CL_USR_CODE(+) " + 
									"                       AND au_full.CREATOR = USR_CRT.ID_SRV " + 
									"                       AND au_full.MODIFICATOR = USR_UPD.ID_SRV(+) " + 
									"                       AND AU_FULL.ID_SRV = t4.t_core_id) t1 " + 
									"         WHERE 1 = 1  " + 
									
									(settingsMap.containsKey("FILTER_FIO") ? 
											 " and upper(t1_fio) LIKE  upper('%"+settingsMap.get("FILTER_FIO")+"%')" : "")
									+
									(settingsMap.containsKey("FILTER_ORG") ? 
											 " and upper(t1_org_name) LIKE  upper('%"+settingsMap.get("FILTER_ORG")+"%')" : "")
                                    +
									"  ) ")
							.setParameter("idIS", idIS)
							.setParameter("onlyISUsers",
									CUDConstants.categorySYS.equals(category) ? 1: -1).getSingleResult()).intValue();
				   }
			 }
				
			}

			isu.setUsers(result);
			isu.setCount(resultCount);

			sys_audit(72L, "idIS:" + idIS
					+ "result_count:" + resultCount, "true; ", IPAddress,
					idUserAuth);

		} catch (Exception e) {
			sys_audit(72L, "; idIS:" + idIS,
					"error", IPAddress, idUserAuth);

			LOGGER.error("users_data+:Error:", e);
			
			throw new GeneralFailure(e.getMessage());
			
			
			
		}
		return isu;
	
	}

	/**
	 * данные по группам
	 */
	public GroupsData groups_data(String idIS, List<String> groupsCodes,
			String category, List<String> rolesCodes, Integer start,
			Integer count, Map<String, String> settings, Long idUserAuth,
			String IPAddress) throws GeneralFailure {

		LOGGER.debug("groups_data:01");

		// onlyISUsers условие сильнее, чем rolesCodes
		// то есть, если стоит onlyISUsers = false [все пользователи],
		// то rolesCodes уже не учитывается

		// category:
		// ALL
		// SYS

		// + USER

		Integer resultCount = 0;
		List<Group> result = new ArrayList<Group>();
		Map<String, Group> resultIds = new HashMap<String, Group>();
		Group group = null;

		List<Object[]> loGr = null;
		String idRec = null;

		GroupsData isu = new GroupsData();

		String rolesLine = null;
		String usersIdsLine = null;
	
		try {

			if (idIS == null) {
				LOGGER.debug("groups_data:return:1");
				throw new GeneralFailure("idIS is null!");
			}

			if (count == null) {
				count = MAX_CONT_GROUPS;
			}
			if (start == null) {
				start = 0;
			}

			if (MAX_CONT_GROUPS < count) {
				LOGGER.debug("groups_data:return:2");
				throw new GeneralFailure("'count' should be less than "
						+ MAX_CONT_GROUPS);
			}

			LOGGER.debug("groups_data:idIS1:" + idIS);

			String uidsLine = null;
			
			uidsLine = CommonUtil.createLine(groupsCodes);
			


			if (CUDConstants.categorySYS.equals(category) && rolesCodes != null
					&& !rolesCodes.isEmpty()) {

				rolesLine = CommonUtil.createLine(rolesCodes);
				
			

			}

			if (idIS.startsWith(CUDConstants.armPrefix)
					|| idIS.startsWith(CUDConstants.subArmPrefix)) {

				// !!!
				idIS = getCodeIs(idIS);

				LOGGER.debug("groups_data:idIS2:" + idIS);

				// системы и подсистемы
				// 1.группы

				if (!CUDConstants.categoryUSER.equals(category)) {
					// от системы группы или весь список групп

					loGr = em.createNativeQuery(

							"select gr_full.ID_SRV, gr_full.SIGN_OBJECT, gr_full.FULL_, gr_full.DESCRIPTION  "
									+ "from GROUP_USERS_KNL_T gr_full, "
									+ "( "
									+ "select GR.ID_SRV gr_id "
									+ "from GROUP_USERS_KNL_T gr, "
									+ "LINK_GROUP_USERS_ROLES_KNL_T lgr, "
									+ "AC_ROLES_BSS_T rol, "
									+ "AC_IS_BSS_T sys "
									+ "where GR.ID_SRV = LGR.UP_GROUP_USERS(+) "
									+ "and ROL.ID_SRV(+) = LGR.UP_ROLES "
									+ "and SYS.ID_SRV(+)=ROL.UP_IS "
									+ " "
									+ "and( SYS.SIGN_OBJECT = :idIS or -1 = :onlyISUsers ) "
									+

									(uidsLine != null ? "and GR.SIGN_OBJECT in ("
											+ uidsLine + ") "
											: " ")
									+

									(rolesLine != null ? "and (ROL.SIGN_OBJECT in ("
											+ rolesLine
											+ ") and SYS.SIGN_OBJECT = :idIS ) "
											: " ")
									+

									"group by GR.ID_SRV "
									+ ") "
									+ "where gr_id=gr_full.ID_SRV "
									+ "order by gr_full.FULL_ ")

							.setParameter("idIS", idIS)
							.setParameter(
									"onlyISUsers",
									CUDConstants.categorySYS.equals(category) ? 1
											: -1)
							.setFirstResult(
									start != null ? start.intValue() : 0)
							.setMaxResults(
									count != null ? count.intValue() : 1000000)
							.getResultList();

				} else {
					// группы текущего пользователя

					LOGGER.debug("groups_data:02_01");
					
					loGr = em.createNativeQuery(
							"select gr_full.ID_SRV, gr_full.SIGN_OBJECT, gr_full.FULL_, gr_full.DESCRIPTION  "
									+ "from GROUP_USERS_KNL_T gr_full, "
									+ "( "
									+ "select GR.ID_SRV gr_id "
									+ "from GROUP_USERS_KNL_T gr, "
									+ "LINK_GROUP_USERS_ROLES_KNL_T lgr, "
									+ "AC_ROLES_BSS_T rol, "
									+ "AC_IS_BSS_T sys, "
									+ "LINK_GROUP_USERS_USERS_KNL_T guu "
									+ "where GR.ID_SRV = LGR.UP_GROUP_USERS(+) "
									+ "and ROL.ID_SRV(+) = LGR.UP_ROLES "
									+ "and SYS.ID_SRV(+)=ROL.UP_IS "
									+ " "
									+ "and( SYS.SIGN_OBJECT = :idIS or -1 = :onlyISUsers ) "
									+ "and guu.UP_GROUP_USERS = GR.ID_SRV "
									+ "and guu.UP_USERS = :idUser "
									+

									(uidsLine != null ? "and GR.SIGN_OBJECT in ("
											+ uidsLine + ") "
											: " ")
									+

									"group by GR.ID_SRV "
									+ ") "
									+ "where gr_id=gr_full.ID_SRV "
									+ "order by gr_full.FULL_ ")

							.setParameter("idIS", idIS)
							.setParameter("onlyISUsers", -1)
							.setParameter("idUser", idUserAuth)
							.setFirstResult(
									start != null ? start.intValue() : 0)
							.setMaxResults(
									count != null ? count.intValue() : 1000000)
							.getResultList();

				}

				
				for (Object[] objectArray : loGr) {

					idRec = objectArray[0].toString();

					group = new Group();

					group.setName (objectArray[2] != null ? objectArray[2]
							.toString() : "");
					
					group.setCode (objectArray[1] != null ? objectArray[1]
							.toString() : "");
					group.setDescription (objectArray[3] != null ? objectArray[3]
							.toString() : "");

					result.add(group); // для сохранения сортировки из запроса
					resultIds.put(idRec, group);

					if (usersIdsLine == null) {
						usersIdsLine = "'" + idRec + "'";
					} else {
						usersIdsLine = usersIdsLine + ", '" + idRec + "'";
					}
				}

				LOGGER.debug("groups_data:02");

				// 2.роли

				loGr = em.createNativeQuery(
						"  SELECT gr.ID_SRV, ROL.SIGN_OBJECT "
								+ "    FROM AC_IS_BSS_T sys, "
								+ "         AC_ROLES_BSS_T rol, "
								+ "         GROUP_USERS_KNL_T gr, "
								+ "         AC_SUBSYSTEM_CERT_BSS_T subsys, "
								+ "         LINK_GROUP_USERS_ROLES_KNL_T lugr "
								+ "   WHERE (SYS.SIGN_OBJECT = :idIS "
								+ "          OR SUBSYS.SUBSYSTEM_CODE = :idIS ) "
								+ "         AND ROL.ID_SRV = LUGR.UP_ROLES "
								+ "         AND LUGR.UP_GROUP_USERS = gr.ID_SRV "
								+ "         AND ROL.UP_IS = sys.ID_SRV "
								+ "         AND gr.ID_SRV IN (" + usersIdsLine
								+ ") "
								+ "         AND SUBSYS.UP_IS(+) = SYS.ID_SRV "
								+ "GROUP BY gr.ID_SRV, ROL.SIGN_OBJECT ")
						.setParameter("idIS", idIS).getResultList();

				for (Object[] objectArray : loGr) {

					if (resultIds.get(objectArray[0].toString())
							.getCodesRoles() == null) {
						resultIds.get(objectArray[0].toString())
								.setCodesRoles(new ArrayList<String>());
					}

					resultIds.get(objectArray[0].toString()).getCodesRoles()
							.add(objectArray[1].toString());
				}

				// 3.количество

				LOGGER.debug("groups_data:03");

				if (!CUDConstants.categoryUSER.equals(category)) {
					// от системы группы или весь список групп

					resultCount = ((java.math.BigDecimal) em
							.createNativeQuery(
									"select count(*) from  GROUP_USERS_KNL_T gr_full, "
											+ "( "
											+ "select GR.ID_SRV gr_id "
											+ "from GROUP_USERS_KNL_T gr, "
											+ "LINK_GROUP_USERS_ROLES_KNL_T lgr, "
											+ "AC_ROLES_BSS_T rol, "
											+ "AC_IS_BSS_T sys "
											+ "where GR.ID_SRV = LGR.UP_GROUP_USERS(+) "
											+ "and ROL.ID_SRV(+) = LGR.UP_ROLES "
											+ "and SYS.ID_SRV(+)=ROL.UP_IS "
											+ " "
											+ "and( SYS.SIGN_OBJECT = :idIS or -1 = :onlyISUsers ) "
											+

											(uidsLine != null ? "and GR.SIGN_OBJECT in ("
													+ uidsLine + ") "
													: " ")
											+

											(rolesLine != null ? "and ( ROL.SIGN_OBJECT in ("
													+ rolesLine
													+ ") and SYS.SIGN_OBJECT = :idIS ) "
													: " ") +

											"group by GR.ID_SRV " + ") "
											+ "where gr_id=gr_full.ID_SRV "
											+ "order by gr_full.FULL_ ")
							.setParameter("idIS", idIS)
							.setParameter(
									"onlyISUsers",
									CUDConstants.categorySYS.equals(category) ? 1
											: -1).getSingleResult())
							.intValue();

				} else {
					// группы текущего пользователя

					resultCount = ((java.math.BigDecimal) em
							.createNativeQuery(
									"select count(*)  "
											+ "from GROUP_USERS_KNL_T gr_full, "
											+ "( "
											+ "select GR.ID_SRV gr_id "
											+ "from GROUP_USERS_KNL_T gr, "
											+ "LINK_GROUP_USERS_ROLES_KNL_T lgr, "
											+ "AC_ROLES_BSS_T rol, "
											+ "AC_IS_BSS_T sys, "
											+ "LINK_GROUP_USERS_USERS_KNL_T guu "
											+ "where GR.ID_SRV = LGR.UP_GROUP_USERS(+) "
											+ "and ROL.ID_SRV(+) = LGR.UP_ROLES "
											+ "and SYS.ID_SRV(+)=ROL.UP_IS "
											+ " "
											+ "and( SYS.SIGN_OBJECT = :idIS or -1 = :onlyISUsers ) "
											+ "and guu.UP_GROUP_USERS = GR.ID_SRV "
											+ "and guu.UP_USERS = :idUser "
											+

											(uidsLine != null ? "and GR.SIGN_OBJECT in ("
													+ uidsLine + ") "
													: " ") +

											"group by GR.ID_SRV " + ") "
											+ "where gr_id=gr_full.ID_SRV "
											+ "order by gr_full.FULL_ ")

							.setParameter("idIS", idIS)
							.setParameter("onlyISUsers", -1)
							.setParameter("idUser", idUserAuth)
							.getSingleResult()).intValue();

				}

			} else if (idIS.startsWith(CUDConstants.groupArmPrefix)) {

				// группы
				// 1. пользователи

				if (!CUDConstants.categoryUSER.equals(category)) {
					// от системы группы или весь список групп

					loGr = em.createNativeQuery(

							"select gr_full.ID_SRV, gr_full.SIGN_OBJECT, gr_full.FULL_, gr_full.DESCRIPTION  "
									+ "from GROUP_USERS_KNL_T gr_full, "
									+ "( "
									+ "select GR.ID_SRV gr_id "
									+ "from GROUP_USERS_KNL_T gr, "
									+ "LINK_GROUP_USERS_ROLES_KNL_T lgr, "
									+ "AC_ROLES_BSS_T rol, "
									+ "AC_IS_BSS_T sys, "
									+ "GROUP_SYSTEMS_KNL_T sysgroup,  LINK_GROUP_SYS_SYS_KNL_T linksysgroup "
									+ "where GR.ID_SRV = LGR.UP_GROUP_USERS(+) "
									+ "and ROL.ID_SRV(+) = LGR.UP_ROLES "
									+ "and SYS.ID_SRV(+)=ROL.UP_IS "
									+

									"and SYSGROUP.ID_SRV = LINKSYSGROUP.UP_GROUP_SYSTEMS "
									+ "and ( "
									+ "LINKSYSGROUP.UP_SYSTEMS=SYS.ID_SRV  "
									+ "and SYSGROUP.GROUP_CODE = :idIS "
									+ "or -1 = :onlyISUsers  "
									+ " ) "
									+

									(uidsLine != null ? "and GR.SIGN_OBJECT in ("
											+ uidsLine + ") "
											: " ")
									+

									(rolesLine != null ? "and ( ROL.SIGN_OBJECT in ("
											+ rolesLine
											+ ") and LINKSYSGROUP.UP_SYSTEMS=SYS.ID_SRV ) "
											: " ")
									+

									"group by GR.ID_SRV "
									+ ") "
									+ "where gr_id=gr_full.ID_SRV "
									+ "order by gr_full.FULL_ ")

							.setParameter("idIS", idIS)
							.setParameter(
									"onlyISUsers",
									CUDConstants.categorySYS.equals(category) ? 1
											: -1)
							.setFirstResult(
									start != null ? start.intValue() : 0)
							.setMaxResults(
									count != null ? count.intValue() : 1000000)
							.getResultList();

				} else {
					// группы текущего пользователя

					loGr = em.createNativeQuery(

							"select gr_full.ID_SRV, gr_full.SIGN_OBJECT, gr_full.FULL_, gr_full.DESCRIPTION  "
									+ "from GROUP_USERS_KNL_T gr_full, "
									+ "( "
									+ "select GR.ID_SRV gr_id "
									+ "from GROUP_USERS_KNL_T gr, "
									+ "LINK_GROUP_USERS_ROLES_KNL_T lgr, "
									+ "AC_ROLES_BSS_T rol, "
									+ "AC_IS_BSS_T sys, "
									+ "LINK_GROUP_USERS_USERS_KNL_T guu, "
									+ "GROUP_SYSTEMS_KNL_T sysgroup,  LINK_GROUP_SYS_SYS_KNL_T linksysgroup "
									+ "where GR.ID_SRV = LGR.UP_GROUP_USERS(+) "
									+ "and ROL.ID_SRV(+) = LGR.UP_ROLES "
									+ "and SYS.ID_SRV(+)=ROL.UP_IS "
									+

									"and guu.UP_GROUP_USERS = GR.ID_SRV "
									+ "and guu.UP_USERS = :idUser "
									+

									"and SYSGROUP.ID_SRV = LINKSYSGROUP.UP_GROUP_SYSTEMS "
									+ "and ( "
									+ "LINKSYSGROUP.UP_SYSTEMS=SYS.ID_SRV  "
									+ "and SYSGROUP.GROUP_CODE = :idIS "
									+ "or -1 = :onlyISUsers  "
									+ " ) "
									+

									(uidsLine != null ? "and GR.SIGN_OBJECT in ("
											+ uidsLine + ") "
											: " ")
									+

									"group by GR.ID_SRV "
									+ ") "
									+ "where gr_id=gr_full.ID_SRV "
									+ "order by gr_full.FULL_ ")

							.setParameter("idIS", idIS)
							.setParameter("onlyISUsers", -1)
							.setParameter("idUser", idUserAuth)
							.setFirstResult(
									start != null ? start.intValue() : 0)
							.setMaxResults(
									count != null ? count.intValue() : 1000000)
							.getResultList();

				}

				for (Object[] objectArray : loGr) {

					idRec = objectArray[0].toString();

					group = new Group();

					group.setCode (objectArray[1] != null ? objectArray[1]
							.toString() : "");
					group.setName (objectArray[2] != null ? objectArray[2]
							.toString() : "");
					group.setDescription (objectArray[3] != null ? objectArray[3]
							.toString() : "");

					result.add(group); // для сохранения сортировки из запроса
					resultIds.put(idRec, group);

					if (usersIdsLine == null) {
						usersIdsLine = "'" + idRec + "'";
					} else {
						usersIdsLine = usersIdsLine + ", '" + idRec + "'";
					}
				}

				LOGGER.debug("groups_data:04");

				// 2. роли

				loGr = em.createNativeQuery(
						        SELECT_ROLE_START
								+ usersIdsLine
								+SELECT_ROLE_END
								)
						.setParameter("idIS", idIS).getResultList();

				for (Object[] objectArray : loGr) {

					if (resultIds.get(objectArray[0].toString())
							.getCodesRoles() == null) {
						resultIds.get(objectArray[0].toString())
								.setCodesRoles(new ArrayList<String>());
					}

					resultIds.get(objectArray[0].toString()).getCodesRoles()
							.add(objectArray[1].toString());
				}

				// 3. количество

				LOGGER.debug("groups_data:05");

				if (!CUDConstants.categoryUSER.equals(category)) {
					// от системы группы или весь список групп

					resultCount = ((java.math.BigDecimal) em
							.createNativeQuery(
									"select count(*) "
											+ "from GROUP_USERS_KNL_T gr_full, "
											+ "( "
											+ "select GR.ID_SRV gr_id "
											+ "from GROUP_USERS_KNL_T gr, "
											+ "LINK_GROUP_USERS_ROLES_KNL_T lgr, "
											+ "AC_ROLES_BSS_T rol, "
											+ "AC_IS_BSS_T sys, "
											+ "GROUP_SYSTEMS_KNL_T sysgroup,  LINK_GROUP_SYS_SYS_KNL_T linksysgroup "
											+ "where GR.ID_SRV = LGR.UP_GROUP_USERS(+) "
											+ "and ROL.ID_SRV(+) = LGR.UP_ROLES "
											+ "and SYS.ID_SRV(+)=ROL.UP_IS "
											+

											"and SYSGROUP.ID_SRV = LINKSYSGROUP.UP_GROUP_SYSTEMS "
											+ "and ( "
											+ "LINKSYSGROUP.UP_SYSTEMS=SYS.ID_SRV  "
											+ "and SYSGROUP.GROUP_CODE = :idIS "
											+ "or -1 = :onlyISUsers  "
											+ " ) "
											+

											(uidsLine != null ? "and GR.SIGN_OBJECT in ("
													+ uidsLine + ") "
													: " ")
											+

											(rolesLine != null ? "and ( ROL.SIGN_OBJECT in ("
													+ rolesLine
													+ ") and LINKSYSGROUP.UP_SYSTEMS=SYS.ID_SRV ) "
													: " ") +

											"group by GR.ID_SRV " + ") "
											+ "where gr_id=gr_full.ID_SRV "
											+ "order by gr_full.FULL_ ")
							.setParameter("idIS", idIS)
							.setParameter(
									"onlyISUsers",
									CUDConstants.categorySYS.equals(category) ? 1
											: -1).getSingleResult())
							.intValue();

				} else {
					// группы текущего пользователя

					resultCount = ((java.math.BigDecimal) em
							.createNativeQuery(

									"select count(*)  "
											+ "from GROUP_USERS_KNL_T gr_full, "
											+ "( "
											+ "select GR.ID_SRV gr_id "
											+ "from GROUP_USERS_KNL_T gr, "
											+ "LINK_GROUP_USERS_ROLES_KNL_T lgr, "
											+ "AC_ROLES_BSS_T rol, "
											+ "AC_IS_BSS_T sys, "
											+ "LINK_GROUP_USERS_USERS_KNL_T guu, "
											+ "GROUP_SYSTEMS_KNL_T sysgroup,  LINK_GROUP_SYS_SYS_KNL_T linksysgroup "
											+ "where GR.ID_SRV = LGR.UP_GROUP_USERS(+) "
											+ "and ROL.ID_SRV(+) = LGR.UP_ROLES "
											+ "and SYS.ID_SRV(+)=ROL.UP_IS "
											+

											"and guu.UP_GROUP_USERS = GR.ID_SRV "
											+ "and guu.UP_USERS = :idUser "
											+

											"and SYSGROUP.ID_SRV = LINKSYSGROUP.UP_GROUP_SYSTEMS "
											+ "and ( "
											+ "LINKSYSGROUP.UP_SYSTEMS=SYS.ID_SRV  "
											+ "and SYSGROUP.GROUP_CODE = :idIS "
											+ "or -1 = :onlyISUsers  "
											+ " ) "
											+

											(uidsLine != null ? "and GR.SIGN_OBJECT in ("
													+ uidsLine + ") "
													: " ") +

											"group by GR.ID_SRV " + ") "
											+ "where gr_id=gr_full.ID_SRV "
											+ "order by gr_full.FULL_ ")

							.setParameter("idIS", idIS)
							.setParameter("onlyISUsers", -1)
							.setParameter("idUser", idUserAuth)
							.getSingleResult()).intValue();

				}
			}

			isu.setGroups(result);
			isu.setCount(resultCount);

			sys_audit(72L,"idIS:" + idIS
					+ "result_count:" + resultCount, "true; ", IPAddress,
					idUserAuth);

		} catch (Exception e) {
			sys_audit(72L, "; idIS:" + idIS,
					"error", IPAddress, idUserAuth);

			LOGGER.error("groups_data:Error:", e);
			throw new GeneralFailure(e.getMessage());
		}
		return isu;
	}

	/**
	 * данные по ресурсам
	 */
	public ResourcesData resources_data(String idIS,
			List<String> resourcesCodes, String category,
			List<String> rolesCodes, Integer start, Integer count,
			Map<String, String> settings, Long idUserAuth, String IPAddress)
			throws GeneralFailure {

		LOGGER.debug("resources_data:01");

		// onlyISUsers[category:SYS] условие сильнее, чем rolesCodes
		// то есть, если стоит onlyISUsers = false [все пользователи],
		// то rolesCodes уже не учитывается

		// category:
		// USER (используем idUserAuth)
		// SYS

		Integer resultCount = 0;
		List<ResourceNU> result = new ArrayList<ResourceNU>();
		Map<String, ResourceNU> resultIds = new HashMap<String, ResourceNU>();
		ResourceNU uat = null;

		List<Object[]> lo = null;
		String idRec = null;

		ResourcesData isu = new ResourcesData();

		String rolesLine = null;
		String usersIdsLine = null;
	
		try {

			if (idIS == null) {
				LOGGER.debug("resources_data:return:1");
				throw new GeneralFailure("idIS is null!");
			}

			if (count == null) {
				count = MAX_CONT_RES;
			}
			if (start == null) {
				start = 0;
			}

			if (MAX_CONT_RES < count) {
				LOGGER.debug("resources_data:return:2");
				throw new GeneralFailure("'count' should be less than "
						+ MAX_CONT_RES);
			}

			LOGGER.debug("resources_data:idIS1:" + idIS);

			String uidsLine = null;

			uidsLine = CommonUtil.createLine(resourcesCodes);
			
	

			if (CUDConstants.categorySYS.equals(category) && rolesCodes != null
					&& !rolesCodes.isEmpty()) {

				rolesLine = CommonUtil.createLine(rolesCodes);
			
			}

			if (idIS.startsWith(CUDConstants.armPrefix)
					|| idIS.startsWith(CUDConstants.subArmPrefix)) {

				// !!!
				idIS = getCodeIs(idIS);

				LOGGER.debug("resources_data:idIS2:" + idIS);

				// системы и подсистемы
				// 1.группы

				lo = em.createNativeQuery(
						" SELECT res_full.ID_SRV, "
								+ "         res_full.SIGN_OBJECT, "
								+ "         res_full.FULL_, "
								+ "         res_full.DESCRIPTION "
								+ "    FROM AC_RESOURCES_BSS_T res_full, "
								+ "         (  SELECT res.ID_SRV res_id "
								+ "              FROM AC_RESOURCES_BSS_T res, "
								+ "                   AC_LINK_ROLE_RESOURCE_KNL_T lgr, "
								+ "                   AC_ROLES_BSS_T rol, "
								+ "                   AC_IS_BSS_T sys "
								+ "             WHERE     res.ID_SRV = LGR.UP_RESOURCE(+) "
								+ "                   AND ROL.ID_SRV(+) = LGR.UP_ROLE "
								+ "                   AND SYS.ID_SRV(+) = ROL.UP_IS "
								+ "                   AND res.up_is = SYS.ID_SRV "
								+ "                   AND (SYS.SIGN_OBJECT = :idIS OR -1 = :onlyISUsers) "
								+ (uidsLine != null ? "   and res.SIGN_OBJECT in ("
										+ uidsLine + ") "
										: "")
								+

								(rolesLine != null ? "    and (ROL.SIGN_OBJECT in ("
										+ rolesLine
										+ ") and SYS.SIGN_OBJECT = :idIS ) "
										: "") +

								"          GROUP BY res.ID_SRV) "
								+ "   WHERE res_id = res_full.ID_SRV "
								+ "ORDER BY res_full.FULL_")
						.setParameter("idIS", idIS)
						.setParameter(
								"onlyISUsers",
								CUDConstants.categorySYS.equals(category) ? 1
										: -1)
						.setFirstResult(start != null ? start.intValue() : 0)
						.setMaxResults(
								count != null ? count.intValue() : 1000000)
						.getResultList();

				for (Object[] objectArray : lo) {

					idRec = objectArray[0].toString();

					uat = new ResourceNU();

					uat.setDescription (objectArray[3] != null ? objectArray[3]
							.toString() : "");
					uat.setCode (objectArray[1] != null ? objectArray[1]
							.toString() : "");
					uat.setName (objectArray[2] != null ? objectArray[2]
							.toString() : "");
					
					result.add(uat); // для сохранения сортировки из запроса
					resultIds.put(idRec, uat);

					if (usersIdsLine == null) {
						usersIdsLine = "'" + idRec + "'";
					} else {
						usersIdsLine = usersIdsLine + ", '" + idRec + "'";
					}
				}

				LOGGER.debug("resources_data:02");

				// 2.роли

				lo = em.createNativeQuery(
						"  SELECT gr.ID_SRV, ROL.SIGN_OBJECT "
								+ "    FROM AC_IS_BSS_T sys, "
								+ "         AC_ROLES_BSS_T rol, "
								+ "         GROUP_USERS_KNL_T gr, "
								+ "         AC_SUBSYSTEM_CERT_BSS_T subsys, "
								+ "         LINK_GROUP_USERS_ROLES_KNL_T lugr "
								+ "   WHERE (SYS.SIGN_OBJECT = :idIS "
								+ "          OR SUBSYS.SUBSYSTEM_CODE = :idIS ) "
								+ "         AND ROL.ID_SRV = LUGR.UP_ROLES "
								+ "         AND LUGR.UP_GROUP_USERS = gr.ID_SRV "
								+ "         AND ROL.UP_IS = sys.ID_SRV "
								+ "         AND gr.ID_SRV IN (" + usersIdsLine
								+ ") "
								+ "         AND SUBSYS.UP_IS(+) = SYS.ID_SRV "
								+ "GROUP BY gr.ID_SRV, ROL.SIGN_OBJECT ")
						.setParameter("idIS", idIS).getResultList();

				for (Object[] objectArray : lo) {

					if (resultIds.get(objectArray[0].toString())
							.getCodesRoles() == null) {
						resultIds.get(objectArray[0].toString())
								.setCodesRoles(new ArrayList<String>());
					}

					resultIds.get(objectArray[0].toString()).getCodesRoles()
							.add(objectArray[1].toString());
				}

				// 3.количество

				LOGGER.debug("resources_data:03");

				resultCount = ((java.math.BigDecimal) em
						.createNativeQuery(
								"select count(*) from  GROUP_USERS_KNL_T gr_full, "
										+ "( "
										+ "select GR.ID_SRV gr_id "
										+ "from GROUP_USERS_KNL_T gr, "
										+ "LINK_GROUP_USERS_ROLES_KNL_T lgr, "
										+ "AC_ROLES_BSS_T rol, "
										+ "AC_IS_BSS_T sys "
										+ "where GR.ID_SRV = LGR.UP_GROUP_USERS(+) "
										+ "and ROL.ID_SRV(+) = LGR.UP_ROLES "
										+ "and SYS.ID_SRV(+)=ROL.UP_IS "
										+ " "
										+ "and( SYS.SIGN_OBJECT = :idIS or -1 = :onlyISUsers ) "
										+

										(uidsLine != null ? "and GR.SIGN_OBJECT in ("
												+ uidsLine + ") "
												: " ")
										+

										(rolesLine != null ? "and ( ROL.SIGN_OBJECT in ("
												+ rolesLine
												+ ") and SYS.SIGN_OBJECT = :idIS ) "
												: " ") +

										"group by GR.ID_SRV " + ") "
										+ "where gr_id=gr_full.ID_SRV "
										+ "order by gr_full.FULL_ ")
						.setParameter("idIS", idIS)
						.setParameter(
								"onlyISUsers",
								CUDConstants.categorySYS.equals(category) ? 1
										: -1).getSingleResult()).intValue();

			} else if (idIS.startsWith(CUDConstants.groupArmPrefix)) {

				// группы
				// 1. пользователи

				lo = em.createNativeQuery(

						"select gr_full.ID_SRV, gr_full.SIGN_OBJECT, gr_full.FULL_, gr_full.DESCRIPTION  "
								+ "from GROUP_USERS_KNL_T gr_full, "
								+ "( "
								+ "select GR.ID_SRV gr_id "
								+ "from GROUP_USERS_KNL_T gr, "
								+ "LINK_GROUP_USERS_ROLES_KNL_T lgr, "
								+ "AC_ROLES_BSS_T rol, "
								+ "AC_IS_BSS_T sys, "
								+ "GROUP_SYSTEMS_KNL_T sysgroup,  LINK_GROUP_SYS_SYS_KNL_T linksysgroup "
								+ "where GR.ID_SRV = LGR.UP_GROUP_USERS(+) "
								+ "and ROL.ID_SRV(+) = LGR.UP_ROLES "
								+ "and SYS.ID_SRV(+)=ROL.UP_IS "
								+

								"and SYSGROUP.ID_SRV = LINKSYSGROUP.UP_GROUP_SYSTEMS "
								+ "and ( "
								+ "LINKSYSGROUP.UP_SYSTEMS=SYS.ID_SRV  "
								+ "and SYSGROUP.GROUP_CODE = :idIS "
								+ "or -1 = :onlyISUsers  "
								+ " ) "
								+

								(uidsLine != null ? "and GR.SIGN_OBJECT in ("
										+ uidsLine + ") " : " ")
								+

								(rolesLine != null ? "and ( ROL.SIGN_OBJECT in ("
										+ rolesLine
										+ ") and LINKSYSGROUP.UP_SYSTEMS=SYS.ID_SRV ) "
										: " ")
								+

								"group by GR.ID_SRV "
								+ ") "
								+ "where gr_id=gr_full.ID_SRV "
								+ "order by gr_full.FULL_ ")

						.setParameter("idIS", idIS)
						.setParameter(
								"onlyISUsers",
								CUDConstants.categorySYS.equals(category) ? 1
										: -1)
						.setFirstResult(start != null ? start.intValue() : 0)
						.setMaxResults(
								count != null ? count.intValue() : 1000000)
						.getResultList();

				for (Object[] objectArray : lo) {

					idRec = objectArray[0].toString();

					uat = new ResourceNU();

					uat.setDescription (objectArray[3] != null ? objectArray[3]
							.toString() : "");
					
					uat.setCode (objectArray[1] != null ? objectArray[1]
							.toString() : "");
					
					uat.setName (objectArray[2] != null ? objectArray[2]
							.toString() : "");

					result.add(uat); // для сохранения сортировки из запроса
					resultIds.put(idRec, uat);

					if (usersIdsLine == null) {
						usersIdsLine = "'" + idRec + "'";
					} else {
						usersIdsLine = usersIdsLine + ", '" + idRec + "'";
					}
				}

				LOGGER.debug("resources_data:04");

				// 2. роли

				lo = em.createNativeQuery(
						 SELECT_ROLE_START
							+ usersIdsLine
							+SELECT_ROLE_END)
						.setParameter("idIS", idIS).getResultList();

				for (Object[] objectArray : lo) {

					if (resultIds.get(objectArray[0].toString())
							.getCodesRoles() == null) {
						resultIds.get(objectArray[0].toString())
								.setCodesRoles(new ArrayList<String>());
					}

					resultIds.get(objectArray[0].toString()).getCodesRoles()
							.add(objectArray[1].toString());
				}

				// 3. количество

				LOGGER.debug("resources_data:05");

				resultCount = ((java.math.BigDecimal) em
						.createNativeQuery(
								"select count(*) "
										+ "from GROUP_USERS_KNL_T gr_full, "
										+ "( "
										+ "select GR.ID_SRV gr_id "
										+ "from GROUP_USERS_KNL_T gr, "
										+ "LINK_GROUP_USERS_ROLES_KNL_T lgr, "
										+ "AC_ROLES_BSS_T rol, "
										+ "AC_IS_BSS_T sys, "
										+ "GROUP_SYSTEMS_KNL_T sysgroup,  LINK_GROUP_SYS_SYS_KNL_T linksysgroup "
										+ "where GR.ID_SRV = LGR.UP_GROUP_USERS(+) "
										+ "and ROL.ID_SRV(+) = LGR.UP_ROLES "
										+ "and SYS.ID_SRV(+)=ROL.UP_IS "
										+

										"and SYSGROUP.ID_SRV = LINKSYSGROUP.UP_GROUP_SYSTEMS "
										+ "and ( "
										+ "LINKSYSGROUP.UP_SYSTEMS=SYS.ID_SRV  "
										+ "and SYSGROUP.GROUP_CODE = :idIS "
										+ "or -1 = :onlyISUsers  "
										+ " ) "
										+

										(uidsLine != null ? "and GR.SIGN_OBJECT in ("
												+ uidsLine + ") "
												: " ")
										+

										(rolesLine != null ? "and ( ROL.SIGN_OBJECT in ("
												+ rolesLine
												+ ") and LINKSYSGROUP.UP_SYSTEMS=SYS.ID_SRV ) "
												: " ") +

										"group by GR.ID_SRV " + ") "
										+ "where gr_id=gr_full.ID_SRV "
										+ "order by gr_full.FULL_ ")
						.setParameter("idIS", idIS)
						.setParameter(
								"onlyISUsers",
								CUDConstants.categorySYS.equals(category) ? 1
										: -1).getSingleResult()).intValue();

			}

			isu.setResources(result);
			isu.setCount(resultCount);

			sys_audit(72L, "idIS:" + idIS + "result_count:" + resultCount,
					"true; ", IPAddress, idUserAuth);

		} catch (Exception e) {
			sys_audit(72L, "; idIS:" + idIS, "error", IPAddress, idUserAuth);

			LOGGER.error("resources_data:Error:", e);
			throw new GeneralFailure(e.getMessage());
		}
		return isu;

	}

	/**
	 * данные по подсистемам
	 */
	public List<Resource> resources_data_subsys(String idIS,
	
	String category, Long idUserAuth, String IPAddress) throws GeneralFailure {

		LOGGER.debug("resources_data:01");

		// onlyISUsers[category:SYS] условие сильнее, чем rolesCodes
		// то есть, если стоит onlyISUsers = false [все пользователи],
		// то rolesCodes уже не учитывается

		// category:
		// USER (используем idUserAuth)
		// SYS

		Integer resultCount = 0;
		List<Resource> result = new ArrayList<Resource>();
		Map<String, Resource> resultIds = new HashMap<String, Resource>();
		Resource uat = null;

		List<Object[]> lo = null;
		String idRec = null;

		try {

			if (idIS == null) {
				LOGGER.debug("resources_data:return:1");
				throw new GeneralFailure("idIS is null!");
			}

			LOGGER.debug("resources_data:idIS1:" + idIS);

			if (idIS.startsWith(CUDConstants.armPrefix)
					|| idIS.startsWith(CUDConstants.subArmPrefix)) {

				// !!!
				idIS = getCodeIs(idIS);

				LOGGER.debug("resources_data:idIS2:" + idIS);

				// системы и подсистемы

				// информация по системе
				if (CUDConstants.categorySYS.equals(category)) {

					lo = em.createNativeQuery(
							"select arm.ID_SRV, ARM.SIGN_OBJECT, ARM.FULL_,  ARM.DESCRIPTION , arm.LINKS from AC_IS_BSS_T arm "
									+ "where ARM.SIGN_OBJECT= :idIS ")
							.setParameter("idIS", idIS).getResultList();

				} else {
					// доступная система для пользователя
					lo = em.createNativeQuery(
							"select sys_full.ID_SRV, sys_full.SIGN_OBJECT, sys_full.FULL_, SYS_FULL.DESCRIPTION, sys_full.LINKS from AC_IS_BSS_T sys_full,"
									+ " (select   SYS.ID_SRV sys_id"
									+ "                     from  AC_IS_BSS_T sys,     "
									+ "                              AC_ROLES_BSS_T rol,     "
									+ "                              AC_USERS_LINK_KNL_T url,     "
									+ "                              AC_USERS_KNL_T AU,     "
									+ "                              LINK_GROUP_USERS_ROLES_KNL_T lugr,   "
									+ "                              LINK_GROUP_USERS_USERS_KNL_T lugu     "
									+ "                     where SYS.SIGN_OBJECT= :idIS      "
									+ "                           and (ROL.ID_SRV = URL.UP_ROLES or ROL.ID_SRV = LUGR.UP_ROLES )   "
									+ "                           and LUGU.UP_GROUP_USERS= LUGR.UP_GROUP_USERS(+)   "
									+ "                           and LUGU.UP_USERS(+)  = AU.ID_SRV   "
									+ "                           and URL.UP_USERS(+)  = AU.ID_SRV   "
									+ "                           and ROL.UP_IS=sys.ID_SRV    "
									+ "                           and AU.ID_SRV = :idUser       "
									+ "                     group by SYS.ID_SRV  ) t1 "
									+ "                     where t1.sys_id = SYS_FULL.ID_SRV ")
							.setParameter("idIS", idIS)
							.setParameter("idUser", idUserAuth).getResultList();

				}

				for (Object[] objectArray : lo) {

					idRec = objectArray[0].toString();

					uat = new Resource();

					if (objectArray[4] != null
							&& !objectArray[4].toString().trim().equals("")) {
						uat.setLinks(Arrays.asList(objectArray[4].toString()
								.replaceAll(" ", "").split(LINKS_SEPARATOR)));
					}
					
					uat.setName (objectArray[2] != null ? objectArray[2]
							.toString() : "");
					
					uat.setCode (objectArray[1] != null ? objectArray[1]
							.toString() : "");
					
					uat.setDescription (objectArray[3] != null ? objectArray[3]
							.toString() : "");

					

					result.add(uat); // для сохранения сортировки из запроса
					resultIds.put(idRec, uat);

				}

				LOGGER.debug("resources_data:02");

			} else if (idIS.startsWith(CUDConstants.groupArmPrefix)) {

				// группы
				// 1. пользователи

				if (CUDConstants.categorySYS.equals(category)) {

					lo = em.createNativeQuery(
							" select SYS.ID_SRV, SYS.SIGN_OBJECT, SYS.FULL_, SYS.DESCRIPTION, sys.LINKS from "
									+ " GROUP_SYSTEMS_KNL_T gsys, "
									+ " AC_IS_BSS_T sys, "
									+ " LINK_GROUP_SYS_SYS_KNL_T lgr "
									+ " where GSYS.ID_SRV=LGR.UP_GROUP_SYSTEMS "
									+ " and LGR.UP_SYSTEMS= SYS.ID_SRV "
									+ " and GSYS.GROUP_CODE=:idIS ")
							.setParameter("idIS", idIS).getResultList();

				} else {

					lo = em.createNativeQuery(
							"select sys_full.ID_SRV,  sys_full.SIGN_OBJECT, sys_full.FULL_, SYS_FULL.DESCRIPTION , sys_full.LINKS "
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
									+ "                         where GSYS.GROUP_CODE=:idIS   "
									+ "                         and GSYS.ID_SRV=LGR.UP_GROUP_SYSTEMS   "
									+ "                         and LGR.UP_SYSTEMS=SYS.ID_SRV   "
									+ "                         and ROL.UP_IS= SYS.ID_SRV   "
									+ "                         and (ROL.ID_SRV = URL.UP_ROLES or ROL.ID_SRV = LUGR.UP_ROLES )   "
									+ "                         and LUGU.UP_GROUP_USERS = LUGR.UP_GROUP_USERS(+)   "
									+ "                         and LUGU.UP_USERS(+)  = AU.ID_SRV   "
									+ "                         and URL.UP_USERS(+)  = AU.ID_SRV   "
									+ "                        and AU.ID_SRV = :idUser   "
									+ "                         group by SYS.ID_SRV )   t1 "
									+ "                          where t1.sys_id = SYS_FULL.ID_SRV "
									+ "                         order by sys_full.SIGN_OBJECT ")
							.setParameter("idIS", idIS)
							.setParameter("idUser", idUserAuth).getResultList();

				}

				for (Object[] objectArray : lo) {

			
					idRec = objectArray[0].toString();

					uat = new Resource();

					uat.setCode (objectArray[1] != null ? objectArray[1]
							.toString() : "");
					uat.setName (objectArray[2] != null ? objectArray[2]
							.toString() : "");
					uat.setDescription (objectArray[3] != null ? objectArray[3]
							.toString() : "");

					if (objectArray[4] != null
							&& !objectArray[4].toString().trim().equals("")) {
						uat.setLinks(Arrays.asList(objectArray[4].toString()
								.replaceAll(" ", "").split(LINKS_SEPARATOR)));
					}

					result.add(uat); // для сохранения сортировки из запроса
					resultIds.put(idRec, uat);

				}

				LOGGER.debug("resources_data:04");

			}

			sys_audit(72L, "idIS:" + idIS + "result_count:" + resultCount,
					"true; ", IPAddress, idUserAuth);

		} catch (Exception e) {
			sys_audit(72L, "; idIS:" + idIS, "error", IPAddress, idUserAuth);

			LOGGER.error("resources_data:Error:", e);

			throw new GeneralFailure(e.getMessage());
		}
		return result;

	}

	
	public List<Role> roles_data(String idIS, String category,
			Long idUserAuth, String IPAddress) throws GeneralFailure{
		     
		 //расширенная версия SyncManager is_roles
		
		// для групп систем, систем и подсистем

		       // category:
				// USER (используем idUserAuth)
				// SYS

		
				LOGGER.debug("roles_data:01");

				
				List<Role> result = new ArrayList<Role>();
				List<String> keyList = new ArrayList<String>();

				List<Object[]> lo = null;
				
				try {

					if (idIS == null || idIS.trim().isEmpty()) {
						LOGGER.debug("roles_data:01");
						throw new GeneralFailure("idIS is null!");
					}

					if (idIS.startsWith(CUDConstants.groupArmPrefix)) {
						// группа ИС

						LOGGER.debug("roles_data:02");

						// информация по системе
						if (CUDConstants.categorySYS.equals(category)) {
						 lo = em
								.createNativeQuery(
										"  SELECT '[' || sys_code || ']' || role_full.SIGN_OBJECT role_is_code, "
												+ "         role_full.FULL_, "
												+ "         role_full.DESCRIPTION "
												+ "    FROM (  SELECT SYS.SIGN_OBJECT sys_code, ROL.ID_SRV role_id "
												+ "              FROM GROUP_SYSTEMS_KNL_T gsys, "
												+ "                   AC_IS_BSS_T sys, "
												+ "                   AC_ROLES_BSS_T rol, "
												+ "                   LINK_GROUP_SYS_SYS_KNL_T lgr "
												+ "             WHERE     GSYS.GROUP_CODE = ? "
												+ "                   AND GSYS.ID_SRV = LGR.UP_GROUP_SYSTEMS "
												+ "                   AND LGR.UP_SYSTEMS = SYS.ID_SRV "
												+ "                   AND ROL.UP_IS = SYS.ID_SRV "
												+ "          GROUP BY SYS.SIGN_OBJECT, ROL.ID_SRV), "
												+ "         AC_ROLES_BSS_T role_full "
												+ "   WHERE role_full.ID_SRV = role_id "
												+ "ORDER BY sys_code ")
								.setParameter(1, idIS).getResultList();
						}else{
							lo = em
									.createNativeQuery(
											/*"select '['||sys_code||']' || role_full.SIGN_OBJECT role_full_code, "
													+ "role_full.FULL_, "
													+ "role_full.DESCRIPTION "
													+ "from( "
													+ "select SYS.SIGN_OBJECT sys_code,  ROL.ID_SRV role_id "
													+ "from GROUP_SYSTEMS_KNL_T gsys, "
													+ "AC_IS_BSS_T sys, "
													+ "AC_ROLES_BSS_T rol, "
													+ "LINK_GROUP_SYS_SYS_KNL_T lgr, "
													+ "LINK_GROUP_USERS_ROLES_KNL_T lugr, "
													+ "LINK_GROUP_USERS_USERS_KNL_T lugu, "
													+ "AC_USERS_LINK_KNL_T url, "
													+ "AC_USERS_KNL_T AU "
													+ "where GSYS.GROUP_CODE=:idIs "
													+ "and GSYS.ID_SRV=LGR.UP_GROUP_SYSTEMS "
													+ "and LGR.UP_SYSTEMS=SYS.ID_SRV "
													+ "and ROL.UP_IS= SYS.ID_SRV "
													+ "and (ROL.ID_SRV = URL.UP_ROLES or ROL.ID_SRV = LUGR.UP_ROLES ) "
													+ "and LUGU.UP_GROUP_USERS = LUGR.UP_GROUP_USERS(+) "
													+ "and LUGU.UP_USERS(+)  = AU.ID_SRV "
													+ "and URL.UP_USERS(+)  = AU.ID_SRV "
													+ "and AU.ID_SRV= :idUser "
													+ "group by SYS.SIGN_OBJECT, ROL.ID_SRV), "
													+ "AC_ROLES_BSS_T role_full "
													+ "WHERE role_full.ID_SRV = role_id "
													+ "order by sys_code "*/
											
											" SELECT '[' || sys_code || ']' || role_full.SIGN_OBJECT role_full_code, " + 
											"         role_full.FULL_, " + 
											"         role_full.DESCRIPTION " + 
											"    FROM (  SELECT sys_code, role_id " + 
											"              FROM (  SELECT SYS.SIGN_OBJECT sys_code, ROL.ID_SRV role_id " + 
											"                        FROM GROUP_SYSTEMS_KNL_T gsys, " + 
											"                             AC_IS_BSS_T sys, " + 
											"                             AC_ROLES_BSS_T rol, " + 
											"                             LINK_GROUP_SYS_SYS_KNL_T lgr, " + 
											"                             AC_USERS_LINK_KNL_T url, " + 
											"                             AC_USERS_KNL_T AU " + 
											"                       WHERE     GSYS.GROUP_CODE = :idIs " + 
											"                             AND GSYS.ID_SRV = LGR.UP_GROUP_SYSTEMS " + 
											"                             AND LGR.UP_SYSTEMS = SYS.ID_SRV " + 
											"                             AND ROL.UP_IS = SYS.ID_SRV " + 
											"                             AND ROL.ID_SRV = URL.UP_ROLES " + 
											"                             AND URL.UP_USERS = AU.ID_SRV " + 
											"                             AND AU.ID_SRV = :idUser " + 
											"                    GROUP BY SYS.SIGN_OBJECT, ROL.ID_SRV " + 
											"                    UNION ALL " + 
											"                      SELECT SYS.SIGN_OBJECT sys_code, ROL.ID_SRV role_id " + 
											"                        FROM GROUP_SYSTEMS_KNL_T gsys, " + 
											"                             AC_IS_BSS_T sys, " + 
											"                             AC_ROLES_BSS_T rol, " + 
											"                             LINK_GROUP_SYS_SYS_KNL_T lgr, " + 
											"                             LINK_GROUP_USERS_ROLES_KNL_T lugr, " + 
											"                             LINK_GROUP_USERS_USERS_KNL_T lugu, " + 
											"                             AC_USERS_KNL_T AU " + 
											"                       WHERE     GSYS.GROUP_CODE = :idIs " + 
											"                             AND GSYS.ID_SRV = LGR.UP_GROUP_SYSTEMS " + 
											"                             AND LGR.UP_SYSTEMS = SYS.ID_SRV " + 
											"                             AND ROL.UP_IS = SYS.ID_SRV " + 
											"                             AND ROL.ID_SRV = LUGR.UP_ROLES " + 
											"                             AND LUGU.UP_GROUP_USERS = LUGR.UP_GROUP_USERS " + 
											"                             AND LUGU.UP_USERS = AU.ID_SRV " + 
											"                             AND AU.ID_SRV = :idUser " + 
											"                    GROUP BY SYS.SIGN_OBJECT, ROL.ID_SRV) " + 
											"          GROUP BY sys_code, role_id), " + 
											"         AC_ROLES_BSS_T role_full " + 
											"   WHERE role_full.ID_SRV = role_id " + 
											"ORDER BY sys_code " )
									.setParameter("idIs", idIS)
									.setParameter("idUser", idUserAuth).getResultList();
						}
						for (Object[] objectArray : lo) {
							LOGGER.debug("IdRole:" + objectArray[0].toString());

							Role role = new Role();

							role.setCode(objectArray[0].toString());
							role.setName(objectArray[1].toString());
							role.setDescription (objectArray[2] != null ? objectArray[2]
									.toString() : null);

							result.add(role);

							keyList.add(objectArray[0].toString());

						}

						LOGGER.debug("roles_data:03");

					} else if (idIS.startsWith(CUDConstants.armPrefix)
							|| idIS.startsWith(CUDConstants.subArmPrefix)) {
						// система или подсистема

						// !!!
						idIS = getCodeIs(idIS);

						
						// информация по системе
						if (CUDConstants.categorySYS.equals(category)) {
						
						 lo = em
								.createNativeQuery(
										"SELECT ROL.SIGN_OBJECT, ROL.FULL_, ROL.DESCRIPTION "
												+ "  FROM AC_IS_BSS_T app, AC_ROLES_BSS_T rol "
												+ " WHERE APP.SIGN_OBJECT = ? AND ROL.UP_IS = APP.ID_SRV ")
								.setParameter(1, idIS).getResultList();
						}else{
							// доступные роли для пользователя
							
							lo =  em
									.createNativeQuery(
											/*"select ROL.SIGN_OBJECT, ROL.FULL_, ROL.DESCRIPTION "
													+ "from  AC_IS_BSS_T sys, "
													+ "         AC_ROLES_BSS_T rol, "
													+ "         AC_USERS_LINK_KNL_T url, "
													+ "         AC_USERS_KNL_T AU, "
													+ "         AC_SUBSYSTEM_CERT_BSS_T subsys, "
													+ "         LINK_GROUP_USERS_ROLES_KNL_T lugr, "
													+ "         LINK_GROUP_USERS_USERS_KNL_T lugu "
													+ "where (SYS.SIGN_OBJECT= :idIs or  SUBSYS.SUBSYSTEM_CODE=:idIs) "
													+ "      and (ROL.ID_SRV = URL.UP_ROLES or ROL.ID_SRV = LUGR.UP_ROLES ) "
													+ "      and LUGU.UP_GROUP_USERS= LUGR.UP_GROUP_USERS(+) "
													+ "      and LUGU.UP_USERS(+)  = AU.ID_SRV "
													+ "      and URL.UP_USERS(+)  = AU.ID_SRV "
													+ "      and ROL.UP_IS=sys.ID_SRV "
													+ "      and AU.ID_SRV= :idUser "
													+ "      and  SUBSYS.UP_IS(+) =SYS.ID_SRV "
													+ "group by ROL.SIGN_OBJECT, ROL.FULL_, ROL.DESCRIPTION "*/
													
											"SELECT SIGN_OBJECT, FULL_, DESCRIPTION " + 
											"    FROM (  SELECT ROL.SIGN_OBJECT, ROL.FULL_, ROL.DESCRIPTION " + 
											"              FROM AC_IS_BSS_T sys, " + 
											"                   AC_ROLES_BSS_T rol, " + 
											"                   AC_USERS_LINK_KNL_T url, " + 
											"                   AC_USERS_KNL_T AU, " + 
											"                   AC_SUBSYSTEM_CERT_BSS_T subsys " + 
											"             WHERE     (SYS.SIGN_OBJECT = :idIs OR SUBSYS.SUBSYSTEM_CODE = :idIs) " + 
											"                   AND ROL.ID_SRV = URL.UP_ROLES " + 
											"                   AND URL.UP_USERS = AU.ID_SRV " + 
											"                   AND ROL.UP_IS = sys.ID_SRV " + 
											"                   AND AU.ID_SRV = :idUser " + 
											"                   AND SUBSYS.UP_IS(+) = SYS.ID_SRV " + 
											"          GROUP BY ROL.SIGN_OBJECT, ROL.FULL_, ROL.DESCRIPTION " + 
											"          UNION ALL " + 
											"            SELECT ROL.SIGN_OBJECT, ROL.FULL_, ROL.DESCRIPTION " + 
											"              FROM AC_IS_BSS_T sys, " + 
											"                   AC_ROLES_BSS_T rol, " + 
											"                   AC_USERS_KNL_T AU, " + 
											"                   AC_SUBSYSTEM_CERT_BSS_T subsys, " + 
											"                   LINK_GROUP_USERS_ROLES_KNL_T lugr, " + 
											"                   LINK_GROUP_USERS_USERS_KNL_T lugu " + 
											"             WHERE     (SYS.SIGN_OBJECT = :idIs OR SUBSYS.SUBSYSTEM_CODE = :idIs) " + 
											"                   AND ROL.ID_SRV = LUGR.UP_ROLES " + 
											"                   AND LUGU.UP_GROUP_USERS = LUGR.UP_GROUP_USERS " + 
											"                   AND LUGU.UP_USERS = AU.ID_SRV " + 
											"                   AND ROL.UP_IS = sys.ID_SRV " + 
											"                   AND AU.ID_SRV = :idUser " + 
											"                   AND SUBSYS.UP_IS(+) = SYS.ID_SRV " + 
											"          GROUP BY ROL.SIGN_OBJECT, ROL.FULL_, ROL.DESCRIPTION) " + 
											"GROUP BY SIGN_OBJECT, FULL_, DESCRIPTION "
											)
									.setParameter("idIs", idIS)
									.setParameter("idUser", idUserAuth).getResultList();
						}
						for (Object[] objectArray : lo) {

							Role role = new Role();

							role.setCode(objectArray[0].toString());
							role.setName(objectArray[1].toString());
							role.setDescription (objectArray[2] != null ? objectArray[2]
									.toString() : null);

							result.add(role);

							keyList.add(objectArray[0].toString());

						}

						LOGGER.debug("roles_data:04");

					}

					sys_audit(80L, "idIS:" + idIS, "true", IPAddress, null);

					return result;

				} catch (Exception e) {
					sys_audit(80L, "idIS:" + idIS, "error", IPAddress, null);
					throw new GeneralFailure(e.getMessage());
				}
		
	}
	
	/**
	 * данные по организациям
	 */
	public ISOrganisations is_organisations(
	    String idIS, Integer start, Integer count, List<String> rolesCodes,
	    Long idUserAuth, String IPAddress) throws GeneralFailure {

		LOGGER.debug("is_organisations:STUB");

		return null;
	}

	/**
	 * проверки наличия системы
	 */
	public void is_exist(String idIS) throws GeneralFailure {

		LOGGER.debug("is_exist:idIS:" + idIS);

		try {

			em.createNativeQuery(
					"select APP.ID_SRV " + "from AC_IS_BSS_T app "
							+ "where APP.SIGN_OBJECT=?").setParameter(1, idIS)
					.getSingleResult();

		} catch (NoResultException ex) {
			LOGGER.error("is_exist:NoResultException");
			throw new GeneralFailure("Информационная система не определёна!");
		} catch (Exception e) {
			LOGGER.error("is_exist:Error:", e);
			throw new GeneralFailure(e.getMessage());
		}
	}

	/**
	 * протоколирование действий
	 */
	private void sys_audit(Long idServ, String inp_param, String result,
			String ip_adr, Long idUser) {
		LOGGER.debug("sys_audit");
		try {

			if (idUser != null && !idUser.equals(-1L)) {
				em.createNativeQuery(
						"insert into SERVICES_LOG_KNL_T( "
								+ "ID_SRV,  UP_SERVICES, DATE_ACTION, CREATED, "
								+ "input_param, result_value, ip_address, UP_USERS ) "
								+ "values(SERVICES_LOG_KNL_SEQ.nextval , ?, sysdate, sysdate, "
								+ "?, ?, ?, ? ) ").setParameter(1, idServ)
						.setParameter(2, inp_param).setParameter(3, result)
						.setParameter(4, ip_adr).setParameter(5, idUser)
						.executeUpdate();
			} else {
				em.createNativeQuery(
						"insert into SERVICES_LOG_KNL_T( "
								+ "ID_SRV,  UP_SERVICES, DATE_ACTION, CREATED, "
								+ "input_param, result_value, ip_address ) "
								+ "values(SERVICES_LOG_KNL_SEQ.nextval , ?, sysdate, sysdate, "
								+ "?, ?, ? ) ").setParameter(1, idServ)
						.setParameter(2, inp_param).setParameter(3, result)
						.setParameter(4, ip_adr).executeUpdate();
			}
		} catch (Exception e) {
			LOGGER.error("sys_audit:Error:", e);
			
		}

	}

	/**
	 * получение ИД системы по коду системы
	 */
	private String getCodeIs(String codeSys) throws GeneralFailure {

		String result = null;

		try {

			// сюда заходим только в случае систем и подсистем
			// вообще нужно толко для подсистем - взять код их ИС.
			// при группе систем своя ветка - там код группы
			// используется прямо в запросе.

			// системы и подсистемы

			result = (String) em
					.createNativeQuery(
							"select SYS.SIGN_OBJECT "
									+ "from  AC_IS_BSS_T sys, "
									+ "AC_SUBSYSTEM_CERT_BSS_T subsys "
									+ "where (SYS.SIGN_OBJECT= :codeSys or  SUBSYS.SUBSYSTEM_CODE= :codeSys) "
									+ "and  SUBSYS.UP_IS(+) =SYS.ID_SRV "
									+ "group by SYS.SIGN_OBJECT ")
					.setParameter("codeSys", codeSys).getSingleResult();

		

		} catch (NoResultException ex) {
			LOGGER.error("get_code_is:NoResultException");
			throw new GeneralFailure("System is not defined");
		} catch (Exception e) {
			throw new GeneralFailure(e.getMessage());
		}
		return result;
	}
	
	
	private Map<String,String> getSettingsMap(List<String> settings) throws GeneralFailure {

		String filterSt = null;
		Map<String,String> settingsMap = new HashMap<String,String>();
		
		if(settings==null||settings.isEmpty()){
			return settingsMap;
		}
		
		try {

			
			for(String setting :settings){
				if(setting==null){
					break;
				}
				LOGGER.info("getSettingsMap:01:"+setting);
				
				if(setting.startsWith("FILTER_FIO:")){
					
					filterSt = setting.substring(setting.indexOf(":")+1).trim();
					
                    if("".equals(filterSt)){
                    	filterSt="_#_";
					}
					
					
					settingsMap.put("FILTER_FIO", filterSt);
					
					LOGGER.info("getSettingsMap:02:"+filterSt);
					
					
					
				}else if(setting.startsWith("FILTER_ORG:")){
					
                    filterSt = setting.substring(setting.indexOf(":")+1).trim();
					
                    if("".equals(filterSt)){
                    	filterSt="_#_";
					}
                    
					settingsMap.put("FILTER_ORG", filterSt);
					
					LOGGER.info("getSettingsMap:03:"+filterSt);
					
					
				}else if(setting.startsWith("ACCOUNTS_ONLY:")){
					
					settingsMap.put("ACCOUNTS_ONLY", 
							"TRUE".equals(setting.substring(setting.indexOf(":")+1).toUpperCase())
							 ?"TRUE":"FALSE"
							);
					
				}
			}

		
			if(!"TRUE".equals(settingsMap.get("ACCOUNTS_ONLY"))){
				settingsMap.remove("FILTER_FIO");
				settingsMap.remove("FILTER_ORG");
			}

		} catch (Exception e) {
			throw new GeneralFailure(e.getMessage());
		}
		return settingsMap;
	}
	
	
	
}
