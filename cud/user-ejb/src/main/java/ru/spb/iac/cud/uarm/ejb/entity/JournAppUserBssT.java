package ru.spb.iac.cud.uarm.ejb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the JOURN_APP_USER_BSS_T database table.
 * 
 */
@Entity
@Table(name="JOURN_APP_USER_BSS_T")
 public class JournAppUserBssT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="JOURN_APP_USER_BSS_T_IDSRV_GENERATOR", sequenceName="JOURN_APP_USER_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="JOURN_APP_USER_BSS_T_IDSRV_GENERATOR")
	@Column(name="ID_SRV")
	private Long idSrv;

	@Column(name="EMAIL_USER")
	private String emailUser;

	
	@Column(name="CERTIFICATE_USER")
	private String certificateUser;

	@Column(name="COMMENT_")
	private String comment;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	
	@Column(name="NAME_DEPARTAMENT")
	private String nameDepartament;

	@Column(name="NAME_ORG")
	private String nameOrg;

	
	@Column(name="PATRONYMIC_USER")
	private String patronymicUser;

	@Column(name="NAME_USER")
	private String nameUser;

	
	@Column(name="PHONE_USER")
	private String phoneUser;

	@Column(name="POSITION_USER")
	private String positionUser;

	@Column(name="REJECT_REASON")
	private String rejectReason;

	

	@Column(name="SIGN_ORG")
	private String signOrg;

	@Column(name="SIGN_USER")
	private String signUser;

	private String secret;
	
	private Long status;

	@Column(name="SURNAME_USER")
	private String surnameUser;

	//bi-directional many-to-one association to AcUsersKnlT
	@ManyToOne
	@JoinColumn(name="UP_USER_APP")
	private AcUsersKnlT acUsersKnlT1;

	//bi-directional many-to-one association to AcUsersKnlT
	@ManyToOne
	@JoinColumn(name="UP_USER")
	private AcUsersKnlT acUsersKnlT2;

	//bi-directional many-to-one association to AcUsersKnlT
	@ManyToOne
	@JoinColumn(name="UP_USER_EXEC")
	private AcUsersKnlT acUsersKnlT3;

	public JournAppUserBssT() {
	}

	public Long getIdSrv() {
		return this.idSrv;
	}

	public void setIdSrv(Long idSrv) {
		this.idSrv = idSrv;
	}

	public String getCertificateUser() {
		return this.certificateUser;
	}

	public void setCertificateUser(String certificateUser) {
		this.certificateUser = certificateUser;
	}

	public Date getCreated() {
		return this.created;
	}
    public void setCreated(Date created) {
		this.created = created;
	}
	
	public String getComment() {
		return this.comment;
	}
    public void setComment(String comment) {
		this.comment = comment;
	}

	

	public String getNameDepartament() {
		return this.nameDepartament;
	}
	public void setNameDepartament(String nameDepartament) {
		this.nameDepartament = nameDepartament;
	}

	
	public String getEmailUser() {
		return this.emailUser;
	}
	public void setEmailUser(String emailUser) {
		this.emailUser = emailUser;
	}
	public String getNameOrg() {
		return this.nameOrg;
	}

	public void setNameOrg(String nameOrg) {
		this.nameOrg = nameOrg;
	}

	

	public String getPatronymicUser() {
		return this.patronymicUser;
	}

	public void setPatronymicUser(String patronymicUser) {
		this.patronymicUser = patronymicUser;
	}

	public String getPhoneUser() {
		return this.phoneUser;
	}

	public void setPhoneUser(String phoneUser) {
		this.phoneUser = phoneUser;
	}

	public String getPositionUser() {
		return this.positionUser;
	}

	public void setPositionUser(String positionUser) {
		this.positionUser = positionUser;
	}

	public String getNameUser() {
		return this.nameUser;
	}
	public void setNameUser(String nameUser) {
		this.nameUser = nameUser;
	}
	
	public String getRejectReason() {
		return this.rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}



	public String getSignOrg() {
		return this.signOrg;
	}

	public void setSignOrg(String signOrg) {
		this.signOrg = signOrg;
	}

	public String getSignUser() {
		return this.signUser;
	}

	public void setSignUser(String signUser) {
		this.signUser = signUser;
	}

	public Long getStatus() {
		return this.status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public String getSecret() {
		return this.secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	public String getSurnameUser() {
		return this.surnameUser;
	}

	public void setSurnameUser(String surnameUser) {
		this.surnameUser = surnameUser;
	}

	

	public AcUsersKnlT getAcUsersKnlT2() {
		return this.acUsersKnlT2;
	}

	public void setAcUsersKnlT2(AcUsersKnlT acUsersKnlT2) {
		this.acUsersKnlT2 = acUsersKnlT2;
	}

	public AcUsersKnlT getAcUsersKnlT3() {
		return this.acUsersKnlT3;
	}

	public void setAcUsersKnlT3(AcUsersKnlT acUsersKnlT3) {
		this.acUsersKnlT3 = acUsersKnlT3;
	}
	
	public AcUsersKnlT getAcUsersKnlT1() {
		return this.acUsersKnlT1;
	}
	public void setAcUsersKnlT1(AcUsersKnlT acUsersKnlT1) {
		this.acUsersKnlT1 = acUsersKnlT1;
	}

}