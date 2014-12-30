package iac.cud.infosweb.dataitems;

import java.io.Serializable;
import java.util.HashMap; import java.util.Map;

 public class BaseParamItem implements Serializable {
	 private static final long serialVersionUID = 1L;

	 private Map<String, Object> paramMap;
	 
	 public BaseParamItem(){
	 }
	 
	 public BaseParamItem(String value){
		 put("gtype", value);
	 }
	 
	 public void put(String key, Object value){
		 if (this.paramMap==null){
			 this.paramMap = new HashMap<String, Object>();
		 }
		 this.paramMap.put(key, value);
	 }
	 
	 public Object get(String key){
		 if (this.paramMap==null){
			 return null;
		 }else{
			 return this.paramMap.get(key);
		 }
	 }
}
