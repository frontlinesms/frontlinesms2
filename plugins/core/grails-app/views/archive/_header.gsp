<div id='archive-header' class="content-header section-header">
	<div id="archive-title">
		<g:if test="${messageSection == 'inbox'}">
			<h3 id="inbox-title">${messageSection} Archive</h3>
		</g:if>
		<g:elseif test="${messageSection == 'sent'}">
			<h3 id="sent-title">${messageSection} Archive</h3>
		</g:elseif>
		<g:elseif test="${messageSection == 'activity'}">
			<h3 id="activity-title">Activity Archive</h3>
		</g:elseif>
		<g:elseif test="${messageSection == 'folder'}">
			<h3 id="folder-title">${messageSection} Archive</h3>
		</g:elseif>
		<g:render template="../message/section_action_buttons"/>
	</div>
</div>