package ru.spb.iac.cud.idp.web.handlers;

import org.picketlink.identity.federation.web.handlers.saml2.AbstractSignatureHandler;
import org.picketlink.common.constants.GeneralConstants;

import java.security.KeyPair;
import org.picketlink.identity.federation.api.saml.v2.sig.SAML2Signature;
import org.picketlink.common.exceptions.ProcessingException;
import org.picketlink.common.util.DocumentUtil;
import org.picketlink.identity.federation.core.saml.v2.interfaces.SAML2HandlerRequest;
import org.picketlink.identity.federation.core.saml.v2.interfaces.SAML2HandlerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.spb.iac.cud.idp.web.sig.GOSTSAML2Signature;

 public class GOSTSAML2SignatureAssertionGenerationHandler extends
		AbstractSignatureHandler {

	private static final Logger LOGGERSLF4J = LoggerFactory
			.getLogger(GOSTSAML2SignatureAssertionGenerationHandler.class);

	public void handleRequestType(SAML2HandlerRequest request,
			SAML2HandlerResponse response) throws ProcessingException {

		Document responseDocument = response.getResultingDocument();

		LOGGERSLF4J.debug("handleRequestType:01");

		if (responseDocument == null) {
			logger.trace("No response document found");
			return;
		}

		this.signAssertion(responseDocument, request, response);
	}

	private void signAssertion(Document samlDocument,
			SAML2HandlerRequest request, SAML2HandlerResponse response)
			throws ProcessingException {
		if (!isSupportsSignature(request)) {
			return;
		}

		// Get the Key Pair
		KeyPair keypair = (KeyPair) this.handlerChainConfig
				.getParameter(GeneralConstants.KEYPAIR);
	
		if (keypair == null) {
			logger.samlHandlerKeyPairNotFound();
			throw logger.samlHandlerKeyPairNotFoundError();
		}

		// сами решили подписывать Assertion
		try {

			 

			NodeList assertionList = samlDocument.getElementsByTagNameNS(
					"urn:oasis:names:tc:SAML:2.0:assertion", "Assertion");

			 

			 

			if (assertionList != null && assertionList.getLength() > 0) {

				Node oldAssertion = assertionList.item(0);

				Node parentOldAssertion = oldAssertion.getParentNode();

				Document assertionDocument = DocumentUtil.createDocument();
				Node n = assertionDocument.importNode(oldAssertion, true);
				assertionDocument.appendChild(n);

				 

				SAML2Signature samlSignature = new GOSTSAML2Signature();
				samlSignature
						.setSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411");
				samlSignature
						.setDigestMethod("http://www.w3.org/2001/04/xmldsig-more#gostr3411");

				Node nextSibling = samlSignature
						.getNextSiblingOfIssuer(assertionDocument);
				samlSignature.setNextSibling(nextSibling);
				 
				samlSignature.signSAMLDocument(assertionDocument, keypair);

				 

				Node newAssertion = samlDocument.importNode(
						assertionDocument.getFirstChild(), true);

				parentOldAssertion.replaceChild(newAssertion, oldAssertion);

				 

			}

		} catch (Exception e) {
			LOGGERSLF4J.error("signAssertion:Error:", e);
		}

	}

}
