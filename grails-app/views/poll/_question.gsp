<div id="tabs-1">
	<div class="section">
		<div>
			<h3>Select the kind of poll to create</h3>
			<div>
				<g:radio name="poll-type" value="standard" checked='checked'/>Question with a 'Yes' or 'No' answer
			</div>
			<div>
				<g:radio name="poll-type" value="multiple"/>Multiple choice question (e.g. 'Red', 'Blue', 'Green')
			</div>
			<div>
				<label for='question'>Enter question:</label>
				<g:textArea name="question"/>
			</div>
			<g:checkBox name="collect-responses" value="no-message" checked='false'/>Do not send a message for this poll(collect responses only)
		</div>
	</div>
</div>
<g:javascript>
	$("input[name='collect-responses']").live("change", function() {
		if(isGroupChecked("collect-responses")) {
			disableTab(4);
		} else {
			enableTab(4);
		}
	});

	$("input[name='poll-type']").live("change", function() {
		if ($("input[name='poll-type']:checked").val() == "standard") {
			disableTab(1);
		} else {
			enableTab(1);
		}
		updateConfirmationMessage();
	});
</g:javascript>
