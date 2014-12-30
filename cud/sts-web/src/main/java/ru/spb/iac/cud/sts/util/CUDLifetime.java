package ru.spb.iac.cud.sts.util;

import org.picketlink.common.PicketLinkLogger;
import org.picketlink.common.PicketLinkLoggerFactory;
import org.picketlink.identity.federation.core.saml.v2.util.XMLTimeUtil;
import org.picketlink.identity.federation.core.wstrust.wrappers.Lifetime;
import org.picketlink.identity.federation.ws.trust.LifetimeType;
import org.picketlink.identity.federation.ws.wss.utility.AttributedDateTime;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;


 public class CUDLifetime extends Lifetime {

	private static final PicketLinkLogger LOGGER = PicketLinkLoggerFactory
			.getLogger();

	private final LifetimeType delegate;

	private XMLGregorianCalendar created;

	private XMLGregorianCalendar expires;

	private DatatypeFactory factory;

	/**
	 * <p>
	 * Creates an instance of {@code Lifetime} with the specified parameters.
	 * </p>
	 * 
	 * @param created
	 *            a {@code GregorianCalendar} representing the token creation
	 *            time.
	 * @param expires
	 *            a {@code GregorianCalendar} representing the token expiration
	 *            time.
	 */
	public CUDLifetime(GregorianCalendar created, GregorianCalendar expires) {

		super(created, expires);

		try {
			this.factory = XMLTimeUtil.newDatatypeFactory();
		} catch (DatatypeConfigurationException dce) {
			throw LOGGER.wsTrustUnableToGetDataTypeFactory(dce);
		}

		// normalize the parameters (convert to UTC).
		// this.created = factory.newXMLGregorianCalendar(created).normalize();
		// this.expires = factory.newXMLGregorianCalendar(expires).normalize();

		this.created = factory.newXMLGregorianCalendar(created);
		this.expires = factory.newXMLGregorianCalendar(expires);

		// set the delegate fields.
		this.delegate = new LifetimeType();
		AttributedDateTime dateTime = new AttributedDateTime();
		dateTime.setValue(this.created.toXMLFormat());
		this.delegate.setCreated(dateTime);
		dateTime = new AttributedDateTime();
		dateTime.setValue(this.expires.toXMLFormat());
		this.delegate.setExpires(dateTime);

	}

	/**
	 * <p>
	 * Creates a {@code Lifetime} instance using the specified
	 * {@code LifetimeType}.
	 * </p>
	 * 
	 * @param lifetime
	 *            a reference to the {@code LifetimeType} instance that contains
	 *            the information used in the {@code Lifetime} construction.
	 */
	public CUDLifetime(LifetimeType lifetime) {

		super(lifetime);

		if (lifetime == null) {
			throw LOGGER.nullArgumentError("LifetimeType");
		}

		try {
			this.factory = XMLTimeUtil.newDatatypeFactory();
		} catch (DatatypeConfigurationException dce) {
			throw LOGGER.wsTrustUnableToGetDataTypeFactory(dce);
		}
		this.delegate = lifetime;

		// construct the created and expires instances from the lifetime object.
		this.created = factory.newXMLGregorianCalendar(lifetime.getCreated()
				.getValue());
		this.expires = factory.newXMLGregorianCalendar(lifetime.getExpires()
				.getValue());

		// check if the supplied lifetime needs to be normalized.
		if (this.created.getTimezone() != 0) {
			this.created = this.created.normalize();
			this.delegate.getCreated().setValue(this.created.toXMLFormat());
		}
		if (this.expires.getTimezone() != 0) {
			this.expires = this.expires.normalize();
			this.delegate.getExpires().setValue(this.expires.toXMLFormat());
		}
	}

	/**
	 * <p>
	 * Obtains the creation time as a {@code XMLGregorianCalendar}.
	 * </p>
	 * 
	 * @return a reference to the {@code XMLGregorianCalendar} that represents
	 *         the creation time.
	 */
	public XMLGregorianCalendar getCreated() {
		return this.created;
	}

	/**
	 * <p>
	 * Sets the creation time.
	 * </p>
	 * 
	 * @param created
	 *            a reference to the {@code XMLGregorianCalendar} that
	 *            represents the creation time to be set.
	 */
	public void setCreated(XMLGregorianCalendar created) {
		this.created = created.normalize();
		this.delegate.getCreated().setValue(this.created.toXMLFormat());
	}

	/**
	 * <p>
	 * Obtains the creation time as a {@code GregorianCalendar}.
	 * </p>
	 * 
	 * @return a reference to the {@code GregorianCalendar} that represents the
	 *         creation time.
	 */
	public GregorianCalendar getCreatedCalendar() {
		return this.created.toGregorianCalendar();
	}

	/**
	 * <p>
	 * Sets the creation time.
	 * </p>
	 * 
	 * @param created
	 *            a reference to the {@code GregorianCalendar} that represents
	 *            the creation time to be set.
	 */
	public void setCreatedCalendar(GregorianCalendar created) {
		this.setCreated(this.factory.newXMLGregorianCalendar(created));
	}

	/**
	 * <p>
	 * Obtains the expiration time as a {@code XMLGregorianCalendar}.
	 * </p>
	 * 
	 * @return a reference to the {@code XMLGregorianCalendar} that represents
	 *         the expiration time.
	 */
	public XMLGregorianCalendar getExpires() {
		return this.expires;
	}

	/**
	 * <p>
	 * Sets the expiration time.
	 * </p>
	 * 
	 * @param expires
	 *            a reference to the {@code XMLGregorianCalendar} that
	 *            represents the expiration time.
	 */
	public void setExpires(XMLGregorianCalendar expires) {
		this.expires = expires.normalize();
		this.delegate.getExpires().setValue(this.expires.toXMLFormat());
	}

	/**
	 * <p>
	 * Obtains the expiration time as a {@code GregorianCalendar}.
	 * </p>
	 * 
	 * @return a reference to the {@code GregorianCalendar} that represents the
	 *         expiration time.
	 */
	public GregorianCalendar getExpiresCalendar() {
		return this.expires.toGregorianCalendar();
	}

	/**
	 * <p>
	 * Sets the expiration time.
	 * </p>
	 * 
	 * @param expires
	 *            a reference to the {@code GregorianCalendar} that represents
	 *            the expiration time.
	 */
	public void setExpiresCalendar(GregorianCalendar expires) {
		this.setExpires(this.factory.newXMLGregorianCalendar(expires));
	}

	/**
	 * <p>
	 * Obtains a reference to the {@code LifetimeType} delegate.
	 * </p>
	 * 
	 * @return a reference to the delegate instance.
	 */
	public LifetimeType getDelegate() {
		return this.delegate;
	}
}