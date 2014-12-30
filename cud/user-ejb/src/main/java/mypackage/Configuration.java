package mypackage;


 public class Configuration{
 
  private static boolean signRequired;

  private static String samlRequestLoginUarm ; 
	
  private static String samlRequestLogout; 
	
  private static String samlAssertion ; 
	
  private static String storePath ; 
	
  private static String auditService ;
  
  private static String stsOboService ;
  
  private static String stsService ;
  
  public static boolean isSignRequired() {
	return signRequired;
  }

  public static void setSignRequired(boolean signRequired) {
	Configuration.signRequired = signRequired;
  }


public static String getSamlRequestLoginUarm() {
	return samlRequestLoginUarm;
}

public static void setSamlRequestLoginUarm(String samlRequestLoginUarm) {
	Configuration.samlRequestLoginUarm = samlRequestLoginUarm;
}

public static String getSamlRequestLogout() {
	return samlRequestLogout;
}

public static void setSamlRequestLogout(String samlRequestLogout) {
	Configuration.samlRequestLogout = samlRequestLogout;
}

public static String getSamlAssertion() {
	return samlAssertion;
}

public static void setSamlAssertion(String samlAssertion) {
	Configuration.samlAssertion = samlAssertion;
}

public static String getStorePath() {
	return storePath;
}

public static void setStorePath(String storePath) {
	Configuration.storePath = storePath;
}

public static String getAuditService() {
	return auditService;
}

public static void setAuditService(String auditService) {
	Configuration.auditService = auditService;
}

public static String getStsOboService() {
	return stsOboService;
}

public static void setStsOboService(String stsOboService) {
	Configuration.stsOboService = stsOboService;
}

public static String getStsService() {
	return stsService;
}

public static void setStsService(String stsService) {
	Configuration.stsService = stsService;
}


  
} 