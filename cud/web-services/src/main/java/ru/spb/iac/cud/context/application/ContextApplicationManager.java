package ru.spb.iac.cud.context.application;

import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.core.app.ApplicationManagerLocal;
import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.items.app.AppAttribute;
import ru.spb.iac.cud.items.app.AppAccept;

 public class ContextApplicationManager {

	static Context ctx;
	ApplicationManagerLocal aml = null;

	private static final Logger LOGGER = LoggerFactory.getLogger(ContextApplicationManager.class);

	static {
		try {
			ctx = new InitialContext();

		} catch (Exception e) {
			LOGGER.error("error",e);
		}
	}

	
	public ContextApplicationManager() {
		try {
			this.aml = (ApplicationManagerLocal) ctx
					.lookup("java:global/AuthServices/ApplicationManager!ru.spb.iac.cud.core.app.ApplicationManagerLocal");

		} catch (Exception e) {
			LOGGER.error("ContextApplicationManager:error:", e);
		}
	}

	public AppAccept system_registration(List<AppAttribute> attributes,
			Long idUserAuth, String IPAddress) throws GeneralFailure {

		LOGGER.debug("system_registration");

		return aml.system_registration(attributes, null, idUserAuth, IPAddress);
	}

	public AppAccept user_registration(List<AppAttribute> attributes,
			Long idUserAuth, String IPAddress) throws GeneralFailure {

		LOGGER.debug("user_registration");

		return aml.user_registration(attributes, null, idUserAuth, IPAddress);
	}

	public AppAccept access_roles(String modeExec, String loginUser,
			String codeSystem, List<String> codesRoles, Long idUserAuth,
			String IPAddress) throws GeneralFailure {

		LOGGER.debug("access_roles");
		return aml.access_roles(modeExec, loginUser, codeSystem, codesRoles,
				null, idUserAuth, IPAddress);
	}

	public AppAccept access_groups(String modeExec, String loginUser,
			String codeSystem, List<String> codesGroups, Long idUserAuth,
			String IPAddress) throws GeneralFailure {

		LOGGER.debug("access_groups");

		return aml.access_groups(modeExec, loginUser, codeSystem, codesGroups,
				null, idUserAuth, IPAddress);
	}

	public AppAccept block(String modeExec, String loginUser,
			String blockReason, Long idUserAuth, String IPAddress)
			throws GeneralFailure {

		LOGGER.debug("block");

		return aml.block(modeExec, loginUser, blockReason, null, idUserAuth,
				IPAddress);
	}

	public AppAccept system_modification(String codeSystem,
			List<AppAttribute> attributes, Long idUserAuth, String IPAddress)
			throws GeneralFailure {

		return aml.system_modification(codeSystem, attributes, null,
				idUserAuth, IPAddress);
	}

	public AppAccept user_modification(String loginUser,
			List<AppAttribute> attributes, Long idUserAuth, String IPAddress)
			throws GeneralFailure {

		return aml.user_modification(loginUser, attributes, null, idUserAuth,
				IPAddress);
	}

	public AppAccept user_identity_modification(String loginUser, String login,
			String password, Long idUserAuth, String IPAddress)
			throws GeneralFailure {

		return aml.user_identity_modification(loginUser, login, password,
				idUserAuth, IPAddress);
	}

	public AppAccept user_cert_modification(String modeExec, String loginUser,
			String certBase64, Long idUserAuth, String IPAddress)
			throws GeneralFailure {

		return aml.user_cert_modification(modeExec, loginUser, certBase64,
				idUserAuth, IPAddress);
	}

	public AppAccept user_dep_modification(String loginUser,
			List<AppAttribute> attributes, Long idUserAuth, String IPAddress)
			throws GeneralFailure {

		return aml.user_dep_modification(loginUser, attributes, null,
				idUserAuth, IPAddress);
	}

}
