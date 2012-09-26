<div class="info">
	<p><g:message code="webConnection.ushahidi.description"/></p>
</div>
<div class="input">
	<label for="httpMethod"><g:message code="webConnection.ushahidi.service.label"/></label>
	<ul class="select">
		<g:set var="service" value="${activityInstanceToEdit?.httpMethod}"/>
		<li>
			<label for="service"><g:message code="webConnection.ushahidi.service.crowdmap"/></label>
			<g:radio name="service" value="crowdmap" checked="${!activityInstanceToEdit || service == 'crowdmap'}" />
		</li>
		<li>
			<label for="service"><g:message code="webConnection.ushahidi.service.ushahidi"/></label>
			<g:radio name="service" value="ushahidi" checked="${activityInstanceToEdit && service != 'ushahidi'}" />
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
