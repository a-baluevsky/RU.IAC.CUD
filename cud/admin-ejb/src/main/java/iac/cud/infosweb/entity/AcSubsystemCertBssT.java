package iac.cud.infosweb.entity;

import iac.cud.infosweb.dataitems.BaseItem;

import java.io.Serializable;
import javax.persistence.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Role;

import java.util.Date;


/**
 * The persistent class for the AC_SUBSYSTEM_CERT_BSS_T database table.
 * 
 */
@Entity
@Table(name="AC_SUBSYSTEM_CERT_BSS_T")
@Name("armSubBean")
@Role(name="armSubBeanCrt")
 public class AcSubsystemCertBssT extends BaseItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="AC_SUBSYSTEM_CERT_BSS_T_IDSRV_GENERATOR", sequenceName="AC_SUBSYSTEM_CERT_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="AC_SUBSYSTEM_CERT_BSS_T_IDSRV_GENERATOR")
	@Column(name="ID_SRV")
	private Long idSrv;

	@Column(name="CERT_ALIAS")
	private String certAlias;

	@Lob
	@Column(name="CERT_DATE")
	private String certDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	private Long creator;

	private Long modificator;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modified;

	@Column(name="SUBSYSTEM_CODE")
	private String subsystemCode;

	@Column(name="SUBSYSTEM_NAME")
	private String subsystemName;

	@ManyToOne
	@JoinColumn(name="UP_IS", insertable=false, updatable=false)
	private AcApplication acIsBssT;

	@Column(name="UP_IS")
	private Long acIsBssTLong;
	
	@Transient
	private String armName;
	
	@Transient
	private Boolean isAllowedSys=true;
	
	public AcSubsystemCertBssT() {
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

	public String getCertAlias() {
		return this.certAlias;
	}

	public void setCertAlias(String certAlias) {
		this.certAlias = certAlias;
	}

	public String getCertDate() {
		return this.certDate;
	}

	public void setCertDate(String certDate) {
		this.certDate = certDate;
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

	public String getSubsystemCode() {
		return this.subsystemCode;
	}

	public void setSubsystemCode(String subsystemCode) {
		this.subsystemCode = subsystemCode;
	}

	public String getSubsystemName() {
		return this.subsystemName;
	}

	public void setSubsystemName(String subsystemName) {
		this.subsystemName = subsystemName;
	}

	public AcApplication getAcIsBssT() {
		return this.acIsBssT;
	}

	public void setAcIsBssT(AcApplication acIsBssT) {
		this.acIsBssT = acIsBssT;
	}
	
	public Long getAcIsBssTLong() {
		return acIsBssTLong;
	}
    public void setAcIsBssTLong(Long acIsBssTLong) {
		this.acIsBssTLong = acIsBssTLong;
	}

    public String getArmName() {
		if(this.armName==null){
			if(this.acIsBssT!=null){
			  this.armName = this.acIsBssT.getName();
			}
		}
		return this.armName;
	}
	public void setArmName(String armName) {
		this.armName = armName;
	}

	public Boolean getIsAllowedSys() {
		return isAllowedSys;
	}

	public void setIsAllowedSys(Boolean isAllowedSys) {
		this.isAllowedSys = isAllowedSys;
	}

}