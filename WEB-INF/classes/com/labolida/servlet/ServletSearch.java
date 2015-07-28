package com.labolida.servlet;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;




public class ServletSearch  extends HttpServlet {
	
	
	
	public void doGet( HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {

		
		String words = request.getParameter("words");  // REQUEST
		
		
		String report = searchAll(words);              // BUSINESS SEARCH
		
		request.setAttribute("report", report);        // RESPONSE
		request.getRequestDispatcher("report-present.jsp").include(request, response);
	}
	
	
	
	
	
	
	private String searchAll ( String words ) {
		
		words=words.toUpperCase();
		
		StringBuffer buff = new StringBuffer(); //output
		
		try {
			File f = new File("A:\\TTF\\Mantis\\");
			File fs[] = f.listFiles();
			
			for (int i=0; i<fs.length; i++) {
				String fullfilename = fs[i].getPath() + "/deploy.info";
				
				if (new File(fullfilename).exists()) {
					
					String content = new String( readFile( fullfilename ) );
					content = content.toUpperCase();
					
					boolean signal=false;
					String arrwords[] = words.split(" ");
					for (int w=0; w<arrwords.length; w++) {
						if ( content.contains( arrwords[w] ) ) {
							signal=true;
						}
					}
					
					if ( signal ) {
						content = content.replaceAll("<", "&nbsp;");
						content = content.replaceAll(">", "&nbsp;");
						content = content.replaceAll("\n", "<br>");
						content = content.replaceAll("DESARROLLO", "<B>DESARROLLO</B>");
						content = content.replaceAll("INTEGRADO", "<B>INTEGRADO</B>");
						content = content.replaceAll("PREPRODUCCION", "<B>PREPRODUCCION</B>");
						content = content.replaceAll("PRODUCCION", "<B>PRODUCCION</B>");
						buff.append( "<HR> " + fullfilename + "<HR>" );
						buff.append( content );
					}
					
				}
				
			}
			
		} 
		catch (Exception e) {
			System.out.println( e.getMessage() );
		}
		return new String(buff);
	}
	
	
	
	
	
	public byte[] readFile ( String pathname ) throws Exception {
		try {
			File f = new File(pathname);
			FileInputStream fis = new FileInputStream(f);
			byte buff[] = new byte[ (int)f.length() ];
			fis.read(buff);
			fis.close();
			return buff;
		}
		catch (Exception e) {
			throw new Exception( "Error at readFile: " + e.getMessage() );
		}
	}
	
	
	
	
	
}
