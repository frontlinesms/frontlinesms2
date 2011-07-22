<%@ page contentType="text/html;charset=UTF-8" %>
<script type="text/javascript">
function closeWizard() {
	$("#modalBox").dialog().remove();
}
</script>
<div>
	<g:form name="export-form" action="downloadReport">
		<g:hiddenField name="messageSection" value="${messageSection}" />
		<g:hiddenField name="ownerId" value="${ownerId}" />
		<g:hiddenField name="searchString" value="${searchString}" />
		<g:hiddenField name="groupInstance" value="${groupId}" />
		<g:hiddenField name="activityId" value="${activityId}" />
		<p>To export messages from FrontlineSMS, choose the type of export and the information to be included in the exported data.</p>
			<div>
				<h2>Select an output format:</h2>
				<input type="radio" name="format" value="csv" checked="checked" /> CSV format for use in spreadsheet<br />
				<input type="radio" name="format" value="pdf" /> PDF format for printing
			</div>
			<div class="buttons">
				<g:link class="cancel" controller="message" action="inbox" >Cancel</g:link>
				<input type="submit" value="Export" class="submit" onclick="closeWizard()" />
			</div>
	</g:form>
</div>
