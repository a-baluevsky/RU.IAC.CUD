package ru.spb.iac.cud.items.app;

 public class AppUserResult extends AppResult {

	private String loginUser;
	private String passwordUser;

	public AppUserResult() {
	}

	public AppUserResult(String status, String loginUser, String passwordUser,
			String rejectReason) {

		super(status);
		this.loginUser = loginUser;
		this.passwordUser = passwordUser;
	}

	public String getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}

	public String getPasswordUser() {
		return passwordUser;
	}

	public void setPasswordUser(String passwordUser) {
		this.passwordUser = passwordUser;
	}

}
