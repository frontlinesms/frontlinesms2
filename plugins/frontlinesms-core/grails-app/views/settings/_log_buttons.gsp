<ul class="buttons">
	<li>
		<label for="timePeriod" id="show-log-text"><g:message code="logs.filter.label"/></label>
		<select class="dropdown" name="timePeriod" onChange="filterLogEntries(this.value)">
			<option value="forever"><g:message code="logs.filter.anytime"/></option>
			<option value="1"><g:message code="logs.filter.1day"/></option>
			<option value="3"><g:message code="logs.filter.3days"/></option>
			<option value="7"><g:message code="logs.filter.7days"/></option>
			<option value="14"><g:message code="logs.filter.14days"/></option>
			<option value="28"><g:message code="logs.filter.28days"/></option>
		</select>
	</li>
	<li>
		<fsms:popup class="btn" controller="settings" action="logsWizard" id="downloadLogs" method="launchSmallPopup(i18n('logs.download.title'), data, i18n('logs.download.continue'))">
			<g:message code="logs.download.buttontext"/>
		</fsms:popup>
	</li>
</ul>

