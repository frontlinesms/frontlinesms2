<div id="tabs-3"  class="${configureTabs.contains('tabs-3') ? '' : 'hide'}">
	<div class="confirm-header">
		<label>Confirm details:</label>
		<div class="message">
			<div class="title">Message:</div>
			<div id="confirm-message-text">none</div>
		</div>
		
		<div class="clear"></div>
		<g:if test="${recipients.size() == 1}">
			<div class="title"> Recipient: <span id="recipient">${recipientName}</span></div>
		</g:if>
		<g:else>
			<div class="title">Recipients:</div>
			<span id="confirm-recipients-count"><span id="contacts-count">${recipients.size()}</span> contacts selected</span>
			<span id="confirm-messages-count">(<span id="messages-count"></span>messages will be sent)</span>
		</g:else>
	</div>
</div>