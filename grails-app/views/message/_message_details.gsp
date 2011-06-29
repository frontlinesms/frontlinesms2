<g:if test="${messageInstance != null}">
	<div id="message-details">
		<p class="message-name">${messageInstance.displaySrc}</p>
		<g:def var="thisAddress" value="${messageInstance.src}"/>
		<g:if test="${!messageInstance.contactExists}">
			<g:link class="button" controller="contact" action="createContact" params="[address: thisAddress]">+</g:link>
		</g:if>
		<p class="message-date"><g:formatDate format="dd-MMM-yyyy hh:mm" date="${messageInstance.dateCreated}" /></p>
		<p class="message-body">${messageInstance.text}</p>
		<div class="buttons">
			<g:if test="${messageSection == 'poll'}">
				<g:link action="deleteMessage" params="[messageSection: messageSection, ownerId: ownerInstance.id, messageId: messageInstance.id]">Delete</g:link>
			</g:if>
			<g:elseif test="${messageSection == 'folder'}">
				<g:link disabled="true" action="deleteMessage" params="[messageSection: messageSection, ownerId: ownerInstance.id, messageId: messageInstance.id]">Delete</g:link>
			</g:elseif>
			<g:else>
				<g:remoteLink controller="quickMessage" action="create" params="[recipient: messageInstance.src]" onSuccess="loadContents(data);" class="quick_message">
					Reply
				</g:remoteLink>
				<g:link action="deleteMessage" params="[messageSection: messageSection, messageId: messageInstance.id]">Delete</g:link>
			</g:else>
		</div>
	</div>
	<g:render template="action_list"/>
</g:if>
