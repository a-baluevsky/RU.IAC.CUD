package mypackage;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * MyPhaseListener. The phase listener which prints the current phase to the system console.
 *
 * @author BalusC
 * @link http://balusc.blogspot.com/2006/06/using-datatables.html
 */

 public class MyPhaseListener implements PhaseListener {

	
	private static final Logger LOGGER = LoggerFactory.getLogger(MyPhaseListener.class);
	
    private Long phaseTime;
    private Long requestTime;
    
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    /**
     * This is invoked at the beginning of any JSF lifecycle phase.
     * @param event The Phase Event.
     */
    public void beforePhase(PhaseEvent event) {
    	if(event.getPhaseId().equals(PhaseId.RESTORE_VIEW)){
    		requestTime=System.currentTimeMillis();
    	}
    	phaseTime=System.currentTimeMillis();
    	
        LOGGER.debug("Phase Start: " + event.getPhaseId());
       
     }

    /**
     * This is invoked at the end of any JSF lifecycle phase.
     * @param event The Phase Event.
     */
    public void afterPhase(PhaseEvent event) {
    	LOGGER.debug("Phase Finish: " + event.getPhaseId());
    	LOGGER.debug("Phase Time: " + (System.currentTimeMillis()-phaseTime));
      if(event.getPhaseId().equals(PhaseId.RENDER_RESPONSE)){
    	  LOGGER.debug("Request Time: " + (System.currentTimeMillis()-requestTime));
  	 }
   }
}
