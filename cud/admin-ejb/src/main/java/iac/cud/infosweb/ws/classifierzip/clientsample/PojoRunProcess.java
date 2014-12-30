package iac.cud.infosweb.ws.classifierzip.clientsample;

/**
 * 
 */
 public class PojoRunProcess implements Runnable {

	public PojoRunProcess() {

	}

	
	public void startProcess() {
		Thread t = new Thread(this);
		t.start();
	}

	public void run() {

		ClientSample.run2(null, null, null, null);

		
	}

}
