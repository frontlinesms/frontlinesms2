<g:if test="${messageSection == 'pending'}">
	<g:if test="${failedMessageIds.contains(messageInstance.id)}">
		<g:link class="msg-btn btn" elementId="retry" action="send" params="${[failedMessageIds: [messageInstance.id]]}">Retry</g:link>
	</g:if>
</g:if>
<g:elseif test="${messageSection != 'trash'}">
 	<div id="msg-response-dropdown" class="msg-btn">
		<a id="btn_reply" onclick="messageResponseClick('Reply')">Reply</a>
		<a id='btn_dropdown' href="#" onclick="toggleDropdown();">More</a>
	</div>
	<div id="dropdown_options" style='display: none'>
		<a class='dropdown-item' id="btn_forward" onclick="messageResponseClick('Forward')">Forward</a>
	</div>
</g:elseif>
<g:if test="${!messageInstance.messageOwner && !messageInstance.archived}">
	<g:actionSubmit class="msg-btn" value="Archive" action="archive"/>
</g:if>
<g:actionSubmit class="msg-btn" value="Delete" action="delete"/>

<script>
	function toggleDropdown() {
		$()
		$("#dropdown_options").toggle()
		return false;
	};

	$("#btn_dropdown").empty();
</script>
