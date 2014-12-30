package iac.grn.infosweb.context.mc.audit.sys;

import iac.grn.serviceitems.BaseTableItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap; import java.util.Map;
import java.util.List;

 public class ASysContext {

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
	
	public ASysContext(){
		
		auditItemsMap.put("createdValue",new BaseTableItem("Дата и время обращения", "...", "createdValue", "crt_date", "crt_date", 1));
		auditItemsMap.put("servName",new BaseTableItem("Сервис", "...", "servName", "serv_name", "serv_name", 2));
		auditItemsMap.put("inputParam",new BaseTableItem("Входные параметры", "...", "inputParam",  3));
	    auditItemsMap.put("resultValue",new BaseTableItem("Результат", "...", "resultValue", 4));
		auditItemsMap.put("ipAddress",new BaseTableItem("IP-адрес", "...", "ipAddress", "IP_ADDRESS", "IP_ADDRESS", 5));
		auditItemsMap.put("userFio",new BaseTableItem("Пользователь", "...", "userFio", "fio", "fio", 6));
		
	
		
	}
}
