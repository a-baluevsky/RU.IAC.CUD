package iac.cud.infosweb.dataitems;

 public class AppUserDepModifyItem extends AppItem {

	// полученные
	private String departament;

	// исходные
	private String nameOrg;
	private Long idUser;
	private String loginUser;
	private String fioUser;
	private String positionUser;
	private String nameDep;
	private String certUser;
	private String iogvCodeUser;
	private String emailUser;
	private String phoneUser;
	private String iogvCodeOrg;

	public AppUserDepModifyItem() {
	}

	public AppUserDepModifyItem(Long idApp, String created, int status,
			String orgName, String usrFio, String rejectReason, String comment,

			String departament,

			String nameOrg, Long idUser, String loginUser, String fioUser,
			String positionUser, String nameDep, String certUser,
			String iogvCodeUser, String emailUser, String phoneUser,
			String iogvCodeOrg) {
		super(idApp, created, status, orgName, usrFio, rejectReason, comment);

		this.setDepartament(departament);

		this.nameOrg = nameOrg;
		this.idUser = idUser;
		this.loginUser = loginUser;
		this.fioUser = fioUser;
		this.positionUser = positionUser;
		this.nameDep = nameDep;
		this.certUser = certUser;
		this.iogvCodeUser = iogvCodeUser;
		this.emailUser = emailUser;
		this.phoneUser = phoneUser;
		this.iogvCodeOrg = iogvCodeOrg;
	}

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public String getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}

	public String getFioUser() {
		return fioUser;
	}

	public void setFioUser(String fioUser) {
		this.fioUser = fioUser;
	}

	public String getPositionUser() {
		return positionUser;
	}

	public void setPositionUser(String positionUser) {
		this.positionUser = positionUser;
	}

	public String getNameDep() {
		return nameDep;
	}

	public void setNameDep(String nameDep) {
		this.nameDep = nameDep;
	}

	public String getNameOrg() {
		return nameOrg;
	}

	public void setNameOrg(String nameOrg) {
		this.nameOrg = nameOrg;
	}

	public String getCertUser() {
		return certUser;
	}

	public void setCertUser(String certUser) {
		this.certUser = certUser;
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

	public String getIogvCodeUser() {
		return iogvCodeUser;
	}

	public void setIogvCodeUser(String iogvCodeUser) {
		this.iogvCodeUser = iogvCodeUser;
	}

	public String getDepartament() {
		return departament;
	}

	public void setDepartament(String departament) {
		this.departament = departament;
	}

	public String getIogvCodeOrg() {
		return iogvCodeOrg;
	}

	public void setIogvCodeOrg(String iogvCodeOrg) {
		this.iogvCodeOrg = iogvCodeOrg;
	}

}
