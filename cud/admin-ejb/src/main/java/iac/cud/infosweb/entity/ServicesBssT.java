package iac.cud.infosweb.entity;


import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the SERVICES_BSS_T database table.
 * 
 */
@Entity
@Table(name="SERVICES_BSS_T")
 public class ServicesBssT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID_SRV")
	private Long idSrv;

    @Temporal( TemporalType.DATE)
	private Date created;

	private Long creator;

	@Column(name="FULL_")
	private String full;

	@Column(name="SIGN_OBJECT")
	private Long signObject;

	@OneToMany(mappedBy="servicesBssT")
	private List<ServicesLogKnlT> servicesLogKnlTs;

	@OneToMany(mappedBy="servicesBssT")
	private List<SettingsKnlT> settingsKnlTs;
	
    public ServicesBssT() {
    }

	public Long getIdSrv() {
		return this.idSrv;
	}

	public void setIdSrv(Long idSrv) {
		this.idSrv = idSrv;
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

	public String getFull() {
		return this.full;
	}

	public void setFull(String full) {
		this.full = full;
	}

	public Long getSignObject() {
		return this.signObject;
	}

	public void setSignObject(Long signObject) {
		this.signObject = signObject;
	}

	public List<ServicesLogKnlT> getServicesLogKnlTs() {
		return this.servicesLogKnlTs;
	}

	public void setServicesLogKnlTs(List<ServicesLogKnlT> servicesLogKnlTs) {
		this.servicesLogKnlTs = servicesLogKnlTs;
	}
	
	public List<SettingsKnlT> getSettingsKnlTs() {
		return this.settingsKnlTs;
	}

	public void setSettingsKnlTs(List<SettingsKnlT> settingsKnlTs) {
		this.settingsKnlTs = settingsKnlTs;
	}
}