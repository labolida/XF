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
			
			DatabaseMetaData  dbmeta = conn.getMetaData();
			ResultSetMetaData rsmeta =   rs.getMetaData();
			
			HashMap reg0 = new HashMap();
			for (int c=1; c<=rsmeta.getColumnCount(); c++ ) {
				String comumnname = rsmeta.getColumnName(c);
				reg0.put(c, comumnname);
			}
			list.add(reg0);
			
			while( rs.next()  ) {
				HashMap reg = new HashMap();
				for (int c=1; c<=rsmeta.getColumnCount(); c++ ) {
					reg.put(c,  rs.getString(c) );
				}
				list.add(reg);
			}
			
			
			System.out.println("Test project XF::LMLJDBCOMM.execute()");
			for (int x=0; x<list.size(); x++) {
				System.out.println( "-" );
				HashMap reg2 = (java.util.HashMap) list.get(x);
				for (int fk=1; fk<=reg2.size() ; fk++) {
					System.out.println( "reg:" + reg2.get(fk) );
				}
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
