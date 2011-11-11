<html>
    <head>
        <meta name="layout" content="messages" />
        <title>${pageTitle?:params.action.capitalize()}</title>
        <g:javascript>
			function isArchived() {
				return ${params.viewingArchive}
			}
		</g:javascript>
    </head>
    <body>
		<g:if test="${messageInstanceTotal > 0}">
			<g:render template="../message/message_details" />
		</g:if>
    </body>
</html>
