<div class="input">
	<label for="name"><g:message code="customactivity.name.prompt"/></label>
	<g:textField name="name" value="${activityInstanceToEdit?.name}"/>
</div>
<div class="confirm">
	<h2><g:message code="announcement.details.label"/></h2>
	<table>
		<tr>
			<td><g:message code="autoreply.keyword.label"/></td>
			<td id="keyword-confirm"/>
		</tr>
		<tr>
			<td><g:message code="customactivity.action.steps.label"/></td>
			<td id="customactivity-confirm-action-steps"/>
		</tr>
	</table>
</div>

