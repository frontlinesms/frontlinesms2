<div class="input">
	<label for="url"><g:message code="webConnection.url"/></label>
	<g:textField name="url" value="${activityInstanceToEdit?.connection?.url}"/>
</div>
<div class="input">
	<label for="requestType"><g:message code="webConnection.request.type"/></label>
	<ul class="select">
		<g:set var="requestType" value="${activityInstanceToEdit?.yesNo}"/>
		<li>
			<label for="requestType"><g:message code="webConnection.request.get"/></label>
			<g:radio name="requestType" value="get" checked="${!activityInstanceToEdit || requestType}" disabled="${activityInstanceToEdit && !requestType}"/>
		</li>
		<li>
			<label for="requestType"><g:message code="webConnection.request.post"/></label>
			<g:radio name="requestType" value="post" checked="${activityInstanceToEdit && !requestType}" disabled="${activityInstanceToEdit && requestType}"/>
		</li>
	</ul>
</div>
<h2><g:message code="webConnection.parameters"/></h2>
<tr class="prop web-connection-criteria">
	<td>
		
	</td>
	<td>
		<g:textField name="rule-text" class="rule-text" value='${value}'/>
	</td>
	<td>
		<a onclick="removeRule(this)" class="remove-command" style="display:${isFirst?'none':'auto'}"/>
	</td>
</tr>

