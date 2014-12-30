package iac.cud.infosweb.dataitems;

import java.util.List;

 public class NavigItem {

	private String idRes;
	private String pageCode;
	private String pageName;
	private NavigItem parent;
	private List<NavigItem> children;

	public NavigItem() {
	}

	public NavigItem(String idRes, String pageCode, String pageName) {
		this.idRes = idRes;
		this.pageCode = pageCode;
		this.pageName = pageName;
	}

	public String getIdRes() {
		return idRes;
	}

	public void setIdRes(String idRes) {
		this.idRes = idRes;
	}

	public String getPageCode() {
		return pageCode;
	}

	public void setPageCode(String pageCode) {
		this.pageCode = pageCode;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public NavigItem getParent() {
		return parent;
	}

	public void setParent(NavigItem parent) {
		this.parent = parent;
	}

	public List<NavigItem> getChildren() {
		return children;
	}

	public void setChildren(List<NavigItem> children) {
		this.children = children;
	}
}
