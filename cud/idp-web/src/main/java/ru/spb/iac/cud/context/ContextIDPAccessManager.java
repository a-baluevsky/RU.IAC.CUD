package ru.spb.iac.cud.context;

import java.util.List;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.idp.core.access.IDPAccessManagerLocal;

 public class ContextIDPAccessManager {

	static Context ctx;
	IDPAccessManagerLocal iml = null;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ContextIDPAccessManager.class);

	public static void initContext(String jboss_jndi_port) {
		try {
			LOGGER.debug("initContext:jboss_jndi_port:" + jboss_jndi_port);

			ctx = new InitialContext();

		} catch (Exception e) {
			LOGGER.error("initContext:error:", e);
		}
	}

	public ContextIDPAccessManager() {
		try {

			this.iml = (IDPAccessManagerLocal) ctx
					.lookup("java:global/AuthServices/IDPAccessManager!ru.spb.iac.cud.idp.core.access.IDPAccessManagerLocal");

		} catch (Exception e) {
			LOGGER.error("error:", e);
		}
	}

	public Map<String, String> attributes(String login) throws Exception {
		return iml.attributes(login);
	}

	public List<String> rolesCodes(String login, String domain)
			throws Exception {
		return iml.rolesCodes(login, domain);
	}

	public List<String> resources(String login, String domain) throws Exception {
		return iml.resources(login, domain);
	}

	public void saveTokenID(String tokenID, String userID)
			throws Exception {
		 iml.saveTokenID(tokenID, userID);
	}
	
	

}
