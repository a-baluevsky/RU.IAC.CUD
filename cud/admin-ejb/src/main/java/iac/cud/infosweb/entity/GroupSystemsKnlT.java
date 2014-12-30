package iac.cud.infosweb.entity;

import iac.cud.infosweb.dataitems.BaseItem;

import java.io.Serializable;
import javax.persistence.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Role;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the GROUP_SYSTEMS_KNL_T database table.
 * 
 */
@Entity
@Table(name="GROUP_SYSTEMS_KNL_T")
@Name("armGroupBean")
@Role(name="armGroupBeanCrt")
 public class GroupSystemsKnlT extends BaseItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="GROUP_SYSTEMS_ID_GENERATOR", sequenceName="GROUP_SYSTEMS_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="GROUP_SYSTEMS_ID_GENERATOR")
	@Column(name="ID_SRV")
	private Long idSrv;

	@Lob
	@Column(name="CERT_DATA")
	private String certData;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	private Long creator;

	@Column(name="GROUP_CODE")
	private String groupCode;

	@Column(name="GROUP_NAME")
	private String groupName;

	private Long modificator;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modified;

	private String description;
	
	@OneToMany(mappedBy="groupSystemsKnlT", cascade={CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
	private List<LinkGroupSysSysKnlT> linkGroupSysSysKnlTs;

	@Transient
	private Boolean isAllowedSys=true;
	
	public Long getBaseId() {
		return this.idSrv;
	} 
	
	public GroupSystemsKnlT() {
	}

	public Long getIdSrv() {
		return this.idSrv;
	}

	public void setIdSrv(Long idSrv) {
		this.idSrv = idSrv;
	}

	public String getCertData() {
		return this.certData;
	}

	public void setCertData(String certData) {
		this.certData = certData;
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

	public String getGroupCode() {
		return this.groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
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

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<LinkGroupSysSysKnlT> getLinkGroupSysSysKnlTs() {
		return this.linkGroupSysSysKnlTs;
	}

	public void setLinkGroupSysSysKnlTs(List<LinkGroupSysSysKnlT> linkGroupSysSysKnlTs) {
		this.linkGroupSysSysKnlTs = linkGroupSysSysKnlTs;
	}

	public LinkGroupSysSysKnlT addLinkGroupSysSysKnlT(LinkGroupSysSysKnlT linkGroupSysSysKnlT) {
		getLinkGroupSysSysKnlTs().add(linkGroupSysSysKnlT);
		linkGroupSysSysKnlT.setGroupSystemsKnlT(this);

		return linkGroupSysSysKnlT;
	}

	public LinkGroupSysSysKnlT removeLinkGroupSysSysKnlT(LinkGroupSysSysKnlT linkGroupSysSysKnlT) {
		getLinkGroupSysSysKnlTs().remove(linkGroupSysSysKnlT);
		linkGroupSysSysKnlT.setGroupSystemsKnlT(null);

		return linkGroupSysSysKnlT;
	}

	public Boolean getIsAllowedSys() {
		return isAllowedSys;
	}

	public void setIsAllowedSys(Boolean isAllowedSys) {
		this.isAllowedSys = isAllowedSys;
	}
}