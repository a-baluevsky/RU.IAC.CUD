package ru.spb.iac.cud.exs.shedule;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 public class LaunchCRLTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(LaunchCRLTask.class);

		
	private static final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);


	public static void initTask(int delaySeconds) {
		LOGGER.debug("initTask:01");

		
		scheduler.schedule(new Runnable() {

			public void run() {

				try {

					LOGGER.debug("initTask:run");

					
					LaunchCRLRepeatTask.initTask(calendar());

				} catch (Exception e) {
					LOGGER.error("initTask:error:", e);
				} finally {
					try {
						scheduler.shutdown();
					} catch (Exception e) {
						LOGGER.error("initTask:finally:is:error:", e);
					}
				}
			}
		}, delaySeconds, TimeUnit.SECONDS);
	}

	public static Long calendar() {

		Long currentTime = System.currentTimeMillis();

		Calendar cln = Calendar.getInstance();
		//cln.set(Calendar.HOUR_OF_DAY, 3);
		cln.set(Calendar.HOUR_OF_DAY, 15);
		cln.set(Calendar.MINUTE, 0);
		cln.set(Calendar.SECOND, 0);
		cln.set(Calendar.MILLISECOND, 0);

		Long trans = cln.getTimeInMillis();

		Long start = trans - currentTime;

		if (start <= 0) {
			start = start + 24 * 60 * 60 * 1000;
		}

		
		LOGGER.debug("calendar:03:start:" + start);

		return start /* 10L */;
	}
}
