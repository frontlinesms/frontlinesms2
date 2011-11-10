<div id="message-detail">
	<g:hiddenField name="checkedMessageList" id="checkedMessageList" value="${checkedMessageList}" />
	<div id="single-message">
	<g:if test="${messageInstance}">
		<g:hiddenField id="message-src" name="message-src" value="${messageInstance.src}"/>
		<g:hiddenField id="message-id" name="message-id" value="${messageInstance.id}"/>
		<div id='message-info'>
			<p id="message-detail-sender">${messageInstance.contactName}
				<g:if test="${!messageInstance.contactExists}">
					<g:link class="button" id="add-contact" controller="contact" action="createContact" params="[primaryMobile: (messageSection == 'sent' || messageSection == 'pending') ? messageInstance.dst : messageInstance.src]"><img src='${resource(dir: 'images/icons', file: 'add.png')}'/></g:link>
				</g:if>
			</p>
			<p id="message-detail-date"><g:formatDate date="${messageInstance.dateReceived ?: messageInstance.dateSent}"/></p>
			<div id="message-detail-content"><p><!-- TODO convert linebreaks in message to new paragraphs (?)  -->${messageInstance.text}</p></div>
		</div>
        <div id="message-detail-buttons">
            <g:form controller="message" method="POST">
                <g:hiddenField name="messageSection" value="${messageSection}"></g:hiddenField>
                <g:hiddenField name="ownerId" value="${ownerInstance?.id}"></g:hiddenField>
            	<g:hiddenField name="messageId" value="${messageInstance.id}"></g:hiddenField>
	            <g:hiddenField name="checkedMessageList" value="${params.checkedMessageList}"></g:hiddenField>
            	<g:hiddenField name="viewingArchive" value="${params.viewingArchive}"></g:hiddenField>
                <g:if test="${messageSection == 'result'}">
                    <g:hiddenField name="searchId" value="${search.id}"></g:hiddenField>
                </g:if>
                <g:render template="../message/message_actions"></g:render>
				<g:if test="${!messageInstance.messageOwner && !messageInstance.archived}">
                    <g:actionSubmit value="Archive" action="archive"/>
				</g:if>
                <g:actionSubmit value="Delete" action="delete"/>
            </g:form>
	</div>
	<div id="multiple-messages">
		<div id='message-info'>
			<p id='checked-message-count'>${checkedMessageCount} messages selected</p>
			<div class="actions">
				<ul class="buttons">
					<g:if test="${messageSection == 'pending'}">
						<li class='static_btn'>
							<g:if test="${checkedMessageList.tokenize(',').intersect(failedMessageIds*.toString())}">
								<g:link elementId="retry-failed" action="send" params="${[failedMessageIds : checkedMessageList.tokenize(',').intersect(failedMessageIds*.toString())]}">Retry failed</g:link>
							</g:if>
						</li>
						<g:render template="../message/message_button_renderer" model="${[value:'Delete All',id:'btn_delete_all',action:'delete']}"/>
					</g:if>
					<g:elseif test="${messageSection == 'trash' && ownerInstance}"
						<div id='activity-info'>
							<h2 id="activity-name">${ownerInstance instanceof frontlinesms2.Poll ? ownerInstance.title : ownerInstance.name}	</h2>
							<p id="activity-date"><g:formatDate date="${ownerInstance.dateCreated}"/></p>
							<p id="activity-body">${ownerInstance.getLiveMessageCount() == 1 ? "1 message" : ownerInstance.getLiveMessageCount() + " messages"}</p>
						</div>
						<g:remoteLink controller="${(ownerInstance instanceof frontlinesms2.Folder) ? 'folder' : 'poll'}" action="restore" params="[id: ownerInstance?.id]" onSuccess="function() { window.location = location}" >Restore</g:remoteLink>
					</g:elseif>	
					<g:elseif test="${messageSection != 'trash'}">
						<div id='other_btns'>
							<li class='static_btn'>
								<g:remoteLink elementId="reply-all" controller="quickMessage" action="create" params="[messageSection: messageSection, recipients: params.checkedMessageList, ownerId: ownerInstance?.id, viewingArchive: params.viewingArchive, configureTabs: 'tabs-1,tabs-3,tabs-4']" onSuccess="launchMediumWizard('Reply All', data, 'Send', true);">
									Reply All
								</g:remoteLink>
							</li>
							<g:if test="${(messageSection != 'poll' && messageSection != 'folder') && !params.viewingArchive}">
								<g:render template="../message/message_button_renderer" model="${[value:'Archive All',id:'btn_archive_all',action:'archive']}"></g:render>
							</g:if>
							<g:render template="../message/message_button_renderer" model="${[value:'Delete All',id:'btn_delete_all',action:'delete']}"></g:render>
						</div>
					</g:elseif>
					<g:else>
						<div id='message-info'>
							<p id="message-body">No message selected</p>
						</div>
					</g:else>
				</ul>
				<g:render template="../message/other_actions"/>
			</div>
		</g:if>
	</div
</div>
