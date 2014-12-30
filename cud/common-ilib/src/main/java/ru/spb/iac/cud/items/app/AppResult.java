package ru.spb.iac.cud.items.app;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "appResult", propOrder = { "status", "attributes" })
 public class AppResult {

	@XmlElement(name = "status", /* required=true, */namespace = "http://application.services.cud.iac.spb.ru/")
	private String status;

	@XmlElement(name = "attributes", /* required=true, */namespace = "http://application.services.cud.iac.spb.ru/")
	private List<AppAttribute> attributes;

	public AppResult() {
	}

	public AppResult(String status) {
		this.status = status;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<AppAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<AppAttribute> attributes) {
		this.attributes = attributes;
	}
}
