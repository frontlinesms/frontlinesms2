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

	function indexOfLastStep(){
		return $(".step:last").attr("index") || 0;
	}

	var joinActionStepHtml = function(){
		<%
			def divElement = fsms.joinActionStep()
		%>
		var divElement = $(${divElement} + "").attr("index", (parseInt(indexOfLastStep()) + 1));
		return divElement;
	};

	var leaveActionStepHtml = function(){
		<%
			divElement = fsms.leaveActionStep()
		%>
		var divElement = $(${divElement} + "").attr("index", (parseInt(indexOfLastStep()) + 1));
		return divElement;
	};
	
	var replyActionStepHTml = function(){
		<%
			divElement = fsms.replyActionStep()
		%>
		var divElement = $(${divElement} + "").attr("index", (parseInt(indexOfLastStep()) + 1));
		return divElement;
	};

</r:script>
