    <g:if test="${messageSection == 'pending'}">
			<g:if test="${failedMessageIds.contains(messageInstance.id)}">
					<g:link elementId="retry" action="send" params="${[failedMessageIds: [messageInstance.id]]}">Retry</g:link>
			</g:if>
			<g:render template="../message/message_button_renderer" model="${[value:'Delete',id:'btn_delete',action:'delete']}"></g:render>
    </g:if>
		<g:elseif test="${messageSection != 'trash'}">
            <select>
		    <option id="btn_reply" onclick="messageResponseClick('Reply')">Reply</option>
		    <a id='btn_dropdown' href="#" onclick="toggleDropdown();"><img src='${resource(dir:'images/buttons',file:'paginationright_default.png')}' width='20px' height='25px' width="36" height="40"/></a>
		    <option class='dropdown-item' id="btn_forward" onclick="messageResponseClick('Forward')">Forward</option>
            </select>
    </g:elseif>

<script>
	function toggleDropdown() {
		$("#dropdown_options").toggle()
		return false;
	};
</script>
