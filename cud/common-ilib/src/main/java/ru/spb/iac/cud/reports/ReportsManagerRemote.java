package ru.spb.iac.cud.reports;

import iac.cud.infosweb.dataitems.BaseParamItem;
import iac.cud.infosweb.dataitems.ReportDownloadItem;

import javax.ejb.Remote;

@Remote
public interface ReportsManagerRemote {

	public int create_report(BaseParamItem paramMap) throws Exception;

	public ReportDownloadItem download_report(String reportCode,
			String reportType) throws Exception;
}
