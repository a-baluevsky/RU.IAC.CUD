package iac.grn.infosweb.context.proc.bindunbind;

import org.jboss.seam.annotations.Name;

@Name("procBindUnBindSettingsBean")
 public class ProcBindUnBindSettingsBean {

	private Long paramActualData;
	
	public Long getParamActualData(){
		return this.paramActualData;
	}
	public void setParamActualData(Long paramActualData){
		this.paramActualData=paramActualData;
	}
	
	
}
