<% def isCrowdmap = !activityInstanceToEdit || activityInstanceToEdit?.serviceType == 'crowdmap'; %>
<div class="info">
	<p><g:message code="webconnection.ushahidi.description"/></p>
	<p><g:message code="webconnection.ushahidi.key.description"/></p>
</div>
<div class="input">
	<label for="serviceType"><g:message code="webconnection.ushahidi.service.label"/></label>
	<ul class="select">
		<g:set var="serviceType" value="${activityInstanceToEdit?.serviceType}"/>
		<li>
			<label for="serviceType"><g:message code="webconnection.ushahidi.serviceType.crowdmap"/></label>
			<g:radio name="serviceType" value="crowdmap" checked="${isCrowdmap}"/>
		</li>
		<li>
			<label for="serviceType"><g:message code="webconnection.ushahidi.serviceType.ushahidi"/></label>
			<g:radio name="serviceType" value="ushahidi" checked="${!isCrowdmap}"/>
		</li>
	</ul>
</div>
<div class="input">
	<label for="url" class="ushahidi${isCrowdmap? ' hidden':''}"><g:message code="webconnection.ushahidi.url.label"/></label>
	<label for="url" class="crowdmap${isCrowdmap?'':' hidden'}"><g:message code="webconnection.crowdmap.url.label"/></label>
	<g:textField name="displayed_url" value="${activityInstanceToEdit ? (isCrowdmap? (activityInstanceToEdit.url - 'https://' - '.crowdmap.com/frontlinesms/') : activityInstanceToEdit.url ) : ''}" required="true"/>
	<g:hiddenField name="url" value="${activityInstanceToEdit?.url}" />
	<label for='url' class="crowdmap${isCrowdmap?'':' hidden'} suffix">.crowdmap.com</label>
</div>
<div class="input"> 
	<label for="key" class="ushahidi${isCrowdmap?' hidden':''}"><g:message code="webconnection.ushahidi.key.label"/></label>
	<label for="key" class="crowdmap${isCrowdmap?'':' hidden'}"><g:message code="webconnection.crowdmap.key.label"/></label>
	<g:textField name="key" value="${activityInstanceToEdit?.key}" required="true"/>
</div>

