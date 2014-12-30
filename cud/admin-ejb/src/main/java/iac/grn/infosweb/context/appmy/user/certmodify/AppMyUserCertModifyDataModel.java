package iac.grn.infosweb.context.appmy.user.certmodify;

import iac.cud.infosweb.dataitems.BaseItem;
import iac.grn.infosweb.session.table.BaseDataModel;

import java.util.List;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
 

@Name("appMyUserCertModifyDataModel")
 public class AppMyUserCertModifyDataModel extends BaseDataModel<BaseItem, Long>  {
	
	@In(create=true)
	private AppMyUserCertModifyManager appMyUserCertModifyManager;

	@Override
	public int getNumRecords(String modelType) {
		
		int ri=0;
		Long rL=appMyUserCertModifyManager.getAuditCount();
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
		
    	return appMyUserCertModifyManager.getAuditList(firstRow, numberOfRows);  
	}
	
	public void filterAction() {
		 log.info("appMyUserCertModifyDataModel:filterAction");
	     super.filterAction();
		 
	     AppMyUserCertModifyStateHolder appMyUserCertModifyStateHolder = (AppMyUserCertModifyStateHolder)Component.getInstance("appMyUserCertModifyStateHolder", ScopeType.SESSION);
		 appMyUserCertModifyStateHolder.clearFilters();
	}

}

