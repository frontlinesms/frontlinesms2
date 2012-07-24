<div class="input">
	<label for='poll-choices'><g:message code="poll.responses.prompt"/></label>
	<ul id='poll-choices'>
		<g:each in="${['A','B','C','D','E']}" var="option" status="i">
			<li>
				<g:if test="${activityInstanceToEdit?.id}">
					<label for='choice${option}' class="${option == 'A' || option == 'B' || pollResponse?.value || (i == (activityInstanceToEdit?.responses.size() - 1)) ? 'field-enabled': ''}">${option}</label>
					<% 	
						def pollResponse = activityInstanceToEdit?.responses.find {it.key == option} 
						def mode = pollResponse?"edit":"create"
					%>
					<g:if test="${(option == 'A' || option == 'B' || pollResponse?.value || (i == (activityInstanceToEdit?.responses.size() - 2)))}">
						<g:textField class='choices ${mode}' name="choice${option}" value="${pollResponse?.value}" onkeyup="addRespectiveAliases(this);highlightNextPollResponse(this);"/>
					</g:if>
					<g:else>
						<g:textField class='choices create' name="choice${option}" value="${pollResponse?.value}" disabled="true" onkeyup="addRespectiveAliases(this);highlightNextPollResponse(this);"/>
					</g:else>
				</g:if>
				<g:else>
					<label for='choice${option}' class="${option == 'A' || option == 'B' ? 'field-enabled': ''}">${option}</label>
					<g:if test="${(option == 'A' || option == 'B')}">
						<g:textField class='choices create required' name="choice${option}" onkeyup="addRespectiveAliases(this);highlightNextPollResponse(this);"/>
					</g:if>
					<g:else>
						<g:textField class='choices create' name="choice${option}" disabled="true"  onkeyup="addRespectiveAliases(this);highlightNextPollResponse(this);"/>	
					</g:else>
				</g:else>
			</li>
		</g:each>
	</ul>
</div>
