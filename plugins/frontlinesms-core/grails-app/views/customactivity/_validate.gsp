<%@ page import="frontlinesms2.Group" %>
<r:script>
	function initializePopup() {
		$("#add-join-action-step").click(function() { addJoinActionStep(); });
		$("#add-leave-action-step").click(function() { addLeaveActionStep(); });
		$("#add-reply-action-step").click(function() { addReplyActionStep(); });
	}

	function addJoinActionStep(){
		var container = $("#custom-activity-config-container");
		container.append(joinActionStepHtml);
	}

	function addLeaveActionStep(){
		var container = $("#custom-activity-config-container");
		container.append(leaveActionStepHtml);
	}

	function addReplyActionStep(){
		var container = $("#custom-activity-config-container");
		container.append(replyActionStepHTml);
	}

	function updateConfirmationMessage() {

	}

	var joinActionStepHtml = function(){
		<% def divElement = fsms.joinActionStep() %>
		return ${divElement} + "";
	};

	var leaveActionStepHtml = function(){
		<% divElement = fsms.leaveActionStep() %>
		return ${divElement} + "";
	};
	
	var replyActionStepHTml = function(){
		<% divElement = fsms.replyActionStep() %>
		return ${divElement} + "";
	};


</r:script>
