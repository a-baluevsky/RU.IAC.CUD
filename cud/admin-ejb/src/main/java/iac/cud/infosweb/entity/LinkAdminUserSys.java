package iac.cud.infosweb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the LINK_ADMIN_USER_SYS database table.
 * 
 */
@Entity
@Table(name="LINK_ADMIN_USER_SYS")
 public class LinkAdminUserSys implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private LinkAdminUserSysPK pk;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	private Long creator;

	@ManyToOne
	@JoinColumn(name="UP_USER", insertable=false, updatable=false)
	private AcUser acUsersKnlT;

	
	@ManyToOne
	@JoinColumn(name="UP_SYS", insertable=false, updatable=false)
	private AcApplication acIsBssT;

	
	public LinkAdminUserSys() {
	}

	public LinkAdminUserSys(Long upUser, Long upSys) {
		this.pk=new LinkAdminUserSysPK(upUser, upSys);
	}
	
	public LinkAdminUserSysPK getPk() {
		return this.pk;
	}

	public void setPk(LinkAdminUserSysPK pk) {
		this.pk = pk;
	}

	

	public Long getCreator() {
		return this.creator;
	}

	public void setCreator(Long creator) {
		this.creator = creator;
	}

	public AcApplication getAcIsBssT() {
		return this.acIsBssT;
	}

	public void setAcIsBssT(AcApplication acIsBssT) {
		this.acIsBssT = acIsBssT;
	}

	public Date getCreated() {
		return this.created;
	}
   public void setCreated(Date created) {
		this.created = created;
	}
   
	public AcUser getAcUsersKnlT() {
		return this.acUsersKnlT;
	}

	public void setAcUsersKnlT(AcUser acUsersKnlT) {
		this.acUsersKnlT = acUsersKnlT;
	}
	@Override
	public boolean equals(Object o) {
		
		if (o == this) {
			return true;
		}
		if ( ! (o instanceof LinkAdminUserSys)) {
			return false;
		}
		LinkAdminUserSys other = (LinkAdminUserSys) o;
	
		return this.pk.equals(other.pk);
	}

	@Override
	public int hashCode() {
		return this.pk.hashCode()
			;
	}

}