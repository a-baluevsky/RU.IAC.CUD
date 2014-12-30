package iac.grn.dataitems.repos;

import java.io.Serializable;
import java.util.Date;

import iac.cud.infosweb.dataitems.BaseItem;

 public class ReposItem  extends BaseItem implements Serializable {
	 
    private static final long serialVersionUID = 1L;
	
    private Long idDoc;
    
    private String title;

    private String annotation;
    
	private String keywords;
	
	private String number;
	
	private Date dateConfirm;
	
	private String organConfirm;
	
	private String status;
	
	private Date datePlace;
    
	private Date datePublic;
	
	private String importance;
	
	private String storingPlace;
	
	public ReposItem(){
		
	}
	
	public ReposItem(Long idDoc, String title, String annotation, String keywords, 
			String number, Date dateConfirm, String organConfirm, String status, 
			Date datePlace, Date datePublic, String importance, String storingPlace){
		
		this.idDoc=idDoc;
		this.title=title;
		this.annotation=annotation;
		this.keywords=keywords; 
		this.number=number;
		this.dateConfirm=dateConfirm;
		this.organConfirm=organConfirm;
		this.status=status;
		this.datePlace=datePlace;
		this.datePublic=datePublic;
		this.importance=importance;
		this.storingPlace=storingPlace;
	}
	
	public Long getBaseId() {
		return this.idDoc;
	}
	
	public Long getIdDoc() {
		return this.idDoc;
	}
	public void setIdDocn(Long idDoc) {
		this.idDoc = idDoc;
	}
	
	public String getTitle() {
		return this.title;
	}
    public void setTitle(String title) {
		this.title = title;
	}
	
	public String getAnnotation() {
		return this.annotation;
	}
	public void setFAnnotation(String annotation) {
		this.annotation = annotation;
	}
	
	public String getKeywords() {
		return this.keywords;
	}
    public void setKeywordse(String keywords) {
		this.keywords = keywords;
	}
    
    public Date getDateConfirm() {
		return this.dateConfirm;
	}
    public void setDateConfirm(Date dateConfirm) {
		this.dateConfirm = dateConfirm;
	}
    
    public String getOrganConfirm() {
		return this.organConfirm;
	}
	public void setOrganConfirm(String organConfirm) {
		this.organConfirm = organConfirm;
	}
	
	public String getNumber() {
		return this.number;
	}
    public void setNumber(String number) {
		this.number = number;
	}
    
    public String getStatus() {
		return this.status;
	}
    public void setStatus(String status) {
		this.status = status;
	}
    
    public Date getDatePlace() {
		return this.datePlace;
	}
    public void setDatePlace(Date datePlace) {
		this.datePlace = datePlace;
	}
    
    public Date getDatePublic() {
		return this.datePublic;
	}
    public void setDatePublic(Date datePublic) {
		this.datePublic = datePublic;
	}
    
    public String getImportance() {
		return this.importance;
	}
	public void setImportance(String importance) {
		this.importance = importance;
	}
	
	public String getStoringPlace() {
		return this.storingPlace;
	}
    public void setStoringPlace(String storingPlace) {
		this.storingPlace = storingPlace;
	}
}
