package iac.grn.ramodule.session.audit.zags.search;

import iac.grn.ramodule.entity.zags.VAuditReport;

import java.util.List;

public interface IndexerServiceRemote {

	public void purgeAll();
	public List<VAuditReport> getSearchResults(String searchPattern,
			 int firstRow, int numberOfRows);
	public Long getSearchCount(String searchPattern);

	public void test();
	public void index_pro() ;
}
