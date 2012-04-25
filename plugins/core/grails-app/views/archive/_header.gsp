<div class="section-header ${messageSection}" id="message-list-header">
	<g:hiddenField name="starred" value="${params.starred}" />
	<g:hiddenField name="failed" value="${params.failed}" />
	<g:if test="${messageSection == 'activity'}">
		<h3 id="activity-title"><g:message code="archive.activity" /></h3>
	</g:if>
	<g:elseif test="${messageSection == 'folder'}">
		<h3 id="folder-title"><g:message code="archive.folder" /></h3>
	</g:elseif>
	<g:elseif test="${messageSection == 'sent'}">
		<h3 id="folder-title"><g:message code="archive.sent" /></h3>
	</g:elseif>
	<g:else>
		<h3 id="folder-title"><g:message code="archive.inbox" /></h3>
	</g:else>
</div>
