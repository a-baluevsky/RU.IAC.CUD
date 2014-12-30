package iac.cud.infosweb.entity;

import iac.cud.infosweb.dataitems.BaseItem;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the SERVICES_LOG_KNL_T database table.
 * 
 */
@Entity
@Table(name="SERVICES_LOG_KNL_T")
 public class ServicesLogKnlT  extends BaseItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID_SRV")
	private Long idSrv;

    @Temporal( TemporalType.DATE)
	private Date created;

    @Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_ACTION")
	private Date dateAction;

	@Column(name="INPUT_PARAM")
	private String inputParam;

	@Column(name="IP_ADDRESS")
	private String ipAddress;

    @Temporal( TemporalType.DATE)
	private Date modified;

	@Column(name="RESULT_VALUE")
	private String resultValue;

	 @ManyToOne
	@JoinColumn(name="UP_SERVICES")
	private ServicesBssT servicesBssT;

    @ManyToOne
	@JoinColumn(name="UP_USERS")
	private AcUser acUsersKnlT;
    
    @Transient
  	private String servName;
    
    @Transient
  	private String userFio;
    
    @Transient
  	private String createdValue;
    
    public ServicesLogKnlT() {
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

	public Date getDateAction() {
		return this.dateAction;
	}

	public void setDateAction(Date dateAction) {
		this.dateAction = dateAction;
	}

	public String getInputParam() {
		return this.inputParam;
	}

	public void setInputParam(String inputParam) {
		this.inputParam = inputParam;
	}

	public String getIpAddress() {
		return this.ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Date getModified() {
		return this.modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public String getResultValue() {
		return this.resultValue;
	}

	public void setResultValue(String resultValue) {
		this.resultValue = resultValue;
	}

	public ServicesBssT getServicesBssT() {
		return this.servicesBssT;
	}

	public void setServicesBssT(ServicesBssT servicesBssT) {
		this.servicesBssT = servicesBssT;
	}
	
	public String getServName() {
		return this.servName;
	}

	public void setServName(String servName) {
		this.servName = servName;
	}
	
	public AcUser getAcUsersKnlT() {
		return this.acUsersKnlT;
	}

	public void setAcUsersKnlT(AcUser acUsersKnlT) {
		this.acUsersKnlT = acUsersKnlT;
	}
	
    public String getUserFio() {
		return this.userFio;
	}

	public void setUserFio(String userFio) {
		this.userFio = userFio;
	}
	
	public String getCreatedValue() {
		return this.createdValue;
	}
	public void setCreatedValue(String createdValue) {
		this.createdValue = createdValue;
	}

}