package iac.cud.infosweb.dataitems;

import java.io.Serializable;
import java.util.Date;

 public class AuditReportsItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date currdate;

	private String ipAddress;

	private String osUser;

	private Long qdelete;

	private Long qinsert;

	private Long qselect;

	private Long qupdate;

	private String tableName;

	public AuditReportsItem() {
	}

	public Date getCurrdate() {
		return this.currdate;
	}

	public void setCurrdate(Date currdate) {
		this.currdate = currdate;
	}

	public String getIpAddress() {
		return this.ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getOsUser() {
		return this.osUser;
	}

	public void setOsUser(String osUser) {
		this.osUser = osUser;
	}

	public Long getQdelete() {
		return this.qdelete;
	}

	public void setQdelete(Long qdelete) {
		this.qdelete = qdelete;
	}

	public Long getQinsert() {
		return this.qinsert;
	}

	public void setQinsert(Long qinsert) {
		this.qinsert = qinsert;
	}

	public Long getQselect() {
		return this.qselect;
	}

	public void setQselect(Long qselect) {
		this.qselect = qselect;
	}

	public Long getQupdate() {
		return this.qupdate;
	}

	public void setQupdate(Long qupdate) {
		this.qupdate = qupdate;
	}

	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}
