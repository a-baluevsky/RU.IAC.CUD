package ru.spb.iac.cud.core.app;

import java.util.List;
import javax.ejb.Local;

import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.items.app.AppResult;
import ru.spb.iac.cud.items.app.AppResultRequest;

/**
 * @author bubnov
 */
@Local
public interface ApplicationResultManagerLocal {

	public List<AppResult> result(List<AppResultRequest> request_list,
			Long idUserAuth, String IPAddress) throws GeneralFailure;

	public void number_secret_valid(String number, String secret, String type)
			throws GeneralFailure;

	public Long principal_exist(String principal) throws GeneralFailure;
}
