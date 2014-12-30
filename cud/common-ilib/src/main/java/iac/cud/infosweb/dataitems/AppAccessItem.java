package iac.cud.infosweb.dataitems;

 public class AppAccessItem extends AppItem {

	private Long idArm;
	private String codeArm;
	private String fullNameArm;
	private String descriptionArm;

	private String nameOrg;

	
	private String nameDep;

	
	private Long idUser;
	private String loginUser;
	private String fioUser;
	private String positionUser;

	private String modeExecValue;
	
	private int modeExec;

	

	public AppAccessItem() {
	}

	public AppAccessItem(Long idApp, String created, int status,
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

		
		this.modeExec = modeExec;
		
		this.nameOrg = nameOrg;
		this.idUser = idUser;
		this.loginUser = loginUser;
		this.fioUser = fioUser;
		this.positionUser = positionUser;
		this.nameDep = nameDep;

	}

	public Long getIdArm() {
		return idArm;
	}

	public void setIdArm(Long idArm) {
		this.idArm = idArm;
	}

	

	public String getFullNameArm() {
		return fullNameArm;
	}

	public void setFullNameArm(String fullNameArm) {
		this.fullNameArm = fullNameArm;
	}

	public String getCodeArm() {
		return codeArm;
	}
	public void setCodeArm(String codeArm) {
		this.codeArm = codeArm;
	}
	
	public String getDescriptionArm() {
		return descriptionArm;
	}

	public void setDescriptionArm(String descriptionArm) {
		this.descriptionArm = descriptionArm;
	}

	

	public Long getIdUser() {
		return idUser;
	}
    public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public String getNameOrg() {
		return nameOrg;
	}
    public void setNameOrg(String nameOrg) {
		this.nameOrg = nameOrg;
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

	public String getLoginUser() {
		return loginUser;
	}
    public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}
    
	public void setPositionUser(String positionUser) {
		this.positionUser = positionUser;
	}

	

	public int getModeExec() {
		return modeExec;
	}

	public void setModeExec(int modeExec) {
		this.modeExec = modeExec;
	}

	public void setNameDep(String nameDep) {
		this.nameDep = nameDep;
	}
	public String getNameDep() {
		return nameDep;
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
