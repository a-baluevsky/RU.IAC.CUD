package iac.grn.infosweb.session.support;


import iac.cud.infosweb.entity.AcUser;
import iac.grn.infosweb.session.navig.LinksMap;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.jboss.seam.transaction.Transaction;


/**
 * ”правл€ющий Ѕин аудита
 * @author bubnov
 *
 */
@Name("supportManager")
 public class SupportManager {
	
	@Logger private Log log;
	
	@In 
	EntityManager entityManager;
	
	public void sendMail(String helpFio, String helpPost, String helpMail, String helpText,  String helpTel){
		log.info("supportManager:sendMail:01");
		SupportMail sm = (SupportMail)
				Component.getInstance("supportMail", ScopeType.EVENT);
		
		sm.init(helpFio, helpPost, helpMail, helpText, helpTel);
		
		Thread t = new Thread(sm);
        t.start();
		
        logMail(helpFio, helpPost, helpMail, helpText, helpTel);
        
		log.info("supportManager:sendMail:02");
	}
	private void logMail(String helpFio, String helpPost, String helpMail, String helpText,  String helpTel){
		  log.info("supportManager:logMail:01");
		  
		  try{
			AcUser au = (AcUser) Component.getInstance("currentUser",ScopeType.SESSION);   
			
			Transaction.instance().begin();
			
			 Transaction.instance().enlist(entityManager);
			 
			 LinksMap linksMap = (LinksMap)
					  Component.getInstance("linksMap",ScopeType.APPLICATION);
			 
			 entityManager.createNativeQuery("insert into HELP_DESK_PROTOTYPE( "+
	          "ID_HELP, AUTHOR, POSITION,  PHONE,  EMAIL, MESSAGE, CREATOR, ID_APP) values( "+
	          "SQNC_HELP_DESK_PROTOTYPE.nextval, :p1, :p2, :p3, :p4, :p5, :p6, :p7)")
	          .setParameter("p1", helpFio)
	          .setParameter("p2", helpPost)
	          .setParameter("p3", helpTel)
	          .setParameter("p4", helpMail)
	          .setParameter("p5", helpText)
	         .setParameter("p6", au.getIdUser())
	         .setParameter("p7", linksMap.getAppCode())
	        .executeUpdate() ;
			 
			Transaction.instance().commit();
			 
		  }catch(Exception e){
			  log.error("supportManager:logMail:error:"+e); 
			  try{
			   Transaction.instance().rollback();
			  }catch(Exception et){}
		  }
		}
		
	
	
}

