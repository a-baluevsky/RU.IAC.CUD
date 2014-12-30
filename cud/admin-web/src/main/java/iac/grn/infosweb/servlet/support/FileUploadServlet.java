package iac.grn.infosweb.servlet.support;

import iac.grn.infosweb.context.mc.arm.ArmManager;
import iac.grn.infosweb.context.mc.armgroup.ArmGroupManager;
import iac.grn.infosweb.context.mc.armsub.ArmSubManager;
import iac.grn.infosweb.context.mc.usr.UsrManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.servlet.ContextualHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

 public class FileUploadServlet extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FileUploadServlet.class);

	private static final long serialVersionUID = 1L;

	public FileUploadServlet() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		LOGGER.debug("FileUploadServlet:init:01");
	}

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		LOGGER.debug("FileUploadServlet:service:01");

		
		try {
			response.setContentType("text/html;charset=UTF-8");

			boolean isMultipartContent = ServletFileUpload
					.isMultipartContent(request);
			if (!isMultipartContent) {
					return;
			}

			String id_sys_param = request.getParameter("sessionId");

			String type_sys_param = request.getParameter("typeSystem");

			LOGGER.debug("FileUploadServlet:service:02:" + id_sys_param);
			LOGGER.debug("FileUploadServlet:service:02_1:" + type_sys_param);

			if (id_sys_param == null || id_sys_param.isEmpty()
					|| type_sys_param == null || type_sys_param.isEmpty()) {
				LOGGER.debug("FileUploadServlet:service:02_2:return");
				return;
			}

			Long id_sys = new Long(id_sys_param);

			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);

			List<FileItem> fields = upload.parseRequest(request);
			Iterator<FileItem> it = fields.iterator();
			if (!it.hasNext()) {
				LOGGER.debug("FileUploadServlet:service:03");
				return;
			}
				while (it.hasNext()) {
				LOGGER.debug("FileUploadServlet:service:04");

				FileItem fileItem = it.next();
				boolean isFormField = fileItem.isFormField();
				if (isFormField) {
				} else {
					LOGGER.debug("FileUploadServlet:service:05");

					saveToBD(fileItem, request, id_sys, type_sys_param,
							response);

					}
			}
		

		} catch (Exception e) {
			LOGGER.error("FileUploadServlet:service:ERROR:", e);
		}
		LOGGER.debug("FileUploadServlet:service:06");
	}

	
	private void saveToBD(FileItem item, final HttpServletRequest f_request,
			final Long id_sys, final String type_sys,
			final HttpServletResponse response) throws Exception {

		LOGGER.debug("FileUploadServlet:saveToBD:01:" + item.getName());

		
		final byte[] file_byte = item.get();
		try {
			new ContextualHttpServletRequest(f_request) {
				@Override
				public void process() throws Exception {
					doWork(file_byte, id_sys, type_sys, response);
				}
			}.run();

		} catch (Exception e) {
			LOGGER.error("FileUploadServlet:saveToBD:ERROR:", e);
		}
	}

	private void doWork(byte[] file_byte, Long id_sys, String type_sys,
			HttpServletResponse response) {

		LOGGER.debug("FileUploadServlet:doWork:01:" + type_sys);

		if (type_sys.equals("system")) {
			ArmManager sm = (ArmManager) Component.getInstance("armManager",
					ScopeType.EVENT);
			sm.saveArmCertificate(file_byte, id_sys);
		} else if (type_sys.equals("subsystem")) {
			ArmSubManager sm = (ArmSubManager) Component.getInstance(
					"armSubManager", ScopeType.EVENT);
			sm.saveArmSubCertificate(file_byte, id_sys);
		} else if (type_sys.equals("groupsystem")) {
			ArmGroupManager sm = (ArmGroupManager) Component.getInstance(
					"armGroupManager", ScopeType.EVENT);
			sm.saveArmGroupCertificate(file_byte, id_sys);
		} else if (type_sys.equals("user")) {
			UsrManager um = (UsrManager) Component.getInstance("usrManager",
					ScopeType.EVENT);
			boolean result = um.saveUserCertificate(file_byte, id_sys);

			if (!result) {
				// такой сертификат уже используется!
				try {
					PrintWriter out = response.getWriter();
					out.println("<html>");
					out.println("<head>");
					out.println("<title>FileUploadServlet</title>");
					out.println("</head>");
					out.println("<body>");
					out.println("<h1>FileUploadServlet</h1>");
					out.println("<div>");
					out.println("</div>");
					out.println("</body>");
					out.println("<script>");
					out.println("window.top.iframeCertExistFlag=1;");
					out.println("</script>");
					out.println("</html>");
				} catch (Exception e) {
					LOGGER.error("FileUploadServlet:doWork:ERROR:", e);
				}
			}
		}

	}
	
}
