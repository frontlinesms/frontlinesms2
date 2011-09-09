<%@ page contentType="text/html;charset=UTF-8" %>
<div id="tabs" class="vertical-tabs">
	<ol>
		<li><a href="#tabs-1">Enter Question</a></li>
		<li id='responseTab-text'><a href="#tabs-2">Response list</a></li>
		<li><a href="#tabs-3">Automatic sorting</a></li>
		<li><a href="#tabs-4">Automatic reply</a></li>
		<li id='recipientsTab-text'><a href="#tabs-5">Select recipients</a></li>
		<li><a href="#tabs-6">Confirm</a></li>
		<li class="confirm-tab"><a href="#tabs-7"></a></li>
	</ol>

	<g:formRemote url="${[action:'save', controller:'poll']}" name='poll-details' method="post" onSuccess="goToNextTab()">
		<div class="error-panel hide">Please fill in all the required fields</div>
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
		<div id="tabs-7">
			<h2>The poll has been created!</h2>
			<h2>The messages  have been added to the pending message queue.</h2>

			<h2>It may take some time for all the messages to be sent, depending on the
			number of messages and the network connection.</h2>

			<h2>To see the status of your message, open the 'Pending' messages folder.</h2>
		</div>
	</g:formRemote>
</div>

<g:javascript>
	function initializePoll() {
		$("#tabs").tabs("disable", getTabLength());
		$('#tabs').tabs("disable", 1);
		$('#responseTab-text a').css('color', '#DFDFDF');
		
		highlightPollResponses();

		$("#tabs-1").contentWidget({
			validate: function() {
				$("#question").removeClass('error');
				var isValid = !isElementEmpty($("#question"));
				if ($("input[name='poll-type']:checked").val() == "standard")
					$('#tabs').tabs("disable", 1);
				else
					$('#tabs').tabs("enable", 1);
				if(!isValid)
					$("#question").addClass('error');
				return isValid;

			}
		});

		$("#tabs-2").contentWidget({
			validate: function() {
				$('#choiceA').removeClass('error');
				$('#choiceB').removeClass('error');
				var isValid =  $("input[name='poll-type']:checked").val() != "standard" ?  validatePollResponses() : true;
				if(!isValid) {
					isElementEmpty($('#choiceA')) && $('#choiceA').addClass('error');
					isElementEmpty($('#choiceB')) && $('#choiceB').addClass('error');
				}
				return isValid;
			}
		});

		$("#tabs-4").contentWidget({
			validate: function() {
				$('#tabs-4 textarea').removeClass("error");
				var isValid = isGroupChecked('auto-reply') ? !(isElementEmpty('#tabs-4 textarea')) : true;
				if(!isValid) {
					$('#tabs-4 textarea').addClass("error");
				}
				return isValid;
			}
		});

		$("#tabs-5").contentWidget({
			validate: function() {
				return isGroupChecked('collect-responses') ?  true : isGroupChecked('addresses');
			}
		});

		$("#tabs-6").contentWidget({
			validate: function() {
				$("#tabs-6 #title").removeClass("error");
				var isEmpty = isElementEmpty($("#tabs-6 #title"));
				if(isEmpty) {
					$("#tabs-6 #title").addClass("error");
				}
				return !isEmpty;
			},
			
			onDone: function() {
				$("#poll-details").submit();
				return false;
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
				if(this.id != "choiceA" && this.id != "choiceB") {
					var label = $("label[for='" + this.id + "']");
					if (!$.trim(this.value).length) label.removeClass('bold');
					else label.addClass('bold');
				}
			});
		})
	}

	function validatePollResponses() {
		return !isElementEmpty($("#choiceA")) && !isElementEmpty($("#choiceB"))
	}

	function goToNextTab() {
		$("#tabs").tabs("enable", getTabLength());
		$('#tabs').tabs('select', getCurrentTab() + 1);
	}
</g:javascript>
