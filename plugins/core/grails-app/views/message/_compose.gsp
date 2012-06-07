<div class="input">
	<label for="messageText"><g:message code="message.create.prompt"/></label>
	<g:textArea name="messageText" value="${messageText}" rows="5" cols="40"/>
	<div class="controls">
		<span id="send-message-stats" class="character-count"><g:message code="message.character.count" args="[0, 1]"/></span>
		<fsms:magicWand controller="${controllerName}"/>
	</div>
</div>
<r:script>
	$("#messageText").live("keyup", updateSmsCharacterCount);
</r:script>

