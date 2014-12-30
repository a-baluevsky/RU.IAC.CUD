package iac.cud.infosweb.local.service;

import iac.cud.infosweb.dataitems.BaseParamItem;
import javax.ejb.Local;

@Local
public interface IHLocal {
	public BaseParamItem run(BaseParamItem paramMap)throws Exception;
}
