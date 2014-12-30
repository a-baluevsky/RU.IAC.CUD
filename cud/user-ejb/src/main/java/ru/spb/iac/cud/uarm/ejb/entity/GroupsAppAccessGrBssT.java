package ru.spb.iac.cud.uarm.ejb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the GROUPS_APP_ACCESS_GR_BSS_T database table.
 * 
 */
@Entity
@Table(name="GROUPS_APP_ACCESS_GR_BSS_T")
//@NamedQuery(name="GroupsAppAccessGrBssT.findAll", query="SELECT g FROM GroupsAppAccessGrBssT g")
 public class GroupsAppAccessGrBssT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="GROUPS_APP_ACCESS_GR_BSS_T_IDSRV_GENERATOR", sequenceName="GROUPS_APP_ACCESS_GR_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="GROUPS_APP_ACCESS_GR_BSS_T_IDSRV_GENERATOR")
	@Column(name="ID_SRV")
	private Long idSrv;

	@ManyToOne
	@JoinColumn(name="UP_GROUP", insertable=false, updatable=false)
	private GroupUsersKnlT groupUsersKnlT;

	@Column(name="UP_GROUP")
	private Long groupUsersKnlTLong;
	
	@ManyToOne
	@JoinColumn(name="UP_APP_ACC_GR")
	private JournAppAccessGroupsBssT journAppAccessGroupsBssT;

	public GroupsAppAccessGrBssT() {
	}

	public Long getIdSrv() {
		return this.idSrv;
	}

	public void setIdSrv(Long idSrv) {
		this.idSrv = idSrv;
	}

	public GroupUsersKnlT getGroupUsersKnlT() {
		return this.groupUsersKnlT;
	}

	public void setGroupUsersKnlT(GroupUsersKnlT groupUsersKnlT) {
		this.groupUsersKnlT = groupUsersKnlT;
	}

	public JournAppAccessGroupsBssT getJournAppAccessGroupsBssT() {
		return this.journAppAccessGroupsBssT;
	}

	public void setJournAppAccessGroupsBssT(JournAppAccessGroupsBssT journAppAccessGroupsBssT) {
		this.journAppAccessGroupsBssT = journAppAccessGroupsBssT;
	}

	public Long getGroupUsersKnlTLong() {
		return groupUsersKnlTLong;
	}

	public void setGroupUsersKnlTLong(Long groupUsersKnlTLong) {
		this.groupUsersKnlTLong = groupUsersKnlTLong;
	}

}