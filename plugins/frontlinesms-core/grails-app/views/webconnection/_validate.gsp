<%@ page import="frontlinesms2.Webconnection" %>
<r:script>
	var webconnectionDialog = (function() {
		var _updateConfirmationScreen = function() {};
		var validationMessageGenerator = function(fieldName, ruleName) {
			var i18nKey = "webconnection." + fieldName + ".validation.error";
			var i18nString = i18n(i18nKey);
			return i18nKey == i18nString? "": i18nString;
		};
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

		return {
			resetValidator: function(messageRules) {
				$("#new-webconnection-form").data("validator", null);
				validator = $("#new-webconnection-form").validate({
					errorContainer:".error-panel",
					messages:generateMessages(messageRules)
				});
			},
			setScripts: function(scripts) {
				webconnectionDialog.resetValidator(scripts.validation);
				webconnectionDialog.updateConfirmationScreen = scripts.updateConfirmationScreen;
			},
			updateConfirmationScreen:_updateConfirmationScreen,
			___end___:null
		};
	})();

	function initializePopup() {
		var initialScripts = <fsms:render template="/webconnection/generic/scripts"/>;
		webconnectionDialog.setScripts(initialScripts);

		var keyWordTabValidation = function() {
			return isGroupChecked("blankKeyword") || validator.element("#keyword");
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

		addValidation('webconnection-sorting', keyWordTabValidation);
		addValidation('webconnection-configure', configureTabValidation);
		addValidation('webconnection-confirm', confirmTabValidation);

		$("#tabs").bind("tabsshow", function(event, ui) {
			updateConfirmationMessage();
			webconnectionDialog.updateConfirmationScreen();
		});
	}

	function setType(type) {
		$.getJSON(url_root + "webconnection/" + type + "/config", function(data) {
			var configTab = $("#webconnection-config");
			var confirmTab = $("#webconnection-confirm");
			configTab.html(data.config);
			confirmTab.html(data.confirm);
			magicwand.init(configTab.find('select[id^="magicwand-select"]'));

			$("#webconnection-confirm").html(data.confirm);

			webconnectionDialog.setScripts(eval("(" + data.scripts + ")"));
			webconnectionDialog.updateConfirmationScreen();
		});
	}

	function setPara(selecter, text) {
		$(selecter).html("<p>" + text + "</p>");
	}
	
	function updateConfirmationMessage() {
		var keywordConfirmationText;
		if(!(isGroupChecked("blankKeyword"))) {
			keywordConfirmationText = $('#keyword').val().toUpperCase();
		} else {
			keywordConfirmationText = i18n("autoreply.blank.keyword");
		}
		setPara("#keyword-confirm", keywordConfirmationText);
		setPara("#autoreply-confirm", $('#messageText').val());
	}	
</r:script>

