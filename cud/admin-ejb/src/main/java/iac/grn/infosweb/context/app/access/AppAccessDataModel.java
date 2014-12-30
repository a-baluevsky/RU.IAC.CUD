package iac.grn.infosweb.context.app.access;

import iac.cud.infosweb.dataitems.BaseItem;
import iac.grn.infosweb.session.table.BaseDataModel;

import java.util.List;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;


@Name("appAccessDataModel")
 public class AppAccessDataModel extends BaseDataModel<BaseItem, Long>  {
	
	@In(create=true)
	private AppAccessManager appAccessManager;

	@Override
	public int getNumRecords(String modelType) {
		
		int ri=0;
		Long rL=appAccessManager.getAuditCount();
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
		
    	return appAccessManager.getAuditList(firstRow, numberOfRows);  
	}
	
	public void filterAction() {
		 log.info("appAccessDataModel:filterAction");
	     super.filterAction();
		 
	     AppAccessStateHolder appAccessStateHolder = (AppAccessStateHolder)Component.getInstance("appAccessStateHolder", ScopeType.SESSION);
		 appAccessStateHolder.clearFilters();
	}

}

