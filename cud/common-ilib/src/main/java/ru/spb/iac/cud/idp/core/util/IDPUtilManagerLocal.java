package ru.spb.iac.cud.idp.core.util;

import java.security.cert.X509Certificate;

import javax.ejb.Local;

/**
 * @author bubnov
 */
@Local
public interface IDPUtilManagerLocal {

	public X509Certificate system_cert(String domain_name) throws Exception;
}
