<%@ page import="frontlinesms2.*" %>
<div class="header-buttons">
	<g:if test='${ownerInstance?.shortName=='subscription'}'>
		<fsms:quickMessage class="section-action-button activity-btn btn" groupList="${ownerInstance?.group?.id?:''}"/>
	</g:if>
	<g:else>
		<fsms:quickMessage class="section-action-button activity-btn btn"/>
	</g:else>
	<g:if test="${params.controller!='archive'}">
		<g:link class="activity-btn btn" controller="${ownerInstance?.shortName}" action="archive" id="${ownerInstance?.id}"><g:message code="fmessage.activity.archive" args="${[ownerInstance?.shortName]}"/></g:link>
	</g:if>
	<g:else>
		<g:link class="activity-btn btn" controller="${ownerInstance?.shortName}" action="unarchive" id="${ownerInstance?.id}"><g:message code="fmessage.unarchive" args="${[ownerInstance?.shortName]}"/></g:link>
	</g:else>
	<g:select class="dropdown more-actions activity-btn" name="more-actions"  
			from="${['export', 'rename', 'delete'] + (ownerInstance.editable?['edit']:[]) + ownerInstance.moreActions}"
			noSelection="${['': g.message(code:'fmessage.moreactions')]}"
			valueMessagePrefix="${ownerInstance.shortName}.moreactions"/>
</div>
