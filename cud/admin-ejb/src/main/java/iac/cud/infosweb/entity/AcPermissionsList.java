package iac.cud.infosweb.entity;

import iac.cud.infosweb.dataitems.BaseItem;

import java.io.Serializable;
import javax.persistence.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Role;

import java.util.List;


/**
 * Сущность Список Привилегий
 * @author bubnov
 *
 */
@Entity
@Table(name="AC_PERMISSIONS_LIST_BSS_T")
@Name("permBean")
@Role(name="permBeanCrt")
 public class AcPermissionsList  extends BaseItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="AC_PERMISSIONS_LIST_BSS_T_IDSRV_GENERATOR", sequenceName="AC_PERMISSIONS_BSS_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="AC_PERMISSIONS_LIST_BSS_T_IDSRV_GENERATOR")
	@Column(name="ID_SRV")
	private Long idPerm;

	@Column(name="NOTE")
	private String permDescr;

	@Column(name="FULL_")
	private String permName;

	@Column(name="SORT_ODER")
	private Long orderNum;

	@OneToMany(mappedBy="acPermissionsList")
	private List<AcLinkRoleAppPagePrmssn> acLinkRoleAppPagePrmssns;

    public AcPermissionsList() {
    }

    public Long getBaseId() {
 	   return this.idPerm;
 	} 
    
	public Long getIdPerm() {
		return this.idPerm;
	}

	public void setIdPerm(Long idPerm) {
		this.idPerm = idPerm;
	}

	public String getPermDescr() {
		return this.permDescr;
	}

	public void setPermDescr(String permDescr) {
		this.permDescr = permDescr;
	}

	public String getPermName() {
		return this.permName;
	}

	public void setPermName(String permName) {
		this.permName = permName;
	}
	
	public Long getOrderNum() {
		return this.orderNum;
	}
	public void setOrderNum(Long orderNum) {
		this.orderNum = orderNum;
	}


	public List<AcLinkRoleAppPagePrmssn> getAcLinkRoleAppPagePrmssns() {
		return this.acLinkRoleAppPagePrmssns;
	}

	public void setAcLinkRoleAppPagePrmssns(List<AcLinkRoleAppPagePrmssn> acLinkRoleAppPagePrmssns) {
		this.acLinkRoleAppPagePrmssns = acLinkRoleAppPagePrmssns;
	}
	
}