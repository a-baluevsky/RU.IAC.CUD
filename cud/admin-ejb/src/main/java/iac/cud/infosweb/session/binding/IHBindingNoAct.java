package iac.cud.infosweb.session.binding;

import iac.cud.infosweb.dataitems.BaseParamItem;
import iac.cud.infosweb.local.service.IHLocal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import iac.grn.infosweb.context.proc.TaskProcessor;

 
 
import org.apache.log4j.Logger;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
 public class IHBindingNoAct extends IHBindingBase implements IHLocal {

	@PersistenceContext(unitName = "InfoSCUD-web")
	EntityManager em;

	@Resource
	UserTransaction utx;

	private Logger log = Logger.getLogger(IHBindingNoAct.class);

	private static final String proc_binding_noact_info_file = System
			.getProperty("jboss.server.config.dir")
			+ "/"
			+ "proc_binding_noact_info.properties";

	private static final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);

	public BaseParamItem process_start(BaseParamItem paramMap) throws Exception {

		BaseParamItem jpiBna = new BaseParamItem();

		log.info("IHBindingNoAct:process_start:01");

		Date startDate = (Date) paramMap.get("startDate");
		Long period = (Long) paramMap.get("period");

		if (startDate == null || period == null) {
			log.info("IHBindingNoAct:process_start:return");
			return jpiBna;
		}

		Long currentTime = System.currentTimeMillis();
		Long trans = startDate.getTime();

		Long start = trans - currentTime;

		log.info("IHBindingNoAct:process_start:start:1:" + start);

		while (start < 0) {

			log.info("IHBindingNoAct:process_start:01:2");

			int batch = 0;
			while (trans < currentTime) {
				batch++;
				trans += period * 24 * 60 * 60 * 1000;
				if (batch % 100 == 0) {
					log.info("IHBindingNoAct:process_start:batch:" + batch);
				}
			}
			start = trans - System.currentTimeMillis();
			currentTime = System.currentTimeMillis();

			log.info("IHBindingNoAct:process_start:start:2:" + start);
		}

		ScheduledFuture shfBna = scheduler.scheduleAtFixedRate(new Runnable() {

			public void run() {

		
				try {

					log.info("IHBindingNoAct:process_start:run");

				
					process_start_content();
			
				} catch (Exception eBna) {
					log.error("IHBindingNoAct:process_start:run:error:", eBna);
				} 
			}
		}, start, period * 24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS);

	
		if (TaskProcessor.getControls().containsKey("bindingNoActScheduled")) {
			try {
				TaskProcessor.getControls().get("bindingNoActScheduled")
						.cancel(false);
			} catch (Exception eBna) {
				log.info("IHBindingNoAct:process_start:error:", eBna);
			}
		}
		TaskProcessor.getControls().put("bindingNoActScheduled", shfBna);

		return jpiBna;
	}

	public BaseParamItem process_stop(BaseParamItem paramMap) throws Exception {

		BaseParamItem jpiBna = new BaseParamItem();

		log.info("IHBindingNoActa:process_stop:01");

		try {
		
			ScheduledFuture shfBna = TaskProcessor.getControls().get(
					"bindingNoActScheduled");

			log.info("IHBindingNoAct:process_stop:02");
			if (shfBna != null) { // может быть = null, когда приостановили, а
								// потом отключаем
				shfBna.cancel(false);
			}
		} catch (Exception eBna) {
			log.error("IHBindingNoAct:process_stop:error:", eBna);
			throw eBna;
		}

		return jpiBna;
	}

	public BaseParamItem task_run(BaseParamItem paramMap) throws Exception {

		BaseParamItem jpiBna = new BaseParamItem();

		log.info("IHBindingNoAct:task_run:01");

		try {
		
			process_start_content();

		} catch (Exception eBna) {
			log.error("IHBindingNoAct:task_run:error:", eBna);
			throw eBna;
		}
		return jpiBna;
	}

	private synchronized void process_start_content() throws Exception {

		boolean hit = true;
		OutputStream os = null;

		log.info("IHBindingNoAct:process_start_content:01");

		try {
			if (BindingProcessor.getControls().containsKey("binding_no_act")) {
				log.info("IHBindingNoAct:process_start_content:return");
				return;
			}

			BindingProcessor.getControls().put("binding_no_act", "");

			utx.begin();

			em.createNativeQuery(
					"delete from BINDING_AUTO_LINK_BSS_T tt "
							+ "WHERE TT.TYPE_BINDING = 2 ").executeUpdate();

			em.createNativeQuery(
					"INSERT INTO BINDING_AUTO_LINK_BSS_T (UP_USERS, UP_ISP_SIGN_USER, TYPE_BINDING ) "
							+ "select   AU.ID_SRV, cl_usr_full.SIGN_OBJECT, 2 "
							+ "                     from AC_USERS_KNL_T au, "
							+ "                    (select max(CL_usr.ID_SRV) CL_USR_ID,  CL_USR.SIGN_OBJECT  CL_USR_CODE "
							+ "                   from ISP_BSS_T cl_usr "
							+ "                  group by CL_usr.SIGN_OBJECT "
							+ "                   ) t1_no_act, "
							+ "                   ISP_BSS_T cl_usr_full, "
							+ "                    ISP_BSS_T cl_usr_no_act_full "
							+ "                   where   AU.UP_SIGN_user is not null "
							+ "                    and  upper( REGEXP_REPLACE(cl_usr_full.FIO,'( ){2,}', ' ')) like upper( REGEXP_REPLACE(cl_usr_no_act_full.FIO,'( ){2,}', ' ')) "
							+ "                    and cl_usr_full.STATUS='A' "
							+ "                    and AU.UP_SIGN= substr(cl_usr_full.SIGN_OBJECT ,1,3)||'00000' "
							+ "                     and cl_usr_no_act_full.STATUS='H' "
							+ "                     and cl_usr_no_act_full.ID_SRV=t1_no_act.CL_USR_ID "
							+ "                     and   t1_no_act.CL_USR_CODE=au.UP_SIGN_user "
							+ " group by AU.ID_SRV, cl_usr_full.SIGN_OBJECT ")
					.executeUpdate();

			utx.commit();
		} catch (Exception eBna) {
			log.error("IHBindingNoAct:process_start_content:error", eBna);

			utx.rollback();

			hit = false;
			throw eBna;

		} finally {

			try {

				BindingProcessor.getControls().remove("binding_no_act");

				log.info("IHBindingNoAct:process_start_content:finally:hit:"
						+ hit);

				DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
		
				File fBna = new File(proc_binding_noact_info_file);

				Properties properties = new Properties();

				properties.setProperty("exec_date",
						df.format(System.currentTimeMillis()));
				properties.setProperty("exec_hit", hit ? "true" : "false");

				properties.store(os = new FileOutputStream(fBna), null);

			} catch (Exception eBna) {
				log.error("IHBindingNoAct:process_start_content:error:2:", eBna);
			} finally {
				try {
					if (os != null) {
						os.close();
					}
				} catch (Exception eBna) {
					log.error("IHBindingNoAct:process_start_content:error:2:", eBna);
				}
			}
		}
	}
	

}
