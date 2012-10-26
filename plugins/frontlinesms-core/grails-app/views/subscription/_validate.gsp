<r:script>
	function initializePopup() {
		initializeTabValidation(createFormValidator());
		sortingOptionChanged();
		addCustomValidationClasses();
		updateAliasTips();
		console.log("called AddCustomVal");
	}

	function createFormValidator() {
		var validator = $("#create_subscription").validate({
			errorContainer: ".error-panel",
			rules: {
				addresses: {
					required: true,
					minlength: 1
				},
				messageText: {
					required:true
				}
			},
			messages: {
				addresses: {
					required: i18n("poll.recipients.validation.error")
				},
				"keyword": {
					required: i18n("subscription.keyword.required")
				},
				"joinAutoreplyText": {
					required: i18n("subscription.jointext.required")
				},
				"leaveAutoreplyText": {
					required: i18n("subscription.leavetext.required")
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

	function initializeTabValidation(validator) {
		var groupTabValidation = function() {
			return validator.element($('#subscriptionGroup'));
		};

		var sortingTabValidation = function() {
			var valid = true;
			$('input:not(:disabled).keywords').each(function() {
				if (!validator.element(this) && valid) {
				    valid = false;
				}
			});
			return validator.element('#topLevelKeywords') && valid;
		};

		var autoreplyTabValidation = function() {
			var valid = true;
			if($('#joinAutoreplyText').attr('disabled') != 'disabled'){
				valid = valid && validator.element($('#joinAutoreplyText'));
			}
			if($('#leaveAutoreplyText').attr('disabled') != 'disabled'){
				valid = valid && validator.element($('#leaveAutoreplyText'));
			}
			return valid;
		};

		var confirmTabValidation = function() {
			return validator.element('#name');
		};

		mediumPopup.addValidation('subscription-group-header', groupTabValidation);
		mediumPopup.addValidation('subscription-sorting', sortingTabValidation);
		mediumPopup.addValidation('subscription-autoreplies', autoreplyTabValidation);
		mediumPopup.addValidation('subscription-confirm', confirmTabValidation);
		
		$("#tabs").bind("tabsshow", function(event, ui) {
			updateConfirmTab();
		});
	}

	function updateConfirmTab() {
		var state = $("input:radio[name=sorting]:checked").val()  == 'enabled';
		$("#confirm-group-text").html($("#subscriptionGroup option:selected").text()? $("#subscriptionGroup option:selected").text():i18n("announcement.message.none"));
		$("#confirm-keyword-text").html($("#topLevelKeywords").val() && state ? $("#topLevelKeywords").val():i18n("announcement.message.none"));
		$("#confirm-join-alias-text").html($("#joinKeywords").val() && state? $("#joinKeywords").val():i18n("announcement.message.none"));
		$("#confirm-leave-alias-text").html($("#leaveKeywords").val() && state? $("#leaveKeywords").val():i18n("announcement.message.none"));
		$("#confirm-default-action-text").html($("#defaultAction").val()? $("#defaultAction:checked").val():i18n("announcement.message.none"));
		$("#confirm-join-autoreply-text").html($("#joinAutoreplyText").val()? $("#joinAutoreplyText").val().htmlEncode():i18n("announcement.message.none"));
		$("#confirm-leave-autoreply-text").html($("#leaveAutoreplyText").val()? $("#leaveAutoreplyText").val().htmlEncode():i18n("announcement.message.none"));
	}

	function addCustomValidationClasses() {
		console.log("adding custom val");
		jQuery.validator.addMethod("not-empty", function(value, element) {
			console.log("validating not emptiness");
			return ($('#subscriptionGroup').val() != '');
		}, i18n("subscription.group.required.error"));

		aliasCustomValidation();
		genericSortingValidation();
	}
</r:script>
