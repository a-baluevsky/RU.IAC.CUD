package ru.spb.iac.cud.reports;

import java.util.concurrent.ConcurrentHashMap;

 public class TaskProcessor {

	private static volatile ConcurrentHashMap<String, String> controls = new ConcurrentHashMap();
	
	public static ConcurrentHashMap<String, String> getControls(){
			
		return controls;
	}
	
	public static void setControls(ConcurrentHashMap<String, String> pcontrols){
		controls=pcontrols;
	}
}
