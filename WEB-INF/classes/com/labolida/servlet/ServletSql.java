package com.labolida.servlet;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/*
http://127.0.0.1/ttfdeployment/ServletSql?sql=select * from  TTF.MINING
*/
public class ServletSql  extends HttpServlet {
	
	
	public void doGet( HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
		ArrayList list = search(request.getParameter("sql"));
		request.setAttribute("list", list);      
		request.getRequestDispatcher("sqlresult.jsp").include(request, response);
	}
	
	
	
	
	private ArrayList search ( String sql ) {
		ArrayList list = new ArrayList();
		try{
			Class.forName("org.apache.derby.jdbc.ClientDriver");
			
			Connection conn = DriverManager.getConnection("jdbc:derby://localhost:8081/database;create=true","lml","arable");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			while( rs.next()  ) {
				HashMap mapBean = new HashMap();
				String bean[] = new String[10];     // 10 fields	
				for (int col=1; col<10; col++) {
					mapBean.put(col,  rs.getString(col) );
				}
				list.add(mapBean);
			}
			
			rs.close();
			stmt.close();
			conn.close();
			
			//@test
			/*
			for (int x=0; x<list.size(); x++) {
				HashMap mapbean = (HashMap) list.get(x);
				mapbean.get(1);
				[OK] it works...!
			}
			*/
			
			return list;
		}
		catch(Exception e) {
			System.out.println( " ERROR AT ServletSql \n SQL="+sql + "\n message="+e.getMessage() );
			e.printStackTrace();
			return null;
		}
		
	}

	
}
