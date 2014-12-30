package ru.spb.iac.cud.services.handlers;

import org.picketlink.trust.jbossws.handler.AbstractSAML2Handler;

import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.jboss.security.SecurityContext;
import org.picketlink.common.constants.JBossSAMLURIConstants;
import org.picketlink.common.util.StringUtil;
import org.picketlink.identity.federation.bindings.jboss.subject.PicketLinkPrincipal;
import org.picketlink.identity.federation.core.saml.v2.util.AssertionUtil;
import org.picketlink.identity.federation.core.wstrust.SamlCredential;
import org.picketlink.identity.federation.core.wstrust.plugins.saml.SAMLUtil;
import org.picketlink.identity.federation.saml.v2.assertion.AssertionType;
import org.picketlink.trust.jbossws.SAML2Constants;
import org.picketlink.trust.jbossws.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

 public class CUDSAML2Handler extends AbstractSAML2Handler {

	public static final String ROLE_KEY_SYS_PROP = "picketlink.rolekey";

	protected boolean handleInbound(MessageContext msgContext) {
		logger.trace("Handling Inbound Message");

		String assertionNS = JBossSAMLURIConstants.ASSERTION_NSURI.get();
		SOAPMessageContext ctx = (SOAPMessageContext) msgContext;
		SOAPMessage soapMessage = ctx.getMessage();

		if (soapMessage == null) {
			throw logger.nullValueError("SOAP Message");
		}

		// retrieve the assertion
		Document document = soapMessage.getSOAPPart();
		Element soapHeader = Util.findOrCreateSoapHeader(document
				.getDocumentElement());
		Element assertion = Util.findElement(soapHeader, new QName(assertionNS,
				"Assertion"));
		if (assertion != null) {
			AssertionType assertionType = null;
			try {
				assertionType = SAMLUtil.fromElement(assertion);
				if (AssertionUtil.hasExpired(assertionType)) {
					throw new RuntimeException(
							logger.samlAssertionExpiredError());
				}
			} catch (Exception e) {
				logger.samlAssertionPasingFailed(e);
			}
			SamlCredential credential = new SamlCredential(assertion);
			if (logger.isTraceEnabled()) {
				logger.trace("Assertion included in SOAP payload: "
						+ credential.getAssertionAsString());
			}
			Element subject = Util.findElement(assertion, new QName(
					assertionNS, "Subject"));
			Element nameID = Util.findElement(subject, new QName(assertionNS,
					"NameID"));
			String username = getUsername(nameID);

			// set SecurityContext
			Subject theSubject = new Subject();
			PicketLinkPrincipal principal = new PicketLinkPrincipal(username);

			createSecurityContext(credential, theSubject, principal);

			if (assertionType != null) {
				List<String> roleKeys = new ArrayList<String>();
				String roleKey = SecurityActions.getSystemProperty(
						ROLE_KEY_SYS_PROP, "Role");
				if (StringUtil.isNotNull(roleKey)) {
					roleKeys.addAll(StringUtil.tokenize(roleKey));
				}

				logger.trace("Rolekeys to extract roles from the assertion: "
						+ roleKeys);

				List<String> roles = AssertionUtil.getRoles(assertionType,
						roleKeys);
				if (roles.size() > 0) {
					logger.trace("Roles in the assertion: " + roles);
					Group roleGroup = SecurityActions.group(roles);
					theSubject.getPrincipals().add(roleGroup);
				} else {
					logger.trace("Did not find roles in the assertion");
				}
			}
		} else {
			logger.trace("We did not find any assertion");
		}
		return true;
	}

	protected void createSecurityContext(SamlCredential credential,
			Subject theSubject, Principal principal) {
		SecurityContext sc = SecurityActions.createSecurityContext(principal,
				credential, theSubject);
		SecurityActions.setSecurityContext(sc);
	}

	protected boolean handleOutbound(MessageContext msgContext) {
		logger.trace("Handling Outbound Message");

		SOAPMessageContext ctx = (SOAPMessageContext) msgContext;
		SOAPMessage soapMessage = ctx.getMessage();

		// retrieve assertion first from the message context
		Element assertion = (Element) ctx
				.get(SAML2Constants.SAML2_ASSERTION_PROPERTY);

		// Assertion can also be obtained from the JAAS subject
		if (assertion == null) {
			assertion = getAssertionFromSubject();
		}

		if (assertion == null) {
			logger.trace("We did not find any assertion");
			return true;
		}

		// add wsse header
		Document document = soapMessage.getSOAPPart();
		Element soapHeader = Util.findOrCreateSoapHeader(document
				.getDocumentElement());
		try {
			Element wsse = getSecurityHeaderElement(document);
			// !!!
			// ws/se.setAt/tributeNS(soa/pHeader.ge/tNamespaceURI(),
			// soap/Header.getP/refix() + ":mustU/nderstand", "1");

			wsse.setAttributeNS(
					"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd",
					"wsu:Id", "_id_sec");

			// закомментировать
			/*
			 *if/ (assertion != null) { // add the assertion as a child of the
			 * wsse header // check if the assertion element comes from the same
			 * docu/ment, otherwise import the node if (document !=
			 * asse/rtion.getOwnerDocument()) {
			 * wsse.app/endChild(document.importNode(assertion, true)); } else {
			 * wsse.a/ppendChild(assertion); } }
			 */
			soapHeader.insertBefore(wsse, soapHeader.getFirstChild());
		} catch (Exception e) {
			logger.error(e);
			return false;
		}

		return true;
	}

}
