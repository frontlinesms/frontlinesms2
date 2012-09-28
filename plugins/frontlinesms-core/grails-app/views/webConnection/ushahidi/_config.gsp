<div class="info">
	<p><g:message code="webConnection.ushahidi.description"/></p>
</div>
<div class="input">
	<label for="serviceType"><g:message code="webConnection.ushahidi.service.label"/></label>
	<ul class="select">
		<g:set var="serviceType" value="${activityInstanceToEdit?.serviceType}"/>
		<li>
			<label for="serviceType"><g:message code="webConnection.ushahidi.serviceType.crowdmap"/></label>
			<g:radio name="serviceType" value="crowdmap" checked="${!activityInstanceToEdit || serviceType == 'crowdmap'}" />
		</li>
		<li>
			<label for="serviceType"><g:message code="webConnection.ushahidi.serviceType.ushahidi"/></label>
			<g:radio name="serviceType" value="ushahidi" checked="${activityInstanceToEdit && serviceType != 'ushahidi'}" />
		</li>
	</ul>
</div>
<div class="input">
	<label for="url"><g:message code="webConnection.ushahidi.url.label"/></label>
	<g:textField name="url" value="${activityInstanceToEdit?.url}" required="true"/>
	<label id="crowdmap-url-suffix"><g:message code="webConnection.crowdmap.url.suffix.label"/></label>
</div>
<div class="input">
	<label for="key"><g:message code="webConnection.ushahidi.key.label"/></label>
	<g:textField name="key" value="${activityInstanceToEdit?.key}" required="true"/>
</div>

<r:script>
	function updateServerConfiguration() {
		
	}
</r:script>
