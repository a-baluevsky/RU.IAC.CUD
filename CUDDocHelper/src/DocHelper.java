import jrf5driver.*;

/** Helper tool to maintain ru.spb.iac.cud documentation 
 * 
 */

/**
 * @author Andrey Baluevsky
 *
 */
public class DocHelper {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("**** ru.spb.iac.cud Document Helper ***");
		switch(args.length) {
		case 1:
			try {
				DocHelperCore.HelperCommand cmd = 
						DocHelperCore.HelperCommand.valueOf(args[0]);
				DocHelperCore h = new DocHelperCore(cmd);
				System.out.println((h.execute())?"DONE!": "FAILED!");
				return;
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
			//break;	
		case 0:
			System.out.println("Usage: DocHelp cmdName");						
		default:
			System.out.println("No valid argument specified! Starting interactive mode.");
			System.out.println("\nPlease select command:");
			System.out.println("1. Generate CMD files for building projects");
			System.out.println("2. Generate summary HTML reports for each project");
			System.out.println("3. Generate project dependency spanning tree");
			System.out.println("4. Generate package dependency report");
			System.out.println("q. Press 'q' key to quit");				
				
		}		
	}
}
