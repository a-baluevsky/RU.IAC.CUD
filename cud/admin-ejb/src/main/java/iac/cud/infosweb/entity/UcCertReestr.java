package iac.cud.infosweb.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the UC_CERT_REESTR database table.
 * 
 */
@Entity
@Table(name="UC_CERT_REESTR")
 public class UcCertReestr implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="UC_CERT_REESTR_IDSRV_GENERATOR", sequenceName="UC_CERT_REESTR_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="UC_CERT_REESTR_IDSRV_GENERATOR")
	@Column(name="ID_SRV")
	private Long idSrv;

	@Column(name="CERT_DATE")
	private String certDate;

	@Column(name="CERT_NUM")
	private String certNum;

    @Lob()
    @Basic(fetch = FetchType.LAZY)
	@Column(name="CERT_VALUE_BIN")
	private byte[] certValue;

    @Temporal( TemporalType.TIMESTAMP)
	private Date created;

	private Long creator;

	@Column(name="ORG_NAME")
	private String orgName;

	@Column(name="USER_EMAIL")
	private String userEmail;

	@Column(name="USER_FIO")
	private String userFio;

	@Column(name="USER_POSITION")
	private String userPosition;

	@Column(name="X_TYPE")
	private String xType;

    public UcCertReestr() {
    }

	public Long getIdSrv() {
		return this.idSrv;
	}

	public void setIdSrv(Long idSrv) {
		this.idSrv = idSrv;
	}

	public String getCertDate() {
		return this.certDate;
	}

	public void setCertDate(String certDate) {
		this.certDate = certDate;
	}

	public String getCertNum() {
		return this.certNum;
	}

	public void setCertNum(String certNum) {
		this.certNum = certNum;
	}

	public byte[] getCertValue() {
		return this.certValue;
	}

	public void setCertValue(byte[] certValue) {
		this.certValue = certValue;
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

	public String getOrgName() {
		return this.orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getUserEmail() {
		return this.userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserFio() {
		return this.userFio;
	}

	public void setUserFio(String userFio) {
		this.userFio = userFio;
	}

	public String getUserPosition() {
		return this.userPosition;
	}

	public void setUserPosition(String userPosition) {
		this.userPosition = userPosition;
	}

	public String getXType() {
		return this.xType;
	}

	public void setXType(String xType) {
		this.xType = xType;
	}

}