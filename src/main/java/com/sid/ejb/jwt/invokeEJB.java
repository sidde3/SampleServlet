/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sid.ejb.jwt;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.omg.CosNotification.StartTime;
import org.wildfly.security.auth.client.AuthenticationConfiguration;
import org.wildfly.security.auth.client.AuthenticationContext;
import org.wildfly.security.auth.client.MatchRule;
import org.wildfly.security.credential.BearerTokenCredential;
import org.wildfly.security.sasl.SaslMechanismSelector;

/**
 *
 * @author sidde
 */
public class invokeEJB extends HttpServlet {

   /**
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
    * methods.
    *
    * @param request servlet request
    * @param response servlet response
    * @throws ServletException if a servlet-specific error occurs
    * @throws IOException if an I/O error occurs
    */
   protected void processRequest(HttpServletRequest request, HttpServletResponse response)
	     throws ServletException, IOException {
	HttpSession session = (HttpSession) request.getSession();
	final Principal userPrincipal = request.getUserPrincipal();
	if (userPrincipal instanceof KeycloakPrincipal) {
	   KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) userPrincipal;
	   String AcToken = kp.getKeycloakSecurityContext().getTokenString();

	   System.out.println(AcToken);

	   AuthenticationConfiguration common = AuthenticationConfiguration.empty().setSaslMechanismSelector(SaslMechanismSelector.NONE.addMechanism("OAUTHBEARER"));
	   AuthenticationConfiguration tuser = common.useBearerTokenCredential(new BearerTokenCredential(AcToken));
	   final AuthenticationContext authCtx = AuthenticationContext.empty().with(MatchRule.ALL, tuser);
	   AuthenticationContext.getContextManager().setThreadDefault(authCtx);
		try {
		   invokeIntermediateBean();
		} catch (Exception ex) {
		   Logger.getLogger(invokeEJB.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
   }

   private void invokeIntermediateBean() throws Exception {
	final Hashtable<String, String> jndiProperties = new Hashtable<>();
	jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
	jndiProperties.put(Context.PROVIDER_URL, "remote+http://localhost:18080");
	final Context context = new InitialContext(jndiProperties);

	BearTokenInterface intermediate = (BearTokenInterface) context.lookup("ejb:/ejb-jwt/BearTokenEjb!"
		  + BearTokenInterface.class.getName());
	System.out.println("\n\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n");
	System.out.println(intermediate.shareToken());
	System.out.println("\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n\n");
   }

   // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
   /**
    * Handles the HTTP <code>GET</code> method.
    *
    * @param request servlet request
    * @param response servlet response
    * @throws ServletException if a servlet-specific error occurs
    * @throws IOException if an I/O error occurs
    */
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
	     throws ServletException, IOException {
	processRequest(request, response);
   }

   /**
    * Handles the HTTP <code>POST</code> method.
    *
    * @param request servlet request
    * @param response servlet response
    * @throws ServletException if a servlet-specific error occurs
    * @throws IOException if an I/O error occurs
    */
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
	     throws ServletException, IOException {
	processRequest(request, response);
   }

   /**
    * Returns a short description of the servlet.
    *
    * @return a String containing servlet description
    */
   @Override
   public String getServletInfo() {
	return "Short description";
   }// </editor-fold>

}
