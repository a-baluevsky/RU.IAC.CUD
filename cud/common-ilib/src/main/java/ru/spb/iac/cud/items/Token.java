package ru.spb.iac.cud.items;

import javax.xml.bind.annotation.XmlElement;

 public class Token {
	private String id;

	public Token() {
	}

	public Token(String id) {
		this.id = id;
	}

	@XmlElement(required = true)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
