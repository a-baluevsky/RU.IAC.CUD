package ru.spb.iac.cud.exs.shedule;

import iac.cud.infosweb.dataitems.BaseParamItem;
import iac.cud.infosweb.local.service.ServiceReestrAction;
import iac.cud.infosweb.local.service.ServiceReestrPro;
import iac.cud.infosweb.remote.frontage.IRemoteFrontageLocal;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 public class LaunchBindingNoActTask {

	private static final Logger LOGGER = LoggerFactory
				.getLogger(LaunchBindingNoActTask.class);

		
	private static final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);

	private static String jndiBinding = "java:global/InfoS-ear/InfoS-ejb/IRemoteFrontage!iac.cud.infosweb.remote.frontage.IRemoteFrontageLocal";

	private static final String proc_binding_noact_exec_file = System
			.getProperty("jboss.server.config.dir")
			+ "/"
			+ "proc_binding_noact_exec.properties";


	public static void initTask(int delaySeconds) {

		LOGGER.debug("initTask");

		scheduler.schedule(new Runnable() {

			public void run() {

				String startDate = null, period = null, status = null;
				Properties properties = new Properties();
				String path = proc_binding_noact_exec_file;
			
				try {

					LOGGER.debug("initTask:run:01");

					DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm");

					File f = new File(path);

					if (f.exists()) {

						LOGGER.debug("initTask:run:02");

						properties.load(new FileInputStream(f));

						startDate = properties.getProperty("start_date");
						period = properties.getProperty("period");

						status = properties.getProperty("status");

						LOGGER.debug("initTask:run:status!:" + status);

						if (status != null && "active".equals(status)
								&& startDate != null && period != null) {

							LOGGER.debug("initTask:run:03");

							Context ctx = new InitialContext();

							BaseParamItem bpi = new BaseParamItem(
									ServiceReestrPro.BindingNoAct.name());
							bpi.put("gactiontype",
									ServiceReestrAction.PROCESS_START.name());

							bpi.put("startDate", df.parse(startDate));
							bpi.put("period", new Long(period));

							((IRemoteFrontageLocal) ctx.lookup(jndiBinding))
									.run(bpi);
						}
					}

				} catch (Exception e) {
					LOGGER.error("initTask:run:error:", e);
				} finally {
					try {
						scheduler.shutdown();
					} catch (Exception e) {
						LOGGER.error("initTask:run:finally:is:error:", e);
					}
				}
			}
		}, delaySeconds, TimeUnit.SECONDS);
	}

}
