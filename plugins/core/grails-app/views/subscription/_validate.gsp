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
			console.log("validate keyword tab");
		};

		var aliasTabValidation = function() {
			console.log("validate alias tab");
		};

		var autoreplyTabValidation = function() {
			console.log("validate autoreply tab");
		};

		var confirmTabValidation = function() {
			console.log("validate confirm tab");
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
</r:script>