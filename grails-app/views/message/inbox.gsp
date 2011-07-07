<%@ page import="frontlinesms2.Contact" %>
<html>
    <head>
        <meta name="layout" content="messages" />
        <title>Inbox</title>
    </head>
    <body>
		<g:if test="${messageInstance != null}">
			<g:set var="buttons">
				<g:remoteLink controller="quickMessage" action="create" params="[recipient: messageInstance.src]" onSuccess="launchWizard('Reply', data);" class="quick_message">
					Reply
				</g:remoteLink>
				<g:link action="deleteMessage" params="[messageSection: messageSection, messageId: messageInstance.id]">Delete</g:link>
			</g:set>
			<g:render template="message_details" model="${[buttons: buttons]}"/>
		</g:if>
    </body>
</html>