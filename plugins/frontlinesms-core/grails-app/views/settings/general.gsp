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
			<fsms:render template="/settings/sections/language"/>
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

			<fsms:render template="/settings/sections/database_backup"/>
			<fsms:render template="/settings/sections/basic_auth"/>

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
									<label for="routeRule-${i}" class="grabber">
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
									<label for="routeRule-${i}" class="grabber">										
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
