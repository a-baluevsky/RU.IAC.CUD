package iac.cud.infosweb.dataitems;

import java.io.Serializable;

 public class BaseItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long baseId;
	private boolean selected;

	public Long getBaseId() {
		return this.baseId;
	}

	public void setBaseId(Long baseId) {
		this.baseId = baseId;
	}

	public boolean getSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
