package ru.spb.iac.cud.items;

import java.util.List;
import java.util.ArrayList;

 public class ISOrganisations {
	private Integer count;
	private List<OrganisationAttributes> organisationAttributes = new ArrayList<OrganisationAttributes>();

	

	public ISOrganisations() {
	}

	public Integer getCount() {
		return this.count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public List<OrganisationAttributes> getOrganisationAttributes() {
		return organisationAttributes;
	}

	public void setOrganisationAttributes(
			List<OrganisationAttributes> organisationAttributes) {
		this.organisationAttributes = organisationAttributes;
	}

	
}
