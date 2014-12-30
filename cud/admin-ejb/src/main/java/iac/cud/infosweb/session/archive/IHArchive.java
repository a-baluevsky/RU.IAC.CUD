package iac.cud.infosweb.session.archive;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import iac.cud.infosweb.local.service.IHLocal;

@Stateless
 public class IHArchive extends IHArchiveBase implements IHLocal {

	@PersistenceContext(unitName = "InfoSCUD-web")
	EntityManager em;

}
