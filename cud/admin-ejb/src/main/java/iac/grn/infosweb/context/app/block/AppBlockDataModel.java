package iac.grn.infosweb.context.app.block;

import iac.cud.infosweb.dataitems.BaseItem;
import iac.grn.infosweb.session.table.BaseDataModel;

import java.util.List;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
 

@Name("appBlockDataModel")
 public class AppBlockDataModel extends BaseDataModel<BaseItem, Long>  {
	
	@In(create=true)
	private AppBlockManager appBlockManager;

	@Override
	public int getNumRecords(String modelType) {
		
		int ri=0;
		Long rL=appBlockManager.getAuditCount();
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
		
    	return appBlockManager.getAuditList(firstRow, numberOfRows);  
	}
	
	public void filterAction() {
		 log.info("appBlockDataModel:filterAction");
	     super.filterAction();
		 
	     AppBlockStateHolder appBlockStateHolder = (AppBlockStateHolder)Component.getInstance("appBlockStateHolder", ScopeType.SESSION);
		 appBlockStateHolder.clearFilters();
	}

}

