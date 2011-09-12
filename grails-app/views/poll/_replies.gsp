<div id="tabs-4" class="poll-response-reply">
	<h3>
		Reply automatically to poll responses (optional)
	</h3>
	<div>
		When an incoming message is identified as a poll response, send a
		message to the person who sent the response.
	</div>
	<g:checkBox name="enableAutoReply" />Send an automatic reply to poll responses
	<g:textArea name="autoReplyText" rows="5" cols="40" disabled='disabled'/>
</div>

<g:javascript>
	// FIXME change this to a checked/unchecked listener and remove the lookup of 'auto-reply' "group"
	$("#enableAutoReply").live("change", function() {
		if(isGroupChecked('enableAutoReply')) {
			$("#autoReplyText").removeAttr("disabled");
		} else {
			$("#autoReplyText").attr('disabled','disabled');
		}
	})
</g:javascript>
