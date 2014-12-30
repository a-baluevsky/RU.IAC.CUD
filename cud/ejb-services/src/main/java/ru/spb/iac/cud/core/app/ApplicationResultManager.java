package ru.spb.iac.cud.core.app;

import java.util.HashMap; import java.util.Map;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.items.app.AppAttribute;
import ru.spb.iac.cud.items.app.AppAttributesClassif;
import ru.spb.iac.cud.items.app.AppResult;
import ru.spb.iac.cud.items.app.AppResultRequest;
import ru.spb.iac.cud.items.app.AppStatusClassif;
import ru.spb.iac.cud.items.app.AppSystemAttributesClassif;
import ru.spb.iac.cud.items.app.AppTypeClassif;
import ru.spb.iac.cud.items.app.AppUserAttributesClassif;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EJB дл€ ответов за€вок
 * 
 * @author bubnov
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
 public class ApplicationResultManager implements ApplicationResultManagerLocal,
		ApplicationResultManagerRemote {

	@PersistenceContext(unitName = "AuthServices")
	EntityManager em;

	private static Map<Integer, String> status_map = new HashMap<Integer, String>();

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationResultManager.class);

	static {
		status_map.put(0, AppStatusClassif.IN_PROCESSING.name());
		status_map.put(1, AppStatusClassif.DONE.name());
		status_map.put(2, AppStatusClassif.REJECTED.name());
	}

	public ApplicationResultManager() {
	}

	public List<AppResult> result(
			
			List<AppResultRequest> request_list, Long idUserAuth,
			String IPAddress) throws GeneralFailure {

		// principal не используетс€

		LOGGER.debug("registration_result:appType:");

		List<AppResult> resultList = null;

		try {

			for (AppResultRequest request : request_list) {

				String appType = request.getAppType();
				String number = request.getNumber();
				String secret = request.getSecret();

				number_secret_valid(number, secret, appType);

				if (resultList == null) {
					resultList = new ArrayList<AppResult>();
				}
				if (appType.equals(AppTypeClassif.SYSTEM_REGISTRATION.name())) {

					resultList.add(system_registration_result(number, secret,
							idUserAuth, IPAddress));

				} else if (appType.equals(AppTypeClassif.USER_REGISTRATION
						.name())) {

					resultList.add(user_registration_result(number, secret,
							idUserAuth, IPAddress));

				} else if (appType.equals(AppTypeClassif.USER_ACCESS_ROLES
						.name())) {

					resultList.add(user_access_roles_result(number, secret,
							idUserAuth, IPAddress));

				} else if (appType.equals(AppTypeClassif.USER_ACCESS_GROUPS
						.name())) {

					resultList.add(user_access_groups_result(number, secret,
							idUserAuth, IPAddress));

				} else if (appType.equals(AppTypeClassif.USER_BLOCK.name())) {

					resultList.add(user_block_result(number, secret,
							idUserAuth, IPAddress));

				} else if (appType.equals(AppTypeClassif.SYSTEM_MODIFICATION
						.name())) {

					resultList.add(system_modification_result(number, secret,
							idUserAuth, IPAddress));

				} else if (appType.equals(AppTypeClassif.USER_MODIFICATION
						.name())) {

					resultList.add(user_modification_result(number, secret,
							idUserAuth, IPAddress));
				} else if (appType.equals(AppTypeClassif.USER_MODIFICATION_ACC
						.name())) {

					resultList.add(user_modification_acc_result(number,
							secret, idUserAuth, IPAddress));
				} else if (appType.equals(AppTypeClassif.USER_MODIFICATION_CERT
						.name())) {

					resultList.add(user_modification_cert_result(number,
							secret, idUserAuth, IPAddress));
				}
			}
		} catch (Exception e) {
		

			LOGGER.error("system_registration_result:Error:", e);
			throw new GeneralFailure(e.getMessage());
		}
		return resultList;

	}

	private AppResult system_registration_result(String number, String secret,
			Long idUserAuth, String IPAddress) throws GeneralFailure {

		LOGGER.debug("system_registration_result:01");

		AppResult result = new AppResult();
		List<AppAttribute> atList = new ArrayList<AppAttribute>();

		try {

			List<Object[]> results = em
					.createNativeQuery(
							"select JAS.STATUS, ARM.SIGN_OBJECT, ARM.FULL_, ARM.DESCRIPTION, JAS.REJECT_REASON "
									+ "from JOURN_APP_SYSTEM_BSS_T jas, "
									+ "AC_IS_BSS_T arm "
									+ "where ARM.ID_SRV(+) =JAS.UP_IS "
									+ "and JAS.ID_SRV=? ")
					.setParameter(1, new Long(number)).getResultList();

			int status = Integer.parseInt(results.get(0)[0].toString());

			result.setStatus(status_map.get(status));

			if (status == 1) {

				AppAttribute ats = new AppAttribute();
				ats.setName(AppSystemAttributesClassif.CODE_SYS.name());
				ats.setValue(results.get(0)[1] != null ? results.get(0)[1]
						.toString() : "");
				atList.add(ats);

				result.setAttributes(atList);

			} else if (status == 2) {

				AppAttribute ats = new AppAttribute();
				ats.setName(AppAttributesClassif.REJECT_REASON.name());
				ats.setValue(results.get(0)[4] != null ? results.get(0)[4]
						.toString() : "");
				atList.add(ats);

				result.setAttributes(atList);

			}

		} catch (Exception es) {
		
			LOGGER.error("system_registration_result:Error:", es);
			throw new GeneralFailure(es.getMessage());
		}
		return result;
	}

	private AppResult user_registration_result(String number, String secret,
			Long idUserAuth, String IPAddress) throws GeneralFailure {

		LOGGER.debug("user_registration_result:01");

		AppResult result = new AppResult();
		List<AppAttribute> atList = new ArrayList<AppAttribute>();

		try {

			List<Object[]> results = em
					.createNativeQuery(
							"select JAS.STATUS, USR.LOGIN, USR.PASSWORD_, JAS.REJECT_REASON, USR.ID_SRV "
									+ "from JOURN_APP_USER_BSS_T jas, "
									+ "AC_USERS_KNL_T usr "
									+ "where usr.ID_SRV(+) =JAS.UP_USER_APP "
									+ "and JAS.ID_SRV=? ")
					.setParameter(1, new Long(number)).getResultList();

			int status = Integer.parseInt(results.get(0)[0].toString());

			result.setStatus(status_map.get(status));

			if (status == 1) {

				AppAttribute at = new AppAttribute();
				at.setName(AppUserAttributesClassif.LOGIN_USER.name());
				at.setValue(results.get(0)[1] != null ? results.get(0)[1]
						.toString() : "");
				atList.add(at);

				at = new AppAttribute();
				at.setName(AppUserAttributesClassif.PASSWORD_USER.name());
				at.setValue(results.get(0)[2] != null ? results.get(0)[2]
						.toString() : "");
				atList.add(at);

				at = new AppAttribute();
				at.setName(AppUserAttributesClassif.UID_USER.name());
				at.setValue(results.get(0)[4] != null ? results.get(0)[4]
						.toString() : "");
				atList.add(at);

				result.setAttributes(atList);

			} else if (status == 2) {
				AppAttribute atur = new AppAttribute();
				atur.setName(AppAttributesClassif.REJECT_REASON.name());
				atur.setValue(results.get(0)[3] != null ? results.get(0)[3]
						.toString() : "");
				atList.add(atur);

				result.setAttributes(atList);
			}

			LOGGER.debug("user_registration_result:02");

			

		} catch (Exception eur) {
			
			LOGGER.error("user_registration_result:Error:", eur);
			throw new GeneralFailure(eur.getMessage());
		}
		return result;
	}

	private AppResult user_access_roles_result(String number, String secret,
			Long idUserAuth, String IPAddress) throws GeneralFailure {

		LOGGER.debug("user_access_roles_result:01");

		AppResult result = new AppResult();
		List<AppAttribute> atList = new ArrayList<AppAttribute>();

		try {

			List<Object[]> results = em
					.createNativeQuery(
							"select JAS.STATUS, JAS.REJECT_REASON "
									+ "from JOURN_APP_ACCESS_BSS_T jas "
									+ "where JAS.ID_SRV=? ")
					.setParameter(1, new Long(number)).getResultList();

			int status = Integer.parseInt(results.get(0)[0].toString());

			result.setStatus(status_map.get(status));

			if (status == 2) {
				AppAttribute atuar = new AppAttribute();
				atuar.setName(AppAttributesClassif.REJECT_REASON.name());
				atuar.setValue(results.get(0)[1] != null ? results.get(0)[1]
						.toString() : "");
				atList.add(atuar);

				result.setAttributes(atList);
			}

			LOGGER.debug("user_access_roles_result:02");

		

		} catch (Exception euar) {
			

			LOGGER.error("user_access_roles_result:Error:", euar);
			throw new GeneralFailure(euar.getMessage());
		}
		return result;
	}

	private AppResult user_access_groups_result(String number, String secret,
			Long idUserAuth, String IPAddress) throws GeneralFailure {

		LOGGER.debug("user_access_groups_result:01");

		AppResult result = new AppResult();
		List<AppAttribute> atList = new ArrayList<AppAttribute>();

		try {

			List<Object[]> results = em
					.createNativeQuery(
							"select JAS.STATUS, JAS.REJECT_REASON "
									+ "from JOURN_APP_ACCESS_GROUPS_BSS_T jas "
									+ "where JAS.ID_SRV=? ")
					.setParameter(1, new Long(number)).getResultList();

			int status = Integer.parseInt(results.get(0)[0].toString());

			result.setStatus(status_map.get(status));

			if (status == 2) {
				AppAttribute at = new AppAttribute();
				at.setName(AppAttributesClassif.REJECT_REASON.name());
				at.setValue(results.get(0)[1] != null ? results.get(0)[1]
						.toString() : "");
				atList.add(at);

				result.setAttributes(atList);
			}

			LOGGER.debug("user_access_groups_result:02");

		

		} catch (Exception e) {
			

			LOGGER.error("user_access_groups_result:Error:", e);
			throw new GeneralFailure(e.getMessage());
		}
		return result;
	}

	private AppResult user_block_result(String number, String secret,
			Long idUserAuth, String IPAddress) throws GeneralFailure {

		LOGGER.debug("user_block_result:01");

		AppResult result = new AppResult();
		List<AppAttribute> atList = new ArrayList<AppAttribute>();

		try {

			List<Object[]> results = em
					.createNativeQuery(
							"select JAS.STATUS, JAS.REJECT_REASON "
									+ "from JOURN_APP_BLOCK_BSS_T jas "
									+ "where JAS.ID_SRV=? ")
					.setParameter(1, new Long(number)).getResultList();

			int status = Integer.parseInt(results.get(0)[0].toString());

			result.setStatus(status_map.get(status));

			if (status == 2) {
				AppAttribute atub = new AppAttribute();
				atub.setName(AppAttributesClassif.REJECT_REASON.name());
				atub.setValue(results.get(0)[1] != null ? results.get(0)[1]
						.toString() : "");
				atList.add(atub);

				result.setAttributes(atList);
			}

			LOGGER.debug("user_block_result:02");

		

		} catch (Exception eub) {
		

			LOGGER.error("user_block_result:Error:", eub);
			throw new GeneralFailure(eub.getMessage());
		}
		return result;
	}

	private AppResult system_modification_result(String number, String secret,
			Long idUserAuth, String IPAddress) throws GeneralFailure {

		LOGGER.debug("system_modification_result:01");

		AppResult result = new AppResult();
		List<AppAttribute> atList = new ArrayList<AppAttribute>();

		try {

			List<Object[]> results = em
					.createNativeQuery(
							"select JAS.STATUS, JAS.REJECT_REASON "
									+ "from JOURN_APP_SYSTEM_MODIFY_BSS_T jas "
									+ "where JAS.ID_SRV=? ")
					.setParameter(1, new Long(number)).getResultList();

			int status = Integer.parseInt(results.get(0)[0].toString());

			result.setStatus(status_map.get(status));

			if (status == 2) {
				AppAttribute atsm = new AppAttribute();
				atsm.setName(AppAttributesClassif.REJECT_REASON.name());
				atsm.setValue(results.get(0)[1] != null ? results.get(0)[1]
						.toString() : "");
				atList.add(atsm);

				result.setAttributes(atList);
			}

			LOGGER.debug("system_modification_result:02");

			

		} catch (Exception esm) {
		

			LOGGER.error("system_modification_result:Error:", esm);
			throw new GeneralFailure(esm.getMessage());
		}
		return result;
	}

	private AppResult user_modification_result(String number, String secret,
			Long idUserAuth, String IPAddress) throws GeneralFailure {

		LOGGER.debug("user_modification_result:01");

		AppResult result = new AppResult();
		List<AppAttribute> atList = new ArrayList<AppAttribute>();

		try {

			List<Object[]> results = em
					.createNativeQuery(
							"select JAS.STATUS, JAS.REJECT_REASON "
									+ "from JOURN_APP_USER_MODIFY_BSS_T jas "
									+ "where JAS.ID_SRV=? ")
					.setParameter(1, new Long(number)).getResultList();

			int status = Integer.parseInt(results.get(0)[0].toString());

			result.setStatus(status_map.get(status));

			if (status == 2) {
				AppAttribute atum = new AppAttribute();
				atum.setName(AppAttributesClassif.REJECT_REASON.name());
				atum.setValue(results.get(0)[1] != null ? results.get(0)[1]
						.toString() : "");
				atList.add(atum);

				result.setAttributes(atList);
			}

			LOGGER.debug("user_modification_result:02");

			

		} catch (Exception e) {
			

			LOGGER.error("user_modification_result:Error:", e);
			throw new GeneralFailure(e.getMessage());
		}
		return result;
	}

	private AppResult user_modification_acc_result(String number,
			String secret, Long idUserAuth, String IPAddress)
			throws GeneralFailure {

		LOGGER.debug("user_modification_acc_result:01");

		AppResult result = new AppResult();
		List<AppAttribute> atList = new ArrayList<AppAttribute>();

		try {

			List<Object[]> results = em
					.createNativeQuery(
							"select JAS.STATUS, JAS.REJECT_REASON "
									+ "from JOURN_APP_USER_ACCMODIFY_BSS_T jas "
									+ "where JAS.ID_SRV=? ")
					.setParameter(1, new Long(number)).getResultList();

			int status = Integer.parseInt(results.get(0)[0].toString());

			result.setStatus(status_map.get(status));

			if (status == 2) {
				AppAttribute atuma = new AppAttribute();
				atuma.setName(AppAttributesClassif.REJECT_REASON.name());
				atuma.setValue(results.get(0)[1] != null ? results.get(0)[1]
						.toString() : "");
				atList.add(atuma);

				result.setAttributes(atList);
			}

			LOGGER.debug("user_modification_acc_result:02");

		} catch (Exception euma) {
	
			LOGGER.error("user_modification_acc_result:Error:", euma);
			throw new GeneralFailure(euma.getMessage());
		}
		return result;
	}

	private AppResult user_modification_cert_result(String number,
			String secret, Long idUserAuth, String IPAddress)
			throws GeneralFailure {

		LOGGER.debug("user_modification_cert_result:01");

		AppResult result = new AppResult();
		List<AppAttribute> atList = new ArrayList<AppAttribute>();

		try {

			List<Object[]> results = em
					.createNativeQuery(
							"select JAS.STATUS, JAS.REJECT_REASON "
									+ "from JOURN_APP_USER_CERTADD_BSS_T jas "
									+ "where JAS.ID_SRV=? ")
					.setParameter(1, new Long(number)).getResultList();

			int status = Integer.parseInt(results.get(0)[0].toString());

			result.setStatus(status_map.get(status));

			if (status == 2) {
				AppAttribute atumc = new AppAttribute();
				atumc.setName(AppAttributesClassif.REJECT_REASON.name());
				atumc.setValue(results.get(0)[1] != null ? results.get(0)[1]
						.toString() : "");
				atList.add(atumc);

				result.setAttributes(atList);
			}

			LOGGER.debug("user_modification_cert_result:02");

	
		} catch (Exception eumc) {
		
			LOGGER.error("user_modification_cert_result:Error:", eumc);
			throw new GeneralFailure(eumc.getMessage());
		}
		return result;
	}

	public void number_secret_valid(String number, String secret, String type)
			throws GeneralFailure {

		LOGGER.debug("number_secret_valid:number:" + number);

		Long numberLong = null;
		String table_name = null;
		try {

			try {
				numberLong = new Long(number);
			} catch (Exception e) {
				throw new GeneralFailure("Ќе верный номер за€вки [" + number
						+ "]! ");
			}

			if (type.equals(AppTypeClassif.SYSTEM_REGISTRATION.name())) {
				table_name = "JOURN_APP_SYSTEM_BSS_T";
			} else if (type.equals(AppTypeClassif.USER_REGISTRATION.name())) {
				table_name = "JOURN_APP_USER_BSS_T";
			} else if (type.equals(AppTypeClassif.USER_ACCESS_ROLES.name())) {
				table_name = "JOURN_APP_ACCESS_BSS_T";
			} else if (type.equals(AppTypeClassif.USER_ACCESS_GROUPS.name())) {
				table_name = "JOURN_APP_ACCESS_GROUPS_BSS_T";
			} else if (type.equals(AppTypeClassif.USER_BLOCK.name())) {
				table_name = "JOURN_APP_BLOCK_BSS_T";
			} else if (type.equals(AppTypeClassif.SYSTEM_MODIFICATION.name())) {
				table_name = "JOURN_APP_SYSTEM_MODIFY_BSS_T";
			} else if (type.equals(AppTypeClassif.USER_MODIFICATION.name())) {
				table_name = "JOURN_APP_USER_MODIFY_BSS_T";
			} else if (type.equals(AppTypeClassif.USER_MODIFICATION_ACC.name())) {
				table_name = "JOURN_APP_USER_ACCMODIFY_BSS_T";
			} else if (type
					.equals(AppTypeClassif.USER_MODIFICATION_CERT.name())) {
				table_name = "JOURN_APP_USER_CERTADD_BSS_T";
			}

			String secretVal = (String) em
					.createNativeQuery(
							"select JAS.SECRET " + "from " + table_name
									+ " jas " + "where JAS.ID_SRV=? ")
					.setParameter(1, numberLong).getSingleResult();

			if (secretVal != null) {
				if (!secretVal.equals(secret)) {
					throw new GeneralFailure("Ќе верный секрет за€вки ["
							+ secret + "]! ");
				}
			}
			// если у нас в базе секрет не установлен,
			// то в запросах допускаем любые значени€ секретов секретами с

		} catch (NoResultException ex) {
			LOGGER.error("number_secret_valid:NoResultException");
			throw new GeneralFailure("Ќе верный номер за€вки[" + number + "]! ");
		} catch (GeneralFailure e) {
			LOGGER.error("number_secret_valid:Error:", e);
			throw e;
		} catch (Exception e2) {
			LOGGER.error("number_secret_valid:Error:" + e2);
			throw new GeneralFailure(e2.getMessage());
		}
	}

	public Long principal_exist(String principal) throws GeneralFailure {

		Long idUser = null;

		try {
			principal = principal.replaceAll(" ", "").toUpperCase();

			LOGGER.debug("principal_exist:principal:" + principal);

			idUser = ((java.math.BigDecimal) em
					.createNativeQuery(
							"select AU.ID_SRV "
									+ "from "
									+ "AC_USERS_KNL_T au "
									+ "where AU.CERTIFICATE=? "
									+ "and (AU.START_ACCOUNT is null or au.START_ACCOUNT <= sysdate) "
									+ "and (AU.START_ACCOUNT is null or au.START_ACCOUNT > sysdate) "
									+
									// "and AU.STATUS != 2 ")
									"and AU.STATUS = 1 ")
					.setParameter(1, principal).getSingleResult()).longValue();

			LOGGER.debug("principal_exist:idUser:" + idUser);

			return idUser;

		} catch (NoResultException ex) {
			LOGGER.error("principal_exist:NoResultException");
			throw new GeneralFailure("ѕользователь не идентифицирован! ");
		} catch (Exception e) {
			LOGGER.error("principal_exist:Error:", e);
			throw new GeneralFailure(e.getMessage());
		}
	}


}
