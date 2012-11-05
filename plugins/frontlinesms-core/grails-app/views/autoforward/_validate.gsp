<r:script>
	function initializePopup() {
		<g:if test="${activityInstanceToEdit?.id}">
			$("#messageText").val("${activityInstanceToEdit.autoforwardText}");
			$("#messageText").trigger("keyup");
		</g:if>

		aliasCustomValidation();
		genericSortingValidation();

		var validator = $("#create_autoforward").validate({
			errorContainer: ".error-panel",
			rules: {
				messageText: { required:true },
				name: { required:true }
			}
		});

		var keyWordTabValidation = function() {
			if(!isGroupChecked("blankKeyword")){
				return validator.element('#keywords');
			}
			 else return true;
		};
		var messageTextTabValidation = function() {
			return validator.element('#messageText');
		};

		var confirmTabValidation = function() {
			return validator.element('input[name=name]');
		};

		mediumPopup.addValidation('activity-generic-sorting', keyWordTabValidation);
		mediumPopup.addValidation('autoforward-create-message', messageTextTabValidation);
		mediumPopup.addValidation('autoforward-confirm', confirmTabValidation);

		$("#tabs").bind("tabsshow", function(event, ui) {
			updateConfirmationMessage();
		});
	}

	function updateConfirmationMessage() {
		var autoforwardText = $('#messageText').val().htmlEncode();
		if(!(isGroupChecked("blankKeyword"))){
			var keywords = $('#keywords').val().toUpperCase();
			$("#keyword-confirm").html('<p>' + keywords  + '</p>');
		} else {
			$("#keyword-confirm").html('<p>' + i18n("autoforward.blank.keyword")  + '</p>');
		}
		$("#autoforward-confirm").html('<p>' + autoforwardText  + '</p>');
	}

</r:script>
