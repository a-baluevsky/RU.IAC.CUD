package iac.cud.infosweb.entity;

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
	
  
	 @ManyToOne
	@JoinColumn(name="UP_ROLES", insertable=false,updatable=false)
	private AcRole acRolesBssT;

	 @ManyToOne
	@JoinColumn(name="UP_GROUP_USERS", insertable=false,updatable=false)
	private GroupUsersKnlT groupUsersKnlT;

	  @Temporal(TemporalType.DATE)
		private Date created;

		private Long creator;

		
    public LinkGroupUsersRolesKnlT() {
    }

    public LinkGroupUsersRolesKnlT(Long role, Long groupUser) {
		this.pk=new LinkGroupUsersRolesKnlTPK(role, groupUser);
	}
    
    public LinkGroupUsersRolesKnlTPK getPk() {
		return this.pk;
	}

	public void setPk(LinkGroupUsersRolesKnlTPK pk) {
		this.pk = pk;
	}
	
	public Long getCreator() {
		return this.creator;
	}

	public void setCreator(Long creator) {
		this.creator = creator;
	}

	
	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public GroupUsersKnlT getGroupUsersKnlT() {
		return this.groupUsersKnlT;
	}

	public void setGroupUsersKnlT(GroupUsersKnlT groupUsersKnlT) {
		this.groupUsersKnlT = groupUsersKnlT;
	}

	
	public AcRole getAcRolesBssT() {
		return this.acRolesBssT;
	}

	public void setAcRolesBssT(AcRole acRolesBssT) {
		this.acRolesBssT = acRolesBssT;
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