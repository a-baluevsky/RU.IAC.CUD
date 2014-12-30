package iac.grn.infosweb.context.proc.archasys;

import org.jboss.seam.annotations.Name;

@Name("procArchASysSettingsBean")
 public class ProcArchASysSettingsBean {

	private Long paramActualData;
	
	public Long getParamActualData(){
		return this.paramActualData;
	}
	public void setParamActualData(Long paramActualData){
		this.paramActualData=paramActualData;
	}
	
	
}
