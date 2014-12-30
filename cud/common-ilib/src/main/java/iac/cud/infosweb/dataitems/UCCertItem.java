package iac.cud.infosweb.dataitems;

import iac.cud.infosweb.dataitems.BaseItem;

import java.io.Serializable;

 public class UCCertItem extends BaseItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long idSrv;

	private String orgName;

	private String usrFio;

	private String usrPosition;

	private String userEmail;

	private String certNum;

	private String certDate;

	private String certUsed;

	public UCCertItem() {
	}

	public UCCertItem(Long idSrv, String orgName, String usrFio,
			String usrPosition, String userEmail, String certNum,
			String certDate, String certUsed) {
		this.idSrv = idSrv;
		this.orgName = orgName;
		this.usrFio = usrFio;
		this.usrPosition = usrPosition;
		this.userEmail = userEmail;
		this.certNum = certNum;
		this.certDate = certDate;
		this.setCertUsed(certUsed);
	}

	public Long getBaseId() {
		return this.idSrv;
	}

	public Long getIdSrv() {
		return this.idSrv;
	}

	public void setIdSrv(Long idSrv) {
		this.idSrv = idSrv;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getUsrFio() {
		return usrFio;
	}

	public void setUsrFio(String usrFio) {
		this.usrFio = usrFio;
	}

	public String getUsrPosition() {
		return usrPosition;
	}

	public void setUsrPosition(String usrPosition) {
		this.usrPosition = usrPosition;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getCertNum() {
		return certNum;
	}

	public void setCertNum(String certNum) {
		this.certNum = certNum;
	}

	public String getCertDate() {
		return certDate;
	}

	public void setCertDate(String certDate) {
		this.certDate = certDate;
	}

	public String getCertUsed() {
		return certUsed;
	}

	public void setCertUsed(String certUsed) {
		this.certUsed = certUsed;
	}

}