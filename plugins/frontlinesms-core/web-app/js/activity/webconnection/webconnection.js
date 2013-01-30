var webconnectionDialog = (function() {
	var _updateConfirmationScreen = function() {};
	var validationMessageGenerator = function(fieldName, ruleName) {
		var i18nKey = "webconnection." + fieldName + ".validation.error";
		var i18nString = i18n(i18nKey);
		return i18nKey == i18nString? "": i18nString;
	};
	var _showTestRouteBtn = function() {
		var buttonSet, testRouteBtn; 
		buttonSet = $('.ui-dialog-buttonset');
		testRouteBtn = buttonSet.find("#testRoute");
		if(testRouteBtn.length === 0) {
			testRouteBtn = $('<input/>', {
								id: "testRoute",
								type:"submit",
								value: i18n('webconnection.testroute.label'),
								click: testRouteStatus
							});
		} else {
			testRouteBtn.show();
		}
		buttonSet.append(testRouteBtn);
	};
	var _handlers = {}
	var generateMessages = function(fieldsAndRules) {
		var messageMap = {};
		for(field in fieldsAndRules) {
			var i, rules = fieldsAndRules[field];
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

	function toggleWizardButtons() {
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
	};

	var pollInterval;

	function _checkRouteStatus(response) {
		if(response.ok) {
			$("#testRoute").attr("value", i18n('webconnection.testing.label'));
			$.ajaxSetup({
				type: 'post',
				data: {ownerId:response.ownerId},
				url: url_root + "webconnection/checkRouteStatus"
			});
			toggleWizardButtons();
			pollInterval = setInterval( function() {
				$.ajax({
					success: function(response) {
								if(response.status === "success" || response.status === "failed") {
									$(".error-panel").text(i18n('webconnection.popup.'+ response.status + '.label'));
									$(".error-panel").show();
									toggleWizardButtons();
									if(response.status === "success") {
										loadSummaryTab(response, i18n('webconnection.label'));
									} else {
										$("#testRoute").attr("value", i18n('webconnection.testroute.label'));
									}
									clearInterval(pollInterval);
								}
							}
				});	
			}, 3000);

		} else {
			displayErrors(response)
		}
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
			aliasCustomValidation();
			genericSortingValidation();

			var keyWordTabValidation = function() {
				 if(!isGroupChecked("blankKeyword")) return validator.element('#keywords');
				 else return true;
			};

			var configureTabValidation = function() {
				var isValid = true;
				$('#webconnection-config input:visible').each(function() {
					isValid = isValid && validator.element(this);
				});
				return isValid;
			};

			var confirmTabValidation = function() {
				return validator.element('input[name=name]');
			};

			mediumPopup.addValidation('activity-generic-sorting', keyWordTabValidation);
			mediumPopup.addValidation('webconnection-configure', configureTabValidation);
			mediumPopup.addValidation('webconnection-confirm', confirmTabValidation);
		},
		setScripts: function(scripts) {
			webconnectionDialog.resetValidator(scripts.validation);
			webconnectionDialog.updateConfirmationScreen = scripts.updateConfirmationScreen;
			webconnectionDialog.handlers = scripts.handlers
		},
		updateConfirmationScreen:_updateConfirmationScreen,
		handlers:_handlers,
		showTestRouteBtn:_showTestRouteBtn,
		checkRouteStatus:_checkRouteStatus,
		___end___:null
	};
})();

function setPara(selecter, text) {
	$(selecter).html("<p>" + text + "</p>");
}

function toggleApiTab() {
	$("input[name=webconnectionType]").live('change', function() {
		if($(this).val() === 'generic') {
			mediumPopup.enableTab('webconnection-api');
		} else {
			mediumPopup.disableTab('webconnection-api');
		}
	});
}

