<r:script>
	function initializePopup() {
		var validator, keyWordTabValidation, messageTextTabValidation, confirmTabValidation;

		<g:if test="${activityInstanceToEdit?.id}">
			$("#messageText").val("${activityInstanceToEdit.autoreplyText.escapeForJavascript()}");
			$("#messageText").trigger("keyup");
		</g:if>
		
		aliasCustomValidation();
		genericSortingValidation();

		validator = $("#create_autoreply").validate({
			errorContainer: ".error-panel",
			rules: {
				messageText: { required:true },
				name: { required:true }
			}
		});

		keyWordTabValidation = function() {
			if(!isGroupChecked("blankKeyword")){
				return validator.element('#keywords');
			}
			 else return true;
		};
		messageTextTabValidation = function() {
			return validator.element('#messageText');
		};

		confirmTabValidation = function() {
			return validator.element('input[name=name]');
		};

		mediumPopup.addValidation('activity-generic-sorting', keyWordTabValidation);
		mediumPopup.addValidation('autoreply-create-message', messageTextTabValidation);
		mediumPopup.addValidation('autoreply-confirm', confirmTabValidation);

		$("#tabs").bind("tabsshow", function(event, ui) {
			updateConfirmationMessage();
		});
	}
	
	function updateConfirmationMessage() {
		var autoreplyText, keywords;
		autoreplyText = $('#messageText').val().htmlEncode();
		if(!(isGroupChecked("blankKeyword"))){
			keywords = $('#keywords').val().toUpperCase();
			$("#keyword-confirm").html('<p>' + keywords  + '</p>');
		} else {
			$("#keyword-confirm").html('<p>' + i18n("autoreply.blank.keyword")  + '</p>');
		}
		$("#autoreply-confirm").html('<p>' + autoreplyText  + '</p>');
	}

</r:script>
