package ru.spb.iac.cud.sts.web.handlers;

import java.net.URI;
import java.security.KeyPair;
import java.security.Principal;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.namespace.QName;

import org.picketlink.common.PicketLinkLogger;
import org.picketlink.common.PicketLinkLoggerFactory;
import org.picketlink.common.constants.WSTrustConstants;
import org.picketlink.common.exceptions.ParsingException;
import org.picketlink.common.exceptions.ProcessingException;
import org.picketlink.common.exceptions.fed.WSTrustException;
import org.picketlink.common.util.Base64;
import org.picketlink.common.util.DocumentUtil;
import org.picketlink.identity.federation.core.saml.v1.SAML11Constants;
import org.picketlink.identity.federation.core.saml.v2.util.SignatureUtil;
import org.picketlink.identity.federation.core.sts.PicketLinkCoreSTS;
import org.picketlink.identity.federation.core.wstrust.ClaimsProcessor;
import org.picketlink.identity.federation.core.wstrust.STSConfiguration;
import org.picketlink.identity.federation.core.wstrust.SecurityToken;
import org.picketlink.identity.federation.core.wstrust.WSTrustRequestContext;
import org.picketlink.identity.federation.core.wstrust.WSTrustRequestHandler;
import org.picketlink.identity.federation.core.wstrust.WSTrustUtil;
import org.picketlink.identity.federation.core.wstrust.wrappers.RequestSecurityToken;
import org.picketlink.identity.federation.core.wstrust.wrappers.RequestSecurityTokenResponse;
import org.picketlink.identity.federation.ws.policy.AppliesTo;
import org.picketlink.identity.federation.ws.trust.BinarySecretType;
import org.picketlink.identity.federation.ws.trust.ClaimsType;
import org.picketlink.identity.federation.ws.trust.ComputedKeyType;
import org.picketlink.identity.federation.ws.trust.EntropyType;
import org.picketlink.identity.federation.ws.trust.RequestedProofTokenType;
import org.picketlink.identity.federation.ws.trust.RequestedSecurityTokenType;
import org.picketlink.identity.federation.ws.trust.RequestedTokenCancelledType;
import org.picketlink.identity.federation.ws.trust.StatusType;
import org.picketlink.identity.federation.ws.trust.UseKeyType;
import org.picketlink.identity.xmlsec.w3.xmldsig.KeyInfoType;
import org.picketlink.identity.xmlsec.w3.xmldsig.KeyValueType;
import org.picketlink.identity.xmlsec.w3.xmldsig.X509DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ru.spb.iac.cud.idp.web.util.GOSTXMLEncryptionUtil;
import ru.spb.iac.cud.idp.web.util.GOSTXMLSignatureUtil;
import ru.spb.iac.cud.sts.util.CUDWSTrustUtil;

/**
 * <p>
 * Default implementation of the {@code WSTrustRequestHandler} interface. It
 * creates the request context containing the original WS-Trust request as well
 * as any information that may be relevant to the token processing, and
 * delegates the actual token handling processing to the appropriate
 * {@code SecurityTokenProvider}.
 * </p>
 * 
 * @author
 * @author
 */

 public class GOSTStandardRequestHandler implements WSTrustRequestHandler {

	private static final PicketLinkLogger LOGGER = PicketLinkLoggerFactory
			.getLogger();

	private static final Logger LOGGERSLF4J = LoggerFactory
			.getLogger(GOSTStandardRequestHandler.class);

	private static long KEY_SIZE = 128;

	private STSConfiguration configuration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.picketlink.identity.federation.core.wstrust.WSTrustRequestHandler
	 * #initialize(
	 * org.picketlink.identity.federation.core.wstrust.STSConfiguration)
	 */
	public void initialize(STSConfiguration configuration) {

		this.configuration = configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	public RequestSecurityTokenResponse issue(RequestSecurityToken request,
			Principal callerPrincipal) throws WSTrustException {

		 

		LOGGER.trace("Issuing token for principal " + callerPrincipal);

		

		// first try to obtain the security token provider using the applies-to
		// contents.
		AppliesTo appliesTo = request.getAppliesTo();
		X509Certificate providerCertificate = null;
		PublicKey providerPublicKey = null;
		if (appliesTo != null) {
			String serviceName = WSTrustUtil.parseAppliesTo(appliesTo);

			 

			if (serviceName != null) {
				String tokenTypeFromServiceName = configuration
						.getTokenTypeForService(serviceName);

				 

				if (request.getTokenType() == null
						&& tokenTypeFromServiceName != null) {
					request.setTokenType(URI.create(tokenTypeFromServiceName));
				}

				// закомментили
				// providerCertificate =
				// this.configuration.getServiceProviderCertificate(serviceName);

				// если придёт appliesTo, то
				// PicketLinkSTSConfiguration.getServiceProviderCertificate ищет
				// соответствия по Endpoint в picketlink-sts.xml
				// и если не найдет выдаёт ошибку
				// "Error obtaining public key for service"

				// для appliesTo надо
				/*
				 * <ServiceProviders> <ServiceProvider
				 * Endpoint="http://cudvm/CudServicesSTS/PicketLinkSTS"
				 * TokenType=
				 * "http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0"
				 * TruststoreAlias="cudvm_export"/> </ServiceProviders>
				 */

				 

				if (providerCertificate != null) {
					providerPublicKey = providerCertificate.getPublicKey();
				}

				// provider =
				// this.configuration.getProviderForService(serviceName);
				/*
				 * if (provider != null) {
				 * request.setTokenType(URI.create(this.configuration
				 * .getTokenTypeForService(serviceName))); providerPublicKey =
				 * this.configuration.getServiceProviderPublicKey(serviceName);
				 * }
				 */
			}
		}

		 
		// if applies-to is not available or if no provider was found for the
		// service, use the token type.
		/*
		 * if (provider == null && request.getTokenType() != null) { provider =
		 * this
		 * .configuration.getProviderForTokenType(request.getTokenType().toString
		 * ()); } else if (appliesTo == null && request.getTokenType() == null)
		 * throw new WSTrustException(
		 * "Either AppliesTo or TokenType must be present in a security token request"
		 * );
		 * 
		 * if (provider != null) {
		 */
		// create the request context and delegate token generation to the
		// provider.
		WSTrustRequestContext requestContext = new WSTrustRequestContext(
				request, callerPrincipal);
		requestContext.setTokenIssuer(this.configuration.getSTSName());
		if (request.getLifetime() == null
				&& this.configuration.getIssuedTokenTimeout() != 0) {
			// if no lifetime has been specified, use the configured timeout
			// value.
			LOGGER.stsTokenTimeoutNotSpecified();
			// request.setLifetime(WSTrustUtil.createDefaultLifetime(this.configuration.getIssuedTokenTimeout()));
			request.setLifetime(CUDWSTrustUtil
					.createDefaultLifetime(this.configuration
							.getIssuedTokenTimeout()));

		}
		requestContext.setServiceProviderPublicKey(providerPublicKey);

		// process the claims if needed.
		if (request.getClaims() != null) {
			ClaimsType claims = request.getClaims();
			ClaimsProcessor processor = this.configuration
					.getClaimsProcessor(claims.getDialect());
			// if there is a processor, process the claims and set the resulting
			// attributes in the context.
			if (processor != null) {
				requestContext.setClaimedAttributes(processor.processClaims(
						claims, callerPrincipal));
			} else if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Claims have been specified in the request but no processor was found for dialect "
						+ claims.getDialect());
			}
		}

		 

		// get the OnBehalfOf principal, if one has been specified.
		if (request.getOnBehalfOf() != null) {
			Principal onBehalfOfPrincipal = WSTrustUtil
					.getOnBehalfOfPrincipal(request.getOnBehalfOf());
			requestContext.setOnBehalfOfPrincipal(onBehalfOfPrincipal);

			 
			 
		}

		// get the key type and size from the request, setting default values if
		// not specified.
		URI keyType = request.getKeyType();
		if (keyType == null) {
			LOGGER.debug("No key type could be found in the request. Using the default BEARER type.");
			keyType = URI.create(WSTrustConstants.KEY_TYPE_BEARER);
			request.setKeyType(keyType);
		}
		long keySize = request.getKeySize();
		if (keySize == 0) {
			LOGGER.debug("No key size could be found in the request. Using the default size. ("
					+ KEY_SIZE + ")");
			keySize = KEY_SIZE;
			request.setKeySize(keySize);
		}

		// get the key wrap algorithm.
		URI keyWrapAlgo = request.getKeyWrapAlgorithm();

		// create proof-of-possession token and server entropy (if needed).
		RequestedProofTokenType requestedProofToken = null;
		EntropyType serverEntropy = null;

		if (WSTrustConstants.KEY_TYPE_SYMMETRIC.equalsIgnoreCase(keyType
				.toString())) {
			// symmetric key case: if client entropy is found, compute a key. If
			// not, generate a new key.
			requestedProofToken = new RequestedProofTokenType();

			byte[] serverSecret = WSTrustUtil
					.createRandomSecret((int) keySize / 8);
			BinarySecretType serverBinarySecret = new BinarySecretType();
			serverBinarySecret.setType(WSTrustConstants.BS_TYPE_NONCE);
			serverBinarySecret.setValue(Base64.encodeBytes(serverSecret)
					.getBytes());

			byte[] clientSecret = null;
			EntropyType clientEntropy = request.getEntropy();
			if (clientEntropy != null) {
				clientSecret = Base64.decode(new String(WSTrustUtil
						.getBinarySecret(clientEntropy)));
				serverEntropy = new EntropyType();
				serverEntropy.addAny(serverBinarySecret);
			}

			if (clientSecret != null && clientSecret.length != 0) {
				// client secret has been specified - combine it with the sts
				// secret.
				requestedProofToken.add(new ComputedKeyType(
						WSTrustConstants.CK_PSHA1));
				byte[] combinedSecret = null;
				try {
					combinedSecret = Base64.encodeBytes(
							WSTrustUtil.P_SHA1(clientSecret, serverSecret,
									(int) keySize / 8)).getBytes();
				} catch (Exception e) {
					throw LOGGER.wsTrustCombinedSecretKeyError(e);
				}
				requestContext.setProofTokenInfo(WSTrustUtil.createKeyInfo(
						combinedSecret, providerPublicKey, keyWrapAlgo,
						providerCertificate));
			} else {
				// client secret has not been specified - use the sts secret
				// only.
				requestedProofToken.add(serverBinarySecret);
				requestContext.setProofTokenInfo(WSTrustUtil.createKeyInfo(
						serverBinarySecret.getValue(), providerPublicKey,
						keyWrapAlgo, providerCertificate));
			}
		} else if (WSTrustConstants.KEY_TYPE_PUBLIC.equalsIgnoreCase(keyType
				.toString())) {
			// try to locate the client cert in the keystore using the caller
			// principal as the alias.
			Certificate certificate = this.configuration
					.getCertificate(callerPrincipal.getName());
			if (certificate != null) {
				requestContext.setProofTokenInfo(WSTrustUtil
						.createKeyInfo(certificate));
			} else if (request.getUseKey() != null) {
				UseKeyType useKeyType = request.getUseKey();
				List<Object> theList = useKeyType.getAny();
				for (Object value : theList) {
					if (value instanceof Element) {
						Element keyElement = (Element) value;
						String elementName = (keyElement).getLocalName();
						// if the specified key is a X509 certificate we must
						// insert it into a X509Data element.
						if (elementName.equals("X509Certificate")) {
							X509DataType data = new X509DataType();
							data.add(value);
							value = data;
						} else if (elementName.equals("KeyValue")) {
							KeyValueType keyValue = null;
							Element child = DocumentUtil
									.getChildElement(
											keyElement,
											new QName(
													WSTrustConstants.XMLDSig.RSA_KEYVALUE));
							if (child != null) {
								try {
									keyValue = SignatureUtil
											.getRSAKeyValue(child);
								} catch (ParsingException e) {
									throw LOGGER.stsError(e);
								}
							}
							if (keyValue == null && child == null) {
								child = DocumentUtil
										.getChildElement(
												keyElement,
												new QName(
														WSTrustConstants.XMLDSig.DSA_KEYVALUE));
								if (child != null) {
									try {
										keyValue = SignatureUtil
												.getDSAKeyValue(child);
									} catch (ParsingException e) {
										throw LOGGER.stsError(e);
									}
								}
								value = keyValue;
							}
						}
						KeyInfoType keyInfo = new KeyInfoType();
						keyInfo.addContent(value);
						requestContext.setProofTokenInfo(keyInfo);
					} else if (value instanceof KeyInfoType) {
						requestContext.setProofTokenInfo((KeyInfoType) value);
					} else {
						throw new WSTrustException(LOGGER.unsupportedType(value
								.toString()));
					}
				}
			} else {
				throw LOGGER.wsTrustClientPublicKeyError();
			}
		}

		// issue the security token using the constructed context.
		try {
			if (request.getTokenType() != null) {
				requestContext.setTokenType(request.getTokenType().toString());
			}
			PicketLinkCoreSTS sts = PicketLinkCoreSTS.instance();
			sts.initialize(configuration);
			sts.issueToken(requestContext);
			// provider.issueToken(requestContext);
		} catch (ProcessingException e) {
			throw LOGGER.stsError(e);
		}

		if (requestContext.getSecurityToken() == null) {
			throw new WSTrustException(
					LOGGER.nullValueError("Token issued by STS"));
		}

		// construct the ws-trust security token response.
		RequestedSecurityTokenType requestedSecurityToken = new RequestedSecurityTokenType();

		SecurityToken contextSecurityToken = requestContext.getSecurityToken();
		if (contextSecurityToken == null) {
			throw new WSTrustException(
					LOGGER.nullValueError("Security Token from context"));
		}

		requestedSecurityToken.add(contextSecurityToken.getTokenValue());

		RequestSecurityTokenResponse response = new RequestSecurityTokenResponse();
		if (request.getContext() != null) {
			response.setContext(request.getContext());
		}

		response.setTokenType(request.getTokenType());
		response.setLifetime(request.getLifetime());
		response.setAppliesTo(appliesTo);
		response.setKeySize(keySize);
		response.setKeyType(keyType);
		response.setRequestedSecurityToken(requestedSecurityToken);

		if (requestedProofToken != null) {
			response.setRequestedProofToken(requestedProofToken);
		}
		if (serverEntropy != null) {
			response.setEntropy(serverEntropy);
		}

		// set the attached and unattached references.
		if (requestContext.getAttachedReference() != null) {
			response.setRequestedAttachedReference(requestContext
					.getAttachedReference());
		}
		if (requestContext.getUnattachedReference() != null) {
			response.setRequestedUnattachedReference(requestContext
					.getUnattachedReference());
		}

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.picketlink.identity.federation.core.wstrust.WSTrustRequestHandler
	 * #renew(
	 * org.picketlink.identity.federation.core.wstrust.wrappers.RequestSecurityToken
	 * , java.security.Principal)
	 */
	public RequestSecurityTokenResponse renew(RequestSecurityToken request,
			Principal callerPrincipal) throws WSTrustException {
		// first validate the provided token signature to make sure it has been
		// issued by this STS and hasn't been
		// tempered.

		 

		LOGGER.trace("Validating token for renew request "
				+ request.getContext());

		if (request.getRenewTargetElement() == null) {
			throw new WSTrustException(LOGGER.nullValueError("renew target"));
		}

		Node securityToken = request.getRenewTargetElement().getFirstChild();
		if (securityToken == null) {
			throw new WSTrustException(LOGGER.nullValueError("security token"));
		}

		 

		/*
		 * try{ Transformer transformer =
		 * TransformerFactory.newInstance().newTransformer();
		 * transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
		 * "yes"); // копирование содержимого XML-документа в поток
		 * transformer.transform(new DOMSource(oldAssertionElement), new
		 * StreamResult(System.out)); }catch(Exception e){ }
		 */
		/*
		 * SecurityTokenProvider provider =
		 * this.configuration.getProviderForTokenElementNS
		 * (securityToken.getLocalName(), securityToken.getNamespaceURI()); if
		 * (provider == null) throw new
		 * WSTrustException("No SecurityTokenProvider configured for " +
		 * securityToken.getNamespaceURI() + ":" +
		 * securityToken.getLocalName());
		 */

		setupIDAttribute(securityToken);

		if (this.configuration.signIssuedToken()
				&& this.configuration.getSTSKeyPair() != null) {
			KeyPair keyPair = this.configuration.getSTSKeyPair();
			try {
				Document tokenDocument = DocumentUtil.createDocument();
				Node importedNode = tokenDocument.importNode(securityToken,
						true);
				tokenDocument.appendChild(importedNode);
				// XMLSignatureUtil.propagateIDAttributeSetup(securityToken,
				// tokenDocument.getDocumentElement());
				// if (!XMLSignatureUtil.validate(tokenDocument,
				// keyPair.getPublic()))

				GOSTXMLSignatureUtil.propagateIDAttributeSetup(securityToken,
						tokenDocument.getDocumentElement());
				if (!GOSTXMLSignatureUtil.validate(tokenDocument,
						keyPair.getPublic())) {
					throw new WSTrustException(LOGGER.signatureInvalidError(
							"Validation failure during renewal", null));
				}
			} catch (Exception e) {
				throw new WSTrustException(LOGGER.signatureInvalidError(
						"Validation failure during renewal:", e));
			}
		} else {
			LOGGER.stsSecurityTokenSignatureNotVerified();
		}

		// set default values where needed.
		if (request.getLifetime() == null
				&& this.configuration.getIssuedTokenTimeout() != 0) {
			// if no lifetime has been specified, use the configured timeout
			// value.
			LOGGER.stsTokenTimeoutNotSpecified();
			// request.setLifetime(WSTrustUtil.createDefaultLifetime(this.configuration.getIssuedTokenTimeout()));
			request.setLifetime(CUDWSTrustUtil
					.createDefaultLifetime(this.configuration
							.getIssuedTokenTimeout()));

		}

		// create a context and dispatch to the proper security token provider
		// for renewal.
		WSTrustRequestContext context = new WSTrustRequestContext(request,
				callerPrincipal);
		context.setTokenIssuer(this.configuration.getSTSName());
		// if the renew request was made on behalf of another identity, get the
		// principal of that identity.
		if (request.getOnBehalfOf() != null) {
			Principal onBehalfOfPrincipal = WSTrustUtil
					.getOnBehalfOfPrincipal(request.getOnBehalfOf());
			context.setOnBehalfOfPrincipal(onBehalfOfPrincipal);
		}
		try {
			if (securityToken != null) {
				String ns = securityToken.getNamespaceURI();

				context.setQName(new QName(ns, securityToken.getLocalName()));
			}
			PicketLinkCoreSTS sts = PicketLinkCoreSTS.instance();
			sts.initialize(configuration);
			sts.renewToken(context);
			// provider.renewToken(context);
		} catch (ProcessingException e) {
			throw new WSTrustException(e.getMessage(), e);
		}

		// create the WS-Trust response with the renewed token.
		RequestedSecurityTokenType requestedSecurityToken = new RequestedSecurityTokenType();
		SecurityToken contextSecurityToken = context.getSecurityToken();
		if (contextSecurityToken == null) {
			throw new WSTrustException(
					LOGGER.nullValueError("Security Token from context"));
		}
		requestedSecurityToken.add(contextSecurityToken.getTokenValue());

		RequestSecurityTokenResponse response = new RequestSecurityTokenResponse();
		if (request.getContext() != null) {
			response.setContext(request.getContext());
		}
		response.setTokenType(request.getTokenType());
		response.setLifetime(request.getLifetime());
		response.setRequestedSecurityToken(requestedSecurityToken);
		if (context.getAttachedReference() != null) {
			response.setRequestedAttachedReference(context
					.getAttachedReference());
		}
		if (context.getUnattachedReference() != null) {
			response.setRequestedUnattachedReference(context
					.getUnattachedReference());
		}
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.picketlink.identity.federation.core.wstrust.WSTrustRequestHandler
	 * #validate(
	 * org.picketlink.identity.federation.core.wstrust.wrappers.RequestSecurityToken
	 * , java.security.Principal)
	 */
	public RequestSecurityTokenResponse validate(RequestSecurityToken request,
			Principal callerPrincipal) throws WSTrustException {

		LOGGER.trace("Started validation for request " + request.getContext());

		if (request.getValidateTargetElement() == null) {
			throw new WSTrustException(
					LOGGER.nullValueError("request does not have a validate target. Unable to validate token"));
		}

		if (request.getTokenType() == null) {
			request.setTokenType(URI.create(WSTrustConstants.STATUS_TYPE));
		}

		Node securityToken = request.getValidateTargetElement().getFirstChild();
		if (securityToken == null) {
			throw new WSTrustException(
					LOGGER.nullValueError("security token:Unable to validate token"));
		}

		setupIDAttribute(securityToken);

		WSTrustRequestContext context = new WSTrustRequestContext(request,
				callerPrincipal);
		// if the validate request was made on behalf of another identity, get
		// the principal of that identity.
		if (request.getOnBehalfOf() != null) {
			Principal onBehalfOfPrincipal = WSTrustUtil
					.getOnBehalfOfPrincipal(request.getOnBehalfOf());
			context.setOnBehalfOfPrincipal(onBehalfOfPrincipal);
		}
		StatusType status = null;

		// validate the security token digital signature.
		if (this.configuration.signIssuedToken()
				&& this.configuration.getSTSKeyPair() != null) {
			KeyPair keyPair = this.configuration.getSTSKeyPair();
			try {
				if (LOGGER.isTraceEnabled()) {
					try {
						LOGGER.trace("Going to validate signature for: "
								+ DocumentUtil.getNodeAsString(securityToken));
					} catch (Exception e) {
					}
				}
				Document tokenDocument = DocumentUtil.createDocument();
				Node importedNode = tokenDocument.importNode(securityToken,
						true);
				tokenDocument.appendChild(importedNode);
				// XMLSignatureUtil.propagateIDAttributeSetup(securityToken,
				// tokenDocument.getDocumentElement());
				// if (!XMLSignatureUtil.validate(tokenDocument,
				// keyPair.getPublic())) {

				GOSTXMLSignatureUtil.propagateIDAttributeSetup(securityToken,
						tokenDocument.getDocumentElement());
				if (!GOSTXMLSignatureUtil.validate(tokenDocument,
						keyPair.getPublic())) {

					status = new StatusType();
					status.setCode(WSTrustConstants.STATUS_CODE_INVALID);
					status.setReason("Validation failure: digital signature is invalid");
				}
			} catch (Exception e) {
				status = new StatusType();
				status.setCode(WSTrustConstants.STATUS_CODE_INVALID);
				status.setReason("Validation failure: unable to verify digital signature: "
						+ e.getMessage());
			}
		} else {
			LOGGER.stsSecurityTokenSignatureNotVerified();
		}

		// if the signature is valid, then let the provider perform any
		// additional validation checks.
		if (status == null) {
			LOGGER.trace("Delegating token validation to token provider. Token NS: "
					+ securityToken.getNamespaceURI()
					+ " ::LocalName: "
					+ securityToken.getLocalName());
			try {
				if (securityToken != null) {
					context.setQName(new QName(securityToken.getNamespaceURI(),
							securityToken.getLocalName()));
				}
				PicketLinkCoreSTS sts = PicketLinkCoreSTS.instance();
				sts.initialize(configuration);
				sts.validateToken(context);
				// provider.validateToken(context);
			} catch (ProcessingException e) {
				throw LOGGER.stsError(e);
			}
			status = context.getStatus();
		}

		// construct and return the response.
		RequestSecurityTokenResponse response = new RequestSecurityTokenResponse();
		if (request.getContext() != null) {
			response.setContext(request.getContext());
		}
		response.setTokenType(request.getTokenType());
		response.setStatus(status);

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.picketlink.identity.federation.core.wstrust.WSTrustRequestHandler
	 * #cancel(
	 * org.picketlink.identity.federation.core.wstrust.wrappers.RequestSecurityToken
	 * , java.security.Principal)
	 */
	public RequestSecurityTokenResponse cancel(RequestSecurityToken request,
			Principal callerPrincipal) throws WSTrustException {
		// check if request contains all required elements.
		if (request.getCancelTargetElement() == null) {
			throw new WSTrustException(
					LOGGER.nullValueError("request does not have a cancel target. Unable to cancel token"));
		}

		// obtain the token provider that will handle the request.
		Node securityToken = request.getCancelTargetElement().getFirstChild();
		if (securityToken == null) {
			throw new WSTrustException(
					LOGGER.nullValueError("security token. Unable to cancel token"));
		}

		/*
		 * SecurityTokenProvider provider =
		 * this.configuration.getProviderForTokenElementNS
		 * (securityToken.getLocalName(), securityToken.getNamespaceURI()); if
		 * (provider == null) throw new
		 * WSTrustException("No SecurityTokenProvider configured for " +
		 * securityToken.getNamespaceURI() + ":" +
		 * securityToken.getLocalName());
		 */

		// create a request context and dispatch to the provider.
		WSTrustRequestContext context = new WSTrustRequestContext(request,
				callerPrincipal);
		// if the cancel request was made on behalf of another identity, get the
		// principal of that identity.
		if (request.getOnBehalfOf() != null) {
			Principal onBehalfOfPrincipal = WSTrustUtil
					.getOnBehalfOfPrincipal(request.getOnBehalfOf());
			context.setOnBehalfOfPrincipal(onBehalfOfPrincipal);
		}
		try {
			if (securityToken != null) {
				context.setQName(new QName(securityToken.getNamespaceURI(),
						securityToken.getLocalName()));
			}
			PicketLinkCoreSTS sts = PicketLinkCoreSTS.instance();
			sts.initialize(configuration);
			sts.cancelToken(context);
			// provider.cancelToken(context);
		} catch (ProcessingException e) {
			throw LOGGER.stsError(e);
		}

		// if no exception has been raised, the token has been successfully
		// canceled.
		RequestSecurityTokenResponse response = new RequestSecurityTokenResponse();
		if (request.getContext() != null) {
			response.setContext(request.getContext());
		}
		response.setRequestedTokenCancelled(new RequestedTokenCancelledType());
		return response;
	}

	public Document postProcess(Document rstrDocument,
			RequestSecurityToken request) throws WSTrustException {

		 

		if (WSTrustConstants.ISSUE_REQUEST.equals(request.getRequestType()
				.toString())
				|| WSTrustConstants.RENEW_REQUEST.equals(request
						.getRequestType().toString())) {
			rstrDocument = DocumentUtil.normalizeNamespaces(rstrDocument);

			// Sign the security token
			if (this.configuration.signIssuedToken()
					&& this.configuration.getSTSKeyPair() != null) {
				KeyPair keyPair = this.configuration.getSTSKeyPair();
				try {
					Node rst = rstrDocument.getElementsByTagNameNS(
							WSTrustConstants.BASE_NAMESPACE,
							"RequestedSecurityToken").item(0);
					Element tokenElement = (Element) rst.getFirstChild();

					LOGGER.trace("NamespaceURI of element to be signed: "
							+ tokenElement.getNamespaceURI());

					LOGGER.debug("postProcess:02" + tokenElement.getLocalName());

					// Is there a certificate?
					X509Certificate x509Certificate = null;
					String signingCertificateAlias = this.configuration
							.getSigningCertificateAlias();
					// если в picketlink-sts.xml
					// убрать <Auth Key="X509CERTIFICATE" Value="cudvm_export"
					// />
					// то signingCertificateAlias = null

					/*
					 * <Auth Key="X509CERTIFICATE" Value="cudvm_export" />
					 * PicketLinkSTSConfiguration public String
					 * getSigningCertificateAlias() { if (this.certificateAlias
					 * == null) { this.certificateAlias =
					 * ((String)this.trustManager
					 * .getAdditionalOption("X509CERTIFICATE")); } return
					 * this.certificateAlias; }
					 */

					 

					if (signingCertificateAlias != null) {
						// вернёт null
						// в KeyStoreKeyManager мы отключили метод
						// getCertificate
						// x509Certificate участвует только в KeyInfo - и нам не
						// нужен,
						// т.к. в KeyInfo мы вставляем только PublicKey
						x509Certificate = (X509Certificate) this.configuration
								.getCertificate(signingCertificateAlias);
					}

					 

					// Set the CanonicalizationMethod if any
					// XMLSignatureUtil.setCanonicalizationMethodType(configuration.getXMLDSigCanonicalizationMethod());
					GOSTXMLSignatureUtil
							.setCanonicalizationMethodType(this.configuration
									.getXMLDSigCanonicalizationMethod());
					// http://www.w3.org/2001/10/xml-exc-c14n#WithComments
					 

					String set_digestMethod = "http://www.w3.org/2001/04/xmldsig-more#gostr3411";
					String set_signatureMethod = "http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411";

					// rstrDocument = XMLSignatureUtil.sign(rstrDocument,
					// tokenElement, keyPair, DigestMethod.SHA1,
					// signatureMethod,
					// setupIDAttribute(tokenElement),x509Certificate);

					rstrDocument = GOSTXMLSignatureUtil.sign(rstrDocument,
							tokenElement, keyPair, set_digestMethod,
							set_signatureMethod,
							setupIDAttribute(tokenElement), x509Certificate);

					// if (LOGGER.isTraceEnabled()) {
					try {
						/*
						 * Document tokenDocument =
						 * DocumentUtil.createDocument();
						 * tokenDocument.appendChild
						 * (tokenDocument.importNode(tokenElement, true));
						 * LOGGER.trace("valid=" +
						 * XMLSignatureUtil.validate(tokenDocument,
						 * keyPair.getPublic()));
						 */

						// tokenElement не содержит сигнатуру
						// он заменён (replaceChild) на новый элемент с
						// сигнатурой
						// поэтому надо получить ссылку на этот новый элемент с
						// сигнатурой

						/*Element tokenElement_ver = (Element) rst
								.getFirstChild();

						Document tokenDocument = DocumentUtil.createDocument();
						tokenDocument.appendChild(tokenDocument.importNode(
								tokenElement_ver, true));

						GOSTXMLSignatureUtil.propagateIDAttributeSetup(
								tokenElement_ver,
								tokenDocument.getDocumentElement());

						// LOGGER.debug("valid=" +
						// GOSTXMLSignatureUtil.validate(tokenDocument,
						// keyPair.getPublic()));*/

					} catch (Exception ignore) {
						LOGGER.debug("ignore"); 
					}
					 
				} catch (Exception e) {
					throw new WSTrustException(LOGGER.signatureError(e));
				}
			}

			// мы не шифруем токен (вместо этого используем https)
			if (this.configuration.encryptIssuedToken()) {
				// get the public key that will be used to encrypt the token.
				PublicKey providerPublicKey = null;
				if (request.getAppliesTo() != null) {
					String serviceName = WSTrustUtil.parseAppliesTo(request
							.getAppliesTo());

					LOGGER.trace("Locating public key for " + serviceName);

					if (serviceName != null) {
						providerPublicKey = this.configuration
								.getServiceProviderPublicKey(serviceName);
					}
				}
				if (providerPublicKey == null) {
					LOGGER.stsSecurityTokenShouldBeEncrypted();
				} else {
					// generate the secret key.
					long keySize = request.getKeySize();
					byte[] secret = WSTrustUtil
							.createRandomSecret((int) keySize / 8);
					SecretKey secretKey = new SecretKeySpec(secret, "AES");

					// encrypt the security token.
					Node rst = rstrDocument.getElementsByTagNameNS(
							WSTrustConstants.BASE_NAMESPACE,
							"RequestedSecurityToken").item(0);
					Element tokenElement = (Element) rst.getFirstChild();
					try {
						// XMLEncryptionUtil.encryptElement(rstrDocument,
						// tokenElement, providerPublicKey, secretKey,
						// (int) keySize);
						GOSTXMLEncryptionUtil.encryptElement(rstrDocument,
								tokenElement, providerPublicKey, secretKey,
								(int) keySize);

					} catch (ProcessingException e) {
						throw new WSTrustException(
								LOGGER.encryptProcessError(e));
					}
				}
			}
		}

		return rstrDocument;
	}

	/**
	 * Setup the ID attribute in the provided node if it's a SAML Assertion
	 * element.
	 * 
	 * @param node
	 *            The node representing the SAML Assertion
	 * @return A reference to the correct ID
	 */
	private static String setupIDAttribute(Node node) {
		if (node instanceof Element) {
			Element assertion = (Element) node;
			if (assertion.getLocalName().equals("Assertion")) {
				if (assertion.getNamespaceURI().equals(
						WSTrustConstants.SAML2_ASSERTION_NS)
						&& assertion.hasAttribute("ID")) {
					assertion.setIdAttribute("ID", true);
					return "#" + assertion.getAttribute("ID");
				} else if (assertion.getNamespaceURI().equals(
						SAML11Constants.ASSERTION_11_NSURI)
						&& assertion.hasAttribute(SAML11Constants.ASSERTIONID)) {
					assertion.setIdAttribute(SAML11Constants.ASSERTIONID, true);
					return "#"
							+ assertion
									.getAttribute(SAML11Constants.ASSERTIONID);
				}
			}
		}
		return "";
	}

}
