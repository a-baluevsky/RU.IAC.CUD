package iac.cud.infosweb.entity;

import iac.cud.infosweb.dataitems.BaseItem;

import java.io.Serializable;

import javax.persistence.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Role;

import java.util.Date;
import java.util.List;


/**
 * Сущность Консоль
 * @author bubnov
 *
 */
@Entity
@Table(name="AC_IS_BSS_T")
@Name("armBean")
@Role(name="armBeanCrt")
 public class AcApplication extends BaseItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="AC_APPLICATIONS_IDAPP_GENERATOR", sequenceName="AC_IS_BSS_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="AC_APPLICATIONS_IDAPP_GENERATOR")
	@Column(name="ID_SRV")
	private Long idArm;

	@Column(name="SIGN_OBJECT")
	private String code;
	
	@Column(name="FULL_")
	private String name;

	private String description;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name="CREATOR")
	private Long creator;

	private String links;
	 

	@OneToMany(mappedBy="acApplication2", cascade={CascadeType.REMOVE})
	private List<AcAppPage> acAppPages;
	
	@OneToMany(mappedBy="acApplication2", cascade=CascadeType.REMOVE)
	private List<AcRole> acRoles;
	
	@Transient
	private List<AcRole> rolList;
	
	@OneToMany(mappedBy="acIsBssT", cascade=CascadeType.REMOVE)
	private List<AcSubsystemCertBssT> acSubsystemCertBssTs;
	
	
	@OneToMany(mappedBy="acIsBssT", cascade=CascadeType.REMOVE)
	private List<LinkGroupSysSysKnlT> linkGroupSysSysKnlTs;
	
	//важно CascadeType!!!
	@OneToMany(mappedBy="acIsBssT", cascade={CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
	private List<LinkAdminUserSys> linkAdminUserSys;

	
	@Transient
	private Boolean usrChecked=false;
	
	public AcApplication() {
    }
    
	public Long getBaseId() {
	   return this.idArm;
	} 

    public List<AcRole> getRolList() {
    	 return this.rolList;
	}
	public void setRolList(List<AcRole> rolList) {
		this.rolList=rolList;
	}
	
	public Long getIdArm() {
		return this.idArm;
	}
	public void setIdArm(Long idArm) {
		this.idArm = idArm;
	}
	
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
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

	
	public List<AcAppPage> getAcAppPages() {
		return this.acAppPages;
	}
	public void setAcAppPages(List<AcAppPage> acAppPages) {
		this.acAppPages = acAppPages;
	}
	
	public List<AcRole> getAcRoles() {
	    return this.acRoles;
	}
	public void setAcRoles(List<AcRole> acRoles) {
		this.acRoles = acRoles;
	}
	
	
	
	public List<AcSubsystemCertBssT> getAcSubsystemCertBssTs() {
		return this.acSubsystemCertBssTs;
	}
	public void setAcSubsystemCertBssTs(List<AcSubsystemCertBssT> acSubsystemCertBssTs) {
		this.acSubsystemCertBssTs = acSubsystemCertBssTs;
	}

	public List<LinkGroupSysSysKnlT> getLinkGroupSysSysKnlTs() {
		return this.linkGroupSysSysKnlTs;
	}
	public void setLinkGroupSysSysKnlTs(List<LinkGroupSysSysKnlT> linkGroupSysSysKnlTs) {
		this.linkGroupSysSysKnlTs = linkGroupSysSysKnlTs;
	}
	
	public Boolean getUsrChecked(){
		return this.usrChecked;
	}
	
	public void setUsrChecked(Boolean usrChecked){
		this.usrChecked=usrChecked;
	}
	
	public List<LinkAdminUserSys> getLinkAdminUserSys() {
		return this.linkAdminUserSys;
	}

	public void setLinkAdminUserSys(List<LinkAdminUserSys> linkAdminUserSys) {
		this.linkAdminUserSys = linkAdminUserSys;
	}

	public String getLinks() {
		return links;
	}

	public void setLinks(String links) {
		this.links = links;
	}

}