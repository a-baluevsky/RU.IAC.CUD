package ru.spb.iac.cud.context;

import javax.naming.Context;
import javax.naming.InitialContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.core.AccessManagerLocal;
import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.exceptions.InvalidCredentials;
import ru.spb.iac.cud.exceptions.RevokedCertificate;
import ru.spb.iac.cud.items.AuthMode;

 public class ContextAccessSTSManager {

	static Context ctx;
	AccessManagerLocal aml = null;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ContextAccessSTSManager.class);

	
	public static void initContext(String jboss_jndi_port) {
		try {
			LOGGER.debug("initContext:jboss_jndi_port:" + jboss_jndi_port);

			ctx = new InitialContext();

		} catch (Exception e) {
			LOGGER.error("initContext:error:", e);
		}
	}

	public ContextAccessSTSManager() {
		try {
			this.aml = (AccessManagerLocal) ctx
					.lookup("java:global/AuthServices/AccessManager!ru.spb.iac.cud.core.AccessManagerLocal");

		} catch (Exception e) {
			LOGGER.error("initContext:error:", e);
		}
	}

	public String authenticate_cert_sn(String sn, String IPAddress,
			String codeSys) throws GeneralFailure, InvalidCredentials,
			RevokedCertificate {
		LOGGER.debug("authenticate_cert_sn");
		return aml.authenticate_cert_sn(sn, AuthMode.WEB_SERVICES, IPAddress,
				codeSys);
	}

	public String authenticate_login(String login, String password,
			AuthMode authMode, String IPAddress, String codeSys)
			throws GeneralFailure, InvalidCredentials {
		LOGGER.debug("authenticate_login");
		return aml.authenticate_login(login, password, authMode, IPAddress,
				codeSys);
	}

	public Long authenticate_login_obo(String login, AuthMode authMode,
			String IPAddress, String codeSys) throws GeneralFailure {
		// пока не используется
		// !!!
		// отказались от передачи токена целиком
		// передаём token_id
		// а в нём указан uid пользователя
		LOGGER.debug("authenticate_login_obo");
		return aml.authenticate_login_obo(login, authMode, IPAddress, codeSys);
	}

	public String authenticate_uid_obo(String uid, AuthMode authMode,
			String IPAddress, String codeSys) throws GeneralFailure {
		LOGGER.debug("authenticate_uid_obo");
		return aml.authenticate_uid_obo(uid, authMode, IPAddress, codeSys);
	}

	public void sys_audit_public(Long idServ, String inp_param, String result,
			String ip_adr, Long idUser, String codeSys) {
		LOGGER.debug("sys_audit_public");
		aml.sys_audit_public(idServ, inp_param, result, ip_adr, idUser, null,
				codeSys);
	}

}
