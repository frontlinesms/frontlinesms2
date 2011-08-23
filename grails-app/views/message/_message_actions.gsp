<div class="actions buttons">
	<ol class="buttons">
		<g:if test="${buttons != null}">
				${buttons}
		</g:if>
		<g:else>
			<li id="btn_replace">
				<div id='static'>
					<a id="btn_reply" onclick="messageResponseClick('Reply')">Reply</a>
					<a id='btn_dropdown' href="#"><img src='${resource(dir:'images/buttons',file:'paginationright_default.png')}' width='20px' height='25px' width="36" height="40"/></a>
				</div>
				<div id="dropdown_options" class='hide'>
					<a class='dropdown-item' id="btn_forward" onclick="messageResponseClick('Forward')">Forward</a>
				</div>
			</li>
			<div id='other_btns'>
				<g:if test="${!params['archived'] && messageSection != 'poll'}">
					<li class='static_btn'>
						<g:link elementId="message-archive" action="archive" params="[messageSection: messageSection, ownerId: ownerInstance?.id, messageId: messageInstance.id]">
							Archive
						</g:link>
					</li>
				</g:if>
				<g:if test="${messageSection != 'trash'}">
					<li class='static_btn'>
						<g:link elementId="message-delete" action="delete" params="[messageSection: messageSection, ownerId: ownerInstance?.id, messageId: messageInstance.id]">
							Delete
						</g:link>
					</li>
				</g:if>
			</div>
		</g:else>
	</ol>
</div>

<script>
	$("#static #btn_dropdown").bind('click', function() {
		$("#static #dropdown_options").toggle()
		return false;
	});
</script>