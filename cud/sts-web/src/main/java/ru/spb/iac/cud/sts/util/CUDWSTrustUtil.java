package ru.spb.iac.cud.sts.util;

import org.picketlink.identity.federation.core.wstrust.WSTrustUtil;
import org.picketlink.identity.federation.core.wstrust.wrappers.Lifetime;
import java.util.GregorianCalendar;

 public class CUDWSTrustUtil extends WSTrustUtil {

	/**
	 * <p>
	 * Creates a {@code Lifetime} instance that specifies a range of time that
	 * starts at the current GMT time and has the specified duration in
	 * milliseconds.
	 * </p>
	 * 
	 * @param tokenTimeout
	 *            the token timeout value (in milliseconds).
	 * @return the constructed {@code Lifetime} instance.
	 */
	public static Lifetime createDefaultLifetime(long tokenTimeout) {
		GregorianCalendar created = new GregorianCalendar();
		GregorianCalendar expires = new GregorianCalendar();
		expires.setTimeInMillis(created.getTimeInMillis() + tokenTimeout);

		
		return new CUDLifetime(created, expires);
	}

}