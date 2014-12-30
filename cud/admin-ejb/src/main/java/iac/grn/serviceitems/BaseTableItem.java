package iac.grn.serviceitems;

 

 public class BaseTableItem {

	private String itemLabel;
	private String itemColumn;
	private String itemField;
	private String itemSortField;
	private String itemFiltField;
	private HeaderTableItem header;
	private int itemSort;
	
	public BaseTableItem(String itemLabel, String itemColumn, String itemField){
		this.itemLabel=itemLabel;
		this.itemColumn=itemColumn;
		this.itemField=itemField;
	}
	
	public BaseTableItem(String itemLabel, String itemColumn, String itemField, int itemSort){
		this.itemLabel=itemLabel;
		this.itemColumn=itemColumn;
		this.itemField=itemField;
		this.itemSort=itemSort;
	}
	
	public BaseTableItem(String itemLabel, String itemColumn, String itemField, String itemSortField, int itemSort){
		this.itemLabel=itemLabel;
		this.itemColumn=itemColumn;
		this.itemField=itemField;
		this.itemSortField=itemSortField;
		this.itemSort=itemSort;
	}
	
	public BaseTableItem(String itemLabel, String itemColumn, String itemField, String itemSortField, int itemSort, HeaderTableItem header){
		this.itemLabel=itemLabel;
		this.itemColumn=itemColumn;
		this.itemField=itemField;
		this.itemSortField=itemSortField;
		this.itemSort=itemSort;
		this.header=header;
		
		this.header.getItems().add(this);
	}
	
	public BaseTableItem(String itemLabel, String itemColumn, String itemField, String itemSortField, String itemFiltField, int itemSort){
		this.itemLabel=itemLabel;
		this.itemColumn=itemColumn;
		this.itemField=itemField;
		this.itemSortField=itemSortField;
		this.itemFiltField=itemFiltField;
		this.itemSort=itemSort;
	}
	
	public String getItemLabel(){
		return this.itemLabel;
	}
	public void setItemLabel(String itemLabel){
		this.itemLabel=itemLabel;
	}
	
	public String getItemColumn(){
		return this.itemColumn;
	}
	public void setItemColumn(String itemColumn){
		this.itemColumn=itemColumn;
	}
	
	public String getItemField(){
		return this.itemField;
	}
	public void setItemField(String itemField){
		this.itemField=itemField;
	}
	
	public String getItemSortField(){
		if(this.itemSortField==null){
			this.itemSortField=this.itemField;
		}
		return this.itemSortField;
	}
	public void setItemSortField(String itemSortField){
		this.itemSortField=itemSortField;
	}
	
	public String getItemFiltField(){
		if(this.itemFiltField==null){
			this.itemFiltField=this.itemField;
		}
		return this.itemFiltField;
	}
	public void setItemFiltField(String itemFiltField){
		this.itemFiltField=itemFiltField;
	}
	
	public int getItemSort(){
		return this.itemSort;
	}
	public void setItemSort(int itemSort){
		this.itemSort=itemSort;
	}
	
	public HeaderTableItem getHeader(){
		return this.header;
	}
	public void setHeader(HeaderTableItem header){
		this.header=header;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if ( ! (o instanceof BaseTableItem)) {
			return false;
		}
		BaseTableItem other = (BaseTableItem) o;
		return this.itemField.equals(other.itemField);
	}
	
	@Override
	public int hashCode() {
		return this.itemField.hashCode()
			 ^ this.itemField.hashCode()
			;
	}
}
