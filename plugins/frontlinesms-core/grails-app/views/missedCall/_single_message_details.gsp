<%@ page defaultCodec="html" import="frontlinesms2.*" %>
<div id="single-message">
	<g:if test="${interactionInstance}">
		<g:hiddenField id="message-id" name="message-id" value="${interactionInstance.id}"/>
		<g:hiddenField id="message-src" name="message-src" value="${interactionInstance.src}"/>
		<g:hiddenField id="message-id" name="message-id" value="${interactionInstance.id}"/>
		<div id='message-info'>
			<p id="message-detail-sender">
				<i class="${interactionInstance.contactFlagCSSClasses}"></i>
				<span>
					${interactionInstance.displayName}
				</span> 
				<g:if test="${interactionInstance.displayName ==~ /^\+?[\d -.]+$/}">
					<g:link class="add" elementId="add-contact" controller="contact" title="${g.message(code:'fmessage.addsender')}" action="createContact" params="[mobile:  interactionInstance.src]"><g:message code="contact.new"/></g:link>
				</g:if>
			</p>
			<p id="message-detail-date"><g:formatDate date="${interactionInstance.date}"/></p>
			<g:if test="${interactionInstance.inbound && interactionInstance.receivedOn}">
				<p id="message-detail-fconnection">
					<g:message code="fmessage.connection.receivedon" args="${[interactionInstance.receivedOn?.name]}"/>
				</p>
			</g:if>
			<g:if test="${interactionInstance.messageOwner}">
				<p id="message-detail-owner" class="${interactionInstance.messageOwner.shortName}">
					<g:link action="${interactionInstance.messageOwner.shortName}" params="[ownerId: interactionInstance.messageOwner.id]">
						<g:message code="${interactionInstance.messageOwner.shortName}.title" args="${[interactionInstance.messageOwner.name]}"/>
					</g:link>
				</p>
			</g:if>
			<div id="message-detail-content"><p>
				<!-- TODO convert linebreaks in message to new paragraphs (?)  -->
				<g:message code="missedCall.displaytext" args="${[interactionInstance.displayName]}"/>
			</p></div>
		</div>
		<fsms:render template="/interaction/message_actions"/>
		<g:if test="${!(messageSection == 'pending') && (activityInstanceList || folderInstanceList)}">
			<fsms:render template="/interaction/other_actions"/>
		</g:if>
	</g:if>
	<g:elseif test="${messageSection == 'trash' && ownerInstance}">	
		<div id='message-info'>
			<p id="message-detail-sender">
				<g:message code="${ownerInstance.shortName}.title" args="${[ownerInstance.name]}"/>
			</p>
			<p id="message-detail-date"><g:formatDate date="${Trash.findByObject(ownerInstance).dateCreated}"/></p>
			<div id="message-detail-content"><p>${ownerInstance.messages.size() == 1 ? g.message(code:'fmessage.count') : ownerInstance.messages.size() + " " + g.message(code:'fmessage.many')}</p></div>
		</div>
		<fsms:render template="/interaction/message_actions"/>
	</g:elseif>
	<g:else>
		<div id='message-info'>
			<div  id="message-detail-content"><p id="no-message"><g:message code="fmessage.selected.none"/></p></div>
		</div>
	</g:else>
</div>

<r:script>
function showRecipientList(list) {
	var html = '<ul class="recipient-list">';
	for(r in list) { r=list[r]; html += "<li>" + r.display + " (" + r.status + ")</li>"; }
	html += '</ul>';
	launchSmallPopup(i18n('smallpopup.recipients.title'), html, i18n('action.done'), cancel);
}
</r:script>
