package com.labolida.servlet;


import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;

/*
http://127.0.0.1/ttfdeployment/ServletSql?sql=select * from  TTF.MINING
*/
public class ServletSql  extends HttpServlet {
	
	
	public void doGet( HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
		String cachedResult[][] = search(request.getParameter("sql"));
		request.setAttribute("cachedResult", cachedResult);      
		request.getRequestDispatcher("sqlresult.jsp").include(request, response);
	}
	
	
	private String[][] search ( String sql ) {
		StringBuffer buff = new StringBuffer(); //output
		try{
			
			Class.forName("org.apache.derby.jdbc.ClientDriver");
			
			Connection conn = DriverManager.getConnection("jdbc:derby://localhost:8081/database;create=true","lml","arable");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			int MAX = 300 ;
			int row = 0;
			String cachedResult[][] = new String[MAX][19];  // ROW,COL
			
			while( rs.next() && row<MAX ) {
				
				for (int col=1; col<17; col++) {
					cachedResult[row][col] = rs.getString(col);
				}
				
				row++;
			}
			
			rs.close();
			stmt.close();
			conn.close();
			return cachedResult;
		}
		catch(Exception e) {
			System.out.println( " ERROR AT ServletSql \n SQL="+sql + "\n message="+e.getMessage() );
			e.printStackTrace();
			return null;
		}
		
	}

	
}
