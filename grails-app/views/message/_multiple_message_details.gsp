<div id="multiple-messages">
	<div id='message-info'>
		<g:if test="${messageSection == 'trash' && ownerInstance}">
			<div id='activity-info'>
				<p id="message-detail-sender">${ownerInstance instanceof frontlinesms2.Poll ? ownerInstance.title : ownerInstance.name}</p>
				<p id="message-detail-date"><g:formatDate date="${ownerInstance.dateCreated}"/></p>
				<div id="message-detail-content"><p>${ownerInstance.getLiveMessageCount() == 1 ? "1 message" : ownerInstance.getLiveMessageCount() + " messages"}</p></div>
			</div>
		</g:if>
		<g:else>
			<div id="message-detail-content"><p id='checked-message-count'>${checkedMessageCount} messages selected</p></div>
		</g:else>
	</div>
	<div id="message-detail-buttons">
		<g:form name="message-action" controller="message" method="POST">
			<g:hiddenField name="messageSection" value="${messageSection}"></g:hiddenField>
			<g:hiddenField name="ownerId" value="${ownerInstance?.id}"></g:hiddenField>
			<g:hiddenField name="messageId" value="${messageInstance?.id}"></g:hiddenField>
			<g:hiddenField name="checkedMessageList" value="${params.checkedMessageList}"></g:hiddenField>
			<g:hiddenField name="viewingArchive" value="${params.viewingArchive}"></g:hiddenField>
			<g:if test="${messageSection == 'result'}">
				<g:hiddenField name="searchId" value="${search?.id}"></g:hiddenField>
			</g:if>
			
			<g:if test="${messageSection == 'pending'}">
				<g:if test="${checkedMessageList.tokenize(',').intersect(failedMessageIds*.toString())}">
					<g:link class="msg-btn btn" elementId="retry-failed" action="send" params="${[failedMessageIds : checkedMessageList.tokenize(',').intersect(failedMessageIds*.toString())]}">Retry failed</g:link>
				</g:if>
				<g:actionSubmit class="msg-btn" value="Delete All" id="btn_delete_all" action="delete"/>
			</g:if>
			<g:elseif test="${messageSection == 'trash' && ownerInstance}">
				<g:remoteLink class="msg-btn btn" controller="${(ownerInstance instanceof frontlinesms2.Folder) ? 'folder' : 'poll'}" action="restore" params="[id: ownerInstance?.id]" onSuccess="function() { window.location = location}" >Restore</g:remoteLink>
			</g:elseif>	
			<g:elseif test="${messageSection != 'trash'}">
					<g:remoteLink class="msg-btn btn" elementId="reply-all" controller="quickMessage" action="create" params="[messageSection: messageSection, recipients: params.checkedMessageList, ownerId: ownerInstance?.id, viewingArchive: params.viewingArchive, configureTabs: 'tabs-1,tabs-3,tabs-4']" onSuccess="launchMediumWizard('Reply All', data, 'Send', true);">
						Reply All
					</g:remoteLink>
					<g:if test="${(messageSection != 'poll' && messageSection != 'folder') && !viewingArchive}">
						<g:actionSubmit class="msg-btn" value="Archive All" id="btn_archive_all" action="archive"/>
					</g:if>
					<g:actionSubmit class="msg-btn" value="Delete All" id="btn_delete_all" action="delete"/>
			</g:elseif>
			<g:render template="../message/other_actions"/>
		</g:form>
	</div>
</div>