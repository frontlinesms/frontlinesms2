<div id="routing-preferences">
	<h2><g:message code="routing.title"/></h2>
	<fsms:info message="routing.info"/>
	<div class="input"><p class="warning_message"></p></div>
	<g:form name="routing-form" url="[controller:'connection', action:'changeRoutingPreferences']">
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
<r:script>
// FIXME please move this javascript to where it should be
$(function() {
	var checkedValues, warningObject, chkboxSelector;

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
			if ($(this).is(':checked')) {
				checkedValues++;
			} else {
				checkedValues--;
			}
		});
		checkboxChecker(checkedValues, warningObject);
	});

	function checkboxChecker(checkedValues, warningObject) {
		if (checkedValues === 0) {
			warningObject.html(i18n("routing.rules.none-selected.warning"));
			warningObject.show( "drop", { direction: "up" }, "slow");
		} else {
			warningObject.html("");
			warningObject.hide();
		}
	}
});
</r:script>

