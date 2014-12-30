package ru.spb.iac.cud.idp.core;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.context.ContextIDPAccessManager;
import ru.spb.iac.cud.idp.items.CUDRoleGenerator;

 public class RolesManager implements CUDRoleGenerator {

	private String codeSystem;

	private static final Logger LOGGER = LoggerFactory.getLogger(RolesManager.class);

	public List<String> generateRoles(Principal principal) {

		List<String> roles = new ArrayList<String>();
		try {

			 
			 

			String domain = this.codeSystem;

			roles = (new ContextIDPAccessManager()).rolesCodes(
					principal.getName(), domain);

		} catch (Exception e) {
			LOGGER.error("generateRoles:error:", e);
		}
		return roles;
	}

	public List<String> generateResources(Principal principal) {

		List<String> resources = new ArrayList<String>();
		try {

			LOGGER.debug("generateResources:01:" + principal);
			LOGGER.debug("generateResources:02:" + this.codeSystem);

			String domain = this.codeSystem;

			resources = (new ContextIDPAccessManager()).resources(
					principal.getName(), domain);

		} catch (Exception e) {
			LOGGER.debug("generateResources:error:", e);
		}
		return resources;
	}

	public void setSystemCode(String codeSystem) {
		this.codeSystem = codeSystem;

	}
}
