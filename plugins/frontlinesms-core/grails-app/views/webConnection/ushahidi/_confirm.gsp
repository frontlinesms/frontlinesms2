<div class="input">
	<label for="name"><g:message code="webConnection.name.prompt"/></label>
	<g:textField name="name" value="${activityInstanceToEdit?.name}"/>
</div>
<div class="confirm">
	<h2><g:message code="webConnection.details.label"/></h2>
	<table>
		<tr>
			<td><g:message code="webConnection.ushahidi.service.label"/></td>
			<td id="httpMethod-confirm"/>
		</tr>
		<tr>
			<td><g:message code="webConnection.url.label"/></td>
			<td id="url-confirm"/>
		</tr>
		<tr>
			<td><g:message code="webConnection.keyword.label"/></td>
			<td id="keyword-confirm"/>
		</tr>
		<tr>
			<td><g:message code="webConnection.parameters.confirm"/></td>
			<td id="requestParameters-confirm"/>
		</tr>
	</table>
</div>

