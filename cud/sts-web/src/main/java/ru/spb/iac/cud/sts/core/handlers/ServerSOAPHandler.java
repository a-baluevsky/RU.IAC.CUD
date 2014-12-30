package ru.spb.iac.cud.sts.core.handlers;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.xml.security.transforms.Transforms;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.picketlink.common.constants.WSTrustConstants;
import org.picketlink.common.util.Base64;
import org.picketlink.identity.federation.core.saml.v2.util.DocumentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.spb.iac.cud.context.ContextAccessSTSManager;
import ru.spb.iac.cud.context.ContextIDPUtilManager;
import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.idp.web.util.GOSTSignatureUtil;
import ru.spb.iac.cud.items.AuthMode;
import ru.spb.iac.cud.services.web.init.Configuration;
import ru.spb.iac.pl.sp.key.KeyStoreKeyManager;

 public class ServerSOAPHandler implements SOAPHandler<SOAPMessageContext> {

	private static final String authTypePassword = "urn:oasis:names:tc:SAML:2.0:ac:classes:password";
	private static final String authTypeX509 = "urn:oasis:names:tc:SAML:2.0:ac:classes:X509";
	private static String WSSENS ="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
	
	private static PublicKey publicKey = null;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ServerSOAPHandler.class);

	public Set<QName> getHeaders() {
		return null;
	}

	public void close(MessageContext mc) {
		LOGGER.debug("close");
	}

	public boolean handleFault(SOAPMessageContext mc) {
		return true;
	}

	private static Provider xmlDSigProvider;
	
	private static XMLSignatureFactory fac;
	
	static {
		xmlDSigProvider = new ru.CryptoPro.JCPxml.dsig.internal.dom.XMLDSigRI();

		fac = XMLSignatureFactory.getInstance(
				"DOM", xmlDSigProvider);

	}
	
	public boolean handleMessage(SOAPMessageContext mc) {

		LOGGER.debug("handleMessage:01:"
				+ mc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY));

		// приём soap с подписью usernametoken в header
		// с использованием transform

		PublicKey publicKey2 = null;
		String loginUser = null;

		try {
			HttpServletRequest req = (HttpServletRequest) mc
					.get(MessageContext.SERVLET_REQUEST);
			HttpSession http_session = req.getSession();

			String system_principal = null;

			String user_principal = null;

			String user_obo_principal = null;

			String user_obo_auth_type = null;
			
			String user_password = null;

			X509Certificate userCertX = null;

			SOAPMessage soapMessage = mc.getMessage();
			SOAPHeader soapHeader = soapMessage.getSOAPHeader();

			SOAPBody soapBody = soapMessage.getSOAPBody();

			SOAPPart soapPart = soapMessage.getSOAPPart();

			Document soapDoc = soapMessage.getSOAPPart().getEnvelope()
					.getOwnerDocument();

			// запрос
			if (Boolean.FALSE.equals(mc
					.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY))) {
 
               //здесь было создание провайдера и фабрики
				
				// signature
				NodeList signatureList = soapHeader.getElementsByTagNameNS(XMLSignature.XMLNS,
						"Signature");

				if (signatureList == null || signatureList.getLength() == 0) {
					LOGGER.debug("handleMessage:02_1");
					
					
					if(Configuration.isSignRequired()){
					   throw new GeneralFailure(
							"This service requires <dsig:Signature>, which is missing!!!");
					}
				}

				// исправление некорректной работы apache cxf
				// ReadHeadersInterceptor с дублированием id
				NodeList securityList = soapHeader.getElementsByTagNameNS(
						WSSENS,
						"Security");

				if (securityList == null || securityList.getLength() == 0) {
					LOGGER.debug("handleMessage:02_2");
					throw new GeneralFailure(
							"This service requires <Security>, which is missing!!!");
				}

				
				LOGGER.debug("handleMessage:02_3");

				// взятие ключа из хранилища доверенных

				// проверяем Body-RequestSecurityToken на UseKey
				NodeList onUseKeyList = soapBody.getElementsByTagNameNS(
						WSTrustConstants.BASE_NAMESPACE,
						"UseKey");

				// проверяем Body-RequestSecurityToken на OnBehalfOf
				NodeList onBehalfOfList = soapBody.getElementsByTagNameNS(
						WSTrustConstants.BASE_NAMESPACE,
						"OnBehalfOf");

				if (onBehalfOfList != null && onBehalfOfList.getLength() > 0) {
					LOGGER.debug("handleMessage:02_2_1");

					if (onUseKeyList != null && onUseKeyList.getLength() > 0) {
						// нельзя одновременно UseKey и OnBehalfOf
						throw new GeneralFailure(
								"This <UseKey> and <OnBehalfOf> аre not compatible!!!");
					}

					NodeList usernameTokenOnBehalfOfList = ((Element) onBehalfOfList
							.item(0)).getElementsByTagNameNS(WSSENS,
							"UsernameToken");

					if (usernameTokenOnBehalfOfList == null
							|| usernameTokenOnBehalfOfList.getLength() == 0) {
						LOGGER.debug("handleMessage:02_2_2");
						throw new GeneralFailure(
								"This <OnBehalfOf> requires <UsernameToken>, which is missing!!!");
					}

					NodeList usernameOnBehalfOfList = ((Element) usernameTokenOnBehalfOfList
							.item(0)).getElementsByTagNameNS(WSSENS, "Username");

					if (usernameOnBehalfOfList == null
							|| usernameOnBehalfOfList.getLength() == 0) {
						LOGGER.debug("handleMessage:02_2_3");
						throw new GeneralFailure(
								"This <UsernameToken> of OnBehalfOf requires <Username>, which is missing!!!");
					}

					String base64TokenId = usernameOnBehalfOfList.item(0)
							.getTextContent();

					// !!!
					// переделали на передачу не целиком токена, а только
					// token_id

					if (base64TokenId != null) {

						LOGGER.debug("handleMessage:02:" + base64TokenId);

						byte[] byteTokenID = Base64.decode(base64TokenId);

						String tokenID = new String(byteTokenID, "UTF-8");

						LOGGER.debug("handleMessage:03:" + tokenID);

						String[] arrTokenID = tokenID.split("_");

						if (arrTokenID == null || arrTokenID.length != 4) {
							throw new Exception("UserAuthToken is not valid!!!");
						}

						StringBuilder sb = new StringBuilder();

						sb.append(arrTokenID[0] + "_" + arrTokenID[1] + "_" + arrTokenID[2]);

						byte[] sigValue = Base64.decode(arrTokenID[3]);

					if(this.publicKey ==null){
						
						KeyStore ks = KeyStore.getInstance("HDImageStore",
								"JCP");
						ks.load(null, null);

						this.publicKey = ks.getCertificate("cudvm_export")
								.getPublicKey();

					}
					
						boolean tokenIDValidateResult = GOSTSignatureUtil
								.validate(sb.toString().getBytes("UTF-8"),
										sigValue, this.publicKey);

						LOGGER.debug("handleMessage:" + tokenIDValidateResult);

						user_obo_principal = arrTokenID[0].toString();
						Date expired = new Date(Long.parseLong(arrTokenID[1]));

						LOGGER.debug("handleMessage:" + user_obo_principal);

						LOGGER.debug("handleMessage:" + expired);

						if (!tokenIDValidateResult) {
							throw new Exception(
									"TokenId of OnBehalfOf is not valid!!!");
						}

						//на самом деле спорный момент
						//если токен просрочен, то можно ли на его основе получить 
						//другой токен?
						//ведь принимающая сторона ничего не знает о токене
						// и если она получает исключение, что токен просрочен -
						//то что её делать?
						//так что видимо надо убирать проверку на дату или 
						//расширять период до 1 суток
						if (new Date(System.currentTimeMillis()).after(expired)) {
							//th/row new Token/Expired("TokenId of OnBehalfOf is expired!!!");
							//th/row new Exce/ption(
							//		"TokenId of OnBehalfOf is expired!!!");
						}

						 user_obo_auth_type = arrTokenID[2].toString();
						 
					} else {
						throw new Exception("TokenId of OnBehalfOf is empty!!!");
					}

					/*
					 * //!!! //переделали на передачу не целиком токена, а
					 * только token_id
					 */

					LOGGER.debug("handleMessage:02_2_4+:" + user_obo_principal);

				}

				NodeList usernameTokenList = soapHeader.getElementsByTagNameNS(
						WSSENS, "UsernameToken");

				if (usernameTokenList == null
						|| usernameTokenList.getLength() == 0) {
					LOGGER.debug("handleMessage:02_3");
					throw new GeneralFailure(
							"This service requires UsernameToken, which is missing!!!");
				}

				if (user_obo_principal == null) {

					if (usernameTokenList.getLength() == 1) {
						// может быть
						// 1) аутентификация системы
						// 2) аутентификация пользователя по сертификату

						// ид системы должен быть обязательно
						Element el = (Element) usernameTokenList.item(0);
						String el_id = el.getAttribute("wsu:Id");

						LOGGER.debug("handleMessage:02_3_1:" + el_id);

						if ("SystemToken_1".equals(el_id)) {
							// правильно - система должна присутствовать
							NodeList usernameListSys = ((Element) usernameTokenList
									.item(0)).getElementsByTagNameNS("*",
									"Username");

							if (usernameListSys == null
									|| usernameListSys.getLength() == 0) {
								LOGGER.debug("handleMessage:02_3_1+");
								throw new GeneralFailure(
										"This service requires <Username>, which is missing!!!");
							}

							system_principal = usernameListSys.item(0)
									.getTextContent();

							LOGGER.debug("handleMessage:02_3_1_2:"
									+ system_principal);

						} else {
							throw new Exception(
									"This service requires SystemToken_1, which is missing!!!");
						}

						// смотрим - есть ли сертификат пользователя
						NodeList usernameCertList = soapHeader
								.getElementsByTagNameNS(XMLSignature.XMLNS, "X509Certificate");

						if (usernameCertList != null
								&& usernameCertList.getLength() != 0) {
							// есть
							LOGGER.debug("handleMessage:02_3_2");

							if (onUseKeyList != null
									&& onUseKeyList.getLength() > 0) {
								// нельзя одновременно UseKey и сертификат
								// пользователя
								throw new GeneralFailure(
										"This <UseKey> and users X509Certificate аre not compatible!!!");
							}

							String base64X509Certificate = usernameCertList
									.item(0).getTextContent();

							 
							byte[] byteX509Certificate = Base64
									.decode(base64X509Certificate);

							CertificateFactory cf = CertificateFactory
									.getInstance("X.509");
							ByteArrayInputStream bais = new ByteArrayInputStream(
									byteX509Certificate);

							while (bais.available() > 0) {
								userCertX = (X509Certificate) cf
										.generateCertificate(bais);
							}

							 

						}

					} else if (usernameTokenList.getLength() == 2) {
						// аутентификация пользователя по логин/пароль

						// ид системы должен быть обязательно
						Element el_1 = (Element) usernameTokenList.item(0);
						String el_id_1 = el_1.getAttribute("wsu:Id");

						LOGGER.debug("handleMessage:02_3_4:" + el_id_1);

						Element el_2 = (Element) usernameTokenList.item(1);
						String el_id_2 = el_2.getAttribute("wsu:Id");

						LOGGER.debug("handleMessage:02_3_5:" + el_id_2);

						if ("SystemToken_1".equals(el_id_1)
								&& "UsernameToken_1".equals(el_id_2)) {

							if (onUseKeyList != null
									&& onUseKeyList.getLength() > 0) {
								// нельзя одновременно UseKey и логин/пароль
								// пользователя
								throw new GeneralFailure(
										"This <UseKey> and UsernameToken_1 аre not compatible!!!");
							}

							// система
							NodeList usernameListUsrSys = ((Element) usernameTokenList
									.item(0)).getElementsByTagNameNS(WSSENS,
									"Username");

							if (usernameListUsrSys == null
									|| usernameListUsrSys.getLength() == 0) {
								LOGGER.debug("handleMessage:02_3_5+");
								throw new GeneralFailure(
										"This service requires <Username>, which is missing!!!");
							}

							system_principal = usernameListUsrSys.item(0)
									.getTextContent();

							// пользователь

							// 1)логин
							usernameListUsrSys = ((Element) usernameTokenList.item(1))
									.getElementsByTagNameNS(WSSENS, "Username");

							if (usernameListUsrSys == null
									|| usernameListUsrSys.getLength() == 0) {
								LOGGER.debug("handleMessage:02_3_5+");
								throw new GeneralFailure(
										"This service requires <Username>, which is missing!!!");
							}

							user_principal = usernameListUsrSys.item(0)
									.getTextContent();

							// 2)пароль
							NodeList passwordListUsr = ((Element) usernameTokenList
									.item(1)).getElementsByTagNameNS(WSSENS,
									"Password");

							if (passwordListUsr == null
									|| passwordListUsr.getLength() == 0) {
								LOGGER.debug("handleMessage:02_3_5+");
								throw new GeneralFailure(
										"This service requires <Password>, which is missing!!!");
							}

							user_password = passwordListUsr.item(0)
									.getTextContent();

						} else if ("SystemToken_1".equals(el_id_2)
								&& "UsernameToken_1".equals(el_id_1)) {

							if (onUseKeyList != null
									&& onUseKeyList.getLength() > 0) {
								// нельзя одновременно UseKey и логин/пароль
								// пользователя
								throw new GeneralFailure(
										"This <UseKey> and UsernameToken_1 аre not compatible!!!");
							}

							// система
							NodeList usernameList = ((Element) usernameTokenList
									.item(1)).getElementsByTagNameNS(WSSENS,
									"Username");

							if (usernameList == null
									|| usernameList.getLength() == 0) {
								LOGGER.debug("handleMessage:02_3_5+");
								throw new GeneralFailure(
										"This service requires <Username>, which is missing!!!");
							}

							system_principal = usernameList.item(0)
									.getTextContent();

							// пользователь

							// 1)логин
							usernameList = ((Element) usernameTokenList.item(0))
									.getElementsByTagNameNS(WSSENS, "Username");

							if (usernameList == null
									|| usernameList.getLength() == 0) {
								LOGGER.debug("handleMessage:02_3_5+");
								throw new GeneralFailure(
										"This service requires <Username>, which is missing!!!");
							}

							user_principal = usernameList.item(0)
									.getTextContent();

							// 2)пароль
							NodeList passwordList = ((Element) usernameTokenList
									.item(0)).getElementsByTagNameNS(WSSENS,
									"Password");

							if (passwordList == null
									|| passwordList.getLength() == 0) {
								LOGGER.debug("handleMessage:02_3_5+");
								throw new GeneralFailure(
										"This service requires <Password>, which is missing!!!");
							}

							user_password = passwordList.item(0)
									.getTextContent();

						} else {
							throw new Exception(
									"Невозможно определить SystemToken_1 и UsernameToken_1!");
						}
					}

				} else { // onBehalfOf

					LOGGER.debug("handleMessage:onBehalfOf:01");

					if (usernameTokenList.getLength() == 1) {
						// должна присутствовать - аутентификация системы
						// аутентификация пользователя по сертификату -
						// если и есть - игнорируем

						Element el = (Element) usernameTokenList.item(0);
						String el_id = el.getAttribute("wsu:Id");

						LOGGER.debug("handleMessage:onBehalfOf:02:" + el_id);

						if ("SystemToken_1".equals(el_id)) {
							// правильно - система должна присутствовать
							NodeList usernameList = ((Element) usernameTokenList
									.item(0)).getElementsByTagNameNS(WSSENS,
									"Username");

							if (usernameList == null
									|| usernameList.getLength() == 0) {
								LOGGER.debug("handleMessage:onBehalfOf:03");
								throw new GeneralFailure(
										"This service requires <Username>, which is missing!!!");
							}

							system_principal = usernameList.item(0)
									.getTextContent();

							LOGGER.debug("handleMessage:onBehalfOf:04:"
									+ system_principal);

						} else {
							throw new Exception(
									"This service requires SystemToken_1, which is missing!!!");
						}

					} else if (usernameTokenList.getLength() == 2) {
						// возможно присутствует аутентификация пользователя по
						// логин/пароль
						// плюём на неё, главное найти ид системы

						// ид системы должен быть обязательно
						Element el_1 = (Element) usernameTokenList.item(0);
						String el_id_1 = el_1.getAttribute("wsu:Id");

						LOGGER.debug("handleMessage:onBehalfOf:05:" + el_id_1);

						Element el_2 = (Element) usernameTokenList.item(1);
						String el_id_2 = el_2.getAttribute("wsu:Id");

						LOGGER.debug("handleMessage:02_3_5:" + el_id_2);

						if ("SystemToken_1".equals(el_id_1)) {

							// система
							NodeList usernameList = ((Element) usernameTokenList
									.item(0)).getElementsByTagNameNS(WSSENS,
									"Username");

							if (usernameList == null
									|| usernameList.getLength() == 0) {
								LOGGER.debug("handleMessage:02_3_5+");
								throw new GeneralFailure(
										"This service requires <Username>, which is missing!!!");
							}

							system_principal = usernameList.item(0)
									.getTextContent();

						} else if ("SystemToken_1".equals(el_id_2)) {

							// система
							NodeList usernameList = ((Element) usernameTokenList
									.item(1)).getElementsByTagNameNS(WSSENS,
									"Username");

							if (usernameList == null
									|| usernameList.getLength() == 0) {
								LOGGER.debug("handleMessage:02_3_5+");
								throw new GeneralFailure(
										"This service requires <Username>, which is missing!!!");
							}

							system_principal = usernameList.item(0)
									.getTextContent();

						} else {
							throw new Exception(
									"Невозможно определить SystemToken_1 !");
						}
					}

				}
				LOGGER.debug("handleMessage:02_5_1:" + system_principal);
				LOGGER.debug("handleMessage:02_5_2:" + user_principal);
				LOGGER.debug("handleMessage:02_5_3:" + user_password);
				
				LOGGER.debug("handleMessage:obo:01:" + user_obo_principal);

				if (user_obo_principal != null) {
					// аутентификация пользователя по onBehalfOf
					// idUser = (new
					// ContextAccessSTSManager()).authenticate_login_obo(user_obo_principal,
					// AuthMode.HTTP_REDIRECT, getIPAddress(req));

					// !!!
					// отказались от передачи токена целиком
					// передаём token_id
					// а в нём указан uid пользователя
					// поэтому у нас user_obo_principal - это id пользователя
					// и нужно вызывать authenticate_uid_obo
					// а когда передавали токен целиком, то был логин
					// пользователя
					// и вызывали authenticate_login_obo
					loginUser = (new ContextAccessSTSManager())
							.authenticate_uid_obo(user_obo_principal,
									AuthMode.WEB_SERVICES, getIPAddress(req),
									system_principal);

					LOGGER.debug("handleMessage:obo:02:" + loginUser);

					http_session.setAttribute("user_principal", loginUser);

					http_session.setAttribute("cud_auth_type", user_obo_auth_type);
					 
				} else if (user_principal != null) {
					// аутентификация пользователя по логин/паролю

					loginUser = (new ContextAccessSTSManager())
							.authenticate_login(user_principal, user_password,
									AuthMode.WEB_SERVICES, getIPAddress(req),
									system_principal);

					// user_principal и так логин
					http_session.setAttribute("user_principal", user_principal);

					http_session.setAttribute("cud_auth_type", authTypePassword);
					 
				} else if (userCertX != null) {
					// аутентификация пользователя по сертификату

					String certSN = decToHex(userCertX.getSerialNumber());

					LOGGER.debug("handleMessage:02_5_5:" + certSN);

					loginUser = (new ContextAccessSTSManager())
							.authenticate_cert_sn(certSN, getIPAddress(req),
									system_principal);

					LOGGER.debug("handleMessage:02_5_6:" + loginUser);

					user_principal = loginUser;

					http_session.setAttribute("user_principal", user_principal);

					http_session.setAttribute("cud_auth_type", authTypeX509);
					 
				} else {
					// просто аутентификация системы

					// делаем аудит
					(new ContextAccessSTSManager()).sys_audit_public(83L,
							"inp_param", "true", getIPAddress(req), null,
							system_principal);

				}

				LOGGER.debug("handleMessage:02_5_7");

				http_session.setAttribute("system_principal", system_principal);

			 if(Configuration.isSignRequired()){
				
				X509Certificate cert_user = (new ContextIDPUtilManager())
						.system_cert(system_principal);

				 

				
				if (cert_user != null) {

					publicKey2 = cert_user.getPublicKey();
				}

				 

				if (publicKey2 == null) {

					throw new GeneralFailure("Public key is null!!!");
				}

				Node securityToken2 = soapDoc.getDocumentElement()
						.getElementsByTagNameNS(WSSENS, "UsernameToken").item(0);

				LOGGER.debug("handleMessage:09:" + securityToken2.getNodeName());

				// боевой
				Document newDoc = DocumentUtil.createDocument();
				Node signingNode = newDoc.importNode(securityToken2, true);
				newDoc.appendChild(signingNode);

				 

				Node signatureNode1 = signatureList.item(0);

				LOGGER.debug("handleMessage:09_2:"
						+ signatureNode1.getNodeName());

				DOMValidateContext valContext1 = new DOMValidateContext(
						publicKey2, signatureNode1);

				valContext1.putNamespacePrefix(XMLSignature.XMLNS, "dsig");

				LOGGER.debug("handleMessage:09_3");

				valContext1
						.setIdAttributeNS(
								soapHeader,
								"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd",
								"Id");
				valContext1
						.setIdAttributeNS(
								soapBody,
								"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd",
								"Id");

				LOGGER.debug("handleMessage:09_4");

				javax.xml.crypto.dsig.XMLSignature signature1 = fac
						.unmarshalXMLSignature(valContext1);

				boolean result1 = signature1.validate(valContext1);

				LOGGER.debug("dispatch:011_5:" + result1);

				if (result1 == false) {
					throw new GeneralFailure("Signature is not valid!!!");
				}

				
			}
				
				// ответ
			} else {

			

			
				soapPart.getEnvelope()
						.addNamespaceDeclaration(
								"wsse",
								"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
				soapPart.getEnvelope().addNamespaceDeclaration("ds",
						"http://www.w3.org/2000/09/xmldsig#");

				soapPart.getEnvelope()
						.addNamespaceDeclaration(
								"wsu",
								"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");

				soapBody.addNamespaceDeclaration(
						"wsu",
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
				soapBody.setAttributeNS(
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd",
						"wsu:Id", "Body");

				soapHeader
						.addNamespaceDeclaration(
								"wsu",
								"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
				soapHeader
						.setAttributeNS(
								"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd",
								"wsu:Id", "Header");

				LOGGER.debug("dispatch:03");

				// при подписывании Pre-digested input показывает просто
				// <wsse:Username/>,
				// то есть подписывается именно <wsse:Username/>
				// а когда мы перед отправкой вызываем parentNode.replaceChild,
				// то элемент становится
				// <wsse:Username
				// xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"/><wsse:Password
				// xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"/>
				// таким он и отправляется.
				// и на приёмной стороне при проверке подписи Pre-digested input
				// честно показывает
				// <wsse:Username xmlns:wsse="http://..."/>, то есть проверяется
				// именно <wsse:Username xmlns:wsse="http://..."/>
				// и чтобы этого избежать нужно на SOAPElement использовать
				// Usern/ame/SOAP.addNamespa/ceDeclaration.

				QName securityQN = new QName(
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
						"Security", "wsse");
				QName timestampQN = new QName(
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd",
						"Timestamp", "wsu");
				QName createdQN = new QName(
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd",
						"Created", "wsu");

				SOAPElement securitySOAP = soapHeader
						.addChildElement(securityQN);

				securitySOAP
						.addNamespaceDeclaration(
								"wsu",
								"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
				securitySOAP
						.setAttributeNS(
								"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd",
								"wsu:Id", "_id_sec");

				SOAPElement timestampSOAP = securitySOAP
						.addChildElement(timestampQN);

				SOAPElement createdSOAP = timestampSOAP
						.addChildElement(createdQN);

				createdSOAP.addTextNode(SimpleTypeBindings
						.marshalDateTime(new GregorianCalendar()));

				 

				
				if(Configuration.isSignRequired()){
					
					
					KeyStoreKeyManager kskm = new KeyStoreKeyManager();
				
					KeyPair keyPair = kskm.getSigningKeyPair();

					PublicKey publicKey = keyPair.getPublic();

					PrivateKey privateKey = keyPair.getPrivate();	
					
				
			if(!org.apache.xml.security.Init.isInitialized()) {		
				org.apache.xml.security.Init.init();
			}
				
			 //здесь было создание провайдера и фабрики

				// Преобразования над блоком SignedInfo
				List<Transform> transformList = new ArrayList<Transform>();
				Transform transform = fac.newTransform(Transform.ENVELOPED,
						(XMLStructure) null);
				Transform transformC14N = fac.newTransform(
						Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS,
						(XMLStructure) null);
				transformList.add(transform);
				transformList.add(transformC14N);

				LOGGER.debug("dispatch:07");

				// что подписывать

				Reference ref1 = fac
						.newReference(
								"#Header",
								fac.newDigestMethod(
										"http://www.w3.org/2001/04/xmldsig-more#gostr3411",
										null), transformList, null, null);
				Reference ref2 = fac.newReference("#Body", fac.newDigestMethod(
						"http://www.w3.org/2001/04/xmldsig-more#gostr3411",
						null), transformList, null, null);
				List<Reference> referenceList = new ArrayList<Reference>();

				referenceList.add(ref1);
				referenceList.add(ref2);

				LOGGER.debug("dispatch:06");

				SignedInfo si = fac
						.newSignedInfo(
								fac.newCanonicalizationMethod(
										CanonicalizationMethod.EXCLUSIVE,
										(C14NMethodParameterSpec) null),
								fac.newSignatureMethod(
										"http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411",
										null), referenceList);

				KeyInfoFactory kif = fac.getKeyInfoFactory();

				KeyValue kv = kif.newKeyValue(publicKey);
				KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));

				javax.xml.crypto.dsig.XMLSignature sig = fac.newXMLSignature(
						si, ki);

				// куда вставлять подпись
				DOMSignContext signContext = new DOMSignContext(privateKey,
						securitySOAP);

				signContext.putNamespacePrefix(XMLSignature.XMLNS, "dsig");

				// фиксация аттрибута id в подписываемом элементе
				// место ответственное за факт появления Pre-digested input в
				// логе

				signContext
						.setIdAttributeNS(
								soapBody,
								"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd",
								"Id");
				signContext
						.setIdAttributeNS(
								soapHeader,
								"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd",
								"Id");

				LOGGER.debug("dispatch:011");

				sig.sign(signContext);

				}
			}
			LOGGER.debug("handleMessage:0100");

		} catch (Exception e) {
			LOGGER.error("handleMessage:error:", e);
			throw new ProtocolException(e);

		}
		return true;
	}

	private String getIPAddress(HttpServletRequest request) {

		return request.getRemoteAddr();
	}

	private static String decToHex(BigInteger bi) {

		String result = null;

		try {
			result = bi.toString(16);
		} catch (NumberFormatException e) {
			LOGGER.error("Error! tried to parse an invalid number format");
		}
		return result;
	}

	

	

}
