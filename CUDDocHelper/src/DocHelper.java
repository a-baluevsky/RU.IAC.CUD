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
		if( args.length==0) {
			System.out.println("Usage: DocHelp -cmd");
			System.out.println("\nPlease select command:");
			System.out.println("1. Generate CMD files for building projects");
			System.out.println("2. Generate summary HTML reports for each project");
			System.out.println("3. Generate project dependency spanning tree");
			System.out.println("4. Generate package dependency report");
			System.out.println("q. Press 'q' key to quit");			
		} else {
			System.out.println("Not implemented!");
		}		
	}
}
