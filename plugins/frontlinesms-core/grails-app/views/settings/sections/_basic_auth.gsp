<div id="basic-authentication">
	<h2><g:message code="auth.basic.label" /></h2>
	<fsms:info message="auth.basic.info"/>
	<g:form name="basic-auth" action="basicAuth" controller="settings">
		<fsms:inputs labelPrefix="auth.basic." table="true" submit="action.save"
				fields="enabled, username, password, confirmPassword"
				values="${[authEnabled, username, '', '']}"
				types="${['isBoolean', null, 'password', 'password']}"/>
	</g:form>
</div>