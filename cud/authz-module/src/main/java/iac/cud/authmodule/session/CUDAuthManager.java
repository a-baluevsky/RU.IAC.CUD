package iac.cud.authmodule.session;

import iac.cud.authmodule.dataitem.AuthItem;
import iac.cud.authmodule.dataitem.PageItem;

import java.util.ArrayList;
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

/**
 * Предоставление информации о наличии прав доступа
 * 
 * @author bubnov
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
 public class CUDAuthManager implements CUDAuthManagerLocal,
		CUDAuthManagerRemote {

	@PersistenceContext(unitName = "CUDAuthModule")
	EntityManager em;

	private static final Logger LOGGER = LoggerFactory.getLogger(CUDAuthManager.class);

	public CUDAuthManager() {
	}

	public Map<String, List<String>[]> authComplete(Long appCode,
			String login, String password) throws Exception {

		LOGGER.debug("authComplete");
		try {
			if (appCode == null || login == null || password == null) {
				return null;
			}

			Long idUser = createAuth(login, password);

			if (idUser == null) {
				return null;
			}
			LOGGER.debug("authComplete:idUser:" + idUser);

			return createResourceTree(appCode, null, idUser);
		} catch (Exception e) {
			LOGGER.error("authComplete:Error:", e);
			throw e;
		}
	}

	/**
	 * Аутентификация пользователя
	 * 
	 * @param login
	 *            логин
	 * @param password
	 *            пароль
	 * @return ид пользователя, если упешно, иначе null
	 * @throws Exception
	 */
	public Long authenticate(String login, String password) throws Exception {
		LOGGER.debug("authenticate");
		if (login == null || password == null) {
			return null;
		}
		return createAuth(login, password);
	}

	public List<String>[] access(Long appCode, String pageCode, Long idUser)
			throws Exception {
		LOGGER.debug("access");
		if (appCode == null || pageCode == null || idUser == null) {
			return null;
		}
		return createResourceTree(appCode, pageCode, idUser).get(pageCode);
	}

	private Long createAuth(String login, String password) throws Exception {
		Long idUser = null;
		try {
			LOGGER.debug("createAuth:01");
			idUser = ((java.math.BigDecimal) em
					.createNativeQuery(
							"select AU.ID_SRV "
									+ "from "
									+ "AC_USERS_KNL_T au "
									+ "where AU.LOGIN=? "
									+ "and AU.PASSWORD_=? "
									+ "and (AU.START_ACCOUNT is null or au.START_ACCOUNT <= sysdate) "
									+ "and (AU.START_ACCOUNT is null or au.START_ACCOUNT > sysdate) ")
					.setParameter(1, login).setParameter(2, password)
					.getSingleResult()).longValue();

			LOGGER.debug("authenticate:idUser:" + idUser);

		} catch (NoResultException ex) {
			LOGGER.error("createAuth:NoResultException");
		} catch (Exception e) {
			LOGGER.error("createAuth:Error:", e);
			throw e;
		}
		return idUser;
	}

	private Map<String, List<String>[]> createResourceTree(Long appCode,
			String pageCode, Long idUser) throws Exception {

		Map<String, List<String>[]> result = new HashMap<String, List<String>[]>();

		ArrayList<String> raiList = new ArrayList<String>();
		ArrayList<String> permList = new ArrayList<String>();
		String pageCode_prev = "", pageCode_curr = "";
		String idRai_prev = "", idRai_curr = "";
		int raiChange = 0;

		try {
			List<Object[]> lo = em
					.createNativeQuery(
							"select AAD.PAGE_CODE, ARR.ID_RAION, ADP.ID_PERM "
									+ "from AC_APP_DOMAINS aad, "
									+ "AC_LINK_ROLE_APP_DOMEN_PRMSSNS adp, "
									+ "AC_LINK_USER_TO_ROLE_TO_RAIONS arr "
									+ "where AAD.APP_CODE=? and "
									+ "adp.ID_DOMEN=AAD.ID_DOMEN "
									+ "and adp.ID_ROLE=arr.ID_ROLE "
									+ "and arr.ID_USER=? "
									+ (pageCode != null ? "and AAD.PAGE_CODE=? "
											: "and 1=? ")
									+ "group by AAD.PAGE_CODE, ARR.ID_RAION, ADP.ID_PERM "
									+ "order by  AAD.PAGE_CODE, ARR.ID_RAION, ADP.ID_PERM ")
					.setParameter(1, appCode).setParameter(2, idUser)
					.setParameter(3, (pageCode != null ? pageCode : 1))
					.getResultList();

			for (Object[] objectArray : lo) {
				pageCode_curr = objectArray[0].toString();
				idRai_curr = objectArray[1].toString();

				if (pageCode_curr.equals(pageCode_prev)
						|| "".equals(pageCode_prev)) {

					if ((idRai_curr.equals(idRai_prev) || "".equals(idRai_prev))) {
						if (raiChange == 0) {
							permList.add(objectArray[2].toString());
						}
						if ("".equals(idRai_prev) && !"-1".equals(idRai_curr)) {
							raiList.add(idRai_curr);
						}
					} else {

						if (!"-1".equals(idRai_curr)) {
							raiList.add(idRai_curr);
						}
						raiChange = 1;
					}

				} else {
					result.put(pageCode_prev, new ArrayList[] { raiList,
							permList });
					raiChange = 0;
					raiList.clear();
					permList.clear();
					if (!"-1".equals(idRai_curr)) {
						raiList.add(objectArray[1].toString());
					}
					permList.add(objectArray[2].toString());

				}

				idRai_prev = idRai_curr;
				pageCode_prev = pageCode_curr;
			}
			if (!idRai_curr.equals("")) {
				result.put(pageCode_prev, new ArrayList[] { raiList, permList });
			}
		} catch (Exception e) {
			LOGGER.error("createResourceTree:Error:", e);
			throw e;
		}
		return result;
	}

	/**
	 * Аутентификация и авторизация пользователя
	 * 
	 * @param appCode
	 *            ид приложения
	 * @param login
	 *            логин
	 * @param password
	 *            пароль
	 * @return AuthItem, если упешно, иначе null
	 * @throws Exception
	 */
	public AuthItem authCompleteItem(Long appCode, String login, String password)
			throws Exception {
		try {
			LOGGER.debug("authCompleteItem:appCode:" + appCode);

			if (appCode == null || login == null || password == null) {
				return null;
			}
			LOGGER.debug("auth_01");
			Long idUser = createAuth(login, password);

			if (idUser == null) {
				return null;
			}
			LOGGER.debug("authCompleteItem:idUser:" + idUser);

			return createResourceTreeItem(appCode, null, idUser);
		} catch (Exception e) {
			LOGGER.error("authCompleteItem:Error:", e);
			throw e;
		}
	}

	private AuthItem createResourceTreeItem(Long appCode, String pageCode,
			Long idUser) throws Exception {

		LOGGER.debug("createResourceTreeItem:01");

		AuthItem result = new AuthItem();
		result.setIdUser(idUser);

		ArrayList<String> permList = new ArrayList<String>();
		String pageCode_prev = "", pageCode_curr = "";
		
		try {
			List<Object[]> lo = em
					.createNativeQuery(

							"select * from( "
									+ "select AAD.PAGE_CODE , ADP.UP_PERMISS "
									+ "from AC_APP_DOMAINS_BSS_T aad, "
									+ "AC_LINK_ROLE_APP_DOM_PRM_KNL_T adp, "
									+ "AC_USERS_LINK_KNL_T arr "
									+ "where AAD.UP_IS = ? and "
									+ "ADP.UP_DOM =AAD.ID_SRV "
									+ "and ADP.UP_ROLES =ARR.UP_ROLES "
									+ "and ARR.UP_USERS = ? "
									+ (pageCode != null ? "and AAD.PAGE_CODE=? ) "
											: "and 1=? ) ")
									+ "group by PAGE_CODE,UP_PERMISS "
									+ "order by PAGE_CODE, UP_PERMISS ")

					.setParameter(1, appCode).setParameter(2, idUser)
					.setParameter(3, (pageCode != null ? pageCode : 1))
					.getResultList();

			for (Object[] objectArray : lo) {
				pageCode_curr = objectArray[0].toString();

				if (pageCode_curr.equals(pageCode_prev)
						|| "".equals(pageCode_prev)) {

					permList.add(objectArray[1].toString());
				} else {

					result.getPageList().put(pageCode_prev,
							new PageItem(permList));

				
					permList = new ArrayList<String>();

					permList.add(objectArray[1].toString());
				}

				pageCode_prev = pageCode_curr;
			}

			if (!"".equals(pageCode_curr)) {

				result.getPageList().put(pageCode_prev, new PageItem(permList));
			}

		} catch (Exception e) {
			LOGGER.error("createResourceTreeItem:Error:", e);
			throw e;
		}
		return result;
	}

	/**
	 * Проверка прав пользователя к заданному ресурсу приложения
	 * 
	 * @param appCode
	 *            ид приложения
	 * @param pageCode
	 *            внутренний ид ресурса в приложении
	 * @param idUser
	 *            ид пользователя
	 * @return PageItem, если упешно, иначе null
	 * @throws Exception
	 */
	public PageItem accessItem(Long appCode, String pageCode, Long idUser)
			throws Exception {
		LOGGER.debug("accessItem");
		if (appCode == null || pageCode == null || idUser == null) {
			return null;
		}
		return createResourceTreeItem(appCode, pageCode, idUser).getPageList()
				.get(pageCode);
	}
}