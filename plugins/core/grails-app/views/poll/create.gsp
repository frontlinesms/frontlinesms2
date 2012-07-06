<%@ page contentType="text/html;charset=UTF-8" %>
<meta name="layout" content="popup"/>

<div id="tabs" class="vertical-tabs">
	<div class="error-panel hide"><div id="error-icon"></div><g:message code="poll.validation.prompt"/></div>
	<ul>
		<li><a class="tabs-1" href="#tabs-1"><g:message code="poll.question"/></a></li>
		<li><a class="tabs-2" href="#tabs-2"><g:message code="poll.response"/></a></li>
		<li><a class="tabs-3" href="#tabs-3"><g:message code="poll.alias"/></a></li>
		<li><a class="tabs-4" href="#tabs-4"><g:message code="poll.sort"/></a></li>
		<li><a class="tabs-5" href="#tabs-5"><g:message code="poll.reply"/></a></li>
		<li><a class="tabs-6" href="#tabs-6"><g:message code="poll.edit.message"/></a></li>
		<li><a class="tabs-7" href="#tabs-7"><g:message code="poll.recipients"/></a></li>
		<li><a class="tabs-8" href="#tabs-8"><g:message code="poll.confirm"/></a></li>
	</ul>

	<g:formRemote url="[action: 'save', controller:'poll', params: [ownerId:activityInstanceToEdit?.id ?: null, format: 'json']]" name='new-poll-form' method="post" onSuccess="checkForSuccessfulSave(data, i18n('poll.label') )">
		<fsms:wizardTabs templates="
				/poll/question,
				/poll/responses,
				/poll/aliases,
				/poll/sorting,
				/poll/replies,
				/message/compose,
				/message/select_recipients,
				/poll/confirm,
				/poll/save"/>
	</g:formRemote>
</div>

<r:script>
	var autoUpdate = true;
	$("#messageText").live("keyup", updateSmsCharacterCount);
	$("button#nextPage").click(setAliasValues);

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
					var yesAlias = $("ul#poll-aliases li input#aliasA").val().split(",")[0].trim();
					var noAlias = $("ul#poll-aliases li input#aliasB").val().split(",")[0].trim();
					replyText = i18n("poll.reply.text", keywordText, yesAlias, keywordText, noAlias);
				} else {
					replyText = i18n("poll.reply.text5");
					$(".choices").each(function() {
						if (replyText != 'Reply' && this.value) replyText = replyText + ',';
						if (this.value) replyText = i18n("poll.reply.text1", replyText, keywordText, $("ul#poll-aliases li input#alias"+this.name.substring(6,7)).val().split(",")[0].trim(), this.value);
					});
					replyText = replyText + '.';
				}
			} else if ($("input[name='pollType']:checked").val() == "yesNo") {
				replyText = i18n("poll.reply.text2");
			} else {
				replyText = i18n("poll.reply.text6")+ ' ';
				$(".choices").each(function() {
					if (replyText!=i18n("poll.reply.text6")+ ' ' && this.value) replyText += ' ' + i18n("poll.reply.text3");
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

	function setAliasValues(){
		var yesNo = $("input[name='pollType']:checked").val() == "yesNo";
		if (yesNo) {
			var myMap = {'A':'Yes', 'B':'No', 'C':'', 'D':'', 'E':''};
			$.each(myMap, function(key, value){
				$("ul#poll-aliases li label[for='alias"+key+"']").text(value);
				if(value == ''){
					$("ul#poll-aliases li input#alias"+value).attr('disabled','disabled');
				}
			});
			addRespectiveAliases("anything will do");//Yes No polls do not have custom fields
		}else{
			var myArray = ['A', 'B', 'C', 'D', 'E'];
			$.each(myArray, function(index, value){
				var labelValue = $("ul#poll-choices li input#choice"+value).val().trim();
				var aliasTextFieldLabel = $("ul#poll-aliases li label[for='alias"+value+"']");
				var aliasTextField = $("ul#poll-aliases li input#alias"+value);
				if(labelValue.length == 0){
					aliasTextFieldLabel.text(value);
					aliasTextField.attr('disabled', 'disabled');
					aliasTextFieldLabel.removeClass("field-enabled");
				}else{
					aliasTextFieldLabel.text(labelValue);
					aliasTextFieldLabel.addClass("field-enabled");
				}
			});
		}
	}

	function addRespectiveAliases(field){
		var yesNo = $("input[name='pollType']:checked").val() == "yesNo";
		if (yesNo) {
			var aliasYesTextField = $("ul#poll-aliases li input#aliasA");
			var aliasNoTextField = $("ul#poll-aliases li input#aliasB");

			var choices = {'A,Yes':aliasYesTextField, 'B,No':aliasNoTextField};
			<% 	
				def pollResponse = activityInstanceToEdit?.responses.find {it.key == option} 
				def mode = pollResponse?"edit":"create"
			%>
			$.each(choices, function(key, value){
				if("${mode}" == "create"){
					if( value.val().trim().length == 0 ) value.val(key);
				}
			});
		}else{
			if($(field).hasClass("create")) {
				var aliases = "";
				var rawKey = $(field).attr('id').trim();
				var rawVal = $(field).val().trim();
				var value = rawVal.split(' ')[0]
				var key = rawKey.substring(rawKey.length-1);
				var aliasTextFieldLabel = $("ul#poll-aliases li label[for='alias"+value+"']");
				var aliasTextField = $("ul#poll-aliases li input#alias"+key);
				if(value.length > 0){
					aliases += key+","+value;
					aliasTextField.val(aliases);
					aliasTextField.removeAttr("disabled");
				}else{
					aliasTextFieldLabel.text("");
					aliasTextField.val("");
					aliasTextField.attr("disabled","disabled");
				}
			}
		}
	}
	
	function autoUpdateOff() {
		autoUpdate = false;
	}
	
	function autoUpdateOn() {
		autoUpdate = true;
	}

	function initializePopup() {
		<g:if test="${activityInstanceToEdit}">
			$("#messageText").trigger("keyup");
		</g:if>
		/* Poll type tab */
		$("#tabs-1").contentWidget({
			validate: function() {
				$("#question").removeClass('error');
				if ($("input[name='pollType']:checked").val() == "yesNo") {
					disableTab(1);
				} else {
					enableTab(1);
				}
				var isValid = $("input[name='dontSendMessage']").is(':checked') || !isElementEmpty($("#question"));
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
				var isValid =  $("input[name='pollType']:checked").val() == "yesNo" || validatePollResponses();
				if(!isValid) {
					if(isElementEmpty($('#choiceA'))) $('#choiceA').addClass('error');
					if(isElementEmpty($('#choiceB'))) $('#choiceB').addClass('error');
				}
				setAliasValues();
				return isValid;
			}
		});

		/* Aliases tab */
		$("#tabs-3").contentWidget({
			validate: function() {
				var isValid = true;
				var allAliases = ",";
				$('input.aliases').each(function() {
					var currentInput = $(this);
					currentInput.removeClass("invalid");
					if(currentInput.attr('disabled') != "disabled")
					{
						if(currentInput.val().trim().length == 0) {
							currentInput.addClass("invalid");
							isValid = false;
						}
						else {
							var aliases = currentInput.val().split(",");
							$.each(aliases, function(index, value) {
								if(allAliases.indexOf(","+value.toUpperCase()+",") == -1) {
									// alias not in allAliases
									allAliases += value.toUpperCase() + ","
								}
								else {
									// alias not unique
									isValid = false;
									currentInput.addClass("invalid");
								}
							});
						}
					}
				});
				return isValid;
			}
		});
		
		/* Auto-sort tab */
		$("#tabs-4").contentWidget({
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
		$("#tabs-5").contentWidget({
			validate: function() {
				$('#tabs-5 textarea').removeClass("error");
				var isValid = !isGroupChecked('enableAutoReply') || !(isElementEmpty('#tabs-4 textarea'));
				if(!isValid) {
					$('#tabs-5 textarea').addClass("error");
				}
				return isValid;
			}
		});
		
		$("#tabs-7").contentWidget({
			validate: function() {
				if(!isGroupChecked('dontSendMessage')) {
					addAddressHandler();
					return isGroupChecked('addresses');
				}
				return true;
			}
		});

		$("#tabs-8").contentWidget({
			validate: function() {
				$("#tabs-8 #name").removeClass("error");
				var isEmpty = isElementEmpty($("#tabs-8 #name"));
				if(isEmpty) {
					$("#tabs-8 #name").addClass("error");
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
		//setConfirmAliasValues();

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
		$(".choices").each(function(index){
			highlightNextPollResponse(this);
		});
	}

	function validatePollResponses() {
		return !isElementEmpty($("#choiceA")) && !isElementEmpty($("#choiceB"))
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
</r:script>
