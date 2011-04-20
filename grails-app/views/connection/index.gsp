<%--
  Created by IntelliJ IDEA.
  User: david
  Date: 4/19/11
  Time: 6:58 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head><meta name="layout" content="connection" /></head>
	<body>
		<ol id='connections'>
			<li>'MTN Dongle' (Phone/Modem)</li>
			<li>'David's Clickatell account' (Clickatell SMS Gateway)</li>
			<li>'Miriam's Clickatell account' (Clickatell SMS Gateway)</li>
		</ol>
		<div id='btnNewConnection'>
			<g:link action='create'>Add new connection</g:link>
		</div>
	</body>
</html>