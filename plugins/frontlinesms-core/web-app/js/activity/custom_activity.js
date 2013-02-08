var custom_activity = (function() {
	var
	CONFIG_CONTAINER = "#custom-activity-config-container",
	random = function() { return Math.floor(Math.random() * 9007199254740992) },
	addStep = function(stepName) {
		sanchez.append(CONFIG_CONTAINER, "step-" + stepName, { stepId:'', groupId:'', autoreplyText:'', random:random() });
		initSteps();
	},
	removeStep = function() {
		var p = $(this).closest(".step");
		p.hide(300, function() { $(this).remove(); });
	},
	initSteps = function() {
		var titles, widths, maxWidth;
		$(CONFIG_CONTAINER + " .step .remove-command").click(removeStep);
		selectmenuTools.initAll(CONFIG_CONTAINER + " select");
		magicwand.init($(CONFIG_CONTAINER + " select[id^='magicwand-select']"));

		// calculate the length of the longest title here
		// and force other steps to conform to that...
		titles = $(CONFIG_CONTAINER + " .step:not(.reply) h4")
		widths = titles.map(function() { return $(this).width(); });
		maxWidth = Math.max.apply(null, widths.get());
		titles.map(function() { $(this).width(maxWidth); });
	},
	init = function() {
		$(CONFIG_CONTAINER).sortable();
		// Defer creation of selectmenus and magic wand widgets until
		// they're actually visible...
		$("#tabs").bind("tabsshow", function(event, ui) {
			if(ui.index === 1) {
				initSteps();
			}
		});
	};
	return {
		addStep:addStep,
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
			}
			if(stepType === "leave") {
				groupValue = $(element).find('select[name=group]').val();
				groupName = $(element).find('select[name=group]').find("option[value="+groupValue+"]").text();
				output = i18n("customactivity.leave.description", groupName);
			}
			if(stepType === "reply") {
				messageText = $(element).find('textarea[name=autoreplyText]').val();
				console.log(messageText);
				output = i18n("customactivity.reply.description", messageText);
			}
			output = "<p>"+output+"</p>";
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
