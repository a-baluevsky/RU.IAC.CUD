package iac.cud.infosweb.entity;

import iac.cud.infosweb.dataitems.BaseItem;

import java.io.Serializable;

import javax.persistence.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Role;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the GROUP_USERS_KNL_T database table.
 * 
 */
@Entity
@Table(name="GROUP_USERS_KNL_T")
@Name("ugroupBean")
@Role(name="ugroupBeanCrt")
 public class GroupUsersKnlT extends BaseItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="GROUP_USERS_ID_GENERATOR", sequenceName="GROUP_USERS_KNL_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="GROUP_USERS_ID_GENERATOR")
	@Column(name="ID_SRV")
	private Long idSrv;

	private String description;

	@Column(name="FULL_")
	private String full;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	private Long creator;
	
	@Column(name="MODIFICATOR")
	private Long modificator;

	@Column(name="SIGN_OBJECT")
	private String signObject;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date modified;
	    
	@OneToMany(mappedBy="groupUsersKnlT", cascade={CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
	private List<LinkGroupUsersRolesKnlT> linkGroupUsersRolesKnlTs;

	@OneToMany(mappedBy="groupUsersKnlT",  cascade={CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
	private List<LinkGroupUsersUsersKnlT> linkGroupUsersUsersKnlTs;

	@Transient
	private List<AcApplication> armList;
	
	@Transient
	private Boolean usrChecked=false;
	
	@Transient
	private Boolean isAllowedSys=true;
	
    public GroupUsersKnlT() {
    }

    public Long getBaseId() {
  	   return this.idSrv;
  	}
    
	public Long getIdSrv() {
		return this.idSrv;
	}

	public void setIdSrv(Long idSrv) {
		this.idSrv = idSrv;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFull() {
		return this.full;
	}

	public void setFull(String full) {
		this.full = full;
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
	
	public Date getModified() {
		return this.modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	
	public Long getModificator() {
		return this.modificator;
	}
	public void setModificator(Long modificator) {
		this.modificator = modificator;
	}
	
	public List<LinkGroupUsersRolesKnlT> getLinkGroupUsersRolesKnlTs() {
		return this.linkGroupUsersRolesKnlTs;
	}

	public void setLinkGroupUsersRolesKnlTs(List<LinkGroupUsersRolesKnlT> linkGroupUsersRolesKnlTs) {
		this.linkGroupUsersRolesKnlTs = linkGroupUsersRolesKnlTs;
	}
	
	public List<LinkGroupUsersUsersKnlT> getLinkGroupUsersUsersKnlTs() {
		return this.linkGroupUsersUsersKnlTs;
	}

	public void setLinkGroupUsersUsersKnlTs(List<LinkGroupUsersUsersKnlT> linkGroupUsersUsersKnlTs) {
		this.linkGroupUsersUsersKnlTs = linkGroupUsersUsersKnlTs;
	}
	
	public List<AcApplication> getArmList() {
	    return this.armList;
	}
	public void setArmList(List<AcApplication> armList) {
		this.armList = armList;
	}
	
	public Boolean getUsrChecked(){
		return this.usrChecked;
	}
	
	public void setUsrChecked(Boolean usrChecked){
		this.usrChecked=usrChecked;
	}

	public Boolean getIsAllowedSys() {
		return isAllowedSys;
	}

	public void setIsAllowedSys(Boolean isAllowedSys) {
		this.isAllowedSys = isAllowedSys;
	}

	public String getSignObject() {
		return signObject;
	}

	public void setSignObject(String signObject) {
		this.signObject = signObject;
	}
	
}