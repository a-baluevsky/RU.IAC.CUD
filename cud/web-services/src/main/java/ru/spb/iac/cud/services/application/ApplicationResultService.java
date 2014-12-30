package ru.spb.iac.cud.services.application;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.items.app.AppResult;
import ru.spb.iac.cud.items.app.AppResultRequest;

@WebService(targetNamespace = ApplicationResultServiceImpl.NS)
public interface ApplicationResultService {

	public static final String NS = ApplicationResultServiceImpl.NS;

	@WebMethod
	@WebResult(targetNamespace = NS)
	public List<AppResult> result(
			@WebParam(name = "appResultRequestList", targetNamespace = NS) List<AppResultRequest> appResultRequestList)
			throws GeneralFailure;

}
