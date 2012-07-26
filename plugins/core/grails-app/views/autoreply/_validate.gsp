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
				keyword: { required:true },
				name: { required:true }
			}
		});

		var keyWordTabValidation = function() {
			 if(!isGroupChecked("blankKeyword")) return validator.element('#keyword');
			 else return true;
		};
		var messageTextTabValidation = function() {
			return validator.element('#messageText');
		};

		var confirmTabValidation = function() {
			return validator.element('input[name=name]');
		};

		//Validation Map
		tabValidation = {};
		tabValidation["#tab-1"] = keyWordTabValidation;
		tabValidation["#tab-2"] = messageTextTabValidation;
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
		if(!(isGroupChecked("blankKeyword"))){
			var keyword = $('#keyword').val().toUpperCase();
			var autoreplyText = $('#messageText').val();

			$("#keyword-confirm").html('<p>' + keyword  + '</p>');
			$("#autoreply-confirm").html('<p>' + autoreplyText  + '</p>');
		}
		else{
			var autoreplyText = $('#messageText').val();
			$("#keyword-confirm").html('<p>' + i18n("autoreply.blank.keyword")  + '</p>');
			$("#autoreply-confirm").html('<p>' + autoreplyText  + '</p>');
		}
		
	}
</r:script>