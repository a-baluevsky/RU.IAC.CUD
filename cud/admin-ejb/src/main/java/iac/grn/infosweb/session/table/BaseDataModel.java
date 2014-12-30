package iac.grn.infosweb.session.table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.ajax4jsf.model.SerializableDataModel;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.richfaces.model.FilterField;
import org.richfaces.model.SortField2;

@Name("baseDataModel")
 public class BaseDataModel<T, U> extends SerializableDataModel {

	@Logger protected Log log;
	
	protected U currentPk;
	protected Map<U, T> wrappedData = new HashMap<U, T>();
	protected List<U> wrappedKeys = null;

	
	protected List<T> cachedItems=new ArrayList<T>();
	protected Integer rowCount;
	
	protected HashMap<String, String> filterColumnValues = new HashMap<String, String>();
	 
	protected int flagAction=0;
 
	private static final long serialVersionUID = -1956179896877538628L;

	protected String modelType=null;
	
	public void init(String modelType){
		log.info("tableDataModel:init:modelType:"+modelType);
		this.modelType=modelType;
	}
	
	public String getModelType(){
		return this.modelType;
	}
	/**
	 * This method never called from framework.
	 * (non-Javadoc)
	 * @see org.ajax4jsf.model.ExtendedDataModel#getRowKey()
	 */
	@Override
	public Object getRowKey() {
	//	log.info("getRowKey:currentPk:"+currentPk);
		return currentPk;
	}

	/**
	 * This method normally called by Visitor before request Data Row.
	 */
	@Override
	public void setRowKey(Object key) {
	//	log.info("setRowKey:key:"+key);
		this.currentPk = (U) key;
		
	}
	/**
	 * This is main part of Visitor pattern. Method called by framework many times during request processing. 
	 */
	@Override
	public void walk(FacesContext context, DataVisitor visitor, Range range, Object argument) throws IOException {
		
		log.info("walk:01:start");
	
		
	
		
		int firstRow = ((SequenceRange)range).getFirstRow();
		int numberOfRows = ((SequenceRange)range).getRows();
		
		log.info("walk:firstRow:"+firstRow);
		log.info("walk:numberOfRows:"+numberOfRows);
		log.info("walk:cachedItems:01"+(this.cachedItems==null));
		
		wrappedKeys = new ArrayList<U>();
		if(this.cachedItems==null){
			log.info("walk:cachedItems:02");
			 this.cachedItems=findObjects(firstRow, numberOfRows, null, true, 
					 modelType);
			
		}
		log.info("walk:cachedItems:03");
		if(this.cachedItems!=null){
		  for (T item:cachedItems) {
			  
		
			  
			wrappedKeys.add(getId(item));
			wrappedData.put(getId(item), item);  
	
			 
		    visitor.process(context, getId(item), argument);
		   
		 }
		}
		log.info("walk:end");
	}
	public int getNumRecords(String modelType){
		return 0;
	}
	
	public U getId(T object){
		return null;
	}
	
	public List<T> findObjects(int firstRow, int numberOfRows, String sortField, 
            boolean ascending, String modelType){
		return null;
	}
	
	public int getRowCount2() {
		log.info("auditDataModel:getRowCount2!!!");
		return getRowCount();
	}
	/**
	 * This method must return actual data rows count from the Data Provider. It is used by pagination control
	 * to determine total number of data items.
	 */
	 // better to buffer row count locally
	@Override
	public int getRowCount() {
		
	
		log.info("auditDataModel:getRowCount:01");
		
		String remoteAudit = FacesContext.getCurrentInstance().getExternalContext()
		         .getRequestParameterMap()
		         .get("remoteAudit");
		String  auditListCount = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("auditListCount");
		log.info("auditDataModel:getRowCount:remoteAudit:"+remoteAudit);
		log.info("auditDataModel:getRowCount:auditListCount:"+auditListCount);
	
		if(this.flagAction==0){
			if (rowCount==null) {
				if(auditListCount!=null){
				   rowCount = new Integer(auditListCount);
				 }else{
				   rowCount = 0;
				 }
				log.info("getRowCount:02:rowCount:"+rowCount);
			}
		}else{
			if (rowCount==null) {
				// При selRecAllFact, clRecAllFact, clSelOneFact запросах
				// dataScroller не рендерится, а в параметрах
				// rowCount и так определяется через param['auditListCount']
				if(("rowSelectFact".equals(remoteAudit)/*||
				    "selRecAllFact".equals(remoteAudit)||
					"clRecAllFact".equals(remoteAudit)||
					"clSelOneFact".equals(remoteAudit)*/)&&
					auditListCount!=null){
					rowCount = new Integer(auditListCount);
				}else{
					rowCount = getNumRecords(modelType);
				}
				log.info("getRowCount:03:rowCount:"+rowCount);
			}
		}
		return rowCount.intValue();
	}
	/**
	 * This is main way to obtain data row. It is intensively used by framework. 
	 * We strongly recommend use of local cache in that method. 
	 */
	@Override
	public T getRowData() {
		if (currentPk==null) {
			return null;
		} else {
			T ret = wrappedData.get(currentPk);
			if (ret==null) {
			
				return ret;
			} else {
				return ret;
			}
		}
	}
	//@Override
	 public void modify(List<FilterField> filterFields, List<SortField2> sortFields) {
		 log.info("!!!modify!!!");
	
		
	 }

	 public void filterAction() {
		 log.info("filterAction");
		 this.cachedItems = null;
		 this.rowCount=null;
		 this.flagAction=1;
		 
		}
	/**
	 * Unused rudiment from old JSF staff.
	 */
	@Override
	public int getRowIndex() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unused rudiment from old JSF staff.
	 */
	@Override
	public Object getWrappedData() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Never called by framework.
	 */
	@Override
	public boolean isRowAvailable() {
		 
		if (currentPk==null) {
			return false;
		} else {
			 
			if(wrappedKeys!=null){
				 
			}
			
			return true;
		
		}
	}

	/**
	 * Unused rudiment from old JSF staff.
	 */
	@Override
	public void setRowIndex(int rowIndex) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unused rudiment from old JSF staff.
	 */
	@Override
	public void setWrappedData(Object data) {
		throw new UnsupportedOperationException();
	}

	/**
	 * This method suppose to produce SerializableDataModel that will be serialized into View State and used on a post-back.
	 * In current implementation we just mark current model as serialized. In more complicated cases we may need to 
	 * transform data to actually serialized form.
	 */
	public  SerializableDataModel getSerializableDataModel(Range range) {
		
		log.info("getSerializableModel");
		
		if (wrappedKeys!=null) {
			return this; 
		} else {
			return null;
		}
	}
	

	
	private String auctionDataModelExpressionString;

	private String auctionDataProviderExpressionString;

	
	/**
	 * This is helper method that is called by framework after model update. 
	 * In must delegate actual database update to Data Provider.
	 */
	@Override
	public void update() {
	
	}
	
	
	
	public String getAuctionDataModelExpressionString() {
		return auctionDataModelExpressionString;
	}
	public void setAuctionDataModelExpressionString(
			String auctionDataModelExpressionString) {
		this.auctionDataModelExpressionString = auctionDataModelExpressionString;
	}
	public String getAuctionDataProviderExpressionString() {
		return auctionDataProviderExpressionString;
	}
	public void setAuctionDataProviderExpressionString(
			String auctionDataProviderExpressionString) {
		this.auctionDataProviderExpressionString = auctionDataProviderExpressionString;
	}
	    
	   public Map<String, String> getFilterColumnValues() {
		   if(filterColumnValues!=null){
		       log.info("getFilterColumnValues:01:"+filterColumnValues.size());
		   }else{
			   log.info("getFilterColumnValues:02:");
		   }
	        return filterColumnValues;
	    }
	   public void setFilterColumnValues(HashMap<String, String> filterColumnValues){
		   if(filterColumnValues!=null){
		       log.info("setFilterColumnValues:01:"+filterColumnValues.size());
		   }else{
			   log.info("setFilterColumnValues:02:");
		   }
		   this.filterColumnValues=filterColumnValues;
	   }
	
	
}

