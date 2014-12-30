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
 public class IHArchiveAuditFunc extends IHArchiveBase implements IHLocal {

	@PersistenceContext(unitName = "InfoSCUD-web")
	EntityManager em;

	@Resource
	UserTransaction utx;

	private Logger log = Logger.getLogger(IHArchiveAuditFunc.class);

	private static String file_path = Configuration.getArchiveAuditFunc();

	private static String param_code = "to_archive_audit_func";

	private static final String proc_aafunc_info_file = System
			.getProperty("jboss.server.config.dir")
			+ "/"
			+ "proc_aafunc_info.properties";

	private static final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);

	public BaseParamItem process_start(BaseParamItem paramMap) throws Exception {

		BaseParamItem jpiFunc = new BaseParamItem();

		log.info("IHArchiveAuditFunc:process_start");

		ScheduledFuture shfFunc = scheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {

			
				try {

					log.info("IHArchiveAuditFunc:process_start:run");

					Calendar clnFunc = Calendar.getInstance();

					int dayFunc = clnFunc.get(Calendar.DAY_OF_MONTH);

					log.info("IHArchiveAuditFunc:process_start:run:day:" + dayFunc);

					if (dayFunc == 1) {

						process_start_content(null);
					}

				} catch (Exception eFunc) {
					log.error("IHArchiveAuditFunc:process_start:run:error:", eFunc);
				} 
			}
		}, calendar(), 24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS);

		
		if (TaskProcessor.getControls()
				.containsKey("archiveAuditFuncScheduled")) {
			try {
				TaskProcessor.getControls().get("archiveAuditFuncScheduled")
						.cancel(false);
			} catch (Exception eFunc) {
				log.info("IHArchiveAuditFunc:process_start:error:", eFunc);
			}
		}
		TaskProcessor.getControls().put("archiveAuditFuncScheduled", shfFunc);

		return jpiFunc;
	}

	public BaseParamItem process_stop(BaseParamItem paramMap) throws Exception {

		BaseParamItem jpiFunc = new BaseParamItem();

		log.info("IHArchiveAuditFunc:stopTask:01");

		try {
		
			ScheduledFuture shfFunc = TaskProcessor.getControls().get(
					"archiveAuditFuncScheduled");

			log.info("IHArchiveAuditFunc:stopTask:02");
			if (shfFunc != null) { // может быть = null, когда приостановили, а
								// потом отключаем
				shfFunc.cancel(false);
			}
		} catch (Exception eFunc) {
			log.error("IHArchiveAuditFunc:stopTask:error:", eFunc);
			throw eFunc;
		}

		return jpiFunc;
	}

	public BaseParamItem task_run(BaseParamItem paramMap) throws Exception {

		BaseParamItem jpiFunc = new BaseParamItem();

		log.info("IHArchiveAuditFunc:task_run:01");

		try {
			Long archiveParamValue = (Long) paramMap.get("archiveParamValue");

			process_start_content(archiveParamValue);

		} catch (Exception eFunc) {
			log.error("IHArchiveAuditFunc:task_run:error:", eFunc);
			throw eFunc;
		}
		return jpiFunc;
	}

	private synchronized void process_start_content(Long archiveParamValue)
			throws Exception {

		String prev_date = null;
		BufferedWriter bwFunc = null;
		File fileFunc = null;
		int i = 1;
		String monthInterval = null;
		boolean hit = true;
		OutputStream os = null;

		log.info("IHArchiveAuditFunc:process_start_content:01");

		try {
			utx.begin();

			File dirFunc = new File(file_path);
			if (!dirFunc.exists()) {
				dirFunc.mkdirs();
			}

			List<String> losFunc = em
					.createNativeQuery(
							"select ST.VALUE_PARAM "
									+ "from SETTINGS_KNL_T st "
									+ "where ST.SIGN_OBJECT=? ")
					.setParameter(1, param_code).getResultList();

			if (losFunc != null && !losFunc.isEmpty()) {
				monthInterval = losFunc.get(0);
			}

			if (monthInterval == null) {
				monthInterval = "6";
			}

			log.info("IHArchiveAuditFunc:process_start_content:monthInterval:"
					+ monthInterval);

			List<Object[]> loFunc = em
					.createNativeQuery(
							"select to_char(AL.CREATED , 'YYYY_MM') vdate, AL.ID_SRV, AL.UP_ACTIONS, AL.UP_USERS, "
									+ "to_char(AL.DATE_ACTION ,'DD.MM.YYYY HH24:MI:SS') DATE_ACTION, to_char(AL.CREATED,'DD.MM.YYYY HH24:MI:SS') CREATED "
									+ "from ACTIONS_LOG_KNL_T AL "
									+ "where AL.CREATED < to_date('01.'||to_char(SYSDATE - INTERVAL '"
									+ monthInterval
									+ "' month, 'MM.YYYY'),'DD.MM.YYYY') "
									+ "order by AL.CREATED ")
					.getResultList();
			log.info("IHArchiveAuditFunc:process_start_content:02");

			int BUFF_SIZE = 1000 * 1024;

			for (Object[] objectArray : loFunc) {
				if (prev_date == null
						|| !prev_date.equals(objectArray[0].toString())) {

					if (bwFunc != null) {
						bwFunc.flush();
						bwFunc.close();
					}

					fileFunc = new File(file_path + "audit_func_"
							+ objectArray[0].toString() + ".txt");
					bwFunc = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(fileFunc), "Cp1251"), BUFF_SIZE);

					bwFunc.append("ID_SRV" + "\t" + "UP_ACTIONS" + "\t"
							+ "UP_USERS" + "\t" + "DATE_ACTION" + "\t"
							+ "CREATED" + "\n");
				}

				bwFunc.append ((objectArray[1] != null ? objectArray[1].toString()
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
					bwFunc.flush();
				}

				prev_date = objectArray[0].toString();
			}
			if (bwFunc != null) {
				bwFunc.flush();
			}

			em.createNativeQuery(
					"delete from ACTIONS_LOG_KNL_T AL "
							+ "where AL.CREATED < to_date('01.'||to_char(SYSDATE - INTERVAL '"
							+ monthInterval
							+ "' month, 'MM.YYYY'),'DD.MM.YYYY') ")
					.executeUpdate();

			utx.commit();
		} catch (Exception eFunc) {
			log.error("IHArchiveAuditFunc:process_start_content:error", eFunc);

			utx.rollback();

			/*
			 * можно в принципе файл и оставить. если в базе сведения не
			 * удалились, то при следующем запуске файл будет перезаписан
			 * i f(b w !=null){ bw .close } file .delete
			 */

			hit = false;
			throw eFunc;
		} finally {

			try {

				log.info("IHArchiveAuditFunc:process_start_content:finally:hit:"
						+ hit);

				DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
			
				File fFunc = new File(proc_aafunc_info_file);

				Properties properties = new Properties();

				properties.setProperty("exec_date",
						df.format(System.currentTimeMillis()));
				properties.setProperty("exec_hit", hit ? "true" : "false");

				properties.store(os = new FileOutputStream(fFunc), null);

			} catch (Exception eFunc) {
				log.error("IHArchiveAuditFunc:process_start_content:error:2:"
						+ eFunc);
			} finally {
				try {
					if (os != null) {
						os.close();
					}
				} catch (Exception eFunc) {
					log.error("IHArchiveAuditFunc:process_start_content:error:2:"
							+ eFunc);
				}
				try {
					if (bwFunc != null) {
						bwFunc.close();
					}
				} catch (Exception eFunc) {
					log.error("IHArchiveAuditFunc:process_start_content:error:2:"
							+ eFunc);
				}
			}
		}
	}

	private static Long calendar() {
	
		Long currentTime = System.currentTimeMillis();

		// 4.20 в jboss - это в реальном времени 5.20

		Calendar clnFunc = Calendar.getInstance();
		clnFunc.set(Calendar.HOUR_OF_DAY, 4);
		clnFunc.set(Calendar.MINUTE, 20);
		clnFunc.set(Calendar.SECOND, 0);
		clnFunc.set(Calendar.MILLISECOND, 0);

		Long trans = clnFunc.getTimeInMillis();

		Long startFunc = trans - currentTime;

		if (startFunc <= 0) {
			startFunc = startFunc + 24 * 60 * 60 * 1000;
		}

	
		return startFunc;
		//  5000L;
	}

}
