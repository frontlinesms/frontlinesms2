<div id="tabs-2">
	<h2 class="bold"><g:message code="autoreply.message.title"/></h2>
	<g:textArea name="autoreplyText" rows="5" cols="40" value="${activityInstanceToEdit?.autoreplyText}"/>
	<span id="send-message-stats" class="character-count"><g:message code="autoreply.message.count"/></span>
</div>
<g:javascript>
	$("#autoreplyText").live("keyup", updateCount);
</g:javascript>
