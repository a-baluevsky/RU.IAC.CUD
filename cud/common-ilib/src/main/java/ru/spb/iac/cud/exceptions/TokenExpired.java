package ru.spb.iac.cud.exceptions;

import javax.xml.ws.WebFault;

@WebFault(name = "tokenExpired")
 public class TokenExpired extends Exception {

	public TokenExpired(String message) {
		super(message);
	}

}
