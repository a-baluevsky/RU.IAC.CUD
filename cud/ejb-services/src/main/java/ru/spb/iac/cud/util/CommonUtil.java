package ru.spb.iac.cud.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.spb.iac.cud.items.Attribute;
import ru.spb.iac.cud.items.User;

 public class CommonUtil {

	public static String createLine (List<String> uids){
		String result = null;
		
		if (uids != null && !uids.isEmpty()) {

			for (String uidValue : uids) {
				if (result == null) {
					result = "'" + uidValue + "'";
				} else {
					result = result + ", '" + uidValue + "'";
				}
			}
		}
		
		return result;
	}

	public static String createAttributes (List<Object[]> lo, List<User> result, Map<String, User> resultIds){
		
		   Attribute at = null;
		   String uid = null;
		   String idRec = null;
		   User uat = null;
		   List<Attribute> atlist = null;
		   String usersIdsLine = null;
		   
		   for (Object[] objectArray : lo) {

			// !!!
				// ѕроверить
				uid = objectArray[0].toString();

				idRec = objectArray[0].toString();

				uat = new User();
				atlist = new ArrayList<Attribute>();
				String name = null;
	
			for (int i = 0; i < objectArray.length; i++) {

				at = new Attribute();

				switch (i) {

				case 0: {
					name = "USER_UID";
					break;
				}
				case 1: {
					name = "USER_LOGIN";
					break;
				}
				case 4: {
					name = "USER_FIO";
					break;
				}
				case 5: {
					name = "USER_PHONE";
					break;
				}
				case 6: {
					name = "USER_EMAIL";
					break;
				}
				case 10: {
					name = "ORG_NAME";
					break;
				}
				case 9: {
					name = "ORG_CODE_IOGV";
					break;
				}
				case 11: {
					name = "ORG_ADDRESS";
					break;
				}
				case 12: {
					name = "ORG_PHONE";
					break;
				}
				case 8: {
					name = "DEP_NAME";
					break;
				}
				case 7: {
					name = "USER_POSITION";
					break;
				}
				case 24: {
					name = "ORG_CODE_OKATO";
					break;
				}
				case 25: {
					name = "DEP_ADDRESS";
					break;
				}

				default:
					name = null;
				}

				if (name != null) {

					at.setName(name);
					at.setValue (objectArray[i] != null ? objectArray[i]
							.toString() : "");

					atlist.add(at);
				}
			}
			
			uat.setUid(uid);
			uat.setAttributes(atlist);

			result.add(uat); // дл€ сохранени€ сортировки из запроса
			resultIds.put(idRec, uat);

			if (usersIdsLine == null) {
				usersIdsLine = "'" + idRec + "'";
			} else {
				usersIdsLine = usersIdsLine + ", '" + idRec + "'";
			}
	       }
		   
		   return usersIdsLine ;
	}
}
