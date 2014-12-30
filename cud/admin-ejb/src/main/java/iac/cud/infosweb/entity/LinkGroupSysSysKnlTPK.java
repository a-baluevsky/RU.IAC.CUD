package iac.cud.infosweb.entity;

import java.io.Serializable;
import javax.persistence.*;



@Embeddable
 public class LinkGroupSysSysKnlTPK implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name="UP_SYSTEMS")
	private Long acIsBssT;

	@Column(name="UP_GROUP_SYSTEMS")
	private Long groupSystemsKnlT;

	public LinkGroupSysSysKnlTPK() {
	}

	public LinkGroupSysSysKnlTPK(Long acSys, Long groupSys) {
		this.acIsBssT=acSys;
		this.groupSystemsKnlT=groupSys;
	}
	
	public Long getAcIsBssT() {
		return this.acIsBssT;
	}
	public void setAcIsBssT(Long acIsBssT) {
		this.acIsBssT = acIsBssT;
	}

	public Long getGroupSystemsKnlT() {
		return this.groupSystemsKnlT;
	}
	public void setGroupSystemsKnlT(Long groupSystemsKnlT) {
		this.groupSystemsKnlT = groupSystemsKnlT;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if ( ! (o instanceof LinkGroupSysSysKnlTPK)) {
			return false;
		}
		LinkGroupSysSysKnlTPK other = (LinkGroupSysSysKnlTPK) o;
		return this.acIsBssT.equals(other.acIsBssT)
			   && this.groupSystemsKnlT.equals(other.groupSystemsKnlT);
	}

	@Override
	public int hashCode() {
		return this.acIsBssT.hashCode()
			 ^ this.groupSystemsKnlT.hashCode()
			;
	}
}