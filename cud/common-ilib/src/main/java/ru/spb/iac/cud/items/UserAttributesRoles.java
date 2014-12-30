package ru.spb.iac.cud.items;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "userAttributesRoles", propOrder = { "codesRoles" })
 public class UserAttributesRoles extends UserAttributes {

	@XmlElement(name = "codesRoles")
	private List<String> codesRoles;

	public UserAttributesRoles() {
	}

	public List<String> getCodesRoles() {
		return codesRoles;
	}

	public void setCodesRoles(List<String> codesRoles) {
		this.codesRoles = codesRoles;
	}

}
