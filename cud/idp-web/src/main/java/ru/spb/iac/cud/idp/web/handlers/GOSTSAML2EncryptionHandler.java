package ru.spb.iac.cud.idp.web.handlers;

import java.security.PublicKey;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpSession;
import javax.xml.namespace.QName;

import org.picketlink.common.constants.GeneralConstants;
import org.picketlink.common.constants.JBossSAMLConstants;
import org.picketlink.common.constants.JBossSAMLURIConstants;
import org.picketlink.common.exceptions.ProcessingException;
import org.picketlink.config.federation.IDPType;
import org.picketlink.identity.federation.core.saml.v2.interfaces.SAML2HandlerRequest;
import org.picketlink.identity.federation.core.saml.v2.interfaces.SAML2HandlerResponse;
import org.picketlink.identity.federation.saml.v2.protocol.AuthnRequestType;
import org.picketlink.identity.federation.web.handlers.saml2.BaseSAML2Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import ru.spb.iac.cud.idp.web.util.GOSTXMLEncryptionUtil;


 public class GOSTSAML2EncryptionHandler extends
		GOSTSAML2SignatureGenerationHandler {

	private static final Logger LOGGERSLF4J = LoggerFactory
			.getLogger(GOSTSAML2EncryptionHandler.class);

	@Override
	public void handleRequestType(SAML2HandlerRequest request,
			SAML2HandlerResponse response) throws ProcessingException {

		LOGGERSLF4J.debug("handleRequestType:01");

		HttpSession session = BaseSAML2Handler.getHttpSession(request);
		 
		 
	  // if(/Configuration/.isEncryptRequired/())/{
	   if(session!=null&&session.getAttribute("login_encrypt")!=null){
		//запрос на зашифрованный ответ	   
		//session=null при logout
		   
		 //только здесь используется
		 //поэтому один раз использовали и сразу же удалили
		 //чтобы не влияло на следующие запросы 
		   session.removeAttribute("login_encrypt");  
		   
		if (supportsRequest(request) && isEncryptionEnabled()) {
			Document samlResponseDocument = response.getResultingDocument();

			 

			if (samlResponseDocument == null) {
				throwResponseDocumentOrAssertionNotFound();
			}

			String samlNSPrefix = getSAMLNSPrefix(samlResponseDocument);

			try {
				QName encryptedAssertionElementQName = new QName(
						JBossSAMLURIConstants.ASSERTION_NSURI.get(),
						JBossSAMLConstants.ENCRYPTED_ASSERTION.get(),
						samlNSPrefix);

				// SecretKey /secretKey = new S/ecretKeySpec(/secret,
				// getAlgorithm());

				SecretKey secretKey = KeyGenerator.getInstance("GOST28147")
						.generateKey();

				// encrypt the Assertion element and replace it with a
				// EncryptedAssertion element.
				/*
				 * XMLEncryptionUtil/.encryptElement(new
				 * QName(JBossSAMLURIConstants.ASSERTION_NSURI.get(),
				 * JBossSAMLConstants.ASSERTION.get(), /samlNSPrefix),
				 * samlResponseDocument, /getSenderPublicKey(request), secretKey,
				 * getKeySize(), /encryptedAssertionElementQName, true);
				 */

				GOSTXMLEncryptionUtil.encryptElement(new QName(
						JBossSAMLURIConstants.ASSERTION_NSURI.get(),
						JBossSAMLConstants.ASSERTION.get(), samlNSPrefix),
						samlResponseDocument, getSenderPublicKey(request),
						secretKey, getKeySize(),
						encryptedAssertionElementQName, true);

				 

			} catch (Exception e) {

				LOGGERSLF4J.error("handleRequestType:error:", e);
				throw logger.processingError(e);
			}
		}
            //!!!
		    //обязательно здесь закомментировать
		    //иначе super - это /GOSTSAML2SignatureGenerationHandler
		    //и подписывается второй раз
			//super/.handleRequestType/(request, response);
		}
	}

	private String getSAMLNSPrefix(Document samlResponseDocument) {
		Node assertionElement = samlResponseDocument
				.getDocumentElement()
				.getElementsByTagNameNS(
						JBossSAMLURIConstants.ASSERTION_NSURI.get(),
						JBossSAMLConstants.ASSERTION.get()).item(0);

		if (assertionElement == null) {
			throwResponseDocumentOrAssertionNotFound();
		}

		return assertionElement.getPrefix();
	}

	/**
	 * <p>
	 * Indicates if the IDP has encryption enabled.
	 * </p>
	 * 
	 * @return
	 */
	private boolean isEncryptionEnabled() {
		return getType() == HANDLER_TYPE.IDP && getConfiguration().isEncrypt();
	}

	/**
	 * <p>
	 * Indicates if this handler supports the specified
	 * {@link SAML2HandlerRequest}.
	 * </p>
	 * 
	 * @param request
	 * @return
	 */
	private boolean supportsRequest(SAML2HandlerRequest request) {
		return getType() == HANDLER_TYPE.IDP
				&& (request.getSAML2Object() instanceof AuthnRequestType);
	}

	private IDPType getConfiguration() {
		IDPType configuration = (IDPType) handlerChainConfig
				.getParameter(GeneralConstants.CONFIGURATION);

		if (configuration == null) {
			throw logger.nullArgumentError("IDP Configuration");
		}

		return configuration;
	}

	private int getKeySize() {
		String keySize = (String) handlerConfig
				.getParameter(GeneralConstants.SAML_ENC_KEY_SIZE);

		if (keySize == null) {
			keySize = String.valueOf(128);
		}

		return Integer.valueOf(keySize);
	}

	

	private PublicKey getSenderPublicKey(SAML2HandlerRequest request) {
		PublicKey publicKey = (PublicKey) request.getOptions().get(
				GeneralConstants.SENDER_PUBLIC_KEY);

		 

		if (publicKey == null) {
			throw logger.nullArgumentError("Sender Public Key");
		}

		return publicKey;
	}

	private void throwResponseDocumentOrAssertionNotFound() {
		throw new IllegalStateException(
				"No response document/assertions found. Check if this handler is after the SAML2AuthenticationHandler.");
	}

}
