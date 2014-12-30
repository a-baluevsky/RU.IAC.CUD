package ru.spb.iac.cud.context.eis;

import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.core.UtilManagerLocal;
import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.items.GroupsData;
import ru.spb.iac.cud.items.Resource;
import ru.spb.iac.cud.items.ResourcesData;
import ru.spb.iac.cud.items.Role;
import ru.spb.iac.cud.items.UsersData;

 public class ContextUtilManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContextUtilManager.class);

	static Context ctx;
	UtilManagerLocal aml = null;

	static {
		try {
			ctx = new InitialContext();

		} catch (Exception e) {
			LOGGER.error("error",e);
		}
	}

	

	public ContextUtilManager() {
		try {
			this.aml = (UtilManagerLocal) ctx
					.lookup("java:global/AuthServices/EisUtilManager!ru.spb.iac.cud.core.UtilManagerLocal");

		} catch (Exception e) {
			LOGGER.error("ContextUtilManager:error:", e);
		}
	}

	public UsersData users_data(String idIS, List<String> uidsUsers,
			String category, List<String> rolesCodes, List<String> groupsCodes,
			Integer start, Integer count, List<String> settings,
			Long idUserAuth, String IPAddress) throws GeneralFailure {
		LOGGER.debug("users_data");

		return aml.users_data(idIS, uidsUsers, category, rolesCodes,
				groupsCodes, start, count, settings, idUserAuth, IPAddress);
	}

	public GroupsData groups_data(String idIS, List<String> groupsCodes,
			String category, List<String> rolesCodes, Integer start,
			Integer count, Map<String, String> settings, Long idUserAuth,
			String IPAddress) throws GeneralFailure {
		LOGGER.debug("groups_data");

		return aml.groups_data(idIS, groupsCodes, category, rolesCodes, start,
				count, settings, idUserAuth, IPAddress);
	}

	public ResourcesData resources_data(String idIS,
			List<String> resourcesCodes, String category,
			List<String> rolesCodes, Integer start, Integer count,
			Map<String, String> settings, Long idUserAuth, String IPAddress)
			throws GeneralFailure {
		LOGGER.debug("sys_resources");

		return aml.resources_data(idIS, resourcesCodes, category, rolesCodes,
				start, count, settings, idUserAuth, IPAddress);
	}

	public List<Resource> resources_data_subsys(String idIS, String category,
			Long idUserAuth, String IPAddress) throws GeneralFailure {
		return aml.resources_data_subsys(idIS, category, idUserAuth, IPAddress);

	}

	public List<Role> roles_data(String idIS, 
			String category,
			Long idUserAuth, String IPAddress)
			throws GeneralFailure {

		LOGGER.debug("roles_data");

		return aml.roles_data(idIS, category, idUserAuth, IPAddress);
	}
	
	
}
