<%@ page import="frontlinesms2.Webconnection" %>
<g:if test="${!activityInstanceToEdit?.id}">
	<div class="input">
		<label for="pollType"><g:message code="webconnection.select.type"/></label>
		<ul class="select">
			<g:each in="${Webconnection.implementations}" status="i" var="it">
				<li>
					<label for="webconnectionType"><g:message code="webconnection.${it.type}.label"/></label>
					<p class="info"><g:message code="webconnection.${it.type}.description"/></p>
					<g:radio name="webconnectionType" checked="${it.type == 'generic'}"
							value="${it.type}" onchange="setType('${it.type}')"/>
				</li>
			</g:each>
		</ul>
	</div>
</g:if>
<r:script>
$(function() {
	setType('generic');
});
</r:script>