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
			<td> <TEXTAREA rows="4" cols="50" name="sql">select * from ttf_deploy.request</TEXTAREA> </td>
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
			java.util.ArrayList list = (java.util.ArrayList) request.getAttribute("list");
			
			for (int x=0; x<list.size(); x++) {
				java.util.HashMap mapbean = (java.util.HashMap) list.get(x);
				//mapbean.get(1);
				out.print("<tr>");
				for (int fk=1; fk<=9 ; fk++) {
					out.print( "<td>" + mapbean.get(fk)  + "</td>" );
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
