package com.labolida.actions;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * LMLServletAction MVC-Framework
 * Used by LMLServletController 
 */

public class LMLActionController {

	
	public String execute( HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
		
		String html = null;
		String action = request.getParameter("action");
		
		if (action==null) { action="default"; }
		
		if (action.equals("default")) { html = new ActionDefault().execute(request, response); }
		if (action.equals("report"))  { html = new ActionReport().execute(request, response); }
		if (action.equals("default")) { html = new ActionDefault().execute(request, response); }
		if (action.equals("default")) { html = new ActionDefault().execute(request, response); }
		if (action.equals("default")) { html = new ActionDefault().execute(request, response); }
		
		if (html==null) { html = "<html><h1>404 - action not found </h1></html>"; }
		
		return html;
		
	}
	
	
}
