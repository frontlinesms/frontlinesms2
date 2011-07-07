<html>
    <head>
        <meta name="layout" content="messages" />
        <title>Folder</title>
    </head>
    <body>
		<g:if test="${messageInstance != null}">
			<g:set var="buttons">
				<g:remoteLink controller="quickMessage" action="create" params="[recipient: messageInstance.src]" onSuccess="launchWizard('folder-reply', data);" class="quick_message">
					Reply
				</g:remoteLink>
				<g:link disabled="true" action="deleteMessage" params="[messageSection: messageSection, ownerId: ownerInstance.id, messageId: messageInstance.id]">Delete</g:link>
			</g:set>
			<g:render template="message_details" model="${[buttons: buttons]}"/>
		</g:if>
    </body>
</html>
