package com.labolida.actions;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ActionReport {
	
	public String execute( HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
		
		return "<html>welcome to the report!</html>";
		
	}
	
}
