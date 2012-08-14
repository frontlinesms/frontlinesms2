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
			return (validator.element($('#subscriptionGroup')) && validator.element($("#subscription-keyword")));
		};

		var aliasTabValidation = function() {
			console.log("validate alias tab");
		};

		var autoreplyTabValidation = function() {
			console.log("validate autoreply tab");
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
	}
</r:script>