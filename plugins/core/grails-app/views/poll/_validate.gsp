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
			disableTab("poll-response");
			disableTab("poll-alias");
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
		jQuery.validator.addMethod("aliases", function(value, element) {
			var isValid = true;
			var allAliases = {}
			$('input:not(:disabled).aliases').each(function() {
				var currentInput = $(this);
				var aliases = currentInput.val().split(",");
				$.each(aliases, function(index, value) {
					alias = value.trim();
					if(alias.length != 0){
						alias = alias.toUpperCase();
						if((alias in allAliases) && (element.id == currentInput.attr("id"))) {
							isValid = false; return;
						}
						else {
							allAliases[alias] = true;
						}
					}
				});
				if(!isValid) { return; }
			});
			return isValid;
		}, i18n("poll.alias.validation.error"));

		jQuery.validator.addMethod("validcommas", function(value, element) {
			return value.match(/^(\s*,*\s*[\w-]+\s*,*\s*)(,*\s*[\w-]+\s*,*\s*)*$/) !== null;
		}, i18n("poll.alias.validation.error.invalid.alias"));
		
		jQuery.validator.addMethod("edit", function(value, element) {
			return (value.trim().length != 0);
		}, i18n("poll.choice.validation.error.deleting.response"));
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
			$('input:not(:disabled).aliases').each(function() {
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

		addValidation('poll-question', questionTabValidation);
		addValidation('poll-response', responseTabValidation);
		addValidation('poll-sort', autoSortTabValidation);
		addValidation('poll-alias', aliasTabValidation);
		addValidation('poll-reply', autoReplyTabValidation);
		addValidation('poll-edit-message', composeMessageTabValidation)
		addValidation('poll-recipients', recipientTabValidation);
		addValidation('poll-confirm', confirmTabValidation);

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
					var yesAlias = getFirstAlias($("ul#poll-aliases li input#aliasA"))
					var noAlias = getFirstAlias($("ul#poll-aliases li input#aliasB"))
					replyText = i18n("poll.reply.text", keywordText, yesAlias, keywordText, noAlias);
				} else {
					replyText = i18n("poll.reply.text5");
					$(".choices").each(function() {
						if (replyText != 'Reply' && this.value) replyText = replyText + ',';
						if (this.value) replyText = i18n("poll.reply.text1", replyText, keywordText, getFirstAlias($("ul#poll-aliases li input#alias"+this.name.substring(6,7))), this.value);
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
				$("ul#poll-aliases li label:first[for='alias"+key+"']").text(value);
				if(value == '') {
					$("ul#poll-aliases li input#alias"+key).val('');
					$("ul#poll-aliases li input#alias"+key).attr('disabled','disabled');
				} else {
					$("ul#poll-aliases li input#alias"+key).removeAttr('disabled');
				}
			});
		} else {
			var myArray = ['A', 'B', 'C', 'D', 'E'];
			$.each(myArray, function(index, value) {
				var labelValue = $("ul#poll-choices li input#choice"+value).val().trim();
				var aliasTextFieldLabel = $("ul#poll-aliases li label:first[for='alias"+value+"']");
				var aliasTextField = $("ul#poll-aliases li input#alias"+value);
				if(labelValue.length == 0) {
					aliasTextFieldLabel.text(value);
					aliasTextField.attr('disabled', 'disabled');
					aliasTextFieldLabel.removeClass("field-enabled");
				} else {
					aliasTextFieldLabel.text(labelValue);
					aliasTextFieldLabel.addClass("field-enabled");
					$("ul#poll-aliases li input#alias"+value).removeAttr('disabled');
				}
			});
		}
	}

	function addRespectiveAliases(field) {
		var yesNo = $("input[name='pollType']:checked").val() == "yesNo";
		if(yesNo) {
			var aliasYesTextField = $("ul#poll-aliases li input#aliasA");
			var aliasNoTextField = $("ul#poll-aliases li input#aliasB");
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
				if("${mode}" == "create") {
					if( value.val().trim().length == 0 ) value.val(key);
				}
			});
		} else {
			var aliases = "";
			var rawKey = $(field).attr('id').trim();
			var rawVal = $(field).val().trim();
			var value = rawVal.split(' ')[0]
			var key = rawKey.substring(rawKey.length-1);
			var aliasTextFieldLabel = $("ul#poll-aliases li label[for='alias" + value + "']");
			var aliasTextField = $("ul#poll-aliases li input#alias" + key);
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
			if (!$.trim(choice.value).length) {
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
				var aliases = $("ul#poll-aliases li input#alias"+value).val();
				if(choice.length != 0){
					aliasText += "<p>"+choice+" : "+aliases + "</p>";
				}
			});
		}
		else {
			aliasText += "<p>"+i18n("poll.yes") + " : " + $("ul#poll-aliases li input#aliasA").val() + "</p>";
			aliasText += "<p>"+i18n("poll.no") + " : " + $("ul#poll-aliases li input#aliasB").val() + "</p>";
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
