<div class="frontlinesync-edit-form">
	<p><g:message code="frontlinesync.info-setup"/></p>
	<br/>
	<div class="input-item">
		<label><g:message code="frontlinesync.name.label"/></label>
		<%
			def connectionsCount = frontlinesms2.FrontlinesyncFconnection.countByNameLike("%FrontlineSync%")
			def suffix = connectionsCount?"($connectionsCount)" :''
			def connectionName = fconnectionInstance?.name?:'FrontlineSync '+ suffix
		%>
		<g:textField name="frontlinesyncname" value="${connectionName}"/>
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
