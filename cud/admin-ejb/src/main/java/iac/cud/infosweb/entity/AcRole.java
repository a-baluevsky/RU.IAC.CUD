package iac.cud.infosweb.entity;

import iac.cud.infosweb.dataitems.BaseItem;

import java.io.Serializable;
import javax.persistence.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Role;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Сущность Роль
 * @author bubnov
 *
 */
@Entity
@Table(name="AC_ROLES_BSS_T")
@Name("rolBean")
@Role(name="rolBeanCrt")
 public class AcRole extends BaseItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="AC_ROLES_IDROLE_GENERATOR", sequenceName="AC_ROLES_BSS_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="AC_ROLES_IDROLE_GENERATOR")
	@Column(name="ID_SRV")
	private Long idRol;

    @Temporal(TemporalType.TIMESTAMP)
	private Date created;

    @ManyToOne
   	@JoinColumn(name="CREATOR", insertable=false, updatable=false)
   	private AcUser crtUser;
    
    @ManyToOne
   	@JoinColumn(name="MODIFICATOR", insertable=false, updatable=false)
   	private AcUser updUser;

    @Column(name="CREATOR")
    private Long creator;

    @Column(name="MODIFICATOR")
    private Long modificator;

    @Temporal(TemporalType.TIMESTAMP)
	private Date modified;

	@Column(name="DESCRIPTION")
	private String roleDescription;

	@Column(name="FULL_")
	private String roleTitle;

	@Column(name="SIGN_OBJECT")
	private String sign;
	
	@OneToMany(mappedBy="acRole", cascade={CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
	private Set<AcLinkRoleAppPagePrmssn> acLinkRoleAppPagePrmssns;

	@OneToMany(mappedBy="acRole", cascade={CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
	private List<AcLinkUserToRoleToRaion> acLinkUserToRoleToRaions;

	@OneToMany(mappedBy="acRolesBssT", cascade=CascadeType.REMOVE)
	private List<LinkGroupUsersRolesKnlT> linkGroupUsersRolesKnlTs;
	
	@ManyToOne
	@JoinColumn(name="UP_IS", insertable=false, updatable=false)
	private AcApplication acApplication2;

	@Column(name="UP_IS")
	private Long acApplication;
	
	@Transient
	private Boolean usrChecked=false;
	
	@Transient
	private String armName;
	 
	@Transient
	private String crtUserName;
	    
	@Transient
	private String updUserName;
	
	@Transient
	private Long isCudRole=0L;
	
	@Transient
	private Long isSysAdminRole=0L;
	
	public AcRole() {
    }
	
	public Long getBaseId() {
	   return this.idRol;
	} 
	public Long getIdRol() {
		return this.idRol;
	}
	public void setIdRol(Long idRol) {
		this.idRol = idRol;
	}

	public Date getCreated() {
		return this.created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	
	public AcUser getCrtUser() {
		return this.crtUser;
	}
	public void setCrtUser(AcUser crtUser) {
		this.crtUser = crtUser;
	}
	
	public AcUser getUpdUser() {
		return this.updUser;
	}
	public void setUpdUser(AcUser updUser) {
		this.updUser = updUser;
	}
	
	public Long getCreator() {
		return this.creator;
	}
	public void setCreator(Long creator) {
		this.creator = creator;
	}

	public Long getModificator() {
		return this.modificator;
	}
	public void setModificator(Long modificator) {
		this.modificator = modificator;
	}

	public Date getModified() {
		return this.modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}

	public String getRoleDescription() {
		return this.roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}

	public String getRoleTitle() {
		return this.roleTitle;
	}

	public void setRoleTitle(String roleTitle) {
		this.roleTitle = roleTitle;
	}

	public String getSign() {
		return this.sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
	public Set<AcLinkRoleAppPagePrmssn> getAcLinkRoleAppPagePrmssns() {
		return this.acLinkRoleAppPagePrmssns;
	}

	public void setAcLinkRoleAppPagePrmssns(Set<AcLinkRoleAppPagePrmssn> acLinkRoleAppPagePrmssns) {
		this.acLinkRoleAppPagePrmssns = acLinkRoleAppPagePrmssns;
	}
	
	public List<AcLinkUserToRoleToRaion> getAcLinkUserToRoleToRaions() {
		return this.acLinkUserToRoleToRaions;
	}

	public void setAcLinkUserToRoleToRaions(List<AcLinkUserToRoleToRaion> acLinkUserToRoleToRaions) {
		this.acLinkUserToRoleToRaions = acLinkUserToRoleToRaions;
	}
	public Boolean getUsrChecked(){
		return this.usrChecked;
	}
	public void setUsrChecked(Boolean usrChecked){
		 
		this.usrChecked=usrChecked;
	}
	public Long getAcApplication() {
		return this.acApplication;
	}
	public void setAcApplication(Long acApplication) {
		this.acApplication = acApplication;
	}
	public AcApplication getAcApplication2() {
		return this.acApplication2;
	}
	public void setAcApplication2(AcApplication acApplication2) {
		this.acApplication2 = acApplication2;
	}
	public String getArmName() {
		if(this.armName==null){
			if(this.acApplication2!=null){
			  this.armName = this.acApplication2.getName();
			}
		}
		return this.armName;
	}
	public void setArmName(String armName) {
		this.armName = armName;
	}
	public String getCrtUserName() {
		if(this.crtUserName==null){
			if(this.crtUser!=null){
			  this.crtUserName = this.crtUser.getFio();
			}
		}
		return this.crtUserName;
	}
	public void setCrtUserName(String crtUserName) {
		this.crtUserName = crtUserName;
	}
	
	public String getUpdUserName() {
		if(this.updUserName==null){
			if(this.updUser!=null){
			  this.updUserName = this.updUser.getFio();
			}
		}
		return this.updUserName;
	}
	public void setUpdUserName(String updUserName) {
		this.updUserName = updUserName;
	}
	
	public Long getIsCudRole() {
		return this.isCudRole;
	}
	public void setIsCudRole(Long isCudRole) {
		this.isCudRole = isCudRole;
	}
	
	public Long getIsSysAdminRole() {
		return this.isSysAdminRole;
	}
	public void setIsSysAdminRole(Long isSysAdminRole) {
		this.isSysAdminRole = isSysAdminRole;
	}

	public List<LinkGroupUsersRolesKnlT> getLinkGroupUsersRolesKnlTs() {
		return this.linkGroupUsersRolesKnlTs;
	}
    public void setLinkGroupUsersRolesKnlTs(List<LinkGroupUsersRolesKnlT> linkGroupUsersRolesKnlTs) {
		this.linkGroupUsersRolesKnlTs = linkGroupUsersRolesKnlTs;
	}
}