package ru.spb.iac.pl.sp.key;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.HashMap; import java.util.Map;
import java.util.List;
import javax.crypto.SecretKey;

import org.picketlink.common.PicketLinkLogger;
import org.picketlink.common.PicketLinkLoggerFactory;
import org.picketlink.common.exceptions.TrustKeyConfigurationException;
import org.picketlink.common.exceptions.TrustKeyProcessingException;
import org.picketlink.config.federation.AuthPropertyType;
import org.picketlink.config.federation.KeyValueType;
import org.picketlink.identity.federation.core.interfaces.TrustKeyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.context.ContextIDPUtilManager;

 public class KeyStoreKeyManager implements TrustKeyManager {

	private static final Logger LOGGERSLF4J = LoggerFactory
			.getLogger(KeyStoreKeyManager.class);

	private static final PicketLinkLogger LOGGER = PicketLinkLoggerFactory
			.getLogger();

	

	private final Map<String, Object> options = new HashMap();

	private final HashMap<String, String> domainAliasMap = new HashMap();

	private final HashMap<String, String> authPropsMap = new HashMap();

	private static KeyStore ks = null;
	private PublicKey publicKey = null;

	private String keyStoreURL;
	private static char[] signingKeyPass;
	private static String signingAlias;
	private String keyStorePass;
	public static final String KEYSTORE_URL = "KeyStoreURL";
	public static final String KEYSTORE_PASS = "KeyStorePass";
	public static final String SIGNING_KEY_PASS = "SigningKeyPass";
	public static final String SIGNING_KEY_ALIAS = "SigningKeyAlias";

	private PrivateKey privateKey = null;
	
	public PrivateKey getSigningKey() throws TrustKeyConfigurationException,
			TrustKeyProcessingException {
		try {
			LOGGERSLF4J.debug("getSigningKey:01");

			initKeyStore();

			 

			if(this.privateKey==null){
				this.privateKey=(PrivateKey) this.ks.getKey(this.signingAlias,
						this.signingKeyPass);
			}
			return this.privateKey;

		} catch (KeyStoreException e) {
			throw LOGGER.keyStoreConfigurationError(e);
		} catch (NoSuchAlgorithmException e) {
			throw LOGGER.keyStoreProcessingError(e);
		} catch (UnrecoverableKeyException e) {
			throw LOGGER.keyStoreProcessingError(e);
		} catch (GeneralSecurityException e) {
			throw LOGGER.keyStoreProcessingError(e);
		} catch (IOException e) {
			throw LOGGER.keyStoreProcessingError(e);
		}

	}

	// формирование подписи ЦУД
	public KeyPair getSigningKeyPair() throws TrustKeyConfigurationException,
			TrustKeyProcessingException {
		try {
			
			LOGGERSLF4J.debug("getSigningKeyPair:01");

			initKeyStore();

			 

			if(this.privateKey==null) {
				this.privateKey = getSigningKey();
			}
			

			if (this.publicKey == null) {
				 
				 

				Certificate cert = this.ks.getCertificate(this.signingAlias);

				this.publicKey = cert.getPublicKey();

			}

			 

			return new KeyPair(publicKey, this.privateKey);
		} catch (KeyStoreException e) {
			throw LOGGER.keyStoreConfigurationError(e);
		} catch (GeneralSecurityException e) {
			throw LOGGER.keyStoreProcessingError(e);
		} catch (IOException e) {
			throw LOGGER.keyStoreProcessingError(e);
		}

	}

	// есть вызовы, но дальше не использованные
	// поэтому чтобы эти вызовы не комментировать
	// будем здесь возвращать null
	public Certificate getCertificate(String alias)
			throws TrustKeyConfigurationException, TrustKeyProcessingException {

		LOGGERSLF4J.debug("getCertificate:01");
		return null;

	}

	// не используется
	public PublicKey getPublicKey(String alias)
			throws TrustKeyConfigurationException, TrustKeyProcessingException {
		LOGGERSLF4J.debug("getPublicKey:01");
		throw new TrustKeyConfigurationException("getPublicKey not used!");

	}

	// провверка подписи ИС
	public PublicKey getValidatingKey(String domain)
			throws TrustKeyConfigurationException, TrustKeyProcessingException {
		PublicKey publicKey = null;
		try {
			LOGGERSLF4J.debug("getValidatingKey:01:" + domain);

			X509Certificate cert_user = null;
			try {

				cert_user = (new ContextIDPUtilManager()).system_cert(domain);

				 

				if (cert_user != null) {
					publicKey = cert_user.getPublicKey();
				}

			} catch (Exception e) {
				LOGGERSLF4J.error("getValidatingKey:04:error", e);
			}

			 

		} catch (Exception e) {
			throw LOGGER.keyStoreProcessingError(e);
		}
		return publicKey;
	}

	private void initKeyStore() throws GeneralSecurityException, IOException {

		LOGGERSLF4J.debug("initKeyStore:01");

		if (this.ks == null) {
			 
			LOGGER.keyStoreSetup();
			setUpKeyStore();
		}
		 

		if (this.ks == null) {
			throw LOGGER.keyStoreNullStore();
		}
	}

	public void setAuthProperties(List<AuthPropertyType> authList)
			throws TrustKeyConfigurationException, TrustKeyProcessingException {
		for (AuthPropertyType auth : authList) {
			this.authPropsMap.put(auth.getKey(), auth.getValue());
		}

		this.keyStoreURL = ((String) this.authPropsMap.get("KeyStoreURL"));
		this.keyStorePass = ((String) this.authPropsMap.get("KeyStorePass"));

		this.signingAlias = ((String) this.authPropsMap.get("SigningKeyAlias"));

		String keypass = (String) this.authPropsMap.get("SigningKeyPass");
		if ((keypass == null) || (keypass.length() == 0)) {
			throw LOGGER.keyStoreNullSigningKeyPass();
		}
		this.signingKeyPass = keypass.toCharArray();
	}

	public void setValidatingAlias(List<KeyValueType> aliases)
			throws TrustKeyConfigurationException, TrustKeyProcessingException {
		for (KeyValueType alias : aliases) {
			this.domainAliasMap.put(alias.getKey(), alias.getValue());
		}
	}

	// не используется
	public SecretKey getEncryptionKey(String domain,
			String encryptionAlgorithm, int keyLength)
			throws TrustKeyConfigurationException, TrustKeyProcessingException {
		LOGGERSLF4J.debug("getEncryptionKey:01");
		throw new TrustKeyConfigurationException("getEncryptionKey not used!");

	}

	public void addAdditionalOption(String key, Object value) {
		this.options.put(key, value);
	}

	public Object getAdditionalOption(String key) {
		return this.options.get(key);
	}

	private void setUpKeyStore() throws GeneralSecurityException, IOException {
		LOGGERSLF4J.debug("setUpKeyStore:01");

		this.ks = loadKeyStore();
	}

	
	public KeyStore loadKeyStore() throws IOException {

		String keystoreType = "HDImageStore";
		String keystoreProvider = "JCP";

		FileInputStream inputStream = null;

		try {
			KeyStore keyStore = KeyStore.getInstance(keystoreType,
					keystoreProvider);

			inputStream = new FileInputStream(new File(this.keyStoreURL));

			keyStore.load(inputStream, this.keyStorePass.toCharArray());

			return keyStore;

		} catch (Exception ex) {

		
			throw new IOException(ex);

		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException ioe) {
				}
			}
		}

	}

}