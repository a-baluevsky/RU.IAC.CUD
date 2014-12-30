package iac.cud.infosweb.entity;

import iac.cud.infosweb.dataitems.BaseItem;

import java.io.Serializable;
import javax.persistence.*;

import org.jboss.seam.annotations.Name;

import java.util.Date;


/**
 * The persistent class for the SETTINGS_KNL_T database table.
 * 
 */
@Entity
@Table(name="SETTINGS_KNL_T")
@Name("cparBean")
 public class SettingsKnlT extends BaseItem  implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID_SRV")
	private Long idSrv;

    @Temporal( TemporalType.DATE)
	private Date created;

	private Long creator;

	@Column(name="SIGN_OBJECT")
	private String signObject;

	@Column(name="VALUE_PARAM")
	private String valueParam;

	@Column(name="NAME_PARAM")
	private String nameParam;
	
	@Column(name="description")
	private String description;
	
	 @ManyToOne
	@JoinColumn(name="UP_SERVICES")
	private ServicesBssT servicesBssT;

    @Transient
  	private String servName;
    
    public SettingsKnlT() {
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

	public String getSignObject() {
		return this.signObject;
	}

	public void setSignObject(String signObject) {
		this.signObject = signObject;
	}

	public String getValueParam() {
		return this.valueParam;
	}

	public void setValueParam(String valueParam) {
		this.valueParam = valueParam;
	}

	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public ServicesBssT getServicesBssT() {
		return this.servicesBssT;
	}

	public void setServicesBssT(ServicesBssT servicesBssT) {
		this.servicesBssT = servicesBssT;
	}
	
    public String getServName() {
		
		if(this.servName==null&&this.servicesBssT!=null){
			this.servName=this.servicesBssT.getFull();
		}
		
		return this.servName;
	}

	public void setServName(String servName) {
		this.servName = servName;
	}
	
	public String getNameParam() {
		return this.nameParam;
	}
	public void setNameParam(String nameParam) {
		this.nameParam = nameParam;
	}
}