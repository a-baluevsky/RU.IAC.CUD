package ru.spb.iac.cud.items;

import java.util.List;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "isUsers", propOrder = { "count", "userAttributesRoles" })
 public class ISUsers {

	@XmlElement(name = "count")
	private Integer count;

	@XmlElement(name = "userAttributesRoles")
	private List<UserAttributesRoles> userAttributesRoles = new ArrayList<UserAttributesRoles>();

	public ISUsers() {
	}

	public Integer getCount() {
		return this.count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public List<UserAttributesRoles> getUserAttributesRoles() {
		return userAttributesRoles;
	}

	public void setUserAttributesRoles(
			List<UserAttributesRoles> userAttributesRoles) {
		this.userAttributesRoles = userAttributesRoles;
	}

	
}
