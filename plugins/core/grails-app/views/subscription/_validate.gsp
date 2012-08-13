<r:script>
	function initializePopup() {
		var validator = $("#create_announcement").validate({
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

		var groupAndKeywordTabValidation = function() {
			return validator.element('#messageText');
		};

		var aliasTabValidation = function() {

		};

		var autoreplyTabValidation = function() {
			
		};

		var confirmTabValidation = function() {
			return validator.element('input[name=name]');
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
		$("#confirm-group-text").val("THIS IS MY GROUP Y'ALL!") //TODO: confirm tab update & wizard validation
	}
}
</r:script>