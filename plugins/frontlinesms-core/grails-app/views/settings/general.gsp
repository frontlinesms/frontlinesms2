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
				<g:form name="routing-form" url="[controller:'settings', action:'changeRoutingPreferences']">
					<g:hiddenField name="routingUseOrder" value=""/>
					<fsms:checkboxGroup label="routing.rule" title="routing.rules.sending" listClass="sortable checklist no-description">
						<g:each in="${fconnectionRoutingMap}" status="i" var="it">
							<li>
								<label for="routeRule-${i}">
									<g:if test="${!(it.key instanceof frontlinesms2.Fconnection)}">
										<g:message code="routing.rule.${it.key}"/>
										<g:checkBox name="routeRule-${i}" value="${it.key}" checked="${it.value}"/>
									</g:if>
									<g:else>
										<g:message code="routing.rules.device" args="[it.key.name]" />
										<g:checkBox name="routeRule-${i}" value="fconnection-${it.key.id}" checked="${it.value}"/>
									</g:else>
								</label>
							</li>
						</g:each>
					</fsms:checkboxGroup>
					<fsms:radioGroup name="otherwise" title="routing.rules.otherwise"
							values="any,dontsend"
							labels="${g.message(code:'routing.rule.useany')}, ${g.message(code:'routing.rule.dontsend')}"
							checked="${appSettings['routing.otherwise']}"/>
					<g:submitButton name="saveRoutingDetails" class="btn" value="${message(code:'action.save')}" />
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
