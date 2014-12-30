package iac.cud.infosweb.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the BINDING_AUTO_LINK_BSS_T database table.
 * 
 */
@Entity
@Table(name="BINDING_AUTO_LINK_BSS_T")
 public class BindingAutoLinkBssT implements Serializable {
	private static final long serialVersionUID = 1L;

	
	@EmbeddedId
	private BindingAutoLinkBssTPK pk;
	
    @Temporal(TemporalType.DATE)
	private Date created;

	@Column(name="TYPE_BINDING")
	private Long typeBinding;

	@Column(name="UP_ISP_SIGN_USER", insertable=false,updatable=false)
	private String upIspSignUser;

	@ManyToOne
	@JoinColumn(name="UP_USERS", insertable=false,updatable=false)
	private AcUser acUsersKnlT;

    public BindingAutoLinkBssT(String userCode, Long userId) {
    	this.pk= new BindingAutoLinkBssTPK(userCode, userId) ;
    }

    public BindingAutoLinkBssT() {
    }
    
	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Long getTypeBinding() {
		return this.typeBinding;
	}

	public void setTypeBinding(Long typeBinding) {
		this.typeBinding = typeBinding;
	}

	public String getUpIspSignUser() {
		return this.upIspSignUser;
	}

	public void setUpIspSignUser(String upIspSignUser) {
		this.upIspSignUser = upIspSignUser;
	}

	public AcUser getAcUsersKnlT() {
		return this.acUsersKnlT;
	}

	public void setAcUsersKnlT(AcUser acUsersKnlT) {
		this.acUsersKnlT = acUsersKnlT;
	}
	
	public BindingAutoLinkBssTPK getPk() {
			return pk;
	}
	public void setPk(BindingAutoLinkBssTPK pk) {
			this.pk = pk;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == this) {
			return true;
		}
		if ( ! (o instanceof BindingAutoLinkBssT)) {
			return false;
		}
		BindingAutoLinkBssT other = (BindingAutoLinkBssT) o;
	
		return this.pk.equals(other.pk);
	}

	@Override
	public int hashCode() {
		return this.pk.hashCode()
			;
	}
}