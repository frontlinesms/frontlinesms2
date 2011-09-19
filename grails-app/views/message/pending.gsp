<!-<%@ page import="frontlinesms2.Contact" %>
<html>
	<head>
		<meta name="layout" content="messages"/>
		<title>Pending</title>
	</head>
	<body>
		<g:if test="${messageInstance != null}">
			<g:render template="../message/message_details" model="${[buttons: buttons, multiButtons: multiButtons]}"/>
		</g:if>
	</body>
</html>