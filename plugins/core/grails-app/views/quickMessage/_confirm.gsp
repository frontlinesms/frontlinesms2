<div id="tabs-3"  class="confirm ${configureTabs.contains('tabs-3') ? '' : 'hide'}">
	<h2>Confirm details</h2>
	<table>
		<tr>
			<td class="bold">Message:</td><td id="confirm-message-text">none</td>
		</tr>
		<tr>
			<g:if test="${recipients.size() == 1}">
				<td class="bold"> Recipient:</td><td id="recipient">${recipientName}</td>
			</g:if>
			<g:else>
				<td class="bold">Recipients:</td>
				<td id="confirm-recipients-count"><span id="contacts-count">${recipients.size()}</span> contacts selected</td>
				<td id="confirm-messages-count">(<span id="messages-count"></span> messages will be sent)</td>
			</g:else>
		</tr>
	</table>
</div>