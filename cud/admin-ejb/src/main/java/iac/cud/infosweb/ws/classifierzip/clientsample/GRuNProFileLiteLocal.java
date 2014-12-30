package iac.cud.infosweb.ws.classifierzip.clientsample;

import javax.ejb.Local;

/**
 * Программа, обеспечивающая загрузку данных 
 * входной посылки в соответствующей схеме БД
 * @author bubnov
 *
 */
@Local
public interface GRuNProFileLiteLocal {
	
	public int process();
}
