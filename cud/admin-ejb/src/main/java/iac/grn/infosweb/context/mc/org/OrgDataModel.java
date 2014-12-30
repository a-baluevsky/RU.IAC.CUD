package iac.grn.infosweb.context.mc.org;

import iac.cud.infosweb.dataitems.BaseItem;
import iac.grn.infosweb.session.table.BaseDataModel;

import java.io.IOException;
import java.util.ArrayList;
import javax.faces.context.FacesContext;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
 

@Name("orgDataModel")
 public class OrgDataModel extends BaseDataModel<BaseItem, Long> {
	
	@Logger private Log log;
	
	@In(create=true)
	private OrgDataProvider orgDataProvider;
	
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
			log.info("walk:cachedItems:02:");
			 this.cachedItems=getDataProvider().getItemsByrange(firstRow, numberOfRows, null, true);
		}
		log.info("walk:cachedItems:03:");
		if(this.cachedItems!=null){
		  for (BaseItem itemOrg:cachedItems) {
			wrappedKeys.add(itemOrg.getBaseId());
			wrappedData.put(itemOrg.getBaseId(), itemOrg);  
				 
		    visitor.process(context, itemOrg.getBaseId(), argument);
			 
		 }
		}
		log.info("walk:end");
	}
	/**
	 * This method must return actual data rows count from the Data Provider. It is used by pagination control
	 * to determine total number of data items.
	 */
	@Override
	public int getRowCount() {
		
	log.info("auditDataModel:getRowCount:01");
		
		String remoteAuditOrg = FacesContext.getCurrentInstance().getExternalContext()
		         .getRequestParameterMap()
		         .get("remoteAudit");
		String  auditListCount = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("auditListCount");
		log.info("auditDataModel:getRowCount:remoteAudit:"+remoteAuditOrg);
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
				if(remoteAuditOrg==null){
					log.info("getRowCount:03_+");
					return 0;
				}
				if(remoteAuditOrg!=null &&
					("rowSelectFact".equals(remoteAuditOrg)/*||
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
		 
	
	}
		
	protected void resetDataProvider() {
		this.orgDataProvider = null;
	}

	public OrgDataProvider getDataProvider() {
		  log.info("getDataProvider:01");
		if (orgDataProvider == null) {
			log.info("getDataProvider:02");
			//dat/aProvider = lookupInCon/text(auction/DataProviderExpressionSt/ring, AuctionDataProvider.class);
		}
		return orgDataProvider;
	}
	
	
}
