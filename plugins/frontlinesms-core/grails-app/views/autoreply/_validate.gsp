<r:script>
	function initializePopup() {
		<g:if test="${activityInstanceToEdit?.id}">
			$("#messageText").val("${activityInstanceToEdit.autoreplyText}");
			$("#messageText").trigger("keyup");
		</g:if>
		var validator = $("#create_autoreply").validate({
			errorContainer: ".error-panel",
			rules: {
				messageText: { required:true },
				name: { required:true }
			}
		});

		var keyWordTabValidation = function() {
			 if(!isGroupChecked("blankKeyword")) return validator.element('#keywords');
			 else return true;
		};
		var messageTextTabValidation = function() {
			return validator.element('#messageText');
		};

		var confirmTabValidation = function() {
			return validator.element('input[name=name]');
		};

		mediumPopup.addValidation('autoreply-enter-keyword', keyWordTabValidation);
		mediumPopup.addValidation('autoreply-create-message', messageTextTabValidation);
		mediumPopup.addValidation('autoreply-confirm', confirmTabValidation);

		$("#tabs").bind("tabsshow", function(event, ui) {
			updateConfirmationMessage();
		});
	}
	
	function updateConfirmationMessage() {
		if(!(isGroupChecked("blankKeyword"))){
			var keywords = $('#keywords').val().toUpperCase();
			var autoreplyText = $('#messageText').val();

			$("#keyword-confirm").html('<p>' + keywords  + '</p>');
			$("#autoreply-confirm").html('<p>' + autoreplyText  + '</p>');
		} else {
			var autoreplyText = $('#messageText').val();
			$("#keyword-confirm").html('<p>' + i18n("autoreply.blank.keyword")  + '</p>');
			$("#autoreply-confirm").html('<p>' + autoreplyText  + '</p>');
		}
		
	}
</r:script>
