package ru.spb.iac.cud.reports;

import iac.cud.infosweb.dataitems.BaseParamItem;
import iac.cud.infosweb.dataitems.ReportDownloadItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap; import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jasperreports.engine.JRParameter;

/**
 * @author bubnov
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
 public class ReportsManager implements ReportsManagerLocal,
		ReportsManagerRemote {

	
	private static final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportsManager.class);

	public ReportsManager() {
	}

	public int create_report(BaseParamItem paramMap) throws Exception {

		LOGGER.debug("create_report:01");

		final String reportCodeFinal = (String) paramMap.get("reportCode");
		

		try {

			if (TaskProcessor.getControls().containsKey(reportCodeFinal)) {
				LOGGER.debug("create_report:return");
				return 0;
			}

			Date reportDate1 = (Date) paramMap.get("reportDate1");
			Date reportDate2 = (Date) paramMap.get("reportDate2");

			LOGGER.debug("create_report:02:" + reportDate1);

			DateFormat df = new SimpleDateFormat("dd.MM.yy ");

			String reportDateValue = null;

			if (reportDate1 != null && reportDate2 != null) {
				reportDateValue = "( с " + df.format(reportDate1) + " по "
						+ df.format(reportDate2) + " )";
			} else if (reportDate1 != null) {
				reportDateValue = "( с " + df.format(reportDate1) + " )";
				reportDate2 = new Date();

			} else if (reportDate2 != null) {

				reportDateValue = "( по " + df.format(reportDate2) + " )";

				Calendar cln = Calendar.getInstance();
				cln.set(Calendar.YEAR, 2000);
				reportDate1 = cln.getTime();

			} else {

				reportDateValue = " ";

				Calendar cln = Calendar.getInstance();
				cln.set(Calendar.YEAR, 2000);
				reportDate1 = cln.getTime();
				reportDate2 = new Date();
			}

			String reportName = (String) paramMap.get("reportName");

			final Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ReportTitle", reportName);
			parameters.put("ReportDateValue", reportDateValue);

			parameters.put("ReportDate1", reportDate1);
			parameters.put("ReportDate2", reportDate2);

			
			parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
			 

			TaskProcessor.getControls().put(reportCodeFinal, "1");

			scheduler.schedule(new Runnable() {

				public void run() {

					try {

						CUDQueryAppFull caf = new CUDQueryAppFull(
								reportCodeFinal, parameters);
						caf.create_report();

					} catch (Exception e) {
						LOGGER.error("create_report:error:", e);
					} finally {
						TaskProcessor.getControls().remove(reportCodeFinal);

					}
				}
			}, 0, TimeUnit.MILLISECONDS);

			LOGGER.debug("create_report:02");

			return 1;

		} catch (Exception e) {

			TaskProcessor.getControls().remove(reportCodeFinal);

			LOGGER.error("create_report:error_2:", e);

			return -1;
		}
	}

	public ReportDownloadItem download_report(String reportCode,
			String reportType) throws Exception {

		ReportDownloadItem result = null;

		try {
			LOGGER.debug("download_report:01");

			if (TaskProcessor.getControls().containsKey(reportCode)) {

				// формирование ещё не завершено

				LOGGER.debug("download_report:return");
				result = new ReportDownloadItem();
				result.setFlagExec(-1);
				return result;
			}

			CUDQueryAppFull caf = new CUDQueryAppFull(reportCode);
			result = caf.download_report(reportType);

		} catch (Exception e) {
			LOGGER.error("download_report:error:", e);

			if (result == null) {
				// техническая ошибка
				result = new ReportDownloadItem();
				result.setFlagExec(-2);
			}
		}

		return result;
	}
}