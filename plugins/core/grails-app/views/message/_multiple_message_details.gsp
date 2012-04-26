<div id="multiple-messages">
	<div id='message-info'>
		<g:if test="${messageSection == 'trash' && ownerInstance}">
			<div id='activity-info'>
				<p id="message-detail-sender">${ownerInstance.name}</p>
				<p id="message-detail-date"><g:formatDate date="${ownerInstance.dateCreated}"/></p>
				<div id="message-detail-content">
					<p>
						<g:if test="${ownerInstance.getLiveMessageCount() == 1}">
							<g:message code="fmessage.count.single"/>
						</g:if>
					 	<g:else>
							<g:message code="fmessage.many" args="${[ownerInstance.liveMessageCount]}"/>
						</g:else>
					</p>
				</div>
			</div>
		</g:if>
		<g:else>
			<div id="message-detail-content">
				<p id='checked-message-count'>
					<g:message code="fmessage.selected.many" args="${[checkedMessageCount]}"/>
				</p>
			</div>
		</g:else>
	</div>
	<div id="message-detail-buttons">
		<g:form name="message-action" controller="${params.controller}" method="POST"
				params="[messageSection: messageSection, ownerId: ownerInstance?.id, messageId: messageInstance?.id, searchId: search?.id]">
			<g:hiddenField name="checkedMessageList" value="${params.checkedMessageList}"/>

			<g:if test="${messageSection == 'pending'}">
				<g:actionSubmit class="msg-btn btn" id="retry-failed" action="retry"
						params="${[type: 'multiple_failed']}" value="${g.message(code:'fmessage.retry.many')}"/>
				<g:actionSubmit class="msg-btn" value="${g.message(code:'fmessage.delete.many')}" id="btn_delete_all" action="delete"/>
			</g:if>
			<g:elseif test="${messageSection == 'trash' && ownerInstance}">
				<g:remoteLink class="msg-btn btn"
						controller="${(ownerInstance instanceof frontlinesms2.Folder) ? 'folder' : 'poll'}"
						action="restore" params="[id: ownerInstance?.id]"
						onSuccess="function() { window.location = location }" >
					<g:message code="fmessage.restore.many"/>
				</g:remoteLink>
			</g:elseif>	
			<g:elseif test="${messageSection != 'trash'}">
					<g:remoteLink class="msg-btn btn" elementId="reply-all" controller="quickMessage" action="create" params="[messageSection: messageSection, recipients: params.checkedMessageList, ownerId: ownerInstance?.id, configureTabs: 'tabs-1,tabs-3,tabs-4']" onSuccess="launchMediumWizard(i18n('wizard.messages.replyall.title'), data, 'Send', true);">
						<g:message code="fmessage.reply.many"/>
					</g:remoteLink>
					<g:if test="${(messageSection != 'activity' && messageSection != 'folder') && params.controller !='archive'}">
						<g:actionSubmit class="msg-btn" value="${g.message(code:'fmessage.archive.many')}" id="btn_archive_all" action="archive"/>
					</g:if>
					<g:elseif test="${!ownerInstance && params.controller == 'archive'}">
						<g:actionSubmit id="unarchive-msg" class="msg-btn" value="${g.message(code:'fmessage.unarchive.many')}" action="unarchive"/>
					</g:elseif>
					<g:if test="${messageSection != 'pending'}">
						<g:actionSubmit class="msg-btn" value="${g.message(code:'fmessage.delete.many')}" id="btn_delete_all" action="delete"/>
					</g:if>
			</g:elseif>
			<g:if test="${grailsApplication.config.frontlinesms.plugin == 'core'}">
				<g:render template="../message/other_actions"/>
			</g:if>
			<g:else>
				<g:render template="/message/other_actions" plugin="core"/>
			</g:else>
		</g:form>
	</div>
</div>
