<div class="frontlinesync-edit-form">
	<p><g:message code="frontlinesync.info-setup"/></p>
	<br/>
	<div class="input-item">
		<label><g:message code="frontlinesync.name.label"/></label>
		<g:textField name="frontlinesyncname" value="${fconnectionInstance?.name?:'FrontlineSync'}"/>
	</div>
	<br/>
	<p><g:message code="frontlinesync.passcode-setup"/></p>
	<br/>
	<div class="frontlinesync-passcode">
		<fsms:frontlineSyncPasscode connection="${fconnectionInstance}"/>
	</div>
	<br/>
	<p><g:message code="frontlinesync.final-setup"/></p>
</div>
