package ru.spb.iac.cud.uarm.ejb.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the JOURN_APP_ADMIN_USER_SYS_BSS_T database table.
 * 
 */
@Entity
@Table(name="JOURN_APP_ORG_MANAGER_BSS_T")
 public class JournAppOrgManagerBssT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="JOURN_APP_ORG_MANAGER_IDSRV_GENERATOR", sequenceName="JOURN_APP_ORG_MANAGER_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="JOURN_APP_ORG_MANAGER_IDSRV_GENERATOR")
	@Column(name="ID_SRV")
	private Long idSrv;

	@Column(name="COMMENT_")
	private String comment;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;


	@Column(name="REJECT_REASON")
	private String rejectReason;

	private String secret;

	@Column(name="MODE_EXEC")
	private Long modeExec;

	
	

	//кому назначается привилегия
	@ManyToOne
	@JoinColumn(name="UP_USER_APP", insertable=false, updatable=false)
	private AcUsersKnlT acUsersKnlT1;

	@Column(name="UP_USER_APP")
	private Long acUsersKnlT1Long;
	
	private Long status;
	
	//заявитель
	@ManyToOne
	@JoinColumn(name="UP_USER", insertable=false, updatable=false)
	private AcUsersKnlT acUsersKnlT2;

	@Column(name="UP_USER")
	private Long acUsersKnlT2Long;

		
	@ManyToOne
	@JoinColumn(name="UP_USER_EXEC")
	private AcUsersKnlT acUsersKnlT3;

	
	
	@Transient
	private String statusValue;
	
	public JournAppOrgManagerBssT() {
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

	public String getComment() {
		return this.comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

	

	public Long getModeExec() {
		return this.modeExec;
	}

	public void setModeExec(Long modeExec) {
		this.modeExec = modeExec;
	}

	

	public String getSecret() {
		return this.secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getRejectReason() {
		return this.rejectReason;
	}
	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}
	
	

	public AcUsersKnlT getAcUsersKnlT1() {
		return this.acUsersKnlT1;
	}

	public void setAcUsersKnlT1(AcUsersKnlT acUsersKnlT1) {
		this.acUsersKnlT1 = acUsersKnlT1;
	}

	public AcUsersKnlT getAcUsersKnlT2() {
		return this.acUsersKnlT2;
	}

	public void setAcUsersKnlT2(AcUsersKnlT acUsersKnlT2) {
		this.acUsersKnlT2 = acUsersKnlT2;
	}

	public Long getStatus() {
		return this.status;
	}
	public void setStatus(Long status) {
		this.status = status;
	}
	
	public AcUsersKnlT getAcUsersKnlT3() {
		return this.acUsersKnlT3;
	}

	public void setAcUsersKnlT3(AcUsersKnlT acUsersKnlT3) {
		this.acUsersKnlT3 = acUsersKnlT3;
	}

	

	public Long getAcUsersKnlT2Long() {
		return acUsersKnlT2Long;
	}
	public void setAcUsersKnlT2Long(Long acUsersKnlT2Long) {
		this.acUsersKnlT2Long = acUsersKnlT2Long;
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

	public void setStatusValue(String statusValue) {
		this.statusValue = statusValue;
	}
	
	public Long getAcUsersKnlT1Long() {
		return acUsersKnlT1Long;
	}
	public void setAcUsersKnlT1Long(Long acUsersKnlT1Long) {
		this.acUsersKnlT1Long = acUsersKnlT1Long;
	}
}