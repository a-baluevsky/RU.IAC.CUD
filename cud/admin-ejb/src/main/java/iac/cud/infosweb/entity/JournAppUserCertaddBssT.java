package iac.cud.infosweb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the JOURN_APP_USER_CERTADD_BSS_T database table.
 * 
 */
@Entity
@Table(name="JOURN_APP_USER_CERTADD_BSS_T")
 public class JournAppUserCertaddBssT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="JOURN_APP_USER_CERTADD_BSS_T_IDSRV_GENERATOR", sequenceName="JOURN_APP_USER_CERTADD_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="JOURN_APP_USER_CERTADD_BSS_T_IDSRV_GENERATOR")
	@Column(name="ID_SRV")
	private Long idSrv;

	@Lob
	@Column(name="CERT_VALUE")
	private byte[] certValue;

	@Column(name="COMMENT_")
	private String comment;

	@Temporal(TemporalType.DATE)
	private Date created;

	@Column(name="MODE_EXEC")
	private Long modeExec;

	@Column(name="COMMENT_APP")
	private String commentApp;

	
	@Column(name="REJECT_REASON")
	private String rejectReason;

	private String secret;

	private Long status;

	@Column(name="UP_USER")
	private java.math.BigDecimal upUser;

	@Column(name="UP_USER_EXEC")
	private java.math.BigDecimal upUserExec;

	@Column(name="UP_USER_APP")
	private java.math.BigDecimal upUserApp;

	
	public JournAppUserCertaddBssT() {
	}

	public Long getIdSrv() {
		return this.idSrv;
	}

	public void setIdSrv(Long idSrv) {
		this.idSrv = idSrv;
	}

	public byte[] getCertValue() {
		return this.certValue;
	}

	public void setCertValue(byte[] certValue) {
		this.certValue = certValue;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCommentApp() {
		return this.commentApp;
	}

	public void setCommentApp(String commentApp) {
		this.commentApp = commentApp;
	}

	

	public Long getModeExec() {
		return this.modeExec;
	}

	public void setModeExec(Long modeExec) {
		this.modeExec = modeExec;
	}

	public Date getCreated() {
		return this.created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	
	public String getRejectReason() {
		return this.rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getSecret() {
		return this.secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public java.math.BigDecimal getUpUserExec() {
		return this.upUserExec;
	}
   public void setUpUserExec(java.math.BigDecimal upUserExec) {
		this.upUserExec = upUserExec;
	}
   
	public java.math.BigDecimal getUpUser() {
		return this.upUser;
	}

	public void setUpUser(java.math.BigDecimal upUser) {
		this.upUser = upUser;
	}

	public java.math.BigDecimal getUpUserApp() {
		return this.upUserApp;
	}

	public Long getStatus() {
		return this.status;
	}
	public void setStatus(Long status) {
		this.status = status;
	}

	
	public void setUpUserApp(java.math.BigDecimal upUserApp) {
		this.upUserApp = upUserApp;
	}

	

}