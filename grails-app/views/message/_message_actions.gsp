<li id="btn_replace">
	<div id='static'>
		<a id="btn_reply">Reply</a>
		<a id='btn_dropdown'></a>
	</div>
	<div id="dropdown_options">
		<a class='dropdown-item' id="btn_forward">Forward</a>
	</div>
</li>
<div id='other_btns'>
	<li class='static_btn'><g:link elementId="message-delete" action="deleteMessage" params="[messageSection: messageSection, ownerId: ownerInstance?.id, ids: messageInstance.id, archived: params.archived]">Delete</g:link></li>
	<g:if test="${!params['archived'] && messageSection != 'poll'}">
		<li class='static_btn'><g:link elementId="message-archive" action="archiveMessage" params="[messageSection: messageSection, ownerId: ownerInstance?.id, ids: messageInstance.id]">Archive</g:link></li>
	</g:if>
</div>
