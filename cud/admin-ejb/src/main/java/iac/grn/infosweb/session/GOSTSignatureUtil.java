package iac.grn.infosweb.session;

import org.picketlink.common.PicketLinkLogger;
import org.picketlink.common.PicketLinkLoggerFactory;
import org.picketlink.common.constants.JBossSAMLConstants;
import org.picketlink.identity.federation.core.constants.PicketLinkFederationConstants;
import org.picketlink.identity.xmlsec.w3.xmldsig.SignatureType;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;

/**
 * Signature utility for signing content
 *
 * @author 
 * @since 
 */

 public class GOSTSignatureUtil {

    
    private static final PicketLinkLogger LOGGER = PicketLinkLoggerFactory.getLogger();
    
    /**
     * Marshall a SignatureType to output stream
     *
     * @param signature
     * @param os
     * @throws SAXException
     * @throws JAXBException
     */
    public static void marshall(SignatureType signature, OutputStream os) throws JAXBException, SAXException {
        throw LOGGER.notImplementedYet("NYI");
       
    }
    
    /**
     * Get the XML Signature URI for the algo (RSA, DSA)
     *
     * @param algo
     * @return
     */
    public static String getXMLSignatureAlgorithmURI(String algo) {
        String xmlSignatureAlgo = null;

        if ("DSA".equalsIgnoreCase(algo)) {
            xmlSignatureAlgo = JBossSAMLConstants.SIGNATURE_SHA1_WITH_DSA.get();
        } else if ("RSA".equalsIgnoreCase(algo)) {
            xmlSignatureAlgo = JBossSAMLConstants.SIGNATURE_SHA1_WITH_RSA.get();
        }
        return xmlSignatureAlgo;
    }

    /**
     * Sign a string using the private key
     *
     * @param stringToBeSigned
     * @param signingKey
     * @return
     * @throws GeneralSecurityException
     */
    public static byte[] sign(String stringToBeSigned, PrivateKey signingKey) throws GeneralSecurityException {
        if (stringToBeSigned == null) {
			throw LOGGER.nullArgumentError("stringToBeSigned");
		}
        if (signingKey == null) {
			throw LOGGER.nullArgumentError("signingKey");
		}

         
        Signature sig = Signature.getInstance("GOST3411withGOST3410EL");
        
        sig.initSign(signingKey);
        sig.update(stringToBeSigned.getBytes());
        return sig.sign();
    }

    /**
     * Validate the signed content with the signature value
     *
     * @param signedContent
     * @param signatureValue
     * @param validatingKey
     * @return
     * @throws GeneralSecurityException
     */
    public static boolean validate(byte[] signedContent, byte[] signatureValue, PublicKey validatingKey)
            throws GeneralSecurityException {
        if (signedContent == null) {
			throw LOGGER.nullArgumentError("signedContent");
		}
        if (signatureValue == null) {
			throw LOGGER.nullArgumentError("signatureValue");
		}
        if (validatingKey == null) {
			throw LOGGER.nullArgumentError("validatingKey");
		}

        // We assume that the sigatureValue has the same algorithm as the public key
        // If not, there will be an exception anyway
       
        Signature sig = Signature.getInstance("GOST3411withGOST3410EL");
        
        sig.initVerify(validatingKey);
        sig.update(signedContent);
        return sig.verify(signatureValue);
    }

    /**
     * Validate the signature using a x509 certificate
     *
     * @param signedContent
     * @param signatureValue
     * @param signatureAlgorithm
     * @param validatingCert
     * @return
     * @throws GeneralSecurityException
     */
    public static boolean validate(byte[] signedContent, byte[] signatureValue, String signatureAlgorithm,
            X509Certificate validatingCert) throws GeneralSecurityException {
        if (signedContent == null) {
			throw LOGGER.nullArgumentError("signedContent");
		}
        if (signatureValue == null) {
			throw LOGGER.nullArgumentError("signatureValue");
		}
        if (signatureAlgorithm == null) {
			throw LOGGER.nullArgumentError("signatureAlgorithm");
		}
        if (validatingCert == null) {
			throw LOGGER.nullArgumentError("validatingCert");
		}

        Signature sig = getSignature(signatureAlgorithm);

        sig.initVerify(validatingCert);
        sig.update(signedContent);
        return sig.verify(signatureValue);
    }
    


    private static Signature getSignature(String algo) throws GeneralSecurityException {
        Signature sig = null;

        if ("DSA".equalsIgnoreCase(algo)) {
            sig = Signature.getInstance(PicketLinkFederationConstants.DSA_SIGNATURE_ALGORITHM);
        } else if ("RSA".equalsIgnoreCase(algo)) {
            sig = Signature.getInstance(PicketLinkFederationConstants.RSA_SIGNATURE_ALGORITHM);
        } else {
			throw LOGGER.signatureUnknownAlgo(algo);
		}
        return sig;
    }

	
}
