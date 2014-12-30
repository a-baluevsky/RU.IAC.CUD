package iac.grn.infosweb.context.mc.rol;

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
 

@Name("rolUsrDataModel")
 public class RolUsrDataModel extends BaseDataModel<BaseItem, Long> {
	
@Logger private Log log;
	
	@In(create=true)
	private RolUsrDataProvider rolUsrDataProvider;
	
	
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
		//идёт вместе с - в filter/Acti/on(): setAudi/tList(nu/ll);
	
			log.info("ugroupUsrDataModel:walk:cachedItems:02");
			 this.cachedItems=getDataProvider().getItemsByrange(firstRow, numberOfRows, null, true);
		 
		log.info("ugroupUsrDataModel:walk:cachedItems:03");
		if(this.cachedItems!=null){
		  for (BaseItem itemRolUsr:cachedItems) {
			wrappedKeys.add(itemRolUsr.getBaseId());
			wrappedData.put(itemRolUsr.getBaseId(), itemRolUsr);  
				 
		    visitor.process(context, itemRolUsr.getBaseId(), argument);
			 
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
		
		String remoteAuditRolUsr = FacesContext.getCurrentInstance().getExternalContext()
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
				if(remoteAuditRolUsr==null){
					log.info("usrDataModel:getRowCount:03_+");
					return 0;
				}
				if(remoteAuditRolUsr!=null &&
					("rowSelectFact".equals(remoteAuditRolUsr)/*||
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
		 
		 RolUsrStateHolder rolUsrStateHolder = (RolUsrStateHolder)Component.getInstance("rolUsrStateHolder", ScopeType.SESSION);
		 rolUsrStateHolder.clearFilters();
		 
		 RolUsrManager ugroupUsrManager = (RolUsrManager)Component.getInstance("rolUsrManager", ScopeType.EVENT);
		 ugroupUsrManager.setAuditList(null);
	}
	protected void resetDataProvider() {
		this.rolUsrDataProvider = null;
	}

	public RolUsrDataProvider getDataProvider() {
		  log.info("getDataProvider:01");
		if (rolUsrDataProvider == null) {
			log.info("getDataProvider:02");
		}
		return rolUsrDataProvider;
	}
	}
