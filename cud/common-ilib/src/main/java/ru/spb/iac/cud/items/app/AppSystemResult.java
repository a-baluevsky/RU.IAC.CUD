package ru.spb.iac.cud.items.app;

 public class AppSystemResult extends AppResult {

	private String codeArm;
	private String fullNameArm;
	private String descriptionArm;

	public AppSystemResult() {
	}

	public AppSystemResult(String status, String codeArm, String fullNameArm,
			String descriptionArm, String rejectReason) {

		super(status);
		this.codeArm = codeArm;
		this.fullNameArm = fullNameArm;
		this.descriptionArm = descriptionArm;
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
