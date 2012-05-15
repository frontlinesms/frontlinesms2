<h2 class="bold" for="messageText"><g:message code="message.create.prompt"/></h2><br/>
<g:textArea name="messageText" id="123" value="${messageText}" rows="5" cols="40"/>
<span id="send-message-stats" class="character-count"><g:message code="message.character.count" args="[0, 1]"/></span>
<fsms:magicWand view="quickCompose" target="123"/>
<r:script>
	$("#messageText").live("keyup", updateSmsCharacterCount);
</r:script>

