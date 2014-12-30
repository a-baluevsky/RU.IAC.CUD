package ru.spb.iac.cud.uarm.ejb.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the LINK_GROUP_USERS_USERS_KNL_T database table.
 * 
 */
@Entity
@Table(name="LINK_GROUP_USERS_USERS_KNL_T")
 public class LinkGroupUsersUsersKnlT implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private LinkGroupUsersUsersKnlTPK pk;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	private Long creator;

	//bi-directional many-to-one association to AcUsersKnlT
	@ManyToOne
	@JoinColumn(name="UP_USERS", insertable=false,updatable=false)
	private AcUsersKnlT acUsersKnlT;

	//bi-directional many-to-one association to GroupUsersKnlT
	@ManyToOne
	@JoinColumn(name="UP_GROUP_USERS", insertable=false,updatable=false)
	private GroupUsersKnlT groupUsersKnlT;

	public LinkGroupUsersUsersKnlT() {
	}

	public LinkGroupUsersUsersKnlT(Long acUser, Long groupUser) {
		this.pk=new LinkGroupUsersUsersKnlTPK(acUser, groupUser);
	}
    
    public LinkGroupUsersUsersKnlTPK getPk() {
		return this.pk;
	}

	public void setPk(LinkGroupUsersUsersKnlTPK pk) {
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

	public AcUsersKnlT getAcUsersKnlT() {
		return this.acUsersKnlT;
	}

	public void setAcUsersKnlT(AcUsersKnlT acUsersKnlT) {
		this.acUsersKnlT = acUsersKnlT;
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
		if ( ! (o instanceof LinkGroupUsersUsersKnlT)) {
			return false;
		}
		LinkGroupUsersUsersKnlT other = (LinkGroupUsersUsersKnlT) o;
	
		return this.pk.equals(other.pk);
	}

	@Override
	public int hashCode() {
		return this.pk.hashCode()
			;
	}
	
}