package iac.grn.infosweb.session.support;


import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.log.Log;



/**
 * ”правл€ющий Ѕин аудита
 * @author bubnov
 *
 */
@Name("supportMail")
 public class SupportMail implements Runnable {
	
	@Logger private Log log;
	
	@In 
	EntityManager entityManager;
	
	@In(create=true)
	private Renderer renderer;

	private String etoMail="bubnov@iac.spb.ru";
	
	private String helpFio;
	
	private String helpPost;
	
	private String helpMail;
	
	private String helpText;
	
	private String helpTel;
	
	public void init(String helpFioValue, String helpPostValue, String helpMailValue, String helpTextValue, String helpTelValue) {
		
		log.info("supportMail:init:01");
	       
	    helpFio=helpFioValue;
	    helpPost=helpPostValue;
	    helpMail=helpMailValue;
	    helpText=helpTextValue;
	    helpTel=helpTelValue;
	    
	    log.info("supportMail:init:02");
	}
	public void run() {
		log.info("supportMail:run:01");
		send();
		log.info("supportMail:run:02");
	}
	
	
	public void send() {
		
	    try {
	       log.info("supportMail:send:01");
	       
	 
	       renderer.render("/services/support/mail.xhtml");
	       
	       log.info("supportMail:send:02");
	 
	  	   
	    } 
	   catch (Exception e) {
		   log.error("supportMail:send:ERROR="+e);
			     }
	}
   public String getHelpFio(){
	   return this.helpFio;
   }
   public void setHelpFio(String helpFio){
	   this.helpFio=helpFio;
   }
   
   public String getHelpPost(){
	   return this.helpPost;
   }
   public void setHelpPost(String helpPost){
	   this.helpPost=helpPost;
   }
   
   public String getHelpMail(){
	   return this.helpMail;
   }
   public void setHelpMail(String helpMail){
	   this.helpMail=helpMail;
   }
   
   public String getHelpText(){
	   return this.helpText;
   }
   public void setHelpText(String helpText){
	   this.helpText=helpText;
   }
   
   public String getEtoMail(){
	   return this.etoMail;
   }
   public void setEtoMail(String etoMail){
	   this.etoMail=etoMail;
   }
   
   public String getHelpTel(){
	   return this.helpTel;
   }
   public void setHelpTel(String helpTel){
	   this.helpTel=helpTel;
   }
}

