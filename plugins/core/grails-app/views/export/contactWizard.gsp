<%@ page contentType="text/html;charset=UTF-8" %>
<div>
	<g:form name="export-form" controller="export" action="downloadContactReport">
		<g:hiddenField name="contactsSection" value="${contactsSection}" />
		<g:hiddenField name="groupId" value="${groupId}" />
		<p class="info"><g:message code="export.contact.info" /></p>
			<div>
				<h2><g:message code="export.selectformat" /></h2>
				<input type="radio" name="format" value="csv" checked="checked" /> <g:message code="export.csv.format" /><br />
				<input type="radio" name="format" value="pdf" /> <g:message code="export.pdf.format" />
			</div>
	</g:form>
</div>
<script>
function updateExportInfo() {
	$(".ui-dialog-title").html("Export Contacts: ${reportName}");
}
</script>
