package iac.grn.infosweb.context.mc.usr;

import iac.cud.infosweb.dataitems.BaseItem;
import iac.grn.infosweb.session.table.BaseDataModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
 

@Name("usrDataModel")
 public class UsrDataModel extends BaseDataModel<BaseItem, Long>  {
	
	@Logger private Log log;
	
	@In(create=true)
	private UsrDataProvider usrDataProvider;
	
		
	private static final long serialVersionUID = -1956179896877538628L;

	/**
	 * This is main part of Visitor pattern. Method called by framework many times during request processing. 
	 */
	@Override
	public void walk(FacesContext context, DataVisitor visitor, Range range, Object argument) throws IOException {
		
		log.info("walk:01:start");
	
	
		
		int firstRow = ((SequenceRange)range).getFirstRow();
		int numberOfRows = ((SequenceRange)range).getRows();
		
		
		wrappedKeys = new ArrayList<Long>();
		if(this.cachedItems==null){
			log.info("walk:cachedItems:02");
			 this.cachedItems=getDataProvider().getItemsByrange(firstRow, numberOfRows, null, true);
		}
		log.info("walk:cachedItems:03");
		if(this.cachedItems!=null){
		  for (BaseItem itemUsr:cachedItems) {
			wrappedKeys.add(itemUsr.getBaseId());
			wrappedData.put(itemUsr.getBaseId(), itemUsr);  
				 
		    visitor.process(context, itemUsr.getBaseId(), argument);
			 
		 }
		}
		log.info("walk:end");
	}
	/**
	 * This method must return actual data rows count from the Data Provider. It is used by pagination control
	 * to determine total number of data items.
	 */
	 // better to buffer row count locally
	@Override
	public int getRowCount() {
		
	log.info("auditDataModel:getRowCount:01");
		
		String remoteAuditUsr = FacesContext.getCurrentInstance().getExternalContext()
		         .getRequestParameterMap()
		         .get("remoteAudit");
		String  auditListCount = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("auditListCount");
	
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
				if(remoteAuditUsr==null){
					log.info("getRowCount:03_+");
					return 0;
				}
				if(remoteAuditUsr!=null &&
					("rowSelectFact".equals(remoteAuditUsr)/*||
				    "selRecAllFact".equals(remoteAudit)||
					"clRecAllFact".equals(remoteAudit)||
					"clSelOneFact".equals(remoteAudit)*/)&&
					auditListCount!=null){
					rowCount = new Integer(auditListCount);
				}else{
					rowCount = new Integer(
							getDataProvider().getRowCount());
				}
				log.info("getRowCount:03:rowCount:"+rowCount);
			}
		}
		return rowCount.intValue();
	}
	

	 public void filterAction() {
		 log.info("filterAction");
		 this.cachedItems = null;
		 this.rowCount=null;
		 this.flagAction=1;
		 
		 UsrStateHolder usrStateHolder = (UsrStateHolder)Component.getInstance("usrStateHolder", ScopeType.SESSION);
		 usrStateHolder.clearFilters();
	}
		
	protected void resetDataProvider() {
		this.usrDataProvider = null;
	}

	public UsrDataProvider getDataProvider() {
		  log.info("getDataProvider:01");
		if (usrDataProvider == null) {
			log.info("getDataProvider:02");
			//dataPr/ovider = looku/pInContext(auctio/nDataProviderExpr/essionStr/ing, AuctionDataProvider.class);
		}
		return usrDataProvider;
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
