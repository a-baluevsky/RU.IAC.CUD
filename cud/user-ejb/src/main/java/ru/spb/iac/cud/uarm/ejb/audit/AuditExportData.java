package ru.spb.iac.cud.uarm.ejb.audit;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.spb.iac.cud.items.AuditFunction;


/**
 * @author bubnov
 *
 */
@Stateless(mappedName = "auditExportData")
@LocalBean
 public class AuditExportData {

	 private static final Logger LOGGER = LoggerFactory.getLogger(AuditExportData.class);
	 

	 public synchronized void addFunc(String idFunction){
		
		 LOGGER.debug("AuditExportData:addFunc:01");
		 
		 try{
			 HttpSession session = (HttpSession)  FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			
			 List<AuditFunction> funcList =  (List<AuditFunction>) session.getAttribute("auditExportFuncList");
			 
		 if(funcList==null){
			 
			 
			 funcList = new ArrayList<AuditFunction>();
			 session.setAttribute("auditExportFuncList", funcList);
		 }
		 
		 
		 AuditFunction func = new AuditFunction();
		 func.setDateFunction(new Date());
		 func.setCodeFunction(idFunction);
		 
		 funcList.add(func);
		 
		 }catch(Exception e){
			 LOGGER.error("AuditExportData:addFunc:ERROR:"+e);
		 }
	 }
}

