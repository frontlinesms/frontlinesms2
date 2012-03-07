<div id="tabs-4" class="poll-response-reply">
	<h2 class="bold">Reply automatically to poll responses (optional)</h2>
	<p class="info">When an incoming message is identified as a poll response, send a
		message to the person who sent the response.
	</p>
	<g:checkBox name="enableAutoReply" checked="${activityInstanceToEdit?.autoReplyText as boolean}"/>Send an automatic reply to poll responses
	<g:if test="${activityInstanceToEdit?.autoReplyText as boolean}">
		<g:textArea name="autoReplyText" rows="5" cols="40" value="${activityInstanceToEdit?.autoReplyText ?:''}"/>
	</g:if>
	<g:else>
		<g:textArea name="autoReplyText" rows="5" cols="40" disabled="true" value="${activityInstanceToEdit?.autoReplyText ?:''}"/>
	</g:else>
	<span class="hide character-count" id="reply-count">Characters remaining 160 (1 SMS message)</span> 
</div>

<g:javascript>
	$("#enableAutoReply").live("change", function() {
		// FIXME remove lookup of 'auto-reply' "group" - it's just 'this', but instead gets searched for 3 times inside this function
		if(isGroupChecked('enableAutoReply')) {
			$("#autoReplyText").removeAttr("disabled");
			$("span.character-count").removeClass("hide");
		} else {
			$("#autoReplyText").attr('disabled','disabled');
			$("span.character-count").addClass("hide");
			$("#autoReplyText").removeClass('error');
			$(".error-panel").hide();
		}
	});
	
	$("#autoReplyText").live("keyup", updateCount);
</g:javascript>
