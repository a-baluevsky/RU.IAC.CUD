package ru.spb.iac.cud.services.web.init;


 public class Configuration{
 
  private static boolean signRequired;

  public static boolean isSignRequired() {
	return signRequired;
  }

  public static void setSignRequired(boolean signRequired) {
	Configuration.signRequired = signRequired;
  }
   

  
} 