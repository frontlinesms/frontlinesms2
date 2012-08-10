<div class="input optional" id="subscription-join-autoreply">
	<label for="enableJoinAutoreply">
		<g:message code="subscription.autoreply.join"/>
	</label>
	<g:checkBox name="enableJoinAutoreply" checked="${activityInstanceToEdit?.joinAutoreplyText as boolean}" class="enableAutoReply"/>
	<g:textArea name="joinAutoreplyText" rows="5" cols="40" disabled="${activityInstanceToEdit?.joinAutoreplyText ? false:'disabled'}" value="${activityInstanceToEdit?.joinAutoreplyText ?:''}"/>
	<div class="controls">
		<div class="stats">
			<span id="send-message-stats" class="character-count">
				<g:message code="message.character.count" args="[0, 1]"/>
			</span>
		</div>
		<div class="stats character-count-warning" style="display:none;">
			<g:message code="message.character.count.warning"/>
		</div>
		<fsms:magicWand target="joinAutoreplyText" controller="${controllerName}" hidden="true" instance="${activityInstanceToEdit?:null}"/>
	</div>
</div>

<div class="input optional" id="subscription-leave-autoreply">
	<label for="enableLeaveAutoreply">
		<g:message code="subscription.autoreply.leave"/>
	</label>
	<g:checkBox name="enableLeaveAutoreply" checked="${activityInstanceToEdit?.leaveAutoreplyText as boolean}" class="enableAutoReply"/>
	<g:textArea name="leaveAutoreplyText" rows="5" cols="40" disabled="${activityInstanceToEdit?.leaveAutoreplyText ? false:'disabled'}" value="${activityInstanceToEdit?.leaveAutoreplyText ?:''}"/>
	<div class="controls">
		<div class="stats">
			<span id="send-message-stats" class="character-count">
				<g:message code="message.character.count" args="[0, 1]"/>
			</span>
		</div>
		<div class="stats character-count-warning" style="display:none;">
			<g:message code="message.character.count.warning"/>
		</div>
		<fsms:magicWand target="leaveAutoreplyText" controller="${controllerName}" hidden="true" instance="${activityInstanceToEdit?:null}"/>
	</div>
</div>

<r:script>
	$("input.enableAutoReply").live("change", function() {
		// FIXME remove lookup of 'auto-reply' "group" - it's just 'this', but instead gets searched for 3 times inside this function
		var isJoin = $(this).attr("id").indexOf("enableJoin") > -1;
		var autoreplyText;
		var base;
		if (isJoin) {
			autoreplyText = $("#joinAutoreplyText");
			base = $("#subscription-join-autoreply")
		}
		else {
			autoreplyText = $("#leaveAutoreplyText");
			base = $("#subscription-leave-autoreply")
		}
		var characterCount = base.find("span.character-count")
		var magicwandContainer = base.find(".magicwand-container")

		if($(this).is(':checked')) {
			autoreplyText.removeAttr("disabled");
			autoreplyText.addClass("required");
			characterCount.removeClass("hide");
			magicwandContainer.css({"visibility":"visible"});
			magicwandContainer.parent().find(".character-count-warning").css({"visibility":"visible"});
		} else {
			autoreplyText.attr('disabled','disabled');
			autoreplyText.removeClass("required");
			characterCount.addClass("hide");
			magicwandContainer.css({"visibility":"hidden"});
			magicwandContainer.parent().find(".character-count-warning").css({"visibility":"hidden"});
			autoreplyText.removeClass('error');
		}
	});
	
	$("#autoreplyText").live("keyup", updateSmsCharacterCount);
</r:script>
