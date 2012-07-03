<h2><g:message code="poll.replies.header"/></h2>
<div class="info">
	<p><g:message code="poll.replies.description"/></p>
</div>
<div class="input optional">
	<label for="enableAutoreply">
		<g:message code="poll.autoreply.send"/>
	</label>
	<g:checkBox name="enableAutoreply" checked="${activityInstanceToEdit?.autoreplyText as boolean}"/>
	<g:textArea name="autoreplyText" rows="5" cols="40" disabled="${activityInstanceToEdit? activityInstanceToEdit.autoreplyText as boolean: true}" value="${activityInstanceToEdit?.autoreplyText ?:''}"/>
	<div class="controls">
		<div class="stats">
			<span id="send-message-stats" class="character-count">
				<g:message code="message.character.count" args="[0, 1]"/>
			</span>
		</div>
		<div class="stats character-count-warning" style="display:none;">
			<g:message code="message.character.count.warning"/>
		</div>
		<fsms:magicWand target="autoreplyText" controller="${controllerName}" instance="${activityInstanceToEdit?:null}"/>
	</div>
</div>

<r:script>
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
	
	$("#autoreplyText").live("keyup", updateSmsCharacterCount);
</r:script>
