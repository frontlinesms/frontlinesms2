<div id="single-message">
	<g:if test="${messageInstance}">
		<g:hiddenField id="message-src" name="message-src" value="${messageInstance.src}"/>
		<g:hiddenField id="message-id" name="message-id" value="${messageInstance.id}"/>
		<div id='message-info'>
			<p id="message-detail-sender">${messageInstance.contactName}
				<g:if test="${!messageInstance.contactExists}">
					<g:link id="add-contact" controller="contact" action="createContact" params="[primaryMobile: (messageSection == 'sent' || messageSection == 'pending') ? messageInstance.dst : messageInstance.src]"><img src='${resource(dir: 'images/icons', file: 'add.png')}'/></g:link>
				</g:if>
			</p>
			<p id="message-detail-date"><g:formatDate format="dd MMMM, yyyy hh:mm a" date="${messageInstance.dateReceived ?: messageInstance.dateSent}"/></p>
			<div id="message-detail-content"><p><!-- TODO convert linebreaks in message to new paragraphs (?)  -->${messageInstance.text}</p></div>
		</div>
		<div id="message-detail-buttons">
			<g:form controller="${params.viewingArchive ? 'archive' : 'message'}" method="POST">
				<g:hiddenField name="messageSection" value="${messageSection}"></g:hiddenField>
				<g:hiddenField name="ownerId" value="${ownerInstance?.id}"></g:hiddenField>
				<g:hiddenField name="messageId" value="${messageInstance.id}"></g:hiddenField>
				<g:hiddenField name="checkedMessageList" value="${params.checkedMessageList}"></g:hiddenField>
				<g:hiddenField name="viewingArchive" value="${params.viewingArchive}"></g:hiddenField>
				<g:if test="${messageSection == 'result'}">
					<g:hiddenField name="searchId" value="${search.id}"></g:hiddenField>
				</g:if>
				
				<g:render template="../message/message_actions"></g:render>
			</g:form>
			<g:render template="../message/other_actions"/>
		</div>
	</g:if>
	<g:elseif test="${messageSection == 'trash' && ownerInstance}"
		<div id='message-info'>
			<h2 id="message-detail-sender">${ownerInstance instanceof frontlinesms2.Poll ? ownerInstance.title : ownerInstance.name} </h2>
			<p id="message-detail-date"><g:formatDate date="${ownerInstance.dateCreated}"/></p>
			<p id="message-detail-content">${ownerInstance.getLiveMessageCount() == 1 ? "1 message" : ownerInstance.getLiveMessageCount() + " messages"}</p>
		</div>
		<g:remoteLink controller="${(ownerInstance instanceof frontlinesms2.Folder) ? 'folder' : 'poll'}" action="restore" params="[id: ownerInstance?.id]" onSuccess="function() { window.location = location}" >Restore</g:remoteLink>
	</g:elseif>
	<g:else>
		<div id='message-info'>
			<g:hiddenField name="viewingArchive" value="${viewingArchive}"></g:hiddenField>
			<div  id="message-detail-content"><p>No message selected</p></div>
		</div>
	</g:else>
</div>
