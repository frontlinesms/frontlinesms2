<div class="section-header ${messageSection}" id="message-list-header">
	<g:hiddenField name="starred" value="${params.starred}" />
	<g:hiddenField name="failed" value="${params.failed}" />
	<g:if test="${messageSection == 'activity'}">
		<h3 id="message-title">Activity Archive</h3>
	</g:if>
	<g:elseif test="${messageSection == 'folder'}">
		<h3 id="folder-title">Folder Archive</h3>
	</g:elseif>
	<g:elseif test="${messageSection == 'sent'}">
		<h3 id="folder-title">Sent Archive</h3>
	</g:elseif>
	<g:else>
		<h3 id="folder-title">Inbox Archive</h3>
	</g:else>
</div>
