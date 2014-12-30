package ru.spb.iac.cud.core;

import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.items.GroupsData;
import ru.spb.iac.cud.items.ISOrganisations;
import ru.spb.iac.cud.items.Resource;
import ru.spb.iac.cud.items.ResourcesData;
import ru.spb.iac.cud.items.Role;
import ru.spb.iac.cud.items.UsersData;

@Remote
public interface UtilManagerRemote {

	public UsersData users_data(String idIS, List<String> uidsUsers,
			String category, List<String> rolesCodes, List<String> groupsCodes,
			Integer start, Integer count, List<String> settings,
			Long idUserAuth, String IPAddress) throws GeneralFailure;

	public ISOrganisations is_organisations(
	String idIS, Integer start, Integer count, List<String> rolesCodes,
			Long idUserAuth, String IPAddress) throws GeneralFailure;

	public GroupsData groups_data(String idIS, List<String> groupsCodes,
			String category, List<String> rolesCodes, Integer start,
			Integer count, Map<String, String> settings, Long idUserAuth,
			String IPAddress) throws GeneralFailure;

	public ResourcesData resources_data(String idIS,
			List<String> resourcesCodes, String category,
			List<String> rolesCodes, Integer start, Integer count,
			Map<String, String> settings, Long idUserAuth, String IPAddress)
			throws GeneralFailure;

	public List<Resource> resources_data_subsys(String idIS, String category,
			Long idUserAuth, String IPAddress) throws GeneralFailure;

	public List<Role> roles_data(String idIS, String category,
			Long idUserAuth, String IPAddress) throws GeneralFailure;
	
	public void is_exist(String idIS) throws GeneralFailure;
}
