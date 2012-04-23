<%@ page import="frontlinesms2.*" %>
<ul class="header-buttons">
	<li>
		<g:remoteLink class="section-action-button activity-btn btn" controller="quickMessage" action="create" onSuccess="launchMediumWizard('Quick Message', data, 'Send', true);" id="quick_message">
			<div id="quick-message"><g:message code="fmessage.quickmessage" /></div>
		</g:remoteLink>
	</li>
	<g:if test="${params.controller!='archive'}">
		<li><g:link class="activity-btn btn" controller="${ownerInstance?.shortName}" action="archive" id="${ownerInstance?.id}"><g:message code="fmessage.activity.archive" args="${[ownerInstance?.shortName]}" /></g:link></li>
	</g:if>
	<g:else>
		<li><g:link class="activity-btn btn" controller="${ownerInstance?.shortName}" action="unarchive" id="${ownerInstance?.id}"><g:message code="fmessage.unarchive" args="${[ownerInstance?.shortName]}" /></g:link></li>
	</g:else>
	<li><g:render template="/message/activity_more_actions" plugin="${grailsApplication.config.frontlinesms2.plugin}"/></li>
	<g:if test="${ownerInstance instanceof Poll}">
		<li><a id='poll-graph-btn' class='show-arrow'><g:message code="fmessage.showpolldetails" /></a></li>
	</g:if>
</ul>
