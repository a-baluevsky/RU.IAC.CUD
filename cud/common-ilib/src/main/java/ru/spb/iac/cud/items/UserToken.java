package ru.spb.iac.cud.items;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;

 public class UserToken {

	private String tokenId;

	private List<Attribute> userAttributes;

	private List<Role> userRoles;

	public UserToken() {
	}

	public UserToken(String tokenId) {
		this.tokenId = tokenId;
	}

	public UserToken(String tokenId, List<Attribute> userAttributes) {
		this.tokenId = tokenId;
		this.setUserAttributes(userAttributes);
	}

	public UserToken(String tokenId, List<Attribute> userAttributes,
			List<Role> userRoles) {
		this.tokenId = tokenId;
		this.setUserAttributes(userAttributes);
		this.userRoles = userRoles;
	}

	@XmlElement(required = true)
	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public List<Role> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(List<Role> userRoles) {
		this.userRoles = userRoles;
	}

	public List<Attribute> getUserAttributes() {
		return userAttributes;
	}

	public void setUserAttributes(List<Attribute> userAttributes) {
		this.userAttributes = userAttributes;
	}
}
