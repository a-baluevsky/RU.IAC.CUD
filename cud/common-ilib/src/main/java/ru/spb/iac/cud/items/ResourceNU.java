package ru.spb.iac.cud.items;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resource", propOrder = { "code", "name", "description",
		"codesRoles" })
 public class ResourceNU {

	@XmlElement(name = "code", required = true, namespace = "http://admin.services.cud.iac.spb.ru/")
	private String code;

	@XmlElement(name = "name", required = true, namespace = "http://admin.services.cud.iac.spb.ru/")
	private String name;

	@XmlElement(name = "description", namespace = "http://admin.services.cud.iac.spb.ru/")
	private String description;

	@XmlElement(name = "codesRoles", /* required=true, */namespace = "http://util.services.cud.iac.spb.ru/")
	private List<String> codesRoles;

	public ResourceNU() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<String> getCodesRoles() {
		return codesRoles;
	}

	public void setCodesRoles(List<String> codesRoles) {
		this.codesRoles = codesRoles;
	}
}
