<%@ page contentType="text/html;charset=UTF-8" %>
<div id="tabs">
	<ul>
		<li><a href="#tabs-1">Enter Question</a></li>
		<li><a href="#tabs-2">Automatic Sorting</a></li>
		<li><a href="#tabs-3">Automatic reply</a></li>
		<li><a href="#tabs-4">Select recipients</a></li>
		<li><a href="#tabs-5">Confirm</a></li>
	</ul>

<g:form action="save" controller="poll" method="post">
	<div id="tabs-1">
		<div class="section">
			<h3>Select the kind of poll to create</h3>
			<div>
				<ul>
					<g:radioGroup name="someName" values="[false, true]" labels="['Question with a \'Yes\' or \'No\' answer', 'Multiple choice question (e.g. \'Red\', \'Blue\', \'Green\')']">
						<li>${it.label} ${it.radio}</li>
					</g:radioGroup>
				</ul>
				Responses:
				 <g:textField name="responseString" />
			</div>
		</div>
		<g:link url="#" class="next">Next</g:link>
	</div>
	<div id="tabs-2">
		<g:link url="#" class="next">Next</g:link>
	</div>
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
		<g:link url="#" class="next-validate" onClick="moveToNextTab(validate(),
															function(){\$('.error-panel').html('please enter all the details'); },
															function(){\$('#auto-reply-read-only-text').html(\$('.check-bound-text-area').val()); })">
			Next
		</g:link>
	</div>
	<div id="tabs-4">
		<g:link url="#" class="next">Next</g:link>
	</div>
	<div id="tabs-5">
		Name this poll:
		<g:textField name="title"></g:textField>
		<label>Auto reply </label> <span id="auto-reply-read-only-text"></span>
		<g:link url="#" class="back">Back</g:link>
		<g:submitButton name="send-msg" id="sendMsg" value="Send"></g:submitButton>
	</div>
</g:form>
</div>


<script>
	function validate() {
		return isGroupChecked('auto-reply') ?  !isElementEmpty('.check-bound-text-area') : true;
	}
</script>





