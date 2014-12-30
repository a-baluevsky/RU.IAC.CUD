package iac.grn.infosweb.context.app.user.dep.modify;

import iac.cud.infosweb.dataitems.BaseItem;
import iac.grn.infosweb.session.table.BaseDataModel;

import java.util.List;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
 

@Name("appUserDepModifyDataModel")
 public class AppUserDepModifyDataModel extends BaseDataModel<BaseItem, Long>  {
	
	@In(create=true)
	private AppUserDepModifyManager appUserDepModifyManager;

	@Override
	public int getNumRecords(String modelType) {
		
		int ri=0;
		Long rL=appUserDepModifyManager.getAuditCount();
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
		
    	return appUserDepModifyManager.getAuditList(firstRow, numberOfRows);  
	}
	
	public void filterAction() {
		 log.info("appUserDepModifyDataModel:filterAction");
	     super.filterAction();
		 
	     AppUserDepModifyStateHolder appUserDepModifyStateHolder = (AppUserDepModifyStateHolder)Component.getInstance("appUserDepModifyStateHolder", ScopeType.SESSION);
		 appUserDepModifyStateHolder.clearFilters();
	}

}

