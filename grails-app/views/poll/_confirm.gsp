<div id="tabs-7" class="confirm confirm-responses-tab">
	<div>
		<h3><label for="title">Name this poll:</label></h3>
		<g:textField name="title" id="title"></g:textField>
	</div>
	<div>
		<h3>Confirm details:</h3>
		<table>
			<tr>
				<td><h3>Message: </h3></td>
				<td id="poll-question-text"/>
			</tr>
			<tr>
				<td><h3>Auto-sort: </h3></td>
				<td id="auto-sort-confirm">
					<p>Messages will not be automatically sorted.</p>
					<p style="display:hidden">Sort messages with the keyword <span id="auto-sort-confirm-keyword">KEYWORD</span></p>
				</td>
			</tr>
			<tr>
				<td><h3>Auto-reply: </h3></td>
				<td id="auto-reply-read-only-text">None</div>
			</tr>
			<tr>
				<td><h3>Recipients: </h3></td>
				<td id="confirm-recepients-count">
					<span id="contacts-count">0</span> contacts selected
					(<span id="messages-count">0</span> messages will be sent)
				</td>
			</tr>
		</table>
	</div>
</div>