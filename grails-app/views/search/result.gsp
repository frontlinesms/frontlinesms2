<%@ page import="frontlinesms2.Contact" %>
<html>
    <head>
        <meta name="layout" content="search" />
        <title>Results</title>
    </head>
    <body>
		<g:if test="${messageInstance != null}">
			<g:render template="../message/message_details" />
		</g:if>
    </body>
</html>