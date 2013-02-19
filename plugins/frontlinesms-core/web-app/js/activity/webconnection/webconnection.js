var webconnectionDialog = (function () {
	var pollInterval, _updateConfirmationScreen, validationMessageGenerator, _showTestRouteBtn, _toggleWizardButtons,
			_setType, _handlers, _testRouteStatus, _checkRouteStatus, generateMessages, _toggleApiTab;
	_updateConfirmationScreen = function () {};
	_setType = function(type) {
		$.getJSON(url_root + "webconnection/" + type + "/config", function(data) {
			var configTab, confirmTab; 
			configTab = $("#webconnection-config");
			confirmTab = $("#webconnection-confirm");
			configTab.html(data.config);
			confirmTab.html(data.confirm);
			magicwand.init(configTab.find('select[id^="magicwand-select"]'));

			$("#webconnection-confirm").html(data.confirm);
			webconnectionDialog.setScripts(eval("(" + data.scripts + ")"));
			webconnectionDialog.updateConfirmationScreen();
		});
	};

	_handlers = {};
	validationMessageGenerator = function(fieldName, ruleName) {
		var i18nKey, i18nString;
		i18nKey = "webconnection." + fieldName + ".validation.error";
		i18nString = i18n(i18nKey);
		return i18nKey === i18nString? "": i18nString;
	};
	_showTestRouteBtn = function() {
		var buttonSet, testRouteBtn; 
		buttonSet = $('.ui-dialog-buttonset');
		testRouteBtn = buttonSet.find("#testRoute");
		if(testRouteBtn.length === 0) {
			testRouteBtn = mediumPopup.appendButton("testRoute", "submit", i18n('webconnection.testroute.label'));
			testRouteBtn.bind({
				click: _testRouteStatus
			});
		} else {
			testRouteBtn.show();
		}
		buttonSet.append(testRouteBtn);
	};

	_testRouteStatus = function() {
		var params = {};
		if(mediumPopup.tabValidates(mediumPopup.getCurrentTab())) {
			params.ownerId = $("#activityId").val();
			params.format = "json";
			$.ajax({
				type: 'post',
				data: $("#new-webconnection-form").serialize() + "&" + $.param(params),
				url: url_root + "webconnection/testRoute",
				success: function(data, textStatus) {_checkRouteStatus(data);}
			});
		} else {
			$('.error-panel').show();
		}
		return false;
	};

	_toggleWizardButtons = function() {
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
	}
	
	_checkRouteStatus = function(response) {
		if(response.ok) {
			$("#testRoute").children().remove();
			$("#testRoute").append("<span>"+i18n('webconnection.testing.label')+"</span>");
			$("#activityId").val(response.ownerId);
			$.ajaxSetup({
				type: 'post',
				data: {ownerId:response.ownerId},
				url: url_root + "webconnection/checkRouteStatus"
			});
			_toggleWizardButtons();
			pollInterval = setInterval( function() {
				$.ajax({
					success: function(response) {
								if(response.status === "success" || response.status === "failed") {
									$(".error-panel").text(i18n('webconnection.popup.'+ response.status + '.label'));
									$(".error-panel").show();
									_toggleWizardButtons();
									if(response.status === "success") {
										loadSummaryTab(response, i18n('webconnection.label'));
									} else {
										$("#testRoute").children().remove();
										$("#testRoute").append("<span>"+i18n('webconnection.testroute.label')+"</span>");
									}
									clearInterval(pollInterval);
								}
							}
				});
			}, 3000);

		} else {
			displayErrors(response);
		}
	};

	_toggleApiTab = function() {
		$("input[name=webconnectionType]").live('change', function() {
			if($(this).val() === 'generic') {
				mediumPopup.enableTab('webconnection-api');
			} else {
				mediumPopup.disableTab('webconnection-api');
			}
		});
	};
	
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
	};

	return {
		resetValidator: function(messageRules) {
			$("#new-webconnection-form").data("validator", null);
			validator = $("#new-webconnection-form").validate({
				errorContainer:".error-panel",
				messages:generateMessages(messageRules)
			});
		},
		addValidationRules: function() {
			var keyWordTabValidation, configureTabValidation, confirmTabValidation;

			aliasCustomValidation();
			genericSortingValidation();

			keyWordTabValidation = function() {
				var valid;
				if(!isGroupChecked("blankKeyword")) { valid = validator.element('#keywords'); }
				else { valid = true; }
				return valid;
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
		setScripts: function(scripts) {
			webconnectionDialog.resetValidator(scripts.validation);
			webconnectionDialog.updateConfirmationScreen = scripts.updateConfirmationScreen;
			webconnectionDialog.handlers = scripts.handlers;
		},
		updateConfirmationScreen:_updateConfirmationScreen,
		handlers:_handlers,
		setType:_setType,
		showTestRouteBtn:_showTestRouteBtn,
		toggleApiTab:_toggleApiTab,
		___end___:null
	};
}());

