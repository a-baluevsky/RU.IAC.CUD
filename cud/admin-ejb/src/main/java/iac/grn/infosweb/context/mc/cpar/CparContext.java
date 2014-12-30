package iac.grn.infosweb.context.mc.cpar;

import iac.grn.serviceitems.BaseTableItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap; import java.util.Map;
import java.util.List;

 public class CparContext {

    private Map<String, BaseTableItem> auditItemsMap=new HashMap<String, BaseTableItem>();
	
	public Map <String, BaseTableItem> getAuditItemsMap() {
        return this.auditItemsMap;
    }
		
	public List <BaseTableItem> getAuditItemsCollection() {
		
		List<BaseTableItem> btiList = new ArrayList<BaseTableItem>(this.auditItemsMap.values());
		Collections.sort(btiList, new Comparator<BaseTableItem>() {

	        public int compare(BaseTableItem o1, BaseTableItem o2) {
	        	
	        	int sort1=o1.getItemSort();
	        	int sort2=o2.getItemSort();
	        	
	        	if(sort1 > sort2){
	                return 1;
	            }else if(sort1 < sort2){
	                return -1; 
	            }else{
	                return 0;
	            }    
	         }
	    });

        return btiList;
    }
	
	public CparContext(){
		auditItemsMap.put("nameParam",new BaseTableItem("Наименование", "...", "nameParam", 1));
		auditItemsMap.put("valueParam",new BaseTableItem("Значение", "...", "valueParam", 2));
		auditItemsMap.put("servName",new BaseTableItem("Сервис", "...", "servName", "o1.full", 3));
		auditItemsMap.put("description",new BaseTableItem("Описание", "...", "description", 4));
	}
}
