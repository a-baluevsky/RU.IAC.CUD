package iac.grn.infosweb.context.mc.perm;

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
 

@Name("permDataModel")
 public class PermDataModel extends BaseDataModel<BaseItem, Long>{
	
	@Logger private Log log;
	
	@In(create=true)
	private PermDataProvider permDataProvider;
	
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
		  for (BaseItem itemPerm:cachedItems) {
			wrappedKeys.add(itemPerm.getBaseId());
			wrappedData.put(itemPerm.getBaseId(), itemPerm);  
		
			 
		    visitor.process(context, itemPerm.getBaseId(), argument);
			 
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
		
		String remoteAuditPerm = FacesContext.getCurrentInstance().getExternalContext()
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
				// ��� selRecAllFact, clRecAllFact, clSelOneFact ��������
				// dataScroller �� ����������, � � ����������
				// rowCount � ��� ������������ ����� param['auditListCount']
				if(remoteAuditPerm==null){
					log.info("getRowCount:03_+");
					return 0;
				}
				if(remoteAuditPerm!=null &&
					("rowSelectFact".equals(remoteAuditPerm)/*||
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
		this.permDataProvider = null;
	}

	public PermDataProvider getDataProvider() {
		  log.info("getDataProvider:01");
		if (permDataProvider == null) {
			log.info("getDataProvider:02");
		}
		return permDataProvider;
	}
	
}
