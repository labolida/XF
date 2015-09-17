package com.labolida.leonardo.dimensionsql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class DimensionSql {

	// PRODUCT,ID,TITLE,STATUS,STAGE_ID,ESTADO_PROMOCION,PHASE,FECHA_IMPLANTACION,FULLPATH,FILE,UPDATE_DATE,REVISION,LENGTH
	
	public static void main( String[] args ) {
		try {
			
			StringBuffer buff = new StringBuffer();
			buff.append("  \n\n\n DELETE FROM ttf_deploy.dimensions   \n\n\n ");
			
			
			byte[] bcontent = fileRead("DimensionsReport.csv");
			String content = new String(bcontent);
			
			String lines[] = content.split("\n");
			
			for (int i= 0; i<lines.length; i++) {
				buff.append("INSERT INTO ttf_deploy.dimensions (PRODUCT,ID,TITLE,STATUS,STAGE_ID,ESTADO_PROMOCION,PHASE,FECHA_IMPLANTACION,FULLPATH,FILE,UPDATE_DATE,REVISION,LENGTH) VALUES (");
				
				String fieldValues[] = lines[i].split(";");
				
				for (int v= 0; v<fieldValues.length; v++) {
					
					String fieldvalue = fieldValues[v].replaceAll("\r", "");
					
					if (v<fieldValues.length-1 )
						buff.append( "'" + fieldvalue + "',");
					else
						buff.append( "'" + fieldvalue + "') \n ; \n");
				}
			}
			
			buff.append("  \n\n\n SELECT * FROM ttf_deploy.dimensions   \n\n\n");
			
			fileWrite("DimensionsReport.sql",  new String(buff).getBytes()  , false );
			
		}
		catch (Exception e) {
			System.out.println( "Error at DimensionSql.main():"+ e.getMessage() );
		}

	}

	
	private static byte[] fileRead(String FilePathName) throws Exception {
		try{
			File file = new File(FilePathName);
			int size = (int)file.length();
			byte [] content = new byte[size];
			FileInputStream fis = new FileInputStream(file);
			size = fis.read( content );
			fis.close();
			return content;
		}
		catch(Exception e) {
			//throw new Exception("Error at fileRead():" + e.getMessage() );
			System.out.println( "Error at fileRead():" + e.getMessage() );
			return null;
		}
	}
	
	
	private static void fileWrite(String fileName, byte[] body , boolean appender ) throws Exception {
        try {
            FileOutputStream fos = new FileOutputStream(fileName,appender);
            PrintStream stream = new PrintStream(fos);
            stream.write(body);
            stream.close();
        }
        catch(Exception e) {
            //throw new Exception("Error at fileWrite():" + e.getMessage() );
        	System.out.println( "Error at fileWrite():" + e.getMessage() );
        }
    }
	
	
}



