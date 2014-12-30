package iac.grn.infosweb.context.mc.clorg;

import iac.grn.serviceitems.BaseTableItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap; import java.util.Map;
import java.util.List;

 public class ClOrgContext {

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
	
	public ClOrgContext(){
		
		auditItemsMap.put("signObject",new BaseTableItem("Код ИОГВ", "...", "signObject", 1));
		auditItemsMap.put("full",new BaseTableItem("Наименование", "...", "full", 2));
		auditItemsMap.put("fio",new BaseTableItem("ФИО", "...", "fio", 3));
	    auditItemsMap.put("postalCode",new BaseTableItem("Почтовый код", "...", "postalCode", 4));
		auditItemsMap.put("place",new BaseTableItem("Населённый пункт", "...", "place", 5));
		auditItemsMap.put("prefix",new BaseTableItem("Префикс адреса", "...", "prefix", 6));
		auditItemsMap.put("house",new BaseTableItem("Номер дома", "...", "house", 7));
		auditItemsMap.put("phone",new BaseTableItem("Телефон", "...", "phone", 8));
		auditItemsMap.put("fax",new BaseTableItem("Факс", "...", "fax", 9));
		auditItemsMap.put("email",new BaseTableItem("E-mail", "...", "email", 10));
		
	
	}
}
