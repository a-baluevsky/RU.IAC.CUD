package iac.cud.infosweb.dataitems;

import java.io.Serializable;

 public class AuditFuncItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private String user;

	private String action;

	private Long count;

	public AuditFuncItem() {
	}

	public AuditFuncItem(String user, String action, Long count) {
		this.user = user;
		this.action = action;
		this.count = count;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Long getCount() {
		return this.count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

}
