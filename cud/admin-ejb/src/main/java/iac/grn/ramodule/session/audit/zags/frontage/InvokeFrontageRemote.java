package iac.grn.ramodule.session.audit.zags.frontage;
import iac.cud.infosweb.dataitems.AuditReportsItem;
import iac.cud.infosweb.dataitems.AuditTransferItem;
import iac.grn.ramodule.entity.zags.VAuditReport;
import java.util.Date;
import java.util.HashMap; 
import java.util.List;

public interface InvokeFrontageRemote {
	    // InvokeHandler
		// public String invoke(String type);
		 public VAuditReport invokeAuditBean(String type, Long sessionId, Long entryid)throws Exception;
		 public List<VAuditReport> invokeAuditList(String type)throws Exception;
		 public List<VAuditReport> invokeAuditListJPA(String type, int firstRow,
				 int numberOfRows, HashMap<String,String> fFHM)throws Exception;
		 public Long invokeAuditCountJPA(String type,
				 HashMap<String,String> fFHM)throws Exception;
		 public void auditStatusChange(String type) throws Exception;
		 public int auditStatus() throws Exception;
		 public AuditTransferItem<VAuditReport> auditStatusListCount(String type,
	 			 HashMap<String,String> fFHM, int firstRow, int numberOfRows) throws Exception;
		
		 public void auditClear(Date d1, Date d2)throws Exception;

		 // InvokeHandlerSearch
		 public String invoke(String type)throws Exception;
		 public List<VAuditReport> invokeSearchResults(String type, String searchPattern,
					 int firstRow, int numberOfRows)throws Exception;
		 public Long invokeSearchCount(String type, String searchPattern)throws Exception;

		 //InvokeHandlerReports
		 public List<AuditReportsItem> getReportItemsList(
					String type,
					int cD,
					int cU,
					int cT,
					String date1,
					String date2)throws Exception;
}
