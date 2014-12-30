package iac.grn.infosweb.context.mc.arm;

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
 

@Name("armUsrDataModel")
 public class ArmUsrDataModel  extends BaseDataModel<BaseItem, Long>  {
	
@Logger private Log log;
	
	@In(create=true)
	private ArmUsrDataProvider armUsrDataProvider;
	
	private static final long serialVersionUID = -1956179896877538628L;

	/**
	 * This is main part of Visitor pattern. Method called by framework many times during request processing. 
	 */
	@Override
	public void walk(FacesContext context, DataVisitor visitor, Range range, Object argument) throws IOException {
		
		log.info("ugroupUsrDataModel:walk:01:start");
	
		
		int firstRow = ((SequenceRange)range).getFirstRow();
		int numberOfRows = ((SequenceRange)range).getRows();
		
		log.info("ugroupUsrDataModel:walk:firstRow:"+firstRow);
		log.info("ugroupUsrDataModel:walk:numberOfRows:"+numberOfRows);
		log.info("ugroupUsrDataModel:walk:cachedItems:01:"+(this.cachedItems==null));
		
		wrappedKeys = new ArrayList<Long>();
		//!!!важно закомментили 24.12.13
		//идёт вместе с - в filterAction(): setAuditList(null);
	
		
			log.info("ugroupUsrDataModel:walk:cachedItems:02");
			 this.cachedItems=getDataProvider().getItemsByrange(firstRow, numberOfRows, null, true);
		
		log.info("ugroupUsrDataModel:walk:cachedItems:03");
		if(this.cachedItems!=null){
		  for (BaseItem itemArmUsr:cachedItems) {
			wrappedKeys.add(itemArmUsr.getBaseId());
			wrappedData.put(itemArmUsr.getBaseId(), itemArmUsr);  
				 
		    visitor.process(context, itemArmUsr.getBaseId(), argument);
			 
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
		
		String remoteAuditArmUsr = FacesContext.getCurrentInstance().getExternalContext()
		         .getRequestParameterMap()
		         .get("remoteAudit");
		String  auditListCount = FacesContext.getCurrentInstance().getExternalContext()
			        .getRequestParameterMap()
			        .get("auditListCount");
		log.info("usrDataModel:getRowCount:remoteAudit:"+remoteAuditArmUsr);
		log.info("usrDataModel:getRowCount:auditListCount:"+auditListCount);
		log.info("usrDataModel:getRowCount:flagAction:"+this.flagAction);
		
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
				if(remoteAuditArmUsr==null){
					log.info("usrDataModel:getRowCount:03_+");
					return 0;
				}
				if(remoteAuditArmUsr!=null &&
					("rowSelectFact".equals(remoteAuditArmUsr)/*||
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
		 
		 ArmUsrStateHolder armUsrStateHolder = (ArmUsrStateHolder)Component.getInstance("armUsrStateHolder", ScopeType.SESSION);
		 armUsrStateHolder.clearFilters();
		 
		 ArmUsrManager ugroupUsrManager = (ArmUsrManager)Component.getInstance("armUsrManager", ScopeType.EVENT);
		 ugroupUsrManager.setAuditList(null);
	}
	
	protected void resetDataProvider() {
		this.armUsrDataProvider = null;
	}

	public ArmUsrDataProvider getDataProvider() {
		  log.info("getDataProvider:01");
		if (armUsrDataProvider == null) {
			log.info("getDataProvider:02");
			}
		return armUsrDataProvider;
	}
}
