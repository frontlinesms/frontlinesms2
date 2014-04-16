<g:uploadForm name="importForm" controller="import" action="importData" method="post">
	<p class="csvDisabledNotification">
		<g:message code="import.prompt.csv.warning"/>
		<a href="https://frontlinecloud.zendesk.com/entries/24634062-Importing-Contacts"><g:message code="import.prompt.csv.moreInfo"/></a>
	</p>
	<fsms:radioGroup name="data" title="import.prompt.type"
			values="contacts.csv,contacts.vcf,messages"
			disabledValues="contacts.csv"
			labelPrefix="import."
			checked="contacts.vcf"/>
	<fsms:info message="import.contacts.info"/>
	<input type="file" name="contactImportFile" onchange="showThinking();this.form.submit();" accept="text/csv,text/vcard,text/directory,.csv,.vcf"/>
	<label for="contactImportFile"><g:message code="import.prompt"/></label>
</g:uploadForm>
