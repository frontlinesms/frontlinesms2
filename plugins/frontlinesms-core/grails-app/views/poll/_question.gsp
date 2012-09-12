<%@ page import="frontlinesms2.Poll" %>
<div class="input">
	<label for="pollType"><g:message code="poll.type.prompt"/></label>
	<ul class="select">
		<g:set var="isYesNo" value="${activityInstanceToEdit?.yesNo}"/>
		<li>
			<label for="pollType"><g:message code="poll.question.yes.no"/></label>
			<g:radio name="pollType" value="yesNo" checked="${!activityInstanceToEdit || isYesNo}" disabled="${activityInstanceToEdit && !isYesNo}"/>
		</li>
		<li>
			<label for="pollType"><g:message code="poll.question.multiple"/></label>
			<g:radio name="pollType" value="multiple" checked="${activityInstanceToEdit && !isYesNo}" disabled="${activityInstanceToEdit && isYesNo}"/>
		</li>
	</ul>
</div>
<div class="input">
	<label for="question">
		<g:message code="poll.question.prompt"/>
	</label>
	<g:textArea name="question" value="${activityInstanceToEdit?.question}" class="required"/>
</div>
<div class="input optional">
	<label for="dontSendMessage"><g:message code="poll.message.none"/></label>
	<g:checkBox name="dontSendMessage" value="no-message" checked='false'/>
</div>

<r:script>
	$("input[name='dontSendMessage']").live("change", function() {
		if(isGroupChecked("dontSendMessage")) {
			disableTab("poll-edit-message");
			disableTab("poll-recipients");
			//update confirm screen
			updateConfirmationMessage();
		} else {
			enableTab("poll-edit-message");
			enableTab("poll-recipients");
		}
	});

	var setPollType = function() {
		if ($("input[name='pollType']:checked").val() == "yesNo") {
			disableTab("poll-response");
			resetResponses();
			addRespectiveAliases();
		} else {
			enableTab("poll-response");
			resetResponses();
		}
		autoUpdate = true;
		updateConfirmationMessage();
	}

	$("input[name='pollType']").live("change", setPollType);
</r:script>
