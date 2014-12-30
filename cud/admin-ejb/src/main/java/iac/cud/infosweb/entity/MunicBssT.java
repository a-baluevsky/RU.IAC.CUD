package iac.cud.infosweb.entity;

import iac.cud.infosweb.dataitems.BaseItem;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the MUNIC_BSS_T database table.
 * 
 */
@Entity
@Table(name="MUNIC_BSS_T")
@NamedQuery(name="MunicBssT.findAll", query="SELECT m FROM MunicBssT m")
 public class MunicBssT extends BaseItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID_SRV")
	private Long idSrv;

	private String adate;

	@Temporal(TemporalType.DATE)
	private Date created;

	private String ddate;

	@Column(name="FULL_NAME")
	private String fullName;

	private String nomdescr;

	@Column(name="OKATO_ADOC")
	private String okatoAdoc;

	@Column(name="OKATO_DDOC")
	private String okatoDdoc;

	@Column(name="SHORT_NAME")
	private String shortName;

	@Column(name="SIGN_KZR")
	private String signKzr;

	@Column(name="SIGN_OKATO")
	private String signOkato;

	@Column(name="SIGN_OKTMO")
	private String signOktmo;

	private String status;

	
	public MunicBssT() {
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

	public String getAdate() {
		return this.adate;
	}

	public void setAdate(String adate) {
		this.adate = adate;
	}

	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getDdate() {
		return this.ddate;
	}

	public void setDdate(String ddate) {
		this.ddate = ddate;
	}

	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getNomdescr() {
		return this.nomdescr;
	}

	public void setNomdescr(String nomdescr) {
		this.nomdescr = nomdescr;
	}

	public String getOkatoAdoc() {
		return this.okatoAdoc;
	}

	public void setOkatoAdoc(String okatoAdoc) {
		this.okatoAdoc = okatoAdoc;
	}

	public String getOkatoDdoc() {
		return this.okatoDdoc;
	}

	public void setOkatoDdoc(String okatoDdoc) {
		this.okatoDdoc = okatoDdoc;
	}

	public String getShortName() {
		return this.shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getSignKzr() {
		return this.signKzr;
	}

	public void setSignKzr(String signKzr) {
		this.signKzr = signKzr;
	}

	public String getSignOkato() {
		return this.signOkato;
	}

	public void setSignOkato(String signOkato) {
		this.signOkato = signOkato;
	}

	public String getSignOktmo() {
		return this.signOktmo;
	}

	public void setSignOktmo(String signOktmo) {
		this.signOktmo = signOktmo;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


}