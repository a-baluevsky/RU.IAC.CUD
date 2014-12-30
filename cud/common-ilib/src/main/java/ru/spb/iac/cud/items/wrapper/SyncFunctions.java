package ru.spb.iac.cud.items.wrapper;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import ru.spb.iac.cud.items.Function;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sync_functions", propOrder = { "functions" })
// используется совместно с @RequestWrapper
 public class SyncFunctions {

	@XmlElement(name = "functions", required = true, namespace = "http://audit.services.cud.iac.spb.ru/")
	protected List<Function> functions;

	public List<Function> getFunctions() {
		return functions;
	}

	public void setFunctions(List<Function> functions) {
		this.functions = functions;
	}

}
