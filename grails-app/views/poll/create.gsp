<%@ page contentType="text/html;charset=UTF-8" %>
<div id="tabs">
	<ul>
		<li><a href="#tabs-1">New Poll: Enter Question</a></li>
		<li><a href="#tabs-2">New Poll: AnswerList</a></li>
		<li><a href="#tabs-3">Automatic reply</a></li>
		<li><a href="#tabs-4">Confirm</a></li>
	</ul>

	<g:form action="save" name="poll-details" controller="poll" method="post">
		<div id="tabs-1">
			<div class="section">
				<div>
					<h3>Select the kind of poll to create</h3>
					<g:radio name="poll-type" value="standard" onclick="populateResponses()"/>Question with a 'Yes' or 'No' answer
					<g:radio name="poll-type" value="multiple"  onclick="populateResponses()"/>Multiple choice question (e.g. 'Red', 'Blue', 'Green')
				</div>
			</div>
			<g:link url="#" onclick="moveForward()">Next</g:link>
		</div>

		<div id="tabs-2">
			Responses:
			<g:textField name="responses" id="responses"/>
			<g:link url="#" class="next">Next</g:link>
			<g:link url="#" class="back">Back</g:link>

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
			<g:link url="#" class="back">Back</g:link>
			<g:link url="#" class="next-validate" onClick="moveToNextTab(validate(),
															function(){\$('.error-panel').html('please enter all the details'); },
															function(){\$('#auto-reply-read-only-text').html(\$('.check-bound-text-area').val()); })">
				Next
			</g:link>
		</div>
		<div id="tabs-4">
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

	function populateResponses() {
		if (getSelectedGroupElements('poll-type')[0].value == 'standard') {
			$("#responses").val("yes no");
		}
		else {
			$("#responses").val("")
		}
	}

	function moveForward() {
		var selectedElements = getSelectedGroupElements('poll-type');
		if (selectedElements.size() > 0 && selectedElements[0].value == 'standard') {
			moveToTabBy(2)
		}
		else {
			moveToTabBy(1)
		}
	}
</script>





