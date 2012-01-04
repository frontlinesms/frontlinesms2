<div class="section-header ${messageSection}" id="radio-inbox">
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
			<h3>${ownerInstance?.name}</h3>
			<g:render template="../message/section_action_buttons"/>
			<p id="activity-details">${ownerInstance?.sentMessage}</p>
		</div>
	</g:elseif>
	<g:elseif test="${messageSection == 'folder'}">
		<div class="activity-title">
			<h3>${ownerInstance?.name} ${messageSection}</h3>
			<g:render template="../message/section_action_buttons"/>
		</div>
	</g:elseif>
	<g:elseif test="${messageSection == 'radioShow'}">
		<div class="message-title">
			<h3 id="on-air" class="${ownerInstance.isRunning ? 'active' : ''}">On air</h3>
			<g:render template="/message/section_action_buttons" plugin="core"/>
			<g:render template="radio_actions" />
		</div>
	</g:elseif>
	<g:else>
		<div class="message-title">
			<h3>${messageSection}</h3>
			<g:render template="../message/section_action_buttons"/>
		</div>
	</g:else>
</div>