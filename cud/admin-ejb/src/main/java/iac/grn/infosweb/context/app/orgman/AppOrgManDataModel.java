package iac.grn.infosweb.context.app.orgman;

import iac.cud.infosweb.dataitems.BaseItem;
import iac.grn.infosweb.session.table.BaseDataModel;

import java.util.List;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
 

@Name("appOrgManDataModel")
 public class AppOrgManDataModel extends BaseDataModel<BaseItem, Long>  {
	
	@In(create=true)
	private AppOrgManManager appOrgManManager;

	@Override
	public int getNumRecords(String modelType) {
		
		int ri=0;
		Long rL=appOrgManManager.getAuditCount();
		if(rL!=null){
			ri = Integer.parseInt(rL.toString());
		}
		return ri;
	}

	@Override
	public Long getId(BaseItem object) {
		// TODO Auto-generated method stub
		 
		return object.getBaseId();
	}

	@Override
	public List<BaseItem> findObjects(int firstRow, int numberOfRows,
			String sortField, boolean ascending, String modelType) {
		
    	return appOrgManManager.getAuditList(firstRow, numberOfRows);  
	}
	
	public void filterAction() {
		 log.info("appOrgManDataModel:filterAction");
	     super.filterAction();
		 
	     AppOrgManStateHolder appOrgManStateHolder = (AppOrgManStateHolder)Component.getInstance("appOrgManStateHolder", ScopeType.SESSION);
		 appOrgManStateHolder.clearFilters();
	}

}

