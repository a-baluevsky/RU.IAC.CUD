package ru.spb.iac.cud.uarm.ejb.entity;


import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the GROUP_USERS_KNL_T database table.
 * 
 */
@Entity
@Table(name="GROUP_USERS_KNL_T")
 public class GroupUsersKnlT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="GROUP_USERS_KNL_T_IDSRV_GENERATOR", sequenceName="GROUP_USERS_KNL_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="GROUP_USERS_KNL_T_IDSRV_GENERATOR")
	@Column(name="ID_SRV")
	private Long idSrv;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	private Long creator;

	private String description;

	@Column(name="FULL_")
	private String full;

	private Long modificator;

	@Temporal(TemporalType.DATE)
	private Date modified;

	@OneToMany(mappedBy="groupUsersKnlT")
	private List<LinkGroupUsersRolesKnlT> linkGroupUsersRolesKnlTs;

	@OneToMany(mappedBy="groupUsersKnlT")
	private List<LinkGroupUsersUsersKnlT> linkGroupUsersUsersKnlTs;

	@OneToMany(mappedBy="groupUsersKnlT")
	private List<GroupsAppAccessGrBssT> groupsAppAccessGrBssTs;
	
	@Column(name="SIGN_OBJECT")
	private String signObject;
	
	@Transient
	private Boolean checked;
	
	@Transient
	private List<AcIsBssT> armList;
	
	public GroupUsersKnlT() {
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

	public Long getCreator() {
		return this.creator;
	}

	public void setCreator(Long creator) {
		this.creator = creator;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFull() {
		return this.full;
	}

	public void setFull(String full) {
		this.full = full;
	}

	public Long getModificator() {
		return this.modificator;
	}

	public void setModificator(Long modificator) {
		this.modificator = modificator;
	}

	public Date getModified() {
		return this.modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public List<LinkGroupUsersRolesKnlT> getLinkGroupUsersRolesKnlTs() {
		return this.linkGroupUsersRolesKnlTs;
	}

	public void setLinkGroupUsersRolesKnlTs(List<LinkGroupUsersRolesKnlT> linkGroupUsersRolesKnlTs) {
		this.linkGroupUsersRolesKnlTs = linkGroupUsersRolesKnlTs;
	}

	public LinkGroupUsersRolesKnlT addLinkGroupUsersRolesKnlT(LinkGroupUsersRolesKnlT linkGroupUsersRolesKnlT) {
		getLinkGroupUsersRolesKnlTs().add(linkGroupUsersRolesKnlT);
		linkGroupUsersRolesKnlT.setGroupUsersKnlT(this);

		return linkGroupUsersRolesKnlT;
	}

	public LinkGroupUsersRolesKnlT removeLinkGroupUsersRolesKnlT(LinkGroupUsersRolesKnlT linkGroupUsersRolesKnlT) {
		getLinkGroupUsersRolesKnlTs().remove(linkGroupUsersRolesKnlT);
		linkGroupUsersRolesKnlT.setGroupUsersKnlT(null);

		return linkGroupUsersRolesKnlT;
	}

	public List<LinkGroupUsersUsersKnlT> getLinkGroupUsersUsersKnlTs() {
		return this.linkGroupUsersUsersKnlTs;
	}

	public void setLinkGroupUsersUsersKnlTs(List<LinkGroupUsersUsersKnlT> linkGroupUsersUsersKnlTs) {
		this.linkGroupUsersUsersKnlTs = linkGroupUsersUsersKnlTs;
	}

	public LinkGroupUsersUsersKnlT addLinkGroupUsersUsersKnlT(LinkGroupUsersUsersKnlT linkGroupUsersUsersKnlT) {
		getLinkGroupUsersUsersKnlTs().add(linkGroupUsersUsersKnlT);
		linkGroupUsersUsersKnlT.setGroupUsersKnlT(this);

		return linkGroupUsersUsersKnlT;
	}

	public LinkGroupUsersUsersKnlT removeLinkGroupUsersUsersKnlT(LinkGroupUsersUsersKnlT linkGroupUsersUsersKnlT) {
		getLinkGroupUsersUsersKnlTs().remove(linkGroupUsersUsersKnlT);
		linkGroupUsersUsersKnlT.setGroupUsersKnlT(null);

		return linkGroupUsersUsersKnlT;
	}

	public List<GroupsAppAccessGrBssT> getGroupsAppAccessGrBssTs() {
		return groupsAppAccessGrBssTs;
	}

	public void setGroupsAppAccessGrBssTs(
			List<GroupsAppAccessGrBssT> groupsAppAccessGrBssTs) {
		this.groupsAppAccessGrBssTs = groupsAppAccessGrBssTs;
	}
	
	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public String getSignObject() {
		return signObject;
	}

	public void setSignObject(String signObject) {
		this.signObject = signObject;
	}

	public List<AcIsBssT> getArmList() {
		return armList;
	}

	public void setArmList(List<AcIsBssT> armList) {
		this.armList = armList;
	}


}