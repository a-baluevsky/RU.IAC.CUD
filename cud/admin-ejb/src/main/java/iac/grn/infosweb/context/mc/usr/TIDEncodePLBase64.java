package iac.grn.infosweb.context.mc.usr;

import java.util.Random;

import org.picketlink.common.util.Base64;

 public class TIDEncodePLBase64 {

	public static String getNewKey_not_used() {

		
		Random random = new Random();
	    
		byte[] keyRandom = new byte[12];
        random.nextBytes(keyRandom);

	       String key = Base64.encodeBytes(keyRandom);
      
	       key += Long.toString(System.currentTimeMillis());
	       
	       String result =  Base64.encodeBytes(key.getBytes());
	         
	       return result;
	   }
	
    public static String getSecret() {

		
		Random random = new Random();
	    
		byte[] keyRandom = new byte[3];
        random.nextBytes(keyRandom);

	    String key = Base64.encodeBytes(keyRandom);
       
	       key += Long.toString(System.currentTimeMillis());
	       
	       String result =  Base64.encodeBytes(key.getBytes());
	       
	       return result;
	   }
    
   
    
}
