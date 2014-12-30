package iac.grn.serviceitems;

import java.util.Date;

 public class ProcInfoItem {

    private Date execDate;
	
	private String execHit;
	
	private Date confDate;
	
    private Long confPeriod;
	
	public Date getExecDate(){
		return this.execDate;
	}
	public void setExecDate(Date execDate){
		this.execDate=execDate;
	}
	
	public String getExecHit(){
		return this.execHit;
	}
	public void setExecHit(String execHit){
		this.execHit=execHit;
	}
	
	public Date getConfDate(){
		return this.confDate;
	}
	public void setConfDate(Date confDate){
		this.confDate=confDate;
	}
	
	public Long getConfPeriod(){
		return this.confPeriod;
	}
	public void setConfPeriod(Long confPeriod){
		this.confPeriod=confPeriod;
	}
}
