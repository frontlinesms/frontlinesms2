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
			<g:link url="#" onclick="nextTabToMove()">Next</g:link>
		</div>

		<div id="tabs-2" class="poll-responses-tab">
			<label for='instruction'>Enter Instructions:</label>
			<g:textField name="instruction" id="instruction" value="" />
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
		<div id="tabs-5" class="confirm-responses-tab">
			<label for='title'>Name this poll:</label>
			<g:textField name="title" id="title"></g:textField>
			<label>Confirm details:</label>
			<div class="confirm-header">Message:</div><div id="poll-question-text">.</div>
			<div class="clear"></div>
			<div class="confirm-header">Auto reply:</div><div id="auto-reply-read-only-text">None</div>
			<div class="confirm-header">Recipients:</div><div id="confirm-recepients-count"><span id="contacts-count">0</span> contacts selected</div>
			<g:link url="#" class="back">Back</g:link>
			<g:submitButton name="save" id="create-poll" value="Create"></g:submitButton>
		</div>
	</g:form>
</div>

<script>
	function initializePoll() {
		highlightPollResponses();
		$("#tabs").bind("tabsshow", function(event, ui) {
			updateConfirmationMessage();
		});
		$(".poll-responses-tab .next").addClass('disabled');
	}

	function updateConfirmationMessage() {
		var question = $("#question").val();
		var instruction = $("#instruction").val();
		var choices = '';
		$(".choices").each(function() {
			if (this.value) choices = choices + ' ' + this.name.substring(6,7) + ') ' + this.value
		});
		$("#poll-question-text").html('<pre>' + question + ' ' + choices + ' ' + instruction + '</pre>');
	}

	function validate() {
		return isGroupChecked('auto-reply') ? !isElementEmpty('.check-bound-text-area') : true;
	}

	function skipTabBy(numberOfTabs, condition) {
		if (condition) moveToTabBy(2)
		else moveToTabBy(1)
	}

	function nextTabToMove() {
		var selectedElements = getSelectedGroupElements('poll-type');
		skipTabBy(2, selectedElements.size() > 0 && selectedElements[0].value == 'standard');
	}

	function highlightPollResponses() {
		$(".choices").each(function() {
			$(this).blur(function() {
				validatePollResponses();
				var label = $("label[for='" + this.id + "']");
				if (!$.trim(this.value).length) label.removeClass('bold');
				else label.addClass('bold');
			});
		})
	}

	function validatePollResponses() {
		var validResponsesCount = 0;
		$("#poll-choices input[type=text]").each(function() {
			if ($.trim($(this).val()).length > 0) validResponsesCount++;
		});
		if (validResponsesCount < 2) {
			$(".poll-responses-tab .next").addClass('disabled');
			$(".error-panel").html("Please enter at least two responses");
		} else {
			$(".poll-responses-tab .next").removeClass('disabled');
			$(".error-panel").html("");
		}
	}
</script>