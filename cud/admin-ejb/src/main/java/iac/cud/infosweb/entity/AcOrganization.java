package iac.cud.infosweb.entity;

import iac.cud.infosweb.dataitems.BaseItem;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Role;


/**
 * Сущность Организация
 * @author bubnov
 *
 */
@Entity
@Table(name="ORG_BSS_T")
@Name("orgBean")
@Role(name="orgBeanCrt")
 public class AcOrganization extends BaseItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID_SRV")
	private Long idOrg;

	@Column(name="FIO")
	private String contactEmployeeFio;

	@Column(name="PHONE")
	private String contactEmployeePhone;

	@Column(name="POSITION")
	private String contactEmployeePosition;

    @Temporal(TemporalType.TIMESTAMP)
	private Date created;

    @Temporal(TemporalType.TIMESTAMP)
	private Date modified;

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
	
	@Column(name="FULL_")
	private String fullName;

	@Column(name="SHORT")
	private String shortName;

	@ManyToOne
	@JoinColumn(name="UP_TOR",insertable=false,updatable=false)
	private AcLegalEntityType acLegalEntityType2;
    
	@Column(name="UP_TOR")
    private Long acLegalEntityType;
    
	@Column(name="IS_EXTERNAL")
    private Long isExternal;
	

	@Transient
	private String isExternalValue;

	@Transient
	private String letValue;
	
	@Transient
	private String crtUserName;
	    
	@Transient
	private String updUserName;
	
    public AcOrganization() {
    }

    public Long getBaseId() {
 	   return this.idOrg;
 	} 
    
	public Long getIdOrg() {
		return this.idOrg;
	}

	public void setIdOrg(Long idOrg) {
		this.idOrg = idOrg;
	}

	public String getContactEmployeeFio() {
		return this.contactEmployeeFio;
	}

	public void setContactEmployeeFio(String contactEmployeeFio) {
		this.contactEmployeeFio = contactEmployeeFio;
	}

	public String getContactEmployeePhone() {
		return this.contactEmployeePhone;
	}

	public void setContactEmployeePhone(String contactEmployeePhone) {
		this.contactEmployeePhone = contactEmployeePhone;
	}

	public String getContactEmployeePosition() {
		return this.contactEmployeePosition;
	}

	public void setContactEmployeePosition(String contactEmployeePosition) {
		this.contactEmployeePosition = contactEmployeePosition;
	}

	public Date getCreated() {
		return this.created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return this.modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	
	public AcUser getCrtUser() {
		return this.crtUser;
	}
	public void setCrtUser(AcUser crtUser) {
		this.crtUser = crtUser;
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
	
	public String getFullName() {
		return this.fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getShortName() {
		return this.shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Long getAcLegalEntityType() {
		return this.acLegalEntityType;
	}

	public void setAcLegalEntityType(Long acLegalEntityType) {
		this.acLegalEntityType = acLegalEntityType;
	}
	public AcLegalEntityType getAcLegalEntityType2() {
		return this.acLegalEntityType2;
	}

	public void setAcLegalEntityType2(AcLegalEntityType acLegalEntityType2) {
		this.acLegalEntityType2 = acLegalEntityType2;
	}
	
	public Long getIsExternal() {
		return this.isExternal;
	}
	public void setIsExternal(Long isExternal) {
		this.isExternal = isExternal;
	}
	
	public String getIsExternalValue() {
		if(this.isExternalValue==null&&this.isExternal!=null){
			if(this.isExternal.equals(new Long(0))){
			  isExternalValue="Оператор";
			}else {
			  isExternalValue="Внешняя";	
			}
		}
		return this.isExternalValue;
	}
	
	public String getLetValue() {
		if (this.letValue==null) {
			if (this.isExternal!=null){
		 		letValue=acLegalEntityType2.getName();
		 	}
		}
		return this.letValue;
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
}