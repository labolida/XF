package com.labolida.components;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class LMLJDBCOMM {
	
	public ArrayList execute ( String sql ) {
		
		ArrayList list = new ArrayList();
		try{
			Class.forName("org.apache.derby.jdbc.ClientDriver");
			Connection conn = DriverManager.getConnection("jdbc:derby://localhost:8081/database;create=true","lml","arable");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			HashMap reg = new HashMap();
			
			DatabaseMetaData  dbmeta = conn.getMetaData();
			ResultSetMetaData rsmeta =   rs.getMetaData();
			
			for (int c=1; c<rsmeta.getColumnCount(); c++ ) {
				String comumnname = rsmeta.getColumnName(c);
				reg.put(c, comumnname);
			}
			list.add(reg);
			
			while( rs.next()  ) {
				for (int c=1; c<rsmeta.getColumnCount(); c++ ) {
					reg.put(c,  rs.getString(c) );
				}
				list.add(reg);
			}
			rs.close();
			stmt.close();
			conn.close();
			return list;
		}
		catch(Exception e) {
			System.out.println( " ERROR AT ServletSql \n SQL="+sql + "\n message="+e.getMessage() );
			e.printStackTrace();
			return null;
		}
	}
}
/*
for (int x=0; x<list.size(); x++) {
	HashMap mapbean = (HashMap) list.get(x);
	mapbean.get(1);
	[OK] it works...!
}
*/
