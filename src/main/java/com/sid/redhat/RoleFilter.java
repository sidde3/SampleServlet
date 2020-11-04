/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sid.redhat;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.IDToken;

/**
 *
 * @author sidde
 */
public class RoleFilter implements Filter {

   private static final boolean debug = true;

   // The filter configuration object we are associated with.  If
   // this value is null, this filter instance is not currently
   // configured. 
   private FilterConfig filterConfig = null;

   public RoleFilter() {
   }

   /**
    *
    * @param request The servlet request we are processing
    * @param response The servlet response we are creating
    * @param chain The filter chain we are processing
    *
    * @exception IOException if an input/output error occurs
    * @exception ServletException if a servlet error occurs
    */
   @Override
   public void doFilter(ServletRequest request, ServletResponse response,
	     FilterChain chain)
	     throws IOException, ServletException {
	HttpServletRequest req = (HttpServletRequest) request;
	
	List<String> roles = new ArrayList<String>();
	
	String principalname = req.getUserPrincipal().getName();
	
	final Principal userPrincipal = req.getUserPrincipal();

	if (userPrincipal instanceof KeycloakPrincipal) {

	   KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) userPrincipal;
	   IDToken token = kp.getKeycloakSecurityContext().getIdToken();
	   
	   principalname = token.getGivenName();
	  
	   Map<String, Object> otherClaims = token.getOtherClaims();

	   if (otherClaims.containsKey("IDP_NAME")) {
		String idpClaim = String.valueOf(otherClaims.get("IDP_NAME"));
		//out.println(yourClaim);
		roles.add(idpClaim);
	   }
	}
	
	chain.doFilter(new ConfigPrincipal(principalname, roles, req), response);
	
   }

   /**
    * Return the filter configuration object for this filter.
    *
    * @return
    */
   public FilterConfig getFilterConfig() {
	return (this.filterConfig);
   }

   /**
    * Set the filter configuration object for this filter.
    *
    * @param filterConfig The filter configuration object
    */
   public void setFilterConfig(FilterConfig filterConfig) {
	this.filterConfig = filterConfig;
   }

   /**
    * Destroy method for this filter
    */
   @Override
   public void destroy() {
   }

   /**
    * Init method for this filter
    */
   @Override
   public void init(FilterConfig filterConfig) {
	this.filterConfig = filterConfig;
	if (filterConfig != null) {
	   if (debug) {
		log("RoleFilter:Initializing filter");
	   }
	}
   }

   /**
    * Return a String representation of this object.
    */
   @Override
   public String toString() {
	if (filterConfig == null) {
	   return ("RoleFilter()");
	}
	StringBuffer sb = new StringBuffer("RoleFilter(");
	sb.append(filterConfig);
	sb.append(")");
	return (sb.toString());
   }

   private void sendProcessingError(Throwable t, ServletResponse response) {
	String stackTrace = getStackTrace(t);

	if (stackTrace != null && !stackTrace.equals("")) {
	   try {
		response.setContentType("text/html");
		PrintStream ps = new PrintStream(response.getOutputStream());
		PrintWriter pw = new PrintWriter(ps);
		pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

		// PENDING! Localize this for next official release
		pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
		pw.print(stackTrace);
		pw.print("</pre></body>\n</html>"); //NOI18N
		pw.close();
		ps.close();
		response.getOutputStream().close();
	   } catch (Exception ex) {
	   }
	} else {
	   try {
		PrintStream ps = new PrintStream(response.getOutputStream());
		t.printStackTrace(ps);
		ps.close();
		response.getOutputStream().close();
	   } catch (Exception ex) {
	   }
	}
   }

   public static String getStackTrace(Throwable t) {
	String stackTrace = null;
	try {
	   StringWriter sw = new StringWriter();
	   PrintWriter pw = new PrintWriter(sw);
	   t.printStackTrace(pw);
	   pw.close();
	   sw.close();
	   stackTrace = sw.getBuffer().toString();
	} catch (Exception ex) {
	}
	return stackTrace;
   }

   public void log(String msg) {
	filterConfig.getServletContext().log(msg);
   }

}
