<div class="input">
	<label for="messageText"><g:message code="message.create.prompt"/></label>
	<g:textArea name="messageText" value="${activityInstanceToEdit?activityInstanceToEdit.sentMessageText:messageText}" rows="5" cols="40" class="required"/>
	<div class="controls">
		<div class="stats">
			<span id="send-contact-infos" class="character-count">
				<g:message code="message.character.count" args="[0, 1]"/>
			</span>
		</div>
		<div class="stats character-count-warning" style="display:none;">
			<g:message code="message.character.count.warning"/>
		</div>
		<fsms:magicWand controller="${controllerName}" instance="${activityInstanceToEdit?:null}"/>
	</div>
</div>
<r:script>
	$("#messageText").live("keyup", updateSmsCharacterCount);
</r:script>

