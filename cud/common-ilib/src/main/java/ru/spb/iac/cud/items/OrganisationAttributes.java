package ru.spb.iac.cud.items;

import java.util.List;
import java.util.ArrayList;

 public class OrganisationAttributes {
	private String uid;
	private List<Attribute> attributes = new ArrayList<Attribute>();

	private List<DepartamentAttributes> departamentAttributes = new ArrayList<DepartamentAttributes>();

	public OrganisationAttributes() {
	}

	public String getUid() {
		return this.uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public List<DepartamentAttributes> getDepartamentAttributes() {
		return departamentAttributes;
	}

	public void setDepartamentAttributes(
			List<DepartamentAttributes> departamentAttributes) {
		this.departamentAttributes = departamentAttributes;
	}

	@Override
	public String toString() {
		return "{organisation " + attributes /* + " " + roles + "}" */;
	}
}
