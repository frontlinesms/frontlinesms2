<%@ page contentType="text/html;charset=UTF-8" %>
<div id="tabs" class="vertical-tabs">
		<ol>
			<li><a href="#tabs-1">Enter Question</a></li>
			<li><a href="#tabs-2">Answer list</a></li>
			<li><a href="#tabs-3">Automatic reply</a></li>
			<li><a href="#tabs-4">Select recipients</a></li>
			<li><a href="#tabs-5">Confirm</a></li>
		</ol>

	<g:form action="save" name="poll-details" controller="poll" method="post">
		<g:render template="question"/>
		<g:render template="answers"/>
		<g:render template="replies"/>
		<div id="tabs-4">
			<g:render template="../quickMessage/select_recipients" model= "['contactList' : contactList,
																			'groupList': groupList,
																			'nonExistingRecipients': [],
																			'recipients': []]"></g:render>
		</div>
		<g:render template="confirm"/>
	</g:form>
</div>

<script>
	function initializePoll() {
		highlightPollResponses();
				
		$("#tabs").bind("tabsshow", function(event, ui) {
			if(ui.index == 1 && $("input[name='poll-type']:checked").val() == "standard")
				$("#tabs").tabs("select", ui.index+1);
			if(ui.index == 3 && $("#collect-responses").is(':checked'))
				$("#tabs").tabs("select", ui.index+1);
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
		$("#auto-reply-read-only-text").html($("#autoReplyText").val())
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
