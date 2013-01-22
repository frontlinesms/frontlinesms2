<fsms:render template="/customactivity/steps/joinstep"/>
<fsms:render template="/customactivity/steps/leavestep"/>
<fsms:render template="/customactivity/steps/replystep"/>

<r:script>
	function initializePopup() {
		custom_activity.steps = ["join", "leave", "reply"];
		custom_activity.init();

		$('#custom-activity-config-container').sortable();

		//> Validation
		var validator = $("#create_customactivity").validate({
			errorContainer: ".error-panel",
			rules: {
				autoreplyText: { required:true },
				name: { required:true }
			}
		});

		customValidationForGroups();

		var keyWordTabValidation = function() {
			if(!isGroupChecked("blankKeyword")){
				return validator.element('#keywords');
			}
			return true;
		};

		var stepActionsValidation = function() {
			var valid = true;
			updateConfirmationMessage();

			$.each($("textarea[name='autoreplyText']"), function(index, element) {
				valid = valid && validator.element($(element));
			});

			$.each($("select[name='group']"), function(index, element) {
				valid = valid && validator.element($(element));
			});

			return valid;
		};

		var confirmTabValidation = function() {
			setJsonToSend();
			return validator.element('input[name=name]');
		};

		mediumPopup.addValidation('activity-generic-sorting', keyWordTabValidation);
		mediumPopup.addValidation('customactivity-config', stepActionsValidation);
		mediumPopup.addValidation('customactivity-confirm', confirmTabValidation);
	}

	function setJsonToSend() {
		var jsonToSend = "";
		var data = [];

		$.each($(".step"), function(index, element){
			var dataToSend = new Object();
			var stepDiv = $(element);
			dataToSend.stepId = stepDiv.attr("index");
			dataToSend.stepType = stepDiv.find("#stepType").val();
			if(stepDiv.find("input").size() > 0) {
				dataToSend.stepProperties = getStepProperties("input", stepDiv);
			}
			
			if(stepDiv.find("textarea").size() > 0) {
				dataToSend.stepProperties = getStepProperties("textarea", stepDiv);
			}
			
			if(stepDiv.find("select").size() > 0) {
				dataToSend.stepProperties = getStepProperties("select", stepDiv);
			}

			data.push(dataToSend);
		});
		$("#jsonToSubmit").val(JSON.stringify(data));
	}

	function getStepProperties(inputType, container) {
		var stepProperties = [];

		$.each(container.find(inputType), function(index, element) {
			var inputField = $(element);
			var key = inputField.attr("name");
			var value = inputField.val();
			var property = new Object();
			property.key = key
			property.value = value
			stepProperties.push(property);
		});

		return stepProperties;
	}

	function indexOfLastStep() {
		return $(".step:last").attr("index") || 0;
	}

	function updateConfirmationMessage() {
		var container, keywords;
		container = $('#customactivity-confirm-action-steps');
		container.html("");
		$.each($(".step"), function(index, element) {
			var groupName, groupValue, messageText, output, stepType;
			output = "";
			stepType = $(element).find('input#stepType').val();
			if(stepType == 'join') {
				groupValue = $(element).find('select[name=group]').val();
				groupName = $(element).find('select[name=group]').find("option[value="+groupValue+"]").text()
				output = i18n("customactivity.group.join", groupName);
				output = "<p>"+output+"</p>";
			}
			if(stepType == "leave") {
				groupValue = $(element).find('select[name=group]').val();
				groupName = $(element).find('select[name=group]').find("option[value="+groupValue+"]").text()
				output = i18n("customactivity.group.leave", groupName);
				output = "<p>"+output+"</p>";
			}
			if(stepType == "reply") {
				messageText = $(element).find('textarea[name=autoreplyText]').val();
				output = i18n("customactivity.reply.messagetext", messageText);
				output = "<p>"+output+"</p>";
			}
			container.append(output);
		});

		if(!(isGroupChecked("blankKeyword"))){
			keywords = $('#keywords').val().toUpperCase();
			$("#keyword-confirm").html('<p>' + keywords  + '</p>');
		} else {
			$("#keyword-confirm").html('<p>' + i18n("autoreply.blank.keyword")  + '</p>');
		}
	}
</r:script>

