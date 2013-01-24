<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings"/>
		<title><g:message code="settings.general.header"/></title>
		<export:resource/>
	</head>
	<body>
		<div id="body-content-head">
			<h1><g:message code="layout.settings.header"/></h1>
		</div>
		<div id="body-content">
			<div id="language">
				<h2><g:message code="language.label"/></h2>
				<fsms:info message="language.prompt"/>
				<g:form action="selectLocale" method="post">
					<g:select class="dropdown" name="language"
							from="${languageList}"
							optionKey="key" optionValue="value"
							noSelection="[currentLanguage:languageList[currentLanguage]?:'English']"
							onchange="\$(this).parent().submit()" />
				</g:form>
				<div class="clearfix"></div>
			</div>
			<div id="import">
				<h2><g:message code="import.label"/></h2>
				<fsms:info message="import.backup.label"/>
				<g:uploadForm name="importForm" controller="import" action="importData" method="post">
					<fsms:radioGroup name="data" title="import.prompt.type"
							values="contacts,messages"
							labelPrefix="import."
							checked="contacts"/>
					<fsms:info message="import.version1.info"/>
					<input type="file" name="importCsvFile" onchange="this.form.submit();"/>
					<label for="importCsvFile"><g:message code="import.prompt"/></label>
				</g:uploadForm>
			</div>
			<div id="database-backup">
				<h2><g:message code="configuration.location.title"/></h2>
				<p><g:message code="configuration.location.instructions" args="${['file://'+frontlinesms2.ResourceUtils.resourcePath, frontlinesms2.ResourceUtils.resourcePath]}"/></p>
			</div>
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
			<div id="routing-preferences">
				<h2><g:message code="routing.title"/></h2>
				<fsms:info message="routing.info"/>
				<g:form name="routing-form" controller="settings" action="changeRoutingPreferences">
					<fsms:checkboxGroup label="routing.rule" title="routing.rules.sending" id="sortable"> 
						<g:each in="${routingRulesMap}">
							<g:if test="${!(it.key instanceof frontlinesms2.Fconnection)}">
								<li>
									<label>
										<g:message code="routing.rule.${it.key}"/>
									</label>
									<g:checkBox name="${it.key}" values="${it.key}" checked="${it.value}"/>
								</li>
							</g:if>
							<g:else>
								<li>
									<label>
										<g:message code="routing.rules.device" args="[it.key.name]" />
									</label>
									<g:checkBox name="fconnection-${it.key.id}" values="it.key.id" checked="${it.value}"/>
								</li>
							</g:else>
						</g:each>
					</fsms:checkboxGroup>
					<fsms:radioGroup name="otherwise" title="routing.rules.otherwise"
							values="any,dontsend"
							labels="${g.message(code:'routing.rule.useany')}, ${g.message(code:'routing.rule.dontsend')}"
							checked="${appSettings['routing.otherwise']}"/>
					<g:submitButton name="saveRoutingDetails" class="btn" value="${message(code:'action.save')}"/>
				</g:form>
			</div>
		</div>
	</body>
</html>

<r:script>
$(function() {
	basicAuthValidation.enable();
	$("#basic-authentication input[name=enabled]").attr("onchange", "basicAuthValidation.toggleFields(this)");
	$("#basic-authentication input[type=submit]").attr("onclick", "basicAuthValidation.showErrors()");
});
</r:script>
