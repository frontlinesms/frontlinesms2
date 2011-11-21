<ul id='poll-button-list' class="button-list">
	<li>
		<g:remoteLink class="poll-btn btn" controller="quickMessage" action="create" onSuccess="launchMediumWizard('Quick Message', data, 'Send', true);" id="quick_message" class="section-action-button btn">
			Quick message
		</g:remoteLink>
	</li>
	<g:if test="${!params.viewingArchive}">
		<li><g:link class="poll-btn btn" controller="poll" action="archive" id="${ownerInstance.id}">Archive Poll</g:link></li>
	</g:if>
	<g:else>
		<li><g:link class="poll-btn btn" controller="poll" action="unarchive" id="${ownerInstance.id}">Unarchive Poll</g:link></li>
	</g:else>
	<li><g:select class="more-actions poll-btn btn" name="more-actions" from="${['Export', 'Rename poll', 'Delete poll']}"
			keys="${['export', 'rename', 'delete']}"
			noSelection="${['': 'More actions...']}"/></li>
	<li><button class="poll-btn btn" id="pollSettings">Show poll details</button></li>
</ul>
<div class="poll-details" style="display:none">
	<div id="pollGraph"></div>
</div>
