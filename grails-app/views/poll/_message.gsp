<div id="tabs-5">
	<h3>Edit message to be sent to recipients</h3>
	<p>The following message will be sent to the recipients of the poll. This message can be edited before sending.</p>
	<g:textArea name="messageText" rows="5" cols="40" />
	<span id="message-stats">0 characters (1 SMS message)</span>
</div>
<g:javascript>
	$("#messageText").live("keyup", updateCount);
</g:javascript>