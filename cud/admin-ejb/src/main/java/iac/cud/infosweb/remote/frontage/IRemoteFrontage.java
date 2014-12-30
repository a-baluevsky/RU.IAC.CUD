package iac.cud.infosweb.remote.frontage;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import iac.cud.infosweb.dataitems.BaseParamItem;
import iac.cud.infosweb.local.service.IHLocal;
import iac.cud.infosweb.local.service.ReestrItem;

@Stateless
 public class IRemoteFrontage implements IRemoteFrontageLocal {

	private Context ctx = null;

	private static final Logger LOGGER = LoggerFactory.getLogger(IRemoteFrontage.class);
	
	@PostConstruct
	public void init() {
		try {
			if (ctx == null) {
				ctx = new InitialContext();
			}
		} catch (Exception e) {
			LOGGER.error("init:error:"+e);
		}
	}

	@PreDestroy
	public void remove() {
		try {
			if (ctx != null) {
				ctx.close();
			}
		} catch (Exception e) {
			LOGGER.error("init:error:"+e);
		}
	}

	public BaseParamItem run(BaseParamItem paramMap) throws Exception {

		
		String gtype = (String) paramMap.get("gtype");

		LOGGER.debug("IRemoteFrontage:run:gtype:" + gtype);

		String url = ReestrItem.getUrl(gtype);

		
		if (url == null) {
			return null;
		}

		return ((IHLocal) ctx.lookup(url)).run(paramMap);
	}

}
