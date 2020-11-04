/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sid.redhat;

import java.security.Principal;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 * @author sidde
 */
public class ConfigPrincipal extends HttpServletRequestWrapper {

   String user;
   List<String> roles;
   HttpServletRequest realRequest;

   public ConfigPrincipal(String user, List<String> roles, HttpServletRequest request) {
	super(request);
	this.user = user;
	this.roles = roles;
	this.realRequest = request;
   }

   @Override
   public boolean isUserInRole(String role) {
	if (roles == null) {
	   return this.realRequest.isUserInRole(role);
	}
	return roles.contains(role);
   }

   @Override
   public Principal getUserPrincipal() {
	if (this.user == null) {
	   return realRequest.getUserPrincipal();
	}

	// make an anonymous implementation to just return our user
	return new Principal() {
	   @Override
	   public String getName() {
		return user;
	   }
	};
   }
   
   public boolean authenticate(){
	return true;
   }
}
