<div id="tabs-4" class="poll-response-reply">
	<div class="error-panel hide"></div>
	<h3>
		Reply automatically to poll responses (optional)
	</h3>
	<div>
		When an incoming message is identified as a poll response, send a
		message to the person who sent the response.
	</div>
	<g:checkBox name="auto-reply" id="send_auto_reply" />
	<label for='auto-reply'>Send an automatic reply to poll responses</label>
	<g:textArea name="autoReplyText" rows="5" cols="40" disabled='disabled'></g:textArea>
</div>

<g:javascript>
	$("#send_auto_reply").live("change", function() {
		if(isGroupChecked('auto-reply')) {
			$("#autoReplyText").removeAttr("disabled")
		}
		else {
		    $("#autoReplyText").val("")
			$("#autoReplyText").attr('disabled','disabled');
		}
	})
</g:javascript>
