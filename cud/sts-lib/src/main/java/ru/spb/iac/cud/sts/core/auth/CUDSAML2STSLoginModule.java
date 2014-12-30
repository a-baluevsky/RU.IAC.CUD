package ru.spb.iac.cud.sts.core.auth;

import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.login.LoginException;

import org.jboss.security.JBossJSSESecurityDomain;
import org.picketlink.identity.federation.bindings.jboss.auth.AS7AuthCacheInvalidationFactory;
import org.picketlink.identity.federation.core.factories.JBossAuthCacheInvalidationFactory;
import org.picketlink.identity.federation.core.saml.v2.util.AssertionUtil;
import org.picketlink.identity.federation.core.wstrust.plugins.saml.SAMLUtil;
import org.picketlink.identity.federation.saml.v2.assertion.AssertionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import ru.spb.iac.cud.sts.core.auth.util.GOSTAssertionUtil;

/**
 * <p>
 * This {@code LoginModule} implements the local validation of SAML assertions
 * on AS7. The specified {@code localValidationSecurityDomain} property must
 * correspond to a AS7 JSSE domain that configures a truststore and a
 * server-alias that identifies the certificate used to validate the assertions.
 * </p>
 * 
 * @author <a href="mailto:sguilhen@redhat.com">Stefan Guilhen</a>
 */

 public class CUDSAML2STSLoginModule extends CUDSAML2STSCommonLoginModule {

	private static final Logger LOGGERSLF4J = LoggerFactory
			.getLogger(CUDSAML2STSLoginModule.class);

	protected boolean localValidation(Element assertionElement)
			throws Exception {
		// For unit tests
		if (localTestingOnly) {
			return true;
		}

		try {
			LOGGERSLF4J.debug("localValidation:01:"
					+ localValidationSecurityDomain);

			Context ctx = new InitialContext();
			String jsseLookupString = super.localValidationSecurityDomain
					+ "/jsse";

			JBossJSSESecurityDomain sd = (JBossJSSESecurityDomain) ctx
					.lookup(jsseLookupString);

			LOGGERSLF4J.debug("localValidation:02:" + sd);

			String securityDomain = sd.getSecurityDomain();

			LOGGERSLF4J.debug("localValidation:03:" + securityDomain);

			KeyStore ts = sd.getTrustStore();

			LOGGERSLF4J.debug("localValidation:04:" + ts);

			if (ts == null) {
				throw logger
						.authNullKeyStoreFromSecurityDomainError(securityDomain);
			}

			String alias = sd.getServerAlias();

			LOGGERSLF4J.debug("localValidation:05:" + alias);

			if (alias == null) {
				throw logger
						.authNullKeyStoreAliasFromSecurityDomainError(securityDomain);
			}

			Certificate cert = ts.getCertificate(alias);
			if (cert == null) {
				throw logger.authNoCertificateFoundForAliasError(alias,
						securityDomain);
			}

			PublicKey publicKey = cert.getPublicKey();
			// boolean sigValid =
			// AssertionUtil.isSignatureValid(assertionElement, publicKey);
			boolean sigValid = GOSTAssertionUtil.isSignatureValid(
					assertionElement, publicKey);

			if (!sigValid) {
				throw logger.authSAMLInvalidSignatureError();
			}

			AssertionType assertion = SAMLUtil.fromElement(assertionElement);
			if (AssertionUtil.hasExpired(assertion)) {
				throw logger.authSAMLAssertionExpiredError();
			}
		} catch (NamingException e) {
			throw new LoginException(e.toString());
		}
		return true;
	}

	@Override
	protected JBossAuthCacheInvalidationFactory.TimeCacheExpiry getCacheExpiry()
			throws Exception {
		return AS7AuthCacheInvalidationFactory.getCacheExpiry();
	}

}
