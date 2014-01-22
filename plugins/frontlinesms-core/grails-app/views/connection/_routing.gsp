<div id="routing-preferences">
	<h2><g:message code="routing.title"/></h2>
	<fsms:info message="routing.info"/>
	<div class="input"><p class="warning_message hidden"><g:message code="routing.rules.none-selected.warning"/></p></div>
	<g:form name="routing-form" url="[controller:'connection', action:'changeRoutingPreferences']">
		<g:hiddenField name="routingUseOrder" value=""/>
		<fsms:checkboxGroup label="routing.rule" title="routing.rules.sending" listClass="sortable checklist no-description">
			<g:each in="${fconnectionRoutingMap}" status="i" var="it">
				<g:if test="${!(it.key instanceof frontlinesms2.Fconnection)}">
					<li>
						<label for="routeRule-${i}" class="grabber">
							<g:message code="routing.rule.${it.key}"/>
							<g:checkBox name="routeRule-${i}" value="${it.key}" checked="${it.value}"/>
							<span class="progress"></span>
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
							<span class="progress"></span>
						</label>
					</li>
				</g:if>
			</g:each>
		</fsms:checkboxGroup>
	</g:form>
</div>
<g:javascript>
$(function() {
	routing.init();
});
</g:javascript>
