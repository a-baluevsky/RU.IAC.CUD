package iac.grn.infosweb.context.proc;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 public class TaskProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskProcessor.class);
	
	private static volatile ConcurrentHashMap<String, ScheduledFuture> controls = new ConcurrentHashMap();
	
	public static ConcurrentHashMap<String, ScheduledFuture> getControls(){
		
		Set<Map.Entry<String, ScheduledFuture>> set = controls.entrySet();
        for (Map.Entry<String, ScheduledFuture> me : set) {
        	LOGGER.debug("TaskProcessor:key:"+me.getKey());
        	if(me.getValue()!=null) {
        	  LOGGER.debug("TaskProcessor:isCancelled:"+me.getValue().isCancelled());
        	}else{
        	  LOGGER.debug("TaskProcessor:value:is null");	
        	}
		 }
		
		return controls;
	}
	
	public static void setControls(ConcurrentHashMap<String, ScheduledFuture> pcontrols){
		controls=pcontrols;
	}
}
