package iac.cud.infosweb.remote.frontage;

import iac.cud.infosweb.dataitems.BaseParamItem;

import javax.ejb.Local;

@Local
public interface IRemoteFrontageLocal {

	public BaseParamItem run(BaseParamItem paramMap) throws Exception;

}
