<%@ page import="frontlinesms2.FrontlinesyncFconnection" %>
<div class="config-table-container">
	<p class="api-instructions-title"><g:message code="frontlinesync.api.title"/></p>

	<div class="config-table">
		<div class="config-values">
			<div class="connection-config-container">
				<div class="connection-config">
					<div class="field-label"><g:message code="frontlinesync.api.connection.id.label"/></div>
					<div class="field-value database-config-value connection-id">${c.id}</div>
				</div>
				<div class="icon"><i class="fa fa-android"></i></div>
			</div>
			<div class="connection-config-container">
				<div class="connection-config">
					<div class="field-label"><g:message code="frontlinesync.api.connection.secret.label"/></div>
					<div class="field-value database-config-value connection-secret">${c.secret}</div>
				</div>
				<div class="icon"><i class="fa fa-lock"></i></div>
			</div>
		</div>
	</div>
</div>

<div class="sync-config-status-container">
	<i class="expand fa fa-chevron-circle-right"></i>
	<a class="sync-config-status-toggler" href="#" onclick="frontlinesync.toggleOptions(${c.id})"><g:message code="frontlinesync.connection.options.label"/></a>
	<span class='sync-config-status'>
		${message(code:"frontlinesync.sync.config.dirty."+(!c.configSynced))}
	</span>
</div>

<div class="sync-config-container">
	<g:formRemote name="sync-config-form" url="${[controller:'frontlinesync', action:'update', params:[id:c.id]]}" before="frontlinesync.beforeUpdate()" after="frontlinesync.afterUpdate(${c.id})">
		<div class="synced-config">
			<g:checkBox name="sendEnabled" checked="${c.sendEnabled}"/>
			<label for="sendEnabled"><g:message code="frontlinesync.sendEnabled.sync.config.label"/></label>
		</div>
		<div class="synced-config">
			<g:checkBox name="receiveEnabled" checked="${c.receiveEnabled}"/>
			<label for="receiveEnabled"><g:message code="frontlinesync.receiveEnabled.sync.config.label"/></label>
		</div>
		<div class="synced-config">
			<g:checkBox name="missedCallEnabled" checked="${c.missedCallEnabled}"/>
			<label for="missedCallEnabled"><g:message code="frontlinesync.missedCallEnabled.sync.config.label"/></label>
		</div>
		<div class="synced-config frequency">
			<label for="syncFrequency">Check frequency: <em class="syncFrequencyValue">${g.message(code: 'frontlinesync.syncOption.' + (c.checkIntervalMinutes ?: 'manual'))}</em></label>
			<input name="syncFrequency" value="${c.getSyncFrequencyOptionListIndex()}" type="text" data-slider="true" data-slider-snap="true" data-slider-values="${(0..(FrontlinesyncFconnection.syncFrequencyOptions.size() - 1)).join(',')}">
		</div>
		<div>
			<g:submitButton class="btn" name="sync-config-button" controller="frontlinesync" action="update" value="${message(code:'frontlinesync.sync.config.button')}"/>
		</div>
	</g:formRemote>
</div>
<g:javascript>
if(typeof frontlineSyncCheckSettingOptions == 'undefined') {
	frontlineSyncCheckSettingOptions = [];
	<g:each in="${FrontlinesyncFconnection.syncFrequencyOptions}" var="minuteVal">
		frontlineSyncCheckSettingOptions.push({
			'valueInMinutes': ${minuteVal},
			'i18n': "${g.message(code: 'frontlinesync.syncOption.' + (minuteVal ?: 'manual'))}"
		});
	</g:each>
}
</g:javascript>

