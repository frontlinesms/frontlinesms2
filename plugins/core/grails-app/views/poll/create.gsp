<%@ page contentType="text/html;charset=UTF-8" %>
<g:javascript src="characterSMS-count.js"/>

<div id="tabs" class="vertical-tabs">
	<div class="error-panel hide"><div id="error-icon"></div>Please fill in all required fields</div>
	<ol>
		<li><a class="tabs-1" href="#tabs-1">Enter Question</a></li>
		<li><a class="tabs-2" href="#tabs-2">Response list</a></li>
		<li><a class="tabs-3" href="#tabs-3">Automatic sorting</a></li>
		<li><a class="tabs-4" href="#tabs-4">Automatic reply</a></li>
		<li><a class="tabs-5" href="#tabs-5">Edit Message</a></li>
		<li><a class="tabs-6" href="#tabs-6">Select recipients</a></li>
		<li><a class="tabs-7" href="#tabs-7">Confirm</a></li>
	</ol>

	<g:render template="/poll/new_poll_form"/>
</div>

<g:javascript>
	function initializePopup() {
		<g:if test="activityInstanceToEdit">
			$("#autoReplyText").trigger("keyup");
		</g:if>
		
		highlightPollResponses();
		
		/* Poll type tab */
		$("#tabs-1").contentWidget({
			validate: function() {
				$("#question").removeClass('error');
				if ($("input[name='poll-type']:checked").val() == "standard") {
					disableTab(1);
				}
				else {
					enableTab(1);
				}
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
				var isValid = $("input[name='enableKeyword']:checked").val() == 'false' ||	pollKeywordTextfield.val().trim().length > 0;
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
				if(!isGroupChecked('dontSendMessage')) {
					addAddressHandler();
					return isGroupChecked('addresses');
				}
				return true
			}
		});

		$("#tabs-7").contentWidget({
			validate: function() {
				$("#tabs-7 #name").removeClass("error");
				var isEmpty = isElementEmpty($("#tabs-7 #name"));
				if(isEmpty) {
					$("#tabs-7 #name").addClass("error");
				}
				return !isEmpty;
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
		tabValidates($("#tabs-1"));
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
			$("#confirm-recipients-count #sending-messages").hide()
			$("#no-recipients").show()
		} else {
			$("#confirm-recipients-count #sending-messages").show()
			$("#no-recipients").hide()
		}
		$("#poll-message").html('<pre>' + sendMessage  + '</pre>');
	}

	function highlightPollResponses() {
		$(".choices").each(function(index) {
				
			var changeHandler = function() {
				if(this.id != "choiceA" && this.id != "choiceE") {
					var nextLabel = $(this).parent().next().find('label');
					var nextInput = $(this).parent().next().find('.choices');
					if (!$.trim(this.value).length) {
						nextLabel.removeClass('field-enabled');
						nextInput.val('');
						nextInput.attr('disabled', 'disabled');
					} else {
						nextLabel.addClass('field-enabled');
						nextInput.removeAttr('disabled');
					}
				}
			}
			$(this).keyup(changeHandler);
			$(this).change(changeHandler);
		})
	}

	function validatePollResponses() {
		return !isElementEmpty($("#choiceA")) && !isElementEmpty($("#choiceB"))
	}
	
	function summaryRedirect() {
		var ownerId = $(".summary #ownerId").val();
		$(this).dialog('close');
		window.location.replace(url_root + "message/activity/" + ownerId);
	}
</g:javascript>
