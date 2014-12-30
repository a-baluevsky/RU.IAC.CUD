package iac.cud.infosweb.session.binding;

import java.util.concurrent.ConcurrentHashMap;

 public class BindingProcessor {

	private static volatile ConcurrentHashMap<String, String> controls = new ConcurrentHashMap<String, String>();

	public static ConcurrentHashMap<String, String> getControls() {

		return controls;
	}

	public static void setControls(ConcurrentHashMap<String, String> pcontrols) {
		controls = pcontrols;
	}
}
