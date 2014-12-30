package ru.spb.iac.cud.services.util;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.items.Function;
import ru.spb.iac.cud.items.GroupsData;
import ru.spb.iac.cud.items.Resource;
import ru.spb.iac.cud.items.Role;
import ru.spb.iac.cud.items.UsersData;

@WebService(targetNamespace = UtilServiceImpl.NS)
public interface UtilService {

	public static final String NS = UtilServiceImpl.NS;

	@WebMethod
	@WebResult(targetNamespace = NS)
	public UsersData users_data(
			@WebParam(name = "uidsUsers", targetNamespace = NS) List<String> uidsUsers,
			@WebParam(name = "category", targetNamespace = NS) String category,
			@WebParam(name = "rolesCodes", targetNamespace = NS) List<String> rolesCodes,
			@WebParam(name = "groupsCodes", targetNamespace = NS) List<String> groupsCodes,
			@WebParam(name = "startRow", targetNamespace = NS) Integer startRow,
			@WebParam(name = "countRow", targetNamespace = NS) Integer countRow,
			@WebParam(name = "settings", targetNamespace = NS) List<String> settings)
			throws GeneralFailure;

	@WebResult(targetNamespace = NS)
	public List<Role> sys_roles() throws GeneralFailure;

	@WebResult(targetNamespace = NS)
	public List<Function> sys_functions() throws GeneralFailure;

	@WebMethod
	@WebResult(targetNamespace = NS)
	public GroupsData groups_data(
			@WebParam(name = "groupsCodes", targetNamespace = NS) List<String> groupsCodes,
			@WebParam(name = "category", targetNamespace = NS) String category,
			@WebParam(name = "rolesCodes", targetNamespace = NS) List<String> rolesCodes,
			@WebParam(name = "startRow", targetNamespace = NS) Integer startRow,
			@WebParam(name = "countRow", targetNamespace = NS) Integer countRow,
			@WebParam(name = "settings", targetNamespace = NS) List<String> settings)
			throws GeneralFailure;

	
	@WebResult(targetNamespace = NS)
	public List<Resource> resources_data(
			@WebParam(name = "category", targetNamespace = NS) String category)
			throws GeneralFailure;

	@WebResult(targetNamespace = NS)
	public List<Role> roles_data(
			@WebParam(name = "category", targetNamespace = NS) String category) throws GeneralFailure;

	
}
