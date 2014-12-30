package ru.spb.iac.cud.services.admin;

import java.util.List;

import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.SOAPBinding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spb.iac.cud.context.ContextSyncManager;
import ru.spb.iac.cud.context.eis.ContextAdminManager;
import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.items.Function;
import ru.spb.iac.cud.items.Group;
import ru.spb.iac.cud.items.Resource;
import ru.spb.iac.cud.items.Role;

@WebService(targetNamespace = AdminServiceImpl.NS)
@HandlerChain(file = "/handlers.xml")
@BindingType(SOAPBinding.SOAP12HTTP_BINDING)
 public class AdminServiceImpl implements AdminService {

	public static final String NS = "http://admin.services.cud.iac.spb.ru/";

	private static final Logger LOGGER = LoggerFactory.getLogger(AdminServiceImpl.class);

	@javax.annotation.Resource(name = "wsContext")
	private WebServiceContext wsContext;

	@WebMethod
	public void sync_roles(
			@WebParam(name = "roles", targetNamespace = NS) List<Role> roles,
			@WebParam(name = "modeExec", targetNamespace = NS) String modeExec)
			throws GeneralFailure {
		LOGGER.debug("sync_roles");
		(new ContextSyncManager()).sync_roles(getIDSystem(), roles, modeExec,
				getIDUser(), getIPAddress());
	}

	@WebMethod
	public void access_roles(
			@WebParam(name = "uidsUsers", targetNamespace = NS) List<String> uidsUsers,
			@WebParam(name = "codesRoles", targetNamespace = NS) List<String> codesRoles,
			@WebParam(name = "modeExec", targetNamespace = NS) String modeExec)
			throws GeneralFailure {
		LOGGER.debug("access_roles");
		(new ContextAdminManager()).access(getIDSystem(), uidsUsers, modeExec,
				codesRoles, getIDUser(), getIPAddress());
	}

	@WebMethod
	public void cert_change_sys(
			@WebParam(name = "newCert", targetNamespace = NS) String newCert)
			throws GeneralFailure {
		
		LOGGER.debug("cert_change_sys");
		
		(new ContextAdminManager()).cert_change(getIDSystem(), newCert,
				getIDUser(), getIPAddress());
	}

	@WebMethod
	public void sync_functions(
			@WebParam(name = "functions", targetNamespace = NS) List<Function> functions,
			@WebParam(name = "modeExec", targetNamespace = NS) String modeExec)
			throws GeneralFailure {
		LOGGER.debug("sync_functions");
		(new ContextSyncManager()).sync_functions(getIDSystem(), functions,
				modeExec, getIDUser(), getIPAddress());
	}

	@WebMethod
	public void sync_groups(
			@WebParam(name = "groups", targetNamespace = NS) List<Group> groups,
			@WebParam(name = "modeExec", targetNamespace = NS) String modeExec)
			throws GeneralFailure {
		LOGGER.debug("sync_groups");
		(new ContextSyncManager()).sync_groups(getIDSystem(), groups, modeExec,
				getIDUser(), getIPAddress());
	}

	@WebMethod
	public void sync_groups_roles(
			@WebParam(name = "codesGroups", targetNamespace = NS) List<String> codesGroups,
			@WebParam(name = "codesRoles", targetNamespace = NS) List<String> codesRoles,
			@WebParam(name = "modeExec", targetNamespace = NS) String modeExec)
			throws GeneralFailure {
		LOGGER.debug("sync_groups_roles");
		(new ContextSyncManager()).sync_groups_roles(getIDSystem(),
				codesGroups, codesRoles, modeExec, getIDUser(), getIPAddress());
	}

	@WebMethod
	public void access_groups(
			@WebParam(name = "uidsUsers", targetNamespace = NS) List<String> uidsUsers,
			@WebParam(name = "codesGroups", targetNamespace = NS) List<String> codesGroups,
			@WebParam(name = "modeExec", targetNamespace = NS) String modeExec)
			throws GeneralFailure {

		(new ContextAdminManager()).access_groups(getIDSystem(), uidsUsers,
				modeExec, codesGroups, getIDUser(), getIPAddress());

	}

	@WebMethod
	public void sync_resources(
			@WebParam(name = "resources", targetNamespace = NS) List<Resource> resources,
			@WebParam(name = "modeExec", targetNamespace = NS) String modeExec)
			throws GeneralFailure {

		(new ContextSyncManager()).sync_resources(getIDSystem(), resources,
				modeExec, getIDUser(), getIPAddress());

	}

	@WebMethod
	public void sync_resources_roles(
			@WebParam(name = "codesResources", targetNamespace = NS) List<String> codesResources,
			@WebParam(name = "codesRoles", targetNamespace = NS) List<String> codesRoles,
			@WebParam(name = "modeExec", targetNamespace = NS) String modeExec)
			throws GeneralFailure {

		(new ContextSyncManager()).sync_resources_roles(getIDSystem(),
				codesResources, codesRoles, modeExec, getIDUser(),
				getIPAddress());

	}

	private String getIPAddress() {
		MessageContext context = wsContext.getMessageContext();
		HttpServletRequest request = (HttpServletRequest) context
				.get(MessageContext.SERVLET_REQUEST);

		String ipAddress = request.getRemoteAddr();
		return ipAddress;
	}

	private String getIDSystem() {
		MessageContext context = wsContext.getMessageContext();
		HttpServletRequest request = (HttpServletRequest) context
				.get(MessageContext.SERVLET_REQUEST);

		String idSystem = (String) request.getSession().getAttribute(
				"cud_sts_principal");

		LOGGER.debug("getIDSystem:" + idSystem);

		return idSystem;
	}

	private Long getIDUser() throws GeneralFailure {
		MessageContext context = wsContext.getMessageContext();
		HttpServletRequest request = (HttpServletRequest) context
				.get(MessageContext.SERVLET_REQUEST);

		Long idUser = null;
		try {
			// user из ApplicantToken_1
			// это заявитель

			// когда пользователя определяли по логину, то сначала в
			// AppSOAPHandler
			// вычисляли его ИД через authenticate_login_obo
			// и в сессию клали уже Long idUser,
			// поэтому при извлечении из сессии можно было делать привидение к
			// Long
			// сейчас же мы кладём в сессиию ид пользователя из текстового поля
			// запроса
			// Long idUs/er =
			// (Long)req/uest.getSe/ssion(/).getAtt/ribute/("user_id_principal");

			if (request.getSession().getAttribute("user_id_principal") != null
					&& !request.getSession().getAttribute("user_id_principal")
							.toString().isEmpty()) {

				// это заявитель
				idUser = new Long((String) request.getSession().getAttribute(
						"user_id_principal"));

				 

			} else {
				// анаоним
				idUser = -1L;
			}
			return idUser;

		} catch (Exception e) {
			throw new GeneralFailure("USER UID IS NOT CORRECT");
		}
	}
}
