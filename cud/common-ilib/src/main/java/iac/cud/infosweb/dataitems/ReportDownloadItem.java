package iac.cud.infosweb.dataitems;

import java.io.Serializable;

 public class ReportDownloadItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private byte[] content;
	private int flagExec;

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public int getFlagExec() {
		return flagExec;
	}

	public void setFlagExec(int flagExec) {
		this.flagExec = flagExec;
	}

}
