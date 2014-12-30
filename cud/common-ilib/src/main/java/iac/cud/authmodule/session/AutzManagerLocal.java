package iac.cud.authmodule.session;

import iac.cud.authmodule.dataitem.AuthItem;
import iac.cud.authmodule.dataitem.PageItem;

import java.util.List;
import javax.ejb.Local;

/**
 * Предоставление информации о наличии прав доступа, jndi name:
 * authModule.AuthManager.local
 * 
 * @author bubnov
 */
@Local
public interface AutzManagerLocal {

	public AuthItem getAccessComplete(Long appCode, List<String> roles)
			throws Exception;

	/**
	 * Аутентификация и авторизация пользователя
	 * 
	 * @param appCode
	 *            ид приложения
	 * @param login
	 *            логин
	 * @param password
	 *            пароль
	 * @return AuthItem, если упешно, иначе null
	 * @throws Exception
	 */
	public PageItem getAccessPage(Long appCode, List<String> roles,
			String pageCode) throws Exception;

	public boolean getAccessPage(Long appCode, List<String> roles,
			String pageCode, String permCode) throws Exception;
}
