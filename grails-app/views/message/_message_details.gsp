<div id="message-details">
	<g:hiddenField id="message-src" name="message-src" value="${messageInstance.src}" />
	<g:hiddenField id="message-id" name="message-id" value="${messageInstance.id}" />
	<h2 id="contact-name">${messageInstance.displayName}
		<g:if test="${!messageInstance.contactExists}">
			<g:link class="button" id="add-contact" controller="contact" action="createContact" params="[primaryMobile: '${messageInstance.src ?: messageInstance.dst}']"><img src='${resource(dir:'images/icons',file:'messagehistory.gif')}' /></g:link>
		</g:if>
	</h2>
	<p id="message-date"><g:formatDate format="dd-MMM-yyyy hh:mm" date="${messageInstance.dateCreated}" /></p>
	<p id="message-body">${messageInstance.text}</p>
	<div class='actions'>
		<ol class="buttons">
			<g:if test="${messageSection != 'trash'}">
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
					<li class='static_btn'><g:link  elementId="message-delete" action="deleteMessage" params="[messageSection: messageSection, ownerId: ownerInstance?.id, ids: messageInstance.id]">Delete</g:link></li>
					<li class='static_btn'><g:link elementId="message-archive" action="archiveMessage" params="[messageSection: messageSection, ownerId: ownerInstance?.id, ids: messageInstance.id]">Archive</g:link></li>
				</div>
			</g:if>
		</ol>
			<g:render template="/message/action_list"/>
			<div id="poll-actions">
				<g:if test="${messageInstance && messageSection == 'poll'}">
					<g:render  template="categorize_response"/>
				</g:if>
			</div>
	</div>
</div>
