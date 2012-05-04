<div id="tabs-1">
	<h2 class="bold" for="messageText"><g:message code="announcement.create.message.title"/></h2><br/>
	<g:textArea name="messageText" value="${messageText}" rows="5" cols="40"/>
	<span id="send-message-stats" class="character-count"><g:message code="announcement.message.count"/></span>
</div>
<r:script>
	$("#messageText").live("keyup", updateCount);
</r:script>
