package iac.grn.infosweb.context.mc.audit.report;

import iac.cud.infosweb.dataitems.BaseParamItem;
import iac.cud.infosweb.dataitems.ReportDownloadItem;
import iac.cud.infosweb.entity.ReportsBssT;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;

import mypackage.Configuration;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import ru.spb.iac.cud.reports.ReportsManagerLocal;

@Name("reportsManager")
 public class ReportsManager {
	
	@Logger private Log log;
	
	@In 
	EntityManager entityManager;
	
	private List<ReportsBssT> reportsList;
	private int runReportFlag; 
	
	private Date reportDate1; 
	
	private Date reportDate2; 
	
	//http://192.168.68.7:8080/jasperserver/rest_v2/reports/
	private String jasperServer = Configuration.getJasperServer();
	
	private String jasperLogin = Configuration.getJasperLogin();
	
	private String jasperPassword = Configuration.getJasperPassword();
	
	private String reportUrl;
	
	public void create_report(){
		  
		log.info("reportsManager:create_report:01:"+this.reportDate1);
		log.info("reportsManager:create_report:02:"+this.reportDate2);
		
		
		try{
			String reportId = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("reportId");
			
			if(reportId==null){
				return;
			}
			
			ReportsBssT rep = entityManager.find(ReportsBssT.class, new Long(reportId));
			
			
			
			Context ctx = new InitialContext();
			
			ReportsManagerLocal aml = (ReportsManagerLocal) ctx.lookup("java:global/ReportsServices/ReportsManager!ru.spb.iac.cud.reports.ReportsManagerLocal");
			
			BaseParamItem paramMap = new BaseParamItem();
			paramMap.put("reportCode", rep.getReportCode());
			paramMap.put("reportDate1", reportDate1);
			paramMap.put("reportDate2", reportDate2);
			paramMap.put("reportName", rep.getReportName());
			
			this.runReportFlag = aml.create_report(paramMap);
			
			log.info("reportsManager:create_report:02");
			
		}catch(Exception e){
			log.error("reportsManager:create_report:error:"+e);
			
		}
			 	
	}

	public void download_report(String reportType){
		  
		
		try{
			String reportCode = FacesContext.getCurrentInstance().getExternalContext()
			             .getRequestParameterMap()
			             .get("reportCode");
			
			OutputStream os = null;
			 
			Context ctx = new InitialContext();
			
			ReportsManagerLocal aml = (ReportsManagerLocal) ctx.lookup("java:global/ReportsServices/ReportsManager!ru.spb.iac.cud.reports.ReportsManagerLocal");
			
			ReportDownloadItem report = aml.download_report(reportCode, reportType);
			
			byte[] content = report.getContent();
			int flagExec = 	report.getFlagExec();
			
			HttpServletResponse response = (HttpServletResponse)
					  FacesContext.getCurrentInstance().getExternalContext().getResponse();
			
			os = response.getOutputStream();
			
			//1 - успешно
			//0 - нет ресурса
			//-1 - формирование ещё не завершено
			//-2 - техническая ошибка
			
			if(flagExec==1){
			
			  response.setHeader("Content-disposition", "attachment; filename="+reportCode+".xls");
		      response.setContentType("application/xls");
			 
		      os.write(content);
	          
	      		
			}else if(flagExec==0){
				 response.setContentType("text/html; charset=utf-8");
				 
				 os.write("Запрашиваемый ресурс не найден!".getBytes("utf-8"));
				 os.write("<br/><br/><a href=\"javascript:window.close();\" style=\"color:black;font-size:18px!important;\"> Закрыть </a>".getBytes("utf-8"));
				         
		   	}else if(flagExec==-1){
				 response.setContentType("text/html; charset=utf-8");
				 
				 os.write("Формирование отчёта ещё не завершено!".getBytes("utf-8"));
				 os.write("<br/><br/><a href=\"javascript:window.close();\" style=\"color:black;font-size:18px!important;\"> Закрыть </a>".getBytes("utf-8"));
				         
		   	}else if(flagExec==-2){
				 response.setContentType("text/html; charset=utf-8");
				 
				 os.write("При скачивании произошла ошибка !".getBytes("utf-8"));
				 os.write("<br/><br/><a href=\"javascript:window.close();\" style=\"color:black;font-size:18px!important;\"> Закрыть </a>".getBytes("utf-8"));
				         
		   	}
			
			  os.flush();
	          os.close();
			  FacesContext.getCurrentInstance().responseComplete();
		    
			log.info("reportsManager:download_report:03");
		}catch(Exception e){
			log.error("reportsManager:download_report:error:"+e);
			
		}
			 	
	}
	
public void server_report(String reportType){
		  //сделали даты обязятельными!!!
		
		try{
			
			log.info("reportsManager:server_report:01");
			
			
			String reportId = FacesContext.getCurrentInstance().getExternalContext()
		             .getRequestParameterMap()
		             .get("reportId");
			
			
			ReportsBssT rep = entityManager.find(ReportsBssT.class, new Long(reportId));
			
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		
			if (reportDate1 != null && reportDate2 != null) {
				
				log.info("reportsManager:server_report:02");
				
			} else if (reportDate1 != null) {
				reportDate2 = new Date();

			} else if (reportDate2 != null) {

		
				Calendar cln = Calendar.getInstance();
				cln.set(Calendar.YEAR, 2000);
				reportDate1 = cln.getTime();

			} else {

			
				Calendar cln = Calendar.getInstance();
				cln.set(Calendar.YEAR, 2000);
				reportDate1 = cln.getTime();
				reportDate2 = new Date();
			}

			reportUrl = jasperServer+rep.getReportPath()+"?"
					+ "ReportDate1="+df.format(reportDate1)
					+ "&ReportDate2="+df.format(reportDate2)
					+ "&j_username="+jasperLogin
					+ "&j_password="+jasperPassword;
			
			    
			log.info("reportsManager:server_report:02");
		}catch(Exception e){
			log.error("reportsManager:server_report:error:"+e);
			
		}
			 	
	}

	
	
	public List<ReportsBssT> getReportsList() {
		if(this.reportsList==null){
			this.reportsList = entityManager
					.createQuery("SELECT r FROM ReportsBssT r order by orderNum ")
					.getResultList();
			
		}
		
		return reportsList;
	}

	public void setReportsList(List<ReportsBssT> reportsList) {
		this.reportsList = reportsList;
	}

	public int getRunReportFlag() {
		return runReportFlag;
	}

	public void setRunReportFlag(int runReportFlag) {
		this.runReportFlag = runReportFlag;
	}

	public Date getReportDate1() {
		return reportDate1;
	}

	public void setReportDate1(Date reportDate1) {
		this.reportDate1 = reportDate1;
	}

	public Date getReportDate2() {
		return reportDate2;
	}

	public void setReportDate2(Date reportDate2) {
		this.reportDate2 = reportDate2;
	}

	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}

	
			
}
		
