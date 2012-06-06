<div class="confirm">
	<h2><g:message code="quickmessage.details.label"/></h2>
	<table>
		<tr>
			<td>
				<g:message code="quickmessage.message.label"/>
			</td>
			<td id="confirm-message-text">
				<g:message code="quickmessage.message.none"/>
			</td>
		</tr>
		<tr>
			<td><g:message code="quickmessage.recipient.label"/></td>
			<g:if test="${recipients.size() == 1}">
				<td id="recipient">${recipientName}</td>
			</g:if>
			<g:else>
				<td id="confirm-recipients-count"><span id="contacts-count">${recipients.size()}</span> <g:message code="quickmessage.recipients.count"/></td>
			</g:else>
		</tr>
		<tr>
			<td><g:message code="quickmessage.count.label"/></td>
			<td id="confirm-messages-count"><span id="messages-count"></span> <g:message code="quickmessage.messages.count"/></td>
		</tr>
	</table>
</div>

