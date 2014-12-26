
public class DocHelperCore {
	public enum HelperCommand {
		GenDocBuilderCmd,
		GenSummaryHTMLReport,
		GenProjDepSpTree,
		GenPackageDepReport
	}
	private HelperCommand m_Cmd;
	public DocHelperCore(HelperCommand Command) {
		this.m_Cmd = Command;
	}
	public Boolean execute() {
		System.out.println("Not implemented!");
		return false;
	}
}
