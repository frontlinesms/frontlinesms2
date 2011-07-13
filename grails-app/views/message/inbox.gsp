<%@ page import="frontlinesms2.Contact" %>
<html>
    <head>
        <meta name="layout" content="messages" />
        <title>Inbox</title>
    </head>
    <body>
		<g:if test="${messageInstance != null}">
			<g:render template="message_details" />
		</g:if>
    </body>
</html>