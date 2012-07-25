<%@ page contentType="text/html;charset=UTF-8" %>
<meta name="layout" content="popup"/>
<fsms:wizard url="${[action:'save', controller:'announcement', params:[ownerId:activityInstanceToEdit?.id ?: null, format:'json']]}" name="create_announcement" method="post" onSuccess="checkForSuccessfulSave(data, i18n('announcement.label'))"
		verticalTabs="announcement.create.message,
				announcement.select.recipients,
				announcement.confirm"
		templates="/message/compose,
				/message/select_recipients,
				/announcement/confirm,
				/announcement/save"/>
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

		//Validation Map
		tabValidation = {};

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

		tabValidation["#tab-1"] = messageTextTabValidation;
		tabValidation["#tab-2"] = recepientTabValidation;
		tabValidation["#tab-3"] = confirmTabValidation;

		$("#tabs-1").contentWidget({
			validate: function() {
				return tabValidation["#tab-1"].call();
			}
		});
	
		$("#tabs-2").contentWidget({
			validate: function() {
				return tabValidation["#tab-2"].call();
			}
		});
		
		$("#tabs-3").contentWidget({
			validate: function() {
				return tabValidation["#tab-3"].call();
			}
		});
		
		$("#tabs").bind("tabsshow", function(event, ui) {
			updateConfirmationMessage();
		});
	}
	
	function updateConfirmationMessage() {
		var sendMessage = $('#messageText').val();

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

