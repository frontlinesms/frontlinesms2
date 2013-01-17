<r:script>
	$("#messageText").live("keyup", updateSmsCharacterCount);
	$("button#nextPage").click(poll.setAliasValues);

	function initializePopup() {
		<g:if test="${activityInstanceToEdit?.id}">
			if($("#messageText").val().length > 0) {
				$("#messageText").trigger("keyup");
				$("input[name='enableKeyword']:checked").trigger("change");
				$("input[name='pollType']").trigger("change");
			}
		</g:if>
		<g:else>
			mediumPopup.disableTab("poll-response");
			$("input[name='pollType']").trigger("change");
			$("#yesAutosort").attr("checked","true");
			$("input[name='enableKeyword']:checked").trigger("change");
		</g:else>
		<g:if test="${activityInstanceToEdit?.archived}">
			$("input#dontSendMessage").attr('checked', true);
			$("input#dontSendMessage").trigger("change");
			$("input#dontSendMessage").attr('disabled', 'disabled');
		</g:if>
		poll.addCustomValidationClasses();
		poll.initializeTabValidation(poll.createFormValidator());
	}

	function addRespectiveAliases(field) {
		var yesNo = $("input[name='pollType']:checked").val() == "yesNo";
		if(yesNo) {
			var aliasYesTextField = $("ul#poll-aliases li input#keywordsA");
			var aliasNoTextField = $("ul#poll-aliases li input#keywordsB");
			var yesAlias = i18n("poll.yes") + ", A";
			var noAlias = i18n("poll.no") + ", B";
			var choices = { };
			choices[yesAlias] = aliasYesTextField;
			choices[noAlias] = aliasNoTextField;
			<% 	
				def pollResponse = activityInstanceToEdit?.responses.find {it.key == option} 
				def mode = pollResponse?"edit":"create"
			%>
			$.each(choices, function(key, value) {
				<g:if test="${mode == 'create'}">
					if(value.val().trim().length == 0) value.val(key);
				</g:if>
			});
		} else {
			var aliases = "";
			var rawKey = $(field).attr('id').trim();
			var rawVal = $(field).val().trim();
			var value = rawVal.split(' ')[0]
			var key = rawKey.substring(rawKey.length-1);
			var aliasTextFieldLabel = $("ul#poll-aliases li label[for='keywords" + value + "']");
			var aliasTextField = $("ul#poll-aliases li input#keywords" + key);
			if($(field).hasClass("create")) {
				if(value.length > 0){
					aliases += value + ", " + key;
					aliasTextField.val(aliases);
					aliasTextField.removeAttr("disabled");
				}
			}
			if(value.length == 0) {
				aliasTextFieldLabel.text("");
				aliasTextField.val("");
				aliasTextField.attr("disabled","disabled");
			}
		}
	}

	function resetResponses(){
		<g:if test="${!activityInstanceToEdit?.id}">
			$("input.choices").each(function(){
				$(this).val('');
			});
			$("input.aliases").each(function(){
				$(this).val('');
			});
		</g:if>
	}
</r:script>
