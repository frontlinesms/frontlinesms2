<%@ page defaultCodec="html" import="frontlinesms2.*" %>
<div id="single-message">
	<g:if test="${messageInstance}">
		<g:hiddenField id="message-id" name="message-id" value="${messageInstance.id}"/>
		<g:hiddenField id="message-src" name="message-src" value="${messageInstance.src}"/>
		<g:hiddenField id="message-id" name="message-id" value="${messageInstance.id}"/>
		<div id='message-info'>
			<p id="message-detail-sender">
				<i class="${messageInstance.contactFlagCSSClasses}"></i>
				<span>
					<g:if test="${!messageInstance.inbound && messageInstance.dispatches.size() > 1}">
						<fsms:popup controller="message" action="listRecipients" params="[messageId: messageInstance.id]" popupCall="showRecipientList(data)" class="btn">
							<g:message code="fmessage.to.multiple" args="${[messageInstance.displayName]}" />
						</fsms:popup>
					</g:if>
					<g:else>
						${messageInstance.displayName}
					</g:else>
					<g:if test="${messageInstance.hasFailed && failedDispatchCount == 1}"> (<g:message code="fmessage.failed"/>)</g:if>
					<g:elseif test="${messageInstance.hasFailed && failedDispatchCount}"> (${failedDispatchCount} <g:message code="fmessage.failed"/>)</g:elseif>
				</span> 
				<g:if test="${messageInstance.displayName ==~ /^\+?[\d -.]+$/ && (messageInstance.inbound || messageInstance.dispatches.size() == 1)}">
					<g:link class="add" elementId="add-contact" controller="contact" title="${g.message(code:'fmessage.addsender')}" action="createContact" params="[mobile: (!messageInstance.inbound && messageInstance.dispatches.size() == 1) ? messageInstance.dispatches.dst : messageInstance.src]"><g:message code="contact.new"/></g:link>
				</g:if>
			</p>
			<p id="message-detail-date"><g:formatDate date="${messageInstance.date}"/></p>
			<g:if test="${messageInstance.inbound && messageInstance.receivedOn}">
				<p id="message-detail-fconnection">
					<g:message code="fmessage.connection.receivedon" args="${[messageInstance.receivedOn?.name]}"/>
				</p>
			</g:if>
			<g:if test="${messageInstance.messageOwner}">
				<p id="message-detail-owner" class="${messageInstance.messageOwner.shortName}">
					<g:link action="${messageInstance.messageOwner.shortName}" params="[ownerId: messageInstance.messageOwner.id]">
						<g:message code="${messageInstance.messageOwner.shortName}.title" args="${[messageInstance.messageOwner.name]}"/>
					</g:link>
				</p>
			</g:if>
			<div id="message-detail-content"><p>
				<!-- TODO convert linebreaks in message to new paragraphs (?)  -->
				${messageInstance.text}
			</p></div>
		</div>
		<fsms:render template="/message/message_actions"/>
		<g:if test="${!(messageSection == 'pending') && (activityInstanceList || folderInstanceList)}">
			<fsms:render template="/message/other_actions"/>
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
		<fsms:render template="/message/message_actions"/>
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
