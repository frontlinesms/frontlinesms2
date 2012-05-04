<div id="tabs-3"  class="confirm ${configureTabs.contains('tabs-3') ? '' : 'hide'}">
	<h2><g:message code="quickmessage.details.label"/></h2>
	<table>
		<tr>
			<td class="bold">
				<g:message code="quickmessage.message.label"/>
			</td>
			<td id="confirm-message-text">
				<g:message code="quickmessage.message.none"/>
			</td>
		</tr>
		<tr>
			<g:if test="${recipients.size() == 1}">
				<td class="bold"><g:message code="quickmessage.recipient.label"/></td><td id="recipient">${recipientName}</td>
			</g:if>
			<g:else>
				<td class="bold"><g:message code="quickmessage.recipients.label"/></td>
				<td id="confirm-recipients-count"><span id="contacts-count">${recipients.size()}</span> <g:message code="quickmessage.recipients.count"/></td>
				<td id="confirm-messages-count">(<span id="messages-count"></span> <g:message code="quickmessage.messages.count"/>)</td>
			</g:else>
		</tr>
	</table>
</div>