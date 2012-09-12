<div class="input">
	<label for="name"><g:message code="autoreply.name.prompt"/></label>
	<g:textField name="name" value="${activityInstanceToEdit?.name}"/>
</div>
<div class="confirm">
	<h2><g:message code="autoreply.details.label"/></h2>
	<table>
		<tr>
			<td><g:message code="autoreply.keyword.label"/></td>
			<td id="keyword-confirm"/>
		</tr>
		<tr>
			<td><g:message code="autoreply.name.label"/></td>
			<td id="autoreply-confirm"/>
		</tr>
	</table>
</div>

