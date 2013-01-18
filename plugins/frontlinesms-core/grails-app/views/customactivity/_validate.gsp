<%@ page import="frontlinesms2.Group" %>
<r:script>
	function initializePopup() {
		$("#add-join-action-step").click(function() { addJoinActionStep(); });
		$("#add-leave-action-step").click(function() { addLeaveActionStep(); });
		$("#add-reply-action-step").click(function() { addReplyActionStep(); });

		$('#custom-activity-config-container').sortable();
		
		$.each($('.remove-step'), function(index, element){
			addRemoveListener(element);
		});

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
			 else return true;
		};

		var stepActionsValidation = function() {
			updateConfirmationMessage();
			var t = true;
			var s = true;

			$.each($("textarea[name='autoreplyText']"), function(index, element){
				t = (validator.element($(element)) && t);
			});
			$.each($("select[name='group']"), function(index, element){
				s = (validator.element($(element)) && s);
			});

			return t && s;
		};

		var confirmTabValidation = function() {
			setJsonToSend();
			return validator.element('input[name=name]');
		};

		mediumPopup.addValidation('activity-generic-sorting', keyWordTabValidation);
		mediumPopup.addValidation('customactivity-config', stepActionsValidation);
		mediumPopup.addValidation('customactivity-confirm', confirmTabValidation);
	}

	//>Adding steps
	function addJoinActionStep() {
		var container = $("#custom-activity-config-container");
		container.append(joinActionStepHtml);
	}

	function addLeaveActionStep() {
		var container = $("#custom-activity-config-container");
		container.append(leaveActionStepHtml);
	}

	function addReplyActionStep() {
		var container = $("#custom-activity-config-container");
		container.append(replyActionStepHtml);
	}

	function addRemoveListener(element) {
		$(element).click(function(){
			var p = $(this).parent().parent();
			p.fadeOut(300, function(){ $(this).remove(); });
		});
	}

	function delete(element) {

	}

	function setJsonToSend() {
		var jsonToSend = "";
		var data = new Array();

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
		var stepProperties =  new Array();

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

	var joinActionStepHtml = function() {
		<%
			def divElement = fsms.joinActionStep()
		%>
		var divElement = $(${divElement} + "").attr("index", (parseInt(indexOfLastStep()) + 1));
		addRemoveListener(divElement.find('.remove-step'));
		return divElement;
	};

	var leaveActionStepHtml = function() {
		<%
			divElement = fsms.leaveActionStep()
		%>
		var divElement = $(${divElement} + "").attr("index", (parseInt(indexOfLastStep()) + 1));
		addRemoveListener(divElement.find('.remove-step'));
		return divElement;
	};
	
	var replyActionStepHtml = function() {
		<%
			divElement = fsms.replyActionStep()
		%>
		var divElement = $(${divElement} + "").attr("index", (parseInt(indexOfLastStep()) + 1));
		addRemoveListener(divElement.find('.remove-step'));
		return divElement;
	};

	function updateConfirmationMessage() {
		var container = $('#customactivity-confirm-action-steps');
		container.html("");
		$.each($(".step"), function(index, element){
			var output = "";
			var stepType = $(element).find('input#stepType').val();
			if(stepType == 'join') {
				var groupValue = $(element).find('select[name=group]').val();
				var groupName = $(element).find('select[name=group]').find("option[value="+groupValue+"]").text()
				output = i18n("customactivity.group.join", groupName);
				output = "<p>"+output+"</p>";
			}
			if(stepType == "leave") {
				var groupValue = $(element).find('select[name=group]').val();
				var groupName = $(element).find('select[name=group]').find("option[value="+groupValue+"]").text()
				output = i18n("customactivity.group.leave", groupName);
				output = "<p>"+output+"</p>";
			}
			if(stepType == "reply") {
				var messageText = $(element).find('textarea[name=autoreplyText]').val();
				output = i18n("customactivity.reply.messagetext", messageText);
				output = "<p>"+output+"</p>";
			}
			container.append(output);
		});

		if(!(isGroupChecked("blankKeyword"))){
			var keywords = $('#keywords').val().toUpperCase();
			$("#keyword-confirm").html('<p>' + keywords  + '</p>');
		} else {
			$("#keyword-confirm").html('<p>' + i18n("autoreply.blank.keyword")  + '</p>');
		}
	}

</r:script>
