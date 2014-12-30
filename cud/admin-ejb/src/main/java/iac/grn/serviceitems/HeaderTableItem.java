package iac.grn.serviceitems;

import java.util.ArrayList;
import java.util.List;

 

 public class HeaderTableItem {

	private String itemLabel;
	private String itemField;
	private List<BaseTableItem> items=new ArrayList<BaseTableItem>();
		
	public HeaderTableItem(String itemLabel, String itemField){
		this.itemLabel=itemLabel;
		this.itemField=itemField;
	}
	
	public String getItemLabel(){
		return this.itemLabel;
	}
	public void setItemLabel(String itemLabel){
		this.itemLabel=itemLabel;
	}
	
	public List<BaseTableItem> getItems(){
		return this.items;
	}
	public void setItems(List<BaseTableItem> items){
		this.items=items;
	}
	
	public String getItemField(){
		return this.itemField;
	}
	public void setItemField(String itemField){
		this.itemField=itemField;
	}
}
