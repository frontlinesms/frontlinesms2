<%@ page import="frontlinesms2.*" %>
<g:hiddenField name="starred" value="${params.starred}"/>
<g:hiddenField name="failed" value="${params.failed}"/>
<g:hiddenField name="inbound" value="${params.inbound}"/>

<div class="content ${ownerInstance? ownerInstance.shortName + ' activity': messageSection}">
	<g:if test="${ownerInstance && messageSection != 'trash'}">
		<fsms:render template="/activity/${ownerInstance.shortName}/list_head"/>
		<fsms:render template="/message/activity_buttons"/>
	</g:if>
	<g:else>
		<h1 class="${messageSection}">
			<g:message code="fmessage.section.${messageSection}"/>
		</h1>
		<fsms:render template="/message/section_action_buttons"/>
	</g:else>
</div>

