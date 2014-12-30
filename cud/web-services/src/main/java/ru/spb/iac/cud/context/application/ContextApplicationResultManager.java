package ru.spb.iac.cud.context.application;

import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.core.app.ApplicationResultManagerLocal;
import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.items.app.AppResult;
import ru.spb.iac.cud.items.app.AppResultRequest;

 public class ContextApplicationResultManager {

	static Context ctx;
	ApplicationResultManagerLocal aml = null;

	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ContextApplicationResultManager.class);

	static {
		try {
			ctx = new InitialContext();

		} catch (Exception e) {
			LOGGER.error("error",e);
		}
	}

	public ContextApplicationResultManager() {
		try {
			this.aml = (ApplicationResultManagerLocal) ctx
					.lookup("java:global/AuthServices/ApplicationResultManager!ru.spb.iac.cud.core.app.ApplicationResultManagerLocal");

		} catch (Exception e) {
			LOGGER.error("ContextApplicationResultManager:error:", e);
		}
	}

	public List<AppResult> result(List<AppResultRequest> request_list,
			Long idUserAuth, String IPAddress) throws GeneralFailure {

		if (request_list == null || request_list.isEmpty()) {
			throw new GeneralFailure("Некорректные данные в заявке!");
		}

		return aml.result(request_list, idUserAuth, IPAddress);
	}

	

}
