package ru.spb.iac.cud.uarm.ejb.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the JOURN_APP_ACCESS_GROUPS_BSS_T database table.
 * 
 */
@Entity
@Table(name="JOURN_APP_ACCESS_GROUPS_BSS_T")
//@NamedQuery(name="JournAppAccessGroupsBssT.findAll", query="SELECT j FROM JournAppAccessGroupsBssT j")
 public class JournAppAccessGroupsBssT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="JOURN_APP_ACCESS_GROUPS_BSS_T_IDSRV_GENERATOR", sequenceName="JOURN_APP_ACCESS_GR_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="JOURN_APP_ACCESS_GROUPS_BSS_T_IDSRV_GENERATOR")
	@Column(name="ID_SRV")
	private Long idSrv;

	@Column(name="COMMENT_")
	private String comment;

	private String secret;

	private Long status;

	
	@Temporal(TemporalType.DATE)
	private Date created;

	@Column(name="MODE_EXEC")
	private Long modeExec;

	@Column(name="REJECT_REASON")
	private String rejectReason;


	
	@Column(name="UP_IS_APP")
	private Long acIsBssTLong;
	
	@ManyToOne
	@JoinColumn(name="UP_USER_EXEC")
	private AcUsersKnlT acUsersKnlT1;

	@OneToMany(mappedBy="journAppAccessGroupsBssT", cascade={CascadeType.PERSIST/*, CascadeType.REFRESH, CascadeType.REMOVE*/})
	private List<GroupsAppAccessGrBssT> groupsAppAccessGrBssTs;

	@ManyToOne
	@JoinColumn(name="UP_IS_APP", insertable=false, updatable=false)
	private AcIsBssT acIsBssT;

	
	//заявитель
	@ManyToOne
	@JoinColumn(name="UP_USER", insertable=false, updatable=false)
	private AcUsersKnlT acUsersKnlT2;

	//кому назначаются группы
		@ManyToOne
		@JoinColumn(name="UP_USER_APP", insertable=false, updatable=false)
		private AcUsersKnlT acUsersKnlT3;

		
	@Column(name="UP_USER")
	private Long acUsersKnlT2Long;
	
	
	@Column(name="UP_USER_APP")
	private Long acUsersKnlT3Long;
	
	@Transient
	private String statusValue;
	
	public JournAppAccessGroupsBssT() {
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

	public void setModeExec(Long modeExec) {
		this.modeExec = modeExec;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	public List<GroupsAppAccessGrBssT> getGroupsAppAccessGrBssTs() {
		return this.groupsAppAccessGrBssTs;
	}

	public void setGroupsAppAccessGrBssTs(List<GroupsAppAccessGrBssT> groupsAppAccessGrBssTs) {
		this.groupsAppAccessGrBssTs = groupsAppAccessGrBssTs;
	}

	public String getSecret() {
		return this.secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	public GroupsAppAccessGrBssT addGroupsAppAccessGrBssT(GroupsAppAccessGrBssT groupsAppAccessGrBssT) {
		getGroupsAppAccessGrBssTs().add(groupsAppAccessGrBssT);
		groupsAppAccessGrBssT.setJournAppAccessGroupsBssT(this);

		return groupsAppAccessGrBssT;
	}

	public GroupsAppAccessGrBssT removeGroupsAppAccessGrBssT(GroupsAppAccessGrBssT groupsAppAccessGrBssT) {
		getGroupsAppAccessGrBssTs().remove(groupsAppAccessGrBssT);
		groupsAppAccessGrBssT.setJournAppAccessGroupsBssT(null);

		return groupsAppAccessGrBssT;
	}

	

	public AcUsersKnlT getAcUsersKnlT1() {
		return this.acUsersKnlT1;
	}

	public void setAcUsersKnlT1(AcUsersKnlT acUsersKnlT1) {
		this.acUsersKnlT1 = acUsersKnlT1;
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

	public void setAcUsersKnlT3(AcUsersKnlT acUsersKnlT3) {
		this.acUsersKnlT3 = acUsersKnlT3;
	}

	

	public Long getAcUsersKnlT2Long() {
		return acUsersKnlT2Long;
	}

	public void setAcUsersKnlT2Long(Long acUsersKnlT2Long) {
		this.acUsersKnlT2Long = acUsersKnlT2Long;
	}

	public Long getAcUsersKnlT3Long() {
		return acUsersKnlT3Long;
	}

	public void setAcUsersKnlT3Long(Long acUsersKnlT3Long) {
		this.acUsersKnlT3Long = acUsersKnlT3Long;
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
	
	public Long getAcIsBssTLong() {
		return acIsBssTLong;
	}
    public void setAcIsBssTLong(Long acIsBssTLong) {
		this.acIsBssTLong = acIsBssTLong;
	}

}