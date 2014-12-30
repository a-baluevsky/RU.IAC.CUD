package ru.spb.iac.cud.core;

import java.util.List;
import javax.ejb.Remote;

import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.items.Function;
import ru.spb.iac.cud.items.Group;
import ru.spb.iac.cud.items.Resource;
import ru.spb.iac.cud.items.Role;

@Remote
public interface SyncManagerRemote {

	public void sync_roles(String idIS, List<Role> roles, String modeExec,
			Long idUserAuth, String IPAddress) throws GeneralFailure;

	public void sync_functions(String idIS, List<Function> functions,
			String modeExec, Long idUserAuth, String IPAddress)
			throws GeneralFailure;

	public List<Role> is_roles(String idIS, Long idUserAuth, String IPAddress)
			throws GeneralFailure;

	public List<Function> is_functions(String idIS, Long idUserAuth,
			String IPAddress) throws GeneralFailure;

	public void sync_groups(String idIS, List<Group> groups, String modeExec,
			Long idUserAuth, String IPAddress) throws GeneralFailure;

	public void sync_groups_roles(String idIS, List<String> codesGroups,
			List<String> codesRoles, String modeExec, Long idUserAuth,
			String IPAddress) throws GeneralFailure;

	public void sync_resources(String idIS, List<Resource> resources,
			String modeExec, Long idUserAuth, String IPAddress)
			throws GeneralFailure;

	public void sync_resources_roles(String idIS, List<String> codesResources,
			List<String> codesRoles, String modeExec, Long idUserAuth,
			String IPAddress) throws GeneralFailure;

	public void is_exist(String idIS) throws GeneralFailure;
}
