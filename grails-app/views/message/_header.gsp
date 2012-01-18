<div class="section-header ${messageSection}" id="inbox-actions">
	<g:hiddenField name="starred" value="${params.starred}" />
	<g:hiddenField name="viewingArchive" value="${params.viewingArchive}" />
	<g:hiddenField name="failed" value="${params.failed}" />
	<g:if test="${params.viewingArchive && params.viewingArchive == false}">
		<g:link controller="archive" action="folder"> &lt;Back </g:link>
	</g:if>
	<g:if test="${messageSection == 'poll'}">
		<div class="activity-title">
			<g:render template="../message/poll_header"/>
		</div>
	</g:if>
	<g:elseif test="${messageSection == 'announcement'}">
		<div class="activity-title">
			<h3 id="announcement-title">${ownerInstance?.name}</h3>
			<g:render template="../message/poll_buttons"/>
			<div id="activity-details">
				<g:formatDate date="${ownerInstance?.dateCreated}" /><span id="announcement-sent">   (${sentMessageCount} messages sent)</span>
				<p>${ownerInstance?.sentMessages?.text[0]}</p>
			</div>
		</div>
	</g:elseif>
	<g:elseif test="${messageSection == 'folder'}">
		<div class="message-title">
			<h3 id="folder-title">${ownerInstance?.name} ${messageSection}</h3>
			<g:render template="../message/section_action_buttons"/>
		</div>
	</g:elseif>
	<g:elseif test="${messageSection == 'radioShow'}">
		<div class="message-title">
			<h3 id="show-title">On air</h3>
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
