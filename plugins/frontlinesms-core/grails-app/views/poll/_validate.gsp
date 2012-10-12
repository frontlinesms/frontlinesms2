<r:script>
	var autoUpdate = true;
	$("#messageText").live("keyup", updateSmsCharacterCount);
	$("button#nextPage").click(setAliasValues);

	function initializePopup() {
		<g:if test="${activityInstanceToEdit?.id}">
			if($("#messageText").val().length > 0) {
				$("#messageText").trigger("keyup");
				$("input[name='enableKeyword']:checked").trigger("change");
				$("input[name='pollType']").trigger("change");
			}
		</g:if>
		<g:else>
			mediumPopup.disableTab("poll-response");
			$("input[name='pollType']").trigger("change");
		</g:else>
		<g:if test="${activityInstanceToEdit?.archived}">
			$("input#dontSendMessage").attr('checked', true);
			$("input#dontSendMessage").trigger("change");
			$("input#dontSendMessage").attr('disabled', 'disabled');
		</g:if>
		addCustomValidationClasses();
		initializeTabValidation(createFormValidator());
	}

	function createFormValidator() {
		var validator = $("#new-poll-form").validate({
			errorContainer: ".error-panel",
			rules: {
				addresses: {
				  required: true,
				  minlength: 1
				}
			},
			messages: {
				addresses: {
					required: i18n("poll.recipients.validation.error")
				}
			},
			errorPlacement: function(error, element) {
				if (element.attr("name") == "addresses") {
					error.insertAfter("#recipients-list");
					$("#recipients-list").addClass("error");
				} else
					error.insertAfter(element);
			}
		});
		return validator;
	}

	function addCustomValidationClasses() {
		aliasCustomValidation();
		
		jQuery.validator.addMethod("edit", function(value, element) {
			return (value.trim().length != 0);
		}, i18n("poll.choice.validation.error.deleting.response"));

		jQuery.validator.addMethod("no-space", function(value, element) {
			return (value.trim().indexOf(" ") === -1);
		}, i18n("validation.nospaces.error"));
	}

	function initializeTabValidation(validator) {
		var questionTabValidation = function() {
			return validator.element($("#question"));
		};
		var responseTabValidation = function() {
			var valid = true;
			if($("input[name='pollType']:checked").val() == "yesNo") {
				valid = true;
			} else {
				var choices = $('input:not(:disabled).choices');
				$.each(choices, function(index, value) {
					if (!validator.element(value) && valid) {
						valid = false;
					}
				});
			}
			setAliasValues();
			return valid;
		};
		var aliasTabValidation = function() {
			var valid = true;
			$('input:not(:disabled).keywords').each(function() {
				if (!validator.element(this) && valid) {
				    valid = false;
				}
			});
			return valid;
		};
		var autoSortTabValidation = function() {
			return validator.element('#poll-keyword');
		};
		var autoReplyTabValidation = function() {
			return validator.element('#autoreplyText');
		};
		var composeMessageTabValidation = function() {
			return validator.element('#messageText');
		};
		var recipientTabValidation = function() {
			if(!isGroupChecked('dontSendMessage')) {
				var valid = false;
				addAddressHandler();
				valid = $('input[name=addresses]:checked').length > 0;
				var addressListener = function() {
					if($('input[name=addresses]:checked').length > 0) {
						validator.element($('#contacts').find("input[name=addresses]"));
						$('#recipients-list').removeClass("error");
						$(".error").hide();
					} else {
						$('#recipients-list').addClass("error");
						validator.showErrors({"addresses": i18n("poll.recipients.validation.error")});
					}
				};
				if (!valid) {
					$('input[name=addresses]').change(addressListener);
					$('input[name=addresses]').trigger("change");
				}
				return valid;
			}
			return true;
		};

		var confirmTabValidation = function() {
			return validator.element('input[name=name]');
		};

		mediumPopup.addValidation('poll-question', questionTabValidation);
		mediumPopup.addValidation('poll-response', responseTabValidation);
		mediumPopup.addValidation('poll-alias', aliasTabValidation);
		mediumPopup.addValidation('poll-reply', autoReplyTabValidation);
		mediumPopup.addValidation('poll-edit-message', composeMessageTabValidation)
		mediumPopup.addValidation('poll-recipients', recipientTabValidation);
		mediumPopup.addValidation('poll-confirm', confirmTabValidation);

		$("#tabs").bind("tabsshow", function(event, ui) {
			updateSendMessage();
			updateConfirmationMessage();
		});
	}


	function updateSendMessage() {
		// TODO check why these are being bound every time - surely could just bind when the page is loaded.
		$("#messageText").live("keypress", autoUpdateOff);
		$(".choices").live("keypress", autoUpdateOn);
		$("#question").live("keypress", autoUpdateOn);
		
		if(autoUpdate) {
			var questionText = $("#question").val();
			if (questionText.substring(questionText.length - 1) != '?') questionText = questionText + '?';
			questionText = questionText + '\n';
			var keywordText = '';
			var replyText = '';
			if ($('#poll-keyword').attr("disabled") == undefined || $('#poll-keyword').attr("disabled") == false) {
				keywordText = $("#poll-keyword").val().toUpperCase();
				if($("input[name='pollType']:checked").val() == "yesNo") {
					var yesAlias = getFirstAlias($("ul#poll-aliases li input#keywordsA"))
					var noAlias = getFirstAlias($("ul#poll-aliases li input#keywordsB"))
					replyText = i18n("poll.reply.text", keywordText, yesAlias, keywordText, noAlias);
				} else {
					replyText = i18n("poll.reply.text5");
					$(".choices").each(function() {
						if (replyText != 'Reply' && this.value) replyText = replyText + ',';
						if (this.value) replyText = i18n("poll.reply.text1", replyText, keywordText, getFirstAlias($("ul#poll-aliases li input#keywords"+this.name.substring(6,7))), this.value);
					});
					replyText = replyText + '.';
				}
			} else if ($("input[name='pollType']:checked").val() == "yesNo") {
				replyText = i18n("poll.reply.text2");
			} else {
				replyText = i18n("poll.reply.text6")+ ' ';
				$(".choices").each(function() {
					if (replyText!=i18n("poll.reply.text6") + ' ' && this.value) replyText += ' ' + i18n("poll.reply.text3");
					if (this.value) replyText += "'" + this.value + "'";
				});
			} 
			var sendMessage = questionText + replyText;
			$("#messageText").val(sendMessage);
			$("#messageText").keyup();
			highlightPollResponses();
			setConfirmAliasValues();
		}
	}

	function setAliasValues() {
		var yesNo = $("input[name='pollType']:checked").val() == "yesNo";
		if (yesNo) {
			var myMap = {'A':'Yes', 'B':'No', 'C':'', 'D':'', 'E':''};
			$.each(myMap, function(key, value) {
				$("ul#poll-aliases li label:first[for='keywords"+key+"']").text(value);
				if(value == '') {
					$("ul#poll-aliases li input#keywords"+key).val('');
					$("ul#poll-aliases li input#keywords"+key).attr('disabled','disabled');
				} else {
					$("ul#poll-aliases li input#keywords"+key).removeAttr('disabled');
				}
			});
		} else {
			var myArray = ['A', 'B', 'C', 'D', 'E'];
			$.each(myArray, function(index, value) {
				var labelValue = $("ul#poll-choices li input#choice"+value).val().trim();
				var aliasTextFieldLabel = $("ul#poll-aliases li label:first[for='keywords"+value+"']");
				var aliasTextField = $("ul#poll-aliases li input#keywords"+value);
				if(labelValue.length == 0) {
					aliasTextFieldLabel.text(value);
					aliasTextField.attr('disabled', 'disabled');
					aliasTextFieldLabel.removeClass("field-enabled");
				} else {
					aliasTextFieldLabel.text(labelValue);
					aliasTextFieldLabel.addClass("field-enabled");
					$("ul#poll-aliases li input#keywords"+value).removeAttr('disabled');
				}
			});
		}
	}

	function addRespectiveAliases(field) {
		var yesNo = $("input[name='pollType']:checked").val() == "yesNo";
		if(yesNo) {
			var aliasYesTextField = $("ul#poll-aliases li input#keywordsA");
			var aliasNoTextField = $("ul#poll-aliases li input#keywordsB");
			var yesAlias = i18n("poll.yes") + ", A";
			var noAlias = i18n("poll.no") + ", B";
			var choices = { };
			choices[yesAlias] = aliasYesTextField;
			choices[noAlias] = aliasNoTextField;
			<% 	
				def pollResponse = activityInstanceToEdit?.responses.find {it.key == option} 
				def mode = pollResponse?"edit":"create"
			%>
			$.each(choices, function(key, value) {
				<g:if test="${mode == 'create'}">
					if(value.val().trim().length == 0) value.val(key);
				</g:if>
			});
		} else {
			var aliases = "";
			var rawKey = $(field).attr('id').trim();
			var rawVal = $(field).val().trim();
			var value = rawVal.split(' ')[0]
			var key = rawKey.substring(rawKey.length-1);
			var aliasTextFieldLabel = $("ul#poll-aliases li label[for='keywords" + value + "']");
			var aliasTextField = $("ul#poll-aliases li input#keywords" + key);
			if($(field).hasClass("create")) {
				if(value.length > 0){
					aliases += value + ", " + key;
					aliasTextField.val(aliases);
					aliasTextField.removeAttr("disabled");
				}
			}
			if(value.length == 0) {
				aliasTextFieldLabel.text("");
				aliasTextField.val("");
				aliasTextField.attr("disabled","disabled");
			}
		}
	}
	
	function autoUpdateOff() {
		autoUpdate = false;
	}
	
	function autoUpdateOn() {
		autoUpdate = true;
	}

	function updateConfirmationMessage() {
		updateMessageDetails();
		
		var currentVal = $("#autoreplyText").val();
		$("#auto-reply-read-only-text").html(currentVal.trim()? currentVal: i18n("autoreply.text.none"))
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
		setConfirmAliasValues();

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

	function highlightNextPollResponse(choice) {
		if(choice.id != "choiceA" && choice.id != "choiceE") {
			var nextLabel = $(choice).parent().next().find('label');
			var nextInput = $(choice).parent().next().find('.choices');
			if (!choice.value.trim().length) {
				if (nextInput.val().trim().length == 0){
					nextLabel.removeClass('field-enabled');
					nextInput.attr('disabled', 'disabled');
				}
			} else {
				nextLabel.addClass('field-enabled');
				nextInput.removeAttr('disabled');
			}
		}
	}

	function highlightPollResponses(){
		$(".choices").each(function(){
			highlightNextPollResponse(this);
		});
	}

	function resetResponses(){
		<g:if test="${!activityInstanceToEdit?.id}">
			$("input.choices").each(function(){
				$(this).val('');
			});
			$("input.aliases").each(function(){
				$(this).val('');
			});
		</g:if>
	}

	function setConfirmAliasValues(){
		var myArray = ['A', 'B', 'C', 'D', 'E'];
		var aliasText = "";
		if($("input[name='pollType']:checked").val() != "yesNo") {
			$.each(myArray, function(index, value){
				var choice = $("ul#poll-choices li input#choice"+value).val();
				var aliases = $("ul#poll-aliases li input#keywords"+value).val();
				if(choice.length != 0){
					aliasText += "<p>"+choice+" : "+aliases + "</p>";
				}
			});
		}
		else {
			aliasText += "<p>"+i18n("poll.yes") + " : " + $("ul#poll-aliases li input#keywordsA").val() + "</p>";
			aliasText += "<p>"+i18n("poll.no") + " : " + $("ul#poll-aliases li input#keywordsB").val() + "</p>";
		}
		$("#poll-confirm-aliases").html(aliasText);
	}

	function getFirstAlias(field){
		var aliases = new Array();
		$.each(field.val().split(","), function(index, value){
			if(value.trim().length != 0){
				aliases.push(value);
			}
		});
		return aliases[0];
	}
</r:script>
