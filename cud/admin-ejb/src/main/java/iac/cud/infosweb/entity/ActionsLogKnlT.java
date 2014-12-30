package iac.cud.infosweb.entity;

import iac.cud.infosweb.dataitems.BaseItem;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the ACTIONS_LOG_KNL_T database table.
 * 
 */
@Entity
@Table(name="ACTIONS_LOG_KNL_T")
 public class ActionsLogKnlT extends BaseItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="ACTIONS_LOG_KNL_T_IDSRV_GENERATOR", sequenceName="ACTIONS_LOG_KNL_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ACTIONS_LOG_KNL_T_IDSRV_GENERATOR")
	@Column(name="ID_SRV")
	private Long idSrv;

	@Column(name="ATTEMPT_SRV")
	private Long attemptSrv;

    @Temporal( TemporalType.DATE)
	private Date created;

    @Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_ACTION")
	private Date dateAction;

    @Temporal( TemporalType.DATE)
	@Column(name="DATE_EVENT_SRV")
	private Date dateEventSrv;

    @Temporal( TemporalType.DATE)
	private Date modified;

	 @ManyToOne
	@JoinColumn(name="UP_ACTIONS")
	private ActionsBssT actionsBssT;

    @ManyToOne
   	@JoinColumn(name="UP_USERS")
   	private AcUser acUsersKnlT;
    
    @Transient
	private String actName;
    
    @Transient
   	private String isName;
    
    @Transient
   	private String userName;
    
    @Transient
   	private String dateActionValue;
    
    public ActionsLogKnlT() {
    }

    public Long getBaseId() {
   	   return this.idSrv;
   	} 
    
	public Long getIdSrv() {
		return this.idSrv;
	}

	public void setIdSrv(Long idSrv) {
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

	public Date getDateAction() {
		return this.dateAction;
	}

	public void setDateAction(Date dateAction) {
		this.dateAction = dateAction;
	}

	public Date getDateEventSrv() {
		return this.dateEventSrv;
	}

	public void setDateEventSrv(Date dateEventSrv) {
		this.dateEventSrv = dateEventSrv;
	}

	public Date getModified() {
		return this.modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public ActionsBssT getActionsBssT() {
		return this.actionsBssT;
	}

	public void setActionsBssT(ActionsBssT actionsBssT) {
		this.actionsBssT = actionsBssT;
	}
	
	
	public String getActName() {
		return this.actName;
	}
	public void setActName(String actName) {
		this.actName = actName;
	}
	
	public String getIsName() {
		return this.isName;
	}
	public void setIsName(String isName) {
		this.isName = isName;
	}
	
	public String getUserName() {
		return this.userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getDateActionValue() {
		return this.dateActionValue;
	}
    public void setDateActionValue(String dateActionValue) {
		this.dateActionValue = dateActionValue;
	}
    
    public AcUser getAcUsersKnlT() {
		return this.acUsersKnlT;
	}
    public void setAcUsersKnlT(AcUser acUsersKnlT) {
		this.acUsersKnlT = acUsersKnlT;
	}
	
}