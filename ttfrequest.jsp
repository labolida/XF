<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

	<head>
		<title>SQL Result</title>
		<link rel="stylesheet" type="text/css" href="style.css">
		<!--
		http://127.0.0.1/XF/ttfrequest.jsp
		-->
	</head>

<body>

<h1>TTF Request</h1>

<form method="get" action="">
	<table>
		<tr>
			<td></td>
			<td></td>
		</tr>
		<tr>
			<td></td>
			<td><input type='submit' name='action' value='Submit'>
		</tr>
	</table>
	
</form>
v2
<table>
<%
	try{
		com.labolida.components.LMLJDBCOMM db = new com.labolida.components.LMLJDBCOMM();
		java.util.ArrayList list = (java.util.ArrayList) db.execute("select * from ttf_deploy.request");
		String oldvalue[] = new String[20];
		
		for (int x=0; x<list.size(); x++) {
			java.util.HashMap reg = (java.util.HashMap) list.get(x);
			out.print("<tr>");
			for (int fk=1; fk<=reg.size() ; fk++) {
				String value = (String) reg.get(fk) ; 
				if (oldvalue[fk]==null) 
					oldvalue[fk]="";
				if (!oldvalue[fk].equals(value) ) {
					out.print( "<td><b>" + value  + "</b></td>" );
					oldvalue[fk]=value;
				}else{
					out.print( "<td>" + value  + "</td>" );
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
