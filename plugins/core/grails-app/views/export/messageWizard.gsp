<%@ page contentType="text/html;charset=UTF-8" %>
<div>
	<g:form name="export-form" controller="export" action="downloadMessageReport">
		<g:hiddenField name="messageSection" value="${messageSection}" />
		<g:hiddenField name="ownerId" value="${ownerId}" />
		<g:hiddenField name="searchId" value="${searchId}" />
		<g:hiddenField name="starred" value="${starred}" />
		<g:hiddenField name="viewingArchive" value="${viewingArchive}" />
		<g:hiddenField name="failed" value="${failed}" />

		<p class="info"><g:message code="export.message.info" /></p>
			<div>
				<h2><g:message code="export.selectformat" /></h2>
				<input type="radio" name="format" value="csv" checked="checked" /> <g:message code="export.csv" /><br />
				<input type="radio" name="format" value="pdf" /> <g:message code="export.pdf" />
			</div>
	</g:form>
</div>
<script>
function updateExportInfo() {
	$(".ui-dialog-title").html("Export Messages: ${reportName}");
}
</script>
