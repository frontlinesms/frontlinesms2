<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings" />
		<title><g:message code="connection.header" /> ${connectionInstance?.name}</title>
	</head>
	<body>
		<g:render template="/connection/connection_list"/>
	</body>
</html>
