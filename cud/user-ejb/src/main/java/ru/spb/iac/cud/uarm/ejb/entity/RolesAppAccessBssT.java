package ru.spb.iac.cud.uarm.ejb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the ROLES_APP_ACCESS_BSS_T database table.
 * 
 */
@Entity
@Table(name="ROLES_APP_ACCESS_BSS_T")
 public class RolesAppAccessBssT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="ROLES_APP_ACCESS_BSS_T_IDSRV_GENERATOR", sequenceName="ROLES_APP_ACCESS_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ROLES_APP_ACCESS_BSS_T_IDSRV_GENERATOR")
	@Column(name="ID_SRV")
	private Long idSrv;

	@Column(name="CODE_ROLE")
	private String codeRole;

	@ManyToOne
	@JoinColumn(name="UP_ROLE", insertable=false, updatable=false)
	private AcRolesBssT acRolesBssT;

	@Column(name="UP_ROLE")
	private Long acRolesBssTLong;
	
	@ManyToOne
	@JoinColumn(name="UP_APP_ACCESS")
	private JournAppAccessBssT journAppAccessBssT;

	public RolesAppAccessBssT() {
	}

	public Long getIdSrv() {
		return this.idSrv;
	}

	public void setIdSrv(Long idSrv) {
		this.idSrv = idSrv;
	}

	public String getCodeRole() {
		return this.codeRole;
	}

	public void setCodeRole(String codeRole) {
		this.codeRole = codeRole;
	}

	public AcRolesBssT getAcRolesBssT() {
		return this.acRolesBssT;
	}

	public void setAcRolesBssT(AcRolesBssT acRolesBssT) {
		this.acRolesBssT = acRolesBssT;
	}

	public JournAppAccessBssT getJournAppAccessBssT() {
		return this.journAppAccessBssT;
	}

	public void setJournAppAccessBssT(JournAppAccessBssT journAppAccessBssT) {
		this.journAppAccessBssT = journAppAccessBssT;
	}

	public Long getAcRolesBssTLong() {
		return acRolesBssTLong;
	}

	public void setAcRolesBssTLong(Long acRolesBssTLong) {
		this.acRolesBssTLong = acRolesBssTLong;
	}

}