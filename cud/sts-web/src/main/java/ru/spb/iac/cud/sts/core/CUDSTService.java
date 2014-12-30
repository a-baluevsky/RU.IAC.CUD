package ru.spb.iac.cud.sts.core;

import org.picketlink.common.ErrorCodes;
import org.picketlink.common.PicketLinkLogger;
import org.picketlink.common.PicketLinkLoggerFactory;
import org.picketlink.common.constants.WSTrustConstants;
import org.picketlink.common.constants.WSTrustConstants.WSSE;
import org.picketlink.common.exceptions.ConfigurationException;
import org.picketlink.common.exceptions.fed.WSTrustException;
import org.picketlink.common.util.DocumentUtil;
import org.picketlink.config.federation.STSType;
import org.picketlink.config.federation.parsers.STSConfigParser;
import org.picketlink.identity.federation.core.parsers.wst.WSTrustParser;
import org.picketlink.identity.federation.core.util.SOAPUtil;
import org.picketlink.identity.federation.core.wstrust.PicketLinkSTSConfiguration;
import org.picketlink.identity.federation.core.wstrust.STSConfiguration;
import org.picketlink.identity.federation.core.wstrust.WSTrustRequestHandler;
import org.picketlink.identity.federation.core.wstrust.wrappers.BaseRequestSecurityToken;
import org.picketlink.identity.federation.core.wstrust.wrappers.RequestSecurityToken;
import org.picketlink.identity.federation.core.wstrust.wrappers.RequestSecurityTokenCollection;
import org.picketlink.identity.federation.core.wstrust.wrappers.RequestSecurityTokenResponse;
import org.picketlink.identity.federation.core.wstrust.wrappers.RequestSecurityTokenResponseCollection;
import org.picketlink.identity.federation.core.wstrust.writers.WSTrustResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.spb.iac.cud.util.CudPrincipal;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.handler.MessageContext;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

@WebServiceProvider(serviceName = "CUDSTS", portName = "CUDSTSPort", targetNamespace = "http://sts.services.cud.iac.spb.ru/", wsdlLocation = "WEB-INF/wsdl/CUDSTS_FULL.wsdl")
@ServiceMode(value = Service.Mode.MESSAGE)
@HandlerChain(file = "/handlers.xml")
 public class CUDSTService implements Provider<SOAPMessage> {
	private static final PicketLinkLogger LOGGER = PicketLinkLoggerFactory
			.getLogger();

	private static final Logger LOGGERSLF4J = LoggerFactory
			.getLogger(CUDSTService.class);

	private static final String SEPARATOR = AccessController
			.doPrivileged(new PrivilegedAction<String>() {
				public String run() {
					return System.getProperty("file.separator");
				}
			});

	private static final String STS_CONFIG_FILE = "picketlink-sts.xml";
	private static final String PICKETLINK_CONFIG_FILE = "picketlink.xml";

	private static final String STS_CONFIG_DIR = "picketlink-store" + SEPARATOR
			+ "sts" + SEPARATOR;

	@Resource
	protected WebServiceContext context;

	protected STSConfiguration config;

	// If the SOAP Message contained a wsse:binaryToken, all the providers can
	// have access to it
	public static ThreadLocal<BinaryToken> binaryToken = new InheritableThreadLocal<BinaryToken>();

	private CudPrincipal cud_principal;

	public static class BinaryToken {
		public Node token;
	}

	public SOAPMessage invoke(SOAPMessage request) {
		String valueType = null;
		Node binaryToken = null;
		boolean soap12 = false;

		MessageContext mess_context = context.getMessageContext();

		HttpServletRequest http_request = (HttpServletRequest) mess_context
				.get(MessageContext.SERVLET_REQUEST);

		HttpSession http_session = http_request.getSession();

		String user_principal_name = (String) http_session
				.getAttribute("user_principal");
		String system_principal_name = (String) http_session
				.getAttribute("system_principal");
    	String auth_type = (String)  http_session
				.getAttribute("cud_auth_type");
			
		LOGGERSLF4J.debug("invoke:01:" + user_principal_name);
		LOGGERSLF4J.debug("invoke:02:" + system_principal_name);

		cud_principal = new CudPrincipal();
		cud_principal.setSystemName(system_principal_name);
		

		if (user_principal_name != null) {
			cud_principal.setUserName(user_principal_name);
			cud_principal.setAuthType(auth_type);
		}

		// Check headers
		try {

			soap12 = SOAPUtil.isSOAP12(request);
			SOAPHeader soapHeader = request.getSOAPHeader();
			binaryToken = getBinaryToken(soapHeader);
			if (binaryToken != null) {
				NamedNodeMap namedNodeMap = binaryToken.getAttributes();
				int length = namedNodeMap != null ? namedNodeMap.getLength()
						: 0;
				for (int i = 0; i < length; i++) {
					Node nodeValueType = namedNodeMap
							.getNamedItem(WSSE.VALUE_TYPE);
					if (nodeValueType != null) {
						valueType = nodeValueType.getNodeValue();
						break;
					}
				}
			}
		} catch (SOAPException e) {
			LOGGERSLF4J.error("invoke:04_error:", e);
			throw LOGGER.stsWSError(e);
		}

		Node payLoad;
		BaseRequestSecurityToken baseRequest;
		try {
			payLoad = SOAPUtil.getSOAPData(request);

			WSTrustParser parser = new WSTrustParser();

			baseRequest = (BaseRequestSecurityToken) parser.parse(DocumentUtil
					.getNodeAsStream(payLoad));
		} catch (Exception e) {
			LOGGERSLF4J.error("invoke:05_error:", e);
			throw LOGGER.stsWSError(e);
		}

		if (baseRequest instanceof RequestSecurityToken) {

			RequestSecurityToken req = (RequestSecurityToken) baseRequest;
			try {
				req.setRSTDocument((Document) payLoad);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			if (binaryToken != null) {
				req.setBinaryToken(binaryToken);
			}

			if (valueType != null) {
				req.setBinaryValueType(URI.create(valueType));
			}
			Source theResponse = this.handleTokenRequest(req);

			return convert(theResponse, soap12);
		} else if (baseRequest instanceof RequestSecurityTokenCollection) {

			return convert(
					this.handleTokenRequestCollection((RequestSecurityTokenCollection) baseRequest),
					soap12);
		} else {
			throw LOGGER.stsWSInvalidTokenRequestError();
		}
	}

	private SOAPMessage convert(Source theResponse, boolean wantSOAP12) {
		try {
			SOAPMessage response = null;

			if (wantSOAP12) {
				response = SOAPUtil.createSOAP12();
			} else {
				response = SOAPUtil.create();
			}
			Document theResponseDoc = (Document) DocumentUtil
					.getNodeFromSource(theResponse);
			response.getSOAPBody().addDocument(theResponseDoc);

		
			return response;
		} catch (Exception e) {
			throw LOGGER.stsWSError(e);
		}
	}

	private Node getBinaryToken(SOAPHeader soapHeader) {
		if (soapHeader != null) {
			NodeList children = soapHeader.getChildNodes();
			int length = children != null ? children.getLength() : 0;
			for (int i = 0; i < length; i++) {
				Node child = children.item(i);
				if (child.getNodeName().contains(WSSE.BINARY_SECURITY_TOKEN)) {
					return child;
				}
			}
		}
		return null;
	}

	/**
	 * <p>
	 * Process a security token request.
	 * </p>
	 * 
	 * @param request
	 *            a {@code RequestSecurityToken} instance that contains the
	 *            request information.
	 * @return a {@code Source} instance representing the marshalled response.
	 * @throws WebServiceException
	 *             Any exception encountered in handling token
	 */
	protected Source handleTokenRequest(RequestSecurityToken request) {
		if (context == null) {
			throw new IllegalStateException(ErrorCodes.NULL_VALUE
					+ "WebServiceContext");
		}
		if (this.config == null) {
			try {
				LOGGER.debug("Loading STS configuration");
				this.config = this.getConfiguration();
			} catch (ConfigurationException e) {
				throw LOGGER.stsWSConfigurationError(e);
			}
		}

		WSTrustRequestHandler handler = this.config.getRequestHandler();
		if (handler == null) {
			throw LOGGER.nullValueError("WSTrustRequestHandler");
		}

		String requestType = request.getRequestType().toString();

		LOGGER.trace("STS received request of type " + requestType);

		try {
			if (requestType.equals(WSTrustConstants.ISSUE_REQUEST)) {

				Source source = this.marshallResponse(handler.issue(request,
						cud_principal));

				Document doc = handler.postProcess(
						(Document) ((DOMSource) source).getNode(), request);

				 

				return new DOMSource(doc);
			} else if (requestType.equals(WSTrustConstants.RENEW_REQUEST)) {
				Source source = this.marshallResponse(handler.renew(request,
						cud_principal));

				Document document = handler.postProcess(
						(Document) ((DOMSource) source).getNode(), request);
				return new DOMSource(document);
			} else if (requestType.equals(WSTrustConstants.CANCEL_REQUEST)) {
				return this.marshallResponse(handler.cancel(request,
						this.context.getUserPrincipal()));
			} else if (requestType.equals(WSTrustConstants.VALIDATE_REQUEST)) {
				return this.marshallResponse(handler.validate(request,
						this.context.getUserPrincipal()));
			} else {
				throw LOGGER.stsWSInvalidRequestTypeError(requestType);
			}
		} catch (WSTrustException we) {
			throw LOGGER.stsWSHandlingTokenRequestError(we);
		}
	}

	/**
	 * <p>
	 * Process a collection of security token requests.
	 * </p>
	 * 
	 * @param requestCollection
	 *            a {@code RequestSecurityTokenCollection} containing the
	 *            various requests information.
	 * @return a {@code Source} instance representing the marshalled response.
	 */
	protected Source handleTokenRequestCollection(
			RequestSecurityTokenCollection requestCollection) {
		throw new UnsupportedOperationException();
	}

	/**
	 * <p>
	 * Marshalls the specified {@code RequestSecurityTokenResponse} into a
	 * {@code Source} instance.
	 * </p>
	 * 
	 * @param response
	 *            the {@code RequestSecurityTokenResponse} to be marshalled.
	 * @return the resulting {@code Source} instance.
	 */
	protected Source marshallResponse(RequestSecurityTokenResponse response) {
		// add the single response to a RequestSecurityTokenResponse collection,
		// as per the specification.
		RequestSecurityTokenResponseCollection responseCollection = new RequestSecurityTokenResponseCollection();
		responseCollection.addRequestSecurityTokenResponse(response);

		try {
			DOMResult result = new DOMResult(DocumentUtil.createDocument());
			WSTrustResponseWriter writer = new WSTrustResponseWriter(result);
			writer.write(responseCollection);
			return new DOMSource(result.getNode());
		} catch (Exception e) {
			throw LOGGER.stsWSResponseWritingError(e);
		}
	}

	/**
	 * <p>
	 * Obtains the STS configuration options.
	 * </p>
	 * 
	 * @return an instance of {@code STSConfiguration} containing the STS
	 *         configuration properties.
	 */
	protected STSConfiguration getConfiguration() throws ConfigurationException {
		URL configurationFileURL = null;

		try {
			// check the user home for a configuration file generated by the
			// picketlink console.
			String configurationFilePath = System.getProperty("user.home")
					+ SEPARATOR + STS_CONFIG_DIR + STS_CONFIG_FILE;
			File configurationFile = new File(configurationFilePath);
			if (configurationFile.exists()) {
				configurationFileURL = configurationFile.toURI().toURL();
			} else {
				configurationFileURL = SecurityActions.loadResource(getClass(),
						PICKETLINK_CONFIG_FILE);

				// fallback to the old configuration
				if (configurationFileURL == null) {
					configurationFileURL = SecurityActions.loadResource(
							getClass(), STS_CONFIG_FILE);
				}
			}

			// if no configuration file was found, log a warn message and use
			// default configuration values.
			if (configurationFileURL == null) {
				LOGGER.stsUsingDefaultConfiguration("");
				return new PicketLinkSTSConfiguration();
			}

			InputStream stream = configurationFileURL.openStream();
			STSType stsConfig = (STSType) new STSConfigParser().parse(stream);
			STSConfiguration configuration = new PicketLinkSTSConfiguration(
					stsConfig);
			if (LOGGER.isInfoEnabled()) {
				LOGGER.stsConfigurationFileLoaded(STS_CONFIG_FILE);
			}
			return configuration;
		} catch (Exception e) {
			throw LOGGER.stsConfigurationFileParsingError(e);
		}
	}
}
