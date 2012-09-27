<%@ page import="frontlinesms2.WebConnection" %>
<r:script>
	var webConnectionDialog = (function() {
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
				webConnectionDialog.resetValidator(scripts.validation);
				webConnectionDialog.updateConfirmationScreen = scripts.updateConfirmationScreen;
			},
			updateConfirmationScreen:_updateConfirmationScreen,
			___end___:null
		};
	})();

	function initializePopup() {
		var initialScripts = <fsms:render template="generic/scripts"/>;
		webConnectionDialog.setScripts(initialScripts);

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
			webConnectionDialog.updateConfirmationScreen();
		});
	}

	function setType(type) {
		$.getJSON(url_root + "webConnection/" + type + "/config", function(data) {
			var configTab = $("#webconnection-config");
			configTab.html(data.config);
			magicwand.init(configTab.find('select[id^="magicwand-select"]'));

			$("#webconnection-confirm").html(data.confirm);

			webConnectionDialog.setScripts(eval("(" + data.scripts + ")"));
			webConnectionDialog.updateConfirmationScreen();
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

