<p class="api-instructions-title"><g:message code="frontlinesync.api.title"/></p>

<div class="config-table">
	<div class="config-values">
		<div class="connection-config">
			<div class="field-label"><g:message code="frontlinesync.api.connection.id.label"/></div>
			<div class="field-value database-config-value connection-id">${c.id}</div>
		</div>
		<div class="connection-config">
			<div class="field-label"><g:message code="frontlinesync.api.connection.secret.label"/></div>
			<div class="field-value database-config-value connection-secret">${c.secret}</div>
		</div>
		<div class="connection-config api-url">
			<div class="field-label"><g:message code="frontlinesync.api.url.label"/></div>
			<div class="field-value connection-url">${c.getFullApiUrl(request)}</div>
		</div>
	</div>
<div>
