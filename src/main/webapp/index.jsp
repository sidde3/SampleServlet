<%-- 
    Document   : index
    Created on : Sep 14, 2018, 4:05:56 PM
    Author     : sidde
--%>


<%@page import="java.util.Map"%>
<%@page import="org.keycloak.representations.IDToken"%>
<%@page import="org.keycloak.KeycloakSecurityContext"%>
<%@page import="java.security.Principal"%>
<%@page import="org.keycloak.KeycloakPrincipal" %>
<%@page import="org.keycloak.adapters.saml.SamlPrincipal" %>

<%@page import="org.wildfly.security.auth.principal.NamePrincipal" %>
<%@page import="org.wildfly.security.authz.Attributes" %>
<%@page import="org.wildfly.security.auth.server.RealmIdentity"%>
<%@page import="org.wildfly.security.auth.server.SecurityIdentity"%>
<%@page import="org.wildfly.security.auth.server.SecurityDomain"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
   <head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Welcome</title>
   </head>

   Welcome, 

   <%
	final Principal userPrincipal = request.getUserPrincipal();
	
	if (userPrincipal instanceof KeycloakPrincipal) {
	   
	   KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) userPrincipal;
	   IDToken token = kp.getKeycloakSecurityContext().getIdToken();
	   out.println(token.getGivenName()+"<p>");
	   Map<String, Object> otherClaims = token.getOtherClaims();

	   if (otherClaims.containsKey("Groups")) {
		String yourClaim = String.valueOf(otherClaims.get("Groups"));
		out.println("</p>Groups:"+yourClaim);
		out.println("<p>Roles:"+kp.getKeycloakSecurityContext().getToken().getRealmAccess().getRoles()+"</p>");
	   }
	}

//	   String atoken = controller.getTokenString(request);
//	   out.println(atoken);

//	   Subject subject = (Subject) PolicyContext.getContext("javax.security.auth.Subject.container");
//	   Subject subject = (Subject) PolicyContext.getContext(SecurityConstants.SUBJECT_CONTEXT_KEY);
//	   out.println(subject.getPrincipals());
//	   out.println(request.isUserInRole("broker-realm"));

	if(userPrincipal instanceof NamePrincipal){
  
	  SecurityIdentity testID = SecurityDomain.getCurrent().getCurrentSecurityIdentity();
	  RealmIdentity id = SecurityDomain.getCurrent().getIdentity(testID.getPrincipal());
	  //out.println(testID.getAttributes().get("Roles"));
	  Attributes map = id.getAuthorizationIdentity().getAttributes();
	  out.println(request.getUserPrincipal());
	  out.println("<p>Groups: "+map.get("Groups"));
	  out.println("</p>Roles: "+map.get("Roles"));
	}
	
	if(userPrincipal instanceof SamlPrincipal){
	 SamlPrincipal samlPrincipal = (SamlPrincipal) userPrincipal;
	 out.println(samlPrincipal.getSamlSubject());
	}
   %>
   <a href="?GLO=true">logout</a>
</html>
