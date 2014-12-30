package ru.spb.iac.cud.services.handlers;

import java.security.KeyStore;
import java.security.PublicKey;
import java.util.Date;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.picketlink.common.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.exceptions.TokenExpired;
import ru.spb.iac.cud.sign.GOSTSignatureUtil;

 public class AnonymSOAPHandler implements SOAPHandler<SOAPMessageContext> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnonymSOAPHandler.class);

    private static PublicKey publicKey = null;
	
    public void close(MessageContext mc) {
    	LOGGER.debug("close");
	}

	
	public Set<QName> getHeaders() {
		return null;
	}

	
	public boolean handleFault(SOAPMessageContext mc) {
		return true;
	}

	public boolean handleMessage(SOAPMessageContext mc) {

		LOGGER.debug("handleMessage:01:"
				+ mc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY));

		String userId = null;
		String base64TokenId;

		try {

			HttpServletRequest req = (HttpServletRequest) mc
					.get(MessageContext.SERVLET_REQUEST);
			HttpSession http_session = req.getSession();

			SOAPMessage smAnonym = mc.getMessage();
			

			SOAPHeader soapHeader = smAnonym.getSOAPHeader();
			
			// запрос
			if (Boolean.FALSE.equals(mc
					.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY))) {

				 

				NodeList usernameTokenListAnonym = soapHeader.getElementsByTagNameNS(
						"*", "UsernameToken");

				if (usernameTokenListAnonym == null
						|| usernameTokenListAnonym.getLength() == 0) {

					// !!!ещё подумать -
					// некототрые операции могут вызываться анонимно
					// этот обработчик используется для аудита и завки напрямую
					// от пользователя
					// thr/ow new
					// Gen/eralFai/lure("This service requires UsernameToken with Id ApplicantToken_1, which is missing!!!");

				} else {

					if (usernameTokenListAnonym.getLength() > 0/* ==1 */) {
						// логин заявителя
						// берём первый <UsernameToken>
						// ид ApplicantToken_1 должен быть обязательно
						Element el = (Element) usernameTokenListAnonym.item(0);
						String el_id = el.getAttribute("wsu:Id");

						 

						if ("UserAuthTokenId".equals(el_id)) {
							// правильно -
							NodeList usernameList = ((Element) usernameTokenListAnonym
									.item(0)).getElementsByTagNameNS("*",
									"Username");

							if (usernameList == null
									|| usernameList.getLength() == 0) {
								throw new GeneralFailure(
										"This service requires <Username>, which is missing!!!");
							}

							base64TokenId = usernameList.item(0)
									.getTextContent();

							if (base64TokenId != null) {

								LOGGER.debug("AppSOAPHandler:handleMessage:02:"
										+ base64TokenId);

								byte[] byteTokenIDAnonym = Base64
										.decode(base64TokenId);

								String tokenID = new String(byteTokenIDAnonym,
										"UTF-8");

								LOGGER.debug("AppSOAPHandler:handleMessage:03:"
										+ tokenID);

								String[] arrTokenID = tokenID.split("_");

								if (arrTokenID == null
										|| arrTokenID.length != 4) {
									throw new Exception(
											"UserAuthToken is not valid!!!");
								}

								StringBuilder sb = new StringBuilder();

								sb.append(arrTokenID[0] + "_" + arrTokenID[1] + "_" + arrTokenID[2]);

								byte[] sigValueAnonym = Base64.decode(arrTokenID[3]);

							if(publicKey==null){	
								
								KeyStore ks = KeyStore.getInstance(
										"HDImageStore", "JCP");
								ks.load(null, null);

								publicKey = ks.getCertificate(
										"cudvm_export").getPublicKey();
							}
								boolean tokenIDValidateResult = GOSTSignatureUtil
										.validate(
												sb.toString().getBytes("UTF-8"),
												sigValueAnonym, publicKey);

								LOGGER.debug("handleMessage:04:"
										+ tokenIDValidateResult);

								userId = arrTokenID[0].toString();
								Date expired = new Date(
										Long.parseLong(arrTokenID[1]));

								LOGGER.debug("handleMessage:05:" + userId);

								LOGGER.debug("handleMessage:06:" + expired);

								if (!tokenIDValidateResult) {
									throw new Exception(
											"UserAuthToken is not valid!!!");
								}

								if (new Date(System.currentTimeMillis())
										.after(expired)) {
									throw new TokenExpired(
											"UserAuthToken is expired!!!");
									//throw new Exception(
									//		"UserAuthToken is expired!!!");
								}

							} else {
								throw new Exception("UserAuthToken is empty!!!");
							}

							 

							// решили определять пользователей извне ЦУД по их
							// ИД, а не логинам
							// поэтому в usernameList.item(0).getTextContent()
							// передаётся ИД пользователя
							// то есть user_login - это сейчас ИД пользователя
							// и поэтому вызов authenticate_login_obo уже не
							// нужен
							// idUser = (new
							// ContextAc/cessSTSManager()).auth/enticate_logi/n_obo(user_login,
							// AuthMode.HTTP_REDIRECT, getIPAddress(req));

							// это заявитель
							http_session.setAttribute("user_id_principal",
									userId);

						}
						// !!!ещё подумать -
						// некототрые операции могут вызываться анонимно
					}
				}
			}

		} catch (Exception eAnonym) {
			 
			throw new ProtocolException(eAnonym);
		}
		return true;
	}

	

}
