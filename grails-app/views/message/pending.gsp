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
			<g:if test="${failedMessageIds.contains(messageInstance.id)}">
				<li class='static_btn'>
					<g:link elementId="retry" action="send" params="${[failedMessageIds: [messageInstance.id]]}">Retry</g:link>
				</li>
			</g:if>
			<g:render template="message_button_renderer" model="${[value:'Delete',id:'btn_delete',action:'delete']}"></g:render>
		</div>
	</g:set>

	<g:set var="multiButtons">
		<div id='other_btns'>
			<li class='static_btn'>
				<g:if test="${checkedMessageList.tokenize(',').intersect(failedMessageIds*.toString())}">
					<g:link elementId="retry-failed" action="send" params="${[failedMessageIds : checkedMessageList.tokenize(',').intersect(failedMessageIds*.toString())]}">Retry failed</g:link>
				</g:if>
			</li>
			<g:render template="message_button_renderer" model="${[value:'Delete All',id:'btn_delete_all',action:'deleteAll']}"></g:render>
		</div>
	</g:set>

	<g:render template="message_details" model="${[buttons: buttons, multiButtons: multiButtons]}"/>
</g:if>
</body>
</html>