<%@ page contentType="text/html;charset=UTF-8" %>
<div id="tabs" class="vertical-tabs">
		<ol>
			<li><a href="#tabs-1">Enter Question</a></li>
			<li id='responseTab-text'><a href="#tabs-2">Response list</a></li>
			<li><a href="#tabs-3">Automatic reply</a></li>
			<li id='recipientsTab-text'><a href="#tabs-4">Select recipients</a></li>
			<li><a href="#tabs-5">Confirm</a></li>
			<li class="confirm-tab"><a href="#tabs-6"></a></li>
		</ol>

	<g:formRemote url="${[action:'save', controller:'poll']}" name='poll-details' method="post" onSuccess="goToNextTab()">
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
		<div id="tabs-6">
			<h2>The poll has been created!</h2>
			<h2>The messages  have been added to the pending message queue.</h2>

			<h2>It may take some time for all the messages to be sent, depending on the
			number of messages and the network connection.</h2>

			<h2>To see the status of your message, open the 'Pending' messages folder.</h2>
		</div>
	</g:formRemote>
</div>

<script>
	function initializePoll() {
		$("#tabs").tabs("disable", getTabLength());
		
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

		$("#tabs-2").contentWidget({
			validate: function() {
				return $("input[name='poll-type']:checked").val() != "standard" ?  validatePollResponses() : true;
			}
		});

		$("#tabs-3").contentWidget({
			validate: function() {
				return isGroupChecked('auto-reply') ? !(isElementEmpty('#tabs-3 textarea')) : true;
			}
		});

		$("#tabs-5").contentWidget({
			validate: function() {
				return !isElementEmpty($("#tabs-5 #title"));	
			},
			
			onDone: function() {
				$("#poll-details").submit();
				return false;
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
		if($("input[name='poll-type']:checked").val() == "standard") {
			choices = "A) Yes  B) No";
		} else {
			$(".choices").each(function() {
				if (this.value) choices = choices + ' ' + this.name.substring(6,7) + ') ' + this.value
			});
		}
		$("#poll-question-text").html('<pre>' + question + ' ' + choices + ' '  + '</pre>');
		$("#auto-reply-read-only-text").html($("#autoReplyText").val().trim() ? $("#autoReplyText").val() : "None")
	}


	function highlightPollResponses() {
		$(".choices").each(function() {
			$(this).blur(function() {
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
		return validResponsesCount >= 2		
	}

	function goToNextTab() {
		$("#tabs").tabs("enable", getTabLength());
		$('#tabs').tabs('select', getCurrentTab() + 1);
	}
</script>
