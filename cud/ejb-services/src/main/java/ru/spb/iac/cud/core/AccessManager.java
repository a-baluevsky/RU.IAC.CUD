package ru.spb.iac.cud.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.util.Collection;
import java.util.HashMap; import java.util.Map;
import java.util.List;
import java.util.Properties;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;

import ru.spb.iac.cud.core.util.CUDConstants;
import ru.spb.iac.cud.core.util.HashPassword;
import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.exceptions.InvalidCredentials;
import ru.spb.iac.cud.exceptions.RevokedCertificate;
import ru.spb.iac.cud.items.AuditFunction;
import ru.spb.iac.cud.items.AuthMode;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * EJB для аутентификации и аудита
 * 
 * @author bubnov
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
 public class AccessManager implements AccessManagerLocal, AccessManagerRemote {

	@PersistenceContext(unitName = "AuthServices")
	EntityManager em;

	

	private static final Logger LOGGER = LoggerFactory.getLogger(AccessManager.class);

	private static String reestr_path = System
			.getProperty("jboss.server.config.dir")
			+ "/"
			+ "crl_reestr.properties";

	public AccessManager() {
	}

	/**
	 * аутентификация пользователя по логин/паролю
	 */
	public String authenticate_login(String login, String password,
			AuthMode authMode, String IPAddress, String codeSys)
			throws GeneralFailure, InvalidCredentials {

		LOGGER.debug("authenticate_login:login:" + login);

		 
		
		Object[] dataUser = null;

		Long idUser = null;
		String loginUser = null;

		Long authModeValue = null;

		if (authMode.equals(AuthMode.WEB_SERVICES)) {
			authModeValue = 2L;
		} else if (authMode.equals(AuthMode.HTTP_REDIRECT)) {
			authModeValue = 97L;
		} else if (authMode.equals(AuthMode.HTTP_REDIRECT_EXT_AUTH_OPEN)) {
			authModeValue = 99L;
		} else if (authMode.equals(AuthMode.HTTP_REDIRECT_EXT_AUTH_ENCRYPT)) {
			authModeValue = 79L;
		}

		try {

			if (authModeValue.equals(78L)) {
				LOGGER.debug("authenticate:01");

				dataUser = (Object[]) em
						.createNativeQuery(
								"select AU.ID_SRV, AU.login, AU.PASSWORD_ "
										+ "from "
										+ "AC_USERS_KNL_T au "
										+ "where AU.LOGIN=? "
										+ "and (AU.START_ACCOUNT is null or au.START_ACCOUNT <= sysdate) "
										+ "and (AU.START_ACCOUNT is null or au.START_ACCOUNT > sysdate) "
										+ "and AU.STATUS = 1 ")
						.setParameter(1, login).getSingleResult();

				boolean matched = HashPassword.validatePassword(
						(dataUser[2] != null ? dataUser[2].toString() : ""),
						password);

				if (!matched) {
					throw new NoResultException();
				}
				idUser = ((java.math.BigDecimal) dataUser[0]).longValue();
				loginUser = dataUser[1].toString();
				LOGGER.debug("authenticate:02");

			} else {

				LOGGER.debug("authenticate:03");

				dataUser = (Object[]) em
						.createNativeQuery(
								"select AU.ID_SRV, AU.login "
										+ "from "
										+ "AC_USERS_KNL_T au "
										+ "where AU.LOGIN=? "
										+ "and AU.PASSWORD_=? "
										+ "and (AU.START_ACCOUNT is null or au.START_ACCOUNT <= sysdate) "
										+ "and (AU.START_ACCOUNT is null or au.START_ACCOUNT > sysdate) "
										+ "and AU.STATUS = 1 ")
						.setParameter(1, login).setParameter(2, password)
						.getSingleResult();

				idUser = ((java.math.BigDecimal) dataUser[0]).longValue();
				loginUser = dataUser[1].toString();

				LOGGER.debug("authenticate:04");
			}

			sys_audit(authModeValue, "login:" + login + "; passw:***", "true",
					IPAddress, idUser, codeSys);

			return loginUser;

		} catch (NoResultException ex) {

			sys_audit(authModeValue, "login:" + login + "; passw:" + password,
					"false", IPAddress, null, codeSys);

			LOGGER.debug("authenticate:NoResultException");
			throw new InvalidCredentials("Учетной записи нет в системе!");
		} catch (Exception e) {

			sys_audit(authModeValue, "login:" + login + "; passw:***", "error",
					IPAddress, idUser, codeSys);

			LOGGER.error("authenticate:Error:", e);
			throw new GeneralFailure(e.getMessage());
		}
	}

	/**
	 * аутентификация пользователя по токену от другой системы
	 * @param login логин пользователя
	 */
	public Long authenticate_login_obo(String login, AuthMode authMode,
			String IPAddress, String codeSys) throws GeneralFailure {

		// !!!
		// пока метод не используется
		// !!!
		// отказались от передачи токена целиком
		// передаём token_id
		// а в нём указан uid пользователя

		LOGGER.debug("aauthenticate_login:login_obo:" + login);

		Long idUser = null;
		Long authModeValue = null;

		// auth/Mode.equals(AuthM/ode.WEB_SER/VICES)
		authModeValue = 82L;

		try {

			LOGGER.debug("authenticate_login_obo:01");
			idUser = ((java.math.BigDecimal) em
					.createNativeQuery(
							"select AU.ID_SRV "
									+ "from "
									+ "AC_USERS_KNL_T au "
									+ "where AU.LOGIN=? "
									+ "and (AU.START_ACCOUNT is null or au.START_ACCOUNT <= sysdate) "
									+ "and (AU.START_ACCOUNT is null or au.START_ACCOUNT > sysdate) "
									+ "and AU.STATUS = 1 ")
					.setParameter(1, login).getSingleResult()).longValue();

			LOGGER.debug("authenticate_login_obo:idUser:"
					+ idUser);

			sys_audit(authModeValue, "login:" + login + "; passw:***", "true",
					IPAddress, idUser, codeSys);

			return idUser;

		} catch (NoResultException ex) {

			sys_audit(authModeValue, "login:" + login + "; ", "false",
					IPAddress, null, codeSys);

			LOGGER.debug("authenticate_login_obo:NoResultException");
			throw new GeneralFailure("Учетной записи нет в системе!");
		} catch (Exception e) {

			sys_audit(authModeValue, "login:" + login + "; passw:***", "error",
					IPAddress, idUser, codeSys);

			LOGGER.error("authenticate_login_obo:Error:", e);
			throw new GeneralFailure(e.getMessage());
		}
	}

	/**
	 * аутентификация пользователя по токену от другой системы
	 * @param uid уид пользователя
	 */
	public String authenticate_uid_obo(String uid, AuthMode authMode,
			String IPAddress, String codeSys) throws GeneralFailure {

		LOGGER.debug("authenticate_uid_obo:" + uid);

		String loginUser = null;
		Long authModeValue = null;

		// authMo/de.equa/ls(AuthMode.W/EB_SERVICES)
		authModeValue = 82L;

		if (uid == null || uid.trim().isEmpty()) {
			throw new GeneralFailure("Учетной записи нет в системе!");
		}

		try {

			LOGGER.debug("authenticate_login_obo:01");

			loginUser = ((String) em
					.createNativeQuery(
							"select AU.LOGIN "
									+ "from "
									+ "AC_USERS_KNL_T au "
									+ "where AU.ID_SRV=? "
									+ "and (AU.START_ACCOUNT is null or au.START_ACCOUNT <= sysdate) "
									+ "and (AU.START_ACCOUNT is null or au.START_ACCOUNT > sysdate) "
									+ "and AU.STATUS = 1 ")
					.setParameter(1, new Long(uid)).getSingleResult());

			LOGGER.debug("authenticate_login_obo:loginUser:"
					+ loginUser);

			sys_audit(authModeValue, "uid:" + uid + "; passw:***", "true",
					IPAddress, new Long(uid), codeSys);

			return loginUser;

		} catch (NoResultException ex) {

			sys_audit(authModeValue, "uid:" + uid + "; ", "false", IPAddress,
					null, codeSys);

			LOGGER.debug("authenticate_uid_obo:NoResultException");
			throw new GeneralFailure("Учетной записи нет в системе!");
		} catch (Exception e) {

			sys_audit(authModeValue, "uid:" + uid + "; passw:***", "error",
					IPAddress, new Long(uid), codeSys);

			LOGGER.error("authenticate_uid_obo:Error:", e);
			throw new GeneralFailure(e.getMessage());

		}
	}
	/**
	 * аутентификация пользователя по сертификату
	 * @param sn серийный номер сертификата пользователя
	 */
	public String authenticate_cert_sn(String sn, AuthMode authMode,
			String IPAddress, String codeSys) throws GeneralFailure,
			InvalidCredentials, RevokedCertificate {

		LOGGER.debug("authenticate_cert_sn:01");

		Object[] dataUser = null;

		Long idUser = null;
		String loginUser = null;
		Long authModeValue = null;

		if (authMode.equals(AuthMode.WEB_SERVICES)) {
			authModeValue = 62L;
		} else if (authMode.equals(AuthMode.HTTP_REDIRECT)) {
			authModeValue = 98L;
		}

		if (sn == null) {
			sys_audit(2L, "sn:" + sn, "error:Serial Number is Empty!",
					IPAddress, null, codeSys);
			throw new GeneralFailure("Serial Number is Empty!");
		}

		sn = sn.replaceAll(" ", "").toUpperCase();

		try {

			LOGGER.debug("authenticate_cert_sn:02+:" + sn);

			// с учётом множественности сертификатов
			dataUser = ((Object[]) em
					.createNativeQuery(
							"select AU.ID_SRV, AU.login "
									+ "from "
									+ "AC_USERS_KNL_T au, "
									+ "AC_USERS_CERT_BSS_T auc "
									+ "where (UPPER(AU.CERTIFICATE)= UPPER(:certNum) or UPPER(AUC.CERT_NUM) = UPPER(:certNum)  ) "
									+ "and (AU.START_ACCOUNT is null or au.START_ACCOUNT <= sysdate) "
									+ "and (AU.START_ACCOUNT is null or au.START_ACCOUNT > sysdate) "
									+ "and AU.STATUS = 1 "
									+ "and AU.ID_SRV = AUC.UP_USER(+) ")
					.setParameter("certNum", sn).getSingleResult());

			idUser = ((java.math.BigDecimal) dataUser[0]).longValue();
			loginUser = dataUser[1].toString();

			LOGGER.debug("authenticate_cert_sn:03+:" + loginUser);

			if (isRevoked(get_reestr("curr_crl"), sn)) {
				throw new RevokedCertificate("Сертификат отозван!");
			}

			LOGGER.debug("authenticate_cert_sn:04");

			LOGGER.debug("authenticate_cert_sn:06");

			sys_audit(authModeValue, "sn:" + sn, "true", IPAddress, idUser,
					codeSys);

			LOGGER.debug("authenticate_cert_sn:07");

			return loginUser;

		} catch (NoResultException ex) {

			sys_audit(authModeValue, "sn:" + sn, "false", IPAddress, null,
					codeSys);

			LOGGER.debug("authenticate_cert_sn:NoResultException");
			throw new InvalidCredentials("Учетной записи нет в системе!");
		} catch (RevokedCertificate er) {

			sys_audit(authModeValue, "sn:" + sn, "false, RevokedCertificate",
					IPAddress, null, codeSys);

			LOGGER.debug("authenticate_cert_sn:RevokedCertificate");
			throw er;

		} catch (Exception e) {

			sys_audit(authModeValue, "sn:" + sn, "error", IPAddress, idUser,
					codeSys);

			LOGGER.error("authenticate_cert_sn:Error+:", e);

			throw new GeneralFailure(e.getMessage());
		}
	}

	/**
	 * аудит действий пользователя
	 */
	public void audit_pro(String codeSys, String login,
			List<AuditFunction> userFunctions, Long idUserAuth, String IPAddress)
			throws GeneralFailure {

		LOGGER.debug("audit:01");

		Map<String, Long> actcl = new HashMap<String, Long>();
		Long idFunc = null;
		Long idIS = null;
		Long idUserSubject = null;

		if (userFunctions == null || userFunctions.isEmpty()) {
			LOGGER.debug("audit:return");
			return;
		}

		try {

			if (codeSys == null) {
				LOGGER.debug("audit:System Code is null");
				throw new GeneralFailure("System Code is null");
			}
			if (!codeSys.startsWith(CUDConstants.armPrefix)&&!codeSys.startsWith(CUDConstants.subArmPrefix)) {
				LOGGER.debug("audit:System Code is incorrect");
				throw new GeneralFailure("System Code is incorrect");
			}
			if (login == null) {
				LOGGER.debug("audit:User login is nul");
				throw new GeneralFailure("User login is null");
			}

			idIS = get_id_is(codeSys);
			// решили определять пользователей извне ЦУД по их ИД, а не логинам
			
			idUserSubject = new Long(login);

			LOGGER.debug("audit:02:idIS:" + idIS);
			LOGGER.debug("audit:03:idUserSubject:" + idUserSubject);

			List<Object[]> lo = em
					.createNativeQuery(
							"select ACT.SIGN_OBJECT, ACT.ID_SRV "
									+ "from ACTIONS_BSS_T act, "
									+ "AC_IS_BSS_T app "
									+ "where APP.ID_SRV = ACT.UP_IS "
									+ "and APP.ID_SRV=?").setParameter(1, idIS)
					.getResultList();
			LOGGER.debug("audit:04");

			for (Object[] objectArray : lo) {
				actcl.put(
						objectArray[0] != null ? objectArray[0].toString() : "",
						objectArray[1] != null ? new Long(objectArray[1]
								.toString()) : -1L);
			}

			
			//для отчёта аудита вместе с ролями
			//сохраняем имеющиеся у пользователя роли
			
			String idRec =(String) em.createNativeQuery("select to_char(ACTIONS_LOG_ROLES_KNL_SEQ.nextval) sgnc from dual").getSingleResult();
		      
			em.createNativeQuery(
					"insert into ACTIONS_LOG_ROLES_KNL_T(ID_SRV, ROLES_NAMES, ROLES_CODES, created)  " + 
					"select  :idRec ,  " + 
					"t1.names, t1.codes, sysdate " + 
					"from( " + 
					"select " + 
					"     rtrim( xmlserialize(content  extract( xmlagg(xmlelement(\"e\", t1_roles. role_name ||chr(13)) order by role_name)  , '//text()' ) )  , ','  ) names, " + 
					"        rtrim( xmlserialize(content  extract( xmlagg(xmlelement(\"e\", t1_roles. role_code ||chr(13)) order by  role_name)  , '//text()' ) )  , ','  ) codes, " + 
					"          sysdate " + 
					"from( " + 
					"select  ROL.FULL_ role_name , ROL.SIGN_OBJECT role_code " + 
					"                                        from  AC_IS_BSS_T sys,  " + 
					"                                                 AC_ROLES_BSS_T rol,  " + 
					"                                                 AC_USERS_LINK_KNL_T url,  " + 
					"                                                 AC_USERS_KNL_T AU,  " + 
					"                                                 AC_SUBSYSTEM_CERT_BSS_T subsys,  " + 
					"                                                 LINK_GROUP_USERS_ROLES_KNL_T lugr,  " + 
					"                                                 LINK_GROUP_USERS_USERS_KNL_T lugu  " + 
					"                                        where (SYS.SIGN_OBJECT= :codeSys or  SUBSYS.SUBSYSTEM_CODE= :codeSys )  " + 
					"                                              and (ROL.ID_SRV = URL.UP_ROLES or ROL.ID_SRV = LUGR.UP_ROLES )  " + 
					"                                              and LUGU.UP_GROUP_USERS= LUGR.UP_GROUP_USERS(+)  " + 
					"                                              and LUGU.UP_USERS(+)  = AU.ID_SRV  " + 
					"                                              and URL.UP_USERS(+)  = AU.ID_SRV  " + 
					"                                              and ROL.UP_IS=sys.ID_SRV  " + 
					"                                              and AU. ID_SRV = :idUserSubject " + 
					"                                              and  SUBSYS.UP_IS(+) =SYS.ID_SRV  " + 
					"                                        group by  ROL.FULL_, ROL.SIGN_OBJECT  " + 
					"                                        ) t1_roles) t1")
					.setParameter("idRec", new Long(idRec))
					.setParameter("idUserSubject", idUserSubject)
					.setParameter("codeSys", codeSys)
				 	.executeUpdate();
			
			for (AuditFunction func : userFunctions) {
				idFunc = actcl.get(func.getCodeFunction());
				LOGGER.debug("audit:05:func.getIdFunction():"
						+ func.getCodeFunction());
				LOGGER.debug("audit:06:idFunc:" + idFunc);

				if (func.getDetailsFunction() != null
						&& func.getDetailsFunction().length() > 500) {
					throw new GeneralFailure(
							"Field length 'DetailsFunction' is more than 500 characters! ");
				}

				if (idFunc != null && func.getDateFunction() != null) {

					// насчёт func.getDateFunction подумать:
					// выдавать исключение или просто эту запись не вставлять

					/*
					 * if(func.getDateFunction()==null){ throw new
					 * GeneralFailure("Отсутствует код роли!"); }
					 */

					em.createNativeQuery(
							"insert into ACTIONS_LOG_KNL_T(ID_SRV,  UP_ACTIONS, UP_USERS, DATE_ACTION, DETAILS_ACTION, created, UP_LOG_ROLES) "
									+ "values(ACTIONS_LOG_KNL_SEQ.nextval, ?, ?, ?, ?, sysdate, ?) ")
							.setParameter(1, idFunc)
							.setParameter(2, idUserSubject)
							.setParameter(3, func.getDateFunction(),
									TemporalType.TIMESTAMP)
							.setParameter(4, func.getDetailsFunction())
							.setParameter(5, new Long(idRec))
							.executeUpdate();
				}
			}

			sys_audit(6L, "idIS:" + idIS + "; size of funct_list: "
					+ userFunctions.size(), "true", IPAddress, idUserAuth,
					codeSys);

		} catch (Exception e) {
			sys_audit(6L, "idIS:" + idIS + "; size of funct_list: "
					+ userFunctions.size(), "error", IPAddress, idUserAuth,
					codeSys);

			LOGGER.error("audit:Error:", e);
			throw new GeneralFailure(e.getMessage());
		}
	}

	/**
	 * изменение пароля пользователя
	 */
	public void change_password(String login, String password,
			String new_password, String IPAddress) throws GeneralFailure,
			InvalidCredentials {

		// !!!
		// пока метод не используется

		LOGGER.debug("change_password:login:" + login);

		Long idUser = null;
		try {
			LOGGER.debug("change_password:01");
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

			LOGGER.debug("change_password:idUser:" + idUser);

			if (new_password == null || new_password.trim().equals("")) {
				throw new GeneralFailure("Некорректный новый пароль!");
			}

			em.createNativeQuery(
					"UPDATE AC_USERS_KNL_T au " + "SET au.PASSWORD_=? "
							+ "WHERE au.ID_SRV=? ")
					.setParameter(1, new_password).setParameter(2, idUser)
					.executeUpdate();

			sys_audit(22L, "login:" + login + "; passw:***", "true", IPAddress,
					idUser, null);

		} catch (NoResultException ex) {

			sys_audit(22L, "login:" + login + "; passw:***", "false",
					IPAddress, null, null);

			LOGGER.debug("change_password:NoResultException");
			throw new InvalidCredentials("Учетной записи нет в системе!");

		} catch (GeneralFailure e) {

			sys_audit(22L, "login:" + login + "; passw:***",
					"error: empty new password", IPAddress, idUser, null);

			LOGGER.error("change_password:Error:", e);
			throw e;

		} catch (Exception e) {

			

			LOGGER.error("change_password:Error:", e);
			throw new GeneralFailure(e.getMessage());
		}
	}

	/**
	 * проверка корректности ид системы
	 */
	public void is_exist(String idIS) throws GeneralFailure {

		LOGGER.debug("AuthManager:is_exist:idIS:" + idIS);

		try {

			em.createNativeQuery(
					"select APP.ID_SRV " + "from AC_IS_BSS_T app "
							+ "where APP.SIGN_OBJECT=?").setParameter(1, idIS)
					.getSingleResult();

		} catch (NoResultException ex) {
			LOGGER.debug("createAuth:NoResultException");
			throw new GeneralFailure("Информационная система не определёна!");
		} catch (Exception e) {
			LOGGER.debug("is_exist:Error:", e);
			throw new GeneralFailure(e.getMessage());
		}
	}

	/**
	 * аудит вызовов сервисов (кроме аутентификации систем и logout)
	 */
	private void sys_audit(Long idServ, String inp_param, String result,
			String ip_adr, Long idUser, String codeSys) {
		LOGGER.debug("sys_audit");

		Long idSys = null;
		Long idGrSys = null;
		try {

			if (codeSys != null) {

				// ищем код в системах и подсистемах
				if (codeSys.startsWith(CUDConstants.armPrefix)
						|| codeSys.startsWith(CUDConstants.subArmPrefix)) {

					idSys = get_id_is(codeSys);

				} else if (codeSys.startsWith(CUDConstants.groupArmPrefix)) {
					// группы систем

					idGrSys = ((java.math.BigDecimal) em
							.createNativeQuery(
									"select T1.ID_SRV "
											+ "from GROUP_SYSTEMS_KNL_T t1 "
											+ "where T1.GROUP_CODE=? ")
							.setParameter(1, codeSys).getSingleResult())
							.longValue();

				}
			}

			if (idUser != null && !idUser.equals(-1L)) {
				em.createNativeQuery(
						"insert into SERVICES_LOG_KNL_T( "
								+ "ID_SRV,  UP_SERVICES, DATE_ACTION, CREATED, "
								+ "input_param, result_value, ip_address, UP_USERS, "
								+ "UP_SYS, UP_GR_SYS ) "
								+ "values(SERVICES_LOG_KNL_SEQ.nextval , ?, sysdate, sysdate, "
								+ "?, ?, ?, ?, ?, ? ) ")
						.setParameter(1, idServ).setParameter(2, inp_param)
						.setParameter(3, result).setParameter(4, ip_adr)
						.setParameter(5, idUser)
						.setParameter(6, idSys != null ? idSys : "")
						.setParameter(7, idGrSys != null ? idGrSys : "")
						.executeUpdate();
			} else {
				em.createNativeQuery(
						"insert into SERVICES_LOG_KNL_T( "
								+ "ID_SRV,  UP_SERVICES, DATE_ACTION, CREATED, "
								+ "input_param, result_value, ip_address, "
								+ "UP_SYS, UP_GR_SYS ) "
								+ "values(SERVICES_LOG_KNL_SEQ.nextval , ?, sysdate, sysdate, "
								+ "?, ?, ?, ?, ? ) ").setParameter(1, idServ)
						.setParameter(2, inp_param).setParameter(3, result)
						.setParameter(4, ip_adr)
						.setParameter(5, idSys != null ? idSys : "")
						.setParameter(6, idGrSys != null ? idGrSys : "")
						.executeUpdate();
			}
		} catch (Exception e) {
			LOGGER.error("sys_audit:Error:", e);
		}

	}

	/**
	 * аудит вызовов сервисов аутентификации систем и logout
	 */
	public void sys_audit_public(Long idServ, String inp_param, String result,
			String ip_adr, Long idUser, String loginUser, String codeSys) {

		// используется при аутентификации системы и logout

		LOGGER.debug("sys_audit_public:01");
		try {
			// при аудите logout
			// передаётся логин, а не ид пользователя

			if (idUser == null && loginUser != null) {

				try {
					// если по логину не найдём, то get_id_user выдаст ошибку
					// и мы до sys_audit не дойдём
					// поэтому берём get_id_user в try_catch

					idUser = get_id_user(loginUser);

				} catch (Exception e) {
					LOGGER.error("sys_audit_public:02");
				}
			}
			sys_audit(idServ, inp_param, result, ip_adr, idUser, codeSys);

		} catch (Exception e) {
			LOGGER.error("sys_audit_public:Error:", e);
		}

	}

	/**
	 * определение файла с реестром отозванных сертификатов
	 */
	public String get_reestr(String prop_name) {
		Properties properties = new Properties();
		InputStream is = null;
		String result = null;

		LOGGER.debug("get_reestr");

		try {

			File f = new File(reestr_path);

			if (f.exists()) {

				properties.load(is = new FileInputStream(f));

				result = properties.getProperty(prop_name);

				LOGGER.debug("get_reestr:result:" + result);
			}

		} catch (Exception e) {
			LOGGER.error("initTask:error:", e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (Exception e) {
				LOGGER.error("get_reestr:finally:is:error:", e);
			}
		}
		return result;
	}

	/**
	 * проверка сертификата на наличие в реестре отозванных сертификатов
	 */
	private synchronized boolean isRevoked(String curr_crl, String hex) {

		boolean result = false;

		CertificateFactory cf = null;
		InputStream is = null;
		Collection<? extends X509CRL> x509Crls = null;
		File crlFile = null;

		try {

			crlFile = new File(curr_crl);

			LOGGER.debug("isRevoked:01");

			cf = CertificateFactory.getInstance("X.509");

			x509Crls = (Collection<? extends X509CRL>) cf
					.generateCRLs(is = new FileInputStream(crlFile));

			LOGGER.debug("isRevoked:02");

			for (X509CRL crl : x509Crls) {

			
				X509CRLEntry xce = crl.getRevokedCertificate(hex_to_dec(hex));

				if (xce != null) {
					return true;
				}
				LOGGER.debug("isRevoked:revoked:" + (xce == null));
			
			}

			LOGGER.debug("isRevoked:03");

		} catch (Exception e) {
			LOGGER.error("isRevoked:error:", e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (Exception e) {
				LOGGER.error("isRevoked:finally:is:error:", e);
			}
		}

		return result;
	}

	/**
	 * конвертирование серийного номера сертификата в десятичный формат
	 */
	private BigInteger hex_to_dec(String hex) {

		BigInteger bi = null;

		try {

			bi = new BigInteger(hex, 16);

			LOGGER.debug("hex_to_dec:num:" + bi);
		} catch (NumberFormatException e) {
			LOGGER.error("hex_to_dec:Error:", e);
		}
		return bi;

	}

	/**
	 * определение ид системы по её коду
	 */
	private Long get_id_is(String codeSys) throws GeneralFailure {

		Long result = null;

		try {
			result = ((java.math.BigDecimal) em
					.createNativeQuery(
							"select SYS.ID_SRV "
									+ "from  AC_IS_BSS_T sys, "
									+ "AC_SUBSYSTEM_CERT_BSS_T subsys "
									+ "where (SYS.SIGN_OBJECT= :codeSys or  SUBSYS.SUBSYSTEM_CODE= :codeSys) "
									+ "and  SUBSYS.UP_IS(+) =SYS.ID_SRV "
									+ "group by SYS.ID_SRV ")
					.setParameter("codeSys", codeSys).getSingleResult())
					.longValue();

		} catch (NoResultException ex) {
			LOGGER.debug("get_id_is:NoResultException");
			throw new GeneralFailure("System is not defined");
		} catch (Exception e) {
			throw new GeneralFailure(e.getMessage());
		}
		return result;

	}

	/**
	 * определение уид пользователя по его логину
	 */
	private Long get_id_user(String login) throws GeneralFailure {

		LOGGER.debug("get_id_user:login:" + login);

		Long result = null;
		try {
			result = ((java.math.BigDecimal) em
					.createNativeQuery(
							"select AU.ID_SRV "
									+ "from "
									+ "AC_USERS_KNL_T au "
									+ "where AU.LOGIN=? "
									+ "and (AU.START_ACCOUNT is null or au.START_ACCOUNT <= sysdate) "
									+ "and (AU.START_ACCOUNT is null or au.START_ACCOUNT > sysdate) ")
					.setParameter(1, login).getSingleResult()).longValue();

		} catch (NoResultException ex) {

			LOGGER.debug("get_id_user:NoResultException");
			throw new GeneralFailure("User is not defined!");

		} catch (Exception e) {

			LOGGER.error("get_id_user:Error:", e);
			throw new GeneralFailure(e.getMessage());
		}

		return result;
	}

}