
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class DaemonUnzipSql {

	private static String path ;        

	
	public static void main(String[] args) {
		
		File file = new File( args[0] );                     //  Directory  'Work-Path'  A:\TTF\Mantis\0000
		if ( ! file.isDirectory() ) {
			LOG("Daemon:The first parameter must to be a working_directory. Program will exit." );
			System.exit(0);
		}
		else{
			path = args[0] ;
			LOG("DaemonUnzipSql(c)  Working directory setted: " + path + " Init Application...");
		}
		
		while (true) {                                              // core-loop
			try {
				File files[] = file.listFiles();                    // List of files     KKYT.20150701.01.4422.55333.LC.ZIP
				if (files.length > 0 ) {
					for (int i = 0; i < files.length; i++) {
						String name = files[i].getName();                                                    // file name
						String ext = name.substring( name.length()-4 , name.length() ) ;                     // file extension 
						
						if ( ext.toLowerCase().equals(".zip") && files[i].isFile() ) {
							LOG(" Zip file found. Starting the process!");
							execute( files[i].getPath() );                                                   // CALL deploy /path/file.zip
						}
					}
				}
				Thread.sleep(9000);
			} 
			catch (Exception e) {
				LOG( "Error at Daemon.main():" + e.getMessage() );
			}
		}//core-loop
		
	}
	
	
	
	
	
	
	public static void execute( String pathfilenamezip ) {
		try{
			LOG("\n execute " + pathfilenamezip);                          // the log needs the path
			
			String dirName = pathfilenamezip+".DIR" ;                      //  A:\TTF\Mantis\0000\KKYT.20150701.01.4422.55333.LC.ZIP.DIR
			
			if ( ! new File(dirName).mkdirs() )  { 
				LOG("There was a problem creating the new path: " + pathfilenamezip ); 
			}
			
			// UnZip file
			LOG   (" Unzipping file : "   +  pathfilenamezip );
			ArrayList<String> listOfUnzipped = unzip ( pathfilenamezip ,  dirName );

			// Delete zip
			if ( new File(pathfilenamezip).delete() ==false ) {
				LOG(" Problem deleting file:" + pathfilenamezip );
			}
			
			
			
			// CREATE SQL INSERT
			for (int x=0; x<listOfUnzipped.size(); x++){    // list of components/files

				String sql = "INSERT INTO TTF.MINING (id,mantis,project,DE1,component,path,description) VALUES (vid,'vmantis','vproject',vDE1,'vcomponent','vpath','vdescription')";
				
				String fields[] = pathfilenamezip.split("\\.");       // fields of zipfilename : KKYT.20150701.01.4422.55333.LC.ZIP.DIR
				
				sql = sql.replaceAll("vid"         , "1");
				sql = sql.replaceAll("vmantis"     , fields[3] );
				sql = sql.replaceAll("vproject"    , fields[5] );
				sql = sql.replaceAll("vDE1"        , "20151231" );
				sql = sql.replaceAll("vcomponent"  , listOfUnzipped.get(x) );
				sql = sql.replaceAll("vpath"       , fields[0] );
				sql = sql.replaceAll("vdescription","description" );
				
				LOG( "SQL:" + sql );
				
				if ( JDBC(sql) ) 
					LOG( "[JDBC] executed. MoreThanOneRegReturned" ); 
				else  
					LOG( "[JDBC] executed. NoRegReturned" );
				
			}
		}
		catch(Exception e){
			System.out.println( "Error at main method : " + e.getMessage() );
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static boolean copyListFiles ( String source , String target ) {
		try {
			File fsource = new File(source);
			File ftarget = new File(target);
			if ( fsource.isDirectory() && ftarget.isDirectory() ) {
				String arrFileName[] = fsource.list();
				for (int i = 0; i < arrFileName.length; i++) {
					copyFile ( source+arrFileName[i] , target+arrFileName[i] ) ;
				}
			}
			else{
				LOG ( "ERROR at copyListFiles : probabliy not directories " + source + "  " + target );
			}
			return false;
		} 
		catch (Exception e) {
			LOG("Error at copyListFiles" + e.getMessage() );
			return false;
		}
	}
	
	
	
	
	private static boolean copyFile( String source, String target ) {
		try{
			LOG(" copyFile:" + source + "  -->  " + target + "");
			
			File fsource = new File(source) ;
			File ftarget = new File(target) ;
			
			if ( fsource.exists() && ftarget.exists() ) {
				if ( fsource.lastModified() < ftarget.lastModified() ) {
					LOG(" >>>>>    [FILE COLISION] source: " + source + " " + fsource.lastModified() );
					LOG(" >>>>>    [FILE COLISION] target: " + target + " " + ftarget.lastModified() );
					fileWrite(target+"__COLISION__", "Colision with " + source ,false);
					return false;
				}
			}
			
			final InputStream  in  = new FileInputStream ( fsource );
			final OutputStream out = new FileOutputStream( ftarget );

			final byte[] buf = new byte[1024];
		    int len;
		    while ((len = in.read(buf)) > 0){
		    	out.write(buf, 0, len);
		    }
		    in.close();
		    out.close();
		    
		    ftarget.setLastModified(   fsource.lastModified()  );   // DATES
		    return true;
		}
		catch(final Exception e) {
			LOG( "Error at copyFile(): " + e.getMessage() );
			return false;
		}
	}
	
	
	
	
	private static void echo(String str){
		try{
			System.out.println(str);
		}
		catch(Exception e){
			System.out.println( "Error at echo method : " + e.getMessage() );
		}
	}
	
	
	
	
	
	private static void exec ( String str ) {
		try {
			//System.out.println("executing : " + str[0] +" "+ str[1] +" "+ str[2] );
			LOG(" execute:" + str);
			Runtime.getRuntime().exec(str);	
		} 
		catch (Exception e) {
			System.out.println("error at execute shell command:" + str + " - error:"+e.getMessage() );
			LOG("ERROR at execute:" + str);
		}
	}
	
	
	private static String getFormatedDate() {
		try {
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			return sdf.format( new Date() );                         
		} 
		catch (Exception e) {
			System.out.println( " error at getFormatedDate() " + e.getMessage() );
			return "20150000";
		}
	}
	
	
	private static void createDir(String path){
		try{
			if ( ! new File(path).isDirectory() ) { 
				LOG( " MakeDir:" + path ); 
				new File(path).mkdir();
			}
		}
		catch(final Exception e) {
			LOG(" Error at createDir(): " + path);
		}
	}
	
	private static void fileWrite(String pathfilename , String content, boolean append ) {
		try{
			File file = new File(pathfilename);
			FileOutputStream fos = new FileOutputStream(file , append);
			fos.write(content.getBytes());
			fos.close();
		}
		catch(final Exception e) {
			LOG(" Error at createFile(): " + pathfilename);
		}
	}
	

	
	
	
	
	private static void LOG( String content ) {
		try {
			String path_file_name = path + "/log.log";                                     // PATH FileName
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");        // Date
			String strDate = sdfDate.format( new Date() );                                 // Date
			File file = new File( path_file_name );                                        // File
			FileOutputStream fos = new FileOutputStream(file,true);                        // File append
			
			fos.write(  new String("[" + strDate + "] ").getBytes()  ); 
			fos.write(  content.getBytes()  );
			//fos.write(10);  fos.write(13);                                                 //NewLine
			fos.write( "\n".getBytes() );

			fos.flush();
			fos.close();
			System.out.println( content );
		}
		catch(Exception e){
			System.out.println( " error at LML-LOG " + e.getMessage() );
		}
	}
	
	
	/*
	 * unzip
	 * return ArryList extracted filenames
	 */
	private static ArrayList<String> unzip( String zipFile, String outputFolder ) {
		
		ArrayList<String> list = new ArrayList<String>(); //return
		
		byte[] buffer = new byte[1024];
		
		try{
			File folder = new File(outputFolder);
			if ( ! folder.exists() ) { folder.mkdir(); }
			
			ZipInputStream zis = new ZipInputStream( new FileInputStream(zipFile) );
			ZipEntry ze = zis.getNextEntry();
			
			while(ze!=null) {
				
				if (ze.isDirectory() ) {
					new File(ze.getName()).mkdir();
				}
				else {
					String fileName = ze.getName();                                                         // get file name zipped
					
					File newFile = new File( outputFolder + File.separator + fileName );                    // generate file
					
					LOG (" unzip file: "+ newFile.getName() + "    " +  newFile.getAbsoluteFile()  );
					list.add( newFile.getName() );                                                          //return
					
					new File( newFile.getParent() ).mkdirs();                                              // Make directories
			
					FileOutputStream fos = new FileOutputStream(newFile);                                  // write content           
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();                                                                           // close file
					
					newFile.setLastModified( ze.getTime() );                                               // last modification date
				}
				ze = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
			return list;
		}
		catch(Exception ex) {
			LOG ( "error at unzip: " + ex.getMessage() + "\n" );
			return null;
		}
	}    

	
	
	
	
	
	
	
	private static boolean JDBC( String sql ) {
		try {
			String JDBC_DRIVER = "org.apache.derby.jdbc.ClientDriver";  
			String DB_URL = "jdbc:derby://localhost:8081/database;create=true";
			Class.forName(JDBC_DRIVER);
			Connection conn = DriverManager.getConnection(DB_URL,"lml","arable");
			LOG("JDBC: connection created OK !");
			Statement stmt = conn.createStatement();
			if ( stmt.execute(sql) ) {
				return true;
			}else{
				return false;
			}
		}
		catch(Exception e){
			LOG( " error at JDBC " + e.getMessage() + " ; SQL:" + sql );
			return false;
		}
	}

	
	
	
	
}

/*
JAVA TOOLS BAT BASH LOG copyfile entry parameters hashmap
// PARAMETERS FROM BATCH -- name=value  name=value  etc
    HashMap<String, String> paramap = new HashMap<String, String>();
    for (int x=0; x<args.length; x++) {
        String param = args[x];
        String arr[] = param.split("=");
        paramap.put(arr[0], arr[1]);
        System.out.println( " [PARAMETERS] NAME="+arr[0] + " VALUE=" + paramap.get(arr[0]) );
    }
 */