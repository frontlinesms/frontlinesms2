<div id="single-message">
	<g:if test="${messageInstance}">
		<g:hiddenField id="message-src" name="message-src" value="${messageInstance.src}"/>
		<g:hiddenField id="message-id" name="message-id" value="${messageInstance.id}"/>
		<div id='message-info'>
			<p id="message-detail-sender">${messageInstance.displayName}
				<g:if test="${!messageInstance.contactExists}">
					<g:link controller="contact" action="createContact" params="[primaryMobile: ((messageSection == 'sent' || messageSection == 'pending') && messageInstance.dispatches.size() == 1) ? messageInstance.dispatches.dst : messageInstance.src]"><img id="add-contact" src='${resource(dir: 'images/icons', file: 'add.png')}'/></g:link>
				</g:if>
			</p>
			<p id="message-detail-date"><g:formatDate format="dd MMMM, yyyy hh:mm a" date="${messageInstance.date}"/></p>
			<div id="message-detail-content"><p><!-- TODO convert linebreaks in message to new paragraphs (?)  -->${messageInstance.text}</p></div>
		</div>
		<g:render template="../message/message_actions"></g:render>
		<g:render template="../message/other_actions"/>
	</g:if>
	<g:elseif test="${messageSection == 'trash' && ownerInstance}">
		<div id='message-info'>
			<p id="message-detail-sender">${ownerInstance instanceof frontlinesms2.Poll ? ownerInstance.title : ownerInstance.name} </p>
			<p id="message-detail-date"><g:formatDate format="dd MMMM, yyyy hh:mm a" date="${ownerInstance.dateCreated}"/></p>
			<div id="message-detail-content"><p>${ownerInstance.getLiveMessageCount() == 1 ? "1 message" : ownerInstance.getLiveMessageCount() + " messages"}</p></div>
		</div>
		<g:render template="../message/message_actions"></g:render>
	</g:elseif>
	<g:else>
		<div id='message-info'>
			<g:hiddenField name="viewingArchive" value="${viewingArchive}"></g:hiddenField>
			<div  id="message-detail-content"><p>No message selected</p></div>
		</div>
	</g:else>
</div>
