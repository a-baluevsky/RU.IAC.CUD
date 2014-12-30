package ru.spb.iac.cud.services.audit;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.namespace.QName;

import ru.spb.iac.cud.items.wrapper.Audit;
import ru.spb.iac.cud.items.wrapper.SyncFunctions;

 public class ObjectFactory {

	private static final  QName _audit_QNAME = new QName(AuditServiceImpl.NS,
			"audit");
	private static final  QName _sync_functions_QNAME = new QName(
			AuditServiceImpl.NS, "sync_functions");

	public ObjectFactory() {
	}

	@XmlElementDecl(namespace = AuditServiceImpl.NS, name = "audit")
	public JAXBElement<Audit> createSayHello(Audit value) {
		return new JAXBElement<Audit>(_audit_QNAME, Audit.class, null, value);
	}

	@XmlElementDecl(namespace = AuditServiceImpl.NS, name = "sync_functions")
	public JAXBElement<SyncFunctions> createSyncFunctions(SyncFunctions value) {
		return new JAXBElement<SyncFunctions>(_sync_functions_QNAME,
				SyncFunctions.class, null, value);
	}
}
