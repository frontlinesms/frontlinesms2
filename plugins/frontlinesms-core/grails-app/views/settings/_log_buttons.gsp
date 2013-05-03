<ul class="buttons">
	<li>
		<label for="timePeriod" id="show-log-text"><g:message code="logs.filter.label"/></label>
		<select class="dropdown" name="timePeriod" onChange="filterLogEntries(this.value)">
			<option value="forever"><g:message code="logs.filter.anytime"/></option>
			<g:each in="[1,3,7,14,28]" var="i">
				<option value="${i}"><g:message code="logs.filter.days.${i}"/></option>
			</g:each>
		</select>
	</li>
	<li>
		<fsms:popup class="btn" controller="settings" action="logsWizard" id="downloadLogs" method="launchSmallPopup(i18n('logs.download.title'), data, i18n('logs.download.continue'))">
			<g:message code="logs.download.buttontext"/>
		</fsms:popup>
	</li>
</ul>

