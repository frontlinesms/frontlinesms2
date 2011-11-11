<html>
    <head>
        <meta name="layout" content="archive" />
        <title>${params.action.capitalize()}</title>
    </head>
    <body>
		<g:if test="${messageInstanceTotal > 0}">
			<g:render template="../message/message_details" />
		</g:if>
    </body>
</html>
