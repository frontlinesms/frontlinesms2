<!-<%@ page import="frontlinesms2.Contact" %>
<html>
    <head>
        <meta name="layout" content="messages" />
        <title>Pending</title>
    </head>
    <body>
		<g:if test="${messageInstance != null}">
			<g:set var="buttons">
				<g:link action="deleteMessage" params="[messageSection: messageSection, messageId: messageInstance.id]">Delete</g:link>
			</g:set>
			<g:render template="message_details" model="${[buttons: buttons]}"/>
		</g:if>
    </body>
</html>