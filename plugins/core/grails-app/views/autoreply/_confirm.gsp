<div id="tabs-3" class="confirm confirm-responses-tab">
	<div class="create-name">
		<h2 class="bold name-label"><g:message code="autoreply.confirm.name.prompt" /></h2>
		<g:textField name="name" class="name-field" value="${activityInstanceToEdit?.name}" />
	</div>
	<div>
		<h2 class="bold"><g:message code="common.confirm.details" /></h2>
		<table>
			<tr>
				<td class="bold"><g:message code="autoreply.confirm.keyword" /></td>
				<td id="keyword-confirm" />
			</tr>
			<tr>
				<td class="bold"><g:message code="common.confirm.message" /></td>
				<td id="autoreply-confirm"/>
			</tr>
		</table>
	</div>
</div>
