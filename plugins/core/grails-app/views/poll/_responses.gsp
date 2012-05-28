<div id="tabs-2" class="poll-responses-tab">
	<label class="bold" for='poll-choices'><g:message code="poll.responses.prompt"/></label>
	<ul id='poll-choices'>
		<g:each in="${['A','B','C','D','E']}" var="option" status="i">
			<li>
				<g:if test="${activityInstanceToEdit?.id}">
					<label for='choice${option}' class="${option == 'A' || option == 'B' || pollResponse?.value || (i == (activityInstanceToEdit?.responses.size() - 1)) ? 'field-enabled': ''}">${option}</label>
					<% def pollResponse = activityInstanceToEdit?.responses.find {it.key == option}%>
					<g:if test="${(option == 'A' || option == 'B' || pollResponse?.value || (i == (activityInstanceToEdit?.responses.size() - 1)))}">
						<g:textField class='choices' name="choice${option}" value="${pollResponse?.value}"/>
					</g:if>
					<g:else>
						<g:textField class='choices' name="choice${option}" value="${pollResponse?.value}" disabled="true"/>	
					</g:else>
				</g:if>
				<g:else>
					<label for='choice${option}' class="${option == 'A' || option == 'B' ? 'field-enabled': ''}">${option}</label>
					<g:if test="${(option == 'A' || option == 'B')}">
						<g:textField class='choices' name="choice${option}"/>
					</g:if>
					<g:else>
						<g:textField class='choices' name="choice${option}" disabled="true"/>	
					</g:else>
				</g:else>
			</li>
		</g:each>
	</ul>
</div>
