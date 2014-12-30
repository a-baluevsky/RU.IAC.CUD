package ru.spb.iac.cud.items;

import java.util.List;
import java.util.ArrayList;

 public class DepartamentAttributes {
	private String uid;
	private List<Attribute> attributes = new ArrayList<Attribute>();

	public DepartamentAttributes() {
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

	@Override
	public String toString() {
		return "{organisation " + attributes ;
	}
}
