package iac.cud.infosweb.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Role;

/**
 * Сущность Рубрика
 * @author bubnov
 *
 */
@Entity
@Table(name="AC_APP_DOMAINS_BSS_T")
@Name("acResBean")
@Role(name="acResBeanCrt")
 public class AcAppPage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="AC_APP_PAGES_IDPAGE_GENERATOR", sequenceName="AC_APP_DOMAINS_BSS_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="AC_APP_PAGES_IDPAGE_GENERATOR")
	@Column(name="ID_SRV")
	private Long idRes;

	@ManyToOne()
	@JoinColumn(name="UP", insertable=false,updatable=false)
	private AcAppPage idParent;
	
	@Column(name="UP")
	private Long idParent2;
	
	@Column(name="PAGE_NAME")
	private String pageName;
	
    @Column(name="PAGE_CODE")
	private String pageCode;
	
	//осознанно убрано CascadeType.REFRESH, так как это важно для resManager:delRes()
	@OneToMany(mappedBy="idParent", cascade={CascadeType.REMOVE})
	@OrderBy("orderNum")
    private Set<AcAppPage> idResCollection;
	
	@ManyToOne
	@JoinColumn(name="UP_IS",insertable=false,updatable=false)
	private AcApplication acApplication2;
   
    @Column(name="UP_IS")
    private Long acApplication;

	@OneToMany(mappedBy="acAppPage", cascade=CascadeType.REMOVE)
	private Set<AcLinkRoleAppPagePrmssn> acLinkRoleAppPagePrmssns;

	@Column(name="IS_VISIBLE")
	private Long visible;
	
	@Column(name="ORDER_NUM")
	private Long orderNum;
	
	@Transient
	private List<Long> permList;
	
	@Transient
	private List<AcAppPage> idResCollectionSet;
	
	@Transient
	private List<AcAppPage> idResCollectionNavigation;
	
	@Transient
	private Boolean visibleBoolean;
	
	@Transient
	private String fullPageName;
	
	@Transient
	private String visibleValue;
	
	public AcAppPage() {
    }
    public List<Long> getPermList(){
     	return this.permList;
    }
    public void setPermList(List<Long>permList){
    	this.permList=permList;
    }
		
	public Long getIdRes() {
		return this.idRes;
	}
	public void setIdRes(Long idRes) {
		this.idRes = idRes;
	}
	
	public AcAppPage getIdParent() {
		return this.idParent;
	}
	public void setIdParent(AcAppPage idParent) {
		this.idParent = idParent;
	}
	
	public String getPageName() {
		return this.pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public AcApplication getAcApplication2() {
		return this.acApplication2;
	}

	public void setAcApplication2(AcApplication acApplication2) {
		this.acApplication2 = acApplication2;
	}
	
	public Long getAcApplication() {
		return this.acApplication;
	}

	public void setAcApplication(Long acApplication) {
		this.acApplication = acApplication;
	}
	
	public Set<AcLinkRoleAppPagePrmssn> getAcLinkRoleAppPagePrmssns() {
		return this.acLinkRoleAppPagePrmssns;
	}
	public void setAcLinkRoleAppPagePrmssns(Set<AcLinkRoleAppPagePrmssn> acLinkRoleAppPagePrmssns) {
		this.acLinkRoleAppPagePrmssns = acLinkRoleAppPagePrmssns;
	}
	
	public Set<AcAppPage> getIdResCollection() {
		return this.idResCollection;
	}
	public void setIdResCollection(Set<AcAppPage> idResCollection) {
		this.idResCollection = idResCollection;
	}
	
	public String getPageCode() {
		return this.pageCode;
	}
	public void setPageCode(String pageCode) {
		this.pageCode = pageCode;
	}
	
	public Long getOrderNum() {
		return this.orderNum;
	}
	public void setOrderNum(Long orderNum) {
		this.orderNum = orderNum;
	}
	
	public Long getIdParent2() {
		return this.idParent2;
	}
	public void setIdParent2(Long idParent2) {
		this.idParent2 = idParent2;
	}
	public List<AcAppPage> getIdResCollectionSet() {
		if(idResCollectionSet==null){
			idResCollectionSet=new ArrayList<AcAppPage>(idResCollection);
		}
		  return idResCollectionSet;
	}
	public List<AcAppPage> getIdResCollectionNavigation() {
		if(idResCollectionNavigation==null){
		List <AcAppPage> laap =new ArrayList<AcAppPage>(idResCollection);
		idResCollectionNavigation=new ArrayList<AcAppPage>();
		
		for(AcAppPage aap :laap){
			if(aap.visible!=null&&aap.visible.intValue()==1){
				idResCollectionNavigation.add(aap);
			}
		}
		}
		return idResCollectionNavigation;
	}
	public Long getVisible() {
		return this.visible;
	}
	public void setVisible(Long visible) {
		this.visible = visible;
	}
	public Boolean getVisibleBoolean() {
		if(this.visibleBoolean==null){
			if(this.visible!=null&&this.visible.equals(new Long(1))){
				this.visibleBoolean=true;
			}else{
				this.visibleBoolean=false;
			}
		}
		return this.visibleBoolean;
	}
	public void setVisibleBoolean(Boolean visibleBoolean) {
		this.visibleBoolean = visibleBoolean;
	}
	public String getFullPageName() {
		return this.fullPageName;
	}
	public void setFullPageName(String fullPageName) {
		this.fullPageName = fullPageName;
	}

	public String getVisibleValue() {
		if(this.visibleValue==null){
			if(this.visible!=null){
				this.visibleValue=(this.visible.intValue()==1?"да":"нет");
			}
		}
		return this.visibleValue;
	}
	
}