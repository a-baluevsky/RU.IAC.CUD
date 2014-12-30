package ru.spb.iac.cud.uarm.ejb.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
 public class LinkGroupUsersUsersKnlTPK  implements Serializable {
	
	@Column(name="UP_USERS")
	private Long acUser;
	
	@Column(name="UP_GROUP_USERS")
	private Long groupUser;
	
	private static final long serialVersionUID = 1L;

	public LinkGroupUsersUsersKnlTPK() {
		super();
	}
	public LinkGroupUsersUsersKnlTPK(Long acUser, Long groupUser) {
		this.acUser=acUser;
		this.groupUser=groupUser;
	}

	public Long getAcUser() {
		return this.acUser;
	}

	public void setAcUser(Long acUser) {
		this.acUser = acUser;
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
		if ( ! (o instanceof LinkGroupUsersUsersKnlTPK)) {
			return false;
		}
		LinkGroupUsersUsersKnlTPK other = (LinkGroupUsersUsersKnlTPK) o;
		return this.groupUser.equals(other.groupUser)
			   && this.acUser.equals(other.acUser);
	}

	@Override
	public int hashCode() {
		return this.groupUser.hashCode()
			 ^ this.acUser.hashCode()
			;
	}
}

