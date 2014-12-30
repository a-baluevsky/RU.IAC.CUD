package iac.grn.ramodule.entity.zags;

import java.io.Serializable;

import java.util.Date;


 public class VAuditReport implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private Long sessionId;
	
	private Long entryid;
	
	private Long action;

	private Long auditType;

	private String authPrivileges;

	private String clientidentifier;

	private String commentText;

	private String dbUser;

	private Long dbid;

	private String econtextId;

	private String execType;

	private String extName;

	private Date extendedTimestamp;

	private String globalUid;

	private String grantee;

	private Long instanceNumber;

	private String newName;

	private String newOwner;

	private String objEditionName;

	private String objectName;

	private String objectSchema;

	private String osHost;

	private String osPrivilege;

	private String osProcess;

	private String osUser;

	private String policyName;

	private Long privUsed;

	private Long proxySessionid;

	private Long returncode;

	private Long scn;

	private String sesActions;

	private String sqlBind;

	private String sqlText;

	private Long statementType;

	private Long statementid;

	private String terminal;

	private byte[] transactionid;

	private String transactionidValue;
	
	private String selected;
	
	private String ipAddress;

	private String module;
	
	
	
    public VAuditReport() {
     }

    public Long getAction() {
    	
		return this.action;
	}

	public void setAction(Long action) {
		this.action = action;
	}

	public Long getAuditType() {
		return this.auditType;
	}

	public void setAuditType(Long auditType) {
		this.auditType = auditType;
	}

	public String getAuthPrivileges() {
		return this.authPrivileges;
	}

	public void setAuthPrivileges(String authPrivileges) {
		this.authPrivileges = authPrivileges;
	}

	public String getClientidentifier() {
		return this.clientidentifier;
	}

	public void setClientidentifier(String clientidentifier) {
		this.clientidentifier = clientidentifier;
	}

	public String getCommentText() {
		return this.commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}

	public String getDbUser() {
		return this.dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public Long getDbid() {
		return this.dbid;
	}

	public void setDbid(Long dbid) {
		this.dbid = dbid;
	}

	public String getEcontextId() {
		return this.econtextId;
	}

	public void setEcontextId(String econtextId) {
		this.econtextId = econtextId;
	}

	public Long getEntryid() {
		return this.entryid;
	}

	public void setEntryid(Long entryid) {
		this.entryid = entryid;
	}

	public String getExecType() {
		return this.execType;
	}

	public void setExecType(String execType) {
		this.execType = execType;
	}

	public String getExtName() {
		return this.extName;
	}

	public void setExtName(String extName) {
		this.extName = extName;
	}

	public Date getExtendedTimestamp() {
		return this.extendedTimestamp;
	}

	public void setExtendedTimestamp(Date extendedTimestamp) {
		this.extendedTimestamp = extendedTimestamp;
	}

	public String getGlobalUid() {
		return this.globalUid;
	}

	public void setGlobalUid(String globalUid) {
		this.globalUid = globalUid;
	}

	public String getGrantee() {
		return this.grantee;
	}

	public void setGrantee(String grantee) {
		this.grantee = grantee;
	}

	public Long getInstanceNumber() {
		return this.instanceNumber;
	}

	public void setInstanceNumber(Long instanceNumber) {
		this.instanceNumber = instanceNumber;
	}

	public String getNewName() {
		return this.newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public String getNewOwner() {
		return this.newOwner;
	}

	public void setNewOwner(String newOwner) {
		this.newOwner = newOwner;
	}

	public String getObjEditionName() {
		return this.objEditionName;
	}

	public void setObjEditionName(String objEditionName) {
		this.objEditionName = objEditionName;
	}

	public String getObjectName() {
		return this.objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getObjectSchema() {
		return this.objectSchema;
	}

	public void setObjectSchema(String objectSchema) {
		this.objectSchema = objectSchema;
	}

	public String getOsHost() {
		return this.osHost;
	}

	public void setOsHost(String osHost) {
		this.osHost = osHost;
	}

	public String getOsPrivilege() {
		return this.osPrivilege;
	}

	public void setOsPrivilege(String osPrivilege) {
		this.osPrivilege = osPrivilege;
	}

	public String getOsProcess() {
		return this.osProcess;
	}

	public void setOsProcess(String osProcess) {
		this.osProcess = osProcess;
	}

	public String getOsUser() {
		return this.osUser;
	}

	public void setOsUser(String osUser) {
		this.osUser = osUser;
	}

	public String getPolicyName() {
		return this.policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public Long getPrivUsed() {
		return this.privUsed;
	}

	public void setPrivUsed(Long privUsed) {
		this.privUsed = privUsed;
	}

	public Long getProxySessionid() {
		return this.proxySessionid;
	}

	public void setProxySessionid(Long proxySessionid) {
		this.proxySessionid = proxySessionid;
	}

	public Long getReturncode() {
		return this.returncode;
	}

	public void setReturncode(Long returncode) {
		this.returncode = returncode;
	}

	public Long getScn() {
		return this.scn;
	}

	public void setScn(Long scn) {
		this.scn = scn;
	}

	public String getSesActions() {
		return this.sesActions;
	}

	public void setSesActions(String sesActions) {
		this.sesActions = sesActions;
	}

	public Long getSessionId() {
       return this.sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	public String getSqlBind() {
		return this.sqlBind;
	}

	public void setSqlBind(String sqlBind) {
		this.sqlBind = sqlBind;
	}

	public String getSqlText() {
		return this.sqlText;
	}

	public void setSqlText(String sqlText) {
		this.sqlText = sqlText;
	}

	public Long getStatementType() {
		return this.statementType;
	}

	public void setStatementType(Long statementType) {
		this.statementType = statementType;
	}

	public Long getStatementid() {
		return this.statementid;
	}

	public void setStatementid(Long statementid) {
		this.statementid = statementid;
	}

	public String getTerminal() {
		return this.terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public byte[] getTransactionid() {
		return this.transactionid;
	}

	public void setTransactionid(byte[] transactionid) {
		this.transactionid = transactionid;
	}
	public String getTransactionidValue() {
		if(this.transactionid!=null){
			this.transactionidValue=this.transactionid.toString();
		}
		return this.transactionidValue;
	}
	public void setTransactionidValue(String transactionidValue) {
		this.transactionidValue = transactionidValue;
	}
	public String getSelected() {
		if(this.selected==null){
			return "false";
		}else{
		    return this.selected;
		}
	}
	public void setSelected (String selected) {
		this.selected = selected;
	}
	public String getId() {
	    return this.id;
	}
	public void setId (String id) {
		this.id = id;
	}
	public String getIpAddress() {
		return this.ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getModule() {
		return this.module;
	}

	public void setModule(String module) {
		this.module = module;
	}
	@Override
	public String toString() {
		return this.id;
	}
}