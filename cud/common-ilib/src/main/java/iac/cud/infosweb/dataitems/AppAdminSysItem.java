package iac.cud.infosweb.dataitems;

 public class AppAdminSysItem extends AppItem {

	private Long idArm;
	private String codeArm;
	private String fullNameArm;
	private String descriptionArm;

	private String nameOrg;

	private Long idUser;
	private String loginUser;
	private String fioUser;
	private String positionUser;

	private String nameDep;

	private int modeExec;

	private String modeExecValue;

	public AppAdminSysItem() {
	}

	public AppAdminSysItem(Long idApp, String created, int status,
			String orgName, String usrFio, String rejectReason, String comment,
			Long idArm, String codeArm, String fullNameArm,
			String descriptionArm, String nameOrg, Long idUser,
			String loginUser, String fioUser, String positionUser,
			String nameDep, int modeExec) {
		super(idApp, created, status, orgName, usrFio, rejectReason, comment);

		this.idArm = idArm;
		this.codeArm = codeArm;
		this.fullNameArm = fullNameArm;
		this.descriptionArm = descriptionArm;

		this.nameOrg = nameOrg;
		this.idUser = idUser;
		this.loginUser = loginUser;
		this.fioUser = fioUser;
		this.positionUser = positionUser;
		this.nameDep = nameDep;

		this.modeExec = modeExec;
	}

	public Long getIdArm() {
		return idArm;
	}

	public void setIdArm(Long idArm) {
		this.idArm = idArm;
	}

	public String getCodeArm() {
		return codeArm;
	}

	public void setCodeArm(String codeArm) {
		this.codeArm = codeArm;
	}

	public String getFullNameArm() {
		return fullNameArm;
	}

	public void setFullNameArm(String fullNameArm) {
		this.fullNameArm = fullNameArm;
	}

	public String getDescriptionArm() {
		return descriptionArm;
	}

	public void setDescriptionArm(String descriptionArm) {
		this.descriptionArm = descriptionArm;
	}

	public String getNameOrg() {
		return nameOrg;
	}

	public void setNameOrg(String nameOrg) {
		this.nameOrg = nameOrg;
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

	public int getModeExec() {
		return modeExec;
	}

	public void setModeExec(int modeExec) {
		this.modeExec = modeExec;
	}

	public String getModeExecValue() {
		if (this.modeExec == 0) {
			this.modeExecValue = "Замена";
		} else if (this.modeExec == 1) {
			this.modeExecValue = "Добавление";
		} else if (this.modeExec == 2) {
			this.modeExecValue = "Удаление";
		}
		return modeExecValue;
	}

	public void setModeExecValue(String modeExecValue) {
		this.modeExecValue = modeExecValue;
	}

}
