package iac.cud.infosweb.ws;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;

import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.exceptions.InvalidCredentials;
import ru.spb.iac.cud.exceptions.TokenExpired;
import ru.spb.iac.cud.items.Attribute;
import ru.spb.iac.cud.items.AuditFunction;
import ru.spb.iac.cud.items.Role;
import ru.spb.iac.cud.items.Token;
import ru.spb.iac.cud.services.access.AccessServices;

@Name("asClient")
 public class AccessServiceClient {

	@Logger
	private Log log;

	private static String endpointURI = "http://cudvm/CudServices/AccessService?wsdl";

	static String idIS = "urn:eis:cud";

	private AccessServices accessServices = null;

	public void invoke() {
		/*
		 * 
		 * System setProperty "javax.net.ssl.trustStore",
		 * "D:/Development/cert/client.truststore"
		 * System setProperty "javax.net.ssl.keyStore",
		 * "D:/Development/cert/client.keystore"
		 * System setProperty "javax.net.ssl.trustStorePassword", "9753560"
		 * System setProperty "javax.net.ssl.keyStorePassword", "9753560"
		 * 
		 */
		

	}

	public String authenticate_login(String login, String password)
			throws Exception {
		try {
			Token retObj = getPort(endpointURI).authenticate_login(login,
					password);

			log.info("authenticate_login:token:" + retObj.getId());

			return retObj.getId();

		} catch (InvalidCredentials e1) {
			log.error("authenticate_login:error1:" + e1.getMessage());
			throw e1;
		} catch (GeneralFailure e2) {
			log.error("authenticate_login:error2:" + e2.getMessage());
			throw e2;
		}

	}

	public List<Role> authorize(String tokenID) throws Exception {
		try {
			List<Role> retObj = getPort(endpointURI).authorize(idIS,
					new Token(tokenID));

			for (Role role : retObj) {
				log.info("authorize:IdRole:" + role.getCode());
				log.info("authorize:Name:" + role.getName());
				log.info("authorize:Description:" + role.getDescription());
			}

			return retObj;

		} catch (TokenExpired e1) {
			log.error("authorize:error1:" + e1.getMessage());
			throw e1;
		} catch (GeneralFailure e2) {
			log.error("authorize:error2:" + e2.getMessage());
			throw e2;
		}
	}

	public List<Attribute> attrib(String tokenID) throws Exception {

		try {

			List<Attribute> retObj = getPort(endpointURI).attributes(
					new Token(tokenID));
			for (Attribute attr : retObj) {
				log.info("attrib:Name:" + attr.getName());
				log.info("attrib:Value:" + attr.getValue());
			}
			return retObj;

		} catch (TokenExpired e1) {
			log.error("attrib:error1:" + e1.getMessage());
			throw e1;
		} catch (GeneralFailure e2) {
			log.error("attrib:error2:" + e2.getMessage());
			throw e2;
		}
	}

	public void audit(String tokenID, List<AuditFunction> funccodes)
			throws Exception {
		try {

			getPort(endpointURI).audit(idIS, funccodes, new Token(tokenID));

		} catch (GeneralFailure e1) {
			log.error("audit:error1:" + e1.getMessage());
			throw e1;
		}
	}

	public void logout(String tokenID) throws Exception {
		try {
			getPort(endpointURI).logout(new Token(tokenID));
		} catch (GeneralFailure e1) {
			log.error("logout:error1:" + e1.getMessage());
			throw e1;
		}
	}

	private AccessServices getPort(String endpointURI)
			throws MalformedURLException {

		if (accessServices == null) {

			QName serviceName = new QName("http://access.services.cud.iac/",
					"AccessServicesImplService");
			URL wsdlURL = new URL(endpointURI);

			Service service = Service.create(wsdlURL, serviceName);

			QName portName = new QName("http://access.services.cud.iac/",
					"AccessServicesImplPort");

			this.accessServices = service.getPort(portName,
					AccessServices.class);
		}

		return this.accessServices;
	}
}
