package ru.spb.iac.cud.uarm.ejb.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the AC_USERS_CERT_BSS_T database table.
 * 
 */
@Entity
@Table(name="AC_USERS_CERT_BSS_T")
 public class AcUsersCertBssT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="AC_USERS_CERT_BSS_T_IDSRV_GENERATOR", sequenceName="AC_USERS_CERT_BSS_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="AC_USERS_CERT_BSS_T_IDSRV_GENERATOR")
	@Column(name="ID_SRV")
	private Long idSrv;

	@Column(name="CERT_DATE")
	private String certDate;

	@Column(name="CERT_NUM")
	private String certNum;

    @Lob()
	@Column(name="CERT_VALUE")
	private byte[] certValue;

    @Temporal( TemporalType.DATE)
	private Date created;

	private Long creator;

	@Column(name="ORG_NAME")
	private String orgName;

	@Column(name="USER_FIO")
	private String userFio;

	@Column(name="DEP_NAME")
	private String depName;
	
	@ManyToOne
	@JoinColumn(name="UP_USER", insertable=false, updatable=false)
	private AcUsersKnlT upUser;

	@Column(name="UP_USER")
	private Long upUserRaw;
	 
	
    public AcUsersCertBssT() {
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

	public AcUsersKnlT getUpUser() {
		return this.upUser;
	}

	public void setUpUser(AcUsersKnlT upUser) {
		this.upUser = upUser;
	}

	public Long getUpUserRaw() {
		return upUserRaw;
	}

	public void setUpUserRaw(Long upUserRaw) {
		this.upUserRaw = upUserRaw;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getUserFio() {
		return userFio;
	}

	public void setUserFio(String userFio) {
		this.userFio = userFio;
	}

	public String getDepName() {
		return depName;
	}

	public void setDepName(String depName) {
		this.depName = depName;
	}


}