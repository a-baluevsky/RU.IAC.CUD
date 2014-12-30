package iac.grn.infosweb.context.mc.org;

import iac.grn.serviceitems.BaseTableItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap; import java.util.Map;
import java.util.List;

 public class OrgContext {

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
	
	public OrgContext(){
		auditItemsMap.put("fullName",new BaseTableItem("Полное наименование", "...", "fullName", 1));
		auditItemsMap.put("shortName",new BaseTableItem("Краткое наименование", "...", "shortName", 2));
		auditItemsMap.put("contactEmployeeFio",new BaseTableItem("ФИО руководителя", "...", "contactEmployeeFio", 3));
		auditItemsMap.put("contactEmployeePosition",new BaseTableItem("Должность","...", "contactEmployeePosition", 4));
		auditItemsMap.put("contactEmployeePhone",new BaseTableItem("Телефон", "...", "contactEmployeePhone", 5));
		auditItemsMap.put("isExternalValue",new BaseTableItem("Вид организации","...", "isExternalValue", "isExternal", 6));
		auditItemsMap.put("letValue",new BaseTableItem("Тип организации","...", "letValue", "acLegalEntityType2.name", 7));

	}
}
