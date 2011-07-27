<%@ page contentType="text/html;charset=UTF-8" %>
<div id="tabs">
	<ul>
		<li><a href="#tabs-1">AnswerList</a></li>
		<li><a href="#tabs-2">Automatic reply</a></li>
		<li><a href="#tabs-3">Confirm</a></li>
	</ul>

	<g:form action="save" name="poll-details" controller="poll" method="post">
		<div id="tabs-1">
			<div class="section">
				<h3>Select the kind of poll to create</h3>
				<div>
					Responses:
					<g:textField name="responses"/>
				</div>
			</div>
			<g:link url="#" class="back">Back</g:link>
			<g:link url="#" class="next">Next</g:link>
		</div>
		<div id="tabs-2">
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
		<div id="tabs-3">
			Name this poll:
			<g:textField name="title"></g:textField>
			<div>
				<label>Auto reply</label> <span id="auto-reply-read-only-text"></span>
			</div>
			<g:link url="#" class="back">Back</g:link>
			<g:submitButton name="save" id="create-poll" value="Create"></g:submitButton>
		</div>
	</g:form>
</div>


<script>
	function validate() {
		return isGroupChecked('auto-reply') ? !isElementEmpty('.check-bound-text-area') : true;
	}
</script>





