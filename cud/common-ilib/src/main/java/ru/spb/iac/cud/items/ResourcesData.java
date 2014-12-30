package ru.spb.iac.cud.items;

import java.util.List;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resourcesData", propOrder = { "count", "resources" })
 public class ResourcesData {

	@XmlElement(name = "count", /* required=true, */namespace = "http://util.services.cud.iac.spb.ru/")
	private Integer count;

	@XmlElement(name = "resources", /* required=true, */namespace = "http://util.services.cud.iac.spb.ru/")
	private List<ResourceNU> resources = new ArrayList<ResourceNU>();

	public ResourcesData() {
	}

	public Integer getCount() {
		return this.count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public List<ResourceNU> getResources() {
		return resources;
	}

	public void setResources(List<ResourceNU> resources) {
		this.resources = resources;
	}

	
}
