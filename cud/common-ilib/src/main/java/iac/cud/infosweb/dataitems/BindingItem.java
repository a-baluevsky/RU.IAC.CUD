package iac.cud.infosweb.dataitems;

 public class BindingItem extends BaseItem {

	private Long idSrv;

	private UserItem userItem;

	private String created;

	private int bindType;

	public BindingItem() {
	}

	public BindingItem(Long idSrv, UserItem userItem, String created,
			int bindType) {
		this.idSrv = idSrv;
		this.userItem = userItem;
		this.created = created;
		this.bindType = bindType;
	}

	public Long getBaseId() {
		return this.idSrv;
	}

	public UserItem getUserItem() {
		return userItem;
	}

	public void setUserItem(UserItem userItem) {
		this.userItem = userItem;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public Long getIdSrv() {
		return idSrv;
	}

	public void setIdSrv(Long idSrv) {
		this.idSrv = idSrv;
	}

	public int getBindType() {
		return bindType;
	}

	public void setBindType(int bindType) {
		this.bindType = bindType;
	}

}
