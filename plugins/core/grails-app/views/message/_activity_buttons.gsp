<ul class="button-list">
	<li>
		<g:remoteLink class="section-action-button activity-btn btn" controller="quickMessage" action="create" onSuccess="launchMediumWizard('Quick Message', data, 'Send', true);" id="quick_message">
			<div id="quick-message"><g:message code="fmessage.quickmessage" /></div>
		</g:remoteLink>
	</li>
	<g:if test="${params.controller!='archive'}">
		<li><g:link class="activity-btn btn" controller="${ownerInstance?.type}" action="archive" id="${ownerInstance?.id}"><g:message code="fmessage.activity.archive" args="${ [ownerInstance?.type] }" /></g:link></li>
	</g:if>
	<g:else>
		<li><g:link class="activity-btn btn" controller="${ownerInstance?.type}" action="unarchive" id="${ownerInstance?.id}"><g:message code="fmessage.unarchive" args="${ [ownerInstance?.type] }" /></g:link></li>
	</g:else>
	<li><g:render template="/message/activity_more_actions" plugin="${grailsApplication.config.frontlinesms2.plugin}"/></li>
</ul>
<g:if test="${ownerInstance.type == 'poll'}">
	<a id='poll-graph-btn' class='show-arrow'><g:message code="fmessage.showpolldetails" /></a>
</g:if>
