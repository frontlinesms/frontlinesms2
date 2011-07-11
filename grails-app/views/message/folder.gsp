<html>
    <head>
        <meta name="layout" content="messages" />
        <title>Folder</title>
    </head>
    <body>
		<g:if test="${messageInstance != null}">
			<g:set var="buttons">
				<g:remoteLink controller="quickMessage" action="create" params="[recipient: messageInstance.src]" onSuccess="launchWizard('Reply' ,data);" class="quick_message">
					Reply
				</g:remoteLink>
				<g:remoteLink controller="quickMessage" action="create" params="[messageText: messageInstance.text]" onSuccess="launchWizard('Forward', data);" class="quick_message">
					Forward
				</g:remoteLink>
				<g:link disabled="true" action="deleteMessage" params="[messageSection: messageSection, ownerId: ownerInstance.id, messageId: messageInstance.id]">Delete</g:link>
			</g:set>
			<g:render template="message_details" model="${[buttons: buttons]}"/>
		</g:if>
    </body>
</html>
