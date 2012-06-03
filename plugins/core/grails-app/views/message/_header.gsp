<%@ page import="frontlinesms2.*" %>
<g:hiddenField name="starred" value="${params.starred}"/>
<g:hiddenField name="failed" value="${params.failed}"/>

<g:if test="${ownerInstance}">
	<h1 class="${ownerInstance.shortName}">
		<g:message code="fmessage.section.${ownerInstance.shortName}" args="${[ownerInstance.name]}"/>
	</h1>
	<fsms:render template="/activity/${ownerInstance.shortName}/list_head"/>
</g:if>
<g:else>
	<h1 class="${messageSection}">
		<g:message code="fmessage.section.${messageSection}"/>
	</h1>
	<fsms:render template="/message/section_action_buttons"/>
</g:else>

<g:if test="${false}">
	The following is kept for quick reference when recreating... shortly ;¬)
	<g:if test="${messageSection == 'activity'}">
		<g:if test="${params.controller == 'archive' && viewingMessages}">
			<g:link controller="archive" action="${params.action}List"> 
				<g:message code="fmessage.archive.back"/>
			</g:link>
		</g:if>

		<g:if test="${ownerInstance}">
			<h3 class="activity"><g:message code="${ownerInstance.shortName}.title"  args="${[ownerInstance.name]}"/></h3>
			<fsms:render template="/message/activity_buttons"/>
		</g:if>
		<div id="activity-details" class='section-details'>
			<g:if test="${ownerInstance instanceof Poll}">
				<fsms:render template="/message/poll_header"/>
			</g:if>
			<g:else>
				<g:formatDate date="${ownerInstance?.dateCreated}"/>
				<g:if test="${ownerInstance instanceof Announcement}">
					<span id="announcement-sent"><g:message code="fmessage.activity.sentmessage" args="${ [sentMessageCount] }"/></span>
					<p>${ownerInstance.sentMessageText}</p>
				</g:if>
				<g:else>
					<p>${ownerInstance?.autoreplyText}</p>
				</g:else>
			</g:else>
		</div>
	</g:if>
	<g:elseif test="${messageSection == 'folder'}">
		<h3 class="folder">${message(code: 'message.folder.header', args:[ownerInstance?.name])}</h3>
		<fsms:render template="/message/section_action_buttons"/>
	</g:elseif>
	<g:else>
		<h3 class="${messageSection}">${message(code:'fmessage.'+messageSection)}</h3>
		<fsms:render template="/message/section_action_buttons"/>
	</g:else>
</g:if>

