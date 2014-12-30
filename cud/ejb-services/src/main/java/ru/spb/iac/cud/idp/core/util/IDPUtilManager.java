package ru.spb.iac.cud.idp.core.util;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.xml.security.utils.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.core.util.CUDConstants;

/**
 * EJB для предоставления сертификата системы по коду системы
 * 
 * @author bubnov
 * 
 */
@Stateless
 public class IDPUtilManager implements IDPUtilManagerLocal,
		IDPUtilManagerRemote {

	@PersistenceContext(unitName = "AuthServices")
	EntityManager em;

	private static final Logger LOGGER = LoggerFactory.getLogger(IDPUtilManager.class);

	/**
	 * предоставление сертификата системы по коду системы
	 */
	public X509Certificate system_cert(String domain_name) throws Exception {

		X509Certificate userCertX = null;
		String certDataX = null;
		try {

			// domain_name - это код системы или код подсистемы
			// берётся напрямую из issuer

			LOGGER.debug("system_cert:01:" + domain_name);

			// ищем код в системах
			if (domain_name.startsWith(CUDConstants.armPrefix)) {

				certDataX = (String) em
						.createNativeQuery(
								"select to_char(T1.CERT_DATE) "
										+ "from AC_IS_BSS_T t1 "
										+ "where T1.SIGN_OBJECT=? ")
						.setParameter(1, domain_name).getSingleResult();

			} else if (domain_name.startsWith(CUDConstants.subArmPrefix)) {
				// подсистемы
				certDataX = (String) em
						.createNativeQuery(
								"select to_char(T1.CERT_DATE) "
										+ "from AC_SUBSYSTEM_CERT_BSS_T t1 "
										+ "where T1.SUBSYSTEM_CODE=? ")
						.setParameter(1, domain_name).getSingleResult();

			} else if (domain_name.startsWith(CUDConstants.groupArmPrefix)) {
				// группы систем

				certDataX = (String) em
						.createNativeQuery(
								"select to_char(T1.CERT_DATA) "
										+ "from GROUP_SYSTEMS_KNL_T t1 "
										+ "where T1.GROUP_CODE=? ")
						.setParameter(1, domain_name).getSingleResult();

			}

			 

			if (certDataX != null) {

				CertificateFactory userCf = CertificateFactory
						.getInstance("X.509");
				userCertX = (X509Certificate) userCf
						.generateCertificate(new ByteArrayInputStream(Base64.decode(certDataX)));

			}

			 

		} catch (Exception e) {
			LOGGER.error("system_cert:error:", e);
		}

		// если result = null, то будет выброшено исключение
		// в KeySto/reKeyManager.getVali/datingKey() -
		// throw log/ger.keyS/toreMissingDomainAlias(domain);

		return  userCertX  ;
	}

}
