package iac.grn.infosweb.session.table;

import java.util.ArrayList;
import java.util.HashMap; import java.util.Map;
import java.util.List;


 public class BaseStateHolder {
	
	   protected Map<String, String> sortOrders = new HashMap<String, String>();
       protected Map<String, String> columnFilterValues = new HashMap<String, String>();
      
       protected List <String> auditFieldListSelect = new ArrayList<String>();
       
       protected int pageNumber;

     
       
       public List <String> getAuditFieldListSelect() {
    	   return this.auditFieldListSelect;
       }
       public void setAuditFieldListSelect(List <String> auditFieldListSelect) {
    	   this.auditFieldListSelect=auditFieldListSelect;
       }
       public Map<String, String> getColumnFilterValues() {
    	 
           return columnFilterValues;
       }
       public void setColumnFilterValues(HashMap<String, String> columnFilterValues) {
    	  
    	   this.columnFilterValues = columnFilterValues;
       }
       public Map<String, String> getSortOrders() {
               return sortOrders;
       }
       public void setSortOrders(HashMap<String, String> sortOrders) {
                this.sortOrders = sortOrders;
       }
       
       
		public int getPageNumber() {
			return this.pageNumber;
		}
	
		public void setPageNumber(int pageNumber) {
			this.pageNumber = pageNumber;
		}
	
		public void resetPageNumber() {
			this.pageNumber = 1;
		}
         
    }