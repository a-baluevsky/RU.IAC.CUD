package iac.cud.infosweb.ws.classifierzip.clientsample;

import java.sql.Connection;
import java.sql.SQLException;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.jboss.seam.annotations.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Name("gRuNProFileLite")
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
 public class GRuNProFileLite implements GRuNProFileLiteLocal {

	@PersistenceContext(unitName = "InfoSCUD-web")
	EntityManager entityManager;

	
	private static final Logger LOGGER = LoggerFactory.getLogger(GRuNProFileLite.class);

	public int process() {

		Session session = (Session) entityManager.getDelegate();
		final int[] result = new int[] { -1 };

		session.doWork(new Work() {
			public void execute(Connection conn) throws SQLException {
				LOGGER.debug("IHLogContrList:logContrList:execute:conn:"
						+ conn.isClosed());
				try {
					conn.setAutoCommit(false);
				
					PojoRunProcess pp = new PojoRunProcess();
					pp.startProcess();

					LOGGER.debug("IHLogContrList:logContrList:execute:01");
				
				} catch (Exception e) {
					LOGGER.error("IHLogContrList:logContrList:execute:error:1:"
							+ e);
				} finally {

					// !!!
					// обязательно здесь НЕ закрывать коннект!!!

					/*
					 * if/(conn/!=null/&&!conn./isClosed/()){ /conn.close/() }
					 */

				}
			}
		});
		return result[0];
	}

}
