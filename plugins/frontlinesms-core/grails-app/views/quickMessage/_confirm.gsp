<div class="confirm">
	<table>
		<tr>
			<td><g:message code="quickmessage.recipient.label"/></td>
			<g:if test="${recipientCount == 1}">
				<td id="recipient">${recipientName}</td>
			</g:if>
			<g:else>
				<td id="confirm-recipients-count"><span id="contacts-count">${recipientCount}</span> <g:message code="quickmessage.recipients.count"/></td>
			</g:else>
		</tr>
		<tr>
			<td><g:message code="quickmessage.count.label"/></td>
			<td id="confirm-messages-count"><span id="messages-count"></span> <g:message code="quickmessage.messages.count"/></td>
		</tr>
	</table>
</div>

