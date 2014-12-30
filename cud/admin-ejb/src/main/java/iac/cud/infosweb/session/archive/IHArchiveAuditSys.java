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
 public class IHArchiveAuditSys extends IHArchiveBase implements IHLocal {

	@PersistenceContext(unitName = "InfoSCUD-web")
	EntityManager em;

	@Resource
	UserTransaction utx;

	private Logger log = Logger.getLogger(IHArchiveAuditSys.class);

	private static String file_path = Configuration.getArchiveAuditSys();

	private static String param_code = "to_archive_audit_sys";

	private static final String proc_aasys_info_file = System
			.getProperty("jboss.server.config.dir")
			+ "/"
			+ "proc_aasys_info.properties";

	private static final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);

	public BaseParamItem process_start(BaseParamItem paramMap) throws Exception {

		BaseParamItem jpiSys = new BaseParamItem();

		log.info("IHArchiveAuditSys:process_start");

		ScheduledFuture shfSys = scheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {

			
				try {

					log.info("IHArchiveAuditSys:process_start:run");

				
					Calendar cln = Calendar.getInstance();

					int daySys = cln.get(Calendar.DAY_OF_MONTH);

					log.info("IHArchiveAuditSys:process_start:run:day:" + daySys);

					if (daySys == 1) {
						process_start_content(null);
					}

				} catch (Exception eSys) {
					log.error("IHArchiveAuditSys:process_start:run:error:", eSys);
				}
			}
		}, calendar(), 24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS);

	
		if (TaskProcessor.getControls().containsKey("archiveAuditSysScheduled")) {
			try {
				TaskProcessor.getControls().get("archiveAuditSysScheduled")
						.cancel(false);
			} catch (Exception eSys) {
				log.info("IHArchiveAuditSys:process_start:error:", eSys);
			}
		}
		TaskProcessor.getControls().put("archiveAuditSysScheduled", shfSys);

		return jpiSys;
	}

	public BaseParamItem process_stop(BaseParamItem paramMap) throws Exception {

		BaseParamItem jpiSys = new BaseParamItem();

		log.info("IHArchiveAuditSysa:process_stop:01");

		try {
		
			ScheduledFuture shfSys = TaskProcessor.getControls().get(
					"archiveAuditSysScheduled");

			log.info("IHArchiveAuditSys:process_stop:02");
			if (shfSys != null) { // может быть = null, когда приостановили, а
								// потом отключаем
				shfSys.cancel(false);
			}
		} catch (Exception eSys) {
			log.error("IHArchiveAuditSys:process_stop:error:", eSys);
			throw eSys;
		}

		return jpiSys;
	}

	public BaseParamItem task_run(BaseParamItem paramMap) throws Exception {

		BaseParamItem jpiSys = new BaseParamItem();

		log.info("IHArchiveAuditSys:task_run:01");

		try {
			Long archiveParamValue = (Long) paramMap.get("archiveParamValue");

			process_start_content(archiveParamValue);

		} catch (Exception eSys) {
			log.error("IHArchiveAuditSys:task_run:error:", eSys);
			throw eSys;
		}
		return jpiSys;
	}

	private synchronized void process_start_content(Long archiveParamValue)
			throws Exception {

		String prev_date = null;
		BufferedWriter bwSys = null;
		File fileSys = null;
		int i = 1;
		String monthInterval = null;
		boolean hit = true;
		OutputStream os = null;

		log.info("IHArchiveAuditSys:process_start_content:archiveParamValue:"
				+ archiveParamValue);

		try {
			utx.begin();

			File dirSys = new File(file_path);
			if (!dirSys.exists()) {
				dirSys.mkdirs();
			}

			if (archiveParamValue == null) {

				List<String> losSys = em
						.createNativeQuery(
								"select ST.VALUE_PARAM "
										+ "from SETTINGS_KNL_T st "
										+ "where ST.SIGN_OBJECT=? ")
						.setParameter(1, param_code).getResultList();

				if (losSys != null && !losSys.isEmpty()) {
					monthInterval = losSys.get(0);
				}

			} else {
				monthInterval = archiveParamValue.toString();
			}

			if (monthInterval == null) {
				monthInterval = "6";
			}

			log.info("IHArchiveAuditSys:process_start_content:monthInterval:"
					+ monthInterval);

			List<Object[]> loSys = em
					.createNativeQuery(
							"select to_char(SL.CREATED , 'YYYY_MM') vdate, SL.ID_SRV, SL.UP_SERVICES, SL.UP_USERS, "
									+ "to_char(SL.CREATED,'DD.MM.YYYY HH24:MI:SS') CREATED, SL.INPUT_PARAM, SL.RESULT_VALUE, SL.IP_ADDRESS "
									+ "from SERVICES_LOG_KNL_T SL "
									+ "where SL.CREATED < to_date('01.'||to_char(SYSDATE - INTERVAL '"
									+ monthInterval
									+ "' month, 'MM.YYYY'),'DD.MM.YYYY') "
									+ "order by SL.CREATED ")
					.getResultList();
			log.info("IHArchiveAuditSys:process_start_content:02");

			int BUFF_SIZE = 1000 * 1024;

			for (Object[] objectArray : loSys) {
				if (prev_date == null
						|| !prev_date.equals(objectArray[0].toString())) {

					if (bwSys != null) {
						bwSys.flush();
						bwSys.close();
					}

					fileSys = new File(file_path + "audit_sys_"
							+ objectArray[0].toString() + ".txt");
					bwSys = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(fileSys), "Cp1251"), BUFF_SIZE);

					bwSys.append("ID_SRV" + "\t" + "UP_SERVICES" + "\t"
							+ "UP_USERS" + "\t" + "CREATED" + "\t"
							+ "INPUT_PARAM" + "\t" + "RESULT_VALUE" + "\t"
							+ "IP_ADDRESS" + "\n");
				}

				bwSys.append ((objectArray[1] != null ? objectArray[1].toString()
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
								: "null")
						+ "\t"
						+ (objectArray[6] != null ? objectArray[6].toString()
								: "null")
						+ "\t"
						+ (objectArray[7] != null ? objectArray[7].toString()
								: "null") + "\n");

				i++;

				if ((i % 100) == 0) {
					bwSys.flush();
				}

				prev_date = objectArray[0].toString();
			}
			if (bwSys != null) {
				bwSys.flush();
			}

			em.createNativeQuery(
					"delete from SERVICES_LOG_KNL_T SL "
							+ "where SL.CREATED < to_date('01.'||to_char(SYSDATE - INTERVAL '"
							+ monthInterval
							+ "' month, 'MM.YYYY'),'DD.MM.YYYY') ")
					.executeUpdate();

			utx.commit();
		} catch (Exception eSys) {
			log.error("IHArchiveAuditSys:process_start_content:error", eSys);

			utx.rollback();

			/*
			 * можно в принципе файл и оставить. если в базе сведения не
			 * удалились, то при следующем запуске файл будет перезаписан
			 */

			hit = false;
			throw eSys;
		} finally {

			try {

				log.info("IHArchiveAuditSys:process_start_content:finally:hit:"
						+ hit);

				DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
			
				File fSys = new File(proc_aasys_info_file);

				Properties properties = new Properties();

				properties.setProperty("exec_date",
						df.format(System.currentTimeMillis()));
				properties.setProperty("exec_hit", hit ? "true" : "false");

				properties.store(os = new FileOutputStream(fSys), null);

			} catch (Exception eSys) {
				log.error("ConfLoadDataTask:run:error:2:", eSys);
			} finally {
				try {
					if (os != null) {
						os.close();
					}
				} catch (Exception eSys) {
					log.error("ConfLoadDataTask:run:error:2:", eSys);
				}
				try {
					if (bwSys != null) {
						bwSys.close();
					}
				} catch (Exception eSys) {
					log.error("ConfLoadDataTask:run:error:2:", eSys);
				}
			}
		}
	}

	private static Long calendar() {
	
		Long currentTime = System.currentTimeMillis();

		// 4.40 в jboss - это в реальном времени 5.40

		Calendar clnSys = Calendar.getInstance();
		clnSys.set(Calendar.HOUR_OF_DAY, 4);
		clnSys.set(Calendar.MINUTE, 40);
		clnSys.set(Calendar.SECOND, 0);
		clnSys.set(Calendar.MILLISECOND, 0);

		Long trans = clnSys.getTimeInMillis();

		Long startSys = trans - currentTime;

		if (startSys <= 0) {
			startSys = startSys + 24 * 60 * 60 * 1000;
		}

		return startSys;
		//  5000L;
	}

}
