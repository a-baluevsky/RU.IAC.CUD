package iac.grn.infosweb.context.app.user;

import iac.grn.serviceitems.BaseTableItem;
import iac.grn.serviceitems.HeaderTableItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 public class AppUserContext {

		
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
	
	public AppUserContext(){
		
		
		HeaderTableItem h1=null, h2=null, h3=null, h4=null, h5=null;
		
		headerItemsList.add(h4=new HeaderTableItem("Результат выполнения", "h4")); 
		headerItemsList.add(h1=new HeaderTableItem("Содержание", "h1"));
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
		
		auditItemsMap.put("surnameUser",new BaseTableItem("Фамилия", "...", "surnameUser", "t1_SURNAME_USER", 1, h1));
		auditItemsMap.put("nameUser",new BaseTableItem("Имя", "...", "nameUser", "t1_NAME_USER", 2, h1));
		auditItemsMap.put("patronymicUser",new BaseTableItem("Отчество", "...", "patronymicUser", "t1_PATRONYMIC_USER", 3, h1));
		auditItemsMap.put("iogvCodeUser",new BaseTableItem("Код ИОГВ пользователя", "...", "iogvCodeUser", "t1_SIGN_USER", 4, h1));
		auditItemsMap.put("positionUser",new BaseTableItem("Должность", "...", "positionUser", "t1_POSITION_USER", 5, h1));
		auditItemsMap.put("emailUser",new BaseTableItem("Email", "...", "emailUser", "t1_EMAIL_USER", 6, h1));
		auditItemsMap.put("phoneUser",new BaseTableItem("Телефон", "...", "phoneUser", "t1_PHONE_USER", 7, h1));
		auditItemsMap.put("certificateUser",new BaseTableItem("Сертификат", "...", "certificateUser", "t1_CERTIFICATE_USER", 8, h1));
		auditItemsMap.put("nameDepartament",new BaseTableItem("Подразделение", "...", "nameDepartament", "t1_NAME_DEPARTAMENT", 9, h1));
		auditItemsMap.put("nameOrg",new BaseTableItem("Организация", "...", "nameOrg", "t1_NAME_ORG", 10, h1));
		auditItemsMap.put("iogvCodeOrg",new BaseTableItem("Код ИОГВ организации", "...", "iogvCodeOrg", "t1_SIGN_ORG", 11, h1));
		auditItemsMap.put("commentApp",new BaseTableItem("Комментарий заявки", "...", "commentApp", "t1_comment_app", 12, h1));
		auditItemsMap.put("rolesApp",new BaseTableItem("Роли", "...", "rolesApp", "t1_roles_app", 13, h1));
		

		auditItemsMap.put("login",new BaseTableItem("Логин пользователя", "...", "login", "t1_user_login", 34, h4));
			
	
		}
}
