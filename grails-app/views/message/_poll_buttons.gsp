<ul id='poll-button-list' class="button-list">
	<li>
		<g:remoteLink class="section-action-button poll-btn btn" controller="quickMessage" action="create" onSuccess="launchMediumWizard('Quick Message', data, 'Send', true);" id="quick_message">
			<div id="quick-message">Quick message</div>
		</g:remoteLink>
	</li>
	<g:if test="${!params.viewingArchive}">
		<li><g:link class="poll-btn btn" controller="$messageSection" action="archive" id="${ownerInstance.id}">Archive ${messageSection}</g:link></li>
	</g:if>
	<g:else>
		<li><g:link class="poll-btn btn" controller="$messageSection" action="unarchive" id="${ownerInstance.id}">Unarchive ${messageSection}</g:link></li>
	</g:else>
	<li><g:select class="dropdown more-actions poll-btn" name="more-actions" from="${['Export', 'Rename ' + messageSection, 'Delete ' + messageSection]}"
			keys="${['export', 'rename', 'delete']}"
			noSelection="${['': 'More actions...']}"/></li>
</ul>
<g:if test="${messageSection == 'poll'}">
	<a id='poll-graph-btn' class='show-arrow'>Show poll details</a>
</g:if>
