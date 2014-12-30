package iac.cud.infosweb.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the LINK_ADMIN_USER_SYS database table.
 * 
 */
@Embeddable
 public class LinkAdminUserSysPK implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name="UP_USER")
	private Long upUser;

	@Column(name="UP_SYS")
	private Long upSys;

	public LinkAdminUserSysPK() {
	}
	
	public LinkAdminUserSysPK(Long upUser, Long upSys) {
		this.upUser=upUser;
		this.upSys=upSys;
		
	}
	public Long getUpUser() {
		return this.upUser;
	}
	public void setUpUser(Long upUser) {
		this.upUser = upUser;
	}
	public Long getUpSys() {
		return this.upSys;
	}
	public void setUpSys(Long upSys) {
		this.upSys = upSys;
	}

	public boolean equals(Object other) {
		
		if (this == other) {
			return true;
		}
		if (!(other instanceof LinkAdminUserSysPK)) {
			return false;
		}
		LinkAdminUserSysPK castOther = (LinkAdminUserSysPK)other;
		
		return 
			this.upUser.equals(castOther.upUser)
			&& this.upSys.equals(castOther.upSys);
	}

	@Override
	public int hashCode() {
		return this.upUser.hashCode()
			 ^ this.upSys.hashCode()
			;
	}
}