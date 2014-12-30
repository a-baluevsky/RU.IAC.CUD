package ru.spb.iac.cud.exceptions;

import javax.xml.ws.WebFault;

@WebFault(name = "generalFailure")
 public class GeneralFailure extends Exception {

	public GeneralFailure(String message) {
		super(message);
	}
}
