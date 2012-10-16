<r:script>
	function initializePopup() {
		initializeTabValidation(createFormValidator());
		addCustomValidationClasses();
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
			return (validator.element($('#subscriptionGroup')));
		};

		var sortingTabValidation = function() {
			return true;	
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

		mediumPopup.addValidation('subscription-group', groupTabValidation);
		mediumPopup.addValidation('subscription-sorting', sortingTabValidation);
		mediumPopup.addValidation('subscription-autoreplies', autoreplyTabValidation);
		mediumPopup.addValidation('subscription-confirm', confirmTabValidation);
		
		$("#tabs").bind("tabsshow", function(event, ui) {
			updateConfirmTab();
		});
	}

	function updateConfirmTab() {
		$("#confirm-group-text").html($("#subscription-group").val());
	}

	function addCustomValidationClasses() {
		jQuery.validator.addMethod("notEmpty", function(value, element) {
			return ($('select#subscriptionGroup').val() != '');
		}, i18n("subscription.group.required.error"));

		aliasCustomValidation();
	}
</r:script>
