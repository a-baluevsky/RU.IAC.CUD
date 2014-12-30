package iac.cud.infosweb.dataitems;

 public class AppSystemModifyItem extends AppItem {

	private String fullName;
	private String shortName;
	private String description;

	private Long idArm;
	private String codeArm;
	private String fullNameArm;
	private String descriptionArm;

	public AppSystemModifyItem() {
	}

	public AppSystemModifyItem(Long idApp, String created, int status,
			String fullName, String shortName, String description,
			String orgName, String usrFio, String rejectReason, Long idArm,
			String codeArm, String fullNameArm, String descriptionArm,
			String comment) {
		super(idApp, created, status, orgName, usrFio, rejectReason, comment);
		this.fullName = fullName;
		this.shortName = shortName;
		this.description = description;

		this.idArm = idArm;
		this.codeArm = codeArm;
		this.fullNameArm = fullNameArm;
		this.descriptionArm = descriptionArm;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
}
