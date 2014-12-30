package ru.spb.iac.cud.items.app;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "attribute", propOrder = { "name", "value" })
 public class AppAttribute {

	@XmlElement(name = "name", /* required=true, */namespace = "http://application.services.cud.iac.spb.ru/")
	private String name;

	@XmlElement(name = "value", /* required=true, */namespace = "http://application.services.cud.iac.spb.ru/")
	private String value;

	public AppAttribute() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "'" + name + "'=" + value;
	}
}
