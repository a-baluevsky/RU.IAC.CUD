package ru.spb.iac.cud.services.sync;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.items.Function;
import ru.spb.iac.cud.items.Role;

@WebService(targetNamespace = SyncServices.NS)
public interface SyncServices {
	
	 public static final String NS = "http://sync.services.cud.iac/";
	 
	 @WebMethod
	 @WebResult(targetNamespace = NS)
	 public void sync_roles(
			 @WebParam(name = "idIS", targetNamespace = NS) String idIS, 
			 @WebParam(name = "roles", targetNamespace = NS) List<Role> roles) throws GeneralFailure;
    
	 @WebMethod
	 @WebResult(targetNamespace = NS)
	 public void sync_functions(
			 @WebParam(name = "idIS", targetNamespace = NS) String idIS, 
			 @WebParam(name = "functions", targetNamespace = NS) List<Function> functions) throws GeneralFailure;
	
	
	 
}
