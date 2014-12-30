package ru.spb.iac.cud.uarm.ejb.entity;

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

	//bi-directional many-to-one association to AcIsBssT
	@ManyToOne
	@JoinColumn(name="UP_SYS", insertable=false, updatable=false)
	private AcIsBssT acIsBssT;

	//bi-directional many-to-one association to AcUsersKnlT
	@ManyToOne
	@JoinColumn(name="UP_USER", insertable=false, updatable=false)
	private AcUsersKnlT acUsersKnlT;

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

	public AcIsBssT getAcIsBssT() {
		return this.acIsBssT;
	}

	public void setAcIsBssT(AcIsBssT acIsBssT) {
		this.acIsBssT = acIsBssT;
	}

	public AcUsersKnlT getAcUsersKnlT() {
		return this.acUsersKnlT;
	}

	public void setAcUsersKnlT(AcUsersKnlT acUsersKnlT) {
		this.acUsersKnlT = acUsersKnlT;
	}

}