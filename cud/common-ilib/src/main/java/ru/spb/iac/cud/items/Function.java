package ru.spb.iac.cud.items;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "functions", propOrder = { "code", "name", "description" })
 public class Function {

	@XmlElement(name = "code", required = true)
	private String code;

	@XmlElement(name = "name", required = true)
	private String name;

	@XmlElement(name = "description")
	private String description;

	public void Function() {
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
}
