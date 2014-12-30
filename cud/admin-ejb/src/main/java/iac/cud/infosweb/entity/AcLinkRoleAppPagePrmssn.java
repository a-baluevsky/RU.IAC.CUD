package iac.cud.infosweb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * Сущность Связи Роли, Рубрики и Привилегий
 * @author bubnov
 *
 */
@Entity
@Table(name="AC_LINK_ROLE_APP_DOM_PRM_KNL_T")
 public class AcLinkRoleAppPagePrmssn implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private AcLinkRoleAppPagePrmssnPK pk;
	
    @Temporal(TemporalType.DATE)
	private Date created;

	private Long creator;

	@ManyToOne
	@JoinColumn(name="UP_DOM",insertable=false,updatable=false)
	private AcAppPage acAppPage;

	@ManyToOne
	@JoinColumn(name="UP_PERMISS",insertable=false,updatable=false)
	private AcPermissionsList acPermissionsList;

	@ManyToOne
	@JoinColumn(name="UP_ROLES",insertable=false,updatable=false)
	private AcRole acRole;

    public AcLinkRoleAppPagePrmssn() {
    }
    
    public AcLinkRoleAppPagePrmssn(Long acAppPage, Long acPermissionsList, Long acRole) {
		this.pk=new AcLinkRoleAppPagePrmssnPK(acAppPage, acPermissionsList, acRole);
	}
    public AcLinkRoleAppPagePrmssnPK getPk() {
		return this.pk;
	}

	public void setPk(AcLinkRoleAppPagePrmssnPK pk) {
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

	public AcAppPage getAcAppPage() {
		return this.acAppPage;
	}

	public void setAcAppPage(AcAppPage acAppPage) {
		this.acAppPage = acAppPage;
	}
	
	public AcPermissionsList getAcPermissionsList() {
		return this.acPermissionsList;
	}

	public void setAcPermissionsList(AcPermissionsList acPermissionsList) {
		this.acPermissionsList = acPermissionsList;
	}
	
	public AcRole getAcRole() {
		return this.acRole;
	}

	public void setAcRole(AcRole acRole) {
		this.acRole = acRole;
	}
	
}