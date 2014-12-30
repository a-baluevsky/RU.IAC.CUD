package iac.grn.infosweb.context.app.system.modify;

import iac.grn.serviceitems.BaseTableItem;
import iac.grn.serviceitems.HeaderTableItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap; import java.util.Map;
import java.util.List;

 public class AppSystemModifyContext {

		
	private List<HeaderTableItem> headerItemsList=new ArrayList<HeaderTableItem>();
	
    private Map<String, BaseTableItem> auditItemsMap=new HashMap<String, BaseTableItem>();
	
	public Map <String, BaseTableItem> getAuditItemsMap() {
        return this.auditItemsMap;
    }
	
	public List<HeaderTableItem> getHeaderItemsList() {
        return this.headerItemsList;
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
	
	public AppSystemModifyContext(){
		
		
		HeaderTableItem h1=null, h2=null, h3=null, h4=null, h5=null;
		
		
		headerItemsList.add(h1=new HeaderTableItem("Содержание", "h1"));
		headerItemsList.add(h4=new HeaderTableItem("Исходные данные", "h4")); 
		headerItemsList.add(h2=new HeaderTableItem("Отправитель", "h2"));
		headerItemsList.add(h3=new HeaderTableItem("Учётные данные", "h3")); 
		headerItemsList.add(h5=new HeaderTableItem("Дополнительно", "h5"));
		
		auditItemsMap.put("statusValue",new BaseTableItem("Статус", "...", "statusValue", "t1_status", 0, h3));
		auditItemsMap.put("idApp",new BaseTableItem("Номер", "...", "idApp", "t1_id", 2, h3));
		auditItemsMap.put("created",new BaseTableItem("Дата", "...", "created", "t1_created", 3, h3));
		
		auditItemsMap.put("rejectReason",new BaseTableItem("Причина отказа", "...", "rejectReason", "t1_reject_reason", 1, h5));
		auditItemsMap.put("comment",new BaseTableItem("Комментарий", "...", "comment", "t1_comment", 2, h5));
		
		auditItemsMap.put("orgName", new BaseTableItem("Организация", "...", "orgName", "t1_org_name", 20, h2));
		auditItemsMap.put("usrFio", new BaseTableItem("Пользователь", "...", "usrFio", "t1_user_fio", 21, h2));
		
		auditItemsMap.put("fullName",new BaseTableItem("Полное наименование ИС", "...", "fullName", "t1_full_name", 30, h1));
		auditItemsMap.put("shortName",new BaseTableItem("Краткое наименование ИС", "...", "shortName", "t1_short_name", 31, h1));
		auditItemsMap.put("description",new BaseTableItem("Описание ИС", "...", "description", "t1_description", 32, h1));
		
		
		auditItemsMap.put("codeArm",new BaseTableItem("Код ИС", "...", "codeArm", "t1_arm_code", 34, h4));
		auditItemsMap.put("fullNameArm",new BaseTableItem("Полное наименование ИС", "...", "fullNameArm", "t1_arm_name", 35, h4));
		auditItemsMap.put("descriptionArm",new BaseTableItem("Описание ИС", "...", "descriptionArm", "t1_arm_description", 36, h4));
		
	
		}
}
