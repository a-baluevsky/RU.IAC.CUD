package iac.cud.infosweb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the BINDING_LOG_T database table.
 * 
 */
@Entity
@Table(name="BINDING_LOG_T")
 public class BindingLogT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="BINDING_LOG_T_IDSRV_GENERATOR", sequenceName="BINDING_LOG_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="BINDING_LOG_T_IDSRV_GENERATOR")
	@Column(name="ID_SRV")
	private long idSrv;

	@Column(name="ATTEMPT_SRV")
	private Long attemptSrv;

    @Temporal( TemporalType.DATE)
	private Date created;

	private Long creator;

    @Temporal( TemporalType.DATE)
	@Column(name="DATE_EVENT_SRV")
	private Date dateEventSrv;

	@Column(name="NOT_UP_ISP_SIGN_USER")
	private Long notUpIspSignUser;

	@Column(name="UP_BINDING")
	private Long upBinding;

	@Column(name="UP_ISP_SIGN_USER")
	private String upIspSignUser;

	 @ManyToOne
	@JoinColumn(name="UP_USERS")
	private AcUser acUsersKnlT;

    public BindingLogT() {
    }

	public long getIdSrv() {
		return this.idSrv;
	}

	public void setIdSrv(long idSrv) {
		this.idSrv = idSrv;
	}

	public Long getAttemptSrv() {
		return this.attemptSrv;
	}

	public void setAttemptSrv(Long attemptSrv) {
		this.attemptSrv = attemptSrv;
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

	public Date getDateEventSrv() {
		return this.dateEventSrv;
	}

	public void setDateEventSrv(Date dateEventSrv) {
		this.dateEventSrv = dateEventSrv;
	}

	public Long getNotUpIspSignUser() {
		return this.notUpIspSignUser;
	}

	public void setNotUpIspSignUser(Long notUpIspSignUser) {
		this.notUpIspSignUser = notUpIspSignUser;
	}

	public Long getUpBinding() {
		return this.upBinding;
	}

	public void setUpBinding(Long upBinding) {
		this.upBinding = upBinding;
	}

	public String getUpIspSignUser() {
		return this.upIspSignUser;
	}

	public void setUpIspSignUser(String upIspSignUser) {
		this.upIspSignUser = upIspSignUser;
	}

	public AcUser getAcUsersKnlT() {
		return this.acUsersKnlT;
	}

	public void setAcUsersKnlT(AcUser acUsersKnlT) {
		this.acUsersKnlT = acUsersKnlT;
	}
	
}