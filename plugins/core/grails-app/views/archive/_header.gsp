<div class="section-header ${messageSection}" id="radio-inbox">
	<g:hiddenField name="starred" value="${params.starred}" />
	<g:hiddenField name="failed" value="${params.failed}" />
	<g:if test="${messageSection == 'activity'}">
		<div class="message-title">
			<h3 id="message-title">Activity Archive</h3>
		</div>
	</g:if>
	<g:elseif test="${messageSection == 'folder'}">
		<div class="message-title">
			<h3 id="folder-title">Folder Archive</h3>
		</div>
	</g:elseif>
	<g:elseif test="${messageSection == 'sent'}">
		<div class="message-title">
			<h3 id="folder-title">Sent Archive</h3>
		</div>
	</g:elseif>
	<g:else>
		<div class="message-title">
			<h3 id="folder-title">Inbox Archive</h3>
		</div>
	</g:else>
</div>
