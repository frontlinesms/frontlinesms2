<div id="tabs-7" class="confirm confirm-responses-tab">
	<div class="create-name">
		<h2 class="bold name-label"><g:message code="poll.prompt"/></h2>
		<g:textField name="name" class="name-field" value="${activityInstanceToEdit?.name}"/>
	</div>
	<div>
		<h2 class="bold"><g:message code="poll.details.label"/></h2>
		<table>
			<tr>
				<td class="bold"><g:message code="poll.message.label"/></td>
				<td id="poll-message"/>
			</tr>
			<tr>
				<td class="bold"><g:message code="poll.sort.label"/></td>
				<td id="auto-sort-confirm">
					<p><g:message code="poll.no.sort"/></p>
					<p style="display:hidden"><g:message code="poll.sort.by"/><span id="auto-sort-confirm-keyword"><g:message code="poll.sort.keyword"/></span></p>
				</td>
			</tr>
			<tr>
				<td class="bold"><g:message code="poll.autoreply.label"/></td>
				<td id="auto-reply-read-only-text"><g:message code="poll.autoreply.none"/></div>
			</tr>
			<tr>
				<td class="bold"><g:message code="poll.recipients.label"/></td>
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
</div>
