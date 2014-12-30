package iac.cud.infosweb.entity;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the BINDING_AUTO_LINK_BSS_T database table.
 * 
 */
@Embeddable
 public class BindingAutoLinkBssTPK implements Serializable {
	private static final long serialVersionUID = 1L;

  	@Column(name="UP_ISP_SIGN_USER")
	private String upIspSignUser;

	@Column(name="UP_USERS")
	private Long acUsersKnlT;

    public BindingAutoLinkBssTPK() {
    }

    public BindingAutoLinkBssTPK(String userCode, Long userId) {
		this.upIspSignUser=userCode;
		this.acUsersKnlT=userId;
	}
    
	public String getUpIspSignUser() {
		return this.upIspSignUser;
	}

	public void setUpIspSignUser(String upIspSignUser) {
		this.upIspSignUser = upIspSignUser;
	}

	public Long getAcUsersKnlT() {
		return this.acUsersKnlT;
	}

	public void setAcUsersKnlT(Long acUsersKnlT) {
		this.acUsersKnlT = acUsersKnlT;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if ( ! (o instanceof BindingAutoLinkBssTPK)) {
			return false;
		}
		BindingAutoLinkBssTPK other = (BindingAutoLinkBssTPK) o;
		return this.upIspSignUser.equals(other.upIspSignUser)
			   && this.acUsersKnlT.equals(other.acUsersKnlT);
	}

	@Override
	public int hashCode() {
		return this.upIspSignUser.hashCode()
			 ^ this.acUsersKnlT.hashCode()
			;
	}
}