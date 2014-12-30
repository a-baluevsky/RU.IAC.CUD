package ru.spb.iac.cud.services.application;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.items.app.AppAttribute;
import ru.spb.iac.cud.items.app.AppAccept;

@WebService(targetNamespace = ApplicationServiceImpl.NS)
public interface ApplicationService {

	public static final String NS = ApplicationServiceImpl.NS;

	@WebMethod
	@WebResult(targetNamespace = NS)
	public AppAccept system_registration(
			@WebParam(name = "attributes", targetNamespace = NS) List<AppAttribute> attributes)
			throws GeneralFailure;

	@WebMethod
	@WebResult(targetNamespace = NS)
	public AppAccept user_registration(
			@WebParam(name = "attributes", targetNamespace = NS) List<AppAttribute> attributes)
			throws GeneralFailure;

	@WebMethod
	@WebResult(targetNamespace = NS)
	public AppAccept access_roles(
			@WebParam(name = "modeExec", targetNamespace = NS) String modeExec,
			@WebParam(name = "uidUser", targetNamespace = NS) String uidUser,
			@WebParam(name = "codesRoles", targetNamespace = NS) List<String> codesRoles)
			throws GeneralFailure;

	@WebMethod
	@WebResult(targetNamespace = NS)
	public AppAccept access_groups(
			@WebParam(name = "modeExec", targetNamespace = NS) String modeExec,
			@WebParam(name = "uidUser", targetNamespace = NS) String uidUser,
			@WebParam(name = "codesGroups", targetNamespace = NS) List<String> codesGroups)
			throws GeneralFailure;

	@WebMethod
	@WebResult(targetNamespace = NS)
	public AppAccept block(
			@WebParam(name = "modeExec", targetNamespace = NS) String modeExec,
			@WebParam(name = "uidUser", targetNamespace = NS) String uidUser,
			@WebParam(name = "blockUser", targetNamespace = NS) String blockReason)
			throws GeneralFailure;

	@WebMethod
	@WebResult(targetNamespace = NS)
	public AppAccept system_modification(
			@WebParam(name = "attributes", targetNamespace = NS) List<AppAttribute> attributes)
			throws GeneralFailure;

	@WebMethod
	@WebResult(targetNamespace = NS)
	public AppAccept user_modification(
			@WebParam(name = "uidUser", targetNamespace = NS) String uidUser,
			@WebParam(name = "attributes", targetNamespace = NS) List<AppAttribute> attributes)
			throws GeneralFailure;

	@WebMethod
	@WebResult(targetNamespace = NS)
	public AppAccept user_identity_modification(
			@WebParam(name = "uidUser", targetNamespace = NS) String uidUser,
			@WebParam(name = "login", targetNamespace = NS) String login,
			@WebParam(name = "password", targetNamespace = NS) String password)
			throws GeneralFailure;

	@WebMethod
	@WebResult(targetNamespace = NS)
	public AppAccept user_cert_modification(
			@WebParam(name = "modeExec", targetNamespace = NS) String modeExec,
			@WebParam(name = "uidUser", targetNamespace = NS) String uidUser,
			@WebParam(name = "certBase64", targetNamespace = NS) String certBase64)
			throws GeneralFailure;

}
