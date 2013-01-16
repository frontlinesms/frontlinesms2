<%@ page import="frontlinesms2.Group" %>
<r:script>
	function initializePopup() {
		$("#add-join-action-step").click(function() { addJoinActionStep(); });
		$("#add-leave-action-step").click(function() { addLeaveActionStep(); });
		$("#add-reply-action-step").click(function() { addReplyActionStep(); });

		//> Validation
		var validator = $("#create_customactivity").validate({
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
			setJsonToSend();
			return validator.element('input[name=name]');
		};

		mediumPopup.addValidation('activity-generic-sorting', keyWordTabValidation);
		mediumPopup.addValidation('customactivity-config', messageTextTabValidation);
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
		container.append(replyActionStepHTml);
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

	function updateConfirmationMessage() {

	}

	function indexOfLastStep() {
		return $(".step:last").attr("index") || 0;
	}

	var joinActionStepHtml = function() {
		<%
			def divElement = fsms.joinActionStep()
		%>
		var divElement = $(${divElement} + "").attr("index", (parseInt(indexOfLastStep()) + 1));
		return divElement;
	};

	var leaveActionStepHtml = function() {
		<%
			divElement = fsms.leaveActionStep()
		%>
		var divElement = $(${divElement} + "").attr("index", (parseInt(indexOfLastStep()) + 1));
		return divElement;
	};
	
	var replyActionStepHTml = function() {
		<%
			divElement = fsms.replyActionStep()
		%>
		var divElement = $(${divElement} + "").attr("index", (parseInt(indexOfLastStep()) + 1));
		return divElement;
	};

</r:script>
