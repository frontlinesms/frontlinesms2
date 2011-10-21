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
	<span id="message-stats" class="hide">0 characters (1 SMS message)</span> 
</div>

<g:javascript>
	$("#enableAutoReply").live("change", function() {
		// FIXME remove lookup of 'auto-reply' "group" - it's just 'this', but instead gets searched for 3 times inside this function
		if(isGroupChecked('enableAutoReply')) {
			$("#autoReplyText").removeAttr("disabled");
			$("#message-stats").removeClass("hide");
		} else {
			$("#autoReplyText").attr('disabled','disabled');
			$("#message-stats").addClass("hide");
		}
	})
	
	$("#autoReplyText").live("keyup", function() {
		var value = $(this).val();
		if(value.length > 3000) {
			//prevent addition of new content to message
			$(this).val(value.substring(0, 3000));
			
		} else if(value.length > 140) {
			$.get(url_root + 'message/getSendMessageCount', {message: value}, function(data) {
				$("#message-stats").html(value.length + " characters " + data);
			});
		}
		else {
			$("#message-stats").html(value.length + " characters (1 SMS message)");
		}
	})
</g:javascript>
