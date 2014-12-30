package ru.spb.iac.cud.services.application;

import java.util.List;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceContext;

import ru.spb.iac.cud.context.application.ContextApplicationResultManager;
import ru.spb.iac.cud.exceptions.GeneralFailure;
import ru.spb.iac.cud.items.app.AppResult;
import ru.spb.iac.cud.items.app.AppResultRequest;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.SOAPBinding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService(targetNamespace = ApplicationResultServiceImpl.NS)
@HandlerChain(file = "/handlers_anonym.xml")
@BindingType(SOAPBinding.SOAP12HTTP_BINDING)
 public class ApplicationResultServiceImpl implements ApplicationResultService {

	public static final String NS = "http://application.services.cud.iac.spb.ru/";

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationResultServiceImpl.class);

	@Resource(name = "wsContext")
	private WebServiceContext wsContext;

	@WebMethod
	@WebResult(targetNamespace = NS)
	public List<AppResult> result(
			@WebParam(name = "appResultRequestList", targetNamespace = NS) List<AppResultRequest> appResultRequestList)
			throws GeneralFailure {

		return (new ContextApplicationResultManager()).result(
				appResultRequestList, getIDUser(), getIPAddress());

	}

	private Long getIDUser() throws GeneralFailure {
		MessageContext context = wsContext.getMessageContext();
		HttpServletRequest request = (HttpServletRequest) context
				.get(MessageContext.SERVLET_REQUEST);

		Long idUser = null;
		try {
			// user из ApplicantToken_1
			// это заявитель

			// когда пользователя определяли по логину, то сначала в
			// AppSOAPHandler
			// вычисляли его ИД через authenticate_login_obo
			// и в сессию клали уже Long idUser,
			// поэтому при извлечении из сессии можно было делать привидение к
			// Long
			// сейчас же мы кладём в сессиию ид пользователя из текстового поля
			// запроса
			// Long id/User =
			// (Long)r/equest.getSess/ion().getAttri/bu/te("user_id_principal");

			if (request.getSession().getAttribute("user_id_principal") != null
					&& !request.getSession().getAttribute("user_id_principal")
							.toString().isEmpty()) {

				// это заявитель
				idUser = new Long((String) request.getSession().getAttribute(
						"user_id_principal"));

				 

			} else {
				// анаоним
				idUser = -1L;
			}
			return idUser;

		} catch (Exception e) {
			throw new GeneralFailure("USER UID IS NOT CORRECT");
		}
	}

	private String getIPAddress() {
		MessageContext context = wsContext.getMessageContext();
		HttpServletRequest request = (HttpServletRequest) context
				.get(MessageContext.SERVLET_REQUEST);
		String ipAddress = request.getRemoteAddr();

		return ipAddress;
	}

}
