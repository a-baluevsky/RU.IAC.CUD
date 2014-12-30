package iac.cud.infosweb.session.archive;

import iac.cud.infosweb.dataitems.BaseParamItem;
import iac.cud.infosweb.local.service.IHLocal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import javax.ejb.TransactionManagementType;

import mypackage.Configuration;

import iac.grn.infosweb.context.proc.TaskProcessor;

 
 
import org.apache.log4j.Logger;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
 public class IHArchiveToken extends IHArchiveBase implements IHLocal {

	@PersistenceContext(unitName = "InfoSCUD-web")
	EntityManager em;

	@Resource
	UserTransaction utx;

	private Logger log = Logger.getLogger(IHArchiveToken.class);

	private static String file_path = Configuration.getArchiveToken();

	private static String param_code = "to_archive_audit_token";

	private static final String proc_atoken_info_file = System
			.getProperty("jboss.server.config.dir")
			+ "/"
			+ "proc_atoken_info.properties";

	private static final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);

	public BaseParamItem process_start(BaseParamItem paramMap) throws Exception {

		BaseParamItem jpiTkn = new BaseParamItem();

		log.info("IHArchiveToken:process_start");

		ScheduledFuture shfTkn = scheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {

		
				try {

					log.info("IHArchiveToken:process_start:run");

			
					Calendar cln = Calendar.getInstance();

					int dayTkn = cln.get(Calendar.DAY_OF_MONTH);

					log.info("IHArchiveToken:process_start:run:day:" + dayTkn);

					if (dayTkn == 1) {

						process_start_content();
					}

				} catch (Exception eTkn) {
					log.error("IHArchiveToken:process_start:run:error:", eTkn);
				}
			}
		}, calendar(), 24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS);

	
		if (TaskProcessor.getControls().containsKey("archiveTokenScheduled")) {
			try {
				TaskProcessor.getControls().get("archiveTokenScheduled")
						.cancel(false);
			} catch (Exception eTkn) {
				log.info("IHArchiveToken:process_start:error:", eTkn);
			}
		}
		TaskProcessor.getControls().put("archiveTokenScheduled", shfTkn);

		return jpiTkn;
	}

	public BaseParamItem process_stop(BaseParamItem paramMap) throws Exception {

		BaseParamItem jpiTkn = new BaseParamItem();

		log.info("IHConfLoadData:stopTask:01");

		try {
		
			ScheduledFuture shfTkn = TaskProcessor.getControls().get(
					"archiveTokenScheduled");

			log.info("IHConfLoadData:stopTask:02");
			if (shfTkn != null) { // может быть = null, когда приостановили, а
								// потом отключаем
				shfTkn.cancel(false);
			}
		} catch (Exception eTkn) {
			log.error("IHConfLoadData:stopTask:error:", eTkn);
			throw eTkn;
		}

		return jpiTkn;
	}

	private synchronized void process_start_content() throws Exception {

		String prev_date = null;
		BufferedWriter bwTkn = null;
		File fileTkn = null;
		int i = 1;
		String monthInterval = null;
		boolean hit = true;
		OutputStream os = null;

		log.info("IHArchiveToken:process_start_content:01");

		try {
			utx.begin();

			File dirTkn = new File(file_path);
			if (!dirTkn.exists()) {
				dirTkn.mkdirs();
			}

			List<String> losTkn = em
					.createNativeQuery(
							"select ST.VALUE_PARAM "
									+ "from SETTINGS_KNL_T st "
									+ "where ST.SIGN_OBJECT=? ")
					.setParameter(1, param_code).getResultList();

			if (losTkn != null && !losTkn.isEmpty()) {
				monthInterval = losTkn.get(0);
			}

			if (monthInterval == null) {
				monthInterval = "6";
			}

			log.info("IHArchiveToken:process_start_content:monthInterval:"
					+ monthInterval);

			List<Object[]> loTkn = em
					.createNativeQuery(
							"select to_char(tt.CREATED , 'YYYY_MM') vdate, TT.ID_SRV, TT.UP_USERS, TT.SIGN_OBJECT, "
									+ "to_char(tt.CREATED,'DD.MM.YYYY HH24:MI:SS') CREATED, UP_SERVICE "
									+ "from TOKEN_KNL_T tt "
									+ "where tt.CREATED < to_date('01.'||to_char(SYSDATE - INTERVAL '"
									+ monthInterval
									+ "' month, 'MM.YYYY'),'DD.MM.YYYY')  "
									+ "order by tt.CREATED ")
					.getResultList();
			log.info("IHArchiveToken:process_start_content:02");

			int BUFF_SIZE = 1000 * 1024;

			for (Object[] objectArray : loTkn) {
				if (prev_date == null
						|| !prev_date.equals(objectArray[0].toString())) {

					if (bwTkn != null) {
						bwTkn.flush();
						bwTkn.close();
					}

					fileTkn = new File(file_path + "audit_token_"
							+ objectArray[0].toString() + ".txt");
					bwTkn = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(fileTkn), "Cp1251"), BUFF_SIZE);

					bwTkn.append("ID_SRV" + "\t" + "UP_USERS" + "\t"
							+ "SIGN_OBJECT" + "\t" + "CREATED" + "\t"
							+ "UP_SERVICE" + "\n");
				}

				bwTkn.append ((objectArray[1] != null ? objectArray[1].toString()
						: "null")
						+ "\t"
						+ (objectArray[2] != null ? objectArray[2].toString()
								: "null")
						+ "\t"
						+ (objectArray[3] != null ? objectArray[3].toString()
								: "null")
						+ "\t"
						+ (objectArray[4] != null ? objectArray[4].toString()
								: "null")
						+ "\t"
						+ (objectArray[5] != null ? objectArray[5].toString()
								: "null") + "\n");

				i++;

				if ((i % 100) == 0) {
					bwTkn.flush();
				}

				prev_date = objectArray[0].toString();
			}
			if (bwTkn != null) {
				bwTkn.flush();
			}

			em.createNativeQuery(
					"delete from TOKEN_KNL_T tt "
							+ "where tt.CREATED < to_date('01.'||to_char(SYSDATE - INTERVAL '"
							+ monthInterval
							+ "' month, 'MM.YYYY'),'DD.MM.YYYY') ")
					.executeUpdate();

			utx.commit();
		} catch (Exception eTkn) {
			log.error("IHArchiveToken:process_start_content:error", eTkn);

			utx.rollback();

			/*
			 * можно в принципе файл и оставить. если в базе сведения не
			 * удалились, то при следующем запуске файл будет перезаписан
			 */

			hit = false;
			throw eTkn;
		} finally {

			try {

				log.info("IHArchiveToken:process_start_content:finally:hit:"
						+ hit);

				DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
		
				File fTkn = new File(proc_atoken_info_file);

				Properties properties = new Properties();

				properties.setProperty("exec_date",
						df.format(System.currentTimeMillis()));
				properties.setProperty("exec_hit", hit ? "true" : "false");

				properties.store(os = new FileOutputStream(fTkn), null);

			} catch (Exception eTkn) {
				log.error("IHArchiveToken:process_start_content:error:2:", eTkn);
			} finally {
				try {
					if (os != null) {
						os.close();
					}
				} catch (Exception eTkn) {
					log.error("IHArchiveToken:process_start_content:error:2:", eTkn);
				}
				try {
					if (bwTkn != null) {
						bwTkn.close();
					}
				} catch (Exception eTkn) {
					log.error("IHArchiveToken:process_start_content:error:2:", eTkn);
				}
			}
		}
	}

	private static Long calendar() {
	
		Long currentTime = System.currentTimeMillis();

		// 5.00 в jboss - это в реальном времени 6.00

		Calendar clnTkn = Calendar.getInstance();
		clnTkn.set(Calendar.HOUR_OF_DAY, 5);
		clnTkn.set(Calendar.MINUTE, 0);
		clnTkn.set(Calendar.SECOND, 0);
		clnTkn.set(Calendar.MILLISECOND, 0);

		Long trans = clnTkn.getTimeInMillis();

		Long startTkn = trans - currentTime;

		if (startTkn <= 0) {
			startTkn = startTkn + 24 * 60 * 60 * 1000;
		}

		
		return startTkn;
		//  5000L;
	}

}
