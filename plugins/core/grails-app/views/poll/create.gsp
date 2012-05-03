<%@ page contentType="text/html;charset=UTF-8" %>
<g:javascript src="characterSMS-count.js"/>

<div id="tabs" class="vertical-tabs">
	<div class="error-panel hide"><div id="error-icon"></div><g:message code="poll.validation.prompt"/></div>
	<ul>
		<li><a class="tabs-1" href="#tabs-1"><g:message code="poll.question"/></a></li>
		<li><a class="tabs-2" href="#tabs-2"><g:message code="poll.response"/></a></li>
		<li><a class="tabs-3" href="#tabs-3"><g:message code="poll.sort"/></a></li>
		<li><a class="tabs-4" href="#tabs-4"><g:message code="poll.reply"/></a></li>
		<li><a class="tabs-5" href="#tabs-5"><g:message code="poll.edit.message"/></a></li>
		<li><a class="tabs-6" href="#tabs-6"><g:message code="poll.recipients"/></a></li>
		<li><a class="tabs-7" href="#tabs-7"><g:message code="poll.confirm"/></a></li>
	</ul>

	<g:formRemote url="[action: 'save', controller:'poll', params: [ownerId:activityInstanceToEdit?.id ?: null]]" name='new-poll-form' method="post" onSuccess="checkForSuccessfulSave(data, i18n('poll.label') )">
		<f:render template="../poll/question"/>
		<f:render template="../poll/responses"/>
		<f:render template="../poll/sorting"/>
		<f:render template="../poll/replies"/>
		<f:render template="../poll/message"/>
		<div id="tabs-6">
			<f:render template="/quickMessage/select_recipients" model= "['contactList' : contactList,
				                                                           'groupList': groupList,
				                                                           'nonExistingRecipients': [],
				                                                           'recipients': []]"/>
		</div>
		<f:render template="../poll/confirm"/>
	</g:formRemote>
</div>

<g:javascript>
	function initializePopup() {
		<g:if test="${activityInstanceToEdit}">
			$("#autoReplyText").trigger("keyup");
		</g:if>
		
		highlightPollResponses();
		
		/* Poll type tab */
		$("#tabs-1").contentWidget({
			validate: function() {
				$("#question").removeClass('error');
				if ($("input[name='pollType']:checked").val() == "standard") {
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
				var isValid =  $("input[name='pollType']:checked").val() == "standard" || validatePollResponses();
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
		$("#auto-reply-read-only-text").html($("#autoreplyText").val().trim() ? $("#autoreplyText").val() : i18n("autoreply.text.none"))
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
		isGroupChecked("dontSendMessage") ?	sendMessage = i18n("poll.send.messages.none") : sendMessage = $('#messageText').val();

		var contactNo = $("#contacts-count").text()
		
		if(contactNo == 0 || isGroupChecked("dontSendMessage")) {
			$("#confirm-recipients-count #sending-messages").hide()
			$("#no-recipients").show()
		} else {
			$("#confirm-recipients-count #sending-messages").show()
			$("#no-recipients").hide()
		}
		$("#poll-message").html('<p>' + sendMessage  + '</p>');
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
</g:javascript>
