package iac.cud.infosweb.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the JOURN_ISP_LOAD database table.
 * 
 */
@Entity
@Table(name="JOURN_ISP_LOAD")
@NamedQuery(name="JournIspLoad.findAll", query="SELECT j FROM JournIspLoad j")
 public class JournIspLoad implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="JOURN_ISP_LOAD_IDSRV_GENERATOR", sequenceName="JOURN_ISP_LOAD_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="JOURN_ISP_LOAD_IDSRV_GENERATOR")
	@Column(name="ID_SRV")
	private Long idSrv;

	@Column(name="CLASSIF_VERSION")
	private String classifVersion;

	@Temporal(TemporalType.DATE)
	private Date created;

	@Column(name="FILE_NAME")
	private String fileName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LOAD_FINISH")
	private Date loadFinish;

	
	private Long creator;

	
	@Column(name="LOAD_MODE")
	private Long loadMode;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LOAD_START")
	private Date loadStart;

	@Column(name="LOAD_REC_COUNT")
	private Long loadRecCount;
	
	@Column(name="FILE_REC_COUNT")
	private Long fileRecCount;
	
	
	private Long success;
	
	
	@Transient
	private String successValue;
	
	public JournIspLoad() {
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

	public String getClassifVersion() {
		return this.classifVersion;
	}
    public void setClassifVersion(String classifVersion) {
		this.classifVersion = classifVersion;
	}
    
	

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getLoadFinish() {
		return this.loadFinish;
	}

	public void setLoadFinish(Date loadFinish) {
		this.loadFinish = loadFinish;
	}

	public Long getCreator() {
		return this.creator;
	}
    public void setCreator(Long creator) {
		this.creator = creator;
	}
    
	public Long getLoadMode() {
		return this.loadMode;
	}

	public void setLoadMode(Long loadMode) {
		this.loadMode = loadMode;
	}

	public Date getLoadStart() {
		return this.loadStart;
	}

	public void setLoadStart(Date loadStart) {
		this.loadStart = loadStart;
	}

	

	public String getSuccessValue() {
		
		if(this.successValue==null){
			
		  if(this.success==null){
			  this.successValue="Не определён";
		  }else if(this.success.equals(1L)){
			  this.successValue="Успешно";
		  }else if(this.success.equals(0L)){
			  this.successValue="Неудачно";
		  }
		}
		return this.successValue;
	}

	
	public void setSuccessValue(String successValue) {
		this.successValue = successValue;
	}

	public Long getSuccess() {
		return this.success;
	}
   public void setSuccess(Long success) {
		this.success = success;
	}

	

	public Long getFileRecCount() {
		return fileRecCount;
	}

	public void setFileRecCount(Long fileRecCount) {
		this.fileRecCount = fileRecCount;
	}
	
	public Long getLoadRecCount() {
		return loadRecCount;
	}
  public void setLoadRecCount(Long loadRecCount) {
		this.loadRecCount = loadRecCount;
	}
}