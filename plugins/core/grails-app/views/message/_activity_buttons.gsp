<ul id='poll-button-list' class="button-list">
	<li>
		<g:remoteLink class="section-action-button poll-btn btn" controller="quickMessage" action="create" onSuccess="launchMediumWizard('Quick Message', data, 'Send', true);" id="quick_message">
			<div id="quick-message">Quick message</div>
		</g:remoteLink>
	</li>
	<g:if test="${!params.viewingArchive}">
		<li><g:link class="poll-btn btn" controller="${ownerInstance?.type}" action="archive" id="${ownerInstance?.id}">Archive ${ownerInstance?.type}</g:link></li>
	</g:if>
	<g:else>
		<li><g:link class="poll-btn btn" controller="${ownerInstance?.type}" action="unarchive" id="${ownerInstance?.id}">Unarchive ${ownerInstance?.type}</g:link></li>
	</g:else>
	<li><g:render template="/poll/activity_more_actions" plugin="${grailsApplication.config.frontlinesms2.plugin}"/></li>
</ul>
<g:if test="${messageSection == 'activity' && ownerInstance.type == 'poll'}">
	<a id='poll-graph-btn' class='show-arrow'>Show poll details</a>
</g:if>
