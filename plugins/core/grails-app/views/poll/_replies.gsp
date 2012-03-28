<div id="tabs-4" class="poll-response-reply">
	<h2 class="bold">Reply automatically to poll responses (optional)</h2>
	<p class="info">When an incoming message is identified as a poll response, send a
		message to the person who sent the response.
	</p>
	<g:checkBox name="enableAutoreply" checked="${activityInstanceToEdit?.autoreplyText as boolean}"/>Send an automatic reply to poll responses
	<g:if test="${activityInstanceToEdit?.autoreplyText as boolean}">
		<g:textArea name="autoreplyText" rows="5" cols="40" value="${activityInstanceToEdit?.autoreplyText ?:''}"/>
	</g:if>
	<g:else>
		<g:textArea name="autoreplyText" rows="5" cols="40" disabled="true" value="${activityInstanceToEdit?.autoreplyText ?:''}"/>
	</g:else>
	<span class="hide character-count" id="reply-count">Characters remaining 160 (1 SMS message)</span> 
</div>

<g:javascript>
	$("#enableAutoreply").live("change", function() {
		// FIXME remove lookup of 'auto-reply' "group" - it's just 'this', but instead gets searched for 3 times inside this function
		if(isGroupChecked('enableAutoreply')) {
			$("#autoreplyText").removeAttr("disabled");
			$("span.character-count").removeClass("hide");
		} else {
			$("#autoreplyText").attr('disabled','disabled');
			$("span.character-count").addClass("hide");
			$("#autoreplyText").removeClass('error');
			$(".error-panel").hide();
		}
	});
	
	$("#autoreplyText").live("keyup", updateCount);
</g:javascript>
