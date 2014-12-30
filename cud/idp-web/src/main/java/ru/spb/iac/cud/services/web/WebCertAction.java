package ru.spb.iac.cud.services.web;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathValidator;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.Session;
import org.jboss.as.web.security.SecurityContextAssociationValve;
import org.picketlink.common.constants.GeneralConstants;
import org.picketlink.identity.federation.api.saml.v2.request.SAML2Request;
import org.picketlink.identity.federation.core.saml.v2.common.SAMLDocumentHolder;
import org.picketlink.identity.federation.saml.v2.protocol.AuthnRequestType;
import org.picketlink.identity.federation.web.util.PostBindingUtil;
import org.picketlink.identity.federation.web.util.RedirectBindingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.objsys.asn1j.runtime.Asn1BerDecodeBuffer;

import ru.spb.iac.cud.context.ContextAccessWebManager;
import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.exceptions.InvalidCredentials;
import ru.spb.iac.cud.exceptions.RevokedCertificate;
import com.objsys.asn1j.runtime.Asn1BerEncodeBuffer;

import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.ContentInfo;
import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.DigestAlgorithmIdentifier;
import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.SignedData;
import ru.CryptoPro.JCP.JCP;
import ru.CryptoPro.JCP.params.OID;
import ru.CryptoPro.JCP.tools.Decoder;

 public class WebCertAction extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebCertAction.class);

	private static final long serialVersionUID = 1L;

	public static final String STR_CMS_OID_SIGNED = "1.2.840.113549.1.7.2";
	public static final String DIGEST_OID = JCP.GOST_DIGEST_OID;

private static String alias_root = "уцспбгуп«спбиац».crt";

	private String root_sn = null;

	private static String cert_store_url;

	private static KeyStore keyStore = null;
			
	public WebCertAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init(ServletConfig config) throws ServletException {
		try {

			if (cert_store_url == null) {
				cert_store_url = config.getServletContext().getInitParameter(
						"cert_store_url");
			}

			if (cert_store_url == null) {
				throw new Exception("cert_store_url is not set!!!");
			}

			 

		} catch (Exception e) {
			LOGGER.error("error:", e);
		}
	}

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		String backUrl = null, forceBack = null;
		String success = "false";
		String signatureValue = null;
		String repeatLoginUrl = null;
		int revokedCertificate = 0;

		String loginUser = null;

		try {

			backUrl = request.getParameter("backUrl");
			signatureValue = request.getParameter("signatureValue");

			forceBack = request.getParameter("forceBack");

			if (signatureValue != null) {

				String certSN = validate(signatureValue);

				LOGGER.debug("service:certSN:" + certSN);

				if (certSN != null) {

					loginUser = (new ContextAccessWebManager())
							.authenticate_cert_sn(certSN,
									getIPAddress(request),
									getCodeSystem(request));

					success = "true";

					HttpSession hs = request.getSession();

					hs.setAttribute("login_user", loginUser);

				}
			}
		} catch (InvalidCredentials e1) {
			LOGGER.error("error1:" + e1.getMessage());
		} catch (GeneralFailure e2) {
			LOGGER.error("error2:" + e2.getMessage());
		} catch (RevokedCertificate e3) {
			revokedCertificate = 1;
			LOGGER.error("error3:" + e3.getMessage());
		} catch (Exception e4) {
			LOGGER.error("error4:" + e4.getMessage());
		}

		if ("true".equals(success)) {

			request.getSession().setAttribute("cud_auth_type",
					"urn:oasis:names:tc:SAML:2.0:ac:classes:X509");

			request.getSession().setAttribute("authenticate", "success");
			response.sendRedirect(request.getContextPath()
					+ "/"
					+ (request.getParameter("overauth") != null ? "?overauth"
							: ""));

		} else {

			if (forceBack == null) {// всегда здесь

				repeatLoginUrl = request.getContextPath()
						+ "/services/access_cert.jsp?success=false"
						+ (backUrl != null ? "&backUrl=" + backUrl : "")
						+ (revokedCertificate == 1 ? "&revokedCertificate=1"
								: "")
						+ (request.getParameter("overauth") != null ? "&overauth"
								: "");

				response.sendRedirect(repeatLoginUrl);

			}
		}
	}

	
	private String getIPAddress(HttpServletRequest request) {

		String ipAddress = request.getRemoteAddr();

		return ipAddress;
	}

	private String validate(String message) {

		try {

			final Decoder decoder = new Decoder();
			final byte[] enc = decoder.decodeBuffer(new ByteArrayInputStream(
					message.getBytes()));

			LOGGER.debug("validate:04");

			return CMSVerify(enc, null, "12345".getBytes());
		} catch (Exception e) {
			LOGGER.error("validate:error:", e);
		}
		return null;

	}

	public String CMSVerify(byte[] buffer, Certificate[] certs, byte[] data)
			throws Exception {
		// clear buffers fo logs

		 

		final Asn1BerDecodeBuffer asnBuf = new Asn1BerDecodeBuffer(buffer);

		final ContentInfo all = new ContentInfo();

		all.decode(asnBuf);

		if (!new OID(STR_CMS_OID_SIGNED).eq(all.contentType.value)) {
			throw new Exception("Not supported");
		}
		final SignedData cms = (SignedData) all.content;
		
		
		OID digestOid = null;
		final DigestAlgorithmIdentifier digestAlgorithmIdentifier = new DigestAlgorithmIdentifier(
				new OID(DIGEST_OID).value);
		for (int i = 0; i < cms.digestAlgorithms.elements.length; i++) {
			if (cms.digestAlgorithms.elements[i].algorithm
					.equals(digestAlgorithmIdentifier.algorithm)) {
				digestOid = new OID(
						cms.digestAlgorithms.elements[i].algorithm.value);
				break;
			}
		}
		if (digestOid == null) {
			throw new Exception("Unknown digest");
		}
		

		if (cms.certificates != null) {

			 

			// Проверка на вложенных сертификатах
			for (int i = 0; i < cms.certificates.elements.length; i++) {
				final Asn1BerEncodeBuffer encBuf = new Asn1BerEncodeBuffer();
				cms.certificates.elements[i].encode(encBuf);

				final CertificateFactory cf = CertificateFactory
						.getInstance("X.509");
				final X509Certificate cert = (X509Certificate) cf
						.generateCertificate(encBuf.getInputStream());

				 
				 
				 
				 
				 

				if (root_sn() != null
						&& !root_sn()
								.equals(dec_to_hex(cert.getSerialNumber()))) {

					if (chain_check(cert)) {
						return dec_to_hex(cert.getSerialNumber());
					}
				}

			}
		}

		return null;
	}

	public static boolean chain_check(Certificate pcert) {
		// TODO Auto-generated method stub
		boolean result = false;

		try {

			if(keyStore==null) {
			keyStore = KeyStore.getInstance("CertStore", "JCP");

			keyStore.load(new FileInputStream(cert_store_url),
					"Access_Control".toCharArray());
			}
			
			

			Certificate tr = keyStore.getCertificate(alias_root);
			Certificate crt = pcert;
			

			 

			final Certificate[] certs = new Certificate[2];
			certs[0] = crt;
			certs[1] = tr; // root

			final Set trust = new HashSet(0);
			trust.add(new TrustAnchor((X509Certificate) tr, null));

			final List cert = new ArrayList(0);
			for (int i = 0; i < certs.length; i++) {
				cert.add(certs[i]);
			}

			// Параметры
			final PKIXBuilderParameters cpp = new PKIXBuilderParameters(trust,
					null);
			cpp.setSigProvider(null);
			final CollectionCertStoreParameters par = new CollectionCertStoreParameters(
					cert);
			final CertStore store = CertStore.getInstance("Collection", par);
			cpp.addCertStore(store);
			final X509CertSelector selector = new X509CertSelector();
			selector.setCertificate((X509Certificate) crt);
			cpp.setTargetCertConstraints(selector);

			// Сертификаты (CertPath)
			// 1)просто из списка сертификатов (в правильном порядке)
			// final CertificateFactory cf =
			// CertificateFactory.getInstance("X509");
			// final CertPath cp = cf.generateCertPath(cert);

			// 2) построение цепочки
			// а) с проверкой crl
			// cpp.setRevocationEnabled(true);
			// для использования расширения сертификата CRL Distribution Points
			// установить Property com.sun.security.enableCRLDP,
			// true
			// или Property com.ibm.security.enableCRLDP true

			// б) без проверки crl
			cpp.setRevocationEnabled(false);
			final PKIXCertPathBuilderResult res = (PKIXCertPathBuilderResult) CertPathBuilder
					.getInstance("PKIX").build(cpp);
			final CertPath cp = res.getCertPath();

			// Проверка
			final CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
			cpp.setRevocationEnabled(false);
			cpv.validate(cp, cpp);

			result = true;

		

		} catch (Exception e) {
			LOGGER.error("error:", e);
		}

		return result;
	}

	private static String dec_to_hex(BigInteger bi) {

		String result = null;

		try {
			result = bi.toString(16);
		} catch (NumberFormatException e) {
			LOGGER.error("Error! tried to parse an invalid number format");
		}
		return result;
	}

	public String root_sn() {
		// TODO Auto-generated method stub

		if (root_sn == null) {

			 

			try {

			if(keyStore==null) {
				keyStore = KeyStore.getInstance("CertStore", "JCP");

				keyStore.load(new FileInputStream(cert_store_url),
						"Access_Control".toCharArray());
				}
				
				X509Certificate tr = (X509Certificate) keyStore
						.getCertificate(alias_root);

				root_sn = dec_to_hex(tr.getSerialNumber());

			} catch (Exception e) {
				LOGGER.error("root_sn:error:", e);
			}
		}

		return root_sn;
	}

	private String getCodeSystem(HttpServletRequest request) {

		// !!!
		// для метода важно, что в ExtFilter при
		// if(...||requestURI.endsWith(cert_to_auth)...)
		// идёт установка
		// request2/.getSessionInternal/()/.setNote(GeneralConstants.SAML_REQUEST_KEY/,
		// request/.getParameter/(SAMLMessageKey))
		// и request.getSession()/.setAttribute/("incoming_http_method",
		// request/.getParameter(HTTPMethodKey))

		LOGGER.debug("getCodeSystem:031");
		String result = null;

		try {
			org.apache.catalina.connector.Request request2 = null;
			request2 = SecurityContextAssociationValve.getActiveRequest();
			Session session = request2.getSessionInternal(false);

			// "SAMLRequest"
			String samlRequestMessage = (String) session
					.getNote(GeneralConstants.SAML_REQUEST_KEY);

			if (samlRequestMessage != null) {

				
				boolean begin_req_method = "GET".equals((String) request
						.getSession().getAttribute("incoming_http_method"));

				SAMLDocumentHolder samlDocumentHolder = getSAMLDocumentHolder(
						samlRequestMessage, begin_req_method);

				if (samlDocumentHolder != null) {

					if (samlDocumentHolder.getSamlObject() != null) {
						AuthnRequestType requestAbstractType = (AuthnRequestType) samlDocumentHolder
								.getSamlObject();
						result = requestAbstractType.getIssuer().getValue();

						LOGGER.debug("getCodeSystem:032:" + result);

					}
				}

			}

		} catch (Exception e) {
			LOGGER.error("getCodeSystem:error:", e);
		}

		return result;
	}

	public SAMLDocumentHolder getSAMLDocumentHolder(String samlMessage,
			boolean redirectProfile) throws Exception {
		LOGGER.debug("getSAMLDocumentHolder:01:" + redirectProfile);

		InputStream is = null;
		SAML2Request saml2Request = new SAML2Request();
		try {
			if (redirectProfile) {
				is = RedirectBindingUtil.base64DeflateDecode(samlMessage);
			} else {
				byte[] samlBytes = PostBindingUtil.base64Decode(samlMessage);
				is = new ByteArrayInputStream(samlBytes);
			}
		} catch (Exception rte) {
			LOGGER.error("getSAMLDocumentHolder:error:" + rte);
			throw rte;
		}

		saml2Request.getSAML2ObjectFromStream(is);

		return saml2Request.getSamlDocumentHolder();
	}

}
