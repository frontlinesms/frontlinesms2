<div class="section-header ${messageSection}" id="message-list-header">
	<g:hiddenField name="starred" value="${params.starred}" />
	<g:hiddenField name="failed" value="${params.failed}" />
	<g:if test="${messageSection == 'activity'}">
		<g:if test="${params.controller == 'archive' && viewingMessages}">
			<g:link controller="archive" action="${params.action}List"> 
				<g:message code="fmessage.archive.back" />
			</g:link>
		</g:if>
		<h3 class="activity">${ownerInstance?.name} ${ownerInstance?.type}</h3>
		<g:if test="${ownerInstance}">
			<g:render template="../message/activity_buttons"/>
		</g:if>
		<div id="activity-details" class='section-details'>
			<g:if test="${ownerInstance?.type == 'poll'}">
				<g:render template="../message/poll_header"/>
			</g:if>
			<g:else>
				<g:formatDate date="${ownerInstance?.dateCreated}" />
				<g:if test="${ownerInstance?.type == 'announcement'}">
					<span id="announcement-sent"><g:message code="fmessage.activity.sentmessage" args="${ [sentMessageCount] }" /></span>
					<p>${ownerInstance.sentMessageText}</p>
				</g:if>
				<g:else>
					<p>${ownerInstance?.autoreplyText}</p>
				</g:else>
			</g:else>
		</div>
	</g:if>
	<g:elseif test="${messageSection == 'folder'}">
		<h3 class="folder">${ownerInstance?.name} ${messageSection}</h3>
		<g:render template="../message/section_action_buttons"/>
	</g:elseif>
	<g:else>
		<h3 class="${messageSection}">${messageSection}</h3>
		<g:render template="../message/section_action_buttons"/>
	</g:else>
</div>
