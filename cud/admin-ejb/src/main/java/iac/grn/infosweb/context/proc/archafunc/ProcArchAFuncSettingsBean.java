package iac.grn.infosweb.context.proc.archafunc;

import org.jboss.seam.annotations.Name;

@Name("procArchAFuncSettingsBean")
 public class ProcArchAFuncSettingsBean {

	private Long paramActualData;
	
	public Long getParamActualData(){
		return this.paramActualData;
	}
	public void setParamActualData(Long paramActualData){
		this.paramActualData=paramActualData;
	}
	
	
}
