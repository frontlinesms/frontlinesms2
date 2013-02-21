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
				<fsms:info message="configuration.location.description"/>
				<p><g:message code="configuration.location.instructions"/></p>
				<% def loc = frontlinesms2.ResourceUtils.resourcePath %>
				<input type="text" value="${loc}" size="${loc.size()}"/>
				<a href="file://${loc}"><g:message code="action.view"/></a>
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
				<div class="input"><p class="warning_message"></p></div>
				<g:form name="routing-form" url="[controller:'settings', action:'changeRoutingPreferences']">
					<g:hiddenField name="routingUseOrder" value=""/>
					<fsms:checkboxGroup label="routing.rule" title="routing.rules.sending" listClass="sortable checklist no-description">
						<g:each in="${fconnectionRoutingMap}" status="i" var="it">						
							<g:if test="${!(it.key instanceof frontlinesms2.Fconnection)}">
								<li>
									<label for="routeRule-${i}">
										<span class="grabber"></span>
										<g:message code="routing.rule.${it.key}"/>
										<g:checkBox name="routeRule-${i}" value="${it.key}" checked="${it.value}"/>
									</label>
								</li>
							</g:if>		
						</g:each>
					</fsms:checkboxGroup>
					<fsms:checkboxGroup label="routing.rule" title="routing.rules.not_selected" listClass="sortable checklist no-description">
						<g:each in="${fconnectionRoutingMap}" status="i" var="it">					
							<g:if test="${(it.key instanceof frontlinesms2.Fconnection)}">
								<li>
									<label for="routeRule-${i}">										
										<span class="grabber"></span>
										<g:message code="routing.rules.device" args="[it.key.name]" />										
										<g:checkBox name="routeRule-${i}" value="fconnection-${it.key.id}" checked="${it.value}"/>
									</label>
								</li>
							</g:if>		
						</g:each>
					</fsms:checkboxGroup>
					<g:submitButton name="saveRoutingDetails" class="btn" value="${message(code:'action.save')}" />		
				</g:form>
			</div>
		</div>
	</body>
</html>

<r:script>
$(function() {
	var checkedValues, warningObject, chkboxSelector;
	basicAuthValidation.enable();
	$("#basic-authentication input[name=enabled]").attr("onchange", "basicAuthValidation.toggleFields(this)");
	$("#basic-authentication input[type=submit]").attr("onclick", "basicAuthValidation.showErrors()");

	checkedValues = 0;
	chkboxSelector = 'input[name^="routeRule"]';
	warningObject = $(".warning_message");
	warningObject.hide();

	$(chkboxSelector).each(function() {
		if ($(this).is(':checked')) { checkedValues++; }
	});
	checkboxChecker(checkedValues, warningObject);
	$(chkboxSelector).change(function() {
		$($(this)).each(function() {
			if ($(this).is(':checked')) { checkedValues++; }
			else { checkedValues--; }
		});
		checkboxChecker(checkedValues, warningObject);
	});

	function checkboxChecker(checkedValues, warningObject){
		if (checkedValues === 0) { warningObject.html("Warning: You have no rules or phone numbers selected.No messages will be sent. If you wish to send messages, please enable one of the above options").show( "drop", { direction: "up" }, "slow"); 
		} else { warningObject.html("").hide(); }
}
});

</r:script>
