<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings" />
		<title>Settings > Connections > Simple GSP page</title>
	</head>
	<body>
		<ul id="connectionTypes">
			<g:link action="createSmslib" class="smslib button">Phone/Modem</g:link>
			<g:link action="createEmail" class="email button">Email</g:link>
		</ul>
	</body>
</html>