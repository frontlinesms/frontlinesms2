<div class="input">
	<label for="name"><g:message code="announcement.prompt"/></label>
	<g:textField name="name" value="${activityInstanceToEdit?.name}"/>
</div>
<div class="confirm">
	<h2><g:message code="announcement.details.label"/></h2>
	<table>
		<tr>
			<td><g:message code="announcement.confirm.message"/>
			</td><td id="confirm-message-text"><g:message code="announcement.message.none"/></td>
		</tr>
		<tr>
			<td><g:message code="quickmessage.recipient.label"/></td>
			<td id="confirm-recipients-count"><span id="contacts-count">0</span> <g:message code="quickmessage.recipients.count"/></td>
		</tr>
		<tr>
			<td><g:message code="quickmessage.count.label"/></td>
			<td id="confirm-messages-count"><span id="messages-count"></span> <g:message code="quickmessage.messages.count"/></td>
		</tr>
	</table>
</div>

