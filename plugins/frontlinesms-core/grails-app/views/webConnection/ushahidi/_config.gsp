<% def isCrowdmap = !activityInstanceToEdit || serviceType == 'crowdmap'; %>
<div class="info">
	<p><g:message code="webConnection.ushahidi.description"/></p>
</div>
<div class="input">
	<label for="serviceType"><g:message code="webConnection.ushahidi.service.label"/></label>
	<ul class="select">
		<g:set var="serviceType" value="${activityInstanceToEdit?.serviceTypeType}"/>
		<li>
			<label for="serviceType"><g:message code="webConnection.ushahidi.serviceType.crowdmap"/></label>
			<g:radio name="serviceType" value="crowdmap" checked="${isCrowdmap}"/>
		</li>
		<li>
			<label for="serviceType"><g:message code="webConnection.ushahidi.serviceType.ushahidi"/></label>
			<g:radio name="serviceType" value="ushahidi" checked="${!isCrowdmap}"/>
		</li>
	</ul>
</div>
<div class="input">
	<label for="url" class="ushahidi${isCrowdmap?' hidden':''}"><g:message code="webConnection.ushahidi.url.label"/></label>
	<label for="url" class="crowdmap${isCrowdmap?'':' hidden'}"><g:message code="webConnection.crowdmap.url.label"/></label>
	<g:textField name="url" value="${activityInstanceToEdit?.url}" required="true"/>
	<label class="crowdmap${isCrowdmap?'':' hidden'}">.crowdmap.com</label>
</div>
<div class="input">
	<label for="key" class="ushahidi${isCrowdmap?' hidden':''}"><g:message code="webConnection.ushahidi.key.label"/></label>
	<label for="key" class="crowdmap${isCrowdmap?'':' hidden'}"><g:message code="webConnection.crowdmap.key.label"/></label>
	<g:textField name="key" value="${activityInstanceToEdit?.key}" required="true"/>
</div>

