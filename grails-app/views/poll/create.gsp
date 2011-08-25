<%@ page contentType="text/html;charset=UTF-8" %>
<div id="tabs" class="vertical-tabs">
		<ol>
			<li><a href="#tabs-1">Enter Question</a></li>
			<li><a href="#tabs-2">Response list</a></li>
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

		$("#tabs-1").contentWidget({
			validate: function() {
				if ($("input[name='poll-type']:checked").val() == "standard")
					$('#tabs').tabs("disable", 1);
				else
					$('#tabs').tabs("enable", 1);
				return true
			}
		});

		$("#tabs-3").contentWidget({
			validate: function() {
				return isGroupChecked('auto-reply') ? !(isElementEmpty('#tabs-3 textarea')) : true;
			}
		});

		$("#tabs").bind("tabsshow", function(event, ui) {
			updateConfirmationMessage();
		});
	}

	function updateConfirmationMessage() {
		var question = $("#question").val();
		var instruction = $("#instruction").val();
		var choices = '';
		$(".choices").each(function() {
			if (this.value) choices = choices + ' ' + this.name.substring(6,7) + ') ' + this.value
		});
		$("#poll-question-text").html('<pre>' + question + ' ' + choices + ' '  + '</pre>');
		$("#auto-reply-read-only-text").html($("#autoReplyText").val().trim() ? $("#autoReplyText").val() : "None")
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
