<%-- 
    Document   : logout
    Created on : Jul 9, 2019, 1:46:58 PM
    Author     : sidde
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
   <head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Logout</title>
   </head>
   <body>
	<%
	   session.invalidate();
	   
	%>
	<a href="/SampleServlet">click</a>
   </body>
</html>
