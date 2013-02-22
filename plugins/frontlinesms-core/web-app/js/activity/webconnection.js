var webconnectionDialog = (function () {
	var
	updateConfirmationScreen = function () {},
	setScripts =  function(scripts) {
		resetValidator(scripts.validation);
		webconnectionDialog.updateConfirmationScreen = scripts.updateConfirmationScreen;
		webconnectionDialog.handlers = scripts.handlers;
	},
	setType = function(type) {
		$.getJSON(url_root + "webconnection/" + type + "/config", function(data) {
			var configTab, confirmTab; 
			configTab = $("#webconnection-config");
			confirmTab = $("#webconnection-confirm");
			configTab.html(data.config);
			confirmTab.html(data.confirm);
			magicwand.init(configTab.find('select[id^="magicwand-select"]'));

			$("#webconnection-confirm").html(data.confirm);
			setScripts(eval("(" + data.scripts + ")"));
			updateConfirmationScreen();
		});
	},
	validationMessageGenerator = function(fieldName, ruleName) {
		var i18nKey, i18nString;
		i18nKey = "webconnection." + fieldName + ".validation.error";
		i18nString = i18n(i18nKey);
		return i18nKey === i18nString? "": i18nString;
	},
	showTestRouteBtn = function() {
		var buttonSet, testRouteBtn; 
		buttonSet = $('.ui-dialog-buttonset');
		testRouteBtn = buttonSet.find("#testRoute");
		if(testRouteBtn.length === 0) {
			testRouteBtn = mediumPopup.appendButton("testRoute", "submit", i18n('webconnection.testroute.label'));
			testRouteBtn.bind({
				click: testRouteStatus
			});
		} else {
			testRouteBtn.show();
		}
		buttonSet.append(testRouteBtn);
	},
	testRouteStatus = function() {
		var params = {};
		if(mediumPopup.tabValidates(mediumPopup.getCurrentTab())) {
			params.ownerId = $("#activityId").val();
			params.format = "json";
			$.ajax({
				type:"POST",
				data:$("#new-webconnection-form").serialize() + "&" + $.param(params),
				url:url_root + "webconnection/testRoute",
				success:checkRouteStatus
			});
		} else {
			$('.error-panel').show();
		}
		return false;
	},
	toggleWizardButtons = function() {
		if($("#submit").is(":disabled")) {
			$("#testRoute").attr('disabled', false);
			$("#submit").attr('disabled', false);
			$("#cancel").attr('disabled', false);
			$("#prevPage").attr('disabled', false);
		} else {
			$("#testRoute").attr('disabled', "disabled");
			$("#submit").attr('disabled', "disabled");
			$("#cancel").attr('disabled', "disabled");
			$("#prevPage").attr('disabled', "disabled");
		}
	},
	processCheckRouteResponse = function(response) {
		response = response.webconnection_status;
		if(!response) { return; }
		if(response.status !== "success" && response.status !== "failed") {
			// checking still in progress...
			return;
		}
		$(".error-panel").text(i18n('webconnection.popup.'+ response.status + '.label'));
		$(".error-panel").show();
		toggleWizardButtons();
		if(response.status === "success") {
			loadSummaryTab(response, i18n('webconnection.label'));
		} else {
			$("#testRoute").children().remove();
			$("#testRoute").append("<span>"+i18n('webconnection.testroute.label')+"</span>");
		}
		app_info.stopListening("webconnection_status");
	},
	checkRouteStatus = function(response) {
		if(response.ok) {
			$("#testRoute").children().remove();
			$("#testRoute").append("<span>"+i18n('webconnection.testing.label')+"</span>");
			$("#activityId").val(response.ownerId);
			toggleWizardButtons();
			app_info.listen("webconnection_status", { ownerId:response.ownerId }, processCheckRouteResponse);
		} else {
			displayErrors(response);
		}
	},
	toggleApiTab = function() {
		$("input[name=webconnectionType]").live('change', function() {
			if($(this).val() === 'generic') {
				mediumPopup.enableTab('webconnection-api');
			} else {
				mediumPopup.disableTab('webconnection-api');
			}
		});
	},
	generateMessages = function(fieldsAndRules) {
		var i, rules, field, messageMap = {};
		for(field in fieldsAndRules) {
			rules = fieldsAndRules[field];
			messageMap[field] = {};
			if(typeof(rules) === "string") {
				messageMap[field][rules] = validationMessageGenerator(field, rules);
			} else {
				for(i=0; i<rules.length; ++i) {
					messageMap[field][rules[i]] = validationMessageGenerator(field, rules[i]);
				}
			}
		}
		return messageMap;
	},
	addValidationRules =  function() {
		var keyWordTabValidation, configureTabValidation, confirmTabValidation;

		aliasCustomValidation();
		genericSortingValidation();

		keyWordTabValidation = function() {
			return !isGroupChecked("blankKeyword") || validator.element('#keywords');
		};

		configureTabValidation = function() {
			var isValid = true;
			$('#webconnection-config input:visible').each(function() {
				isValid = isValid && validator.element(this);
			});
			return isValid;
		};

		confirmTabValidation = function() {
			return validator.element('input[name=name]');
		};

		mediumPopup.addValidation('activity-generic-sorting', keyWordTabValidation);
		mediumPopup.addValidation('webconnection-configure', configureTabValidation);
		mediumPopup.addValidation('webconnection-confirm', confirmTabValidation);
	},
	resetValidator = function(messageRules) {
		$("#new-webconnection-form").data("validator", null);
		validator = $("#new-webconnection-form").validate({
			errorContainer:".error-panel",
			messages:generateMessages(messageRules)
		});
	};

	return {
		resetValidator:resetValidator,
		addValidationRules:addValidationRules,
		setScripts:setScripts,
		updateConfirmationScreen:updateConfirmationScreen,
		handlers:{},
		setType:setType,
		showTestRouteBtn:showTestRouteBtn,
		toggleApiTab:toggleApiTab
	}
}());

