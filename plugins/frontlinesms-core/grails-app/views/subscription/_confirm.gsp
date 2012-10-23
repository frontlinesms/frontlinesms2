<div class="input">
	<label for="name"><g:message code="subscription.name.prompt"/></label>
	<g:textField name="name" class="required" value="${activityInstanceToEdit?.name}"/>
</div>
<div class="confirm">
	<h2><g:message code="subscription.details.label"/></h2>
	<table>
		<tr>
			<td><g:message code="subscription.confirm.group"/>
			</td><td id="confirm-group-text"><g:message code="announcement.message.none"/></td>
		</tr>
		<tr>
			<td><g:message code="subscription.confirm.keyword"/>
			</td><td id="confirm-keyword-text"><g:message code="announcement.message.none"/></td>
		</tr>
		<tr>
			<td><g:message code="subscription.confirm.join.alias"/>
			</td><td id="confirm-join-alias-text"><g:message code="announcement.message.none"/></td>
		</tr>
		<tr>
			<td><g:message code="subscription.confirm.leave.alias"/>
			</td><td id="confirm-leave-alias-text"><g:message code="announcement.message.none"/></td>
		</tr>
		<tr>
			<td><g:message code="subscription.confirm.default.action"/>
			</td><td id="confirm-default-action-text"><g:message code="announcement.message.none"/></td>
		</tr>
		<tr>
			<td><g:message code="subscription.confirm.join.autoreply"/></td>
			<td id="confirm-join-autoreply-text"><g:message code="announcement.message.none"/></td>
		</tr>
		<tr>
			<td><g:message code="subscription.confirm.leave.autoreply"/></td>
			<td id="confirm-leave-autoreply-text"><g:message code="announcement.message.none"/></td>
		</tr>
	</table>
</div>
