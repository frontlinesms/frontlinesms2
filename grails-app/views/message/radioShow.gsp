<html>
    <head>
        <meta name="layout" content="messages" />
        <title>Radio Show</title>
    </head>
    <body>
		<g:if test="${messageInstance != null}">
			<g:render template="message_details" />
		</g:if>
    </body>
</html>
