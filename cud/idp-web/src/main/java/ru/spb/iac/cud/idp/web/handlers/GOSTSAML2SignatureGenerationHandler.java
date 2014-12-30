package ru.spb.iac.cud.idp.web.handlers;

import org.picketlink.identity.federation.web.handlers.saml2.AbstractSignatureHandler;
import org.picketlink.common.constants.GeneralConstants;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.picketlink.identity.federation.api.saml.v2.sig.SAML2Signature;
import org.picketlink.common.exceptions.ConfigurationException;
import org.picketlink.common.exceptions.ProcessingException;
import org.picketlink.common.util.DocumentUtil;
import org.picketlink.identity.federation.core.saml.v2.interfaces.SAML2HandlerRequest;
import org.picketlink.identity.federation.core.saml.v2.interfaces.SAML2HandlerResponse;
import org.picketlink.identity.federation.web.util.RedirectBindingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import ru.spb.iac.cud.idp.web.sig.GOSTSAML2Signature;
import ru.spb.iac.cud.idp.web.util.GOSTRedirectBindingSignatureUtil;
import ru.spb.iac.cud.services.web.init.Configuration;

import static org.picketlink.common.util.StringUtil.isNotNull;

 public class GOSTSAML2SignatureGenerationHandler extends
		AbstractSignatureHandler {

	private static final Logger LOGGERSLF4J = LoggerFactory
			.getLogger(GOSTSAML2SignatureGenerationHandler.class);

	@Override
	public void generateSAMLRequest(SAML2HandlerRequest request,
			SAML2HandlerResponse response) throws ProcessingException {
		// for SP create Request
		Document samlDocument = response.getResultingDocument();

		if (samlDocument == null) {
			logger.trace("No document generated in the handler chain. Cannot generate signature");
			return;
		}

		this.sign(samlDocument, request, response);
	}

	public void handleRequestType(SAML2HandlerRequest request,
			SAML2HandlerResponse response) throws ProcessingException {
		
		//for IDP handle Request from SP
		
		if(Configuration.isSignRequired()){
			 
		Document responseDocument = response.getResultingDocument();

		LOGGERSLF4J.debug("handleRequestType:01");

		if (responseDocument == null) {
			logger.trace("No response document found");
			return;
		}

		this.sign(responseDocument, request, response);
		
		}
	}

	@Override
	public void handleStatusResponseType(SAML2HandlerRequest request,
			SAML2HandlerResponse response) throws ProcessingException {
		
		//for SP handle Request from IDP
		
		Document responseDocument = response.getResultingDocument();
		if (responseDocument == null) {
			logger.trace("No response document found");
			return;
		}

		this.sign(responseDocument, request, response);
	}

	private void sign(Document samlDocument, SAML2HandlerRequest request,
			SAML2HandlerResponse response) throws ProcessingException {
		if (!isSupportsSignature(request)) {
			return;
		}

		// Get the Key Pair
		KeyPair keypair = (KeyPair) this.handlerChainConfig
				.getParameter(GeneralConstants.KEYPAIR);
		X509Certificate x509Certificate = (X509Certificate) this.handlerChainConfig
				.getParameter(GeneralConstants.X509CERTIFICATE);

		if (keypair == null) {
			logger.samlHandlerKeyPairNotFound();
			throw logger.samlHandlerKeyPairNotFoundError();
		}

		if (response.isPostBindingForResponse()) {
			logger.trace("Going to sign response document with POST binding type");
			signPost(samlDocument, keypair, x509Certificate);
		} else {
			logger.trace("Going to sign response document with REDIRECT binding type");
			String destinationQueryString = signRedirect(samlDocument,
					response.getRelayState(), keypair,
					response.getSendRequest());
			response.setDestinationQueryStringWithSignature(destinationQueryString);
		}
	}

	private void signPost(Document samlDocument, KeyPair keypair,
			X509Certificate x509Certificate) throws ProcessingException {

		SAML2Signature samlSignature = new GOSTSAML2Signature();
		samlSignature
				.setSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411");
		samlSignature
				.setDigestMethod("http://www.w3.org/2001/04/xmldsig-more#gostr3411");

		Node nextSibling = samlSignature.getNextSiblingOfIssuer(samlDocument);
		samlSignature.setNextSibling(nextSibling);
		if (x509Certificate != null) {
			samlSignature.setX509Certificate(x509Certificate);
		}
		samlSignature.signSAMLDocument(samlDocument, keypair);
	}

	private String signRedirect(Document samlDocument, String relayState,
			KeyPair keypair, boolean willSendRequest)
			throws ProcessingException {
		try {
			String samlMessage = DocumentUtil.getDocumentAsString(samlDocument);
			String base64Request = RedirectBindingUtil
					.deflateBase64URLEncode(samlMessage.getBytes("UTF-8"));
			PrivateKey signingKey = keypair.getPrivate();

			String url;

			// Encode relayState before signing
			if (isNotNull(relayState)) {
				relayState = RedirectBindingUtil.urlEncode(relayState);
			}

			if (willSendRequest) {
				url = GOSTRedirectBindingSignatureUtil
						.getSAMLRequestURLWithSignature(base64Request,
								relayState, signingKey);
			} else {
				url = GOSTRedirectBindingSignatureUtil
						.getSAMLResponseURLWithSignature(base64Request,
								relayState, signingKey);
			}

			return url;
		} catch (ConfigurationException ce) {
			logger.samlHandlerErrorSigningRedirectBindingMessage(ce);
			throw logger.samlHandlerSigningRedirectBindingMessageError(ce);
		} catch (GeneralSecurityException ce) {
			logger.samlHandlerErrorSigningRedirectBindingMessage(ce);
			throw logger.samlHandlerSigningRedirectBindingMessageError(ce);
		} catch (IOException ce) {
			logger.samlHandlerErrorSigningRedirectBindingMessage(ce);
			throw logger.samlHandlerSigningRedirectBindingMessageError(ce);
		}
	}

}
