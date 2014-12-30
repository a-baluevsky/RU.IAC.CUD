package iac.grn.infosweb.context.proc.archtoken;

import org.jboss.seam.annotations.Name;

@Name("procArchTokenSettingsBean")
 public class ProcArchTokenSettingsBean {

	private Long paramActualData;
	
	public Long getParamActualData(){
		return this.paramActualData;
	}
	public void setParamActualData(Long paramActualData){
		this.paramActualData=paramActualData;
	}
	
	
}
