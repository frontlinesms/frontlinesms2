<div id="tabs-3"  class="${configureTabs.contains('tabs-3') ? '' : 'hide'}">
	<label>Summary</label>
	<div class="confirm-header">Message:</div><span id="confirm-message-text">none</span>
	<div class="clear"></div>
	<g:if test="${recipients.size() == 1}">
		<div class="confirm-header"> Recipient: <span id="recipient">${recipientName}</span></div>
	</g:if>
	<g:else>
		<div class="confirm-header">Recipients:</div><span id="confirm-recipients-count"><span id="contacts-count">${recipients.size()}</span> contacts selected</span>
	</g:else>
</div>
