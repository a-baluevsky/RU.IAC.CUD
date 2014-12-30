package iac.cud.authmodule.dataitem;

import java.util.HashMap; import java.util.Map;
import java.io.Serializable;

/**
 * Данные по наличию прав доступа к ресурсам приложения
 * 
 * @author bubnov
 */
 public class AuthItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long idUser;
	private Map<String, PageItem> pageList = new HashMap<String, PageItem>();

	/**
	 * ид пользователя
	 * 
	 * @return ид пользователя
	 */
	public Long getIdUser() {
		return this.idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	/**
	 * карта с отображением внутреннего ид ресурса приложения на PageItem
	 * 
	 * @return карта доступных пользователю ресурсов, если есть, иначе - empty
	 */
	public Map<String, PageItem> getPageList() {
		return this.pageList;
	}

	public void setPageList(Map<String, PageItem> pageList) {
		this.pageList = pageList;
	}
}
