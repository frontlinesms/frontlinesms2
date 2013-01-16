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

		$.each($(".step"), function(index, element){  // for each Step
			var dataToSend = new Object();
			var stepDiv = $(element);
			
			dataToSend.stepId = stepDiv.attr("index");
			dataToSend.index = indexOfLastStep;

			$.each(stepDiv.find("input"), function(index, element){ // for each input in the step
				var inputField = $(element);
				var key = inputField.attr("name");
				var value = inputField.val();

				var prop = new Object();
				prop.key = key
				prop.value = value
				dataToSend.stepProperty = prop
			});

			$.each(stepDiv.find("select"), function(index, element){ // for each select in the step
				var selectField = $(element);
				var key = selectField.attr("name");
				var value = selectField.val();

				var prop = new Object();
				prop.key = key
				prop.value = value
				dataToSend.stepProperty = prop
			});

			$.each(stepDiv.find("textarea"), function(index, element){ // for each textarea in the step
				var textAreaField = $(element);
				var key = textAreaField.attr("name");
				var value = textAreaField.val();

				var prop = new Object();
				prop.key = key
				prop.value = value
				dataToSend.stepProperty = prop
			});

			data.push(dataToSend);
		});
		
		console.log(JSON.stringify(data));

		$("#jsonToSubmit").val(JSON.stringify(data));
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
