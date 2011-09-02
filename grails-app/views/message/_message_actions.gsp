<div class="actions buttons">
	<ol class="buttons">
		<g:if test="${buttons != null}">
			${buttons}
		</g:if>
		<g:else>
			<li id="btn_replace">
				<div id='static'>
					<a id="btn_reply" onclick="messageResponseClick('Reply')">Reply</a>
					<a id='btn_dropdown' href="#" onclick="toggleDropdown();"><img src='${resource(dir:'images/buttons',file:'paginationright_default.png')}' width='20px' height='25px' width="36" height="40"/></a>
				</div>
				<div id="dropdown_options" style='display: none'>
					<a class='dropdown-item' id="btn_forward" onclick="messageResponseClick('Forward')">Forward</a>
				</div>
			</li>
			<div id='other_btns'>
				<g:render template="message_button_renderer" model="${[value:'Archive',id:'message-archive',action:'archive']}"></g:render>
				<g:render template="message_button_renderer" model="${[value:'Delete',id:'message-delete',action:'delete']}"></g:render>
			</div>
		</g:else>
	</ol>
</div>

<script>
	function toggleDropdown() {
		$("#dropdown_options").toggle()
		return false;
	};
</script>
