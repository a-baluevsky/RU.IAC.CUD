package iac.grn.infosweb.context.mc.arm;

import iac.cud.infosweb.dataitems.BaseItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;


@Name("armDataProvider")
 public class ArmDataProvider implements Serializable{
	
	@Logger private Log log;
	
	@In(create=true)
	private ArmManager armManager;


	
	public List<BaseItem> getItemsByrange(int firstRow, int numberOfRows, String sortField, 
			                                   boolean ascending) {
		List<BaseItem> ret = new ArrayList<BaseItem>();
	
		log.info("AuditDataProvider:getItemsByrange");
		ret=armManager.getAuditList(firstRow, numberOfRows);
		return ret;
	}
	
	public void update() {
		// nothing need to do
	}
	
	public int getRowCount() {
		int ri=0;
		Long rL=armManager.getAuditCount();
		if(rL!=null){
			ri = Integer.parseInt(rL.toString());
		}
		return ri;
	}
}
