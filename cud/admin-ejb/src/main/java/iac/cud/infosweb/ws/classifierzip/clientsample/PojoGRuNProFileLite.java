package iac.cud.infosweb.ws.classifierzip.clientsample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.HashMap; 
import java.util.List;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import mypackage.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 public class PojoGRuNProFileLite {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PojoGRuNProFileLite.class);

	private UserTransaction utx = null;

	private EntityManager em = null;

	
	private String tmp_dir_name = Configuration.getClassifLoadTmp();

	private int result = -1;

	private File file1 = null;

	private String ftype;

	private HashMap hm = new HashMap();

	private HashMap hm_act = new HashMap();

	private File buf_rec_file;

	private File buf_rec1_file;

	
	private BufferedReader br = null;

	private BufferedWriter bw = null;

	Long seancact = null;
	Integer clVersion = null;

		public int process22(String fileClassifName, EntityManager em,
			UserTransaction utx, Long seancact, Integer clVersion) {

		LOGGER.debug("GRuNProFileLite:process:01");
		try {

		
			this.result = -1;

			this.utx = utx;
			this.em = em;
			this.seancact = seancact;
			this.clVersion = clVersion;

		
			this.file1 = new File(fileClassifName);

			phase_fixed(this.file1);

			temp_files_init();

			clear_prev_load();

			this.utx.commit();
			this.utx.begin();

			configuration_file();

			file_action(this.file1, "1");

		
			records_db("1");

			this.utx.commit();
			this.utx.begin();

			this.result = 1;

			seanc_end(1);

			this.utx.commit();
		
			
		} catch (Exception e) {
			LOGGER.error("GRuNProFileLite:process:error:", e);
			try {
				this.result = -1;

				this.utx.rollback();

				this.utx.begin();

				seanc_end(0);

				this.utx.commit();
			
			} catch (Exception e2) {
				LOGGER.error("GRuNProFileLite:process:rollback:error:" + e2);
			}
		} finally {
			
			delTempFiles();
			delClassifFiles();

			reset_field();
		}
		return this.result;
	}

	private void reset_field() {

		LOGGER.debug("GRuNProFileLite:reset_field:01");

		file1 = null;
		ftype = null;
		hm = new HashMap();
		hm_act = new HashMap();
		buf_rec_file = null;
		buf_rec1_file = null;
		br = null;
		bw = null;
	}

	private void temp_files_init() {

		LOGGER.debug("GRuNProFileLite:temp_files_init:01");

		File tmp_dir = new File(tmp_dir_name);
		if (!tmp_dir.exists()) {
			tmp_dir.mkdir();
		}
		buf_rec1_file = new File(tmp_dir_name + "f1_t1");
		if (buf_rec1_file.exists()) {
			buf_rec1_file.deleteOnExit();
		}

	}

	
	private void control_files_failed() throws Exception {
		LOGGER.debug("control_files_failed!!!");
		throw new BreakException();
	}

	public void clear_prev_load() throws Exception {

		LOGGER.debug("GRuNProFileLite:clear_prev_load:01");

		try {
			bd_execute("TRUNCATE TABLE ISP_TEMP_BSS_T ");

		} catch (Exception e) {
			LOGGER.error("clear_prev_load:error:", e);
			throw e;
		}
	}

	public void bd_execute(String query) throws Exception {
		try {

		
			this.em.createNativeQuery(query).executeUpdate();

		} catch (Exception e) {
			LOGGER.error("bd_execute:error=", e);
			throw e;
		}
	}

	

	private void configuration_file() throws Exception {
		try {
			
			List<Object[]> lo = this.em
					.createNativeQuery(
							"select to_char(INF.ORDER_), INF.NAME_, to_char(INF.ID_SRV) from ISP_INPUT_FIELDS inf order by ORDER_ ")
					.getResultList();

			for (Object[] rec : lo) {
				hm_act.put((String) rec[0], new String[] { (String) rec[0],
						(String) rec[1], (String) rec[2] });
			}

		} catch (Exception e) {
			LOGGER.error("configuration_file:error:", e);
			throw e;
		}
	}

	private void file_action(File file, String s) throws Exception {

		ftype = s;

		try {

			if (ftype.equals("1")) {
				hm = hm_act;
				buf_rec_file = buf_rec1_file;
			}
		
			file_interaction(file);

		} catch (Exception e) {
			LOGGER.error("file_action:error:", e);
			throw e;
		}
	}

	private void file_interaction(File file) throws Exception {

		String s;
		try {

			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "Cp1251"));

			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(buf_rec_file), "Cp1251"));

			int i = 1;

		
			while ((s = br.readLine()) != null) {
				file_record_handling(s, i);
				i++;
				if ((i % 1000) == 0) {
					bw.flush();
				}
			}

			/*
			 * убрал, так как переместил подчёт строк в inputfiles_save() с
			 * помощью LineNumberReader bd_execute(
			 * "update LOG_LOADING_SESSIONS LL " + "set LL.REC_CNT="+i+
			 * " where LL.ID_LOG="+id_seancact);
			 */

			if (i == 1) {// файл совсем пуст
				LOGGER.debug("file_interaction:файл " + file.getName() + " пуст");

				control_files_failed();
			}

		
		} catch (Exception e) {
			LOGGER.error("file_interaction:error=", e);
			throw e;
		} finally {
			try {
				br.close();
				bw.close();
			} catch (Exception e) {
				LOGGER.error("file_interaction:finally:error=", e);
			}
		}
	}

	

	private void file_record_handling(String ps, int pi) throws Exception {

		try {

			file_record_write(ps, pi);

		} catch (Exception e) {
			LOGGER.error("file_record_handling:error=", e);
			throw e;
		}
	}

	/**
	 * Разбор строки и запись в буфер
	 * 
	 * @param ps
	 *            строка
	 * @param ppi
	 *            номер строки
	 * @throws Exception
	 */
	private void file_record_write(String ps, int ppi) throws Exception {

		
		try {

			String[] sa = ps.split(">", -1);
			
			if ((sa.length - 1) != hm.size()) {
				LOGGER.debug("file_record_write: в записи № " + ppi
						+ " количество столбцов - " + sa.length
						+ ", а должно быть - " + hm.size());
				control_files_failed();

			}

			
			bw.append(ps + "\n");

		} catch (Exception e) {
			LOGGER.error("file_record_write:error=", e);
			throw e;
		}
	}

	private void records_db(String s) throws Exception {

		try {
			ftype = s;
			if (ftype.equals("1")) {
				buf_rec_file = buf_rec1_file;
				hm = hm_act;
				}
			records_db_ir();
		} catch (Exception e) {
			LOGGER.error("records_db:error=", e);
			throw e;
		}
	}

	/**
	 * Работа с временным буфером на уровне записей
	 * 
	 * @throws Exception
	 */
	private void records_db_ir() throws Exception {
		PreparedStatement ps = null;
		try {
			int i = 1;
			String  st;// numrec;

			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					buf_rec_file), "Cp1251"));

			while ((st = br.readLine()) != null) {

				records_db_wh(ps, st);

				if ((i % 1000) == 0) {
					this.utx.commit();
					this.utx.begin();
				}
				i++;
			}
		} catch (Exception e) {
			LOGGER.error("records_db_ir:error=", e);
			throw e;
		} finally {
			br.close();
		}
	}

	/**
	 * Работа с временным буфером на уровне столбцов
	 * 
	 * @param ps
	 *            заготовка для записи в БД
	 * @param rec
	 *            запись файла
	 * @throws Exception
	 */
	private void records_db_wh(PreparedStatement ps, String rec)
			throws Exception {
		try {
			int i = 1;
			String query;
			StringBuffer sbn = new StringBuffer(), sbv = new StringBuffer();
			String[] sa = rec.split(">", -1);

			for (String pss : sa) {

		
				if (i <= hm.size()) {

					String[] sconfrec = ((String[]) hm.get(Integer.toString(i)));

					if (i == 1) {
						sbn.append(sconfrec[1]);
						sbv.append("'"
								+ ((pss.toLowerCase()).equals("null") ? ""
										: pss.trim().replaceAll("'", "''"))
								+ "'");

					} else {
						sbn.append(", " + sconfrec[1]);
						sbv.append(", '"
								+ ((pss.toLowerCase()).equals("null") ? ""
										: pss.trim().replaceAll("'", "''"))
								+ "'");
					}

				}

					i++;
			}

			query = "INSERT /*+ APPEND */ into ISP_TEMP_BSS_T (" + sbn
					+ ", UP_ISP_LOAD ) values ( " + sbv + ", " + this.seancact
					+ " ) ";

		
			this.em.createNativeQuery(query).executeUpdate();

		} catch (Exception e) {
			LOGGER.error("records_db_wh:error=", e);
			throw e;
		}
	}

	
	public void phase_fixed(File file) throws Exception {

		FileReader fr = null;
		LineNumberReader lineNumberReader = null;
		String fileName;
		int file_rec_count;

		try {
			fileName = file.getName();

			fr = new FileReader(file);
			lineNumberReader = new LineNumberReader(fr);
			lineNumberReader.skip(Long.MAX_VALUE);
			file_rec_count = lineNumberReader.getLineNumber();

			bd_execute("update JOURN_ISP_LOAD LL " + "set LL.FILE_NAME = '"
					+ fileName + "', " + "LL.FILE_REC_COUNT = "
					+ file_rec_count + ", " + "LL.CLASSIF_VERSION =  "
					+ this.clVersion + " " + "where LL.ID_SRV=" + this.seancact);
		} catch (Exception e) {
			LOGGER.error("phase_fixed:error:", e);
			throw e;
		} finally {
			if (fr != null) {
				fr.close();
			}
			if (lineNumberReader != null) {
				lineNumberReader.close();
			}
		}
	}

	public void seanc_end(int success) throws Exception {
		try {
			// 1 - успех
			// 0 или null- не успех

			bd_execute("update JOURN_ISP_LOAD LL "
					+ "set LL.LOAD_FINISH = sysdate, "
					+ "LL.SUCCESS =  "
					+ success
					+ ", "
					+ "LL.LOAD_REC_COUNT = (select count(*) from ISP_TEMP_BSS_T ) "
					+ "where LL.ID_SRV=" + this.seancact);
		} catch (Exception e) {
			LOGGER.error("seanc_end:error:", e);
			throw e;
		}
	}

	public void saveCount(File file1, File file2,
			HashMap<String, String> paramMap) throws Exception {
		FileReader fr = null;
		LineNumberReader lineNumberReader = null;
		int lines = -1;

		LOGGER.debug("saveCount:01");

		try {

			if (file1 == null) {
				return;
			}

			try {
				fr = new FileReader(file1);
				lineNumberReader = new LineNumberReader(fr);
				lineNumberReader.skip(Long.MAX_VALUE);
				lines = lineNumberReader.getLineNumber();

				if (lines != 0) { // исключаем заголовок
					lines = lines - 1;
				}
				LOGGER.debug("saveCount:lines1:" + lines);

				paramMap.put("act_file_count", Long.toString(lines));

			} catch (Exception e) {
				LOGGER.error("inputfiles_save:lines1:error", e);
			}

			if (file2 != null) {
				try {
					fr = new FileReader(file2);
					lineNumberReader = new LineNumberReader(fr);
					lineNumberReader.skip(Long.MAX_VALUE);
					lines = lineNumberReader.getLineNumber();

					if (lines != 0) { // исключаем заголовок
						lines = lines - 1;
					}
					LOGGER.debug("inputfiles_save:lines2:" + lines);

					paramMap.put("dep_file_count", Long.toString(lines));

				} catch (Exception e) {
					LOGGER.error("inputfiles_save:lines1:error", e);
				}
			}
	
		} catch (Exception e) {
			LOGGER.error("saveCount:error:", e);
		} finally {
			if (fr != null) {
				fr.close();
			}
			if (lineNumberReader != null) {
				lineNumberReader.close();
			}
		}
	}

	

	
	public String time_period(Date d1) {

		String s = "";
		Date d2 = new Date();

		long diff = d2.getTime() - d1.getTime();

		long diffDays = diff / (24 * 60 * 60 * 1000);
		diff -= diffDays * (24 * 60 * 60 * 1000);

		long diffHours = diff / (60 * 60 * 1000);
		diff -= diffHours * (60 * 60 * 1000);

		long diffMinutes = diff / (60 * 1000);
		diff -= diffMinutes * (60 * 1000);

		long diffSeconds = diff / 1000;
		diff -= diffSeconds * 1000;

		if (diffDays != 0) {
			s = diffDays + " дней, ";
		}
		if (diffHours != 0) {
			s += (diffHours + " часов, ");
		}
		if (diffMinutes != 0) {
			s += (diffMinutes + " минут, ");
		}
		if (diffSeconds != 0) {
			s += (diffSeconds + " секунд, ");
		}
		if (diff != 0) {
			s += (diff + " миллисекунд");
		}
		return s;
	}

	private void delTempFiles() {
		try {
			if (buf_rec1_file != null) {
				buf_rec1_file.delete();
			}

		} catch (Exception e) {
			LOGGER.error("delTempFiles:error:", e);
		}
	}

	public void delClassifFiles() {

		LOGGER.debug("delClassifFiles:01:" + file1.getAbsolutePath());
		try {
			if (file1 != null) {
				File parent_dir = file1.getParentFile();

				LOGGER.debug("delClassifFiles:02:" + parent_dir.getName());

				if (parent_dir != null && parent_dir.isDirectory()) {

					LOGGER.debug("delClassifFiles:03");

					for (File file_in_dir : parent_dir.listFiles()) {

						LOGGER.debug("delClassifFiles:04:"
								+ file_in_dir.getName());

						try {
							file_in_dir.delete();
						} catch (Exception e) {
							LOGGER.error("delClassifFiles:error:01:"
									+ file_in_dir.getName());
							LOGGER.error("delClassifFiles:error:02:", e);
						}
					}
				}
			}

		} catch (Exception e) {
			LOGGER.error("delTempFiles:error:", e);
		}
	}

	

}
