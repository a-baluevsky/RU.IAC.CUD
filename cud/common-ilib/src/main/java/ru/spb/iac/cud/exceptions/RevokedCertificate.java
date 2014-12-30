package ru.spb.iac.cud.exceptions;

import javax.xml.ws.WebFault;

@WebFault(name = "revokedCertificate")
 public class RevokedCertificate extends Exception {

	public RevokedCertificate(String message) {
		super(message);
	}
}
