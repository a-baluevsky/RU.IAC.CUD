package iac.cud.infosweb.local.service;

import java.util.EnumSet;
import java.util.HashMap; import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 public class ReestrItem {

	private static final Map<String, String> lookup = new HashMap<String,String>();

	private static final Logger LOGGER = LoggerFactory.getLogger(ReestrItem.class);
	
    static {
      for(ServiceReestrPro s : EnumSet.allOf(ServiceReestrPro.class)){
    	  try{
           lookup.put(s.name(), (String)ServiceReestr.class.getDeclaredField(s.name()).get(null));
          }catch(Exception e){
        	  LOGGER.error("ReestrItem:Error:"+e);
          }
         }
    }
    
    public static String getUrl(String s){
    	return lookup.get(s);
    }
}
