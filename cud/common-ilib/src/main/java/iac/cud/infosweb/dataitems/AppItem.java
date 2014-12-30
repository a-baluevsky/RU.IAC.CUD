package iac.cud.infosweb.dataitems;

 public class AppItem extends BaseItem {

	private Long idApp;
	private String created;
	private int status;
	private String statusValue;
	private String orgName;
	private String usrFio;
	private String rejectReason;
	private String comment;
	private String commentApp;

	public AppItem() {
	}

	public AppItem(Long idApp, String created, int status, String orgName,
			String usrFio, String rejectReason, String comment) {
		this.idApp = idApp;
		this.created = created;
		this.status = status;
		this.orgName = orgName;
		this.usrFio = usrFio;
		this.rejectReason = rejectReason;
		this.comment = comment;
	}

	public AppItem(Long idApp, String created, int status, String orgName,
			String usrFio, String rejectReason, String comment,
			String commentApp) {
		this.idApp = idApp;
		this.created = created;
		this.status = status;
		this.orgName = orgName;
		this.usrFio = usrFio;
		this.rejectReason = rejectReason;
		this.comment = comment;
		this.commentApp = commentApp;
	}

	public Long getBaseId() {
		return this.idApp;
	}

	public Long getIdApp() {
		return idApp;
	}

	public void setIdApp(Long idApp) {
		this.idApp = idApp;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusValue() {
		if (this.status == 0) {
			this.statusValue = "В обработке";
		} else if (this.status == 1) {
			this.statusValue = "Выполнена";
		} else if (this.status == 2) {
			this.statusValue = "Отклонена";
		}
		return this.statusValue;
	}

	public void setStatusValue(String statusValue) {
		this.statusValue = statusValue;
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

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCommentApp() {
		return commentApp;
	}

	public void setCommentApp(String commentApp) {
		this.commentApp = commentApp;
	}
}
