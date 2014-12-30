package iac.grn.infosweb.context.mc.ugroup;

import iac.cud.infosweb.dataitems.BaseItem;
import iac.grn.infosweb.session.table.BaseDataModel;

import java.io.IOException;
import java.util.ArrayList;
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
 

@Name("ugroupUsrDataModel")
 public class UgroupUsrDataModel extends BaseDataModel<BaseItem, Long>   {
	
@Logger private Log log;
	
	@In(create=true)
	private UgroupUsrDataProvider ugroupUsrDataProvider;
	
		
	private static final long serialVersionUID = -1956179896877538628L;

	/**
	 * This is main part of Visitor pattern. Method called by framework many times during request processing. 
	 */
	@Override
	public void walk(FacesContext context, DataVisitor visitor, Range range, Object argument) throws IOException {
		
		log.info("ugroupUsrDataModel:walk:01:start");
	
	
		
		int firstRow = ((SequenceRange)range).getFirstRow();
		int numberOfRows = ((SequenceRange)range).getRows();
		
		
		wrappedKeys = new ArrayList<Long>();
		//!!!важно закомментили 24.12.13
		//идёт вместе с - в filterAction(): setAuditList(null);
	
			log.info("ugroupUsrDataModel:walk:cachedItems:02");
			 this.cachedItems=getDataProvider().getItemsByrange(firstRow, numberOfRows, null, true);
		log.info("ugroupUsrDataModel:walk:cachedItems:03");
		if(this.cachedItems!=null){
		  for (BaseItem itemUgroupUsr:cachedItems) {
			wrappedKeys.add(itemUgroupUsr.getBaseId());
			wrappedData.put(itemUgroupUsr.getBaseId(), itemUgroupUsr);  
				 
		    visitor.process(context, itemUgroupUsr.getBaseId(), argument);
			 
		 }
		}
		log.info("usrDataModel:walk:end");
	}
	/**
	 * This method must return actual data rows count from the Data Provider. It is used by pagination control
	 * to determine total number of data items.
	 */
	@Override
	public int getRowCount() {
		
	log.info("usrDataModel:getRowCount:01");
		
		String remoteAuditUgroupUsr = FacesContext.getCurrentInstance().getExternalContext()
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
				log.info("usrDataModel:getRowCount:02:rowCount:"+rowCount);
			}
		}else{
			if (rowCount==null) {
				// При selRecAllFact, clRecAllFact, clSelOneFact запросах
				// dataScroller не рендерится, а в параметрах
				// rowCount и так определяется через param['auditListCount']
				if(remoteAuditUgroupUsr==null){
					log.info("usrDataModel:getRowCount:03_+");
					return 0;
				}
				if(remoteAuditUgroupUsr!=null &&
					("rowSelectFact".equals(remoteAuditUgroupUsr)/*||
				    "selRecAllFact".equals(remoteAudit)||
					"clRecAllFact".equals(remoteAudit)||
					"clSelOneFact".equals(remoteAudit)*/)&&
					auditListCount!=null){
					rowCount = new Integer(auditListCount);
				}else{
					rowCount = new Integer(
							getDataProvider().getRowCount());
				}
				log.info("usrDataModel:getRowCount:03:rowCount:"+rowCount);
			}
		}
		return rowCount.intValue();
	}
	 public void filterAction() {
		 log.info("usrDataModel:filterAction");
		 this.cachedItems = null;
		 this.rowCount=null;
		 this.flagAction=1;
		 
			 
		 UgroupUsrStateHolder ugroupUsrStateHolder = (UgroupUsrStateHolder)Component.getInstance("ugroupUsrStateHolder", ScopeType.SESSION);
		 ugroupUsrStateHolder.clearFilters();
		 
		 
		 UgroupUsrManager ugroupUsrManager = (UgroupUsrManager)Component.getInstance("ugroupUsrManager", ScopeType.EVENT);
		 ugroupUsrManager.setAuditList(null);
	}
	protected void resetDataProvider() {
		this.ugroupUsrDataProvider = null;
	}

	public UgroupUsrDataProvider getDataProvider() {
		  log.info("getDataProvider:01");
		if (ugroupUsrDataProvider == null) {
			log.info("getDataProvider:02");
			//data/Provider/ = lookupIn/Context(/auctionDataProviderExpression/String, AuctionDataP/rovider.class);
		}
		return ugroupUsrDataProvider;
	}
}
