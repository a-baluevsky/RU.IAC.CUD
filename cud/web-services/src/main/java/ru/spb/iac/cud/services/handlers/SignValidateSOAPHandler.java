package ru.spb.iac.cud.services.handlers;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
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
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.xml.security.transforms.Transforms;
import org.picketlink.common.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.services.web.init.Configuration;

 public class SignValidateSOAPHandler implements SOAPHandler<SOAPMessageContext> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SignValidateSOAPHandler.class);

	private static PublicKey publicKey = null;
	
	private static PrivateKey privateKey = null;
	
	public Set<QName> getHeaders() {
		return null;
	}

	public void close(MessageContext mc) {
		LOGGER.debug("close");
	}

	public boolean handleFault(SOAPMessageContext mc) {
		return true;
	}

	public boolean handleMessage(SOAPMessageContext mc) {

		LOGGER.debug("handleMessage:01+:"
				+ mc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY));

		try {

			HttpServletRequest req = (HttpServletRequest) mc
					.get(MessageContext.SERVLET_REQUEST);
			HttpSession http_session = req.getSession();

			String cud_sts_principal = null;

			PublicKey publicKey2 = null;

			X509Certificate cert_user = null;

			SOAPMessage sm = mc.getMessage();
			

			SOAPHeader soapHeader = sm.getSOAPHeader();
			SOAPBody soapBody = sm.getSOAPBody();

			// запрос
			if (Boolean.FALSE.equals(mc
					.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY))) {

				 

				// взятие ключа из хранилища доверенных

				NodeList assertionList = soapHeader.getElementsByTagNameNS("*",
						"Assertion");

				if (assertionList == null || assertionList.getLength() == 0) {
					 
					throw new GeneralFailure(
							"This service requires <saml:Assertion>, which is missing!!!");
				}

				NodeList subjectList = ((Element) assertionList.item(0))
						.getElementsByTagNameNS("*", "Subject");

				if (subjectList == null || subjectList.getLength() == 0) {
					 
					throw new GeneralFailure(
							"This service requires <Subject>, which is missing!!!");
				}

				NodeList nameIDList = ((Element) subjectList.item(0))
						.getElementsByTagNameNS("*", "NameID");

				if (nameIDList == null || nameIDList.getLength() == 0) {
					 
					throw new GeneralFailure(
							"This service requires <NameID>, which is missing!!!");
				}

			
				String codeSystem = nameIDList.item(0).getTextContent();

				 

				cud_sts_principal = codeSystem;
				http_session.setAttribute("cud_sts_principal",
						cud_sts_principal);

			 if(Configuration.isSignRequired()){
				/*
				 * //1-й вариант: взять код системы из Assertion/subject/NameID
				 * //по этому коду найти в базе сертификат
				 * 
				 * cert_/user = (n/ew
				 * Contex/tIDPUtilManager()).syst/em_cert(codeSystem);
				 */

				// 2-й вариант: взять прямо сертификат из
				// Assertion/subject/SubjectConfirmationData/X509Certificate

				NodeList x509CertificateList = ((Element) subjectList.item(0))
						.getElementsByTagNameNS("*", "X509Certificate");

				if (x509CertificateList == null
						|| x509CertificateList.getLength() == 0) {
					 
					throw new GeneralFailure(
							"This service requires SubjectConfirmationData/X509Certificate, which is missing!!!");
				}

				String x509CertSystem = x509CertificateList.item(0)
						.getTextContent();

				 

				byte[] byteX509Certificate = Base64.decode(x509CertSystem);

				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				ByteArrayInputStream bais = new ByteArrayInputStream(
						byteX509Certificate);

				while (bais.available() > 0) {
					cert_user = (X509Certificate) cf.generateCertificate(bais);
				}

				if (cert_user != null) {

					publicKey2 = cert_user.getPublicKey();

					/*
					 * cert_verify_sign =
					 * Han/dlerUtils.getStore().getCertificate(domainAlias);
					 * 
					 * 
					 * if(cer/t_verify_sign!=null){
					 * public/Key2=cert_veri/fy_sign.getPublicKey(); }
					 */
				} else {
					throw new GeneralFailure(
							"Cannot to define a client public key!!!");
				}

				 

				Provider xmlDSigProvider = new ru.CryptoPro.JCPxml.dsig.internal.dom.XMLDSigRI();

				XMLSignatureFactory fac = XMLSignatureFactory.getInstance(
						"DOM", xmlDSigProvider);

				Node securityNode = soapHeader.getFirstChild();

				if (securityNode == null) {
					 
					throw new GeneralFailure(
							"This service requires <wsse:Security>, which is missing!!!");
				}

				NodeList securityNodeChilds = securityNode.getChildNodes();

				Node signatureNode1 = null;

				for (int i = 0; i < securityNodeChilds.getLength(); i++) {

					 

					if (securityNodeChilds.item(i).getLocalName() != null
							&& securityNodeChilds.item(i).getLocalName()
									.equals("Signature")) {
						signatureNode1 = securityNodeChilds.item(i);
					}
				}

				 

				// signature

				if (signatureNode1 == null) {
					 
					throw new GeneralFailure(
							"This service requires <dsig:Signature>, which is missing!!!");
				}

				 

				DOMValidateContext valContext1 = new DOMValidateContext(
						publicKey2, signatureNode1);

				valContext1.putNamespacePrefix(XMLSignature.XMLNS, "dsig");

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

				javax.xml.crypto.dsig.XMLSignature signature1 = fac
						.unmarshalXMLSignature(valContext1);

				boolean result1 = signature1.validate(valContext1);

				LOGGER.debug("010:" + result1);

				if (result1 == false) {
					throw new GeneralFailure("Signature is not valid!!!");
				}

			  }
			} else {
				// ответ

				if(Configuration.isSignRequired()){
					
				
				char[] signingKeyPass = "Access_Control".toCharArray();
				String signingAlias = "cudvm_export";

				
				if(publicKey==null){
				
				KeyStore ks = KeyStore.getInstance("HDImageStore", "JCP");
				ks.load(null, null);

				privateKey = (PrivateKey) ks.getKey(signingAlias,
						signingKeyPass);

				Certificate cert = ks.getCertificate(signingAlias);
				publicKey = cert.getPublicKey();

				}
				if(!org.apache.xml.security.Init.isInitialized()){
				  org.apache.xml.security.Init.init();
				}
				Provider xmlDSigProvider = new ru.CryptoPro.JCPxml.dsig.internal.dom.XMLDSigRI();

				XMLSignatureFactory fac = XMLSignatureFactory.getInstance(
						"DOM", xmlDSigProvider);

				Node SecuritySOAP = soapHeader.getFirstChild();

			
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

				List<Transform> transformList = new ArrayList<Transform>();
				Transform transform = fac.newTransform(Transform.ENVELOPED,
						(XMLStructure) null);
				Transform transformC14N = fac.newTransform(
						Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS,
						(XMLStructure) null);
				transformList.add(transform);
				transformList.add(transformC14N);

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
						SecuritySOAP);

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

			
				sig.sign(signContext);

				// !!!
				// проверка
				// включать для проверки

				
				}
			}

		} catch (Exception e) {
			LOGGER.error("handleMessage:error:", e);
			throw new ProtocolException(e);
		}
		return true;
	}

}
