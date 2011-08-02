<div id="tabs-3">
	<div class="error-panel"></div>
	<h3>
		Reply automatically to poll responses (optional)
	</h3>
	<div class="info">
		When an incoming message is identified as a poll response, send a
		message to the person who sent the response.
	</div>
	<g:checkBox name="auto-reply" id="send_auto_reply_check">Send an automatic reply to poll responses</g:checkBox>
	<g:textArea name="autoReplyText" class="check-bound-text-area" checkbox_id="send_auto_reply_check" rows="5" cols="40"></g:textArea>
	<g:link url="#" class="back">Back</g:link>
	<g:link url="#" class="next-validate" onClick="moveToNextTab(validate(),
				function(){\$('.error-panel').html('please enter all the details'); },
				function(){\$('#auto-reply-read-only-text').html(\$('.check-bound-text-area').val()); })">
		Next
	</g:link>
</div>