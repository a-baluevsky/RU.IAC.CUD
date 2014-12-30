package ru.spb.iac.cud.reports;

import iac.cud.infosweb.dataitems.ReportDownloadItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOdsReportConfiguration;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.core.util.Configuration;

/**
 * @author bubnov
 */
 public class CUDQueryAppFull {

	private static String REPORT_JRXML = Configuration.getReportJRXML();
	
	private static String REPORT_JASPER = Configuration.getReportJASPER();

	private static String REPORT_JRNPRINT = Configuration.getReportJRNPRINT();

	 private static String REPORT_DOWNLOAD = Configuration.getReportDOWNLOAD();

	private String reportCode;

	private Map<String, Object> parameters;

	private static final Logger LOGGER = LoggerFactory.getLogger(CUDQueryAppFull.class);

	CUDQueryAppFull() {

	}

	CUDQueryAppFull(String reportCode) {
		this.reportCode = reportCode;
	}

	CUDQueryAppFull(String reportCode, Map<String, Object> parameters) {
		this.reportCode = reportCode;
		this.parameters = parameters;
	}

	public static void main(String[] args) {
		try {

			LOGGER.debug("main:01");

			CUDQueryAppFull caf = new CUDQueryAppFull();

			caf.create_report();

			LOGGER.debug("main:0100");

		} catch (Exception e) {
			LOGGER.error("main:error:", e);
		}
	}

	public void create_report() throws Exception {
		try {

			LOGGER.debug("create_report:01");

			LOGGER.debug("create_report:02");

			if (!new File(REPORT_JASPER + this.reportCode + ".jasper").exists()) {
				compile();
			}

			fill();

			xls();

			LOGGER.debug("create_report:03");

		} catch (Exception e) {
			LOGGER.error("create_report:error:", e);
			throw e;
		}
	}

	public ReportDownloadItem download_report(String reportType)
			throws Exception {

		ReportDownloadItem result = new ReportDownloadItem();
		byte[] content = null;
		File file = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = null;

		try {

			LOGGER.debug("download_report:01:" + REPORT_DOWNLOAD
					+ this.reportCode + "." + reportType);

		
			if ((file = new File(REPORT_DOWNLOAD + this.reportCode + "."
					+ reportType)).exists()) {

				LOGGER.debug("download_report:02");

				is = new FileInputStream(file);

				byte[] buffer = new byte[4096];

				int bytesRead;
				while ((bytesRead = is.read(buffer)) >= 0) {
					baos.write(buffer, 0, bytesRead);
				}

				content = baos.toByteArray();

			}

			if (content != null) {
				result.setContent(content);
				result.setFlagExec(1);
			} else {
				// нет ресурса
				result.setFlagExec(0);
			}

			LOGGER.debug("download_report:03");

		} catch (Exception e) {
			LOGGER.error("download_report:error:", e);
			throw e;
		} finally {
			try {
				if (baos != null) {
					baos.close();
				}
				if (is != null) {
					is.close();
				}

			} catch (Exception e) {

			}
		}

		return result;
	}

	protected Connection getDemoHsqldbConnection() throws JRException {
		Connection conn;

		try {

			//"jdbc:oracle:thin:CUD/CUD@192.168.2.28:1521:cudvm";
			String url = Configuration.getReportDBConnect();
			
			String driver = "oracle.jdbc.driver.OracleDriver";

			Class.forName(driver);
			conn = DriverManager.getConnection(url);
		} catch (ClassNotFoundException e) {
			throw new JRException(e);
		} catch (SQLException e) {
			throw new JRException(e);

		}

		return conn;
	}

	/**
	 *
	 */
	public void test() throws JRException {
		fillIgnorePagination();
		fill();
		pdf();
		xmlEmbed();
		xml();
		html();
		rtf();
		xls();
		jxl();
		csv();
		odt();
		ods();
		docx();
		xlsx();
		pptx();
		xhtml();
	}

	/**
	 *
	 */
	public void compile() throws JRException {
		JasperCompileManager.compileReportToFile(REPORT_JRXML + this.reportCode
				+ ".jrxml", REPORT_JASPER + this.reportCode + ".jasper");
		
	}

	public void fill() throws JRException {
		fill(false);
	}

	/**
	 *
	 */
	public void fillIgnorePagination() throws JRException {
		fill(true);
	}

	/**
	 *
	 */
	private void fill(boolean ignorePagination) throws JRException {
	
		JasperFillManager.fillReportToFile(REPORT_JASPER + this.reportCode
				+ ".jasper", REPORT_JRNPRINT + this.reportCode + ".jrprint",
				this.parameters, getDemoHsqldbConnection());
		
	}

	/**
	 *
	 */
	public void print() throws JRException {
		JasperPrintManager.printReport(REPORT_JRNPRINT + this.reportCode
				+ ".jrprint", true);
	
	}

	/**
	 *
	 */
	public void pdf() throws JRException {
		JasperExportManager.exportReportToPdfFile(REPORT_JRNPRINT
				+ this.reportCode + ".jrprint");
		
	}

	/**
	 *
	 */
	public void rtf() throws JRException {
		File sourceFile = new File(REPORT_JRNPRINT + this.reportCode
				+ ".jrprint");

		JasperPrint jasperPrint = (JasperPrint) JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName()
				+ ".rtf");

		JRRtfExporter exporter = new JRRtfExporter();

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleWriterExporterOutput(destFile));

		exporter.exportReport();

	
	}

	/**
	 *
	 */
	public void xml() throws JRException {
		JasperExportManager.exportReportToXmlFile(REPORT_JRNPRINT
				+ this.reportCode + ".jrprint", false);
		
	}

	/**
	 *
	 */
	public void xmlEmbed() throws JRException {
		JasperExportManager.exportReportToXmlFile(REPORT_JRNPRINT
				+ this.reportCode + ".jrprint", true);
	
	}

	/**
	 *
	 */
	public void html() throws JRException {
		JasperExportManager.exportReportToHtmlFile(REPORT_JRNPRINT
				+ this.reportCode + ".jrprint");
	
	}

	/**
	 *
	 */
	public void xls() throws JRException {
		File sourceFile = new File(REPORT_JRNPRINT + this.reportCode
				+ ".jrprint");

		JasperPrint jasperPrint = (JasperPrint) JRLoader.loadObject(sourceFile);

			File destFile = new File(sourceFile.getParent(), this.reportCode
				+ ".xls");

		JRXlsExporter exporter = new JRXlsExporter();

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(
				destFile));
		SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
		configuration.setOnePagePerSheet(false);

		configuration.setRemoveEmptySpaceBetweenRows(true);
		configuration.setRemoveEmptySpaceBetweenColumns(true);
		configuration.setCollapseRowSpan(true);

		exporter.setConfiguration(configuration);

		exporter.exportReport();

	
	}

	/**
	 *
	 */
	@SuppressWarnings("deprecation")
	public void jxl() throws JRException {
		File sourceFile = new File(REPORT_JRNPRINT + this.reportCode
				+ ".jrprint");

		JasperPrint jasperPrint = (JasperPrint) JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName()
				+ ".jxl.xls");

		net.sf.jasperreports.engine.export.JExcelApiExporter exporter = new net.sf.jasperreports.engine.export.JExcelApiExporter();

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(
				destFile));
		net.sf.jasperreports.export.SimpleJxlReportConfiguration configuration = new net.sf.jasperreports.export.SimpleJxlReportConfiguration();
		configuration.setOnePagePerSheet(true);
		exporter.setConfiguration(configuration);

		exporter.exportReport();

	
	}

	/**
	 *
	 */
	public void csv() throws JRException {
		File sourceFile = new File(REPORT_JRNPRINT + this.reportCode
				+ ".jrprint");

		JasperPrint jasperPrint = (JasperPrint) JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName()
				+ ".csv");

		JRCsvExporter exporter = new JRCsvExporter();

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleWriterExporterOutput(destFile));

		exporter.exportReport();

	
	}

	/**
	 *
	 */
	public void odt() throws JRException {
		File sourceFile = new File(REPORT_JRNPRINT + this.reportCode
				+ ".jrprint");

		JasperPrint jasperPrint = (JasperPrint) JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName()
				+ ".odt");

		JROdtExporter exporter = new JROdtExporter();

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(
				destFile));

		exporter.exportReport();

		
	}

	/**
	 *
	 */
	public void ods() throws JRException {
		File sourceFile = new File(REPORT_JRNPRINT + this.reportCode
				+ ".jrprint");

		JasperPrint jasperPrint = (JasperPrint) JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName()
				+ ".ods");

		JROdsExporter exporter = new JROdsExporter();

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(
				destFile));
		SimpleOdsReportConfiguration configuration = new SimpleOdsReportConfiguration();
		configuration.setOnePagePerSheet(true);
		exporter.setConfiguration(configuration);

		exporter.exportReport();

	
	}

	/**
	 *
	 */
	public void docx() throws JRException {
		File sourceFile = new File(REPORT_JRNPRINT + this.reportCode
				+ ".jrprint");

		JasperPrint jasperPrint = (JasperPrint) JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName()
				+ ".docx");

		JRDocxExporter exporter = new JRDocxExporter();

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(
				destFile));

		exporter.exportReport();

		
	}

	/**
	 *
	 */
	public void xlsx() throws JRException {
		File sourceFile = new File(REPORT_JRNPRINT + this.reportCode
				+ ".jrprint");

		JasperPrint jasperPrint = (JasperPrint) JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName()
				+ ".xlsx");

		JRXlsxExporter exporter = new JRXlsxExporter();

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(
				destFile));
		SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
		configuration.setOnePagePerSheet(false);
		exporter.setConfiguration(configuration);

		exporter.exportReport();

		
	}

	/**
	 *
	 */
	public void pptx() throws JRException {
		File sourceFile = new File(REPORT_JRNPRINT + this.reportCode
				+ ".jrprint");

		JasperPrint jasperPrint = (JasperPrint) JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName()
				+ ".pptx");

		JRPptxExporter exporter = new JRPptxExporter();

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(
				destFile));

		exporter.exportReport();

		
	}

	/**
	 *
	 */
	@SuppressWarnings("deprecation")
	public void xhtml() throws JRException {
		File sourceFile = new File(REPORT_JRNPRINT + this.reportCode
				+ ".jrprint");

		JasperPrint jasperPrint = (JasperPrint) JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName()
				+ ".x.html");

		net.sf.jasperreports.engine.export.JRXhtmlExporter exporter = new net.sf.jasperreports.engine.export.JRXhtmlExporter();

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleHtmlExporterOutput(destFile));

		exporter.exportReport();

	
	}

}
