<div id="tabs-1">
	<h2 class="bold" for="messageText">Enter message:</h2><br />
	<g:textArea name="messageText" value="${messageText}" rows="5" cols="40"/>
	<span id="send-message-stats" requiewclass="character-count">0 characters (1 SMS message)</span> 
</div>
<g:javascript>
	$("#messageText").live("keyup", updateCount);
</g:javascript>
