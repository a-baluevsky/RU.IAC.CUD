package iac.cud.infosweb.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;
import java.util.Set;


/**
 * The persistent class for the ACTIONS_BSS_T database table.
 * 
 */
@Entity
@Table(name="ACTIONS_BSS_T")
 public class ActionsBssT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="ACTIONS_BSS_T_IDSRV_GENERATOR", sequenceName="ACTIONS_BSS_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ACTIONS_BSS_T_IDSRV_GENERATOR")
	@Column(name="ID_SRV")
	private Long idSrv;

	private String abridgment;

    @Temporal( TemporalType.DATE)
	private Date created;

	private Long creator;

	private String descriptions;

	@Column(name="FULL_")
	private String full;

	@Column(name="SHORT")
	private String short_;

	@Column(name="SIGN_OBJECT")
	private String sign;

    @ManyToOne
	@JoinColumn(name="UP_IS")
	private AcApplication acIsBssT;

    @ManyToOne
	@JoinColumn(name="UP_IS", insertable=false, updatable=false)
	private AcApplication acIsBssT2;
    
	@OneToMany(mappedBy="actionsBssT")
	private Set<ActionsLogKnlT> actionsLogKnlTs;

    public ActionsBssT() {
    }

	public Long getIdSrv() {
		return this.idSrv;
	}

	public void setIdSrv(Long idSrv) {
		this.idSrv = idSrv;
	}

	public String getAbridgment() {
		return this.abridgment;
	}

	public void setAbridgment(String abridgment) {
		this.abridgment = abridgment;
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

	public String getDescriptions() {
		return this.descriptions;
	}

	public void setDescriptions(String descriptions) {
		this.descriptions = descriptions;
	}

	public String getFull() {
		return this.full;
	}

	public void setFull(String full) {
		this.full = full;
	}

	public String getShort_() {
		return this.short_;
	}

	public void setShort_(String short_) {
		this.short_ = short_;
	}

	public String getSign() {
		return this.sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public AcApplication getAcIsBssT() {
		return this.acIsBssT;
	}
	public void setAcIsBssT(AcApplication acIsBssT) {
		this.acIsBssT = acIsBssT;
	}
	
	public AcApplication getAcIsBssT2() {
		return this.acIsBssT;
	}
	public void setAcIsBssT2(AcApplication acIsBssT2) {
		this.acIsBssT2 = acIsBssT2;
	}
	
	public Set<ActionsLogKnlT> getActionsLogKnlTs() {
		return this.actionsLogKnlTs;
	}

	public void setActionsLogKnlTs(Set<ActionsLogKnlT> actionsLogKnlTs) {
		this.actionsLogKnlTs = actionsLogKnlTs;
	}
	
}