package ru.spb.iac.cud.uarm.ejb.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
 public class LinkGroupUsersRolesKnlTPK  implements Serializable {
	
	@Column(name="UP_ROLES")
	private Long acRole;
	
	@Column(name="UP_GROUP_USERS")
	private Long groupUser;
	
	private static final long serialVersionUID = 1L;

	public LinkGroupUsersRolesKnlTPK() {
		super();
	}
	public LinkGroupUsersRolesKnlTPK(Long acRole, Long groupUser) {
		this.acRole=acRole;
		this.groupUser=groupUser;
	}

	public Long getAcRole() {
		return this.acRole;
	}

	public void setAcRole(Long acRole) {
		this.acRole = acRole;
	}
	
	public Long getGroupUser() {
		return this.groupUser;
	}

	public void setGroupUser(Long groupUser) {
		this.groupUser = groupUser;
	}

	

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if ( ! (o instanceof LinkGroupUsersRolesKnlTPK)) {
			return false;
		}
		LinkGroupUsersRolesKnlTPK other = (LinkGroupUsersRolesKnlTPK) o;
		return this.acRole.equals(other.acRole)
			   && this.groupUser.equals(other.groupUser);
	}

	@Override
	public int hashCode() {
		return this.acRole.hashCode()
			 ^ this.groupUser.hashCode()
			;
	}
}

