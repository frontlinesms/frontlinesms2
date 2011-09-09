<%@ page contentType="text/html;charset=UTF-8" %>
<div id="tabs" class="vertical-tabs">
	<ol>
		<li><a href="#tabs-1">Enter Question</a></li>
		<li id='responseTab-text'><a href="#tabs-2">Response list</a></li>
		<li><a href="#tabs-3">Automatic sorting</a></li>
		<li><a href="#tabs-4">Automatic reply</a></li>
		<li id='recipientsTab-text'><a href="#tabs-5">Select recipients</a></li>
		<li><a href="#tabs-6">Confirm</a></li>
	</ol>

	<g:form action="save" name="poll-details" controller="poll" method="post">
		<g:render template="question"/>
		<g:render template="answers"/>
		<g:render template="sorting"/>
		<g:render template="replies"/>
		<div id="tabs-5">
			<g:render template="../quickMessage/select_recipients" model= "['contactList' : contactList,
			                                                                'groupList': groupList,
			                                                                'nonExistingRecipients': [],
			                                                                'recipients': []]"/>
		</div>
		<g:render template="confirm"/>
	</g:form>
</div>

<g:javascript>
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

		$("#tabs-4").contentWidget({
			validate: function() {
				return isGroupChecked('auto-reply') ? !(isElementEmpty('#tabs-4 textarea')) : true;
			}
		});

		$("#tabs").bind("tabsshow", function(event, ui) {
			updateConfirmationMessage();
		});
		
		/* SET UP KEYWORD SORTING PAGE */
		// Add change listener to enableKeyword radio group such that when value is false
		// poll-keyword is disabled, and when value is true then poll-keyword is enabled
		$("input[name='enableKeyword']").change(function() {
			var enabled = $(this).val() == 'true';
			if(enabled) $('#poll-keyword').removeAttr("disabled");
			else $('#poll-keyword').attr("disabled", "disabled");
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
		
		// update auto-sort
		var autoSort = $("input[name='enableKeyword']:checked").val();
		var autoSortMessages = $('#auto-sort-confirm p');
		if(autoSort == 'true') {
			var keyword = $("input[name='keyword']").val();
			autoSortMessages.eq(0).hide();
			autoSortMessages.eq(1).show();
			$('#auto-sort-confirm-keyword').text(keyword);
		} else {
			autoSortMessages.eq(0).show();
			autoSortMessages.eq(1).hide();
		}
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
</g:javascript>
