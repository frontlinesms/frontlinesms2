<div id="message-details">
	<g:hiddenField name="checkedMessageList" id="checkedMessageList" value="${checkedMessageList}" />
	<div id="single-message">
		<g:if test="${messageInstance}">
			<g:hiddenField id="message-src" name="message-src" value="${messageInstance.src}"/>
			<g:hiddenField id="message-id" name="message-id" value="${messageInstance.id}"/>
			<div id='message-info'>
				<h2 id="contact-name">${messageInstance.contactName}
					<g:if test="${!messageInstance.contactExists}">
						<g:link class="button" id="add-contact" controller="contact" action="createContact" params="[primaryMobile: (messageSection == 'sent' || messageSection == 'pending') ? messageInstance.dst : messageInstance.src]"><img src='${resource(dir: 'images/icons', file: 'add.png')}'/></g:link>
					</g:if>
				</h2>
				<p id="message-date"><g:formatDate date="${messageInstance.dateCreated ?: ownerInstance.lastUpdated}"/></p>
				<p id="message-body">${messageInstance.text}</p>
			</div>
			<g:render template="../message/message_actions"></g:render>
			<g:render template="../message/other_actions"></g:render>
		</g:if>
		<g:if test="${messageSection == 'trash' && ownerInstance}"
			<div id='activity-info'>
				<h2 id="activity-name">${ownerInstance instanceof frontlinesms2.Poll ? ownerInstance.title : ownerInstance.name}	</h2>
				<p id="activity-date"><g:formatDate date="${ownerInstance.lastUpdated}"/></p>
				<p id="activity-body">${ownerInstance.getLiveMessageCount() == 1 ? "1 message" : ownerInstance.getLiveMessageCount() + " messages"}</p>
			</div>
		</g:if>
		<g:if test="${!messageInstance && !ownerInstance}"
			<div id='message-info'>
				<p id="message-body">No message selected</p>
			</div>
		</g:if>
	</div>
	<div id="multiple-messages">
		<g:if test="${messageInstance}"
			<div id='message-info'>
				<h2 id='checked-message-count'>${checkedMessageCount} messages selected</h2>
				<div class="actions">
					<ol class="buttons">
						<g:if test="${messageSection == 'pending'}">
							<li class='static_btn'>
								<g:if test="${checkedMessageList.tokenize(',').intersect(failedMessageIds*.toString())}">
									<g:link elementId="retry-failed" action="send" params="${[failedMessageIds : checkedMessageList.tokenize(',').intersect(failedMessageIds*.toString())]}">Retry failed</g:link>
								</g:if>
							</li>
							<g:render template="../message/message_button_renderer" model="${[value:'Delete All',id:'btn_delete_all',action:'delete']}"></g:render>
						</g:if>
						<g:elseif test="${messageSection != 'trash'}">
							<div id='other_btns'>
								<li class='static_btn'>
									<g:remoteLink elementId="reply-all" controller="quickMessage" action="create" params="[messageSection: messageSection, recipients: params.checkedMessageList, ownerId: ownerInstance?.id, viewingArchive: params.viewingArchive, configureTabs: 'tabs-1,tabs-3,tabs-4']" onSuccess="launchMediumWizard('Reply All', data, 'Send', null, true);addTabValidations()">
										Reply All
									</g:remoteLink>
								</li>
								<g:if test="${(messageSection != 'poll' && messageSection != 'folder') && !params.viewingArchive}">
									<g:render template="../message/message_button_renderer" model="${[value:'Archive All',id:'btn_archive_all',action:'archive']}"></g:render>
								</g:if>
								<g:render template="../message/message_button_renderer" model="${[value:'Delete All',id:'btn_delete_all',action:'delete']}"></g:render>
							</div>
						</g:elseif>
					</ol>
					<g:render template="../message/other_actions"></g:render>
				</div>
			</div>
		</g:if>
	</div
</div>
