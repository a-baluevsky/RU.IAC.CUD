package ru.spb.iac.cud.sts.core.attrib;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.picketlink.common.PicketLinkLogger;
import org.picketlink.common.PicketLinkLoggerFactory;
import org.picketlink.common.util.Base64;
import org.picketlink.identity.federation.core.wstrust.plugins.saml.SAML20TokenAttributeProvider;
import org.picketlink.identity.federation.saml.v2.assertion.AttributeStatementType;
import org.picketlink.identity.federation.saml.v2.assertion.AttributeStatementType.ASTChoiceType;
import org.picketlink.identity.federation.saml.v2.assertion.AttributeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.context.ContextIDPAccessManager;
import ru.spb.iac.cud.idp.web.util.GOSTSignatureUtil;

 public class CUDSAML20CommonTokenRoleAttributeProvider implements
		SAML20TokenAttributeProvider {

	private static final PicketLinkLogger LOGGER = PicketLinkLoggerFactory
			.getLogger();

	private static final Logger LOGGERSLF4J = LoggerFactory
			.getLogger(CUDSAML20CommonTokenRoleAttributeProvider.class);
	/**
	 * The name of the principal in JBoss that is expected to include user roles
	 */
	public static final String JBOSS_ROLE_PRINCIPAL_NAME = "Roles";

	/**
	 * The default attribute name in the SAML Token that will carry the user's
	 * roles, if not configured otherwise
	 */
	public static final String DEFAULT_TOKEN_ROLE_ATTRIBUTE_NAME = "role";

	/**
	 * The name of the attribute in the SAML Token that will carry the user's
	 * roles
	 */
	private String tokenRoleAttributeName;

	private static final String auth_type_password = "urn:oasis:names:tc:SAML:2.0:ac:classes:password";

	private static PrivateKey privateKey = null;
	
	public void setProperties(Map<String, String> properties) {

		String roleAttrKey = this.getClass().getName()
				+ ".tokenRoleAttributeName";
		tokenRoleAttributeName = properties.get(roleAttrKey);
		if (tokenRoleAttributeName == null) {
			tokenRoleAttributeName = DEFAULT_TOKEN_ROLE_ATTRIBUTE_NAME;
		}
	}

	public AttributeStatementType getAttributeStatement() {

		return null;
	}

	public AttributeStatementType getAttributeStatement(String systemCode,
			String userCode, String authType, String lifetimeMs) {

		LOGGERSLF4J.debug("getAttributeStatement:01:" + systemCode);
		LOGGERSLF4J.debug("getAttributeStatement:02:" + userCode);

		List<String> roles = new ArrayList<String>();

		if (systemCode == null) {
			LOGGER.trace("No authentication Subject found, cannot provide any user roles!");

			return null;
		} else {
			AttributeStatementType attributeStatement = null;

			try {

				if (userCode != null && systemCode != null) {

					// роли

					roles = (new ContextIDPAccessManager()).rolesCodes(
							userCode, systemCode);

					if (roles != null && !roles.isEmpty()) {

						attributeStatement = new AttributeStatementType();
						// AttributeConstants.ROLE_IDENTIFIER_ASSERTION
						AttributeType rolesAttribute = new AttributeType(
								"USER_ROLES");
						attributeStatement.addAttribute(new ASTChoiceType(
								rolesAttribute));

						for (String role : roles) {
							rolesAttribute.addAttributeValue(role);
						}
					}

					// аттрибуты

					Map<String, String> userAttributes =
							(new ContextIDPAccessManager()) 
							.attributes(userCode);

					if (userAttributes != null && !userAttributes.isEmpty()
							&& attributeStatement == null) {

						attributeStatement = new AttributeStatementType();

					}

					if (userAttributes != null
							&& userAttributes.get("USER_UID") != null) {

						userAttributes.put(
								"TOKEN_ID",
								tokenIDCreate(userAttributes.get("USER_UID"),
									authType, lifetimeMs));

						/*
						 * //формирование tokenID
						 * 
						 * //перенесено в отдельный метод tokenIDCreate() //этот
						 * метод вызывается ещё при token_renew, поэтому он
						 * public try{
						 * 
						 * //!!!так нельзя! //так как инизиализация происходит
						 * при получении токена на систему // а если первый
						 * вызов - токен на пользователя, то //signingKeyPass и
						 * signingAlias будут пустыми!!!
						
						 */

					}

					Iterator<Entry<String, String>> it = userAttributes
							.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, String> pairs = (Map.Entry<String, String>) it
								.next();
					
						AttributeType fioAttribute = new AttributeType(
								pairs.getKey());
						attributeStatement.addAttribute(new ASTChoiceType(
								fioAttribute));
						fioAttribute.addAttributeValue(pairs.getValue());
					}

				}

			} catch (Exception e) {
				LOGGERSLF4J.error("getAttributeStatement:error:", e);

			}

			// LOGGER.trace("Returning an AttributeStatement with a [" +
			// tokenRoleAttributeName + "] attribute containing: " +
			// rolesAttribute.getAttributeValue().toString());
			return attributeStatement;
		}
	}

	public String tokenIDCreate(String userUID, String authType, String lifetime) {

		String base64tokenID = null;

		// формирование tokenID
		try {

			// !!!так нельзя!
			// так как инизиализация происходит при получении токена на систему
			// а если первый вызов - токен на пользователя, то
			// signingKeyPass и signingAlias будут пустыми!!!
			

			// надо переделать!!!

			if(this.privateKey == null) {
				
			
			char[] signingKeyPass = "Access_Control".toCharArray();
			String signingAlias = "cudvm_export";

			KeyStore ks = KeyStore.getInstance("HDImageStore", "JCP");
			ks.load(null, null);

			this.privateKey = (PrivateKey) ks.getKey(signingAlias,
					signingKeyPass);
			}
			
			LOGGERSLF4J.debug("tokenIDCreate:01+:"
					+ new Date(new Long(lifetime)));

			StringBuilder sb = new StringBuilder();

			sb.append(userUID).append("_").append(lifetime).append("_").append(authType!=null?authType:auth_type_password);

			byte[] sigValue = GOSTSignatureUtil.sign(sb.toString(), this.privateKey);

			String base64SigValue = Base64.encodeBytes(sigValue,
					Base64.DONT_BREAK_LINES);

			String tokenID = sb.toString() + "_" + base64SigValue;

			base64tokenID = Base64.encodeBytes(tokenID.getBytes("utf-8"),
					Base64.DONT_BREAK_LINES);

			LOGGERSLF4J.debug("tokenIDCreate:01:" + tokenID);
			LOGGERSLF4J.debug("tokenIDCreate:02+:" + base64tokenID);

			
			(new ContextIDPAccessManager())
			   .saveTokenID(base64tokenID, userUID);
			
			LOGGERSLF4J.debug("tokenIDCreate:03");
			
		} catch (Exception e) {
			LOGGERSLF4J.error("tokenIDCreate:tokenID:error:", e);
		}

		return base64tokenID;

	}

}
