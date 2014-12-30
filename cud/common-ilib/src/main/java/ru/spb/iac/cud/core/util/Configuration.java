package ru.spb.iac.cud.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;


 public class Configuration{
 
  private static final String config_file=System.getProperty("jboss.server.config.dir")+"/"+"cud_config.properties";
	
  private static String reportJRXML ; 
	
  private static String reportJASPER; 
	
  private static String reportJRNPRINT ; 
	
  private static String reportDOWNLOAD ; 

  private static String reportDBConnect ; 
  
  static{
	  
	  InputStream is = null;
	  
	  try{
		  Properties properties = new Properties();
	 
		  File f=new File(config_file); 
		    
		    if(f.exists()) { 
		    	
		       properties.load(is=new FileInputStream(f));
		    	 
		       setReportJRXML(properties.getProperty("REPORT_JRXML"));
		       setReportJASPER(properties.getProperty("REPORT_JASPER"));
		       setReportJRNPRINT(properties.getProperty("REPORT_JRNPRINT"));
		       setReportDOWNLOAD(properties.getProperty("REPORT_DOWNLOAD"));
		       setReportDBConnect(properties.getProperty("REPORT_DB_CONNECT"));
		    }
	  }catch(Exception e){
		  try {
				if (is!=null) {
					is.close();
				}
			} catch (Exception er) {
			     is=null;
			}
		  
	  } finally{
			try {
				if (is!=null) {
					is.close();
				}
			} catch (Exception e) {
				 is=null;
			}
			}
  }



public static String getReportJRXML() {
	return reportJRXML;
}



public static void setReportJRXML(String reportJRXML) {
	Configuration.reportJRXML = reportJRXML;
}



public static String getReportJASPER() {
	return reportJASPER;
}



public static void setReportJASPER(String reportJASPER) {
	Configuration.reportJASPER = reportJASPER;
}



public static String getReportJRNPRINT() {
	return reportJRNPRINT;
}



public static void setReportJRNPRINT(String reportJRNPRINT) {
	Configuration.reportJRNPRINT = reportJRNPRINT;
}



public static String getReportDOWNLOAD() {
	return reportDOWNLOAD;
}



public static void setReportDOWNLOAD(String reportDOWNLOAD) {
	Configuration.reportDOWNLOAD = reportDOWNLOAD;
}



public static String getReportDBConnect() {
	return reportDBConnect;
}



public static void setReportDBConnect(String reportDBConnect) {
	Configuration.reportDBConnect = reportDBConnect;
}
  
  

  
} 