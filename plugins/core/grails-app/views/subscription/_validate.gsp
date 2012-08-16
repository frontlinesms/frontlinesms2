<r:script>
	function initializePopup() {
		addCustomValidationClasses()
		initializeTabValidation(createFormValidator());
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
					required: "Keyword is required"
				},
				"joinAutoreplyText": {
					required: "Please enter join autoreply text"
				},
				"leaveAutoreplyText": {
					required: "Please enter leave autoreply text"
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
		var groupAndKeywordTabValidation = function() {
			return (validator.element($('#subscriptionGroup')) && validator.element($("#keyword")));
		};

		var aliasTabValidation = function() {
			console.log("validate alias tab");
			return (validator.element($('#joinAliases')) && validator.element($("#leaveAliases")));
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

		addValidation('subscription-select-group-keyword', groupAndKeywordTabValidation);
		addValidation('subscription-aliases', aliasTabValidation);
		addValidation('subscription-autoreplies', autoreplyTabValidation);
		addValidation('subscription-confirm', confirmTabValidation);
		
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
