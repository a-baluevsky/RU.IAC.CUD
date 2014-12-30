package iac.cud.authmodule.dataitem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Данные по наличию прав доступа к ресурсу приложения
 * 
 * @author bubnov
 */
 public class PageItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<String> permList = new ArrayList<String>();

	public PageItem() {
	}

	public PageItem(ArrayList<String> permList) {
		this.permList = permList;
	}

	/**
	 * привилегии на ресурс
	 * 
	 * @return список доступных привилегий, если есть, иначе - empty
	 */
	public List<String> getPermList() {
		return this.permList;
	}

	public void setPermList(ArrayList<String> permList) {
		this.permList = permList;
	}

}
