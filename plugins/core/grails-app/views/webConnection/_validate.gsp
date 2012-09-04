<r:script>
	function initializePopup() {
		var validator = $("#new-webconnection-form").validate({
			errorContainer: ".error-panel",
			rules: {
				name: { required:true }
			}
		});

		var keyWordTabValidation = function() {
			 if(!isGroupChecked("blankKeyword")) return validator.element('#keyword');
			 else return true;
		};
		var configureTabValidation = function() {
			return validator.element('#url');
		};

		var confirmTabValidation = function() {
			return validator.element('input[name=name]');
		};

		addValidation('webconnection-sorting', keyWordTabValidation);
		addValidation('webconnection-configure', configureTabValidation);
		addValidation('webconnection-confirm', confirmTabValidation);

		$("#tabs").bind("tabsshow", function(event, ui) {
			updateConfirmationMessage();
			updateServerConfiguration();
		});
	}
	
	function updateConfirmationMessage() {
		if(!(isGroupChecked("blankKeyword"))){
			var keyword = $('#keyword').val().toUpperCase();
			var autoreplyText = $('#messageText').val();

			$("#keyword-confirm").html('<p>' + keyword  + '</p>');
			$("#autoreply-confirm").html('<p>' + autoreplyText  + '</p>');
		} else {
			var autoreplyText = $('#messageText').val();
			$("#keyword-confirm").html('<p>' + i18n("autoreply.blank.keyword")  + '</p>');
			$("#autoreply-confirm").html('<p>' + autoreplyText  + '</p>');
		}
				
	}	
</r:script>
