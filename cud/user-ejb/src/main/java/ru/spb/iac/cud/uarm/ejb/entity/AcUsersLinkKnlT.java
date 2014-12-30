package ru.spb.iac.cud.uarm.ejb.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the AC_USERS_LINK_KNL_T database table.
 * 
 */
@Entity
@Table(name="AC_USERS_LINK_KNL_T")
 public class AcUsersLinkKnlT implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private AcUsersLinkKnlTPK pk;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	private Long creator;

	//bi-directional many-to-one association to AcRolesBssT
	@ManyToOne
	@JoinColumn(name="UP_ROLES", insertable=false, updatable=false)
	private AcRolesBssT acRolesBssT;

	//bi-directional many-to-one association to AcUsersKnlT
	@ManyToOne
	@JoinColumn(name="UP_USERS", insertable=false, updatable=false)
	private AcUsersKnlT acUsersKnlT;

	public AcUsersLinkKnlT() {
	}

	public AcUsersLinkKnlT(Long acRolesBssT, Long acUsersKnlT) {
		this.pk=new AcUsersLinkKnlTPK(acRolesBssT, acUsersKnlT);
	}
	
	public AcUsersLinkKnlTPK getPk() {
		return pk;
	}

	public void setPk(AcUsersLinkKnlTPK pk) {
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

	public AcUsersKnlT getAcUsersKnlT() {
		return this.acUsersKnlT;
	}

	public void setAcUsersKnlT(AcUsersKnlT acUsersKnlT) {
		this.acUsersKnlT = acUsersKnlT;
	}

	@Override
	public boolean equals(Object o) {
		
		if (o == this) {
			return true;
		}
		if ( ! (o instanceof AcUsersLinkKnlT)) {
			return false;
		}
		AcUsersLinkKnlT other = (AcUsersLinkKnlT) o;
	
		return this.pk.equals(other.pk);
	}

	@Override
	public int hashCode() {
		return this.pk.hashCode()
			;
	}
	
}