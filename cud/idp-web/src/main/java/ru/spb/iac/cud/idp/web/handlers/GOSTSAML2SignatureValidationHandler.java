package ru.spb.iac.cud.idp.web.handlers;

import org.picketlink.identity.federation.web.handlers.saml2.AbstractSignatureHandler;
import org.jboss.security.audit.AuditLevel;
import org.picketlink.common.constants.GeneralConstants;
import org.picketlink.common.exceptions.ProcessingException;
import org.picketlink.common.util.DocumentUtil;
import org.picketlink.identity.federation.api.saml.v2.sig.SAML2Signature;
import org.picketlink.identity.federation.core.audit.PicketLinkAuditEvent;
import org.picketlink.identity.federation.core.audit.PicketLinkAuditEventType;
import org.picketlink.identity.federation.core.audit.PicketLinkAuditHelper;
import org.picketlink.identity.federation.core.saml.v2.interfaces.SAML2HandlerErrorCodes;
import org.picketlink.identity.federation.core.saml.v2.interfaces.SAML2HandlerRequest;
import org.picketlink.identity.federation.core.saml.v2.interfaces.SAML2HandlerResponse;
import org.picketlink.identity.federation.web.core.HTTPContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import ru.spb.iac.cud.idp.web.sig.GOSTSAML2Signature;
import ru.spb.iac.cud.idp.web.util.GOSTRedirectBindingSignatureUtil;
import ru.spb.iac.cud.services.web.init.Configuration;

import java.security.PublicKey;
import java.util.Map;

 public class GOSTSAML2SignatureValidationHandler extends
		AbstractSignatureHandler {

	private static final Logger LOGGERSLF4J = LoggerFactory
			.getLogger(GOSTSAML2SignatureValidationHandler.class);

	private SAML2Signature saml2Signature = new GOSTSAML2Signature();

	public void handleRequestType(SAML2HandlerRequest request,
			SAML2HandlerResponse response) throws ProcessingException {
		
		 if(Configuration.isSignRequired()){
		    validateSender(request, response);
		}
	}

	@Override
	public void handleStatusResponseType(SAML2HandlerRequest request,
			SAML2HandlerResponse response) throws ProcessingException {
		validateSender(request, response);
	}

	// Same method can be used for "handleRequestType" and
	// "handleStatusResponseType" validations
	private void validateSender(SAML2HandlerRequest request,
			SAML2HandlerResponse response) throws ProcessingException {
		
		LOGGERSLF4J.debug("validateSender:01");
		
		if (!isSupportsSignature(request)) {
			return;
		}

		Map<String, Object> requestOptions = request.getOptions();
		PicketLinkAuditHelper auditHelper = (PicketLinkAuditHelper) requestOptions
				.get(GeneralConstants.AUDIT_HELPER);
		Boolean ignoreSignatures = (Boolean) requestOptions
				.get(GeneralConstants.IGNORE_SIGNATURES);
		if (ignoreSignatures == Boolean.TRUE) {
			return;
		}

		LOGGERSLF4J.debug("validateSender:02");
		
		Document signedDocument = request.getRequestDocument();

		if (logger.isTraceEnabled()) {
			logger.trace("Going to validate signature for: "
					+ DocumentUtil.asString(signedDocument));
		}

		PublicKey publicKey = (PublicKey) request.getOptions().get(
				GeneralConstants.SENDER_PUBLIC_KEY);
		try {
			boolean isValid;

			HTTPContext httpContext = (HTTPContext) request.getContext();
			boolean isPost = httpContext.getRequest().getMethod()
					.equalsIgnoreCase("POST");

			logger.trace("HTTP method for validating response: "
					+ httpContext.getRequest().getMethod());

			LOGGERSLF4J.debug("isPost:" + isPost);

			if (isPost) {
				isValid = verifyPostBindingSignature(signedDocument, publicKey);
			} else {
				isValid = verifyRedirectBindingSignature(httpContext, publicKey);
			}

			LOGGERSLF4J.debug("validateSender:03:"+isValid);
			
			if (!isValid) {
				if (auditHelper != null) {
					PicketLinkAuditEvent auditEvent = new PicketLinkAuditEvent(
							AuditLevel.INFO);
					auditEvent.setWhoIsAuditing((String) requestOptions
							.get(GeneralConstants.CONTEXT_PATH));
					auditEvent
							.setType(PicketLinkAuditEventType.ERROR_SIG_VALIDATION);
					auditHelper.audit(auditEvent);
				}

				throw constructSignatureException();
			}
		} catch (ProcessingException pe) {
			if (auditHelper != null) {
				PicketLinkAuditEvent auditEvent = new PicketLinkAuditEvent(
						AuditLevel.INFO);
				auditEvent.setWhoIsAuditing((String) requestOptions
						.get(GeneralConstants.CONTEXT_PATH));
				auditEvent
						.setType(PicketLinkAuditEventType.ERROR_SIG_VALIDATION);
				auditHelper.audit(auditEvent);
			}
			response.setError(SAML2HandlerErrorCodes.SIGNATURE_INVALID,
					"Signature Validation Failed");
			throw pe;
		}
	}

	private boolean verifyPostBindingSignature(Document signedDocument,
			PublicKey publicKey) throws ProcessingException {
		try {
			return this.saml2Signature.validate(signedDocument, publicKey);
		} catch (Exception e) {
			logger.samlHandlerErrorValidatingSignature(e);
			throw logger.samlHandlerInvalidSignatureError();
		}
	}

	/**
	 * <p>
	 * Validates the signature for SAML tokens received via HTTP Redirect
	 * Binding.
	 * </p>
	 * 
	 * @param httpContext
	 * @throws org.picketlink.identity.federation.core.saml.v2.exceptions.IssuerNotTrustedException
	 * @throws ProcessingException
	 */
	private boolean verifyRedirectBindingSignature(HTTPContext httpContext,
			PublicKey publicKey) throws ProcessingException {
		try {
			String queryString = httpContext.getRequest().getQueryString();

			 

			byte[] sigValue;

			sigValue = GOSTRedirectBindingSignatureUtil
					.getSignatureValueFromSignedURL(queryString);

			if (sigValue == null) {
				throw logger.samlHandlerSignatureNotPresentError();
			}

			return GOSTRedirectBindingSignatureUtil.validateSignature(
					queryString, publicKey, sigValue);

		} catch (Exception e) {
			throw logger.samlHandlerSignatureValidationError(e);
		}
	}

	private ProcessingException constructSignatureException() {
		return new ProcessingException(
				logger.samlHandlerSignatureValidationFailed());
	}

}
