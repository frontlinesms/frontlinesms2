<h2><g:message code="poll.replies.header"/></h2>
<div class="info">
	<p><g:message code="poll.replies.description"/></p>
</div>
<div class="input optional">
	<label for="enableAutoreply">
		<g:message code="poll.autoreply.send"/>
	</label>
	<g:checkBox name="enableAutoreply" checked="${activityInstanceToEdit?.autoreplyText as boolean}"/>
	<g:textArea name="autoreplyText" rows="5" cols="40" disabled="${activityInstanceToEdit?.autoreplyText ? false:'disabled'}" value="${activityInstanceToEdit?.autoreplyText ?:''}"/>
	<div class="controls">
		<div class="stats">
			<span id="send-contact-infos" class="character-count">
				<g:message code="message.character.count" args="[0, 1]"/>
			</span>
		</div>
		<div class="stats character-count-warning" style="display:none;">
			<g:message code="message.character.count.warning"/>
		</div>
		<fsms:magicWand target="autoreplyText" controller="${controllerName}" hidden="true" instance="${activityInstanceToEdit?:null}"/>
	</div>
</div>

<r:script>
	$("#enableAutoreply").live("change", function() {
		// FIXME remove lookup of 'auto-reply' "group" - it's just 'this', but instead gets searched for 3 times inside this function
		var autoreplyText = $("#autoreplyText");
		if(isGroupChecked("enableAutoreply")) {
			autoreplyText.removeAttr("disabled");
			autoreplyText.addClass("required");
			$("span.character-count").removeClass("hide");
			$(".magicwand-container").css({"visibility":"visible"});
			$(".magicwand-container").parent().find(".character-count-warning").css({"visibility":"visible"});
		} else {
			autoreplyText.attr('disabled','disabled');
			autoreplyText.removeClass("required");
			$("span.character-count").addClass("hide");
			$(".magicwand-container").css({"visibility":"hidden"});
			$(".magicwand-container").parent().find(".character-count-warning").css({"visibility":"hidden"});
			$("#autoreplyText").removeClass('error');
			$(".error-panel").hide();
		}
	});
	
	$("#autoreplyText").live("keyup", updateSmsCharacterCount);
</r:script>
