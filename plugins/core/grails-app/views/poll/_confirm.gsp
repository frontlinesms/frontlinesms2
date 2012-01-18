<div id="tabs-7" class="confirm confirm-responses-tab">
	<div class="create-name">
		<h2 class="bold name-label">Name this poll:</h2>
		<g:textField name="title" class="name-field"></g:textField>
	</div>
	<div>
		<h2 class="bold">Confirm details</h2>
		<table>
			<tr>
				<td class="bold">Message:</td>
				<td id="poll-message"/>
			</tr>
			<tr>
				<td class="bold">Auto-sort:</td>
				<td id="auto-sort-confirm">
					<p>Messages will not be automatically sorted.</p>
					<p style="display:hidden">Sort messages with the keyword <span id="auto-sort-confirm-keyword">KEYWORD</span></p>
				</td>
			</tr>
			<tr>
				<td class="bold">Auto-reply:</td>
				<td id="auto-reply-read-only-text">None</div>
			</tr>
			<tr>
				<td class="bold">Recipients:</td>
				<td id="confirm-recepients-count">
					<span id="contacts-count">0</span> contacts selected
					(<span id="messages-count">0</span> messages will be sent)
				</td>
				<td id="no-recepients" class="hide">None</td>
			</tr>
		</table>
	</div>
</div>