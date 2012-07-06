<div class="input">
	<label for='poll-aliases'><g:message code="poll.aliases.prompt"/></label>
	<div class="info">
		<p><g:message code="poll.aliases.prompt.details"/></p>
	</div>
	<ul id='poll-aliases'>
		<g:each in="${['A','B','C','D','E']}" var="alias" status="i">
			<li>
				<g:if test="${activityInstanceToEdit?.id}">
					<label for='alias${alias}' class="${alias == 'A' || alias == 'B' || pollResponse?.value || (i == (activityInstanceToEdit?.responses.size() - 1)) ? 'field-enabled': ''}">alias${alias}</label>
					<% def pollResponse = activityInstanceToEdit?.responses.find {it.key == alias} %>
					<g:if test="${(alias == 'A' || alias == 'B' || pollResponse?.value || (i == (activityInstanceToEdit?.responses.size() - 1)))}">
						<g:textField class='aliases' name="alias${alias}" value="${pollResponse?.aliases}"/>
					</g:if>
					<g:else>
						<g:textField class='aliases' name="alias${alias}" value="${pollResponse?.aliases}" disabled="true"/>	
					</g:else>
				</g:if>
				<g:else>
					<label for='alias${alias}' class="${alias == 'A' || alias == 'B' ? 'field-enabled': ''}">${option}</label>
					<g:if test="${(alias == 'A' || alias == 'B')}">
						<g:textField class='aliases' name="alias${alias}"/>
					</g:if>
					<g:else>
						<g:textField class='aliases' name="alias${alias}" disabled="true"/>	
					</g:else>
				</g:else>
			</li>
		</g:each>
	</ul>
</div>
