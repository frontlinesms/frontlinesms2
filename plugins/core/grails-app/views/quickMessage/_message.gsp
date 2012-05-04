<div id="tabs-1" class="${configureTabs.contains('tabs-1') ? '' : 'hide'}">
	<label class="header" for="messageText"><g:message code="quickmessage.messages.label"/></label><br/>
	<g:textArea name="messageText" value="${messageText}" rows="5" cols="40"/>
	<span id="send-message-stats" class="character-count"><g:message code="quickmessage.message.count"/></span> 
</div>
<r:script>
	$("#messageText").live("keyup", updateCount);
	$("#messageText").live("blur", function() {
		var value = $(this).val();
		if(value) {
			$("#confirm-message-text").html(value);
		} else {
			$("#confirm-message-text").html("none");
		}
	})
</r:script>
