<div id="tabs-3" class="poll-response-reply">
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

<script>
	$("#send_auto_reply").live("change", function(){
		if(isGroupChecked('auto-reply')) {
			$("#autoReplyText").removeAttr("disabled")
		}
		else {
		    $("#autoReplyText").val("")
			$("#autoReplyText").attr('disabled','disabled');
		}
	})
</script>
