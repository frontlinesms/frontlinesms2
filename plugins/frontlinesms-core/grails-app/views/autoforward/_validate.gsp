<r:script>
	function initializePopup() {
		<g:if test="${activityInstanceToEdit?.id}">
			$("#messageText").val("${activityInstanceToEdit.sentMessageText}");
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

		var recipientTabValidation = function() {
			var valid = false;
			addAddressHandler();
			valid = ($('input[name=addresses]:checked').length > 0) || ($('input[name=groups]:checked').length > 0);
			return valid;
		};

		var confirmTabValidation = function() {
			return validator.element('input[name=name]');
		};

		mediumPopup.addValidation('activity-generic-sorting', keyWordTabValidation);
		mediumPopup.addValidation('autoforward-create-message', messageTextTabValidation);
		mediumPopup.addValidation('autoforward-recipients', recipientTabValidation);
		mediumPopup.addValidation('autoforward-confirm', confirmTabValidation);

		$("#tabs").bind("tabsshow", function(event, ui) {
			updateConfirmationMessage();
		});
	}

	function updateConfirmationMessage() {
		var regx = new RegExp("/\(\d+\)/", "g");
		var autoforwardText = $('#messageText').val().htmlEncode();
		var contactInputIds = $('input[name=addresses]:checked').map(function() { return this.id; });
		var contactsList = contactInputIds.map(function(){
			if($("label[for="+ this +"]").text().length > 0){
				return $("label[for="+ this +"]").text();
			}
		});
		var manualContactsList = $('li.manual.contact input').map(function() { return this.value; });
		var contacts = jQuery.merge(jQuery.makeArray(manualContactsList), jQuery.makeArray(contactsList)).join(', ');

		var groupInputIds = $('input[name=groups]:checked').map(function() { return this.id; });
		var groupsList = groupInputIds.map(function(){ return $("label[for="+ this +"]").text() });
		var groups = jQuery.makeArray(groupsList).join(', ').replace(/\s*\(\d+\)/g, "");

		if(!(isGroupChecked("blankKeyword"))){
			var keywords = $('#keywords').val().toUpperCase();
			$("#keyword-confirm").html('<p>' + keywords  + '</p>');
		} else {
			$("#keyword-confirm").html('<p>' + i18n("autoforward.blank.keyword")  + '</p>');
		}
		$("#autoforward-confirm-messagetext").html('<p>' + autoforwardText  + '</p>');
		$("#autoforward-confirm-contacts").html('<p>' + contacts + '</p>');
		$("#autoforward-confirm-groups").html('<p>' + groups + '</p>');
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
