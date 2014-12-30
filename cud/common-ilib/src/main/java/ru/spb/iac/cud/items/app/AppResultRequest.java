package ru.spb.iac.cud.items.app;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "appResultRequest", propOrder = { "appType", "number", "secret" })
 public class AppResultRequest {

	@XmlElement(name = "appType", required = true, namespace = "http://application.services.cud.iac.spb.ru/")
	private String appType;

	@XmlElement(name = "number", required = true, namespace = "http://application.services.cud.iac.spb.ru/")
	private String number;

	@XmlElement(name = "secret", required = true, namespace = "http://application.services.cud.iac.spb.ru/")
	private String secret;

	public AppResultRequest() {
	}

	public AppResultRequest(String number, String secret) {
		this.number = number;
		this.secret = secret;
	}

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public String getNumber() {
		return this.number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getSecret() {
		return this.secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
}
