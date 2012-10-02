<%@ page import="frontlinesms2.Webconnection" %>
<g:if test="${!activityInstanceToEdit?.id}">
	<div class="input">
		<label for="pollType"><g:message code="webconnection.select.type"/></label>
		<ul class="select">
			<g:each in="${Webconnection.implementations}" status="i" var="it">
				<li>
					<label for="webconnectionType"><g:message code="webconnection.${it.type}.label"/></label>
					<p><g:message code="webconnection.${it.type}.description"/></p>
					<g:radio name="webconnectionType" checked="${i == 0}"
							value="${it.type}" onclick="setType('${it.type}')"/>
				</li>
			</g:each>
		</ul>
	</div>
</g:if>