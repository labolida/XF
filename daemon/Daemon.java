
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
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Daemon {

	private static String path ;        
	
	
	public static void main(String[] args) {
		System.out.println("Daemon version 2.4 init...");
		initConfig(args);
	}
	
	public static void initConfig(String[] args) {
		File file = new File( args[0] );                     //  Directory  'Work-Path'  A:\TTF\Mantis\0000
		if ( ! file.isDirectory() ) {
			LOG("Daemon.initConfig:ERROR:The first parameter must to be a working_directory. Program will exit." );
			System.exit(0);
		}
		else{
			path = args[0] ;
			LOG("Daemon.initConfig: Working directory setted: " + path + " [OK]");
			LOG("Daemon.initConfig: Call CORE.");
			JDBC(null);                                                                         // initialize: JDBC connection configuration.
			core(path);
		}
	}
	
	public static void core( String path ) {
		File file = new File( path ); 
		while (true) {                                              // core-loop
			try {
				File files[] = file.listFiles();                    // List of files     KKYT.20150701.01.4422.55333.LC.ZIP
				if (files.length > 0 ) {
					for (int i = 0; i < files.length; i++) {
						String name = files[i].getName();                                                    // file name
						String ext = name.substring( name.length()-4 , name.length() ) ;                     // file extension 
						if ( ext.toLowerCase().equals(".zip") && files[i].isFile() ) {
							LOG("Daemon.core: Zip file found. Starting the process!");
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
			LOG("Daemon.execute: pathfilenamezip:" + pathfilenamezip);     // the log needs the path
			
			String simpleFileName = new File(pathfilenamezip).getName();
			LOG("Daemon.execute: simpleFileName:" + simpleFileName); 
			
			String dirName = pathfilenamezip+".DIR" ;                      //  A:\TTF\Mantis\0000\KKYT.20150701.01.4422.55333.LC.ZIP.DIR
			
			
			// mk dir
			if ( ! new File(dirName).mkdirs() )  { 
				LOG("Daemon.execute: ERROR : There was a problem creating the new path: " + pathfilenamezip ); 
			}
			
			
			// UnZip file
			LOG   ("Daemon.execute: Unzipping file : "   +  pathfilenamezip );
			ArrayList<String> listOfUnzipped = unzip ( pathfilenamezip ,  dirName );

			
			// Delete zip
			if ( new File(pathfilenamezip).delete() ==false ) {
				LOG("Daemon.execute: ERROR : Problem deleting file:" + pathfilenamezip );
			}

			
			// Properties
			String propertyDirFileName = dirName+"/deploy.properties";
			propertiesLoad(propertyDirFileName);
			String value_path = propertiesGetProperty("path");
			String value_description = propertiesGetProperty("description");
			LOG("Daemon.execute: properties.file:" + propertyDirFileName );
			LOG("Daemon.execute: properties.value_path:" + value_path );
			LOG("Daemon.execute: properties.value_description:" + value_description );

			
			// CREATE SQL INSERT
			for (int x=0; x<listOfUnzipped.size(); x++){    // list of components/files
				//String sql = "INSERT INTO TTF.MINING (id,mantis,project,DE1,component,path,description) VALUES (vid,'vmantis','vproject',vDE1,'vcomponent','vpath','vdescription')";
				String sql1="INSERT INTO TTF.MINING_FILE (id, UUAA , mantis , project ,date_DE1, component , path , description ) ";
				String sql2=                     "VALUES (id,'UUAA','mantis','project',date_DE1,'component','path','description') ";
				String fields[] = simpleFileName.split("-");     // KKYT-20150701-01-4422-55334.zip
				sql2 = sql2.replaceAll("id"         , new Integer(autonumber).toString() ); autonumber++;
				sql2 = sql2.replaceAll("UUAA"       , fields[0]);
				sql2 = sql2.replaceAll("mantis"     , fields[3] );
				sql2 = sql2.replaceAll("project"    , "" );
				sql2 = sql2.replaceAll("date_DE1"   , getFormatedDate() );   //YYYYMMDD
				sql2 = sql2.replaceAll("component"  , listOfUnzipped.get(x) );
				sql2 = sql2.replaceAll("path"       , value_path );
				sql2 = sql2.replaceAll("description", value_description );
				String sql=sql1+sql2;
				LOG( "SQL:" + sql );
				JDBC(sql); 
			}
		}
		catch(Exception e){
			System.out.println( "Error at main method : " + e.getMessage() );
		}
	}
	
	
	
	/** >>>>>>>>>>>>>> <JDBC> <<<<<<<<<<<<<<<< **/
	
	private static Connection JDBC_CONNECTION = null;
	private static int autonumber = 1;
	
	private static boolean JDBC( String sql ) {
		try {
			LOG("Daemon.JDBC: Init.");
			String JDBC_DRIVER = "org.apache.derby.jdbc.ClientDriver";  
			String DB_URL = "jdbc:derby://localhost:8081/database;create=true";
			if ( JDBC_CONNECTION==null ) {
				LOG("Daemon.JDBC: There is NO Connection! Connecting...");
				Class.forName(JDBC_DRIVER);
				JDBC_CONNECTION = DriverManager.getConnection(DB_URL,"lml","arable");
				LOG("Daemon.JDBC: Connection created [OK] ");
			}else{
				LOG("Daemon.JDBC: Connection already created. ");
			}
			if (sql!=null){
				Statement stmt = JDBC_CONNECTION.createStatement();
				LOG("Daemon.JDBC: JDBC-Statement-Connection created [OK] ");
				if ( stmt.execute(sql) ) {
					LOG("Daemon.JDBC: Statement.executed [OK] MoreThanOneRegReturned. ");
				}else{
					LOG("Daemon.JDBC: Statement.executed [OK] NoRegReturned. ");
				}
			}
			return true;
		}
		catch(Exception e){
			LOG( "Daemon.JDBC: ERROR : " + e.getMessage() + " ;\n  SQL:" + sql );
			e.printStackTrace();
			System.exit(-1);
			return false;
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
	private static String getFormatedDateTime() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			
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
			System.out.println( content );
			
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
			
		}
		catch(Exception e){
			System.out.println( " error at LML-LOG " + e.getMessage() );
		}
	}
	
	
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

	
	
	

	/**
		Properties:
	**/

	private static Properties prop = new Properties();
	
	public static void propertiesLoad ( String filename ) {
		try {
			File file = new File( filename );
			InputStream is = (InputStream) new FileInputStream(file);
			prop.load( is );
		} 
		catch (Exception e) {
			e.printStackTrace();
			System.out.println( "Error at FileProperties.load():" + e.getMessage() );
		}
	}

	public static String propertiesGetProperty ( String name ) {
		try {
			return prop.getProperty(name);
		} 
		catch (Exception e) {
			e.printStackTrace();
			System.out.println( e.getMessage() );
			return null;
		}
	}

	/*
	public static void propertiesSetProperty ( String name , String value ) {
		try {
			prop.setProperty( name , value );
		} 
		catch (Exception e) {
			e.printStackTrace();
			System.out.println( e.getMessage() );
		}
	}
	public static void propertiesSave () {
		try {
			File file = new File(   "/JFile.properties");
			OutputStream os = (OutputStream)  new FileOutputStream  ( file );
			prop.store( os , null );
		} 
		catch (Exception e) {
			e.printStackTrace();
			System.out.println( "Error at FileProperties.save():" + e.getMessage() );
		}
	}
	*/
	
	
	
	
	/**
	
	public static String FF_01 x &00F

	**/
	
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