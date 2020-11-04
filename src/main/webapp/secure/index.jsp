<%@page import="java.util.Date"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SampleServlet</title>
    </head>
    <body>
        <h1>Sample Application</h1>
        
        <%
            out.println("Welcome, "+request.getUserPrincipal());
            out.println("Has role: "+request.isUserInRole("JBossAdmin"));
            out.println("<p>"+new Date()+"</p>");
            
            out.println("<p> Cookie: "+request.getHeader("Cookie"));
            out.println("<p> Set-Cookie: "+response.getHeader("Set-Cookie"));
            
            out.println(request.getSession().getId());
		
        %>
        <p><a href="logout.jsp">Click Here to Logout</a></p>
	  
    </body>
</html>
