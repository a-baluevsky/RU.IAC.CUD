package ru.spb.iac.cud.idp.web.util;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.picketlink.common.PicketLinkLogger;
import org.picketlink.common.PicketLinkLoggerFactory;
import org.picketlink.common.constants.JBossSAMLURIConstants;
import org.picketlink.common.constants.WSTrustConstants;
import org.picketlink.common.exceptions.ProcessingException;
import org.picketlink.common.util.DocumentUtil;
import org.picketlink.common.util.StringUtil;
import org.picketlink.common.util.SystemPropertiesUtil;
import org.picketlink.common.util.TransformerUtil;
import org.picketlink.identity.federation.core.util.ProvidersUtil;
import org.picketlink.identity.federation.core.util.SignatureUtilTransferObject;
import org.picketlink.identity.xmlsec.w3.xmldsig.SignatureType;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utility for XML Signature <b>Note:</b> You can change the canonicalization
 * method type by using the system property "picketlink.xmlsig.canonicalization"
 * 
 * @author
 * @author
 * @since Dec 15, 2008
 */

 public class GOSTXMLSignatureUtil {
	private static final PicketLinkLogger LOGGER = PicketLinkLoggerFactory
			.getLogger();

	// Set some system properties and Santuario providers. Run this block before
	// any other class initialization.
	static {
		ProvidersUtil.ensure();
		SystemPropertiesUtil.ensure();
		String keyInfoProp = SecurityActions.getSystemProperty(
				"picketlink.xmlsig.includeKeyInfo", null);
		if (StringUtil.isNotNull(keyInfoProp)) {
			includeKeyInfoInSignature = Boolean.parseBoolean(keyInfoProp);
		}
	}

	private static String canonicalizationMethodType = CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS;

	private static XMLSignatureFactory fac = getXMLSignatureFactory();

	/**
	 * By default, we include the keyinfo in the signature
	 */
	private static boolean includeKeyInfoInSignature = true;

	private static XMLSignatureFactory getXMLSignatureFactory() {
		XMLSignatureFactory xsf = null;

		try {
		
			Provider xmlDSigProvider = new ru.CryptoPro.JCPxml.dsig.internal.dom.XMLDSigRI();
			xsf = XMLSignatureFactory.getInstance("DOM", xmlDSigProvider);

		} catch (Exception ex) {
			try {
				xsf = XMLSignatureFactory.getInstance("DOM");
			} catch (Exception err) {
				throw new RuntimeException(LOGGER.couldNotCreateInstance("DOM",
						err));
			}
		}

		return xsf;
	}

	/**
	 * Set the canonicalization method type
	 * 
	 * @param canonical
	 */
	public static void setCanonicalizationMethodType(String canonical) {
		if (canonical != null) {
			canonicalizationMethodType = canonical;
		}
	}

	/**
	 * Use this method to not include the KeyInfo in the signature
	 * 
	 * @param includeKeyInfoInSignature
	 * @since v2.0.1
	 */
	public static void setIncludeKeyInfoInSignature(
			boolean includeKeyInfoInSignature) {
		// XMLSignatureUtil.includeKeyInfoInSignature =
		// includeKeyInfoInSignature;
		GOSTXMLSignatureUtil.includeKeyInfoInSignature = includeKeyInfoInSignature;
	}

	/**
	 * Precheck whether the document that will be validated has the right
	 * signedinfo
	 * 
	 * @param doc
	 * @return
	 */
	public static boolean preCheckSignedInfo(Document doc) {
		NodeList nl = doc.getElementsByTagNameNS(
				JBossSAMLURIConstants.XMLDSIG_NSURI.get(), "SignedInfo");
		return nl != null ? nl.getLength() > 0 : false;
	}

	/**
	 * Sign a node in a document
	 * 
	 * @param doc
	 *            Document
	 * @param parentOfNodeToBeSigned
	 *            Parent Node of the node to be signed
	 * @param signingKey
	 *            Private Key
	 * @param certificate
	 *            X509 Certificate holding the public key
	 * @param digestMethod
	 *            (Example: DigestMethod.SHA1)
	 * @param signatureMethod
	 *            (Example: SignatureMethod.DSA_SHA1)
	 * @param referenceURI
	 * @return Document that contains the signed node
	 * @throws XMLSignatureException
	 * @throws MarshalException
	 * @throws GeneralSecurityException
	 * @throws ParserConfigurationException
	 */
	public static Document sign(Document doc, Node parentOfNodeToBeSigned,
			PrivateKey signingKey, X509Certificate certificate,
			String digestMethod, String signatureMethod, String referenceURI)
			throws ParserConfigurationException, GeneralSecurityException,
			MarshalException, XMLSignatureException {
		KeyPair keyPair = new KeyPair(certificate.getPublicKey(), signingKey);
		return sign(doc, parentOfNodeToBeSigned, keyPair, digestMethod,
				signatureMethod, referenceURI);
	}

	/**
	 * Sign a node in a document
	 * 
	 * @param doc
	 * @param nodeToBeSigned
	 * @param keyPair
	 * @param publicKey
	 * @param digestMethod
	 * @param signatureMethod
	 * @param referenceURI
	 * @return
	 * @throws ParserConfigurationException
	 * @throws XMLSignatureException
	 * @throws MarshalException
	 * @throws GeneralSecurityException
	 */
	public static Document sign(Document doc, Node nodeToBeSigned,
			KeyPair keyPair, String digestMethod, String signatureMethod,
			String referenceURI) throws ParserConfigurationException,
			GeneralSecurityException, MarshalException, XMLSignatureException {
		if (nodeToBeSigned == null) {
			throw LOGGER.nullArgumentError("Node to be signed");
		}

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Document to be signed=" + DocumentUtil.asString(doc));
		}

		Node parentNodeSign = nodeToBeSigned.getParentNode();

		// Let us create a new Document
		Document newDoc = DocumentUtil.createDocument();
		// Import the node
		Node signingNodeSign = newDoc.importNode(nodeToBeSigned, true);
		newDoc.appendChild(signingNodeSign);

		if (!referenceURI.isEmpty()) {
			propagateIDAttributeSetup(nodeToBeSigned,
					newDoc.getDocumentElement());
		}
		newDoc = sign(newDoc, keyPair, digestMethod, signatureMethod,
				referenceURI);

		// if the signed element is a SAMLv2.0 assertion we need to move the
		// signature element to the position
		// specified in the schema (before the assertion subject element).
		if ("Assertion".equals(nodeToBeSigned.getLocalName())
				&& WSTrustConstants.SAML2_ASSERTION_NS.equals(nodeToBeSigned
						.getNamespaceURI())) {
			Node signatureNode = DocumentUtil.getElement(newDoc, new QName(
					WSTrustConstants.DSIG_NS, "Signature"));
			Node subjectNode = DocumentUtil.getElement(newDoc, new QName(
					WSTrustConstants.SAML2_ASSERTION_NS, "Subject"));
			if (signatureNode != null && subjectNode != null) {
				newDoc.getDocumentElement().removeChild(signatureNode);
				newDoc.getDocumentElement().insertBefore(signatureNode,
						subjectNode);
			}
		}

		// Now let us import this signed doc into the original document we got
		// in the method call
		Node signedNode = doc.importNode(newDoc.getFirstChild(), true);

		if (!referenceURI.isEmpty()) {
			propagateIDAttributeSetup(newDoc.getDocumentElement(),
					(Element) signedNode);
		}

		parentNodeSign.replaceChild(signedNode, nodeToBeSigned);
		

		return doc;
	}

	/**
	 * +++ Sign a node in a document
	 * 
	 * @param doc
	 * @param nodeToBeSigned
	 * @param keyPair
	 * @param publicKey
	 * @param digestMethod
	 * @param signatureMethod
	 * @param referenceURI
	 * @return
	 * @throws ParserConfigurationException
	 * @throws XMLSignatureException
	 * @throws MarshalException
	 * @throws GeneralSecurityException
	 */
	public static Document sign(Document doc, Node nodeToBeSigned,
			KeyPair keyPair, String digestMethod, String signatureMethod,
			String referenceURI, X509Certificate x509Certificate)
			throws ParserConfigurationException, GeneralSecurityException,
			MarshalException, XMLSignatureException {
		if (nodeToBeSigned == null) {
			throw LOGGER.nullArgumentError("Node to be signed");
		}

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Document to be signed=" + DocumentUtil.asString(doc));
		}

		Node parentNode = nodeToBeSigned.getParentNode();

		// Let us create a new Document
		Document newDoc = DocumentUtil.createDocument();
		// Import the node
		Node signingNode = newDoc.importNode(nodeToBeSigned, true);
		newDoc.appendChild(signingNode);

		if (!referenceURI.isEmpty()) {
			propagateIDAttributeSetup(nodeToBeSigned,
					newDoc.getDocumentElement());
		}
		newDoc = sign(newDoc, keyPair, digestMethod, signatureMethod,
				referenceURI, x509Certificate);

		// if the signed element is a SAMLv2.0 assertion we need to move the
		// signature element to the position
		// specified in the schema (before the assertion subject element).
		if ("Assertion".equals(nodeToBeSigned.getLocalName())
				&& WSTrustConstants.SAML2_ASSERTION_NS.equals(nodeToBeSigned
						.getNamespaceURI())) {
			Node signatureNode = DocumentUtil.getElement(newDoc, new QName(
					WSTrustConstants.DSIG_NS, "Signature"));
			Node subjectNode = DocumentUtil.getElement(newDoc, new QName(
					WSTrustConstants.SAML2_ASSERTION_NS, "Subject"));
			if (signatureNode != null && subjectNode != null) {
				newDoc.getDocumentElement().removeChild(signatureNode);
				newDoc.getDocumentElement().insertBefore(signatureNode,
						subjectNode);
			}
		}

		// Now let us import this signed doc into the original document we got
		// in the method call
		Node signedNode = doc.importNode(newDoc.getFirstChild(), true);

		if (!referenceURI.isEmpty()) {
			propagateIDAttributeSetup(newDoc.getDocumentElement(),
					(Element) signedNode);
		}

		parentNode.replaceChild(signedNode, nodeToBeSigned);
		

		return doc;
	}

	/**
	 * Sign only specified element (assumption is that it already has ID
	 * attribute set)
	 * 
	 * @param elementToSign
	 *            element to sign with set ID
	 * @param nextSibling
	 *            child of elementToSign, which will be used as next sibling of
	 *            created signature
	 * @param keyPair
	 * @param digestMethod
	 * @param signatureMethod
	 * @param referenceURI
	 * @throws GeneralSecurityException
	 * @throws MarshalException
	 * @throws XMLSignatureException
	 */
	public static void sign(Element elementToSign, Node nextSibling,
			KeyPair keyPair, String digestMethod, String signatureMethod,
			String referenceURI) throws GeneralSecurityException,
			MarshalException, XMLSignatureException {
		sign(elementToSign, nextSibling, keyPair, digestMethod,
				signatureMethod, referenceURI, null);
	}

	/**
	 * Sign only specified element (assumption is that it already has ID
	 * attribute set)
	 * 
	 * @param elementToSign
	 *            element to sign with set ID
	 * @param nextSibling
	 *            child of elementToSign, which will be used as next sibling of
	 *            created signature
	 * @param keyPair
	 * @param digestMethod
	 * @param signatureMethod
	 * @param referenceURI
	 * @param x509Certificate
	 *            {@link X509Certificate} to be placed in SignedInfo
	 * @throws GeneralSecurityException
	 * @throws MarshalException
	 * @throws XMLSignatureException
	 * @since 2.5.0
	 */
	public static void sign(Element elementToSign, Node nextSibling,
			KeyPair keyPair, String digestMethod, String signatureMethod,
			String referenceURI, X509Certificate x509Certificate)
			throws GeneralSecurityException, MarshalException,
			XMLSignatureException {
		PrivateKey signingKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();

		DOMSignContext dsc = new DOMSignContext(signingKey, elementToSign,
				nextSibling);

		signImpl(dsc, digestMethod, signatureMethod, referenceURI, publicKey,
				x509Certificate);
	}

	/**
	 * Setup the ID attribute into <code>destElement</code> depending on the
	 * <code>isId</code> flag of an attribute of <code>sourceNode</code>.
	 * 
	 * @param sourceNode
	 * @param destDocElement
	 */
	public static void propagateIDAttributeSetup(Node sourceNode,
			Element destElement) {
		NamedNodeMap nnm = sourceNode.getAttributes();
		for (int i = 0; i < nnm.getLength(); i++) {
			Attr attr = (Attr) nnm.item(i);
			if (attr.isId()) {
				destElement.setIdAttribute(attr.getName(), true);
				break;
			}
		}
	}

	/**
	 * Sign the root element
	 * 
	 * @param doc
	 * @param signingKey
	 * @param publicKey
	 * @param digestMethod
	 * @param signatureMethod
	 * @param referenceURI
	 * @return
	 * @throws GeneralSecurityException
	 * @throws XMLSignatureException
	 * @throws MarshalException
	 */
	public static Document sign(Document doc, KeyPair keyPair,
			String digestMethod, String signatureMethod, String referenceURI)
			throws GeneralSecurityException, MarshalException,
			XMLSignatureException {
		return sign(doc, keyPair, digestMethod, signatureMethod, referenceURI,
				null);
	}

	/**
	 * Sign the root element
	 * 
	 * @param doc
	 * @param signingKey
	 * @param publicKey
	 * @param digestMethod
	 * @param signatureMethod
	 * @param referenceURI
	 * @return
	 * @throws GeneralSecurityException
	 * @throws XMLSignatureException
	 * @throws MarshalException
	 * @since 2.5.0
	 */
	public static Document sign(Document doc, KeyPair keyPair,
			String digestMethod, String signatureMethod, String referenceURI,
			X509Certificate x509Certificate) throws GeneralSecurityException,
			MarshalException, XMLSignatureException {
		LOGGER.trace("Document to be signed=" + DocumentUtil.asString(doc));
		PrivateKey signingKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();

		DOMSignContext dsc = new DOMSignContext(signingKey,
				doc.getDocumentElement());

		signImpl(dsc, digestMethod, signatureMethod, referenceURI, publicKey,
				x509Certificate);

		return doc;
	}

	/**
	 * Sign the root element
	 * 
	 * @param doc
	 * @param signingKey
	 * @param publicKey
	 * @param digestMethod
	 * @param signatureMethod
	 * @param referenceURI
	 * @return
	 * @throws GeneralSecurityException
	 * @throws XMLSignatureException
	 * @throws MarshalException
	 */
	public static Document sign(SignatureUtilTransferObject dto)
			throws GeneralSecurityException, MarshalException,
			XMLSignatureException {
		Document doc = dto.getDocumentToBeSigned();
		KeyPair keyPair = dto.getKeyPair();
		Node nextSibling = dto.getNextSibling();
		String digestMethod = dto.getDigestMethod();
		String referenceURI = dto.getReferenceURI();
		String signatureMethod = dto.getSignatureMethod();

		LOGGER.trace("Document to be signed=" + DocumentUtil.asString(doc));

		PrivateKey signingKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();

		DOMSignContext dsc = new DOMSignContext(signingKey,
				doc.getDocumentElement(), nextSibling);

		signImpl(dsc, digestMethod, signatureMethod, referenceURI, publicKey,
				dto.getX509Certificate());

		return doc;
	}

	/**
	 * Validate a signed document with the given public key
	 * 
	 * @param signedDoc
	 * @param publicKey
	 * @return
	 * @throws MarshalException
	 * @throws XMLSignatureException
	 */
	@SuppressWarnings("unchecked")
	public static boolean validate(Document signedDoc, Key publicKey)
			throws MarshalException, XMLSignatureException {
		if (signedDoc == null) {
			throw LOGGER.nullArgumentError("Signed Document");
		}

		propagateIDAttributeSetup(signedDoc.getDocumentElement(),
				signedDoc.getDocumentElement());

		NodeList nl = signedDoc.getElementsByTagNameNS(XMLSignature.XMLNS,
				"Signature");
		if (nl == null || nl.getLength() == 0) {
			throw LOGGER.nullValueError("Cannot find Signature element");
		}
		if (publicKey == null) {
			throw LOGGER.nullValueError("Public Key");
		}

		// написан GostKeyHandler, где GOSTKeyValue из запроса помещается в
		// "SENDER_PUBLIC_KEY"
		// а здесь это будет родной параметр publicKey.

		DOMValidateContext valContext = new DOMValidateContext(publicKey,
				nl.item(0));
		XMLSignature signature = fac.unmarshalXMLSignature(valContext);

		boolean coreValidity = signature.validate(valContext);

		if (LOGGER.isTraceEnabled() && !coreValidity) {
			boolean sv = signature.getSignatureValue().validate(valContext);
			LOGGER.trace("Signature validation status: " + sv);

			List<Reference> references = signature.getSignedInfo()
					.getReferences();
			for (Reference ref : references) {
				LOGGER.trace("[Ref id=" + ref.getId() + ":uri=" + ref.getURI()
						+ "]validity status:" + ref.validate(valContext));
			}
		}
		return coreValidity;
	}

	/**
	 * Marshall a SignatureType to output stream
	 * 
	 * @param signature
	 * @param os
	 * @throws SAXException
	 * @throws JAXBException
	 */
	public static void marshall(SignatureType signature, OutputStream os)
			throws JAXBException, SAXException {
		throw LOGGER.notImplementedYet("NYI");
	
	}

	/**
	 * Marshall the signed document to an output stream
	 * 
	 * @param signedDocument
	 * @param os
	 * @throws TransformerException
	 */
	public static void marshall(Document signedDocument, OutputStream os)
			throws TransformerException {
		TransformerFactory tf = TransformerUtil.getTransformerFactory();
		Transformer trans = tf.newTransformer();
		trans.transform(DocumentUtil.getXMLSource(signedDocument),
				new StreamResult(os));
	}

	/**
	 * Given the X509Certificate in the keyinfo element, get a
	 * {@link X509Certificate}
	 * 
	 * @param certificateString
	 * @return
	 * @throws ProcessingException
	 */
	public static X509Certificate getX509CertificateFromKeyInfoString(
			String certificateString) throws ProcessingException {
		X509Certificate cert = null;
		StringBuilder builder = new StringBuilder();
		builder.append("-----BEGIN CERTIFICATE-----\n")
				.append(certificateString)
				.append("\n-----END CERTIFICATE-----");

		String derFormattedString = builder.toString();

		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			ByteArrayInputStream bais = new ByteArrayInputStream(
					derFormattedString.getBytes());

			while (bais.available() > 0) {
				cert = (X509Certificate) cf.generateCertificate(bais);
			}
		} catch (java.security.cert.CertificateException e) {
			throw LOGGER.processingError(e);
		}
		return cert;
	}

	
	private static void signImpl(DOMSignContext dsc, String digestMethod,
			String signatureMethod, String referenceURI, PublicKey publicKey,
			X509Certificate x509Certificate) throws GeneralSecurityException,
			MarshalException, XMLSignatureException {
		dsc.setDefaultNamespacePrefix("dsig");

		DigestMethod digestMethodObj = fac.newDigestMethod(digestMethod, null);
		Transform transform1 = fac.newTransform(Transform.ENVELOPED,
				(TransformParameterSpec) null);
		Transform transform2 = fac.newTransform(
				"http://www.w3.org/2001/10/xml-exc-c14n#",
				(TransformParameterSpec) null);

		List<Transform> transformList = new ArrayList<Transform>();
		transformList.add(transform1);
		transformList.add(transform2);

		Reference ref = fac.newReference(referenceURI, digestMethodObj,
				transformList, null, null);

		CanonicalizationMethod canonicalizationMethod = fac
				.newCanonicalizationMethod(canonicalizationMethodType,
						(C14NMethodParameterSpec) null);

		List<Reference> referenceList = Collections.singletonList(ref);
		SignatureMethod signatureMethodObj = fac.newSignatureMethod(
				signatureMethod, null);
		SignedInfo si = fac.newSignedInfo(canonicalizationMethod,
				signatureMethodObj, referenceList);

		KeyInfo ki = null;
		if (includeKeyInfoInSignature) {
			ki = createKeyInfo(publicKey, x509Certificate);
		}
		XMLSignature signature = fac.newXMLSignature(si, ki);

		signature.sign(dsc);
	}

	private static KeyInfo createKeyInfo(PublicKey publicKey,
			X509Certificate x509Certificate) throws KeyException {
		KeyInfoFactory keyInfoFactory = fac.getKeyInfoFactory();
		KeyInfo keyInfo = null;
		KeyValue keyValue = null;
		// Just with public key
		if (publicKey != null) {
			keyValue = keyInfoFactory.newKeyValue(publicKey);
			keyInfo = keyInfoFactory.newKeyInfo(Collections
					.singletonList(keyValue));
		}
		if (x509Certificate != null) {
			List x509list = new ArrayList();

			x509list.add(x509Certificate);
			X509Data x509Data = keyInfoFactory.newX509Data(x509list);
			List items = new ArrayList();

			items.add(x509Data);
			if (keyValue != null) {
				items.add(keyValue);
			}
			keyInfo = keyInfoFactory.newKeyInfo(items);
		}
		return keyInfo;
	}
}
