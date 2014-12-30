package iac.cud.infosweb.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;


/**
 * Сущность Связи Роли и Пользователя
 * @author bubnov
 *
 */
@Entity
@Table(name="AC_USERS_LINK_KNL_T")
 public class AcLinkUserToRoleToRaion implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private AcLinkUserToRoleToRaionPK pk;
	
    @Temporal(TemporalType.TIMESTAMP)
	private Date created;

	private Long creator;

	@ManyToOne
	@JoinColumn(name="UP_ROLES",insertable=false,updatable=false)
	private AcRole acRole;

	@ManyToOne
	@JoinColumn(name="UP_USERS",insertable=false,updatable=false)
	private AcUser acUser;

    public AcLinkUserToRoleToRaion() {
    }
    public AcLinkUserToRoleToRaion(Long acRole, Long acUser) {
		this.pk=new AcLinkUserToRoleToRaionPK(acRole, acUser);
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

	public AcRole getAcRole() {
		return this.acRole;
	}

	public void setAcRole(AcRole acRole) {
		this.acRole = acRole;
	}
	
	public AcUser getAcUser() {
		return this.acUser;
	}

	public void setAcUser(AcUser acUser) {
		this.acUser = acUser;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == this) {
			return true;
		}
		if ( ! (o instanceof AcLinkUserToRoleToRaion)) {
			return false;
		}
		AcLinkUserToRoleToRaion other = (AcLinkUserToRoleToRaion) o;
	
		return this.pk.equals(other.pk);
	}

	@Override
	public int hashCode() {
		return this.pk.hashCode()
			;
	}
}