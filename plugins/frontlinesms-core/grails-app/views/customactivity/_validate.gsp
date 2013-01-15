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
		container.append("<div>Leaving Group<div>");
	}

	function addReplyActionStep(){
		var container = $("#custom-activity-config-container");
		container.append("<div>Replying to Humans<div>");
	}

	function updateConfirmationMessage() {

	}

	var joinActionStepHtml = function(){
		var divElement = $("<div class='join-action-step'></div>");
		divElement.append("<h2>Join Group</h2>");
		<%
			def groupSelect = g.select(name:"joinGroup", class:"dropdown not-empty" ,from:Group.getAll() , noSelection:"Select a group.." ,optionKey:"id",optionValue:"name")
		%>
		divElement.append("${groupSelect}");
		return divElement;
	};

	var leaveActionStepHtml = "";
	
	var replyActionStepHTml = "";


</r:script>
