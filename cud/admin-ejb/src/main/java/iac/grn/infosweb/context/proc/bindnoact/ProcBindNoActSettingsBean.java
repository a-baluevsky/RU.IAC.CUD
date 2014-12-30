package iac.grn.infosweb.context.proc.bindnoact;

import org.jboss.seam.annotations.Name;

@Name("procBindNoActSettingsBean")
 public class ProcBindNoActSettingsBean {

	private Long paramActualData;
	
	public Long getParamActualData(){
		return this.paramActualData;
	}
	public void setParamActualData(Long paramActualData){
		this.paramActualData=paramActualData;
	}
	
	
}
