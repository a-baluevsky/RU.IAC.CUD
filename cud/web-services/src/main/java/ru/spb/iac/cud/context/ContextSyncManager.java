package ru.spb.iac.cud.context;

import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.core.SyncManagerLocal;
import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.items.Function;
import ru.spb.iac.cud.items.Group;
import ru.spb.iac.cud.items.Resource;
import ru.spb.iac.cud.items.Role;

 public class ContextSyncManager {

	static Context ctx;
	SyncManagerLocal aml = null;
	private static final Logger LOGGER = LoggerFactory.getLogger(ContextSyncManager.class);

	static {
		try {
			ctx = new InitialContext();

		} catch (Exception e) {
			LOGGER.error("error",e);
		}
	}

	
	public ContextSyncManager() {
		try {

			this.aml = (SyncManagerLocal) ctx
					.lookup("java:global/AuthServices/SyncManager!ru.spb.iac.cud.core.SyncManagerLocal");

		} catch (Exception e) {
			LOGGER.error("ContextSyncManager:error:", e);
		}
	}

	public void sync_roles(String idIS, List<Role> roles, String modeExec,
			Long idUserAuth, String IPAddress) throws GeneralFailure {
		LOGGER.debug("sync_roles");

		aml.sync_roles(idIS, roles, modeExec, idUserAuth, IPAddress);
	}

	public void sync_functions(String idIS, List<Function> functions,
			String modeExec, Long idUserAuth, String IPAddress)
			throws GeneralFailure {
		LOGGER.debug("sync_functions");

		aml.sync_functions(idIS, functions, modeExec, idUserAuth, IPAddress);
	}

	public List<Role> is_roles(String idIS, Long idUserAuth, String IPAddress)
			throws GeneralFailure {

		LOGGER.debug("is_roles");

		return aml.is_roles(idIS, idUserAuth, IPAddress);
	}

	public List<Function> is_functions(String idIS, Long idUserAuth,
			String IPAddress) throws GeneralFailure {

		LOGGER.debug("is_functions");

		return aml.is_functions(idIS, idUserAuth, IPAddress);
	}

	public void sync_groups(String idIS, List<Group> groups, String modeExec,
			Long idUserAuth, String IPAddress) throws GeneralFailure {
		LOGGER.debug("sync_groups");

		aml.sync_groups(idIS, groups, modeExec, idUserAuth, IPAddress);
	}

	public void sync_groups_roles(String idIS, List<String> codesGroups,
			List<String> codesRoles, String modeExec, Long idUserAuth,
			String IPAddress) throws GeneralFailure {
		LOGGER.debug("sync_groups_roles");

		aml.sync_groups_roles(idIS, codesGroups, codesRoles, modeExec,
				idUserAuth, IPAddress);
	}

	public void sync_resources(String idIS, List<Resource> resources,
			String modeExec, Long idUserAuth, String IPAddress)
			throws GeneralFailure {
		LOGGER.debug("sync_resources");

		aml.sync_resources(idIS, resources, modeExec, idUserAuth, IPAddress);
	}

	public void sync_resources_roles(String idIS, List<String> codesResources,
			List<String> codesRoles, String modeExec, Long idUserAuth,
			String IPAddress) throws GeneralFailure {
		LOGGER.debug("sync_resources_roles");

		aml.sync_resources_roles(idIS, codesResources, codesRoles, modeExec,
				idUserAuth, IPAddress);
	}

	
}
