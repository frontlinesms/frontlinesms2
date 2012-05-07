<div id="tabs-4" class="poll-response-reply">
	<h2 class="bold"><g:message code="poll.replies.header"/></h2>
	<p class="info">
		<g:message code="poll.replies.description"/>
	</p>
	<g:checkBox name="enableAutoreply" checked="${activityInstanceToEdit?.autoreplyText as boolean}"/>
		<g:message code="poll.autoreply.send"/>
	<g:if test="${activityInstanceToEdit?.autoreplyText as boolean}">
		<g:textArea name="autoreplyText" rows="5" cols="40" value="${activityInstanceToEdit?.autoreplyText ?:''}"/>
	</g:if>
	<g:else>
		<g:textArea name="autoreplyText" rows="5" cols="40" disabled="true" value="${activityInstanceToEdit?.autoreplyText ?:''}"/>
	</g:else>
	<span class="hide character-count" id="reply-count"><g:message code="poll.message.count"/></span> 
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
