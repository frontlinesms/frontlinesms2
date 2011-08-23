<html>
    <head>
        <meta name="layout" content="messages" />
        <title>${pageTitle?:params.action.capitalize()}</title>
    </head>
    <body>
		<g:if test="${messageInstance != null}">
			<g:render template="../message/message_details" />
		</g:if>
    </body>
</html>
