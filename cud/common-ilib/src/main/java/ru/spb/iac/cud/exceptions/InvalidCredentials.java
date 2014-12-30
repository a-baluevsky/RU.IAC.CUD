package ru.spb.iac.cud.exceptions;

import javax.xml.ws.WebFault;

@WebFault(name = "invalidGredentials")
 public class InvalidCredentials extends Exception {

	public InvalidCredentials(String message) {
		super(message);
	}

}
