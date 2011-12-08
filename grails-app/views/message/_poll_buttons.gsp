<ul id='poll-button-list' class="button-list">
	<li>
		<g:remoteLink class="section-action-button poll-btn btn" controller="quickMessage" action="create" onSuccess="launchMediumWizard('Quick Message', data, 'Send', true);" id="quick_message">
			Quick message
		</g:remoteLink>
	</li>
	<g:if test="${!params.viewingArchive}">
		<li><g:link class="poll-btn btn" controller="poll" action="archive" id="${ownerInstance.id}">Archive Poll</g:link></li>
	</g:if>
	<g:else>
		<li><g:link class="poll-btn btn" controller="poll" action="unarchive" id="${ownerInstance.id}">Unarchive Poll</g:link></li>
	</g:else>
	<li><g:select class="dropdown more-actions poll-btn" name="more-actions" from="${['Export', 'Rename poll', 'Delete poll']}"
			keys="${['export', 'rename', 'delete']}"
			noSelection="${['': 'More actions...']}"/></li>
</ul>
<a id='poll-graph-btn' class='show-arrow'>Show poll details</a>
