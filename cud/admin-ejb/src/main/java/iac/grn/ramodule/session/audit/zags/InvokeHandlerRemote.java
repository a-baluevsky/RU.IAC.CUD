package iac.grn.ramodule.session.audit.zags;
import java.util.Date;
import java.util.HashMap; 
import java.util.List;

import iac.grn.ramodule.entity.zags.VAuditReport;

public interface InvokeHandlerRemote {

	 public String invoke(String type);
	 public VAuditReport invokeAuditBean(String type, Long sessionId, Long entryid);
	 public List<VAuditReport> invokeAuditList(String type)throws Exception;
	 public List<VAuditReport> invokeAuditListJPA(String type, int firstRow,
			 int numberOfRows, HashMap<String,String> fFHM)throws Exception;
	 public Long invokeAuditCountJPA(String type,
			 HashMap<String,String> fFHM)throws Exception;
	 public void auditStatusChange(String type) throws Exception;
	 public int auditStatus() throws Exception;
	 public void auditClear(Date d1, Date d2)throws Exception;
}
