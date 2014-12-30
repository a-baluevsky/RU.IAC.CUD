package ru.spb.iac.cud.uarm.ejb.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the JOURN_APP_ADMIN_USER_SYS_BSS_T database table.
 * 
 */
@Entity
@Table(name="JOURN_APP_ADMIN_USER_SYS_BSS_T")
 public class JournAppAdminUserSysBssT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="JOURN_APP_ADMIN_USER_SYS_BSS_T_IDSRV_GENERATOR", sequenceName="JOURN_APP_ADMIN_USER_SYS_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="JOURN_APP_ADMIN_USER_SYS_BSS_T_IDSRV_GENERATOR")
	@Column(name="ID_SRV")
	private Long idSrv;

	
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name="COMMENT_")
	private String comment;

	@Column(name="MODE_EXEC")
	private Long modeExec;

	private String secret;
	
	@Column(name="REJECT_REASON")
	private String rejectReason;

	private Long status;

	//кому назначается привилегия
	@ManyToOne
	@JoinColumn(name="UP_USER_APP", insertable=false, updatable=false)
	private AcUsersKnlT acUsersKnlT1;

	@Column(name="UP_USER_APP")
	private Long acUsersKnlT1Long;
	
	//заявитель
	@ManyToOne
	@JoinColumn(name="UP_USER", insertable=false, updatable=false)
	private AcUsersKnlT acUsersKnlT2;

	
	
	@ManyToOne
	@JoinColumn(name="UP_USER_EXEC")
	private AcUsersKnlT acUsersKnlT3;

	@Column(name="UP_USER")
	private Long acUsersKnlT2Long;
	
	@ManyToOne
	@JoinColumn(name="UP_SYS", insertable=false, updatable=false)
	private AcIsBssT acIsBssT;
	
	@Column(name="UP_SYS")
	private Long acIsBssTLong;
	
	@Transient
	private String statusValue;
	
	public JournAppAdminUserSysBssT() {
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

	public Long getModeExec() {
		return this.modeExec;
	}

	

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getComment() {
		return this.comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getSecret() {
		return this.secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public Long getStatus() {
		return this.status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public void setModeExec(Long modeExec) {
		this.modeExec = modeExec;
	}
	public String getRejectReason() {
		return this.rejectReason;
	}
	
	public AcUsersKnlT getAcUsersKnlT1() {
		return this.acUsersKnlT1;
	}

	public void setAcUsersKnlT1(AcUsersKnlT acUsersKnlT1) {
		this.acUsersKnlT1 = acUsersKnlT1;
	}

	

	public AcUsersKnlT getAcUsersKnlT3() {
		return this.acUsersKnlT3;
	}

	public void setAcUsersKnlT3(AcUsersKnlT acUsersKnlT3) {
		this.acUsersKnlT3 = acUsersKnlT3;
	}

	
	public AcIsBssT getAcIsBssT() {
		return this.acIsBssT;
	}

	public void setAcIsBssT(AcIsBssT acIsBssT) {
		this.acIsBssT = acIsBssT;
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

	public AcUsersKnlT getAcUsersKnlT2() {
		return this.acUsersKnlT2;
	}
	public void setAcUsersKnlT2(AcUsersKnlT acUsersKnlT2) {
		this.acUsersKnlT2 = acUsersKnlT2;
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

	
    public Long getAcUsersKnlT1Long() {
		return acUsersKnlT1Long;
	}
	public void setAcUsersKnlT1Long(Long acUsersKnlT1Long) {
		this.acUsersKnlT1Long = acUsersKnlT1Long;
	}
}