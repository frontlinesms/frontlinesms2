var poll = (function() {
	var addCustomValidationClasses, autoUpdate, createFormValidator, highlightNextPollResponse,
			highlightPollResponses, initializeTabValidation,
			setAliasValues, setPollType, updateConfirmationMessage;

	autoUpdate = true;

	createFormValidator = function() {
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
				if (element.attr("name") === "addresses") {
					error.insertAfter("#recipients-list");
					$("#recipients-list").addClass("error");
				} else {
					error.insertAfter(element);
				}
			}
		});
		return validator;
	};

	addCustomValidationClasses = function() {
		aliasCustomValidation();
		genericSortingValidation();

		jQuery.validator.addMethod("edit", function(value, element) {
			return (value.trim().length !== 0);
		}, i18n("poll.choice.validation.error.deleting.response"));

		jQuery.validator.addMethod("no-space", function(value, element) {
			return (value.trim().indexOf(" ") === -1);
		}, i18n("validation.nospaces.error"));
	};

	initializeTabValidation = function(validator) {
		var autoReplyTabValidation, autoSortTabValidation, confirmTabValidation,
				composeMessageTabValidation, choices, questionTabValidation,
				recipientTabValidation, responseTabValidation, valid;
		questionTabValidation = function() {
			return validator.element($("#question"));
		};
		responseTabValidation = function() {
			valid = true;
			if($("input[name='pollType']:checked").val() === "yesNo") {
				valid = true;
			} else {
				choices = $('input:not(:disabled).choices');
				$.each(choices, function(index, value) {
					if (!validator.element(value) && valid) {
						valid = false;
					}
				});
			}
			setAliasValues();
			return valid;
		};

		autoSortTabValidation = function() {
			valid = true;
			$('input:not(:disabled).keywords').each(function() {
				if (!validator.element(this) && valid) {
				    valid = false;
				}
			});
			return validator.element('#poll-keyword') && valid;
		};

		autoReplyTabValidation = function() {
			return validator.element('#autoreplyText');
		};

		composeMessageTabValidation = function() {
			return validator.element('#messageText');
		};

		recipientTabValidation = function() {
			return isGroupChecked('dontSendMessage') ||
					recipientSelecter.validateImmediate();
		};

		confirmTabValidation = function() {
			return validator.element('input[name=name]');
		};

		mediumPopup.addValidation('poll-question', questionTabValidation);
		mediumPopup.addValidation('poll-response', responseTabValidation);
		mediumPopup.addValidation('poll-sort', autoSortTabValidation);
		mediumPopup.addValidation('poll-reply', autoReplyTabValidation);
		mediumPopup.addValidation('poll-edit-message', composeMessageTabValidation);
		mediumPopup.addValidation('poll-recipients', recipientTabValidation);
		mediumPopup.addValidation('poll-confirm', confirmTabValidation);

		$("#tabs").bind("tabsshow", function(event, ui) {
			updateSendMessage();
			updateConfirmationMessage();
		});
	};


	function updateSendMessage() {
		var choiceKeyword, keywordText, noAlias, questionText, replyText, sendMessage, yesAlias;
		// TODO check why these are being bound every time - surely could just bind when the page is loaded.
		$("#messageText").live("keypress", autoUpdateOff);
		$(".choices").live("keypress", autoUpdateOn);
		$("#question").live("keypress", autoUpdateOn);

		if(autoUpdate) {
			questionText = $("#question").val();
			if (questionText.substring(questionText.length - 1) !== '?') {
				questionText += '?';
			}
			questionText += '\n';
			keywordText = "";
			replyText = "";
			if (!$("#poll-keyword").attr("disabled")) {
				keywordText = (getFirstAlias($("#poll-keyword")) || "").toUpperCase();
				if($("input[name='pollType']:checked").val() === "yesNo") {
					yesAlias = getFirstAlias($("ul#poll-aliases li input#keywordsA"));
					noAlias = getFirstAlias($("ul#poll-aliases li input#keywordsB"));
					if(keywordText) {
						yesAlias = keywordText + " " + yesAlias;
						noAlias = keywordText + " " + noAlias;
					}
					replyText = i18n("poll.reply.text", yesAlias, noAlias);
				} else {
					replyText = i18n("poll.reply.text5");
					$(".choices").each(function() {
						if (replyText !== "Reply" && this.value) {
							replyText = replyText + ",";
						}
						if (this.value) {
							choiceKeyword = getFirstAlias($("ul#poll-aliases li input#keywords"+this.name.substring(6, 7)));
							if(keywordText) {
								choiceKeyword = keywordText + " " + choiceKeyword;
							}
							replyText = i18n("poll.reply.text1", replyText, choiceKeyword, this.value);
						}
					});
					replyText = replyText + '.';
				}
			} else if ($("input[name='pollType']:checked").val() === "yesNo") {
				replyText = i18n("poll.reply.text2");
			} else {
				replyText = i18n("poll.reply.text6")+ " ";
				$(".choices").each(function() {
					if(this.value) {
						if(replyText !== i18n("poll.reply.text6") + " ") {
							replyText += " " + i18n("poll.reply.text3");
						} else {
							replyText += "'" + this.value + "'";
						}
					}

				});
			}
			sendMessage = questionText + replyText;
			$("#messageText").val(sendMessage);
			$("#messageText").keyup();
			highlightPollResponses();
			setConfirmAliasValues();
		}
	}

	setAliasValues = function() {
		var aliasTextField, aliasTextFieldLabel, labelValue, myArray, myMap, yesNo;
		yesNo = $("input[name='pollType']:checked").val() === "yesNo";
		if (yesNo) {
			myMap = {A:"Yes", B:"No", C:"", D:"", E:""};
			$.each(myMap, function(key, value) {
				$("ul#poll-aliases li label:first[for='keywords"+key+"']").text(value);
				if(value === "") {
					$("ul#poll-aliases li input#keywords"+key).val('');
					$("ul#poll-aliases li input#keywords"+key).attr('disabled','disabled');
				} else {
					$("ul#poll-aliases li input#keywords"+key).removeAttr('disabled');
				}
			});
		} else {
			myArray = ['A', 'B', 'C', 'D', 'E'];
			$.each(myArray, function(index, value) {
				labelValue = $("ul#poll-choices li input#choice"+value).val().trim();
				aliasTextFieldLabel = $("ul#poll-aliases li label:first[for='keywords"+value+"']");
				aliasTextField = $("ul#poll-aliases li input#keywords"+value);
				if(labelValue.length === 0) {
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
	};

	function autoUpdateOff() {
		autoUpdate = false;
	}

	function autoUpdateOn() {
		autoUpdate = true;
	}

	updateConfirmationMessage = function() {
		var autoSort, autoSortMessages, currentVal, keyword;
		updateMessageDetails();

		currentVal = $("#autoreplyText").val();
		$("#auto-reply-read-only-text").html(currentVal.trim()? currentVal.htmlEncode(): i18n("autoreply.text.none"));
		// update auto-sort
		autoSort = $("input[name='enableKeyword']:checked").val();
		autoSortMessages = $('#auto-sort-confirm p');
		if(autoSort === 'true') {
			keyword = $("input[name='keyword']").val();
			autoSortMessages.eq(0).hide();
			autoSortMessages.eq(1).show();
			$('#auto-sort-confirm-keyword').text(keyword);
		} else {
			autoSortMessages.eq(0).show();
			autoSortMessages.eq(1).hide();
		}
		setConfirmAliasValues();
	};

	function updateMessageDetails() {
		var contactNo, sendMessage;
		sendMessage = isGroupChecked("dontSendMessage")?
				i18n("poll.send.messages.none"):
				$('#messageText').val().htmlEncode();
		contactNo = $("#contacts-count").text();

		if(contactNo === 0 || isGroupChecked("dontSendMessage")) {
			$("#confirm-recipients-count #sending-messages").hide();
			$("#no-recipients").show();
		} else {
			$("#confirm-recipients-count #sending-messages").show();
			$("#no-recipients").hide();
		}
		$("#poll-message").html('<p>' + sendMessage  + '</p>');
	}

	highlightNextPollResponse = function (choice) {
		var nextLabel, nextInput;
		if(choice.id !== "choiceA" && choice.id !== "choiceE") {
			nextLabel = $(choice).parent().next().find('label');
			nextInput = $(choice).parent().next().find('.choices');
			if (!choice.value.trim().length) {
				if (nextInput.val().trim().length === 0){
					nextLabel.removeClass('field-enabled');
					nextInput.attr('disabled', 'disabled');
				}
			} else {
				nextLabel.addClass('field-enabled');
				nextInput.removeAttr('disabled');
			}
		}
	};

	highlightPollResponses = function() {
		$(".choices").each(function() {
			highlightNextPollResponse(this);
		});
	};

	function setConfirmAliasValues() {
		var aliases, aliasText, choice, myArray, topLevelKeyword;
		myArray = ["A", "B", "C", "D", "E"];
		aliasText = "";
		topLevelKeyword = $("#poll-keyword").val();
		if($("#yesAutosort").attr("checked")) {
			if(topLevelKeyword) {
				aliasText += "<p>"+i18n("poll.toplevelkeyword")+" : "+ topLevelKeyword + "</p>";
			}
			if($("input[name='pollType']:checked").val() !== "yesNo") {
				$.each(myArray, function(index, value) {
					choice = $("ul#poll-choices li input#choice"+value).val();
					aliases = $("ul#poll-aliases li input#keywords"+value).val();
					if(choice.length !== 0) {
						aliasText += "<p>"+choice+" : "+aliases + "</p>";
					}
				});
			} else {
				aliasText += "<p>"+i18n("poll.yes") + " : " + $("ul#poll-aliases li input#keywordsA").val() + "</p>";
				aliasText += "<p>"+i18n("poll.no") + " : " + $("ul#poll-aliases li input#keywordsB").val() + "</p>";
			}
			$("#poll-confirm-aliases").html(aliasText);
		}
	}

	function getFirstAlias(field){
		var aliases = [];
		$.each(field.val().split(","), function(index, value){
			if(value.trim().length !== 0){
				aliases.push(value);
			}
		});
		return aliases[0];
	}

	setPollType = function() {
		if ($("input[name='pollType']:checked").val() === "yesNo") {
			mediumPopup.disableTab("poll-response");
			resetResponses();
			addRespectiveAliases();
		} else {
			mediumPopup.enableTab("poll-response");
			resetResponses();
		}
		autoUpdateOn();
		updateConfirmationMessage();
	};

	return {
		addCustomValidationClasses:addCustomValidationClasses,
		createFormValidator:createFormValidator,
		initializeTabValidation:initializeTabValidation,
		setAliasValues:setAliasValues,
		setPollType:setPollType,
		updateConfirmationMessage:updateConfirmationMessage
	};
}());

