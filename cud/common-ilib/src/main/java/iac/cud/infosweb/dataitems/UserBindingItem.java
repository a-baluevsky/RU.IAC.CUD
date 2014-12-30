package iac.cud.infosweb.dataitems;

import java.util.List;

 public class UserBindingItem extends UserItem {

	private static final long serialVersionUID = 1L;

	private List<BaseItem> bindingList;

	// есть ли привязки полученные на автомате у пользователя
	private int binFlag;

	public UserBindingItem() {
	}

	public UserBindingItem(Long idUser, String login, String cert,
			String usrCode, String fio, String phone, String email,
			String position, String department, String orgCode, String orgName,
			String orgAdr, String orgTel, String start, String finish,
			Long status, String crtDate, String crtUserLogin, String updDate,
			String updUserLogin, String depCode, String orgIogvStatus,
			String usrIogvStatus, String depIogvStatus, Long iogvBindType,
			int binFlag) {

		super(idUser, login, cert, usrCode, fio, phone, email, position,
				department, orgCode, orgName, orgAdr, orgTel, start, finish,
				status, crtDate, crtUserLogin, updDate, updUserLogin, depCode,
				orgIogvStatus, usrIogvStatus, depIogvStatus, iogvBindType);

		this.binFlag = binFlag;
	}

	public List<BaseItem> getBindingList() {
		return bindingList;
	}

	public void setBindingList(List<BaseItem> bindingList) {
		this.bindingList = bindingList;
	}

	public int getBinFlag() {
		return binFlag;
	}

	public void setBinFlag(int binFlag) {
		this.binFlag = binFlag;
	}

}
