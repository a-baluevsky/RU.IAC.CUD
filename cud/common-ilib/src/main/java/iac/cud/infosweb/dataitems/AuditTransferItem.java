package iac.cud.infosweb.dataitems;

import java.io.Serializable;
import java.util.List;

 public class AuditTransferItem<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private int status;

	private Long count;

	private List<T> list;

	public AuditTransferItem() {
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Long getCount() {
		return this.count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public List<T> getList() {
		return this.list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

}
