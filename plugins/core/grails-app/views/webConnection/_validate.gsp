<r:script>
	function initializePopup() {
		var validator = $("#new-webconnection-form").validate({
			errorContainer: ".error-panel",
			rules: {
				name: { required:true },
				"param-name": { required:true }
			},
			messages: {
				keyword: {
					required: i18n("webconnection.keyword.validation.error")
				},
				url: {
					required: i18n("webconnection.url.validation.error")
				},
				"param-name": {
					required: i18n("webconnection.paramName.validation.error")
				}
			}
		});

		var keyWordTabValidation = function() {
			 if(!isGroupChecked("blankKeyword")) return validator.element('#keyword');
			 else return true;
		};
		var configureTabValidation = function() {
			var isValid = true;
			$('#web-connection-param-table input').each(function() {
				if (!validator.element(this) && isValid) {
					isValid = false;
				}
			});
			return (validator.element('#url') && isValid);
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
