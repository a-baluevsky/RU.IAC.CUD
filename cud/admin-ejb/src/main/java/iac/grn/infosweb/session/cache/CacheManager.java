package iac.grn.infosweb.session.cache;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.cache.CacheProvider;
import org.jboss.seam.log.Log;

/**
 * ”правл€ющий бин, осуществл€ющий реализацию навигации 
 * по используемым ресурсам приложени€
 * @author bubnov
 *
 */
@Name("cacheManager")
 public class CacheManager {

	 @Logger private Log log;
	 
	 @In CacheProvider cacheProvider;
	 
	 public void removeCache(String regionName, String keyName) throws Exception{
		 log.info("cacheManager:removeCache:01");
		 try {
			cacheProvider.remove(regionName, keyName);
		 } catch (Exception e) {
		   log.error("cacheManager:removeCache:ERROR:"+e);
		   throw e;
		 }
	  }
	
}
