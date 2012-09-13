<%@ page contentType="text/html;charset=UTF-8" %>
<meta name="layout" content="popup"/>
<div>
	<g:form name="export-form" controller="export" action="downloadMessageReport">
		<g:hiddenField name="messageSection" value="${messageSection}"/>
		<g:hiddenField name="ownerId" value="${ownerId}"/>
		<g:hiddenField name="searchId" value="${searchId}"/>
		<g:hiddenField name="starred" value="${starred}"/>
		<g:hiddenField name="viewingArchive" value="${viewingArchive}"/>
		<g:hiddenField name="failed" value="${failed}"/>

		<p class="info"><g:message code="export.message.info"/></p>
		<fsms:radioGroup name="format" values="csv, pdf" labels="export.csv, export.pdf" checked="csv"/>
	</g:form>
</div>
<r:script>
function updateExportInfo() {
	$(".ui-dialog-title").html("Export Messages: ${reportName}");
}
</r:script>
