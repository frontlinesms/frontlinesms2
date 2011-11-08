<%@ page contentType="text/html;charset=UTF-8" %>
<g:javascript src="characterSMS-count.js"/>

<div id="tabs" class="vertical-tabs">
	<ol>
		<li><a class="tabs-1" href="#tabs-1">Enter Question</a></li>
		<li><a class="tabs-2" href="#tabs-2">Response list</a></li>
		<li><a class="tabs-3" href="#tabs-3">Automatic sorting</a></li>
		<li><a class="tabs-4" href="#tabs-4">Automatic reply</a></li>
		<li><a class="tabs-5" href="#tabs-5">Edit Message</a></li>
		<li><a class="tabs-6" href="#tabs-6">Select recipients</a></li>
		<li><a class="tabs-7" href="#tabs-7">Confirm</a></li>
		<li class="confirm-tab"><a class="tabs-8" href="#tabs-8"></a></li>
	</ol>

	<g:formRemote url="${[action:'save', controller:'poll']}" name='poll-details' method="post" onSuccess="goToSummaryTab()">
		<div class="error-panel hide">Please fill in all the required fields</div>
		<g:render template="question"/>
		<g:render template="responses"/>
		<g:render template="sorting"/>
		<g:render template="replies"/>
		<g:render template="message"/>
		<div id="tabs-6">
			<g:render template="../quickMessage/select_recipients" model= "['contactList' : contactList,
			                                                                'groupList': groupList,
			                                                                'nonExistingRecipients': [],
			                                                                'recipients': []]"/>
		</div>
		<g:render template="confirm"/>
		<div id="tabs-8" class='summary'>
			<g:render template="summary"/>
		</div>
	</g:formRemote>
</div>

<g:javascript>
	function initialize() {
		$("#tabs").tabs("disable", getTabLength());
		disableTab(1);
		highlightPollResponses();

		/* Poll type tab */
		$("#tabs-1").contentWidget({
			validate: function() {
				$("#question").removeClass('error');
				if ($("input[name='poll-type']:checked").val() == "standard")
					$('#tabs').tabs("disable", 1);
				else
					$('#tabs').tabs("enable", 1);
				var isValid = !isElementEmpty($("#question"));
				if(!isValid)
					$("#question").addClass('error');
				return isValid;

			}
		});

		/* Replies tab */
		$("#tabs-2").contentWidget({
			validate: function() {
				$('#choiceA').removeClass('error');
				$('#choiceB').removeClass('error');
				var isValid =  $("input[name='poll-type']:checked").val() == "standard" || validatePollResponses();
				if(!isValid) {
					if(isElementEmpty($('#choiceA'))) $('#choiceA').addClass('error');
					if(isElementEmpty($('#choiceB'))) $('#choiceB').addClass('error');
				}
				return isValid;
			}
		});
		
		/* Auto-sort tab */
		$("#tabs-3").contentWidget({
			validate: function() {
				var pollKeywordTextfield = $("input[name='keyword']");
				var isValid = $("input[name='enableKeyword']:checked").val() == 'false' ||
						pollKeywordTextfield.val().trim().length > 0;
				if(isValid) pollKeywordTextfield.removeClass('error');
				else pollKeywordTextfield.addClass('error');
				return isValid;
			}
		});

		/* Auto-reply tab */
		$("#tabs-4").contentWidget({
			validate: function() {
				$('#tabs-4 textarea').removeClass("error");
				var isValid = !isGroupChecked('enableAutoReply') || !(isElementEmpty('#tabs-4 textarea'));
				if(!isValid) {
					$('#tabs-4 textarea').addClass("error");
				}
				return isValid;
			}
		});
		
		$("#tabs-6").contentWidget({
			validate: function() {
				return isGroupChecked('dontSendMessage') || isGroupChecked('addresses');
			}
		});

		$("#tabs-7").contentWidget({
			validate: function() {
				$("#tabs-7 #title").removeClass("error");
				var isEmpty = isElementEmpty($("#tabs-7 #title"));
				if(isEmpty) {
					$("#tabs-7 #title").addClass("error");
				}
				return !isEmpty;
			},
			
			onDone: function() {
				$("#poll-details").submit();
				return false;
			}
		});


		$("#tabs").bind("tabsshow", function(event, ui) {
			updateSendMessage();
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
		updateMessageDetails();
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
	
	function updateMessageDetails() {
		var sendMessage
		isGroupChecked("dontSendMessage") ?	sendMessage = "No messages will be sent" : sendMessage = $('#messageText').val();

		var contactNo = $("#contacts-count").text()
		
		if(contactNo == 0 || isGroupChecked("dontSendMessage")) {
			$("#confirm-recepients-count").addClass("hide")
			$("#no-recepients").removeClass("hide")
		} else {
			$("#confirm-recepients-count").removeClass("hide")
			$("#no-recepients").addClass("hide")
		}
		$("#poll-message").html('<pre>' + sendMessage  + '</pre>');
	}

	function highlightPollResponses() {
		$(".choices").each(function() {
			var changeHandler = function() {
				if(this.id != "choiceA" && this.id != "choiceE") {
					var label = $(this).parent().next().find('label');
					if (!$.trim(this.value).length) label.removeClass('field-enabled');
					else label.addClass('field-enabled');
				}
			}
			$(this).keyup(changeHandler);
			$(this).change(changeHandler);
		})
	}

	function validatePollResponses() {
		return !isElementEmpty($("#choiceA")) && !isElementEmpty($("#choiceB"))
	}

	function goToSummaryTab() {
		$("#tabs").tabs("enable", getTabLength());
		$('#tabs').tabs('select', getCurrentTab() + 1);
	}
</g:javascript>
