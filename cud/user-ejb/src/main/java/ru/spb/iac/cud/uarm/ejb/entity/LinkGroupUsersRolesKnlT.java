package ru.spb.iac.cud.uarm.ejb.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the LINK_GROUP_USERS_ROLES_KNL_T database table.
 * 
 */
@Entity
@Table(name="LINK_GROUP_USERS_ROLES_KNL_T")
 public class LinkGroupUsersRolesKnlT implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private LinkGroupUsersRolesKnlTPK pk;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	private Long creator;

	//bi-directional many-to-one association to AcRolesBssT
	@ManyToOne
	@JoinColumn(name="UP_ROLES", insertable=false,updatable=false)
	private AcRolesBssT acRolesBssT;

	//bi-directional many-to-one association to GroupUsersKnlT
	@ManyToOne
	@JoinColumn(name="UP_GROUP_USERS", insertable=false,updatable=false)
	private GroupUsersKnlT groupUsersKnlT;

	public LinkGroupUsersRolesKnlT() {
	}

	public LinkGroupUsersRolesKnlT(Long role, Long groupUser) {
		this.pk=new LinkGroupUsersRolesKnlTPK(role, groupUser);
	}
	
	public LinkGroupUsersRolesKnlTPK getPk() {
		return pk;
	}

	public void setPk(LinkGroupUsersRolesKnlTPK pk) {
		this.pk = pk;
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

	public AcRolesBssT getAcRolesBssT() {
		return this.acRolesBssT;
	}

	public void setAcRolesBssT(AcRolesBssT acRolesBssT) {
		this.acRolesBssT = acRolesBssT;
	}

	public GroupUsersKnlT getGroupUsersKnlT() {
		return this.groupUsersKnlT;
	}

	public void setGroupUsersKnlT(GroupUsersKnlT groupUsersKnlT) {
		this.groupUsersKnlT = groupUsersKnlT;
	}

	@Override
	public boolean equals(Object o) {
		
		if (o == this) {
			return true;
		}
		if ( ! (o instanceof LinkGroupUsersRolesKnlT)) {
			return false;
		}
		LinkGroupUsersRolesKnlT other = (LinkGroupUsersRolesKnlT) o;
	
		return this.pk.equals(other.pk);
	}

	@Override
	public int hashCode() {
		return this.pk.hashCode()
			;
	}

}