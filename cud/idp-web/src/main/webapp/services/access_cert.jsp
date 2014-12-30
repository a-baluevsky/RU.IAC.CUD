<%@ page language="java" contentType="text/html; charset=windows-1251"
	pageEncoding="windows-1251"%>
<%@ page
	import="org.jboss.as.web.security.SecurityContextAssociationValve,
                 org.picketlink.common.constants.GeneralConstants"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type"
	content="text/html; charset=windows-1251">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/stylesheet/theme.css" />
<script language="javascript"
	src="<%=request.getContextPath()%>/js/Code.js"></script>
</head>

<%
final String SAMLMessageKey = "CUD_SAML_MESSAGE";
final String HTTPMethodKey = "CUD_HTTP_METHOD";
final String RequestQueryStringKey = "CUD_REQUEST_QUERY_STRING";
final String RequestRequestURIKey = "CUD_REQUEST_REQUEST_URI";

org.apache.catalina.connector.Request request2=null;
request2 = SecurityContextAssociationValve.getActiveRequest();
String samlRequestMessage = (String) request2.getSessionInternal().getNote(GeneralConstants.SAML_REQUEST_KEY);
String httpMethod =(String) request.getSession().getAttribute("incoming_http_method");

String multiAuthRequest = (String) request.getSession().getAttribute(
		"multi_auth_request");

String RequestQueryString = (String) request.getSession().getAttribute(RequestQueryStringKey);
String RequestRequestURI  = (String) request.getSession().getAttribute(RequestRequestURIKey);

%>

<body style="padding: 10px;">

	<table width="100%" cellspacing="0" frame="void" border="0">
		<tr height="40px">
			<td align="right"><span
				style="font-size: 12pt; font-weight: bold;" id="PlugInEnabledTxt"></span>
				<!--img src="Img/red_dot.png" width="10" height="10" alt="Плагин загружен" id="PluginEnabledImg"
                                    style="padding-left: 5px" /--> <a
				class="active" style="display: none; font-size: 12pt;" target="#"
				id="PlugInEnabledTxtDownload"
				href="http://www.cryptopro.ru/products/cades/plugin/get">Скачать
					плагин</a>
				<div style="clear: both"></div></td>
		</tr>
	</table>
	<table border="0" width="100%" cellspacing="0" height="50px">
		<% if(request.getParameter("success")!=null){%>
		<tr>
			<td align="center"><span style="color: red"> Пользователь
					не идентифицирован! <% if(request.getParameter("revokedCertificate")!=null){%>
					Сертификат в списке отозванных! <%} %>
			</span></td>
		</tr>
		<%} %>
		<tr>
			<td height="50px" valign="top">

				<form name="aspnetForm" method="post"
					action="<%=request.getContextPath()%>/WebCertAction<%=(request.getParameter("overauth")!=null?"?overauth":"") %>"
					id="aspnetForm">

					<% if(request.getParameter("backUrl")!=null){%>
					<input type="hidden" name="backUrl"
						value="<%=request.getParameter("backUrl")%>" />
					<%} %>
					<% if(samlRequestMessage!=null&&!samlRequestMessage.isEmpty()){%>
					<input type="hidden" name="<%=SAMLMessageKey%>"
						value="<%=samlRequestMessage%>" />
					<%} %>
					<% if(httpMethod!=null&&!httpMethod.isEmpty()){%>
					<input type="hidden" name="<%=HTTPMethodKey%>"
						value="<%=httpMethod%>" />
					<%} %>
					<% if(RequestQueryString!=null&&!RequestQueryString.isEmpty()){%>
					<input type="hidden" name="<%=RequestQueryStringKey%>"
						value="<%=RequestQueryString%>" />
					<%} %>
					<% if(RequestRequestURI!=null&&!RequestRequestURI.isEmpty()){%>
					<input type="hidden" name="<%=RequestRequestURIKey%>"
						value="<%=RequestRequestURI%>" />
					<%} %>

					<input type="hidden" name="ctl00$isPlugInEnabled"
						id="ctl00_isPlugInEnabled" value="0" /> <input type="hidden"
						name="ctl00$BrowserName" id="ctl00_BrowserName" value="" /> <input
						type="hidden" name="signatureValue" id="signatureValue" value="" />
					<input type="hidden"
						name="ctl00$ContentPlaceHolder1$SignatureTypeHF"
						id="ctl00_ContentPlaceHolder1_SignatureTypeHF" value="" /> <span
						style="font-size: 12pt;">Выберите сертификат:</span>
					<div style="margin-top: 15px;">
						<select size="12" name="ctl00$ContentPlaceHolder1$CertListBox"
							id="ctl00_ContentPlaceHolder1_CertListBox"
							style="border: 1px solid silver; width: 100%;">

						</select>
					</div>
					<div>
						<input id="SignBtn" type="button" value="Отправить"
							name="SignData" class="but_class"
							onclick="SignBtn_Click('ctl00_ContentPlaceHolder1_CertListBox', 'ctl00_BrowserName', 'ctl00_ContentPlaceHolder1_DataToSignTxtBox', 'ctl00_ContentPlaceHolder1_TSPAddressTxtBox', 'signatureValue', 'ctl00_ContentPlaceHolder1_SignatureTypeHF');" />
					</div>
					<% if(multiAuthRequest!=null){%>
					<div style="margin-top: 5px;">
                       <a href="<%=request.getContextPath()+"/AccessServicesWebLogin"%>">Войти по логину</a>
                    </div>
					<%} %>
					<script language="javascript">

                        CheckForPlugIn('ctl00_isPlugInEnabled', 'ctl00_BrowserName');
                        
                            //Выставляем параметры подписи по умолчанию.
                          /*  if (document.getElementById('ctl00_ContentPlaceHolder1_SignatureTypeHF').value == 0) {
                                document.getElementById('RdBtn1').checked = true;
                                SignatureTypeBtnClick("1", 'ctl00_ContentPlaceHolder1_TSPAddressTxtBox');
                           } else {
                                document.getElementById('RdBtn2').checked = true;
                                SignatureTypeBtnClick("2", 'ctl00_ContentPlaceHolder1_TSPAddressTxtBox');
                            }*/
                            
                            // Скрываем или показываем панель загрузки плагина
                            var isPlugInExists = document.getElementById('ctl00_isPlugInEnabled').getAttribute("value");
                            if (isPlugInExists == "1") {
                                FillCertList('ctl00_ContentPlaceHolder1_CertListBox', 'ctl00_BrowserName');
                            } else {
                                document.getElementById('SignBtn').setAttribute("disabled", "disabled");
                            }
                           // CheckServerResponce('signatureValue');        
                        </script>

				</form>

			</td>
		</tr>
	</table>

</body>
</html>