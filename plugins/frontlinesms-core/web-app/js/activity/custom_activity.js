var custom_activity = (function() {
	var
	addStep = function(stepName) {
		sanchez.append("#custom-activity-config-container", "step-" + stepName, { stepId:'', groupId:'', autoreplyText:'' });
	},
	removeStep = function() {
		var p = $(this).parent().parent();
		p.fadeOut(300, function() { $(this).remove(); });
	},
	initAddStepButton = function(stepName) {
		$("#add-" + stepName + "-action-step").click(
			function() {
				addStep(stepName);
			});
	},
	init = function() {
		var i, steps = custom_activity.steps;
		for(i=steps.length-1; i>=0; --i) {
			initAddStepButton(steps[i]);
		}
		$('.remove-step').live("click", removeStep);
	};
	return {
		init:init
	};
}());

var customActivityDialog = (function(){
	var 
	_addValidationRules = function() {
		var 
		validator = $("#create_customactivity").validate({
			errorContainer: ".error-panel",
			rules: {
				autoreplyText: { required:true },
				name: { required:true }
			},
			errorPlacement: function(error, element){
				$(".error-panel").html(error);
			}
		}),
		keyWordTabValidation = function() {
			if(!isGroupChecked("blankKeyword")){
				return validator.element('#keywords');
			}
			return true;
		},
		stepActionsValidation = function() {
			var valid = true;
			updateConfirmationMessage();

			$.each($("textarea[name='autoreplyText']"), function(index, element) {
				valid = valid && validator.element($(element));
			});

			$.each($("select[name='group']"), function(index, element) {
				valid = valid && validator.element($(element));
			});

			return valid;
		},
		confirmTabValidation = function() {
			customActivityDialog.setJsonToSend();
			return validator.element('input[name=name]');
		};
		customValidationForGroups();

		mediumPopup.addValidation('activity-generic-sorting', keyWordTabValidation);
		mediumPopup.addValidation('customactivity-config', stepActionsValidation);
		mediumPopup.addValidation('customactivity-confirm', confirmTabValidation);
	},
	_setJsonToSend = function() {
		var jsonToSend = "", data = [];

		$.each($("li.step"), function(index, element){
			var dataToSend = {}, 
				stepDiv = $(element);
			dataToSend.stepId = stepDiv.attr("index");
			dataToSend.stepType = stepDiv.find("#stepType").val();
			if(stepDiv.find("input").size() > 0) {
				dataToSend.stepProperties = getStepProperties("input", stepDiv);
			}
			
			if(stepDiv.find("textarea").size() > 0) {
				dataToSend.stepProperties = getStepProperties("textarea", stepDiv);
			}
			
			if(stepDiv.find("select").size() > 0) {
				dataToSend.stepProperties = getStepProperties("select", stepDiv);
			}

			data.push(dataToSend);
		});
		$("#jsonToSubmit").val(JSON.stringify(data));
	},

	getStepProperties = function(inputType, container) {
		var stepProperties = [];

		$.each(container.find(inputType), function(index, element) {
			var inputField = $(element),
				key = inputField.attr("name"),
				value = inputField.val(),
				property = {};
			property.key = key;
			property.value = value;
			stepProperties.push(property);
		});

		return stepProperties;
	},

	updateConfirmationMessage = function() {
		var container, keywords;
		container = $('#customactivity-confirm-action-steps');
		container.html("");
		$.each($(".step"), function(index, element) {
			var groupName, groupValue, messageText, output, stepType;
			output = "";
			stepType = $(element).find('input#stepType').val();
			if(stepType === 'join') {
				groupValue = $(element).find('select[name=group]').val();
				groupName = $(element).find('select[name=group]').find("option[value="+groupValue+"]").text();
				output = i18n("customactivity.join.description", groupName);
				output = "<p>"+output+"</p>";
			}
			if(stepType === "leave") {
				groupValue = $(element).find('select[name=group]').val();
				groupName = $(element).find('select[name=group]').find("option[value="+groupValue+"]").text();
				output = i18n("customactivity.leave.description", groupName);
				output = "<p>"+output+"</p>";
			}
			if(stepType === "reply") {
				messageText = $(element).find('textarea[name=autoreplyText]').val();
				output = i18n("customactivity.reply.description", messageText);
				output = "<p>"+output+"</p>";
			}
			container.append(output);
		});

		if(!(isGroupChecked("blankKeyword"))){
			keywords = $('#keywords').val().toUpperCase();
			$("#keyword-confirm").html('<p>' + keywords  + '</p>');
		} else {
			$("#keyword-confirm").html('<p>' + i18n("autoreply.blank.keyword")  + '</p>');
		}
	};

	return {
		addValidationRules:_addValidationRules,
		setJsonToSend:_setJsonToSend
	};
}());

function refreshPageWithStepMessages(c) {
	if($(c).val() === 'na') {
		window.location = url_root + "message/activity/" + more_actions.getOwnerId();
	} else {
		window.location = url_root + "message/activity/" + more_actions.getOwnerId() +"/step/"+$(c).val();
	}
}
