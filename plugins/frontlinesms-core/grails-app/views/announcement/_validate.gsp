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

		var messageTextTabValidation = function() {
			return validator.element('#messageText');
		};

		var recepientTabValidation = function() {
			var valid = true;
			addAddressHandler();
			valid = $('input[name=addresses]:checked').length > 0;
			var addressListener = function() {
				if($('input[name=addresses]:checked').length > 0) {
					validator.element($('#contacts').find("input[name=addresses]"));
					$('#recipients-list').removeClass("error");
				} else {
					$('#recipients-list').addClass("error");
					validator.showErrors({"addresses": i18n("poll.recipients.validation.error")});
				}
			};
			if (!valid) {
				$('input[name=addresses]').change(addressListener);
				$('input[name=addresses]').trigger("change");
			}
			return valid;
		};

		var confirmTabValidation = function() {
			return validator.element('input[name=name]');
		};

		mediumPopup.addValidation('announcement-create-message', messageTextTabValidation);
		mediumPopup.addValidation('announcement-select-recipients', recepientTabValidation);
		mediumPopup.addValidation('announcement-confirm', confirmTabValidation);
		
		$("#tabs").bind("tabsshow", function(event, ui) {
			updateConfirmationMessage();
		});
	}
	
	function updateConfirmationMessage() {
		var sendMessage = $('#messageText').val().htmlEncode();

		var contactNo = $("#contacts-count").text()
		
		if(contactNo == 0 || isGroupChecked("dontSendMessage")) {
			$("#confirm-recepients-count").addClass("hide")
			$("#no-recepients").removeClass("hide")
		} else {
			$("#confirm-recepients-count").removeClass("hide")
			$("#no-recepients").addClass("hide")
		}
		$("#confirm-message-text").html('<p>' + sendMessage  + '</p>');
	}
</r:script>
