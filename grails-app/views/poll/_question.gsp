<div id="tabs-1">
	<div class="section">
		<div>
			<h3>Select the kind of poll to create</h3>
			<div>
				<g:radio name="poll-type" value="standard"/>Question with a 'Yes' or 'No' answer
			</div>
			<div>
				<g:radio name="poll-type" value="multiple"/>Multiple choice question (e.g. 'Red', 'Blue', 'Green')
			</div>
			<label for='question'>Enter question:</label>
			<g:textArea name="question" id="question" value="" />
			
			<g:checkBox name="collect-responses" value="no-message" checked='false'/>Do not send a message for this poll(collect responses only)
		</div>
	</div>
</div>