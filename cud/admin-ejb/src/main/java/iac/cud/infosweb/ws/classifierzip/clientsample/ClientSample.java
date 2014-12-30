package iac.cud.infosweb.ws.classifierzip.clientsample;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import iac.cud.infosweb.ws.classifierzip.*;
import iac.cud.infosweb.ws.classif.common.Response4;

 public class ClientSample {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientSample.class);

	private static String fileZipName = "classif.zip";

	public static String getVersion() {

		LOGGER.debug("ClientSample:getVersion:01");
		LOGGER.debug("Create Web Service Client...");
		ClassifierZip_Service service1 = new ClassifierZip_Service();
		LOGGER.debug("Create Web Service...");
		ClassifierZip port1 = service1.getClassifierZipPort();
		LOGGER.debug("Call Web Service Operation...");
	
		int actualDoc = 0;
		FileOutputStream fos = null;
		String result = null;
		try {

			ResponseElement53 re53 = port1
					.getClassifierZipListByClassifierNumber(
							CUDWSSMEVConstants.getLogin(),
							CUDWSSMEVConstants.getPasswordMD5(),
							CUDWSSMEVConstants.getClassifRegNum());

			if (re53.getItem() == null) {
				LOGGER.debug("Server said: return");
				return null;
			}

			LOGGER.debug("Server said: " + re53.getItem().size());

			for (Response4 r1 : re53.getItem()) {

				if (r1.getActualDoc() > actualDoc) {
					actualDoc = r1.getActualDoc();
				}
				LOGGER.debug("actualDoc: " + actualDoc);

			}
			return String.valueOf(actualDoc);

		} catch (Exception e) {
			LOGGER.error("error", e);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				LOGGER.error("error", e);
			}
		}

		return result;
	}

	
	public static void run2(EntityManager em, UserTransaction utx,
			Long seancact, Integer clVersion) {

		LOGGER.debug("ClientSample:run:01+:" + CUDWSSMEVConstants.fileZipPath);
		LOGGER.debug("Create Web Service Client...");
		ClassifierZip_Service service1 = new ClassifierZip_Service();
		LOGGER.debug("Create Web Service...");
		ClassifierZip port1 = service1.getClassifierZipPort();
		LOGGER.debug("Call Web Service Operation...");
	
		int actualDoc = 0;
		FileOutputStream fos = null;

		try {
	
			if (clVersion != null) {

				actualDoc = clVersion.intValue();

			} else {

				ResponseElement53 re53 = port1
						.getClassifierZipListByClassifierNumber(
								CUDWSSMEVConstants.getLogin(),
								CUDWSSMEVConstants.getPasswordMD5(),
								CUDWSSMEVConstants.getClassifRegNum());

				if (re53.getItem() == null) {
					LOGGER.debug("Server said: return");
					return;
				}

				LOGGER.debug("Server said: " + re53.getItem().size());

				for (Response4 r1 : re53.getItem()) {

					if (r1.getActualDoc() > actualDoc) {
						actualDoc = r1.getActualDoc();
					}
					LOGGER.debug("actualDoc: " + actualDoc);

				}
			}
			ResponseElement51 re51 = port1.getClassifierByClassifierNumber(
					CUDWSSMEVConstants.getLogin(),
					CUDWSSMEVConstants.getPasswordMD5(),
					CUDWSSMEVConstants.getClassifRegNum(), actualDoc);

			if (re51.getItem() == null) {
				LOGGER.debug("Server said: return");
				return;
			}

			LOGGER.debug("Server said: " + re51.getItem().size());

			for (Item51 r1 : re51.getItem()) {

				LOGGER.debug("Fullname: " + r1.getFileName());

				fos = new FileOutputStream(CUDWSSMEVConstants.fileZipPath
						+ fileZipName);

				fos.write(r1.getFileContent());

				fos.flush();
			}

			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				LOGGER.error("error", e);
			}

			String fileClassifName = unZip(CUDWSSMEVConstants.fileZipPath
					+ fileZipName);

			runLoader2(fileClassifName, em, utx, seancact, actualDoc);

			LOGGER.debug("***********************");
			LOGGER.debug("Call Over!");

		} catch (Exception e) {
				LOGGER.error("error", e);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				LOGGER.error("error", e);
			}
		}
	}

	public static String unZip(String file) {

		String result = null;
		final int BUFFER = 2048;
		try {
			BufferedOutputStream dest = null;
			FileInputStream fis = new FileInputStream(file);
			ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(fis));
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				LOGGER.debug("Extracting: " + entry);

				if (entry.getName().startsWith("1b")) {
					result = entry.getName();
				}

				int count;
				byte data[] = new byte[BUFFER];
				FileOutputStream fos = new FileOutputStream(
						CUDWSSMEVConstants.fileZipPath + entry.getName());
				dest = new BufferedOutputStream(fos, BUFFER);
				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}
			zis.close();
		} catch (Exception e) {
			LOGGER.error("error", e);
		}

		return result;
	}

	public static void runLoader2(String fileClassifName, EntityManager em,
			UserTransaction utx, Long seancact, Integer clVersion) {

		final int[] result = new int[] { -1 };
		try {

			PojoGRuNProFileLite pojo = new PojoGRuNProFileLite();

			result[0] = pojo.process22(CUDWSSMEVConstants.fileZipPath
					+ fileClassifName, em, utx, seancact, clVersion);

		} catch (Exception e) {
			LOGGER.error("error", e);
		}
	}

}
