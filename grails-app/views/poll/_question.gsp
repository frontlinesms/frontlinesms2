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
			<label for='question'>Enter question:</label>
			<g:textArea name="question" id="question" value="" />
			
			<g:checkBox name="collect-responses" value="no-message" checked='false'/>Do not send a message for this poll(collect responses only)
		</div>
	</div>
</div>

<g:javascript>
	$("input[name='collect-responses']").live("change", function() {
		if(isGroupChecked("collect-responses")) {
			$('#tabs').tabs("disable", 4);
			$('#recipientsTab-text a').css('color', '#DFDFDF'); // FIXME should be doing this with CSS class reflecting enabled/disabled rather than directly modifying style
		} else {
			$('#tabs').tabs("enable", 4);
			$('#recipientsTab-text a').css('color', '#333333'); // FIXME should be doing this with CSS class reflecting enabled/disabled rather than directly modifying style
		}
	});

	$("input[name='poll-type']").live("change", function() {
		if ($("input[name='poll-type']:checked").val() == "standard") {
			$('#tabs').tabs("disable", 1);
			$('#responseTab-text a').css('color', '#DFDFDF'); // FIXME should be doing this with CSS class reflecting enabled/disabled rather than directly modifying style
		} else {
			$('#tabs').tabs("enable", 1);
			$('#responseTab-text a').css('color', '#333333'); // FIXME should be doing this with CSS class reflecting enabled/disabled rather than directly modifying style
		}
		updateConfirmationMessage();
	});
	
	// TODO refactor above repeated code
</g:javascript>
