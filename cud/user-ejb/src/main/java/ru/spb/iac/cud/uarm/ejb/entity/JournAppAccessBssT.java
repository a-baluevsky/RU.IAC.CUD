package ru.spb.iac.cud.uarm.ejb.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the JOURN_APP_ACCESS_BSS_T database table.
 * 
 */
@Entity
@Table(name="JOURN_APP_ACCESS_BSS_T")
 public class JournAppAccessBssT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="JOURN_APP_ACCESS_BSS_T_IDSRV_GENERATOR", sequenceName="JOURN_APP_ACCESS_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="JOURN_APP_ACCESS_BSS_T_IDSRV_GENERATOR")
	@Column(name="ID_SRV")
	private Long idSrv;

	@Column(name="CODE_SYSTEM")
	private String codeSystem;

	
	@Column(name="LOGIN_USER")
	private String loginUser;

	@Column(name="MODE_EXEC")
	private Long modeExec;

	@Column(name="REJECT_REASON")
	private String rejectReason;

	@Column(name="COMMENT_")
	private String comment;

	@Temporal(TemporalType.DATE)
	private Date created;

	
	private String secret;

	private Long status;

	@Column(name="UP_IS_APP")
	private Long acIsBssTLong;
	
	
	@ManyToOne
	@JoinColumn(name="UP_IS_APP", insertable=false, updatable=false)
	private AcIsBssT acIsBssT;

	
	@ManyToOne
	@JoinColumn(name="UP_USER_EXEC")
	private AcUsersKnlT acUsersKnlT1;

	//заявитель
	@ManyToOne
	@JoinColumn(name="UP_USER", insertable=false, updatable=false)
	private AcUsersKnlT acUsersKnlT2;

	@Column(name="UP_USER_APP")
	private Long acUsersKnlT3Long;
	
	@OneToMany(mappedBy="journAppAccessBssT", cascade={CascadeType.PERSIST/*, CascadeType.REFRESH, CascadeType.REMOVE*/})
	private List<RolesAppAccessBssT> rolesAppAccessBssTs;

	
	@Column(name="UP_USER")
	private Long acUsersKnlT2Long;
	
	//кому назначаются роли
	@ManyToOne
	@JoinColumn(name="UP_USER_APP", insertable=false, updatable=false)
	private AcUsersKnlT acUsersKnlT3;

	
	@Transient
	private String statusValue;
	
	public JournAppAccessBssT() {
	}

	public Long getIdSrv() {
		return this.idSrv;
	}

	public void setIdSrv(Long idSrv) {
		this.idSrv = idSrv;
	}

	

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCodeSystem() {
		return this.codeSystem;
	}

	public void setCodeSystem(String codeSystem) {
		this.codeSystem = codeSystem;
	}
	
	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	

	public Long getModeExec() {
		return this.modeExec;
	}

	public void setModeExec(Long modeExec) {
		this.modeExec = modeExec;
	}

	public String getLoginUser() {
		return this.loginUser;
	}
	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}
	
	public String getRejectReason() {
		return this.rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
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

	
	public AcIsBssT getAcIsBssT() {
		return this.acIsBssT;
	}

	public void setAcIsBssT(AcIsBssT acIsBssT) {
		this.acIsBssT = acIsBssT;
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

	public AcUsersKnlT getAcUsersKnlT1() {
		return this.acUsersKnlT1;
	}
	public void setAcUsersKnlT1(AcUsersKnlT acUsersKnlT1) {
		this.acUsersKnlT1 = acUsersKnlT1;
	}

	
	public void setAcUsersKnlT3(AcUsersKnlT acUsersKnlT3) {
		this.acUsersKnlT3 = acUsersKnlT3;
	}

	public List<RolesAppAccessBssT> getRolesAppAccessBssTs() {
		return this.rolesAppAccessBssTs;
	}

	public void setRolesAppAccessBssTs(List<RolesAppAccessBssT> rolesAppAccessBssTs) {
		this.rolesAppAccessBssTs = rolesAppAccessBssTs;
	}

	public RolesAppAccessBssT addRolesAppAccessBssT(RolesAppAccessBssT rolesAppAccessBssT) {
		getRolesAppAccessBssTs().add(rolesAppAccessBssT);
		rolesAppAccessBssT.setJournAppAccessBssT(this);

		return rolesAppAccessBssT;
	}

	public RolesAppAccessBssT removeRolesAppAccessBssT(RolesAppAccessBssT rolesAppAccessBssT) {
		getRolesAppAccessBssTs().remove(rolesAppAccessBssT);
		rolesAppAccessBssT.setJournAppAccessBssT(null);

		return rolesAppAccessBssT;
	}

	

	public Long getAcUsersKnlT2Long() {
		return acUsersKnlT2Long;
	}

	public void setAcUsersKnlT2Long(Long acUsersKnlT2Long) {
		this.acUsersKnlT2Long = acUsersKnlT2Long;
	}

	public Long getAcIsBssTLong() {
		return acIsBssTLong;
	}

	public void setAcIsBssTLong(Long acIsBssTLong) {
		this.acIsBssTLong = acIsBssTLong;
	}
	
	public Long getAcUsersKnlT3Long() {
		return acUsersKnlT3Long;
	}

	public void setAcUsersKnlT3Long(Long acUsersKnlT3Long) {
		this.acUsersKnlT3Long = acUsersKnlT3Long;
	}

	public void setStatusValue(String statusValue) {
		this.statusValue = statusValue;
	}
	
	public String getStatusValue() {
		
		if(statusValue==null&&status!=null){
			
			if(status.equals(0L)){
				statusValue="В обработке";
			}else if(status.equals(1L)){
				statusValue="Выполнено";
			}else if(status.equals(2L)){
				statusValue="Отклонено";
			}
		}
		
		return statusValue;
	}

	

}