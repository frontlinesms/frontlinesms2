<div id="tabs-1">
	<div class="section">
		<div id="responseType">
			<h2 class="bold">Select the kind of poll to create:</h2>
			<ul>
				<g:if test="${activityInstanceToEdit}">
					<g:set var="standard" value="${activityInstanceToEdit?.responses*.key.contains('A')}"/>
					<li><g:radio name="pollType" value="standard" checked="${standard}" disabled="${!standard}" />Question with a 'Yes' or 'No' answer</li>
					<li><g:radio name="pollType" value="multiple" checked="${!standard}" disabled="${standard}"/>Multiple choice question (e.g. 'Red', 'Blue', 'Green')</li>
				</g:if>
				<g:else>
					<li><g:radio name="pollType" value="standard" checked='checked'/>Question with a 'Yes' or 'No' answer</li>
					<li><g:radio name="pollType" value="multiple"/>Multiple choice question (e.g. 'Red', 'Blue', 'Green')</li>
				</g:else>
			</ul>
		</div>
		<div id="poll-question" >
			<label class="bold" for='question'>Enter question:</label>
			<g:textArea name="question" value="${activityInstanceToEdit?.question}"/>
		</div>
		<g:checkBox name="dontSendMessage" value="no-message" checked='false'/>Do not send a message for this poll(collect responses only)
	</div>
</div>
<g:javascript>
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
</g:javascript>
