var custom_activity = (function() {
	var
	CONFIG_CONTAINER = "#custom-activity-config-container",
	random = function() { return Math.floor(Math.random() * 9007199254740992); },
	addStep = function(stepName) {
		sanchez.append(CONFIG_CONTAINER, "step-" + stepName, { stepId:'', groupId:"", autoreplyText:"", random:random() });
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
		titles = $(CONFIG_CONTAINER + " .step:not(.reply) h4");
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

// TODO this can probably be combined with custom_activity
var customActivityDialog = (function(){
	var 
	addValidationRules = function() {
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
				return validator.element("#keywords");
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
			setJsonToSend();
			return validator.element("input[name=name]");
		};
		customValidationForGroups();

		mediumPopup.addValidation("activity-generic-sorting", keyWordTabValidation);
		mediumPopup.addValidation("customactivity-config", stepActionsValidation);
		mediumPopup.addValidation("customactivity-confirm", confirmTabValidation);
	},

	setJsonToSend = function() {
		var data = $("li.step").map(function(index, element) {
			return getStepProperties("select.customactivity-field,input.customactivity-field,textarea.customactivity-field",
					$(element));
		}).get();
		$("#jsonToSubmit").val(JSON.stringify(data));
	},

	getStepProperties = function(fieldSelecter, container) {
		var data = {};
		container.find(fieldSelecter).each(function(index, field) {
			field = $(field);
			data[field.attr("name")] = field.val();
		});
		return data;
	},

	updateConfirmationMessage = function() {
		var container, keywords;
		container = $("#customactivity-confirm-action-steps");
		container.html("");
		$.each($("#custom-activity-config-container .step"), function(index, element) {
			var groupValue, output, stepType;
			element = $(element);
			stepType = element.find("input[name=stepType]").val();
			if(stepType === "join" || stepType === "leave") {
				detail = element.find("select[name=group] option:selected").text();
			} else if(stepType === "reply") {
				detail = element.find("textarea[name=autoreplyText]").val();
			}
			alert("Step type: " + stepType);
			output = "<p>" +
					i18n("customactivity." + stepType + ".description", detail) +
					"</p>";
			container.append(output);
		});

		sortingType = $("input[name=sorting]:checked").val();
		if(sortingType === "enabled") {
			keywords = $("#keywords").val().toUpperCase();
		} else if (sortingType === "global") {
			keywords = i18n("autoreply.blank.keyword");
		} else {
			alert("please define i18n message for this sorting option");
		}
		$("#keyword-confirm").html("<p>" + keywords  + "</p>");
	};

	return {
		addValidationRules:addValidationRules
	};
}());

// FIXME this function should NOT be global
function refreshPageWithStepMessages(c) {
	var val = $(c).val();
	if(val === "na") {
		window.location = url_root + "message/activity/" + more_actions.getOwnerId();
	} else {
		window.location = url_root + "message/activity/" + more_actions.getOwnerId() + "/step/" + val;
	}
}

