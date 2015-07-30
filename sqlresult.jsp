<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

	<head>
		<title>SQL Result</title>
		<link rel="stylesheet" type="text/css" href="style.css">
	</head>

<body>

<h1>SQL J2EE OnLine Query (Scriptlet)</h1>

<form method="get" action="ServletSql">
	<table>
		<tr>
			<td>sql</td>
			<td> <TEXTAREA rows="4" cols="50" name="sql">select * from  TTF.MINING_FILE</TEXTAREA> </td>
		</tr>
		<tr>
			<td></td>
			<td><input type='submit' name='action' value='Submit'>
		</tr>
	</table>
	
</form>



<table>
<%
		try{
			String cachedResult[][] = (String[][]) request.getAttribute("cachedResult");
			for (int row=0; row<=cachedResult.length-1 ; row++) {
				out.print("<tr>");
				for (int col=0; col<=18 ; col++) {
					if ( cachedResult[row][col]==null ) {
						out.print( "<td>.</td>" );
					}else{
						out.print( "<td>" + cachedResult[row][col]  + "</td>" );
					}
				}
				out.print("</tr>");
			}
		}
		catch(Exception e) {
			out.print("<br>ERROR: " + e.getMessage() + "\n\n");
			e.printStackTrace();
		}

%>
</table>



</body>
</html>
