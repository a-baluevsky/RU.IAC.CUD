package ru.spb.iac.cud.reports;

import iac.cud.infosweb.dataitems.BaseParamItem;
import iac.cud.infosweb.dataitems.ReportDownloadItem;

import javax.ejb.Local;

/**
 * @author bubnov
 */
@Local
public interface ReportsManagerLocal {

	public int create_report(BaseParamItem paramMap) throws Exception;

	public ReportDownloadItem download_report(String reportCode,
			String reportType) throws Exception;

}
