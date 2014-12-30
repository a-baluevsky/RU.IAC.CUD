package iac.cud.infosweb.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
 public class AcLinkRoleAppPagePrmssnPK  implements Serializable {
		
	@Column(name="UP_DOM")
	private Long acAppPage;
	
	@Column(name="UP_PERMISS")
	private Long acPermissionsList;
	
	@Column(name="UP_ROLES")
	private Long acRole;
	
	private static final long serialVersionUID = 1L;

	public AcLinkRoleAppPagePrmssnPK() {
		super();
	}
	public AcLinkRoleAppPagePrmssnPK(Long acAppPage, Long acPermissionsList, Long acRole) {
		this.acAppPage=acAppPage;
		this.acPermissionsList=acPermissionsList;
		this.acRole=acRole;
	}
	public Long getAcAppPage() {
		return this.acAppPage;
	}

	public void setAcAppPage(Long acAppPage) {
		this.acAppPage = acAppPage;
	}

	public Long getAcPermissionsList() {
		return this.acPermissionsList;
	}

	public void setAcPermissionsList(Long acPermissionsList) {
		this.acPermissionsList = acPermissionsList;
	}

	public Long getAcRole() {
		return this.acRole;
	}

	public void setAcRole(Long acRole) {
		this.acRole = acRole;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if ( ! (o instanceof AcLinkRoleAppPagePrmssnPK)) {
			return false;
		}
		AcLinkRoleAppPagePrmssnPK other = (AcLinkRoleAppPagePrmssnPK) o;
		return this.acAppPage.equals(other.acAppPage)
			&& this.acPermissionsList.equals(other.acPermissionsList)
			&& this.acRole.equals(other.acRole);
	}

	@Override
	public int hashCode() {
		return this.acAppPage.hashCode()
			^ this.acPermissionsList.hashCode()
			^ this.acRole.hashCode();
	}
}