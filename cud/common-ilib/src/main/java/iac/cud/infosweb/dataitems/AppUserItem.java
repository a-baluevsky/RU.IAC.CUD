package iac.cud.infosweb.dataitems;

 public class AppUserItem extends AppItem {

	private String fioIogvUser;
	private String passwordTechUser;
	private String surnameUser;
	private String nameUser;
	private String patronymicUser;
	private String iogvCodeUser;
	private String positionUser;
	private String emailUser;
	private String phoneUser;
	private String certificateUser;
	private String nameDepartament;
	private String nameOrg;
	private String iogvCodeOrg;

	private Long idUser;
	private String login;

	public AppUserItem() {
	}

	public AppUserItem(Long idApp, String created, int status, String orgName,
			String usrFio, String rejectReason, String comment,

			String surnameUser, String nameUser, String patronymicUser,
			String iogvCodeUser, String positionUser, String emailUser,
			String phoneUser, String certificateUser, String nameDepartament,
			String nameOrg, String iogvCodeOrg,

			Long idUser, String login,

			String commentApp) {
		super(idApp, created, status, orgName, usrFio, rejectReason, comment,
				commentApp);
		this.surnameUser = surnameUser;
		this.nameUser = nameUser;
		this.patronymicUser = patronymicUser;
		this.iogvCodeUser = iogvCodeUser;
		this.positionUser = positionUser;
		this.emailUser = emailUser;
		this.phoneUser = phoneUser;
		this.certificateUser = certificateUser;
		this.nameDepartament = nameDepartament;
		this.nameOrg = nameOrg;
		this.iogvCodeOrg = iogvCodeOrg;

		this.idUser = idUser;
		this.login = login;
	}

	public String getSurnameUser() {
		return surnameUser;
	}

	public void setSurnameUser(String surnameUser) {
		this.surnameUser = surnameUser;
	}

	public String getNameUser() {
		return nameUser;
	}

	public void setNameUser(String nameUser) {
		this.nameUser = nameUser;
	}

	public String getPatronymicUser() {
		return patronymicUser;
	}

	public void setPatronymicUser(String patronymicUser) {
		this.patronymicUser = patronymicUser;
	}

	public String getIogvCodeUser() {
		return iogvCodeUser;
	}

	public void setIogvCodeUser(String iogvCodeUser) {
		this.iogvCodeUser = iogvCodeUser;
	}

	public String getPositionUser() {
		return positionUser;
	}

	public void setPositionUser(String positionUser) {
		this.positionUser = positionUser;
	}

	public String getEmailUser() {
		return emailUser;
	}

	public void setEmailUser(String emailUser) {
		this.emailUser = emailUser;
	}

	public String getPhoneUser() {
		return phoneUser;
	}

	public void setPhoneUser(String phoneUser) {
		this.phoneUser = phoneUser;
	}

	public String getCertificateUser() {
		return certificateUser;
	}

	public void setCertificateUser(String certificateUser) {
		this.certificateUser = certificateUser;
	}

	public String getNameDepartament() {
		return nameDepartament;
	}

	public void setNameDepartament(String nameDepartament) {
		this.nameDepartament = nameDepartament;
	}

	public String getNameOrg() {
		return nameOrg;
	}

	public void setNameOrg(String nameOrg) {
		this.nameOrg = nameOrg;
	}

	public String getIogvCodeOrg() {
		return iogvCodeOrg;
	}

	public void setIogvCodeOrg(String iogvCodeOrg) {
		this.iogvCodeOrg = iogvCodeOrg;
	}

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getFioIogvUser() {
		return fioIogvUser;
	}

	public void setFioIogvUser(String fioIogvUser) {
		this.fioIogvUser = fioIogvUser;
	}

	public String getPasswordTechUser() {
		return passwordTechUser;
	}

	public void setPasswordTechUser(String passwordTechUser) {
		this.passwordTechUser = passwordTechUser;
	}

}
