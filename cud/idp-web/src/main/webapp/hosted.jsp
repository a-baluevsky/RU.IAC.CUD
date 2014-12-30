<%@ page language="java" contentType="text/html; charset=windows-1251"
	pageEncoding="windows-1251"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
<title>ЦУД</title>
<link rel="StyleSheet" href="/css/idp.css" type="text/css">
</head>

<body>
	<i>cud:hosted</i>
	<br />
	<br />
	<br /> Вы аутентифицированы как :
	<% if(request.getUserPrincipal()!=null) {%>
	<%=request.getUserPrincipal().getName() %>
	<%} %>
</body>
</html>