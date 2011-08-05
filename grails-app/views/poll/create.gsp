<%@ page contentType="text/html;charset=UTF-8" %>
<div id="tabs">
	<ul>
		<li><a href="#tabs-1">New Poll: Enter Question</a></li>
		<li><a href="#tabs-2">New Poll: AnswerList</a></li>
		<li><a href="#tabs-3">Automatic reply</a></li>
		<li><a href="#tabs-4">Select recipients</a></li>
		<li><a href="#tabs-5">Confirm</a></li>
	</ul>

	<g:form action="save" name="poll-details" controller="poll" method="post">
	<div class="error-panel" />
		<div id="tabs-1">
			<div class="section">
				<div>
					<h3>Select the kind of poll to create</h3>
					<div>
						<g:radio name="poll-type" value="standard" onclick="populateResponses()"/>Question with a 'Yes' or 'No' answer
					</div>
					<div>
						<g:radio name="poll-type" value="multiple" onclick="populateResponses()"/>Multiple choice question (e.g. 'Red', 'Blue', 'Green')
					</div>
					<g:checkBox name="collect-responses" value="no-message" checked='false'/>Do not send a message for this poll(collect responses only)
				</div>
			</div>
			<g:link url="#" onclick="nextTabToMove()">Next</g:link>
		</div>

		<div id="tabs-2" class="poll-responses-tab">
			<label for='instruction'>Enter Instructions:</label>
			<g:textField name="instruction" id="instruction" value="Reply 'A', 'B', 'C', etc." />
			<label for='poll-choices'>Enter possible responses (between 2 and 5):</label>
			<ul id='poll-choices'>
				<g:each in="${['A','B','C','D','E']}" var="option">
					<li>
						<label for='choice${option}'>${option}</label>
					   	<g:textField class='choices' name="choice${option}" id="choice${option}" value="" />
					</li>
				</g:each>
			</ul>
			<g:link url="#" class="back">Back</g:link>
			<g:link url="#" class="next">Next</g:link>
		</div>
		
		<div id="tabs-3">
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
															function(){
																skipTabBy(2, isGroupChecked('collect-responses'));
																\$('#auto-reply-read-only-text').html(\$('.check-bound-text-area').val());
															})">
				Next
			</g:link>
		</div>
		<div id="tabs-4">
			<g:render template="../quickMessage/select_recipients" model= "['contactList' : contactList,
																			'groupList': groupList,
																			'nonExistingRecipients': [],
																			'recipients': []]"></g:render>
		</div>
		<div id="tabs-5">
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
		} else {
			$("#responses").val("")
		}
	}
	
	function skipTabBy(numberOfTabs, condition) {
		if (condition) moveToTabBy(2)
		else moveToTabBy(1)
	}

	function nextTabToMove() {
		var selectedElements = getSelectedGroupElements('poll-type');
		skipTabBy(2, selectedElements.size() > 0 && selectedElements[0].value == 'standard');
		validatePollResponses();
	}

	function validatePollResponses() {
		$(".choices").each(function() {
			$(this).blur(function() {
				var label = $("label[for='" + this.id + "']");
				if (!$.trim(this.value).length) label.removeClass('bold');
				else label.addClass('bold');
			});
		})
	}
</script>





