package iac.cud.authmodule.session;

import iac.cud.authmodule.dataitem.AuthItem;
import iac.cud.authmodule.dataitem.PageItem;

import java.util.List;
import javax.ejb.Remote;

@Remote
public interface AutzManagerRemote {

	public AuthItem getAccessComplete(Long appCode, List<String> roles)
			throws Exception;

	public PageItem getAccessPage(Long appCode, List<String> roles,
			String pageCode) throws Exception;

	public boolean getAccessPage(Long appCode, List<String> roles,
			String pageCode, String permCode) throws Exception;
}
