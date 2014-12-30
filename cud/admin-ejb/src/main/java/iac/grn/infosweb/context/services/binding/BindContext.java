package iac.grn.infosweb.context.services.binding;

import iac.grn.serviceitems.BaseTableItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap; import java.util.Map;
import java.util.List;

 public class BindContext {

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
	
	public BindContext(){
		
		
		
		auditItemsMap.put("statusValue",new BaseTableItem("Статус","...", "statusValue", "t1_status", 0));
		auditItemsMap.put("fio",new BaseTableItem("ФИО","...", "fio", "t1_fio", 10));
		
		
		auditItemsMap.put("login",new BaseTableItem("Логин","...", "login", "t1_login", 12));
		auditItemsMap.put("cert",new BaseTableItem("Номер сертификата","...", "cert", 13));
		
		auditItemsMap.put("phone",new BaseTableItem("Телефон","...", "phone", 20));
		auditItemsMap.put("email",new BaseTableItem("E-mail","...", "email", 30));
		auditItemsMap.put("orgName",new BaseTableItem("Организация","...", "orgName", "t1_org_name", 40));
		auditItemsMap.put("orgCode",new BaseTableItem("Код ИОГВ организации","...", "orgCode", 41));
		
		auditItemsMap.put("department",new BaseTableItem("Подразделение","...", "department", "t1_dep_name", 42));
		auditItemsMap.put("position",new BaseTableItem("Должность","...", "position", "t1_pos", 43));
		
		auditItemsMap.put("usrCode",new BaseTableItem("Код ИОГВ пользователя","...", "usrCode", 44));
		
		
		
		
		auditItemsMap.put("start",new BaseTableItem("Дата начала действия","...", "start", 70));
		auditItemsMap.put("finish",new BaseTableItem("Дата окончания действия","...", "finish", 80));
		auditItemsMap.put("crtDate",new BaseTableItem("Дата создания","...", "crtDate", "t1_crt_date", 90));
		auditItemsMap.put("crtUserLogin",new BaseTableItem("Создал","...", "crtUserLogin", 91));
		auditItemsMap.put("updDate",new BaseTableItem("Дата редактирования","...", "updDate", "t1_crt_date",92));
		auditItemsMap.put("updUserLogin",new BaseTableItem("Редактировал","...", "updUserLogin", 93));
		
		}
}
