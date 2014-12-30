package iac.cud.infosweb.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the LINK_GROUP_SYS_SYS_KNL_T database table.
 * 
 */
@Entity
@Table(name="LINK_GROUP_SYS_SYS_KNL_T")
 public class LinkGroupSysSysKnlT implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private LinkGroupSysSysKnlTPK pk;
	
	
	@ManyToOne
	@JoinColumn(name="UP_SYSTEMS", insertable=false,updatable=false)
	private AcApplication acIsBssT;

	@ManyToOne
	@JoinColumn(name="UP_GROUP_SYSTEMS", insertable=false,updatable=false)
	private GroupSystemsKnlT groupSystemsKnlT;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	private Long creator;

	
	public LinkGroupSysSysKnlT() {
	}
	
	public LinkGroupSysSysKnlT(Long acSys, Long groupSys) {
		this.pk = new LinkGroupSysSysKnlTPK(acSys, groupSys);
	}
	
	public LinkGroupSysSysKnlTPK getPk() {
		return pk;
	}
    public void setPk(LinkGroupSysSysKnlTPK pk) {
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

	public GroupSystemsKnlT getGroupSystemsKnlT() {
		return this.groupSystemsKnlT;
	}

	public void setGroupSystemsKnlT(GroupSystemsKnlT groupSystemsKnlT) {
		this.groupSystemsKnlT = groupSystemsKnlT;
	}

	
	public Date getCreated() {
		return this.created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	
	
	@Override
	public boolean equals(Object o) {
		
		if (o == this) {
			return true;
		}
		if ( ! (o instanceof LinkGroupSysSysKnlT)) {
			return false;
		}
		LinkGroupSysSysKnlT other = (LinkGroupSysSysKnlT) o;
	
		return this.pk.equals(other.pk);
	}

	@Override
	public int hashCode() {
		return this.pk.hashCode()
			;
	}
	
	
}