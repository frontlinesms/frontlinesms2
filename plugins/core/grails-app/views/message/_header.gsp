<div class="section-header ${messageSection}" id="radio-inbox">
	<g:hiddenField name="starred" value="${params.starred}" />
	<g:hiddenField name="failed" value="${params.failed}" />
	<g:if test="${messageSection == 'activity' && ownerInstance?.type == 'poll'}">
		<div class="activity-title">
			<g:if test="${params.controller=='archive'}">
				<g:link controller="archive" action="${params.action}List"> &lt;Back </g:link>
			</g:if>
			<g:render template="../message/poll_header"/>
		</div>
	</g:if>
	<g:elseif test="${messageSection == 'activity' && ownerInstance?.type == 'announcement'}">
		<div class="activity-title">
			<g:if test="${params.controller=='archive'}">
				<g:link controller="archive" action="${params.action}List"> &lt;Back </g:link>
			</g:if>
			<h3 id="announcement-title">${ownerInstance?.name} ${ownerInstance?.type}</h3>
			<g:render template="../message/activity_buttons"/>
			<div id="activity-details">
				<g:formatDate date="${ownerInstance?.dateCreated}" /><span id="announcement-sent">   (${sentMessageCount} messages sent)</span>
				<p>${ownerInstance.sentMessageText}</p>
			</div>
		</div>
	</g:elseif>
	<g:elseif test="${messageSection == 'folder'}">
		<div class="message-title">
			<h3 id="folder-title">${ownerInstance?.name} ${messageSection}</h3>
			<g:render template="../message/section_action_buttons"/>
		</div>
	</g:elseif>
	<g:else>
		<div class="message-title">
			<h3 id="${messageSection}-title">${messageSection}</h3>
			<g:render template="../message/section_action_buttons"/>
		</div>
	</g:else>
</div>
