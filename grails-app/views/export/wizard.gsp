<%@ page contentType="text/html;charset=UTF-8" %>
<div>
	<g:form name="export-form" controller="export" action="downloadReport">
		<g:hiddenField name="messageSection" value="${messageSection}" />
		<g:hiddenField name="ownerId" value="${ownerId}" />
		<g:hiddenField name="searchId" value="${searchId}" />
		<g:hiddenField name="starred" value="${starred}" />
		<g:hiddenField name="viewingArchive" value="${viewingArchive}" />
		<g:hiddenField name="failed" value="${failed}" />

		<p class="info">To export messages from FrontlineSMS, choose the type of export and the information to be included in the exported data.</p>
			<div>
				<h2>Select an output format:</h2>
				<input type="radio" name="format" value="csv" checked="checked" /> CSV format for use in spreadsheet<br />
				<input type="radio" name="format" value="pdf" /> PDF format for printing
			</div>
	</g:form>
</div>
<script>
function updateExportInfo() {
	$(".ui-dialog-title").html("Export Messages: ${reportName}");
}
</script>
