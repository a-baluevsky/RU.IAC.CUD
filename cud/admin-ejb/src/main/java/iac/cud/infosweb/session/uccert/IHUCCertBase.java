package iac.cud.infosweb.session.uccert;

import iac.cud.infosweb.dataitems.BaseParamItem;
import iac.cud.infosweb.local.service.ServiceReestrAction;

 public class IHUCCertBase {

	public BaseParamItem run(BaseParamItem paramMap) throws Exception {

		BaseParamItem jpi = new BaseParamItem();

		String gactiontype = (String) paramMap.get("gactiontype");

		if (gactiontype.equals(ServiceReestrAction.PROCESS_START.name())) {
			jpi = process_start(paramMap);
		} else if (gactiontype.equals(ServiceReestrAction.PROCESS_STOP.name())) {
			jpi = process_stop(paramMap);
		} else if (gactiontype.equals(ServiceReestrAction.TASK_RUN.name())) {
			jpi = task_run(paramMap);
		}
		return jpi;
	}

	public BaseParamItem process_start(BaseParamItem paramMap) throws Exception {

		BaseParamItem jpi = new BaseParamItem();

	
		return jpi;
	}

	public BaseParamItem process_stop(BaseParamItem paramMap) throws Exception {

		BaseParamItem jpi = new BaseParamItem();

		return jpi;
	}

	public BaseParamItem task_run(BaseParamItem paramMap) throws Exception {

		BaseParamItem jpi = new BaseParamItem();

		return jpi;
	}

}
