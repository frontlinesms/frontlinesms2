<!-<%@ page import="frontlinesms2.Contact" %>
<html>
<head>
	<meta name="layout" content="messages"/>
	<title>Pending</title>
</head>
<body>
<g:if test="${messageInstance != null}">
	<g:set var="buttons">
		<div id='other_btns'>
			<li class='static_btn'><g:link url="#" elementId="btn_forward">Forward</g:link></li>
			<li class='static_btn'><g:link elementId="message-delete" action="delete" params="[messageSection: messageSection, ownerId: ownerInstance?.id, messageId: messageInstance.id, archived: params.archived]">Delete</g:link></li>
			<g:if test="${!params['archived'] && messageSection != 'poll'}">
				<li class='static_btn'><g:link elementId="message-archive" action="archiveMessage" params="[messageSection: messageSection, ownerId: ownerInstance?.id, messageId: messageInstance.id]">Archive</g:link></li>
			</g:if>
		</div>
	</g:set>

	<g:render template="message_details" model="${[buttons: buttons]}"/>
</g:if>
</body>
</html>