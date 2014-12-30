package iac.cud.infosweb.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the REPORTS_BSS_T database table.
 * 
 */
@Entity
@Table(name="REPORTS_BSS_T")
 public class ReportsBssT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID_SRV")
	private Long idSrv;

	@Temporal(TemporalType.DATE)
	private Date created;

	private Long creator;

	@Column(name="REPORT_CODE")
	private String reportCode;

	@Column(name="REPORT_NAME")
	private String reportName;

	@Column(name="ORDER_NUM")
	private Long orderNum;
	
	@Column(name="DATE_REQUIRED")
	private Long dateRequired;
		
	@Column(name="REPORT_PATH")
	private String reportPath;
	
	public ReportsBssT() {
	}

	public Long getIdSrv() {
		return this.idSrv;
	}

	public void setIdSrv(Long idSrv) {
		this.idSrv = idSrv;
	}

	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Long getCreator() {
		return this.creator;
	}

	public void setCreator(Long creator) {
		this.creator = creator;
	}

	public String getReportCode() {
		return this.reportCode;
	}

	public void setReportCode(String reportCode) {
		this.reportCode = reportCode;
	}

	public String getReportName() {
		return this.reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public Long getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(Long orderNum) {
		this.orderNum = orderNum;
	}

	public Long getDateRequired() {
		return dateRequired;
	}

	public void setDateRequired(Long dateRequired) {
		this.dateRequired = dateRequired;
	}

	public String getReportPath() {
		return reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

}