<r:script>
	function initializePopup() {
		<g:if test="${activityInstanceToEdit?.id}">
			$("#messageText").trigger("keyup");
			checkSavedContactsAndGroups();
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
		mediumPopup.addValidation('autoforward-recipients', recipientSelecter.validateDeferred);
		mediumPopup.addValidation('autoforward-confirm', confirmTabValidation);

		$("#tabs").bind("tabsshow", function(event, ui) {
			updateConfirmationMessage();
		});
	}

	function updateConfirmationMessage() {
		var autoforwardText = $('#messageText').val().htmlEncode();
		var keywordstate = $("input:radio[name=sorting]:checked").val();

		if(keywordstate === "enabled") {
			var keywords = $('#keywords').val().toUpperCase();
			$("#keyword-confirm").html('<p>' + keywords  + '</p>');
		}
		else {
			$("#keyword-confirm").html('<p>' + i18n("autoforward." + keywordstate + ".keyword")  + '</p>');
		}
		$("#autoforward-confirm-recipient-count").html('<p>' + recipientSelecter.getRecipientCount() + '</p>');
		$("#autoforward-confirm-messagetext").html('<p>' + autoforwardText  + '</p>');
	}
	
	function checkSavedContactsAndGroups(){
		<g:each in="${activityInstanceToEdit?.contacts}" var="c">
			$("#recipients-list input[value='${c.mobile}']").trigger("click");
		</g:each>
		<g:each in="${activityInstanceToEdit?.groups}" var="g">
			$("#recipients-list input[value='group-${g.id}']").trigger("click");
		</g:each>
		<g:each in="${activityInstanceToEdit?.smartGroups}" var="g">
			$("#recipients-list input[value='smartgroup-${g.id}']").trigger("click");
		</g:each>
	}
</r:script>

