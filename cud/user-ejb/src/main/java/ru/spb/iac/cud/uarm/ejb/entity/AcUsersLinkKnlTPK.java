package ru.spb.iac.cud.uarm.ejb.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
 public class AcUsersLinkKnlTPK  implements Serializable {
	
	@Column(name="UP_ROLES")
	private Long acRole;
	
	@Column(name="UP_USERS")
	private Long acUser;
	
	private static final long serialVersionUID = 1L;

	public AcUsersLinkKnlTPK() {
		super();
	}
	public AcUsersLinkKnlTPK(Long acRole, Long acUser) {
		this.acRole=acRole;
		this.acUser=acUser;
		
	}

	public Long getAcRole() {
		return this.acRole;
	}

	public void setAcRole(Long acRole) {
		this.acRole = acRole;
	}
	
	public Long getAcUser() {
		return this.acUser;
	}

	public void setAcUser(Long acUser) {
		this.acUser = acUser;
	}

	

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if ( ! (o instanceof AcUsersLinkKnlTPK)) {
			return false;
		}
		AcUsersLinkKnlTPK other = (AcUsersLinkKnlTPK) o;
		return this.acRole.equals(other.acRole)
			   && this.acUser.equals(other.acUser);
	}

	@Override
	public int hashCode() {
		return this.acRole.hashCode()
			 ^ this.acUser.hashCode()
			;
	}
}

