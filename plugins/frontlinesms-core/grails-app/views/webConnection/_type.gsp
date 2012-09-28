<%@ page import="frontlinesms2.WebConnection" %>
<g:if test="${!activityInstanceToEdit}">
	<div class="input">
		<label for="pollType"><g:message code="webconnection.select.type"/></label>
		<ul class="select">
			<g:each in="${WebConnection.implementations}" status="i" var="it">
				<li>
					<label for="webConnectionType"><g:message code="webconnection.${it.type}.label"/></label>
					<p><g:message code="webconnection.${it.type}.description"/></p>
					<g:radio name="webConnectionType" checked="${i == 0}"
							value="${it.type}" onclick="setType('${it.type}')"/>
				</li>
			</g:each>
		</ul>
	</div>
</g:if>
<g:else>
	<g:hiddenField name="webConnectionType" value="${activityInstanceToEdit?.type}"/>
</g:else>

