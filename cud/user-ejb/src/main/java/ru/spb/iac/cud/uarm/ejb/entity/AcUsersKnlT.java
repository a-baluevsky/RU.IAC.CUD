package ru.spb.iac.cud.uarm.ejb.entity;


import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the AC_USERS_KNL_T database table.
 * 
 */
@Entity
@Table(name="AC_USERS_KNL_T")
 public class AcUsersKnlT implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="AC_USERS_KNL_T_IDSRV_GENERATOR", sequenceName="AC_USERS_KNL_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="AC_USERS_KNL_T_IDSRV_GENERATOR")
	@Column(name="ID_SRV")
	private Long idSrv;

	private String certificate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	private Long creator;

    private String department;

	@Column(name="E_MAIL")
	private String eMail;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="END_ACCOUNT")
	private Date endAccount;

	private String login;

	private Long modificator;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modified;

	@Column(name="NAME_")
	private String name;

	@Column(name="PASSWORD_")
	private String password;

	private String patronymic;

	private String phone;

	@Column(name="\"POSITION\"")
	private String position;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="START_ACCOUNT")
	private Date startAccount;

	private Long status;

	private String surname;

	@Column(name="UP_BINDING")
	private Long upBinding;

	@Column(name="UP_SIGN")
	private String upSign;

	@Column(name="UP_SIGN_USER")
	private String upSignUser;

	// fetch=FetchType.LAZY - default
	
	@OneToMany(mappedBy="acUsersKnlT1")
	private List<JournAppUserBssT> journAppUserBssTs1;

	@OneToMany(mappedBy="acUsersKnlT2")
	private List<JournAppUserBssT> journAppUserBssTs2;

	@OneToMany(mappedBy="acUsersKnlT3")
	private List<JournAppUserBssT> journAppUserBssTs3;

	@OneToMany(mappedBy="acUsersKnlT")
	private List<AcUsersLinkKnlT> acUsersLinkKnlTs;

	@OneToMany(mappedBy="acUsersKnlT")
	private List<LinkGroupUsersUsersKnlT> linkGroupUsersUsersKnlTs;

	@OneToMany(mappedBy="acUsersKnlT1")
	private List<JournAppAccessBssT> journAppAccessBssTs1;

	@OneToMany(mappedBy="acUsersKnlT2")
	private List<JournAppAccessBssT> journAppAccessBssTs2;

	@OneToMany(mappedBy="acUsersKnlT3")
	private List<JournAppAccessBssT> journAppAccessBssTs3;

	@OneToMany(mappedBy="acUsersKnlT1")
	private List<JournAppAccessGroupsBssT> journAppAccessGroupsBssTs1;

	@OneToMany(mappedBy="acUsersKnlT2")
	private List<JournAppAccessGroupsBssT> journAppAccessBssGroupsTs2;

	@OneToMany(mappedBy="acUsersKnlT3")
	private List<JournAppAccessGroupsBssT> journAppAccessBssGroupsTs3;
	
	@OneToMany(mappedBy="acUsersKnlT1")
	private List<JournAppAdminUserSysBssT> journAppAdminUserSysBssTs1;

	@OneToMany(mappedBy="acUsersKnlT2")
	private List<JournAppAdminUserSysBssT> journAppAdminUserSysBssTs2;

	@OneToMany(mappedBy="acUsersKnlT3")
	private List<JournAppAdminUserSysBssT> journAppAdminUserSysBssTs3;

	@OneToMany(mappedBy="acUsersKnlT")
	private List<LinkAdminUserSys> linkAdminUserSys;

	@Transient
	private UserItem userItem;
	
	public AcUsersKnlT() {
	}

	public Long getIdSrv() {
		return this.idSrv;
	}

	public void setIdSrv(Long idSrv) {
		this.idSrv = idSrv;
	}

	public String getCertificate() {
		return this.certificate;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
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

	public String getDepartment() {
		return this.department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getEMail() {
		return this.eMail;
	}

	public void setEMail(String eMail) {
		this.eMail = eMail;
	}

	public Date getEndAccount() {
		return this.endAccount;
	}

	public void setEndAccount(Date endAccount) {
		this.endAccount = endAccount;
	}

	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Long getModificator() {
		return this.modificator;
	}

	public void setModificator(Long modificator) {
		this.modificator = modificator;
	}

	public Date getModified() {
		return this.modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPatronymic() {
		return this.patronymic;
	}

	public void setPatronymic(String patronymic) {
		this.patronymic = patronymic;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPosition() {
		return this.position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Date getStartAccount() {
		return this.startAccount;
	}

	public void setStartAccount(Date startAccount) {
		this.startAccount = startAccount;
	}

	public Long getStatus() {
		return this.status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public String getSurname() {
		return this.surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Long getUpBinding() {
		return this.upBinding;
	}

	public void setUpBinding(Long upBinding) {
		this.upBinding = upBinding;
	}

	public String getUpSign() {
		return this.upSign;
	}

	public void setUpSign(String upSign) {
		this.upSign = upSign;
	}

	public String getUpSignUser() {
		return this.upSignUser;
	}

	public void setUpSignUser(String upSignUser) {
		this.upSignUser = upSignUser;
	}

	public List<JournAppUserBssT> getJournAppUserBssTs1() {
		return this.journAppUserBssTs1;
	}

	public void setJournAppUserBssTs1(List<JournAppUserBssT> journAppUserBssTs1) {
		this.journAppUserBssTs1 = journAppUserBssTs1;
	}

	public JournAppUserBssT addJournAppUserBssTs1(JournAppUserBssT journAppUserBssTs1) {
		getJournAppUserBssTs1().add(journAppUserBssTs1);
		journAppUserBssTs1.setAcUsersKnlT1(this);

		return journAppUserBssTs1;
	}

	public JournAppUserBssT removeJournAppUserBssTs1(JournAppUserBssT journAppUserBssTs1) {
		getJournAppUserBssTs1().remove(journAppUserBssTs1);
		journAppUserBssTs1.setAcUsersKnlT1(null);

		return journAppUserBssTs1;
	}

	public List<JournAppUserBssT> getJournAppUserBssTs2() {
		return this.journAppUserBssTs2;
	}

	public void setJournAppUserBssTs2(List<JournAppUserBssT> journAppUserBssTs2) {
		this.journAppUserBssTs2 = journAppUserBssTs2;
	}

	public JournAppUserBssT addJournAppUserBssTs2(JournAppUserBssT journAppUserBssTs2) {
		getJournAppUserBssTs2().add(journAppUserBssTs2);
		journAppUserBssTs2.setAcUsersKnlT2(this);

		return journAppUserBssTs2;
	}

	public JournAppUserBssT removeJournAppUserBssTs2(JournAppUserBssT journAppUserBssTs2) {
		getJournAppUserBssTs2().remove(journAppUserBssTs2);
		journAppUserBssTs2.setAcUsersKnlT2(null);

		return journAppUserBssTs2;
	}

	public List<JournAppUserBssT> getJournAppUserBssTs3() {
		return this.journAppUserBssTs3;
	}

	public void setJournAppUserBssTs3(List<JournAppUserBssT> journAppUserBssTs3) {
		this.journAppUserBssTs3 = journAppUserBssTs3;
	}

	public JournAppUserBssT addJournAppUserBssTs3(JournAppUserBssT journAppUserBssTs3) {
		getJournAppUserBssTs3().add(journAppUserBssTs3);
		journAppUserBssTs3.setAcUsersKnlT3(this);

		return journAppUserBssTs3;
	}

	public JournAppUserBssT removeJournAppUserBssTs3(JournAppUserBssT journAppUserBssTs3) {
		getJournAppUserBssTs3().remove(journAppUserBssTs3);
		journAppUserBssTs3.setAcUsersKnlT3(null);

		return journAppUserBssTs3;
	}

	public List<AcUsersLinkKnlT> getAcUsersLinkKnlTs() {
		return this.acUsersLinkKnlTs;
	}

	public void setAcUsersLinkKnlTs(List<AcUsersLinkKnlT> acUsersLinkKnlTs) {
		this.acUsersLinkKnlTs = acUsersLinkKnlTs;
	}

	public AcUsersLinkKnlT addAcUsersLinkKnlT(AcUsersLinkKnlT acUsersLinkKnlT) {
		getAcUsersLinkKnlTs().add(acUsersLinkKnlT);
		acUsersLinkKnlT.setAcUsersKnlT(this);

		return acUsersLinkKnlT;
	}

	public AcUsersLinkKnlT removeAcUsersLinkKnlT(AcUsersLinkKnlT acUsersLinkKnlT) {
		getAcUsersLinkKnlTs().remove(acUsersLinkKnlT);
		acUsersLinkKnlT.setAcUsersKnlT(null);

		return acUsersLinkKnlT;
	}

	public List<LinkGroupUsersUsersKnlT> getLinkGroupUsersUsersKnlTs() {
		return this.linkGroupUsersUsersKnlTs;
	}

	public void setLinkGroupUsersUsersKnlTs(List<LinkGroupUsersUsersKnlT> linkGroupUsersUsersKnlTs) {
		this.linkGroupUsersUsersKnlTs = linkGroupUsersUsersKnlTs;
	}

	public LinkGroupUsersUsersKnlT addLinkGroupUsersUsersKnlT(LinkGroupUsersUsersKnlT linkGroupUsersUsersKnlT) {
		getLinkGroupUsersUsersKnlTs().add(linkGroupUsersUsersKnlT);
		linkGroupUsersUsersKnlT.setAcUsersKnlT(this);

		return linkGroupUsersUsersKnlT;
	}

	public LinkGroupUsersUsersKnlT removeLinkGroupUsersUsersKnlT(LinkGroupUsersUsersKnlT linkGroupUsersUsersKnlT) {
		getLinkGroupUsersUsersKnlTs().remove(linkGroupUsersUsersKnlT);
		linkGroupUsersUsersKnlT.setAcUsersKnlT(null);

		return linkGroupUsersUsersKnlT;
	}

	public List<JournAppAccessBssT> getJournAppAccessBssTs1() {
		return this.journAppAccessBssTs1;
	}

	public void setJournAppAccessBssTs1(List<JournAppAccessBssT> journAppAccessBssTs1) {
		this.journAppAccessBssTs1 = journAppAccessBssTs1;
	}

	public JournAppAccessBssT addJournAppAccessBssTs1(JournAppAccessBssT journAppAccessBssTs1) {
		getJournAppAccessBssTs1().add(journAppAccessBssTs1);
		journAppAccessBssTs1.setAcUsersKnlT1(this);

		return journAppAccessBssTs1;
	}

	public JournAppAccessBssT removeJournAppAccessBssTs1(JournAppAccessBssT journAppAccessBssTs1) {
		getJournAppAccessBssTs1().remove(journAppAccessBssTs1);
		journAppAccessBssTs1.setAcUsersKnlT1(null);

		return journAppAccessBssTs1;
	}

	public List<JournAppAccessBssT> getJournAppAccessBssTs2() {
		return this.journAppAccessBssTs2;
	}

	public void setJournAppAccessBssTs2(List<JournAppAccessBssT> journAppAccessBssTs2) {
		this.journAppAccessBssTs2 = journAppAccessBssTs2;
	}

	public JournAppAccessBssT addJournAppAccessBssTs2(JournAppAccessBssT journAppAccessBssTs2) {
		getJournAppAccessBssTs2().add(journAppAccessBssTs2);
		journAppAccessBssTs2.setAcUsersKnlT2(this);

		return journAppAccessBssTs2;
	}

	public JournAppAccessBssT removeJournAppAccessBssTs2(JournAppAccessBssT journAppAccessBssTs2) {
		getJournAppAccessBssTs2().remove(journAppAccessBssTs2);
		journAppAccessBssTs2.setAcUsersKnlT2(null);

		return journAppAccessBssTs2;
	}

	public List<JournAppAccessBssT> getJournAppAccessBssTs3() {
		return this.journAppAccessBssTs3;
	}

	public void setJournAppAccessBssTs3(List<JournAppAccessBssT> journAppAccessBssTs3) {
		this.journAppAccessBssTs3 = journAppAccessBssTs3;
	}

	public JournAppAccessBssT addJournAppAccessBssTs3(JournAppAccessBssT journAppAccessBssTs3) {
		getJournAppAccessBssTs3().add(journAppAccessBssTs3);
		journAppAccessBssTs3.setAcUsersKnlT3(this);

		return journAppAccessBssTs3;
	}

	public JournAppAccessBssT removeJournAppAccessBssTs3(JournAppAccessBssT journAppAccessBssTs3) {
		getJournAppAccessBssTs3().remove(journAppAccessBssTs3);
		journAppAccessBssTs3.setAcUsersKnlT3(null);

		return journAppAccessBssTs3;
	}

	public List<JournAppAdminUserSysBssT> getJournAppAdminUserSysBssTs1() {
		return this.journAppAdminUserSysBssTs1;
	}

	public void setJournAppAdminUserSysBssTs1(List<JournAppAdminUserSysBssT> journAppAdminUserSysBssTs1) {
		this.journAppAdminUserSysBssTs1 = journAppAdminUserSysBssTs1;
	}

	public JournAppAdminUserSysBssT addJournAppAdminUserSysBssTs1(JournAppAdminUserSysBssT journAppAdminUserSysBssTs1) {
		getJournAppAdminUserSysBssTs1().add(journAppAdminUserSysBssTs1);
		journAppAdminUserSysBssTs1.setAcUsersKnlT1(this);

		return journAppAdminUserSysBssTs1;
	}

	public JournAppAdminUserSysBssT removeJournAppAdminUserSysBssTs1(JournAppAdminUserSysBssT journAppAdminUserSysBssTs1) {
		getJournAppAdminUserSysBssTs1().remove(journAppAdminUserSysBssTs1);
		journAppAdminUserSysBssTs1.setAcUsersKnlT1(null);

		return journAppAdminUserSysBssTs1;
	}

	public List<JournAppAdminUserSysBssT> getJournAppAdminUserSysBssTs2() {
		return this.journAppAdminUserSysBssTs2;
	}

	public void setJournAppAdminUserSysBssTs2(List<JournAppAdminUserSysBssT> journAppAdminUserSysBssTs2) {
		this.journAppAdminUserSysBssTs2 = journAppAdminUserSysBssTs2;
	}

	public JournAppAdminUserSysBssT addJournAppAdminUserSysBssTs2(JournAppAdminUserSysBssT journAppAdminUserSysBssTs2) {
		getJournAppAdminUserSysBssTs2().add(journAppAdminUserSysBssTs2);
		journAppAdminUserSysBssTs2.setAcUsersKnlT2(this);

		return journAppAdminUserSysBssTs2;
	}

	public JournAppAdminUserSysBssT removeJournAppAdminUserSysBssTs2(JournAppAdminUserSysBssT journAppAdminUserSysBssTs2) {
		getJournAppAdminUserSysBssTs2().remove(journAppAdminUserSysBssTs2);
		journAppAdminUserSysBssTs2.setAcUsersKnlT2(null);

		return journAppAdminUserSysBssTs2;
	}

	public List<JournAppAdminUserSysBssT> getJournAppAdminUserSysBssTs3() {
		return this.journAppAdminUserSysBssTs3;
	}

	public void setJournAppAdminUserSysBssTs3(List<JournAppAdminUserSysBssT> journAppAdminUserSysBssTs3) {
		this.journAppAdminUserSysBssTs3 = journAppAdminUserSysBssTs3;
	}

	public JournAppAdminUserSysBssT addJournAppAdminUserSysBssTs3(JournAppAdminUserSysBssT journAppAdminUserSysBssTs3) {
		getJournAppAdminUserSysBssTs3().add(journAppAdminUserSysBssTs3);
		journAppAdminUserSysBssTs3.setAcUsersKnlT3(this);

		return journAppAdminUserSysBssTs3;
	}

	public JournAppAdminUserSysBssT removeJournAppAdminUserSysBssTs3(JournAppAdminUserSysBssT journAppAdminUserSysBssTs3) {
		getJournAppAdminUserSysBssTs3().remove(journAppAdminUserSysBssTs3);
		journAppAdminUserSysBssTs3.setAcUsersKnlT3(null);

		return journAppAdminUserSysBssTs3;
	}

	public List<LinkAdminUserSys> getLinkAdminUserSys() {
		return this.linkAdminUserSys;
	}

	public void setLinkAdminUserSys(List<LinkAdminUserSys> linkAdminUserSys) {
		this.linkAdminUserSys = linkAdminUserSys;
	}

	public LinkAdminUserSys addLinkAdminUserSy(LinkAdminUserSys linkAdminUserSy) {
		getLinkAdminUserSys().add(linkAdminUserSy);
		linkAdminUserSy.setAcUsersKnlT(this);

		return linkAdminUserSy;
	}

	public LinkAdminUserSys removeLinkAdminUserSy(LinkAdminUserSys linkAdminUserSy) {
		getLinkAdminUserSys().remove(linkAdminUserSy);
		linkAdminUserSy.setAcUsersKnlT(null);

		return linkAdminUserSy;
	}

	
	
	public List<JournAppAccessGroupsBssT> getJournAppAccessGroupsBssTs1() {
		return journAppAccessGroupsBssTs1;
	}

	public void setJournAppAccessGroupsBssTs1(
			List<JournAppAccessGroupsBssT> journAppAccessGroupsBssTs1) {
		this.journAppAccessGroupsBssTs1 = journAppAccessGroupsBssTs1;
	}

	public List<JournAppAccessGroupsBssT> getJournAppAccessBssGroupsTs2() {
		return journAppAccessBssGroupsTs2;
	}

	public void setJournAppAccessBssGroupsTs2(
			List<JournAppAccessGroupsBssT> journAppAccessBssGroupsTs2) {
		this.journAppAccessBssGroupsTs2 = journAppAccessBssGroupsTs2;
	}

	public List<JournAppAccessGroupsBssT> getJournAppAccessBssGroupsTs3() {
		return journAppAccessBssGroupsTs3;
	}

	public void setJournAppAccessBssGroupsTs3(
			List<JournAppAccessGroupsBssT> journAppAccessBssGroupsTs3) {
		this.journAppAccessBssGroupsTs3 = journAppAccessBssGroupsTs3;
	}

	public UserItem getUserItem() {
		return userItem;
	}
	public void setUserItem(UserItem userItem) {
		this.userItem = userItem;
	}
	
	 public class UserItem /*extends BaseItem*/ implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private Long idUser;

		private String login;
		
		private String cert;
		
		private String usrCode;
		
		private String fio;
		
		private String phone;
		
		private String email;
		
		private String position;
		
		private String department;
		 
		private String orgCode;
		
		private String orgName;
		
		private String orgAdr;
		
		private String orgTel;
		
		private String start;
		
		private String finish;
		
		private Long status;
		 
		private String statusValue;
		
		private String crtDate;
		
		private String crtUserLogin;
		
	    private String updDate;
		
		private String updUserLogin;
		
		private String depCode;
		
		private String orgIogvStatus;
		
		private String usrIogvStatus;

		private String depIogvStatus;
		
		private Long iogvBindType;
		
		private Long isCudRole=0L;
		
		private Long isSysAdmin=0L;
		
		private Boolean usrChecked=false;
		
		private String emailSecond;
		
		public UserItem() {
	    }

	    public UserItem(Long idUser,String login,String cert,String usrCode,String fio,
	    		        String phone,String email,String position,String department,String orgCode,
	    		        String orgName,String orgAdr,String orgTel,String start,String finish,
	    		        Long status,String crtDate,String crtUserLogin,String updDate,String updUserLogin, 
	    		        String depCode, String orgIogvStatus, String usrIogvStatus, String depIogvStatus,
		                Long iogvBindType,String emailSecond) {
	    	this.idUser=idUser;
	    	this.login=login;
	    	this.cert=cert;
	    	this.usrCode=usrCode;
	    	this.fio=fio;
	    	this.phone=phone;
	    	this.email=email;
	    	this.position=position;
	    	this.department=department;
	    	this.orgCode=orgCode;
	    	this.orgName=orgName;
	    	this.orgAdr=orgAdr;
	    	this.orgTel=orgTel;
	    	this.start=start;
	    	this.finish=finish;
	    	this.status=status;
	    	this.crtDate=crtDate;
	    	this.crtUserLogin=crtUserLogin;
	    	this.updDate=updDate;
	    	this.updUserLogin=updUserLogin;
	    	
	    	this.depCode=depCode; 
	    	this.orgIogvStatus=orgIogvStatus; 
	    	this.usrIogvStatus=usrIogvStatus; 
	    	this.depIogvStatus=depIogvStatus;
	    	
	    	this.iogvBindType=iogvBindType;
	    	
	    	this.emailSecond=emailSecond;
	     }
	    
	    public Long getBaseId() {
	 	   return this.idUser;
	 	}

	    public Long getIdUser() {
			return this.idUser;
		}
		public void setIdUser(Long idUser) {
			this.idUser = idUser;
		}

	    public String getLogin() {
			return this.login;
		}
	    public void setLogin(String login) {
			this.login = login;
		}

		public String getCert() {
			return this.cert;
		}
		public void setCert(String cert) {
			this.cert = cert;
		}

		public String getUsrCode() {
			return this.usrCode;
		}
		public void setUsrCode(String usrCode) {
			this.usrCode = usrCode;
		}

		public String getFio(){
			  return this.fio;
		}
		public void setFio(String fio){
			this.fio=fio;
		}
		
		public String getPhone() {
			return this.phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getEmail() {
			  return this.email;
		}
		public void setEmail(String email) {
			this.email = email;
		}

		public String getPosition() {
			return this.position;
		}
	    public void setPosition(String position) {
			this.position = position;
		}
	    
		public String getDepartment() {
			return this.department;
		}
	    public void setDepartment(String department) {
			this.department = department;
		}
	    
		public String getOrgCode() {
			return this.orgCode;
		}
	    public void setOrgCode(String orgCode) {
			this.orgCode = orgCode;
		}

	    public String getOrgName() {
			return this.orgName;
		}
		public void setOrgName(String orgName) {
			this.orgName = orgName;
		}
		
		public String getOrgAdr() {
			return this.orgAdr;
		}
		public void setOrgAdr(String orgAdr) {
			this.orgAdr = orgAdr;
		}

		public String getOrgTel() {
			return this.orgTel;
		}
		public void setOrgTel(String orgTel) {
			this.orgTel = orgTel;
		}

		public String getStart() {
			return this.start;
		}
		public void setStart(String start) {
			this.start = start;
		}
		
		public String getFinish() {
			return this.finish;
		}
		public void setFinish(String finish) {
			this.finish = finish;
		}
		
		public Long getStatus() {
			return this.status;
		}
	    public void setStatus(Long status) {
			this.status = status;
		}
		
		public String getStatusValue() {
	
				if(this.status!=null){
					if(this.status.equals(0L)){
						this.statusValue="Не активен";
					}else if(this.status.equals(1L)){
						this.statusValue="Активен";
					}else if(this.status.equals(2L)){
						this.statusValue="Заблокирован";
					}
				}
	
			
			return this.statusValue;
		}
		public void setStatusValue(String statusValue) {
			this.statusValue = statusValue;
		}
		
	    public String getProtectLogin() {
			return "*****";
		}
		public String getProtectPassword() {
			return "*****";
		}
		
		public String getCrtDate() {
				return this.crtDate;
		}
		public void setCrtDate(String crtDate) {
			this.crtDate = crtDate;
		}
		
		public String getCrtUserLogin() {
			return this.crtUserLogin;
	    }
	    public void setCrtUserLogin(String crtUserLogin) {
		   this.crtUserLogin = crtUserLogin;
	    }
		
		public String getUpdDate() {
			return this.updDate;
		}
		public void setUpdDate(String updDate) {
			this.updDate = updDate;
		}
		
		public String getUpdUserLogin() {
			return this.updUserLogin;
		}
		public void setUpdUserLogin(String updUserLogin) {
			this.updUserLogin = updUserLogin;
		}
		
		public Long getIogvBindType() {
			return this.iogvBindType;
		}
		public void setIogvBindType(Long iogvBindType) {
			this.iogvBindType = iogvBindType;
		}
		
		public Long getIsCudRole() {
			return this.isCudRole;
		}
		public void setIsCudRole(Long isCudRole) {
			this.isCudRole = isCudRole;
		}
		
		public Long getIsSysAdmin() {
			return this.isSysAdmin;
		}
		public void setIsSysAdmin(Long isSysAdmin) {
			this.isSysAdmin = isSysAdmin;
		}

		public String getDepCode() {
			return depCode;
		}

		public void setDepCode(String depCode) {
			this.depCode = depCode;
		}

		public String getOrgIogvStatus() {
			return orgIogvStatus;
		}

		public void setOrgIogvStatus(String orgIogvStatus) {
			this.orgIogvStatus = orgIogvStatus;
		}

		public String getUsrIogvStatus() {
			return usrIogvStatus;
		}

		public void setUsrIogvStatus(String usrIogvStatus) {
			this.usrIogvStatus = usrIogvStatus;
		}

		public String getDepIogvStatus() {
			return depIogvStatus;
		}

		public void setDepIogvStatus(String depIogvStatus) {
			this.depIogvStatus = depIogvStatus;
		}
		
		public Boolean getUsrChecked(){
			return this.usrChecked;
		}
		public void setUsrChecked(Boolean usrChecked){
			this.usrChecked=usrChecked;
		}
		
		public String getEmailSecond() {
			return emailSecond;
		}
        public void setEmailSecond(String emailSecond) {
			this.emailSecond = emailSecond;
		}
	}
	
}