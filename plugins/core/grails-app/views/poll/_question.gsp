<%@ page import="frontlinesms2.Poll" %>
<div id="tabs-1">
	<div class="section">
		<div id="responseType">
			<h2 class="bold"><g:message code="poll.type.prompt"/></h2>
			<ul>
				<g:if test="${activityInstanceToEdit}">
					<g:set var="isStandard" value="${activityInstanceToEdit?.isStandard}"/>
					<li>
						<g:radio name="pollType" value="standard" checked="${isStandard}" disabled="${!isStandard}"/>
						<g:message code="poll.question.yes.no"/>
					</li>
					<li>
						<g:radio name="pollType" value="multiple" checked="${!isStandard}" disabled="${isStandard}"/>
						<g:message code="poll.question.multiple"/>
					</li>
				</g:if>
				<g:else>
					<li>
						<g:radio name="pollType" value="standard" checked='checked'/>
						<g:message code="poll.question.yes.no"/>
					</li>
					<li>
						<g:radio name="pollType" value="multiple"/>
						<g:message code="poll.question.multiple"/>
					</li>
				</g:else>
			</ul>
		</div>
		<div id="poll-question" >
			<label class="bold" for='question'>
				<g:message code="poll.question.prompt"/>
				<span class="required-indicator"> *</span>
			</label>
			<g:textArea name="question" value="${activityInstanceToEdit?.question}" class="required"/>
		</div>
		<g:checkBox name="dontSendMessage" value="no-message" checked='false'/><g:message code="poll.message.none"/>
	</div>
</div>
<r:script>
	$("input[name='dontSendMessage']").live("change", function() {
		if(isGroupChecked("dontSendMessage")) {
			disableTab(4);
			disableTab(5);
			//update confirm screen
			updateConfirmationMessage();
		} else {
			enableTab(4);
			enableTab(5);
		}
	});

	$("input[name='pollType']").live("change", function() {
		if ($("input[name='pollType']:checked").val() == "standard") {
			disableTab(1);
		} else {
			enableTab(1);
		}
		autoUpdate = true;
		updateConfirmationMessage();
	});
</r:script>
