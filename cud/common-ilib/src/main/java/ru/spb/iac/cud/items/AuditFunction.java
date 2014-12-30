package ru.spb.iac.cud.items;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "userFunctions", propOrder = { "codeFunction", "dateFunction",
		"detailsFunction" })
 public class AuditFunction {

	@XmlElement(name = "codeFunction", required = true)
	private String codeFunction;

	@XmlElement(name = "dateFunction", required = true)
	private Date dateFunction;

	@XmlElement(name = "detailsFunction")
	private String detailsFunction;

	public void Function() {
	}

	public Date getDateFunction() {
		return dateFunction;
	}

	public void setDateFunction(Date dateFunction) {
		this.dateFunction = dateFunction;
	}

	public String getDetailsFunction() {
		return detailsFunction;
	}

	public void setDetailsFunction(String detailsFunction) {
		this.detailsFunction = detailsFunction;
	}

	public String getCodeFunction() {
		return codeFunction;
	}

	public void setCodeFunction(String codeFunction) {
		this.codeFunction = codeFunction;
	}
}
