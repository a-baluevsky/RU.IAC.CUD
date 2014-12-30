package ru.spb.iac.cud.services.admin;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.items.Function;
import ru.spb.iac.cud.items.Group;
import ru.spb.iac.cud.items.Resource;
import ru.spb.iac.cud.items.Role;

@WebService(targetNamespace = AdminServiceImpl.NS)
public interface AdminService {

	public static final String NS = AdminServiceImpl.NS;

	@WebMethod
	public void sync_roles(
			@WebParam(name = "roles", targetNamespace = NS) List<Role> roles,
			@WebParam(name = "modeExec", targetNamespace = NS) String modeExec)
			throws GeneralFailure;

	@WebMethod
	public void access_roles(
			@WebParam(name = "uidsUsers", targetNamespace = NS) List<String> uidsUsers,
			@WebParam(name = "codesRoles", targetNamespace = NS) List<String> codesRoles,
			@WebParam(name = "modeExec", targetNamespace = NS) String modeExec)
			throws GeneralFailure;

	@WebMethod
	public void cert_change_sys(
			@WebParam(name = "newCert", targetNamespace = NS) String newCert)
			throws GeneralFailure;

	@WebMethod
	public void sync_functions(
			@WebParam(name = "functions", targetNamespace = NS) List<Function> functions,
			@WebParam(name = "modeExec", targetNamespace = NS) String modeExec)
			throws GeneralFailure;

	@WebMethod
	public void sync_groups(
			@WebParam(name = "groups", targetNamespace = NS) List<Group> groups,
			@WebParam(name = "modeExec", targetNamespace = NS) String modeExec)
			throws GeneralFailure;

	@WebMethod
	public void sync_groups_roles(
			@WebParam(name = "codesGroups", targetNamespace = NS) List<String> codesGroups,
			@WebParam(name = "codesRoles", targetNamespace = NS) List<String> codesRoles,
			@WebParam(name = "modeExec", targetNamespace = NS) String modeExec)
			throws GeneralFailure;

	@WebMethod
	public void access_groups(
			@WebParam(name = "uidsUsers", targetNamespace = NS) List<String> uidsUsers,
			@WebParam(name = "codesGroups", targetNamespace = NS) List<String> codesGroups,
			@WebParam(name = "modeExec", targetNamespace = NS) String modeExec)
			throws GeneralFailure;

	@WebMethod
	public void sync_resources(
			@WebParam(name = "resources", targetNamespace = NS) List<Resource> resources,
			@WebParam(name = "modeExec", targetNamespace = NS) String modeExec)
			throws GeneralFailure;

	@WebMethod
	public void sync_resources_roles(
			@WebParam(name = "codesResources", targetNamespace = NS) List<String> codesResources,
			@WebParam(name = "codesRoles", targetNamespace = NS) List<String> codesRoles,
			@WebParam(name = "modeExec", targetNamespace = NS) String modeExec)
			throws GeneralFailure;

}