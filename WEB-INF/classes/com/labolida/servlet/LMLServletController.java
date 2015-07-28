package com.labolida.servlet;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.labolida.actions.LMLActionController;

/**
    Tomcat 7.0 - J2EE Servlet 
	JDK: 1.6
	--
	Name: javax/servlet/
	Specification-Title: Java API for Servlets
	Specification-Version: 3.0
	Specification-Vendor: Sun Microsystems, Inc.
	Implementation-Title: javax.servlet
	Implementation-Version: 3.0.FR
	Implementation-Vendor: Apache Software Foundation
*/

public class LMLServletController extends HttpServlet {

	/*
	 *   http://127.0.0.1/LMLJ2EEWeb/controller.server
	 *   http://127.0.0.1/LMLJ2EEWeb/controller.server?action=default
	 *   http://127.0.0.1/LMLJ2EEWeb/controller.server?action=report
	 */
	private String message;

	
	public void init() throws ServletException {
		message = "Hello World";
	}

	
	
	public void doGet( HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		String val1 = request.getSession().getServletContext().getInitParameter("context-parameter-001");
		String val2 = getServletContext().getInitParameter("context-parameter-001");
		
		//out.println("<h2>" + message + val1 + "</h2>");
		//out.println("<h2>" + message + val2 + "</h2>");
		
		String html = new LMLActionController().execute(request, response);
		out.println(html);
	}

}


