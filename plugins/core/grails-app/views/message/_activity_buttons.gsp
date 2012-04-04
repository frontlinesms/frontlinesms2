<ul class="header-buttons">
	<li>
		<g:remoteLink class="section-action-button activity-btn btn" controller="quickMessage" action="create" onSuccess="launchMediumWizard('Quick Message', data, 'Send', true);" id="quick_message">
			<div id="quick-message">Quick message</div>
		</g:remoteLink>
	</li>
	<g:if test="${params.controller!='archive'}">
		<li><g:link class="activity-btn btn" controller="${ownerInstance?.type}" action="archive" id="${ownerInstance?.id}">Archive ${ownerInstance?.type}</g:link></li>
	</g:if>
	<g:else>
		<li><g:link class="activity-btn btn" controller="${ownerInstance?.type}" action="unarchive" id="${ownerInstance?.id}">Unarchive ${ownerInstance?.type}</g:link></li>
	</g:else>
	<li><g:render template="/message/activity_more_actions" plugin="${grailsApplication.config.frontlinesms2.plugin}"/></li>
	<g:if test="${ownerInstance.type == 'poll'}">
		<li><a id='poll-graph-btn' class='show-arrow'>Show poll details</a></li>
	</g:if>
</ul>

