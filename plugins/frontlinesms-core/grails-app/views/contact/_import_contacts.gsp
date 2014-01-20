<g:uploadForm name="importForm" controller="import" action="importData" method="post">
	<fsms:radioGroup name="data" title="import.prompt.type"
			values="contacts.csv,contacts.vcf,messages"
			labelPrefix="import."
			checked="contacts.csv"/>
	<fsms:info message="import.contacts.info"/>
	<input type="file" name="importCsvFile" onchange="this.form.submit();" accept="text/csv,text/vcard,text/directory,.csv,.vcf"/>
	<label for="importCsvFile"><g:message code="import.prompt"/></label>
</g:uploadForm>