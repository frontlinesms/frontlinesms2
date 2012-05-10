<%@ page import="frontlinesms2.*" %>
<div class="section-header ${messageSection}" id="message-list-header">
	<g:hiddenField name="starred" value="${params.starred}"/>
	<g:hiddenField name="failed" value="${params.failed}"/>
	<g:if test="${messageSection == 'activity'}">
		<g:if test="${params.controller == 'archive' && viewingMessages}">
			<g:link controller="archive" action="${params.action}List"> 
				<g:message code="fmessage.archive.back"/>
			</g:link>
		</g:if>
		
		<g:if test="${ownerInstance instanceof Poll}">
			<h3 class="activity"><g:message code="poll.title"  args="${ [ownerInstance?.name] }"/></h3>
		</g:if>
		<g:if test="${ownerInstance instanceof Announcement}">
			<h3 class="activity"><g:message code="announcement.title"  args="${ [ownerInstance?.name] }"/></h3>
		</g:if>
		<g:if test="${ownerInstance instanceof Autoreply}">
			<h3 class="activity"><g:message code="autoreply.title"  args="${ [ownerInstance?.name] }"/></h3>
		</g:if>

		<g:if test="${ownerInstance}">
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
	<g:elseif test="${messageSection == message(code: 'folder.label')}">
		<h3 class="folder">${message(code: 'message.folder.header', args:[ownerInstance?.name])}</h3>
		<fsms:render template="/message/section_action_buttons"/>
	</g:elseif>
	<g:else>
		<h3 class="${messageSection}">${messageSection}</h3>
		<fsms:render template="/message/section_action_buttons"/>
	</g:else>
</div>
