<div class="input">
	<label for="name"><g:message code="poll.prompt"/></label>
	<g:textField name="name" value="${activityInstanceToEdit?.name}"/>
</div>
<div class="confirm">
	<h2><g:message code="poll.details.label"/></h2>
	<table>
		<tr>
			<td><g:message code="poll.message.label"/></td>
			<td id="poll-message"/>
		</tr>
		<tr>
			<td><g:message code="poll.sort.label"/></td>
			<td id="auto-sort-confirm">
				<p><g:message code="poll.autosort.no.description"/></p>
				<p style="display:hidden"><g:message code="poll.sort.by"/><span id="auto-sort-confirm-keyword"><g:message code="poll.sort.keyword"/></span></p>
			</td>
		</tr>
		<tr>
			<td><g:message code="poll.autoreply.label"/></td>
			<td id="auto-reply-read-only-text"><g:message code="poll.autoreply.none"/></div>
		</tr>
		<tr>
			<td><g:message code="poll.recipients.label"/></td>
			<td id="confirm-recipients-count">
				<span id="sending-messages">
					<span id="contacts-count">0</span> <g:message code="poll.recipients.count"/>
					(<span id="messages-count">0</span> <g:message code="poll.messages.count"/>)
				</span>
				<span id="no-recipients"><g:message code="poll.recipients.none"/></span>
			</td>
		</tr>
	</table>
</div>

