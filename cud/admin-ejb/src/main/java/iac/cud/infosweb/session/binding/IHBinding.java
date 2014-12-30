package iac.cud.infosweb.session.binding;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import iac.cud.infosweb.local.service.IHLocal;

@Stateless
 public class IHBinding extends IHBindingBase implements IHLocal {

	@PersistenceContext(unitName = "InfoSCUD-web")
	EntityManager em;

}
