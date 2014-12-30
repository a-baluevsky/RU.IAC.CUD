package ru.spb.iac.cud.exceptions.web;

import ru.spb.iac.cud.items.CodesErrors;

 public class BaseError extends Exception {

	private static final long serialVersionUID = 1L;

	private Enum<CodesErrors> codeError;

	public BaseError(String message) {
		super(message);
	}

	public BaseError(String message, Enum<CodesErrors> codeError) {
		super(message);
		this.codeError = codeError;
	}

	public Enum<CodesErrors> getCodeError() {
		return codeError;
	}

	public void setCodeError(Enum<CodesErrors> codeError) {
		this.codeError = codeError;
	}

}
