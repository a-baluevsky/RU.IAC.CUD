package ru.spb.iac.cud.services.access;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.exceptions.InvalidCredentials;
import ru.spb.iac.cud.exceptions.TokenExpired;
import ru.spb.iac.cud.items.Attribute;
import ru.spb.iac.cud.items.AuditFunction;
import ru.spb.iac.cud.items.Role;
import ru.spb.iac.cud.items.Token;

@WebService(targetNamespace = "http://access.services.cud.iac/")
public interface AccessServices {
	
	 public static final String NS = "http://access.services.cud.iac/";
	
	 @WebResult(targetNamespace = NS)
	 public Token authenticate_login(
			 @WebParam(name = "login", targetNamespace = NS) String login, 
			 @WebParam(name = "password", targetNamespace = NS) String password) throws GeneralFailure, InvalidCredentials;
    
	 public Token authenticate_cert() throws GeneralFailure, InvalidCredentials;
	
	 @WebResult(targetNamespace = NS)
	 public List<Role> authorize(
			 @WebParam(name = "idIS", targetNamespace = NS) String idIS,
			 @WebParam(name = "subject", targetNamespace = NS) Token subject) throws GeneralFailure, TokenExpired;
	 
	 @WebResult(targetNamespace = NS)
	 public List<Attribute> attributes(
			/* @WebParam(name = "attributeNames", targetNamespace = NS) List<String> attributeNames,*/
			 @WebParam(name = "subject", targetNamespace = NS) Token subject) throws GeneralFailure, TokenExpired;

	 @WebResult(targetNamespace = NS)
	 public void audit(
			 @WebParam(name = "idIS", targetNamespace = NS) String idIS,
			 @WebParam(name = "userFunctions", targetNamespace = NS) List<AuditFunction> userFunctions,
			 @WebParam(name = "subject", targetNamespace = NS) Token subject) throws GeneralFailure;
	 
	 @WebResult(targetNamespace = NS)
	 public void logout(
			 @WebParam(name = "subject", targetNamespace = NS) Token subject) throws GeneralFailure;
	 
}
