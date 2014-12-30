package ru.spb.iac.cud.items.app;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "appAccept", propOrder = { "number", "date", "secret" })
 public class AppAccept {

	@XmlElement(name = "number", required = true, namespace = "http://application.services.cud.iac.spb.ru/")
	private String number;

	@XmlElement(name = "date", required = true, namespace = "http://application.services.cud.iac.spb.ru/")
	private Date date;

	@XmlElement(name = "secret", required = true, namespace = "http://application.services.cud.iac.spb.ru/")
	private String secret;

	public AppAccept() {
	}

	public AppAccept(String number, String secret) {
		this.number = number;
		this.secret = secret;
	}

	public String getNumber() {
		return this.number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSecret() {
		return this.secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
}
