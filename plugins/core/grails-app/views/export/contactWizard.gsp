<%@ page contentType="text/html;charset=UTF-8" %>
<div>
	<g:form name="export-form" controller="export" action="downloadContactReport">
		<g:hiddenField name="contactsSection" value="${contactsSection}" />
		<g:hiddenField name="groupId" value="${groupId}" />
		<p class="info">To export contacts from FrontlineSMS, choose the type of export and the information to be included in the exported data.</p>
			<div>
				<h2>Select an output format:</h2>
				<input type="radio" name="format" value="csv" checked="checked" /> CSV format for use in spreadsheet<br />
				<input type="radio" name="format" value="pdf" /> PDF format for printing
			</div>
	</g:form>
</div>
<script>
function updateExportInfo() {
	$(".ui-dialog-title").html("Export Contacts: ${reportName}");
}
</script>
